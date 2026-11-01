package com.prostudios.client.mixin;

import com.prostudios.client.util.StudioFeatures;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Sky fog tweak: pushes the distance fog start/end much farther out while the
 * "Sky Fog Tweak" module is enabled, so distant terrain stays visible.
 */
@Mixin(FogRenderer.class)
public class FogRendererMixin {
	@ModifyVariable(
			method = "setupFog",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/renderer/fog/FogData;renderDistanceEnd:F",
					opcode = org.objectweb.asm.Opcodes.PUTFIELD,
					shift = At.Shift.AFTER),
			ordinal = 0)
	private static FogData studios$pushFogBack(FogData data) {
		if (!StudioFeatures.fogTweakEnabled || data == null) return data;
		if (Float.isNaN(data.environmentalStart) || Float.isNaN(data.environmentalEnd)) return data;
		data.environmentalStart *= 2.0f;
		data.environmentalEnd *= 2.0f;
		data.renderDistanceStart *= 1.5f;
		data.renderDistanceEnd *= 1.5f;
		return data;
	}
}
