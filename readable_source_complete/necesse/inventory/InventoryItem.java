/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.GameState;
import necesse.engine.input.Input;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.ItemSave;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.window.WindowManager;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.TileEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.HumanAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.item.Item;
import necesse.inventory.item.TickItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class InventoryItem
implements Comparable<InventoryItem> {
    private static final int AMOUNT_OFFSET = (int)(GameRandom.globalRandom.nextGaussian() * 10000.0);
    public final Item item;
    private int amount;
    private boolean isLocked;
    private boolean isNew;
    private GNDItemMap gndData;

    public InventoryItem(Item item, int amount, boolean isLocked) {
        Objects.requireNonNull(item);
        this.item = item;
        this.setAmount(amount);
        this.setLocked(isLocked);
        this.gndData = new GNDItemMap();
    }

    public InventoryItem(Item item, int amount) {
        this(item, amount, false);
    }

    public InventoryItem(Item item) {
        this(item, 1);
    }

    public InventoryItem(String itemStringID, int amount) {
        this(ItemRegistry.getItem(itemStringID), amount);
    }

    public InventoryItem(String itemStringID) {
        this(ItemRegistry.getItem(itemStringID));
    }

    public InventoryItem copy(int newAmount, boolean isLocked) {
        InventoryItem copy = new InventoryItem(this.item, newAmount, isLocked);
        copy.isNew = this.isNew;
        copy.gndData = this.gndData.copy();
        return copy;
    }

    public InventoryItem copy(int newAmount) {
        return this.copy(newAmount, this.isLocked());
    }

    public InventoryItem copy() {
        return this.copy(this.getAmount());
    }

    public boolean canCombine(Level level, PlayerMob player, InventoryItem other, String purpose) {
        return this.item.canCombineItem(level, player, this, other, purpose);
    }

    @Deprecated
    public ItemCombineResult combine(Level level, PlayerMob player, InventoryItem other, String purpose) {
        return this.combine(level, player, null, -1, other, purpose, null);
    }

    public ItemCombineResult combine(Level level, PlayerMob player, Inventory myInventory, int mySlot, InventoryItem other, String purpose, InventoryAddConsumer addConsumer) {
        return this.combine(level, player, myInventory, mySlot, other, other.getAmount(), false, purpose, addConsumer);
    }

    @Deprecated
    public ItemCombineResult combine(Level level, PlayerMob player, InventoryItem other, int amount, boolean combineIsNew, String purpose) {
        return this.combine(level, player, null, -1, other, amount, combineIsNew, purpose, null);
    }

    public ItemCombineResult combine(Level level, PlayerMob player, Inventory myInventory, int mySlot, InventoryItem other, int amount, boolean combineIsNew, String purpose, InventoryAddConsumer addConsumer) {
        if (this.canCombine(level, player, other, purpose)) {
            amount = Math.min(other.getAmount(), amount);
            if (amount <= 0) {
                return ItemCombineResult.failure();
            }
            boolean result = this.item.onCombine(level, player, myInventory, mySlot, this, other, this.item.getStackSize(), amount, combineIsNew, purpose, addConsumer);
            if (result) {
                return ItemCombineResult.success();
            }
            return ItemCombineResult.failure();
        }
        return ItemCombineResult.failure();
    }

    public float getBrokerValue() {
        return this.item.getBrokerValue(this) * (float)this.getAmount();
    }

    public int itemStackSize() {
        return this.item.getStackSize();
    }

    public int getAmount() {
        return this.amount - AMOUNT_OFFSET;
    }

    public void setAmount(int amount) {
        this.amount = amount + AMOUNT_OFFSET;
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public boolean isNew() {
        return this.isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public void drawIcon(PlayerMob perspective, int x, int y, int size, Color color) {
        this.item.drawIcon(this, perspective, x, y, size, color);
    }

    @Deprecated
    public void drawIcon(PlayerMob perspective, int x, int y, int size) {
        this.drawIcon(perspective, x, y, size, null);
    }

    public static Color getSpoilTimeColor(int spoilsInSeconds, float brightness) {
        int lowestValue = 120;
        int highestValue = 3600;
        int whiteTransitionTime = 900;
        if (spoilsInSeconds <= highestValue) {
            float spoilPercent = 1.0f - GameMath.limit((float)(spoilsInSeconds - lowestValue) / (float)(highestValue - lowestValue), 0.0f, 1.0f);
            return Color.getHSBColor(GameMath.lerp(spoilPercent, 60.0f, 0.0f) / 360.0f, 0.85f, brightness);
        }
        if (spoilsInSeconds <= highestValue + whiteTransitionTime) {
            float whiteTransition = 1.0f - GameMath.limit((float)(spoilsInSeconds - highestValue) / (float)whiteTransitionTime, 0.0f, 1.0f);
            return Color.getHSBColor(0.16666667f, GameMath.lerp(whiteTransition, 0.0f, 0.85f), brightness);
        }
        return null;
    }

    public void draw(PlayerMob perspective, int x, int y, boolean minimize, boolean inInventory) {
        int amount;
        WorldSettings worldSettings;
        this.item.draw(this, perspective, x, y, inInventory);
        Color color = null;
        WorldSettings worldSettings2 = worldSettings = perspective == null ? null : perspective.getWorldSettings();
        if ((worldSettings == null || worldSettings.survivalMode) && this.item.shouldSpoilTick(this)) {
            long spoilTime = this.item.getCurrentSpoilTime(this);
            if (perspective != null && spoilTime > 0L) {
                long spoilsInMillis = Math.max(spoilTime - perspective.getWorldTime(), 0L);
                int spoilsInSeconds = (int)(spoilsInMillis / 1000L);
                color = InventoryItem.getSpoilTimeColor(spoilsInSeconds, 0.85f);
            }
        }
        if ((amount = this.getAmount()) > 1 || color != null) {
            String amountString = minimize && amount > 9999 ? GameUtils.metricNumber(amount, 2, true, RoundingMode.FLOOR, null) : "" + amount;
            FontOptions options = Item.tipFontOptions;
            if (color != null) {
                options = new FontOptions(options).color(color);
            }
            int width = FontManager.bit.getWidthCeil(amountString, options);
            FontManager.bit.drawString(x + 32 - width, y, amountString, options);
        }
    }

    public void draw(PlayerMob player, int x, int y, boolean inInventory) {
        this.draw(player, x, y, true, inInventory);
    }

    public void draw(PlayerMob player, int x, int y) {
        this.draw(player, x, y, true);
    }

    public DrawOptions getWorldDrawOptions(PlayerMob player, int x, int y, GameLight light, float sinking) {
        return this.item.getWorldDrawOptions(this, player, x, y, light, sinking);
    }

    public DrawOptions getWorldDrawOptions(PlayerMob player, int x, int y, GameLight light, float sinking, int size) {
        return this.item.getWorldDrawOptions(this, player, x, y, light, sinking, size);
    }

    public HumanAttackDrawOptions getAttackDrawOptions(Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int mobDir, float attackDirX, float attackDirY, GameSprite armSprite, float attackProgress) {
        return this.item.getAttackDrawOptions(this, level, player, headItem, chestItem, feetItem, mobDir, attackDirX, attackDirY, attackProgress, armSprite);
    }

    public Color getDrawColor(PlayerMob player) {
        return this.item.getDrawColor(this, player);
    }

    public ListGameTooltips getTooltip(boolean minimized, PlayerMob perspective, GameBlackboard blackboard) {
        WorldSettings worldSettings;
        ListGameTooltips tooltips = this.item.getTooltips(this, perspective, blackboard);
        if (minimized && this.getAmount() > 9999) {
            tooltips.add(Localization.translate("itemtooltip", "itemcount", "amount", (Object)this.getAmount()));
        }
        WorldSettings worldSettings2 = worldSettings = perspective == null ? null : perspective.getWorldSettings();
        if ((worldSettings == null || worldSettings.survivalMode) && this.item.shouldSpoilTick(this)) {
            long spoilTime = this.item.getCurrentSpoilTime(this);
            if (spoilTime > 0L) {
                if (perspective != null) {
                    long spoilsInMillis = Math.max(spoilTime - perspective.getWorldTime(), 0L);
                    int spoilsInSeconds = (int)(spoilsInMillis / 1000L);
                    Color color = InventoryItem.getSpoilTimeColor(spoilsInSeconds, 0.8f);
                    Input input = WindowManager.getWindow().getInput();
                    if (input.isKeyDown(340) || input.isKeyDown(344)) {
                        if (color != null) {
                            tooltips.add(new StringTooltips(ConsumableItem.getSpoilsTimeWithRateMessage(spoilsInSeconds, this.item.getCurrentSpoilRateModifier(this)).translate(), color));
                        } else {
                            tooltips.add(new StringTooltips(ConsumableItem.getSpoilsTimeWithRateMessage(spoilsInSeconds, this.item.getCurrentSpoilRateModifier(this)).translate()));
                        }
                    } else if (color != null) {
                        tooltips.add(new StringTooltips(ConsumableItem.getSpoilsTimeMessage(spoilsInSeconds).translate(), color));
                    } else {
                        tooltips.add(new StringTooltips(ConsumableItem.getSpoilsTimeMessage(spoilsInSeconds).translate()));
                    }
                }
            } else if (spoilTime == 0L) {
                int spoilDurationSeconds = (int)((float)this.item.getStartSpoilSeconds(this) * Item.GLOBAL_SPOIL_TIME_MODIFIER);
                Color color = InventoryItem.getSpoilTimeColor(spoilDurationSeconds, 0.8f);
                if (color != null) {
                    tooltips.add(new StringTooltips(ConsumableItem.getSpoilsTimeMessage(spoilDurationSeconds).translate(), color));
                } else {
                    tooltips.add(new StringTooltips(ConsumableItem.getSpoilsTimeMessage(spoilDurationSeconds).translate()));
                }
            } else if (perspective != null) {
                long spoilsInMillis = Math.max(-spoilTime, 0L);
                int spoilsInSeconds = (int)(spoilsInMillis / 1000L);
                tooltips.add(new StringTooltips(ConsumableItem.getSpoilStoppedTimeMessage(spoilsInSeconds).translate()));
            }
        }
        return tooltips;
    }

    public ListGameTooltips getTooltip(PlayerMob perspective, GameBlackboard blackboard) {
        return this.getTooltip(true, perspective, blackboard);
    }

    public GameMessage getItemLocalization() {
        return this.item.getLocalization(this);
    }

    public String getItemDisplayName() {
        return this.item.getDisplayName(this);
    }

    public GNDItemMap getGndData() {
        return this.gndData;
    }

    public void setGndData(GNDItemMap data) {
        this.gndData = data != null ? data.copy() : new GNDItemMap();
    }

    public ItemPickupEntity getPickupEntity(Level level, float x, float y, float dx, float dy) {
        return this.item.getPickupEntity(level, this, x, y, dx, dy);
    }

    public ItemPickupEntity getPickupEntity(Level level, float x, float y) {
        Point2D.Float dir = GameMath.getAngleDir(GameRandom.globalRandom.nextInt(360));
        float speed = GameRandom.globalRandom.getFloatBetween(50.0f, 65.0f);
        return this.getPickupEntity(level, x, y, dir.x * speed, dir.y * speed);
    }

    public boolean combineOrAddToList(Level level, PlayerMob player, Collection<InventoryItem> items, String purpose) {
        for (InventoryItem other : items) {
            boolean success;
            if (other.canCombine(level, player, this, purpose) && (success = other.item.onCombine(level, player, null, -1, other, this, Integer.MAX_VALUE, this.getAmount(), false, purpose, null)) && this.getAmount() <= 0) {
                return true;
            }
            if (this.getAmount() > 0) continue;
            break;
        }
        if (this.getAmount() >= 0) {
            items.add(this);
        }
        return false;
    }

    public static void tickList(GameClock clock, GameState state, Entity entity, TileEntity tileEntity, WorldSettings worldSettings, float spoilModifier, List<InventoryItem> items) {
        ListIterator<InventoryItem> li = items.listIterator();
        while (li.hasNext()) {
            AtomicReference<InventoryItem> itemRef = new AtomicReference<InventoryItem>(li.next());
            if (itemRef.get().item.isTickItem()) {
                ((TickItem)((Object)itemRef.get().item)).tick(null, -1, itemRef.get(), clock, state, entity, tileEntity, worldSettings, newItem -> {
                    if (newItem == null) {
                        li.remove();
                    } else {
                        li.set((InventoryItem)newItem);
                    }
                    itemRef.set((InventoryItem)newItem);
                });
            }
            if (worldSettings != null && !worldSettings.survivalMode || itemRef.get() == null || !itemRef.get().item.shouldSpoilTick(itemRef.get())) continue;
            itemRef.get().item.tickSpoilTime(itemRef.get(), clock, spoilModifier, newItem -> {
                if (newItem == null) {
                    li.remove();
                } else {
                    li.set((InventoryItem)newItem);
                }
                itemRef.set((InventoryItem)newItem);
            });
        }
    }

    public boolean equals(Level level, InventoryItem other, boolean ignoreMeta, boolean ignoreGNDData, String purpose) {
        if (this == other) {
            return true;
        }
        if (!this.item.isSameItem(level, this, other, purpose)) {
            return false;
        }
        if (!ignoreMeta) {
            if (this.getAmount() != other.getAmount()) {
                return false;
            }
            if (this.isLocked != other.isLocked) {
                return false;
            }
            if (this.isNew != other.isNew) {
                return false;
            }
        }
        if (!ignoreGNDData) {
            return this.item.isSameGNDData(level, this, other, purpose);
        }
        return true;
    }

    public boolean equals(Level level, InventoryItem other, String purpose) {
        return this.equals(level, other, false, false, purpose);
    }

    public boolean equals(Object obj) {
        if (obj instanceof InventoryItem) {
            return this.equals(null, (InventoryItem)obj, "equals");
        }
        return super.equals(obj);
    }

    public void addPacketContent(PacketWriter writer) {
        InventoryItem.addPacketContent(this, writer);
    }

    public static InventoryItem fromContentPacket(Packet packet) {
        return InventoryItem.fromContentPacket(new PacketReader(packet));
    }

    public static InventoryItem fromContentPacket(PacketReader reader) {
        short id = reader.getNextShort();
        if (id == -1) {
            return null;
        }
        int amount = reader.getNextInt();
        boolean isLocked = reader.getNextBoolean();
        Packet gndContent = reader.getNextContentPacket();
        Item item = ItemRegistry.getItem(id & 0xFFFF);
        if (item == null) {
            new Throwable("Could not find item with ID " + id).printStackTrace(System.err);
            return null;
        }
        InventoryItem out = new InventoryItem(item, amount);
        out.isLocked = isLocked;
        out.gndData = new GNDItemMap(gndContent);
        return out;
    }

    public static Packet getContentPacket(InventoryItem item) {
        Packet out = new Packet();
        PacketWriter writer = new PacketWriter(out);
        InventoryItem.addPacketContent(item, writer);
        return out;
    }

    public static void addPacketContent(InventoryItem item, PacketWriter writer) {
        if (item == null || item.item == null) {
            writer.putNextShort((short)-1);
        } else {
            writer.putNextShort((short)item.item.getID());
            writer.putNextInt(item.getAmount());
            writer.putNextBoolean(item.isLocked());
            writer.putNextContentPacket(item.gndData.getContentPacket());
        }
    }

    public void addSaveData(SaveData save) {
        save.addUnsafeString("stringID", this.item.getStringID());
        save.addInt("amount", this.getAmount());
        if (this.gndData.getMapSize() > 0) {
            SaveData gnd = new SaveData("GNDData");
            this.gndData.addSaveData(gnd);
            save.addSaveData(gnd);
        }
    }

    public SaveData getSaveData(String name) {
        SaveData save = new SaveData(name);
        this.addSaveData(save);
        return save;
    }

    public static InventoryItem fromLoadData(LoadData save) {
        if (save == null) {
            return null;
        }
        Item item = ItemSave.loadItem(save.getUnsafeString("stringID", null));
        if (item == null) {
            return null;
        }
        int amount = save.getInt("amount", 0);
        if (amount == 0) {
            return null;
        }
        InventoryItem out = new InventoryItem(item, amount);
        LoadData gnd = save.getFirstLoadDataByName("GNDData");
        if (gnd != null) {
            out.gndData = new GNDItemMap(gnd);
        }
        return out;
    }

    @Override
    public int compareTo(InventoryItem o) {
        int itemCompare = this.item.compareTo(this, o);
        return itemCompare == 0 ? Integer.compare(o.amount, this.amount) : itemCompare;
    }

    public String toString() {
        return super.toString() + "{" + this.getItemDisplayName() + ", " + this.getAmount() + (this.gndData.getMapSize() > 0 ? ", " + this.gndData.toString() : "") + "}";
    }
}

