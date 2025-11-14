/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class SecondWindTrinketBuff
extends TrinketBuff {
    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "secondwindcharmtip1"), 400);
        tooltips.add(Localization.translate("itemtooltip", "secondwindcharmtip2"), 400);
        return tooltips;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.DASH_STACKS, 1);
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        if (buff.owner.buffManager.hasBuff(BuffRegistry.Debuffs.DASH_COOLDOWN)) {
            buff.owner.buffManager.removeStack(BuffRegistry.Debuffs.DASH_COOLDOWN, false, false);
            if (buff.owner.isClient()) {
                for (int i = 0; i < 5; ++i) {
                    int angle = GameRandom.globalRandom.nextInt(360);
                    Point2D.Float dir = GameMath.getAngleDir(angle);
                    float range = GameRandom.globalRandom.getFloatBetween(25.0f, 40.0f);
                    float startX = dir.x * range;
                    float startY = 20.0f;
                    float endHeight = 29.0f;
                    float startHeight = endHeight + dir.y * range;
                    int lifeTime = GameRandom.globalRandom.getIntBetween(200, 500);
                    float speed = dir.x * range * 250.0f / (float)lifeTime;
                    buff.owner.getLevel().entityManager.addParticle(buff.owner, startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(24, 48).rotates().heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0f).fadesAlphaTime(100, 50).lifeTime(lifeTime);
                }
                SoundManager.playSound(GameResources.magicbolt4, (SoundEffect)SoundEffect.effect(buff.owner).volume(0.2f).pitch(GameRandom.globalRandom.getFloatBetween(1.9f, 2.1f)));
                SoundManager.playSound(GameResources.magicbolt1, (SoundEffect)SoundEffect.effect(buff.owner).volume(0.2f).pitch(GameRandom.globalRandom.getFloatBetween(1.9f, 2.1f)));
            }
        }
    }
}

