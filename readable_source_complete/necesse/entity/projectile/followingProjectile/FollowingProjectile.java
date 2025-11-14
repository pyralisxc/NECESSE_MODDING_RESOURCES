/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.followingProjectile;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.function.Predicate;
import necesse.engine.Settings;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketProjectileTargetUpdate;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LineHitbox;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public abstract class FollowingProjectile
extends Projectile {
    public Entity target;
    public Point targetPos;
    public float turnSpeed = 0.5f;
    public boolean clearTargetPosWhenAligned;
    public boolean clearTargetWhenAligned;
    public boolean stopsAtTarget;
    private float originalSpeed;
    private boolean isStoppedAtTarget;
    public float angleLeftToTurn = -1.0f;
    private int updateTicker;

    public FollowingProjectile(boolean isNetworkCapable, boolean hasHitbox) {
        super(isNetworkCapable, hasHitbox);
    }

    public FollowingProjectile(boolean hasHitbox) {
        super(hasHitbox);
    }

    public FollowingProjectile() {
    }

    public void addTargetData(PacketWriter writer) {
        if (this.target != null) {
            writer.putNextMaxValue(1, 2);
            if (this.target == null) {
                writer.putNextInt(-1);
            } else {
                writer.putNextInt(this.target.getUniqueID());
            }
        } else if (this.targetPos != null) {
            writer.putNextMaxValue(2, 2);
            writer.putNextInt(this.targetPos.x);
            writer.putNextInt(this.targetPos.y);
        } else {
            writer.putNextMaxValue(0, 2);
        }
    }

    public void applyTargetData(PacketReader reader) {
        switch (reader.getNextMaxValue(2)) {
            case 0: {
                this.target = null;
                this.targetPos = null;
                break;
            }
            case 1: {
                this.targetPos = null;
                int targetID = reader.getNextInt();
                if (targetID == -1) {
                    this.target = null;
                    break;
                }
                this.target = GameUtils.getLevelMob(targetID, this.getLevel());
                break;
            }
            case 2: {
                this.target = null;
                int x = reader.getNextInt();
                int y = reader.getNextInt();
                this.targetPos = new Point(x, y);
            }
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        this.addTargetData(writer);
        writer.putNextBoolean(this.angleLeftToTurn >= 0.0f);
        if (this.angleLeftToTurn >= 0.0f) {
            writer.putNextFloat(this.angleLeftToTurn);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.applyTargetData(reader);
        if (reader.getNextBoolean()) {
            this.angleLeftToTurn = reader.getNextFloat();
        }
    }

    @Override
    public void init() {
        super.init();
        this.updateTicker = 0;
        this.stopsAtTarget = false;
        this.isStoppedAtTarget = false;
        this.originalSpeed = 0.0f;
    }

    @Override
    public float tickMovement(float delta) {
        float toTarget;
        if (this.removed()) {
            return 0.0f;
        }
        float f = toTarget = this.hasTarget() ? (float)new Point(this.getTargetX(), this.getTargetY()).distance(this.x, this.y) : 1.0f;
        if (this.isStoppedAtTarget && toTarget >= 1.0f) {
            this.speed = this.originalSpeed;
            this.isStoppedAtTarget = false;
        }
        if (this.isStoppedAtTarget) {
            float moveY;
            float moveX = this.getMoveDist(this.dx * this.originalSpeed, delta);
            double totalDist = Math.sqrt(moveX * moveX + (moveY = this.getMoveDist(this.dy * this.originalSpeed, delta)) * moveY);
            if (Double.isNaN(totalDist)) {
                totalDist = 0.0;
            }
            this.traveledDistance = (float)((double)this.traveledDistance + totalDist);
            this.checkRemoved();
            float width = this.getWidth();
            int size = (int)(width <= 0.0f ? 2.0 : Math.ceil(width));
            this.checkCollision(new Rectangle(this.getX() - size / 2, this.getY() - size / 2, size, size));
            return (float)totalDist;
        }
        return super.tickMovement(delta);
    }

    @Override
    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        if (!this.isStoppedAtTarget) {
            this.updateTarget();
            if (this.hasTarget()) {
                float delta = (float)movedDist;
                int tx = this.getTargetX();
                int ty = this.getTargetY();
                float angle = this.getTurnSpeed(tx, ty, delta);
                if (this.angleLeftToTurn >= 0.0f) {
                    angle = Math.min(this.angleLeftToTurn, angle);
                    this.angleLeftToTurn -= angle;
                }
                if (this.turnToward(tx, ty, angle)) {
                    if (this.clearTargetPosWhenAligned) {
                        this.targetPos = null;
                    }
                    if (this.clearTargetWhenAligned) {
                        this.target = null;
                    }
                    if (this.stopsAtTarget) {
                        float toTarget;
                        float f = toTarget = this.hasTarget() ? (float)new Point(this.getTargetX(), this.getTargetY()).distance(this.x, this.y) : 1.0f;
                        if ((double)toTarget <= movedDist) {
                            this.isStoppedAtTarget = true;
                            this.originalSpeed = this.speed;
                        }
                    }
                }
            }
        }
        if (this.isStoppedAtTarget) {
            this.speed = 0.0f;
            this.x = this.getTargetX();
            this.y = this.getTargetY();
        }
    }

    public float getOriginalSpeed() {
        return this.originalSpeed;
    }

    public final float getTurnSpeed(float delta) {
        return this.turnSpeed * delta;
    }

    public float getTurnSpeed(int targetX, int targetY, float delta) {
        return this.getTurnSpeed(delta);
    }

    protected float dynamicTurnSpeedMod(int targetX, int targetY, float startDistance) {
        float distance = (float)new Point(targetX, targetY).distance(this.getX(), this.getY());
        if (distance < startDistance && distance > 5.0f) {
            return Math.abs(distance - startDistance) / startDistance * 4.0f + 1.0f;
        }
        return 1.0f;
    }

    protected float dynamicTurnSpeedMod(int targetX, int targetY) {
        return this.dynamicTurnSpeedMod(targetX, targetY, this.speed * 1.5f);
    }

    protected float invDynamicTurnSpeedMod(int targetX, int targetY, float maxDistance) {
        float distance = (float)new Point(targetX, targetY).distance(this.getX(), this.getY());
        if (distance > maxDistance && distance > 5.0f) {
            float deltaAngle = Math.abs(this.getAngleDifference(this.getAngleToTarget(targetX, targetY)));
            float mod = Math.abs(distance - maxDistance) / maxDistance;
            if (deltaAngle < 90.0f) {
                mod *= 3.0f;
            }
            return 1.0f + mod;
        }
        return 1.0f;
    }

    protected float invDynamicTurnSpeedMod(int targetX, int targetY) {
        return this.invDynamicTurnSpeedMod(targetX, targetY, this.getTurnRadius());
    }

    public static int getTurnRadius(float velocity, float turnSpeed) {
        float distancePerDegree = velocity / turnSpeed;
        float circumference = distancePerDegree * 360.0f;
        return (int)((double)circumference / (Math.PI * 2));
    }

    public int getTurnRadius() {
        return FollowingProjectile.getTurnRadius(this.getMoveDist(this.speed, 1.0f), this.getTurnSpeed(1.0f));
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.isStoppedAtTarget) {
            this.updateTarget();
        }
        ++this.updateTicker;
        if (this.updateTicker >= 20) {
            this.sendServerTargetUpdate(false);
            this.updateTicker = 0;
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isStoppedAtTarget) {
            this.updateTarget();
        }
    }

    public void sendClientTargetUpdate() {
        this.getLevel().getClient().network.sendPacket(new PacketProjectileTargetUpdate(this));
    }

    public void sendServerTargetUpdate(boolean forced) {
        if (Settings.strictServerAuthority || forced) {
            this.getLevel().getServer().network.sendToClientsWithEntity(new PacketProjectileTargetUpdate(this), this);
        }
    }

    public boolean hasTarget() {
        return this.target != null && !this.target.removed() || this.targetPos != null;
    }

    public int getTargetX() {
        if (this.target != null && !this.target.removed()) {
            return this.target.getX();
        }
        if (this.targetPos != null) {
            return this.targetPos.x;
        }
        return this.getX();
    }

    public int getTargetY() {
        if (this.target != null && !this.target.removed()) {
            return this.target.getY();
        }
        if (this.targetPos != null) {
            return this.targetPos.y;
        }
        return this.getY();
    }

    public void updateTarget() {
    }

    public void findTarget(Predicate<Mob> filter, float frontOffset, float maxDistance) {
        this.target = null;
        int targetX = (int)(this.x + this.dx * frontOffset);
        int targetY = (int)(this.y + this.dy * frontOffset);
        ComputedObjectValue nextTarget = GameUtils.streamTargetsRange(this.getOwner(), targetX, targetY, (int)maxDistance).filter(m -> m != null && !m.removed()).filter(filter).map(m -> new ComputedObjectValue<Mob, Double>((Mob)m, () -> m.getPositionPoint().distance(targetX, targetY))).min(Comparator.comparing(ComputedValue::get)).orElse(null);
        if (nextTarget != null && (Double)nextTarget.get() <= (double)maxDistance) {
            this.target = (Entity)nextTarget.object;
        }
    }

    public void drawDebug(GameCamera camera) {
        Mob m;
        FontOptions fontOptions = new FontOptions(16);
        int drawX = camera.getDrawX(this.x) - 16;
        int drawY = camera.getDrawY(this.y - this.getHeight());
        float angle = this.getAngleToTarget(camera.getMouseLevelPosX(), camera.getMouseLevelPosY());
        float dif = this.getAngleDifference(angle);
        FontManager.bit.drawString(drawX, drawY - 16, "" + dif, fontOptions);
        FontManager.bit.drawString(drawX, drawY - 32, "" + angle, fontOptions);
        FontManager.bit.drawString(drawX, drawY - 48, "" + this.getAngle(), fontOptions);
        int targetX = (int)(this.x + this.dx * 160.0f);
        int targetY = (int)(this.y + this.dy * 160.0f);
        Renderer.initQuadDraw(6, 6).color(0.0f, 1.0f, 0.0f).draw(camera.getDrawX(targetX) - 3, camera.getDrawY(targetY) - 3);
        if (this.target != null) {
            Renderer.initQuadDraw(30, 30).color(1.0f, 0.0f, 0.0f, 0.5f).draw(camera.getDrawX(this.target.getX()) - 15, camera.getDrawY(this.target.getY()) - 15);
        }
        FontManager.bit.drawString(drawX, drawY - 64, this.dx + ", " + this.dy, fontOptions);
        FontManager.bit.drawString(drawX, drawY - 80, "" + this.getTeam(), fontOptions);
        FontManager.bit.drawString(drawX, drawY - 96, "" + this.getOwnerID(), fontOptions);
        FontManager.bit.drawString(drawX, drawY - 112, "" + (this.target == null ? "null" : Integer.valueOf(this.target.getUniqueID())), fontOptions);
        LineHitbox.fromAngled(camera.getDrawX(this.x), camera.getDrawY(this.y), this.getAngle(), 10.0f, this.getWidth()).draw(1.0f, 0.0f, 0.0f, 0.5f);
        Mob mob = m = this.target != null ? (Mob)this.target : (Mob)this.streamTargets(this.getOwner(), null).min((m1, m2) -> (int)(m1.getDistance(this.x, this.y) - m2.getDistance(this.x, this.y))).orElse(null);
        if (m != null) {
            Line2D.Float perpLine = new Line2D.Float(m.x, m.y, m.x + -this.dy, m.y + this.dx);
            Point2D p = GameMath.getIntersectionPoint(new Line2D.Float(this.x, this.y, this.x + this.dx, this.y + this.dy), perpLine, true);
            Renderer.drawLineRGBA(camera.getDrawX(m.x), camera.getDrawY(m.y), camera.getDrawX((float)p.getX()), camera.getDrawY((float)p.getY()), 1.0f, 0.0f, 0.0f, 1.0f);
            Renderer.drawLineRGBA(camera.getDrawX(this.x), camera.getDrawY(this.y), camera.getDrawX((float)p.getX()), camera.getDrawY((float)p.getY()), 1.0f, 0.0f, 0.0f, 1.0f);
            Rectangle hit = m.getHitBox();
            Renderer.initQuadDraw(hit.width, hit.height).color(0.0f, 0.0f, 1.0f, 0.5f).draw(camera.getDrawX(hit.x), camera.getDrawY(hit.y));
        }
    }
}

