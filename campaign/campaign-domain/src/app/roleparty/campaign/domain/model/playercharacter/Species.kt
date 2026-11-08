package app.roleparty.campaign.domain.model.playercharacter

enum class Size {
    TINY, SMALL, MEDIUM, LARGE
}

private typealias GlobalSize = Size

sealed interface Species {
    val size: Size

    data object Dragonborn : Species {
        override val size = Size.MEDIUM
    }

    data object Dwarf : Species {
        override val size = Size.MEDIUM
    }

    data object Elf : Species {
        override val size = Size.MEDIUM
    }

    data object Gnome : Species {
        override val size = Size.SMALL
    }

    data object Halfling : Species {
        override val size = Size.SMALL
    }

    data object Orc : Species {
        override val size = Size.MEDIUM
    }

    data object Tiefling : Species {
        override val size = Size.MEDIUM
    }

    data class Human(val chosenSize: Size = Size.MEDIUM) : Species {
        override val size: GlobalSize = when (chosenSize) {
            Size.SMALL -> GlobalSize.SMALL
            Size.MEDIUM -> GlobalSize.MEDIUM
        }

        enum class Size {
            SMALL,
            MEDIUM
        }
    }

    data class Goliath(val chosenSize: Size = Size.MEDIUM) : Species {
        override val size: GlobalSize = when (chosenSize) {
            Size.MEDIUM -> GlobalSize.MEDIUM
            Size.LARGE -> GlobalSize.LARGE
        }

        enum class Size {
            MEDIUM,
            LARGE
        }
    }
}