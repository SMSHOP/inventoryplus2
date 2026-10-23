package com.example.inventoryplus;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class InventorySorter {
    private InventorySorter() {}

    /** Junta stacks iguales y los reparte ordenados por id de item en los slots dados. */
    public static void sort(List<Slot> slots) {
        List<ItemStack> stacks = new ArrayList<>();

        for (Slot slot : slots) {
            ItemStack s = slot.getStack().copy();
            if (s.isEmpty()) continue;

            for (ItemStack other : stacks) {
                if (s.isEmpty()) break;
                if (ItemStack.areItemsAndComponentsEqual(other, s) && other.getCount() < other.getMaxCount()) {
                    int move = Math.min(s.getCount(), other.getMaxCount() - other.getCount());
                    other.increment(move);
                    s.decrement(move);
                }
            }
            if (!s.isEmpty()) stacks.add(s);
        }

        stacks.sort(Comparator
                .comparing((ItemStack s) -> Registries.ITEM.getId(s.getItem()).toString())
                .thenComparing(s -> -s.getCount()));

        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).setStack(i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY);
        }
    }
}
