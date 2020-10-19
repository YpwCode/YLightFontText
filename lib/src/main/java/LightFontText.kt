package com.ypw.code.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.TextView

class LightFontText constructor(ctx: Context, attrs: AttributeSet? = null) : TextView(ctx, attrs) {

    var gradientX = 0f

    private val normalColor = currentTextColor
    private var lightColor = Color.RED
    private var lightWidth = 0f
    private var duration = 5000
    private var repeatCount = -1

    private val linearGradientMatrix = Matrix()

    private val linearGradient by lazy {
        LinearGradient(
            -lightWidth, 0f, 0f, 0f,
            intArrayOf(normalColor, lightColor, normalColor),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    private val animator by lazy {
        val fromX = -lightWidth
        val toX = width.toFloat() + lightWidth
        val animator = ObjectAnimator.ofFloat(this, "gradientX", fromX, toX)
        animator.repeatCount = repeatCount
        animator.duration = duration.toLong()
        animator.addUpdateListener {
            postInvalidate()
        }
        animator
    }

    init {
        attrs?.let {
            val array = ctx.obtainStyledAttributes(it, R.styleable.LightFontText)
            lightColor = array.getColor(R.styleable.LightFontText_lft_lightColor, lightColor)
            lightWidth = array.getDimension(R.styleable.LightFontText_lft_lightWidth, lightWidth)
            duration = array.getInt(R.styleable.LightFontText_lft_duration, duration)
            repeatCount = array.getInt(R.styleable.LightFontText_lft_repeatCount, repeatCount)
            val index = array.getInt(R.styleable.LightFontText_lft_family, -1)
            setFamily(index)
            array.recycle()
        }
    }

    private fun setFamily(index: Int) {
        val name = when (index) {
            0 -> {
                "DIN-Alternate-Bold.ttf"
            }
            1 -> {
                "Herculanum.ttf"
            }
            else -> {
                "DIN-Alternate-Bold.ttf"
            }
        }
        if (index >= 0) {
            typeface = Typeface.createFromAsset(context.assets, "fonts/$name")
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (paint.shader == null) {
            paint.shader = linearGradient
        }
        linearGradientMatrix.setTranslate(gradientX, 0f)
        linearGradient.setLocalMatrix(linearGradientMatrix)
        super.onDraw(canvas)
    }

    private fun startAnimation() {
        animator.start()
    }

    private fun stopAnimation() {
        animator.cancel()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    private fun setup() {
        if (lightWidth < 1) {
            lightWidth = width.toFloat()
        }
        val fromX = -lightWidth
        val toX = width.toFloat() + lightWidth
        animator.setFloatValues(fromX, toX)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }
}