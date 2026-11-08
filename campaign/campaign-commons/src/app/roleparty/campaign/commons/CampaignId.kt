package app.roleparty.campaign.commons

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class CampaignId private constructor(private val value: Uuid) {
    override fun toString() = value.toString()

    fun toUuid() = value

    companion object {
        fun generate() = CampaignId(Uuid.generateV7())
        fun from(value: String) = CampaignId(Uuid.parse(value))
        fun from(value: Uuid) = CampaignId(value)
    }
}
