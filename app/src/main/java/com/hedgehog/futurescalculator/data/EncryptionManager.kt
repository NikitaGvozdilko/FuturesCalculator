package com.hedgehog.futurescalculator.data

import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class EncryptionManager @Inject constructor(
    private val keyStoreDataSource: KeyStoreDataSource
) {
//    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
//        load(null)
//    }

    private fun getEncryptionCipher(): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, keyStoreDataSource.getKey())
        }
    }

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, keyStoreDataSource.getKey(), IvParameterSpec(iv))
        }
    }

//    private fun getKey(): SecretKey {
//        val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
//        return existingKey?.secretKey ?: createKey()
//    }
//
//    private fun createKey(): SecretKey {
//        return KeyGenerator.getInstance(ALGORITHM).apply {
//            init(
//                KeyGenParameterSpec.Builder(
//                    "secret",
//                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
//                )
//                    .setBlockModes(BLOCK_MODE)
//                    .setEncryptionPaddings(PADDING)
//                    .setUserAuthenticationRequired(false)
//                    .setRandomizedEncryptionRequired(true)
//                    .build()
//            )
//        }.generateKey()
//    }

    fun encrypt(bytes: ByteArray, outputStream: OutputStream? = null): ByteArray {
        val encryptCipher = getEncryptionCipher()
        val encryptedBytes = encryptCipher.doFinal(bytes)
        outputStream?.use {
            it.write(encryptCipher.iv.size)
            it.write(encryptCipher.iv)
            it.write(encryptedBytes.size)
            it.write(encryptedBytes)
        }
        return encryptedBytes
    }

//    fun encrypt(data: String): String {
//        val bytes = encryptCipher.doFinal(data.toByteArray())
//        return Base64.encodeToString(bytes, Base64.DEFAULT)
//    }
//
//    fun decrypt(data: String): String {
//        encryptCipher.init(Cipher.DECRYPT_MODE, getKey())
//        val encryptedData = Base64.decode(data, Base64.DEFAULT)
//        val decodedData = encryptCipher.doFinal(encryptedData)
//        return String(decodedData)
//    }

    fun decrypt(inputStream: InputStream): ByteArray {
        return inputStream.use {
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)

            val encryptedBytesSize = it.read()
            val encryptedBytes = ByteArray(encryptedBytesSize)
            it.read(encryptedBytes)

            getDecryptCipherForIv(iv).doFinal(encryptedBytes)
        }
    }

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}