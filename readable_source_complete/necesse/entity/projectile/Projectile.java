/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketProjectileHit;
import necesse.engine.network.packet.PacketProjectilePositionUpdate;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.ProjectileModifierRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.IntersectionPoint;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.LineHitbox;
import necesse.engine.util.PointHashSet;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ProjectileHitboxMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.modifiers.ProjectileModifier;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public abstract class Projectile
extends Entity
implements Attacker {
    public final IDData idData = new IDData();
    protected ParticleTypeSwitcher spinningTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.COSMETIC);
    protected ProjectileModifier modifier;
    public float dx;
    public float dy;
    public float targetX;
    public float targetY;
    public float speed;
    public float traveledDistance;
    public float height;
    public int knockback;
    public int distance;
    public boolean heightBasedOnDistance;
    private GameDamage damage;
    public boolean doesImpactDamage;
    public boolean canBreakObjects;
    public boolean canHitMobs = true;
    public boolean clientHandlesHit;
    public NetworkClient handlingClient;
    public boolean isSolid;
    protected boolean canBounce = true;
    public int bouncing;
    public int piercing;
    protected int bounced;
    protected float angle;
    public boolean dropItem;
    public boolean sendPositionUpdate;
    protected long spawnTime;
    public int maxMovePerTick = 32;
    public float particleRandomOffset = 4.0f;
    public float particleRandomPerpOffset = 0.0f;
    public float particleDirOffset = -8.0f;
    public float particleSpeedMod = 0.1f;
    public float trailOffset = -10.0f;
    public final boolean hasHitbox;
    protected int hitboxMobID = -1;
    public float hitboxOffset = -5.0f;
    protected boolean isBoomerang = false;
    protected boolean returningToOwner = false;
    public boolean removeIfOutOfBounds = true;
    public boolean sendRemovePacket = true;
    public boolean isCircularHitbox = false;
    int team;
    public Trail trail;
    protected float width;
    protected float hitLength;
    protected boolean useWidthForCollision;
    public boolean givesLight;
    public float lightSaturation = 0.75f;
    protected float lightDistMoved;
    protected float tickDistMoved;
    protected double trailParticles;
    private float distToMove;
    private float angleToTurn;
    protected int amountHit;
    protected final MobHitCooldowns hitCooldowns;
    protected final PointHashSet objectHits;
    private int owner;
    private Mob ownerMob;
    protected GameTexture texture;
    protected GameTexture shadowTexture;
    protected SoundPlayer spawnSound;
    protected SoundPlayer moveSoundPlayer;

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public Projectile(boolean isNetworkCapable, boolean hasHitbox) {
        if (isNetworkCapable) {
            ProjectileRegistry.instance.applyIDData(this.getClass(), this.idData);
            this.texture = ProjectileRegistry.Textures.getTexture(this.getID());
            this.shadowTexture = ProjectileRegistry.Textures.getShadowTexture(this.getID());
        } else {
            this.idData.setData(-1, "unknown");
            this.texture = GameResources.error;
            this.shadowTexture = null;
        }
        this.hasHitbox = hasHitbox;
        this.setDamage(new GameDamage(0.0f));
        this.hitCooldowns = new MobHitCooldowns();
        this.objectHits = new PointHashSet();
        this.traveledDistance = 0.0f;
        this.lightDistMoved = 0.0f;
        this.tickDistMoved = 0.0f;
        this.isSolid = true;
        this.height = 18.0f;
        this.doesImpactDamage = true;
        this.knockback = 25;
        this.owner = -1;
    }

    public Projectile(boolean hasHitbox) {
        this(true, hasHitbox);
    }

    public Projectile() {
        this(false);
    }

    public void applyData(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, Mob owner) {
        this.applyData(x, y, targetX, targetY, speed, distance, damage, 25, owner);
    }

    public void applyData(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
        this.setDistance(distance);
    }

    public boolean shouldSendSpawnPacket() {
        return this.getID() != -1;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        writer.putNextInt(this.getUniqueID());
        writer.putNextBoolean(this.modifier != null);
        if (this.modifier != null) {
            writer.putNextShortUnsigned(this.modifier.getID());
            this.modifier.setupSpawnPacket(writer);
        }
        this.setupPositionPacket(writer);
        writer.putNextFloat(this.speed);
        writer.putNextInt(this.distance);
        writer.putNextShort((short)this.knockback);
        writer.putNextInt(this.getOwnerID());
        this.damage.writePacket(writer);
        if (this.hasHitbox) {
            writer.putNextBoolean(true);
            if (this.hitboxMobID == -1) {
                this.generateHitboxMob(true);
            }
            writer.putNextInt(this.hitboxMobID);
        } else {
            writer.putNextBoolean(false);
        }
        writer.putNextInt(this.amountHit);
        this.hitCooldowns.setupPacket(writer, false, this.getWorldEntity().getTime());
        writer.putNextShortUnsigned(this.objectHits.size());
        for (Point objectHit : this.objectHits) {
            writer.putNextInt(objectHit.x);
            writer.putNextInt(objectHit.y);
        }
    }

    public void applySpawnPacket(PacketReader reader) {
        this.setUniqueID(reader.getNextInt());
        boolean hasModifier = reader.getNextBoolean();
        if (hasModifier) {
            int modifierID = reader.getNextShortUnsigned();
            this.modifier = ProjectileModifierRegistry.getModifier(modifierID);
            if (this.modifier == null) {
                throw new IllegalStateException("Could not find projectile modifier with ID " + modifierID);
            }
            this.modifier.projectile = this;
            this.modifier.applySpawnPacket(reader);
        }
        this.applyPositionPacket(reader);
        this.updateAngle();
        this.speed = reader.getNextFloat();
        this.setDistance(reader.getNextInt());
        this.knockback = reader.getNextShort();
        this.setOwner(reader.getNextInt());
        this.damage = GameDamage.fromReader(reader);
        if (reader.getNextBoolean()) {
            this.hitboxMobID = reader.getNextInt();
        }
        this.amountHit = reader.getNextInt();
        this.hitCooldowns.applyPacket(reader, this.getWorldEntity().getTime());
        int totalObjectHits = reader.getNextShortUnsigned();
        for (int i = 0; i < totalObjectHits; ++i) {
            int tileX = reader.getNextInt();
            int tileY = reader.getNextInt();
            this.objectHits.add(tileX, tileY);
        }
    }

    public void setupPositionPacket(PacketWriter writer) {
        writer.putNextFloat(this.x);
        writer.putNextFloat(this.y);
        writer.putNextFloat(this.dx);
        writer.putNextFloat(this.dy);
        writer.putNextFloat(this.traveledDistance);
        if (this.modifier != null) {
            this.modifier.setupPositionPacket(writer);
        }
    }

    public void applyPositionPacket(PacketReader reader) {
        float x = reader.getNextFloat();
        float y = reader.getNextFloat();
        this.changePosition(x, y);
        this.dx = reader.getNextFloat();
        this.dy = reader.getNextFloat();
        this.traveledDistance = reader.getNextFloat();
        this.updateAngle();
        if (this.modifier != null) {
            this.modifier.applyPositionPacket(reader);
        }
    }

    public void changePosition(float x, float y) {
        double distance = new Point2D.Float(this.x, this.y).distance(x, y);
        this.x = x;
        this.y = y;
        if (distance > 10.0) {
            this.replaceTrail();
        }
    }

    public Projectile setModifier(ProjectileModifier modifier) {
        if (this.isInitialized()) {
            throw new IllegalStateException("Cannot set projectile modifier after initialization");
        }
        this.modifier = modifier;
        modifier.projectile = this;
        return this;
    }

    @Override
    public void onRegionChanged(int lastRegionX, int lastRegionY, int newRegionX, int newRegionY) {
        super.onRegionChanged(lastRegionX, lastRegionY, newRegionX, newRegionY);
        if (this.isServer() && this.shouldSendSpawnPacket()) {
            this.sendPacketToNewClientsWithRegion(lastRegionX, lastRegionY, newRegionX, newRegionY, () -> new PacketSpawnProjectile(this));
        }
    }

    @Override
    public void init() {
        super.init();
        Mob owner = this.getOwner();
        if (owner != null) {
            this.setTeam(owner.getTeam());
        }
        boolean bl = this.canBreakObjects = owner != null && owner.isPlayer;
        if (owner != null) {
            if (this.isServer()) {
                if (!Settings.strictServerAuthority) {
                    if (owner.isPlayer) {
                        this.handlingClient = ((PlayerMob)owner).getServerClient();
                    }
                    this.clientHandlesHit = true;
                }
            } else if (this.isClient()) {
                ClientClient client = this.getClient().getClient();
                if (!this.getClient().hasStrictServerAuthority()) {
                    if (client != null && owner == client.playerMob) {
                        this.handlingClient = client;
                    }
                    this.clientHandlesHit = true;
                }
            }
        }
        if (this.getLevel() != null && this.isClient()) {
            if (this.trail != null) {
                this.trail.removeOnFadeOut = true;
            }
            this.trail = this.getTrail();
            if (this.trail != null) {
                this.trail.removeOnFadeOut = false;
                this.getLevel().entityManager.addTrail(this.trail);
            }
        }
        if (this.hasHitbox) {
            this.generateHitboxMob(this.hitboxMobID == -1);
        }
        if (this.modifier != null) {
            this.modifier.init();
        }
        this.spawnTime = this.getWorldEntity().getTime();
        if (this.isClient()) {
            this.playSpawnSound();
            this.moveSoundPlayer = this.playMoveSound();
            if (this.removed()) {
                this.stopSounds();
            }
        }
    }

    @Override
    public void postInit() {
        super.postInit();
        if (this.modifier != null) {
            this.modifier.postInit();
        }
    }

    protected void replaceTrail() {
        if (this.trail != null) {
            this.trail.removeOnFadeOut = true;
            if (this.getLevel() != null && this.isClient()) {
                this.trail = this.getTrail();
                if (this.trail != null) {
                    this.trail.removeOnFadeOut = false;
                    this.getLevel().entityManager.addTrail(this.trail);
                }
            }
        }
    }

    public float tickMovement(float delta) {
        float moveY;
        float moveX;
        double totalDist;
        if (this.removed()) {
            return 0.0f;
        }
        if (this.isBoomerang) {
            Mob owner = this.getOwner();
            if (owner == null) {
                this.remove();
                return 0.0f;
            }
            if (this.returningToOwner) {
                this.setTarget(owner.x, owner.y);
            }
        }
        if (Double.isNaN(totalDist = Math.sqrt((moveX = this.getMoveDist(this.dx * this.speed, delta)) * moveX + (moveY = this.getMoveDist(this.dy * this.speed, delta)) * moveY)) || Double.isInfinite(totalDist)) {
            totalDist = 0.0;
        }
        this.moveDist(totalDist);
        if (this.removeIfOutOfBounds && !this.getLevel().regionManager.isTileLoaded(this.getTileX(), this.getTileY())) {
            this.remove();
        }
        return (float)totalDist;
    }

    @Override
    public void serverTick() {
        if (this.sendPositionUpdate && this.handlingClient == null) {
            this.sendServerUpdatePacket();
            this.sendPositionUpdate = false;
        }
    }

    @Override
    public void clientTick() {
        float particleChance;
        if (this.sendPositionUpdate) {
            if (this.isClient() && this.handlingClient == this.getLevel().getClient().getClient()) {
                this.sendClientUpdatePacket();
            }
            this.sendPositionUpdate = false;
        }
        if (this.givesLight && this.isClient()) {
            this.refreshParticleLight();
        }
        if ((particleChance = this.getParticleChance()) > 0.0f && (particleChance >= 1.0f || GameRandom.globalRandom.nextFloat() <= particleChance)) {
            this.spawnSpinningParticle();
        }
        if (this.moveSoundPlayer != null) {
            this.moveSoundPlayer.refreshLooping(0.5f);
        }
    }

    protected SoundPlayer playMoveSound() {
        SoundSettings sound = this.getMoveSound();
        if (sound == null) {
            return null;
        }
        sound.setFallOffDistanceIfNotSet(2000);
        SoundPlayer soundPlayer = SoundManager.playSound(sound, SoundEffect.effect(this));
        if (soundPlayer != null) {
            soundPlayer.refreshLooping(0.5f);
        }
        return soundPlayer;
    }

    protected SoundSettings getMoveSound() {
        return null;
    }

    public void refreshParticleLight() {
        Color color = this.getParticleColor();
        if (color == null) {
            this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y);
        } else {
            this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, color, this.lightSaturation);
        }
    }

    protected int getExtraSpinningParticles() {
        return 1;
    }

    protected void spawnSpinningParticle() {
        Color particleColor = this.getParticleColor();
        if (particleColor != null) {
            Point2D.Float perp = GameMath.getPerpendicularDir(this.dx, this.dy);
            float height = this.getHeight();
            float randomPerp = GameRandom.globalRandom.floatGaussian();
            this.modifySpinningParticle(this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * this.particleRandomOffset + this.dx * this.particleDirOffset + perp.x * randomPerp * this.particleRandomPerpOffset, this.y + GameRandom.globalRandom.floatGaussian() * this.particleRandomOffset + this.dy * this.particleDirOffset + perp.y * randomPerp * this.particleRandomPerpOffset, this.spinningTypeSwitcher.next()).movesConstant(this.dx * this.speed * this.particleSpeedMod, this.dy * this.speed * this.particleSpeedMod).color(particleColor).height(height));
            int extra = this.getExtraSpinningParticles();
            for (int i = 0; i < extra; ++i) {
                randomPerp = GameRandom.globalRandom.floatGaussian();
                this.modifySpinningParticle(this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * this.particleRandomOffset + this.dx * GameRandom.globalRandom.nextFloat() * (float)this.maxMovePerTick + this.dx * this.particleDirOffset + perp.x * randomPerp * this.particleRandomPerpOffset, this.y + GameRandom.globalRandom.floatGaussian() * this.particleRandomOffset + this.dy * GameRandom.globalRandom.nextFloat() * (float)this.maxMovePerTick + this.dy * this.particleDirOffset + perp.y * randomPerp * this.particleRandomPerpOffset, this.spinningTypeSwitcher.next()).movesConstant(this.dx * this.speed * this.particleSpeedMod, this.dy * this.speed * this.particleSpeedMod).color(this.getParticleColor()).height(height));
            }
        }
    }

    protected void modifySpinningParticle(ParticleOption particle) {
    }

    protected void spawnDeathParticles() {
        Color particleColor = this.getParticleColor();
        if (particleColor != null) {
            float height = this.getHeight();
            for (int i = 0; i < 8; ++i) {
                this.getLevel().entityManager.addParticle(this.x, this.y, this.spinningTypeSwitcher.next()).movesConstant(GameRandom.globalRandom.getIntBetween(5, 20) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1), GameRandom.globalRandom.getIntBetween(5, 20) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1)).color(this.getParticleColor()).height(height);
            }
        }
    }

    public float getParticleChance() {
        return 1.0f;
    }

    public Color getParticleColor() {
        return null;
    }

    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(150, 150, 150), 6.0f, 250, 18.0f);
    }

    public float getTrailThickness() {
        return this.trail.thickness;
    }

    protected Mob generateHitboxMob(boolean generateMobID) {
        Mob lastMob = this.getHitboxMob();
        if (lastMob != null) {
            lastMob.remove();
        }
        Mob m = this.constructHitboxMob();
        m.setLevel(this.getLevel());
        if (generateMobID) {
            m.getUniqueID(new GameRandom(this.getUniqueID()));
        } else {
            m.setUniqueID(this.hitboxMobID);
        }
        this.getLevel().entityManager.addMob(m, 10.0f, 10.0f);
        this.hitboxMobID = m.getUniqueID();
        return m;
    }

    public Mob getHitboxMob() {
        if (this.hitboxMobID == -1) {
            return null;
        }
        return this.getLevel().entityManager.mobs.get(this.hitboxMobID, false);
    }

    protected Mob constructHitboxMob() {
        ProjectileHitboxMob m = new ProjectileHitboxMob();
        m.projectile = this;
        return m;
    }

    public Rectangle getHitbox() {
        float size = Math.max(16.0f, this.getWidth());
        return new Rectangle((int)(this.x + this.dx * this.hitboxOffset - size / 2.0f), (int)(this.y + this.dy * this.hitboxOffset - size / 2.0f), (int)size, (int)size);
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void checkHitCollision(Line2D hitLine) {
        this.checkCollision(this.toHitbox(hitLine));
    }

    public final void setWidth(float width) {
        this.setWidth(width, false);
    }

    public final void setWidth(float width, float hitLength) {
        this.setWidth(width, false, hitLength);
    }

    public void setWidth(float width, boolean useWidthForCollision) {
        this.setWidth(width, useWidthForCollision, useWidthForCollision ? width / 2.0f : 0.0f);
    }

    public void setWidth(float width, boolean useWidthForCollision, float hitLength) {
        this.width = width;
        this.useWidthForCollision = useWidthForCollision;
        this.hitLength = hitLength;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHitLength() {
        return this.hitLength;
    }

    public int movePerIteration() {
        return GameMath.limit((int)(Math.pow(this.speed, 1.7f) / 250.0), 1, 8);
    }

    public void moveDist(double dist) {
        dist = Math.min(dist, (double)((float)this.distance + this.distToMove - this.traveledDistance));
        this.distToMove = (float)((double)this.distToMove + dist);
        if (this.distToMove < 0.0f || Float.isNaN(this.distToMove) || Float.isInfinite(this.distToMove)) {
            this.distToMove = 0.0f;
        }
        if (this.distToMove == 0.0f) {
            this.checkRemoved();
        }
        int distPerIteration = this.movePerIteration();
        while (this.distToMove > (float)distPerIteration && !this.removed()) {
            Point2D.Float startPos = new Point2D.Float(this.x, this.y);
            double movedDist = this.getDistanceMovedBeforeCollision(Math.min(distPerIteration, this.maxMovePerTick));
            this.distToMove = (float)((double)this.distToMove - movedDist);
            this.traveledDistance = (float)((double)this.traveledDistance + movedDist);
            this.tickDistMoved = (float)((double)this.tickDistMoved + movedDist);
            this.onMoveTick(startPos, movedDist);
            if (this.tickDistMoved >= (float)this.maxMovePerTick) {
                this.onMaxMoveTick();
                this.tickDistMoved -= (float)this.maxMovePerTick;
            }
            if (this.isClient() && this.givesLight) {
                this.lightDistMoved = (float)((double)this.lightDistMoved + movedDist);
                if (this.lightDistMoved > 32.0f) {
                    this.lightDistMoved -= 32.0f;
                    if (this.isClient()) {
                        this.refreshParticleLight();
                    }
                }
            }
            this.checkRemoved();
            this.addTrailPoint(this.x, this.y);
        }
    }

    public void addTrailPoint(float x, float y) {
        if (this.trail != null) {
            this.trail.addPoint(new TrailVector(x + this.dx * this.trailOffset, y + this.dy * this.trailOffset, this.dx, this.dy, this.getTrailThickness(), this.getHeight()));
        }
    }

    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        if (this.modifier != null) {
            this.modifier.onMoveTick(startPos, movedDist);
        }
    }

    public void spawnTrailParticle(Point2D.Float startPos, double movedDist, Color color, float countDiv, int lifeTime) {
        if (this.isClient()) {
            float height = this.getHeight();
            this.trailParticles += movedDist / (double)countDiv;
            if (this.trailParticles > 1.0) {
                Point2D.Float endPos = new Point2D.Float(this.x, this.y);
                Point2D.Float norm = GameMath.normalize(startPos.x - endPos.x, startPos.y - endPos.y);
                Point2D.Float perp = GameMath.getPerpendicularDir(norm.x, norm.y);
                while (this.trailParticles > 1.0) {
                    this.trailParticles -= 1.0;
                    float dist = (float)((double)GameRandom.globalRandom.nextFloat() * movedDist);
                    float perpDist = (GameRandom.globalRandom.nextFloat() - 0.5f) * 8.0f;
                    float xOffset = norm.x * dist + perp.x * perpDist;
                    float yOffset = norm.y * dist + perp.y * perpDist;
                    this.getLevel().entityManager.addParticle(startPos.x + xOffset, startPos.y + yOffset, this.spinningTypeSwitcher.next()).color(color).height(height).lifeTime(lifeTime);
                }
            }
        }
    }

    public void onMaxMoveTick() {
    }

    public void checkRemoved() {
        if (this.traveledDistance >= (float)this.distance) {
            if (this.isBoomerang) {
                if (!this.returningToOwner) {
                    this.returnToOwner();
                } else {
                    this.remove();
                }
            } else {
                this.doHitLogic(null, null, this.x, this.y);
                if (this.isServer() && this.dropItem) {
                    this.dropItem();
                }
                this.remove();
                this.sendRemovePacket = false;
            }
        }
    }

    protected void returnToOwner() {
        if (this.returningToOwner) {
            return;
        }
        Mob owner = this.getOwner();
        if (owner != null) {
            this.distance = (int)((float)this.distance + owner.getDistance(this.x, this.y) * 4.0f);
            this.setTarget(owner.x, owner.y);
            if (this.trail != null) {
                this.trail.addBreakPoint(new TrailVector(this.x + this.dx * this.trailOffset, this.y + this.dy * this.trailOffset, this.dx, this.dy, this.getTrailThickness(), this.getHeight()));
            }
        } else {
            this.distance *= 5;
        }
        if (this.piercing > 0) {
            this.clearHits();
        }
        this.returningToOwner = true;
        this.isSolid = false;
    }

    public boolean returningToOwner() {
        return this.returningToOwner;
    }

    public float getBoomerangUsage() {
        return 1.0f;
    }

    @Override
    public void remove() {
        if (!this.removed()) {
            Mob hitboxMob;
            Mob owner;
            if (this.isClient()) {
                this.spawnDeathParticles();
                this.stopSounds();
            }
            if (this.isBoomerang && (owner = this.getOwner()) instanceof ItemAttackerMob) {
                ((ItemAttackerMob)owner).boomerangs.remove(this);
            }
            if (this.hasHitbox && (hitboxMob = this.getHitboxMob()) != null) {
                hitboxMob.remove();
            }
        }
        this.sendRemovePacket = true;
        super.remove();
    }

    public void stopSounds() {
        if (this.spawnSound != null) {
            this.spawnSound.fadeOutAndStop(0.5f);
        }
    }

    public final Shape toHitbox(Line2D line) {
        if (line == null) {
            return null;
        }
        float width = this.getWidth();
        if (width > 0.0f) {
            if (this.isCircularHitbox) {
                return new LineHitbox(line, this.x, this.y, width);
            }
            return new LineHitbox(line, width);
        }
        return line;
    }

    protected CollisionFilter getLevelCollisionFilter() {
        Mob owner;
        CollisionFilter filter = new CollisionFilter().projectileCollision();
        if (this.canBreakObjects) {
            filter = filter.addFilter(tp -> !tp.object().object.attackThrough);
        }
        if ((owner = this.getOwner()) != null) {
            owner.modifyChasingCollisionFilter(filter, null);
        }
        return filter;
    }

    protected double getDistanceMovedBeforeCollision(double dist) {
        IntersectionPoint<LevelObjectHit> p;
        float width = this.getWidth();
        float hitLength = this.getHitLength();
        double hitDist = dist + (double)hitLength;
        Point2D.Float startPoint = new Point2D.Float(this.x, this.y);
        Point2D.Float endPoint = new Point2D.Float(this.x + (float)((double)this.dx * hitDist), this.y + (float)((double)this.dy * hitDist));
        Line2D.Float hitLine = new Line2D.Float(startPoint, endPoint);
        if (!this.isSolid) {
            this.x = (float)((double)this.x + (double)this.dx * dist);
            this.y = (float)((double)this.y + (double)this.dy * dist);
            this.checkHitCollision(hitLine);
            return dist;
        }
        Point2D.Float hitStartPoint = startPoint;
        Line2D.Float objectHitLine = hitLine;
        CollisionFilter collisionFilter = this.getLevelCollisionFilter();
        ArrayList<LevelObjectHit> hitCollisions = this.getLevel().getCollisions(hitLine, collisionFilter);
        if (hitCollisions.isEmpty() && this.useWidthForCollision) {
            int i = 8;
            while ((float)i < width / 2.0f) {
                hitStartPoint = GameMath.getPerpendicularPoint(startPoint, (float)i, this.dx, this.dy);
                Point2D.Float perpEndPoint = new Point2D.Float(hitStartPoint.x + (float)((double)this.dx * dist), hitStartPoint.y + (float)((double)this.dy * dist));
                Line2D.Float perpLine = new Line2D.Float(hitStartPoint, perpEndPoint);
                hitCollisions = this.getLevel().getCollisions(perpLine, collisionFilter);
                if (!hitCollisions.isEmpty()) {
                    objectHitLine = perpLine;
                    break;
                }
                hitStartPoint = GameMath.getPerpendicularPoint(startPoint, (float)(-i), this.dx, this.dy);
                perpEndPoint = new Point2D.Float(hitStartPoint.x + (float)((double)this.dx * dist), hitStartPoint.y + (float)((double)this.dy * dist));
                perpLine = new Line2D.Float(hitStartPoint, perpEndPoint);
                hitCollisions = this.getLevel().getCollisions(perpLine, collisionFilter);
                if (!hitCollisions.isEmpty()) {
                    objectHitLine = perpLine;
                    break;
                }
                i += 8;
            }
        }
        if ((p = this.getLevel().getCollisionPoint(hitCollisions, objectHitLine, false)) != null) {
            Point2D.Float objectHitOffset = new Point2D.Float(this.x - hitStartPoint.x, this.y - hitStartPoint.y);
            this.x = (float)p.getX() + objectHitOffset.x;
            this.y = (float)p.getY() + objectHitOffset.y;
            if (this.bounced >= this.getTotalBouncing() || !this.canBounce) {
                if (p.dir == IntersectionPoint.Dir.UP) {
                    this.y += 8.0f;
                } else if (p.dir == IntersectionPoint.Dir.RIGHT) {
                    this.x -= 8.0f;
                } else if (p.dir == IntersectionPoint.Dir.DOWN) {
                    this.y -= 8.0f;
                } else if (p.dir == IntersectionPoint.Dir.LEFT) {
                    this.x += 8.0f;
                }
            } else {
                if (this.trail != null) {
                    this.trail.addBreakPoint(new TrailVector((float)p.getX(), (float)p.getY(), this.dx, this.dy, this.getTrailThickness(), this.getHeight()));
                }
                if (p.dir == IntersectionPoint.Dir.RIGHT || p.dir == IntersectionPoint.Dir.LEFT) {
                    this.dx = -this.dx;
                    this.x += Math.signum(this.dx) * 4.0f;
                } else if (p.dir == IntersectionPoint.Dir.UP || p.dir == IntersectionPoint.Dir.DOWN) {
                    this.dy = -this.dy;
                    this.y += Math.signum(this.dy) * 4.0f;
                }
                this.updateAngle();
                this.sendPositionUpdate = true;
            }
            this.onHit(null, (LevelObjectHit)p.target, (float)p.getX(), (float)p.getY(), false, null);
            ++this.bounced;
            float distance = (float)hitStartPoint.distance(p);
            this.checkHitCollision(new Line2D.Float(this.x, this.y, this.x + this.dx * distance, this.y + this.dy * distance));
            return Math.max(distance, 1.0f);
        }
        this.x = (float)((double)this.x + (double)this.dx * dist);
        this.y = (float)((double)this.y + (double)this.dy * dist);
        this.checkHitCollision(hitLine);
        return dist;
    }

    public int getTotalBouncing() {
        Mob owner = this.getOwner();
        if (owner == null) {
            return this.bouncing;
        }
        return this.bouncing + owner.buffManager.getModifier(BuffModifiers.PROJECTILE_BOUNCES);
    }

    public float getMoveDist(float speed, float delta) {
        return speed * delta / 250.0f;
    }

    public boolean shouldAlwaysKnockAwayFromOwner() {
        return true;
    }

    public void onHit(Mob mob, LevelObjectHit object, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
        if (this.modifier != null && this.modifier.onHit(mob, object, x, y, fromPacket, packetSubmitter)) {
            return;
        }
        this.hit(mob, x, y, fromPacket, packetSubmitter);
        if (mob == null) {
            if (this.isClient()) {
                this.spawnWallHitParticles(x, y);
            }
            this.doHitLogic(mob, object, x, y);
            int bouncing = this.bouncing;
            Mob owner = this.getOwner();
            if (owner != null) {
                bouncing += owner.buffManager.getModifier(BuffModifiers.PROJECTILE_BOUNCES).intValue();
            }
            if (this.bounced >= bouncing || !this.canBounce) {
                if (this.isBoomerang) {
                    this.returnToOwner();
                } else {
                    if (this.dropItem && this.isServer()) {
                        this.dropItem();
                    }
                    this.remove();
                    this.sendRemovePacket = false;
                }
            }
        } else {
            boolean isFromServerPacket = this.isClient() && fromPacket;
            boolean canHit = this.checkHitCooldown(mob, this.isServer() && (packetSubmitter != null || this.handlingClient != null || this.clientHandlesHit && mob.isPlayer) ? 100 : 0);
            if (canHit && (this.amountHit() <= this.piercing || this.returningToOwner) || isFromServerPacket) {
                boolean addHit = true;
                if (this.isServer()) {
                    boolean isClientProjectile;
                    boolean bl = isClientProjectile = this.handlingClient != null || this.clientHandlesHit && mob.isPlayer;
                    if (packetSubmitter != null || !isClientProjectile) {
                        if (this.doesImpactDamage) {
                            this.applyDamage(mob, x, y);
                        }
                        this.doHitLogic(mob, object, x, y);
                        if (packetSubmitter != null) {
                            this.getServer().network.sendToClientsWithEntityExcept(new PacketProjectileHit(this, x, y, mob), this, packetSubmitter);
                        } else {
                            this.getServer().network.sendToClientsWithEntity(new PacketProjectileHit(this, x, y, mob), this);
                        }
                    } else {
                        addHit = false;
                    }
                } else if (this.isClient()) {
                    if (!this.getClient().hasStrictServerAuthority()) {
                        ClientClient client = this.getClient().getClient();
                        if (this.clientHandlesHit && mob == client.playerMob || this.handlingClient == client) {
                            if (!fromPacket) {
                                this.getClient().network.sendPacket(new PacketProjectileHit(this, x, y, mob));
                                this.startMobHitCooldown(mob);
                                this.doHitLogic(mob, object, x, y);
                            } else {
                                addHit = false;
                            }
                        } else {
                            this.doHitLogic(mob, object, x, y);
                        }
                    } else {
                        this.doHitLogic(mob, object, x, y);
                    }
                } else if (this.doesImpactDamage) {
                    this.applyDamage(mob, x, y);
                }
                if (addHit) {
                    this.addHit(mob);
                }
            }
            if (this.amountHit() > this.piercing) {
                if (this.isBoomerang) {
                    if (!this.returningToOwner) {
                        this.returnToOwner();
                    }
                } else {
                    this.remove();
                }
            }
        }
    }

    @Deprecated
    public void hit(Mob mob, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
    }

    public void applyDamage(Mob mob, float x, float y) {
        Mob owner;
        float knockbackDirX = mob.x - x * this.dx * 50.0f;
        float knockbackDirY = mob.y - y * this.dy * 50.0f;
        if (this.shouldAlwaysKnockAwayFromOwner() && (owner = this.getOwner()) != null) {
            knockbackDirX = mob.x - owner.x;
            knockbackDirY = mob.y - owner.y;
        }
        this.applyDamage(mob, x, y, knockbackDirX, knockbackDirY);
    }

    public void applyDamage(Mob mob, float x, float y, float knockbackDirX, float knockbackDirY) {
        mob.isServerHit(this.getDamage(), knockbackDirX, knockbackDirY, this.knockback, this);
    }

    public void attackThrough(LevelObjectHit hit) {
        if (this.hasHit(hit.tileX, hit.tileY)) {
            return;
        }
        hit.getLevelObject().attackThrough(this.getDamage(), this);
        this.addHit(hit.tileX, hit.tileY);
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        this.addTrailPoint(x, y);
        if (this.modifier != null) {
            this.modifier.doHitLogic(mob, object, x, y);
        }
        this.doHitLogic(mob, x, y);
        if (!this.isClient()) {
            return;
        }
        if (mob != null || object != null) {
            this.playHitSound(x, y);
        } else {
            this.playDisappearSound(x, y);
        }
    }

    @Deprecated
    public void doHitLogic(Mob mob, float x, float y) {
    }

    protected Color getWallHitColor() {
        Color c = this.getParticleColor();
        if (c == null) {
            return new Color(0.5f, 0.5f, 0.5f);
        }
        return c;
    }

    protected void spawnWallHitParticles(float x, float y) {
        Color c = this.getWallHitColor();
        if (c == null) {
            return;
        }
        float height = this.getHeight();
        for (int i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle(x, y, this.spinningTypeSwitcher.next()).movesConstant((float)GameRandom.globalRandom.nextGaussian() * 10.0f, (float)GameRandom.globalRandom.nextGaussian() * 10.0f).height(height).color(c).size((options, lifeTime, timeAlive, lifePercent) -> options.size(8, 8)).lifeTime(200);
        }
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        DeathMessageTable ownerDeathMessages;
        Mob owner = this.getOwner();
        if (owner != null && (ownerDeathMessages = owner.getDeathMessages()) != null) {
            return ownerDeathMessages;
        }
        return this.getDeathMessages("projectile", 3);
    }

    @Override
    public GameMessage getAttackerName() {
        Mob owner = this.getOwner();
        if (owner != null) {
            return owner.getAttackerName();
        }
        return new LocalMessage("deaths", "unknownatt");
    }

    @Override
    public Mob getFirstAttackOwner() {
        return this.getOwner();
    }

    @Override
    public int getAttackerUniqueID() {
        return this.getUniqueID();
    }

    protected void playHitSound(float x, float y) {
        SoundSettings svp = this.getHitSound();
        if (svp == null || svp.sounds == null) {
            return;
        }
        svp.setPitchVarianceIfNotSet(0.03f);
        SoundManager.playSound(svp, x, y);
    }

    protected SoundSettings getHitSound() {
        return null;
    }

    public void playDisappearSound(float x, float y) {
        SoundManager.playSound(this.getDisappearSound(), x, y);
    }

    protected SoundSettings getDisappearSound() {
        return null;
    }

    public void playSpawnSound() {
        this.spawnSound = SoundManager.playSound(this.getSpawnSound(), this);
    }

    protected SoundSettings getSpawnSound() {
        return null;
    }

    protected CollisionFilter getAttackThroughCollisionFilter() {
        return new CollisionFilter().projectileCollision().attackThroughCollision();
    }

    protected final void checkCollision(Shape hitbox) {
        Mob ownerMob = this.getOwner();
        if (ownerMob != null && this.isBoomerang && this.returningToOwner && hitbox.intersects(ownerMob.getHitBox())) {
            this.remove();
        }
        if (this.isServer() && this.canBreakObjects) {
            ArrayList<LevelObjectHit> hits = this.getLevel().getCollisions(hitbox, this.getAttackThroughCollisionFilter());
            for (LevelObjectHit hit : hits) {
                if (hit.invalidPos() || !hit.getObject().attackThrough) continue;
                this.attackThrough(hit);
            }
        }
        if (this.canHitMobs) {
            List targets = this.streamTargets(ownerMob, hitbox).filter(m -> this.canHit((Mob)m) && hitbox.intersects(m.getHitBox())).filter(m -> !this.isSolid || m.canHitThroughCollision() || !this.perpLineCollidesWithLevel(m.x, m.y)).collect(Collectors.toCollection(LinkedList::new));
            for (Mob target : targets) {
                this.onHit(target, null, this.x, this.y, false, null);
            }
        }
    }

    public boolean canHit(Mob mob) {
        return mob.canBeHit(this);
    }

    protected boolean perpLineCollidesWithLevel(float x, float y) {
        Line2D.Float perpLine = new Line2D.Float(x, y, x + -this.dy, y + this.dx);
        Point2D p = GameMath.getIntersectionPoint(new Line2D.Float(this.x, this.y, this.x + this.dx, this.y + this.dy), perpLine, true);
        if (p != null) {
            Line2D.Float colLine = new Line2D.Float(x, y, (float)p.getX(), (float)p.getY());
            return this.getLevel().collides(colLine, this.getLevelCollisionFilter());
        }
        return false;
    }

    protected Stream<Mob> streamTargets(Mob owner, Shape hitBounds) {
        if (owner != null) {
            return GameUtils.streamTargets(owner, hitBounds);
        }
        return Stream.concat(this.getLevel().entityManager.mobs.streamInRegionsShape(hitBounds, 1), GameUtils.streamNetworkClients(this.getLevel()).filter(c -> !c.isDead() && c.hasSpawned()).map(sc -> sc.playerMob));
    }

    protected void dropItem() {
    }

    public void setDamage(GameDamage damage) {
        if (damage == null) {
            return;
        }
        this.damage = damage;
    }

    public GameDamage getDamage() {
        return this.damage;
    }

    public void setDir(float dx, float dy) {
        this.setTarget(this.x + dx * 100.0f, this.y + dy * 100.0f);
    }

    public void setTarget(float x, float y) {
        this.targetX = x;
        this.targetY = y;
        Point2D.Float dir = GameMath.normalize(x - this.x, y - this.y);
        if (dir.x == 0.0f && dir.y == 0.0f) {
            this.dx = 1.0f;
            this.dy = 0.0f;
        } else {
            this.dx = dir.x;
            this.dy = dir.y;
        }
        this.angle = (float)Math.toDegrees(Math.atan2(this.dy, this.dx));
        this.angle += 90.0f;
        this.fixAngle();
    }

    public static Point2D.Float getPredictedTargetPos(float targetStartX, float targetStartY, float targetDx, float targetDy, float myStartX, float myStartY, float mySpeed, float distanceOffset) {
        float distance = (float)new Point2D.Float(myStartX, myStartY).distance(targetStartX, targetStartY) + distanceOffset;
        float travelTime = Projectile.getTravelTimeMillis(mySpeed, distance);
        float nextX = Projectile.getPositionAfterMillis(targetDx, travelTime);
        float nextY = Projectile.getPositionAfterMillis(targetDy, travelTime);
        return new Point2D.Float(targetStartX + nextX, targetStartY + nextY);
    }

    public static Point2D.Float getPredictedTargetPos(Mob target, float myStartX, float myStartY, float mySpeed, float distanceOffset) {
        return Projectile.getPredictedTargetPos(target.x, target.y, target.dx, target.dy, myStartX, myStartY, mySpeed, distanceOffset);
    }

    public void setTargetPrediction(float x, float y, float dx, float dy, float distanceOffset) {
        Point2D.Float targetPos = Projectile.getPredictedTargetPos(x, y, dx, dy, this.x, this.y, this.speed, distanceOffset);
        this.setTarget(targetPos.x, targetPos.y);
    }

    public void setTargetPrediction(Mob mob, float distanceOffset) {
        Mob mount;
        for (int i = 0; i < 10 && (mount = mob.getMount()) != null; ++i) {
            mob = mount;
        }
        this.setTargetPrediction(mob.x, mob.y, mob.dx, mob.dy, distanceOffset);
    }

    public void setTargetPrediction(Mob mob) {
        this.setTargetPrediction(mob, 0.0f);
    }

    public void fixAngle() {
        this.angle = GameMath.fixAngle(this.angle);
    }

    public void setAngle(float angle) {
        this.angle = angle;
        this.fixAngle();
        Point2D.Float dir = GameMath.getAngleDir(this.angle - 90.0f);
        this.dx = dir.x;
        this.dy = dir.y;
        this.targetX = this.x + this.dx * 100.0f;
        this.targetY = this.y + this.dy * 100.0f;
    }

    public void updateAngle() {
        this.angle = (float)Math.toDegrees(Math.atan2(this.dy, this.dx));
        this.angle += 90.0f;
        this.fixAngle();
    }

    public float getAngle() {
        return this.angle % 360.0f;
    }

    public boolean turnToward(float x, float y, float angle) {
        this.angleToTurn += angle;
        float anglePerIteration = 2.0f;
        boolean out = false;
        while (this.angleToTurn >= anglePerIteration) {
            float angleToTarget = this.getAngleToTarget(x, y);
            float dif = this.getAngleDifference(angleToTarget);
            if (Math.abs(dif) - anglePerIteration < 1.0f) {
                anglePerIteration = Math.abs(dif) - 1.0f;
                this.angleToTurn = 0.0f;
                out = true;
            }
            this.setAngle(this.angle + anglePerIteration * Math.signum(dif));
            if (out) break;
            this.angleToTurn -= anglePerIteration;
        }
        return out;
    }

    public static float getAngleToTarget(float thisX, float thisY, float targetX, float targetY) {
        float dx = targetX - thisX;
        float dy = targetY - thisY;
        float angleToTarget = (float)Math.toDegrees(Math.atan(dy / dx));
        angleToTarget = dx < 0.0f ? (angleToTarget += 270.0f) : (angleToTarget += 90.0f);
        return angleToTarget;
    }

    public float getAngleToTarget(float x, float y) {
        return Projectile.getAngleToTarget(this.x, this.y, x, y);
    }

    public float getAngleDifference(float angle) {
        return GameMath.getAngleDifference(angle, this.getAngle());
    }

    public float getHeight() {
        if (!this.heightBasedOnDistance) {
            return this.height;
        }
        float distPerc = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0f, 1.0f);
        return this.height - (float)((int)((this.height - 4.0f) * distPerc));
    }

    public void setOwner(int uniqueID, Level level) {
        this.owner = uniqueID;
        if (level == null) {
            return;
        }
        this.ownerMob = GameUtils.getLevelMob(uniqueID, level);
        this.setOwner(this.ownerMob);
    }

    public void setOwner(int uniqueID) {
        this.setOwner(uniqueID, this.getLevel());
    }

    public void setOwner(Mob owner) {
        if (owner == null) {
            return;
        }
        this.owner = owner.getUniqueID();
        this.team = owner.getTeam();
        this.ownerMob = owner;
    }

    public Mob getOwner() {
        if (this.ownerMob == null && this.owner != 0) {
            this.setOwner(this.owner);
        }
        return this.ownerMob;
    }

    public int getOwnerID() {
        return this.owner;
    }

    public boolean checkHitCooldown(Mob target, int tolerance) {
        return this.hitCooldowns.canHit(target, this.getWorldEntity().getTime(), tolerance);
    }

    public void addHit(Mob target) {
        this.startHitCooldown(target);
        ++this.amountHit;
    }

    public void startMobHitCooldown(Mob target) {
        target.startHitCooldown();
    }

    public void startHitCooldown(Mob target) {
        this.hitCooldowns.startCooldown(target, this.getWorldEntity().getTime());
    }

    public int amountHit() {
        return this.amountHit;
    }

    public boolean hasHit(int tileX, int tileY) {
        return this.objectHits.contains(tileX, tileY);
    }

    public void addHit(int tileX, int tileY) {
        this.objectHits.add(tileX, tileY);
    }

    public void clearHits() {
        this.hitCooldowns.resetCooldowns();
        this.objectHits.clear();
    }

    public int getTeam() {
        return this.team;
    }

    public boolean isSameTeam(Mob other) {
        if (this.getTeam() == -1 || other.getTeam() == -1) {
            return false;
        }
        return this.getTeam() == other.getTeam();
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public float getLifeTime() {
        return this.traveledDistance / (float)this.distance;
    }

    public void sendServerUpdatePacket() {
        if (this.handlingClient != null) {
            this.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketProjectilePositionUpdate(this), this, (ServerClient)this.handlingClient);
        } else {
            this.getLevel().getServer().network.sendToClientsWithEntity(new PacketProjectilePositionUpdate(this), this);
        }
    }

    public void sendClientUpdatePacket() {
        this.getLevel().getClient().network.sendPacket(new PacketProjectilePositionUpdate(this));
    }

    public Shape getSelectBox() {
        float height = this.getHeight();
        float size = Math.max(this.getWidth(), 16.0f);
        float size2 = size / 2.0f;
        return new LineHitbox(new Line2D.Float(this.x - this.dx * size2, this.y - this.dy * size2 - height, this.x + this.dx * size2, this.y + this.dy * size2 - height), size);
    }

    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        if (debug) {
            StringTooltips tips = new StringTooltips();
            tips.add("Projectile: " + this.getStringID() + " (" + this.getID() + ")");
            tips.add("UniqueID: " + this.getUniqueID());
            Mob owner = this.getOwner();
            if (owner != null) {
                tips.add("Owner: " + owner.getDisplayName() + " (" + this.getOwnerID() + ")");
            } else {
                tips.add("Owner: " + this.getOwnerID());
            }
            tips.add("Pos: " + this.x + ", " + this.y);
            tips.add("Delta: " + this.dx + ", " + this.dy);
            tips.add("Speed: " + this.speed + ", Angle: " + this.getAngle());
            tips.add("Width: " + this.getWidth());
            if (this.hasHitbox) {
                tips.add("HitboxMobID: " + this.hitboxMobID);
            }
            GameTooltipManager.addTooltip(tips, TooltipLocation.INTERACT_FOCUS);
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.trail != null) {
            this.trail.removeOnFadeOut = true;
        }
    }

    protected float getFadeAlphaDistance(int fadeInDistance, int fadeOutDistance) {
        if (fadeInDistance > 0 && this.traveledDistance < (float)fadeInDistance) {
            return this.traveledDistance / (float)fadeInDistance;
        }
        if (fadeOutDistance > 0 && this.traveledDistance > (float)(this.distance - fadeOutDistance)) {
            return Math.abs((Math.min(this.traveledDistance, (float)this.distance) - (float)(this.distance - fadeOutDistance)) / (float)fadeOutDistance - 1.0f);
        }
        return 1.0f;
    }

    protected float getFadeAlphaTime(int fadeInMilliseconds, int fadeOutMilliseconds) {
        float fadeOutDist;
        float fadeInDist;
        if (fadeInMilliseconds > 0 && this.traveledDistance < (fadeInDist = this.getMoveDist(this.speed, fadeInMilliseconds))) {
            return this.traveledDistance / fadeInDist;
        }
        if (fadeOutMilliseconds > 0 && this.traveledDistance > (float)this.distance - (fadeOutDist = this.getMoveDist(this.speed, fadeOutMilliseconds))) {
            return Math.abs((Math.min(this.traveledDistance, (float)this.distance) - ((float)this.distance - fadeOutDist)) / fadeOutDist - 1.0f);
        }
        return 1.0f;
    }

    protected void addShadowDrawables(OrderableDrawables list, int drawX, int drawY, GameLight light, float angle, int centerY) {
        this.addShadowDrawables(list, drawX, drawY, light, o -> o.rotate(angle, this.shadowTexture.getWidth() / 2, centerY));
    }

    protected void addShadowDrawables(OrderableDrawables list, int drawX, int drawY, GameLight light, float angle, int centerX, int centerY) {
        this.addShadowDrawables(list, drawX, drawY, light, o -> o.rotate(angle, centerX, centerY));
    }

    protected void addShadowDrawables(OrderableDrawables list, int drawX, int drawY, GameLight light, Function<TextureDrawOptionsEnd, TextureDrawOptionsEnd> modifier) {
        this.addShadowDrawables(list, this.shadowTexture.initDraw().next(), drawX, drawY, light, modifier);
    }

    protected void addShadowDrawables(OrderableDrawables list, TextureDrawOptionsEnd options, int drawX, int drawY, GameLight light, Function<TextureDrawOptionsEnd, TextureDrawOptionsEnd> modifier) {
        if (this.shadowTexture == null) {
            return;
        }
        TextureDrawOptionsEnd shadowOptions = (options = modifier.apply(options.light(light))).pos(drawX, drawY);
        if (shadowOptions != null) {
            list.add(tm -> shadowOptions.draw());
        }
    }

    protected void addServerProjectileDrawable(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Projectile other;
        Server localServer;
        if (GlobalData.debugActive() && this.isClient() && (localServer = this.getLevel().getClient().getLocalServer()) != null && (other = localServer.world.getLevel((LevelIdentifier)this.getLevel().getIdentifier()).entityManager.projectiles.get(this.getUniqueID(), false)) != null) {
            other.addDrawables(list, tileList, topList, overlayList, level, tickManager, camera, perspective);
            int drawX = camera.getDrawX(other.x);
            int drawY = camera.getDrawY(other.y - other.getHeight());
            final DrawOptions asterisk = () -> FontManager.bit.drawChar(drawX, drawY, '*', new FontOptions(12));
            list.add(new EntityDrawable(this){

                @Override
                public int getSortY() {
                    return Integer.MAX_VALUE;
                }

                @Override
                public void draw(TickManager tickManager) {
                    asterisk.draw();
                }
            });
        }
    }

    @Override
    public void addDebugDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        this.drawHitBox(overlayList, camera);
    }

    protected void drawHitBox(OrderableDrawables overlayList, GameCamera camera) {
        float hitLength = Math.max(this.getHitLength(), 16.0f);
        Line2D.Float hitLine = new Line2D.Float(this.x, this.y, this.x + this.dx * hitLength, this.y + this.dy * hitLength);
        Shape shape = this.toHitbox(hitLine);
        overlayList.add(tickManager -> Renderer.drawShape(shape, camera, true, 1.0f, 0.0f, 0.0f, 0.3f));
    }
}

