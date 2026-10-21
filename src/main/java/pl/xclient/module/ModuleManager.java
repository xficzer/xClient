package pl.xclient.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.SimpleOption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("xclient.json");

    public static List<Module> all() { return MODULES; }

    private static <T extends Enum<T>> Module lastValue(String name, Supplier<SimpleOption<T>> opt) {
        final List<T> prev = new ArrayList<>();
        return new Module(name,
                () -> {
                    SimpleOption<T> o = opt.get();
                    prev.clear();
                    prev.add(o.getValue());
                    T[] all = o.getValue().getDeclaringClass().getEnumConstants();
                    o.setValue(all[all.length - 1]);
                },
                () -> {
                    if (!prev.isEmpty()) opt.get().setValue(prev.get(0));
                });
    }

    public static void init() {
        MinecraftClient mc = MinecraftClient.getInstance();

        final int[] prevFps = {120};
        final CloudRenderMode[] prevClouds = {CloudRenderMode.FAST};
        final int[] prevView = {12};

        MODULES.add(new Module("Limit FPS 60",
                () -> { prevFps[0] = mc.options.getMaxFps().getValue(); mc.options.getMaxFps().setValue(60); },
                () -> mc.options.getMaxFps().setValue(prevFps[0])));

        MODULES.add(lastValue("Minimalne czasteczki", () -> mc.options.getParticles()));

        MODULES.add(new Module("Bez chmur",
                () -> { prevClouds[0] = mc.options.getCloudRenderMode().getValue(); mc.options.getCloudRenderMode().setValue(CloudRenderMode.OFF); },
                () -> mc.options.getCloudRenderMode().setValue(prevClouds[0])));

        MODULES.add(new Module("Render distance 8",
                () -> { prevView[0] = mc.options.getViewDistance().getValue(); mc.options.getViewDistance().setValue(8); },
                () -> mc.options.getViewDistance().setValue(prevView[0])));
    }

    public static void load() {
        try {
            if (!Files.exists(FILE)) return;
            Map<String, Boolean> data = GSON.fromJson(Files.readString(FILE),
                    new TypeToken<Map<String, Boolean>>() {}.getType());
            if (data == null) return;
            for (Module m : MODULES) m.load(data.getOrDefault(m.getName(), false));
        } catch (IOException | RuntimeException ignored) {}
    }

    public static void save() {
        Map<String, Boolean> data = new HashMap<>();
        for (Module m : MODULES) data.put(m.getName(), m.isEnabled());
        try {
            Files.writeString(FILE, GSON.toJson(data));
        } catch (IOException ignored) {}
    }
}