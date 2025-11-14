/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketGhostBoots;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.MovementTickBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class GhostBootsActiveBuff
extends Buff
implements MovementTickBuff {
    public GhostBootsActiveBuff() {
        this.shouldSave = false;
        this.isVisible = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.SPEED_FLAT, Float.valueOf(10.0f));
        buff.setModifier(BuffModifiers.SPEED, Float.valueOf(0.4f));
        Mob owner = buff.owner;
        if (owner.buffManager.hasBuff("dashcooldown")) {
            Integer dashStacks = owner.buffManager.getModifier(BuffModifiers.DASH_STACKS);
            if (owner.buffManager.getBuff("dashcooldown").getStacks() >= dashStacks) {
                return;
            }
        }
        Level level = owner.getLevel();
        int strength = 150;
        Point2D.Float dir = PacketGhostBoots.getMobDir(owner);
        PacketGhostBoots.applyToPlayer(level, owner, dir.x, dir.y, strength);
        PacketGhostBoots.addCooldownStack(owner, 6.0f, false);
        owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.GHOST_DASH_ACTIVE, owner, 0.3f, null), false);
        owner.buffManager.forceUpdateBuffs();
        if (level != null && level.isClient()) {
            SoundManager.playSound(GameResources.swoosh, (SoundEffect)SoundEffect.effect(owner).volume(0.5f).pitch(1.7f));
        }
    }

    @Override
    public void tickMovement(ActiveBuff buff, float delta) {
        Mob owner;
        if (!buff.owner.inLiquid() && (owner = buff.owner).isClient() && (owner.dx != 0.0f || owner.dy != 0.0f)) {
            float speed = owner.getCurrentSpeed() * delta / 250.0f;
            GNDItemMap gndData = buff.getGndData();
            float particleBuffer = gndData.getFloat("particleBuffer") + speed;
            if (particleBuffer >= 15.0f) {
                particleBuffer -= 15.0f;
                float xOffset = GameRandom.globalRandom.floatGaussian() * 2.0f;
                float yOffset = GameRandom.globalRandom.floatGaussian() * 2.0f;
                boolean alternate = gndData.getBoolean("particleAlternate");
                gndData.setBoolean("particleAlternate", !alternate);
                int dir = owner.getDir();
                if (dir == 0 || dir == 2) {
                    xOffset += alternate ? 4.0f : -4.0f;
                } else {
                    yOffset += alternate ? 4.0f : -4.0f;
                }
                owner.getLevel().entityManager.addParticle(owner.x + xOffset, owner.y + yOffset - 2.0f, Particle.GType.IMPORTANT_COSMETIC).color(new Color(41, 41, 43)).sizeFadesInAndOut(10, 16, 50, 200).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).lifeTime(300).height(2.0f);
            }
            gndData.setFloat("particleBuffer", particleBuffer);
            float soundBuffer = gndData.getFloat("soundBuffer") + Math.min(speed, 80.0f * delta / 250.0f);
            if (soundBuffer >= 45.0f) {
                soundBuffer -= 45.0f;
                SoundManager.playSound(GameResources.run, (SoundEffect)SoundEffect.effect(owner).volume(0.3f).pitch(GameRandom.globalRandom.getFloatBetween(1.75f, 2.0f)));
                SoundManager.playSound(GameResources.swing2, (SoundEffect)SoundEffect.effect(owner).volume(0.3f).pitch(2.2f));
            }
            gndData.setFloat("soundBuffer", soundBuffer);
        }
    }
}

