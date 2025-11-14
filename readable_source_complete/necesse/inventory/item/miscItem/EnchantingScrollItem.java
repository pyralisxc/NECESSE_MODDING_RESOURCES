/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemEnchantment;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.EnchantingScrollContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class EnchantingScrollItem
extends Item {
    private final HashMap<String, GameTexture> typeTextures = new HashMap();
    public static ArrayList<EnchantScrollType> types = new ArrayList();

    public EnchantingScrollItem() {
        super(1);
        this.rarity = Item.Rarity.RARE;
        this.setItemCategory("misc");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        ItemEnchantment enchantment = this.getEnchantment(item);
        if (enchantment != null && enchantment != EquipmentItemEnchant.noEnchant) {
            EnchantScrollType type = this.getType(enchantment);
            if (type != null) {
                tooltips.add(type.itemTooltip.apply(enchantment), 300);
            } else {
                tooltips.add("UNKNOWN_ENCHANT_TYPE");
            }
            tooltips.add(Localization.translate("itemtooltip", "singleuse"));
            tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
            tooltips.add(enchantment.getTooltips());
        } else {
            tooltips.add(Localization.translate("itemtooltip", "singleuse"));
            tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        }
        return tooltips;
    }

    public int getTypeIndex(ItemEnchantment enchantment) {
        if (enchantment != null) {
            for (int i = 0; i < types.size(); ++i) {
                EnchantScrollType type = types.get(i);
                if (!type.isPartOfThis.test(enchantment)) continue;
                return i;
            }
        }
        return -1;
    }

    public EnchantScrollType getType(ItemEnchantment enchantment) {
        int index = this.getTypeIndex(enchantment);
        if (index != -1) {
            return types.get(index);
        }
        return null;
    }

    @Override
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        GameTexture texture;
        EnchantScrollType type;
        ItemEnchantment enchantment = this.getEnchantment(item);
        if (enchantment != null && enchantment != EquipmentItemEnchant.noEnchant && (type = this.getType(enchantment)) != null && (texture = this.typeTextures.get(type.stringID)) != null) {
            return new GameSprite(texture);
        }
        return super.getItemSprite(item, perspective);
    }

    @Override
    protected void loadItemTextures() {
        super.loadItemTextures();
        for (EnchantScrollType type : types) {
            try {
                GameTexture texture = GameTexture.fromFileRaw("items/" + this.getStringID() + "_" + type.stringID);
                this.typeTextures.put(type.stringID, texture);
            }
            catch (FileNotFoundException fileNotFoundException) {}
        }
    }

    @Override
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            if (slot.getInventory() == container.getClient().playerMob.getInv().main) {
                if (container.getClient().isServer()) {
                    ServerClient client = container.getClient().getServerClient();
                    PacketOpenContainer p = new PacketOpenContainer(ContainerRegistry.ENCHANTING_SCROLL_CONTAINER, EnchantingScrollContainer.getContainerContent(client, slotIndex));
                    ContainerRegistry.openAndSendContainer(client, p);
                }
                return new ContainerActionResult(-1390943614);
            }
            return new ContainerActionResult(-1281215057, Localization.translate("itemtooltip", "rclickinvopenerror"));
        };
    }

    public void setEnchantment(InventoryItem item, int enchantment) {
        item.getGndData().setItem("enchantment", (GNDItem)new GNDItemEnchantment(enchantment));
    }

    public int getEnchantmentID(InventoryItem item) {
        int seed;
        GNDItem enchantment = item.getGndData().getItem("enchantment");
        if (enchantment == null && (seed = item.getGndData().getInt("generateScrollSeed")) != 0) {
            item.getGndData().clearItem("generateScrollSeed");
            int enchantmentID = EnchantingScrollItem.getRandomEnchantmentID(seed == -1 ? GameRandom.globalRandom : new GameRandom(seed));
            this.setEnchantment(item, enchantmentID);
            return enchantmentID;
        }
        GNDItemEnchantment enchantmentItem = GNDItemEnchantment.convertEnchantmentID(enchantment);
        item.getGndData().setItem("enchantment", (GNDItem)enchantmentItem);
        return enchantmentItem.getRegistryID();
    }

    public ItemEnchantment getEnchantment(InventoryItem item) {
        return EnchantmentRegistry.getEnchantment(this.getEnchantmentID(item), ItemEnchantment.class, EquipmentItemEnchant.noEnchant);
    }

    @Override
    public boolean isSameGNDData(Level level, InventoryItem me, InventoryItem them, String purpose) {
        return me.getGndData().sameKeys(them.getGndData(), "enchantment");
    }

    @Override
    public boolean canCombineItem(Level level, PlayerMob player, InventoryItem me, InventoryItem them, String purpose) {
        if (!super.canCombineItem(level, player, me, them, purpose)) {
            return false;
        }
        return this.isSameGNDData(level, me, them, purpose);
    }

    @Override
    public float getBrokerValue(InventoryItem item) {
        return super.getBrokerValue(item) * this.getEnchantment(item).getEnchantCostMod();
    }

    @Override
    public GameMessage getLocalization(InventoryItem item) {
        ItemEnchantment enchant = this.getEnchantment(item);
        if (enchant != null && enchant.getID() > 0) {
            return new LocalMessage("enchantment", "format", "enchantment", enchant.getLocalization(), "item", super.getLocalization(item));
        }
        return super.getLocalization(item);
    }

    @Override
    public void addDefaultItems(List<InventoryItem> list, PlayerMob player) {
        for (ItemEnchantment enchantment : EnchantmentRegistry.getEnchantments()) {
            if (enchantment.getID() == 0) continue;
            InventoryItem item = this.getDefaultItem(player, 1);
            this.setEnchantment(item, enchantment.getID());
            list.add(item);
        }
    }

    @Override
    public int compareToSameItem(InventoryItem me, InventoryItem them) {
        int theirIndex;
        ItemEnchantment theirEnchantment;
        ItemEnchantment myEnchantment = this.getEnchantment(me);
        if (myEnchantment == (theirEnchantment = this.getEnchantment(them))) {
            return super.compareToSameItem(me, them);
        }
        if (myEnchantment == null || myEnchantment == EquipmentItemEnchant.noEnchant) {
            return 1;
        }
        if (theirEnchantment == null || theirEnchantment == EquipmentItemEnchant.noEnchant) {
            return -1;
        }
        int myIndex = this.getTypeIndex(myEnchantment);
        if (myIndex != (theirIndex = this.getTypeIndex(theirEnchantment))) {
            return Integer.compare(myIndex, theirIndex);
        }
        return super.compareToSameItem(me, them);
    }

    @Override
    public InventoryItem getDefaultLootItem(GameRandom random, int amount) {
        InventoryItem item = super.getDefaultLootItem(random, amount);
        this.setEnchantment(item, EnchantingScrollItem.getRandomEnchantmentID(random));
        return item;
    }

    public static ItemEnchantment getRandomEnchantment(GameRandom random) {
        TicketSystemList ticketSystem = new TicketSystemList();
        for (EnchantScrollType type : types) {
            ticketSystem.addObject(type.tickets, type);
        }
        EnchantScrollType type = (EnchantScrollType)ticketSystem.getRandomObject(random);
        return type.randomEnchantmentGetter.apply(random);
    }

    public static int getRandomEnchantmentID(GameRandom random) {
        ItemEnchantment enchantment = EnchantingScrollItem.getRandomEnchantment(random);
        return enchantment == null ? 0 : enchantment.getID();
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "scroll");
    }

    static {
        types.add(new EnchantScrollType("equipment", 200, enchantment -> EnchantmentRegistry.equipmentEnchantments.contains(enchantment.getID()), random -> random.getOneOf((ItemEnchantment[])EnchantmentRegistry.equipmentEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter(e -> e.getEnchantCostMod() >= 1.0f).toArray(ItemEnchantment[]::new)), enchantment -> new LocalMessage("itemtooltip", "enchantingscrollequipmenttip", "enchantment", enchantment.getLocalization()), enchantment -> new LocalMessage("ui", "enchantscrollequipment")));
        types.add(new EnchantScrollType("tool", 40, enchantment -> EnchantmentRegistry.toolDamageEnchantments.contains(enchantment.getID()), random -> random.getOneOf((ItemEnchantment[])EnchantmentRegistry.toolDamageEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter(e -> e.getEnchantCostMod() >= 1.0f).toArray(ItemEnchantment[]::new)), enchantment -> new LocalMessage("itemtooltip", "enchantingscrolltooltip", "enchantment", enchantment.getLocalization()), enchantment -> new LocalMessage("ui", "enchantscrolltool")));
        types.add(new EnchantScrollType("melee", 100, enchantment -> EnchantmentRegistry.meleeItemEnchantments.contains(enchantment.getID()), random -> random.getOneOf((ItemEnchantment[])EnchantmentRegistry.meleeItemEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter(e -> e.getEnchantCostMod() >= 1.0f).toArray(ItemEnchantment[]::new)), enchantment -> new LocalMessage("itemtooltip", "enchantingscrollmeleetip", "enchantment", enchantment.getLocalization()), enchantment -> new LocalMessage("ui", "enchantscrollmelee")));
        types.add(new EnchantScrollType("ranged", 100, enchantment -> EnchantmentRegistry.rangedItemEnchantments.contains(enchantment.getID()), random -> random.getOneOf((ItemEnchantment[])EnchantmentRegistry.rangedItemEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter(e -> e.getEnchantCostMod() >= 1.0f).toArray(ItemEnchantment[]::new)), enchantment -> new LocalMessage("itemtooltip", "enchantingscrollrangedtip", "enchantment", enchantment.getLocalization()), enchantment -> new LocalMessage("ui", "enchantscrollranged")));
        types.add(new EnchantScrollType("magic", 100, enchantment -> EnchantmentRegistry.magicItemEnchantments.contains(enchantment.getID()), random -> random.getOneOf((ItemEnchantment[])EnchantmentRegistry.magicItemEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter(e -> e.getEnchantCostMod() >= 1.0f).toArray(ItemEnchantment[]::new)), enchantment -> new LocalMessage("itemtooltip", "enchantingscrollmagictip", "enchantment", enchantment.getLocalization()), enchantment -> new LocalMessage("ui", "enchantscrollmagic")));
        types.add(new EnchantScrollType("summon", 100, enchantment -> EnchantmentRegistry.summonItemEnchantments.contains(enchantment.getID()), random -> random.getOneOf((ItemEnchantment[])EnchantmentRegistry.summonItemEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter(e -> e.getEnchantCostMod() >= 1.0f).toArray(ItemEnchantment[]::new)), enchantment -> new LocalMessage("itemtooltip", "enchantingscrollsummontip", "enchantment", enchantment.getLocalization()), enchantment -> new LocalMessage("ui", "enchantscrollsummon")));
    }

    public static class EnchantScrollType {
        public String stringID;
        public int tickets;
        public Predicate<ItemEnchantment> isPartOfThis;
        public Function<GameRandom, ItemEnchantment> randomEnchantmentGetter;
        public Function<ItemEnchantment, GameMessage> itemTooltip;
        public Function<ItemEnchantment, GameMessage> enchantTip;

        public EnchantScrollType(String stringID, int tickets, Predicate<ItemEnchantment> isPartOfThis, Function<GameRandom, ItemEnchantment> randomEnchantmentGetter, Function<ItemEnchantment, GameMessage> itemTooltip, Function<ItemEnchantment, GameMessage> enchantTip) {
            this.stringID = stringID;
            this.tickets = tickets;
            this.isPartOfThis = isPartOfThis;
            this.randomEnchantmentGetter = randomEnchantmentGetter;
            this.itemTooltip = itemTooltip;
            this.enchantTip = enchantTip;
        }
    }
}

