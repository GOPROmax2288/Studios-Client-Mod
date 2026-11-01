package com.prostudios.client.hud;

import com.prostudios.client.StudiosClientClient;
import com.prostudios.client.module.ModuleDef;
import com.prostudios.client.util.CpsTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/**
 * Renders all HUD modules at their configured positions.
 * Each element owns a default position; actual positions come from config (editable via the HUD editor).
 */
public final class HudOverlay {
	public record Element(ModuleDef def, int defX, int defY) {}
	public static final Element[] ELEMENTS = {
			new Element(ModuleDef.FPS, 4, 4),
			new Element(ModuleDef.CPS, 4, 26),
			new Element(ModuleDef.KEYSTROKES, 4, 48),
			new Element(ModuleDef.COORDS, 4, 160),
			new Element(ModuleDef.PING, 4, 182),
	};

	private HudOverlay() {}

	public static int[] pos(Element e) {
		return StudiosClientClient.CONFIG.pos(e.def.id(), e.defX(), e.defY());
	}

	public static void render(GuiGraphics g) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.options.hideGui) return;
		Font font = mc.font;

		if (StudiosClientClient.CONFIG.isEnabled(ModuleDef.FPS.id())) renderFps(g, font, mc);
		if (StudiosClientClient.CONFIG.isEnabled(ModuleDef.CPS.id())) renderCps(g, font);
		if (StudiosClientClient.CONFIG.isEnabled(ModuleDef.KEYSTROKES.id())) renderKeystrokes(g, font, mc);
		if (StudiosClientClient.CONFIG.isEnabled(ModuleDef.COORDS.id())) renderCoords(g, font, mc);
		if (StudiosClientClient.CONFIG.isEnabled(ModuleDef.PING.id())) renderPing(g, font, mc);
	}

	private static void panel(GuiGraphics g, int x, int y, int w, int h) {
		g.fill(x, y, x + w, y + h, 0x990B1120);
		// subtle amber top edge
		g.fill(x, y, x + w, y + 1, StudiosClientClient.AMBER_DIM);
	}

	private static void renderFps(GuiGraphics g, Font font, Minecraft mc) {
		String value = String.valueOf(mc.getFps());
		String label = " FPS";
		int[] p = pos(ELEMENTS[0]);
		int w = font.width(value + label) + 8;
		panel(g, p[0], p[1], w, 14);
		int tx = p[0] + 4;
		g.drawString(font, value, tx, p[1] + 4, StudiosClientClient.AMBER, true);
		g.drawString(font, label, tx + font.width(value), p[1] + 4, StudiosClientClient.WHITE, true);
	}

	private static void renderCps(GuiGraphics g, Font font) {
		int l = CpsTracker.left();
		int r = CpsTracker.right();
		String text = l + " | " + r + " CPS";
		int[] p = pos(ELEMENTS[1]);
		int w = font.width(text) + 8;
		panel(g, p[0], p[1], w, 14);
		g.drawString(font, String.valueOf(l), p[0] + 4, p[1] + 4, StudiosClientClient.AMBER, true);
		int off = p[0] + 4 + font.width(String.valueOf(l));
		g.drawString(font, " | " + r + " CPS", off, p[1] + 4, StudiosClientClient.WHITE, true);
	}

	private static void renderCoords(GuiGraphics g, Font font, Minecraft mc) {
		int x = mc.player.getBlockX();
		int y = mc.player.getBlockY();
		int z = mc.player.getBlockZ();
		String text = "XYZ " + x + " / " + y + " / " + z;
		int[] p = pos(ELEMENTS[3]);
		int w = font.width(text) + 8;
		panel(g, p[0], p[1], w, 14);
		g.drawString(font, "XYZ ", p[0] + 4, p[1] + 4, StudiosClientClient.WHITE, true);
		g.drawString(font, x + " / " + y + " / " + z, p[0] + 4 + font.width("XYZ "), p[1] + 4, StudiosClientClient.AMBER, true);
	}

	private static void renderPing(GuiGraphics g, Font font, Minecraft mc) {
		int ping = -1;
		if (mc.getConnection() != null && mc.player != null) {
			var info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
			if (info != null) ping = info.getLatency();
		}
		String text = "Ping " + (ping < 0 ? "--" : ping + " ms");
		int[] p = pos(ELEMENTS[4]);
		int w = font.width(text) + 8;
		panel(g, p[0], p[1], w, 14);
		g.drawString(font, "Ping ", p[0] + 4, p[1] + 4, StudiosClientClient.WHITE, true);
		g.drawString(font, ping < 0 ? "--" : ping + " ms", p[0] + 4 + font.width("Ping "), p[1] + 4, StudiosClientClient.AMBER, true);
	}

	private static void renderKeystrokes(GuiGraphics g, Font font, Minecraft mc) {
		int[] p = pos(ELEMENTS[2]);
		int k = 20; // key size
		int gap = 2;
		int w = k * 3 + gap * 2;
		int h = k * 3 + gap * 2;
		panel(g, p[0], p[1], w, h);

		int baseX = p[0] + gap;
		int baseY = p[1] + gap;
		key(g, font, mc.options.keyUp().isDown(), "W", baseX + k + gap, baseY, k);
		key(g, font, mc.options.keyLeft().isDown(), "A", baseX, baseY + k + gap, k);
		key(g, font, mc.options.keyDown().isDown(), "S", baseX + k + gap, baseY + k + gap, k);
		key(g, font, mc.options.keyRight().isDown(), "D", baseX + (k + gap) * 2, baseY + k + gap, k);
		// mouse buttons
		long win = mc.getWindow().getWindow();
		keyWide(g, font, GLFW.glfwGetMouseButton(win, 0) == GLFW.GLFW_PRESS, "LMB", baseX, baseY + (k + gap) * 2, k * 2 + gap, k);
		keyWide(g, font, GLFW.glfwGetMouseButton(win, 1) == GLFW.GLFW_PRESS, "RMB", baseX + (k + gap) * 2, baseY + (k + gap) * 2, k, k);
	}

	private static void key(GuiGraphics g, Font font, boolean pressed, String label, int x, int y, int size) {
		keyWide(g, font, pressed, label, x, y, size, size);
	}

	private static void keyWide(GuiGraphics g, Font font, boolean pressed, String label, int x, int y, int w, int h) {
		int bg = pressed ? StudiosClientClient.AMBER : 0x880B1120;
		int fg = pressed ? StudiosClientClient.NAVY : StudiosClientClient.WHITE;
		g.fill(x, y, x + w, y + h, bg);
		int tx = x + (w - font.width(label)) / 2;
		int ty = y + (h - font.lineHeight) / 2 + 1;
		g.drawString(font, label, tx, ty, fg, false);
	}
}
