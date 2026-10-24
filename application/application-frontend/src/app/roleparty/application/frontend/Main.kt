package app.roleparty.application.frontend

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.singleWindowApplication
import app.roleparty.core.designsystem.theme.Theme

fun main() = singleWindowApplication {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        BasicText("Hello World!", style = TextStyle(color = Theme.colors.primary))
    }
}