package fuzs.multiloader.mixin

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import javax.inject.Inject

abstract class MixinConfigJsonSpec {
    @get:Inject
    abstract val objects: ObjectFactory

    /**
     * The parent configuration resource of this configuration
     */
    @get:Input
    @get:Optional
    abstract val parent: Property<String>

    /**
     * The target environment for this configuration
     */
    @get:Input
    @get:Optional
    abstract val target: Property<String>

    /**
     * Minimum Mixin version required for this configuration
     */
    @get:Input
    @get:Optional
    abstract val minVersion: Property<String>

    /**
     * List of required features
     */
    @get:Input
    @get:Optional
    abstract val requiredFeatures: ListProperty<String>

    /**
     * Compatibility level for this configuration
     */
    @get:Input
    @get:Optional
    abstract val compatibilityLevel: Property<String>

    /**
     * Whether this mixin configuration is required or not
     */
    @get:Input
    @get:Optional
    abstract val required: Property<Boolean>

    /**
     * Priority of this configuration
     */
    @get:Input
    @get:Optional
    abstract val priority: Property<Int>

    /**
     * Priority of mixins in this configuration
     */
    @get:Input
    @get:Optional
    abstract val mixinPriority: Property<Int>

    /**
     * Base package for mixins
     */
    @get:Input
    abstract val mixinPackage: Property<String>

    /**
     * List of mixin classes
     */
    @get:Input
    @get:Optional
    abstract val mixins: ListProperty<String>

    /**
     * List of client-side mixin classes
     */
    @get:Input
    @get:Optional
    abstract val client: ListProperty<String>

    /**
     * List of server-side mixin classes
     */
    @get:Input
    @get:Optional
    abstract val server: ListProperty<String>

    /**
     * Whether to set source file information
     */
    @get:Input
    @get:Optional
    abstract val setSourceFile: Property<Boolean>

    /**
     * Reference map resource
     */
    @get:Input
    @get:Optional
    abstract val refmap: Property<String>

    /**
     * Reference map wrapper class
     */
    @get:Input
    @get:Optional
    abstract val refmapWrapper: Property<String>

    /**
     * Whether to enable verbose logging
     */
    @get:Input
    @get:Optional
    abstract val verbose: Property<Boolean>

    /**
     * Plugin class to use
     */
    @get:Input
    @get:Optional
    abstract val plugin: Property<String>

    /**
     * Injector options
     */
    @get:Nested
    @get:Optional
    abstract val injectors: Property<InjectorsSpec>

    /**
     * Overwrite options
     */
    @get:Nested
    @get:Optional
    abstract val overwrites: Property<OverwritesSpec>

    /**
     * [Mixin Extras](https://github.com/LlamaLad7/MixinExtras) options
     */
    @get:Nested
    @get:Optional
    abstract val mixinExtras: Property<MixinExtrasSpec>

    fun mixin(value: String) {
        mixin(*arrayOf(value))
    }

    fun mixin(vararg values: String) {
        values.forEach { mixins.add(it) }
    }

    fun accessor(value: String) {
        accessor(*arrayOf(value))
    }

    fun accessor(vararg values: String) {
        values.forEach { mixins.add("accessor.$it") }
    }

    fun clientMixin(value: String) {
        clientMixin(*arrayOf(value))
    }

    fun clientMixin(vararg values: String) {
        values.forEach { client.add("client.$it") }
    }

    fun clientAccessor(value: String) {
        clientAccessor(*arrayOf(value))
    }

    fun clientAccessor(vararg values: String) {
        values.forEach { client.add("client.accessor.$it") }
    }

    fun serverMixin(value: String) {
        serverMixin(*arrayOf(value))
    }

    fun serverMixin(vararg values: String) {
        values.forEach { server.add("server.$it") }
    }

    fun serverAccessor(value: String) {
        serverAccessor(*arrayOf(value))
    }

    fun serverAccessor(vararg values: String) {
        values.forEach { server.add("server.accessor.$it") }
    }

    fun injectors(action: Action<InjectorsSpec>) {
        val entry = objects.newInstance(InjectorsSpec::class.java).also { action.execute(it) }
        injectors.set(entry)
    }

    abstract class InjectorsSpec {
        /**
         * Specifies the default value for `require` to be used when no
         * explicit value is defined on the injector. Setting this value to 1
         * essentially makes all injectors in the config automatically required.
         * Individual injectors can still be marked optional by explicitly
         * setting their `require` value to 0.
         */
        @get:Input
        @get:Optional
        abstract val defaultRequire: Property<Int>

        /**
         * Specifies the name for injector groups which have no explicit group
         * name defined. It is recommended to set this value when grouping
         * injectors to support global injector groupings in the future.
         */
        @get:Input
        @get:Optional
        abstract val defaultGroup: Property<String>

        /**
         * The namespace for custom injection points and dynamic selectors
         */
        @get:Input
        @get:Optional
        abstract val namespace: Property<String>

        /**
         * List of fully-qualified custom injection point classes to register
         */
        @get:Input
        @get:Optional
        abstract val injectionPoints: ListProperty<String>

        /**
         * List of fully-qualified dynamic selector classes to register
         */
        @get:Input
        @get:Optional
        abstract val dynamicSelectors: ListProperty<String>

        /**
         * Allows the max Shift.By value to adjusted from the environment
         * default, max value is 5
         */
        @get:Input
        @get:Optional
        abstract val maxShiftBy: Property<Int>
    }

    fun overwrites(action: Action<OverwritesSpec>) {
        val entry = objects.newInstance(OverwritesSpec::class.java).also { action.execute(it) }
        overwrites.set(entry)
    }

    abstract class OverwritesSpec {
        /**
         * Flag which specifies whether an overwrite with lower visibility than
         * its target is allowed to be applied, the visibility will be upgraded
         * if the target method is nonprivate but the merged method is private.
         */
        @get:Input
        @get:Optional
        abstract val conformVisibility: Property<Boolean>

        /**
         * Changes the default always-overwrite behaviour of mixins to
         * explicitly require {@link Overwrite} annotations on overwrite methods
         */
        @get:Input
        @get:Optional
        abstract val requireAnnotations: Property<Boolean>
    }

    fun mixinExtras(action: Action<MixinExtrasSpec>) {
        val entry = objects.newInstance(MixinExtrasSpec::class.java).also { action.execute(it) }
        mixinExtras.set(entry)
    }

    abstract class MixinExtrasSpec {
        /**
         * Minimum Mixin Extras version required for this using [Expressions](https://github.com/LlamaLad7/MixinExtras/wiki/Expressions-Setup)
         */
        @get:Input
        @get:Optional
        abstract val minVersion: Property<String>
    }
}
