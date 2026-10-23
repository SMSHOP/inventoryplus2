package com.example.inventoryplus;

import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Inventario de 27 slots que lee/escribe directamente el attachment del jugador (se carga de forma "lazy"). */
public class ExtraInventory implements Inventory {
    public static final int SIZE = 27;

    public static final Codec<List<ItemStack>> CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(
            stacks -> {
                List<ItemStack> list = new ArrayList<>(stacks);
                while (list.size() < SIZE) list.add(ItemStack.EMPTY);
                return list;
            },
            stacks -> stacks);

    public static List<ItemStack> createEmpty() {
        return new ArrayList<>(Collections.nCopies(SIZE, ItemStack.EMPTY));
    }

    private final PlayerEntity player;

    public ExtraInventory(PlayerEntity player) {
        this.player = player;
    }

    private List<ItemStack> list() {
        return player.getAttachedOrCreate(InventoryPlus.EXTRA);
    }

    @Override public int size() { return SIZE; }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : list()) if (!s.isEmpty()) return false;
        return true;
    }

    @Override public ItemStack getStack(int slot) { return list().get(slot); }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(list(), slot, amount);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override public ItemStack removeStack(int slot) { return Inventories.removeStack(list(), slot); }

    @Override
    public void setStack(int slot, ItemStack stack) {
        list().set(slot, stack);
        markDirty();
    }

    @Override public void markDirty() {}
    @Override public boolean canPlayerUse(PlayerEntity player) { return true; }
    @Override public void clear() { Collections.fill(list(), ItemStack.EMPTY); }
}
