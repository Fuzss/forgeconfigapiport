package fuzs.forgeconfigapiport.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import fuzs.forgeconfigapiport.impl.network.config.ConfigSync;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public ClientLevel level;

    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Game;onLeaveGameSession()V"))
    public void forgeconfigapiport$clearLevel(Screen screen, CallbackInfo callback) {
        ConfigSync.INSTANCE.unloadSyncedConfig();
    }
}
