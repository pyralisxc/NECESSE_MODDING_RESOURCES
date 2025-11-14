/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.util.GameUtils
 *  necesse.engine.world.WorldFile
 */
package medievalsim.packets;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import medievalsim.ui.AdminToolsHudForm;
import medievalsim.ui.AdminToolsHudManager;
import medievalsim.ui.PlayerDropdownEntry;
import medievalsim.util.ModLogger;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.engine.world.WorldFile;

public class PacketRequestPlayerList
extends Packet {
    public List<PlayerDropdownEntry> players;

    public PacketRequestPlayerList() {
        this.players = new ArrayList<PlayerDropdownEntry>();
    }

    public PacketRequestPlayerList(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader((Packet)this);
        int count = reader.getNextShortUnsigned();
        this.players = new ArrayList<PlayerDropdownEntry>(count);
        for (int i = 0; i < count; ++i) {
            String name = reader.getNextString();
            long auth = reader.getNextLong();
            boolean isOnline = reader.getNextBoolean();
            long lastLogin = reader.getNextLong();
            this.players.add(new PlayerDropdownEntry(name, auth, isOnline, lastLogin));
        }
    }

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        try {
            long auth;
            ArrayList<PlayerDropdownEntry> playerList = new ArrayList<PlayerDropdownEntry>();
            HashSet<Long> onlineAuths = new HashSet<Long>();
            for (ServerClient sc : server.getClients()) {
                if (sc == null || sc.playerMob == null) continue;
                onlineAuths.add(sc.authentication);
                String name = sc.getName();
                auth = sc.authentication;
                playerList.add(new PlayerDropdownEntry(name, auth, true, System.currentTimeMillis()));
            }
            try {
                for (WorldFile playerFile : server.world.fileSystem.getPlayerFiles()) {
                    try {
                        String characterName;
                        String authString = GameUtils.removeFileExtension((String)playerFile.getFileName().toString());
                        auth = Long.parseLong(authString);
                        if (onlineAuths.contains(auth) || (characterName = server.world.loadClientName(playerFile)) == null || characterName.equals("N/A") || characterName.isEmpty()) continue;
                        long lastLogin = 0L;
                        try {
                            BasicFileAttributes attrs = Files.readAttributes(playerFile.toAbsolutePath(), BasicFileAttributes.class, new LinkOption[0]);
                            lastLogin = attrs.lastModifiedTime().toMillis();
                        }
                        catch (Exception e) {
                            lastLogin = 0L;
                        }
                        playerList.add(new PlayerDropdownEntry(characterName, auth, false, lastLogin));
                    }
                    catch (NumberFormatException e) {
                        ModLogger.debug("Found invalid player file name: " + playerFile);
                    }
                }
            }
            catch (Exception e) {
                ModLogger.error("Error loading offline world players for dropdown: " + e.getMessage());
                e.printStackTrace();
            }
            Collections.sort(playerList);
            PacketRequestPlayerList response = new PacketRequestPlayerList();
            response.players = playerList;
            PacketWriter writer = new PacketWriter((Packet)response);
            writer.putNextShortUnsigned(playerList.size());
            for (PlayerDropdownEntry entry : playerList) {
                writer.putNextString(entry.characterName);
                writer.putNextLong(entry.steamAuth);
                writer.putNextBoolean(entry.isOnline);
                writer.putNextLong(entry.lastLogin);
            }
            client.sendPacket((Packet)response);
            ModLogger.info("Sent player list to " + client.getName() + " (" + playerList.size() + " players: " + onlineAuths.size() + " online, " + (playerList.size() - onlineAuths.size()) + " offline)");
        }
        catch (Exception e) {
            ModLogger.error("Error processing player list request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void processClient(NetworkPacket packet, Client client) {
        ModLogger.info("Received player list with " + this.players.size() + " entries");
        AdminToolsHudForm hudForm = AdminToolsHudManager.getHudForm();
        if (hudForm != null) {
            hudForm.updatePlayerList(this.players);
        }
    }
}

