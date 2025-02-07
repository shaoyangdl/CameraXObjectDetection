package au.com.masconn.cameraxobjectdetection

import au.com.masconn.cameraxobjectdetection.extensions.calculateRealWorldWidthRatio
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import org.tensorflow.lite.task.gms.vision.detector.Detection

class DetectionExtsTest {

    @Test
    fun `test calculateRealWorldWidthRatio`() {
        // Arrange
        val detection = mockk<Detection>(relaxed = true)
        val realWorldWidthInMM = 200f
        every { detection.boundingBox.width() } returns 100f
        // Act
        val ratio = detection.calculateRealWorldWidthRatio(realWorldWidthInMM)

        // Assert
        assertEquals(2.0f, ratio, 0.0f)
    }
}