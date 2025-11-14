/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Color;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class PacketQuartzSetEvent
extends Packet {
    public final int uniqueID;

    public PacketQuartzSetEvent(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.uniqueID = reader.getNextByteUnsigned();
    }

    public PacketQuartzSetEvent(int uniqueID) {
        this.uniqueID = uniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(uniqueID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob target = GameUtils.getLevelMob(this.uniqueID, client.getLevel());
        if (target != null && target.getLevel() != null) {
            for (int i = 0; i < 12; ++i) {
                target.getLevel().entityManager.addParticle(target.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), target.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).color(new Color(255, 245, 198)).movesConstant(target.dx, target.dy).height(16.0f);
            }
            SoundManager.playSound(GameResources.teleportfail, (SoundEffect)SoundEffect.effect(target).pitch(1.3f));
        }
    }
}

