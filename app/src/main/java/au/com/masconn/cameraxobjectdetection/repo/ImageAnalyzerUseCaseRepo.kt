package au.com.masconn.cameraxobjectdetection.repo

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import au.com.masconn.cameraxobjectdetection.datasource.AssetTfLiteModelDataSource
import au.com.masconn.cameraxobjectdetection.datasource.LocalObjectDetectorOptionsDataSource
import au.com.masconn.cameraxobjectdetection.datasource.ObjectDetectorOptionsDataSource
import au.com.masconn.cameraxobjectdetection.datasource.TfLiteModelDataSource
import au.com.masconn.cameraxobjectdetection.extensions.calculateRealWorldWidthRatio
import au.com.masconn.cameraxobjectdetection.extensions.toObjectModel
import au.com.masconn.cameraxobjectdetection.ml.ObjectDetectionAnalyzer
import au.com.masconn.cameraxobjectdetection.models.ObjectModel
import au.com.masconn.cameraxobjectdetection.models.ReferenceObjects
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.util.concurrent.Executor

/**
 * Repository class responsible for creating and configuring the ObjectDetectionAnalyzer.
 * It loads the TensorFlow Lite model and object detector options from datasource, and processes the detections
 * to map them to ObjectModel instances with real-world measurements.
 *
 * @property tfLiteModelDataSource Data source for loading the TensorFlow Lite model.
 * @property objectDetectorOptionsDataSource Data source for obtaining object detector options.
 */
class ImageAnalyzerUseCaseRepo(
    private val tfLiteModelDataSource: TfLiteModelDataSource = AssetTfLiteModelDataSource(),
    private val objectDetectorOptionsDataSource: ObjectDetectorOptionsDataSource = LocalObjectDetectorOptionsDataSource()
) {
    /**
     * Provides an ImageAnalysis use case with an analyzer to process detected objects.
     *
     * @param executor Executor to run the analyzer on.
     * @param onDetected Callback function to handle the list of detected ObjectModel instances.
     * @return Configured ImageAnalysis use case.
     */
    suspend fun provideImageAnalyzerUseCase(
        executor: Executor,
        onDetected: (List<ObjectModel>) -> Unit
    ): ImageAnalysis {
        val imageAnalyzer = createImageAnalyzer(onDetected)
        return ImageAnalysis.Builder()
            .setResolutionSelector(
                ResolutionSelector.Builder().setAspectRatioStrategy(
                    AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
                ).build()
            )
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build().apply {
                setAnalyzer(executor, imageAnalyzer)
            }
    }

    /**
     * Creates an ObjectDetectionAnalyzer with the provided detection callback.
     *
     * @param onDetected Callback function to handle the list of detected ObjectModel instances.
     * @return Configured ObjectDetectionAnalyzer instance.
     */
    suspend fun createImageAnalyzer(onDetected: (List<ObjectModel>) -> Unit): ObjectDetectionAnalyzer {
        val modelByteBuffer = tfLiteModelDataSource.loadModel()
        val options = objectDetectorOptionsDataSource.getOptions()

        return ObjectDetectionAnalyzer(
            modelByteBuffer = modelByteBuffer,
            objectDetectorOptions = options,
            onDetected = { detections, widthHeightPair ->
                val realWorldWidthRatio = findRealWorldWidthRatio(detections)
                val objectModels = detections.map { detection ->
                    detection.toObjectModel(widthHeightPair = widthHeightPair)
                }.map { objectModel ->
                    updateObjectModelWithRealWorldMeasurements(objectModel, realWorldWidthRatio)
                }
                onDetected(objectModels)
            }
        )
    }

    /**
     * Finds the real-world width ratio based on the detected reference object's width and predefined reference
     * object's real-world width.
     *
     * @param detections List of detected objects.
     * @return Real-world width ratio if a reference object is found, otherwise a default value.
     */
    private fun findRealWorldWidthRatio(detections: List<Detection>): Float {
        detections.forEach { detection ->
            val label = detection.categories.firstOrNull()?.label ?: return@forEach
            val referenceDimensions = ReferenceObjects.referenceObjectsMap[label] ?: return@forEach
            if (referenceDimensions.first.toFloat() != REAL_WORLD_WIDTH_UNKNOWN) {
                return detection.calculateRealWorldWidthRatio(referenceDimensions.first.toFloat())
            }
        }
        return REAL_WORLD_WIDTH_UNKNOWN
    }

    /**
     * Updates the ObjectModel with real-world measurements based on the real-world width ratio.
     *
     * @param objectModel The ObjectModel to update.
     * @param realWorldWidthRatio The real-world width ratio.
     * @return Updated ObjectModel with real-world measurements.
     */
    private fun updateObjectModelWithRealWorldMeasurements(
        objectModel: ObjectModel,
        realWorldWidthRatio: Float
    ): ObjectModel {
        return when {
            realWorldWidthRatio == REAL_WORLD_WIDTH_UNKNOWN -> objectModel

            ReferenceObjects.referenceObjectsMap.containsKey(objectModel.label) -> objectModel.copy(
                isReference = true,
                realWorldMeasurement = ReferenceObjects.referenceObjectsMap[objectModel.label]
                    ?: Pair(0, 0)
            )

            else -> objectModel.copy(
                realWorldMeasurement = (objectModel.width * realWorldWidthRatio).toInt() to (objectModel.height * realWorldWidthRatio).toInt()
            )
        }
    }

    companion object {
        private const val REAL_WORLD_WIDTH_UNKNOWN = 0f
    }
}