package app.roleparty.media.commons

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ImageId private constructor(private val value: Uuid) {
    override fun toString() = value.toString()

    fun toUuid() = value

    companion object {
        fun generate() = ImageId(Uuid.generateV7())
        fun from(value: String) = ImageId(Uuid.parse(value))
        fun from(value: Uuid) = ImageId(value)
    }
}
