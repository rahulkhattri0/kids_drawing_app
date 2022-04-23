package com.example.drawingapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.get
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.health.PackageHealthStats
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private var DrawingView : DrawingView? =null
    private var ImageButtonCurrent : ImageButton? =null
    private var galleryButton : ImageButton? =null
    private var undobutton: ImageButton? =null
    private var redobutton: ImageButton? =null
    private var opengallery: ActivityResultLauncher<Intent> =registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode== Activity.RESULT_OK && result.data!=null){
            val background: ImageView = findViewById(R.id.background)
            Log.e("something",result.data?.data.toString())
            background.setImageURI(result.data?.data)
        }
    }
    private var  permissions : ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){
            it.entries.forEach {
                permission ->
                val granted = permission.value
                val nameofpermission = permission.key
                if(granted){
                    if(nameofpermission==Manifest.permission.READ_EXTERNAL_STORAGE){
                        Toast.makeText(this,"permission for storage granted",Toast.LENGTH_LONG).show()
                    }
                    val gallerypicIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    opengallery.launch(gallerypicIntent)
                }
                else{
                    if(nameofpermission==Manifest.permission.READ_EXTERNAL_STORAGE){
                        Toast.makeText(this,"permission for storage denied",Toast.LENGTH_LONG).show()
                    }
                }
            }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DrawingView = findViewById(R.id.Drawing_view)
        DrawingView!!.set_size_for_brush(20.toFloat())
        galleryButton = findViewById(R.id.gallery_button)
        undobutton=findViewById(R.id.undo_button)
        redobutton=findViewById(R.id.redo_button)
        galleryButton!!.setOnClickListener {
            requeststoragepermissions()
        }
        val brushbutton : ImageButton = findViewById(R.id.ib_brush)
        brushbutton.setOnClickListener{
            showBrushSizeDiag()
        }
        val savebutton : ImageButton = findViewById(R.id.save_button)
        savebutton.setOnClickListener{
            if(readstorageallowed()){
                lifecycleScope.launch{
                    val frameLayoutDrawingLayout : FrameLayout = findViewById(R.id.frame_layout)
                    savebmapfile(getbitmap(frameLayoutDrawingLayout))
                }
            }
        }
        val linear_layout :LinearLayout = findViewById(R.id.color_palette)
        ImageButtonCurrent = linear_layout[2] as ImageButton
        ImageButtonCurrent!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallete_selected)
        )
        galleryButton!!.setOnClickListener{
            requeststoragepermissions()
        }
        undobutton!!.setOnClickListener {
            DrawingView!!.undo()
        }
        redobutton!!.setOnClickListener {
            DrawingView!!.redo()
        }
    }
    private fun readstorageallowed():Boolean{
        val result = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requeststoragepermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            showRationale("This app needs storage permission", "please provide storage access")
        } else {
            permissions.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }
    private fun getbitmap(v :View): Bitmap{
        val returnedbitmap = Bitmap.createBitmap(v.width,v.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedbitmap)
        val bg:Drawable = v.background
        if(bg!=null){
            bg.draw(canvas)
        }
        else{
            canvas.drawColor(Color.WHITE)
        }
        v.draw(canvas)
        return returnedbitmap
    }

        private suspend fun savebmapfile(bitmap:Bitmap):String {
            var result: String = ""
            withContext(Dispatchers.IO) {
                if (bitmap != null) {
                    try {
                        val bytes = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                        val f =
                            File(externalCacheDir!!.absoluteFile.toString() + File.separator + "KidsDrawingApp_" + System.currentTimeMillis() / 1000 + ".png")
                        val fo = FileOutputStream(f)
                        fo.write(bytes.toByteArray())
                        fo.close()
                        result = f.absolutePath
                        runOnUiThread {
                            if (result != null) {
                                Toast.makeText(this@MainActivity,
                                    "file saved : $result", Toast.LENGTH_LONG)
                                    .show()
                            }
                            else{
                                Toast.makeText(this@MainActivity,
                                    "could not save file", Toast.LENGTH_LONG)
                                    .show()
                            }
                            }
                        }
                    catch (e: Exception) {
                        result=""
                        e.printStackTrace()
                    }

                    }

                }
            return result
            }

    private fun showRationale(Title:String,message:String) {
        val b: AlertDialog.Builder = AlertDialog.Builder(this)
        b.setTitle(Title).setMessage(message).setPositiveButton("Click here to grant storage access") { _,_ ->
            permissions.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
        val a :AlertDialog= b.create()
        a.show()
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
    fun paintclicked(view: View){
        if(view !== ImageButtonCurrent){
            var imageButton:ImageButton = view as ImageButton
            var colorTag: String = imageButton.tag.toString()
            DrawingView!!.setColor(colorTag)

            imageButton.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallete_selected))
            ImageButtonCurrent!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallete_normal))
            ImageButtonCurrent = view
        }
    }

}