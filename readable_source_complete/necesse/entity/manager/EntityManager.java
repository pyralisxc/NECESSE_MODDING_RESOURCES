/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.packet.PacketObjectEntity;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.packet.PacketRemovePickupEntity;
import necesse.engine.network.packet.PacketRemoveProjectile;
import necesse.engine.network.packet.PacketSpawnMob;
import necesse.engine.network.packet.PacketSpawnPickupEntity;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.EntityListsRegionSearch;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.world.ReturnedObjects;
import necesse.entity.AbstractDamageResult;
import necesse.entity.DamagedObjectEntity;
import necesse.entity.DrawOnMapEntity;
import necesse.entity.Entity;
import necesse.entity.ObjectDamageResult;
import necesse.entity.TileDamageResult;
import necesse.entity.chains.Chain;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.ClientSubmittedHits;
import necesse.entity.manager.EntityComponentManager;
import necesse.entity.manager.EntityList;
import necesse.entity.manager.EntityRegionList;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.manager.LevelEventsManager;
import necesse.entity.manager.MobSpawnArea;
import necesse.entity.manager.OnMobAddedListenerEntityComponent;
import necesse.entity.manager.TileEntityList;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.ParticleOptions;
import necesse.entity.pickup.PickupEntity;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.MobChance;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class EntityManager {
    public static int MAX_PICKUP_ENTITIES = 1000;
    public final Level level;
    public final Object lock = new Object();
    public final EntityComponentManager<Integer> componentManager = new EntityComponentManager();
    public final EntityList<Mob> mobs;
    public final EntityRegionList<PlayerMob> players;
    public final EntityList<PickupEntity> pickups;
    public final EntityList<Projectile> projectiles;
    public final EntityComponentManager<Long> objectEntityComponentManager = new EntityComponentManager();
    public final TileEntityList<ObjectEntity> objectEntities;
    public final EntityList<Particle> particles;
    public final ParticleOptions particleOptions;
    public final TileEntityList<DamagedObjectEntity> damagedObjects;
    public final LevelEventsManager events;
    public final ArrayList<Chain> chains = new ArrayList();
    public final ArrayList<Trail> trails = new ArrayList();
    public final ArrayList<GroundPillarHandler<?>> pillarHandlers = new ArrayList();
    private LinkedList<DrawOnMapEntity> drawOnMap = new LinkedList();
    private float spawnRateMod;
    private float spawnCapMod;
    private float chaserDistanceMod;
    private Rectangle particlesAllowed = new Rectangle();
    public HashMap<Point, Mob> serverOpenedDoors = new HashMap();
    public final ClientSubmittedHits submittedHits;

    public EntityManager(Level level) {
        this.level = level;
        this.submittedHits = new ClientSubmittedHits(this);
        this.mobs = new EntityList<Mob>(this, true, "mob", m -> {
            if (level.isServer() && m.shouldSendSpawnPacket()) {
                level.getServer().network.sendToClientsWithEntity(new PacketSpawnMob((Mob)m), (RegionPositionGetter)m);
            }
        }, m -> {
            if (level.isServer()) {
                level.getServer().network.sendToClientsWithEntity(new PacketRemoveMob(m.getUniqueID()), (RegionPositionGetter)m);
            }
        }, true);
        this.mobs.onHiddenAdded = mob -> level.streamAll(OnMobAddedListenerEntityComponent.class).forEach(listener -> listener.onMobSpawned((Mob)mob));
        this.players = new EntityRegionList(level);
        this.pickups = new EntityList<PickupEntity>(this, true, "pickup", p -> {
            if (level.isServer() && p.shouldSendSpawnPacket()) {
                level.getServer().network.sendToClientsWithEntity(new PacketSpawnPickupEntity((PickupEntity)p), (RegionPositionGetter)p);
            }
        }, p -> {
            if (level.isServer()) {
                level.getServer().network.sendToClientsWithEntity(new PacketRemovePickupEntity(p.getUniqueID()), (RegionPositionGetter)p);
            }
        }, true);
        this.projectiles = new EntityList<Projectile>(this, true, "projectile", p -> {
            if (level.isServer() && p.shouldSendSpawnPacket()) {
                level.getServer().network.sendToClientsWithEntity(new PacketSpawnProjectile((Projectile)p), (RegionPositionGetter)p);
            }
        }, p -> {
            if (level.isServer() && p.sendRemovePacket) {
                level.getServer().network.sendToClientsWithEntity(new PacketRemoveProjectile(p.getUniqueID()), (RegionPositionGetter)p);
            }
        }, true);
        this.projectiles.onHiddenAdded = p -> {
            if (level.isServer()) {
                this.submittedHits.submitNewProjectile((Projectile)p);
            }
        };
        this.particles = new EntityList(this, false, "particle", null, null, true);
        this.particles.cacheTTL = 0;
        this.particleOptions = new ParticleOptions(level);
        this.objectEntities = new TileEntityList(this, this.objectEntityComponentManager, "objectEntity", null, null);
        this.objectEntities.cacheTTL = 0;
        this.damagedObjects = new TileEntityList(this, null, "damagedObject", null, null);
        this.damagedObjects.cacheTTL = 0;
        this.events = new LevelEventsManager(this);
        this.updateMods();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addChain(Chain chain) {
        ArrayList<Chain> arrayList = this.chains;
        synchronized (arrayList) {
            this.chains.add(chain);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addTrail(Trail trail) {
        ArrayList<Trail> arrayList = this.trails;
        synchronized (arrayList) {
            this.trails.add(trail);
        }
    }

    public void changeMobLevel(Mob mob, Level newLevel, int newX, int newY, boolean mountFollow) {
        this.changeMobLevel(mob.getUniqueID(), newLevel, newX, newY, mountFollow);
    }

    public void changeMobLevel(int uniqueID, Level newLevel, int newX, int newY, boolean mountFollow) {
        Mob mob = this.mobs.get(uniqueID, false);
        if (mob != null) {
            Mob mount = mob.getMount();
            if (mount != null && mount.forceFollowRiderLevelChange(mob) || mountFollow && mount != null) {
                this.changeMobLevel(mount, newLevel, newX, newY, true);
            } else {
                mob.dismount();
            }
            this.mobs.onRemoved.accept(mob);
            this.mobs.map.remove(uniqueID);
            newLevel.entityManager.addMob(mob, newX, newY);
        } else {
            System.err.println("Tried to change level of invalid mob unique id " + uniqueID + " from " + this.level.getIdentifier() + " tp " + newLevel.getIdentifier());
        }
    }

    public void addMob(Mob mob, float x, float y) {
        mob.setPos(x, y, true);
        this.mobs.add(mob);
    }

    public void addParticle(Particle particle, Particle.GType gType) {
        this.addParticle(particle, false, gType);
    }

    public void addParticle(Particle particle, boolean overrideAllowed, Particle.GType gType) {
        if (this.level.isServer()) {
            return;
        }
        if (!gType.canAdd(this.level)) {
            return;
        }
        if (!overrideAllowed && !this.particlesAllowed.contains(particle.getX(), particle.getY())) {
            return;
        }
        this.particles.add(particle);
    }

    public ParticleOption addParticle(ParticleOption o, Particle.GType gType) {
        if (gType != null) {
            if (!gType.canAdd(this.level)) {
                o.remove();
                return o;
            }
            Point2D.Float levelPos = o.getLevelPos();
            if (!this.particlesAllowed.contains(levelPos.x, levelPos.y)) {
                o.remove();
                return o;
            }
        }
        this.particleOptions.add(o);
        return o;
    }

    public ParticleOption addParticle(Supplier<Point2D.Float> snapPos, float startXOffset, float startYOffset, Particle.GType gType) {
        return this.addParticle(ParticleOption.standard(startXOffset, startYOffset).snapPosition(snapPos), gType);
    }

    public ParticleOption addParticle(Entity snapPos, float startXOffset, float startYOffset, Particle.GType gType) {
        return this.addParticle(ParticleOption.standard(startXOffset, startYOffset).snapPosition(snapPos), gType);
    }

    public ParticleOption addParticle(Supplier<Point2D.Float> snapPos, Particle.GType gType) {
        return this.addParticle(snapPos, 0.0f, 0.0f, gType);
    }

    public ParticleOption addParticle(Entity snapPos, Particle.GType gType) {
        return this.addParticle(snapPos, 0.0f, 0.0f, gType);
    }

    public ParticleOption addParticle(float x, float y, Particle.GType gType) {
        return this.addParticle(ParticleOption.standard(x, y), gType);
    }

    public ParticleOption addTopParticle(ParticleOption o, Particle.GType gType, int drawOrder) {
        if (gType != null) {
            if (!gType.canAdd(this.level)) {
                o.remove();
                return o;
            }
            Point2D.Float levelPos = o.getLevelPos();
            if (!this.particlesAllowed.contains(levelPos.x, levelPos.y)) {
                o.remove();
                return o;
            }
        }
        this.particleOptions.addTop(o, drawOrder);
        return o;
    }

    public ParticleOption addTopParticle(Supplier<Point2D.Float> snapPos, float startXOffset, float startYOffset, Particle.GType gType, int drawOrder) {
        return this.addTopParticle(ParticleOption.standard(startXOffset, startYOffset).snapPosition(snapPos), gType, drawOrder);
    }

    public ParticleOption addTopParticle(Entity snapPos, float startXOffset, float startYOffset, Particle.GType gType, int drawOrder) {
        return this.addTopParticle(ParticleOption.standard(startXOffset, startYOffset).snapPosition(snapPos), gType, drawOrder);
    }

    public ParticleOption addTopParticle(Supplier<Point2D.Float> snapPos, Particle.GType gType, int drawOrder) {
        return this.addTopParticle(snapPos, 0.0f, 0.0f, gType, drawOrder);
    }

    public ParticleOption addTopParticle(Entity snapPos, Particle.GType gType, int drawOrder) {
        return this.addTopParticle(snapPos, 0.0f, 0.0f, gType, drawOrder);
    }

    public ParticleOption addTopParticle(float x, float y, Particle.GType gType, int drawOrder) {
        return this.addTopParticle(ParticleOption.standard(x, y), gType, drawOrder);
    }

    public ParticleOption addTopParticle(ParticleOption o, Particle.GType gType) {
        return this.addTopParticle(o, gType, 0);
    }

    public ParticleOption addTopParticle(Supplier<Point2D.Float> snapPos, float startXOffset, float startYOffset, Particle.GType gType) {
        return this.addTopParticle(ParticleOption.standard(startXOffset, startYOffset).snapPosition(snapPos), gType);
    }

    public ParticleOption addTopParticle(Entity snapPos, float startXOffset, float startYOffset, Particle.GType gType) {
        return this.addTopParticle(ParticleOption.standard(startXOffset, startYOffset).snapPosition(snapPos), gType);
    }

    public ParticleOption addTopParticle(Supplier<Point2D.Float> snapPos, Particle.GType gType) {
        return this.addTopParticle(snapPos, 0.0f, 0.0f, gType);
    }

    public ParticleOption addTopParticle(Entity snapPos, Particle.GType gType) {
        return this.addTopParticle(snapPos, 0.0f, 0.0f, gType);
    }

    public ParticleOption addTopParticle(float x, float y, Particle.GType gType) {
        return this.addTopParticle(ParticleOption.standard(x, y), gType);
    }

    public void updateParticlesAllowed(GameCamera camera) {
        this.particlesAllowed = new Rectangle(camera.getX() - 50, camera.getY() - 50, camera.getWidth() + 100, camera.getHeight() + 100);
    }

    public boolean isParticlesAllowed(float x, float y) {
        return this.particlesAllowed.contains(x, y);
    }

    public ObjectEntity getObjectEntity(int tileX, int tileY) {
        return this.objectEntities.get(tileX, tileY, false);
    }

    public <T extends ObjectEntity> T getObjectEntity(int tileX, int tileY, Class<T> expectedClass) {
        ObjectEntity objectEntity = this.getObjectEntity(tileX, tileY);
        if (objectEntity != null && expectedClass.isAssignableFrom(objectEntity.getClass())) {
            return (T)((ObjectEntity)expectedClass.cast(objectEntity));
        }
        return null;
    }

    public void removeObjectEntity(int tileX, int tileY) {
        ObjectEntity entity = this.getObjectEntity(tileX, tileY);
        if (entity != null) {
            entity.remove();
        }
    }

    public DamagedObjectEntity getDamagedObjectEntity(int tileX, int tileY) {
        return this.damagedObjects.get(tileX, tileY, false);
    }

    @Deprecated
    public void addLevelEvent(LevelEvent event) {
        this.events.add(event);
    }

    @Deprecated
    public void addLevelEventHidden(LevelEvent event) {
        this.events.addHidden(event);
    }

    @Deprecated
    public LevelEvent getLevelEvent(int uniqueID, boolean searchCache) {
        return this.events.get(uniqueID, searchCache);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPillarHandler(GroundPillarHandler<?> handler) {
        ArrayList<GroundPillarHandler<?>> arrayList = this.pillarHandlers;
        synchronized (arrayList) {
            handler.level = this.level;
            this.pillarHandlers.add(handler);
        }
    }

    public <T extends Entity> GameAreaStream<T> streamArea(float x, float y, int range, EntityRegionList<? extends T> ... entityLists) {
        return this.streamAreaTileRange((int)x, (int)y, range / 32 + 1, entityLists);
    }

    public <T extends Entity> GameAreaStream<T> streamAreaTileRange(int x, int y, int tileRange, EntityRegionList<? extends T> ... entityLists) {
        return new EntityListsRegionSearch<T>(this.level, (float)x, (float)y, tileRange, entityLists).streamEach();
    }

    public GameAreaStream<Mob> streamAreaMobsAndPlayers(float x, float y, int range) {
        return this.streamAreaMobsAndPlayersTileRange((int)x, (int)y, range / 32 + 1);
    }

    public GameAreaStream<Mob> streamAreaMobsAndPlayersTileRange(int x, int y, int tileRange) {
        return new EntityListsRegionSearch(this.level, (float)x, (float)y, tileRange, this.mobs.regionList, this.players).streamEach();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameLinkedList<Entity> getRegionDrawEntities(int regionX, int regionY) {
        GameLinkedList<Entity> out = new GameLinkedList<Entity>();
        Object object = this.lock;
        synchronized (object) {
            out.addAll((Collection<Entity>)this.mobs.getInRegion(regionX, regionY));
            out.addAll(this.players.getInRegion(regionX, regionY));
            out.addAll((Collection<Entity>)this.pickups.getInRegion(regionX, regionY));
            out.addAll((Collection<Entity>)this.projectiles.getInRegion(regionX, regionY));
            out.addAll((Collection<Entity>)this.particles.getInRegion(regionX, regionY));
        }
        return out;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void refreshSetLevel() {
        Object object = this.lock;
        synchronized (object) {
            this.mobs.stream().forEach(e -> e.setLevel(this.level));
            this.pickups.stream().forEach(e -> e.setLevel(this.level));
            this.projectiles.stream().forEach(e -> e.setLevel(this.level));
            this.objectEntities.stream().forEach(e -> e.setLevel(this.level));
            this.particles.stream().forEach(e -> e.setLevel(this.level));
            this.damagedObjects.stream().forEach(e -> e.setLevel(this.level));
        }
    }

    public LinkedList<DrawOnMapEntity> getEntityDrawOnMap() {
        return this.drawOnMap;
    }

    public void frameTick(TickManager tickManager) {
        this.mobs.frameTick(tickManager, Mob::tickMovement);
        this.pickups.frameTick(tickManager, PickupEntity::tickMovement);
        this.projectiles.frameTick(tickManager, Projectile::tickMovement);
        this.particles.frameTick(tickManager, Particle::tickMovement);
        float delta = tickManager.getDelta();
        this.particleOptions.tickMovement(delta);
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "movement", () -> Performance.record((PerformanceTimerManager)this.level.tickManager(), "events", () -> {
            Object object = this.lock;
            synchronized (object) {
                for (LevelEvent event : this.events) {
                    if (event == null || event.isOver()) continue;
                    event.tickMovement(delta);
                }
            }
        }));
        this.objectEntities.frameTick(tickManager, ObjectEntity::frameTick);
    }

    public void clientTick() {
        LinkedList drawOnMap = new LinkedList();
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "mobs", () -> this.mobs.clientTick(Mob::clientTick, drawOnMap));
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "pickups", () -> this.pickups.clientTick(PickupEntity::clientTick, drawOnMap));
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "projectiles", () -> this.projectiles.clientTick(Projectile::clientTick, drawOnMap));
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "particles", () -> this.particles.clientTick(Particle::clientTick, drawOnMap));
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "particleOpts", this.particleOptions::tick);
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "chains", () -> {
            ArrayList<Chain> arrayList = this.chains;
            synchronized (arrayList) {
                for (int i = 0; i < this.chains.size(); ++i) {
                    if (!this.chains.get(i).isRemoved()) continue;
                    this.chains.remove(i);
                    --i;
                }
            }
        });
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "trails", () -> {
            ArrayList<Trail> arrayList = this.trails;
            synchronized (arrayList) {
                for (int i = 0; i < this.trails.size(); ++i) {
                    Trail trail = this.trails.get(i);
                    trail.tick();
                    if (!trail.isRemoved()) continue;
                    this.trails.remove(i);
                    --i;
                }
            }
        });
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "pillars", () -> {
            ArrayList<GroundPillarHandler<?>> arrayList = this.pillarHandlers;
            synchronized (arrayList) {
                for (int i = 0; i < this.pillarHandlers.size(); ++i) {
                    if (!this.pillarHandlers.get(i).tickAndShouldRemove()) continue;
                    this.pillarHandlers.remove(i);
                    --i;
                }
            }
        });
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "levelEvents", this.events::clientTick);
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "damageEntities", () -> this.damagedObjects.clientTick(DamagedObjectEntity::clientTick, drawOnMap));
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "objectEntities", () -> this.objectEntities.clientTick(ObjectEntity::clientTick, drawOnMap));
        this.drawOnMap = drawOnMap;
    }

    public void serverTick() {
        LinkedList drawOnMap = new LinkedList();
        this.submittedHits.tick();
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "mobs", () -> this.mobs.serverTick(mob -> {
            if (this.level.tickManager().getTick() == 1 && GameRandom.globalRandom.nextFloat() < this.getDespawnOdds() && mob.canDespawn()) {
                mob.remove();
                return;
            }
            mob.serverTick();
            if (mob.isDirty()) {
                if (mob.shouldSendSpawnPacket()) {
                    this.level.getServer().network.sendToClientsWithEntity(new PacketSpawnMob((Mob)mob), (RegionPositionGetter)mob);
                }
                mob.markClean();
            }
        }, drawOnMap));
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "pickups", () -> {
            if (this.pickups.count() > MAX_PICKUP_ENTITIES) {
                int toRemove = this.pickups.count() - MAX_PICKUP_ENTITIES;
                this.pickups.stream().sorted(Comparator.comparingLong(p -> p.spawnTime)).limit(toRemove).forEach(Entity::remove);
            }
            this.pickups.serverTick(pickup -> {
                pickup.serverTick();
                if (pickup.isDirty()) {
                    if (this.level.isServer()) {
                        this.level.getServer().network.sendToClientsWithEntity(new PacketSpawnPickupEntity((PickupEntity)pickup), (RegionPositionGetter)pickup);
                    }
                    pickup.markClean();
                }
            }, drawOnMap);
        });
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "projectiles", () -> this.projectiles.serverTick(Projectile::serverTick, drawOnMap));
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "particles", () -> this.particles.serverTick(Particle::serverTick, drawOnMap));
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "chains", () -> {
            ArrayList<Chain> arrayList = this.chains;
            synchronized (arrayList) {
                for (int i = 0; i < this.chains.size(); ++i) {
                    if (!this.chains.get(i).isRemoved()) continue;
                    this.chains.remove(i);
                    --i;
                }
            }
        });
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "trails", () -> {
            ArrayList<Trail> arrayList = this.trails;
            synchronized (arrayList) {
                for (int i = 0; i < this.trails.size(); ++i) {
                    if (!this.trails.get(i).isRemoved()) continue;
                    this.trails.remove(i);
                    --i;
                }
            }
        });
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "pillars", () -> {
            ArrayList<GroundPillarHandler<?>> arrayList = this.pillarHandlers;
            synchronized (arrayList) {
                for (int i = 0; i < this.pillarHandlers.size(); ++i) {
                    if (!this.pillarHandlers.get(i).tickAndShouldRemove()) continue;
                    this.pillarHandlers.remove(i);
                    --i;
                }
            }
        });
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "levelEvents", this.events::serverTick);
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "damageEntities", () -> this.damagedObjects.serverTick(DamagedObjectEntity::serverTick, drawOnMap));
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "objectEntities", () -> this.objectEntities.serverTick(oe -> {
            oe.serverTick();
            if (oe.isDirty()) {
                if (this.level.isServer()) {
                    this.level.getServer().network.sendToClientsWithTile(new PacketObjectEntity((ObjectEntity)oe), this.level, oe.tileX, oe.tileY);
                }
                oe.markClean();
            }
        }, drawOnMap));
        this.drawOnMap = drawOnMap;
    }

    public int countMobs(int levelX, int levelY, MobSpawnArea area, Predicate<Mob> filter) {
        return (int)this.mobs.streamInRegionsShape(GameUtils.rangeBounds(levelX, levelY, area.maxSpawnDistance + 320), 0).filter(filter).count();
    }

    public int countPlayers(int levelX, int levelY, MobSpawnArea area) {
        return (int)this.players.streamInRegionsShape(GameUtils.rangeBounds(levelX, levelY, (int)((float)area.maxSpawnDistance * 1.75f)), 0).filter(p -> !p.removed()).count();
    }

    public boolean tickMobSpawning(Server server, ServerClient client) {
        if (server.world.settings.disableMobSpawns) {
            return true;
        }
        return Performance.record((PerformanceTimerManager)this.level.tickManager(), "mobSpawning", () -> {
            Point spawnTile;
            int hostileMobs = this.countMobs(client.playerMob.getX(), client.playerMob.getY(), Mob.MOB_SPAWN_AREA, m -> m.isHostile && m.canDespawn);
            if ((float)hostileMobs < client.getMobSpawnCap(this.level) && (spawnTile = EntityManager.getMobSpawnTile(this.level, client.playerMob.getX(), client.playerMob.getY(), Mob.MOB_SPAWN_AREA, tile -> {
                if (!this.level.isTileWithinBounds(tile.x, tile.y) || this.level.isSolidTile(tile.x, tile.y)) {
                    return 0;
                }
                return this.level.getTile(tile.x, tile.y).getMobSpawnPositionTickets(this.level, tile.x, tile.y);
            })) != null) {
                MobSpawnTable mobSpawnTable = client.getMobSpawnTable(this.level, spawnTile.x, spawnTile.y);
                return this.spawnRandomMob(server, client, (TilePosition pos) -> pos.tile().tile.getMobSpawnTable((TilePosition)pos, mobSpawnTable), spawnTile);
            }
            return true;
        });
    }

    public boolean tickCritterSpawning(Server server, ServerClient client) {
        return Performance.record((PerformanceTimerManager)this.level.tickManager(), "critterSpawning", () -> {
            int critterMobs;
            Point spawnTile = EntityManager.getMobSpawnTile(this.level, client.playerMob.getX(), client.playerMob.getY(), Mob.CRITTER_SPAWN_AREA, tile -> {
                if (!this.level.isTileWithinBounds(tile.x, tile.y) || this.level.isSolidTile(tile.x, tile.y)) {
                    return 0;
                }
                return 100;
            });
            if (spawnTile != null && (float)(critterMobs = this.countMobs(client.playerMob.getX(), client.playerMob.getY(), Mob.CRITTER_SPAWN_AREA, m -> m.isCritter && m.canDespawn)) < client.getCritterSpawnCap(this.level, spawnTile.x, spawnTile.y)) {
                MobSpawnTable critterSpawnTable = client.getCritterSpawnTable(this.level, spawnTile.x, spawnTile.y);
                return this.spawnRandomMob(server, client, critterSpawnTable, spawnTile);
            }
            return true;
        });
    }

    private boolean spawnRandomMob(Server server, ServerClient client, Function<TilePosition, MobSpawnTable> spawnTableGetter, Point spawnTile) {
        if (spawnTile != null) {
            MobSpawnTable spawnTable = spawnTableGetter.apply(new TilePosition(this.level, spawnTile.x, spawnTile.y));
            return this.spawnRandomMob(server, client, spawnTable, spawnTile);
        }
        return false;
    }

    private boolean spawnRandomMob(Server server, ServerClient client, MobSpawnTable spawnTable, Point spawnTile) {
        MobChance randomMob;
        if (spawnTile == null || spawnTable == null) {
            return false;
        }
        while ((randomMob = spawnTable.getRandomMob(this.level, client, spawnTile, GameRandom.globalRandom, "mobspawning")) != null) {
            Collection<Mob> spawned = randomMob.spawnMob(this.level, client, spawnTile, null, this.level::onMobSpawned, "mobspawning");
            if (spawned != null) {
                return true;
            }
            spawnTable = spawnTable.withoutRandomMob(randomMob);
        }
        return false;
    }

    public static Point getMobSpawnTile(Level level, int centerLevelX, int centerLevelY, MobSpawnArea spawnArea, Function<Point, Integer> ticketsGetter) {
        Point spawnPos = Performance.record((PerformanceTimerManager)level.tickManager(), "getSpawnPos", () -> {
            if (ticketsGetter != null) {
                return spawnArea.getRandomTicketTile(GameRandom.globalRandom, GameMath.getTileCoordinate(centerLevelX), GameMath.getTileCoordinate(centerLevelY), ticketsGetter);
            }
            return spawnArea.getRandomTile(GameRandom.globalRandom, GameMath.getTileCoordinate(centerLevelX), GameMath.getTileCoordinate(centerLevelY));
        });
        if (spawnPos != null) {
            if (!level.isTileWithinBounds(spawnPos.x, spawnPos.y)) {
                return null;
            }
            int spawnLevelPosX = spawnPos.x * 32 + 16;
            int spawnLevelPosY = spawnPos.y * 32 + 16;
            if (level.entityManager.players.streamArea(spawnLevelPosX, spawnLevelPosY, spawnArea.minSpawnDistance).anyMatch(p -> p.getDistance(spawnLevelPosX, spawnLevelPosY) < (float)spawnArea.minSpawnDistance)) {
                return null;
            }
        }
        return spawnPos;
    }

    public static Point getMobSpawnTile(Level level, int centerX, int centerY, int minDistance, int maxDistance) {
        double t = GameRandom.globalRandom.nextDouble() * Math.PI * 2.0;
        double a = 2.0 / (Math.pow(maxDistance, 2.0) - Math.pow(minDistance, 2.0));
        double r = Math.sqrt(2.0 * GameRandom.globalRandom.nextDouble() / a + Math.pow(minDistance, 2.0));
        double vX = r * Math.cos(t) + (double)centerX;
        double vY = r * Math.sin(t) + (double)centerY;
        int tileX = GameMath.getTileCoordinate(vX);
        int tileY = GameMath.getTileCoordinate(vY);
        if (GameUtils.streamServerClients(level).anyMatch(c -> c.playerMob.getDistance(tileX * 32 + 16, tileY * 32 + 16) <= (float)minDistance)) {
            return null;
        }
        return new Point(tileX, tileY);
    }

    public int getSize() {
        return this.mobs.count() + this.pickups.count() + this.damagedObjects.count() + this.projectiles.count() + this.particles.count() + this.objectEntities.count();
    }

    public TileDamageResult doTileDamageOverride(int tileX, int tileY, int damage) {
        return this.getOrCreateDamagedObjectEntity(tileX, tileY).doTileDamageOverride(damage);
    }

    public ObjectDamageResult doObjectDamageOverride(int objectLayerID, int tileX, int tileY, int damage) {
        return this.getOrCreateDamagedObjectEntity(tileX, tileY).doObjectDamageOverride(objectLayerID, damage);
    }

    public TileDamageResult doTileDamage(int tileX, int tileY, int damage, float toolTier, Attacker attacker, ServerClient client) {
        return this.getOrCreateDamagedObjectEntity(tileX, tileY).doTileDamage(damage, toolTier, attacker, client);
    }

    public TileDamageResult doTileDamage(int tileX, int tileY, int damage, float toolTier, Attacker attacker, ServerClient client, boolean showEffects, int mouseX, int mouseY) {
        return this.getOrCreateDamagedObjectEntity(tileX, tileY).doTileDamage(damage, toolTier, attacker, client, showEffects, mouseX, mouseY);
    }

    public ObjectDamageResult doObjectDamage(int objectLayerID, int tileX, int tileY, int damage, float toolTier, Attacker attacker, ServerClient client) {
        return this.getOrCreateDamagedObjectEntity(tileX, tileY).doObjectDamage(objectLayerID, damage, toolTier, attacker, client);
    }

    public ObjectDamageResult doObjectDamage(int objectLayerID, int tileX, int tileY, int damage, float toolTier, Attacker attacker, ServerClient client, boolean showEffects, int mouseX, int mouseY) {
        return this.getOrCreateDamagedObjectEntity(tileX, tileY).doObjectDamage(objectLayerID, damage, toolTier, attacker, client, showEffects, mouseX, mouseY);
    }

    public AbstractDamageResult doToolDamage(int priorityObjectLayerID, int tileX, int tileY, int damage, ToolType toolType, float toolTier, Attacker attacker, ServerClient client, boolean showEffects, int mouseX, int mouseY) {
        return this.getOrCreateDamagedObjectEntity(tileX, tileY).doToolDamage(priorityObjectLayerID, damage, toolType, toolTier, attacker, client, showEffects, mouseX, mouseY);
    }

    public DamagedObjectEntity getOrCreateDamagedObjectEntity(int tileX, int tileY) {
        DamagedObjectEntity damagedObject = this.getDamagedObjectEntity(tileX, tileY);
        if (damagedObject == null) {
            damagedObject = new DamagedObjectEntity(this.level, tileX, tileY);
        }
        this.damagedObjects.add(damagedObject);
        return damagedObject;
    }

    public float getDespawnOdds() {
        return 0.05f;
    }

    public boolean uniqueIDOccupied(int id) {
        if (this.mobs.get(id, false) != null) {
            return true;
        }
        if (this.pickups.get(id, false) != null) {
            return true;
        }
        if (this.projectiles.get(id, false) != null) {
            return true;
        }
        return this.events.hasUniqueID(id);
    }

    public void updateMods() {
        this.spawnRateMod = 1.0f;
        this.spawnCapMod = 1.0f;
        this.chaserDistanceMod = 1.0f;
    }

    public float getChaserDistanceMod() {
        return this.chaserDistanceMod;
    }

    public float getSpawnRate(int tileX, int tileY) {
        return this.level.getBiome(tileX, tileY).getSpawnRateMod(this.level) * this.spawnRateMod;
    }

    public float getSpawnCapMod(int tileX, int tileY) {
        return this.level.getBiome(tileX, tileY).getSpawnCapMod(this.level) * this.spawnCapMod;
    }

    public static float getSpawnCap(int playerCount, float multiplier, float addition) {
        return (float)(Math.pow(playerCount, 0.3333333432674408) * (double)multiplier + (double)addition);
    }

    public void onServerClientLoadedRegion(Region region, ServerClient client) {
        for (Mob mob : this.level.entityManager.mobs.getInRegion(region.regionX, region.regionY)) {
            if (!mob.shouldSendSpawnPacket()) continue;
            client.sendPacket(new PacketSpawnMob(mob));
        }
        for (PickupEntity pickup : this.level.entityManager.pickups.getInRegion(region.regionX, region.regionY)) {
            if (!pickup.shouldSendSpawnPacket()) continue;
            client.sendPacket(new PacketSpawnPickupEntity(pickup));
        }
        for (Projectile projectile : this.level.entityManager.projectiles.getInRegion(region.regionX, region.regionY)) {
            if (!projectile.shouldSendSpawnPacket()) continue;
            client.sendPacket(new PacketSpawnProjectile(projectile));
        }
    }

    public void addReturnedEntities(ReturnedObjects returnedObjects) {
        for (Mob mob : this.mobs) {
            if (!mob.shouldAddToDeletedLevelReturnedMobs()) continue;
            returnedObjects.mobs.add(mob);
        }
        for (PickupEntity pickup : this.pickups) {
            if (!pickup.shouldAddToDeletedLevelReturnedPickups()) continue;
            returnedObjects.pickups.add(pickup);
        }
    }

    public void onLoadingComplete() {
        this.mobs.onLoadingComplete();
        this.pickups.onLoadingComplete();
        this.projectiles.onLoadingComplete();
        this.objectEntities.onLoadingComplete();
        this.particles.onLoadingComplete();
        this.damagedObjects.onLoadingComplete();
        this.events.onLoadingComplete();
    }

    public void onUnloading() {
        this.mobs.onUnloading();
        this.pickups.onUnloading();
        this.projectiles.onUnloading();
        this.objectEntities.onUnloading();
        this.particles.onUnloading();
        this.damagedObjects.onUnloading();
        this.events.onUnloading();
    }

    public void dispose() {
        this.mobs.dispose();
        this.pickups.dispose();
        this.projectiles.dispose();
        this.objectEntities.dispose();
        this.particles.dispose();
        this.damagedObjects.dispose();
        this.events.dispose();
    }
}

