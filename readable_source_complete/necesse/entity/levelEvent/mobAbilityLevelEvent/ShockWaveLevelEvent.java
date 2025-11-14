/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.level.maps.hudManager.HudDrawElement;

public abstract class ShockWaveLevelEvent
extends GroundEffectEvent {
    public int circumferencePerSegment = 30;
    public boolean allowConsecutiveHits = false;
    protected float targetAngle;
    protected float angleExtent;
    protected float expandSpeed;
    protected float maxDistance;
    protected float distancePerHit;
    protected float hitboxWidth;
    protected float hitDistanceOffset;
    protected float currentDistance;
    protected float lastHitDistance;
    protected HashSet<Integer> lastTickHits = new HashSet();
    private LinkedList<HudDrawElement> debugHitboxes = new LinkedList();
    protected boolean drawDebugHitboxes = false;

    public ShockWaveLevelEvent(float angleExtent, float expandSpeed, float maxDistance, float distancePerHit, float hitboxWidth) {
        this.angleExtent = angleExtent;
        this.expandSpeed = expandSpeed;
        this.maxDistance = maxDistance;
        this.distancePerHit = distancePerHit;
        this.hitboxWidth = hitboxWidth;
    }

    public ShockWaveLevelEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, float targetAngle, float angleExtent, float expandSpeed, float maxDistance, float distancePerHit, float hitboxWidth) {
        super(owner, x, y, uniqueIDRandom);
        this.targetAngle = targetAngle;
        this.angleExtent = angleExtent;
        this.expandSpeed = expandSpeed;
        this.maxDistance = maxDistance;
        this.distancePerHit = distancePerHit;
        this.hitboxWidth = hitboxWidth;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.targetAngle);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.targetAngle = reader.getNextFloat();
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        float lastDistance = this.currentDistance;
        this.currentDistance += this.expandSpeed * delta / 250.0f;
        boolean end = false;
        if (this.currentDistance > this.maxDistance) {
            this.currentDistance = this.maxDistance;
            end = true;
        }
        if (this.currentDistance > lastDistance) {
            float offset = this.hitDistanceOffset % this.distancePerHit;
            if (offset < 0.0f) {
                offset = this.distancePerHit - offset;
            }
            float currentDistance = this.currentDistance - offset;
            for (float i = this.lastHitDistance - offset + this.distancePerHit; i <= currentDistance; i += this.distancePerHit) {
                this.lastHitDistance = i + offset;
                if (this.allowConsecutiveHits) {
                    this.lastTickHits.clear();
                }
                float halfAngle = this.angleExtent / 2.0f;
                float startAngle = this.targetAngle - halfAngle;
                float endAngle = this.targetAngle + halfAngle;
                float circumference = (float)(Math.PI * (double)i * 2.0 * (double)this.angleExtent / 360.0);
                float sections = (float)Math.ceil(circumference / (float)this.circumferencePerSegment);
                float anglePerSection = this.angleExtent / sections;
                float halfHitboxWidth = this.hitboxWidth / 2.0f;
                int section = 0;
                while ((float)section < sections) {
                    float sectionStartAngle = startAngle + (float)section * anglePerSection;
                    float sectionEndAngle = sectionStartAngle + anglePerSection;
                    Point2D.Float p1 = this.getAngledPos(i - halfHitboxWidth, sectionStartAngle);
                    Point2D.Float p2 = this.getAngledPos(i + halfHitboxWidth, sectionStartAngle);
                    Point2D.Float p3 = this.getAngledPos(i + halfHitboxWidth, sectionEndAngle);
                    Point2D.Float p4 = this.getAngledPos(i - halfHitboxWidth, sectionEndAngle);
                    int[] xPoints = new int[]{(int)p1.x, (int)p2.x, (int)p3.x, (int)p4.x};
                    int[] yPoints = new int[]{(int)p1.y, (int)p2.y, (int)p3.y, (int)p4.y};
                    final Polygon hitbox = new Polygon(xPoints, yPoints, 4);
                    this.spawnHitboxParticles(hitbox);
                    this.handleHits(hitbox, this::canHit, null);
                    if (this.drawDebugHitboxes && this.isClient()) {
                        HudDrawElement element = new HudDrawElement(){

                            @Override
                            public void addDrawables(List<SortedDrawable> list, final GameCamera camera, PlayerMob perspective) {
                                list.add(new SortedDrawable(){

                                    @Override
                                    public int getPriority() {
                                        return -10000;
                                    }

                                    @Override
                                    public void draw(TickManager tickManager) {
                                        Renderer.drawShape(hitbox, camera, false, 1.0f, 0.0f, 0.0f, 1.0f);
                                    }
                                });
                            }
                        };
                        this.debugHitboxes.addLast(element);
                        this.level.hudManager.addElement(element);
                    }
                    ++section;
                }
                this.spawnHitboxParticles(i, startAngle, endAngle);
            }
        }
        if (end) {
            this.over();
        }
    }

    @Override
    public void clientHit(Mob target) {
        target.startHitCooldown();
        this.lastTickHits.add(target.getUniqueID());
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || !this.lastTickHits.contains(target.getUniqueID())) {
            this.lastTickHits.add(target.getUniqueID());
            this.damageTarget(target);
        }
    }

    public abstract void damageTarget(Mob var1);

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && !this.lastTickHits.contains(mob.getUniqueID());
    }

    protected Point2D.Float getAngledPos(float radius, float angle) {
        return new Point2D.Float((float)this.x + GameMath.cos(angle) * radius, (float)this.y + GameMath.sin(angle) * radius);
    }

    protected abstract void spawnHitboxParticles(Polygon var1);

    protected abstract void spawnHitboxParticles(float var1, float var2, float var3);

    protected Iterable<Point2D.Float> getPositionsAlongHit(final float radius, final float startAngle, float endAngle, float maxDistPerPoint, final boolean addEdge) {
        float totalAngle = endAngle - startAngle;
        float circumference = (float)(Math.PI * (double)radius * 2.0 * (double)totalAngle / 360.0);
        float totalPointsFloat = circumference / maxDistPerPoint;
        final int totalPoints = (int)Math.ceil(totalPointsFloat);
        final float anglePerPoint = totalAngle / (float)totalPoints;
        final float offset = totalPoints == 0 ? totalAngle / 2.0f : 0.0f;
        return () -> {
            final AtomicInteger currentPoint = new AtomicInteger();
            return new Iterator<Point2D.Float>(){

                @Override
                public boolean hasNext() {
                    return currentPoint.get() < totalPoints + (addEdge ? 1 : 0);
                }

                @Override
                public Point2D.Float next() {
                    int current = currentPoint.getAndAdd(1);
                    float angle = startAngle + (float)current * anglePerPoint + offset;
                    return ShockWaveLevelEvent.this.getAngledPos(radius, angle);
                }
            };
        };
    }

    protected Iterable<Point2D.Float> getPositionsThroughHit(final float radius, final float startAngle, float endAngle, float distPerPoint, boolean centerPoints) {
        float totalAngle = endAngle - startAngle;
        double fullCircumference = Math.PI * (double)radius * 2.0;
        double oneDegreeDist = fullCircumference / 360.0;
        final float anglePerPoint = (float)((double)distPerPoint / oneDegreeDist);
        float circumference = (float)(oneDegreeDist * (double)totalAngle);
        final int totalPoints = (int)Math.max(Math.ceil(circumference / distPerPoint), 1.0);
        final float offset = centerPoints ? totalAngle % anglePerPoint / 2.0f : 0.0f;
        return () -> {
            final AtomicInteger currentPoint = new AtomicInteger();
            return new Iterator<Point2D.Float>(){

                @Override
                public boolean hasNext() {
                    return currentPoint.get() < totalPoints;
                }

                @Override
                public Point2D.Float next() {
                    int current = currentPoint.getAndAdd(1);
                    float angle = startAngle + (float)current * anglePerPoint + offset;
                    return ShockWaveLevelEvent.this.getAngledPos(radius, angle);
                }
            };
        };
    }

    @Override
    public Shape getHitBox() {
        return null;
    }

    @Override
    public void over() {
        super.over();
        this.debugHitboxes.forEach(HudDrawElement::remove);
        this.debugHitboxes.clear();
    }
}

