package com.codescannerapp.qrfreecodescanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        lottie_animation.imageAssetsFolder = "images/"
        lottie_animation.setAnimation(R.raw.icon_animation)

        lottie_animation.playAnimation()

        GlobalScope.launch {
            startActivitySuspend()
        }
    }

    private suspend fun startActivitySuspend(){
        val intentScanCode = Intent(this, QrScanerActivity::class.java)
        val coroutineScope = coroutineScope {
            async {
                delay(2700)
                runOnUiThread {
                    startActivity(intentScanCode)
                }
            }
        }
        coroutineScope.await()
    }
}