/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.travel;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.travel.IslandData;
import necesse.inventory.container.travel.TravelContainer;

public class IslandsResponseEvent
extends ContainerEvent {
    public final int startX;
    public final int startY;
    public final int width;
    public final int height;
    public final IslandData[][] islands;

    public IslandsResponseEvent(Server server, ServerClient client, TravelContainer travel, int startX, int startY, int width, int height) {
        width = Math.max(1, width);
        height = Math.max(1, height);
        if (width * height > 361) {
            width = Math.min(13, width);
            height = Math.min(13, height);
        }
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.islands = new IslandData[width][height];
        for (int y = 0; y < height; ++y) {
            int islandY = y + startY;
            for (int x = 0; x < width; ++x) {
                int islandX = x + startX;
                this.islands[x][y] = IslandData.generateIslandData(server, client, travel, islandX, islandY);
            }
        }
    }

    public IslandsResponseEvent(PacketReader reader) {
        super(reader);
        this.startX = reader.getNextInt();
        this.startY = reader.getNextInt();
        this.width = reader.getNextInt();
        this.height = reader.getNextInt();
        this.islands = new IslandData[this.width][this.height];
        for (int y = 0; y < this.height; ++y) {
            int islandY = y + this.startY;
            for (int x = 0; x < this.width; ++x) {
                int islandX = x + this.startX;
                boolean isOutsideWorldBorder = reader.getNextBoolean();
                boolean discovered = reader.getNextBoolean();
                boolean visited = reader.getNextBoolean();
                boolean canTravel = reader.getNextBoolean();
                boolean hasDeath = reader.getNextBoolean();
                boolean knowBiome = reader.getNextBoolean();
                int biomeID = BiomeRegistry.UNKNOWN.getID();
                GameMessage settlementName = null;
                if (knowBiome) {
                    biomeID = reader.getNextShortUnsigned();
                    if (reader.getNextBoolean()) {
                        settlementName = GameMessage.fromContentPacket(reader.getNextContentPacket());
                    }
                }
                this.islands[x][y] = new IslandData(islandX, islandY, biomeID, isOutsideWorldBorder, discovered, visited, hasDeath, canTravel, settlementName);
            }
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.startX);
        writer.putNextInt(this.startY);
        writer.putNextInt(this.width);
        writer.putNextInt(this.height);
        for (int y = 0; y < this.height; ++y) {
            for (int x = 0; x < this.width; ++x) {
                IslandData data = this.islands[x][y];
                writer.putNextBoolean(data.isOutsideWorldBorder);
                writer.putNextBoolean(data.discovered);
                writer.putNextBoolean(data.visited);
                writer.putNextBoolean(data.canTravel);
                writer.putNextBoolean(data.hasDeath);
                if (data.biome != BiomeRegistry.UNKNOWN.getID()) {
                    writer.putNextBoolean(true);
                    writer.putNextShortUnsigned(data.biome);
                    writer.putNextBoolean(data.settlementName != null);
                    if (data.settlementName == null) continue;
                    writer.putNextContentPacket(data.settlementName.getContentPacket());
                    continue;
                }
                writer.putNextBoolean(false);
            }
        }
    }
}

