package fuzs.forgeconfigapiport.mixin.accessor;

import net.minecraft.world.level.storage.LevelResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelResource.class)
public interface LevelResourceAccessor {

    @Invoker("<init>")
    static LevelResource forgeconfigapiport$create(String string) {
        throw new IllegalStateException();
    }
}
