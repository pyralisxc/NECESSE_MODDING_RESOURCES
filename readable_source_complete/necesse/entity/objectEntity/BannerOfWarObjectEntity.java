/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.manager.EntityManager;
import necesse.entity.mobs.BannerOfWarDummyMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.BannerObjectEntity;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.MobChance;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class BannerOfWarObjectEntity
extends BannerObjectEntity {
    public static float BANNER_SPAWN_RATE_MODIFIER = 1.0f;
    public static float BANNER_SPAWN_CAP_MODIFIER = 2.0f;
    public float nextMobSpawn;
    private int dummyMobID = -1;
    protected TicksPerSecond checkInvalidMobs = TicksPerSecond.ticksPerSecond(1);
    private final ArrayList<Mob> spawnedMobs = new ArrayList();
    private HashSet<Integer> loadedSpawnedMobs;

    public BannerOfWarObjectEntity(Level level, int x, int y) {
        super(level, "bannerofwar", x, y);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (!this.spawnedMobs.isEmpty()) {
            save.addIntCollection("spawnedMobs", this.spawnedMobs.stream().map(Entity::getUniqueID).collect(Collectors.toList()));
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.loadedSpawnedMobs = new HashSet<Integer>(save.getIntCollection("spawnedMobs", new ArrayList<Integer>(), false));
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        if (this.dummyMobID == -1) {
            this.generateMobID();
        }
        writer.putNextInt(this.dummyMobID);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.dummyMobID = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void onLoadingComplete() {
        super.onLoadingComplete();
        if (this.loadedSpawnedMobs != null) {
            for (int mobUniqueID : this.loadedSpawnedMobs) {
                Mob mob = this.getLevel().entityManager.mobs.get(mobUniqueID, false);
                if (mob == null) continue;
                this.spawnedMobs.add(mob);
            }
            this.loadedSpawnedMobs = null;
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        BannerOfWarDummyMob m = this.getMob();
        if (m != null) {
            m.keepAlive(this);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        BannerOfWarDummyMob m = this.getMob();
        if (m == null) {
            m = this.generateMobID();
            this.markDirty();
        }
        m.keepAlive(this);
        this.checkInvalidMobs.tick(50.0f);
        if (this.checkInvalidMobs.shouldTick()) {
            for (int i = 0; i < this.spawnedMobs.size(); ++i) {
                Mob mob = this.spawnedMobs.get(i);
                if (!mob.removed()) continue;
                this.spawnedMobs.remove(i);
                --i;
            }
        }
        this.tickMobSpawns();
    }

    private BannerOfWarDummyMob generateMobID() {
        BannerOfWarDummyMob lastMob = this.getMob();
        if (lastMob != null) {
            lastMob.remove();
        }
        BannerOfWarDummyMob m = new BannerOfWarDummyMob();
        this.getLevel().entityManager.addMob(m, this.tileX * 32 + 16, this.tileY * 32 + 16);
        this.dummyMobID = m.getUniqueID();
        return m;
    }

    private BannerOfWarDummyMob getMob() {
        if (this.dummyMobID == -1) {
            return null;
        }
        Mob m = this.getLevel().entityManager.mobs.get(this.dummyMobID, false);
        if (m != null) {
            return (BannerOfWarDummyMob)m;
        }
        return null;
    }

    @Override
    public void remove() {
        super.remove();
        BannerOfWarDummyMob m = this.getMob();
        if (m != null) {
            m.remove();
        }
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        if (debug) {
            GameTooltipManager.addTooltip(new StringTooltips("MobID: " + this.dummyMobID), TooltipLocation.INTERACT_FOCUS);
        }
    }

    public void tickMobSpawns() {
        if (this.getLevel().buffManager.getModifier(LevelModifiers.BANNER_OF_WAR_DISABLED).booleanValue()) {
            return;
        }
        int hostileMobs = -1;
        this.nextMobSpawn += this.getMobSpawnRate();
        while (this.nextMobSpawn >= 1.0f) {
            this.nextMobSpawn -= 1.0f;
            if (hostileMobs == -1) {
                hostileMobs = (int)this.getLevel().entityManager.mobs.streamInRegionsShape(GameUtils.rangeBounds(this.tileX * 32 + 16, this.tileY * 32 + 16, Mob.MOB_SPAWN_AREA.maxSpawnDistance + 320), 0).filter(m -> m.isHostile && m.canDespawn).count();
            }
            if (!((float)hostileMobs < this.getMobSpawnCap())) continue;
            if (!this.attemptSpawnMob()) {
                this.nextMobSpawn += 0.5f;
                continue;
            }
            ++hostileMobs;
        }
    }

    public float getMobSpawnCap() {
        return EntityManager.getSpawnCap(this.getLevel().presentPlayers, 25.0f, 5.0f) * this.getWorldSettings().difficulty.enemySpawnCapModifier * this.getLevel().entityManager.getSpawnCapMod(this.tileX, this.tileY) * BANNER_SPAWN_CAP_MODIFIER;
    }

    public float getMobSpawnRate() {
        return ServerClient.mobSpawnRate * this.getWorldSettings().difficulty.enemySpawnRateModifier * this.getLevel().entityManager.getSpawnRate(this.tileX, this.tileY) * BANNER_SPAWN_RATE_MODIFIER;
    }

    public boolean attemptSpawnMob() {
        MobSpawnTable spawnTable;
        MobChance randomMob;
        Point spawnTile = EntityManager.getMobSpawnTile(this.getLevel(), this.tileX * 32 + 16, this.tileY * 32 + 16, Mob.MOB_SPAWN_AREA, null);
        if (spawnTile != null && (randomMob = (spawnTable = this.getLevel().getBiome(this.tileX, this.tileY).getMobSpawnTable(this.getLevel())).getRandomMob(this.getLevel(), null, spawnTile, GameRandom.globalRandom, "bannerofwar")) != null) {
            return randomMob.spawnMob(this.getLevel(), null, spawnTile, mob -> {
                mob.spawnLightThreshold = new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0).min(140);
                return true;
            }, mob -> {
                mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.BANNER_OF_WAR_MOB, (Mob)mob, 86400000, null), false);
                this.spawnedMobs.add((Mob)mob);
            }, "bannerofwar") != null;
        }
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Mob spawnedMob : this.spawnedMobs) {
            if (spawnedMob.removed()) continue;
            spawnedMob.buffManager.removeBuff(BuffRegistry.BANNER_OF_WAR_MOB, true);
        }
        this.spawnedMobs.clear();
    }
}

