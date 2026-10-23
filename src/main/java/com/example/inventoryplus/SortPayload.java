package com.example.inventoryplus;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SortPayload() implements CustomPayload {
    public static final Id<SortPayload> ID = new Id<>(Identifier.of(InventoryPlus.MOD_ID, "sort"));
    public static final PacketCodec<RegistryByteBuf, SortPayload> CODEC = PacketCodec.unit(new SortPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
