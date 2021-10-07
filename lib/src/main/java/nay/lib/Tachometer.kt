package nay.lib

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.animation.doOnEnd
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@BindingMethods(
    value = [
        BindingMethod(
            type = Tachometer::class,
            method = "setMax",
            attribute = "max"
        ),
        BindingMethod(
            type = Tachometer::class,
            method = "setMin",
            attribute = "min"
        )
    ]
)
class Tachometer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    ///////////////////////////////////////////////////////////////////////////
    // Attribute Defaults
    ///////////////////////////////////////////////////////////////////////////
    private var mMax = 60

    private var mMin = 0

    @Dimension
    private var mBorderSize = 36f

    @Dimension
    private var mTextGap = 50f

    @Dimension
    private var mValueTextSize = 260f

    @ColorInt
    private var mBorderColor = Color.parseColor("#402c47")

    @ColorInt
    private var mFillColor = Color.parseColor("#d83a78")

    @ColorInt
    private var mTextColor = Color.parseColor("#f5f5f5")

    private var mMetricText = "km/h"

    ///////////////////////////////////////////////////////////////////////////
    // Dynamic Values
    ///////////////////////////////////////////////////////////////////////////
    private val indicatorBorderRect = RectF()

    private val tickBorderRect = RectF()

    private val textBounds = Rect()

    private var angle = MIN_ANGLE

    private var value = 0

    ///////////////////////////////////////////////////////////////////////////
    // Dimension Getters
    ///////////////////////////////////////////////////////////////////////////
    private val centerX get() = width / 2f

    private val centerY get() = height / 2f

    ///////////////////////////////////////////////////////////////////////////
    // Core Attributes
    ///////////////////////////////////////////////////////////////////////////
    var max: Int
        get() = mMax
        set(value) {
            mMax = value
            invalidate()
        }

    var min: Int
        get() = mMin
        set(value) {
            mMin = value
            invalidate()
        }

    var borderSize: Float
        @Dimension get() = mBorderSize
        set(@Dimension value) {
            mBorderSize = value
            paintIndicatorBorder.strokeWidth = value
            paintIndicatorFill.strokeWidth = value
            invalidate()
        }

    var textGap: Float
        @Dimension get() = mTextGap
        set(@Dimension value) {
            mTextGap = value
            invalidate()
        }

    var metricText: String
        get() = mMetricText
        set(value) {
            mMetricText = value
            invalidate()
        }

    var borderColor: Int
        @ColorInt get() = mBorderColor
        set(@ColorInt value) {
            mBorderColor = value
            paintIndicatorBorder.color = value
            paintTickBorder.color = value
            paintMajorTick.color = value
            paintMinorTick.color = value
            invalidate()
        }

    var fillColor: Int
        @ColorInt get() = mFillColor
        set(@ColorInt value) {
            mFillColor = value
            paintIndicatorFill.color = value
            invalidate()
        }

    var textColor: Int
        @ColorInt get() = mTextColor
        set(@ColorInt value) {
            mTextColor = value
            paintTickText.color = value
            paintMeterValue.color = value
            paintMetric.color = value
            invalidate()
        }

    var valueTextSize: Float
        @Dimension get() = mValueTextSize
        set(@Dimension value) {
            paintMeterValue.textSize = value
            invalidate()
        }

    ///////////////////////////////////////////////////////////////////////////
    // Paints
    ///////////////////////////////////////////////////////////////////////////
    private val paintIndicatorBorder = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = borderColor
        strokeWidth = borderSize
        strokeCap = Paint.Cap.ROUND
    }

    private val paintIndicatorFill = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = fillColor
        strokeWidth = borderSize
        strokeCap = Paint.Cap.ROUND
    }

    private val paintTickBorder = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = borderColor
        strokeWidth = 4f
        strokeCap = Paint.Cap.ROUND
    }

    private val paintMajorTick = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = borderColor
        strokeWidth = MAJOR_TICK_WIDTH
        strokeCap = Paint.Cap.BUTT
    }

    private val paintMinorTick = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = borderColor
        strokeWidth = MINOR_TICK_WIDTH
        strokeCap = Paint.Cap.BUTT
    }

    private val paintTickText = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = textColor
        textSize = 40f
    }

    private val paintMeterValue = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = textColor
        textSize = valueTextSize
    }

    private val paintMetric = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = textColor
        textSize = 50f
    }

    ///////////////////////////////////////////////////////////////////////////
    // Animators
    ///////////////////////////////////////////////////////////////////////////
    private val animator = ValueAnimator.ofFloat().apply {
        interpolator = AccelerateDecelerateInterpolator()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        indicatorBorderRect.set(
            borderSize / 2,
            borderSize / 2,
            width - borderSize / 2,
            width - borderSize / 2
        )
        tickBorderRect.set(
            borderSize + TICK_MARGIN,
            borderSize + TICK_MARGIN,
            width - borderSize - TICK_MARGIN,
            width - borderSize - TICK_MARGIN
        )
    }

    init {
        obtainStyledAttributes(attrs, defStyleAttr)
    }

    private fun obtainStyledAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.Tachometer,
            defStyleAttr,
            0
        )

        try {
            with(typedArray) {
                max = getInt(
                    R.styleable.Tachometer_max,
                    max
                )
                min = getInt(
                    R.styleable.Tachometer_min,
                    min
                )
                borderSize = getDimension(
                    R.styleable.Tachometer_borderSize,
                    borderSize
                )
                valueTextSize = getDimension(
                    R.styleable.Tachometer_valueTextSize,
                    valueTextSize
                )
                textGap = getDimension(
                    R.styleable.Tachometer_textGap,
                    textGap
                )
                metricText = getString(
                    R.styleable.Tachometer_metricText
                ) ?: metricText
                borderColor = getColor(
                    R.styleable.Tachometer_borderColor,
                    borderColor
                )
                fillColor = getColor(
                    R.styleable.Tachometer_fillColor,
                    borderColor
                )
                textColor = getColor(
                    R.styleable.Tachometer_textColor,
                    borderColor
                )
            }
        } catch (e: Exception) {
            // ignored
        } finally {
            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        renderTicks(canvas, getTicksValue(min, max))
        renderBorder(canvas)
        renderBorderFill(canvas)
        renderTickBorder(canvas)
        renderMeterValueAndMetricText(canvas)
    }

    private fun renderTicks(canvas: Canvas, ticks: List<Tick<Int>>) {
        for (tick in ticks) {
            when (tick) {
                is Tick.Major -> renderMajorTick(canvas, tick.value)
                is Tick.Minor -> renderMinorTick(canvas, tick.value)
            }
        }
    }

    private fun renderMinorTick(canvas: Canvas, v: Int) {
        canvas.drawLine(
            centerX + (centerX - borderSize - MINOR_TICK_SIZE) * cos(mapMeterValueToAngle(v).toRadian()),
            centerY - (centerY - borderSize - MINOR_TICK_SIZE) * sin(mapMeterValueToAngle(v).toRadian()),
            centerX + (centerX - borderSize - TICK_MARGIN) * cos(mapMeterValueToAngle(v).toRadian()),
            centerY - (centerY - borderSize - TICK_MARGIN) * sin(mapMeterValueToAngle(v).toRadian()),
            paintMinorTick
        )
    }

    private fun renderMajorTick(canvas: Canvas, v: Int) {
        canvas.drawLine(
            centerX + (centerX - borderSize - MAJOR_TICK_SIZE) * cos(mapMeterValueToAngle(v).toRadian()),
            centerY - (centerY - borderSize - MAJOR_TICK_SIZE) * sin(mapMeterValueToAngle(v).toRadian()),
            centerX + (centerX - borderSize - TICK_MARGIN) * cos(mapMeterValueToAngle(v).toRadian()),
            centerY - (centerY - borderSize - TICK_MARGIN) * sin(mapMeterValueToAngle(v).toRadian()),
            paintMajorTick
        )
        canvas.drawTextCentred(
            v.toString(),
            centerX + (centerX - borderSize - MAJOR_TICK_SIZE - TICK_MARGIN - TICK_TEXT_MARGIN) * cos(mapMeterValueToAngle(v).toRadian()),
            centerY - (centerY - borderSize - MAJOR_TICK_SIZE - TICK_MARGIN - TICK_TEXT_MARGIN) * sin(mapMeterValueToAngle(v).toRadian()),
            paintTickText
        )
    }

    private fun renderBorder(canvas: Canvas) {
        canvas.drawArc(
            indicatorBorderRect,
            140f,
            260f,
            false,
            paintIndicatorBorder
        )
    }

    private fun renderTickBorder(canvas: Canvas) {
        canvas.drawArc(
            tickBorderRect,
            START_ANGLE,
            SWEEP_ANGLE,
            false,
            paintTickBorder
        )
    }

    private fun renderBorderFill(canvas: Canvas) {
        val sweepAngle = when {
            value >= max -> SWEEP_ANGLE
            value <= min -> 0f
            else -> MIN_ANGLE - angle
        }

        canvas.drawArc(
            indicatorBorderRect,
            START_ANGLE,
            sweepAngle,
            false,
            paintIndicatorFill
        )
    }

    private fun renderMeterValueAndMetricText(canvas: Canvas) {
        canvas.drawTextCentred(
            value.toString(),
            width / 2f,
            height / 2f,
            paintMeterValue
        )
        canvas.drawTextCentred(
            metricText,
            width / 2f,
            height / 2f + paintMeterValue.textSize/2 + textGap,
            paintMetric
        )
    }

    private fun mapMeterValueToAngle(value: Int): Float {
        return (MIN_ANGLE + ((MAX_ANGLE - MIN_ANGLE) / (max - min)) * (value - min))
    }

    private fun mapAngleToMeterValue(angle: Float): Int {
        return (min + ((max - min) / (MAX_ANGLE - MIN_ANGLE)) * (angle - MIN_ANGLE)).toInt()
    }

    fun setMeterValue(v: Int, d: Long, onEnd: (() -> Unit)? = null) {
        animator.apply {
            setFloatValues(mapMeterValueToAngle(value), mapMeterValueToAngle(v))

            addUpdateListener {
                angle = it.animatedValue as Float
                value = mapAngleToMeterValue(angle)
                invalidate()
            }
            doOnEnd {
                onEnd?.invoke()
            }
            duration = d
            start()
        }
    }

    private fun Canvas.drawTextCentred(text: String, cx: Float, cy: Float, paint: Paint) {
        paint.getTextBounds(text, 0, text.length, textBounds)
        drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint)
    }

    /**
     * Angle to Radian
     */
    private fun Float.toRadian(): Float {
        return this * (PI / 180).toFloat()
    }

    /**
     * Get tickValue list
     */
    private fun getTicksValue(min: Int, max: Int): List<Tick<Int>> {
        val range = min..max
        val majorStep = (max - min) / DEFAULT_MAJOR_TICKS_SPLIT
        val minorStep = majorStep / DEFAULT_MINOR_TICKS_SPLIT
        val ticks = mutableListOf<Tick<Int>>(Tick.Major(min), Tick.Major(max))
        var (majorSeek, minorSeek) = 0 to 0

        for (v in range) {
            if (majorSeek == majorStep) {
                if ((range.last - v) >= majorStep) {
                    ticks.add(Tick.Major(v))
                }
                majorSeek = 1
                minorSeek = 1
                continue
            } else majorSeek++

            if (minorSeek == minorStep) {
                if ((range.last - v) >= minorStep) {
                    ticks.add(Tick.Minor(v))
                }
                minorSeek = 1
            } else minorSeek++
        }

        return ticks.sortedBy {
            when (it) {
                is Tick.Major -> it.value
                is Tick.Minor -> it.value
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Binding Adapter Methods
    ///////////////////////////////////////////////////////////////////////////
    fun setMax(max: Int?) {
        this.max = max ?: return
    }

    fun setMin(min: Int?) {
        this.min = min ?: return
    }

    ///////////////////////////////////////////////////////////////////////////
    // Companion
    ///////////////////////////////////////////////////////////////////////////
    companion object {
        private const val MIN_ANGLE = 220f
        private const val MAX_ANGLE = -40f
        private const val START_ANGLE = 140f
        private const val SWEEP_ANGLE = 260f

        private const val TICK_MARGIN = 10f
        private const val TICK_TEXT_MARGIN = 30f
        private const val MAJOR_TICK_SIZE = 50f
        private const val MINOR_TICK_SIZE = 25f
        private const val MAJOR_TICK_WIDTH = 4f
        private const val MINOR_TICK_WIDTH = 2f

        private const val DEFAULT_MAJOR_TICKS_SPLIT = 10
        private const val DEFAULT_MINOR_TICKS_SPLIT = 4
    }
}