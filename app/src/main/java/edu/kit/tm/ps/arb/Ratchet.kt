package edu.kit.tm.ps.arb

class Ratchet {
    init {
        System.loadLibrary("andro_ratchet")
    }
    external fun benchmarkKeygen(): Double
    external fun benchmarkPubRatchet(): Double
    external fun benchmarkPrivRatchet(): Double
    external fun benchmarkEncrypt(): Double
    external fun benchmarkDecrypt(): Double
}