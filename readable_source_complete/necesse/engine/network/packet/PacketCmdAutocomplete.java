/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainGame;

public class PacketCmdAutocomplete
extends Packet {
    public PacketCmdAutocomplete(byte[] data) {
        super(data);
    }

    public PacketCmdAutocomplete(String currentCmd) {
        PacketWriter writer = new PacketWriter(this);
        writer.putNextString(currentCmd);
    }

    public PacketCmdAutocomplete(List<AutoComplete> autoCompletes) {
        PacketWriter writer = new PacketWriter(this);
        for (AutoComplete ac : autoCompletes) {
            writer.putNextContentPacket(ac.getContentPacket());
        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        PacketReader reader = new PacketReader(this);
        String cmd = reader.getNextString();
        List<AutoComplete> autocomplete = server.commandsManager.autocomplete(new ParsedCommand(cmd), client);
        if (autocomplete.size() > 10) {
            autocomplete = autocomplete.subList(0, 10);
        }
        if (!autocomplete.isEmpty()) {
            client.sendPacket(new PacketCmdAutocomplete(autocomplete));
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ArrayList<AutoComplete> autoCompletes = new ArrayList<AutoComplete>();
        PacketReader reader = new PacketReader(this);
        while (reader.hasNext()) {
            autoCompletes.add(new AutoComplete(new PacketReader(reader.getNextContentPacket())));
        }
        if (GlobalData.getCurrentState() instanceof MainGame) {
            ((MainGame)GlobalData.getCurrentState()).formManager.chat.onAutocompletePacket(autoCompletes);
        }
    }
}

