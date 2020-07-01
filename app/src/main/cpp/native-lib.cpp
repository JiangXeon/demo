#include <jni.h>
#include <string>
#include "md5.h"
#include <android/log.h>

#define LOG_TAG    "jiat"
#define LOGD(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

const std::string privateKeys[] = {
        "12",
        "e45c1879e1231202b8940f6f7f8bc4e828e97",
        "05dcea6cd213d2341f0cabb1361a290348",
        "caffed620asd867fea19137504a96ce388f",
        "d90d3ed37b3138e10aef441973052dd75",
        "3f0492f0694sda893194d0605a956276c28dd",
        "18982a1175bfcb512b378e2f112748b125",
        "813b8f96de36c0ased00be0c794e4650f6",
        "58615d462a75f7ec1131aab7164a2de4c6e",
        "2bee96e69c7362a5967asdas4a0d69ded162b"
};

extern "C" JNIEXPORT jstring JNICALL
Java_com_xeon_baseDemo_handler_GenerateKey_generateKey(
        JNIEnv *env,
        jobject /* this */,
        jstring str) {
    const char *data = env->GetStringUTFChars(str, JNI_FALSE);
    std::string temp = privateKeys[strlen(data) % 10];
    temp.insert(0,data);
//    LOGD("jni_debug temp = %s",temp.data());
//    LOGD("jni_debug temp = %d",strlen(temp.data()));


    MD5 md5 = MD5(temp.data());
    std::string md5Result = md5.hexdigest();
    //将char *类型转化成jstring返回给Java层
    return env->NewStringUTF(md5Result.c_str());
}
