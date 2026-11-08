package app.roleparty.campaign.domain.model.playercharacter.classes

import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.ArmorCategory
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency

sealed class Barbarian(
    level: Level,
    features: List<Feature>,
    proficiencyBonus: Int,
) : PlayableClass(
    hitDice = 12,
    level = level,
    features = features,
    armorTrainings = setOf(ArmorCategory.LIGHT, ArmorCategory.MEDIUM, ArmorCategory.SHIELD),
    savingThrows = setOf(SavingThrowProficiency.StrengthSave, SavingThrowProficiency.ConstitutionSave),
    proficiencyBonus = proficiencyBonus
) {
    data class Rage(val uses: Int, val damageBonus: Int) : Feature
    data class WeaponMastery(val weapons: Int) : Feature
    data class BrutalStrike(val damageDice: Int) : Feature
    data object UnarmoredDefense : Feature
    data object DangerSense : Feature
    data object RecklessAttack : Feature
    data object PrimalKnowledge : Feature
    data object FastMovement : Feature
    data object FeralInstinct : Feature
    data object InstinctivePounce : Feature
    data object RelentlessRage : Feature
    data object PersistentRage : Feature
    data object IndomitableMight : Feature
    data object EpicBoon : Feature
    data object PrimalChampion : Feature
    data object Frenzy : Feature
    data object MindlessRage : Feature
    data object Retaliation : Feature
    data object IntimidatingPresence : Feature

    enum class Subclass {
        PATH_OF_THE_BERSERKER
    }

    companion object {
        internal val STANDARD_ABILITY_SCORES = AbilityScores(
            strength = 15,
            dexterity = 13,
            constitution = 14,
            intelligence = 10,
            wisdom = 12,
            charisma = 8
        )
    }
}

class Barbarian1 : Barbarian(
    level = Level.LEVEL_1,
    features = listOf(Rage(uses = 2, damageBonus = 2), UnarmoredDefense, WeaponMastery(weapons = 2)),
    proficiencyBonus = 2
)

class Barbarian2 : Barbarian(
    level = Level.LEVEL_2,
    features = listOf(Rage(uses = 2, damageBonus = 2), UnarmoredDefense, WeaponMastery(weapons = 2), DangerSense, RecklessAttack),
    proficiencyBonus = 2
)

class BerserkerBarbarian3 : Barbarian(
    level = Level.LEVEL_3,
    features = listOf(Rage(uses = 3, damageBonus = 2), UnarmoredDefense, WeaponMastery(weapons = 2), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy),
    proficiencyBonus = 2
)

class BerserkerBarbarian4 : Barbarian(
    level = Level.LEVEL_4,
    features = listOf(Rage(uses = 3, damageBonus = 2), UnarmoredDefense, WeaponMastery(weapons = 3), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy),
    proficiencyBonus = 2
)

class BerserkerBarbarian5 : Barbarian(
    level = Level.LEVEL_5,
    features = listOf(Rage(uses = 3, damageBonus = 2), UnarmoredDefense, WeaponMastery(weapons = 3), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement),
    proficiencyBonus = 3
)

class BerserkerBarbarian6 : Barbarian(
    level = Level.LEVEL_6,
    features = listOf(Rage(uses = 4, damageBonus = 2), UnarmoredDefense, WeaponMastery(weapons = 3), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage),
    proficiencyBonus = 3
)

class BerserkerBarbarian7 : Barbarian(
    level = Level.LEVEL_7,
    features = listOf(Rage(uses = 4, damageBonus = 2), UnarmoredDefense, WeaponMastery(weapons = 3), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce),
    proficiencyBonus = 3
)

class BerserkerBarbarian8 : Barbarian(
    level = Level.LEVEL_8,
    features = listOf(Rage(uses = 4, damageBonus = 2), UnarmoredDefense, WeaponMastery(weapons = 3), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce),
    proficiencyBonus = 3
)

class BerserkerBarbarian9 : Barbarian(
    level = Level.LEVEL_9,
    features = listOf(Rage(uses = 4, damageBonus = 3), UnarmoredDefense, WeaponMastery(weapons = 4), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce, BrutalStrike(damageDice = 1)),
    proficiencyBonus = 4
)

