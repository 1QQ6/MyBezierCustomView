package com.wyq.mybeziercustomview

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wyq.mybeziercustomview.activity.BezierOneActivity
import com.wyq.mybeziercustomview.activity.BezierTwoActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        draw_bezier_one.setOnClickListener {
            startActivity(Intent(this,BezierOneActivity::class.java))
        }
        draw_bezier_two.setOnClickListener {
            startActivity(Intent(this, BezierTwoActivity::class.java))
        }
    }


}
