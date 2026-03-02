package org.delcom.repositories

import org.delcom.dao.HeadsetDAO
import org.delcom.entities.Headset
import org.delcom.helpers.daoToHeadsetModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.HeadsetTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import kotlinx.datetime.Clock
import java.util.UUID

class HeadsetRepository : IHeadsetRepository {
    override suspend fun getHeadsets(search: String): List<Headset> = suspendTransaction {
        if (search.isBlank()) {
            HeadsetDAO.all()
                .orderBy(HeadsetTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToHeadsetModel)
        } else {
            val keyword = "%${search.lowercase()}%"
            HeadsetDAO
                .find { HeadsetTable.nama.lowerCase() like keyword }
                .orderBy(HeadsetTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToHeadsetModel)
        }
    }

    override suspend fun getHeadsetById(id: String): Headset? = suspendTransaction {
        HeadsetDAO
            .find { (HeadsetTable.id eq UUID.fromString(id)) }
            .limit(1)
            .map(::daoToHeadsetModel)
            .firstOrNull()
    }

    override suspend fun getHeadsetByName(name: String): Headset? = suspendTransaction {
        HeadsetDAO
            .find { (HeadsetTable.nama eq name) }
            .limit(1)
            .map(::daoToHeadsetModel)
            .firstOrNull()
    }

    override suspend fun addHeadset(headset: Headset): String = suspendTransaction {
        val headsetDAO = HeadsetDAO.new {
            nama = headset.nama
            merk = headset.merk
            deskripsi = headset.deskripsi
            harga = headset.harga
            pathGambar = headset.pathGambar
            createdAt = headset.createdAt
            updatedAt = headset.updatedAt
        }
        headsetDAO.id.value.toString()
    }

    override suspend fun updateHeadset(id: String, newHeadset: Headset): Boolean = suspendTransaction {
        val headsetDAO = HeadsetDAO
            .find { HeadsetTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (headsetDAO != null) {
            headsetDAO.nama = newHeadset.nama
            headsetDAO.merk = newHeadset.merk
            headsetDAO.deskripsi = newHeadset.deskripsi
            headsetDAO.harga = newHeadset.harga
            headsetDAO.pathGambar = newHeadset.pathGambar
            headsetDAO.updatedAt = Clock.System.now()
            true
        } else {
            false
        }
    }

    override suspend fun removeHeadset(id: String): Boolean = suspendTransaction {
        val rowsDeleted = HeadsetTable.deleteWhere {
            HeadsetTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}