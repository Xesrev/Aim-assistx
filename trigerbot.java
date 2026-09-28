package com.example.aimtrigger;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;

public class TriggerBot {
    private final Minecraft mc = Minecraft.getMinecraft();

    private boolean enabled = false;
    private long lastAttackTime = 0;
    private final long attackDelayMs = 1000 / 5; // 200 миллисекунд = 5 CPS

    public void toggle() {
        this.enabled = !this.enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void onTick() {
        if (!enabled || mc.currentScreen != null) return;

        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            Entity hitEntity = mc.objectMouseOver.entityHit;

            if (hitEntity instanceof EntityLivingBase) {
                long currentTime = System.currentTimeMillis();

                // Удар ровно каждые 200 мс
                if (currentTime - lastAttackTime >= attackDelayMs) {
                    mc.playerController.attackEntity(mc.thePlayer, hitEntity);
                    mc.thePlayer.swingItem();
                    lastAttackTime = currentTime;
                }
            }
        }
    }
}
