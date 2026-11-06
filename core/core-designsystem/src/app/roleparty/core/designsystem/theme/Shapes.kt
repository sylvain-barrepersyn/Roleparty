package app.roleparty.core.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@ConsistentCopyVisibility
data class Shapes internal constructor(
    val rounded: Shape,
    val circle: Shape,
)

internal val defaultShapes = Shapes(
    rounded = RoundedCornerShape(8.dp),
    circle = CircleShape,
)
