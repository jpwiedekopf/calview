package net.wiedekopf.calview.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import net.wiedekopf.calview.mobile.theme.CalViewTheme
import net.wiedekopf.calview.mobile.ui.AppScaffold

class MobileMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalViewTheme(dynamicColor = false) {
                AppScaffold()
            }
        }
    }
}
