package com.example.aimtrigger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = AimTriggerMod.MODID, version = AimTriggerMod.VERSION)
public class AimTriggerMod {
    public static final String MODID = "aimtriggermod";
    public static final String VERSION = "1.0";

    private final Minecraft mc = Minecraft.getMinecraft();

    public static KeyBinding keyToggleTrigger = new KeyBinding("Toggle TriggerBot", Keyboard.KEY_G, "Aim & Trigger");

    // Подключаем твои два файла
    private final AimAssist aimAssist = new AimAssist();
    private final TriggerBot triggerBot = new TriggerBot();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientRegistry.registerKeyBinding(keyToggleTrigger);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (keyToggleTrigger.isPressed()) {
            triggerBot.toggle();
            if (mc.thePlayer != null) {
                mc.thePlayer.addChatMessage(new ChatComponentText(
                        "§7[§cMod§7] TriggerBot: " + (triggerBot.isEnabled() ? "§aВКЛ (5 CPS)" : "§cВЫКЛ")
                ));
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        // Вызываем проверку из твоих файлов
        aimAssist.onTick();
        triggerBot.onTick();
    }
}
