/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.server;

import java.io.File;
import necesse.engine.util.GameRandom;

public class ServerCreationSettings {
    public final File worldFilePath;
    public String worldSeed = ServerCreationSettings.getNewRandomSpawnSeed();
    public boolean spawnGuide = true;

    public static String getNewRandomSpawnSeed(GameRandom random) {
        String charTable = "abcdefghijklmnopqrstuvwyxzABCDEFGHIJKLMNOPQRSTUVWYXZ1234567890";
        StringBuilder seed = new StringBuilder();
        for (int i = 0; i < 5; ++i) {
            seed.append(charTable.charAt(random.nextInt(charTable.length())));
        }
        return seed.toString();
    }

    public static String getNewRandomSpawnSeed() {
        return ServerCreationSettings.getNewRandomSpawnSeed(GameRandom.globalRandom);
    }

    public ServerCreationSettings(File worldFilePath, String worldSeed, boolean spawnGuide) {
        this(worldFilePath);
        this.worldSeed = worldSeed;
        this.spawnGuide = spawnGuide;
    }

    public ServerCreationSettings(File worldFilePath) {
        this.worldFilePath = worldFilePath;
    }
}

