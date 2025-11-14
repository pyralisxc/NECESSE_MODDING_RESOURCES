/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.Function;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.PausableSound;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.TeleportResult;
import necesse.engine.util.TicketSystemList;
import necesse.entity.Entity;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.levelEvent.actions.LevelEventAction;
import necesse.entity.mobs.hostile.bosses.ArenaEntrancePortalMob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.presets.AscendedArenaPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.regionSystem.Region;

public class AscendedBlackHoleEvent
extends LevelEvent {
    public Preset arenaPreset = new AscendedArenaPreset();
    public float inwardSpeed = 20.0f;
    public float circleSpeed = 50.0f;
    public float radiusToStopCounteractMoveSpeed = 500.0f;
    public float radiusToIncreaseInwardSpeed = 800.0f;
    public float inwardSpeedIncreasePerDistance = 0.05f;
    public float maxPlayerDistanceAtEnd = 500.0f;
    protected int tileX;
    protected int tileY;
    protected long spawnTime;
    protected long endTime;
    protected int totalArenaTiles;
    protected LinkedList<ArenaTilePlace> missingArenaTiles;
    protected float nextTileBuffer;
    protected final PlaceArenaLevelEventAction placeArenaAction;
    protected PausableSound blackHoleSound;
    public AscendedWizardMob wizardMob;
    protected TicketSystemList<Integer> tileIDs = new TicketSystemList();

    public AscendedBlackHoleEvent() {
        this.tileIDs.addObject(100, (Object)TileRegistry.ascendedGrowthID);
        this.tileIDs.addObject(50, (Object)TileRegistry.ascendedCorruptionID);
        this.placeArenaAction = this.registerAction(new PlaceArenaLevelEventAction());
    }

    public AscendedBlackHoleEvent(int tileX, int tileY, long endTime) {
        this();
        this.tileX = tileX;
        this.tileY = tileY;
        this.endTime = endTime;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void onUnloading(Region region) {
        super.onUnloading(region);
        if (!this.isClient()) {
            this.doFinalPresetPlace();
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextLong(this.spawnTime);
        writer.putNextLong(this.endTime);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.spawnTime = reader.getNextLong();
        this.endTime = reader.getNextLong();
    }

    @Override
    public void init() {
        super.init();
        if (this.spawnTime == 0L) {
            this.spawnTime = this.getTime();
        }
        if (!this.isClient()) {
            if (this.wizardMob == null) {
                this.wizardMob = new AscendedWizardMob();
                this.wizardMob.setLevel(this.level);
                this.wizardMob.onSpawned(this.tileX * 32 + 16, (this.tileY - 4) * 32 + 16);
                this.wizardMob.setSpawnTilePosition(this.tileX, this.tileY);
                this.wizardMob.startBlackHolePhase();
                this.level.entityManager.mobs.add(this.wizardMob);
            }
            this.missingArenaTiles = new LinkedList();
            for (int presetX = 0; presetX < this.arenaPreset.width; ++presetX) {
                int tileX = this.tileX + presetX - this.arenaPreset.width / 2;
                for (int presetY = 0; presetY < this.arenaPreset.height; ++presetY) {
                    int tileY = this.tileY + presetY - this.arenaPreset.height / 2;
                    int tileID = this.arenaPreset.getTile(presetX, presetY);
                    int objectID = this.arenaPreset.getObject(presetX, presetY);
                    if (tileID == -1 && objectID == -1) continue;
                    this.missingArenaTiles.add(new ArenaTilePlace(tileX, tileY, tileID, objectID));
                }
            }
            this.totalArenaTiles = this.missingArenaTiles.size();
            Collections.shuffle(this.missingArenaTiles, GameRandom.globalRandom);
        }
        if (!this.isServer()) {
            this.blackHoleSound = new PausableSound(GameResources.blackholeBegin, SoundEffect.globalEffect().volume(2.0f));
        }
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        long timeSinceSpawned = this.getTime() - this.spawnTime;
        float force = Math.max(timeSinceSpawned <= 1000L ? (float)timeSinceSpawned / 1000.0f : 1.0f, 0.0f);
        int centerX = this.tileX * 32 + 16;
        int centerY = this.tileY * 32 + 16;
        GameUtils.streamNetworkClients(this.getLevel()).filter(c -> !c.isDead()).map(c -> c.playerMob).filter(p -> p != null && !p.removed()).forEach(p -> {
            float currentSpeed;
            float centerDistance = this.getDistanceToCenter(p.x, p.y);
            Point2D.Float centerDir = GameMath.normalize((float)centerX - p.x, (float)centerY - p.y);
            float centerAngle = GameMath.getAngle(centerDir);
            if (p.dx != 0.0f || p.dy != 0.0f) {
                currentSpeed = (float)Math.sqrt(p.dx * p.dx + p.dy * p.dy);
                Point2D.Float currentSpeedDir = GameMath.normalize(p.dx, p.dy);
                float speedAngle = GameMath.getAngle(currentSpeedDir);
                float angleDifference = Math.abs(GameMath.getAngleDifference(speedAngle, centerAngle));
                if (angleDifference > 90.0f) {
                    if (centerDistance < this.radiusToStopCounteractMoveSpeed) {
                        float differenceFactor = (angleDifference - 90.0f) / 90.0f;
                        currentSpeed *= differenceFactor;
                    }
                } else {
                    currentSpeed = 0.0f;
                }
            } else {
                currentSpeed = 0.0f;
            }
            float inwardSpeed = this.getInwardSpeed(centerDistance, this.maxPlayerDistanceAtEnd) + currentSpeed;
            Point2D.Float perpDir = GameMath.getPerpendicularDir(centerDir.x, centerDir.y);
            float inwardMove = inwardSpeed * force * delta / 250.0f;
            float circleMove = this.circleSpeed * force * delta / 250.0f;
            p.x += perpDir.x * circleMove;
            p.y += perpDir.y * circleMove;
            p.x += centerDir.x * inwardMove;
            p.y += centerDir.y * inwardMove;
            if (this.isServer()) {
                ArrayList<LevelObjectHit> collisions = this.getLevel().getCollisions(p.getCollision(), p.getLevelCollisionFilter());
                PointHashSet damagedTiles = new PointHashSet();
                for (LevelObjectHit collision : collisions) {
                    if (damagedTiles.contains(collision.tileX, collision.tileY) || this.level.isProtected(collision.tileX, collision.tileY)) continue;
                    GameObject object = collision.getObject();
                    if (object.toolType != ToolType.UNBREAKABLE) {
                        this.getLevel().entityManager.doObjectDamage(0, collision.tileX, collision.tileY, object.objectHealth, Float.MAX_VALUE, null, null, true, collision.tileX * 32 + 16, collision.tileY * 32 + 16);
                    }
                    damagedTiles.add(collision.tileX, collision.tileY);
                }
            }
        });
    }

    @Override
    public void clientTick() {
        super.clientTick();
        long timeToEnd = this.endTime - this.getTime();
        if (timeToEnd <= 0L) {
            this.over();
            return;
        }
        if (this.blackHoleSound != null) {
            this.blackHoleSound = this.blackHoleSound.gameTick();
        }
        GameCamera camera = GlobalData.getCurrentState().getCamera();
        float particlesDivider = 1.0f;
        switch (Settings.particles) {
            case Minimal: {
                particlesDivider = 10.0f;
                break;
            }
            case Decreased: {
                particlesDivider = 4.0f;
            }
        }
        double particlesPerTick = (double)(camera.getWidth() * camera.getHeight()) / 32.0 / 10000.0 / (double)particlesDivider;
        while (particlesPerTick > 0.0 && (!(particlesPerTick < 1.0) || GameRandom.globalRandom.getChance(particlesPerTick))) {
            particlesPerTick -= 1.0;
            int particleHeight = 16;
            final int centerX = this.tileX * 32 + 16;
            final int centerY = this.tileY * 32 + 16 + particleHeight;
            int tileX = GameRandom.globalRandom.getIntBetween(camera.getStartTileX() - 5, camera.getEndTileX() + 5);
            int tileY = GameRandom.globalRandom.getIntBetween(camera.getStartTileY() - 5, camera.getEndTileY() + 5);
            int posX = tileX * 32 + GameRandom.globalRandom.nextInt(32);
            int posY = tileY * 32 + GameRandom.globalRandom.nextInt(32);
            int lifeTime = GameRandom.globalRandom.getIntBetween(4000, 6000);
            float distanceToCenter = GameMath.getExactDistance(posX, posY, centerX, centerY);
            float timeToCenter = Entity.getTravelTimeMillis(this.inwardSpeed, distanceToCenter);
            if ((long)(lifeTime = Math.min(lifeTime, (int)(timeToCenter + 500.0f))) > timeToEnd) {
                lifeTime = (int)timeToEnd;
            }
            this.getLevel().entityManager.addParticle(posX, posY, null).moves(new ParticleOption.Mover(){

                @Override
                public void tick(Point2D.Float pos, float delta, int lifeTime, int timeAlive, float lifePercent) {
                    Point2D.Float centerDir = GameMath.normalize((float)centerX - pos.x, (float)centerY - pos.y);
                    Point2D.Float perpDir = GameMath.getPerpendicularDir(centerDir.x, centerDir.y);
                    float inwardMove = AscendedBlackHoleEvent.this.getInwardSpeed(AscendedBlackHoleEvent.this.getDistanceToCenter(pos.x, pos.y), 0.0f) * delta / 250.0f;
                    float circleMove = AscendedBlackHoleEvent.this.circleSpeed * delta / 250.0f;
                    pos.x += perpDir.x * circleMove;
                    pos.y += perpDir.y * circleMove;
                    pos.x += centerDir.x * inwardMove;
                    pos.y += centerDir.y * inwardMove;
                }
            }).sizeFadesInAndOut(10, 20, 500, 500).fadesAlphaTime(500, 500).colorRandom(300.0f, 0.9f, 0.9f, 5.0f, 0.1f, 0.1f).minDrawLight(100).height(particleHeight).lifeTime(lifeTime);
        }
    }

    public float getDistanceToCenter(float posX, float posY) {
        float centerX = this.tileX * 32 + 16;
        float centerY = this.tileY * 32 + 16;
        return GameMath.getExactDistance(posX, posY, centerX, centerY);
    }

    public float getInwardSpeed(float distanceToCenter, float minDistance) {
        if (distanceToCenter >= this.radiusToIncreaseInwardSpeed) {
            return this.inwardSpeed + (distanceToCenter - this.radiusToIncreaseInwardSpeed) * this.inwardSpeedIncreasePerDistance;
        }
        if (distanceToCenter <= minDistance) {
            float factor = distanceToCenter / minDistance;
            return this.inwardSpeed * GameMath.lerp(factor, -1.0f, 1.0f);
        }
        return this.inwardSpeed;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.tickNextTiles(false);
        if (this.getTime() >= this.endTime) {
            final int centerX = this.tileX * 32 + 16;
            final int centerY = this.tileY * 32 + 16;
            GameUtils.streamNetworkClients(this.getLevel()).filter(c -> !c.isDead() && c.hasSpawned()).map(NetworkClient::getServerClient).forEach(c -> {
                float distanceToCenter = GameMath.getExactDistance(c.playerMob.x, c.playerMob.y, centerX, centerY);
                if (distanceToCenter <= this.maxPlayerDistanceAtEnd) {
                    return;
                }
                final Point2D.Float centerDir = GameMath.normalize(c.playerMob.x - (float)centerX, c.playerMob.y - (float)centerY);
                TeleportEvent event = new TeleportEvent((ServerClient)c, 0, this.getLevel().getIdentifier(), 0.0f, null, new Function<Level, TeleportResult>(){

                    @Override
                    public TeleportResult apply(Level newLevel) {
                        float distance = AscendedBlackHoleEvent.this.maxPlayerDistanceAtEnd * GameRandom.globalRandom.getFloatBetween(0.5f, 1.0f);
                        float targetX = (float)centerX + centerDir.x * distance;
                        float targetY = (float)centerY + centerDir.y * distance;
                        return new TeleportResult(true, new Point((int)targetX, (int)targetY));
                    }
                });
                this.getLevel().entityManager.events.add(event);
            });
            this.over();
            this.tickNextTiles(true);
        }
    }

    protected void tickNextTiles(boolean allRemaining) {
        long totalTime = this.endTime - this.spawnTime;
        float tilesPerSecond = (float)this.totalArenaTiles / ((float)totalTime / 1000.0f);
        this.nextTileBuffer += tilesPerSecond / 20.0f;
        while (this.nextTileBuffer >= 1.0f || allRemaining) {
            this.nextTileBuffer -= 1.0f;
            if (this.missingArenaTiles.isEmpty()) break;
            ArenaTilePlace tile = this.missingArenaTiles.remove(0);
            this.placeArenaAction.runAndSend(tile.tileX, tile.tileY, tile.tileID, tile.objectID);
        }
    }

    protected void spawnArenaPortal(int portalTileX, int portalTileY) {
        ArenaEntrancePortalMob mob = new ArenaEntrancePortalMob();
        mob.onSpawned(portalTileX * 32 + 16, portalTileY * 32 + 16);
        Point2D.Float portalDir = GameMath.normalize(portalTileX - this.tileX, portalTileY - this.tileY);
        mob.targetPos = new Point(this.tileX * 32 + 16 + (int)(portalDir.x * 4.0f * 32.0f), this.tileY * 32 + 16 + (int)(portalDir.y * 4.0f * 32.0f));
        mob.keepAliveAlways = true;
        this.level.entityManager.mobs.add(mob);
    }

    @Override
    public void over() {
        if (!this.isOver()) {
            if (this.isClient()) {
                this.getClient().addShockwaveEffect(this.tileX * 32 + 16, this.tileY * 32 + 16, 2000.0f, 200.0f, 1000.0f, 100.0f, 150);
            }
            this.doFinalPresetPlace();
            if (this.wizardMob != null) {
                this.wizardMob.endBlackHolePhase();
            }
        }
        super.over();
    }

    protected void doFinalPresetPlace() {
        int arenaPresetTileX = this.tileX - this.arenaPreset.width / 2;
        int arenaPresetTileY = this.tileY - this.arenaPreset.height / 2;
        this.arenaPreset.applyToLevel(this.level, arenaPresetTileX, arenaPresetTileY);
        this.spawnArenaPortal(arenaPresetTileX + this.arenaPreset.width / 2, arenaPresetTileY - 2);
        this.spawnArenaPortal(arenaPresetTileX + this.arenaPreset.width / 2, arenaPresetTileY + this.arenaPreset.height + 1);
        this.spawnArenaPortal(arenaPresetTileX - 2, arenaPresetTileY + this.arenaPreset.height / 2);
        this.spawnArenaPortal(arenaPresetTileX + this.arenaPreset.width + 1, arenaPresetTileY + this.arenaPreset.height / 2);
    }

    protected class PlaceArenaLevelEventAction
    extends LevelEventAction {
        protected PlaceArenaLevelEventAction() {
        }

        public void runAndSend(int tileX, int tileY, int tileID, int objectID) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(tileX);
            writer.putNextInt(tileY);
            writer.putNextInt(tileID);
            writer.putNextInt(objectID);
            this.runAndSendAction(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int layerID;
            int tileX = reader.getNextInt();
            int tileY = reader.getNextInt();
            int tileID = reader.getNextInt();
            int objectID = reader.getNextInt();
            if (!AscendedBlackHoleEvent.this.getLevel().isClient()) {
                for (layerID = 0; layerID < ObjectLayerRegistry.getTotalLayers(); ++layerID) {
                    GameObject object = AscendedBlackHoleEvent.this.getLevel().objectLayer.getObject(layerID, tileX, tileY);
                    if (object.getID() == 0) continue;
                    AscendedBlackHoleEvent.this.getLevel().entityManager.doObjectDamageOverride(layerID, tileX, tileY, object.objectHealth);
                }
            }
            if (objectID > 0) {
                AscendedBlackHoleEvent.this.getLevel().regionManager.setTileProtected(tileX, tileY, true);
            }
            if (tileID != -1) {
                AscendedBlackHoleEvent.this.getLevel().setTile(tileX, tileY, tileID);
            }
            if (objectID != -1) {
                AscendedBlackHoleEvent.this.getLevel().setObject(tileX, tileY, objectID);
            }
            for (layerID = 1; layerID < ObjectLayerRegistry.getTotalLayers(); ++layerID) {
                AscendedBlackHoleEvent.this.getLevel().objectLayer.setObject(layerID, tileX, tileY, 0);
            }
        }
    }

    protected static class ArenaTilePlace {
        public final int tileX;
        public final int tileY;
        public final int tileID;
        public final int objectID;

        public ArenaTilePlace(int tileX, int tileY, int tileID, int objectID) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.tileID = tileID;
            this.objectID = objectID;
        }
    }
}

