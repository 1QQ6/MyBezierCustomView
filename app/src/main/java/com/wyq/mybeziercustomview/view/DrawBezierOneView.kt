package com.wyq.mybeziercustomview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by wyq on 2020/05/13.
 */
class DrawBezierOneView : View {
    private var eventX = 0f
    private var eventY = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f
    private var paint: Paint? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        paint = Paint()
        paint!!.isAntiAlias = true
    }

    //测量大小完成以后回调
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        startX = centerX - 250f
        startY = centerY
        endX = centerX + 250f
        endY = centerY
        eventX = centerX
        eventY = centerY - 250f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint!!.color = Color.GRAY
        //画3个点
        canvas.drawCircle(startX, startY, 8f, paint!!)
        canvas.drawCircle(endX, endY, 8f, paint!!)
        canvas.drawCircle(eventX, eventY, 8f, paint!!)
        //绘制连线
        paint!!.strokeWidth = 3f
        canvas.drawLine(startX, centerY, eventX, eventY, paint!!)
        canvas.drawLine(endX, centerY, eventX, eventY, paint!!)
        //画二阶贝塞尔曲线
        paint!!.color = Color.GREEN
        paint!!.style = Paint.Style.STROKE
        val path = Path()
        path.moveTo(startX, startY)
        path.quadTo(eventX, eventY, endX, endY)
        canvas.drawPath(path, paint!!)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                eventX = event.x
                eventY = event.y
                invalidate()
            }
        }
        return true
    }
}