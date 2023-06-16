package eu.inscico.aurora_app.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import eu.inscico.aurora_app.BuildConfig
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.core.MainActivity
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*


object ExternalUtils {

    fun openBrowser(context: Context?, url: String?) {
        if (context == null) return

        // abort if url is null/empty
        if (url.isNullOrEmpty()) return
        var thisUrl = url

        // apply prefix if missing (to prevent parse errors with some browsers)
        if (!thisUrl.toLowerCase().contains("http://") && !thisUrl.toLowerCase()
                .contains("https://")
        ) {
            thisUrl = "http://$thisUrl"
        }

        // create intent and open chooser
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(thisUrl).normalizeScheme()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(Intent.createChooser(intent, null))
    }

    fun createJsonFileAndShare(
        context: Context,
        jsonData: String
    ) {

        // Get file path
        val fileName = "Aurora_Export_${CalendarUtils.toDateString(Calendar.getInstance())}"
        val fileExtension = ".json"
        val filePath = context.filesDir.toString() + "/" + fileName + fileExtension

        // create file
        val sharingFile = File(filePath)
        sharingFile.writeText(jsonData)

        // share file via intent
        val uri = FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            sharingFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        ContextCompat.startActivity(context, Intent.createChooser(shareIntent, context.getString(R.string.settings_export_data_share_dialog)), null)

    }

}