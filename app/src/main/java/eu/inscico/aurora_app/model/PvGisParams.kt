package eu.inscico.aurora_app.model

data class PvGisParams(
    val angle: Double,
    val aspect: Double,
    val investmentFactor: Double,
    val lat: Double,
    val long: Double
) {
    companion object {
        fun from(item: PvgisParamsResponse): PvGisParams? {
            return PvGisParams(
                angle = item.angle ?: return null,
                aspect = item.aspect ?: return null,
                investmentFactor = item.investmentFactor ?: return null,
                lat = item.lat ?: return null,
                long = item.long ?: return null
            )
        }
    }
}
