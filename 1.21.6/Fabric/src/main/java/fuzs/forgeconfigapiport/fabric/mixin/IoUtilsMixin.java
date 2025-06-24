package fuzs.forgeconfigapiport.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(targets = "com.electronwill.nightconfig.core.io.IoUtils", remap = false)
@Pseudo
abstract class IoUtilsMixin {

    @Inject(method = "tempConfigFileName", at = @At("HEAD"), cancellable = true, require = 0)
    private static void tempConfigFileName(Path originalFile, CallbackInfoReturnable<String> callback) {
        callback.setReturnValue(originalFile.getFileName() + ".new.tmp");
    }
}
