package fuzs.multiloader.neoforge.toml

import kotlinx.serialization.Serializable
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import javax.inject.Inject

abstract class NeoForgeModsTomlSpec {
    @get:Inject
    abstract val objects: ObjectFactory

    /**
     * The language loader used by the mod(s). Can be used to support alternative language structures, such as Kotlin objects for the main file, or different methods of determining the entrypoint, such as an interface or method. NeoForge provides the Java loader [`"javafml"`](https://docs.neoforged.net/docs/gettingstarted/modfiles/#javafml-and-mod).
     */
    @get:Input
    @get:Optional
    abstract val modLoader: Property<String>

    /**
     * The acceptable version range of the language loader, expressed as a [Maven Version Range](https://maven.apache.org/enforcer/enforcer-rules/versionRanges.html). For `javafml`, this is currently version `1`. If no version is specified, then any version of the mod loader can be used.
     */
    @get:Input
    @get:Optional
    abstract val loaderVersion: Property<String>

    /**
     * The license the mod(s) in this JAR are provided under. It is suggested that this is set to the [SPDX identifier](https://spdx.org/licenses/) you are using and/or a link to the license. You can visit https://choosealicense.com/ to help pick the license you want to use.
     */
    @get:Input
    abstract val license: Property<String>

    /**
     * When `true`, the mod(s)'s resources will be displayed as a separate resource pack on the 'Resource Packs' menu, rather than being combined with the 'Mod Resources' pack.
     */
    @get:Input
    @get:Optional
    abstract val showAsResourcePack: Property<Boolean>

    /**
     * When `true`, the mod(s)'s data files will be displayed as a separate data pack on the 'Data Packs' menu, rather than being combined with the 'Mod Data' pack.
     */
    @get:Input
    @get:Optional
    abstract val showAsDataPack: Property<Boolean>

    /**
     * An array of services your mod uses. This is consumed as part of the created module for the mod from NeoForge's implementation of the Java Platform Module System.
     */
    @get:Input
    @get:Optional
    abstract val services: ListProperty<String>

    /**
     * A table of substitution properties. This is used by `StringSubstitutor` to replace `${file.<key>}` with its corresponding value.
     */
    @get:Input
    @get:Optional
    abstract val properties: MapProperty<String, Any>

    /**
     * A URL representing the place to report and track issues with the mod(s).
     */
    @get:Input
    @get:Optional
    abstract val issueTrackerURL: Property<String>

    /**
     * Mod-specific properties are tied to the specified mod using the `[[mods]]` header. This is an [array of tables](https://toml.io/en/v1.0.0#array-of-tables); all key/value properties will be attached to that mod until the next header.
     */
    @get:Nested
    @get:Optional
    abstract val mods: ListProperty<ModSpec>

    /**
     * See [features](https://docs.neoforged.net/docs/gettingstarted/modfiles#features).
     */
    @get:Nested
    @get:Optional
    abstract val features: ListProperty<FeaturesSpec>

    /**
     * A table of key/values associated with this mod. Unused by NeoForge, but is mainly for use by mods.
     */
    @get:Nested
    @get:Optional
    abstract val modProperties: ListProperty<ModPropertiesSpec>

    /**
     * [Access Transformer-specific properties](https://docs.neoforged.net/docs/advanced/accesstransformers/#adding-ats) are tied to the specified access transformer using the `[[accessTransformers]]` header. This is an [array of tables](https://toml.io/en/v1.0.0#array-of-tables); all key/value properties will be attached to that access transformer until the next header. The access transformer header is optional; however, when specified, all elements are mandatory.
     */
    @get:Nested
    @get:Optional
    abstract val accessTransformers: ListProperty<AccessTransformerSpec>

    /**
     * [Mixin Configuration Properties](https://github.com/SpongePowered/Mixin/wiki/Introduction-to-Mixins---The-Mixin-Environment#mixin-configuration-files) are tied to the specified mixin config using the `[[mixins]]` header. This is an [array of tables](https://toml.io/en/v1.0.0#array-of-tables); all key/value properties will be attached to that mixin block until the next header. The mixin header is optional; however, when specified, all elements are mandatory.
     */
    @get:Nested
    @get:Optional
    abstract val mixins: ListProperty<MixinSpec>

