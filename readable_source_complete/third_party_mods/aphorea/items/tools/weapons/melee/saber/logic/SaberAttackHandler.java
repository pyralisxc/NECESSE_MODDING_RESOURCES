/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.MousePositionAttackHandler
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.gfx.GameResources
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.ItemAttackerWeaponItem
 */
package aphorea.items.tools.weapons.melee.saber.logic;

import aphorea.items.tools.weapons.melee.saber.AphSaberToolItem;
import aphorea.ui.AphCustomUIList;
import aphorea.ui.SaberAttackUIManger;
import java.awt.Point;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemAttackerWeaponItem;

public class SaberAttackHandler
extends MousePositionAttackHandler {
    public int chargeTime;
    public boolean fullyCharged;
    public AphSaberToolItem toolItem;
    public long startTime;
    public InventoryItem item;
    public int seed;
    public boolean endedByInteract;
    public boolean isAuto;

    public SaberAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, AphSaberToolItem toolItem, int chargeTime, boolean isAuto, int seed) {
        super(attackerMob, slot, 20);
        this.item = item;
        this.toolItem = toolItem;
        this.chargeTime = chargeTime;
        this.seed = seed;
        this.startTime = attackerMob.getWorldEntity().getLocalTime();
        this.isAuto = isAuto;
    }

    public long getTimeSinceStart() {
        return this.attackerMob.getWorldEntity().getLocalTime() - this.startTime;
    }

    public float getChargePercent() {
        return (float)this.getTimeSinceStart() / (float)this.chargeTime;
    }

    public Point getNextItemAttackerLevelPos(Mob currentTarget) {
        InventoryItem attackItem = this.item.copy();
        attackItem.getGndData().setFloat("skillPercent", 1.0f);
        return ((ItemAttackerWeaponItem)attackItem.item).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, currentTarget, -1, attackItem);
    }

    public void onUpdate() {
        super.onUpdate();
        float chargePercent = this.getChargePercent();
        InventoryItem showItem = this.item.copy();
        showItem.getGndData().setBoolean("charging", true);
        showItem.getGndData().setFloat("chargePercent", SaberAttackUIManger.barPercent(chargePercent));
        if (this.attackerMob.isClient() && this.attackerMob.isPlayer && AphCustomUIList.saberAttack.form.isHidden()) {
            AphCustomUIList.saberAttack.form.setHidden(false);
            AphCustomUIList.saberAttack.chargeTime = this.chargeTime;
        }
        this.attackerMob.showAttackAndSendAttacker(showItem, this.lastX, this.lastY, 0, this.seed);
        if (chargePercent >= 1.0f) {
            if (!this.attackerMob.isPlayer || this.isAuto) {
                this.attackerMob.endAttackHandler(true);
            }
        } else if (chargePercent >= 0.9f && !this.fullyCharged) {
            this.fullyCharged = true;
            SoundManager.playSound((GameSound)GameResources.cling, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this.attackerMob).volume(0.2f).pitch(1.0f));
        }
    }

    public void onMouseInteracted(int levelX, int levelY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    public void onControllerInteracted(float aimX, float aimY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    public void onEndAttack(boolean bySelf) {
        if (AphCustomUIList.saberAttack.form != null) {
            AphCustomUIList.saberAttack.form.setHidden(true);
        }
        float chargePercent = this.getChargePercent();
        if (!this.endedByInteract && chargePercent >= 0.1f) {
            if (this.attackerMob.isPlayer) {
                ((PlayerMob)this.attackerMob).constantAttack = true;
            }
            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setFloat("chargePercent", SaberAttackUIManger.barPercent(chargePercent));
            attackItem.getGndData().setBoolean("charged", true);
            attackItem.getGndData().setFloat("attackPercent", chargePercent);
            if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null) {
                Point attackPos = ((ItemAttackerWeaponItem)attackItem.item).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, this.lastItemAttackerTarget, -1, attackItem);
                this.lastX = attackPos.x;
                this.lastY = attackPos.y;
            }
            GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(attackItem, this.lastX, this.lastY, 0, this.seed);
            this.toolItem.superOnAttack(this.attackerMob.getLevel(), this.lastX, this.lastY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, this.seed, attackMap);
            for (ActiveBuff b : this.attackerMob.buffManager.getArrayBuffs()) {
                b.onItemAttacked(this.lastX, this.lastY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, new GNDItemMap());
            }
        } else {
            this.attackerMob.doAndSendStopAttackAttacker(false);
        }
    }
}

