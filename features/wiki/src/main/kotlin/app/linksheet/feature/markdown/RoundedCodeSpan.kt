package app.linksheet.feature.markdown

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.text.style.ReplacementSpan
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.SpanFactory
import io.noties.markwon.core.MarkwonTheme


class RoundedCodeSpan2(private val theme: MarkwonTheme) : ReplacementSpan() {
    private val padding = 20
    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val width = paint.measureText(text.subSequence(start, end).toString())
        val rect = RectF(x - padding, top.toFloat(), x + width + padding, bottom.toFloat())
        paint.setColor(theme.getCodeBackgroundColor(paint))
        canvas.drawRoundRect(rect, 20F, 20F, paint)
        theme.applyCodeTextStyle(paint)
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return (padding + paint.measureText(text.subSequence(start, end).toString()) + padding).toInt()
    }

    override fun updateMeasureState(p: TextPaint) {
//        apply(p)
    }

    override fun updateDrawState(ds: TextPaint) {
//        apply(ds)
//        ds.bgColor = theme.getCodeBackgroundColor(ds)
    }

    private fun apply(p: Paint) {
        theme.applyCodeTextStyle(p)
    }
}

class RoundedCodeSpan(private val theme: MarkwonTheme) : MetricAffectingSpan() {
    override fun updateMeasureState(p: TextPaint) {
        apply(p)
    }

    override fun updateDrawState(ds: TextPaint) {
        apply(ds)
        ds.bgColor = theme.getCodeBackgroundColor(ds)
    }

    private fun apply(p: TextPaint) {
        theme.applyCodeTextStyle(p)
    }
}

class RoundedCodeSpanFactory : SpanFactory {
    override fun getSpans(configuration: MarkwonConfiguration, props: RenderProps): Any {
        return RoundedCodeSpan2(configuration.theme())
    }
}
