/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.GameAuth;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketSettlementData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.team.PlayerTeam;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.OneWorldMigration;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.objectEntity.SettlementFlagObjectEntity;
import necesse.gfx.HumanLook;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementBoundsManager;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationManager;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationSeverity;

public class NetworkSettlementData {
    public static int DISBAND_GRACE_SECONDS = 7200;
    public final Level level;
    private ServerSettlementData serverData;
    private boolean nameSet;
    private PlayerStats stats;
    public final int uniqueID;
    private int tileX;
    private int tileY;
    private int flagTier;
    private long disbandTime;
    private GameMessage settlementName;
    private long ownerAuth = -1L;
    private String ownerName;
    private int teamID = -1;
    private HumanLook look;
    private boolean isPrivate = true;
    public final SettlementNotificationManager notifications = new SettlementNotificationManager(this);
    private long raidActiveTick = Integer.MIN_VALUE;
    private long raidApproachingTick = Integer.MIN_VALUE;
    private boolean isDirty;
    private boolean isDirtyFull;
    private boolean disbandingPrevented;
    private boolean isUnloaded;
    private boolean isDisbanded;
    private int checkRegionsUnloadedBuffer;

    public NetworkSettlementData(Level level, int uniqueID, int tileX, int tileY) {
        this.level = level;
        this.uniqueID = uniqueID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.settlementName = new LocalMessage("settlement", "defname", "biome", level.getBiome(tileX, tileY).getLocalization());
        if (level.isServer()) {
            this.updateOwnerVariables();
        }
    }

    public NetworkSettlementData(Level level, int uniqueID, PacketReader reader) {
        this(level, uniqueID, 0, 0);
        this.readPacket(reader, true);
    }

    public void setServerData(ServerSettlementData serverData) {
        if (this.serverData != null) {
            throw new IllegalStateException("Cannot change server data once set");
        }
        this.serverData = serverData;
    }

    public CachedSettlementData getCacheData(boolean passNetworkData) {
        CachedSettlementData cacheData = new CachedSettlementData(this.level.getIdentifier(), this.uniqueID, this.tileX, this.tileY, this.flagTier, this.settlementName, this.ownerAuth, this.teamID);
        if (passNetworkData) {
            cacheData.setLoadedData(this);
        }
        return cacheData;
    }

    public int getTileX() {
        return this.tileX;
    }

    public int getTileY() {
        return this.tileY;
    }

    public int getFlagTier() {
        return this.flagTier;
    }

    public void setFlagTier(int flagTier) {
        boolean changed = this.flagTier != flagTier;
        this.flagTier = flagTier;
        if (changed) {
            ServerSettlementData serverData = this.getServerData();
            if (serverData != null) {
                serverData.worldData.updateSettlement(serverData);
            }
            this.markDirty(false);
        }
    }

    public boolean hasFlag() {
        return this.getFlagObjectEntity() != null;
    }

    public SettlementFlagObjectEntity getFlagObjectEntity() {
        return this.level.entityManager.getObjectEntity(this.tileX, this.tileY, SettlementFlagObjectEntity.class);
    }

    public boolean hasOwner() {
        return this.ownerAuth != -1L;
    }

    public long getDisbandTime() {
        return this.disbandTime;
    }

    public boolean isDisbanding() {
        return this.disbandTime != 0L;
    }

    public boolean tickIsRegionsUnloadedCheck() {
        ++this.checkRegionsUnloadedBuffer;
        if (this.checkRegionsUnloadedBuffer >= 100) {
            this.checkRegionsUnloadedBuffer = 0;
            Rectangle regionRectangle = this.getRegionRectangle();
            boolean hasAnyRegionLoaded = false;
            for (int regionX = regionRectangle.x; regionX < regionRectangle.x + regionRectangle.width; ++regionX) {
                for (int regionY = regionRectangle.y; regionY < regionRectangle.y + regionRectangle.height; ++regionY) {
                    if (!this.level.regionManager.isRegionLoaded(regionX, regionY)) continue;
                    hasAnyRegionLoaded = true;
                    break;
                }
                if (hasAnyRegionLoaded) break;
            }
            return !hasAnyRegionLoaded;
        }
        return false;
    }

