package eu.inscico.aurora_app.languageTests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import eu.inscico.aurora_app.testEnviroment.ScreenshotTestUtils.takeScreenshot
import eu.inscico.aurora_app.model.consumptions.ConsumptionType
import eu.inscico.aurora_app.testModule
import eu.inscico.aurora_app.ui.screens.home.HomeScreen
import eu.inscico.aurora_app.ui.screens.home.consumptionSummary.ConsumptionSummaryScreen
import eu.inscico.aurora_app.ui.screens.home.consumptions.addConsumption.AddConsumptionScreen
import eu.inscico.aurora_app.ui.screens.settings.SettingsScreen
import eu.inscico.aurora_app.ui.theme.AURORAEnergyTrackerTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.koin.test.KoinTest
import java.io.File
import java.util.*

@RunWith(AndroidJUnit4::class)
class GermanScreenshotTest : KoinTest {
    private val filePath = "storage/emulated/0/Pictures/german"
    private val languageCode = "de"

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        GlobalContext.loadKoinModules(testModule)

        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }

        val dir = File(
            filePath
        )
        if (!dir.exists()) {
            dir.mkdir()
        }
    }

    @Test
    fun takeHomeScreenshot() {

        composeTestRule.setContent {
            AURORAEnergyTrackerTheme {
                HomeScreen(language = Locale(languageCode))
            }
        }

        val screenshotPath = "${filePath}/dashboard-${languageCode}.png"

        try {
            Thread.sleep(10000) // Sleep for 5 seconds
            takeScreenshot(screenshotPath)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun screenshotConsumptionSummary() {

        composeTestRule.setContent {
            AURORAEnergyTrackerTheme {
                ConsumptionSummaryScreen(language = Locale(languageCode))
            }
        }

        val screenshotPath = "${filePath}/consumption_summary-${languageCode}.png"

        takeScreenshot(screenshotPath)
    }

    @Test
    fun screenshotAddHeatingConsumption() {


        composeTestRule.setContent {
            AURORAEnergyTrackerTheme {
                AddConsumptionScreen(preSelected = ConsumptionType.HEATING, language = Locale(languageCode))
            }
        }

        try {
            Thread.sleep(4000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // Pfad, um den Screenshot zu speichern
        val screenshotPath = "${filePath}/add_heating_consumption-${languageCode}.png"

        takeScreenshot(screenshotPath)
    }

    @Test
    fun screenshotSettings() {

        composeTestRule.setContent {
            AURORAEnergyTrackerTheme {
                SettingsScreen(language = Locale(languageCode))
            }
        }

        val screenshotPath = "${filePath}/settings-${languageCode}.png"

        takeScreenshot(screenshotPath)
    }
}