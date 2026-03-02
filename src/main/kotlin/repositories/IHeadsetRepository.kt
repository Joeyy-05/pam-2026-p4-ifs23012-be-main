package org.delcom.repositories

import org.delcom.entities.Headset

interface IHeadsetRepository {
    suspend fun getHeadsets(search: String): List<Headset>
    suspend fun getHeadsetById(id: String): Headset?
    suspend fun getHeadsetByName(name: String): Headset?
    suspend fun addHeadset(headset: Headset) : String
    suspend fun updateHeadset(id: String, newHeadset: Headset): Boolean
    suspend fun removeHeadset(id: String): Boolean
}