package app.roleparty.campaign.domain.model.playercharacter.classes

import kotlin.reflect.KClass

enum class CharacterClass(
    val skillCount: Int,
    private val base: KClass<out PlayableClass>,
    private val levels: List<() -> PlayableClass>,
) {
    BARBARIAN(
        2,
        Barbarian::class,
        listOf(
            ::Barbarian1, ::Barbarian2, ::BerserkerBarbarian3, ::BerserkerBarbarian4, ::BerserkerBarbarian5,
            ::BerserkerBarbarian6, ::BerserkerBarbarian7, ::BerserkerBarbarian8, ::BerserkerBarbarian9, ::BerserkerBarbarian10,
            ::BerserkerBarbarian11, ::BerserkerBarbarian12, ::BerserkerBarbarian13, ::BerserkerBarbarian14, ::BerserkerBarbarian15,
            ::BerserkerBarbarian16, ::BerserkerBarbarian17, ::BerserkerBarbarian18, ::BerserkerBarbarian19, ::BerserkerBarbarian20,
        ),
    ),
    BARD(
        3,
        Bard::class,
        listOf(
            ::Bard1, ::Bard2, ::LoreBard3, ::LoreBard4, ::LoreBard5,
            ::LoreBard6, ::LoreBard7, ::LoreBard8, ::LoreBard9, ::LoreBard10,
            ::LoreBard11, ::LoreBard12, ::LoreBard13, ::LoreBard14, ::LoreBard15,
            ::LoreBard16, ::LoreBard17, ::LoreBard18, ::LoreBard19, ::LoreBard20,
        ),
    ),
    CLERIC(
        2,
        Cleric::class,
        listOf(
            ::Cleric1, ::Cleric2, ::LifeCleric3, ::LifeCleric4, ::LifeCleric5,
            ::LifeCleric6, ::LifeCleric7, ::LifeCleric8, ::LifeCleric9, ::LifeCleric10,
            ::LifeCleric11, ::LifeCleric12, ::LifeCleric13, ::LifeCleric14, ::LifeCleric15,
            ::LifeCleric16, ::LifeCleric17, ::LifeCleric18, ::LifeCleric19, ::LifeCleric20,
        ),
    ),
    DRUID(
        2,
        Druid::class,
        listOf(
            ::Druid1, ::Druid2, ::LandDruid3, ::LandDruid4, ::LandDruid5,
            ::LandDruid6, ::LandDruid7, ::LandDruid8, ::LandDruid9, ::LandDruid10,
            ::LandDruid11, ::LandDruid12, ::LandDruid13, ::LandDruid14, ::LandDruid15,
            ::LandDruid16, ::LandDruid17, ::LandDruid18, ::LandDruid19, ::LandDruid20,
        ),
    ),
    FIGHTER(
        2,
        Fighter::class,
        listOf(
            ::Fighter1, ::Fighter2, ::ChampionFighter3, ::ChampionFighter4, ::ChampionFighter5,
            ::ChampionFighter6, ::ChampionFighter7, ::ChampionFighter8, ::ChampionFighter9, ::ChampionFighter10,
            ::ChampionFighter11, ::ChampionFighter12, ::ChampionFighter13, ::ChampionFighter14, ::ChampionFighter15,
            ::ChampionFighter16, ::ChampionFighter17, ::ChampionFighter18, ::ChampionFighter19, ::ChampionFighter20,
        ),
    ),
    MONK(
        2,
        Monk::class,
        listOf(
            ::Monk1, ::Monk2, ::OpenHandMonk3, ::OpenHandMonk4, ::OpenHandMonk5,
            ::OpenHandMonk6, ::OpenHandMonk7, ::OpenHandMonk8, ::OpenHandMonk9, ::OpenHandMonk10,
            ::OpenHandMonk11, ::OpenHandMonk12, ::OpenHandMonk13, ::OpenHandMonk14, ::OpenHandMonk15,
            ::OpenHandMonk16, ::OpenHandMonk17, ::OpenHandMonk18, ::OpenHandMonk19, ::OpenHandMonk20,
        ),
    ),
    PALADIN(
        2,
        Paladin::class,
        listOf(
            ::Paladin1, ::Paladin2, ::DevotionPaladin3, ::DevotionPaladin4, ::DevotionPaladin5,
            ::DevotionPaladin6, ::DevotionPaladin7, ::DevotionPaladin8, ::DevotionPaladin9, ::DevotionPaladin10,
            ::DevotionPaladin11, ::DevotionPaladin12, ::DevotionPaladin13, ::DevotionPaladin14, ::DevotionPaladin15,
            ::DevotionPaladin16, ::DevotionPaladin17, ::DevotionPaladin18, ::DevotionPaladin19, ::DevotionPaladin20,
        ),
    ),
    RANGER(
        3,
        Ranger::class,
        listOf(
            ::Ranger1, ::Ranger2, ::BeastMasterRanger3, ::BeastMasterRanger4, ::BeastMasterRanger5,
            ::BeastMasterRanger6, ::BeastMasterRanger7, ::BeastMasterRanger8, ::BeastMasterRanger9, ::BeastMasterRanger10,
            ::BeastMasterRanger11, ::BeastMasterRanger12, ::BeastMasterRanger13, ::BeastMasterRanger14, ::BeastMasterRanger15,
            ::BeastMasterRanger16, ::BeastMasterRanger17, ::BeastMasterRanger18, ::BeastMasterRanger19, ::BeastMasterRanger20,
        ),
    ),
    ROGUE(
        4,
        Rogue::class,
        listOf(
            ::Rogue1, ::Rogue2, ::ThiefRogue3, ::ThiefRogue4, ::ThiefRogue5,
            ::ThiefRogue6, ::ThiefRogue7, ::ThiefRogue8, ::ThiefRogue9, ::ThiefRogue10,
            ::ThiefRogue11, ::ThiefRogue12, ::ThiefRogue13, ::ThiefRogue14, ::ThiefRogue15,
            ::ThiefRogue16, ::ThiefRogue17, ::ThiefRogue18, ::ThiefRogue19, ::ThiefRogue20,
        ),
    ),
    SORCERER(
        2,
        Sorcerer::class,
        listOf(
            ::Sorcerer1, ::Sorcerer2, ::WildMagicSorcerer3, ::WildMagicSorcerer4, ::WildMagicSorcerer5,
            ::WildMagicSorcerer6, ::WildMagicSorcerer7, ::WildMagicSorcerer8, ::WildMagicSorcerer9, ::WildMagicSorcerer10,
            ::WildMagicSorcerer11, ::WildMagicSorcerer12, ::WildMagicSorcerer13, ::WildMagicSorcerer14, ::WildMagicSorcerer15,
            ::WildMagicSorcerer16, ::WildMagicSorcerer17, ::WildMagicSorcerer18, ::WildMagicSorcerer19, ::WildMagicSorcerer20,
        ),
    ),
    WARLOCK(
        2,
        Warlock::class,
        listOf(
            ::Warlock1, ::Warlock2, ::FiendWarlock3, ::FiendWarlock4, ::FiendWarlock5,
            ::FiendWarlock6, ::FiendWarlock7, ::FiendWarlock8, ::FiendWarlock9, ::FiendWarlock10,
            ::FiendWarlock11, ::FiendWarlock12, ::FiendWarlock13, ::FiendWarlock14, ::FiendWarlock15,
            ::FiendWarlock16, ::FiendWarlock17, ::FiendWarlock18, ::FiendWarlock19, ::FiendWarlock20,
        ),
    ),
    WIZARD(
        2,
        Wizard::class,
        listOf(
            ::Wizard1, ::Wizard2, ::EvocationWizard3, ::EvocationWizard4, ::EvocationWizard5,
            ::EvocationWizard6, ::EvocationWizard7, ::EvocationWizard8, ::EvocationWizard9, ::EvocationWizard10,
            ::EvocationWizard11, ::EvocationWizard12, ::EvocationWizard13, ::EvocationWizard14, ::EvocationWizard15,
            ::EvocationWizard16, ::EvocationWizard17, ::EvocationWizard18, ::EvocationWizard19, ::EvocationWizard20,
        ),
    );

    val maxLevel get() = levels.size

    fun atLevel(level: Int): PlayableClass? = levels.getOrNull(level - 1)?.invoke()

    companion object {
        fun of(playableClass: PlayableClass) = entries.first { it.base.isInstance(playableClass) }
    }
}
