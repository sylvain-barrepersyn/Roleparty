package app.roleparty.core.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import sv.lib.squircleshape.GentleSquircleShape

object Shapes {
    val rectangle: Shape = RectangleShape
    val rounded: Shape = GentleSquircleShape(16.dp)
    val roundedSmall: Shape = GentleSquircleShape(8.dp)
    val circle: Shape = CircleShape
}
