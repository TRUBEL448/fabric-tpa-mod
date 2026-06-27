package com.trubel.tpamod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class TPAClientMod implements ClientModInitializer {
    public static KeyBinding openTPAGuiKey;

    @Override
    public void onInitializeClient() {
        openTPAGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tpamod.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_T,
                "category.tpamod.tpa"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openTPAGuiKey.wasPressed()) {
                client.setScreen(new TPAScreen());
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            TPAManager.tick(client);
        });
    }
}
