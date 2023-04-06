package eu.inscico.aurora_app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import eu.inscico.aurora_app.services.CountriesService
import org.koin.androidx.compose.get

@Composable
fun HomeScreen(
    _countriesService: CountriesService = get()
){

    val countries = _countriesService.countriesLive.observeAsState(emptyList())


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(text = "Home")
        Text(text = countries.value.firstOrNull()?.countryCode ?: "")
    }
}