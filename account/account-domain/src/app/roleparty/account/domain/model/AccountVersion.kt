package app.roleparty.account.domain.model

@JvmInline
value class AccountVersion private constructor(val value: Int) {
    init {
        require(value >= 0) { "Version must not be negative: $value" }
    }

    override fun toString() = value.toString()

    companion object {
        val INITIAL = AccountVersion(0)

        fun from(value: Int) = AccountVersion(value)
    }
}
