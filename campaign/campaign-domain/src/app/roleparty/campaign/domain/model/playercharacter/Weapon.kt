package app.roleparty.campaign.domain.model.playercharacter

enum class WeaponCategory {
    SIMPLE,
    MARTIAL
}

enum class RangeType {
    MELEE,
    RANGED
}

enum class WeaponMastery {
    CLEAVE,
    GRAZE,
    NICK,
    PUSH,
    SAP,
    SLOW,
    TOPPLE,
    VEX,
    NONE
}

enum class WeaponType(
    val category: WeaponCategory,
    val rangeType: RangeType,
    val mastery: WeaponMastery
) {
    CLUB(WeaponCategory.SIMPLE, RangeType.MELEE, WeaponMastery.SLOW),
    DAGGER(WeaponCategory.SIMPLE, RangeType.MELEE, WeaponMastery.NICK),
    GREATCLUB(WeaponCategory.SIMPLE, RangeType.MELEE, WeaponMastery.PUSH),
    HANDAXE(WeaponCategory.SIMPLE, RangeType.MELEE, WeaponMastery.VEX),
    JAVELIN(WeaponCategory.SIMPLE, RangeType.MELEE, WeaponMastery.SLOW),
    LIGHT_HAMMER(WeaponCategory.SIMPLE, RangeType.MELEE, WeaponMastery.NICK),
    MACE(WeaponCategory.SIMPLE, RangeType.MELEE, WeaponMastery.SAP),
    QUARTERSTAFF(WeaponCategory.SIMPLE, RangeType.MELEE, WeaponMastery.TOPPLE),
    SICKLE(WeaponCategory.SIMPLE, RangeType.MELEE, WeaponMastery.NICK),
    SPEAR(WeaponCategory.SIMPLE, RangeType.MELEE, WeaponMastery.SAP),

    DART(WeaponCategory.SIMPLE, RangeType.RANGED, WeaponMastery.VEX),
    LIGHT_CROSSBOW(WeaponCategory.SIMPLE, RangeType.RANGED, WeaponMastery.SLOW),
    SHORTBOW(WeaponCategory.SIMPLE, RangeType.RANGED, WeaponMastery.VEX),
    SLING(WeaponCategory.SIMPLE, RangeType.RANGED, WeaponMastery.SLOW),

    BATTLEAXE(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.TOPPLE),
    FLAIL(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.SAP),
    GLAIVE(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.GRAZE),
    GREATAXE(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.CLEAVE),
    GREATSWORD(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.GRAZE),
    HALBERD(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.CLEAVE),
    LANCE(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.TOPPLE),
    LONGSWORD(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.SAP),
    MAUL(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.TOPPLE),
    MORNINGSTAR(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.SAP),
    PIKE(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.PUSH),
    RAPIER(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.VEX),
    SCIMITAR(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.NICK),
    SHORTSWORD(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.VEX),
    TRIDENT(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.TOPPLE),
    WAR_PICK(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.SAP),
    WARHAMMER(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.PUSH),
    WHIP(WeaponCategory.MARTIAL, RangeType.MELEE, WeaponMastery.SLOW),

    BLOWGUN(WeaponCategory.MARTIAL, RangeType.RANGED, WeaponMastery.VEX),
    HAND_CROSSBOW(WeaponCategory.MARTIAL, RangeType.RANGED, WeaponMastery.VEX),
    HEAVY_CROSSBOW(WeaponCategory.MARTIAL, RangeType.RANGED, WeaponMastery.PUSH),
    LONGBOW(WeaponCategory.MARTIAL, RangeType.RANGED, WeaponMastery.SLOW);

    val isMelee: Boolean get() = rangeType == RangeType.MELEE
    val isRanged: Boolean get() = rangeType == RangeType.RANGED
    val isMartial: Boolean get() = category == WeaponCategory.MARTIAL
    val isSimple: Boolean get() = category == WeaponCategory.SIMPLE
}