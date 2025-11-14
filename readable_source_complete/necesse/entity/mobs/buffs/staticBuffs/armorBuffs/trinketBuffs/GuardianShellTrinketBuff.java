/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class GuardianShellTrinketBuff
extends TrinketBuff
implements BuffAbility {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void runAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        float active = 5.0f;
        float cooldown = 30.0f;
        player.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.GUARDIAN_SHELL_COOLDOWN, (Mob)player, cooldown, null), false);
        player.buffManager.addBuff(new ActiveBuff(BuffRegistry.GUARDIAN_SHELL_ACTIVE, (Mob)player, active, null), false);
        player.buffManager.forceUpdateBuffs();
        int minHeight = 0;
        int maxHeight = 40;
        int particles = 40;
        for (int i = 0; i < particles; ++i) {
            float height = (float)minHeight + (float)(maxHeight - minHeight) * (float)i / (float)particles;
            AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(GameRandom.globalRandom.nextFloat() * 360.0f));
            float distance = 20.0f;
            player.getLevel().entityManager.addParticle(player.x + GameMath.sin(currentAngle.get().floatValue()) * distance, player.y + GameMath.cos(currentAngle.get().floatValue()) * distance * 0.75f, Particle.GType.CRITICAL).color(new Color(78, 112, 31)).height(height).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 150.0f / 250.0f), Float::sum).floatValue();
                float distY = distance * 0.75f;
                pos.x = player.x + GameMath.sin(angle) * distance;
                pos.y = player.y + GameMath.cos(angle) * distY * 0.75f;
            }).lifeTime((int)(active * 1000.0f)).sizeFades(16, 24);
        }
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return !buff.owner.buffManager.hasBuff(BuffRegistry.Debuffs.GUARDIAN_SHELL_COOLDOWN);
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("itemtooltip", "guardianshelltip"));
        return tooltips;
    }
}

