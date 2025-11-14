/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GameState
 *  necesse.engine.localization.Localization
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.network.packet.PacketOpenContainer
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.registries.ContainerRegistry
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.world.GameClock
 *  necesse.engine.world.WorldSettings
 *  necesse.entity.Entity
 *  necesse.entity.TileEntity
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTooltips.GameTooltips
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.Inventory
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.InventorySlot
 *  necesse.inventory.PlayerInventorySlot
 *  necesse.inventory.container.Container
 *  necesse.inventory.container.ContainerActionResult
 *  necesse.inventory.container.item.ItemInventoryContainer
 *  necesse.inventory.container.slots.ContainerSlot
 *  necesse.inventory.item.Item
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.ItemCategory
 *  necesse.inventory.item.TickItem
 *  necesse.inventory.item.miscItem.InternalInventoryItemInterface
 *  necesse.inventory.item.trinketItem.TrinketItem
 *  necesse.inventory.lootTable.presets.TrinketsLootTable
 *  necesse.level.maps.Level
 */
package aphorea.items.runes;

import aphorea.buffs.Runes.AphModifierRuneTrinketBuff;
import aphorea.items.runes.AphBaseRune;
import aphorea.items.runes.AphModifierRune;
import aphorea.registry.AphContainers;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.engine.GameState;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.TileEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.TickItem;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.lootTable.presets.TrinketsLootTable;
import necesse.level.maps.Level;

