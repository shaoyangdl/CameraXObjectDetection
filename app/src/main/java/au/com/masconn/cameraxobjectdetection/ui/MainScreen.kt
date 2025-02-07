package au.com.masconn.cameraxobjectdetection.ui

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import au.com.masconn.cameraxobjectdetection.viewmodel.ObjectDetectionViewModel
import au.com.masconn.cameraxobjectdetection.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        val viewModel = viewModel<ObjectDetectionViewModel>()
        // Show the object detection screen if the camera permission is granted
        ObjectDetectionScreen(viewModel = viewModel, modifier = modifier)
    } else {
        LaunchedEffect(Unit) {
            cameraPermissionState.launchPermissionRequest()
        }
        // Show the camera permission rationale if the permission is not granted
        Column(
            modifier = modifier
                .fillMaxSize()
                .wrapContentSize()
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(id = R.string.permission_camera_rationale),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            // Permission requests are limited, so we should direct to app details if requests limit is reached
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text(stringResource(id = R.string.grant_camera_permission))
            }
        }
    }
}