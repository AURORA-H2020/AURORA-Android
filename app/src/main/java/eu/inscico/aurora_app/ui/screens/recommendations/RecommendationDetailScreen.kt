package eu.inscico.aurora_app.ui.screens.recommendations

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.services.navigation.NavigationService
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.services.shared.UserFeedbackService
import eu.inscico.aurora_app.ui.components.AppBar
import eu.inscico.aurora_app.utils.CalendarUtils
import eu.inscico.aurora_app.utils.TypedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecommendationDetailScreen(
    viewModel: RecommendationDetailViewModel = koinViewModel(),
    navigationService: NavigationService = get(),
    userFeedbackService: UserFeedbackService = get(),
    unitService: UnitService = get()
) {

    val context = LocalContext.current
    val config = LocalConfiguration.current
    val recommendation = viewModel.selectedRecommendation.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar(
            title = recommendation.value?.title ?: stringResource(id = R.string.recommendations_fallback_title),
            hasBackNavigation = true,
            backNavigationCallback = {
                navigationService.navControllerTabHome?.popBackStack()
            },
            actionButton = {
                Row {
                    // Context menu with delete and toggle read actions
                    var expanded = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                    
                    Box {
                        Image(
                            painter = painterResource(id = R.drawable.outline_more_vert_24),
                            modifier = Modifier
                                .size(42.dp)
                                .padding(horizontal = 7.dp)
                                .clickable {
                                    expanded.value = true
                                },
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                        
                        DropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false }
                        ) {
                            recommendation.value?.let { rec ->
                                // Toggle read/unread
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (rec.isRead) {
                                                stringResource(id = R.string.recommendations_action_mark_unread)
                                            } else {
                                                stringResource(id = R.string.recommendations_action_mark_read)
                                            }
                                        )
                                    },
                                    onClick = {
                                        expanded.value = false
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val result = viewModel.toggleReadStatus(rec)
                                            when (result) {
                                                is TypedResult.Failure -> {
                                                    userFeedbackService.showSnackbar(R.string.recommendations_update_error)
                                                }
                                                is TypedResult.Success -> {
                                                    // Success - the listener will update the UI
                                                }
                                            }
                                        }
                                    }
                                )
                                
                                // Delete
                                DropdownMenuItem(
                                    text = { Text(stringResource(id = R.string.delete)) },
                                    onClick = {
                                        expanded.value = false
                                        userFeedbackService.showDialog(
                                            message = context.getString(R.string.recommendations_delete_confirmation),
                                            confirmButtonText = context.getString(R.string.delete),
                                            confirmButtonCallback = {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    val result = viewModel.deleteRecommendation(rec)
                                                    when (result) {
                                                        is TypedResult.Failure -> {
                                                            userFeedbackService.showSnackbar(R.string.recommendations_delete_error)
                                                        }
                                                        is TypedResult.Success -> {
                                                            withContext(Dispatchers.Main) {
                                                                navigationService.navControllerTabHome?.popBackStack()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )

        recommendation.value?.let { rec ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Read status and date
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(id = R.string.recommendations_detail_status_label),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (rec.isRead) {
                                    stringResource(id = R.string.recommendations_status_read)
                                } else {
                                    stringResource(id = R.string.recommendations_status_unread)
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = stringResource(id = R.string.recommendations_detail_created_label),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CalendarUtils.toDateString(
                                    rec.createdAt,
                                    unitService.getDateFormat(config)
                                ),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Message
                Text(
                    text = stringResource(id = R.string.recommendations_detail_message_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = rec.message,
                    style = MaterialTheme.typography.bodyLarge
                )

                // Link (if available)
                rec.link?.let { link ->
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(id = R.string.recommendations_detail_learn_more))
                    }
                }
            }
        }
    }
}
