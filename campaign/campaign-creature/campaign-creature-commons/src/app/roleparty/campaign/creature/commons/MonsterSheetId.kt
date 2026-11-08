package app.roleparty.campaign.creature.commons

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class MonsterSheetId private constructor(private val value: Uuid) {
    override fun toString() = value.toString()

    fun toUuid() = value

    companion object {
        fun generate() = MonsterSheetId(Uuid.generateV7())
        fun from(value: String) = MonsterSheetId(Uuid.parse(value))
        fun from(value: Uuid) = MonsterSheetId(value)
    }
}
