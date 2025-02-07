package au.com.masconn.cameraxobjectdetection.models

/**
 * Data class representing an object detected in an image.
 *
 * @property label The label of the detected object.
 * @property confidence The confidence score of the detection.
 * @property left The left coordinate of the bounding box.
 * @property top The top coordinate of the bounding box.
 * @property width The width of the bounding box.
 * @property height The height of the bounding box.
 * @property imageWidth The width of the image in which the object was detected.
 * @property imageHeight The height of the image in which the object was detected.
 * @property isReference Flag indicating if the object is a reference object.
 * @property realWorldMeasurement The real-world dimensions of the object in millimeters, represented as a pair (width, height).
 */
data class ObjectModel(
    val label: String,
    val confidence: Float,
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float,
    val imageWidth: Int,
    val imageHeight: Int,
    val isReference: Boolean = false,
    val realWorldMeasurement: Pair<Int, Int> = Pair(0, 0)
)
