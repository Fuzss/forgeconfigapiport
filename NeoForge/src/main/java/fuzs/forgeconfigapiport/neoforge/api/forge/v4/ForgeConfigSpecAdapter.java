package fuzs.forgeconfigapiport.neoforge.api.forge.v4;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.utils.UnmodifiableConfigWrapper;
import net.minecraftforge.fml.config.IConfigSpec;

/**
 * A bridge for NeoForge's and Forge's config specs. Used in the implementation of {@link ForgeConfigRegistry}.
 * <p>Exposed in the api to allow for further customization during config registration if necessary.
 */
public final class ForgeConfigSpecAdapter extends UnmodifiableConfigWrapper<IConfigSpec<?>> implements net.neoforged.fml.config.IConfigSpec<ForgeConfigSpecAdapter> {

    public ForgeConfigSpecAdapter(IConfigSpec<?> config) {
        super(config);
    }

    @Override
    public void acceptConfig(CommentedConfig data) {
        this.config.acceptConfig(data);
    }

    @Override
    public boolean isCorrecting() {
        return this.config.isCorrecting();
    }

    @Override
    public boolean isCorrect(CommentedConfig commentedFileConfig) {
        return this.config.isCorrect(commentedFileConfig);
    }

    @Override
    public int correct(CommentedConfig commentedFileConfig) {
        return this.config.correct(commentedFileConfig);
    }

    @Override
    public void afterReload() {
        this.config.afterReload();
    }
}
