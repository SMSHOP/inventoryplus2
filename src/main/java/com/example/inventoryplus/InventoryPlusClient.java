package com.example.inventoryplus;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class InventoryPlusClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Evita que los slots extra aparezcan (por error) en la pestana de inventario del modo creativo
        ExtraSlot.visible = () -> MinecraftClient.getInstance().currentScreen instanceof InventoryScreen;
    }
}
