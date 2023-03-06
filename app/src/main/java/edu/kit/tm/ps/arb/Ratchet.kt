package edu.kit.tm.ps.arb

class Ratchet {
    init {
        System.loadLibrary("andro_ratchet")
    }
    external fun benchmarkKeygen(): DoubleArray
    external fun benchmarkPubRatchet(): DoubleArray
    external fun benchmarkPrivRatchet(): DoubleArray
    external fun benchmarkEncrypt(): DoubleArray
    external fun benchmarkDecrypt(): DoubleArray
}