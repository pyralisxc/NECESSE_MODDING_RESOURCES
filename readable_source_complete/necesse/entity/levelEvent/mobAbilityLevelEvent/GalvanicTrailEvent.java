/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LineHitbox;
import necesse.engine.util.Ray;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.entity.trails.LightningTrail;
import necesse.entity.trails.TrailVector;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;

public class GalvanicTrailEvent
extends MobAbilityLevelEvent
implements Attacker {
    private static final int totalPoints = 32;
    private static final int distance = 200;
    private static final float distanceMod = 1.0f;
    private static final int ticksToComplete = 5;
    private int startX;
    private int startY;
    private int targetX;
    private int targetY;
    private float xDir;
    private float yDir;
    private GameDamage damage;
    private float resilienceGain;
    private int seed;
    private int tickCounter;
    private int pointCounter;
    private ArrayList<Point2D.Float> points;
    private ArrayList<Integer> hits;
    private LightningTrail trail;

    public GalvanicTrailEvent() {
    }

    public GalvanicTrailEvent(Mob owner, GameDamage damage, float resilienceGain, int startX, int startY, int targetX, int targetY, int seed) {
        super(owner, new GameRandom(seed));
        this.damage = damage != null ? damage : new GameDamage(0.0f);
        this.resilienceGain = resilienceGain;
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.seed = seed;
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startX = reader.getNextInt();
        this.startY = reader.getNextInt();
        this.targetX = reader.getNextInt();
        this.targetY = reader.getNextInt();
        this.seed = reader.getNextInt();
        this.tickCounter = reader.getNextShortUnsigned();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.startX);
        writer.putNextInt(this.startY);
        writer.putNextInt(this.targetX);
        writer.putNextInt(this.targetY);
        writer.putNextInt(this.seed);
        writer.putNextShortUnsigned(this.tickCounter);
    }

    @Override
    public void init() {
        super.init();
        float l = (float)new Point(this.startX, this.startY).distance(this.targetX, this.targetY);
        this.xDir = (float)(this.targetX - this.startX) / l;
        this.yDir = (float)(this.targetY - this.startY) / l;
        this.points = this.generatePoints();
        this.trail = new LightningTrail(new TrailVector(this.startX, this.startY, this.xDir, this.yDir, 32.0f, 18.0f), this.level, new Color(116, 245, 253));
        this.trail.addNewPoint(new TrailVector(this.points.get(0), this.xDir, this.yDir, this.trail.thickness, 18.0f));
        if (this.isClient()) {
            this.level.entityManager.addTrail(this.trail);
        }
        this.hits = new ArrayList();
    }

    @Override
    public void clientTick() {
        if (this.isOver()) {
            return;
        }
        ++this.tickCounter;
        int expectedCounter = this.tickCounter * 32 / 5;
        while (this.pointCounter < expectedCounter) {
            ++this.pointCounter;
            if (this.pointCounter >= this.points.size()) {
                this.over();
                break;
            }
            Point2D.Float point = this.points.get(this.pointCounter);
            this.trail.addNewPoint(new TrailVector(point, this.xDir, this.yDir, this.trail.thickness, 18.0f));
            Point2D.Float lastPoint = this.points.get(this.pointCounter - 1);
            Point2D.Float midPoint = new Point2D.Float((point.x + lastPoint.x) / 2.0f, (point.y + lastPoint.y) / 2.0f);
            Point2D.Float norm = GameMath.normalize(point.x - lastPoint.x, point.y - lastPoint.y);
            float distance = (float)point.distance(lastPoint);
            for (int i = 0; i < 2; ++i) {
                this.level.entityManager.addParticle(midPoint.x + norm.x * GameRandom.globalRandom.nextFloat() * distance, midPoint.y + norm.y * GameRandom.globalRandom.nextFloat() * distance, Particle.GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 4.0), (float)(GameRandom.globalRandom.nextGaussian() * 4.0)).color(this.trail.col).height(18.0f);
            }
            if (this.pointCounter == this.points.size() - 1) {
                for (int j = 0; j < 20; ++j) {
                    this.level.entityManager.addParticle(lastPoint.x + norm.x * 4.0f, lastPoint.y + norm.y * 4.0f, Particle.GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 20.0), (float)(GameRandom.globalRandom.nextGaussian() * 20.0)).color(this.trail.col).height(18.0f).lifeTime(250);
                }
            }
            Line2D.Double line = new Line2D.Double(lastPoint.getX(), lastPoint.getY(), point.getX(), point.getY());
            LineHitbox hitbox = new LineHitbox(line, 20.0f);
            this.handleHits(hitbox, (Mob m) -> m.canBeHit(this) && !this.hasHit((Mob)m), null);
        }
    }

    @Override
    public void serverTick() {
        if (this.isOver()) {
            return;
        }
        ++this.tickCounter;
        int expectedCounter = this.tickCounter * 32 / 5;
        while (this.pointCounter < expectedCounter) {
            ++this.pointCounter;
            if (this.pointCounter >= this.points.size()) {
                this.over();
                break;
            }
            Point2D p1 = this.points.get(this.pointCounter - 1);
            Point2D p2 = this.points.get(this.pointCounter);
            Line2D.Double line = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            LineHitbox hitbox = new LineHitbox(line, 20.0f);
            this.handleHits(hitbox, (Mob m) -> !this.hasHit((Mob)m), null);
        }
    }

    private ArrayList<Point2D.Float> generatePoints() {
        ArrayList<Point2D.Float> out = new ArrayList<Point2D.Float>();
        GameRandom random = new GameRandom(this.seed);
        Point2D.Float perp = new Point2D.Float(-this.yDir, this.xDir);
        float lastDist = 0.0f;
        Point2D.Float lastPoint = new Point2D.Float(this.startX, this.startY);
        out.add(lastPoint);
        for (int i = 0; i < 32; ++i) {
            float fluctuation = (random.nextFloat() - 0.5f) * lastDist * 3.0f;
            lastDist = (random.nextFloat() + 1.0f) * 6.25f;
            Point2D.Float nextPoint = new Point2D.Float(lastPoint.x + this.xDir * lastDist - perp.x * fluctuation, lastPoint.y + this.yDir * lastDist - perp.y * fluctuation);
            Ray<LevelObjectHit> firstHit = GameUtils.castRayFirstHit(this.level, new Line2D.Float(lastPoint, nextPoint), new CollisionFilter().projectileCollision());
            if (firstHit != null && firstHit.targetHit != null) {
                out.add(new Point2D.Float((float)firstHit.x2, (float)firstHit.y2));
                break;
            }
            out.add(nextPoint);
            lastPoint = nextPoint;
        }
        return out;
    }

    @Override
    public void clientHit(Mob target, Packet content) {
        super.clientHit(target, content);
        this.hits.add(target.getHitCooldownUniqueID());
    }

    @Override
    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
        super.serverHit(target, content, clientSubmitted);
        target.isServerHit(this.damage, 0.0f, 0.0f, 0.0f, this);
        this.hits.add(target.getHitCooldownUniqueID());
        if (target.canGiveResilience(this.owner) && this.resilienceGain != 0.0f) {
            this.owner.addResilience(this.resilienceGain);
            this.resilienceGain = 0.0f;
        }
    }

    @Override
    public void hit(LevelObjectHit hit) {
        super.hit(hit);
        hit.getLevelObject().attackThrough(this.damage, this);
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("lightning", 2);
    }

    @Override
    public GameMessage getAttackerName() {
        if (this.owner != null) {
            return this.owner.getAttackerName();
        }
        return new LocalMessage("deaths", "unknownatt");
    }

    @Override
    public Mob getFirstAttackOwner() {
        return this.owner;
    }

    public boolean hasHit(Mob mob) {
        return this.hits.contains(mob.getHitCooldownUniqueID());
    }
}

