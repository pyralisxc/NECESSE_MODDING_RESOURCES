/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.util.ArrayList;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModNetworkData;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.presets.ModNetworkListMismatchForm;

public class PacketModsMismatch
extends Packet {
    public final ArrayList<ModNetworkData> mods;

    public PacketModsMismatch(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        int total = reader.getNextShortUnsigned();
        this.mods = new ArrayList(total);
        for (int i = 0; i < total; ++i) {
            ModNetworkData mod = ModNetworkData.fromPacketReader(reader);
            this.mods.add(mod);
        }
    }

    public PacketModsMismatch() {
        PacketWriter writer = new PacketWriter(this);
        this.mods = new ArrayList(ModLoader.getEnabledMods().size());
        for (LoadedMod loadedMod : ModLoader.getEnabledMods()) {
            this.mods.add(new ModNetworkData(loadedMod));
        }
        writer.putNextShortUnsigned(this.mods.size());
        for (ModNetworkData modNetworkData : this.mods) {
            modNetworkData.write(writer);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ModNetworkListMismatchForm form = new ModNetworkListMismatchForm();
        form.setup(this.mods);
        client.error(Localization.translate("disconnect", "modsmismatch"), false, form);
    }
}

