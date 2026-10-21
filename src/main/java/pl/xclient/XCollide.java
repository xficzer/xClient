package pl.xclient;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;

public class XCollide implements ModInitializer {
    private static final String TEAM = "xcollide";

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            Scoreboard sb = server.getScoreboard();
            String name = player.getNameForScoreboard();

            Team team = sb.getScoreHolderTeam(name);
            if (team == null) {
                team = sb.getTeam(TEAM);
                if (team == null) team = sb.addTeam(TEAM);
                sb.addScoreHolderToTeam(name, team);
            }
            team.setCollisionRule(AbstractTeam.CollisionRule.ALWAYS);
        });
    }
}