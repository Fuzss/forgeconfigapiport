package net.minecraftforge.mixin.client;

import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraftforge.network.client.NetworkHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHandshakePacketListenerImpl.class)
public abstract class ClientHandshakePacketListenerImplMixin {
    @Shadow
    @Final
    private Connection connection;

    @Inject(method = "handleGameProfile", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setProtocol(Lnet/minecraft/network/ConnectionProtocol;)V", shift = At.Shift.AFTER))
    public void handleGameProfile$inject$invoke(ClientboundGameProfilePacket clientboundGameProfilePacket, CallbackInfo callbackInfo) {
        NetworkHooks.handleClientLoginSuccess(this.connection);
    }
}
