package com.season.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.season.designsystem.icon.NiaIcons
import com.season.designsystem.theme.NiaTheme

@Composable
fun FloatingCameraButton(
    onClick: () -> Unit,
    isExpanded: Boolean = true,
) {
    ExtendedFloatingActionButton(
        text = {
            Text(text = "Scan")
        },
        icon = {
            Icon(
                imageVector = NiaIcons.Scan,
                contentDescription = "Open Camera",
            )
        },
        expanded = isExpanded,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
    )
}

@Preview
@Composable
private fun PreviewFloatingCameraButton() {
    NiaTheme {
        FloatingCameraButton(onClick = {})
    }
}
