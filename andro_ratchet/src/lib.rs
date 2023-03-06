use std::{hint::black_box, time::Instant};

use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::jdouble;

use asym_ratchet::*;

fn benchmark<T, F: FnMut() -> T>(mut f: F) -> f64 {
    const RUN_COUNT: usize = 20;
    let start = Instant::now();
    for _ in 0..RUN_COUNT {
        black_box(f());
    }
    (start.elapsed().as_millis() / RUN_COUNT as u128) as f64
}

#[no_mangle]
pub extern "system" fn Java_edu_kit_tm_ps_arb_Ratchet_benchmarkKeygen<'local>(_env: JNIEnv<'local>, _class: JClass<'local>) -> jdouble {
    let mut rng = rand::thread_rng();
    benchmark(|| generate_keypair(&mut rng))
}

#[no_mangle]
pub extern "system" fn Java_edu_kit_tm_ps_arb_Ratchet_benchmarkPubRatchet<'local>(_env: JNIEnv<'local>, _class: JClass<'local>) -> jdouble {
    let mut rng = rand::thread_rng();
    let (mut pk, _) = generate_keypair(&mut rng);
    benchmark(|| pk.ratchet())
}

#[no_mangle]
pub extern "system" fn Java_edu_kit_tm_ps_arb_Ratchet_benchmarkPrivRatchet<'local>(_env: JNIEnv<'local>, _class: JClass<'local>) -> jdouble {
    let mut rng = rand::thread_rng();
    let (_, mut sk) = generate_keypair(&mut rng);
    benchmark(|| sk.ratchet(&mut rng))
}

#[no_mangle]
pub extern "system" fn Java_edu_kit_tm_ps_arb_Ratchet_benchmarkEncrypt<'local>(_env: JNIEnv<'local>, _class: JClass<'local>) -> jdouble {
    let mut rng = rand::thread_rng();
    let (pk, _) = generate_keypair(&mut rng);
    let message: &[u8] = b"Hello World!";
    benchmark(|| pk.encrypt(&mut rng, message.into()).unwrap())
}

#[no_mangle]
pub extern "system" fn Java_edu_kit_tm_ps_arb_Ratchet_benchmarkDecrypt<'local>(_env: JNIEnv<'local>, _class: JClass<'local>) -> jdouble {
    let mut rng = rand::thread_rng();
    let (pk, sk) = generate_keypair(&mut rng);
    let message: &[u8] = b"Hello World!";
    let cipher = pk.encrypt(&mut rng, message.into()).unwrap();
    benchmark(|| sk.decrypt(cipher.clone()))
}
