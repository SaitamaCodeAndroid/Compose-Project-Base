package com.season.ui

import android.R
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.season.designsystem.component.NiaBackground
import com.season.designsystem.component.NiaButton
import com.season.designsystem.icon.NiaIcons
import com.season.designsystem.theme.NiaTheme

@Composable
fun MessageActionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconDescription: String? = null,
    @StringRes message: Int,
    @StringRes actionButton: Int,
    onActionButtonClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = icon,
            contentDescription = iconDescription,
        )

        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
            text = stringResource(id = message),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
        )

        NiaButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            onClick = onActionButtonClick
        ) {
            Text(text = stringResource(id = actionButton))
        }
    }
}

@Preview
@Composable
fun PreviewInfoMessageAction() {
    NiaTheme {
        NiaBackground {
            MessageActionItem(
                icon = NiaIcons.NoImages,
                message = R.string.untitled,
                actionButton = R.string.selectAll
            ) {

            }
        }
    }
}
