package pl.xclient.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import pl.xclient.module.Module;
import pl.xclient.module.ModuleManager;

public class XClientScreen extends Screen {
    private static final int W = 200, H = 20, GAP = 4;

    public XClientScreen() {
        super(Text.literal("xClient"));
    }

    @Override
    protected void init() {
        int x = width / 2 - W / 2;
        int y = height / 2 - (ModuleManager.all().size() * (H + GAP)) / 2;

        for (Module m : ModuleManager.all()) {
            addDrawableChild(ButtonWidget.builder(label(m), btn -> {
                m.toggle();
                btn.setMessage(label(m));
            }).dimensions(x, y, W, H).build());
            y += H + GAP;
        }
    }

    private static Text label(Module m) {
        return Text.literal(m.getName() + ": " + (m.isEnabled() ? "§aON" : "§cOFF"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, "xClient", width / 2, height / 2 - 90, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }
}