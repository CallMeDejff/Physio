package com.example.physio.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Self_improvement: ImageVector
    get() {
        if (_Self_improvement != null) {
            return _Self_improvement!!
        }
        _Self_improvement = ImageVector.Builder(
            name = "Self_improvement",
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
                moveTo(272f, 800f)
                quadToRelative(-30f, 0f, -51f, -21f)
                reflectiveQuadToRelative(-21f, -51f)
                quadToRelative(0f, -21f, 12f, -39.5f)
                reflectiveQuadToRelative(32f, -26.5f)
                lineToRelative(156f, -62f)
                verticalLineToRelative(-90f)
                quadToRelative(-54f, 63f, -125.5f, 96.5f)
                reflectiveQuadTo(120f, 640f)
                verticalLineToRelative(-80f)
                quadToRelative(68f, 0f, 123.5f, -28f)
                reflectiveQuadTo(344f, 452f)
                lineToRelative(54f, -64f)
                quadToRelative(12f, -14f, 28f, -21f)
                reflectiveQuadToRelative(34f, -7f)
                horizontalLineToRelative(40f)
                quadToRelative(18f, 0f, 34f, 7f)
                reflectiveQuadToRelative(28f, 21f)
                lineToRelative(54f, 64f)
                quadToRelative(45f, 52f, 100.5f, 80f)
                reflectiveQuadTo(840f, 560f)
                verticalLineToRelative(80f)
                quadToRelative(-83f, 0f, -154.5f, -33.5f)
                reflectiveQuadTo(560f, 510f)
                verticalLineToRelative(90f)
                lineToRelative(156f, 62f)
                quadToRelative(20f, 8f, 32f, 26.5f)
                reflectiveQuadToRelative(12f, 39.5f)
                quadToRelative(0f, 30f, -21f, 51f)
                reflectiveQuadToRelative(-51f, 21f)
                horizontalLineTo(400f)
                verticalLineToRelative(-20f)
                quadToRelative(0f, -26f, 17f, -43f)
                reflectiveQuadToRelative(43f, -17f)
                horizontalLineToRelative(120f)
                quadToRelative(9f, 0f, 14.5f, -5.5f)
                reflectiveQuadTo(600f, 700f)
                reflectiveQuadToRelative(-5.5f, -14.5f)
                reflectiveQuadTo(580f, 680f)
                horizontalLineTo(460f)
                quadToRelative(-42f, 0f, -71f, 29f)
                reflectiveQuadToRelative(-29f, 71f)
                verticalLineToRelative(20f)
                close()
                moveToRelative(208f, -480f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(400f, 240f)
                reflectiveQuadToRelative(23.5f, -56.5f)
                reflectiveQuadTo(480f, 160f)
                reflectiveQuadToRelative(56.5f, 23.5f)
                reflectiveQuadTo(560f, 240f)
                reflectiveQuadToRelative(-23.5f, 56.5f)
                reflectiveQuadTo(480f, 320f)
            }
        }.build()
        return _Self_improvement!!
    }

private var _Self_improvement: ImageVector? = null