class BerserkerBarbarian10 : Barbarian(
    level = Level.LEVEL_10,
    features = listOf(Rage(uses = 4, damageBonus = 3), UnarmoredDefense, WeaponMastery(weapons = 4), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce, BrutalStrike(damageDice = 1), Retaliation),
    proficiencyBonus = 4
)

class BerserkerBarbarian11 : Barbarian(
    level = Level.LEVEL_11,
    features = listOf(Rage(uses = 4, damageBonus = 3), UnarmoredDefense, WeaponMastery(weapons = 4), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce, BrutalStrike(damageDice = 1), Retaliation, RelentlessRage),
    proficiencyBonus = 4
)

class BerserkerBarbarian12 : Barbarian(
    level = Level.LEVEL_12,
    features = listOf(Rage(uses = 5, damageBonus = 3), UnarmoredDefense, WeaponMastery(weapons = 4), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce, BrutalStrike(damageDice = 1), Retaliation, RelentlessRage),
    proficiencyBonus = 4
)

class BerserkerBarbarian13 : Barbarian(
    level = Level.LEVEL_13,
    features = listOf(Rage(uses = 5, damageBonus = 3), UnarmoredDefense, WeaponMastery(weapons = 4), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce, BrutalStrike(damageDice = 2), Retaliation, RelentlessRage),
    proficiencyBonus = 5
)

class BerserkerBarbarian14 : Barbarian(
    level = Level.LEVEL_14,
    features = listOf(Rage(uses = 5, damageBonus = 3), UnarmoredDefense, WeaponMastery(weapons = 4), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce, BrutalStrike(damageDice = 2), Retaliation, RelentlessRage, IntimidatingPresence),
    proficiencyBonus = 5
)

class BerserkerBarbarian15 : Barbarian(
    level = Level.LEVEL_15,
    features = listOf(Rage(uses = 5, damageBonus = 3), UnarmoredDefense, WeaponMastery(weapons = 4), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce, BrutalStrike(damageDice = 2), Retaliation, RelentlessRage, IntimidatingPresence, PersistentRage),
    proficiencyBonus = 5
)

class BerserkerBarbarian16 : Barbarian(
    level = Level.LEVEL_16,
    features = listOf(Rage(uses = 5, damageBonus = 4), UnarmoredDefense, WeaponMastery(weapons = 5), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce, BrutalStrike(damageDice = 2), Retaliation, RelentlessRage, IntimidatingPresence, PersistentRage),
    proficiencyBonus = 5
)

class BerserkerBarbarian17 : Barbarian(
    level = Level.LEVEL_17,
    features = listOf(Rage(uses = -1, damageBonus = 4), UnarmoredDefense, WeaponMastery(weapons = 5), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce, BrutalStrike(damageDice = 3), Retaliation, RelentlessRage, IntimidatingPresence, PersistentRage),
    proficiencyBonus = 6
)

class BerserkerBarbarian18 : Barbarian(
    level = Level.LEVEL_18,
    features = listOf(Rage(uses = -1, damageBonus = 4), UnarmoredDefense, WeaponMastery(weapons = 5), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce, BrutalStrike(damageDice = 3), Retaliation, RelentlessRage, IntimidatingPresence, PersistentRage, IndomitableMight),
    proficiencyBonus = 6
)

class BerserkerBarbarian19 : Barbarian(
    level = Level.LEVEL_19,
    features = listOf(Rage(uses = -1, damageBonus = 4), UnarmoredDefense, WeaponMastery(weapons = 5), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce, BrutalStrike(damageDice = 3), Retaliation, RelentlessRage, IntimidatingPresence, PersistentRage, IndomitableMight, EpicBoon),
    proficiencyBonus = 6
)

class BerserkerBarbarian20 : Barbarian(
    level = Level.LEVEL_20,
    features = listOf(Rage(uses = -1, damageBonus = 4), UnarmoredDefense, WeaponMastery(weapons = 5), DangerSense, RecklessAttack, PrimalKnowledge, Frenzy, ExtraAttack(2), FastMovement, MindlessRage, FeralInstinct, InstinctivePounce, BrutalStrike(damageDice = 3), Retaliation, RelentlessRage, IntimidatingPresence, PersistentRage, IndomitableMight, EpicBoon, PrimalChampion),
    proficiencyBonus = 6
)
