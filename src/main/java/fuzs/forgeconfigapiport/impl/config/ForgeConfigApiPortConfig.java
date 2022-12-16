package fuzs.forgeconfigapiport.impl.config;

import com.google.common.collect.Lists;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ForgeConfigApiPortConfig {
    @Nullable
    private static ForgeConfigApiPortConfig instance;

    private final List<ConfigData<?>> data = Lists.newArrayList();
    public String defaultsConfigPath = "defaultconfigs";
    public boolean disableGlobalServerConfigs = true;
    public boolean disableConfigCommand = false;

    private ForgeConfigSpec buildSpec() {
        if (!this.data.isEmpty()) throw new IllegalStateException("found config data before config was built");
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        this.accept(builder.comment("Path to load default configs from, intended for setting global server configs for newly created worlds, but also works when recreating client and common configs.").define("Default Configs Path", this.defaultsConfigPath), v -> this.defaultsConfigPath = v);
        this.accept(builder.comment("Prevent server config files from being saved inside of the current world directory. Instead, save them to the global config directory in .minecraft/config/. This option effectively disables per world server configs, but helps a lot with avoiding user confusion.").define("Disable Global Server Configs", this.disableGlobalServerConfigs), v -> this.disableGlobalServerConfigs = v);
        this.accept(builder.comment("Disable the custom '/config' command introduced by Forge for opening config files from in-game. Disabling this option also disables registration of custom command argument serializers. This option mainly exists so that Forge Config Api Port may be present when hosting a LAN world, so that vanilla clients may connect. Changing this option requires a game restart.").define("Disable Config Command", this.disableConfigCommand), v -> this.disableConfigCommand = v);
        return builder.build();
    }

    private <T> void accept(ForgeConfigSpec.ConfigValue<T> value, Consumer<T> consumer) {
        this.data.add(new ConfigData<>(value, consumer));
    }

    private void sync() {
        this.data.forEach(ConfigData::sync);
    }

    public synchronized static ForgeConfigApiPortConfig getInstance() {
        if (instance == null) {
            ForgeConfigApiPortConfig config = new ForgeConfigApiPortConfig();
            // really not a good idea on Forge doing this on demand (especially as this is accessed by api code),
            // but shouldn't cause any issues on Fabric as registering configs is possible at any point basically (during mod loading at least)
            ForgeConfigRegistry.INSTANCE.register(ForgeConfigAPIPort.MOD_ID, ModConfig.Type.COMMON, config.buildSpec());
            ModConfigEvents.loading(ForgeConfigAPIPort.MOD_ID).register(config1 -> config.sync());
            ModConfigEvents.reloading(ForgeConfigAPIPort.MOD_ID).register(config1 -> config.sync());
            ModConfigEvents.reloading("").register(config1 -> {

            });
            instance = config;
        }
        return instance;
    }

    private record ConfigData<T>(ForgeConfigSpec.ConfigValue<T> value, Consumer<T> consumer) {

        public void sync() {
            this.consumer.accept(this.value.get());
        }
    }
}
