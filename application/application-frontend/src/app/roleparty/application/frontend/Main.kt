package app.roleparty.application.frontend

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.singleWindowApplication
import app.roleparty.core.designsystem.components.text.BodyText
import app.roleparty.core.designsystem.provideDesignSystem

fun main() = singleWindowApplication {
    provideDesignSystem {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            BodyText("Hello World!")
        }
    }
}