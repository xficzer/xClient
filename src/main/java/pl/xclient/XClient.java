package pl.xclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import pl.xclient.gui.XClientScreen;
import pl.xclient.module.ModuleManager;

public class XClient implements ClientModInitializer {
    public static final String ID = "xclient";
    private static KeyBinding openGui;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        openGui = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.xclient.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.xclient"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGui.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new XClientScreen());
                }
            }
        });
    }
}