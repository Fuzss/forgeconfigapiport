package fuzs.forgeconfigapiport.neoforge.impl.forge;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import fuzs.forgeconfigapiport.neoforge.api.forge.v4.ForgeConfigRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

/**
 * A bridge for NeoForge's and Forge's config specs. Used in the implementation of {@link ForgeConfigRegistry}.
 */
public record ForgeConfigSpecAdapter(ForgeConfigSpec spec) implements net.neoforged.fml.config.IConfigSpec {

    @Override
    public boolean isEmpty() {
        return this.spec.isEmpty();
    }

    @Override
    public boolean isCorrect(UnmodifiableCommentedConfig config) {
        return this.spec.isCorrect((CommentedConfig) config);
    }

    @Override
    public void correct(CommentedConfig config) {
        this.spec.correct(config);
    }

    @Override
    public void acceptConfig(@Nullable net.neoforged.fml.config.IConfigSpec.ILoadedConfig config) {
        this.spec.acceptConfig(config != null ? config.config() : null);
    }
}
