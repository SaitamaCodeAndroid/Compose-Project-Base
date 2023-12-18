package com.season.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.season.designsystem.component.DynamicAsyncImage
import com.season.designsystem.theme.NiaTheme
import com.season.model.DisplayFileItem
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun FileCard(item: DisplayFileItem.File) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        DynamicAsyncImage(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            imageUrl = item.path,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.img_placeholder_4_3),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = item.name,
                textAlign = TextAlign.Start,
                maxLines = 1
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                color = LocalContentColor.current.copy(alpha = 0.6f),
                text = item.date.toString(),
                textAlign = TextAlign.Start,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
            )
        }

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewFileCard() {

    val current = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())


    NiaTheme {
        FileCard(
            DisplayFileItem.File(
                id = 0,
                name = "Test image 1",
                path = "",
                date = current
            ),
        )
    }
}
