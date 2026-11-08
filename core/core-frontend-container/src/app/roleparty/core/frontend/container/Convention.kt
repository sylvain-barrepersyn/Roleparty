package app.roleparty.core.frontend.container

import java.lang.reflect.ParameterizedType

internal object Convention {

    private val ROLES = listOf("State", "Action", "Event", "ViewModel")

    fun check(type: Class<*>, base: Class<*>, suffix: String) {
        val name = type.simpleName
        require(name.endsWith(suffix) && name.length > suffix.length) { "$name must be named <Screen>$suffix" }
        val screen = name.removeSuffix(suffix)

        val superclass = type.genericSuperclass
        require(superclass is ParameterizedType && superclass.rawType == base) { "$name must extend ${base.simpleName} directly" }

        superclass.actualTypeArguments.zip(ROLES).forEach { (argument = first, role = second) ->
            require(argument is Class<*>) { "$name: the $role type must be a class" }
            require(argument.simpleName == "$screen$role") { "$name: ${argument.simpleName} must be named $screen$role" }
            if (role != "ViewModel") require(argument.kotlin.isSealed) { "$name: ${argument.simpleName} must be a sealed interface" }
        }
    }
}
