package com.example.flahlight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch

class MainActivity : AppCompatActivity() {
    lateinit var flashSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flashSwitch = findViewById(R.id.flahSwitch)
        val torch = Torch(this)

        flashSwitch.setOnCheckedChangeListener{buttonView, isChecked ->
            if(isChecked) {
                torch.flashOn()
            }
            else{
                torch.flashOff()
            }
        }
    }
}