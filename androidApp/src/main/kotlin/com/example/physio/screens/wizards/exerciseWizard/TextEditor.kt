package com.example.physio.screens.wizards.exerciseWizard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.physio.screens.wizards.CreatorWizardViewModel
import com.example.physio.ui.colorPrimary
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor

@Composable
fun TextEditorView(
    initialDescription: String,
    viewModel: CreatorWizardViewModel
) {
    val state = rememberRichTextState()
    val titleSize = MaterialTheme.typography.displaySmall.fontSize
    val subtitleSize = MaterialTheme.typography.titleLarge.fontSize

    LaunchedEffect(Unit) {
        state.setHtml(initialDescription)
    }

    Scaffold {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .padding(bottom = it.calculateBottomPadding())
                .padding(top = it.calculateTopPadding()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EditorControls(
                modifier = Modifier.weight(2f),
                state = state,
                onBoldClick = { state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) },
                onItalicClick = { state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) },
                onUnderlineClick = { state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline)) },
                onTitleClick = { state.toggleSpanStyle(SpanStyle(fontSize = titleSize)) },
                onSubtitleClick = { state.toggleSpanStyle(SpanStyle(fontSize = subtitleSize)) },
                onTextColorClick = { state.toggleSpanStyle(SpanStyle(color = Color.Red)) },
                onStartAlignClick = { state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Start)) },
                onEndAlignClick = { state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.End)) },
                onCenterAlignClick = { state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Center)) },
                onExportClick = {
                    val description = state.toHtml()
                    viewModel.updateExerciseDescription(description)

                }
            )

            RichTextEditor(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(8f),
                state = state,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorControls(
    modifier: Modifier = Modifier,
    state: RichTextState,
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onUnderlineClick: () -> Unit,
    onTitleClick: () -> Unit,
    onSubtitleClick: () -> Unit,
    onTextColorClick: () -> Unit,
    onStartAlignClick: () -> Unit,
    onEndAlignClick: () -> Unit,
    onCenterAlignClick: () -> Unit,
    onExportClick: () -> Unit,
) {
    var boldSelected by rememberSaveable { mutableStateOf(false) }
    var italicSelected by rememberSaveable { mutableStateOf(false) }
    var underlineSelected by rememberSaveable { mutableStateOf(false) }
    var titleSelected by rememberSaveable { mutableStateOf(false) }
    var subtitleSelected by rememberSaveable { mutableStateOf(false) }
    var textColorSelected by rememberSaveable { mutableStateOf(false) }
    var alignmentSelected by remember { mutableIntStateOf(0) }
    var showLinkDialog by remember { mutableStateOf(false) }

    if (showLinkDialog) {
        LinkDialog(
            onDismiss = { showLinkDialog = false },
            onLinkAdded = { text, url ->
                state.addLink(text = text, url = url)
                showLinkDialog = false
            }
        )
    }

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(bottom = 20.dp, top = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        ControlWrapper(
            selected = boldSelected,
            onChangeClick = { boldSelected = it },
            onClick = onBoldClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatBold,
                contentDescription = "Bold Control",
                tint = if (boldSelected) Color.White else colorPrimary
            )
        }
        ControlWrapper(
            selected = italicSelected,
            onChangeClick = { italicSelected = it },
            onClick = onItalicClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatItalic,
                contentDescription = "Italic Control",
                tint = if (italicSelected) Color.White else colorPrimary
            )
        }
        ControlWrapper(
            selected = underlineSelected,
            onChangeClick = { underlineSelected = it },
            onClick = onUnderlineClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatUnderlined,
                contentDescription = "Underline Control",
                tint = if (underlineSelected) Color.White else colorPrimary
            )
        }
        ControlWrapper(
            selected = titleSelected,
            onChangeClick = { titleSelected = it },
            onClick = onTitleClick
        ) {
            Icon(
                imageVector = Icons.Default.Title,
                contentDescription = "Title Control",
                tint = if (titleSelected) Color.White else colorPrimary
            )
        }
        ControlWrapper(
            selected = subtitleSelected,
            onChangeClick = { subtitleSelected = it },
            onClick = onSubtitleClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatSize,
                contentDescription = "Subtitle Control",
                tint = if (subtitleSelected) Color.White else colorPrimary
            )
        }
        ControlWrapper(
            selected = textColorSelected,
            onChangeClick = { textColorSelected = it },
            onClick = onTextColorClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatColorText,
                contentDescription = "Text Color Control",
                tint = if (textColorSelected) Color.White else colorPrimary
            )
        }
        ControlWrapper(
            selected = false,
            onChangeClick = { showLinkDialog = it },
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Default.AddLink,
                contentDescription = "Link Control",
                tint = colorPrimary
            )
        }
        ControlWrapper(
            selected = alignmentSelected == 1,
            onChangeClick = { alignmentSelected = if (it) 1 else 0 },
            onClick = onStartAlignClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.FormatAlignLeft,
                contentDescription = "Left Align Control",
                tint = if (alignmentSelected == 1) Color.White else colorPrimary
            )
        }
        ControlWrapper(
            selected = alignmentSelected == 2,
            onChangeClick = { alignmentSelected = if (it) 2 else 0 },
            onClick = onCenterAlignClick
        ) {
            Icon(
                imageVector = Icons.Default.FormatAlignCenter,
                contentDescription = "Center Align Control",
                tint = if (alignmentSelected == 2) Color.White else colorPrimary
            )
        }
        ControlWrapper(
            selected = alignmentSelected == 3,
            onChangeClick = { alignmentSelected = if (it) 3 else 0 },
            onClick = onEndAlignClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.FormatAlignRight,
                contentDescription = "Right Align Control",
                tint = if (alignmentSelected == 3) Color.White else colorPrimary
            )
        }
        ControlWrapper(
            selected = false,
            onChangeClick = {},
            onClick = onExportClick
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Save Control",
                tint = colorPrimary
            )
        }
    }
}

@Composable
fun ControlWrapper(
    selected: Boolean,
    onChangeClick: (Boolean) -> Unit,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 2.dp,
                color = colorPrimary,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = if (selected) colorPrimary else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                onChangeClick(!selected)
                onClick()
            }
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun LinkDialog(
    onDismiss: () -> Unit,
    onLinkAdded: (String, String) -> Unit
) {
    var linkText by remember { mutableStateOf("") }
    var linkUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onLinkAdded(linkText, linkUrl)
                }
            ) {
                Text("Dodaj")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Anuluj")
            }
        },
        title = { Text(text = "Dodaj link") },
        text = {
            Column {
                OutlinedTextField(
                    value = linkText,
                    onValueChange = { linkText = it },
                    label = { Text("Opis linku") },
                )
                OutlinedTextField(
                    value = linkUrl,
                    onValueChange = { linkUrl = it },
                    label = { Text("URL linku") },
                )
            }
        }
    )
}