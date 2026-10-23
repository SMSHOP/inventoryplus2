package com.example.inventoryplus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import java.util.ArrayList;
import java.util.List;

public class InventoryPlus implements ModInitializer {
    public static final String MOD_ID = "inventoryplus";

    /** Los 27 slots extra del jugador, guardados con el jugador (persisten y sobreviven al morir si keepInventory). */
    public static final AttachmentType<List<ItemStack>> EXTRA = AttachmentRegistry.<List<ItemStack>>builder()
            .persistent(ExtraInventory.CODEC)
            .initializer(ExtraInventory::createEmpty)
            .copyOnDeath()
            .buildAndRegister(Identifier.of(MOD_ID, "extra_slots"));

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(SortPayload.ID, SortPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SortPayload.ID, (payload, context) -> {
            if (context.player().currentScreenHandler instanceof PlayerScreenHandler handler
                    && handler.slots.size() >= ExtraSlot.FIRST_INDEX + ExtraInventory.SIZE) {
                List<Slot> slots = new ArrayList<>();
                for (int i = 9; i < 36; i++) slots.add(handler.slots.get(i)); // inventario principal
                for (int i = 0; i < ExtraInventory.SIZE; i++) slots.add(handler.slots.get(ExtraSlot.FIRST_INDEX + i)); // extra
                InventorySorter.sort(slots);
                handler.sendContentUpdates();
            }
        });

        // Al morir (sin keepInventory) los slots extra tambien sueltan sus items
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player
                    && !player.getServerWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
                List<ItemStack> list = player.getAttachedOrCreate(EXTRA);
                for (int i = 0; i < list.size(); i++) {
                    ItemStack stack = list.get(i);
                    if (!stack.isEmpty()) {
                        player.dropItem(stack, true, false);
                        list.set(i, ItemStack.EMPTY);
                    }
                }
            }
            return true;
        });
    }
}
