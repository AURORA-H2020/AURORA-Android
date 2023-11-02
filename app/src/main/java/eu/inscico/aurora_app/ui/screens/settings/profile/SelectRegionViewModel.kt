package eu.inscico.aurora_app.ui.screens.settings.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import eu.inscico.aurora_app.model.consumptions.HeatingFuelType
import eu.inscico.aurora_app.model.user.RegionEnum
import eu.inscico.aurora_app.utils.PrefsUtils

class SelectRegionViewModel: ViewModel() {

    val allSelectableRegions = RegionEnum.getRegionsList()

    fun saveRegion(context: Context, region: RegionEnum){
        PrefsUtils.save(context, "auroraAppRegion", RegionEnum.parseRegionToString(region))
    }

    fun getSavedRegion(context: Context): RegionEnum {
        val regionString = PrefsUtils.get(context, "auroraAppRegion", RegionEnum.parseRegionToString(RegionEnum.SYSTEM))
        return RegionEnum.parseStringToRegion(regionString)
    }

}