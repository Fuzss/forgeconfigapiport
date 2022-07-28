package net.minecraftforge.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.network.config.ConfigSync;

public class ForgeHooksClient {

    public static void handleClientLevelClosing(ClientLevel level)
    {
        ConfigSync.INSTANCE.unloadSyncedConfig();
    }
}
