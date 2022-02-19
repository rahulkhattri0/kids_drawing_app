package com.example.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    private var DrawingView : DrawingView? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DrawingView = findViewById(R.id.Drawing_view)
        DrawingView!!.set_size_for_brush(20.toFloat())
        val brushbutton : ImageButton = findViewById(R.id.ib_brush)
        brushbutton.setOnClickListener{
            showBrushSizeDiag()
        }
    }
    private fun showBrushSizeDiag(){
        var brushDialog :Dialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("select brush size")
        var smallbutton : ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        smallbutton.setOnClickListener{
            DrawingView!!.set_size_for_brush(10.toFloat())
            brushDialog.dismiss()
        }
        var mediumbutton : ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumbutton.setOnClickListener{
            DrawingView!!.set_size_for_brush(20.toFloat())
            brushDialog.dismiss()
        }
        var largebutton : ImageButton = brushDialog.findViewById(R.id.ib_large_brush)
        largebutton.setOnClickListener{
            DrawingView!!.set_size_for_brush(30.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
    }
}