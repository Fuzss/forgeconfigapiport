package fuzs.forgeconfigapiport.neoforge.impl.client;

import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import fuzs.forgeconfigapiport.impl.services.CommonAbstractions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ForgeConfigAPIPort.MOD_ID, dist = Dist.CLIENT)
public class ForgeConfigAPIPortNeoForgeClient {

    public ForgeConfigAPIPortNeoForgeClient(ModContainer modContainer) {
        if (CommonAbstractions.INSTANCE.includeTestConfigs()) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }
}
