package pl.xclient.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.ParticlesMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("xclient.json");

    public static List<Module> all() { return MODULES; }

    public static void init() {
        MinecraftClient mc = MinecraftClient.getInstance();

        final int[] prevFps = {120};
        final ParticlesMode[] prevParticles = {ParticlesMode.ALL};
        final CloudRenderMode[] prevClouds = {CloudRenderMode.FAST};
        final int[] prevView = {12};

        MODULES.add(new Module("Limit FPS 60",
                () -> { prevFps[0] = mc.options.getMaxFps().getValue(); mc.options.getMaxFps().setValue(60); },
                () -> mc.options.getMaxFps().setValue(prevFps[0])));

        MODULES.add(new Module("Minimalne czasteczki",
                () -> { prevParticles[0] = mc.options.getParticles().getValue(); mc.options.getParticles().setValue(ParticlesMode.MINIMAL); },
                () -> mc.options.getParticles().setValue(prevParticles[0])));

        MODULES.add(new Module("Bez chmur",
                () -> { prevClouds[0] = mc.options.getCloudRenderMode().getValue(); mc.options.getCloudRenderMode().setValue(CloudRenderMode.OFF); },
                () -> mc.options.getCloudRenderMode().setValue(prevClouds[0])));

        MODULES.add(new Module("Render distance 8",
                () -> { prevView[0] = mc.options.getViewDistance().getValue(); mc.options.getViewDistance().setValue(8); },
                () -> mc.options.getViewDistance().setValue(prevView[0])));

        load();
    }

    private static void load() {
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