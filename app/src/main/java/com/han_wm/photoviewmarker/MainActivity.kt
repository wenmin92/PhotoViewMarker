package com.han_wm.photoviewmarker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

private const val TAG = "[TEST]"
private const val REQ_GET_IMAGE = 1

fun log(msg: String) {
    Timber.d("$TAG $msg")
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_button.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }, REQ_GET_IMAGE)
        }

        img_drawing.setOnClickListener { log("<OnClick> view:$it") }
        img_drawing.setOnViewTapListener { view, x, y -> log("<OnViewTap> view:$view, x:$x, y:$y") }
//        img_drawing.setOnPhotoTapListener { view, x, y -> log("<OnPhotoTap> view:$view, x:$x, y:$y") }
        img_drawing.setOnOutsidePhotoTapListener { log("<OnOutsidePhotoTap> view:$it") }
        img_drawing.setOnLongClickListener { log("<OnLongClick> view:$it");false }
        img_drawing.setOnDragListener { view, event -> log("<OnDrag> view:$view, event:$event");false }
        img_drawing.setOnMatrixChangeListener { log("<OnMatrixChange> rect:$it") }
        img_drawing.setOnScaleChangeListener { scaleFactor, focusX, focusY -> log("<OnScaleChange> scaleFactor:$scaleFactor, focusX:$focusX, focusY:$focusY") }
        img_drawing.setOnSingleFlingListener { e1, e2, velocityX, velocityY -> log("<OnSingleFling> e1:$e1, e2:$e2, velocityX:$velocityX, velocityY:$velocityY");false }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log("onActivityResult")
        if (requestCode == REQ_GET_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val drawingUri = data.data
            log("$drawingUri")
            img_drawing.setImageURI(drawingUri)
        }
    }
}
