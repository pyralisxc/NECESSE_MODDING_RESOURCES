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

public class PacketLifelineEvent
extends Packet {
    public final int uniqueID;

    public PacketLifelineEvent(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.uniqueID = reader.getNextByteUnsigned();
    }

    public PacketLifelineEvent(int uniqueID) {
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
        if (target != null) {
            if (target == client.getPlayer()) {
                SoundManager.playSound(GameResources.teleportfail, (SoundEffect)SoundEffect.effect(target).pitch(0.7f));
            }
            for (int i = 0; i < 10; ++i) {
                client.getLevel().entityManager.addParticle(target.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), target.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 6.0), (float)(GameRandom.globalRandom.nextGaussian() * 6.0)).color(new Color(150, 50, 50)).heightMoves(16.0f, 48.0f);
            }
        }
    }
}

