package au.com.masconn.cameraxobjectdetection.viewmodel

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import au.com.masconn.cameraxobjectdetection.models.ObjectDetectionState
import au.com.masconn.cameraxobjectdetection.repo.ImageAnalyzerUseCaseRepo
import au.com.masconn.cameraxobjectdetection.repo.PreviewUseCaseRepo
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.Executors

/**
 * ViewModel responsible for managing the object detection process using CameraX.
 * It binds the camera preview and image analysis use cases to the lifecycle owner.
 *
 * @property imageAnalyzerUseCaseRepo Repository for creating and configuring the ObjectDetectionAnalyzer.
 * @property previewUseCaseRepo Repository for creating and configuring the Preview use case.
 */
class ObjectDetectionViewModel(
    private val imageAnalyzerUseCaseRepo: ImageAnalyzerUseCaseRepo = ImageAnalyzerUseCaseRepo(),
    private val previewUseCaseRepo: PreviewUseCaseRepo = PreviewUseCaseRepo()
) : ViewModel() {
    // StateFlow to hold the current SurfaceRequest
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest.asStateFlow()

    // StateFlow to hold the current ObjectDetectionState
    private val _objectDetectionState = MutableStateFlow(ObjectDetectionState())
    val objectDetectionState: StateFlow<ObjectDetectionState> = _objectDetectionState.asStateFlow()

    // Preview use case for the camera
    private val cameraPreviewUseCase = previewUseCaseRepo.providePreviewUseCase { surfaceRequest ->
        _surfaceRequest.update {
            surfaceRequest
        }
    }

    // Executor for running camera tasks
    private val cameraExecutor = Executors.newSingleThreadExecutor()


    /**
     * Binds the camera preview and image analysis use cases to the lifecycle owner.
     * Sets the analyzer for the image analysis use case to process detected objects.
     *
     * @param appContext The application context.
     * @param lifecycleOwner The lifecycle owner to bind the camera use cases to.
     */
    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        val imageAnalysisUseCase =
            imageAnalyzerUseCaseRepo.provideImageAnalyzerUseCase(cameraExecutor) { detectedObjectModels ->
                _objectDetectionState.update {
                    ObjectDetectionState(objectModels = detectedObjectModels)
                }
            }
        processCameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            cameraPreviewUseCase,
            imageAnalysisUseCase
        )
        // wait for cancellation
        try {
            awaitCancellation()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            processCameraProvider.unbindAll()
        }
    }

    override fun onCleared() {
        super.onCleared()
        cameraExecutor.shutdown()
    }
}