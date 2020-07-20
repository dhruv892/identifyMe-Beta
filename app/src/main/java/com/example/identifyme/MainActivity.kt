package com.example.identifyme


import android.graphics.Bitmap
import android.graphics.Canvas
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.identifyme.DigitsDetector.Companion.DIM_IMG_SIZE_X
import com.example.identifyme.DigitsDetector.Companion.DIM_IMG_SIZE_Y
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val mnistClassifier = DigitsDetector(this);


        reset.setOnClickListener{
            prediction.text = "Prediction:"
            myCanvasView.onClickClear()

        }
        detect.setOnClickListener {

//            val bitmap =
//                Bitmap.createBitmap(myCanvasView.width, myCanvasView.height, Bitmap.Config.ARGB_8888)

//            val scaledBitmap =
//                Bitmap.createScaledBitmap(myCanvasView.extraBitmap, 28, 28, false)

            if(myCanvasView.isEmpty()){
                Toast.makeText(this,"Please write digit",Toast.LENGTH_SHORT).show()
            }else{
                //val bitmap = myCanvasView.exportToBitmap(DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y)

                val digit = mnistClassifier.classify(myCanvasView.extraBitmap)
                prediction.text = "Prediction: ${digit}"
//            if (digit >= 0) {
//
//                prediction.text = "Prediction: ${digit.toString()}"
//            } else {
//                prediction.text = "predection: not detected"
//            }
            }

        }
    }
}