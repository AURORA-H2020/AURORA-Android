package eu.inscico.aurora_app.services.shared

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.*

class UserFeedbackService(private val _context: Context) {

    private var snackbarJob: Job? = null

    var _showDialog = mutableStateOf(false)
    var _snackBarHostState = SnackbarHostState()

    var _showLoadingDialog = mutableStateOf(false)

    // Dialog varibables
    var dialogTitle: String? = null
    var dialogMessage: String? = null
    var dialogConfirmButtonText: String? = null
    var dialogConfirmButtonCallback: () -> Unit = {}

    fun showDialog(
        message: String,
        title: String? = null,
        confirmButtonText: String? = null,
        confirmButtonCallback: () -> Unit = {}
    ) {
        dialogTitle = title
        dialogMessage = message
        dialogConfirmButtonText = confirmButtonText
        dialogConfirmButtonCallback = confirmButtonCallback

        _showDialog.value = true
    }

    @Composable
    fun getDialog() {
        AlertDialog(
            onDismissRequest = {
                _showDialog.value = false
            },
            title = {
                dialogTitle?.let {
                    Text(text = it)
                }
            },
            text = {
                Column() {
                    dialogMessage?.let {
                        Text(it)
                    }
                }
            },
            confirmButton = {
                dialogConfirmButtonText?.let {
                    TextButton(onClick = {
                        dialogConfirmButtonCallback.invoke()
                        _showDialog.value = false
                    }) {
                        Text(text = it)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    _showDialog.value = false
                }) {
                    Text(text = stringResource(id = eu.inscico.aurora_app.R.string.cancel))
                }
            }
        )
    }

    fun showLoadingDialog(){
        _showLoadingDialog.value = true
    }

    fun hideLoadingDialog(){
        _showLoadingDialog.value = false
    }

    @Composable
    fun GetLoadingDialog(){

            Dialog(

                onDismissRequest = { _showLoadingDialog.value = false },
                DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
            ) {
                Box(
                    contentAlignment= Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                ) {
                    CircularProgressIndicator()
                }
            }
        }

    fun showSnackbar(@StringRes messageRes: Int){
        showSnackbar(_context.getString(messageRes))
    }

    fun showSnackbar(message: String?) {
        if (message == null) return

        snackbarJob?.cancel()
        snackbarJob = CoroutineScope(Dispatchers.Default).launch {
                _snackBarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Indefinite,
                )
        }
        CoroutineScope(Dispatchers.Default).launch {
            delay(4000)
            snackbarJob?.cancel()
        }
    }

    @Composable
    fun getSnackbar() {
        SnackbarHost(hostState = _snackBarHostState)
    }

}