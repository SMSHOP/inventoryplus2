package com.example.inventoryplus.mixin;

import com.example.inventoryplus.ExtraInventory;
import com.example.inventoryplus.ExtraSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Agrega 27 slots (3 filas) al inventario normal del jugador, tanto en cliente como en servidor. */
@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends ScreenHandler {
    protected PlayerScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void inventoryplus$addExtraSlots(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        ExtraInventory extra = new ExtraInventory(owner);
        for (int i = 0; i < ExtraInventory.SIZE; i++) {
            this.addSlot(new ExtraSlot(extra, i, 8 + (i % 9) * 18, 184 + (i / 9) * 18));
        }
    }
}
