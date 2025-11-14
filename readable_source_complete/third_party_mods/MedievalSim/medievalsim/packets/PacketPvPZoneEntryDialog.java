/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GlobalData
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.state.MainGame
 *  necesse.gfx.forms.components.FormComponent
 */
package medievalsim.packets;

import medievalsim.ui.PvPZoneEntryDialog;
import necesse.engine.GlobalData;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.gfx.forms.components.FormComponent;

public class PacketPvPZoneEntryDialog
extends Packet {
    public int zoneID;
    public String zoneName;
    public float damageMultiplier;
    public int combatLockSeconds;

    public PacketPvPZoneEntryDialog(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.zoneID = reader.getNextInt();
        this.zoneName = reader.getNextString();
        this.damageMultiplier = reader.getNextFloat();
        this.combatLockSeconds = reader.getNextInt();
    }

    public PacketPvPZoneEntryDialog(int zoneID, String zoneName, float damageMultiplier, int combatLockSeconds) {
        this.zoneID = zoneID;
        this.zoneName = zoneName;
        this.damageMultiplier = damageMultiplier;
        this.combatLockSeconds = combatLockSeconds;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextInt(zoneID);
        writer.putNextString(zoneName);
        writer.putNextFloat(damageMultiplier);
        writer.putNextInt(combatLockSeconds);
    }

    public void processClient(NetworkPacket packet, Client client) {
        if (GlobalData.getCurrentState() instanceof MainGame) {
            MainGame mainGame = (MainGame)GlobalData.getCurrentState();
            PvPZoneEntryDialog dialog = new PvPZoneEntryDialog(client, this.zoneID, this.zoneName, this.damageMultiplier, this.combatLockSeconds);
            mainGame.formManager.addComponent((FormComponent)dialog);
        }
    }
}

