package com.example.cameratrial

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cameratrial.ScannerActivity

class MainActivity : AppCompatActivity() {
    private var captureBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        captureBtn = findViewById(R.id.snapbtn)
        captureBtn?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, ScannerActivity::class.java)
            startActivity(intent)
        })
    }
}