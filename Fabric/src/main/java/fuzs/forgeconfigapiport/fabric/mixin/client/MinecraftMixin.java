package fuzs.forgeconfigapiport.fabric.mixin.client;

import fuzs.forgeconfigapiport.fabric.impl.network.config.ConfigSync;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public ClientLevel level;

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;isLocalServer:Z", shift = At.Shift.AFTER))
    public void disconnect(Screen screen, CallbackInfo callback) {
        ConfigSync.unloadSyncedConfig();
    }
}
