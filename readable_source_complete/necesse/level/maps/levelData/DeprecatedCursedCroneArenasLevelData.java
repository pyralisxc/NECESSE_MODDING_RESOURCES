/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.GameLog;
import necesse.engine.save.LoadData;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.OneWorldMigration;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.CursedCroneArenasLevelData;
import necesse.level.maps.levelData.LevelData;

@Deprecated
public class DeprecatedCursedCroneArenasLevelData
extends LevelData {
    public ArrayList<CursedCroneArenasLevelData.ArenaData> loadedArenas = new ArrayList();

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        LoadData arenasSave = save.getFirstLoadDataByName("ARENAS");
        if (arenasSave != null) {
            for (LoadData arenaSave : arenasSave.getLoadDataByName("ARENA")) {
                try {
                    CursedCroneArenasLevelData.ArenaData arenaData = CursedCroneArenasLevelData.loadArenaData(arenaSave);
                    this.loadedArenas.add(arenaData);
                }
                catch (Exception e) {
                    GameLog.warn.println("Could not load some cursed crone arena data");
                }
            }
        }
    }

    @Override
    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Level newLevel, Point tileOffset, Point positionOffset) {
        super.migrateToOneWorld(migrationData, oldLevelIdentifier, newLevel, tileOffset, positionOffset);
        if (!this.loadedArenas.isEmpty()) {
            CursedCroneArenasLevelData newArenas = CursedCroneArenasLevelData.getCursedCroneArenasData(newLevel, true);
            for (CursedCroneArenasLevelData.ArenaData arena : this.loadedArenas) {
                CursedCroneArenasLevelData.ArenaData newArena = newArenas.addArena(arena.tileX + tileOffset.x, arena.tileY + tileOffset.y);
                for (Point spiritSkullTile : arena.spiritSkullTiles) {
                    newArena.spiritSkullTiles.add(new Point(spiritSkullTile.x + tileOffset.x, spiritSkullTile.y + tileOffset.y));
                }
            }
        }
    }
}

