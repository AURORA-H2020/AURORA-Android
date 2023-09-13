package eu.inscico.aurora_app

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import java.io.File
import java.io.IOException

object ScreenshotTestUtils {

    fun takeScreenshot(screenshotPath: String) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        try {
            device.takeScreenshot(File(screenshotPath))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}