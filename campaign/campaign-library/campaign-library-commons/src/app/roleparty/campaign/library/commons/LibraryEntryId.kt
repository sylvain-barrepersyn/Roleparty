package app.roleparty.campaign.library.commons

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class LibraryEntryId private constructor(private val value: Uuid) {
    override fun toString() = value.toString()

    fun toUuid() = value

    companion object {
        fun generate() = LibraryEntryId(Uuid.generateV7())
        fun from(value: String) = LibraryEntryId(Uuid.parse(value))
        fun from(value: Uuid) = LibraryEntryId(value)
    }
}
