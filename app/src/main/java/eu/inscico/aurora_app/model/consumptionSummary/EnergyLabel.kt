package eu.inscico.aurora_app.model.consumptionSummary

import android.content.Context
import androidx.compose.ui.graphics.Color
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.ui.theme.*

enum class EnergyLabel {
    A_PLUS,
    A,
    B,
    C,
    D,
    E,
    F,
    G;

    companion object {
        fun EnergyLabel.getLabelColor(): Color {
            return when (this) {
                A_PLUS -> energyLabelAPlus
                A -> energyLabelA
                B -> energyLabelB
                C -> energyLabelC
                D -> energyLabelD
                E -> energyLabelE
                F -> energyLabelF
                G -> energyLabelG
            }
        }

        fun EnergyLabel.getLabelNameRes(): Int {
            return when (this) {
                A_PLUS -> R.string.home_your_carbon_emissions_labels_a_plus_title
                A -> R.string.home_your_carbon_emissions_labels_a_title
                B -> R.string.home_your_carbon_emissions_labels_b_title
                C -> R.string.home_your_carbon_emissions_labels_c_title
                D -> R.string.home_your_carbon_emissions_labels_d_title
                E -> R.string.home_your_carbon_emissions_labels_e_title
                F -> R.string.home_your_carbon_emissions_labels_f_title
                G -> R.string.home_your_carbon_emissions_labels_g_title
            }
        }

        fun EnergyLabel.getLabelName(context: Context): String {
            return context.getString(this.getLabelNameRes())
        }

        fun parseEnergyLabelToString(label: EnergyLabel?): String? {
            return when (label) {
                A_PLUS -> "A+"
                A -> "A"
                B -> "B"
                C -> "C"
                D -> "D"
                E -> "E"
                F -> "F"
                G -> "G"
                null -> null
            }
        }

        fun parseStringToEnergyLabel(labelString: String?): EnergyLabel? {
            return when (labelString) {
                "A+" -> A_PLUS
                "A" -> A
                "B" -> B
                "C" -> C
                "D" -> D
                "E" -> E
                "F" -> F
                "G" -> G
                else -> null
            }
        }
    }
}