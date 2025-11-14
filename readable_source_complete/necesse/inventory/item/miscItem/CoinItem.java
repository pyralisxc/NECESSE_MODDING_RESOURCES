/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.level.gameObject.GameObject;

public class CoinItem
extends ObjectItem {
    public CoinItem(GameObject object) {
        super(object);
        this.keyWords.add("currency");
        this.worldDrawSize = 28;
        this.attackAnimTime.setBaseValue(100);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "cointip"));
        return tooltips;
    }

    @Override
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        if (item.getGndData().getBoolean("showCoinStackIcon")) {
            return new GameSprite(GameResources.coinStackIcon);
        }
        return new GameSprite(this.itemTexture, 0, 0, 32);
    }

    @Override
    public GameSprite getWorldItemSprite(InventoryItem item, PlayerMob perspective) {
        if (perspective == null || perspective.getWorldEntity() == null) {
            return super.getWorldItemSprite(item, perspective);
        }
        long time = perspective.getWorldEntity().getTime() % 2500L;
        int sprite = time < 2000L ? 0 : GameUtils.getAnim(time - 2000L, this.itemTexture.getWidth() / 32, 500);
        return new GameSprite(this.itemTexture, sprite, 0, 32);
    }

    @Override
    public void tickPickupEntity(ItemPickupEntity entity) {
        super.tickPickupEntity(entity);
        if (entity.isClient()) {
            boolean giveLight;
            boolean bl = giveLight = (Math.abs(entity.dx) > 2.0f || Math.abs(entity.dy) > 2.0f) && GameRandom.globalRandom.getEveryXthChance(2);
            if (entity.getTarget() != null || giveLight || GameRandom.globalRandom.getEveryXthChance(20)) {
                ParticleOption particle = entity.getLevel().entityManager.addParticle(entity.x + GameRandom.globalRandom.floatGaussian() * 2.0f, entity.y + 2.0f + GameRandom.globalRandom.floatGaussian() * 2.0f, Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 4.0f, GameRandom.globalRandom.floatGaussian() * 4.0f).sizeFades(6, 12).color(new Color(-1123718)).heightMoves(0.0f, 10.0f);
                if (giveLight) {
                    particle.givesLight(50.0f, 0.8f);
                }
            }
        }
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "wealth");
    }
}

