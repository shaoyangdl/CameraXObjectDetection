package au.com.masconn.cameraxobjectdetection

import au.com.masconn.cameraxobjectdetection.models.ObjectDetectionState
import au.com.masconn.cameraxobjectdetection.models.ObjectModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ObjectDetectionStateTest {

    @Test
    fun `test hasDetectedObjects returns true when objectModels is not empty`() {
        val objectModels = listOf(
            ObjectModel(
                label = "obj1",
                confidence = 0.6f,
                left = 10f,
                top = 20f,
                width = 30f,
                height = 40f,
                imageWidth = 480,
                imageHeight = 640,
                isReference = true,
                realWorldMeasurement = Pair(10, 20)
            ),
            ObjectModel(
                label = "obj2",
                confidence = 0.6f,
                left = 20f,
                top = 20f,
                width = 30f,
                height = 40f,
                imageWidth = 480,
                imageHeight = 640,
                realWorldMeasurement = Pair(0, 0)
            )
        )
        val state = ObjectDetectionState(objectModels)
        assertTrue(state.hasDetectedObjects())
    }

    @Test
    fun `test hasDetectedObjects returns false when objectModels is empty`() {
        val state = ObjectDetectionState()
        assertFalse(state.hasDetectedObjects())
    }

    @Test
    fun `test hasReferenceObject returns true when there is a reference object`() {
        val objectModels = listOf(
            ObjectModel(
                label = "obj1",
                confidence = 0.6f,
                left = 10f,
                top = 20f,
                width = 30f,
                height = 40f,
                imageWidth = 480,
                imageHeight = 640,
                isReference = true,
                realWorldMeasurement = Pair(10, 20)
            ),
            ObjectModel(
                label = "obj2",
                confidence = 0.6f,
                left = 20f,
                top = 20f,
                width = 30f,
                height = 40f,
                imageWidth = 480,
                imageHeight = 640,
                realWorldMeasurement = Pair(0, 0)
            )
        )
        val state = ObjectDetectionState(objectModels)
        assertTrue(state.hasReferenceObject())
    }

    @Test
    fun `test hasReferenceObject returns false when there is no reference object`() {
        val objectModels = listOf(
            ObjectModel(
                label = "obj1",
                confidence = 0.6f,
                left = 10f,
                top = 20f,
                width = 30f,
                height = 40f,
                imageWidth = 480,
                imageHeight = 640,
                realWorldMeasurement = Pair(0, 0)
            ),
            ObjectModel(
                label = "obj2",
                confidence = 0.6f,
                left = 20f,
                top = 20f,
                width = 30f,
                height = 40f,
                imageWidth = 480,
                imageHeight = 640,
                realWorldMeasurement = Pair(0, 0)
            )
        )
        val state = ObjectDetectionState(objectModels)
        assertFalse(state.hasReferenceObject())
    }

    @Test
    fun `test getReferenceObjectMeasurement returns correct measurement`() {
        val objectModels = listOf(
            ObjectModel(
                label = "obj1",
                confidence = 0.6f,
                left = 10f,
                top = 20f,
                width = 30f,
                height = 40f,
                imageWidth = 480,
                imageHeight = 640,
                isReference = true,
                realWorldMeasurement = Pair(10, 20)
            ),
            ObjectModel(
                label = "obj2",
                confidence = 0.6f,
                left = 20f,
                top = 20f,
                width = 30f,
                height = 40f,
                imageWidth = 480,
                imageHeight = 640,
                realWorldMeasurement = Pair(0, 0)
            )
        )
        val state = ObjectDetectionState(objectModels)
        assertEquals("obj1 is 10 x 20 mm", state.getReferenceObjectMeasurement())
    }

    @Test
    fun `test getReferenceObjectMeasurement returns no reference object message`() {
        val state = ObjectDetectionState()
        assertEquals("No reference object detected", state.getReferenceObjectMeasurement())
    }

    @Test
    fun `test getDetectedObjectMeasurement returns correct measurement`() {
        val objectModels = listOf(
            ObjectModel(
                label = "obj1",
                confidence = 0.6f,
                left = 10f,
                top = 20f,
                width = 30f,
                height = 40f,
                imageWidth = 480,
                imageHeight = 640,
                isReference = true,
                realWorldMeasurement = Pair(10, 20)
            ),
            ObjectModel(
                label = "obj2",
                confidence = 0.6f,
                left = 10f,
                top = 20f,
                width = 30f,
                height = 40f,
                imageWidth = 480,
                imageHeight = 640,
                realWorldMeasurement = Pair(10, 20)
            )
        )
        val state = ObjectDetectionState(objectModels)
        assertEquals("obj2 is 10 x 20 mm", state.getDetectedObjectMeasurement())
    }

    @Test
    fun `test getDetectedObjectMeasurement returns unknown message`() {
        val state = ObjectDetectionState()
        assertEquals("Unknown", state.getDetectedObjectMeasurement())
    }
}