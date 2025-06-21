package fuzs.forgeconfigapiport.fabric.mixin;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.charset.Charset;
import java.nio.file.Path;

@Mixin(value = ConfigWriter.class, remap = false)
public interface ConfigWriterMixin {

    @ModifyExpressionValue(
            method = "write(Lcom/electronwill/nightconfig/core/UnmodifiableConfig;Ljava/nio/file/Path;Lcom/electronwill/nightconfig/core/io/WritingMode;Ljava/nio/charset/Charset;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/electronwill/nightconfig/core/io/IoUtils;tempConfigFileName(Ljava/nio/file/Path;)Ljava/lang/String;"
            ),
            require = 0
    )
    default String write(String tmpFileName, UnmodifiableConfig config, Path file, WritingMode writingMode, Charset charset) {
        return file.getFileName().toString() + ".new.tmp";
    }
}
