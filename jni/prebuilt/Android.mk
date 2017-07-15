LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := forcetv
LOCAL_SRC_FILES := libforcetv.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := aplayer
LOCAL_SRC_FILES := libaplayer_android.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := aplayer_ffmpeg
LOCAL_SRC_FILES := libaplayer_ffmpeg.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := vrtoolkit
LOCAL_SRC_FILES := libvrtoolkit.so
include $(PREBUILT_SHARED_LIBRARY)



