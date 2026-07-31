package io.github.smokahs.bqn.mixin;

import net.minecraft.client.gui.GuiGraphics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import io.github.smokahs.bqn.client.QuestNotification;

/**
 * A quest icon can be an item nested inside any number of wrappers - padding, border, animation,
 * lazy, combined. Rather than unwrap them, catch the item at the point it actually draws, but only
 * while we're rendering a popup.
 */
// remap = false: draw() belongs to FTB Library, not Minecraft, so there's no SRG name to look up.
@Mixin(value = ItemIcon.class, remap = false)
public class ItemIconMixin {
    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    private void bqn$fadeWithPopup(GuiGraphics graphics, int x, int y, int w, int h, CallbackInfo ci) {
        if (QuestNotification.isFading()) {
            QuestNotification.drawFadedItem(graphics, ((ItemIcon) (Object) this).getStack(), x, y, w, h,
                    QuestNotification.fadeAlpha());
            ci.cancel();
        }
    }
}
