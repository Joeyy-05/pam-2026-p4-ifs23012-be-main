package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.HeadsetRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IHeadsetRepository
import java.io.File
import java.util.*

class HeadsetService(private val headsetRepository: IHeadsetRepository) {

    suspend fun getAllHeadsets(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val headsets = headsetRepository.getHeadsets(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar headset",
            mapOf(Pair("headsets", headsets))
        )
        call.respond(response)
    }

    suspend fun getHeadsetById(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID headset tidak boleh kosong!")
        val headset = headsetRepository.getHeadsetById(id) ?: throw AppException(404, "Data headset tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data headset",
            mapOf(Pair("headset", headset))
        )
        call.respond(response)
    }

    private suspend fun getHeadsetRequest(call: ApplicationCall): HeadsetRequest {
        val headsetReq = HeadsetRequest()
        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)

        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> headsetReq.nama = part.value.trim()
                        "merk" -> headsetReq.merk = part.value.trim()
                        "deskripsi" -> headsetReq.deskripsi = part.value
                        "harga" -> headsetReq.harga = part.value
                    }
                }
                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/headsets/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())
                    headsetReq.pathGambar = filePath
                }
                else -> {}
            }
            part.dispose()
        }
        return headsetReq
    }

    private fun validateHeadsetRequest(headsetReq: HeadsetRequest){
        val validatorHelper = ValidatorHelper(headsetReq.toMap())
        validatorHelper.required("nama", "Nama tidak boleh kosong")
        validatorHelper.required("merk", "Merk tidak boleh kosong")
        validatorHelper.required("deskripsi", "Deskripsi tidak boleh kosong")
        validatorHelper.required("harga", "Harga tidak boleh kosong")
        validatorHelper.required("pathGambar", "Gambar tidak boleh kosong")
        validatorHelper.validate()

        val file = File(headsetReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar headset gagal diupload!")
        }
    }

    suspend fun createHeadset(call: ApplicationCall) {
        val headsetReq = getHeadsetRequest(call)
        validateHeadsetRequest(headsetReq)

        val existHeadset = headsetRepository.getHeadsetByName(headsetReq.nama)
        if(existHeadset != null){
            val tmpFile = File(headsetReq.pathGambar)
            if(tmpFile.exists()) tmpFile.delete()
            throw AppException(409, "Headset dengan nama ini sudah terdaftar!")
        }

        val headsetId = headsetRepository.addHeadset(headsetReq.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data headset",
            mapOf(Pair("headsetId", headsetId))
        )
        call.respond(response)
    }

    suspend fun updateHeadset(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID headset tidak boleh kosong!")
        val oldHeadset = headsetRepository.getHeadsetById(id) ?: throw AppException(404, "Data headset tidak tersedia!")

        val headsetReq = getHeadsetRequest(call)
        if(headsetReq.pathGambar.isEmpty()) headsetReq.pathGambar = oldHeadset.pathGambar

        validateHeadsetRequest(headsetReq)

        if(headsetReq.nama != oldHeadset.nama){
            val existHeadset = headsetRepository.getHeadsetByName(headsetReq.nama)
            if(existHeadset != null){
                val tmpFile = File(headsetReq.pathGambar)
                if(tmpFile.exists()) tmpFile.delete()
                throw AppException(409, "Headset dengan nama ini sudah terdaftar!")
            }
        }

        if(headsetReq.pathGambar != oldHeadset.pathGambar){
            val oldFile = File(oldHeadset.pathGambar)
            if(oldFile.exists()) oldFile.delete()
        }

        val isUpdated = headsetRepository.updateHeadset(id, headsetReq.toEntity())
        if (!isUpdated) throw AppException(400, "Gagal memperbarui data headset!")

        val response = DataResponse("success", "Berhasil mengubah data headset", null)
        call.respond(response)
    }

    suspend fun deleteHeadset(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID headset tidak boleh kosong!")
        val oldHeadset = headsetRepository.getHeadsetById(id) ?: throw AppException(404, "Data headset tidak tersedia!")

        val oldFile = File(oldHeadset.pathGambar)
        val isDeleted = headsetRepository.removeHeadset(id)

        if (!isDeleted) throw AppException(400, "Gagal menghapus data headset!")
        if (oldFile.exists()) oldFile.delete()

        val response = DataResponse("success", "Berhasil menghapus data headset", null)
        call.respond(response)
    }

    suspend fun getHeadsetImage(call: ApplicationCall) {
        val id = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest)
        val headset = headsetRepository.getHeadsetById(id) ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(headset.pathGambar)
        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)

        call.respondFile(file)
    }
}