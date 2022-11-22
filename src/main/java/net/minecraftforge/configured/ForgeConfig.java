package net.minecraftforge.configured;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mrcrayfish.configured.Configured;
import com.mrcrayfish.configured.api.ConfigType;
import com.mrcrayfish.configured.api.IConfigEntry;
import com.mrcrayfish.configured.api.IConfigValue;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.util.ConfigHelper;
import net.minecraft.Util;
import net.minecraftforge.api.fml.event.config.ModConfigEvents;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class ForgeConfig implements IModConfig
{
    protected static final EnumMap<ModConfig.Type, ConfigType> TYPE_RESOLVER = Util.make(new EnumMap<>(ModConfig.Type.class), (map) -> {
        map.put(ModConfig.Type.CLIENT, ConfigType.CLIENT);
        map.put(ModConfig.Type.COMMON, ConfigType.UNIVERSAL);
        map.put(ModConfig.Type.SERVER, ConfigType.WORLD_SYNC);
    });

    protected final ModConfig config;
    protected final List<ForgeValueEntry> allConfigValues;

    public ForgeConfig(ModConfig config)
    {
        this.config = config;
        this.allConfigValues = getAllConfigValues(config);
    }

    protected ForgeConfig(ModConfig config, List<ForgeValueEntry> allConfigValues)
    {
        this.config = config;
        this.allConfigValues = allConfigValues;
    }

    @Override
    public void update(IConfigEntry entry)
    {
        Set<IConfigValue<?>> changedValues = ConfigHelper.getChangedValues(entry);
        if(!changedValues.isEmpty())
        {
            CommentedConfig newConfig = CommentedConfig.copy(this.config.getConfigData());
            changedValues.forEach(value ->
            {
                if(value instanceof ForgeValue<?> forge)
                {
                    if(forge instanceof ForgeListValue forgeList)
                    {
                        Function<List<?>, List<?>> converter = forgeList.getConverter();
                        if(converter != null)
                        {
                            newConfig.set(forge.configValue.getPath(), converter.apply(forgeList.get()));
                            return;
                        }
                    }
                    newConfig.set(forge.configValue.getPath(), value.get());
                }
            });
            this.config.getConfigData().putAll(newConfig);
        }

        if(this.getType() == ConfigType.WORLD_SYNC)
        {
            if(!ConfigHelper.isPlayingGame())
            {
                // Unload server configs since still in main menu
                this.config.getHandler().unload(this.config.getFullPath().getParent(), this.config);
                ForgeConfigHelper.setForgeConfigData(this.config, null);
            }
        }
        else if(!changedValues.isEmpty())
        {
            Configured.LOGGER.info("Sending config reloading event for {}", this.config.getFileName());
            this.config.getSpec().afterReload();
            // Forge Config API Port: migrate to Fabric event style
            ModConfigEvents.reloading(this.getModId());
        }
    }

    @Override
    public IConfigEntry getRoot()
    {
        return new ForgeFolderEntry(((ForgeConfigSpec) this.config.getSpec()).getValues(), (ForgeConfigSpec) this.config.getSpec());
    }

    @Override
    public ConfigType getType()
    {
        return TYPE_RESOLVER.get(this.config.getType());
    }

    @Override
    public String getFileName()
    {
        return this.config.getFileName();
    }

    @Override
    public String getModId()
    {
        return this.config.getModId();
    }

    @Override
    public void loadWorldConfig(Path path, Consumer<IModConfig> result)
    {
        final CommentedFileConfig data = this.config.getHandler().reader(path).apply(this.config);
        ForgeConfigHelper.setForgeConfigData(this.config, data);
        result.accept(this);
    }

    @Override
    public void stopEditing()
    {
        // Attempts to unload the server config if player simply just went back
        if(this.config != null && this.getType() == ConfigType.WORLD)
        {
            if(!ConfigHelper.isPlayingGame())
            {
                // Unload server configs since still in main menu
                this.config.getHandler().unload(this.config.getFullPath().getParent(), this.config);
                ForgeConfigHelper.setForgeConfigData(this.config, null);
            }
        }
    }

    @Override
    public boolean isChanged()
    {
        // Block world configs since the path is dynamic
        if(ConfigHelper.isWorldConfig(this) && this.config.getConfigData() == null)
            return false;

        // Check if any config value doesn't equal it's default
        return this.allConfigValues.stream().anyMatch(entry -> {
            return !Objects.equals(entry.value.get(), entry.spec.getDefault());
        });
    }

    @Override
    public void restoreDefaults()
    {
        // Block world configs since the path is dynamic
        if(ConfigHelper.isWorldConfig(this) && this.config.getConfigData() == null)
            return;

        // Creates a copy of the config data then pushes all at once to avoid multiple IO ops
        CommentedConfig newConfig = CommentedConfig.copy(this.config.getConfigData());
        this.allConfigValues.forEach(entry -> newConfig.set(entry.value.getPath(), entry.spec.getDefault()));
        this.config.getConfigData().putAll(newConfig);

        // Finally clear cache of all config values
        this.allConfigValues.forEach(pair -> pair.value.clearCache());
    }

    protected List<ForgeValueEntry> getAllConfigValues(ModConfig config)
    {
        return ForgeConfigHelper.gatherAllForgeConfigValues(config).stream().map(pair -> new ForgeValueEntry(pair.getLeft(), pair.getRight())).toList();
    }

    protected record ForgeValueEntry(ForgeConfigSpec.ConfigValue<?> value, ForgeConfigSpec.ValueSpec spec) {}
}
