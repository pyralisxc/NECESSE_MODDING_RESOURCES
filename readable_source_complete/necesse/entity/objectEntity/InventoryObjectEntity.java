/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.util.ArrayList;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.FairTypeTooltip;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryFilter;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class InventoryObjectEntity
extends ObjectEntity
implements OEInventory,
OEUsers {
    private String name;
    private FairTypeDrawOptions textDrawOptions;
    private int textDrawFontSize;
    public final Inventory inventory;
    public final int slots;
    private boolean interactedWith;
    public final OEUsers.Users users = this.constructUsersObject(2000L);

    public InventoryObjectEntity(final Level level, int x, int y, int slots) {
        super(level, "inventory", x, y);
        this.slots = slots;
        this.name = "";
        this.setInventoryName("");
        this.inventory = new Inventory(slots){

            @Override
            public void updateSlot(int slot) {
                super.updateSlot(slot);
                InventoryObjectEntity.this.onInventorySlotUpdated(slot);
                if (level.isLoadingComplete()) {
                    InventoryObjectEntity.this.triggerInteracted();
                }
            }
        };
        this.inventory.filter = new InventoryFilter(){

            @Override
            public boolean isItemValid(int slot, InventoryItem item) {
                return InventoryObjectEntity.this.isItemValid(slot, item);
            }

            @Override
            public int getItemStackLimit(int slot, InventoryItem item) {
                return InventoryObjectEntity.this.getItemStackLimit(slot, item);
            }
        };
        if (level != null && !level.isLoadingComplete()) {
            this.interactedWith = false;
            this.inventory.spoilRateModifier = -1415.0f;
        } else {
            this.interactedWith = true;
        }
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSafeString("name", this.name);
        save.addSaveData(InventorySave.getSave(this.inventory));
        if (this.interactedWith) {
            save.addBoolean("interactedWith", this.interactedWith);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.setInventoryName(save.getSafeString("name", this.name));
        this.inventory.override(InventorySave.loadSave(save.getFirstLoadDataByName("INVENTORY")));
        boolean loadedInteractedWith = save.getBoolean("interactedWith", this.interactedWith, false);
        if (loadedInteractedWith != this.interactedWith) {
            this.interactedWith = loadedInteractedWith;
            if (this.interactedWith && this.inventory.spoilRateModifier == -1415.0f) {
                this.inventory.spoilRateModifier = 1.0f;
            } else if (!this.interactedWith) {
                this.inventory.spoilRateModifier = 0.0f;
            }
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        this.users.writeUsersSpawnPacket(writer);
        this.inventory.writeContent(writer);
        writer.putNextString(this.name);
        writer.putNextBoolean(this.interactedWith);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.users.readUsersSpawnPacket(reader, this);
        this.inventory.override(Inventory.getInventory(reader));
        this.setInventoryName(reader.getNextString());
        boolean loadedInteractedWith = reader.getNextBoolean();
        if (loadedInteractedWith != this.interactedWith) {
            this.interactedWith = loadedInteractedWith;
            if (this.interactedWith && this.inventory.spoilRateModifier == -1415.0f) {
                this.inventory.spoilRateModifier = 1.0f;
            } else if (!this.interactedWith) {
                this.inventory.spoilRateModifier = 0.0f;
            }
        }
    }

    @Override
    public ArrayList<InventoryItem> getDroppedItems() {
        ArrayList<InventoryItem> list = new ArrayList<InventoryItem>();
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.isSlotClear(i)) continue;
            list.add(this.inventory.getItem(i));
        }
        return list;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "tickItems", () -> this.inventory.tickItems(this));
        this.serverTickInventorySync(this.getLevel().getServer(), this);
        this.users.serverTick(this);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "tickItems", () -> this.inventory.tickItems(this));
        this.users.clientTick(this);
    }

    protected void onInventorySlotUpdated(int slot) {
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        if (!this.name.isEmpty()) {
            GameTooltipManager.addTooltip(new FairTypeTooltip(this.getTextDrawOptions()), TooltipLocation.INTERACT_FOCUS);
        }
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public boolean isItemValid(int slot, InventoryItem item) {
        return true;
    }

    public int getItemStackLimit(int slot, InventoryItem item) {
        if (item == null) {
            return Integer.MAX_VALUE;
        }
        return item.itemStackSize();
    }

    @Override
    public void triggerInteracted() {
        if (!this.interactedWith) {
            if (this.inventory.spoilRateModifier == -1415.0f) {
                this.inventory.spoilRateModifier = 1.0f;
            }
            this.interactedWith = true;
            this.markDirty();
        }
    }

    @Override
    public GameMessage getInventoryName() {
        if (this.name.isEmpty()) {
            return this.getLevel().getObjectName(this.tileX, this.tileY);
        }
        return new StaticMessage(this.name);
    }

    @Override
    public void setInventoryName(String name) {
        String oldName = this.name;
        this.name = this.getLevel().getObjectName(this.tileX, this.tileY).translate().equals(name) ? "" : name;
        if (!this.name.equals(oldName)) {
            this.textDrawOptions = null;
        }
    }

    private FairTypeDrawOptions getTextDrawOptions() {
        if (this.textDrawOptions == null || this.textDrawFontSize != Settings.tooltipTextSize) {
            FairType type = new FairType();
            FontOptions fontOptions = new FontOptions(Settings.tooltipTextSize).outline();
            type.append(fontOptions, this.getInventoryName().translate());
            type.applyParsers(OEInventoryContainerForm.getParsers(fontOptions));
            this.textDrawOptions = type.getDrawOptions(FairType.TextAlign.LEFT);
            this.textDrawFontSize = fontOptions.getSize();
        }
        return this.textDrawOptions;
    }

    @Override
    public boolean canSetInventoryName() {
        return true;
    }

    @Override
    public OEUsers.Users getUsersObject() {
        return this.users;
    }

    @Override
    public GameMessage getCanUseError(Mob mob) {
        return null;
    }

    @Override
    public void remove() {
        super.remove();
        this.users.onRemoved(this);
    }

    @Override
    public void onIsInUseChanged(boolean isInUse) {
    }

    @Override
    public GameTooltips getMapTooltips() {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(new FairTypeTooltip(this.getTextDrawOptions()));
        return tooltips;
    }
}

