package au.com.masconn.cameraxobjectdetection.ui

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.com.masconn.cameraxobjectdetection.R
import au.com.masconn.cameraxobjectdetection.models.ObjectDetectionState
import au.com.masconn.cameraxobjectdetection.viewmodel.ObjectDetectionViewModel
import kotlin.math.max

/**
 * Composable function that displays the object detection screen.
 * It binds the camera to the lifecycle owner and displays the detected objects.
 *
 * @param viewModel The ViewModel that manages the object detection process.
 * @param modifier The modifier to be applied to the layout.
 * @param lifecycleOwner The lifecycle owner to bind the camera use cases to.
 */
@Composable
fun ObjectDetectionScreen(
    viewModel: ObjectDetectionViewModel,
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context, lifecycleOwner)
    }

    val objectDetectionState by viewModel.objectDetectionState.collectAsStateWithLifecycle()

    Column {
        Text(
            text = stringResource(id = R.string.app_name),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        surfaceRequest?.let {
            CameraDetectionBox(surfaceRequest = it, objectDetectionState = objectDetectionState)
        }
        // Show measurements of the detected objects
        Row {
            Text(text = stringResource(id = R.string.reference), modifier = Modifier.padding(16.dp))
            Text(
                text = objectDetectionState.getReferenceObjectMeasurement(),
                modifier = Modifier.padding(16.dp)
            )
        }
        Row {
            Text(text = stringResource(id = R.string.target), modifier = Modifier.padding(16.dp))
            Text(
                text = objectDetectionState.getDetectedObjectMeasurement(),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * Composable function that displays the camera viewfinder and draws bounding boxes
 * around detected objects.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param surfaceRequest The surface request for the camera viewfinder.
 * @param objectDetectionState The state containing the detected objects.
 */
@Composable
fun ColumnScope.CameraDetectionBox(
    modifier: Modifier = Modifier,
    surfaceRequest: SurfaceRequest,
    objectDetectionState: ObjectDetectionState
) {
    var boxSize by remember { mutableStateOf(Size.Zero) }

    Box(modifier = modifier
        .weight(1f)
        .onGloballyPositioned {
            boxSize = it.size.toSize()
        }) {
        val coordinateTransformer = remember { MutableCoordinateTransformer() }
        CameraXViewfinder(
            surfaceRequest = surfaceRequest,
            coordinateTransformer = coordinateTransformer,
            modifier = Modifier
        )

        if (objectDetectionState.hasDetectedObjects()) {
            objectDetectionState.objectModels.forEach {
                val scaleFactor =
                    max(boxSize.width / it.imageWidth, boxSize.height / it.imageHeight)

                val realWidth =
                    max(boxSize.height * it.imageWidth / it.imageHeight, boxSize.width)

                val realHeight = max(boxSize.width * it.height / it.imageWidth, boxSize.height)

                val startLeftOffset = -(realWidth - boxSize.width) / 2
                val startTopOffset = -(realHeight - boxSize.height) / 2

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        color = if (it.isReference) Color.Green else Color.Red,
                        topLeft = Offset(
                            it.left * scaleFactor + startLeftOffset,
                            it.top * scaleFactor + startTopOffset
                        ),
                        size = Size(
                            it.width * scaleFactor,
                            it.height * scaleFactor
                        ),
                        style = Stroke(10F),
                    )
                }
            }
        }
    }
}