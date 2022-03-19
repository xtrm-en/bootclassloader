extern crate jvm_rs;

use std::{
    ffi::c_void,
    os::raw::c_int,
    ptr::null_mut,
};
use jvm_rs::{
    jni::{
        JavaVM,
        jboolean,
        jclass,
        jint,
        JNI_OK,
        JNI_VERSION_1_2,
        JNIEnv,
        jstring,
    },
    jvmti::{
        JVMTI_VERSION_1_1,
        jvmtiEnv,
        jvmtiError_JVMTI_ERROR_NONE,
    }
};

static mut JVMTI: *mut jvmtiEnv = null_mut();

#[no_mangle]
pub unsafe extern "system" fn JNI_OnLoad(_vm: *mut JavaVM, _reserved: &mut c_void) -> c_int {
    let mut ptr: *mut c_void = null_mut();
    let result = (*(*_vm)).GetEnv.unwrap()(_vm, &mut ptr, JVMTI_VERSION_1_1 as jint);
    if result == JNI_OK as jint {
        JVMTI = ptr.cast::<jvmtiEnv>();

        let mut ver: jint = -1;
        let error = (*(*JVMTI)).GetVersionNumber.unwrap()(JVMTI, &mut ver);
        if error != jvmtiError_JVMTI_ERROR_NONE {
            println!("[libbcl] Something has gone horribly wrong. @ GetVersionNumber, ver/error: {}/{}", ver, error);
            return -1;
        }
    }

    JNI_VERSION_1_2 as i32
}

#[no_mangle]
pub unsafe extern "system" fn Java_me_xtrm_bootclassloader_BootClassLoader_appendToClassLoader0(
    env: *mut JNIEnv,
    _class: jclass,
    file_path: jstring,
    boot: jboolean,
) {
    let c_str = (*(*env)).GetStringUTFChars.unwrap()(env, file_path, &mut 0);

    if boot == 1 {
        (*(*JVMTI)).AddToBootstrapClassLoaderSearch.unwrap()(JVMTI, c_str);
    } else {
        (*(*JVMTI)).AddToSystemClassLoaderSearch.unwrap()(JVMTI, c_str);
    }
}
