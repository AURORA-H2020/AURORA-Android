package eu.inscico.aurora_app.model.photovoltaics

import com.squareup.moshi.Json
import eu.inscico.aurora_app.model.user.User
import eu.inscico.aurora_app.model.user.UserResponse

data class PhotovoltaicInvestmentResult(
    val amount: Int,
    val producedEnergy: MeasurementUnit,

    /// The carbon emissions produced by the photovoltaic.
    val carbonEmissions: MeasurementUnit,

    /// The carbon emissions without the photovoltaic.
    val normalCarbonEmissions: MeasurementUnit,

    /// The carbon emissions reduction.
    val carbonEmissionsReduction: MeasurementUnit
) {
    companion object {
        fun from(item: PVGISInvestmentResponse, amount: Int): PhotovoltaicInvestmentResult {

            val energyOutput = item.outputs.totals.fixed.energyOutputPerYear
            val carbonEmissions = energyOutput * 0.0305
            val normalCarbonEmissions = energyOutput * 0.116
            val carbonEmissionsReduction = normalCarbonEmissions - carbonEmissions

            return PhotovoltaicInvestmentResult(
                amount = amount,
                producedEnergy = MeasurementUnit(value = energyOutput, unit = MeasurementType.KWH),
                carbonEmissions = MeasurementUnit(value = carbonEmissions, MeasurementType.KG),
                normalCarbonEmissions = MeasurementUnit(value = normalCarbonEmissions, MeasurementType.KG),
                carbonEmissionsReduction = MeasurementUnit(value = carbonEmissionsReduction, MeasurementType.KG)
            )
        }
    }
}

data class MeasurementUnit(
    val value: Double,
    val unit: MeasurementType
)

data class PVGISInvestmentResponse(
    val inputs: InvestmentInputs,
    val outputs: InvestmentOutputs
)

data class InvestmentInputs(
    @Json(name = "pv_module") val pvModule: InvestmentPvModule
)

data class InvestmentPvModule(
    @Json(name = "peak_power") val peakPower: Double
)

data class InvestmentOutputs(
    val totals: InvestmentTotals
)

data class InvestmentTotals(
    val fixed: InvestmentFixed
)

data class InvestmentFixed(
    @Json(name = "E_y") val energyOutputPerYear: Double,
    @Json(name = "E_m") val energyOutputPerMonth: Double,

)

enum class MeasurementType {
    KG,
    KWH
}
