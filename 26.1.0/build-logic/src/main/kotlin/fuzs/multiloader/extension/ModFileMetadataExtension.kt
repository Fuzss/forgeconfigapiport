package fuzs.multiloader.extension

import fuzs.multiloader.neoforge.toml.NeoForgeModsTomlSpec
import net.fabricmc.loom.api.fmj.FabricModJsonV1Spec
import net.fabricmc.loom.util.fmj.FabricModJson
import org.gradle.api.Action
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

/**
 * A Gradle extension for fine-tuning auto-generated `fabric.mod.json` and `neoforge.mods.toml` files.
 */
abstract class ModFileMetadataExtension {
    /**
     * Custom configuration for the Fabric JSON spec.
     */
    @get:Nested
    abstract val json: Property<Action<FabricModJsonV1Spec>>

    /**
     * Custom configuration for the NeoForge TOML spec.
     */
    @get:Nested
    abstract val toml: Property<Action<NeoForgeModsTomlSpec>>

    fun json(configure: FabricModJsonV1Spec.() -> Unit) {
        json.set(Action(configure))
    }

    fun toml(configure: NeoForgeModsTomlSpec.() -> Unit) {
        toml.set(Action(configure))
    }

    /**
     * Defines a package prefix used when constructing entry points.
     * When defining e.g. `impl`, the default class name is moved from `com.examplemod.fabric.ExampleModFabric` to `com.examplemod.fabric.impl.ExampleModFabric`.
     *
     * Currently only has an effect on Fabric.
     *
     * Note that the entry point classes must actually exist at the new location for entry point entries to be added.
     */
    @get:Input
    @get:Optional
    abstract val packagePrefix: Property<String>

    /**
     * Marks this mod as a library for Mod Menu on Fabric.
     *
     * This will add the corresponding `Library` badge, and categorise the mod as such,
     * meaning it is hidden from the default mods list.
     */
    @get:Input
    @get:Optional
    abstract val library: Property<Boolean>

    /**
     * The file path of a JSON file used for [enum extension](https://docs.neoforged.net/docs/advanced/extensibleenums) on NeoForge.
     */
    @get:Input
    @get:Optional
    abstract val enumExtensions: Property<String>
}
