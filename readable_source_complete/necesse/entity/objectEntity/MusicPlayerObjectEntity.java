/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.MusicOptions;
import necesse.engine.MusicOptionsOffset;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.GameMusic;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.entity.objectEntity.MusicPlayerManager;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.miscItem.VinylItem;
import necesse.level.maps.Level;

public class MusicPlayerObjectEntity
extends InventoryObjectEntity {
    public static int LISTEN_DISTANCE = 1600;
    protected MusicPlayerManager manager;

    public MusicPlayerObjectEntity(Level level, int x, int y) {
        super(level, x, y, 10);
        this.manager = new MusicPlayerManager(this.slots){

            @Override
            public void markDirty() {
                MusicPlayerObjectEntity.this.markDirty();
            }
        };
    }

    @Override
    public boolean isItemValid(int slot, InventoryItem item) {
        if (item != null) {
            return item.item instanceof VinylItem;
        }
        return true;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        this.manager.addSaveData(save);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.manager.applyLoadData(save);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        this.manager.writeContentPacket(writer, false);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.manager.readContentPacket(reader, false);
    }

    @Override
    public InventoryRange getSettlementStorage() {
        return null;
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
    public boolean canUseForNearbyCrafting() {
        return false;
    }

    @Override
    public boolean canSetInventoryName() {
        return false;
    }

    @Override
    public void clientTick() {
        float dist;
        ClientClient client;
        super.clientTick();
        MusicList currentMusicList = this.manager.getCurrentMusicList();
        if (currentMusicList != null && !this.isPaused() && this.isClient() && (client = this.getLevel().getClient().getClient()) != null && client.hasSpawned() && (dist = client.playerMob.getDistance(this.tileX * 32 + 16, this.tileY * 32 + 16)) <= (float)LISTEN_DISTANCE) {
            SoundManager.setMusic((AbstractMusicList)currentMusicList, SoundManager.MusicPriority.MUSIC_PLAYER.thenBy(this.tileX * this.tileY).thenBy(this.tileX));
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.isServer() && this.getLevel().tickManager().getTick() == 1 && this.manager.hasAnyVinyls() && !this.isPaused()) {
            Point centerPos = new Point(this.tileX * 32 + 16, this.tileY * 32 + 16);
            this.getLevel().entityManager.players.streamArea(centerPos.x, centerPos.y, LISTEN_DISTANCE).filter(p -> p.getDistance(centerPos.x, centerPos.y) <= (float)LISTEN_DISTANCE).filter(PlayerMob::isServerClient).map(PlayerMob::getServerClient).forEach(c -> {
                if (c.achievementsLoaded()) {
                    c.achievements().MY_JAM.markCompleted((ServerClient)c);
                }
            });
        }
    }

    @Override
    protected void onInventorySlotUpdated(int slot) {
        this.manager.updatePlaying(this.inventory, slot, this.isClient());
    }

    public void onWireUpdated() {
        this.manager.setIsPaused(this.getLevel().wireManager.isWireActiveAny(this.tileX, this.tileY));
    }

    public MusicPlayerManager getMusicManager() {
        return this.manager;
    }

    public MusicOptionsOffset getCurrentMusic() {
        return this.manager.getCurrentMusic();
    }

    public MusicOptions getPreviousMusic() {
        return this.manager.getPreviousMusic();
    }

    public boolean isPaused() {
        return this.manager.isPaused();
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        MusicOptionsOffset currentMusic = this.getCurrentMusic();
        if (currentMusic != null) {
            GameMusic music = currentMusic.options.music;
            StringTooltips tooltips = new StringTooltips(Localization.translate("ui", this.manager.isPaused ? "musicpaused" : "musicplaying", "name", music.trackName.translate()));
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
        }
        if (debug) {
            long offset = this.manager.getMusicPlayingOffset();
            StringTooltips tooltips = new StringTooltips("Offset: " + (offset < 0L ? "-" : "") + GameUtils.getTimeStringMillis(Math.abs(offset)));
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
        }
    }
}

