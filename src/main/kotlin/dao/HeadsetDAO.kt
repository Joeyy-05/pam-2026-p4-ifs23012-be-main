package org.delcom.dao

import org.delcom.tables.HeadsetTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class HeadsetDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, HeadsetDAO>(HeadsetTable)

    var nama by HeadsetTable.nama
    var merk by HeadsetTable.merk
    var deskripsi by HeadsetTable.deskripsi
    var harga by HeadsetTable.harga
    var pathGambar by HeadsetTable.pathGambar
    var createdAt by HeadsetTable.createdAt
    var updatedAt by HeadsetTable.updatedAt
}