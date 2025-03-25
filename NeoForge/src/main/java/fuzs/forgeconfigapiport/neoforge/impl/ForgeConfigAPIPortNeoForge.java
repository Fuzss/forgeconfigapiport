package fuzs.forgeconfigapiport.neoforge.impl;

import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import fuzs.forgeconfigapiport.impl.services.CommonAbstractions;
import fuzs.forgeconfigapiport.neoforge.api.forge.v4.ForgeConfigRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod(ForgeConfigAPIPort.MOD_ID)
public class ForgeConfigAPIPortNeoForge {

    public ForgeConfigAPIPortNeoForge(ModContainer modContainer) {
        if (CommonAbstractions.INSTANCE.includeTestConfigs()) {
            modContainer.registerConfig(ModConfig.Type.SERVER, new ModConfigSpec.Builder().comment("hello world")
                    .define("dummy_entry", true)
                    .next()
                    .build(), "forgeconfigapiport-server-neoforge.toml");
            ForgeConfigRegistry.INSTANCE.register(modContainer.getModId(), ModConfig.Type.SERVER,
                    new ForgeConfigSpec.Builder().translation("dummy_entry").comment("hello world").define(
                            "dummy_entry", true).next().build(), "forgeconfigapiport-server-forge.toml"
            );
        }
    }
}