    /**
     * Mods can specify their dependencies, which are checked by NeoForge before loading the mods. These configurations are created using the [array of tables](https://toml.io/en/v1.0.0#array-of-tables) `[[dependencies.<modid>]]`, where `modid` is the identifier of the mod that consumes the dependency.
     */
    @get:Nested
    @get:Optional
    abstract val dependencies: ListProperty<DependencyPropertiesSpec>

    /**
     * Arbitrary custom properties.
     */
    @get:Nested
    @get:Optional
    abstract val extraProperties: ListProperty<ExtraPropertiesSpec>

    /**
     * Arbitrary custom array properties.
     */
    @get:Nested
    @get:Optional
    abstract val extraArrayProperties: ListProperty<ExtraPropertiesSpec>

    init {
        // These are mandatory on older FML versions, so provide the defaults from modern versions.
        modLoader.convention("javafml")
        loaderVersion.convention("*")
        license.convention("All Rights Reserved")
    }

    fun mod(modId: String) {
        mod(modId) {
            // NO-OP
        }
    }

    fun mod(modId: String, action: Action<ModSpec>) {
        mod {
            this.modId.set(modId)
            action.execute(this)
        }
    }

    fun mod(action: Action<ModSpec>) {
        val entry = objects.newInstance(ModSpec::class.java).also { action.execute(it) }
        mods.add(entry)
    }

    abstract class ModSpec {
        /**
         * See [The Mod ID](https://docs.neoforged.net/docs/gettingstarted/modfiles#the-mod-id).
         */
        @get:Input
        abstract val modId: Property<String>

        /**
         * An override namespace for the mod. Must also be a valid [mod ID](https://docs.neoforged.net/docs/gettingstarted/modfiles#the-mod-id), but may additionally include dots or dashes. Currently unused.
         */
        @get:Input
        @get:Optional
        abstract val namespace: Property<String>

        /**
         * The version of the mod, preferably in a [variation of Maven versioning](https://docs.neoforged.net/docs/gettingstarted/versioning/). When set to `${file.jarVersion}`, it will be replaced with the value of the `Implementation-Version` property in the JAR's manifest (displays as `0.0NONE` in a development environment).
         */
        @get:Input
        @get:Optional
        abstract val version: Property<String>

        /**
         * The display name of the mod. Used when representing the mod on a screen (e.g., mod list, mod mismatch).
         */
        @get:Input
        @get:Optional
        abstract val displayName: Property<String>

        /**
         * The description of the mod shown in the mod list screen. It is recommended to use a [multiline literal string](https://toml.io/en/v1.0.0#string). This value is also translatable, see [Translating Mod Metadata](https://docs.neoforged.net/docs/resources/client/i18n/#translating-mod-metadata) for more info.
         */
        @get:Input
        @get:Optional
        abstract val description: Property<String>

        /**
         * The name and extension of an image file used on the mods list screen. The location must be an absolute path starting from the root of the JAR or source set (e.g. `src/main/resources` for the main source set). Valid filename characters are lowercase letters (`a-z`), digits (`0-9`), slashes, (`/`), underscores (`_`), periods (`.`) and hyphens (`-`). The complete character set is `[a-z0-9_-.]`.
         */
        @get:Input
        @get:Optional
        abstract val logoFile: Property<String>

        /**
         * Whether to use `GL_LINEAR*` (true) or `GL_NEAREST*` (false) to render the `logoFile`. In simpler terms, this means whether the logo should be blurred or not when trying to scale the logo.
         */
        @get:Input
        @get:Optional
        abstract val logoBlur: Property<Boolean>

        /**
         * A URL to a JSON used by the [update checker](https://docs.neoforged.net/docs/misc/updatechecker/) to make sure the mod you are playing is the latest version.
         */
        @get:Input
        @get:Optional
        abstract val updateJSONURL: Property<String>

        /**
         * A URL to the download page of the mod. Currently unused.
         */
        @get:Input
        @get:Optional
        abstract val modUrl: Property<String>

        /**
         * Credits and acknowledges for the mod shown on the mod list screen.
         */
        @get:Input
        @get:Optional
        abstract val credits: Property<String>

        /**
         * The authors of the mod shown on the mod list screen.
         */
        @get:Input
        @get:Optional
        abstract val authors: Property<String>

        /**
         * A URL to the display page of the mod shown on the mod list screen.
         */
        @get:Input
        @get:Optional
        abstract val displayURL: Property<String>

