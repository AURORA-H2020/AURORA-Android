package eu.inscico.aurora_app.services.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NetworkService(
    private val _context: Context
) {

    init {
        recheckForConnectivity()
    }

    val hasInternetConnectionLive = MutableLiveData<Boolean>()

    private var checkForConnectivityJob: Job? = null
    fun recheckForConnectivity(){
        checkForConnectivityJob?.cancel()
        checkForConnectivityJob =
            CoroutineScope(Dispatchers.IO).launch {
                val hasInternet = isNetworkAvailable()
                Log.e("AURORACONNECTED", hasInternet.toString())
                hasInternetConnectionLive.postValue(hasInternet)
                delay(1000L)
                recheckForConnectivity()
            }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val networkInfo = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return when {
            networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}