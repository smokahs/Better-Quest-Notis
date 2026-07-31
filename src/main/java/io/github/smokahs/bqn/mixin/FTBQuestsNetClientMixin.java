package io.github.smokahs.bqn.mixin;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.ftb.mods.ftbquests.client.FTBQuestsNetClient;
import dev.ftb.mods.ftbquests.client.gui.ToastQuestObject;
import io.github.smokahs.bqn.client.QuestNotification;
import io.github.smokahs.bqn.config.BQNConfig;

/**
 * FTB Quests turns a completion packet straight into a corner toast. We take that toast and turn it
 * into a Better Questing style popup instead; everything else the method does (refreshing an open
 * quest book) is left alone.
 */
@Mixin(FTBQuestsNetClient.class)
public class FTBQuestsNetClientMixin {
    @Redirect(
            method = "displayCompletionToast(J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/toasts/ToastComponent;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"))
    private static void bqn$replaceCompletionToast(ToastComponent toasts, Toast toast) {
        if (toast instanceof ToastQuestObject questToast && BQNConfig.ENABLED.get()) {
            QuestNotification.enqueue(questToast);
            if (!BQNConfig.KEEP_FTB_TOAST.get()) {
                return;
            }
        }
        toasts.addToast(toast);
    }
}
