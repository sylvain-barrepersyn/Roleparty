package app.roleparty.campaign.domain.model.playercharacter

sealed interface Proficiency
sealed class SkillProficiency(val skill: Skill) : Proficiency {

    companion object {
        fun fromSkill(skill: Skill) = when (skill) {
            Skill.ACROBATICS -> Acrobatics
            Skill.ANIMAL_HANDLING -> AnimalHandling
            Skill.ARCANA -> Arcana
            Skill.ATHLETICS -> Athletics
            Skill.DECEPTION -> Deception
            Skill.HISTORY -> History
            Skill.INSIGHT -> Insight
            Skill.INTIMIDATION -> Intimidation
            Skill.INVESTIGATION -> Investigation
            Skill.MEDICINE -> Medicine
            Skill.NATURE -> Nature
            Skill.PERCEPTION -> Perception
            Skill.PERFORMANCE -> Performance
            Skill.PERSUASION -> Persuasion
            Skill.RELIGION -> Religion
            Skill.SLEIGHT_OF_HAND -> SleightOfHand
            Skill.STEALTH -> Stealth
            Skill.SURVIVAL -> Survival
        }
    }

    data object Acrobatics : SkillProficiency(Skill.ACROBATICS)
    data object AnimalHandling : SkillProficiency(Skill.ANIMAL_HANDLING)
    data object Arcana : SkillProficiency(Skill.ARCANA)
    data object Athletics : SkillProficiency(Skill.ATHLETICS)
    data object Deception : SkillProficiency(Skill.DECEPTION)
    data object History : SkillProficiency(Skill.HISTORY)
    data object Insight : SkillProficiency(Skill.INSIGHT)
    data object Intimidation : SkillProficiency(Skill.INTIMIDATION)
    data object Investigation : SkillProficiency(Skill.INVESTIGATION)
    data object Medicine : SkillProficiency(Skill.MEDICINE)
    data object Nature : SkillProficiency(Skill.NATURE)
    data object Perception : SkillProficiency(Skill.PERCEPTION)
    data object Performance : SkillProficiency(Skill.PERFORMANCE)
    data object Persuasion : SkillProficiency(Skill.PERSUASION)
    data object Religion : SkillProficiency(Skill.RELIGION)
    data object SleightOfHand : SkillProficiency(Skill.SLEIGHT_OF_HAND)
    data object Stealth : SkillProficiency(Skill.STEALTH)
    data object Survival : SkillProficiency(Skill.SURVIVAL)
}
sealed interface SavingThrowProficiency : Proficiency {
    data object StrengthSave : SavingThrowProficiency
    data object DexteritySave : SavingThrowProficiency
    data object ConstitutionSave : SavingThrowProficiency
    data object IntelligenceSave : SavingThrowProficiency
    data object WisdomSave : SavingThrowProficiency
    data object CharismaSave : SavingThrowProficiency
}
sealed interface ArmorProficiency : Proficiency {
    data object LightArmor : ArmorProficiency
    data object MediumArmor : ArmorProficiency
    data object HeavyArmor : ArmorProficiency
    data object Shields : ArmorProficiency
}
sealed interface WeaponProficiency : Proficiency {
    data object SimpleWeapons : WeaponProficiency
    data object MartialWeapons : WeaponProficiency
}
sealed interface ToolProficiency : Proficiency

sealed interface ArtisanToolProficiency : ToolProficiency {
    data object AlchemistsSupplies : ArtisanToolProficiency
    data object BrewersSupplies : ArtisanToolProficiency
    data object CalligraphersSupplies : ArtisanToolProficiency
    data object CarpentersTools : ArtisanToolProficiency
    data object CartographersTools : ArtisanToolProficiency
    data object CobblersTools : ArtisanToolProficiency
    data object CooksUtensils : ArtisanToolProficiency
    data object GlassblowersTools : ArtisanToolProficiency
    data object JewelersTools : ArtisanToolProficiency
    data object LeatherworkersTools : ArtisanToolProficiency
    data object MasonsTools : ArtisanToolProficiency
    data object PaintersSupplies : ArtisanToolProficiency
    data object PottersTools : ArtisanToolProficiency
    data object SmithsTools : ArtisanToolProficiency
    data object TinkersTools : ArtisanToolProficiency
    data object WeaversTools : ArtisanToolProficiency
    data object WoodcarversTools : ArtisanToolProficiency
}

data object DisguiseKit : ToolProficiency
data object ForgeryKit : ToolProficiency
data object HerbalismKit : ToolProficiency
data object NavigatorsTools : ToolProficiency
data object PoisonersKit : ToolProficiency
data object ThievesTools : ToolProficiency

sealed interface GamingSetToolProficiency : ToolProficiency {
    data object DiceSet : GamingSetToolProficiency
    data object DragonchessSet : GamingSetToolProficiency
    data object PlayingCardSet : GamingSetToolProficiency
    data object ThreeDragonAnteSet : GamingSetToolProficiency
}

sealed interface MusicalInstrumentToolProficiency : ToolProficiency {
    data object Bagpipes : MusicalInstrumentToolProficiency
    data object Drum : MusicalInstrumentToolProficiency
    data object Dulcimer : MusicalInstrumentToolProficiency
    data object Flute : MusicalInstrumentToolProficiency
    data object Horn : MusicalInstrumentToolProficiency
    data object Lute : MusicalInstrumentToolProficiency
    data object Lyre : MusicalInstrumentToolProficiency
    data object PanFlute : MusicalInstrumentToolProficiency
    data object Shawm : MusicalInstrumentToolProficiency
    data object Viol : MusicalInstrumentToolProficiency
}
sealed interface VehicleProficiency : Proficiency {
    data object LandVehicles : VehicleProficiency
    data object WaterVehicles : VehicleProficiency
}
