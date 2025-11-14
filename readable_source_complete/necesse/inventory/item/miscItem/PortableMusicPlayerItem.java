/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.util.function.Consumer;
import necesse.engine.AbstractMusicList;
import necesse.engine.GameState;
import necesse.engine.GlobalData;
import necesse.engine.MusicList;
import necesse.engine.MusicOptionsOffset;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventory;
import necesse.engine.network.gameNetworkData.GNDMusicPlayerInventory;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.sound.GameMusic;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.TileEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.MusicPlayerManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;
import necesse.inventory.item.miscItem.VinylItem;

public class PortableMusicPlayerItem
extends PouchItem {
    public PortableMusicPlayerItem() {
        this.rarity = Item.Rarity.RARE;
        this.combinePurposes.clear();
        this.insertPurposes.clear();
        this.drawStoredItems = false;
    }

    public GNDMusicPlayerInventory getMusicPlayerGNDItem(InventoryItem item) {
        GNDItem gndItem = item.getGndData().getItem("inventory");
        if (gndItem instanceof GNDItemInventory) {
            GNDItemInventory gndInventory = (GNDItemInventory)gndItem;
            if (gndInventory.inventory.getSize() != this.getInternalInventorySize()) {
                gndInventory.inventory.changeSize(this.getInternalInventorySize());
            }
            GNDMusicPlayerInventory out = new GNDMusicPlayerInventory(gndInventory.inventory);
            item.getGndData().setItem("inventory", (GNDItem)out);
            return out;
        }
        if (gndItem instanceof GNDMusicPlayerInventory) {
            GNDMusicPlayerInventory gndInventory = (GNDMusicPlayerInventory)gndItem;
            if (gndInventory.inventory.getSize() != this.getInternalInventorySize()) {
                gndInventory.inventory.changeSize(this.getInternalInventorySize());
                gndInventory.fixSlots();
            }
            return (GNDMusicPlayerInventory)gndItem;
        }
        Inventory inventory = this.getNewInternalInventory(item);
        GNDMusicPlayerInventory out = new GNDMusicPlayerInventory(inventory);
        item.getGndData().setItem("inventory", (GNDItem)out);
        return out;
    }

    public MusicPlayerManager getMusicManager(InventoryItem item) {
        return this.getMusicPlayerGNDItem((InventoryItem)item).manager;
    }

    @Override
    public Inventory getInternalInventory(InventoryItem item) {
        return this.getMusicPlayerGNDItem((InventoryItem)item).inventory;
    }

    @Override
    public void saveInternalInventory(InventoryItem item, Inventory inventory) {
        GNDMusicPlayerInventory musicPlayer = this.getMusicPlayerGNDItem(item);
        boolean save = musicPlayer.isDefault();
        musicPlayer.inventory.override(inventory, true, true);
        musicPlayer.fixSlots();
        musicPlayer.updatePlayingAll();
        if (save) {
            item.getGndData().setItem("inventory", (GNDItem)musicPlayer);
        }
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        GNDMusicPlayerInventory musicPlayer = this.getMusicPlayerGNDItem(item);
        MusicOptionsOffset currentMusic = musicPlayer.getCurrentMusic();
        if (currentMusic != null) {
            GameMusic music = currentMusic.options.music;
            tooltips.add(Localization.translate("ui", musicPlayer.isPaused() ? "musicpaused" : "musicplaying", "name", music.trackName.translate()));
        } else {
            tooltips.add(Localization.translate("itemtooltip", "musicplayertip"));
        }
        if (GlobalData.debugCheatActive()) {
            long offset = musicPlayer.getMusicPlayingOffset();
            tooltips.add("Offset: " + (offset < 0L ? "-" : "") + GameUtils.getTimeStringMillis(Math.abs(offset)));
        }
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        return tooltips;
    }

    @Override
    protected void openContainer(ServerClient client, PlayerInventorySlot inventorySlot) {
        PacketOpenContainer p = new PacketOpenContainer(ContainerRegistry.ITEM_MUSIC_PLAYER_CONTAINER, ItemInventoryContainer.getContainerContent(this, inventorySlot));
        ContainerRegistry.openAndSendContainer(client, p);
    }

    @Override
    public void tick(Inventory inventory, int slot, InventoryItem item, GameClock clock, GameState state, Entity entity, TileEntity tileEntity, WorldSettings worldSettings, Consumer<InventoryItem> setItem) {
        super.tick(inventory, slot, item, clock, state, entity, tileEntity, worldSettings, setItem);
        GNDMusicPlayerInventory gndItem = this.getMusicPlayerGNDItem(item);
        if (entity instanceof PlayerMob) {
            ServerClient client;
            if (gndItem.isMusicManagerDirty) {
                inventory.markDirty(slot);
                gndItem.isMusicManagerDirty = false;
            }
            if (entity.isClient()) {
                MusicList currentMusicList;
                gndItem.updateClientPlaying();
                if (entity.getClient().getPlayer() == entity && (currentMusicList = gndItem.getCurrentMusicList()) != null && !gndItem.isPaused()) {
                    SoundManager.setMusic((AbstractMusicList)currentMusicList, SoundManager.MusicPriority.PORTABLE_MUSIC_PLAYER.thenBy(slot));
                }
            } else if (entity.isServer() && gndItem.hasAnyVinyls() && !gndItem.isPaused() && (client = ((PlayerMob)entity).getServerClient()).achievementsLoaded()) {
                client.achievements().MY_JAM.markCompleted(client);
            }
        }
    }

    @Override
    public boolean isValidPouchItem(InventoryItem item) {
        return this.isValidRequestItem(item.item);
    }

    @Override
    public boolean isValidRequestItem(Item item) {
        return item instanceof VinylItem;
    }

    @Override
    public boolean isValidRequestType(Item.Type type) {
        return false;
    }

    @Override
    public int getInternalInventorySize() {
        return 10;
    }

    @Override
    public boolean canDisablePickup() {
        return false;
    }

    @Override
    public boolean canQuickStackInventory() {
        return false;
    }

    @Override
    public boolean canRestockInventory() {
        return false;
    }

    @Override
    public boolean canSortInventory() {
        return false;
    }

    @Override
    public boolean canChangePouchName() {
        return false;
    }
}

