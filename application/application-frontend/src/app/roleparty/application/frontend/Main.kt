package app.roleparty.application.frontend

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import app.roleparty.core.designsystem.components.surface.Surface
import app.roleparty.core.designsystem.components.text.BodyText
import app.roleparty.core.designsystem.BaseSurface

fun main() = singleWindowApplication {
    BaseSurface(Modifier.fillMaxSize()) {
        Surface(Modifier.padding(32.dp)) {
            BodyText("Hello World!")
        }
    }
}