/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.mobAbilityLevelEvent.VoidClawDashLevelEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.KatanaDashAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.swordToolItem.KatanaToolItem;

public class VoidClawDashAttackHandler
extends KatanaDashAttackHandler {
    public VoidClawDashAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, KatanaToolItem katanaItem, int chargeTime, Color particleColors, int seed) {
        super(attackerMob, slot, item, katanaItem, chargeTime, particleColors, seed);
    }

    @Override
    protected Color startColor() {
        return new Color(0, 0, 0, 0);
    }

    @Override
    protected Color endColor() {
        return new Color(221, 127, 225, 100);
    }

    @Override
    protected Color edgeColor() {
        return new Color(214, 18, 255, 100);
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        float chargePercent = this.getChargePercent();
        if (this.attackerMob.isClient() && this.katanaChargeSoundPlayer != null) {
            this.katanaChargeSoundPlayer.fadeOutAndStop(0.4f);
        }
        if (!this.endedByInteract && chargePercent >= 0.5f) {
            if (this.attackerMob.isPlayer) {
                ((PlayerMob)this.attackerMob).constantAttack = true;
            }
            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setBoolean("sliceDash", true);
            attackItem.getGndData().setFloat("chargePercent", chargePercent);
            this.attackerMob.showAttackAndSendAttacker(attackItem, this.lastX, this.lastY, 0, this.seed);
            Point2D.Float dir = GameMath.normalize((float)this.lastX - this.attackerMob.x, (float)this.lastY - this.attackerMob.y);
            chargePercent = Math.min(chargePercent, 1.0f);
            VoidClawDashLevelEvent event = new VoidClawDashLevelEvent(this.attackerMob, this.seed, dir.x, dir.y, this.getChargeDistance(chargePercent), (int)(200.0f * chargePercent), this.katanaItem, attackItem);
            this.attackerMob.addAndSendAttackerLevelEvent(event);
            float healthToTake = (float)this.attackerMob.getMaxHealth() * 0.05f;
            this.attackerMob.useLife((int)healthToTake, this.attackerMob.isPlayer && ((PlayerMob)this.attackerMob).isServerClient() ? ((PlayerMob)this.attackerMob).getServerClient() : null, this.item.getItemLocalization());
            if (this.attackerMob.isClient()) {
                SoundManager.playSound(new SoundSettings(GameResources.swing1), this.attackerMob);
                SoundManager.playSound(new SoundSettings(GameResources.katanaDash), this.attackerMob);
            }
        }
        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }
    }
}