        /**
         * The file path of a JSON file used for [enum extension](https://docs.neoforged.net/docs/advanced/extensibleenums).
         */
        @get:Input
        @get:Optional
        abstract val enumExtensions: Property<String>

        /**
         * The file path of a JSON file used for [feature flags](https://docs.neoforged.net/docs/advanced/featureflags).
         */
        @get:Input
        @get:Optional
        abstract val featureFlags: Property<String>
    }

    fun features(modId: String, propertyKey: String, propertyValue: Any) {
        features(modId) {
            put(propertyKey, propertyValue)
        }
    }

    fun features(modId: String, properties: Map<String, Any>) {
        features(modId) {
            putAll(properties)
        }
    }

    fun features(modId: String, action: Action<MapProperty<String, Any>>) {
        val entry = objects.newInstance(FeaturesSpec::class.java).also {
            it.modId.set(modId)
            action.execute(it.properties)
        }
        features.add(entry)
    }

    abstract class FeaturesSpec {
        /**
         * The identifier of the mod that consumes these properties.
         */
        @get:Input
        abstract val modId: Property<String>

        /**
         * The custom properties.
         */
        @get:Input
        abstract val properties: MapProperty<String, Any>
    }

    fun modProperties(modId: String, propertyKey: String, propertyValue: Any) {
        modProperties(modId) {
            put(propertyKey, propertyValue)
        }
    }

    fun modProperties(modId: String, properties: Map<String, Any>) {
        modProperties(modId) {
            putAll(properties)
        }
    }

    fun modProperties(modId: String, action: Action<MapProperty<String, Any>>) {
        val entry = objects.newInstance(ModPropertiesSpec::class.java).also {
            it.modId.set(modId)
            action.execute(it.properties)
        }
        modProperties.add(entry)
    }

    abstract class ModPropertiesSpec {
        /**
         * The identifier of the mod that consumes these properties.
         */
        @get:Input
        abstract val modId: Property<String>

        /**
         * The custom properties.
         */
        @get:Input
        abstract val properties: MapProperty<String, Any>
    }

    fun accessTransformer(file: String) {
        accessTransformer(file) {
            // NO-OP
        }
    }

    fun accessTransformer(file: String, action: Action<AccessTransformerSpec>) {
        accessTransformer {
            this.file.set(file)
            action.execute(this)
        }
    }

    fun accessTransformer(action: Action<AccessTransformerSpec>) {
        val entry = objects.newInstance(AccessTransformerSpec::class.java).also { action.execute(it) }
        accessTransformers.add(entry)
    }

    abstract class AccessTransformerSpec {
        /**
         * See [Adding ATs](https://docs.neoforged.net/docs/advanced/accesstransformers#adding-ats).
         */
        @get:Input
        abstract val file: Property<String>
    }

    fun mixin(config: String) {
        mixin(config) {
            // NO-OP
        }
    }

    fun mixin(config: String, action: Action<MixinSpec>) {
        mixin {
            this.config.set(config)
            action.execute(this)
        }
    }

    fun mixin(action: Action<MixinSpec>) {
        val entry = objects.newInstance(MixinSpec::class.java).also { action.execute(it) }
        mixins.add(entry)
    }

    abstract class MixinSpec {
        /**
         * The location of the mixin configuration file.
         */
        @get:Input
        abstract val config: Property<String>

        @get:Input
        @get:Optional
        abstract val requiredMods: ListProperty<String>

        @get:Input
        @get:Optional
        abstract val behaviorVersion: Property<String>
    }

    fun dependency(modId: String, dependencyId: String) {
        dependency(modId) {
            this.modId.set(dependencyId)
        }
    }

    fun dependency(modId: String, dependencyId: String, versionRange: String) {
        dependency(modId) {
            this.modId.set(dependencyId)
            this.versionRange.set(versionRange)
        }
    }

    fun dependency(modId: String, action: Action<DependencySpec>) {
        val entry = objects.newInstance(DependencyPropertiesSpec::class.java).also {
            it.modId.set(modId)
            action.execute(it.properties.get())
        }
        dependencies.add(entry)
    }

    abstract class DependencyPropertiesSpec {
        @get:Inject
        abstract val objects: ObjectFactory

        /**
         * The identifier of the mod that consumes this dependency.
         */
        @get:Input
        abstract val modId: Property<String>

        /**
         * The dependency, which is checked by NeoForge before loading the mods.
         */
        @get:Nested
        abstract val properties: Property<DependencySpec>

