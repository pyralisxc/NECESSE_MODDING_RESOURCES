/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.quest;

import java.awt.Color;
import java.util.HashSet;
import java.util.Objects;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketQuest;
import necesse.engine.network.packet.PacketQuestRemove;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.QuestManager;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.QuestRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.gfx.drawOptions.DrawOptionsBox;
import necesse.gfx.fairType.FairType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;

public abstract class Quest
implements IDDataContainer {
    public final IDData idData = new IDData();
    private int uniqueID;
    private final HashSet<Long> clients = new HashSet();
    private QuestManager manager;
    private boolean dirty;
    private boolean removed;

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    public Quest() {
        QuestRegistry.instance.applyIDData(this.getClass(), this.idData);
    }

    public void addSaveData(SaveData save) {
        save.addInt("uniqueID", this.getUniqueID());
        save.addLongArray("clients", this.clients.stream().filter(Objects::nonNull).mapToLong(Long::longValue).toArray());
    }

    public void applyLoadData(LoadData save) {
        long[] clients;
        this.uniqueID = save.getInt("uniqueID", this.uniqueID);
        for (long client : clients = save.getLongArray("clients", new long[0])) {
            this.clients.add(client);
        }
    }

    public void setupSpawnPacket(PacketWriter writer) {
        writer.putNextInt(this.getUniqueID());
        this.setupPacket(writer);
    }

    public void applySpawnPacket(PacketReader reader) {
        this.uniqueID = reader.getNextInt();
        this.applyPacket(reader);
    }

    public void setupPacket(PacketWriter writer) {
    }

    public void applyPacket(PacketReader reader) {
    }

    public void init(QuestManager manager) {
        this.manager = manager;
        if (this.isDirty()) {
            manager.markDirty();
        }
    }

    public int getUniqueID() {
        if (this.uniqueID == 0) {
            this.uniqueID = GameRandom.globalRandom.nextInt();
        }
        return this.uniqueID;
    }

    public void makeActiveFor(Server server, ServerClient client) {
        this.clients.add(client.authentication);
        client.addQuest(this, true);
        server.network.sendPacket((Packet)new PacketQuest(this, true), client);
    }

    public void abandonFor(Server server, ServerClient client) {
        this.abandonFor(client.authentication);
        client.removeQuest(this);
        server.network.sendPacket((Packet)new PacketQuestRemove(this), client);
    }

    public void abandonFor(long clientAuth) {
        this.clients.remove(clientAuth);
    }

    public boolean isActiveFor(long clientAuth) {
        return this.clients.contains(clientAuth);
    }

    public int getTotalActiveClients() {
        return this.clients.size();
    }

    public abstract void tick(ServerClient var1);

    public boolean canShare() {
        return true;
    }

    public boolean canShareWith(ServerClient me, ServerClient him) {
        return me.isSameTeam(him);
    }

    public void onShared(Server server, ServerClient from, ServerClient to) {
        if (this.isRemoved()) {
            to.sendChatMessage(new LocalMessage("misc", "questnolongervalid"));
        } else {
            this.makeActiveFor(server, to);
        }
    }

    public void markDirty() {
        this.dirty = true;
        if (this.manager != null) {
            this.manager.markDirty();
        }
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void clean() {
        this.dirty = false;
    }

    public void remove() {
        this.removed = true;
        this.markDirty();
    }

    public final boolean isRemoved() {
        return this.removed;
    }

    public abstract boolean canComplete(NetworkClient var1);

    public void complete(ServerClient client) {
        client.newStats.quests_completed.increment(1);
    }

    public abstract GameMessage getTitle();

    public abstract GameMessage getDescription();

    public abstract DrawOptionsBox getProgressDrawBox(NetworkClient var1, int var2, int var3, int var4, Color var5, boolean var6);

    public abstract FairType getRewardType(NetworkClient var1, boolean var2);

    public abstract FairType getHandInType(NetworkClient var1, boolean var2);

    public MobSpawnTable getExtraCritterSpawnTable(ServerClient client, Level level) {
        return null;
    }

    public MobSpawnTable getExtraMobSpawnTable(ServerClient client, Level level) {
        return null;
    }

    public FishingLootTable getExtraFishingLoot(ServerClient client, FishingSpot spot) {
        return null;
    }

    public LootTable getExtraMobDrops(ServerClient client, Mob mob) {
        return null;
    }
}

