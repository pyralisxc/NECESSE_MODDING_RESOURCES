/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats.stats;

import java.util.HashSet;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.GameStat;
import necesse.engine.playerStats.GameStats;
import necesse.engine.registries.JournalRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class JournalEntriesDiscovered
extends GameStat {
    protected HashSet<String> discoveredJournalEntries = new HashSet();
    protected HashSet<String> dirtyJournalStringIDs = new HashSet();

    public JournalEntriesDiscovered(EmptyStats parent, String stringID) {
        super(parent, stringID);
    }

    @Override
    public void clean() {
        super.clean();
        this.dirtyJournalStringIDs.clear();
    }

    protected void setJournalDiscovered(String journalStringID, boolean discovered, boolean updateSteam) {
        int journalID = JournalRegistry.getJournalEntryID(journalStringID);
        if (journalID != -1) {
            boolean prevStat = this.discoveredJournalEntries.contains(journalStringID);
            if (prevStat == discovered) {
                return;
            }
            if (discovered) {
                this.discoveredJournalEntries.add(journalStringID);
            } else {
                this.discoveredJournalEntries.remove(journalStringID);
            }
            if (updateSteam) {
                this.updatePlatform();
            }
            this.dirtyJournalStringIDs.add(journalStringID);
            this.markImportantDirty();
        }
    }

    public void markDiscoveredJournal(String journalStringID) {
        if (this.parent.mode == EmptyStats.Mode.READ_ONLY) {
            throw new IllegalStateException("Cannot set read only stats");
        }
        this.setJournalDiscovered(journalStringID, true, true);
    }

    public boolean isJournalDiscovered(String journalStringID) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.discoveredJournalEntries.contains(journalStringID);
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof JournalEntriesDiscovered) {
            JournalEntriesDiscovered other = (JournalEntriesDiscovered)stat;
            other.discoveredJournalEntries.forEach(s -> this.setJournalDiscovered((String)s, true, true));
        }
    }

    @Override
    public void resetCombine() {
        this.discoveredJournalEntries.clear();
    }

    protected void updatePlatform() {
        if (!this.parent.controlAchievements) {
            // empty if block
        }
    }

    @Override
    public void loadStatFromPlatform(GameStats stats) {
    }

    @Override
    public void addSaveData(SaveData save) {
        save.addStringHashSet("discoveredJournalEntries", this.discoveredJournalEntries);
    }

    @Override
    public void applyLoadData(LoadData save) {
        HashSet<String> loadedStringIDs = save.getStringHashSet("discoveredJournalEntries", new HashSet<String>(), false);
        this.discoveredJournalEntries = new HashSet(loadedStringIDs.size());
        for (String loadedStringID : loadedStringIDs) {
            int journalEntryID = JournalRegistry.getJournalEntryID(loadedStringID);
            if (journalEntryID == -1) continue;
            this.discoveredJournalEntries.add(loadedStringID);
        }
        for (LoadData component : save.getLoadData()) {
            if (component.getName().equals("discoveredJournalEntries") || !component.isData()) continue;
            try {
                int journalEntryID = JournalRegistry.getJournalEntryID(component.getName());
                if (journalEntryID != -1) {
                    int amount = Integer.parseInt(component.getData());
                    if (amount <= 0) continue;
                    this.setJournalDiscovered(JournalRegistry.getJournalEntryStringID(journalEntryID), true, false);
                    continue;
                }
                if (!save.getName().equals(this.stringID)) continue;
                GameLog.warn.println("Could not load journals discovered stat stringID: " + component.getName());
            }
            catch (NumberFormatException e) {
                if (!save.getName().equals(this.stringID)) continue;
                GameLog.warn.println("Could not load journals discovered stat number: " + component.getData());
            }
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.discoveredJournalEntries.size());
        for (String entryStringID : this.discoveredJournalEntries) {
            writer.putNextShortUnsigned(JournalRegistry.getJournalEntryID(entryStringID));
        }
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        int amount = reader.getNextShortUnsigned();
        this.discoveredJournalEntries.clear();
        for (int i = 0; i < amount; ++i) {
            int entryID = reader.getNextShortUnsigned();
            String entryStringID = JournalRegistry.getJournalEntryStringID(entryID);
            this.setJournalDiscovered(entryStringID, true, true);
        }
    }

    @Override
    public void setupDirtyPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.dirtyJournalStringIDs.size());
        for (String entryStringID : this.dirtyJournalStringIDs) {
            writer.putNextShortUnsigned(JournalRegistry.getJournalEntryID(entryStringID));
            writer.putNextBoolean(this.discoveredJournalEntries.contains(entryStringID));
        }
    }

    @Override
    public void applyDirtyPacket(PacketReader reader) {
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int entryID = reader.getNextShortUnsigned();
            boolean discovered = reader.getNextBoolean();
            String entryStringID = JournalRegistry.getJournalEntryStringID(entryID);
            this.setJournalDiscovered(entryStringID, discovered, true);
        }
    }
}

