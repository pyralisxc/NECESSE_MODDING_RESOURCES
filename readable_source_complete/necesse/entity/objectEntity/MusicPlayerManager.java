/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.util.Arrays;
import java.util.Objects;
import necesse.engine.MusicList;
import necesse.engine.MusicOptions;
import necesse.engine.MusicOptionsOffset;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.GameMusic;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.VinylItem;

public abstract class MusicPlayerManager {
    protected boolean isPaused;
    protected boolean hasAnyVinyls;
    protected VinylItem[] currentVinyls;
    protected MusicList currentMusicList;
    protected long playingOffset;

    public MusicPlayerManager(int slots) {
        this.currentVinyls = new VinylItem[slots];
    }

    public boolean fixSlots(int slots) {
        if (this.currentVinyls.length != slots) {
            this.currentVinyls = Arrays.copyOf(this.currentVinyls, slots);
            return true;
        }
        return false;
    }

    public boolean fixSlots(Inventory inventory, boolean isClient) {
        if (this.fixSlots(inventory.getSize())) {
            this.updatePlayingAll(inventory, isClient);
        }
        return false;
    }

    public abstract void markDirty();

    public void addSaveData(SaveData save) {
        save.addBoolean("isPaused", this.isPaused);
        save.addLong("playingOffset", this.getMusicPlayingOffset());
    }

    public void applyLoadData(LoadData save) {
        this.isPaused = save.getBoolean("isPaused", false, false);
        long playingOffset = save.getLong("playingOffset", 0L, false);
        this.playingOffset = !this.hasAnyVinyls ? 0L : (this.isPaused ? playingOffset : System.currentTimeMillis() - playingOffset);
        if (this.currentMusicList != null) {
            this.currentMusicList.setTimeOffset(this.getMusicPlayingOffset());
        }
    }

    public void writeContentPacket(PacketWriter writer, boolean full) {
        if (full) {
            writer.putNextShortUnsigned(this.currentVinyls.length);
            for (VinylItem currentVinyl : this.currentVinyls) {
                if (currentVinyl != null) {
                    writer.putNextBoolean(true);
                    writer.putNextShortUnsigned(currentVinyl.getID());
                    continue;
                }
                writer.putNextBoolean(false);
            }
            writer.putNextBoolean(this.hasAnyVinyls);
        }
        writer.putNextBoolean(this.isPaused);
        writer.putNextLong(this.getMusicPlayingOffset());
    }

    public void readContentPacket(PacketReader reader, boolean full) {
        if (full) {
            int length = reader.getNextShortUnsigned();
            for (int i = 0; i < length; ++i) {
                Item item;
                VinylItem vinylItem = null;
                if (reader.getNextBoolean() && (item = ItemRegistry.getItem(reader.getNextShortUnsigned())) instanceof VinylItem) {
                    vinylItem = (VinylItem)item;
                }
                if (i >= this.currentVinyls.length) continue;
                this.currentVinyls[i] = vinylItem;
            }
            this.hasAnyVinyls = reader.getNextBoolean();
        }
        this.isPaused = reader.getNextBoolean();
        long offset = reader.getNextLong();
        this.playingOffset = !this.hasAnyVinyls ? 0L : (this.isPaused ? offset : System.currentTimeMillis() - offset);
        if (this.currentMusicList != null) {
            this.currentMusicList.setTimeOffset(this.getMusicPlayingOffset());
        }
    }

    public MusicList getCurrentMusicList() {
        if (this.currentMusicList != null) {
            this.currentMusicList.setTimeOffset(this.getMusicPlayingOffset());
        }
        return this.currentMusicList;
    }

    public boolean hasAnyVinyls() {
        return this.hasAnyVinyls;
    }

    public int getSlots() {
        return this.currentVinyls.length;
    }

    public MusicOptionsOffset getCurrentMusic() {
        if (this.currentMusicList != null) {
            this.currentMusicList.setTimeOffset(this.getMusicPlayingOffset());
            return this.currentMusicList.getExpectedMusicPlaying();
        }
        return null;
    }

    public MusicOptions getPreviousMusic() {
        ItemMusicOptions lastItemOptions;
        int lastSlotPlaying = -1;
        MusicOptionsOffset lastMusic = this.getCurrentMusic();
        if (lastMusic != null && (lastItemOptions = MusicPlayerManager.toItemMusic(lastMusic.options)) != null) {
            lastSlotPlaying = lastItemOptions.itemSlot;
        }
        if (lastSlotPlaying != -1) {
            for (int i = this.currentVinyls.length; i > 0; --i) {
                int slot = Math.floorMod(lastSlotPlaying + i - 1, this.currentVinyls.length);
                if (this.currentVinyls[slot] == null) continue;
                return new ItemMusicOptions(this.currentVinyls[slot], slot);
            }
        }
        return null;
    }

    public boolean setDesiredMusic(GameMusic music, long offset) {
        if (this.currentMusicList == null) {
            return false;
        }
        long currentOffset = 0L;
        for (MusicOptions options : this.currentMusicList.getMusicInList()) {
            if (options.music == music) {
                this.playingOffset = this.isPaused ? currentOffset + offset : System.currentTimeMillis() + currentOffset + offset;
                return true;
            }
            currentOffset += options.getMusicListMilliseconds();
        }
        return false;
    }

