package com.example.musicgo.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.musicgo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val requestExternalStorage = 1
    private val permissionsStorage = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private lateinit var companyLogo: ImageView
    private lateinit var splashScreen: LinearLayout
    private lateinit var fragmentLayout: LinearLayout
    private lateinit var progressLayout : LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        companyLogo = findViewById(R.id.company_logo)
        splashScreen = findViewById(R.id.splash_screen)
        fragmentLayout = findViewById(R.id.fragment_layout)
        progressLayout = findViewById(R.id.progress_layout)
        verifyStoragePermissions(this)
        val fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        scope.launch {
            delay(1000)
            companyLogo.startAnimation(fadeOutAnimation)
            delay(3000)
            companyLogo.visibility = View.GONE
            splashScreen.visibility = View.GONE
            fragmentLayout.visibility = View.VISIBLE
            delay(3000)
            progressLayout.visibility = View.GONE

        }

    }

    private fun verifyStoragePermissions(activity: Activity?) {
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsStorage,
                requestExternalStorage
            )
        }
    }
}