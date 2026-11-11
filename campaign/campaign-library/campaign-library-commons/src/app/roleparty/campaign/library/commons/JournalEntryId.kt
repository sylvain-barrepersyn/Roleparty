package app.roleparty.campaign.library.commons

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class JournalEntryId private constructor(private val value: Uuid) {
    override fun toString() = value.toString()

    fun toUuid() = value

    companion object {
        fun generate() = JournalEntryId(Uuid.generateV7())
        fun from(value: String) = JournalEntryId(Uuid.parse(value))
        fun from(value: Uuid) = JournalEntryId(value)
    }
}
