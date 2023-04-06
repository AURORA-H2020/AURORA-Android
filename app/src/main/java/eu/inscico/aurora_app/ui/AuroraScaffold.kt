package eu.inscico.aurora_app.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.inscico.aurora_app.ui.theme.AURORAEnergyTrackerTheme

@Composable
fun AuroraScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable (() -> Unit) = {},
    bottomBar: @Composable (() -> Unit) = {},
    snackBarHost: @Composable ((SnackbarHostState) -> Unit) = {},
    floatingActionButton: @Composable (() -> Unit) = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable ((PaddingValues) -> Unit) = {}
){

    AURORAEnergyTrackerTheme() {
        Scaffold(
            modifier = modifier,
            topBar = topBar,
            bottomBar = bottomBar,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            snackbarHost = snackBarHost,
            content = content,
        )
    }
}