package com.wyq.mybeziercustomview.view

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.roundToInt

/**
 * Created by wyq on 2020/05/13.
 *
 *
 * 波浪动画
 */
class DrawBezierTwoView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) : View(context, attrs) {
    /**
     * 屏幕高度
     */
    private var mScreenHeight = 0
    /**
     * 屏幕宽度
     */
    private var mScreenWidth = 0
    /**
     * 波浪的画笔
     */
    private var mWavePaint: Paint? = null
    /**
     * 一个周期波浪的长度
     */
    private var mWaveLength = 0
    /**
     * 波浪的路径
     */
    var mWavePath: Path? = null
    /**
     * 平移偏移量
     */
    private var mOffset = 0
    /**
     * 一个屏幕内显示几个周期
     */
    private var mWaveCount = 0
    /**
     * 振幅
     */
    private var mWaveAmplitude = 0
    /**
     * 波形的颜色
     */
    private val waveColor = -0x550081c9
    private val waveType = DEFAULT
    private var valueAnimator: ValueAnimator? = null
    private fun init() {
        mWaveAmplitude = dp2px(10)
        mWaveLength = dp2px(500)
        initPaint()
    }

    /**
     * 初始化画笔
     */
    private fun initPaint() {
        mWavePath = Path()
        mWavePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mWavePaint!!.color = waveColor
        mWavePaint!!.style = Paint.Style.FILL_AND_STROKE
        mWavePaint!!.isAntiAlias = true
    }

    /*

* Math.round()方法: 表示的是“四舍五入”计算。
* Math.round(1.5) = 2
  Math.round(-1.5) = -1
* Math.floor()方法: 表示的是“向下取整”计算。
* Math.floor(1.7) = 1.0
  Math.floor(-1.2) = -2.0
* Math.ceil()方法: 表示的是“向上取整”计算。
* Math.ceil(1.3) = 2.0
  Math.ceil(-1.7) = -1.0

* */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mScreenHeight = h
        mScreenWidth = w
        /**
         * 加上1.5是为了保证至少有两个波形（屏幕外边一个完整的波形，屏幕里边一个完整的波形）
         */
        mWaveCount = (mScreenWidth / mWaveLength + 1.5).roundToInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (waveType) {
            SIN -> drawSinPath(canvas)
            COS -> drawCosPath(canvas)
        }
    }

    /**
     * sin函数图像的波形
     *
     * @param canvas
     */
    private fun drawSinPath(canvas: Canvas) {
        mWavePath!!.reset()
        mWavePath!!.moveTo(-mWaveLength + mOffset.toFloat(), mWaveAmplitude.toFloat())
        for (i in 0 until mWaveCount) {
            mWavePath!!.quadTo(-mWaveLength * 3 / 4 + mOffset + (i * mWaveLength).toFloat(), -mWaveAmplitude.toFloat(),
                    -mWaveLength / 2 + mOffset + (i * mWaveLength).toFloat(),
                    mWaveAmplitude.toFloat())
            mWavePath!!.quadTo(-mWaveLength / 4 + mOffset + (i * mWaveLength).toFloat(),
                    3 * mWaveAmplitude.toFloat(),
                    mOffset + i * mWaveLength.toFloat(),
                    mWaveAmplitude.toFloat())
        }
        mWavePath!!.lineTo(width.toFloat(), height.toFloat())
        mWavePath!!.lineTo(0f, height.toFloat())
        mWavePath!!.close()
        canvas.drawPath(mWavePath!!, mWavePaint!!)
    }

    /**
     * cos函数图像的波形
     *
     * @param canvas
     */
    private fun drawCosPath(canvas: Canvas) {
        mWavePath!!.reset()
        mWavePath!!.moveTo(-mWaveLength + mOffset.toFloat(), mWaveAmplitude.toFloat())
        for (i in 0 until mWaveCount) { //第一个控制点的坐标为(-mWaveLength * 3 / 4,3 * mWaveAmplitude
            mWavePath!!.quadTo(-mWaveLength * 3 / 4 + mOffset + (i * mWaveLength).toFloat(),
                    3 * mWaveAmplitude.toFloat(),
                    -mWaveLength / 2 + mOffset + (i * mWaveLength).toFloat(),
                    mWaveAmplitude.toFloat())
            //第二个控制点的坐标为(-mWaveLength / 4,-mWaveAmplitude)
            mWavePath!!.quadTo(-mWaveLength / 4 + mOffset + (i * mWaveLength).toFloat(), -mWaveAmplitude.toFloat(),
                    mOffset + i * mWaveLength.toFloat(),
                    mWaveAmplitude.toFloat())
        }
        mWavePath!!.lineTo(width.toFloat(), height.toFloat())
        mWavePath!!.lineTo(0f, height.toFloat())
        mWavePath!!.close()
        canvas.drawPath(mWavePath!!, mWavePaint!!)
    }

    /**
     * 波形动画
     */
    private fun initAnimation() {
        valueAnimator = ValueAnimator.ofInt(0, mWaveLength)
        valueAnimator!!.duration = 2000
        valueAnimator!!.startDelay = 300
        valueAnimator!!.repeatCount = ValueAnimator.INFINITE
        valueAnimator!!.interpolator = LinearInterpolator()
        valueAnimator!!.addUpdateListener { animation ->
            mOffset = animation.animatedValue as Int
            invalidate()
        }
        valueAnimator!!.start()
    }

    fun startAnimation() {
        initAnimation()
    }

    fun stopAnimation() {
        if (valueAnimator != null) {
            valueAnimator!!.cancel()
        }
    }

    fun pauseAnimation() {
        if (valueAnimator != null) {
            valueAnimator!!.pause()
        }
    }

    fun resumeAnimation() {
        if (valueAnimator != null) {
            valueAnimator!!.resume()
        }
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected fun dp2px(dpVal: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal.toFloat(), resources.displayMetrics).toInt()
    }

    companion object {
        private const val SIN = 0
        private const val COS = 1
        private const val DEFAULT = SIN
    }

    init {
        init()
    }
}