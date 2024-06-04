/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package fuzs.forgeconfigapiport.fabric.impl.network.configuration;

import fuzs.forgeconfigapiport.fabric.impl.network.ConfigSync;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * Configuration task that syncs the config files to the client
 *
 * @param listener the listener to indicate to that the task is complete
 */
@ApiStatus.Internal
public record SyncConfig(ServerConfigurationPacketListenerImpl listener) implements ConfigurationTask {
    public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(ForgeConfigAPIPort.id("sync_config").toString());

    @Override
    public void start(Consumer<Packet<?>> task) {
        // Forge Config API Port: adapt method for Fabric
        ConfigSync.syncConfigs().forEach(configFilePayload -> {
            task.accept(ServerConfigurationNetworking.createS2CPacket(configFilePayload));
        });
        this.listener().finishCurrentTask(this.type());
    }

    @Override
    public ConfigurationTask.Type type() {
        return TYPE;
    }
}
