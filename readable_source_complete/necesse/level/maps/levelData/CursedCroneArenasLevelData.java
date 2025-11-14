/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.gameAreaSearch.TiledRegionSearch;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.TheCursedCroneMob;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.levelData.RegionLevelDataComponent;
import necesse.level.maps.levelData.SingleRegionBasedLevelData;
import necesse.level.maps.regionSystem.Region;

public class CursedCroneArenasLevelData
extends SingleRegionBasedLevelData<ArenaData>
implements RegionLevelDataComponent {
    @Override
    protected void addRegionSaveData(SaveData save, ArenaData data) {
        data.addSaveData(save);
    }

    @Override
    protected ArenaData loadRegionData(Region region, LoadData save) {
        return CursedCroneArenasLevelData.loadArenaData(save);
    }

    public ArenaData addArena(int tileX, int tileY) {
        ArenaData arena = new ArenaData(tileX, tileY);
        this.addArena(arena);
        return arena;
    }

    protected void addArena(ArenaData arena) {
        this.setDataInRegion(arena.regionX, arena.regionY, arena);
    }

    public boolean hasAnySpawnedCrone() {
        int mobID = MobRegistry.getMobID("thecursedcrone");
        for (ArenaData arena : this.getAllData()) {
            Mob found = this.level.entityManager.mobs.streamInRegionsInTileRange(arena.tileX * 32 + 16, arena.tileY * 32 + 16, 30).filter(mob -> mob.getID() == mobID).findFirst().orElse(null);
            if (found == null) continue;
            return true;
        }
        return false;
    }

    public ArenaData getArena(int tileX, int tileY) {
        int regionX = GameMath.getRegionCoordByTile(tileX);
        int regionY = GameMath.getRegionCoordByTile(tileY);
        return (ArenaData)this.getDataInRegion(regionX, regionY);
    }

    public boolean isArenaTile(int tileX, int tileY) {
        ArenaData arena = this.getArena(tileX, tileY);
        if (arena == null) {
            return false;
        }
        return arena.tileX == tileX && arena.tileY == tileY;
    }

    public ArenaData getClosestArena(int tileX, int tileY, int maxTileRange) {
        ArenaData arena = new TiledRegionSearch(this.level, tileX, tileY, maxTileRange).stream().map(v -> (ArenaData)this.getDataInRegion(v.x, v.y)).filter(Objects::nonNull).findBestDistance(1, Comparator.comparingDouble(p -> GameMath.diagonalMoveDistance(p.tileX, p.tileY, tileX, tileY))).orElse(null);
        if (arena != null && GameMath.getExactDistance(tileX, tileY, arena.tileX, arena.tileY) > (float)maxTileRange) {
            return null;
        }
        return arena;
    }

    public static CursedCroneArenasLevelData getCursedCroneArenasData(Level level, boolean createIfNoneExists) {
        LevelData cursedCroneData = level.getLevelData("cursedcronearenatiles");
        if (cursedCroneData instanceof CursedCroneArenasLevelData) {
            return (CursedCroneArenasLevelData)cursedCroneData;
        }
        if (!createIfNoneExists) {
            return null;
        }
        CursedCroneArenasLevelData newCursedCroneData = new CursedCroneArenasLevelData();
        level.addLevelData("cursedcronearenatiles", newCursedCroneData);
        return newCursedCroneData;
    }

    public static GameMessage startFight(Level level, int tileX, int tileY) {
        ArenaData closestArena;
        CursedCroneArenasLevelData arenas = CursedCroneArenasLevelData.getCursedCroneArenasData(level, false);
        if (arenas != null && (closestArena = arenas.getClosestArena(tileX, tileY, 10)) != null) {
            Mob mob = MobRegistry.getMob("thecursedcrone", level);
            TheCursedCroneMob cursedCroneMob = (TheCursedCroneMob)mob;
            cursedCroneMob.setArenaData(closestArena);
            cursedCroneMob.resetPositionToArena();
            cursedCroneMob.startAnimation(TheCursedCroneMob.AnimationState.DEFAULT_FLYING, 500, true);
            cursedCroneMob.isHostile = true;
            level.entityManager.mobs.add(mob);
            level.getServer().network.sendToClientsWithEntity(new PacketChatMessage(new LocalMessage("misc", "bossawoke", "name", mob.getLocalization())), mob);
            if (level instanceof IncursionLevel) {
                ((IncursionLevel)level).onBossSummoned(mob);
            }
            return null;
        }
        return new LocalMessage("itemtooltip", "spiriturnfail");
    }

    public static ArenaData loadArenaData(LoadData save) {
        Point centerTile = save.getPoint("centerTile", null);
        if (centerTile == null) {
            throw new LoadDataException("ArenaData centerTile is null");
        }
        ArenaData data = new ArenaData(centerTile.x, centerTile.y);
        data.spiritSkullTiles.addAll(save.getPointCollection("spiritSkullTiles", new ArrayList<Point>()));
        return data;
    }

    public static class ArenaData
    extends SingleRegionBasedLevelData.RegionData {
        public final int tileX;
        public final int tileY;
        public final ArrayList<Point> spiritSkullTiles = new ArrayList();

        protected ArenaData(int tileX, int tileY) {
            super(GameMath.getRegionCoordByTile(tileX), GameMath.getRegionCoordByTile(tileY));
            this.tileX = tileX;
            this.tileY = tileY;
        }

        protected SaveData getSaveData(String name) {
            SaveData save = new SaveData(name);
            this.addSaveData(save);
            return save;
        }

        protected void addSaveData(SaveData save) {
            save.addPoint("centerTile", new Point(this.tileX, this.tileY));
            save.addPointCollection("spiritSkullTiles", this.spiritSkullTiles);
        }
    }
}

