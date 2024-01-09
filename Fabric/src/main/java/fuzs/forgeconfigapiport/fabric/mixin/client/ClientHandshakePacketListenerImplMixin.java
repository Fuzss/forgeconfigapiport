package fuzs.forgeconfigapiport.fabric.mixin.client;

import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import fuzs.forgeconfigapiport.impl.network.client.NetworkHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHandshakePacketListenerImpl.class)
abstract class ClientHandshakePacketListenerImplMixin {
    @Shadow
    @Final
    private Connection connection;

    @Inject(method = "handleGameProfile", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;)V", shift = At.Shift.AFTER, ordinal = 0))
    public void handleGameProfile(ClientboundGameProfilePacket clientboundGameProfilePacket, CallbackInfo callbackInfo) {
        NetworkHooks.handleClientLoginSuccess(this.connection);
    }
}
