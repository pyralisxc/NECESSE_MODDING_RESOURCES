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

import medievalsim.ui.PvPZoneSpawnDialog;
import medievalsim.zones.PvPZone;
import necesse.engine.GlobalData;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.gfx.forms.components.FormComponent;

public class PacketPvPZoneSpawnDialog
extends Packet {
    public int zoneID;
    public String zoneName;
    public float damageMultiplier;
    public int combatLockSeconds;

    public PacketPvPZoneSpawnDialog(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        this.zoneID = reader.getNextInt();
        this.zoneName = reader.getNextString();
        this.damageMultiplier = reader.getNextFloat();
        this.combatLockSeconds = reader.getNextInt();
    }

    public PacketPvPZoneSpawnDialog(PvPZone zone) {
        this.zoneID = zone.uniqueID;
        this.zoneName = zone.name;
        this.damageMultiplier = zone.damageMultiplier;
        this.combatLockSeconds = zone.combatLockSeconds;
        PacketWriter writer = new PacketWriter((Packet)this);
        writer.putNextInt(this.zoneID);
        writer.putNextString(this.zoneName);
        writer.putNextFloat(this.damageMultiplier);
        writer.putNextInt(this.combatLockSeconds);
    }

    public void processClient(NetworkPacket packet, Client client) {
        if (GlobalData.getCurrentState() instanceof MainGame) {
            MainGame mainGame = (MainGame)GlobalData.getCurrentState();
            PvPZoneSpawnDialog dialog = new PvPZoneSpawnDialog(client, this.zoneID, this.zoneName, this.damageMultiplier, this.combatLockSeconds);
            mainGame.formManager.addComponent((FormComponent)dialog);
        }
    }
}

