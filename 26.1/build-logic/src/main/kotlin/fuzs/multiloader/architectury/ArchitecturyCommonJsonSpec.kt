package fuzs.multiloader.architectury

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import javax.inject.Inject

abstract class ArchitecturyCommonJsonSpec {
    @get:Inject
    abstract val objects: ObjectFactory

    /**
     * An access widener file; mainly useful for libraries that declare transitive access wideners, i.e. AWs that apply to projects that depend on the library.
     */
    @get:Input
    @get:Optional
    abstract val accessWidener: Property<String>

    /**
     * Definitions for Compile-time interface injection.
     *
     * The keys are the Minecraft class names in Intermediary mappings, and the values are the interfaces to inject as arrays.
     */
    @get:Nested
    @get:Optional
    abstract val injectedInterfaces: ListProperty<InjectedInterfacesSpec>

    fun injectedInterfaces(clazz: String, injectedInterface: String) {
        injectedInterfaces(clazz) {
            add(injectedInterface)
        }
    }

    fun injectedInterfaces(clazz: String, injectedInterfaces: List<String>) {
        injectedInterfaces(clazz) {
            addAll(injectedInterfaces)
        }
    }

    fun injectedInterfaces(clazz: String, action: Action<ListProperty<String>>) {
        val entry = objects.newInstance(InjectedInterfacesSpec::class.java).also {
            it.clazz.set(clazz)
            action.execute(it.injectedInterfaces)
        }
        injectedInterfaces.add(entry)
    }

    abstract class InjectedInterfacesSpec {
        /**
         * The name of the class using [Intermediary](https://github.com/FabricMC/intermediary) mappings.
         *
         * Example: `net/minecraft/class_1234`
         */
        @get:Input
        abstract val clazz: Property<String>

        /**
         * A list of the injected interfaces provided as JVM internal names (e.g. `com/example/Outer$Inner` instead of `com.example.Outer.Inner`).
         *
         * Example: `com/example/MyInterface`
         */
        @get:Input
        abstract val injectedInterfaces: ListProperty<String>
    }
}
