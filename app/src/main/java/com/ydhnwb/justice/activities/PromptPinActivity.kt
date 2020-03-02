package com.ydhnwb.justice.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ydhnwb.justice.R
import com.ydhnwb.justice.utils.JusticeUtils
import kotlinx.android.synthetic.main.activity_prompt_pin.*

class PromptPinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prompt_pin)
        supportActionBar?.hide()
        pin_view.setPinViewEventListener{pinview, bool ->
            val pinEntered = pinview.value.toString()
            if(pinEntered.length == 4){
                val currentPIN = JusticeUtils.getPin(this@PromptPinActivity)
                if(currentPIN.equals(pinEntered)){
                    startActivity(Intent(this, SettingActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this@PromptPinActivity, "NOT OK", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
