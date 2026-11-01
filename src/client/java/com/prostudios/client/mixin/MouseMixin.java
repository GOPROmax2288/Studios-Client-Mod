package com.prostudios.client.mixin;

import com.prostudios.client.util.CpsTracker;
import net.minecraft.client.MouseHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseMixin {
	@Inject(method = "onMouseButton", at = @At("HEAD"))
	private void studios$cps(long window, int button, int action, int mods, CallbackInfo ci) {
		if (action == GLFW.GLFW_PRESS && (button == 0 || button == 1)) {
			CpsTracker.onPress(button);
		}
	}
}
