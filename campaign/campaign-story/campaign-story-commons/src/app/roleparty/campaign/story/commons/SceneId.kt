package app.roleparty.campaign.story.commons

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class SceneId private constructor(private val value: Uuid) {
    override fun toString() = value.toString()

    fun toUuid() = value

    companion object {
        fun generate() = SceneId(Uuid.generateV7())
        fun from(value: String) = SceneId(Uuid.parse(value))
        fun from(value: Uuid) = SceneId(value)
    }
}
