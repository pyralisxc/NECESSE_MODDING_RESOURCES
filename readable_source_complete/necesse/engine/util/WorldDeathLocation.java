/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import necesse.engine.playerStats.PlayerStats;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;

public class WorldDeathLocation {
    public final int deathTime;
    public final LevelIdentifier levelIdentifier;
    public final int x;
    public final int y;

    public WorldDeathLocation(PlayerStats characterStats, LevelIdentifier levelIdentifier, int x, int y) {
        this.deathTime = characterStats.time_played.get();
        this.levelIdentifier = levelIdentifier;
        this.x = x;
        this.y = y;
    }

    public int getSecondsSince(PlayerStats stats) {
        return stats.time_played.get() - this.deathTime;
    }

    public WorldDeathLocation(LoadData save) {
        LevelIdentifier levelIdentifier;
        this.deathTime = save.getInt("time");
        try {
            levelIdentifier = new LevelIdentifier(save.getUnsafeString("level", null, false));
        }
        catch (InvalidLevelIdentifierException e) {
            int islandX = save.getInt("islandX");
            int islandY = save.getInt("islandY");
            int dimension = save.getInt("dimension");
            levelIdentifier = new LevelIdentifier(islandX, islandY, dimension);
        }
        this.levelIdentifier = levelIdentifier;
        this.x = save.getInt("x");
        this.y = save.getInt("y");
    }

    public void addSaveData(SaveData save) {
        save.addInt("time", this.deathTime);
        save.addUnsafeString("level", this.levelIdentifier.stringID);
        save.addInt("x", this.x);
        save.addInt("y", this.y);
    }
}

