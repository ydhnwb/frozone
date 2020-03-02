package com.ydhnwb.justice.activities

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ydhnwb.justice.R
import com.ydhnwb.justice.utils.BranchPopup
import com.ydhnwb.justice.utils.JusticeUtils
import com.ydhnwb.justice.viewmodels.SettingState
import com.ydhnwb.justice.viewmodels.SettingViewModel

import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.content_setting.*

class SettingActivity : AppCompatActivity() {
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        settingViewModel = ViewModelProvider(this).get(SettingViewModel::class.java)
        settingViewModel.listenToUIState().observe(this, Observer {
            when(it){
                is SettingState.SelectedBranch -> {
                    tv_branch.text = it.branchName
                }
            }
        })
        setting_branch.setOnClickListener {
            BranchPopup().show(supportFragmentManager, "branch_popup")
        }
        setting_device_name.setOnClickListener {
            Toast.makeText(this, "OK", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val branchName = JusticeUtils.getCurrentBranchName(this)
        branchName?.let {
            tv_branch.text = it
        } ?: run {
            tv_branch.text = "Belum ada cabang ditautkan"
        }
    }



}