    public void tickSettlementFlagBuff() {
        Rectangle levelRectangle = this.getLevelRectangle();
        this.level.entityManager.players.streamInRegionsShape(this.getLevelRectangle(), 0).filter(player -> levelRectangle.contains(player.getX(), player.getY())).forEach(player -> {
            ActiveBuff ab = new ActiveBuff(BuffRegistry.SETTLEMENT_FLAG, (Mob)player, 100, null);
            player.buffManager.addBuff(ab, false);
        });
    }

    public void serverTick() {
        this.notifications.serverTick();
        if (this.disbandTime != 0L) {
            if (this.hasFlag() || this.disbandingPrevented) {
                this.disbandTime = 0L;
                this.markDirty(false);
                this.notifications.removeNotification("noflag", this.serverData);
            } else if (this.level.getTime() >= this.disbandTime) {
                this.disband();
            }
        } else if (!this.hasFlag() && !this.disbandingPrevented) {
            this.disbandTime = this.level.getTime() + (long)DISBAND_GRACE_SECONDS * 1000L;
            this.notifications.submitNotification("noflag", this.serverData, SettlementNotificationSeverity.URGENT);
            this.streamTeamMembers().forEach(client -> client.sendChatMessage(new LocalMessage("ui", "settlementflagremoved", "settlement", this.getSettlementName())));
            this.setOwner(null);
            this.markDirty(false);
        }
        if (this.isDirty) {
            if (this.level.isServer()) {
                this.level.getServer().network.sendToClientsWithAnyRegion((Packet)new PacketSettlementData(this, this.isDirtyFull), this.level, this.getRegionRectangle());
                ServerSettlementData serverData = this.getServerData();
                if (serverData != null) {
                    new SettlementDataEvent(serverData).applyAndSendToClientsAt(this.level);
                }
            }
            this.isDirty = false;
            this.isDirtyFull = false;
        }
    }

    public void onDisbanded() {
        SettlementFlagObjectEntity flagEntity = this.getFlagObjectEntity();
        if (flagEntity != null) {
            this.level.entityManager.doObjectDamageOverride(ObjectLayerRegistry.BASE_LAYER, flagEntity.tileX, flagEntity.tileY, flagEntity.getObject().objectHealth);
        }
    }

    public void writePacket(PacketWriter writer, boolean isFull) {
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextShortUnsigned(this.flagTier);
        writer.putNextContentPacket(this.settlementName.getContentPacket());
        writer.putNextLong(this.ownerAuth);
        writer.putNextBoolean(this.ownerName != null);
        if (this.ownerName != null) {
            writer.putNextString(this.ownerName);
        }
        writer.putNextInt(this.teamID);
        writer.putNextBoolean(this.look != null);
        if (this.look != null) {
            this.look.setupContentPacket(writer, true);
        }
        writer.putNextLong(this.disbandTime);
        writer.putNextBoolean(this.isPrivate);
        if (isFull) {
            this.notifications.writeFullPacket(writer);
        }
    }

    public void readPacket(PacketReader reader, boolean isFull) {
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.flagTier = reader.getNextShortUnsigned();
        this.settlementName = GameMessage.fromContentPacket(reader.getNextContentPacket());
        this.ownerAuth = reader.getNextLong();
        this.ownerName = reader.getNextBoolean() ? reader.getNextString() : null;
        this.teamID = reader.getNextInt();
        this.look = reader.getNextBoolean() ? new HumanLook(reader) : null;
        this.disbandTime = reader.getNextLong();
        this.isPrivate = reader.getNextBoolean();
        if (isFull) {
            this.notifications.readFullPacket(reader);
        }
    }

