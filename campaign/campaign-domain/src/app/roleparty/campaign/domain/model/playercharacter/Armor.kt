package app.roleparty.campaign.domain.model.playercharacter

enum class ArmorCategory {
    LIGHT,
    MEDIUM,
    HEAVY,
    SHIELD
}

enum class ArmorType(
    val category: ArmorCategory,
    val baseAc: Int,
    val dexterityMaximalBonus: Int?,
    val minStrength: Int,
    val hasStealthDisadvantage: Boolean
) {
    PADDED(ArmorCategory.LIGHT, 11, null, 0, true),
    LEATHER(ArmorCategory.LIGHT, 11, null, 0, false),
    STUDDED_LEATHER(ArmorCategory.LIGHT, 12, null, 0, false),
    HIDE(ArmorCategory.MEDIUM, 12, 2, 0, false),
    CHAIN_SHIRT(ArmorCategory.MEDIUM, 13, 2, 0, false),
    SCALE_MAIL(ArmorCategory.MEDIUM, 14, 2, 0, true),
    BREASTPLATE(ArmorCategory.MEDIUM, 14, 2, 0, false),
    HALF_PLATE(ArmorCategory.MEDIUM, 15, 2, 0, true),
    RING_MAIL(ArmorCategory.HEAVY, 14, 0, 0, true),
    CHAIN_MAIL(ArmorCategory.HEAVY, 16, 0, 13, true),
    SPLINT(ArmorCategory.HEAVY, 17, 0, 15, true),
    PLATE(ArmorCategory.HEAVY, 18, 0, 15, true),
    SHIELD(ArmorCategory.SHIELD, 2, null, 0, false);

    val isLight: Boolean get() = category == ArmorCategory.LIGHT
    val isMedium: Boolean get() = category == ArmorCategory.MEDIUM
    val isHeavy: Boolean get() = category == ArmorCategory.HEAVY
    val isShield: Boolean get() = category == ArmorCategory.SHIELD

    fun calculateAc(dexModifier: Int): Int {
        if (isShield) return baseAc

        val dexBonus = when (dexterityMaximalBonus) {
            null -> dexModifier
            0 -> 0
            else -> dexModifier.coerceAtMost(dexterityMaximalBonus)
        }
        return baseAc + dexBonus
    }
}