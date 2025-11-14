/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GameEventInterface
 *  necesse.engine.GameEventListener
 *  necesse.engine.GameEvents
 *  necesse.engine.commands.ChatCommand
 *  necesse.engine.commands.CommandsManager
 *  necesse.engine.events.ServerClientConnectedEvent
 *  necesse.engine.modLoader.ModSettings
 *  necesse.engine.modLoader.annotations.ModEntry
 *  necesse.engine.network.Packet
 *  necesse.engine.network.server.Server
 *  necesse.engine.registries.PacketRegistry
 *  necesse.inventory.container.object.CraftingStationContainer
 */
package extendedrange;

import extendedrange.RangeCommand;
import extendedrange.Settings;
import extendedrange.UpdateRangePacket;
import necesse.engine.GameEventInterface;
import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.commands.ChatCommand;
import necesse.engine.commands.CommandsManager;
import necesse.engine.events.ServerClientConnectedEvent;
import necesse.engine.modLoader.ModSettings;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.network.Packet;
import necesse.engine.network.server.Server;
import necesse.engine.registries.PacketRegistry;
import necesse.inventory.container.object.CraftingStationContainer;

@ModEntry
public class ExtendedRange {
    public static Settings settings;
    public static Server SERVER;

    public void init() {
        try {
            GameEvents.addListener(ServerClientConnectedEvent.class, (GameEventInterface)new GameEventListener<ServerClientConnectedEvent>(){

                public void onEvent(ServerClientConnectedEvent serverClientConnectedEvent) {
                    if (serverClientConnectedEvent.client.isServer()) {
                        System.out.println("current server range is " + CraftingStationContainer.nearbyCraftTileRange);
                        serverClientConnectedEvent.client.getServer().network.sendPacket((Packet)new UpdateRangePacket(CraftingStationContainer.nearbyCraftTileRange), serverClientConnectedEvent.client.getServerClient());
                    }
                }
            });
            CraftingStationContainer.nearbyCraftTileRange = Settings.CraftingStationsRange;
            PacketRegistry.registerPacket(UpdateRangePacket.class);
        }
        catch (Exception e) {
            System.err.println("[Extended range mod] An error has occurred while initialising the mod.\nError:\n" + e);
        }
    }

    public ModSettings initSettings() {
        settings = new Settings();
        return settings;
    }

    public void postInit() {
        try {
            CommandsManager.registerServerCommand((ChatCommand)new RangeCommand());
        }
        catch (Exception e) {
            System.err.println("[Extended range mod] An error has occurred while registering command 'craftingStationsRange'.\nError:\n" + e);
        }
    }
}

