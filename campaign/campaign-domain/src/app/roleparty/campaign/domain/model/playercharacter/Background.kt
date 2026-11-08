package app.roleparty.campaign.domain.model.playercharacter

sealed interface Background {
    val originFeat: Feat
    val skillProficiencies: Set<SkillProficiency>
    val toolProficiencies: Set<ToolProficiency>

    val proficiencies: Set<Proficiency>
        get() = skillProficiencies + toolProficiencies

    data object Acolyte : Background {
        override val originFeat = MagicInitiate
        override val skillProficiencies = setOf(SkillProficiency.Insight, SkillProficiency.Religion)
        override val toolProficiencies = setOf(ArtisanToolProficiency.CalligraphersSupplies)
    }

    data class Artisan(val toolProficiency: ArtisanToolProficiency) : Background {
        override val originFeat = Crafter
        override val skillProficiencies = setOf(SkillProficiency.Investigation, SkillProficiency.Persuasion)
        override val toolProficiencies = setOf(toolProficiency)
    }

    data object Charlatan : Background {
        override val originFeat = Lucky
        override val skillProficiencies = setOf(SkillProficiency.Deception, SkillProficiency.SleightOfHand)
        override val toolProficiencies = setOf(ForgeryKit)
    }

    data object Criminal : Background {
        override val originFeat = Alert
        override val skillProficiencies = setOf(SkillProficiency.SleightOfHand, SkillProficiency.Stealth)
        override val toolProficiencies = setOf(ThievesTools)
    }

    data class Entertainer(val toolProficiency: MusicalInstrumentToolProficiency) : Background {
        override val originFeat = Musician
        override val skillProficiencies = setOf(SkillProficiency.Acrobatics, SkillProficiency.Performance)
        override val toolProficiencies = setOf(toolProficiency)
    }

    data object Farmer : Background {
        override val originFeat = Tough
        override val skillProficiencies = setOf(SkillProficiency.AnimalHandling, SkillProficiency.Nature)
        override val toolProficiencies = setOf(ArtisanToolProficiency.CarpentersTools)
    }

    data class Guard(val toolProficiency: GamingSetToolProficiency) : Background {
        override val originFeat = Alert
        override val skillProficiencies = setOf(SkillProficiency.Athletics, SkillProficiency.Perception)
        override val toolProficiencies = setOf(toolProficiency)
    }

    data object Guide : Background {
        override val originFeat = MagicInitiate
        override val skillProficiencies = setOf(SkillProficiency.Stealth, SkillProficiency.Survival)
        override val toolProficiencies = setOf(ArtisanToolProficiency.CartographersTools)
    }

    data object Hermit : Background {
        override val originFeat = Healer
        override val skillProficiencies = setOf(SkillProficiency.Medicine, SkillProficiency.Religion)
        override val toolProficiencies = setOf(HerbalismKit)
    }

    data object Merchant : Background {
        override val originFeat = Lucky
        override val skillProficiencies = setOf(SkillProficiency.AnimalHandling, SkillProficiency.Persuasion)
        override val toolProficiencies = setOf(NavigatorsTools)
    }

    data class Noble(val toolProficiency: GamingSetToolProficiency) : Background {
        override val originFeat = Skilled
        override val skillProficiencies = setOf(SkillProficiency.History, SkillProficiency.Persuasion)
        override val toolProficiencies = setOf(toolProficiency)
    }

    data object Sage : Background {
        override val originFeat = MagicInitiate
        override val skillProficiencies = setOf(SkillProficiency.Arcana, SkillProficiency.History)
        override val toolProficiencies = setOf(ArtisanToolProficiency.CalligraphersSupplies)
    }

    data object Sailor : Background {
        override val originFeat = TavernBrawler
        override val skillProficiencies = setOf(SkillProficiency.Acrobatics, SkillProficiency.Perception)
        override val toolProficiencies = setOf(NavigatorsTools)
    }

    data object Scribe : Background {
        override val originFeat = Skilled
        override val skillProficiencies = setOf(SkillProficiency.Investigation, SkillProficiency.Perception)
        override val toolProficiencies = setOf(ArtisanToolProficiency.CalligraphersSupplies)
    }

    data class Soldier(val toolProficiency: GamingSetToolProficiency) : Background {
        override val originFeat = SavageAttacker
        override val skillProficiencies = setOf(SkillProficiency.Athletics, SkillProficiency.Intimidation)
        override val toolProficiencies = setOf(toolProficiency)
    }

    data object Wayfarer : Background {
        override val originFeat = Lucky
        override val skillProficiencies = setOf(SkillProficiency.Insight, SkillProficiency.Stealth)
        override val toolProficiencies = setOf(ThievesTools)
    }
}