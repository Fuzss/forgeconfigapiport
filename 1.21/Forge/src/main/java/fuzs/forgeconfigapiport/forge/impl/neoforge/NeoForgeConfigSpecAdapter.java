package fuzs.forgeconfigapiport.forge.impl.neoforge;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.core.utils.UnmodifiableConfigWrapper;
import com.electronwill.nightconfig.toml.TomlWriter;
import fuzs.forgeconfigapiport.forge.api.neoforge.v4.NeoForgeConfigRegistry;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A bridge for NeoForge's and Forge's config specs. Used in the implementation of {@link NeoForgeConfigRegistry}.
 */
public final class NeoForgeConfigSpecAdapter extends UnmodifiableConfigWrapper<UnmodifiableConfig> implements net.minecraftforge.fml.config.IConfigSpec<NeoForgeConfigSpecAdapter> {
    private static final Map<String, ReentrantLock> LOCKS_BY_MOD = new ConcurrentHashMap<>();

    private final ModConfigSpec spec;
    private final ReentrantLock lock;
    
    public NeoForgeConfigSpecAdapter(String modId, ModConfigSpec spec) {
        super(spec.getSpec());
        this.spec = spec;
        this.lock = LOCKS_BY_MOD.computeIfAbsent(modId, $ -> new ReentrantLock());
    }

    @Override
    public void acceptConfig(@Nullable CommentedConfig data) {
        this.lock.lock();
        this.spec.acceptConfig(data != null ? new IConfigSpec.ILoadedConfig() {

            @Override
            public CommentedConfig config() {
                return data;
            }

            @Override
            public void save() {
                if (data instanceof FileConfig fileConfig) {
                    writeConfig(fileConfig.getNioPath(), data);
                }
            }

            static void writeConfig(Path file, UnmodifiableCommentedConfig config) {
                new TomlWriter().write(config, file, WritingMode.REPLACE_ATOMIC);
            }
        } : null);
        this.lock.unlock();
    }

    @Override
    public boolean isCorrecting() {
        return this.lock.isLocked();
    }

    @Override
    public boolean isCorrect(CommentedConfig commentedFileConfig) {
        return this.spec.isCorrect(commentedFileConfig);
    }

    @Override
    public int correct(CommentedConfig commentedFileConfig) {
        this.lock.lock();
        this.spec.correct(commentedFileConfig);
        this.lock.unlock();
        // return value is never used
        return 0;
    }

    @Override
    public void afterReload() {
        this.spec.afterReload();
    }
}
