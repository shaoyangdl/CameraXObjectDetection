package au.com.masconn.cameraxobjectdetection.datasource

import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector

/**
 * Interface representing a data source for providing ObjectDetector options.
 */
interface ObjectDetectorOptionsDataSource {
    /**
     * Suspends the function to get the ObjectDetector options.
     *
     * @return An ObjectDetector.ObjectDetectorOptions instance.
     */
    suspend fun getOptions(): ObjectDetector.ObjectDetectorOptions
}