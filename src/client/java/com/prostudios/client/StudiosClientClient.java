package com.prostudios.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.prostudios.client.config.ClientConfig;
import com.prostudios.client.gui.StudiosMenuScreen;
import com.prostudios.client.util.StudioFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class StudiosClientClient implements ClientModInitializer {
	public static final String MOD_ID = "studios-client";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	// Theme
	public static final int NAVY = 0xFF0B1120;
	public static final int NAVY_SOFT = 0xAA0B1120;
	public static final int PANEL = 0xFFF8FAFC;
	public static final int AMBER = 0xFFF59E0B;
	public static final int AMBER_DIM = 0x55F59E0B;
	public static final int WHITE = 0xFFE2E8F0;
	public static final int GRAY = 0xFF64748B;

	public static ClientConfig CONFIG;
	public static KeyMapping openMenuKey;
	public static KeyMapping zoomKey;
	private static boolean appliedStartupFeatures = false;

	@Override
	public void onInitializeClient() {
		CONFIG = ClientConfig.load();

		openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.studios_client.open_menu",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_RIGHT_SHIFT,
				"key.categories.studios_client"));
		zoomKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.studios_client.zoom",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_C,
				"key.categories.studios_client"));

		ClientTickEvents.END_CLIENT_TICK.register(mc -> {
			while (openMenuKey.consumeClick()) {
				mc.setScreen(new StudiosMenuScreen(null));
			}
			StudioFeatures.tick(mc);
			if (!appliedStartupFeatures) {
				appliedStartupFeatures = true;
				StudioFeatures.applyFullbright(mc, CONFIG.isEnabled("fullbright"));
			}
		});

		HudElementRegistry.addLast(ResourceLocation.fromNamespaceAndPath(MOD_ID, "main_hud"),
				(gg, tickCounter) -> com.prostudios.client.hud.HudOverlay.render(gg));
	}

	public static void saveConfig() {
		if (CONFIG != null) CONFIG.save();
	}
}
