/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.notifications;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSettlementNotificationUpdate;
import necesse.engine.network.packet.PacketSettlementRequestNotificationFull;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.Mob;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotification;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationRegistry;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationSeverity;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementNotificationManager {
    public final NetworkSettlementData settlement;
    protected HashMap<Integer, ActiveNotification> notifications = new HashMap();
    protected HashSet<Integer> dirtyData = new HashSet();
    protected TicksPerSecond secondTick = TicksPerSecond.ticksPerSecond(1);

    public SettlementNotificationManager(NetworkSettlementData settlement) {
        this.settlement = settlement;
    }

    public void addSaveData(SaveData save) {
        for (ActiveNotification data : this.notifications.values()) {
            SaveData notificationSave = new SaveData("notification");
            data.addSaveData(notificationSave);
            save.addSaveData(notificationSave);
        }
    }

    public void applyLoadData(LoadData save) {
        for (LoadData notificationSave : save.getLoadDataByName("notification")) {
            try {
                ActiveNotification activeNotification = new ActiveNotification(notificationSave);
                this.notifications.put(activeNotification.notification.getID(), activeNotification);
            }
            catch (LoadDataException e) {
                GameLog.warn.println("Could not load notification data: " + e.getMessage());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeFullPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.notifications.size());
        for (ActiveNotification value : this.notifications.values()) {
            writer.putNextShortUnsigned(value.notification.getID());
            value.writeFullPacket(writer);
        }
    }

    public void readFullPacket(PacketReader reader) {
        this.notifications.clear();
        int size = reader.getNextShortUnsigned();
        for (int i = 0; i < size; ++i) {
            SettlementNotification notification = SettlementNotificationRegistry.getNotification(reader.getNextShortUnsigned());
            ActiveNotification value = new ActiveNotification(notification);
            value.readFullPacket(reader);
            this.notifications.put(notification.getID(), value);
        }
    }

    public void writeDirtyPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.dirtyData.size());
        for (int notificationID : this.dirtyData) {
            writer.putNextShortUnsigned(notificationID);
            ActiveNotification activeNotification = this.notifications.get(notificationID);
            if (activeNotification != null) {
                writer.putNextBoolean(true);
                activeNotification.writeDirtyPacket(writer);
                continue;
            }
            writer.putNextBoolean(false);
        }
        writer.putNextShortUnsigned(this.notifications.size());
    }

    public void readDirtyPacket(PacketReader reader) {
        boolean desynced = false;
        int size = reader.getNextShortUnsigned();
        for (int i = 0; i < size; ++i) {
            int notificationID = reader.getNextShortUnsigned();
            if (reader.getNextBoolean()) {
                ActiveNotification activeNotification = this.notifications.get(notificationID);
                if (activeNotification == null) {
                    SettlementNotification notification = SettlementNotificationRegistry.getNotification(notificationID);
                    activeNotification = new ActiveNotification(notification);
                    this.notifications.put(notificationID, activeNotification);
                }
                desynced = activeNotification.readDirtyPacket(reader) || desynced;
                continue;
            }
            this.notifications.remove(notificationID);
        }
        int finalSize = reader.getNextShortUnsigned();
        boolean bl = desynced = finalSize != this.notifications.size() || desynced;
        if (this.settlement.level.isClient() && desynced) {
            this.settlement.level.getClient().network.sendPacket(new PacketSettlementRequestNotificationFull(this.settlement));
        }
    }

    protected ActiveNotification getNotificationData(String notificationStringID, boolean createIfNotExists) {
        SettlementNotification notification = SettlementNotificationRegistry.getNotification(notificationStringID);
        if (notification != null) {
            return this.notifications.compute(notification.getID(), (id, old) -> {
                if (old == null && createIfNotExists) {
                    return new ActiveNotification(notification);
                }
                return old;
            });
        }
        throw new IllegalArgumentException("Could not find notification with stringID \"" + notificationStringID + "\"");
    }

    public ActiveNotification submitNotification(String notificationStringID, SettlerMob mob, SettlementNotificationSeverity severity) {
        ActiveNotification data = this.getNotificationData(notificationStringID, true);
        data.submitNotification(mob, severity);
        return data;
    }

    public void removeNotification(String notificationStringID, SettlerMob mob) {
        this.getNotificationData(notificationStringID, true).removeNotification(mob);
    }

    public ActiveNotification submitNotification(String notificationStringID, ServerSettlementData settlement, SettlementNotificationSeverity severity) {
        ActiveNotification data = this.getNotificationData(notificationStringID, true);
        data.submitNotification(settlement, severity);
        return data;
    }

    public void removeNotification(String notificationStringID, ServerSettlementData settlement) {
        this.getNotificationData(notificationStringID, true).removeNotification(settlement);
    }

    public GNDItemMap getGNDData(String notificationStringID) {
        ActiveNotification data = this.getNotificationData(notificationStringID, false);
        if (data != null) {
            return data.gndData;
        }
        return null;
    }

    public void serverTick() {
        this.secondTick.gameTick();
        if (this.secondTick.shouldTick()) {
            HashSet<Integer> removes = new HashSet<Integer>();
            LinkedList<ActiveNotification> cleans = new LinkedList<ActiveNotification>();
            for (Map.Entry<Integer, ActiveNotification> entry : this.notifications.entrySet()) {
                entry.getValue().serverTickValids();
                if (entry.getValue().shouldRemove()) {
                    removes.add(entry.getKey());
                    continue;
                }
                if (!entry.getValue().isDirty()) continue;
                this.dirtyData.add(entry.getKey());
                cleans.add(entry.getValue());
            }
            for (Integer key : removes) {
                this.notifications.remove(key);
                this.dirtyData.add(key);
            }
            if (!this.dirtyData.isEmpty() && this.settlement.level.isServer()) {
                this.settlement.level.getServer().network.sendToClientsWithAnyRegion((Packet)new PacketSettlementNotificationUpdate(this), this.settlement.level, this.settlement.getRegionRectangle());
                this.dirtyData.clear();
                cleans.forEach(ActiveNotification::clean);
            }
        }
    }

    public ServerSettlementData getSettlementData() {
        return this.settlement.getServerData();
    }

    public Collection<ActiveNotification> getNotifications() {
        return this.notifications.values();
    }

    public Set<Integer> getNotificationIDs() {
        return this.notifications.keySet();
    }

    public class ActiveNotification {
        public final SettlementNotification notification;
        protected SettlementNotificationSeverity highestSeverity = SettlementNotificationSeverity.NOTE;
        protected HashMap<Integer, SettlerSubmission> settlerSubmissions = new HashMap();
        protected SettlementNotificationSeverity settlementSubmitted;
        protected GNDItemMap gndData = new GNDItemMap();

        public ActiveNotification(SettlementNotification notification) {
            this.notification = notification;
        }

        public void addSaveData(SaveData save) {
            save.addSafeString("stringID", this.notification.getStringID());
            SaveData submissionsSave = new SaveData("settlers");
            for (SettlerSubmission submission : this.settlerSubmissions.values()) {
                SaveData submissionSave = new SaveData("submission");
                submission.addSaveData(submissionSave);
                submissionsSave.addSaveData(submissionSave);
            }
            save.addSaveData(submissionsSave);
            if (this.settlementSubmitted != null) {
                save.addEnum("settlementSubmitted", this.settlementSubmitted);
            }
            if (this.gndData.getMapSize() > 0) {
                SaveData gnd = new SaveData("GNDData");
                this.gndData.addSaveData(gnd);
                save.addSaveData(gnd);
            }
        }

        public ActiveNotification(LoadData save) throws LoadDataException {
            String notificationStringID = save.getSafeString("stringID");
            try {
                this.notification = SettlementNotificationRegistry.getNotification(notificationStringID);
                if (this.notification == null) {
                    throw new LoadDataException("Could not find notification with stringID " + notificationStringID);
                }
            }
            catch (LoadDataException e) {
                throw e;
            }
            catch (Exception e) {
                throw new LoadDataException("Could not load notification with stringID " + notificationStringID, e);
            }
            LoadData submissionsSave = save.getFirstLoadDataByName("settlers");
            if (submissionsSave != null) {
                for (LoadData submissionSave : submissionsSave.getLoadDataByName("submission")) {
                    try {
                        SettlerSubmission submission = new SettlerSubmission(submissionSave);
                        this.settlerSubmissions.put(submission.submitterUniqueID, submission);
                    }
                    catch (LoadDataException e) {
                        GameLog.warn.println("Could not load " + notificationStringID + " notification settler submission: " + e.getMessage());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            this.settlementSubmitted = save.getEnum(SettlementNotificationSeverity.class, "settlementSubmitted", this.settlementSubmitted, false);
            LoadData gnd = save.getFirstLoadDataByName("GNDData");
            if (gnd != null) {
                this.gndData = new GNDItemMap(gnd);
                this.gndData.cleanAll();
            }
            this.calculateHighestSeverity();
        }

        public void writeFullPacket(PacketWriter writer) {
            writer.putNextShortUnsigned(this.settlerSubmissions.size());
            for (SettlerSubmission value : this.settlerSubmissions.values()) {
                value.writePacket(writer);
            }
            writer.putNextBoolean(this.settlementSubmitted != null);
            if (this.settlementSubmitted != null) {
                writer.putNextEnum(this.settlementSubmitted);
            }
            this.gndData.writePacket(writer);
        }

        public void readFullPacket(PacketReader reader) {
            this.settlerSubmissions.clear();
            int size = reader.getNextShortUnsigned();
            for (int i = 0; i < size; ++i) {
                SettlerSubmission submission = new SettlerSubmission(reader);
                this.settlerSubmissions.put(submission.submitterUniqueID, submission);
            }
            if (reader.getNextBoolean()) {
                this.settlementSubmitted = reader.getNextEnum(SettlementNotificationSeverity.class);
            }
            this.gndData.readPacket(reader);
            this.calculateHighestSeverity();
        }

        public void writeDirtyPacket(PacketWriter writer) {
            writer.putNextShortUnsigned(this.settlerSubmissions.size());
            for (SettlerSubmission value : this.settlerSubmissions.values()) {
                value.writePacket(writer);
            }
            writer.putNextBoolean(this.settlementSubmitted != null);
            if (this.settlementSubmitted != null) {
                writer.putNextEnum(this.settlementSubmitted);
            }
            this.gndData.writeDirtyPacket(writer);
        }

        public boolean readDirtyPacket(PacketReader reader) {
            this.settlerSubmissions.clear();
            int size = reader.getNextShortUnsigned();
            for (int i = 0; i < size; ++i) {
                SettlerSubmission submission = new SettlerSubmission(reader);
                this.settlerSubmissions.put(submission.submitterUniqueID, submission);
            }
            if (reader.getNextBoolean()) {
                this.settlementSubmitted = reader.getNextEnum(SettlementNotificationSeverity.class);
            }
            boolean desynced = this.gndData.readDirtyPacket(reader);
            this.calculateHighestSeverity();
            return desynced;
        }

        public boolean isDirty() {
            return this.gndData.isDirty();
        }

        public void clean() {
            this.gndData.cleanAll();
        }

        public void serverTickValids() {
            ServerSettlementData data;
            HashSet<Integer> removes = new HashSet<Integer>();
            for (Map.Entry<Integer, SettlerSubmission> entry : this.settlerSubmissions.entrySet()) {
                entry.getValue().refreshMob();
                if (entry.getValue().getMob() != null && this.notification.isStillValid(entry.getValue().getMob())) continue;
                removes.add(entry.getKey());
            }
            if (!(this.settlementSubmitted == null || (data = SettlementNotificationManager.this.getSettlementData()) != null && this.notification.isStillValid(data))) {
                this.settlementSubmitted = null;
            }
            for (Integer key : removes) {
                this.settlerSubmissions.remove(key);
            }
        }

        public void submitNotification(SettlerMob mob, SettlementNotificationSeverity severity) {
            SettlerSubmission current = this.settlerSubmissions.get(mob.getMob().getUniqueID());
            if (current == null || current.severity != severity) {
                if (severity.ordinal() > this.highestSeverity.ordinal()) {
                    this.highestSeverity = severity;
                    SettlementNotificationManager.this.dirtyData.add(this.notification.getID());
                }
                SettlerSubmission submission = new SettlerSubmission(mob, severity);
                this.settlerSubmissions.put(submission.submitterUniqueID, submission);
                SettlementNotificationManager.this.dirtyData.add(this.notification.getID());
            }
        }

        public void removeNotification(SettlerMob mob) {
            SettlerSubmission remove = this.settlerSubmissions.remove(mob.getMob().getUniqueID());
            if (remove != null) {
                if (this.shouldRemove()) {
                    SettlementNotificationManager.this.notifications.remove(this.notification.getID());
                }
                SettlementNotificationManager.this.dirtyData.add(this.notification.getID());
                if (remove.severity.ordinal() >= this.highestSeverity.ordinal()) {
                    this.calculateHighestSeverity();
                }
            }
        }

        public void submitNotification(ServerSettlementData settlement, SettlementNotificationSeverity severity) {
            if (this.settlementSubmitted != severity) {
                this.settlementSubmitted = severity;
                SettlementNotificationManager.this.dirtyData.add(this.notification.getID());
                if (severity.ordinal() >= this.highestSeverity.ordinal()) {
                    this.highestSeverity = severity;
                } else {
                    this.calculateHighestSeverity();
                }
            }
        }

        public void removeNotification(ServerSettlementData settlement) {
            if (this.settlementSubmitted != null) {
                this.settlementSubmitted = null;
                this.calculateHighestSeverity();
                SettlementNotificationManager.this.dirtyData.add(this.notification.getID());
            }
        }

        public boolean shouldRemove() {
            return this.settlementSubmitted == null && this.settlerSubmissions.isEmpty();
        }

        protected void calculateHighestSeverity() {
            this.highestSeverity = this.settlerSubmissions.values().stream().map(s -> s.severity).max(Comparator.comparingInt(Enum::ordinal)).orElse(SettlementNotificationSeverity.NOTE);
            if (this.settlementSubmitted != null && this.settlementSubmitted.ordinal() > this.highestSeverity.ordinal()) {
                this.highestSeverity = this.settlementSubmitted;
            }
        }

        public SettlementNotificationSeverity getHighestSeverity() {
            return this.highestSeverity;
        }

        public Collection<SettlerSubmission> getSettlerSubmissions() {
            return this.settlerSubmissions.values();
        }

        public boolean isSettlementSubmitted() {
            return this.settlementSubmitted != null;
        }

        public GNDItemMap getGndData() {
            return this.gndData;
        }
    }

    public class SettlerSubmission {
        public final int submitterUniqueID;
        protected SettlerMob submitterMob;
        protected SettlementNotificationSeverity severity;

        public SettlerSubmission(SettlerMob submitterMob, SettlementNotificationSeverity severity) {
            this.submitterUniqueID = submitterMob.getMob().getUniqueID();
            this.submitterMob = submitterMob;
            this.severity = severity;
        }

        public void addSaveData(SaveData save) {
            save.addInt("submitterUniqueID", this.submitterUniqueID);
            save.addEnum("severity", this.severity);
        }

        public SettlerSubmission(LoadData save) throws LoadDataException {
            this.submitterUniqueID = save.getInt("submitterUniqueID", -1);
            if (this.submitterUniqueID == -1) {
                throw new LoadDataException("Could not load submitter uniqueID");
            }
            this.severity = save.getEnum(SettlementNotificationSeverity.class, "severity", null);
            if (this.severity == null) {
                throw new LoadDataException("Could not load notification severity");
            }
        }

        public void writePacket(PacketWriter writer) {
            writer.putNextInt(this.submitterUniqueID);
            writer.putNextEnum(this.severity);
        }

        public SettlerSubmission(PacketReader reader) {
            this.submitterUniqueID = reader.getNextInt();
            this.severity = reader.getNextEnum(SettlementNotificationSeverity.class);
        }

        public SettlerMob getMob() {
            if (this.submitterMob == null) {
                this.refreshMob();
            } else {
                Mob mob = this.submitterMob.getMob();
                if (mob.removed() || !mob.getLevel().isSamePlace(SettlementNotificationManager.this.settlement.level)) {
                    this.refreshMob();
                }
            }
            return this.submitterMob;
        }

        public void refreshMob() {
            Mob mob = SettlementNotificationManager.this.settlement.level.entityManager.mobs.get(this.submitterUniqueID, false);
            this.submitterMob = mob instanceof SettlerMob ? (SettlerMob)((Object)mob) : null;
        }

        public SettlementNotificationSeverity getSeverity() {
            return this.severity;
        }
    }
}

