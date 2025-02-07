package au.com.masconn.cameraxobjectdetection.extensions

import org.tensorflow.lite.task.gms.vision.detector.Detection

/**
 * Extension function to calculate the real-world width ratio of a detected object.
 *
 * @param realWorldWidthInMM The real-world width of the object in millimeters.
 * @return The ratio of the real-world width to the bounding box width.
 */
fun Detection.calculateRealWorldWidthRatio(realWorldWidthInMM: Float): Float {
    return realWorldWidthInMM / boundingBox.width()
}