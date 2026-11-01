package com.prostudios.client.mixin;

import com.prostudios.client.util.CpsTracker;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseMixin {
	@Inject(method = "onButton(JLnet/minecraft/client/input/MouseButtonInfo;I)V", at = @At("HEAD"))
	private void studios$cps(long window, MouseButtonInfo info, int action, CallbackInfo ci) {
		if (action == GLFW.GLFW_PRESS && (info.button() == 0 || info.button() == 1)) {
			CpsTracker.onPress(info.button());
		}
	}
}
