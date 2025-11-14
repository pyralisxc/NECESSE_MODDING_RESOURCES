/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Shape;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.MovedRectangle;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.level.maps.hudManager.HudDrawElement;

public class MobDashLevelEvent
extends MobAbilityLevelEvent {
    protected float dirX;
    protected float dirY;
    protected float distance;
    protected long startTime;
    protected int lastProcessTime;
    protected int animTime;
    protected GameDamage damage;
    protected MobHitCooldowns hitCooldowns;
    protected HudDrawElement hudDrawElement;

    public MobDashLevelEvent() {
    }

    public MobDashLevelEvent(Mob owner, int seed, float dirX, float dirY, float distance, int animTime, GameDamage damage) {
        super(owner, new GameRandom(seed));
        this.dirX = dirX;
        this.dirY = dirY;
        this.distance = distance;
        this.startTime = owner.getTime();
        this.animTime = animTime;
        this.damage = damage;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.dirX);
        writer.putNextFloat(this.dirY);
        writer.putNextFloat(this.distance);
        writer.putNextLong(this.startTime);
        writer.putNextInt(this.lastProcessTime);
        writer.putNextInt(this.animTime);
        if (this.damage != null) {
            writer.putNextBoolean(true);
            this.damage.writePacket(writer);
        } else {
            writer.putNextBoolean(false);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.dirX = reader.getNextFloat();
        this.dirY = reader.getNextFloat();
        this.distance = reader.getNextFloat();
        this.startTime = reader.getNextLong();
        this.lastProcessTime = reader.getNextInt();
        this.animTime = reader.getNextInt();
        if (reader.getNextBoolean()) {
            this.damage = GameDamage.fromReader(reader);
        }
    }

    @Override
    public void init() {
        super.init();
        this.hitCooldowns = new MobHitCooldowns();
        if (this.isClient() && !this.isOver()) {
            this.hudDrawElement = this.level.hudManager.addElement(new HudDrawElement(){

                @Override
                public void addDrawables(List<SortedDrawable> list, final GameCamera camera, PlayerMob perspective) {
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return 0;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            Shape hitBox;
                            if (GlobalData.debugActive() && (hitBox = MobDashLevelEvent.this.getHitBox()) != null) {
                                Renderer.drawShape(hitBox, camera, false, 1.0f, 0.0f, 0.0f, 1.0f);
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.owner == null || this.owner.removed()) {
            this.over();
            return;
        }
        int timeProgress = (int)Math.min(this.getTime() - this.startTime, (long)this.animTime);
        if (this.lastProcessTime < timeProgress) {
            double lastPercentToMove = this.getMoveCurve((double)this.lastProcessTime / (double)this.animTime);
            double nextPercentToMove = this.getMoveCurve((double)timeProgress / (double)this.animTime);
            double percentToMove = nextPercentToMove - lastPercentToMove;
            float fullDistanceToMove = (float)((double)this.distance * percentToMove);
            while (fullDistanceToMove > 0.0f) {
                Shape hitBox;
                block9: {
                    float distanceToMove = Math.min(fullDistanceToMove, this.getMaxDistancePerCheck());
                    fullDistanceToMove -= distanceToMove;
                    while (true) {
                        float lastX = this.owner.x;
                        float lastY = this.owner.y;
                        float distanceToMoveX = this.dirX * distanceToMove;
                        float distanceToMoveY = this.dirY * distanceToMove;
                        MovedRectangle collision = new MovedRectangle(this.owner.getCollision(), (int)distanceToMoveX, (int)distanceToMoveY);
                        if (this.getLevel().collides((Shape)collision, this.owner.getLevelCollisionFilter())) {
                            if (!((distanceToMove -= 4.0f) < 0.0f)) continue;
                            break block9;
                        }
                        this.setOwnerPos(this.owner.x + this.dirX * distanceToMove, this.owner.y + this.dirY * distanceToMove);
                        if (!this.owner.collidesWith(this.getLevel())) break block9;
                        boolean success = false;
                        this.setOwnerPos(lastX, this.owner.y + this.dirY * distanceToMove);
                        if (!this.owner.collidesWith(this.getLevel())) {
                            success = true;
                        }
                        if (!success) {
                            this.setOwnerPos(this.owner.x + this.dirX * distanceToMove, lastY);
                            if (!this.owner.collidesWith(this.getLevel())) {
                                success = true;
                            }
                        }
                        if (success) break block9;
                        this.setOwnerPos(lastX, lastY);
                        if ((distanceToMove -= 4.0f) < 0.0f) break;
                    }
                    this.over();
                }
                if (this.damage == null || (hitBox = this.getHitBox()) == null) continue;
                this.handleHits(hitBox, this::canHit, null);
            }
            this.lastProcessTime = timeProgress;
        }
        if (timeProgress >= this.animTime) {
            this.over();
        }
    }

    public void setOwnerPos(float x, float y) {
        this.owner.setPos(x, y, this.owner.isSmoothSnapped() || GameMath.squareDistance(this.owner.x, this.owner.y, x, y) < 4.0f);
    }

    protected double getMoveCurve(double x) {
        return Math.pow(x, 0.5);
    }

    public boolean canHit(Mob mob) {
        return mob.canBeHit(this) && this.hitCooldowns.canHit(mob);
    }

    public float getMaxDistancePerCheck() {
        return 10.0f;
    }

    public Shape getHitBox() {
        return this.owner.getCollision();
    }

    @Override
    public void clientHit(Mob target, Packet content) {
        super.clientHit(target, content);
        target.startHitCooldown();
        this.hitCooldowns.startCooldown(target);
    }

    @Override
    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
        super.serverHit(target, content, clientSubmitted);
        if (clientSubmitted || this.hitCooldowns.canHit(target)) {
            this.dealServerDamage(target, clientSubmitted);
            this.hitCooldowns.startCooldown(target);
        }
    }

    public void dealServerDamage(Mob target, boolean isClientSubmitted) {
        if (this.damage != null) {
            target.isServerHit(this.damage, this.dirX, this.dirY, 75.0f, this);
        }
    }

    @Override
    public void over() {
        super.over();
        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }
    }
}

