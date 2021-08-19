//
// Created by it on 16/05/2020.
//  <opencv2/core/core.hpp>

#include <jni.h>
#include <string>


const int FRAME_WIDTH = 320;
const int FRAME_HEIGHT = 240;
const int MIN_OBJECT_AREA = 25 * 25;
const int MAX_OBJECT_AREA = FRAME_HEIGHT*FRAME_WIDTH / 1.5;



extern "C" {
jstring
Java_com_example_jackyle_opencvtesting_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


}

extern "C"
JNIEXPORT void JNICALL
Java_dev_em_hslscanner_fire_1scan_FindFeatures(JNIEnv *env, jobject thiz, jlong mat_addr_gr,
                                               jlong mat_addr_rgba) {
    // TODO: implement FindFeatures()
}extern "C"
JNIEXPORT jint JNICALL
Java_dev_em_hslscanner_openbc_getMessageFromNative(JNIEnv *env, jobject thiz) {
    // TODO: implement getMessageFromNative()
}extern "C"
JNIEXPORT void JNICALL
Java_dev_em_hslscanner_openbc_callNative(JNIEnv *env, jobject thiz, jboolean do_rgba,
                                         jboolean do_gray, jboolean do_thresh,
                                         jboolean do_adapt_only, jboolean do_otsu_only,
                                         jboolean do_erode, jboolean do_find_contous,
                                         jboolean do_find_squares, jboolean do_pers_trans,
                                         jboolean do_qrreader, jboolean end_with_rgba,
                                         jboolean end_with_gray, jboolean end_adapt_only,
                                         jboolean end_otsu_only, jboolean end_with_erode,
                                         jboolean end_with_contours, jboolean end_with_squares,
                                         jlong mat_addr_rgba, jlong mat_addr_gr,
                                         jlong mat_addr_qr_gray2) {
    // TODO: implement callNative()
}