public class AphRunesInjector
extends TrinketItem
implements InternalInventoryItemInterface,
TickItem {
    protected GameTexture validTexture;
    public int modifierRunesNumber;
    public int baseRunesNumber;
    public int tooltipsNumber;

    public AphRunesInjector(Item.Rarity rarity, int extraToolTips, int modifierRunesNumber) {
        super(rarity, 400, TrinketsLootTable.trinkets);
        this.setItemCategory(ItemCategory.craftingManager, new String[]{"runes", "runesinjectors"});
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 60000;
        this.tooltipsNumber = extraToolTips;
        this.modifierRunesNumber = modifierRunesNumber;
        this.baseRunesNumber = 0;
        ItemRegistry.getItems().forEach(i -> {
            if (i instanceof AphRunesInjector) {
                this.disables.add(i.getStringID());
            }
        });
    }

    public AphRunesInjector(Item.Rarity rarity, int extraToolTips, int modifierRunesNumber, int baseRunesNumber) {
        this(rarity, modifierRunesNumber, extraToolTips);
        this.baseRunesNumber = baseRunesNumber;
    }

    public List<AphBaseRune> getBaseRunes(InventoryItem item) {
        return this.getInternalInventory(item).streamSlots().filter(s -> s.getItem() != null && s.getItem().item instanceof AphBaseRune).map(s -> (AphBaseRune)s.getItem().item).collect(Collectors.toList());
    }

    public InventoryItem getBaseRune(InventoryItem item) {
        return this.getInternalInventory(item).streamSlots().filter(s -> s.getItem() != null && s.getItem().item instanceof AphBaseRune).map(InventorySlot::getItem).findFirst().orElse(null);
    }

    public List<AphModifierRune> getModifierRunes(InventoryItem item) {
        return this.getInternalInventory(item).streamSlots().filter(s -> s.getItem() != null && s.getItem().item instanceof AphModifierRune).map(s -> (AphModifierRune)s.getItem().item).collect(Collectors.toList());
    }

    public List<AphModifierRuneTrinketBuff> getModifierBuffs(InventoryItem item) {
        List<AphModifierRuneTrinketBuff> modifierBuffs = this.getInternalInventory(item).streamSlots().filter(s -> s.getItem() != null && s.getItem().item instanceof AphModifierRune).map(s -> ((AphModifierRune)s.getItem().item).getBuff()).collect(Collectors.toList());
        modifierBuffs.add(this.getBuff());
        return modifierBuffs;
    }

    public void tick(Inventory inventory, int slot, InventoryItem item, GameClock clock, GameState state, Entity entity, TileEntity tileEntity, WorldSettings worldSettings, Consumer<InventoryItem> consumer) {
        this.tickInternalInventory(item, clock, state, entity, tileEntity, worldSettings);
    }

    public boolean isValidPouchItem(InventoryItem item, InventoryItem runesInjector) {
        if (item == null || item.item == null) {
            return false;
        }
        return item.item instanceof AphBaseRune || item.item instanceof AphModifierRune;
    }

    public boolean isValidAddItem(InventoryItem item, InventoryItem runesInjector) {
        return this.isValidPouchItem(item, runesInjector);
    }

    public boolean isValidPouchItem(InventoryItem item) {
        if (item == null || item.item == null) {
            return false;
        }
        return item.item instanceof AphBaseRune || item.item instanceof AphModifierRune;
    }

    public boolean isValidAddItem(InventoryItem item) {
        return this.isValidPouchItem(item);
    }

    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        if (this.validTexture != null && this.isInvalidInjector(item, perspective) == null) {
            return new GameSprite(this.validTexture);
        }
        return super.getItemSprite(item, perspective);
    }

    protected void loadItemTextures() {
        this.itemTexture = GameTexture.fromFile((String)("items/runes/" + this.getStringID()));
        this.validTexture = GameTexture.fromFile((String)("items/runes/" + this.getStringID() + "_valid"));
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotation(attackProgress);
    }

    public GameMessage getLocalization(InventoryItem item) {
        String pouchName = this.getPouchName(item);
        return pouchName != null ? new StaticMessage(pouchName) : super.getLocalization(item);
    }

    public float getBrokerValue(InventoryItem item) {
        float value = super.getBrokerValue(item);
        Inventory internalInventory = this.getInternalInventory(item);
        for (int i = 0; i < internalInventory.getSize(); ++i) {
            if (internalInventory.isSlotClear(i)) continue;
            value += internalInventory.getItem(i).getBrokerValue();
        }
        return value;
    }

    public String isInvalidInjector(InventoryItem inventoryItem, PlayerMob player) {
        String error = this.isInvalidInjector(inventoryItem);
        if (error != null) {
            return error;
        }
        InventoryItem baseRune = this.getBaseRune(inventoryItem);
        if (baseRune == null) {
            return "requiresbaserune";
        }
        String runeOwner = baseRune.getGndData().getString("runeOwner", null);
        if (runeOwner != null && !Objects.equals(player.playerName, runeOwner)) {
            return "notruneowner";
        }
        return null;
    }

    public String isInvalidInjector(InventoryItem inventoryItem) {
        int baseRunes = this.getBaseRunes(inventoryItem).size();
        if (baseRunes == 0) {
            return "requiresbaserune";
        }
        if (baseRunes > 1) {
            return "onlyonebaserune";
        }
        if (this.hasDuplicateModifierRunes(inventoryItem)) {
            return "duplicatedmodifierrunes";
        }
        return null;
    }

    public boolean hasDuplicateModifierRunes(InventoryItem item) {
        return this.getModifierRunes(item).stream().map(Item::getStringID).collect(Collectors.groupingBy(stringID -> stringID, Collectors.counting())).values().stream().anyMatch(count -> count > 1L);
    }

    public TrinketBuff[] getBuffs(InventoryItem inventoryItem) {
        if (this.isInvalidInjector(inventoryItem) == null) {
            List<AphModifierRuneTrinketBuff> modifierBuffs = this.getModifierBuffs(inventoryItem);
            TrinketBuff[] buffs = new TrinketBuff[modifierBuffs.size() + 1];
            buffs[0] = this.getBaseRunes(inventoryItem).get(0).getTrinketBuff();
            Iterator<AphModifierRuneTrinketBuff> iterator = modifierBuffs.iterator();
            int i = 1;
            while (iterator.hasNext()) {
                buffs[i] = iterator.next();
                ++i;
            }
            return buffs;
        }
        return new TrinketBuff[0];
    }

    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            PlayerInventorySlot playerSlot = null;
            if (slot.getInventory() == container.getClient().playerMob.getInv().main) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().main, slot.getInventorySlot());
            }
            if (slot.getInventory() == container.getClient().playerMob.getInv().cloud) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().cloud, slot.getInventorySlot());
            }
            if (playerSlot != null) {
                if (container.getClient().isServer()) {
                    ServerClient client = container.getClient().getServerClient();
                    this.openContainer(client, playerSlot);
                }
                return new ContainerActionResult(-1002911334);
            }
            return new ContainerActionResult(208675834, Localization.translate((String)"itemtooltip", (String)"rclickinvopenerror"));
        };
    }

    protected void openContainer(ServerClient client, PlayerInventorySlot inventorySlot) {
        PacketOpenContainer p = new PacketOpenContainer(AphContainers.RUNES_INJECTOR_CONTAINER, ItemInventoryContainer.getContainerContent((InternalInventoryItemInterface)this, (PlayerInventorySlot)inventorySlot));
        ContainerRegistry.openAndSendContainer((ServerClient)client, (PacketOpenContainer)p);
    }

    public int inventoryCanAddItem(Level level, PlayerMob player, InventoryItem item, InventoryItem input, String purpose, boolean isValid, int stackLimit) {
        if (this.isValidAddItem(input, item)) {
            Inventory internalInventory = this.getInternalInventory(item);
            return internalInventory.canAddItem(level, player, input, purpose);
        }
        return super.inventoryCanAddItem(level, player, item, input, purpose, isValid, stackLimit);
    }

    public int getInternalInventorySize() {
        return 1 + this.modifierRunesNumber;
    }

    public Inventory getInternalInventory(InventoryItem item) {
        return super.getInternalInventory(item);
    }

    public Inventory getNewInternalInventory(InventoryItem item) {
        return super.getNewInternalInventory(item);
    }

    public void saveInternalInventory(InventoryItem item, Inventory inventory) {
        super.saveInternalInventory(item, inventory);
    }

    public boolean isValidItem(InventoryItem item) {
        return item == null || this.isValidAddItem(item);
    }

    public GameTooltips getPickupToggleTooltip(boolean isDisabled) {
        return super.getPickupToggleTooltip(isDisabled);
    }

    public boolean canDisablePickup() {
        return super.canDisablePickup();
    }

    public boolean canQuickStackInventory() {
        return super.canQuickStackInventory();
    }

    public boolean canRestockInventory() {
        return super.canRestockInventory();
    }

    public boolean canSortInventory() {
        return super.canSortInventory();
    }

    public boolean canChangePouchName() {
        return super.canChangePouchName();
    }

    public String getPouchName(InventoryItem item) {
        return super.getPouchName(item);
    }

    public void setPouchName(InventoryItem item, String name) {
        super.setPouchName(item, name);
    }

    public void setPouchPickupDisabled(InventoryItem item, boolean disabled) {
        super.setPouchPickupDisabled(item, disabled);
    }

    public boolean isPickupDisabled(InventoryItem item) {
        return super.isPickupDisabled(item);
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"rclickinvopentip"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"runesslots", (String)"modifierslots", (Object)this.modifierRunesNumber));
        this.addToolTips(tooltips, item, perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"line"));
        String invalid = this.isInvalidInjector(item, perspective);
        if (invalid == null) {
            AphBaseRune baseRune = this.getBaseRunes(item).get(0);
            if (baseRune != null) {
                baseRune.addToolTips(tooltips, item, this, perspective, true);
                tooltips.add(Localization.translate((String)"itemtooltip", (String)"useruneinfusor"));
            } else {
                tooltips.add("Unknown error");
            }
        } else {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)invalid));
        }
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"line"));
        this.getBaseRunes(item).forEach(rune -> tooltips.add("\u00a7i[B]\u00a70 " + Localization.translate((String)"item", (String)rune.getStringID())));
        this.getModifierRunes(item).forEach(rune -> tooltips.add("\u00a7a[M]\u00a70 " + Localization.translate((String)"item", (String)rune.getStringID())));
        return tooltips;
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }

    public String getInventoryRightClickControlTip(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return null;
    }

    public String getTranslatedTypeName() {
        return Localization.translate((String)"item", (String)"runesinjector");
    }

    public AphModifierRuneTrinketBuff getBuff() {
        return (AphModifierRuneTrinketBuff)BuffRegistry.getBuff((String)this.getStringID());
    }

    public void addToolTips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective) {
        AphModifierRuneTrinketBuff buff = this.getBuff();
        float effectNumberVariation = buff.getEffectNumberVariation();
        float effectCooldownVariation = buff.getCooldownVariation();
        float healthCost = buff.getHealthCost();
        if (effectNumberVariation > 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"increaseruneeffectnumber", (String)"variation", (Object)Math.round(effectNumberVariation * 100.0f)));
        } else if (effectNumberVariation < 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"decreaseruneeffectnumber", (String)"variation", (Object)Math.round(-effectNumberVariation * 100.0f)));
        }
        if (effectCooldownVariation > 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"increaserunecooldown", (String)"variation", (Object)Math.round(effectCooldownVariation * 100.0f)));
        } else if (effectCooldownVariation < 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"decreaserunecooldown", (String)"variation", (Object)Math.round(-effectCooldownVariation * 100.0f)));
        }
        if (healthCost > 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"increaserunehealthcost", (String)"health", (Object)Math.round(healthCost * 100.0f)));
        } else if (healthCost < 0.0f) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"increaserunehealthhealing", (String)"health", (Object)Math.round(-healthCost * 100.0f)));
        }
        for (int i = 0; i < this.tooltipsNumber; ++i) {
            String tooltipNumber = i == 0 ? "" : String.valueOf(i + 1);
            tooltips.add(Localization.translate((String)"itemtooltip", (String)(this.getStringID() + "_mod" + tooltipNumber)));
        }
    }

    public int getTooltipsNumber() {
        return this.tooltipsNumber;
    }

    public int getStackSize() {
        return 1;
    }
}

