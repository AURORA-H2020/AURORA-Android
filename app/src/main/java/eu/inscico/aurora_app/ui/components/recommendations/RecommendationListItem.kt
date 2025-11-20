package eu.inscico.aurora_app.ui.components.recommendations

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.inscico.aurora_app.R
import eu.inscico.aurora_app.model.recommendations.Recommendation
import eu.inscico.aurora_app.services.shared.UnitService
import eu.inscico.aurora_app.utils.CalendarUtils
import org.koin.androidx.compose.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationListItem(
    unitService: UnitService = get(),
    recommendation: Recommendation,
    callback: (Recommendation) -> Unit,
) {

    val context = LocalContext.current
    val config = LocalConfiguration.current

    val headlineText = recommendation.title ?: stringResource(id = R.string.recommendations_fallback_title)
    
    val createdAtText = CalendarUtils.toDateString(
        recommendation.createdAt, 
        unitService.getDateFormat(config)
    )
    
    val readStatusText = if (recommendation.isRead) {
        stringResource(id = R.string.recommendations_status_read)
    } else {
        stringResource(id = R.string.recommendations_status_unread)
    }

    val iconColor = MaterialTheme.colorScheme.onSurface
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    ListItem(
        modifier = Modifier.clickable {
            callback.invoke(recommendation)
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .drawBehind {
                        drawRoundRect(
                            backgroundColor,
                            cornerRadius = CornerRadius(16.dp.toPx())
                        )
                    }
                    .size(30.dp)
            ) {
                Image(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(6.dp),
                    painter = painterResource(
                        id = if (recommendation.isRead) {
                            R.drawable.outline_mark_email_read_24
                        } else {
                            R.drawable.outline_mark_email_unread_24
                        }
                    ),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = iconColor),
                )
            }
        },
        headlineContent = {
            Text(
                text = headlineText,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = "$createdAtText • $readStatusText",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}
