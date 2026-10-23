package com.example.inventoryplus.mixin;

import com.example.inventoryplus.ExtraInventory;
import com.example.inventoryplus.ExtraSlot;
import com.example.inventoryplus.SortPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends HandledScreen<PlayerScreenHandler> {
    @Unique private static final int PANEL_TOP = 166; // justo debajo del inventario vanilla
    @Unique private static final int PANEL_HEIGHT = 79;

    @Unique private ButtonWidget inventoryplus$sortButton;
    @Unique private long inventoryplus$animStart = System.currentTimeMillis();

    protected InventoryScreenMixin(PlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void inventoryplus$init(CallbackInfo ci) {
        inventoryplus$animStart = System.currentTimeMillis();
        inventoryplus$sortButton = ButtonWidget.builder(Text.translatable("gui.inventoryplus.sort"), b -> {
                    ClientPlayNetworking.send(new SortPayload());
                    inventoryplus$animStart = System.currentTimeMillis(); // repite la animacion al ordenar
                })
                .dimensions(this.x + 176 - 52, this.y + PANEL_TOP + 3, 46, 12)
                .build();
        this.addDrawableChild(inventoryplus$sortButton);
    }

    /** Dibuja el panel de slots extra con el mismo estilo que el inventario vanilla. */
    @Inject(method = "drawBackground", at = @At("TAIL"))
    private void inventoryplus$drawPanel(DrawContext ctx, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        int px = this.x;
        int py = this.y + PANEL_TOP;

        ctx.fill(px, py, px + 176, py + PANEL_HEIGHT, 0xFF000000);
        ctx.fill(px + 1, py, px + 175, py + PANEL_HEIGHT - 1, 0xFFC6C6C6);
        ctx.fill(px + 1, py, px + 175, py + 2, 0xFFFFFFFF);
        ctx.fill(px + 1, py, px + 3, py + PANEL_HEIGHT - 1, 0xFFFFFFFF);
        ctx.fill(px + 3, py + PANEL_HEIGHT - 4, px + 175, py + PANEL_HEIGHT - 1, 0xFF555555);
        ctx.fill(px + 173, py + 3, px + 175, py + PANEL_HEIGHT - 1, 0xFF555555);

        ctx.drawText(this.textRenderer, Text.translatable("gui.inventoryplus.extra"), px + 8, py + 6, 0x404040, false);

        for (int i = 0; i < ExtraInventory.SIZE; i++) {
            int sx = px + 8 + (i % 9) * 18;
            int sy = py + 18 + (i / 9) * 18;
            ctx.fill(sx, sy, sx + 17, sy + 17, 0xFFFFFFFF);
            ctx.fill(sx - 1, sy - 1, sx + 16, sy + 16, 0xFF373737);
            ctx.fill(sx, sy, sx + 16, sy + 16, 0xFF8B8B8B);
        }
    }

    /** El boton debe seguir al panel cuando se abre/cierra el recetario (la ventana se desplaza). */
    @Inject(method = "render", at = @At("HEAD"))
    private void inventoryplus$renderHead(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (inventoryplus$sortButton != null) {
            inventoryplus$sortButton.setPosition(this.x + 176 - 52, this.y + PANEL_TOP + 3);
        }
    }

    /** Animacion: los slots aparecen en ola (inventario principal y luego el extra). */
    @Inject(method = "render", at = @At("TAIL"))
    private void inventoryplus$renderWave(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        long t = System.currentTimeMillis() - inventoryplus$animStart;
        if (t > 1500) return;

        for (int i = 0; i < 27 + ExtraInventory.SIZE; i++) {
            int slotIndex = i < 27 ? 9 + i : ExtraSlot.FIRST_INDEX + (i - 27);
            if (slotIndex >= this.handler.slots.size()) break;
            Slot slot = this.handler.slots.get(slotIndex);

            float p = MathHelper.clamp((t - i * 12f) / 250f, 0f, 1f);
            if (p >= 1f) continue;
            p = p * p * (3f - 2f * p); // smoothstep
            int alpha = (int) ((1f - p) * 255f);

            int sx = this.x + slot.x;
            int sy = this.y + slot.y;
            ctx.fill(sx, sy, sx + 16, sy + 16, (alpha << 24) | 0x8B8B8B);
        }
    }

    /** Sin esto, hacer clic en un slot extra (fuera del fondo vanilla) tiraria el item al piso. */
    @Inject(method = "isClickOutsideBounds", at = @At("HEAD"), cancellable = true)
    private void inventoryplus$bounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
        if (mouseX >= left && mouseX < left + 176 && mouseY >= top + PANEL_TOP && mouseY < top + PANEL_TOP + PANEL_HEIGHT) {
            cir.setReturnValue(false);
        }
    }
}
