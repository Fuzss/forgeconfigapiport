package fuzs.multiloader.extension

import fuzs.multiloader.mixin.MixinConfigJsonSpec
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import javax.inject.Inject

abstract class MultiLoaderExtension {
    @get:Inject
    abstract val objects: ObjectFactory

    @get:Input
    @get:Optional
    abstract val modFile: Property<ModFileMetadataExtension>

    @get:Input
    @get:Optional
    abstract val mixins: Property<Action<MixinConfigJsonSpec>>

    fun modFile(configure: Action<ModFileMetadataExtension>) {
        objects.newInstance(ModFileMetadataExtension::class.java).also {
            modFile.set(it)
            configure.execute(it)
        }
    }

    fun mixins(configure: MixinConfigJsonSpec.() -> Unit) {
        mixins.set(Action(configure))
    }
}
