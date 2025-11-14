/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.hudManager.floatText.FloatTextFade;

public class PacketShowDPS
extends Packet {
    public final int mobUniqueID;
    public final float dps;

    public PacketShowDPS(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.dps = reader.getNextFloat();
    }

    public PacketShowDPS(int mobUniqueID, float dps) {
        this.mobUniqueID = mobUniqueID;
        this.dps = dps;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(mobUniqueID);
        writer.putNextFloat(dps);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (mob == null) {
            return;
        }
        String text = Localization.translate("misc", "dpscount", "dps", GameUtils.formatNumber(this.dps));
        FloatTextFade floatText = new FloatTextFade(mob.getX() + (int)(GameRandom.globalRandom.nextGaussian() * 6.0), mob.getY() - 32, text, new FontOptions(16).outline().color(Color.ORANGE));
        client.getLevel().hudManager.addElement(floatText);
    }
}

