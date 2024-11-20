package com.example.physio.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Timelapse: ImageVector
    get() {
        if (_Timelapse != null) {
            return _Timelapse!!
        }
        _Timelapse = ImageVector.Builder(
            name = "Timelapse",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(480f, 720f)
                quadToRelative(100f, 0f, 170f, -70f)
                reflectiveQuadToRelative(70f, -170f)
                reflectiveQuadToRelative(-70f, -170f)
                reflectiveQuadToRelative(-170f, -70f)
                verticalLineToRelative(240f)
                lineTo(310f, 650f)
                quadToRelative(35f, 33f, 78.5f, 51.5f)
                reflectiveQuadTo(480f, 720f)
                moveToRelative(0f, 160f)
                quadToRelative(-83f, 0f, -156f, -31.5f)
                reflectiveQuadTo(197f, 763f)
                reflectiveQuadToRelative(-85.5f, -127f)
                reflectiveQuadTo(80f, 480f)
                reflectiveQuadToRelative(31.5f, -156f)
                reflectiveQuadTo(197f, 197f)
                reflectiveQuadToRelative(127f, -85.5f)
                reflectiveQuadTo(480f, 80f)
                reflectiveQuadToRelative(156f, 31.5f)
                reflectiveQuadTo(763f, 197f)
                reflectiveQuadToRelative(85.5f, 127f)
                reflectiveQuadTo(880f, 480f)
                reflectiveQuadToRelative(-31.5f, 156f)
                reflectiveQuadTo(763f, 763f)
                reflectiveQuadToRelative(-127f, 85.5f)
                reflectiveQuadTo(480f, 880f)
                moveToRelative(0f, -80f)
                quadToRelative(134f, 0f, 227f, -93f)
                reflectiveQuadToRelative(93f, -227f)
                reflectiveQuadToRelative(-93f, -227f)
                reflectiveQuadToRelative(-227f, -93f)
                reflectiveQuadToRelative(-227f, 93f)
                reflectiveQuadToRelative(-93f, 227f)
                reflectiveQuadToRelative(93f, 227f)
                reflectiveQuadToRelative(227f, 93f)
                moveToRelative(0f, -320f)
            }
        }.build()
        return _Timelapse!!
    }

private var _Timelapse: ImageVector? = null
