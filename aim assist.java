package com.example.aimtrigger;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.util.List;

public class AimAssist {
    private final Minecraft mc = Minecraft.getMinecraft();

    // Настройки
    private final float fov = 70.0f;         // Угол обзора
    private final double maxDistance = 4.5;  // Дистанция
    private final float smoothSpeed = 0.55f; // Жесткий аим (0.55 - резко, 0.8+ - мгновенно)

    public void onTick() {
        // Работает при зажатой ПКМ (Mouse 1) в игре
        if (Mouse.isButtonDown(1) && mc.currentScreen == null) {
            run();
        }
    }

    private void run() {
        EntityLivingBase target = getClosestTarget();
        if (target == null) return;

        double diffX = target.posX - mc.thePlayer.posX;
        double diffY = (target.posY + target.getEyeHeight() * 0.75) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double diffZ = target.posZ - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) (MathHelper.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float targetPitch = (float) (-(MathHelper.atan2(diffY, dist) * 180.0D / Math.PI));

        float yawDelta = MathHelper.wrapAngleTo180_float(targetYaw - mc.thePlayer.rotationYaw);
        float pitchDelta = MathHelper.wrapAngleTo180_float(targetPitch - mc.thePlayer.rotationPitch);

        mc.thePlayer.rotationYaw += yawDelta * smoothSpeed;
        mc.thePlayer.rotationPitch += pitchDelta * smoothSpeed;
    }

    private EntityLivingBase getClosestTarget() {
        EntityLivingBase bestTarget = null;
        double closestAngle = fov;

        List<EntityLivingBase> entities = mc.theWorld.getEntitiesWithinAABB(
                EntityLivingBase.class,
                mc.thePlayer.getEntityBoundingBox().expand(maxDistance, maxDistance, maxDistance)
        );

        for (EntityLivingBase entity : entities) {
            if (entity == mc.thePlayer || entity.isDead || entity.getHealth() <= 0) continue;

            double diffX = entity.posX - mc.thePlayer.posX;
            double diffZ = entity.posZ - mc.thePlayer.posZ;
            double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

            if (dist > maxDistance) continue;

            float targetYaw = (float) (MathHelper.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
            float yawDelta = Math.abs(MathHelper.wrapAngleTo180_float(targetYaw - mc.thePlayer.rotationYaw));

            if (yawDelta < closestAngle) {
                closestAngle = yawDelta;
                bestTarget = entity;
            }
        }
        return bestTarget;
    }
}
