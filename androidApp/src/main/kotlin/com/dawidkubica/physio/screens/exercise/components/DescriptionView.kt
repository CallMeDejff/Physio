package com.dawidkubica.physio.screens.exercise.components

import android.text.Layout
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.AlignmentSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import com.dawidkubica.physio.screens.exercise.ExerciseViewModel
import com.dawidkubica.physio.ui.theme.RedConfirmed
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun DescriptionView(
    viewModel: ExerciseViewModel,
    titleSize: TextUnit = 24.sp,
    subtitleSize: TextUnit = 18.sp,
    packageId: String?
) {
    val packageName = viewModel.packageName.collectAsState()
    val packageDescription = viewModel.packageDescription.collectAsState()
    val packageAuthor = viewModel.packageAuthor.collectAsState()
    val packageAuthorLicense = viewModel.packageAuthorLicense.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = packageName.value.toString(),
                    style = typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                IconButton(
                    onClick = { viewModel.togglePackageFavoriteStatus(packageId.orEmpty()) }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Favorite,
                        contentDescription = "Favorites management",
                        tint = RedConfirmed,
                        modifier = Modifier
                            .padding(2.dp)
                            .size(36.dp)
                    )
                }
            }


            Spacer(modifier = Modifier.size(12.dp))

            val decodedDescription = HtmlCompat.fromHtml(
                packageDescription.value.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            Text(
                text = spannedToAnnotatedString(decodedDescription, titleSize, subtitleSize),
                style = typography.labelMedium,
                modifier = Modifier
                    .align(Alignment.Start)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Autor: " + packageAuthor.value.toString() + "  |  Licencja: " + packageAuthorLicense.value.toString(),
                style = typography.labelSmall
            )

            Spacer(modifier = Modifier.size(12.dp))
        }
    }
}

fun spannedToAnnotatedString(
    spanned: Spanned,
    titleSize: TextUnit = 24.sp,
    subtitleSize: TextUnit = 18.sp,
): AnnotatedString {
    return buildAnnotatedString {
        val text = spanned.toString()
        append(text)

        spanned.getSpans(0, spanned.length, Any::class.java).forEach { span ->
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)

            when (span) {
                is StyleSpan -> {
                    when (span.style) {
                        android.graphics.Typeface.BOLD -> addStyle(
                            SpanStyle(fontWeight = FontWeight.Bold),
                            start,
                            end
                        )

                        android.graphics.Typeface.ITALIC -> addStyle(
                            SpanStyle(fontStyle = FontStyle.Italic),
                            start,
                            end
                        )
                    }
                }

                is UnderlineSpan -> addStyle(
                    SpanStyle(textDecoration = TextDecoration.Underline),
                    start,
                    end
                )

                is ForegroundColorSpan -> addStyle(
                    SpanStyle(color = Color(span.foregroundColor)),
                    start,
                    end
                )

                is AbsoluteSizeSpan -> {
                    val fontSize = if (span.size > 24) titleSize else subtitleSize
                    addStyle(SpanStyle(fontSize = fontSize), start, end)
                }

                is URLSpan -> {
                    addStyle(
                        SpanStyle(
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline
                        ), start, end
                    )
                    addStringAnnotation("URL", span.url, start, end)
                }

                is AlignmentSpan.Standard -> {
                    val textAlign = when (span.alignment) {
                        Layout.Alignment.ALIGN_CENTER -> TextAlign.Center
                        Layout.Alignment.ALIGN_OPPOSITE -> TextAlign.End
                        else -> TextAlign.Start
                    }
                    addStyle(ParagraphStyle(textAlign = textAlign), start, end)
                }
            }
        }
    }
}
