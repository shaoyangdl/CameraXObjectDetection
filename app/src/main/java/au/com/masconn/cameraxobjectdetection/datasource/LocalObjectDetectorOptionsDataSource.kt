package au.com.masconn.cameraxobjectdetection.datasource

import au.com.masconn.cameraxobjectdetection.MyApplication
import au.com.masconn.cameraxobjectdetection.ml.MAX_RESULT
import au.com.masconn.cameraxobjectdetection.ml.THREADS_NUMBER
import au.com.masconn.cameraxobjectdetection.ml.THRESHOLD
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector
import kotlin.coroutines.suspendCoroutine

/**
 * Local implementation of ObjectDetectorOptionsDataSource that provides
 * ObjectDetector options with support for GPU delegate if available.
 */
class LocalObjectDetectorOptionsDataSource : ObjectDetectorOptionsDataSource {
    private var gpuSupported: Boolean = false

    /**
     * Suspends the function to get the ObjectDetector options. The initialization takes time
     *
     * @return An ObjectDetector.ObjectDetectorOptions instance.
     */
    override suspend fun getOptions(): ObjectDetector.ObjectDetectorOptions =
        suspendCoroutine { continuation ->
            val optionsBuilder =
                TfLiteInitializationOptions.builder()
            // Check if GPU delegate is available
            TfLiteGpu.isGpuDelegateAvailable(MyApplication.context)
                .onSuccessTask { gpuAvailable: Boolean ->
                    // If GPU is available, enable GPU delegate support
                    if (gpuAvailable) {
                        gpuSupported = true
                        optionsBuilder.setEnableGpuDelegateSupport(true)
                    }
                    // Initialize TfLiteVision with the options
                    TfLiteVision.initialize(MyApplication.context, optionsBuilder.build())
                }.addOnSuccessListener {
                    // Build ObjectDetector options with max results and score threshold
                    val options = ObjectDetector.ObjectDetectorOptions.builder()
                        .setMaxResults(MAX_RESULT)
                        .setScoreThreshold(THRESHOLD)
                        .apply {
                            val baseOptionsBuilder =
                                BaseOptions.builder().setNumThreads(THREADS_NUMBER)
                            when {
                                gpuSupported -> {
                                    baseOptionsBuilder.useGpu()
                                }

                                else -> {
                                }
                            }
                            setBaseOptions(baseOptionsBuilder.build())
                        }
                        .build()
                    continuation.resumeWith(Result.success(options))
                }.addOnFailureListener {
                    continuation.resumeWith(Result.failure(Exception("TfLiteVision failed to initialize: ${it.message}")))
                }
        }
}