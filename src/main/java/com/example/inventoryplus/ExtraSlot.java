package com.example.inventoryplus;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

import java.util.function.BooleanSupplier;

public class ExtraSlot extends Slot {
    /** Indice del primer slot extra dentro de PlayerScreenHandler (0-45 son los de vanilla). */
    public static final int FIRST_INDEX = 46;

    /** El cliente lo cambia para que estos slots solo se vean en la pantalla de inventario de supervivencia. */
    public static BooleanSupplier visible = () -> true;

    public ExtraSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isEnabled() {
        return visible.getAsBoolean();
    }
}
