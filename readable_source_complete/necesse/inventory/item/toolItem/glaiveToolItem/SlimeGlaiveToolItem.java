/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.glaiveToolItem;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.GlaiveShowAttackEvent;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.glaiveToolItem.GlaiveToolItem;
import necesse.inventory.lootTable.presets.IncursionGlaiveWeaponsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class SlimeGlaiveToolItem
extends GlaiveToolItem {
    public SlimeGlaiveToolItem() {
        super(1900, IncursionGlaiveWeaponsLootTable.incursionGlaiveWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(400);
        this.attackDamage.setBaseValue(60.0f).setUpgradedValue(1.0f, 75.83335f);
        this.attackRange.setBaseValue(200);
        this.knockback.setBaseValue(150);
        this.width = 20.0f;
        this.attackXOffset = 74;
        this.attackYOffset = 74;
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.raidTicketsModifier = 0.25f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        if (level.isClient()) {
            level.entityManager.events.addHidden(new GlaiveShowAttackEvent(attackerMob, x, y, seed, 10.0f){

                @Override
                public void tick(float angle) {
                    GameRandom gameRandom = new GameRandom();
                    float colorModifier = gameRandom.getFloatBetween(0.0f, 1.0f);
                    Color randomColor = SlimeGlaiveToolItem.this.getParticleColor(colorModifier);
                    Point2D.Float angleDir = this.getAngleDir(angle);
                    this.level.entityManager.addParticle(this.attackMob.x + angleDir.x * 85.0f + (float)this.attackMob.getCurrentAttackDrawXOffset(), this.attackMob.y + angleDir.y * 85.0f + (float)this.attackMob.getCurrentAttackDrawYOffset(), Particle.GType.COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).color(randomColor).movesConstant(angleDir.x * 40.0f, angleDir.y * 40.0f).lifeTime(400);
                    this.level.entityManager.addParticle(this.attackMob.x - angleDir.x * 85.0f + (float)this.attackMob.getCurrentAttackDrawXOffset(), this.attackMob.y - angleDir.y * 85.0f + (float)this.attackMob.getCurrentAttackDrawYOffset(), Particle.GType.COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).color(randomColor).movesConstant(angleDir.x * -40.0f, angleDir.y * -40.0f).lifeTime(400);
                }
            });
        }
    }

    private Color getParticleColor(float modifier) {
        return new Color((int)(70.0f * (1.0f + 1.8f * modifier)), (int)(178.0f * (1.0f + 0.3f * modifier)), (int)(170.0f * (1.0f + 0.2f * modifier)));
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.slimeGlaive).volume(0.26f);
    }
}

