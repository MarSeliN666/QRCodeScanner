package com.codescannerapp.qrfreecodescanner

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import kotlinx.android.synthetic.main.activity_qr_scan.*


class QrScanerActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private val code = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scan)
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        checkPer()

        btn_openLink.visibility = View.GONE
        tv_qrText.text = "Tap screen to focus camera"

        codeScanner = CodeScanner(this, scannerView)

        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                tv_qrText.text = it.text
                btn_openLink.visibility = View.VISIBLE
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                btn_openLink.visibility = View.VISIBLE
                btn_openLink.text = "Set permission"
                btn_openLink.setOnClickListener {
                    requestPermission()
                }
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
            tv_qrText.text = "Tap screen to focus camera"
            btn_openLink.visibility = View.GONE
        }


    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupTabs(url: String) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse("https://www.google.com/search?q=$url"))
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA),
            code
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            code -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                checkPer()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun checkPer(){
        if (!checkPermission()) {
            requestPermission()
        }else{
            btn_openLink.text = "Open link"
            btn_openLink.visibility = View.GONE
            btn_openLink.setOnClickListener {
                if (tv_qrText.text != "") {
                    setupTabs(tv_qrText.text.toString())
                }
            }
        }
    }
}

