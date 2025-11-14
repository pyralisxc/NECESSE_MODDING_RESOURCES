/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.level.maps.Level
 */
package aphorea.packets;

import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import java.awt.Color;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.level.maps.Level;

public class AphAreaShowPacket
extends Packet {
    AphAreaList areaList;
    public final float x;
    public final float y;
    public final float rangeModifier;

    public AphAreaShowPacket(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.x = reader.getNextFloat();
        this.y = reader.getNextFloat();
        this.rangeModifier = reader.getNextFloat();
        this.areaList = new AphAreaList(new AphArea[0]);
        while (reader.hasNext()) {
            float range = reader.getNextFloat();
            boolean onlyVision = reader.getNextBoolean();
            boolean ignoreLight = reader.getNextBoolean();
            int colorsLength = reader.getNextInt();
            Color[] colors = new Color[colorsLength];
            for (int i = 0; i < colorsLength; ++i) {
                colors[i] = new Color(reader.getNextInt(), true);
            }
            this.areaList = this.areaList.addArea(new AphArea(range, colors).setOnlyVision(onlyVision).setIgnoreLight(ignoreLight));
        }
    }

    public AphAreaShowPacket(float x, float y, AphAreaList areaList, float rangeModifier) {
        this.x = x;
        this.y = y;
        this.rangeModifier = rangeModifier;
        this.areaList = areaList;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextFloat(x);
        writer.putNextFloat(y);
        writer.putNextFloat(rangeModifier);
        for (AphArea area : areaList.areas) {
            writer.putNextFloat(area.range);
            writer.putNextBoolean(area.onlyVision);
            writer.putNextBoolean(area.ignoreLight);
            writer.putNextInt(area.colors.length);
            for (Color color : area.colors) {
                writer.putNextInt(color.getRGB());
            }
        }
    }

    public AphAreaShowPacket(float x, float y, AphAreaList areaList) {
        this(x, y, areaList, 1.0f);
    }

    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() != null) {
            AphAreaShowPacket.applyToPlayer(client.getLevel(), this.x, this.y, this.areaList);
        }
    }

    public static void applyToPlayer(Level level, float x, float y, AphAreaList areaList) {
        if (level != null && level.isClient()) {
            areaList.executeClient(level, x, y);
        }
    }
}

