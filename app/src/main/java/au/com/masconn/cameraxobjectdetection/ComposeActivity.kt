package au.com.masconn.cameraxobjectdetection

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import au.com.masconn.cameraxobjectdetection.ui.MainScreen
import au.com.masconn.cameraxobjectdetection.ui.theme.CameraXObjectDetectionTheme

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            CameraXObjectDetectionTheme {
                MainScreen()
            }
        }
    }
}