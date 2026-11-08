package app.roleparty.campaign.domain.model

import app.roleparty.campaign.commons.CampaignId
import java.security.SecureRandom

data class Invitation(
    val code: Code,
    val campaignId: CampaignId,
    val used: Boolean = false,
) {

    @JvmInline
    value class Code private constructor(private val value: String) {
        override fun toString() = value

        companion object {
            private const val CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
            private const val BLOCKS = 4
            private const val BLOCK_LENGTH = 3
            private val random = SecureRandom()

            fun generate() = Code(List(BLOCKS) { String(CharArray(BLOCK_LENGTH) { CHARS[random.nextInt(CHARS.length)] }) }.joinToString("-"))

            fun from(value: String) = Code(value.trim().uppercase())
        }
    }
}
