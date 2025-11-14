/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.SwampSporeObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SwampSporeObject;
import necesse.level.maps.Level;

public class SwampSporeDummyMob
extends Mob {
    private int aliveTimer;
    private int tileX;
    private int tileY;

    public SwampSporeDummyMob() {
        super(300);
        this.setArmor(0);
        this.setSpeed(0.0f);
        this.setFriction(1000.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-18, -18, 36, 36);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.shouldSave = false;
        this.aliveTimer = 20;
        this.isStatic = true;
        this.setTeam(-2);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickAlive();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.tickAlive();
    }

    @Override
    public boolean canHitThroughCollision() {
        return true;
    }

    @Override
    public boolean canBeTargetedFromAdjacentTiles() {
        return true;
    }

    private void tickAlive() {
        --this.aliveTimer;
        if (this.aliveTimer <= 0) {
            this.remove();
        }
    }

    public void keepAlive(SwampSporeObjectEntity entity) {
        this.tileX = entity.tileX;
        this.tileY = entity.tileY;
        this.aliveTimer = 20;
        this.setPos(this.tileX * 32 + 16, this.tileY * 32 + 16, true);
    }

    @Override
    public void playDeathSound() {
        super.playDeathSound();
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    public boolean canTakeDamage() {
        return true;
    }

    @Override
    public boolean countDamageDealt() {
        return false;
    }

    @Override
    public boolean canPushMob(Mob other) {
        return false;
    }

    @Override
    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        if (!debug) {
            return false;
        }
        return super.onMouseHover(camera, perspective, debug);
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        GameObject object;
        super.onDeath(attacker, attackers);
        if (this.isServer() && (object = this.getLevel().getObject(this.tileX, this.tileY)) instanceof SwampSporeObject) {
            this.getLevel().entityManager.doObjectDamageOverride(0, this.tileX, this.tileY, object.objectHealth);
        }
    }
}

