package com.charles445.simpledifficulty.mixin.compat.ignitehud;

import com.deadzoke.ignitehud.IgniteHUD;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.deadzoke.ignitehud.IgniteHUD", remap = false)
public abstract class MixinIgniteHUDMain {

    /**
     * Fuerz la variable estatica 'hasToughAsNails' a 'true' al finalizar preInit
     * para que Ignite HUD reactive la renderización de la sed y temperatura
     */
    @Inject(method = "preInit", at = @At("TAIL"), remap = false)
    private void enableToughAsNailsFlag(FMLPreInitializationEvent event, CallbackInfo ci) {
        IgniteHUD.hasToughAsNails = true;
    }
}
