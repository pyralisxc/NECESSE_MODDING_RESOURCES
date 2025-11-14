/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.pickup;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.HashMap;
import necesse.engine.GameAuth;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketPickupEntityPickup;
import necesse.engine.network.packet.PacketPickupEntityTarget;
import necesse.engine.network.packet.PacketRequestPickupEntity;
import necesse.engine.network.packet.PacketSpawnPickupEntity;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.PickupRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.OneWorldMigration;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public abstract class PickupEntity
extends Entity {
    public final IDData idData = new IDData();
    public float dx;
    public float dy;
    public long spawnTime;
    public int pickupCooldown;
    public HashMap<Long, Integer> authPickupCooldown = new HashMap();
    private NetworkClient target;
    private long reservedAuth;
    protected int targetUpdateCooldown = 20000;
    protected long targetUpdateTime;
    public float bouncy = 0.0f;
    float sinking = 0.0f;
    public Rectangle collisionBox;
    public Rectangle selectionBox;
    public float targetRange = 60.0f;

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public PickupEntity() {
        PickupRegistry.instance.applyIDData(this.getClass(), this.idData);
        this.collisionBox = new Rectangle(-8, -8, 16, 16);
        this.selectionBox = new Rectangle(-12, -12, 24, 24);
    }

    public PickupEntity(Level level, float x, float y, float dx, float dy) {
        this();
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.spawnTime = this.getWorldEntity().getTime();
        this.pickupCooldown = 500;
        this.reservedAuth = -1L;
        this.collisionBox = new Rectangle(-8, -8, 16, 16);
        this.selectionBox = new Rectangle(-12, -12, 24, 24);
    }

    public void addSaveData(SaveData save) {
        save.addInt("uniqueID", this.getUniqueID());
        save.addFloat("x", this.x);
        save.addFloat("y", this.y);
        save.addFloat("dx", this.dx);
        save.addFloat("dy", this.dy);
        save.addLong("spawnTime", this.spawnTime);
        save.addInt("pickupCooldown", this.pickupCooldown);
        save.addLong("reservedAuth", this.reservedAuth);
    }

    public void applyLoadData(LoadData save) {
        this.setUniqueID(save.getInt("uniqueID", 0));
        this.x = save.getFloat("x", this.x);
        this.y = save.getFloat("y", this.y);
        this.dx = save.getFloat("dx", this.dx);
        this.dy = save.getFloat("dy", this.dy);
        this.spawnTime = save.getLong("spawnTime", this.spawnTime);
        this.pickupCooldown = save.getInt("pickupCooldown", this.pickupCooldown);
        this.reservedAuth = save.getLong("reservedAuth", this.reservedAuth);
    }

    public boolean shouldSendSpawnPacket() {
        return this.getID() != -1;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        writer.putNextInt(this.getUniqueID());
        writer.putNextFloat(this.x);
        writer.putNextFloat(this.y);
        writer.putNextFloat(this.dx);
        writer.putNextFloat(this.dy);
        writer.putNextLong(this.reservedAuth);
        writer.putNextLong(this.spawnTime);
        writer.putNextInt(this.pickupCooldown);
        writer.putNextFloat(this.sinking);
        this.writeTargetUpdatePacket(writer, false);
    }

    public void applySpawnPacket(PacketReader reader) {
        this.refreshClientUpdateTime();
        this.setUniqueID(reader.getNextInt());
        this.x = reader.getNextFloat();
        this.y = reader.getNextFloat();
        this.dx = reader.getNextFloat();
        this.dy = reader.getNextFloat();
        this.setReservedAuth(reader.getNextLong());
        this.spawnTime = reader.getNextLong();
        this.pickupCooldown = reader.getNextInt();
        this.sinking = reader.getNextFloat();
        this.readTargetUpdatePacket(reader, false);
    }

    public void writeTargetUpdatePacket(PacketWriter writer, boolean includePosData) {
        if (this.target == null) {
            writer.putNextByteUnsigned(0);
        } else {
            writer.putNextByteUnsigned(1);
            writer.putNextByteUnsigned(this.target.slot);
        }
        if (includePosData) {
            writer.putNextFloat(this.x);
            writer.putNextFloat(this.y);
            writer.putNextFloat(this.dx);
            writer.putNextFloat(this.dy);
        }
    }

    public void readTargetUpdatePacket(PacketReader reader, boolean includePosData) {
        this.refreshClientUpdateTime();
        int type = reader.getNextByteUnsigned();
        if (type == 0) {
            this.resetTarget();
        } else {
            int slot = reader.getNextByteUnsigned();
            if (this.isClient()) {
                this.setTarget(this.getLevel().getClient().getClient(slot));
            } else if (this.isServer()) {
                this.setTarget(this.getLevel().getServer().getClient(slot));
            }
        }
        if (includePosData) {
            this.x = reader.getNextFloat();
            this.y = reader.getNextFloat();
            this.dx = reader.getNextFloat();
            this.dy = reader.getNextFloat();
        }
    }

    public void sendTargetUpdatePacket() {
        this.targetUpdateTime = this.getWorldEntity().getTime();
        if (this.isServer()) {
            this.getLevel().getServer().network.sendToClientsWithEntity(new PacketPickupEntityTarget(this), this);
        }
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void onUnloading(Region region) {
        super.onUnloading(region);
        this.limitWithinRegionBounds(region);
    }

    @Override
    public void onRegionChanged(int lastRegionX, int lastRegionY, int newRegionX, int newRegionY) {
        super.onRegionChanged(lastRegionX, lastRegionY, newRegionX, newRegionY);
        if (this.isServer() && this.shouldSendSpawnPacket()) {
            this.sendPacketToNewClientsWithRegion(lastRegionX, lastRegionY, newRegionX, newRegionY, () -> new PacketSpawnPickupEntity(this));
        }
    }

    public void moveX(float mod) {
        this.x += this.dx * mod / 250.0f;
    }

    public void moveY(float mod) {
        this.y += this.dy * mod / 250.0f;
    }

    @Override
    public void clientTick() {
        if (this.removed()) {
            return;
        }
        if (this.getTimeSinceClientUpdate() >= (long)(this.targetUpdateCooldown + 10000)) {
            this.refreshClientUpdateTime();
            this.requestServerUpdate();
        }
        float sinkingRate = this.getSinkingRate();
        this.sinking = GameMath.limit(this.sinking + sinkingRate, 0.0f, this.getMaxSinking());
        if (this.sinking >= 1.0f) {
            this.remove();
        }
    }

    public void requestServerUpdate() {
        if (this.isClient()) {
            GameLog.debug.println("Client requesting update for pickup " + this);
            this.getLevel().getClient().network.sendPacket(new PacketRequestPickupEntity(this.getUniqueID()));
        }
    }

    public float getTargetRange(ServerClient client) {
        return this.targetRange;
    }

    public float getTargetStreamRange() {
        return this.targetRange;
    }

    public float getSinkingRate() {
        return 0.0f;
    }

    public float getMaxSinking() {
        return 1.0f;
    }

    public long getTimeSinceSpawned() {
        return this.getWorldEntity().getTime() - this.spawnTime;
    }

    public long getLifespanMillis() {
        return 600000L;
    }

    public boolean isOnPickupCooldown() {
        return this.getTimeSinceSpawned() <= (long)this.pickupCooldown;
    }

    public boolean isOnPickupCooldown(long authentication) {
        return this.getTimeSinceSpawned() <= (long)this.authPickupCooldown.getOrDefault(authentication, 0).intValue();
    }

    @Override
    public void serverTick() {
        if (this.removed()) {
            return;
        }
        long lifespan = this.getLifespanMillis();
        if (lifespan > 0L && this.getTimeSinceSpawned() >= lifespan) {
            this.remove();
            return;
        }
        float sinkingRate = this.getSinkingRate();
        this.sinking = GameMath.limit(this.sinking + sinkingRate, 0.0f, this.getMaxSinking());
        if (this.sinking >= 1.0f) {
            this.remove();
            return;
        }
        this.checkCollision();
        if (this.removed()) {
            return;
        }
        if (this.target == null && !this.isOnPickupCooldown()) {
            this.getLevel().entityManager.players.streamArea(this.x, this.y, (int)this.getTargetStreamRange()).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).filter(c -> this.reservedAuth == -1L || c.authentication == this.reservedAuth).filter(c -> !this.isOnPickupCooldown(c.authentication)).map(c -> new ComputedObjectValue<ServerClient, Float>((ServerClient)c, () -> Float.valueOf(GameMath.squareDistance(this.x, this.y, c.playerMob.x, c.playerMob.y)))).filter(v -> ((Float)v.get()).floatValue() <= this.getTargetRange((ServerClient)v.object)).findBestDistance(0, Comparator.comparingDouble(ComputedValue::get)).filter(v -> this.isValidTarget((ServerClient)v.object)).ifPresent(v -> this.setTarget((ServerClient)v.object));
        }
        if (this.targetUpdateTime + (long)this.targetUpdateCooldown < this.getWorldEntity().getTime()) {
            this.sendTargetUpdatePacket();
        }
        if (this.target != null) {
            if (this.target.playerMob != null && this.target.isServer() && this.isValidTarget(this.target.getServerClient())) {
                if (this.collidesWith(this.target.getServerClient())) {
                    this.collidedWith(this.target.getServerClient());
                }
            } else {
                this.resetTarget();
                this.sendTargetUpdatePacket();
            }
        }
    }

    public boolean isValidTarget(ServerClient client) {
        return true;
    }

    public void tickMovement(float delta) {
        if (this.removed()) {
            return;
        }
        this.calcAcceleration(delta);
        if (this.target != null && this.target.playerMob != null && !this.isOnPickupCooldown()) {
            Point2D.Float tempPoint = new Point2D.Float(this.target.playerMob.getX() - this.getX(), this.target.playerMob.getY() - this.getY());
            float dist = (float)tempPoint.distance(0.0, 0.0);
            if (dist <= 0.0f || Float.isNaN(dist)) {
                dist = 1.0f;
            }
            float normX = tempPoint.x / dist;
            float normY = tempPoint.y / dist;
            Point2D.Float dir = new Point2D.Float(normX, normY);
            this.dx = dir.x * Math.max(70.0f, dist / 2.0f);
            this.dy = dir.y * Math.max(70.0f, dist / 2.0f);
        }
        if (this.dx != 0.0f) {
            this.moveX(delta);
            if (this.target == null && this.getLevel().collides((Shape)this.getCollision(), new CollisionFilter().mobCollision())) {
                this.moveX(-delta);
                this.dx = -this.dx * this.bouncy;
            }
        }
        if (this.dy != 0.0f) {
            this.moveY(delta);
            if (this.target == null && this.getLevel().collides((Shape)this.getCollision(), new CollisionFilter().mobCollision())) {
                this.moveY(-delta);
                this.dy = -this.dy * this.bouncy;
            }
        }
        if (Math.abs(this.dx) < 0.01f) {
            this.dx = 0.0f;
        }
        if (Math.abs(this.dy) < 0.01f) {
            this.dy = 0.0f;
        }
    }

    public void setReservedAuth(long auth) {
        this.reservedAuth = auth;
    }

    public long getReservedAuth() {
        return this.reservedAuth;
    }

    public void calcAcceleration(float delta) {
        float friction = 2.0f;
        if (this.dx != 0.0f) {
            this.dx += (0.0f - friction * this.dx) * delta / 250.0f;
        }
        if (this.dy != 0.0f) {
            this.dy += (0.0f - friction * this.dy) * delta / 250.0f;
        }
    }

    public boolean shouldDraw() {
        if (GlobalData.debugCheatActive()) {
            return true;
        }
        long auth = GameAuth.getAuthentication();
        return this.reservedAuth == -1L || this.reservedAuth == auth;
    }

    public boolean inLiquid() {
        return this.inLiquid(this.getX(), this.getY());
    }

    public boolean inLiquid(int x, int y) {
        return this.getLevel() != null && this.getLevel().inLiquid(x, y);
    }

    public int getBobbing() {
        return this.getBobbing(this.getX(), this.getY());
    }

    public int getBobbing(int x, int y) {
        if (!this.inLiquid(x, y)) {
            return 0;
        }
        return this.getLevel().getLevelTile(PickupEntity.getTileCoordinate(x), PickupEntity.getTileCoordinate(y)).getLiquidBobbing();
    }

    public Rectangle getCollision() {
        return new Rectangle((int)((double)this.x + this.collisionBox.getX()), (int)((double)this.y + this.collisionBox.getY()), (int)this.collisionBox.getWidth(), (int)this.collisionBox.getHeight());
    }

    public Rectangle getSelectBox() {
        return new Rectangle((int)((double)this.x + this.selectionBox.getX()), (int)((double)this.y + this.selectionBox.getY()), (int)this.selectionBox.getWidth(), (int)this.selectionBox.getHeight());
    }

    public boolean collidesWith(ServerClient client) {
        return this.getCollision().intersects(client.playerMob.getCollision());
    }

    public boolean collidesWith(PickupEntity item) {
        return this.getCollision().intersects(item.getCollision());
    }

    public void collidedWith(ServerClient client) {
        this.onPickup(client);
    }

    public void collidedWith(PickupEntity pickup) {
    }

    public void checkCollision() {
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "checkCollision", () -> {
            Rectangle collision = this.getCollision();
            int range = (int)GameMath.max(GameMath.diagonalMoveDistance(0, 0, collision.width, collision.height), 100.0);
            this.getLevel().entityManager.pickups.streamArea(this.x, this.y, range).filter(p -> p != this && !p.removed() && this.collidesWith((PickupEntity)p)).forEach(this::collidedWith);
        });
    }

    public Packet getPickupData() {
        return new Packet();
    }

    public void onPickup(ServerClient client) {
        this.getLevel().getServer().network.sendToClientsWithEntity(new PacketPickupEntityPickup(this, new Packet()), this);
        this.remove();
    }

    public void onPickup(ClientClient client, Packet data) {
    }

    public NetworkClient getTarget() {
        return this.target;
    }

    public void setTarget(ServerClient client) {
        this.target = client;
        this.sendTargetUpdatePacket();
    }

    public void setTarget(ClientClient client) {
        this.target = client;
    }

    public void resetTarget() {
        this.target = null;
    }

    public boolean shouldAddToDeletedLevelReturnedPickups() {
        return false;
    }

    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        if (debug) {
            StringTooltips tips = new StringTooltips();
            long time = this.getWorldEntity().getTime();
            tips.add("Spawned: " + GameUtils.formatSeconds((time - this.spawnTime) / 1000L) + " ago");
            long lifespanMillis = this.getLifespanMillis();
            if (lifespanMillis > 0L) {
                tips.add("Despawns in: " + GameUtils.formatSeconds((lifespanMillis - this.getTimeSinceSpawned()) / 1000L));
            }
            tips.add("UniqueID: " + this.getRealUniqueID());
            boolean onPickupCooldown = this.isOnPickupCooldown();
            if (!onPickupCooldown && perspective.isServerClient()) {
                onPickupCooldown = this.isOnPickupCooldown(perspective.getServerClient().authentication);
            }
            tips.add("Pickup: " + !onPickupCooldown);
            GameTooltipManager.addTooltip(tips, TooltipLocation.INTERACT_FOCUS);
        }
        return false;
    }

    public String toString() {
        return super.toString() + "{" + this.getUniqueID() + "}";
    }

    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Point tileOffset, Point positionOffset) {
        this.x += (float)positionOffset.x;
        this.y += (float)positionOffset.y;
    }
}

