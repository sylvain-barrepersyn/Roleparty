package app.roleparty.core.designsystem

import androidx.compose.runtime.Composable
import app.roleparty.core.designsystem.theme.provideTheme

@Composable
fun provideDesignSystem(content: @Composable () -> Unit) {
    provideTheme {
        content()
    }
}