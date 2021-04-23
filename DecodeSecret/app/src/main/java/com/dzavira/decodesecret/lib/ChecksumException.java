package com.dzavira.decodesecret.lib;

public class ChecksumException extends ReaderException {
    private static final ChecksumException instance = new ChecksumException();

    private ChecksumException() {
        // do nothing
    }

    public static ChecksumException getChecksumInstance() {
        return instance;
    }
}