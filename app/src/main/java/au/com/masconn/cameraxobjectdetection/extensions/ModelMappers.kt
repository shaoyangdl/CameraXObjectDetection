package au.com.masconn.cameraxobjectdetection.extensions

import au.com.masconn.cameraxobjectdetection.models.ObjectModel
import org.tensorflow.lite.task.gms.vision.detector.Detection

/**
 * Extension function to map a Detection object to an ObjectModel.
 *
 * @param widthHeightPair A pair containing the width and height of the image.
 * @return An ObjectModel instance representing the detected object.
 */
fun Detection.toObjectModel(widthHeightPair: Pair<Int, Int>): ObjectModel {
    return ObjectModel(
        label = categories.firstOrNull()?.label ?: "UNKNOWN_OBJECT",
        confidence = categories.firstOrNull()?.score ?: 0f,
        left = boundingBox.left,
        top = boundingBox.top,
        width = boundingBox.width(),
        height = boundingBox.height(),
        imageWidth = widthHeightPair.first,
        imageHeight = widthHeightPair.second,
    )
}