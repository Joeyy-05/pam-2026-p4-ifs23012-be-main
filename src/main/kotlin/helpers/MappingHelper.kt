package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.PlantDAO
import org.delcom.entities.Plant
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun daoToModel(dao: PlantDAO) = Plant(
    dao.id.value.toString(),
    dao.nama,
    dao.pathGambar,
    dao.deskripsi,
    dao.manfaat,
    dao.efekSamping,
    dao.createdAt,
    dao.updatedAt
)

fun daoToHeadsetModel(dao: org.delcom.dao.HeadsetDAO) = org.delcom.entities.Headset(
    dao.id.value.toString(),
    dao.nama,
    dao.merk,
    dao.deskripsi,
    dao.harga,
    dao.pathGambar,
    dao.createdAt,
    dao.updatedAt
)