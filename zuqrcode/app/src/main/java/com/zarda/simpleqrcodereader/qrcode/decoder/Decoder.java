/*
 * Copyright 2007 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zarda.simpleqrcodereader.qrcode.decoder;

import android.util.Log;

import java.util.Map;

import com.zarda.simpleqrcodereader.common.BitMatrix;
import com.zarda.simpleqrcodereader.common.DecoderResult;
import com.zarda.simpleqrcodereader.common.reedsolomon.GenericGF;
import com.zarda.simpleqrcodereader.common.reedsolomon.ReedSolomonDecoder;
import com.zarda.simpleqrcodereader.common.reedsolomon.ReedSolomonException;
import com.zarda.simpleqrcodereader.reader.ChecksumException;
import com.zarda.simpleqrcodereader.reader.DecodeHintType;
import com.zarda.simpleqrcodereader.reader.FormatException;

/**
 * <p>
 * The main class which implements QR Code decoding -- as opposed to locating
 * and extracting the QR Code from an image.
 * </p>
 * 
 * @author Sean Owen
 */
public final class Decoder {

	private final ReedSolomonDecoder rsDecoder;

	public Decoder() {
		rsDecoder = new ReedSolomonDecoder(GenericGF.QR_CODE_FIELD_256);
	}

	public DecoderResult decode(boolean[][] image) throws ChecksumException,
			FormatException {
		return decode(image, null);
	}


	public DecoderResult decode(boolean[][] image, Map<DecodeHintType, ?> hints)
			throws ChecksumException, FormatException {
		int dimension = image.length;
		BitMatrix bits = new BitMatrix(dimension);

		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (image[i][j]) {
					bits.set(j, i);
				}
			}
		}
		Log.e("panjang bits : ", String.valueOf(bits));
		return decode(bits, hints);
	}

	public DecoderResult decode(BitMatrix bits) throws ChecksumException,
			FormatException {
		return decode(bits, null);
	}


	public DecoderResult decode(BitMatrix bits, Map<DecodeHintType, ?> hints)
			throws FormatException, ChecksumException {

		// Construct a parser and read version, error-correction level
		BitMatrixParser parser = new BitMatrixParser(bits);
		FormatException fe = null;
		ChecksumException ce = null;
		try {
			return decode(parser, hints);
		} catch (FormatException e) {
			fe = e;
		} catch (ChecksumException e) {
			ce = e;
		}

		try {

			// Revert the bit matrix
			parser.remask();

			// Will be attempting a mirrored reading of the version and format
			// info.
			parser.setMirror(true);

			// Preemptively read the version.
			parser.readVersion();

			// Preemptively read the format information.
			parser.readFormatInformation();

			parser.mirror();

			DecoderResult result = decode(parser, hints);
	Log.e("panjang hints", String.valueOf(hints));
			// Success! Notify the caller that the code was mirrored.
			result.setOther(new QRCodeDecoderMetaData(true));

			return result;

		} catch (ChecksumException e) {
			// Throw the exception from the original reading
			if (fe != null) {
				throw fe;
			}
			if (ce != null) {
				throw ce;
			}
			throw e;

		} catch (FormatException e) {
			// Throw the exception from the original reading
			if (fe != null) {
				throw fe;
			}
			if (ce != null) {
				throw ce;
			}
			throw e;

		}
	}

	private DecoderResult decode(BitMatrixParser parser,
			Map<DecodeHintType, ?> hints) throws FormatException,
			ChecksumException {
		Version version = parser.readVersion();
		ErrorCorrectionLevel ecLevel = parser.readFormatInformation()
				.getErrorCorrectionLevel();

		// Read codewords
		byte[] codewords = parser.readCodewords();
		// Separate into data blocks
		DataBlock[] dataBlocks = DataBlock.getDataBlocks(codewords, version,
				ecLevel);
Log.e("code :"+codewords,"bloks "+dataBlocks);

		// Count total number of data bytes
		int totalBytes = 0;
		for (DataBlock dataBlock : dataBlocks) {
			totalBytes += dataBlock.getNumDataCodewords();
		}
		Log.e("total", String.valueOf(totalBytes));
		byte[] resultBytes = new byte[totalBytes];
		int resultOffset = 0;

		// Error-correct and copy data blocks together into a stream of bytes
		for (DataBlock dataBlock : dataBlocks) {
			byte[] codewordBytes = dataBlock.getCodewords();
			int numDataCodewords = dataBlock.getNumDataCodewords();
			Log.e("panjang", String.valueOf(numDataCodewords));
			correctErrors(codewordBytes, numDataCodewords);
			for (int i = 0; i < numDataCodewords; i++) {
				resultBytes[resultOffset++] = codewordBytes[i];
				Log.e("panjang data "+i, String.valueOf(codewordBytes[i]));

			}
		}

		// Decode the contents of that stream of bytes
		return DecodedBitStreamParser.decode(resultBytes, version, ecLevel,
				hints);
	}


	private void correctErrors(byte[] codewordBytes, int numDataCodewords)
			throws ChecksumException {
		int numCodewords = codewordBytes.length;
		// First read into an array of ints
		int[] codewordsInts = new int[numCodewords];
		for (int i = 0; i < numCodewords; i++) {
			codewordsInts[i] = codewordBytes[i] & 0xFF;
		}
		int numECCodewords = codewordBytes.length - numDataCodewords;
		try {
			rsDecoder.decode(codewordsInts, numECCodewords);
		} catch (ReedSolomonException ignored) {
			throw ChecksumException.getChecksumInstance();
		}
		// Copy back into array of bytes -- only need to worry about the bytes
		// that were data
		// We don't care about errors in the error-correction codewords
		for (int i = 0; i < numDataCodewords; i++) {
			codewordBytes[i] = (byte) codewordsInts[i];
		}
	}

}
