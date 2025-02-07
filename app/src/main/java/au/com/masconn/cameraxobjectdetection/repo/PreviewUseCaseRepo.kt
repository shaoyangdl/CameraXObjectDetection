package au.com.masconn.cameraxobjectdetection.repo

import androidx.camera.core.Preview
import androidx.camera.core.Preview.SurfaceProvider

class PreviewUseCaseRepo {
    fun providePreviewUseCase(surfaceProvider: SurfaceProvider): Preview {
        // Provide the Preview use case
        return Preview.Builder().build().apply {
            setSurfaceProvider(surfaceProvider)
        }
    }
}