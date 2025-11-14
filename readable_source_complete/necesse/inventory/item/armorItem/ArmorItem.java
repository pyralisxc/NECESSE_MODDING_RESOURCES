/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.GlobalData;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemEnchantment;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobGenericEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.ArmorBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameColor;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.DefaultColoredGameTooltips;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.PlayerInventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.ContainerTransferResult;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.inventory.item.upgradeUtils.SalvageableItem;
import necesse.inventory.item.upgradeUtils.UpgradableItem;
import necesse.inventory.item.upgradeUtils.UpgradedItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.light.GameLight;

public class ArmorItem
extends Item
implements Enchantable<EquipmentItemEnchant>,
UpgradableItem,
SalvageableItem {
    protected IntUpgradeValue armorValue = new IntUpgradeValue(0, 0.25f);
    protected IntUpgradeValue enchantCost = new IntUpgradeValue(0, 0.07f);
    public ArmorType armorType;
    public HairDrawMode hairDrawOptions = HairDrawMode.NO_HAIR;
    public FacialFeatureDrawMode facialFeatureDrawOptions = FacialFeatureDrawMode.NO_FACIAL_FEATURE;
    public final String textureName;
    public GameTexture armorTexture;
    public GameTexture frontArmorTexture;
    public GameTexture backArmorTexture;
    public boolean isCosmetic;
    public boolean drawBodyPart = true;
    public ArrayList<OneOfLootItems> onRegisterLootTables = new ArrayList();
    public String tierOneEssencesUpgradeRequirement = "anytier1essence";
    public String tierTwoEssencesUpgradeRequirement = "anytier2essence";

    @Override
    public void setUpgradeLevel(InventoryItem item, int upgradeLevel) {
        super.setUpgradeLevel(item, upgradeLevel);
    }

    @Override
    public int getUpgradeLevel(InventoryItem item) {
        return super.getUpgradeLevel(item);
    }

    public ArmorItem(ArmorType armorType, int armorValue, int enchantCost, OneOfLootItems lootTableCategory, String textureName) {
        super(1);
        this.textureName = textureName;
        this.addToLootTable(lootTableCategory);
        this.keyWords.add("armor");
        this.armorType = armorType;
        this.armorValue.setBaseValue(armorValue);
        this.enchantCost.setBaseValue(enchantCost).setUpgradedValue(1.0f, 2000);
        switch (armorType) {
            case HEAD: {
                if (armorValue > 0) {
                    this.armorValue.setUpgradedValue(1.0f, 30);
                    if (armorValue > 30) {
                        this.armorValue.setBaseValue(30);
                    }
                }
                this.keyWords.add("helmet");
                this.keyWords.add("head");
                this.keyWords.add("hat");
                break;
            }
            case CHEST: {
                if (armorValue > 0) {
                    this.armorValue.setUpgradedValue(1.0f, 40);
                    if (armorValue > 40) {
                        this.armorValue.setBaseValue(40);
                    }
                }
                this.keyWords.add("chest");
                this.keyWords.add("body");
                break;
            }
            case FEET: {
                if (armorValue > 0) {
                    this.armorValue.setUpgradedValue(1.0f, 20);
                    if (armorValue > 20) {
                        this.armorValue.setBaseValue(20);
                    }
                }
                this.keyWords.add("feet");
                this.keyWords.add("boots");
            }
        }
        boolean bl = this.isCosmetic = armorValue == 0;
        if (this.isCosmetic) {
            this.setItemCategory("equipment", "cosmetics");
            this.setItemCategory(ItemCategory.craftingManager, "equipment", "cosmetics");
            this.keyWords.add("cosmetic");
        } else {
            this.setItemCategory("equipment", "armor");
            this.setItemCategory(ItemCategory.equipmentManager, "armor");
            this.setItemCategory(ItemCategory.craftingManager, "equipment", "armor");
        }
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 30000;
    }

    public ArmorItem addToLootTable(OneOfLootItems ... lootTables) {
        if (this.idData.isSet()) {
            for (OneOfLootItems lootTable : lootTables) {
                if (lootTable == null) continue;
                lootTable.add(new LootItem(this.getStringID()));
            }
        } else {
            for (OneOfLootItems lootTable : lootTables) {
                if (lootTable == null) continue;
                this.onRegisterLootTables.add(lootTable);
            }
        }
        return this;
    }

    public ArmorItem hairDrawMode(HairDrawMode drawMode) {
        this.hairDrawOptions = drawMode;
        return this;
    }

    public ArmorItem facialFeatureDrawMode(FacialFeatureDrawMode drawMode) {
        this.facialFeatureDrawOptions = drawMode;
        return this;
    }

    public ArmorItem drawBodyPart(boolean drawBodyPart) {
        this.drawBodyPart = drawBodyPart;
        return this;
    }

    @Override
    public void onItemRegistryClosed() {
        super.onItemRegistryClosed();
        for (OneOfLootItems lootTable : this.onRegisterLootTables) {
            lootTable.add(new LootItem(this.getStringID()));
        }
        this.onRegisterLootTables = null;
        if (this.armorValue.hasMoreThanOneValue() && this.armorValue.getValue(0.0f) > this.armorValue.getValue(1.0f)) {
            this.armorValue.setBaseValue(this.armorValue.getValue(1.0f));
        }
    }

    @Override
    public final ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(this.getPreEnchantmentTooltips(item, perspective, blackboard));
        if (!blackboard.getBoolean("isCosmeticSlot")) {
            tooltips.add(this.getEnchantmentTooltips(item));
        }
        tooltips.add(this.getPostEnchantmentTooltips(item, perspective, blackboard));
        return tooltips;
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        if (this.armorType == ArmorType.HEAD) {
            tooltips.add(Localization.translate("itemtooltip", "headslot"));
        }
        if (this.armorType == ArmorType.CHEST) {
            tooltips.add(Localization.translate("itemtooltip", "chestslot"));
        }
        if (this.armorType == ArmorType.FEET) {
            tooltips.add(Localization.translate("itemtooltip", "feetslot"));
        }
        if (this.isCosmetic) {
            tooltips.add(Localization.translate("itemtooltip", "cosmetic"));
        } else if (blackboard.getBoolean("isCosmeticSlot")) {
            tooltips.add(GameColor.RED.getColorCode() + Localization.translate("itemtooltip", "cosmeticslot"));
        } else {
            Mob equippedMob = blackboard.get(Mob.class, "equippedMob", perspective);
            if (equippedMob == null) {
                equippedMob = blackboard.get(Mob.class, "perspective", perspective);
            }
            if (equippedMob == null) {
                equippedMob = perspective;
            }
            this.addStatTooltips(tooltips, item, blackboard.get(InventoryItem.class, "compareItem"), blackboard.getBoolean("showDifference"), blackboard.getBoolean("forceAdd"), equippedMob);
        }
        return tooltips;
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        if (!blackboard.getBoolean("isCosmeticSlot")) {
            SetBonusBuff nextSetBonus;
            GameTooltips bonus = blackboard.get(GameTooltips.class, "setBonus");
            if (bonus != null) {
                tooltips.add(Localization.translate("itemtooltip", "setbonus"));
                tooltips.add(bonus);
            } else if (perspective != null && (nextSetBonus = this.getSetBuff(item, perspective, false)) != null) {
                tooltips.add(new StringTooltips(Localization.translate("itemtooltip", "setbonus"), GameColor.GRAY));
                GameBlackboard buffBlackboard = new GameBlackboard();
                InventoryItem compareItem = blackboard.get(InventoryItem.class, "compareItem");
                if (compareItem != null) {
                    ActiveBuff compareBuff = new ActiveBuff((Buff)nextSetBonus, (Mob)perspective, 1, null);
                    compareBuff.getGndData().setBoolean("tooltipInit", true);
                    this.setupSetBuff(compareItem, null, null, perspective, compareBuff, false);
                    compareBuff.init(new BuffEventSubscriber(){

                        @Override
                        public <T extends MobGenericEvent> void subscribeEvent(Class<T> eventClass, Consumer<T> onEvent) {
                        }
                    });
                    buffBlackboard.set("compareValues", compareBuff);
                }
                ActiveBuff ab = new ActiveBuff((Buff)nextSetBonus, (Mob)perspective, 1, null);
                ab.getGndData().setBoolean("tooltipInit", true);
                buffBlackboard.set("setItem", item);
                this.setupSetBuff(item, null, null, perspective, ab, false);
                ab.init(new BuffEventSubscriber(){

                    @Override
                    public <T extends MobGenericEvent> void subscribeEvent(Class<T> eventClass, Consumer<T> onEvent) {
                    }
                });
                tooltips.add(new DefaultColoredGameTooltips((GameTooltips)ab.getTooltips(buffBlackboard), GameColor.GRAY));
            }
        }
        return tooltips;
    }

    public final void addStatTooltips(ListGameTooltips tooltips, InventoryItem currentItem, InventoryItem lastItem, boolean showDifference, boolean forceAdd, Mob perspective) {
        ItemStatTipList list = new ItemStatTipList();
        this.addStatTooltips(list, currentItem, lastItem, perspective, forceAdd);
        for (ItemStatTip itemStatTip : list) {
            tooltips.add(itemStatTip.toTooltip(GameColor.GREEN.color.get(), GameColor.RED.color.get(), GameColor.YELLOW.color.get(), showDifference));
        }
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective, boolean forceAdd) {
        this.addArmorTooltip(list, currentItem, lastItem, perspective);
        if (perspective != null && perspective.isHuman || forceAdd) {
            this.addSettlerBonusHealthTooltip(list, currentItem, lastItem, perspective);
        }
        this.addModifierTooltips(list, currentItem, lastItem, perspective);
    }

    @Override
    public String getInventoryRightClickControlTip(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        PlayerMob mob = null;
        Inventory inventory = slot.getInventory();
        if (inventory instanceof PlayerInventory) {
            mob = ((PlayerInventory)inventory).player;
        }
        if (slotIndex == container.CLIENT_HELMET_SLOT || slotIndex == container.CLIENT_CHEST_SLOT || slotIndex == container.CLIENT_FEET_SLOT) {
            return Localization.translate("controls", "removetip");
        }
        if (slotIndex == container.CLIENT_COSM_HELMET_SLOT || slotIndex == container.CLIENT_COSM_CHEST_SLOT || slotIndex == container.CLIENT_COSM_FEET_SLOT) {
            if (this.getFlatArmorValue(item) > 0 && this.canMobEquip(mob, item)) {
                return Localization.translate("controls", "equiptip");
            }
            return Localization.translate("controls", "removetip");
        }
        if (this.canMobEquip(mob, item)) {
            return Localization.translate("controls", "equiptip");
        }
        return null;
    }

    @Override
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            if (slotIndex == container.CLIENT_HELMET_SLOT || slotIndex == container.CLIENT_CHEST_SLOT || slotIndex == container.CLIENT_FEET_SLOT) {
                ContainerTransferResult result = container.transferToSlots(slot, Arrays.asList(new SlotIndexRange(container.CLIENT_HOTBAR_START, container.CLIENT_HOTBAR_END), new SlotIndexRange(container.CLIENT_INVENTORY_START, container.CLIENT_INVENTORY_END)));
                return new ContainerActionResult(843241958, result.error);
            }
            if (slotIndex == container.CLIENT_COSM_HELMET_SLOT || slotIndex == container.CLIENT_COSM_CHEST_SLOT || slotIndex == container.CLIENT_COSM_FEET_SLOT) {
                if (this.getFlatArmorValue(item) > 0) {
                    if (this.armorType == ArmorType.HEAD) {
                        ItemCombineResult result = container.getSlot(container.CLIENT_HELMET_SLOT).swapItems(slot);
                        return new ContainerActionResult(-2094192454, result.error);
                    }
                    if (this.armorType == ArmorType.CHEST) {
                        ItemCombineResult result = container.getSlot(container.CLIENT_CHEST_SLOT).swapItems(slot);
                        return new ContainerActionResult(-2094192453, result.error);
                    }
                    if (this.armorType == ArmorType.FEET) {
                        ItemCombineResult result = container.getSlot(container.CLIENT_FEET_SLOT).swapItems(slot);
                        return new ContainerActionResult(-2094192452, result.error);
                    }
                    return new ContainerActionResult(-2094192455);
                }
                ContainerTransferResult result = container.transferToSlots(slot, Arrays.asList(new SlotIndexRange(container.CLIENT_HOTBAR_START, container.CLIENT_HOTBAR_END), new SlotIndexRange(container.CLIENT_INVENTORY_START, container.CLIENT_INVENTORY_END)));
                return new ContainerActionResult(1314787361, result.error);
            }
            if (this.getFlatArmorValue(item) <= 0) {
                if (this.armorType == ArmorType.HEAD) {
                    ItemCombineResult result = container.getSlot(container.CLIENT_COSM_HELMET_SLOT).swapItems(slot);
                    return new ContainerActionResult(-2004484079, result.error);
                }
                if (this.armorType == ArmorType.CHEST) {
                    ItemCombineResult result = container.getSlot(container.CLIENT_COSM_CHEST_SLOT).swapItems(slot);
                    return new ContainerActionResult(-2004484078, result.error);
                }
                if (this.armorType == ArmorType.FEET) {
                    ItemCombineResult result = container.getSlot(container.CLIENT_COSM_FEET_SLOT).swapItems(slot);
                    return new ContainerActionResult(-2004484077, result.error);
                }
                return new ContainerActionResult(-2004484080);
            }
            if (this.armorType == ArmorType.HEAD) {
                ItemCombineResult result = container.getSlot(container.CLIENT_HELMET_SLOT).swapItems(slot);
                return new ContainerActionResult(674304817, result.error);
            }
            if (this.armorType == ArmorType.CHEST) {
                ItemCombineResult result = container.getSlot(container.CLIENT_CHEST_SLOT).swapItems(slot);
                return new ContainerActionResult(674304818, result.error);
            }
            if (this.armorType == ArmorType.FEET) {
                ItemCombineResult result = container.getSlot(container.CLIENT_FEET_SLOT).swapItems(slot);
                return new ContainerActionResult(674304819, result.error);
            }
            return new ContainerActionResult(674304816);
        };
    }

    public void addArmorTooltip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective) {
        LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "armorvalue", "value", this.getTotalArmorValue(currentItem, perspective), 0);
        if (lastItem != null) {
            tip.setCompareValue(this.getTotalArmorValue(lastItem, perspective));
        }
        list.add(100, tip);
    }

    public void addSettlerBonusHealthTooltip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective) {
        DoubleItemStatTip tip = new DoubleItemStatTip(HumanMob.getBonusHealth(this.getTotalArmorValue(currentItem, perspective)), 0){

            @Override
            public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                LocalMessage replace = new LocalMessage("buffmodifiers", "maxhealthflat", "mod", this.getReplaceValue(betterColor, worseColor, showDifference));
                return new LocalMessage("ui", "settlerbonus", "value", replace);
            }
        };
        tip.setValueToString(value -> {
            if (value > 0.0) {
                return "+" + GameMath.removeDecimalIfZero(value);
            }
            return GameMath.removeDecimalIfZero(value);
        });
        if (lastItem != null) {
            tip.setCompareValue(HumanMob.getBonusHealth(this.getTotalArmorValue(lastItem, perspective)));
        }
        list.add(150, tip);
    }

    public void addModifierTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective) {
        ArmorModifiers modifiers = this.getArmorModifiers(currentItem, perspective);
        if (modifiers != null) {
            for (ModifierTooltip tooltips : modifiers.getModifierTooltips(lastItem == null ? null : this.getArmorModifiers(lastItem, perspective))) {
                list.add(200, tooltips.tip);
            }
        }
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.loadArmorTexture();
    }

    public int getFlatArmorValue(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("armor") ? gndData.getInt("armor") : this.armorValue.getValue(this.getUpgradeTier(item)).intValue();
    }

    public int getTotalArmorValue(InventoryItem item, Mob perspective) {
        return this.getFlatArmorValue(item);
    }

    public float getSettlerEquipmentValueFlat(InventoryItem item) {
        return this.getEnchantCost(item);
    }

    public float getSettlerEquipmentValue(InventoryItem item, HumanMob mob) {
        EquipmentItemEnchant enchantment = this.getEnchantment(item);
        return this.getSettlerEquipmentValueFlat(item) * (enchantment == null ? 1.0f : enchantment.getEnchantCostMod());
    }

    public float getRaiderTicketModifier(InventoryItem item, HashSet<String> obtainedItems) {
        return 1.0f;
    }

    public boolean drawBodyPart(InventoryItem item, PlayerMob player) {
        return this.drawBodyPart;
    }

    protected void loadArmorTexture() {
        if (this.textureName != null) {
            this.backArmorTexture = this.loadTextureIfExists("player/armor/" + this.textureName + "_back");
            this.armorTexture = GameTexture.fromFile("player/armor/" + this.textureName);
            this.frontArmorTexture = this.loadTextureIfExists("player/armor/" + this.textureName + "_front");
        }
    }

    public GameTexture loadTextureIfExists(String path) {
        try {
            return GameTexture.fromFileRaw(path);
        }
        catch (FileNotFoundException e) {
            return null;
        }
    }

    public GameTexture getArmorTexture(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem) {
        return this.armorTexture;
    }

    public DrawOptions getArmorDrawOptions(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        GameTexture armorTexture = this.getArmorTexture(item, level, player, headItem, chestItem, feetItem);
        Color col = this.getDrawColor(item, player);
        if (armorTexture != null) {
            return armorTexture.initDraw().sprite(spriteX, spriteY, spriteRes).colorLight(col, light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY);
        }
        return () -> {};
    }

    public final DrawOptions getArmorDrawOptions(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        return this.getArmorDrawOptions(item, level, player, headItem, chestItem, feetItem, spriteX, spriteY, spriteRes, drawX, drawY, 64, 64, mirrorX, mirrorY, light, alpha, mask);
    }

    @Deprecated
    public DrawOptions getArmorDrawOptions(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        return this.getArmorDrawOptions(item, player == null ? null : player.getLevel(), player, null, null, null, spriteX, spriteY, spriteRes, drawX, drawY, width, height, mirrorX, mirrorY, light, alpha, mask);
    }

    @Deprecated
    public final DrawOptions getArmorDrawOptions(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        return this.getArmorDrawOptions(item, player, spriteX, spriteY, spriteRes, drawX, drawY, 64, 64, mirrorX, mirrorY, light, alpha, mask);
    }

    public GameTexture getFrontArmorTexture(InventoryItem item, PlayerMob player) {
        return this.frontArmorTexture;
    }

    public DrawOptions getFrontArmorDrawOptions(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        GameTexture armorTexture = this.getFrontArmorTexture(item, player);
        if (armorTexture != null) {
            Color col = this.getDrawColor(item, player);
            return armorTexture.initDraw().sprite(spriteX, spriteY, spriteRes).colorLight(col, light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY);
        }
        return null;
    }

    public final DrawOptions getFrontArmorDrawOptions(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        return this.getFrontArmorDrawOptions(item, player, spriteX, spriteY, spriteRes, drawX, drawY, 64, 64, mirrorX, mirrorY, light, alpha, mask);
    }

    public GameTexture getBackArmorTexture(InventoryItem item, PlayerMob player) {
        return this.backArmorTexture;
    }

    public DrawOptions getBackArmorDrawOptions(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        GameTexture armorTexture = this.getBackArmorTexture(item, player);
        if (armorTexture != null) {
            Color col = this.getDrawColor(item, player);
            return armorTexture.initDraw().sprite(spriteX, spriteY, spriteRes).colorLight(col, light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY);
        }
        return null;
    }

    public final DrawOptions getBackArmorDrawOptions(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int spriteRes, int drawX, int drawY, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        return this.getBackArmorDrawOptions(item, player, spriteX, spriteY, spriteRes, drawX, drawY, 64, 64, mirrorX, mirrorY, light, alpha, mask);
    }

    public void addExtraDrawOptions(HumanDrawOptions options, InventoryItem item) {
    }

    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return null;
    }

    public boolean hasSet(InventoryItem helmet, InventoryItem chest, InventoryItem boots) {
        return this.hasSet(helmet.item, chest.item, boots.item);
    }

    @Deprecated
    public boolean hasSet(Item helmet, Item chest, Item boots) {
        return false;
    }

    public SetBonusBuff getSetBuff(InventoryItem item, Mob mob, boolean isCosmetic) {
        return this.getSetBuff(mob, isCosmetic);
    }

    public void setupSetBuff(InventoryItem helmet, InventoryItem chest, InventoryItem boots, Mob mob, ActiveBuff buff, boolean isCosmetic) {
        int sum = 0;
        int count = 0;
        if (helmet != null) {
            sum += this.getUpgradeLevel(helmet);
            ++count;
        }
        if (chest != null) {
            sum += this.getUpgradeLevel(chest);
            ++count;
        }
        if (boots != null) {
            sum += this.getUpgradeLevel(boots);
            ++count;
        }
        buff.getGndData().setInt("upgradeLevel", Math.round((float)sum / (float)count));
    }

    @Deprecated
    public SetBonusBuff getSetBuff(Mob mob, boolean isCosmetic) {
        return null;
    }

    public ArmorBuff[] getBuffs(InventoryItem item) {
        return new ArmorBuff[0];
    }

    @Override
    public float getSinkingRate(ItemPickupEntity entity, float currentSinking) {
        return super.getSinkingRate(entity, currentSinking) / 5.0f;
    }

    @Override
    public boolean canCombineItem(Level level, PlayerMob player, InventoryItem me, InventoryItem them, String purpose) {
        if (!super.canCombineItem(level, player, me, them, purpose)) {
            return false;
        }
        return this.isSameGNDData(level, me, them, purpose);
    }

    @Override
    public boolean isSameGNDData(Level level, InventoryItem me, InventoryItem them, String purpose) {
        return me.getGndData().sameKeys(them.getGndData(), "enchantment", "upgradeLevel");
    }

    @Override
    public boolean isEnchantable(InventoryItem item) {
        return this.getEnchantCost(item) > 0;
    }

    @Override
    public void setEnchantment(InventoryItem item, int enchantment) {
        item.getGndData().setItem("enchantment", (GNDItem)new GNDItemEnchantment(enchantment));
    }

    @Override
    public int getEnchantmentID(InventoryItem item) {
        GNDItem enchantment = item.getGndData().getItem("enchantment");
        GNDItemEnchantment enchantmentItem = GNDItemEnchantment.convertEnchantmentID(enchantment);
        item.getGndData().setItem("enchantment", (GNDItem)enchantmentItem);
        return enchantmentItem.getRegistryID();
    }

    @Override
    public void clearEnchantment(InventoryItem item) {
        item.getGndData().setItem("enchantment", null);
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        return EnchantmentRegistry.equipmentEnchantments;
    }

    @Override
    public EquipmentItemEnchant getRandomEnchantment(GameRandom random, InventoryItem item) {
        return Enchantable.getRandomEnchantment(random, EnchantmentRegistry.equipmentEnchantments, this.getEnchantmentID(item), EquipmentItemEnchant.class);
    }

    @Override
    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return EnchantmentRegistry.equipmentEnchantments.contains(enchantment.getID());
    }

    @Override
    public int getEnchantCost(InventoryItem item) {
        return this.enchantCost.getValue(item.item.getUpgradeTier(item));
    }

    @Override
    public EquipmentItemEnchant getEnchantment(InventoryItem item) {
        return EnchantmentRegistry.getEnchantment(this.getEnchantmentID(item), EquipmentItemEnchant.class, EquipmentItemEnchant.noEnchant);
    }

    @Override
    public GameTooltips getEnchantmentTooltips(InventoryItem item) {
        ListGameTooltips tooltips = new ListGameTooltips();
        int enchantmentMod = UpgradedItem.getEnchantmentMod(item);
        if (enchantmentMod > 1) {
            String empoweredText = Localization.translate("enchantment", "empoweredenchant");
            Color lightBlue = new Color(75, 211, 229);
            tooltips.add(new StringTooltips(empoweredText, lightBlue));
        }
        if (this.getEnchantmentID(item) > 0) {
            tooltips.add(this.getEnchantment(item).modEnchantment(enchantmentMod).getTooltips());
            if (GlobalData.debugActive()) {
                tooltips.addFirst("Enchantment id " + this.getEnchantmentID(item));
            }
        }
        return tooltips;
    }

    @Override
    public float getBrokerValue(InventoryItem item) {
        int totalBrokerValueInceaseBasedOnTier = 0;
        float tier = item.item.getUpgradeTier(item);
        if (tier > 0.0f) {
            int upgradeShardBrokerValue = 8;
            int tierIncreaseBrokerValue = 10 * ((int)tier - 1) * upgradeShardBrokerValue;
            int tierZeroToOneBrokerValue = Math.max((int)((1.0f - this.getTier1CostPercent(item)) * 20.0f), 1) * upgradeShardBrokerValue;
            totalBrokerValueInceaseBasedOnTier = tierZeroToOneBrokerValue + tierIncreaseBrokerValue;
        }
        return super.getBrokerValue(item) * this.getEnchantment(item).getEnchantCostMod() + (float)totalBrokerValueInceaseBasedOnTier;
    }

    @Override
    protected ListGameTooltips getDisplayNameTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getDisplayNameTooltips(item, perspective, blackboard);
        int upgradeLevel = this.getUpgradeLevel(item);
        if (upgradeLevel > 0) {
            String tierString;
            int tier = upgradeLevel / 100;
            if ((float)tier == (float)upgradeLevel / 100.0f) {
                tierString = String.valueOf(tier);
            } else {
                int extra = upgradeLevel - tier * 100;
                tierString = tier + " (+" + extra + "%)";
            }
            tooltips.add(new StringTooltips(Localization.translate("item", "tier", "tiernumber", tierString), new Color(133, 49, 168)));
        }
        return tooltips;
    }

    @Override
    public Item.Rarity getRarity(InventoryItem item) {
        Item.Rarity rarity = super.getRarity(item);
        if (this.getUpgradeTier(item) >= 1.0f) {
            return rarity.getNext(Item.Rarity.EPIC);
        }
        return rarity;
    }

    @Override
    public GameMessage getLocalization(InventoryItem item) {
        GameMessage out = super.getLocalization(item);
        EquipmentItemEnchant enchant = this.getEnchantment(item);
        if (enchant != null && enchant.getID() > 0) {
            out = new LocalMessage("enchantment", "format", "enchantment", enchant.getLocalization(), "item", out);
        }
        return out;
    }

    @Override
    public InventoryItem getDefaultLootItem(GameRandom random, int amount) {
        InventoryItem item = super.getDefaultLootItem(random, amount);
        if (this.isEnchantable(item) && random.getChance(0.65f)) {
            ((Enchantable)((Object)item.item)).addRandomEnchantment(item, random);
        }
        return item;
    }

    @Override
    public String getCanBeUpgradedError(InventoryItem item) {
        if (this.getFlatArmorValue(item) <= 0 || !this.armorValue.hasMoreThanOneValue()) {
            return Localization.translate("ui", "itemnotupgradable");
        }
        if (this.getUpgradeTier(item) >= (float)IncursionData.ITEM_TIER_UPGRADE_CAP) {
            return Localization.translate("ui", "itemupgradelimit");
        }
        return null;
    }

    @Override
    public void addUpgradeStatTips(ItemStatTipList list, InventoryItem lastItem, InventoryItem upgradedItem, ItemAttackerMob perspective, ItemAttackerMob statPerspective) {
        SetBonusBuff upgradedSetBonus;
        DoubleItemStatTip tierTip = new LocalMessageDoubleItemStatTip("item", "tier", "tiernumber", this.getUpgradeTier(upgradedItem), 2).setCompareValue(this.getUpgradeTier(lastItem)).setToString(tier -> {
            int floorTier = (int)tier;
            double percentAdd = tier - (double)floorTier;
            if (percentAdd != 0.0) {
                return floorTier + " (+" + (int)(percentAdd * 100.0) + "%)";
            }
            return String.valueOf(floorTier);
        });
        list.add(Integer.MIN_VALUE, tierTip);
        this.addStatTooltips(list, upgradedItem, lastItem, statPerspective, true);
        int lastEnchantmentMod = UpgradedItem.getEnchantmentMod(lastItem);
        int nextEnchantmentMod = UpgradedItem.getEnchantmentMod(upgradedItem);
        if (nextEnchantmentMod > 1) {
            DoubleItemStatTip empoweredEnchantTip = new DoubleItemStatTip(nextEnchantmentMod, 0){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    GameMessageBuilder builder = new GameMessageBuilder();
                    if (betterColor != null) {
                        builder.append(GameColor.getCustomColorCode(betterColor));
                    } else if (worseColor != null) {
                        builder.append(GameColor.getCustomColorCode(worseColor));
                    } else if (neutralColor != null) {
                        builder.append(GameColor.getCustomColorCode(neutralColor));
                    } else {
                        builder.append(GameColor.getCustomColorCode(new Color(75, 211, 229)));
                    }
                    builder.append("enchantment", "empoweredenchant");
                    return builder;
                }
            };
            empoweredEnchantTip.setCompareValue(lastEnchantmentMod);
            list.add(5000, empoweredEnchantTip);
        }
        if ((upgradedSetBonus = this.getSetBuff(upgradedItem, perspective, false)) != null) {
            ItemStatTipList setBonusTip = new ItemStatTipList();
            setBonusTip.add(Integer.MIN_VALUE, new ItemStatTip(){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("itemtooltip", "setbonus");
                }
            });
            GameBlackboard buffBlackboard = new GameBlackboard();
            ActiveBuff lastSetBuff = new ActiveBuff((Buff)upgradedSetBonus, (Mob)perspective, 1, null);
            lastSetBuff.getGndData().setBoolean("tooltipInit", true);
            this.setupSetBuff(lastItem, null, null, perspective, lastSetBuff, false);
            lastSetBuff.init(new BuffEventSubscriber(){

                @Override
                public <T extends MobGenericEvent> void subscribeEvent(Class<T> eventClass, Consumer<T> onEvent) {
                }
            });
            buffBlackboard.set("compareValues", lastSetBuff);
            ActiveBuff ab = new ActiveBuff((Buff)upgradedSetBonus, (Mob)perspective, 1, null);
            ab.getGndData().setBoolean("tooltipInit", true);
            this.setupSetBuff(upgradedItem, null, null, perspective, ab, false);
            ab.init(new BuffEventSubscriber(){

                @Override
                public <T extends MobGenericEvent> void subscribeEvent(Class<T> eventClass, Consumer<T> onEvent) {
                }
            });
            LinkedList<ItemStatTip> setStatList = new LinkedList<ItemStatTip>();
            upgradedSetBonus.addStatTooltips(setStatList, ab, lastSetBuff);
            int i = 0;
            for (ItemStatTip setTip : setStatList) {
                setBonusTip.add(i++, setTip);
            }
            if (i > 0) {
                list.add(10000, setBonusTip);
            }
        }
    }

    protected int getNextUpgradeTier(InventoryItem item) {
        int currentTier = (int)item.item.getUpgradeTier(item);
        int nextTier = currentTier + 1;
        float baseValue = this.armorValue.getValue(0.0f).intValue();
        float nextTierValue = this.armorValue.getValue(nextTier).intValue();
        if (nextTier == 1 && baseValue < nextTierValue) {
            return nextTier;
        }
        while (baseValue / nextTierValue > 1.0f - this.armorValue.defaultLevelIncreaseMultiplier / 4.0f && nextTier < currentTier + 100) {
            nextTierValue = this.armorValue.getValue(++nextTier).intValue();
        }
        return nextTier;
    }

    protected float getTier1CostPercent(InventoryItem item) {
        return (float)this.armorValue.getValue(0.0f).intValue() / (float)this.armorValue.getValue(1.0f).intValue();
    }

    protected float getUpgradeCostPerTier(InventoryItem item) {
        return 20.0f;
    }

    @Override
    public UpgradedItem getUpgradedItem(InventoryItem item) {
        int nextTier = this.getNextUpgradeTier(item);
        InventoryItem upgradedItem = item.copy();
        upgradedItem.item.setUpgradeTier(upgradedItem, nextTier);
        float cost = nextTier <= 1 ? Math.max((1.0f - this.getTier1CostPercent(item)) * 40.0f, 1.0f) : (float)nextTier * this.getUpgradeCostPerTier(item);
        HashMap<Integer, Ingredient> additionalUpgradeCost = this.getEssenceUpgradeCost();
        if (additionalUpgradeCost.get(nextTier) != null) {
            return new UpgradedItem(item, upgradedItem, new Ingredient[]{new Ingredient("upgradeshard", Math.round(cost)), additionalUpgradeCost.get(nextTier)});
        }
        return new UpgradedItem(item, upgradedItem, new Ingredient[]{new Ingredient("upgradeshard", Math.round(cost))});
    }

    protected float getSavageRewardPerTier(InventoryItem item) {
        return item.getAmount() * 15;
    }

    @Override
    public Collection<InventoryItem> getSalvageRewards(InventoryItem item) {
        float rewardPerTier = this.getSavageRewardPerTier(item);
        float reward = 0.0f;
        for (float tier = this.getUpgradeTier(item); tier > 0.0f; tier -= 1.0f) {
            reward += tier * rewardPerTier;
        }
        return Collections.singleton(new InventoryItem("upgradeshard", Math.round(reward)));
    }

    public boolean canMobEquip(Mob mob, InventoryItem item) {
        return true;
    }

    private HashMap<Integer, Ingredient> getEssenceUpgradeCost() {
        HashMap<Integer, Ingredient> additionalUpgradeCost = new HashMap<Integer, Ingredient>();
        additionalUpgradeCost.put(4, new Ingredient(this.tierOneEssencesUpgradeRequirement, 5));
        additionalUpgradeCost.put(5, new Ingredient(this.tierOneEssencesUpgradeRequirement, 10));
        additionalUpgradeCost.put(6, new Ingredient(this.tierOneEssencesUpgradeRequirement, 15));
        additionalUpgradeCost.put(7, new Ingredient(this.tierOneEssencesUpgradeRequirement, 20));
        additionalUpgradeCost.put(8, new Ingredient(this.tierTwoEssencesUpgradeRequirement, 10));
        additionalUpgradeCost.put(9, new Ingredient(this.tierTwoEssencesUpgradeRequirement, 20));
        additionalUpgradeCost.put(10, new Ingredient(this.tierTwoEssencesUpgradeRequirement, 30));
        return additionalUpgradeCost;
    }

    public static enum HairDrawMode {
        NO_HAIR,
        UNDER_HAIR,
        OVER_HAIR,
        NO_HEAD;

    }

    public static enum FacialFeatureDrawMode {
        NO_FACIAL_FEATURE,
        UNDER_FACIAL_FEATURE,
        OVER_FACIAL_FEATURE;

    }

    public static enum ArmorType {
        HEAD,
        CHEST,
        FEET;

    }
}

