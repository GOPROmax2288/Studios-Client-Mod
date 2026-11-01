package com.prostudios.client.gui;

import com.prostudios.client.StudiosClientClient;
import com.prostudios.client.module.ModuleDef;
import com.prostudios.client.util.StudioFeatures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * The Studios Menu: dark navy panel with an amber-accented grid of module cards.
 * Left-click a card to toggle it. ESC or Done closes and saves.
 */
public class StudiosMenuScreen extends Screen {
	private static final int CARD_W = 180;
	private static final int CARD_H = 42;
	private static final int GAP = 10;
	private static final int COLS = 2;

	private final Screen parent;
	private final List<Rect> cardRects = new ArrayList<>();
	private Rect hudEditorButton;
	private Rect doneButton;

	private record Rect(int x, int y, int w, int h) {
		boolean contains(double mx, double my) {
			return mx >= x && mx < x + w && my >= y && my < y + h;
		}
	}

	public StudiosMenuScreen(Screen parent) {
		super(Component.literal("Studios Menu"));
		this.parent = parent;
	}

	@Override
	public void onClose() {
		StudiosClientClient.saveConfig();
		Minecraft.getInstance().setScreen(parent);
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		cardRects.clear();
		int sw = width;
		int startX = (sw - (CARD_W * COLS + GAP * (COLS - 1))) / 2;
		int gridTop = 86;

		// backdrop
		g.fill(0, 0, sw, height, 0xE60B1120);
		// header wordmark
		g.pose().pushPose();
		g.pose().scale(3.0f, 3.0f, 1.0f);
		String word = "STUDIOS";
		int wordW = font.width(word) * 3;
		g.drawString(font, word, (sw - wordW) / 2 / 3, 14, StudiosClientClient.AMBER, true);
		g.pose().popPose();
		g.drawCenteredString(font, "C L I E N T   C O R E", sw / 2, 48, StudiosClientClient.WHITE);
		g.fill(sw / 2 - 60, 62, sw / 2 + 60, 63, StudiosClientClient.AMBER_DIM);

		// module cards
		for (int i = 0; i < ModuleDef.ALL.length; i++) {
			ModuleDef def = ModuleDef.ALL[i];
			int col = i % COLS;
			int row = i / COLS;
			int x = startX + col * (CARD_W + GAP);
			int y = gridTop + row * (CARD_H + GAP);
			Rect r = new Rect(x, y, CARD_W, CARD_H);
			cardRects.add(r);
			boolean enabled = StudiosClientClient.CONFIG.isEnabled(def.id());
			boolean hovered = r.contains(mouseX, mouseY);
			int border = enabled ? StudiosClientClient.AMBER : (hovered ? 0x80F59E0B : 0xFF1E293B);
			g.fill(x, y, x + CARD_W, y + CARD_H, hovered ? 0xFF111B33 : 0xFF0F172A);
			// border
			g.fill(x, y, x + CARD_W, y + 1, border);
			g.fill(x, y + CARD_H - 1, x + CARD_W, y + CARD_H, border);
			g.fill(x, y, x + 1, y + CARD_H, border);
			g.fill(x + CARD_W - 1, y, x + CARD_W, y + CARD_H, border);

			g.drawString(font, def.name(), x + 12, y + 10, StudiosClientClient.WHITE, true);
			g.drawString(font, enabled ? "ON" : "OFF", x + 12, y + 26, enabled ? StudiosClientClient.AMBER : StudiosClientClient.GRAY, true);
			// toggle pill
			int px = x + CARD_W - 40;
			int py = y + CARD_H / 2 - 6;
			g.fill(px, py, px + 28, py + 12, enabled ? StudiosClientClient.AMBER : 0xFF1E293B);
			g.fill(enabled ? px + 16 : px + 2, py + 2, enabled ? px + 26 : px + 12, py + 10, enabled ? StudiosClientClient.NAVY : 0xFF94A3B8);
		}

		// footer buttons
		int footerY = height - 60;
		hudEditorButton = new Rect(sw / 2 - 110, footerY, 100, 20);
		doneButton = new Rect(sw / 2 + 10, footerY, 100, 20);
		drawButton(g, hudEditorButton, "HUD Editor", mouseX, mouseY);
		drawButton(g, doneButton, "Done", mouseX, mouseY);

		g.drawCenteredString(font, "Right Shift reopens this menu", sw / 2, height - 24, StudiosClientClient.GRAY);

		super.render(g, mouseX, mouseY, delta);
	}

	private void drawButton(GuiGraphics g, Rect r, String label, int mouseX, int mouseY) {
		int bg = r.contains(mouseX, mouseY) ? 0xFF1D2A45 : 0xFF0F172A;
		g.fill(r.x, r.y, r.x + r.w, r.y + r.h, bg);
		g.fill(r.x, r.y, r.x + r.w, r.y + 1, StudiosClientClient.AMBER_DIM);
		g.drawCenteredString(font, label, r.x + r.w / 2, r.y + 6, StudiosClientClient.AMBER);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			for (int i = 0; i < cardRects.size() && i < ModuleDef.ALL.length; i++) {
				if (cardRects.get(i).contains(mouseX, mouseY)) {
					ModuleDef def = ModuleDef.ALL[i];
					boolean now = !StudiosClientClient.CONFIG.isEnabled(def.id());
					StudiosClientClient.CONFIG.setEnabled(def.id(), now);
					if (def == ModuleDef.FULLBRIGHT) {
						StudioFeatures.applyFullbright(Minecraft.getInstance(), now);
					}
					return true;
				}
			}
			if (hudEditorButton != null && hudEditorButton.contains(mouseX, mouseY)) {
				Minecraft.getInstance().setScreen(new HudEditorScreen(this));
				return true;
			}
			if (doneButton != null && doneButton.contains(mouseX, mouseY)) {
				onClose();
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