    public void setIsPaused(boolean isPaused) {
        if (this.isPaused != isPaused) {
            this.isPaused = isPaused;
            this.playingOffset = !this.hasAnyVinyls ? 0L : System.currentTimeMillis() - this.playingOffset;
            this.markDirty();
        }
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public void forwardMilliseconds(long milliseconds) {
        if (!this.hasAnyVinyls) {
            return;
        }
        if (milliseconds != 0L) {
            this.playingOffset = this.isPaused ? (this.playingOffset -= milliseconds) : (this.playingOffset += milliseconds);
            this.markDirty();
        }
    }

    public void setOffset(long milliseconds) {
        if (!this.hasAnyVinyls) {
            return;
        }
        this.playingOffset = this.isPaused ? milliseconds : System.currentTimeMillis() + milliseconds;
        this.markDirty();
    }

    public long getMusicPlayingOffset() {
        if (!this.hasAnyVinyls) {
            return 0L;
        }
        if (this.isPaused) {
            return this.playingOffset;
        }
        return System.currentTimeMillis() - this.playingOffset;
    }

    public void updateHasAnyVinyls(boolean isClient) {
        boolean lastHasAnyVinyls = this.hasAnyVinyls;
        this.hasAnyVinyls = Arrays.stream(this.currentVinyls).anyMatch(o -> !Objects.isNull(o));
        if (this.hasAnyVinyls) {
            if (!lastHasAnyVinyls) {
                this.playingOffset = this.isPaused ? 0L : System.currentTimeMillis();
            }
            if (isClient) {
                this.updateClientPlaying();
            }
        } else {
            this.currentMusicList = null;
            this.playingOffset = this.isPaused ? 0L : System.currentTimeMillis();
        }
    }

    public void updateClientPlaying() {
        ItemMusicOptions lastItemOptions;
        int lastSlotPlaying = -1;
        MusicOptionsOffset lastMusic = this.getCurrentMusic();
        if (lastMusic != null && (lastItemOptions = MusicPlayerManager.toItemMusic(lastMusic.options)) != null) {
            lastSlotPlaying = lastItemOptions.itemSlot;
        }
        this.currentMusicList = new MusicList();
        long offset = 0L;
        boolean found = false;
        for (int slot = 0; slot < this.currentVinyls.length; ++slot) {
            VinylItem item = this.currentVinyls[slot];
            if (item == null) continue;
            MusicOptions next = new ItemMusicOptions(item, slot).fadeInTime(1000).volume(1.5f);
            if (lastMusic != null) {
                if (lastSlotPlaying == slot) {
                    offset -= lastMusic.offset;
                    found = true;
                } else if (!found) {
                    offset -= next.music.sound.getLengthInMillis() - (long)next.getFadeOutTime();
                }
            }
            this.currentMusicList.addMusic(next);
        }
        this.playingOffset = this.isPaused ? offset : System.currentTimeMillis() + offset;
    }

    public void updatePlaying(Inventory inventory, int inventorySlot, boolean isClient) {
        InventoryItem item = inventory.getItem(inventorySlot);
        if (item != null && item.item instanceof VinylItem) {
            VinylItem vinylItem = (VinylItem)item.item;
            if (this.currentVinyls[inventorySlot] != vinylItem) {
                this.currentVinyls[inventorySlot] = vinylItem;
                this.updateHasAnyVinyls(isClient);
            }
        } else if (this.currentVinyls[inventorySlot] != null) {
            this.currentVinyls[inventorySlot] = null;
            this.updateHasAnyVinyls(isClient);
        }
    }

    public void updatePlayingAll(Inventory inventory, boolean isClient) {
        boolean updated = false;
        for (int slot = 0; slot < this.currentVinyls.length; ++slot) {
            InventoryItem item = inventory.getItem(slot);
            if (item != null && item.item instanceof VinylItem) {
                VinylItem vinylItem = (VinylItem)item.item;
                if (this.currentVinyls[slot] == vinylItem) continue;
                this.currentVinyls[slot] = vinylItem;
                updated = true;
                continue;
            }
            if (this.currentVinyls[slot] == null) continue;
            this.currentVinyls[slot] = null;
            updated = true;
        }
        if (updated) {
            this.updateHasAnyVinyls(isClient);
        }
    }

    protected static ItemMusicOptions toItemMusic(MusicOptions options) {
        if (options instanceof ItemMusicOptions) {
            return (ItemMusicOptions)options;
        }
        return null;
    }

    public boolean isSame(MusicPlayerManager other) {
        if (this.isPaused != other.isPaused) {
            return false;
        }
        if (this.hasAnyVinyls != other.hasAnyVinyls) {
            return false;
        }
        if (Math.abs(this.playingOffset - other.playingOffset) >= 500L) {
            return false;
        }
        return this.hasAnyVinyls && Arrays.equals(this.currentVinyls, other.currentVinyls);
    }

    public String getCurrentVinylsDebug() {
        return Arrays.toString(Arrays.stream(this.currentVinyls).map(c -> c == null ? null : c.getStringID()).toArray());
    }

    protected static class ItemMusicOptions
    extends MusicOptions {
        public final VinylItem vinylItem;
        public final int itemSlot;

        public ItemMusicOptions(VinylItem vinylItem, int itemSlot) {
            super(vinylItem.music);
            this.vinylItem = vinylItem;
            this.itemSlot = itemSlot;
        }
    }
}

