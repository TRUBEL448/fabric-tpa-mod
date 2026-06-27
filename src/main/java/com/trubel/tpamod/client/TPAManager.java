package com.trubel.tpamod.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class TPAManager {
    private static final List<String> players = new ArrayList<>();
    private static boolean running = false;
    private static long lastTpaTime = 0;
    private static final long TPA_INTERVAL = 60000; // 1 minuta

    public static void addPlayer(String playerName) {
        if (!players.contains(playerName)) {
            players.add(playerName);
        }
    }

    public static void removePlayer(String playerName) {
        players.remove(playerName);
    }

    public static List<String> getPlayers() {
        return new ArrayList<>(players);
    }

    public static void start() {
        running = true;
        lastTpaTime = System.currentTimeMillis();
    }

    public static void stop() {
        running = false;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void tick(MinecraftClient client) {
        if (!running || players.isEmpty()) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTpaTime >= TPA_INTERVAL) {
            for (String playerName : players) {
                if (client.player != null && client.getNetworkHandler() != null) {
                    String command = "/tpa " + playerName;
                    client.getNetworkHandler().sendPacket(new ChatMessageC2SPacket(command));
                }
            }
            lastTpaTime = currentTime;
        }
    }
}
