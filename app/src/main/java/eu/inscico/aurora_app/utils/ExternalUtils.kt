package eu.inscico.aurora_app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

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

}