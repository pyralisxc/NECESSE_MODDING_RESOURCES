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

import medievalsim.ui.PvPZoneExitDialog;
import necesse.engine.GlobalData;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.gfx.forms.components.FormComponent;

public class PacketPvPZoneExitDialog
extends Packet {
    public int zoneID;
    public String zoneName;
    public int remainingCombatLockSeconds;

    public PacketPvPZoneExitDialog(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.zoneID = reader.getNextInt();
        this.zoneName = reader.getNextString();
        this.remainingCombatLockSeconds = reader.getNextInt();
    }

    public PacketPvPZoneExitDialog(int zoneID, String zoneName, int remainingCombatLockSeconds) {
        this.zoneID = zoneID;
        this.zoneName = zoneName;
        this.remainingCombatLockSeconds = remainingCombatLockSeconds;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextInt(zoneID);
        writer.putNextString(zoneName);
        writer.putNextInt(remainingCombatLockSeconds);
    }

    public void processClient(NetworkPacket packet, Client client) {
        if (GlobalData.getCurrentState() instanceof MainGame) {
            MainGame mainGame = (MainGame)GlobalData.getCurrentState();
            PvPZoneExitDialog dialog = new PvPZoneExitDialog(client, this.zoneID, this.zoneName, this.remainingCombatLockSeconds);
            mainGame.formManager.addComponent((FormComponent)dialog);
        }
    }
}

