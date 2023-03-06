use std::{hint::black_box, time::Instant};

use jni::JNIEnv;
use jni::objects::{JDoubleArray, JClass};

use asym_ratchet::*;

fn benchmark<T, F: FnMut() -> T>(mut f: F) -> Vec<f64> {
    const RUN_COUNT: usize = 20;
    (0..RUN_COUNT).map(|_| {
        let start = Instant::now();
        black_box(f());
        start.elapsed().as_nanos() as f64
    }).collect()
}

fn to_java<'local>(env: JNIEnv<'local>, input: &[f64]) -> JDoubleArray<'local> {
    let jarray = env.new_double_array(input.len() as i32).unwrap();
    env.set_double_array_region(&jarray, 0, input).unwrap();
    jarray
}

#[no_mangle]
pub extern "system" fn Java_edu_kit_tm_ps_arb_Ratchet_benchmarkKeygen<'local>(env: JNIEnv<'local>, _class: JClass<'local>) -> JDoubleArray<'local> {
    let mut rng = rand::thread_rng();
    to_java(env, &benchmark(|| generate_keypair(&mut rng)))
}

#[no_mangle]
pub extern "system" fn Java_edu_kit_tm_ps_arb_Ratchet_benchmarkPubRatchet<'local>(env: JNIEnv<'local>, _class: JClass<'local>) -> JDoubleArray<'local> {
    let mut rng = rand::thread_rng();
    let (mut pk, _) = generate_keypair(&mut rng);
    to_java(env, &benchmark(|| pk.ratchet()))
}

#[no_mangle]
pub extern "system" fn Java_edu_kit_tm_ps_arb_Ratchet_benchmarkPrivRatchet<'local>(env: JNIEnv<'local>, _class: JClass<'local>) -> JDoubleArray<'local> {
    let mut rng = rand::thread_rng();
    let (_, mut sk) = generate_keypair(&mut rng);
    to_java(env, &benchmark(|| sk.ratchet(&mut rng)))
}

#[no_mangle]
pub extern "system" fn Java_edu_kit_tm_ps_arb_Ratchet_benchmarkEncrypt<'local>(env: JNIEnv<'local>, _class: JClass<'local>) -> JDoubleArray<'local> {
    let mut rng = rand::thread_rng();
    let (pk, _) = generate_keypair(&mut rng);
    let message: &[u8] = b"Hello World!";
    to_java(env, &benchmark(|| pk.encrypt(&mut rng, message.into()).unwrap()))
}

#[no_mangle]
pub extern "system" fn Java_edu_kit_tm_ps_arb_Ratchet_benchmarkDecrypt<'local>(env: JNIEnv<'local>, _class: JClass<'local>) -> JDoubleArray<'local> {
    let mut rng = rand::thread_rng();
    let (pk, sk) = generate_keypair(&mut rng);
    let message: &[u8] = b"Hello World!";
    let cipher = pk.encrypt(&mut rng, message.into()).unwrap();
    to_java(env, &benchmark(|| sk.decrypt(cipher.clone())))
}
