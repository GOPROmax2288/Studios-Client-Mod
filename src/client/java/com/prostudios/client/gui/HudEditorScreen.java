package com.prostudios.client.gui;

import com.prostudios.client.StudiosClientClient;
import com.prostudios.client.hud.HudOverlay;
import com.prostudios.client.module.ModuleDef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * In-game HUD editor: renders enabled HUD elements in place; drag to move, Done saves.
 */
public class HudEditorScreen extends Screen {
	private final Screen parent;
	private HudOverlay.Element dragging;
	private int dragOffX;
	private int dragOffY;
	private Rect doneButton;
	private Rect resetButton;

	private record Rect(int x, int y, int w, int h) {
		boolean contains(double mx, double my) {
			return mx >= x && mx < x + w && my >= y && my < y + h;
		}
	}

	public HudEditorScreen(Screen parent) {
		super(Component.literal("HUD Editor"));
		this.parent = parent;
	}

	@Override
	public void onClose() {
		StudiosClientClient.saveConfig();
		Minecraft.getInstance().setScreen(parent);
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		g.fill(0, 0, width, height, 0xB30B1120);
		g.drawCenteredString(font, "HUD Editor - drag elements to position them", width / 2, 8, StudiosClientClient.WHITE);

		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null && !mc.options.hideGui) {
			com.prostudios.client.hud.HudOverlay.render(g);
		}

		// outline each enabled element so it is grabbable
		for (HudOverlay.Element e : HudOverlay.ELEMENTS) {
			if (!StudiosClientClient.CONFIG.isEnabled(e.def().id())) continue;
			int[] p = HudOverlay.pos(e);
			int col = (dragging == e) ? StudiosClientClient.AMBER : 0x80F59E0B;
			g.renderOutline(p[0] - 2, p[1] - 2, p[0] + 90, p[1] + 24, col);
		}

		doneButton = new Rect(width / 2 - 104, height - 50, 100, 20);
		resetButton = new Rect(width / 2 + 4, height - 50, 100, 20);
		button(g, doneButton, "Done", mouseX, mouseY);
		button(g, resetButton, "Reset", mouseX, mouseY);
		super.render(g, mouseX, mouseY, delta);
	}

	private void button(GuiGraphics g, Rect r, String label, int mouseX, int mouseY) {
		int bg = r.contains(mouseX, mouseY) ? 0xFF1D2A45 : 0xFF0F172A;
		g.fill(r.x, r.y, r.x + r.w, r.y + r.h, bg);
		g.fill(r.x, r.y, r.x + r.w, r.y + 1, StudiosClientClient.AMBER_DIM);
		g.drawCenteredString(font, label, r.x + r.w / 2, r.y + 6, StudiosClientClient.AMBER);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			if (doneButton != null && doneButton.contains(mouseX, mouseY)) {
				onClose();
				return true;
			}
			if (resetButton != null && resetButton.contains(mouseX, mouseY)) {
				StudiosClientClient.CONFIG.resetPositions();
				return true;
			}
			for (HudOverlay.Element e : HudOverlay.ELEMENTS) {
				if (!StudiosClientClient.CONFIG.isEnabled(e.def().id())) continue;
				int[] p = HudOverlay.pos(e);
				if (mouseX >= p[0] - 4 && mouseX <= p[0] + 100 && mouseY >= p[1] - 4 && mouseY <= p[1] + 26) {
					dragging = e;
					dragOffX = (int) mouseX - p[0];
					dragOffY = (int) mouseY - p[1];
					return true;
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
		if (dragging != null && button == 0) {
			ModuleDef def = dragging.def();
			StudiosClientClient.CONFIG.setPos(def.id(),
					(int) Math.max(0, Math.min(width - 20, mouseX - dragOffX)),
					(int) Math.max(0, Math.min(height - 20, mouseY - dragOffY)));
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dx, dy);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (dragging != null && button == 0) {
			dragging = null;
			StudiosClientClient.saveConfig();
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
