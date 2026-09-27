package com.charles445.simpledifficulty.mixin.compat.ignitehud;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.deadzoke.ignitehud.IgniteHUD;
import com.deadzoke.ignitehud.References;
import com.deadzoke.ignitehud.config.Config;
import com.deadzoke.ignitehud.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.SharedMonsterAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.deadzoke.ignitehud.gui.GuiWidget", remap = false)
public abstract class MixinIgniteHUDGui {

    @Inject(method = "getArmorValue", at = @At("HEAD"), cancellable = true, remap = false)
    private void redirectArmorValue(EntityPlayerSP player, CallbackInfo ci) {
        ci.cancel();

        int armor = (int) player.getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue();
        int toughness = (int) player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
        String hunger = player.getFoodStats().getFoodLevel() + "";
        String satur = (int) player.getFoodStats().getSaturationLevel() + "";
        int posXicon = 59 + RenderHelper.getStringWidth(hunger) + 5 + 8 + 4 + RenderHelper.getStringWidth(satur) + 5;
        int posXtext = posXicon + 8 + 4;

        if (IgniteHUD.hasToughAsNails && Config.cfgThirst) {
            int thirstLevel = 20;
            IThirstCapability thirstCap = SDCapabilities.getThirstData(player);
            if (thirstCap != null) {
                thirstLevel = thirstCap.getThirstLevel();
            }
            String thirst = thirstLevel + "";
            posXicon += 12 + RenderHelper.getStringWidth(thirst) + 5;
            posXtext += 12 + RenderHelper.getStringWidth(thirst) + 5;
        }

        if (armor > 0) {
            Minecraft.getMinecraft().renderEngine.bindTexture(References.TEX_HUD_ICON);
            RenderHelper.drawIcon(posXicon, 31, 1, 8);
            RenderHelper.drawFontWithShadow(armor + "", posXtext, 32, 12106180, 1579034);
        }

        if (player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue() > 0.0) {
            if (armor > 0) {
                posXicon += 12 + RenderHelper.getStringWidth(toughness + "") + 5;
                posXtext += 12 + RenderHelper.getStringWidth(toughness + "") + 5;
            }

            Minecraft.getMinecraft().renderEngine.bindTexture(References.TEX_HUD_ICON);
            RenderHelper.drawIcon(posXicon, 31, 1, 9);
            RenderHelper.drawFontWithShadow(toughness + "", posXtext, 32, 12106180, 1579034);
        }
    }

    @Inject(method = "getThirst", at = @At("HEAD"), cancellable = true, remap = false)
    private void redirectThirst(EntityPlayerSP player, CallbackInfo ci) {
        ci.cancel();

        String hunger = player.getFoodStats().getFoodLevel() + "";
        String satur = (int) player.getFoodStats().getSaturationLevel() + "";

        int thirstLevel = 20;
        IThirstCapability thirstCap = SDCapabilities.getThirstData(player);
        if (thirstCap != null) {
            thirstLevel = thirstCap.getThirstLevel();
        }
        String thirst = thirstLevel + "";

        int posXicon = 59 + RenderHelper.getStringWidth(hunger) + 5 + 8 + 4 + RenderHelper.getStringWidth(satur) + 5;
        int posXtext = posXicon + 8 + 4;
        int icon = 1;

        Minecraft.getMinecraft().renderEngine.bindTexture(References.TEX_HUD_ICON);
        RenderHelper.drawIcon(posXicon, 31, 2, icon);
        int color = 1859300;
        int shadow = 856862;

        RenderHelper.drawFontWithShadow(thirst, posXtext, 32, color, shadow);
    }

    @Inject(method = "getTemperature", at = @At("HEAD"), cancellable = true, remap = false)
    private void redirectTemperature(EntityPlayerSP player, ScaledResolution scaled, CallbackInfo ci) {
        ci.cancel();

        int screenWidth = scaled.getScaledWidth();
        int screenHeight = scaled.getScaledHeight();

        int tempLevel = 12;
        ITemperatureCapability tempCap = SDCapabilities.getTemperatureData(player);
        if (tempCap != null) {
            tempLevel = tempCap.getTemperatureLevel();
        }

        // Mapeo del nivel de temperatura (0 a 24, donde 12 es el centro)
        int tex = 2; // MILD
        if (tempLevel >= 19) tex = 0;      // HOT
        else if (tempLevel >= 15) tex = 1; // WARM
        else if (tempLevel >= 10) tex = 2; // MILD
        else if (tempLevel >= 6) tex = 3;  // COOL
        else tex = 4;                      // ICY

        // Calcula de la posición del indicador en el medidor (-12 a +12)
        float tempValue = (tempLevel - 12);

        Minecraft.getMinecraft().renderEngine.bindTexture(References.TEX_HUD_BASE);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(screenWidth - 53, screenHeight - 80, 117, 0, 48, 18);

        Minecraft.getMinecraft().renderEngine.bindTexture(References.TEX_HUD_BAR);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(screenWidth - 50, screenHeight - 75, 212, 136, 27, 8);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect((int) (screenWidth - 24 - tempValue), screenHeight - 74, 239, 136, 1, 6);

        Minecraft.getMinecraft().renderEngine.bindTexture(References.TEX_HUD_ICON);
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(screenWidth - 21, screenHeight - 78, 200 + tex % 4 * 14, tex / 4 * 14, 14, 14);
    }
}