        init {
            properties.convention(objects.newInstance(DependencySpec::class.java))
        }
    }

    abstract class DependencySpec {
        /**
         * The identifier of the mod added as a dependency.
         */
        @get:Input
        abstract val modId: Property<String>

        /**
         * Specifies the nature of this dependency: `"required"` is the default and prevents the mod from loading if this dependency is missing; `"optional"` will not prevent the mod from loading if the dependency is missing, but still validates that the dependency is compatible; `"incompatible"` prevents the mod from loading if this dependency is present; `"discouraged"` still allows the mod to load if the dependency is present, but presents a warning to the user.
         */
        @get:Input
        @get:Optional
        abstract val type: Property<Type>

        /**
         * An optional user-facing message to describe why this dependency is required, or why it is incompatible.
         */
        @get:Input
        @get:Optional
        abstract val reason: Property<String>

        /**
         * The acceptable version range of the language loader, expressed as a [Maven Version Range](https://maven.apache.org/enforcer/enforcer-rules/versionRanges.html). An empty string matches any version.
         */
        @get:Input
        @get:Optional
        abstract val versionRange: Property<String>

        /**
         * Defines if the mod must load before (`"BEFORE"`) or after (`"AFTER"`) this dependency. If the ordering does not matter, return `"NONE"`.
         */
        @get:Input
        @get:Optional
        abstract val ordering: Property<Ordering>

        /**
         * The [physical side](https://docs.neoforged.net/docs/concepts/sides) the dependency must be present on: `"CLIENT"`, `"SERVER"`, or `"BOTH"`.
         */
        @get:Input
        @get:Optional
        abstract val side: Property<Side>

        /**
         * A URL to the download page of the dependency. Currently unused.
         */
        @get:Input
        @get:Optional
        abstract val referralUrl: Property<String>

        @Serializable
        enum class Type {
            /**
             * Prevents the game from loading if the dependency is missing.
             */
            REQUIRED,

            /**
             * Does not prevent the game from loading if the dependency is missing.
             */
            OPTIONAL,

            /**
             * Prevents the game from loading if the dependency is loaded.
             */
            INCOMPATIBLE,

            /**
             * Shows a warning if the dependency is loaded.
             */
            DISCOURAGED
        }

        @Serializable
        enum class Ordering {
            /**
             * The mod consuming this dependency must load before the dependency.
             */
            BEFORE,

            /**
             * The mod consuming this dependency must load after the dependency.
             */
            AFTER,

            /**
             * The ordering does not matter.
             */
            NONE
        }

        @Serializable
        enum class Side {
            /**
             * When you open your Minecraft launcher, select a Minecraft installation and press play, you boot up a physical client.
             */
            CLIENT,

            /**
             * The physical server, also known as dedicated server, is what opens when you launch a Minecraft server JAR.
             */
            SERVER,

            /**
             * All physical sides.
             */
            BOTH
        }
    }

    fun extraProperties(property: String, propertyKey: String, propertyValue: Any) {
        extraProperties(property) {
            put(propertyKey, propertyValue)
        }
    }

    fun extraProperties(property: String, properties: Map<String, Any>) {
        extraProperties(property) {
            putAll(properties)
        }
    }

    fun extraProperties(property: String, action: Action<MapProperty<String, Any>>) {
        val entry = objects.newInstance(ExtraPropertiesSpec::class.java).also {
            it.property.set(property)
            action.execute(it.properties)
        }
        extraProperties.add(entry)
    }

    fun extraArrayProperties(property: String, propertyKey: String, propertyValue: Any) {
        extraArrayProperties(property) {
            put(propertyKey, propertyValue)
        }
    }

    fun extraArrayProperties(property: String, properties: Map<String, Any>) {
        extraArrayProperties(property) {
            putAll(properties)
        }
    }

    fun extraArrayProperties(property: String, action: Action<MapProperty<String, Any>>) {
        val entry = objects.newInstance(ExtraPropertiesSpec::class.java).also {
            it.property.set(property)
            action.execute(it.properties)
        }
        extraArrayProperties.add(entry)
    }

    abstract class ExtraPropertiesSpec {
        /**
         * The key of these properties.
         */
        @get:Input
        abstract val property: Property<String>

        /**
         * The custom properties.
         */
        @get:Input
        abstract val properties: MapProperty<String, Any>
    }
}
