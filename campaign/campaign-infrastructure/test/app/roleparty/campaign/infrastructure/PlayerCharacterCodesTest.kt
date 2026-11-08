package app.roleparty.campaign.infrastructure

import app.roleparty.campaign.contract.web.AlignmentName
import app.roleparty.campaign.contract.web.BackgroundName
import app.roleparty.campaign.contract.web.LanguageName
import app.roleparty.campaign.contract.web.PlayableClassName
import app.roleparty.campaign.contract.web.SkillName
import app.roleparty.campaign.contract.web.SpeciesName
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class PlayerCharacterCodesTest : FunSpec({

    test("every web value should map to a domain value and back") {
        SpeciesName.entries.forEach { PlayerCharacterCodes.code(PlayerCharacterCodes.species(it.name, null).shouldNotBeNull()) shouldBe it.name }
        AlignmentName.entries.forEach { PlayerCharacterCodes.code(PlayerCharacterCodes.alignment(it.name).shouldNotBeNull()) shouldBe it.name }
        SkillName.entries.forEach { PlayerCharacterCodes.code(PlayerCharacterCodes.skill(it.name).shouldNotBeNull()) shouldBe it.name }
        LanguageName.entries.forEach { PlayerCharacterCodes.code(PlayerCharacterCodes.language(it.name).shouldNotBeNull()) shouldBe it.name }
        PlayableClassName.entries.forEach { CharacterClass.valueOf(it.name).skillCount shouldBe it.skillCount }
    }

    test("backgrounds should require a tool of the right kind only when they offer a choice") {
        val tools = mapOf("ARTISAN" to "SMITHS_TOOLS", "ENTERTAINER" to "PAN_FLUTE", "GUARD" to "DICE_SET", "NOBLE" to "DRAGONCHESS_SET", "SOLDIER" to "PLAYING_CARD_SET")
        BackgroundName.entries.forEach { name ->
            val tool = tools[name.name]
            val background = PlayerCharacterCodes.background(name.name, tool).shouldNotBeNull()
            PlayerCharacterCodes.code(background) shouldBe name.name
            PlayerCharacterCodes.toolCode(background) shouldBe tool
        }
        PlayerCharacterCodes.background("ARTISAN", null).shouldBeNull()
        PlayerCharacterCodes.background("ARTISAN", "PAN_FLUTE").shouldBeNull()
    }

    test("species with a size choice should keep it") {
        PlayerCharacterCodes.sizeCode(PlayerCharacterCodes.species("GOLIATH", "LARGE").shouldNotBeNull()) shouldBe "LARGE"
        PlayerCharacterCodes.species("HUMAN", "LARGE").shouldBeNull()
    }
})
