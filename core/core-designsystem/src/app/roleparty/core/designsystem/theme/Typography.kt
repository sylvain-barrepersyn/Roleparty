package app.roleparty.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.roleparty.designsystem.resources.*
import org.jetbrains.compose.resources.Font

@ConsistentCopyVisibility
data class Typography internal constructor(
    val bodyLarge: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    val title: TextStyle,
    val subtitle: TextStyle,
    val headline: TextStyle,
)

@Composable
internal fun rememberAppTypography(): Typography {
    val body = FontFamily(
        Font(
            resource = Res.font.Libron_Regular,
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        ),
        Font(
            resource = Res.font.Libron_Bold,
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        ),
        Font(
            resource = Res.font.Libron_Italic,
            weight = FontWeight.Normal,
            style = FontStyle.Italic,
        ),
        Font(
            resource = Res.font.Libron_BoldItalic,
            weight = FontWeight.Bold,
            style = FontStyle.Italic
        ),
    )

    val title = FontFamily(
        Font(
            resource = Res.font.Rubik_VariableFont_wght,
            weight = FontWeight(400),
            variationSettings = FontVariation.Settings(FontVariation.weight(400))
        ),
        Font(
            resource = Res.font.Rubik_VariableFont_wght,
            weight = FontWeight(700),
            variationSettings = FontVariation.Settings(FontVariation.weight(700))
        ),
        Font(
            resource = Res.font.Rubik_VariableFont_wght,
            weight = FontWeight(800),
            variationSettings = FontVariation.Settings(FontVariation.weight(800))
        )
    )

    val textPrimary = Theme.colors.textPrimary
    val textSecondary = Theme.colors.textSecondary

    return remember(body, title, textPrimary, textSecondary) {
        Typography(
            bodyLarge = TextStyle(
                fontFamily = body,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
                color = textSecondary
            ),
            body = TextStyle(
                fontFamily = body,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.2.sp,
                color = textSecondary
            ),
            bodySmall = TextStyle(
                fontFamily = body,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.1.sp,
                color = textSecondary
            ),
            title = TextStyle(
                fontFamily = title,
                fontWeight = FontWeight(700),
                fontSize = 18.sp,
                lineHeight = 20.sp,
                color = textPrimary
            ),
            subtitle = TextStyle(
                fontFamily = title,
                fontWeight = FontWeight(400),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                color = textSecondary
            ),
            headline = TextStyle(
                fontFamily = title,
                fontWeight = FontWeight(800),
                fontSize = 28.sp,
                lineHeight = 33.2.sp,
                color = textPrimary
            )
        )
    }
}
