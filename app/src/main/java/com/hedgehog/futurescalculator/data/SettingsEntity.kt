package com.hedgehog.futurescalculator.data

import androidx.datastore.core.Serializer
import com.google.gson.Gson
import java.io.InputStream
import java.io.OutputStream

data class SettingsEntity(
    val entryPrice: Float = 0f,
    val profit: Int = 0,
    val loss: Int = 0,
    val leverage: Int = 0
)

class SettingsEntitySerializer(
    private val encryptionManager: EncryptionManager
) : Serializer<SettingsEntity> {
    override val defaultValue: SettingsEntity
        get() = SettingsEntity(0f, 0, 0, 1)

    override suspend fun readFrom(input: InputStream): SettingsEntity {
        val decryptedBytes = encryptionManager.decrypt(input)
        return try {
            Gson().fromJson(String(decryptedBytes), SettingsEntity::class.java)
        } catch (ex: Exception) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: SettingsEntity, output: OutputStream) {
        encryptionManager.encrypt(
            bytes = Gson().toJson(t).toByteArray(),
            output
        )
    }
}
