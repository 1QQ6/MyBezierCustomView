package com.wyq.mybeziercustomview.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wyq.mybeziercustomview.R
import kotlinx.android.synthetic.main.activity_bezier_two.*

class BezierTwoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bezier_two)
        wave_bezier.startAnimation()
    }

    override fun onPause() {
        super.onPause()
        wave_bezier.pauseAnimation()
    }

    override fun onResume() {
        super.onResume()
        wave_bezier.resumeAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        wave_bezier.stopAnimation()
    }
}
