/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.Color;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;

public class GreatswordChargeLevel {
    public int timeToCharge;
    public float damageModifier;
    public Color particleColors;

    public GreatswordChargeLevel(int timeToCharge, float damageModifier, Color particleColors) {
        this.timeToCharge = timeToCharge;
        this.damageModifier = damageModifier;
        this.particleColors = particleColors;
    }

    public void setupAttackItem(GreatswordAttackHandler attackHandler, InventoryItem attackItem) {
        attackItem.getGndData().setFloat("chargeDamageMultiplier", this.damageModifier);
    }

    public void onReachedLevel(GreatswordAttackHandler attackHandler) {
        if (attackHandler.attackerMob.isClient()) {
            if (this.particleColors != null) {
                attackHandler.drawParticleExplosion(30, this.particleColors, 30, 50);
            }
            int totalLevels = attackHandler.chargeLevels.length;
            float currentLevelPercent = (float)(attackHandler.currentChargeLevel + 1) / (float)totalLevels;
            float minPitch = Math.max(0.7f, 1.0f - (float)totalLevels * 0.1f);
            float pitch = GameMath.lerp(currentLevelPercent, 1.0f, minPitch);
            SoundManager.playSound(GameResources.cling, (SoundEffect)SoundEffect.effect(attackHandler.attackerMob).volume(0.5f).pitch(pitch));
        }
    }

    public void updateAtLevel(GreatswordAttackHandler attackHandler, InventoryItem showItem) {
        if (attackHandler.attackerMob.isClient() && this.particleColors != null) {
            attackHandler.drawWeaponParticles(showItem, this.particleColors);
        }
    }
}