    public void addSaveData(SaveData save) {
        save.addInt("tileX", this.tileX);
        save.addInt("tileY", this.tileY);
        save.addInt("flagTier", this.flagTier);
        save.addBoolean("nameSet", this.nameSet);
        if (this.settlementName != null) {
            save.addSaveData(this.settlementName.getSaveData("name"));
        }
        save.addLong("ownerAuth", this.ownerAuth);
        save.addBoolean("isPrivate", this.isPrivate);
        if (this.disbandingPrevented) {
            save.addBoolean("disbandingPrevented", this.disbandingPrevented);
        }
        if (this.disbandTime != 0L) {
            save.addLong("disbandTime", this.disbandTime);
        }
        SaveData notificationsSave = new SaveData("notifications");
        this.notifications.addSaveData(notificationsSave);
        save.addSaveData(notificationsSave);
    }

    public void applyLoadData(LoadData save, OneWorldMigration migrationData, int tileXOffset, int tileYOffset) {
        int nextTileX = save.getInt("tileX", Integer.MIN_VALUE, false);
        int nextTileY = save.getInt("tileY", Integer.MIN_VALUE, false);
        if (nextTileX != Integer.MIN_VALUE && nextTileY != Integer.MIN_VALUE) {
            this.tileX = nextTileX + tileXOffset;
            this.tileY = nextTileY + tileYOffset;
        }
        this.flagTier = save.getInt("flagTier", this.flagTier, false);
        this.nameSet = save.getBoolean("nameSet", this.nameSet, false);
        this.settlementName = NetworkSettlementData.getName(save);
        if (this.settlementName == null) {
            this.settlementName = new LocalMessage("settlement", "defname", "biome", this.level.getBiome(this.tileX, this.tileY).getLocalization());
        }
        this.ownerAuth = save.getLong("ownerAuth", -1L);
        this.isPrivate = save.getBoolean("isPrivate", true);
        this.disbandingPrevented = save.getBoolean("disbandingPrevented", false, false);
        this.disbandTime = save.getLong("disbandTime", 0L, false);
        LoadData notificationsSave = save.getFirstLoadDataByName("notifications");
        if (notificationsSave != null) {
            this.notifications.applyLoadData(notificationsSave);
        }
        this.updateOwnerVariables();
    }

    public static GameMessage getName(LoadData save) {
        LoadData data = save.getFirstLoadDataByName("name");
        if (data != null) {
            return GameMessage.loadSave(data);
        }
        return null;
    }

    public static long getOwnerAuth(LoadData save) {
        return save.getLong("ownerAuth", -1L, false);
    }

    public static int getTeamID(Server server, long ownerAuth) {
        if (ownerAuth == -1L) {
            return -1;
        }
        return server.world.getTeams().getPlayerTeamID(ownerAuth);
    }

    public void markDirty(boolean full) {
        this.isDirty = true;
        if (full) {
            this.isDirtyFull = true;
        }
    }

