package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PlantRepository
import org.delcom.services.PlantService
import org.delcom.repositories.IHeadsetRepository
import org.delcom.repositories.HeadsetRepository
import org.delcom.services.HeadsetService
import org.delcom.services.ProfileService
import org.koin.dsl.module

val appModule = module {
    // Plant
    single<IPlantRepository> { PlantRepository() }
    single { PlantService(get()) }

    // Headset
    single<IHeadsetRepository> { HeadsetRepository() }
    single { HeadsetService(get()) }

    // Profile
    single { ProfileService() }
}