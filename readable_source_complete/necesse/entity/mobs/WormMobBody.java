/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Rectangle;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameLinkedList;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.maps.CollisionFilter;

public class WormMobBody<T extends WormMobHead<B, T>, B extends WormMobBody<T, B>>
extends Mob {
    public int removeTicker;
    public final LevelMob<T> master = new LevelMob();
    public GameLinkedList.Element moveLine;
    public float moveLineExtraDist;
    public float height = 0.0f;
    public B next;
    public boolean canHit = true;
    public boolean sharesHitCooldownWithNext = false;
    public boolean relayHitToNext = false;
    public boolean relaysBuffsToNext = false;

    public WormMobBody(int health) {
        super(health);
        this.isSummoned = true;
        this.dropsLoot = false;
        this.setKnockbackModifier(0.0f);
        this.setRegen(0.0f);
        this.buffManager = new BuffManager(this){

            @Override
            public ActiveBuff addBuff(ActiveBuff ab, boolean sendUpdatePacket, boolean forceOverride, boolean forceUpdateBuffs) {
                if (WormMobBody.this.relaysBuffsToNext) {
                    if (WormMobBody.this.next != null) {
                        ActiveBuff newBuff = new ActiveBuff(ab.buff, (Mob)WormMobBody.this.next, ab.getDuration(), ab.getAttacker());
                        newBuff.setGndData(newBuff.getGndData());
                        return ((WormMobBody)WormMobBody.this.next).buffManager.addBuff(newBuff, sendUpdatePacket, forceOverride, forceUpdateBuffs);
                    }
                    WormMobHead master = (WormMobHead)WormMobBody.this.master.get(WormMobBody.this.getLevel());
                    if (master != null) {
                        ActiveBuff newBuff = new ActiveBuff(ab.buff, (Mob)master, ab.getDuration(), ab.getAttacker());
                        newBuff.setGndData(newBuff.getGndData());
                        return master.buffManager.addBuff(newBuff, sendUpdatePacket, forceOverride, forceUpdateBuffs);
                    }
                    return super.addBuff(ab, sendUpdatePacket, forceOverride, forceUpdateBuffs);
                }
                return super.addBuff(ab, sendUpdatePacket, forceOverride, forceUpdateBuffs);
            }
        };
    }

    @Override
    public boolean shouldSendSpawnPacket() {
        return false;
    }

    @Override
    public Mob getSpawnPacketMaster() {
        return this.master.get(this.getLevel());
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.master.uniqueID = reader.getNextInt();
        this.canHit = reader.getNextBoolean();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.master.uniqueID);
        writer.putNextBoolean(this.canHit);
    }

    @Override
    public void init() {
        super.init();
        this.countStats = false;
    }

    public void updateBodyPartPosition(T head, float x, float y) {
        this.setPos(x, y, true);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.tickMaster();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.movementUpdateTime = this.getWorldEntity().getTime();
        this.healthUpdateTime = this.getWorldEntity().getTime();
        this.tickMaster();
    }

    @Override
    public void tickMovement(float delta) {
        if (this.removed()) {
            return;
        }
        this.checkCollision();
    }

    @Override
    public void requestServerUpdate() {
    }

    @Override
    public void sendMovementPacket(boolean isDirect) {
    }

    public void tickMaster() {
        if (this.removed()) {
            return;
        }
        this.master.computeIfPresent(this.getLevel(), m -> {
            this.setMaxHealth(m.getMaxHealth());
            this.setHealthHidden(m.getHealth(), 0.0f, 0.0f, null);
            this.setArmor(m.getArmorFlat());
        });
        ++this.removeTicker;
        if (this.removeTicker > 20) {
            this.remove();
        }
    }

    @Override
    public void playHurtSound() {
        WormMobHead master = (WormMobHead)this.master.get(this.getLevel());
        if (master != null) {
            master.playHurtSound();
        }
    }

    @Override
    public void playDeathSound() {
    }

    @Override
    public boolean canHitThroughCollision() {
        return true;
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return null;
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public boolean canCollisionHit(Mob target) {
        return this.isVisible() && super.canCollisionHit(target);
    }

    @Override
    public boolean isVisible() {
        return this.moveLine != null && !((WormMoveLine)this.moveLine.object).isUnderground;
    }

    @Override
    public void startHitCooldown() {
        super.startHitCooldown();
        if (this.master != null) {
            this.master.computeIfPresent(this.getLevel(), Mob::startHitCooldown);
        }
    }

    @Override
    public int getHitCooldownUniqueID() {
        if (this.sharesHitCooldownWithNext) {
            if (this.next != null) {
                return ((WormMobBody)this.next).getHitCooldownUniqueID();
            }
            WormMobHead master = (WormMobHead)this.master.get(this.getLevel());
            if (master != null) {
                return master.getHitCooldownUniqueID();
            }
            return this.master.uniqueID;
        }
        return super.getHitCooldownUniqueID();
    }

    @Override
    public boolean canBeHit(Attacker attacker) {
        if (!this.isVisible() || !this.canHit) {
            return false;
        }
        return super.canBeHit(attacker);
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public int getHealth() {
        WormMobHead head;
        if (this.master != null && (head = (WormMobHead)this.master.get(this.getLevel())) != null) {
            return head.getHealth();
        }
        return super.getHealth();
    }

    @Override
    public int getMaxHealth() {
        WormMobHead head;
        if (this.master != null && (head = (WormMobHead)this.master.get(this.getLevel())) != null) {
            return head.getMaxHealth();
        }
        return super.getMaxHealth();
    }

    @Override
    public MobWasHitEvent isHit(MobWasHitEvent event, Attacker attacker) {
        if (this.relayHitToNext) {
            if (this.next != null) {
                return ((WormMobBody)this.next).isHit(event, attacker);
            }
            WormMobHead master = (WormMobHead)this.master.get(this.getLevel());
            if (master != null) {
                return master.isHit(event, attacker);
            }
            return super.isHit(event, attacker);
        }
        return super.isHit(event, attacker);
    }

    @Override
    public MobWasHitEvent isServerHit(GameDamage damage, float x, float y, float knockback, Attacker attacker) {
        if (this.relayHitToNext) {
            if (this.next != null) {
                return ((WormMobBody)this.next).isServerHit(damage, x, y, knockback, attacker);
            }
            WormMobHead master = (WormMobHead)this.master.get(this.getLevel());
            if (master != null) {
                return master.isServerHit(damage, x, y, knockback, attacker);
            }
            return super.isServerHit(damage, x, y, knockback, attacker);
        }
        return super.isServerHit(damage, x, y, knockback, attacker);
    }

    @Override
    public void setHealthHidden(int health, float knockbackX, float knockbackY, Attacker attacker, boolean fromNetworkUpdate) {
        if (this.master != null) {
            this.master.computeIfPresent(this.getLevel(), m -> m.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate));
        }
        super.setHealthHidden(health, knockbackX, knockbackY, attacker, fromNetworkUpdate);
    }

    @Override
    public Rectangle getSelectBox(int x, int y) {
        Rectangle selectBox = super.getSelectBox(x, y);
        selectBox.y = (int)((float)selectBox.y - this.height);
        if (this.height < 0.0f) {
            selectBox.height = (int)((float)selectBox.height + this.height);
        }
        return selectBox;
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
    }

    @Override
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        boolean removed = this.removed();
        super.remove(knockbackX, knockbackY, attacker, isDeath);
        if (!removed && this.master != null) {
            this.master.computeIfPresent(this.getLevel(), m -> m.remove(knockbackX, knockbackY, attacker));
        }
    }

    @Override
    protected void addDebugTooltips(ListGameTooltips tooltips) {
        super.addDebugTooltips(tooltips);
        tooltips.add("Height: " + this.height);
    }

    @Override
    public float getIncomingDamageModifier() {
        WormMobHead master = (WormMobHead)this.master.get(this.getLevel());
        return master == null ? super.getIncomingDamageModifier() : master.getIncomingDamageModifier();
    }
}

