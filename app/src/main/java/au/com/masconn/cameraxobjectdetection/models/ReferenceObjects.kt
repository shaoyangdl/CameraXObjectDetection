package au.com.masconn.cameraxobjectdetection.models

/**
 * Singleton object that holds a map of reference objects and their real-world dimensions.
 * The key is the label of the object, and the value is a pair representing the width and height in millimeters.
 */
object ReferenceObjects {
    val referenceObjectsMap = mapOf("mouse" to (74 to 115), "coin" to (15 to 15))
}