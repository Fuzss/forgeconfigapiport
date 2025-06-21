package fuzs.forgeconfigapiport.fabric.mixin;

import com.electronwill.nightconfig.core.concurrent.StampedConfig;
import com.electronwill.nightconfig.core.utils.ConcurrentCommentedConfigWrapper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.file.Path;

@Mixin(targets = "com.electronwill.nightconfig.core.file.AsyncFileConfig", remap = false)
abstract class AsyncFileConfigMixin extends ConcurrentCommentedConfigWrapper<StampedConfig> {
    @Shadow
    @Final
    private Path nioPath;

    protected AsyncFileConfigMixin(StampedConfig config) {
        super(config);
    }

    @ModifyExpressionValue(
            method = "saveNow", at = @At(
            value = "INVOKE",
            target = "Lcom/electronwill/nightconfig/core/io/IoUtils;tempConfigFileName(Ljava/nio/file/Path;)Ljava/lang/String;"
    ), require = 0
    )
    private String saveNow(String tmpFileName) {
        return this.nioPath.getFileName().toString() + ".new.tmp";
    }
}
