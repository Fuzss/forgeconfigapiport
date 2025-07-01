/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package fuzs.forgeconfigapiport.fabric.impl.network.payload;

import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

/**
 * A payload that contains a config file.
 * <p>
 * This is used to send config files to the client.
 * </p>
 *
 * @param fileName The name of the config file.
 * @param contents The contents of the config file.
 */
@ApiStatus.Internal
public record ConfigFilePayload(String fileName, byte[] contents) implements CustomPacketPayload {
    // Forge Config API Port: copied here from NeoForgeStreamCodecs
    public static final StreamCodec<FriendlyByteBuf, byte[]> UNBOUNDED_BYTE_ARRAY = new StreamCodec<>() {
        public byte[] decode(FriendlyByteBuf buf) {
            return buf.readByteArray();
        }

        public void encode(FriendlyByteBuf buf, byte[] data) {
            buf.writeByteArray(data);
        }
    };
    // Forge Config API Port: adjust mod id
    public static final Type<ConfigFilePayload> TYPE = new Type<>(ForgeConfigAPIPort.id("config_file"));
    public static final StreamCodec<FriendlyByteBuf, ConfigFilePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ConfigFilePayload::fileName,
            UNBOUNDED_BYTE_ARRAY,
            ConfigFilePayload::contents,
            ConfigFilePayload::new);

    @Override
    public Type<ConfigFilePayload> type() {
        return TYPE;
    }
}
