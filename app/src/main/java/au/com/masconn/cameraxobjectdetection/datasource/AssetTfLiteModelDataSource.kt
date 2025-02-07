package au.com.masconn.cameraxobjectdetection.datasource

import au.com.masconn.cameraxobjectdetection.MyApplication
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import kotlin.coroutines.suspendCoroutine

/**
 * Implementation of TfLiteModelDataSource that loads a TensorFlow Lite model from the assets folder.
 */
class AssetTfLiteModelDataSource : TfLiteModelDataSource {
    /**
     * Suspends the function to load the TensorFlow Lite model from the assets folder.
     *
     * @return A ByteBuffer containing the loaded model.
     */
    override suspend fun loadModel(): ByteBuffer {
        // Load the model from the assets folder
        return suspendCoroutine { continuation ->
            try {
                MyApplication.context.assets.openFd("ssd_mobilenet_v1.tflite")
                    .let {
                        FileInputStream(it.fileDescriptor).channel.map(
                            FileChannel.MapMode.READ_ONLY,
                            it.startOffset,
                            it.declaredLength
                        )
                    }.also {
                        continuation.resumeWith(Result.success(it))
                    }
            } catch (e: FileNotFoundException) {
                continuation.resumeWith(Result.failure(e))
            }
        }
    }
}