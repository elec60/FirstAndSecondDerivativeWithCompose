package com.hashem.firstandsecondderivativewithcompose

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt

@Composable
fun MainScreen() {
    val animatable = rememberInfiniteTransition(label = "animation")
    val animationProgress = animatable.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        label = "animation",
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000),
        )
    )

    val backgroundColor = Color(0xFFF4F6F9)
    val panelDividerColor = Color(0xFFD8DEE7)
    val axisColor = Color(0xFF5B6B7C)
    val fxColor = Color(0xFF0B5FA5)
    val fPrimeColor = Color(0xFF0E8A7D)
    val fPrime2Color = Color(0xFF6C4BD1)
    val tangentColor = Color(0xFFE0A100)
    val guideColor = Color(0xFF8A97A5)

    val labelStyle = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )

    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        drawRect(color = backgroundColor)
        val canvasWidth = size.width
        val canvasHeight = size.height

        val halfWidth = canvasWidth / 2
        val oneThirdHeight = canvasHeight / 3
        val step = 0.01f
        val startX = -3f
        val endX = 3f
        val sampleSize = ((endX - startX) / step + 1).toInt()
        val stepPx = canvasWidth / sampleSize
        val h = oneThirdHeight / 2

        val fx = FloatArray(sampleSize) { i ->
            val x = startX + i * step
            exp(-x * x)
        }
        val fPrime = FloatArray(sampleSize) { i ->
            val x = startX + i * step
            -2 * x * exp(-x * x)
        }
        val fPrime2 = FloatArray(sampleSize) { i ->
            val x = startX + i * step
            -2 * exp(-x * x) + 4 * x * x * exp(-x * x)
        }

        val fxMax = fx.maxOf { abs(it) }
        val fPrimeMax = fPrime.maxOf { abs(it) }
        val fPrime2Max = fPrime2.maxOf { abs(it) }

        val fxNormalized = fx.map { it / fxMax }
        val fPrimeNormalized = fPrime.map { it / fPrimeMax }
        val fPrime2Normalized = fPrime2.map { it / fPrime2Max }

        val scaleYFx = h / fxMax
        val scaleXPerUnit = canvasWidth / (endX - startX)

        val top1 = oneThirdHeight / 2f
        val top2 = oneThirdHeight + oneThirdHeight / 2f
        val top3 = 2 * oneThirdHeight + oneThirdHeight / 2f

        drawLine(
            start = Offset(0f, oneThirdHeight),
            end = Offset(canvasWidth, oneThirdHeight),
            color = panelDividerColor,
            strokeWidth = 1.5f
        )
        drawLine(
            start = Offset(0f, 2 * oneThirdHeight),
            end = Offset(canvasWidth, 2 * oneThirdHeight),
            color = panelDividerColor,
            strokeWidth = 1.5f
        )

        val currentIndex = (animationProgress.value * (sampleSize - 1)).roundToInt()
            .coerceIn(0, sampleSize - 1)
        val currentXPx = currentIndex * stepPx

        val fxCurrentY = top1 - fxNormalized[currentIndex] * h
        val fPrimeCurrentY = top2 - fPrimeNormalized[currentIndex] * h
        val fPrime2CurrentY = top3 - fPrime2Normalized[currentIndex] * h

        // f(x) = e^(-x^2)
        translate(left = halfWidth, top = top1) {
            drawLine(
                start = Offset(-halfWidth, 0f),
                end = Offset(halfWidth, 0f),
                color = axisColor,
                strokeWidth = 2f
            )
            drawLine(
                start = Offset(0f, -h),
                end = Offset(0f, h),
                color = axisColor,
                strokeWidth = 2f
            )

            drawText(
                textMeasurer = textMeasurer,
                text = "f(x)",
                topLeft = Offset(-halfWidth + 12f, -h + 8f),
                style = labelStyle.copy(color = fxColor)
            )

            fxNormalized.indices.forEach { index ->
                if (index < fxNormalized.size - 1) {
                    val firstY = fxNormalized[index]
                    val secondY = fxNormalized[index + 1]
                    drawLine(
                        start = Offset(index * stepPx - halfWidth, -firstY * h),
                        end = Offset((index + 1) * stepPx - halfWidth, -secondY * h),
                        color = fxColor,
                        strokeWidth = 4f
                    )
                }
            }

            val screenSlope = (fPrime[currentIndex] * scaleYFx) / scaleXPerUnit
            val halfSegLenPx = 100f
            val centerLocalX = currentXPx - halfWidth
            val centerLocalY = -fxNormalized[currentIndex] * h
            val dx = halfSegLenPx
            val dy = halfSegLenPx * screenSlope
            drawLine(
                start = Offset(centerLocalX - dx, centerLocalY + dy),
                end = Offset(centerLocalX + dx, centerLocalY - dy),
                color = tangentColor,
                strokeWidth = 5f
            )
            drawCircle(
                color = fxColor,
                radius = 8f,
                center = Offset(centerLocalX, centerLocalY)
            )
        }

        // f'(x)
        translate(left = halfWidth, top = top2) {
            drawLine(
                start = Offset(-halfWidth, 0f),
                end = Offset(halfWidth, 0f),
                color = axisColor,
                strokeWidth = 2f
            )
            drawLine(
                start = Offset(0f, -h),
                end = Offset(0f, h),
                color = axisColor,
                strokeWidth = 2f
            )

            drawText(
                textMeasurer = textMeasurer,
                text = "f'(x)",
                topLeft = Offset(-halfWidth + 12f, -h + 8f),
                style = labelStyle.copy(color = fPrimeColor)
            )

            for (index in 0 until currentIndex) {
                val firstY = fPrimeNormalized[index]
                val secondY = fPrimeNormalized[index + 1]
                drawLine(
                    start = Offset(index * stepPx - halfWidth, -firstY * h),
                    end = Offset((index + 1) * stepPx - halfWidth, -secondY * h),
                    color = fPrimeColor,
                    strokeWidth = 4f
                )
            }

            val scaleYFPrime = h / fPrimeMax
            val screenSlope2 = (fPrime2[currentIndex] * scaleYFPrime) / scaleXPerUnit
            val halfSegLenPx2 = 100f
            val centerLocalX2 = currentXPx - halfWidth
            val centerLocalY2 = -fPrimeNormalized[currentIndex] * h
            val dx2 = halfSegLenPx2
            val dy2 = halfSegLenPx2 * screenSlope2
            drawLine(
                start = Offset(centerLocalX2 - dx2, centerLocalY2 + dy2),
                end = Offset(centerLocalX2 + dx2, centerLocalY2 - dy2),
                color = tangentColor,
                strokeWidth = 5f
            )

            drawCircle(
                color = fPrimeColor,
                radius = 8f,
                center = Offset(currentXPx - halfWidth, -fPrimeNormalized[currentIndex] * h)
            )
        }

        // f"(x)
        translate(left = halfWidth, top = top3) {
            drawLine(
                start = Offset(-halfWidth, 0f),
                end = Offset(halfWidth, 0f),
                color = axisColor,
                strokeWidth = 2f
            )
            drawLine(
                start = Offset(0f, -h),
                end = Offset(0f, h),
                color = axisColor,
                strokeWidth = 2f
            )

            drawText(
                textMeasurer = textMeasurer,
                text = "f''(x)",
                topLeft = Offset(-halfWidth + 12f, -h + 8f),
                style = labelStyle.copy(color = fPrime2Color)
            )

            for (index in 0 until currentIndex) {
                val firstY = fPrime2Normalized[index]
                val secondY = fPrime2Normalized[index + 1]
                drawLine(
                    start = Offset(index * stepPx - halfWidth, -firstY * h),
                    end = Offset((index + 1) * stepPx - halfWidth, -secondY * h),
                    color = fPrime2Color,
                    strokeWidth = 4f
                )
            }

            drawCircle(
                color = fPrime2Color,
                radius = 8f,
                center = Offset(currentXPx - halfWidth, -fPrime2Normalized[currentIndex] * h)
            )
        }

        drawLine(
            start = Offset(currentXPx, fxCurrentY),
            end = Offset(currentXPx, fPrime2CurrentY),
            color = guideColor,
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
        )
        drawCircle(color = guideColor, radius = 4f, center = Offset(currentXPx, fxCurrentY))
        drawCircle(color = guideColor, radius = 4f, center = Offset(currentXPx, fPrimeCurrentY))
        drawCircle(color = guideColor, radius = 4f, center = Offset(currentXPx, fPrime2CurrentY))
    }
}