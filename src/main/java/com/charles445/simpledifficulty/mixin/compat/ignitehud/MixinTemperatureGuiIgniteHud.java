package com.charles445.simpledifficulty.mixin.compat.ignitehud;

import com.charles445.simpledifficulty.client.gui.TemperatureGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TemperatureGui.class)
public class MixinTemperatureGuiIgniteHud {

    /**
     * Cancela la interfaz de temperatura
     * solamente cuando el mod IgniteHUD este presente en el juego
     */
    @Inject(method = "onPreRenderGameOverlay", at = @At("HEAD"), cancellable = true, remap = false)
    private void cancelTemperatureRender(RenderGameOverlayEvent.Pre event, CallbackInfo ci) {
        if (Loader.isModLoaded("ignitehud")) {
            ci.cancel();
        }
    }
}
