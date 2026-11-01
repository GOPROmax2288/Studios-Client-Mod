package com.prostudios.client.util;

import com.prostudios.client.StudiosClientClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.server.level.ParticleStatus;

/**
 * Handles the "stateful" modules: zoom (smooth FOV interpolation while C is held),
 * fullbright (gamma override), low particle density and the sky fog tweak
 * (implemented by the FogRendererMixin, which reads {@link #fogTweakEnabled}).
 */
public final class StudioFeatures {
	private static float zoomCurrent = 1.0f;
	private static Integer savedFov = null;
	private static Double prevGamma = null;
	private static ParticleStatus prevParticles = null;

	/** Read by FogRendererMixin every frame; flipped by {@link #applyFogTweak}. */
	public static volatile boolean fogTweakEnabled = false;

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
			OptionInstance<Integer> fov = mc.options.fov();
			if (zoomCurrent <= 0.999f) {
				fov.set(Math.max((int) Math.floor(savedFov * zoomCurrent), 5));
			} else {
				fov.set(savedFov);
				savedFov = null;
			}
		}
	}

	private static void releaseZoom(Minecraft mc) {
		if (savedFov != null) {
			mc.options.fov().set(savedFov);
			savedFov = null;
		}
		zoomCurrent = 1.0f;
	}

	public static void applyFullbright(Minecraft mc, boolean on) {
		OptionInstance<Double> gamma = mc.options.gamma();
		if (on) {
			if (prevGamma == null) prevGamma = gamma.get();
			gamma.set(16.0);
		} else if (prevGamma != null) {
			gamma.set(prevGamma);
			prevGamma = null;
		}
	}

	public static void applyParticles(Minecraft mc, boolean on) {
		OptionInstance<ParticleStatus> particles = mc.options.particles();
		if (on) {
			if (prevParticles == null) prevParticles = particles.get();
			particles.set(ParticleStatus.DECREASED);
		} else if (prevParticles != null) {
			particles.set(prevParticles);
			prevParticles = null;
		}
	}

	public static void applyFogTweak(Minecraft mc, boolean on) {
		fogTweakEnabled = on;
	}
}
