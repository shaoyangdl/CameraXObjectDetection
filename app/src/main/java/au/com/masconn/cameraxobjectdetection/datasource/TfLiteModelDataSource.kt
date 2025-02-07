package au.com.masconn.cameraxobjectdetection.datasource

import java.nio.ByteBuffer

/**
 * Interface representing a data source for loading TensorFlow Lite models.
 */
interface TfLiteModelDataSource {
    /**
     * Suspends the function to load the TensorFlow Lite model.
     *
     * @return A ByteBuffer containing the loaded model.
     */
    suspend fun loadModel(): ByteBuffer
}