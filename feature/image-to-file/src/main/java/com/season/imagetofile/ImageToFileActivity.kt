package com.season.imagetofile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.season.analytics.AnalyticsHelper
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import me.pqpo.smartcropperlib.SmartCropper
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ImageToFileActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SmartCropper.buildImageDetector(this)

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            navController = rememberNavController()

            ImageToFileRoute(
                navController = navController,
                analyticsHelper = analyticsHelper,
                onNavigateToEditImage = { filePath ->
                    Uri.fromFile(File(filePath))?.let {
                        val uCrop: UCrop = UCrop.of(it, it)
                        uCrop.start(this)
                    }
                }
            )
        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                // Crop success
            } else if (resultCode == UCrop.RESULT_ERROR) {
                // Crop error
                UCrop.getError(data!!)
            }
            navController.popBackStack()
        }
    }
}
