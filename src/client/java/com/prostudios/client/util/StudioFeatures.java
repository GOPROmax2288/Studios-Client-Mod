package com.prostudios.client.util;

import com.prostudios.client.StudiosClientClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;

/**
 * Handles the two "stateful" modules: zoom (smooth FOV interpolation while C is held)
 * and fullbright (gamma override via OptionInstance value set, bypassing validation).
 */
public final class StudioFeatures {
	private static float zoomCurrent = 1.0f;
	private static Double savedFov = null;
	private static Double prevGamma = null;

	private StudioFeatures() {}

	public static void tick(Minecraft mc) {
		if (mc.player == null) {
			releaseZoom(mc);
			return;
		}
		boolean wantZoom = StudiosClientClient.CONFIG.isEnabled("zoom") && StudiosClientClient.zoomKey.isDown();
		float target = wantZoom ? (float) StudiosClientClient.CONFIG.zoomFactor : 1.0f;
		if (wantZoom && savedFov == null && zoomCurrent > 0.99f) {
			savedFov = mc.options.fov().get();
		}
		zoomCurrent += (target - zoomCurrent) * 0.35f;
		if (Math.abs(zoomCurrent - target) < 0.002f) zoomCurrent = target;

		if (savedFov != null) {
			OptionInstance<Double> fov = mc.options.fov();
			if (zoomCurrent <= 0.999f) {
				setOption(fov, Math.max(savedFov * zoomCurrent, 5.0));
			} else {
				setOption(fov, savedFov);
				savedFov = null;
			}
		}
	}

	private static void releaseZoom(Minecraft mc) {
		if (savedFov != null) {
			setOption(mc.options.fov(), savedFov);
			savedFov = null;
		}
		zoomCurrent = 1.0f;
	}

	public static void applyFullbright(Minecraft mc, boolean on) {
		OptionInstance<Double> gamma = mc.options.gamma();
		if (on) {
			if (prevGamma == null) prevGamma = gamma.get();
			setOption(gamma, 16.0);
		} else if (prevGamma != null) {
			setOption(gamma, prevGamma);
			prevGamma = null;
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void setOption(OptionInstance<T> option, T value) {
		((com.prostudios.client.mixin.OptionInstanceAccessor<T>) option).studios$setValue(value);
	}
}
