package eu.inscico.aurora_app.services.firebase

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import eu.inscico.aurora_app.model.ConsumptionBulkResponse
import eu.inscico.aurora_app.model.user.User
import eu.inscico.aurora_app.model.user.UserResponse
import eu.inscico.aurora_app.services.jsonParsing.JsonParsingService
import eu.inscico.aurora_app.utils.ExternalUtils
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.net.URL

class CloudFunctionsService(
    private val context: Context,
    private val _functions: FirebaseFunctions,
    private val _jsonParsingService: JsonParsingService
) {

    fun downloadUserData(
        resultCallback: (Boolean, String?) -> Unit
    ) {

        val result = _functions
            .getHttpsCallable("/downloadUserData")
            .call()

        result.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                if (task.result != null) {
                    try {
                        val help = task.result.data as HashMap<*, *>
                        val help2 = JSONObject(help).toString()
                        resultCallback.invoke(true, help2)
                        //ExternalUtils.createJsonFileAndShare(context, help2)
                    } catch (e: java.lang.Exception) {
                        resultCallback.invoke(false, null)
                    }
                } else {
                    resultCallback.invoke(false, null)
                }
            }
        }

    }
}