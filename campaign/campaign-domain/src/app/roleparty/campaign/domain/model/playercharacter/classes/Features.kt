package app.roleparty.campaign.domain.model.playercharacter.classes

data class SpellSlots(
    val l1: Int = 0,
    val l2: Int = 0,
    val l3: Int = 0,
    val l4: Int = 0,
    val l5: Int = 0,
    val l6: Int = 0,
    val l7: Int = 0,
    val l8: Int = 0,
    val l9: Int = 0,
) : PlayableClass.Feature

data class ExtraAttack(val numberOfAttacks: Int) : PlayableClass.Feature

internal fun fullCasterSlots(classLevel: Int): SpellSlots = when (classLevel) {
    1 -> SpellSlots(l1 = 2)
    2 -> SpellSlots(l1 = 3)
    3 -> SpellSlots(l1 = 4, l2 = 2)
    4 -> SpellSlots(l1 = 4, l2 = 3)
    5 -> SpellSlots(l1 = 4, l2 = 3, l3 = 2)
    6 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3)
    7 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 1)
    8 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 2)
    9 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 1)
    10 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 2)
    11 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 2, l6 = 1)
    12 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 2, l6 = 1)
    13 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 2, l6 = 1, l7 = 1)
    14 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 2, l6 = 1, l7 = 1)
    15 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 2, l6 = 1, l7 = 1, l8 = 1)
    16 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 2, l6 = 1, l7 = 1, l8 = 1)
    17 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 2, l6 = 1, l7 = 1, l8 = 1, l9 = 1)
    18 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 3, l6 = 1, l7 = 1, l8 = 1, l9 = 1)
    19 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 3, l6 = 2, l7 = 1, l8 = 1, l9 = 1)
    else -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 3, l6 = 2, l7 = 2, l8 = 1, l9 = 1)
}

internal fun halfCasterSlots(classLevel: Int): SpellSlots = when {
    classLevel < 2 -> SpellSlots()
    classLevel < 3 -> SpellSlots(l1 = 2)
    classLevel < 5 -> SpellSlots(l1 = 3)
    classLevel < 7 -> SpellSlots(l1 = 4, l2 = 2)
    classLevel < 9 -> SpellSlots(l1 = 4, l2 = 3)
    classLevel < 11 -> SpellSlots(l1 = 4, l2 = 3, l3 = 2)
    classLevel < 13 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3)
    classLevel < 15 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 1)
    classLevel < 17 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 2)
    classLevel < 19 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 1)
    classLevel < 20 -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 2)
    else -> SpellSlots(l1 = 4, l2 = 3, l3 = 3, l4 = 3, l5 = 2)
}
