package au.com.masconn.cameraxobjectdetection.ml


import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.gms.vision.detector.Detection
import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector
import java.nio.ByteBuffer

/**
 * Analyzer class for performing object detection using TensorFlow Lite.
 * It processes the camera frames and detects objects in the images.
 *
 * @property modelByteBuffer The ByteBuffer containing the TensorFlow Lite model.
 * @property objectDetectorOptions The options for configuring the ObjectDetector.
 * @property onDetected Callback function to handle the detected objects and image dimensions.
 */
class ObjectDetectionAnalyzer(
    private val modelByteBuffer: ByteBuffer,
    private val objectDetectorOptions: ObjectDetector.ObjectDetectorOptions,
    private val onDetected: (List<Detection>, Pair<Int, Int>) -> Unit
) : ImageAnalysis.Analyzer {

    // ObjectDetector instance created from the model and options provided by gms vision
    private val detector: ObjectDetector =
        ObjectDetector.createFromBufferAndOptions(modelByteBuffer, objectDetectorOptions)

    /**
     * Analyzes the image from the camera and detects objects.
     *
     * @param image The image to analyze.
     */
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        // Get the rotation degrees of the image
        val rotationDegrees = image.imageInfo.rotationDegrees
        detect(image.toBitmap(), rotationDegrees)
        image.close()
    }

    fun detect(bitmap: Bitmap, rotationDegrees: Int) {
        // Create an ImageProcessor to rotate the image
        val imageProcessor = ImageProcessor.Builder().add(Rot90Op(-rotationDegrees / 90)).build()
        // Convert the ImageProxy to a TensorImage
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
        // Perform object detection on the TensorImage
        val results = detector.detect(tensorImage)
        onDetected(results, tensorImage.width to tensorImage.height)
    }
}