package com.ydhnwb.justice.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.ydhnwb.justice.MainActivity
import com.ydhnwb.justice.R
import com.ydhnwb.justice.utils.JusticeUtils

class IntroActivity : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        val sliderPage = SliderPage().apply {
            description = "Lorem ipsum dolor sit amet"
            descColor = Color.parseColor("#ffffff")
            imageDrawable = R.drawable.search_ic_outline_history_24px
            bgColor = Color.parseColor("#1E80CE")
        }

        val sliderPage2 = SliderPage().apply {
            description = "Lorem ipsum dolor sit amet"
            descColor = Color.parseColor("#ffffff")
            imageDrawable = R.drawable.search_ic_outline_history_24px
            bgColor = Color.parseColor("#1E80CE")
        }

        addSlide(AppIntroFragment.newInstance(sliderPage))
        addSlide(AppIntroFragment.newInstance(sliderPage2))
        setZoomAnimation()
        skipButtonEnabled = false
        isVibrateOn = true
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        JusticeUtils.setFirstTime(this@IntroActivity, false).also {
            JusticeUtils.setDefaultPin(this@IntroActivity)
            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
            finish()
        }
    }
}