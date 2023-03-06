```bash
# Install cargo ndk for easy Android cross-compilation
cargo install cargo-ndk
# Install stdlib for cross compilation targets
rustup target add \
    aarch64-linux-android \
    armv7-linux-androideabi \
    x86_64-linux-android \
    i686-linux-android
# Compile libs
ANDROID_NDK_HOME=~/Android/Sdk/ndk cargo ndk -t armeabi-v7a -t x86_64-linux-android -t i686-linux-android -o ../app/src/main/jniLibs build --release
```

See also <https://github.com/bbqsrc/cargo-ndk>.

The `andro_ratchet` crate is a small shim that wraps [Asymmetric Ratchet](https://git.scc.kit.edu/ps-chair/asymmetric-ratchet) for benchmarking, providing a Java Native Interface.
