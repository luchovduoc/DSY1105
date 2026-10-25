package com.duoc.dsy1105

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.duoc.dsy1105.navigation.AppNav
import com.duoc.dsy1105.ui.theme.DSY1105Theme
import com.duoc.dsy1105.utils.CameraPermissionHelper

class MainActivity : ComponentActivity() {

    var hasCameraPermission by mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    } // fin requestPermissionLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        hasCameraPermission = CameraPermissionHelper.hasCameraPermission(this)

        setContent {
            DSY1105Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNav(
                        hasCameraPermission = hasCameraPermission,
                        onRequestPermission = {
                            requestPermissionLauncher.launch(
                                Manifest.permission.CAMERA
                            )
                        }
                    )
                } // fin Surface
            } // fin DSY1105Theme
        } // fin setContent

    } // fin onCreate

    override fun onResume() {
        super.onResume()
        hasCameraPermission = CameraPermissionHelper.hasCameraPermission(this)
    } // fin onResume

} // fin MainActivity