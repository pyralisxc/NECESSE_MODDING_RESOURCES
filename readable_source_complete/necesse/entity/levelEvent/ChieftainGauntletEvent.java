/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.engine.CameraShake;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.ArenaEntrancePortalMob;
import necesse.entity.mobs.hostile.bosses.ChieftainGauntletSpawnerPortalMob;
import necesse.entity.mobs.hostile.bosses.ChieftainMob;
import necesse.entity.mobs.hostile.theRunebound.CroneMob;
import necesse.entity.objectEntity.ChieftainBoneSpikeWallObjectEntity;
import necesse.entity.objectEntity.ChieftainsPedestalObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class ChieftainGauntletEvent
extends LevelEvent {
    public long startTime;
    public int tileX;
    public int tileY;
    protected ArrayList<Point> wallTiles = new ArrayList();
    protected ArrayList<Point> enemyPortalTiles = new ArrayList();
    protected ArrayList<Point> teleportPortalTiles = new ArrayList();
    protected Point chieftainSpawnPos;
    protected Point croneSpawnPos;
    protected ArrayList<ArenaEntrancePortalMob> spawnedTeleportPortals = new ArrayList();
    protected int lastSpawnPortalIndex;
    protected ArrayList<ChieftainGauntletSpawnerPortalMob> spawnedEnemyPortals = new ArrayList();
    protected ArrayList<Mob> spawnedMobs = new ArrayList();
    protected ChieftainMob chieftainMob;
    protected Mob croneMob;
    protected int playerCheckTimer;
    protected int currentPlayers;
    protected int noPlayersBuffer;
    protected float currentScaling = 1.0f;
    protected ArrayList<Phase> phases = new ArrayList();
    protected SoundPlayer rumble;

    public ChieftainGauntletEvent() {
    }

    public ChieftainGauntletEvent(int tileX, int tileY, ArrayList<Point> wallTiles, ArrayList<Point> enemyPortalTiles, ArrayList<Point> teleportPortalTiles, Point chieftainSpawnPos, ChieftainMob chieftainMob, Point croneSpawnPos, CroneMob croneMob) {
        this();
        this.tileX = tileX;
        this.tileY = tileY;
        this.wallTiles = wallTiles;
        this.enemyPortalTiles = enemyPortalTiles;
        this.teleportPortalTiles = teleportPortalTiles;
        this.chieftainSpawnPos = chieftainSpawnPos;
        this.chieftainMob = chieftainMob;
        this.croneSpawnPos = croneSpawnPos;
        this.croneMob = croneMob;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextShortUnsigned(this.wallTiles.size());
        for (Point wallTile : this.wallTiles) {
            writer.putNextInt(wallTile.x);
            writer.putNextInt(wallTile.y);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        int wallTilesSize = reader.getNextShortUnsigned();
        for (int i = 0; i < wallTilesSize; ++i) {
            this.wallTiles.add(new Point(reader.getNextInt(), reader.getNextInt()));
        }
    }

    @Override
    public void init() {
        super.init();
        this.startTime = this.level.getTime();
        if (this.isClient()) {
            CameraShake cameraShake = this.level.getClient().startCameraShake(this.tileX * 32 + 16, (float)(this.tileY * 32 + 16), ChieftainBoneSpikeWallObjectEntity.ANIMATION_TIME, 40, 2.0f, 2.0f, true);
            cameraShake.minDistance = 200;
            cameraShake.listenDistance = 2000;
        }
        this.spawnWalls();
        if (this.isServer()) {
            this.phases.clear();
            this.phases.add(new TimePassedPhase(ChieftainBoneSpikeWallObjectEntity.ANIMATION_TIME));
            this.phases.add(new RunPhasesPhase(){

                @Override
                public void run() {
                    Mob mob;
                    for (Point tile : ChieftainGauntletEvent.this.enemyPortalTiles) {
                        mob = new ChieftainGauntletSpawnerPortalMob();
                        mob.onSpawned(tile.x * 32 + 16, tile.y * 32 + 16);
                        ChieftainGauntletEvent.this.level.entityManager.mobs.add(mob);
                        ChieftainGauntletEvent.this.spawnedEnemyPortals.add((ChieftainGauntletSpawnerPortalMob)mob);
                    }
                    for (Point tile : ChieftainGauntletEvent.this.teleportPortalTiles) {
                        mob = new ArenaEntrancePortalMob();
                        mob.onSpawned(tile.x * 32 + 16, tile.y * 32 + 16);
                        Point2D.Float portalDir = GameMath.normalize(tile.x - ChieftainGauntletEvent.this.tileX, tile.y - ChieftainGauntletEvent.this.tileY);
                        ((ArenaEntrancePortalMob)mob).targetPos = new Point(ChieftainGauntletEvent.this.tileX * 32 + 16 + (int)(portalDir.x * 4.0f * 32.0f), ChieftainGauntletEvent.this.tileY * 32 + 16 + (int)(portalDir.y * 4.0f * 32.0f));
                        ChieftainGauntletEvent.this.level.entityManager.mobs.add(mob);
                        ChieftainGauntletEvent.this.spawnedTeleportPortals.add((ArenaEntrancePortalMob)mob);
                    }
                }
            });
            this.phases.add(new TimePassedPhase(4000L));
            this.phases.add(new RunPhasesPhase(){

                @Override
                public void run() {
                    ChieftainGauntletEvent.this.chieftainMob.shoutAbility.runAndSend(1500);
                }
            });
            this.phases.add(new SpawnMobsPhase().addMobs("runeboundbrute", "runeboundshaman"));
            this.phases.add(new TimePassedPhase(3000L));
            this.phases.add(new SpawnMobsPhase().addMobs("runeboundbrute", "runeboundtrapper"));
            this.phases.add(new TimePassedPhase(3000L));
            this.phases.add(new SpawnMobsPhase().addMobs("runeboundshaman", "runeboundtrapper"));
            this.phases.add(new TimePassedPhase(8000L));
            this.phases.add(new SpawnMobsPhase().addMobs("runeboundbrute", "runeboundbrute", "runeboundbrute"));
            this.phases.add(new TimePassedPhase(2000L));
            this.phases.add(new SpawnMobsPhase().addMobs("runeboundtrapper", "runeboundshaman"));
            this.phases.add(new WaitForRemovedMobs());
            this.phases.add(new TimePassedPhase(2000L));
            this.phases.add(new RunPhasesPhase(){

                @Override
                public void run() {
                    ChieftainGauntletEvent.this.chieftainMob.shoutAbility.runAndSend(1500);
                }
            });
            this.phases.add(new TimePassedPhase(4000L));
            this.phases.add(new SpawnMobsPhase().addMobs("runeboundbrute", "runeboundshaman"));
            this.phases.add(new TimePassedPhase(3000L));
            this.phases.add(new SpawnMobsPhase().addMobs("runeboundbrute", "runeboundtrapper"));
            this.phases.add(new TimePassedPhase(3000L));
            this.phases.add(new SpawnMobsPhase().addMobs("runeboundshaman", "runeboundtrapper"));
            this.phases.add(new TimePassedPhase(5000L));
            this.phases.add(new SpawnMobsPhase().addMobs("runeboundbrute", "runeboundbrute"));
            this.phases.add(new TimePassedPhase(8000L));
            this.phases.add(new SpawnMobsPhase().addMobs("runeboundshaman", "runeboundtrapper"));
            this.phases.add(new TimePassedPhase(5000L));
            this.phases.add(new SpawnMobsPhase().addMobs("runeboundbrute", "runeboundbrute", "runeboundbrute", "runeboundbrute"));
            this.phases.add(new WaitForRemovedMobs());
            this.phases.add(new TimePassedPhase(2000L));
            this.phases.add(new RunPhasesPhase(){

                @Override
                public void run() {
                    for (ChieftainGauntletSpawnerPortalMob portal : ChieftainGauntletEvent.this.spawnedEnemyPortals) {
                        portal.remove();
                    }
                    ChieftainGauntletEvent.this.spawnedEnemyPortals.clear();
                }
            });
            this.phases.add(new RunPhasesPhase(){

                @Override
                public void run() {
                    if (ChieftainGauntletEvent.this.isServer()) {
                        ChieftainGauntletEvent.this.getServer().network.sendToClientsWithTile(new PacketChatMessage(new LocalMessage("misc", "bossawoke", "name", ChieftainGauntletEvent.this.chieftainMob.getLocalization())), ChieftainGauntletEvent.this.level, ChieftainGauntletEvent.this.tileX, ChieftainGauntletEvent.this.tileY);
                    }
                    ChieftainGauntletEvent.this.chieftainMob.startJumpChargeUpAbility.runAndSend();
                }
            });
            this.phases.add(new TimePassedPhase(500L));
            this.phases.add(new RunPhasesPhase(){

                @Override
                public void run() {
                    ChieftainGauntletEvent.this.chieftainMob.jumpAbility.runAndSend(ChieftainGauntletEvent.this.tileX * 32 + 16, ChieftainGauntletEvent.this.tileY * 32 + 16);
                    ChieftainGauntletEvent.this.chieftainMob.arenaCenterPos = new Point(ChieftainGauntletEvent.this.tileX * 32 + 16, ChieftainGauntletEvent.this.tileY * 32 + 16);
                }
            });
            this.phases.add(new TimePassedPhase(5000L));
            this.phases.add(new Phase(){

                @Override
                public void onEndedPrematurely() {
                    if (!ChieftainGauntletEvent.this.chieftainMob.removed()) {
                        ChieftainGauntletEvent.this.chieftainMob.setHostileTeleportAbility.makeHostile(false);
                    }
                }

                @Override
                public boolean isComplete() {
                    return ChieftainGauntletEvent.this.chieftainMob.removed() || !ChieftainGauntletEvent.this.chieftainMob.isHostile;
                }
            });
            this.phases.add(new TimePassedPhase(5000L));
            this.phases.add(new RunPhasesPhase(){

                @Override
                public void run() {
                    if (ChieftainGauntletEvent.this.croneMob != null) {
                        ChieftainGauntletEvent.this.croneMob.remove(0.0f, 0.0f, null, true);
                    }
                }
            });
            this.phases.get(0).onStarted();
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.isOver()) {
            return;
        }
        for (ChieftainGauntletSpawnerPortalMob chieftainGauntletSpawnerPortalMob : this.spawnedEnemyPortals) {
            chieftainGauntletSpawnerPortalMob.keepAlive();
        }
        for (ArenaEntrancePortalMob arenaEntrancePortalMob : this.spawnedTeleportPortals) {
            arenaEntrancePortalMob.keepAlive();
        }
        --this.playerCheckTimer;
        if (this.playerCheckTimer <= 0) {
            this.playerCheckTimer = 20;
            int lastPlayers = this.currentPlayers;
            List list = this.level.entityManager.players.streamInRegionsInTileRange(this.tileX * 32 + 16, this.tileY * 32 + 16, 20).filter(p -> {
                if (p.removed()) {
                    return false;
                }
                return this.getLevel().regionManager.BASIC_DOOR_OPTIONS.canMoveToTile(this.tileX, this.tileY + 1, p.getTileX(), p.getTileY(), true);
            }).collect(Collectors.toList());
            this.currentPlayers = list.size();
            if (lastPlayers != this.currentPlayers) {
                int partyMembers = list.stream().filter(PlayerMob::isServerClient).mapToInt(p -> p.getServerClient().adventureParty.getSize()).sum();
                this.currentScaling = GameUtils.getMultiplayerScaling(this.currentPlayers, 0.8f, 0.04f) * GameUtils.getMultiplayerScaling(partyMembers + 1, Integer.MAX_VALUE, 0.2f, 0.02f);
            }
            if (this.currentPlayers == 0) {
                ++this.noPlayersBuffer;
                if (this.noPlayersBuffer >= 5) {
                    for (Phase phase : this.phases) {
                        phase.onEndedPrematurely();
                    }
                    this.over();
                    return;
                }
            } else {
                this.noPlayersBuffer = 0;
            }
        }
        boolean breakIfNotComplete = false;
        while (!this.phases.isEmpty()) {
            Phase phase = this.phases.get(0);
            if (phase.isComplete()) {
                this.phases.remove(0);
                if (!this.phases.isEmpty()) {
                    this.phases.get(0).onStarted();
                }
                breakIfNotComplete = false;
                continue;
            }
            if (breakIfNotComplete) break;
            phase.serverTick();
            breakIfNotComplete = true;
        }
        if (this.phases.isEmpty()) {
            this.over();
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        long timeProgress = this.level.getTime() - this.startTime;
        SoundManager.setMusic(MusicRegistry.TheRuneboundTrialPart1, SoundManager.MusicPriority.EVENT, 1.5f);
        BossNearbyBuff.applyAround(this.level, this.tileX * 32 + 16, this.tileY * 32 + 16, 640);
        for (Point p : Level.adjacentGettersWithCenter) {
            this.level.lightManager.refreshParticleLight(this.tileX + p.x, this.tileY + p.y, 166.0f, 0.7f, 255);
        }
        if (timeProgress < (long)ChieftainBoneSpikeWallObjectEntity.ANIMATION_TIME) {
            if (this.rumble == null || this.rumble.isDone()) {
                this.rumble = SoundManager.playSound(GameResources.rumble, (SoundEffect)SoundEffect.effect(this.tileX * 32 + 16, this.tileY * 32 + 16).volume(4.0f).falloffDistance(2000));
            }
            if (this.rumble != null) {
                this.rumble.refreshLooping(2.0f);
            }
        }
    }

    public Mob getNextSpawnPortal() {
        if (!this.spawnedEnemyPortals.isEmpty()) {
            if (this.lastSpawnPortalIndex > this.spawnedEnemyPortals.size() - 1) {
                Collections.shuffle(this.spawnedEnemyPortals);
                this.lastSpawnPortalIndex = 0;
            }
            Mob portal = this.spawnedEnemyPortals.get(this.lastSpawnPortalIndex);
            ++this.lastSpawnPortalIndex;
            return portal;
        }
        return null;
    }

    public void spawnWalls() {
        int boneSpikeWall = ObjectRegistry.getObjectID("chieftainbonespikewall");
        for (Point wallTile : this.wallTiles) {
            this.getLevel().setObject(wallTile.x, wallTile.y, boneSpikeWall);
            ChieftainBoneSpikeWallObjectEntity objectEntity = this.level.entityManager.getObjectEntity(wallTile.x, wallTile.y, ChieftainBoneSpikeWallObjectEntity.class);
            if (objectEntity == null) continue;
            objectEntity.startAnimation(true);
        }
        ChieftainsPedestalObjectEntity pedestalEntity = this.level.entityManager.getObjectEntity(this.tileX, this.tileY, ChieftainsPedestalObjectEntity.class);
        if (pedestalEntity != null) {
            pedestalEntity.startAnimation(false);
        }
    }

    public void resetEvent() {
        this.level.setObject(this.tileX, this.tileY, ObjectRegistry.getObjectID("chieftainspedestal"));
        ChieftainsPedestalObjectEntity pedestalEntity = this.level.entityManager.getObjectEntity(this.tileX, this.tileY, ChieftainsPedestalObjectEntity.class);
        if (pedestalEntity != null) {
            if (this.isServer()) {
                pedestalEntity.wallTiles = this.wallTiles;
                pedestalEntity.enemyPortalTiles = this.enemyPortalTiles;
                pedestalEntity.teleportPortalTiles = this.teleportPortalTiles;
                pedestalEntity.chieftainSpawnPos = this.chieftainSpawnPos;
                pedestalEntity.chieftainMobID = this.chieftainMob.getUniqueID();
                pedestalEntity.croneSpawnPos = this.croneSpawnPos;
                pedestalEntity.croneMobID = this.croneMob == null ? -1 : this.croneMob.getUniqueID();
            }
            pedestalEntity.startAnimation(true);
        }
        for (Point point : this.wallTiles) {
            ChieftainBoneSpikeWallObjectEntity wallEntity = this.level.entityManager.getObjectEntity(point.x, point.y, ChieftainBoneSpikeWallObjectEntity.class);
            if (wallEntity == null) continue;
            wallEntity.startAnimation(false);
        }
        for (Mob mob : this.spawnedTeleportPortals) {
            mob.remove();
        }
        for (Mob mob : this.spawnedEnemyPortals) {
            mob.remove();
        }
        for (Mob mob : this.spawnedMobs) {
            mob.remove();
        }
    }

    @Override
    public void onUnloading(Region region) {
        super.onUnloading(region);
        this.resetEvent();
    }

    @Override
    public void onDispose() {
        super.onDispose();
        this.resetEvent();
    }

    @Override
    public Point getSaveToRegionPos() {
        return new Point(this.level.regionManager.getRegionCoordByTile(this.tileX), this.level.regionManager.getRegionCoordByTile(this.tileY));
    }

    protected class TimePassedPhase
    extends Phase {
        public long startTime;
        public long duration;

        public TimePassedPhase(long duration) {
            this.duration = duration;
        }

        @Override
        public void onStarted() {
            this.startTime = ChieftainGauntletEvent.this.getTime();
        }

        @Override
        public boolean isComplete() {
            return ChieftainGauntletEvent.this.getTime() - this.startTime >= this.duration;
        }
    }

    protected class SpawnMobsPhase
    extends Phase {
        public ArrayList<Mob> spawnedMobs;
        public ArrayList<Supplier<Mob>> mobConstructors = new ArrayList();

        public SpawnMobsPhase(ArrayList<Mob> spawnedMobs) {
            this.spawnedMobs = spawnedMobs;
        }

        public SpawnMobsPhase() {
            this(this$0.spawnedMobs);
        }

        public SpawnMobsPhase addMob(Supplier<Mob> mobConstructor) {
            this.mobConstructors.add(mobConstructor);
            return this;
        }

        public SpawnMobsPhase addMobs(String ... mobStringIDs) {
            for (String mobStringID : mobStringIDs) {
                this.addMob(() -> MobRegistry.getMob(mobStringID, ChieftainGauntletEvent.this.level));
            }
            return this;
        }

        @Override
        public void onStarted() {
            float totalMobsFloat = (float)this.mobConstructors.size() * ChieftainGauntletEvent.this.currentScaling;
            int totalMobs = (int)totalMobsFloat;
            if (GameRandom.globalRandom.nextFloat() < totalMobsFloat - (float)totalMobs) {
                ++totalMobs;
            }
            for (int i = 0; i < totalMobs; ++i) {
                int listIndex = i % this.mobConstructors.size();
                if (listIndex == 0 && i != 0) {
                    Collections.shuffle(this.mobConstructors);
                }
                Mob mob = this.mobConstructors.get(listIndex).get();
                Mob spawnPortal = ChieftainGauntletEvent.this.getNextSpawnPortal();
                if (spawnPortal != null) {
                    mob.onSpawned(spawnPortal.getX() + GameRandom.globalRandom.getIntBetween(-5, 5), spawnPortal.getY() + GameRandom.globalRandom.getIntBetween(-5, 5));
                } else {
                    Point position = PortalObjectEntity.getTeleportDestinationAroundObject(ChieftainGauntletEvent.this.level, mob, ChieftainGauntletEvent.this.tileX, ChieftainGauntletEvent.this.tileY, true);
                    mob.onSpawned(position.x, position.y);
                }
                mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.QUADRUPLE_CHASE_RANGE, mob, 3600.0f, null), false);
                ChieftainGauntletEvent.this.getLevel().entityManager.mobs.add(mob);
                if (this.spawnedMobs == null) continue;
                this.spawnedMobs.add(mob);
            }
        }

        @Override
        public boolean isComplete() {
            return true;
        }
    }

    protected class WaitForRemovedMobs
    extends Phase {
        public ArrayList<Mob> mobs;

        public WaitForRemovedMobs(ArrayList<Mob> mobs) {
            this.mobs = mobs;
        }

        public WaitForRemovedMobs() {
            this(this$0.spawnedMobs);
        }

        @Override
        public void onEndedPrematurely() {
            for (Mob mob : this.mobs) {
                mob.remove();
            }
            this.mobs.clear();
        }

        @Override
        public void serverTick() {
            for (int i = 0; i < this.mobs.size(); ++i) {
                Mob mob = this.mobs.get(i);
                if (!mob.removed()) continue;
                this.mobs.remove(i);
                --i;
            }
        }

        @Override
        public boolean isComplete() {
            return this.mobs.isEmpty();
        }
    }

    protected static abstract class Phase {
        protected Phase() {
        }

        public void onStarted() {
        }

        public void serverTick() {
        }

        public void onCompleted() {
        }

        public void onEndedPrematurely() {
        }

        public abstract boolean isComplete();
    }

    protected static class ParallelPhasesPhase
    extends Phase {
        public ArrayList<Phase> phases = new ArrayList();

        @Override
        public void onStarted() {
            for (int i = 0; i < this.phases.size(); ++i) {
                Phase phase = this.phases.get(i);
                phase.onStarted();
                if (!phase.isComplete()) continue;
                phase.onCompleted();
                this.phases.remove(i);
                --i;
            }
        }

        @Override
        public void serverTick() {
            for (int i = 0; i < this.phases.size(); ++i) {
                Phase phase = this.phases.get(i);
                phase.serverTick();
                if (!phase.isComplete()) continue;
                phase.onCompleted();
                this.phases.remove(i);
                --i;
            }
        }

        @Override
        public boolean isComplete() {
            return this.phases.isEmpty();
        }
    }

    protected static abstract class RunPhasesPhase
    extends Phase {
        protected RunPhasesPhase() {
        }

        @Override
        public void onStarted() {
            this.run();
        }

        public abstract void run();

        @Override
        public boolean isComplete() {
            return true;
        }
    }
}

