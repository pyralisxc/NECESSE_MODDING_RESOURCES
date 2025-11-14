/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.BannerOfWarObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class BannerOfWarDummyMob
extends Mob {
    private int aliveTimer;
    private float damageBuffer;

    public BannerOfWarDummyMob() {
        super(Integer.MAX_VALUE);
        this.setArmor(0);
        this.setSpeed(0.0f);
        this.setFriction(1000.0f);
        this.setKnockbackModifier(0.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-18, -15, 36, 30);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
        this.shouldSave = false;
        this.aliveTimer = 20;
        this.isStatic = true;
        this.setTeam(-100);
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
    public boolean canBeTargetedFromAdjacentTiles() {
        return true;
    }

    private void tickAlive() {
        this.setHealthHidden(this.getMaxHealth());
        --this.aliveTimer;
        if (this.aliveTimer <= 0) {
            this.remove();
        }
    }

    public void keepAlive(BannerOfWarObjectEntity entity) {
        this.aliveTimer = 20;
        this.setPos(entity.tileX * 32 + 16, entity.tileY * 32 + 16, true);
    }

    @Override
    public void playHurtSound() {
    }

    @Override
    public void playHitDeathSound() {
    }

    @Override
    public void playDeathSound() {
    }

    @Override
    public void spawnDamageText(int damage, int size, boolean isCrit) {
    }

    @Override
    public void spawnResistedDamageText(int damage, int size, boolean isCrit) {
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
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
    protected void doWasHitLogic(MobWasHitEvent event) {
        super.doWasHitLogic(event);
        this.setHealthHidden(this.getMaxHealth());
    }

    @Override
    public boolean canBeTargeted(Mob attacker, NetworkClient attackerClient) {
        if (attackerClient != null) {
            return false;
        }
        if (attacker != null && !attacker.isHostile) {
            return false;
        }
        return super.canBeTargeted(attacker, attackerClient);
    }

    @Override
    public boolean canGiveResilience(Attacker attacker) {
        PlayerMob attackOwner;
        if (attacker != null && (attackOwner = attacker.getFirstPlayerOwner()) != null) {
            return !attackOwner.buffManager.hasBuff(BuffRegistry.BOSS_NEARBY);
        }
        return super.canGiveResilience(attacker);
    }

    @Override
    public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker, boolean fromNetworkUpdate) {
        int afterHealth;
        int beforeHealth = this.getHealth();
        super.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate);
        if (this.getLevel() != null && (afterHealth = this.getHealth()) < beforeHealth) {
            int delta = beforeHealth - afterHealth;
            this.damageBuffer += (float)delta / 40.0f;
            if (this.damageBuffer >= 1.0f) {
                int objectDamage = (int)this.damageBuffer;
                this.damageBuffer -= (float)objectDamage;
                PlayerMob player = attacker == null ? null : attacker.getFirstPlayerOwner();
                ServerClient client = player == null || !player.isServerClient() ? null : player.getServerClient();
                this.getLevel().entityManager.doObjectDamage(0, this.getTileX(), this.getTileY(), objectDamage, 10000.0f, attacker, client, true, this.getTileX() * 32 + 16, this.getTileY() * 32 + 16);
            }
        }
    }

    @Override
    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        if (!debug) {
            return false;
        }
        return super.onMouseHover(camera, perspective, debug);
    }

    @Override
    public float getArmorAfterPen(float armorPen) {
        return this.getArmor() - armorPen;
    }
}

