package com.charles445.simpledifficulty.mixin.compat.ignitehud;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "com.deadzoke.ignitehud.gui.GuiWidget", remap = false)
public class MixinIgniteHUDGuiWidget {

    @Redirect(
        method = "getThirst",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;getCapability(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/util/EnumFacing;)Ljava/lang/Object;"
        )
    )
    private Object redirectThirst(EntityPlayerSP player, Capability capability, EnumFacing facing) {
        // Redirige la busqueda de Capability de TAN hacia la Capability de sed
        return player.getCapability(SDCapabilities.THIRST, null);
    }

    @Redirect(
        method = "getTemperature",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;getCapability(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/util/EnumFacing;)Ljava/lang/Object;"
        )
    )
    private Object redirectTemperature(EntityPlayerSP player, Capability capability, EnumFacing facing) {
        // Redirige la busqueda de Capability de TAN hacia la Capability de temperatura
        return player.getCapability(SDCapabilities.TEMPERATURE, null);
    }
}
