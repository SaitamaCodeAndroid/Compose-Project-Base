package com.season.exportresult

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.season.designsystem.component.NiaBackground
import com.season.designsystem.component.NiaButton
import com.season.designsystem.component.NiaLoadingWheel
import com.season.designsystem.icon.NiaIcons
import com.season.designsystem.theme.NiaTheme

@Composable
internal fun ExportResultRoute(
    viewModel: ExportResultViewModel = hiltViewModel(),
) {

    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExportResultScreen(
        uiState = uiState,
        onShareClicked = {
            viewModel.onShareClicked {
                context.startActivity(it)
            }
        },
        onHomeClicked = {
            viewModel.onHomeClicked()
            (context as Activity).finish()
        }
    )
}

@Composable
private fun ExportResultScreen(
    uiState: ExportResultUiState,
    onShareClicked: () -> Unit,
    onHomeClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {

        when (uiState) {
            ExportResultUiState.Loading -> {
                NiaLoadingWheel(
                    contentDesc = "detect cropping image",
                )

                Text(
                    text = "Exporting pdf...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            ExportResultUiState.Error -> {
                Icon(
                    modifier = Modifier.size(96.dp),
                    painter = rememberVectorPainter(image = NiaIcons.Error),
                    contentDescription = "Export success",
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Oops! Something went wrong")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We failed to generate pdf file, please try again",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(24.dp))

                NiaButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onHomeClicked,
                    text = {
                        Text(
                            text = "Return home",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = rememberVectorPainter(image = NiaIcons.Home),
                            contentDescription = ""
                        )
                    }
                )
            }

            is ExportResultUiState.Shown -> {
                ExportResultUiStateShown(
                    uiState = uiState,
                    onShareClicked = onShareClicked,
                    onHomeClicked = onHomeClicked,
                )
            }

        }
    }
}

@Composable
private fun ExportResultUiStateShown(
    uiState: ExportResultUiState.Shown,
    onShareClicked: () -> Unit,
    onHomeClicked: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            modifier = Modifier.size(96.dp),
            painter = rememberVectorPainter(image = NiaIcons.Checked),
            contentDescription = "Export success",
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Export successfully",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        InformationRowItem(title = "Name", content = uiState.fileName)

        InformationRowItem(title = "Size", content = uiState.fileSize)

        InformationRowItem(title = "Page size", content = uiState.pageSize)

        InformationRowItem(title = "Page margin", content = uiState.pageMargin)

        InformationRowItem(title = "Number of pages", content = uiState.numberOfPages.toString())

        InformationRowItem(title = "Location", content = uiState.fileLocation)

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            NiaButton(
                modifier = Modifier.weight(1f),
                onClick = onShareClicked,
                text = {
                    Text(
                        text = "Share",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = rememberVectorPainter(image = NiaIcons.Share),
                        contentDescription = ""
                    )
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            NiaButton(
                modifier = Modifier.weight(1f),
                onClick = onHomeClicked,
                text = {
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = rememberVectorPainter(image = NiaIcons.Home),
                        contentDescription = ""
                    )
                }
            )

        }
    }
}

@Composable
private fun InformationRowItem(
    title: String,
    content: String,
) {
    Row {
        Text(
            text = "$title: ",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium
        )
    }

}

@Preview
@Composable
private fun PreviewExportResultScreenLoading() {
    NiaTheme {
        NiaBackground {
            ExportResultScreen(
                uiState = ExportResultUiState.Loading,
                onShareClicked = {},
                onHomeClicked = {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewExportResultScreenError() {
    NiaTheme {
        NiaBackground {
            ExportResultScreen(
                uiState = ExportResultUiState.Error,
                onShareClicked = {},
                onHomeClicked = {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewExportResultScreenShown() {
    NiaTheme {
        NiaBackground {
            ExportResultScreen(
                uiState = ExportResultUiState.Shown(
                    fileName = "00102-5488829.pdf",
                    fileLocation = "Downloads/monday/00102-5488829.pdf",
                    pageMargin = ".75in",
                    numberOfPages = 3,
                    pageSize = "A4 (28X110mm)",
                    fileSize = "2.5mb",
                ),
                onShareClicked = {},
                onHomeClicked = {},
            )
        }
    }
}
