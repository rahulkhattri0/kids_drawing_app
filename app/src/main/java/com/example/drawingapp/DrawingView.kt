package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.Toast


class DrawingView(context: Context, attributes: AttributeSet) : View(context, attributes) {
    private var mdrawpath : CustomPath? = null
    private var canvasbitmap: Bitmap? =null
    private var drawpaint: Paint? =null
    private var canvas_paint: Paint?=null
    private var brushsize: Float = 0.toFloat()
    private var color : Int = Color.GREEN
    private var canvas: Canvas?= null
    private val mpaths = ArrayList<CustomPath>()

    init {
        setupeverything()
    }
    private fun setupeverything(){
        drawpaint= Paint()
        mdrawpath= CustomPath(color,brushsize)
        drawpaint!!.color= color
        drawpaint!!.style = Paint.Style.STROKE
        drawpaint!!.strokeCap= Paint.Cap.ROUND
        canvas_paint= Paint(Paint.DITHER_FLAG)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasbitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasbitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasbitmap!!,0f,0f,canvas_paint)
        for(paths in mpaths){
            drawpaint?.strokeWidth = paths.brushThickness
            drawpaint!!.color= paths.color
            canvas.drawPath(paths,drawpaint!!)
        }
        if(!mdrawpath!!.isEmpty){
            drawpaint?.strokeWidth = mdrawpath!!.brushThickness
            drawpaint!!.color= mdrawpath!!.color
            canvas.drawPath(mdrawpath!!,drawpaint!!)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchx = event?.x
        val touchy = event?.y

        when(event!!.action){
            MotionEvent.ACTION_DOWN->{
                mdrawpath!!.color = color
                mdrawpath!!.brushThickness= brushsize

                mdrawpath!!.reset()
                mdrawpath!!.moveTo(touchx!!,touchy!!)
            }
            MotionEvent.ACTION_MOVE->{
                mdrawpath!!.lineTo(touchx!!,touchy!!)
            }
            MotionEvent.ACTION_UP->{
                mpaths.add(mdrawpath!!)
                mdrawpath = CustomPath(color,brushsize)
            }
            else -> {return false}
        }
        invalidate()
        return true
    }
    fun set_size_for_brush(newSize: Float){
        brushsize= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize,resources.displayMetrics)
        drawpaint!!.strokeWidth= brushsize
    }
    fun setColor(newColor:String){
        color = Color.parseColor(newColor)
        drawpaint!!.color= color
    }
    fun paintclicled(Ib : ImageButton){
        
    }
    internal inner class CustomPath(var color:Int,var brushThickness:Float) : Path(){

    }

}