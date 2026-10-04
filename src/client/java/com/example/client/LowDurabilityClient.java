package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
            ItemStack stack = client.player.getMainHandItem();
            if (stack.isEmpty() || !stack.isDamageableItem()) {
                warnedItem = null;
                return;
            }
            int max = stack.getMaxDamage();
            int remaining = max - stack.getDamageValue();
            boolean low = remaining <= Math.max(1, (int) (max * THRESHOLD));
            if (!low) {
                warnedItem = null;
                return;
            }
            if (warnedItem != stack.getItem()) {
                warnedItem = stack.getItem();
                client.getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.ANVIL_LAND, 1.5f));
                client.gui.setOverlayMessage(
                    Component.literal("Low durability! " + remaining + " left"), false);
            }
        });
    }
}
