package net.minecraftforge.modmenu;

import com.google.common.collect.ImmutableMap;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.config.widgets.ConfigGuiScreen;
import net.minecraftforge.client.gui.config.widgets.SpecificationData;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Forge Config API Port: mod menu integration for config screens, some similarities to Forge's ModList class
public class ConfigFactoryHandler implements ModMenuApi {

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        // this will provide a default config screen for any mod using Forge Config API Port that doesn't provide its own screen
        Minecraft minecraft = Minecraft.getInstance();
        ImmutableMap.Builder<String, ConfigScreenFactory<?>> builder = ImmutableMap.builder();
        Map<String, List<ModConfig>> modIdToModConfigs = ConfigTracker.INSTANCE.configSets().values().stream()
                .flatMap(Collection::stream)
                .filter(config -> config.getSpec() instanceof ForgeConfigSpec spec && spec.isVisibleOnModConfigScreen())
                // from us: don't show empty screens coming from main menu when there's just a server config
                .filter(config -> minecraft.level != null || config.getType() != ModConfig.Type.SERVER)
                .collect(Collectors.groupingBy(ModConfig::getModId));
        modIdToModConfigs.forEach((key, value) -> builder.put(key, parent -> new ConfigGuiScreen(
                minecraft.level != null ?
                        Component.translatable("forge.configgui.title.runtimeConfigs", this.getDisplayName(key)) :
                        Component.translatable("forge.configgui.title.defaultConfigs", this.getDisplayName(key)),
                value.stream()
                        .map(entry -> new SpecificationData((ForgeConfigSpec) entry.getSpec(), !minecraft.isLocalServer() && entry.getType() == ModConfig.Type.SERVER))
                        .collect(Collectors.toList()),
                () -> minecraft.setScreen(parent))));
        return builder.build();
    }

    private String getDisplayName(String selectedMod) {
        return FabricLoader.getInstance().getModContainer(selectedMod)
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getName)
                .orElse(selectedMod);
    }
}
