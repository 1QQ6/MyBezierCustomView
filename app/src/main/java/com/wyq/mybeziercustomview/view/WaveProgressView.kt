package com.wyq.mybeziercustomview.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import com.wyq.mybeziercustomview.R
import com.wyq.mybeziercustomview.utils.Dp2PxUtils
import kotlin.math.ceil


class WaveProgressView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var wavePaint : Paint? = null //绘制波浪画笔

    private var wavePath : Path? = null   //绘制波浪Path

    private var waveWidth = 0f//波浪宽度

    private var waveHeight = 0f//波浪高度

    private var waveNum = 0   //波浪组的数量

    private var defaultSize = 0 //自定义view默认的宽高

    private var maxHeight = 0f  //水柱高度

    private var viewSize = 0//重新测量后View实际的宽高

    private var waveProgressAnim: WaveProgressAnim? = null

    private var percent = 0f//进度条占比

    private var progressNum = 0f//可以更新的进度条数值

    private var maxNum = 0f//进度条最大值

    private var waveMovingDistance = 0f//波浪平移的距离

    private var circlePaint : Paint? = null//圆形进度框画笔


    private var bitmap : Bitmap? = null//缓存bitmap

    private var bitmapCanvas: Canvas? = null

    private var waveColor = 0//波浪颜色

    private var bgColor = 0//背景进度框颜色





    init {
        init(context,attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        waveWidth = Dp2PxUtils.dip2px(context, 20f).toFloat()
        waveHeight = Dp2PxUtils.dip2px(context, 10f).toFloat()
        wavePath = Path()
        wavePaint = Paint()
        wavePaint!!.color = resources.getColor(R.color.water)
        wavePaint!!.isAntiAlias = true //设置抗锯齿
        //wavePaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        circlePaint = Paint()
        circlePaint!!.color = Color.GRAY
        circlePaint!!.isAntiAlias = true //设置抗锯齿


        defaultSize =
            Dp2PxUtils.dip2px(context, 200f)
        maxHeight = Dp2PxUtils.dip2px(context, 250f).toFloat()
        //波浪的数量需要进一取整，所以使用Math.ceil函数
        waveNum = ceil((defaultSize / waveWidth / 2).toString().toDouble()).toInt()

        percent = 0f
        progressNum = 0f
        maxNum = 100f
        waveProgressAnim = WaveProgressAnim()
        waveMovingDistance = 0f

        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.WaveProgressView)
        waveWidth = typedArray.getDimension(
            R.styleable.WaveProgressView_wave_width,
            Dp2PxUtils.dip2px(context, 25f).toFloat()
        )
        waveHeight = typedArray.getDimension(
            R.styleable.WaveProgressView_wave_height,
            Dp2PxUtils.dip2px(context, 5f).toFloat()
        )
        waveColor = typedArray.getColor(R.styleable.WaveProgressView_wave_color, Color.GREEN)
        bgColor = typedArray.getColor(R.styleable.WaveProgressView_bg_color, Color.GRAY)
        typedArray.recycle()

        wavePaint!!.color = waveColor

        circlePaint!!.color = bgColor

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val height: Int = measureSize(defaultSize, heightMeasureSpec)
        val width: Int = measureSize(defaultSize, widthMeasureSpec)
        val min = width.coerceAtMost(height) // 获取View最短边的长度

        setMeasuredDimension(min, min) // 强制改View为以最短边为长度的正方形

        viewSize = min
        waveNum = ceil((viewSize / waveWidth / 2).toString().toDouble()).toInt()

    }

    private fun measureSize(defaultSize: Int, measureSpec: Int): Int {
        var result = defaultSize
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = result.coerceAtMost(specSize)
        }
        return result
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //这里用到了缓存技术
        bitmap = Bitmap.createBitmap(viewSize, viewSize, Bitmap.Config.ARGB_8888)
        bitmapCanvas = Canvas(bitmap!!)
        //bitmapCanvas!!.drawCircle((viewSize/2).toFloat(),
        //    (viewSize/2).toFloat(), (viewSize/2).toFloat(), circlePaint!!)
        bitmapCanvas!!.drawPath(getWavePath(),wavePaint!!)

        canvas.drawBitmap(bitmap!!,0f,0f,null)
    }

    private fun getWavePath(): Path {
        wavePath!!.reset()

        //移动到右上方，也就是p0点
        wavePath!!.moveTo(defaultSize.toFloat(),maxHeight-defaultSize)
        //移动到右下方，也就是p1点
        wavePath!!.lineTo(defaultSize.toFloat(), defaultSize.toFloat())
        //移动到左下边，也就是p2点
        wavePath!!.lineTo(0f, defaultSize.toFloat())
        //移动到左上方，也就是p3点
        wavePath!!.lineTo(0f, maxHeight - defaultSize)

        //wavePath!!.lineTo(0f, (1-percent)*viewSize)

        //移动到左上方，也就是p3点（x轴默认方向是向右的，我们要向左平移，因此设为负值）

        wavePath!!.lineTo(-waveMovingDistance, (1-percent)*viewSize)



        for (i in 0 until waveNum * 2) {
            wavePath!!.rQuadTo(waveWidth / 2, waveHeight, waveWidth, 0f)
            wavePath!!.rQuadTo(waveWidth / 2, -waveHeight, waveWidth, 0f)
        }

        wavePath!!.close()

        return wavePath!!
    }

    inner class WaveProgressAnim : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation
        ) {
            super.applyTransformation(interpolatedTime, t)

            //波浪高度到达最大值后就不需要循环了，只需让波浪曲线平移循环即可
            if(percent < progressNum / maxNum){
                percent = interpolatedTime * progressNum / maxNum
            }
            waveMovingDistance = interpolatedTime * waveNum * waveWidth * 2
            postInvalidate()
        }

    }

    /**
     * 设置进度条数值
     * @param progressNum 进度条数值
     * @param time 动画持续时间
     */
    fun setProgressNum(progressNum: Float, time: Int) {
        this.progressNum = progressNum
        percent = 0f
        waveProgressAnim!!.duration = time.toLong()

        waveProgressAnim!!.repeatCount = Animation.INFINITE //让动画无限循环

        waveProgressAnim!!.interpolator = LinearInterpolator() //让动画匀速播放，不然会出现波浪平移停顿的现象

        waveProgressAnim!!.setAnimationListener(object :Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {
                if(percent == progressNum / maxNum){
                    waveProgressAnim!!.duration = 8000
                }
            }

            override fun onAnimationEnd(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {

            }

        })
        startAnimation(waveProgressAnim)
    }


}