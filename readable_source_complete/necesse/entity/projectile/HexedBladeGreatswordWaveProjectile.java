/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.NecroticGreatswordWaveProjectile;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class HexedBladeGreatswordWaveProjectile
extends NecroticGreatswordWaveProjectile {
    protected Integer[] randomlySelectedBuffIDs;

    public HexedBladeGreatswordWaveProjectile() {
    }

    public HexedBladeGreatswordWaveProjectile(Level level, Mob owner, int x, int y, int targetX, int targetY, GameDamage damage, float speed, int range, int chargeLevel) {
        super(level, owner, x, y, targetX, targetY, damage, speed, range);
        this.randomlySelectedBuffIDs = new Integer[chargeLevel];
        ArrayList<Integer> buffSelection = new ArrayList<Integer>(Arrays.asList(this.validDebuffs()));
        for (int i = 0; i < this.randomlySelectedBuffIDs.length; ++i) {
            int buffIndex = GameRandom.globalRandom.nextInt(buffSelection.size());
            this.randomlySelectedBuffIDs[i] = buffSelection.remove(buffIndex);
        }
    }

    protected Integer[] validDebuffs() {
        return new Integer[]{BuffRegistry.Debuffs.GENERIC_ONFIRE.getID(), BuffRegistry.Debuffs.GENERIC_ICE_SLOW.getID(), BuffRegistry.Debuffs.GENERIC_POISON.getID(), BuffRegistry.Debuffs.GENERIC_DAMAGE_DEBUFF.getID(), BuffRegistry.Debuffs.GENERIC_WEAKNESS.getID(), BuffRegistry.Debuffs.GENERIC_ATTACKSPEED_SLOW.getID()};
    }

    @Override
    public Color getParticleColor() {
        return HexedBladeGreatswordWaveProjectile.getHexedParticleColor();
    }

    public static Color getHexedParticleColor() {
        return GameRandom.globalRandom.getOneOf(new Color(33, 20, 145), new Color(227, 227, 227), new Color(38, 3, 128), new Color(3, 29, 54), new Color(185, 106, 4), new Color(11, 88, 33));
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.randomlySelectedBuffIDs.length);
        for (Integer buffID : this.randomlySelectedBuffIDs) {
            writer.putNextShortUnsigned(buffID);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        int buffIDsLength = reader.getNextShortUnsigned();
        this.randomlySelectedBuffIDs = new Integer[buffIDsLength];
        for (int i = 0; i < buffIDsLength; ++i) {
            this.randomlySelectedBuffIDs[i] = reader.getNextShortUnsigned();
        }
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (!this.isServer()) {
            return;
        }
        if (mob != null) {
            for (int i = 0; i < this.randomlySelectedBuffIDs.length; ++i) {
                mob.buffManager.addBuff(new ActiveBuff((int)this.randomlySelectedBuffIDs[i], mob, 2.0f, (Attacker)this), true);
            }
        }
    }

    @Override
    public void refreshParticleLight() {
        Color color = new Color(126, 53, 255);
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, color, this.lightSaturation);
    }
}

