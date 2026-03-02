package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Headset

@Serializable
data class HeadsetRequest(
    var nama: String = "",
    var merk: String = "",
    var deskripsi: String = "",
    var harga: String = "",
    var pathGambar: String = "",
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "merk" to merk,
            "deskripsi" to deskripsi,
            "harga" to harga,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Headset {
        return Headset(
            nama = nama,
            merk = merk,
            deskripsi = deskripsi,
            harga = harga,
            pathGambar = pathGambar,
        )
    }
}