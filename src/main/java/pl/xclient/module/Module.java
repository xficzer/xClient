package pl.xclient.module;

public class Module {
    private final String name;
    private final Runnable onEnable;
    private final Runnable onDisable;
    private boolean enabled;

    public Module(String name, Runnable onEnable, Runnable onDisable) {
        this.name = name;
        this.onEnable = onEnable;
        this.onDisable = onDisable;
    }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean value) {
        if (enabled == value) return;
        enabled = value;
        (value ? onEnable : onDisable).run();
        ModuleManager.save();
    }

    public void toggle() { setEnabled(!enabled); }

    void load(boolean value) {
        enabled = value;
        if (value) onEnable.run();
    }
}