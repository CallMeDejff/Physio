package com.example.physio.retrofitutil

import com.scottyab.aescrypt.AESCrypt
import java.security.GeneralSecurityException

class ApiEncryption {
    @Throws(GeneralSecurityException::class)
    fun encrypt(message: String?, key: String?): String {
        return AESCrypt.encrypt(key, message)
    }

    @Throws(GeneralSecurityException::class)
    fun decrypt(message: String?, key: String?): String {
        return AESCrypt.decrypt(key, message)
    }
}
