package app.roleparty.campaign.creature.domain.model.playercharacter

enum class HitDie(val faces: Int) {
    D4(4), D6(6), D8(8), D10(10), D12(12);

    val fixedValue get() = faces / 2 + 1
}
