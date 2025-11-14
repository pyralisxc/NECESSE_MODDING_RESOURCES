/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.server;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.journal.listeners.AdventurePartyChangedJournalChallengeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketAdventurePartyAdd;
import necesse.engine.network.packet.PacketAdventurePartyBuffPolicy;
import necesse.engine.network.packet.PacketAdventurePartyRemove;
import necesse.engine.network.packet.PacketAdventurePartySync;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.GuardHumanMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.events.AdventurePartyChangedEvent;
import necesse.level.maps.Level;

public class AdventureParty {
    public final Object MOBS_LOCK = new Object();
    protected ServerClient serverClient;
    protected Client client;
    protected int validTicker;
    protected int syncTicker;
    protected BuffPotionPolicy buffPotionPolicy = BuffPotionPolicy.IN_COMBAT;
    protected HashMap<Integer, HumanMob> mobs = new HashMap();

    public AdventureParty(ServerClient serverClient) {
        this.serverClient = serverClient;
    }

    public AdventureParty(Client client) {
        this.client = client;
    }

    public void addSaveData(SaveData save) {
        save.addEnum("buffPotionPolicy", this.buffPotionPolicy);
    }

    public void applyLoadData(LoadData save) {
        this.buffPotionPolicy = save.getEnum(BuffPotionPolicy.class, "buffPotionPolicy", this.buffPotionPolicy, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeUpdatePacket(PacketWriter writer) {
        writer.putNextEnum(this.buffPotionPolicy);
        Object object = this.MOBS_LOCK;
        synchronized (object) {
            writer.putNextShortUnsigned(this.mobs.size());
            for (int mobUniqueID : this.mobs.keySet()) {
                writer.putNextInt(mobUniqueID);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void readUpdatePacket(PacketReader reader) {
        this.buffPotionPolicy = reader.getNextEnum(BuffPotionPolicy.class);
        Object object = this.MOBS_LOCK;
        synchronized (object) {
            this.mobs.clear();
            int size = reader.getNextShortUnsigned();
            for (int i = 0; i < size; ++i) {
                this.mobs.put(reader.getNextInt(), null);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serverTick() {
        ++this.validTicker;
        if (this.validTicker >= 100) {
            LinkedList<Integer> invalid = new LinkedList<Integer>();
            Object object = this.MOBS_LOCK;
            synchronized (object) {
                for (Map.Entry<Integer, HumanMob> entry : this.mobs.entrySet()) {
                    HumanMob mob = entry.getValue();
                    if (mob != null && !mob.removed() && mob.adventureParty.getServerClient() == this.serverClient) continue;
                    invalid.add(entry.getKey());
                    JournalChallengeRegistry.handleListeners(this.serverClient, AdventurePartyChangedJournalChallengeListener.class, challenge -> challenge.onPartyMemberRemoved(this.serverClient, mob, true, false, false));
                }
                invalid.forEach(this.mobs::remove);
            }
            this.validTicker = 0;
            object = this.MOBS_LOCK;
            synchronized (object) {
                this.checkAchievements();
            }
        }
        ++this.syncTicker;
        if (this.syncTicker >= 600) {
            this.serverClient.sendPacket(new PacketAdventurePartySync(this.serverClient));
            this.syncTicker = 0;
        }
    }

    public void clientTick() {
        ++this.validTicker;
        if (this.validTicker >= 100) {
            this.updateMobsFromLevel(this.client.getLevel());
            this.validTicker = 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateMobsFromLevel(Level level) {
        Object object = this.MOBS_LOCK;
        synchronized (object) {
            for (Map.Entry<Integer, HumanMob> entry : this.mobs.entrySet()) {
                Mob mob;
                Mob mob2 = mob = level == null ? null : level.entityManager.mobs.get(entry.getKey(), true);
                if (mob instanceof HumanMob) {
                    entry.setValue((HumanMob)mob);
                    continue;
                }
                entry.setValue(null);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serverAdd(HumanMob mob) {
        Object object = this.MOBS_LOCK;
        synchronized (object) {
            this.mobs.put(mob.getUniqueID(), mob);
        }
        if (this.serverClient.checkHasRequestedSelf()) {
            this.serverClient.sendPacket(new PacketAdventurePartyAdd(this, mob.getUniqueID()));
            new AdventurePartyChangedEvent().applyAndSendToClient(this.serverClient);
            if (this.serverClient.achievementsLoaded()) {
                this.serverClient.achievements().TEAMWORK.markCompleted(this.serverClient);
            }
        }
        JournalChallengeRegistry.handleListeners(this.serverClient, AdventurePartyChangedJournalChallengeListener.class, challenge -> challenge.onPartyMemberAdded(this.serverClient, mob));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean serverRemove(HumanMob mob, boolean mobRemove, boolean isDeath) {
        boolean out;
        Object object = this.MOBS_LOCK;
        synchronized (object) {
            out = this.mobs.remove(mob.getUniqueID()) != null;
        }
        if (this.serverClient.checkHasRequestedSelf()) {
            this.serverClient.sendPacket(new PacketAdventurePartyRemove(this, mob.getUniqueID()));
            new AdventurePartyChangedEvent().applyAndSendToClient(this.serverClient);
        }
        JournalChallengeRegistry.handleListeners(this.serverClient, AdventurePartyChangedJournalChallengeListener.class, challenge -> challenge.onPartyMemberRemoved(this.serverClient, mob, false, mobRemove, isDeath));
        return out;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clientRemove(int uniqueID) {
        Object object = this.MOBS_LOCK;
        synchronized (object) {
            this.mobs.remove(uniqueID);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clientAdd(int uniqueID) {
        Level level = this.client.getLevel();
        Mob mob = level == null ? null : level.entityManager.mobs.get(uniqueID, true);
        Object object = this.MOBS_LOCK;
        synchronized (object) {
            if (mob instanceof HumanMob) {
                this.mobs.put(uniqueID, (HumanMob)mob);
            } else {
                this.mobs.put(uniqueID, null);
            }
        }
    }

    public void setBuffPotionPolicy(BuffPotionPolicy policy, boolean sendUpdatePacket) {
        this.buffPotionPolicy = policy;
        if (sendUpdatePacket) {
            if (this.serverClient != null && this.serverClient.checkHasRequestedSelf()) {
                this.serverClient.sendPacket(new PacketAdventurePartyBuffPolicy(policy));
                new AdventurePartyChangedEvent().applyAndSendToClient(this.serverClient);
            } else if (this.client != null) {
                this.client.network.sendPacket(new PacketAdventurePartyBuffPolicy(policy));
            }
        }
    }

    public BuffPotionPolicy getBuffPotionPolicy() {
        return this.buffPotionPolicy;
    }

    public Set<Integer> getMobUniqueIDs() {
        return this.mobs.keySet();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HumanMob getMemberMob(int uniqueID) {
        Object object = this.MOBS_LOCK;
        synchronized (object) {
            return this.mobs.get(uniqueID);
        }
    }

    public Collection<HumanMob> getMobs() {
        return this.mobs.values();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean contains(int mobUniqueID) {
        Object object = this.MOBS_LOCK;
        synchronized (object) {
            return this.mobs.containsKey(mobUniqueID);
        }
    }

    public boolean contains(HumanMob mob) {
        return this.contains(mob.getUniqueID());
    }

    public int getSize() {
        return this.mobs.size();
    }

    public boolean isEmpty() {
        return this.mobs.isEmpty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getMobsHash() {
        AdventureParty adventureParty = this;
        synchronized (adventureParty) {
            return this.mobs.keySet().hashCode();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getDebugString() {
        if (!this.mobs.isEmpty()) {
            Object object = this.MOBS_LOCK;
            synchronized (object) {
                String arrayString = Arrays.toString(this.mobs.entrySet().stream().map(e -> e.getKey() + (e.getValue() == null ? "?" : "")).toArray());
                return this.mobs.size() + ", " + arrayString;
            }
        }
        return null;
    }

    public void checkAchievements() {
        if (this.serverClient == null) {
            return;
        }
        if (!this.serverClient.achievementsLoaded()) {
            return;
        }
        AchievementManager achievements = this.serverClient.achievements();
        if (!achievements.SECRET_SERVICE.isCompleted() && this.getSize() == 2) {
            boolean valid = true;
            for (HumanMob mob : this.getMobs()) {
                if (!(mob instanceof GuardHumanMob)) {
                    valid = false;
                    break;
                }
                InventoryItem head = mob.getDisplayArmor(0, null);
                if (head == null || !head.item.getStringID().equals("sunglasses")) {
                    valid = false;
                    break;
                }
                InventoryItem chest = mob.getDisplayArmor(1, null);
                if (chest == null || !chest.item.getStringID().equals("blazer")) {
                    valid = false;
                    break;
                }
                InventoryItem feet = mob.getDisplayArmor(2, null);
                if (feet != null && feet.item.getStringID().equals("dressshoes")) continue;
                valid = false;
                break;
            }
            if (valid) {
                achievements.SECRET_SERVICE.markCompleted(this.serverClient);
            }
        }
        if (!achievements.YOU_AND_WHAT_ARMY.isCompleted() && this.getSize() >= 10) {
            int count = 0;
            for (HumanMob mob : this.getMobs()) {
                InventoryItem weapon;
                boolean valid = true;
                for (int i = 0; i < 3; ++i) {
                    InventoryItem armor = mob.getArmorItem(i);
                    if (armor != null && !(armor.item.getUpgradeTier(armor) < 5.0f)) continue;
                    valid = false;
                    break;
                }
                if (!valid || (weapon = mob.getInventoryWeapon()) == null || weapon.item.getUpgradeTier(weapon) < 5.0f || ++count < 10) continue;
                achievements.YOU_AND_WHAT_ARMY.markCompleted(this.serverClient);
                break;
            }
        }
    }

    public static enum BuffPotionPolicy {
        ALWAYS(new LocalMessage("ui", "buffpotionpolicyalways")),
        IN_COMBAT(new LocalMessage("ui", "buffpotionpolicyincombat")),
        SAME_AS_ME(new LocalMessage("ui", "buffpotionpolicysameasme")),
        ON_HOTKEY(new LocalMessage("ui", "buffpotionpolicyonhotkey")),
        NEVER(new LocalMessage("ui", "buffpotionpolicynever"));

        public final GameMessage displayName;

        private BuffPotionPolicy(GameMessage displayName) {
            this.displayName = displayName;
        }
    }
}