    public void setFlagTile(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public void refreshRaidActive() {
        this.raidActiveTick = this.level.getWorldEntity().getGameTicks();
    }

    public boolean isRaidActive() {
        return this.raidActiveTick > this.level.getWorldEntity().getGameTicks() - 20L;
    }

    public void refreshRaidApproaching() {
        this.raidApproachingTick = this.level.getWorldEntity().getGameTicks();
    }

    public boolean isRaidApproaching() {
        return this.raidApproachingTick > this.level.getWorldEntity().getGameTicks() - 20L;
    }

    public boolean isRaidActiveOrApproaching() {
        return this.isRaidActive() || this.isRaidApproaching();
    }

    public void setOwner(ServerClient client) {
        if (client != null) {
            this.disbandingPrevented = false;
            if (this.ownerAuth != client.authentication) {
                this.markDirty(false);
            }
            this.updateOwnerVariables(client);
            if (client.achievementsLoaded()) {
                client.achievements().START_SETTLEMENT.markCompleted(client);
            }
        } else {
            if (this.ownerAuth != -1L) {
                this.markDirty(false);
            }
            this.ownerAuth = -1L;
            this.ownerName = null;
            this.teamID = -1;
            this.look = null;
            this.stats = new PlayerStats(false, EmptyStats.Mode.READ_ONLY);
        }
    }

    public GameMessage getSettlementName() {
        return this.settlementName;
    }

    public boolean isSettlementNameSet() {
        return this.nameSet;
    }

    public void setName(GameMessage name) {
        Objects.requireNonNull(name);
        if (this.settlementName == null || !this.settlementName.isSame(name)) {
            this.markDirty(false);
        }
        this.settlementName = name;
        this.nameSet = true;
    }

    public long getOwnerAuth() {
        return this.ownerAuth;
    }

    public String getOwnerName() {
        if (this.ownerName == null) {
            return "N/A";
        }
        return this.ownerName;
    }

    public PlayerTeam getTeam() {
        ServerClient owner;
        if (!this.level.isServer()) {
            throw new IllegalStateException("Cannot get player team on client levels");
        }
        if (this.ownerAuth != -1L && (owner = this.level.getServer().getClientByAuth(this.ownerAuth)) != null) {
            this.teamID = owner.getTeamID();
        }
        if (this.ownerAuth == -1L && this.teamID != -1) {
            this.teamID = -1;
            this.markDirty(false);
        }
        if (this.teamID == -1) {
            return null;
        }
        PlayerTeam team = this.level.getServer().world.getTeams().getTeam(this.teamID);
        if (team == null || !team.isMember(this.ownerAuth)) {
            this.teamID = -1;
            this.markDirty(false);
            return null;
        }
        return team;
    }

    public int getTeamID() {
        if (this.level.isServer()) {
            this.getTeam();
        }
        return this.teamID;
    }

    public Stream<ServerClient> streamTeamMembers() {
        if (!this.level.isServer()) {
            throw new IllegalStateException("Cannot stream team members on client levels");
        }
        PlayerTeam team = this.getTeam();
        if (team != null) {
            return team.streamOnlineMembers(this.level.getServer());
        }
        if (this.ownerAuth != -1L) {
            return Stream.of(this.level.getServer().getClientByAuth(this.ownerAuth)).filter(Objects::nonNull);
        }
        return Stream.empty();
    }

    public Stream<ServerClient> streamTeamMembersAndInSettlement() {
        Rectangle levelRectangle = this.getLevelRectangle();
        Stream<ServerClient> inSettlementStream = this.level.entityManager.players.streamInRegionsShape(this.getLevelRectangle(), 0).filter(player -> levelRectangle.contains(player.getX(), player.getY())).map(PlayerMob::getServerClient).filter(Objects::nonNull);
        return Stream.concat(this.streamTeamMembers(), inSettlementStream).distinct();
    }

    public HumanLook getLook() {
        if (this.look == null && this.ownerAuth != -1L && this.level.isServer()) {
            this.updateOwnerVariables();
        }
        return this.look;
    }

    public PlayerStats getStats() {
        return this.stats;
    }

    public boolean isPrivate() {
        return this.isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        if (this.isPrivate != isPrivate) {
            this.isPrivate = isPrivate;
            this.markDirty(false);
        }
    }

    public boolean doesClientHaveOwnerAccess(ServerClient client) {
        long ownerAuth = this.getOwnerAuth();
        return ownerAuth == -1L || ownerAuth == client.authentication;
    }

    public boolean isClientPartOf(NetworkClient client) {
        long ownerAuth = this.getOwnerAuth();
        return ownerAuth == client.authentication || client.isSameTeam(this.getTeamID());
    }

    public boolean doesClientHaveAccess(ServerClient client) {
        if (!this.isPrivate()) {
            return true;
        }
        if (this.doesClientHaveOwnerAccess(client)) {
            return true;
        }
        return client.isSameTeam(this.getTeamID());
    }

    public boolean doIHaveOwnerAccess(Client client) {
        long ownerAuth = this.getOwnerAuth();
        return ownerAuth == -1L || ownerAuth == GameAuth.getAuthentication();
    }

    public boolean doIHaveAccess(Client client) {
        if (!this.isPrivate()) {
            return true;
        }
        if (this.doIHaveOwnerAccess(client)) {
            return true;
        }
        if (this.getTeamID() == -1 || client.getTeam() == -1) {
            return false;
        }
        return this.getTeamID() == client.getTeam();
    }

    public void updateOwnerVariables() {
        if (!this.level.isServer()) {
            return;
        }
        if (this.ownerAuth != -1L) {
            ServerClient client = this.level.getServer().getClientByAuth(this.ownerAuth);
            if (client != null) {
                this.updateOwnerVariables(client);
            } else {
                LoadData clientSave = this.level.getServer().world.loadClientScript(this.ownerAuth);
                if (clientSave != null) {
                    this.ownerName = ServerClient.loadClientName(clientSave);
                    this.teamID = this.level.getServer().world.getTeams().getPlayerTeamID(this.ownerAuth);
                    this.look = ServerClient.loadClientLook(clientSave);
                    this.stats = ServerClient.loadClientStats(clientSave);
                } else {
                    this.ownerAuth = -1L;
                    this.ownerName = null;
                    this.teamID = -1;
                    this.look = new HumanLook();
                    this.stats = new PlayerStats(false, EmptyStats.Mode.READ_ONLY);
                    this.markDirty(false);
                }
            }
        } else {
            this.ownerName = null;
            this.look = null;
            this.teamID = -1;
            this.stats = new PlayerStats(false, EmptyStats.Mode.READ_ONLY);
        }
    }

    private void updateOwnerVariables(ServerClient client) {
        this.ownerAuth = client.authentication;
        this.ownerName = client.getName();
        this.teamID = client.getTeamID();
        this.look = new HumanLook(client.playerMob.look);
        this.stats = client.characterStats();
    }

    public boolean isTileWithinBounds(int tileX, int tileY) {
        return SettlementBoundsManager.isTileWithinBounds(tileX, tileY, this.tileX, this.tileY, this.flagTier);
    }

    public boolean isTileWithinLoadedRegionBounds(int tileX, int tileY) {
        return SettlementBoundsManager.isTileWithinLoadedRegionBounds(tileX, tileY, this.tileX, this.tileY, this.flagTier);
    }

    public Rectangle getLoadedRegionRectangle() {
        return SettlementBoundsManager.getLoadedRegionRectangleFromTier(this.tileX, this.tileY, this.flagTier);
    }

    public Rectangle getLoadedTileRectangle() {
        return SettlementBoundsManager.getLoadedTileRectangleFromTier(this.tileX, this.tileY, this.flagTier);
    }

    public Rectangle getRegionRectangle() {
        return SettlementBoundsManager.getRegionRectangleFromTier(this.tileX, this.tileY, this.flagTier);
    }

    public Rectangle getTileRectangle() {
        return SettlementBoundsManager.getTileRectangleFromTier(this.tileX, this.tileY, this.flagTier);
    }

    public Rectangle getLevelRectangle() {
        return SettlementBoundsManager.getLevelRectangleFromTier(this.tileX, this.tileY, this.flagTier);
    }

    public void markUnloaded() {
        this.isUnloaded = true;
    }

    public boolean isUnloaded() {
        return this.isUnloaded;
    }

    public void disband() {
        this.isDisbanded = true;
    }

    public boolean isDisbanded() {
        return this.isDisbanded;
    }

    public boolean isUnloadedOrDisbanded() {
        return this.isUnloaded() || this.isDisbanded();
    }

    public void markPreventDisbanding() {
        this.disbandingPrevented = true;
    }

    public boolean isDisbandingPrevented() {
        return this.disbandingPrevented;
    }

    public ServerSettlementData getServerData() {
        return this.serverData;
    }

    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Level newLevel, Point tileOffset, Point positionOffset) {
        this.tileX += tileOffset.x;
        this.tileY += tileOffset.y;
    }
}

