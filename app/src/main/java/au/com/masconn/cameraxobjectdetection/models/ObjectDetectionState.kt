package au.com.masconn.cameraxobjectdetection.models

/**
 * State class representing the current state of object detection.
 *
 * @property objectModels List of detected ObjectModel instances.
 */
class ObjectDetectionState(
    val objectModels: List<ObjectModel> = emptyList(),
) {
    // The first detected reference object, if any.
    private val referenceObjectModel = objectModels.firstOrNull { it.isReference }

    /**
     * Checks if there are any detected objects.
     *
     * @return True if there are detected objects, false otherwise.
     */
    fun hasDetectedObjects() = objectModels.isNotEmpty()

    /**
     * Checks if there is a detected reference object in the list.
     *
     * @return True if there is a reference object, false otherwise.
     */
    fun hasReferenceObject() = referenceObjectModel != null

    /**
     * Gets the measurement of the reference object in a formatted string.
     *
     * @return Formatted string with the reference object's label and dimensions, or a message indicating no reference object is detected.
     */
    fun getReferenceObjectMeasurement() =
        referenceObjectModel?.let { "${it.label} is ${it.realWorldMeasurement.first} x ${it.realWorldMeasurement.second} mm" }
            ?: "No reference object detected"

    fun getDetectedObjectMeasurement() = objectModels.firstOrNull {
        !it.isReference
    }
        ?.let { "${it.label} is ${it.realWorldMeasurement.first} x ${it.realWorldMeasurement.second} mm" }
        ?: "Unknown"
}