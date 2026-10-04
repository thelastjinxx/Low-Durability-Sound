package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class LowDurabilityClient implements ClientModInitializer {
    private static final float THRESHOLD = 0.10f;
    private Item warnedItem = null;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                warnedItem = null;
                return;
            }
            ItemStack stack = client.player.getMainHandStack();
            if (stack.isEmpty() || !stack.isDamageable()) {
                warnedItem = null;
                return;
            }
            int max = stack.getMaxDamage();
            int remaining = max - stack.getDamage();
            boolean low = remaining <= Math.max(1, (int) (max * THRESHOLD));
            if (!low) {
                warnedItem = null;
                return;
            }
            if (warnedItem != stack.getItem()) {
                warnedItem = stack.getItem();
                client.getSoundManager().play(
                    PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.5f));
                client.inGameHud.setOverlayMessage(
                    Text.literal("Low durability! " + remaining + " left"), false);
            }
        });
    }
}
