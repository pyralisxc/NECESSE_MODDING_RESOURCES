/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.glaiveToolItem;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.entity.levelEvent.GlaiveShowAttackEvent;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.glaiveToolItem.GlaiveToolItem;
import necesse.inventory.lootTable.presets.GlaiveWeaponsLootTable;
import necesse.level.maps.Level;

public class CryoGlaiveToolItem
extends GlaiveToolItem {
    public CryoGlaiveToolItem() {
        super(1500, GlaiveWeaponsLootTable.glaiveWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(400);
        this.attackDamage.setBaseValue(60.0f).setUpgradedValue(1.0f, 75.83335f);
        this.attackRange.setBaseValue(160);
        this.knockback.setBaseValue(100);
        this.width = 20.0f;
        this.attackXOffset = 58;
        this.attackYOffset = 58;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        if (level.isClient()) {
            level.entityManager.events.addHidden(new GlaiveShowAttackEvent(attackerMob, x, y, seed, 10.0f){

                @Override
                public void tick(float angle) {
                    Point2D.Float angleDir = this.getAngleDir(angle);
                    this.level.entityManager.addParticle(this.attackMob.x + angleDir.x * 75.0f + (float)this.attackMob.getCurrentAttackDrawXOffset(), this.attackMob.y + angleDir.y * 75.0f + (float)this.attackMob.getCurrentAttackDrawYOffset(), Particle.GType.COSMETIC).color(new Color(0, 222, 218)).minDrawLight(150).givesLight(179.0f, 1.0f).lifeTime(400);
                    this.level.entityManager.addParticle(this.attackMob.x - angleDir.x * 75.0f + (float)this.attackMob.getCurrentAttackDrawXOffset(), this.attackMob.y - angleDir.y * 75.0f + (float)this.attackMob.getCurrentAttackDrawYOffset(), Particle.GType.COSMETIC).color(new Color(0, 222, 218)).minDrawLight(150).givesLight(179.0f, 1.0f).lifeTime(400);
                }
            });
        }
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.cryoGlaive).volume(0.4f);
    }
}

