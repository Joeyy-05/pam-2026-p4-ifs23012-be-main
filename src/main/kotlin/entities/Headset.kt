package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Headset(
    var id : String = UUID.randomUUID().toString(),
    var nama: String,
    var merk: String,
    var deskripsi: String,
    var harga: String,
    var pathGambar: String,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)