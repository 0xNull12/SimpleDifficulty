package com.charles445.simpledifficulty.mixin.compat.ignitehud;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.SharedMonsterAttributes;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.deadzoke.ignitehud.gui.GuiWidget", remap = false)
public abstract class MixinIgniteHUD {

    /**
     * Forza a que el campo IgniteHUD.hasToughAsNails devuelva true
     * para activar la sed y temperatura
     */
    @Redirect(
        method = "*",
        at = @At(
            value = "FIELD",
            target = "Lcom/deadzoke/ignitehud/IgniteHUD;hasToughAsNails:Z",
            opcode = Opcodes.GETSTATIC
        ),
        remap = false
    )
    private boolean fakeHasToughAsNails() {
        return true;
    }

    // Intercepta getThirst(EntityPlayerSP player)
    @Inject(method = "getThirst", at = @At("HEAD"), cancellable = true, remap = false)
    private void redirectThirst(EntityPlayerSP player, CallbackInfo ci) {
        ci.cancel();
        if (player == null) return;

        IThirstCapability thirstCap = player.getCapability(SDCapabilities.THIRST, null);
        if (thirstCap == null) return;

        String hunger = player.getFoodStats().getFoodLevel() + "";
        String satur = (int) player.getFoodStats().getSaturationLevel() + "";
        String thirst = thirstCap.getThirstLevel() + "";

        int posXicon = 59 + com.deadzoke.ignitehud.util.RenderHelper.getStringWidth(hunger) + 5 + 8 + 4 + com.deadzoke.ignitehud.util.RenderHelper.getStringWidth(satur) + 5;
        int posXtext = posXicon + 8 + 4;
        int icon = 1;

        Minecraft.getMinecraft().renderEngine.bindTexture(com.deadzoke.ignitehud.References.TEX_HUD_ICON);
        com.deadzoke.ignitehud.util.RenderHelper.drawIcon(posXicon, 31, 2, icon);
        int color = 1859300;
        int shadow = 856862;

        com.deadzoke.ignitehud.util.RenderHelper.drawFontWithShadow(thirst, posXtext, 32, color, shadow);
    }

    // Intercepta getTemperature(EntityPlayerSP player, ScaledResolution scaled)
    @Inject(method = "getTemperature", at = @At("HEAD"), cancellable = true, remap = false)
    private void redirectTemperature(EntityPlayerSP player, ScaledResolution scaled, CallbackInfo ci) {
        ci.cancel();
        if (player == null || scaled == null) return;

        ITemperatureCapability tempCap = player.getCapability(SDCapabilities.TEMPERATURE, null);
        if (tempCap == null) return;

        int screenWidth = scaled.getScaledWidth();
        int screenHeight = scaled.getScaledHeight();

        int tempLevel = tempCap.getTemperatureLevel();
        int tex = 2; // MILD por defecto
        if (tempLevel >= 18) {
            tex = 0; // HOT
        } else if (tempLevel >= 14) {
            tex = 1; // WARM
        } else if (tempLevel >= 10) {
            tex = 2; // MILD
        } else if (tempLevel >= 6) {
            tex = 3; // COOL
        } else {
            tex = 4; // ICY
        }

        float tempValue = Math.min(25.0f, Math.max(0.0f, tempLevel * 1.04f));

        Minecraft mc = Minecraft.getMinecraft();
        mc.renderEngine.bindTexture(com.deadzoke.ignitehud.References.TEX_HUD_BASE);
        mc.ingameGUI.drawTexturedModalRect(screenWidth - 53, screenHeight - 80, 117, 0, 48, 18);
        mc.renderEngine.bindTexture(com.deadzoke.ignitehud.References.TEX_HUD_BAR);
        mc.ingameGUI.drawTexturedModalRect(screenWidth - 50, screenHeight - 75, 212, 136, 27, 8);
        mc.renderEngine.bindTexture(com.deadzoke.ignitehud.References.TEX_HUD_ICON);
        mc.ingameGUI.drawTexturedModalRect(screenWidth - 21, screenHeight - 78, 200 + tex % 4 * 14, tex / 4 * 14, 14, 14);
        mc.renderEngine.bindTexture(com.deadzoke.ignitehud.References.TEX_HUD_BAR);
        mc.ingameGUI.drawTexturedModalRect((int) (screenWidth - 24 - tempValue), screenHeight - 74, 239, 136, 1, 6);
    }

    // Intercepta getArmorValue(EntityPlayerSP player)
    @Inject(method = "getArmorValue", at = @At("HEAD"), cancellable = true, remap = false)
    private void redirectArmorValue(EntityPlayerSP player, CallbackInfo ci) {
        ci.cancel();
        if (player == null) return;

        int armor = (int) player.getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue();
        int toughness = (int) player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
        String hunger = player.getFoodStats().getFoodLevel() + "";
        String satur = (int) player.getFoodStats().getSaturationLevel() + "";
        int posXicon = 59 + com.deadzoke.ignitehud.util.RenderHelper.getStringWidth(hunger) + 5 + 8 + 4 + com.deadzoke.ignitehud.util.RenderHelper.getStringWidth(satur) + 5;
        int posXtext = posXicon + 8 + 4;

        if (com.deadzoke.ignitehud.config.Config.cfgThirst) {
            IThirstCapability thirstCap = player.getCapability(SDCapabilities.THIRST, null);
            if (thirstCap != null) {
                String thirst = thirstCap.getThirstLevel() + "";
                posXicon += 12 + com.deadzoke.ignitehud.util.RenderHelper.getStringWidth(thirst) + 5;
                posXtext += 12 + com.deadzoke.ignitehud.util.RenderHelper.getStringWidth(thirst) + 5;
            }
        }

        Minecraft mc = Minecraft.getMinecraft();

        if (armor > 0) {
            mc.renderEngine.bindTexture(com.deadzoke.ignitehud.References.TEX_HUD_ICON);
            com.deadzoke.ignitehud.util.RenderHelper.drawIcon(posXicon, 31, 1, 8);
            com.deadzoke.ignitehud.util.RenderHelper.drawFontWithShadow(armor + "", posXtext, 32, 12106180, 1579034);
        }

        if (player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue() > 0.0) {
            if (armor > 0) {
                posXicon += 12 + com.deadzoke.ignitehud.util.RenderHelper.getStringWidth(toughness + "") + 5;
                posXtext += 12 + com.deadzoke.ignitehud.util.RenderHelper.getStringWidth(toughness + "") + 5;
            }

            mc.renderEngine.bindTexture(com.deadzoke.ignitehud.References.TEX_HUD_ICON);
            com.deadzoke.ignitehud.util.RenderHelper.drawIcon(posXicon, 31, 1, 9);
            com.deadzoke.ignitehud.util.RenderHelper.drawFontWithShadow(toughness + "", posXtext, 32, 12106180, 1579034);
        }
    }
}
