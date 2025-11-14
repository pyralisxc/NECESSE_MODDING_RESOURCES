/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDIncursionDataItem;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameColor;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.item.upgradeUtils.SalvageableItem;
import necesse.inventory.item.upgradeUtils.UpgradableItem;
import necesse.inventory.item.upgradeUtils.UpgradedItem;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.BiomeExtractionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.IncursionRewardGetter;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class GatewayTabletItem
extends Item
implements UpgradableItem,
SalvageableItem {
    public GatewayTabletItem() {
        super(1);
        this.rarity = Item.Rarity.UNIQUE;
        this.setItemCategory("misc");
        this.setItemCategory(ItemCategory.craftingManager, "incursions");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        IncursionData incursionData = GatewayTabletItem.getIncursionData(item);
        int tabletTier = item.getGndData().getInt("displayTier", Integer.MIN_VALUE);
        if (tabletTier == Integer.MIN_VALUE && incursionData != null) {
            tabletTier = incursionData.getTabletTier();
        }
        String tierText = Localization.translate("item", "tier", "tiernumber", (Object)tabletTier);
        if (tabletTier == Integer.MIN_VALUE) {
            tierText = Localization.translate("item", "tier", "tiernumber", "???");
        }
        tooltips.add(new StringTooltips(tierText, GameColor.ITEM_EPIC));
        boolean addedModifierAndRewards = false;
        if (incursionData != null) {
            if (!blackboard.getBoolean("hideModifierAndRewards")) {
                GameTooltips tooltip = incursionData.getIncursionBiome().getKnownIncursionDataPreRewardsTooltip(incursionData, GameColor.ITEM_QUEST);
                if (tooltip != null) {
                    tooltips.add(tooltip);
                }
                for (UniqueIncursionModifier uniqueIncursionModifier : incursionData.getUniqueIncursionModifiers()) {
                    String text = Localization.translate("ui", "incursionmodifier" + uniqueIncursionModifier.getStringID());
                    tooltips.add(new StringTooltips(text, GameColor.ITEM_QUEST));
                }
                ArrayList<StringTooltips> privateRewards = this.getTooltipTextFromRewards(incursionData.getPlayerPersonalIncursionCompleteRewards());
                ArrayList<StringTooltips> sharedRewards = this.getTooltipTextFromRewards(incursionData.getPlayerSharedIncursionCompleteRewards());
                tooltips.addAll(privateRewards);
                tooltips.addAll(sharedRewards);
                addedModifierAndRewards = true;
            }
        } else {
            IncursionBiome biome;
            String biomeKey = item.getGndData().getString("recipeBiome");
            if (biomeKey != null && (biome = IncursionBiomeRegistry.getBiome(biomeKey)) != null) {
                GameTooltips biomeTooltip = biome.getUnknownIncursionDataTooltip(GameColor.ITEM_QUEST);
                if (biomeTooltip != null) {
                    tooltips.add(biomeTooltip);
                }
                addedModifierAndRewards = true;
            }
        }
        if (!addedModifierAndRewards) {
            tooltips.add(new StringTooltips(Localization.translate("ui", "incursionrandommodifier"), GameColor.ITEM_QUEST));
            tooltips.add(new StringTooltips(Localization.translate("ui", "incursionrandomreward"), GameColor.ITEM_QUEST));
        }
        if (incursionData != null && blackboard.getBoolean("showDropTip")) {
            StringTooltips dropTooltip = new StringTooltips(Localization.translate("ui", "incursiondroptip", "biomename", incursionData.getIncursionBiome().displayName, "tier", tabletTier), GameColor.CYAN, 300);
            tooltips.add(dropTooltip);
        } else {
            tooltips.add(Localization.translate("itemtooltip", "gatewaytablettip"), 400);
            if (tabletTier < IncursionData.TABLET_TIER_UPGRADE_CAP) {
                String text = Localization.translate("itemtooltip", "canbeupgradedtip");
                tooltips.add(new StringTooltips(text, GameColor.CYAN));
            }
        }
        return tooltips;
    }

    public ArrayList<StringTooltips> getTooltipTextFromRewards(IncursionRewardGetter incursionRewards) {
        ArrayList<StringTooltips> tooltips = new ArrayList<StringTooltips>();
        ArrayList<InventoryItem> incursionRewardItems = incursionRewards.getRewards(true);
        for (InventoryItem rewardItem : incursionRewardItems) {
            String prefix;
            int upToAmount = rewardItem.getGndData().getInt("incursionUpToAmount");
            String string = prefix = upToAmount > 1 ? "1-" + upToAmount + " " : "";
            if (rewardItem.item instanceof GatewayTabletItem) {
                IncursionData incursionData = GatewayTabletItem.getIncursionData(rewardItem);
                if (incursionData == null) continue;
                String tier = Localization.translate("item", "tier", "tiernumber", (Object)incursionData.getTabletTier());
                String text = tier + " " + incursionData.getIncursionBiome().displayName.translate();
                tooltips.add(new StringTooltips(prefix + text, rewardItem.item.getRarityColor(rewardItem)));
                continue;
            }
            if (rewardItem.item instanceof TrinketItem) {
                String text = rewardItem.getItemDisplayName();
                tooltips.add(new StringTooltips(prefix + text, rewardItem.item.getRarityColor(rewardItem)));
                continue;
            }
            if (rewardItem.item instanceof ToolItem || rewardItem.item instanceof ArmorItem) {
                String text;
                int itemTier = (int)rewardItem.item.getUpgradeTier(rewardItem);
                if (itemTier > 0) {
                    String tier = Localization.translate("item", "tier", "tiernumber", (Object)itemTier);
                    text = tier + " " + Localization.translate("item", rewardItem.item.getStringID());
                } else {
                    text = Localization.translate("item", rewardItem.item.getStringID());
                }
                tooltips.add(new StringTooltips(prefix + text, rewardItem.item.getRarityColor(rewardItem)));
                continue;
            }
            String text = rewardItem.getItemDisplayName();
            tooltips.add(new StringTooltips(prefix + text, rewardItem.item.getRarityColor(rewardItem)));
        }
        return tooltips;
    }

    @Override
    protected ListGameTooltips getCraftingMatTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        return new ListGameTooltips();
    }

    @Override
    protected ListGameTooltips getDisplayNameTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        String biomeKey = item.getGndData().getString("recipeBiome");
        if (blackboard.getBoolean("showDropTip") && biomeKey != null) {
            String itemDisplayName = item.getItemDisplayName().replace(Localization.translate("biome", "extraction") + " ", "").replace(Localization.translate("biome", "hunt") + " ", "").replace(Localization.translate("biome", "trial") + " ", "");
            return new ListGameTooltips(new StringTooltips(itemDisplayName, this.getRarityColor(item)));
        }
        return super.getDisplayNameTooltips(item, perspective, blackboard);
    }

    @Override
    public GameMessage getLocalization(InventoryItem item) {
        IncursionBiome biome;
        String biomeKey = item.getGndData().getString("recipeBiome");
        if (biomeKey != null && (biome = IncursionBiomeRegistry.getBiome(biomeKey)) != null) {
            return new LocalMessage("item", "formattablet", "biome", biome.displayName, "tablet", new LocalMessage("item", "tablet").translate());
        }
        IncursionData incursionData = GatewayTabletItem.getIncursionData(item);
        if (incursionData != null) {
            return new LocalMessage("item", "formattablet", "biome", incursionData.getDisplayName(), "tablet", new LocalMessage("item", "tablet").translate());
        }
        return new LocalMessage("item", "gatewaytablet");
    }

    @Override
    public InventoryItem getDefaultItem(PlayerMob player, int amount) {
        InventoryItem tabletItem = super.getDefaultItem(player, amount);
        GatewayTabletItem.setIncursionData(tabletItem, new BiomeExtractionIncursionData(1.0f, IncursionBiomeRegistry.getBiome("forestcave"), 1));
        return tabletItem;
    }

    @Override
    public InventoryItem getDefaultLootItem(GameRandom random, int amount) {
        InventoryItem tabletItem = super.getDefaultLootItem(random, amount);
        GatewayTabletItem.initializeGatewayTablet(tabletItem, random, 1, null);
        return tabletItem;
    }

    @Override
    public void addDefaultItems(List<InventoryItem> list, PlayerMob player) {
        for (IncursionBiome biome : IncursionBiomeRegistry.getBiomes()) {
            biome.addDefaultTabletItems(this, list, player);
        }
    }

    public static void setIncursionData(InventoryItem item, IncursionData data) {
        if (data == null) {
            item.getGndData().setItem("incursionData", null);
        } else {
            item.getGndData().setItem("incursionData", (GNDItem)new GNDIncursionDataItem(data));
        }
    }

    public static IncursionData getIncursionData(InventoryItem item) {
        GNDItem incursionData = item.getGndData().getItem("incursionData");
        if (incursionData instanceof GNDIncursionDataItem) {
            return ((GNDIncursionDataItem)incursionData).incursionData;
        }
        return null;
    }

    public static void initializeGatewayTablet(InventoryItem item, GameRandom seededRandom, int tabletTier, IncursionData previousIncursionData) {
        if (item.item instanceof GatewayTabletItem) {
            IncursionBiome biome = GatewayTabletItem.getRandomIncursionBasedOnTier(seededRandom, tabletTier, previousIncursionData);
            GatewayTabletItem.initializeCustomGateTablet(item, seededRandom, tabletTier, biome, previousIncursionData);
        } else {
            GameLog.warn.println("Inventory item generated is not a gateway tablet. Can't generate tablet data.");
        }
    }

    public static void initializeCustomGateTablet(InventoryItem item, GameRandom seededRandom, int tabletTier, IncursionBiome biome, IncursionData previousIncursionData) {
        if (item.item instanceof GatewayTabletItem) {
            IncursionData incursion = biome.getRandomIncursion(seededRandom, tabletTier, previousIncursionData);
            if (previousIncursionData != null) {
                incursion.currentIncursionPerkIDs = new HashSet<Integer>(previousIncursionData.nextIncursionPerkIDs);
            }
            GatewayTabletItem.setIncursionData(item, incursion);
        } else {
            GameLog.warn.println("Inventory item generated is not a gateway tablet. Can't generate tablet data.");
        }
    }

    public static void initializeCustomGateTablet(InventoryItem item, GameRandom seededRandom, int tabletTier, IncursionBiome biome) {
        GatewayTabletItem.initializeCustomGateTablet(item, seededRandom, tabletTier, biome, null);
    }

    public static IncursionBiome getRandomIncursionBasedOnTier(GameRandom random, int tier, IncursionData incursionData) {
        IncursionBiome incursion;
        boolean specificTierList;
        ArrayList<IncursionBiome> incursionsFromLowerTiers = new ArrayList<IncursionBiome>();
        ArrayList<IncursionBiome> incursionsFromSpecificTier = new ArrayList<IncursionBiome>();
        for (IncursionBiome biome : IncursionBiomeRegistry.getBiomes()) {
            if (!biome.canDropInIncursion(incursionData) || IncursionBiomeRegistry.getBiomeTier(biome.getID()) > tier) continue;
            if (IncursionBiomeRegistry.getBiomeTier(biome.getID()) == tier) {
                incursionsFromSpecificTier.add(biome);
                continue;
            }
            incursionsFromLowerTiers.add(biome);
        }
        ArrayList<IncursionBiome> listToChooseFrom = !incursionsFromLowerTiers.isEmpty() && !incursionsFromSpecificTier.isEmpty() ? ((specificTierList = random.getChance(0.65f)) ? incursionsFromSpecificTier : incursionsFromLowerTiers) : (!incursionsFromLowerTiers.isEmpty() ? incursionsFromLowerTiers : incursionsFromSpecificTier);
        if (!listToChooseFrom.isEmpty()) {
            incursion = (IncursionBiome)listToChooseFrom.get(random.getIntBetween(0, listToChooseFrom.size() - 1));
        } else {
            GameLog.warn.println("Incursion list did not contain any incursions. No incursions with the tier: " + tier + " was found.Generating default forest cave incursion");
            incursion = IncursionBiomeRegistry.getBiome("forestcave");
        }
        return incursion;
    }

    @Override
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        GameSprite tabletSprite;
        IncursionBiome biome;
        String biomeKey = item.getGndData().getString("recipeBiome");
        if (biomeKey != null && (biome = IncursionBiomeRegistry.getBiome(biomeKey)) != null) {
            return biome.getTabletSprite();
        }
        IncursionData data = GatewayTabletItem.getIncursionData(item);
        if (data != null && (tabletSprite = data.getTabletSprite()) != null) {
            return tabletSprite;
        }
        return super.getItemSprite(item, perspective);
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
        return me.getGndData().sameKeys(them.getGndData(), "incursionData");
    }

    @Override
    public String getCanBeUpgradedError(InventoryItem item) {
        IncursionData incursionData = GatewayTabletItem.getIncursionData(item);
        if (incursionData == null) {
            return Localization.translate("ui", "itemnotupgradable");
        }
        if (incursionData.getTabletTier() >= IncursionData.TABLET_TIER_UPGRADE_CAP) {
            return Localization.translate("ui", "itemupgradelimit");
        }
        return null;
    }

    @Override
    public void addUpgradeStatTips(ItemStatTipList list, InventoryItem lastItem, InventoryItem upgradedItem, ItemAttackerMob perspective, ItemAttackerMob statPerspective) {
        IncursionData upgradedData = GatewayTabletItem.getIncursionData(upgradedItem);
        IncursionData lastData = GatewayTabletItem.getIncursionData(lastItem);
        if (upgradedData != null && lastData != null) {
            DoubleItemStatTip tierTip = new LocalMessageDoubleItemStatTip("item", "tier", "tiernumber", upgradedData.getTabletTier(), 2).setCompareValue(lastData.getTabletTier());
            list.add(Integer.MIN_VALUE, tierTip);
            list.add(Integer.MAX_VALUE, new ItemStatTip(){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("ui", "tabletupgradetip");
                }
            });
        }
    }

    @Override
    public UpgradedItem getUpgradedItem(InventoryItem item) {
        InventoryItem upgradedItem = item.copy();
        IncursionData incursionData = GatewayTabletItem.getIncursionData(upgradedItem);
        if (incursionData != null) {
            int nextTier = incursionData.getTabletTier() + 1;
            incursionData.setTabletTier(nextTier);
            int cost = Math.max(1, nextTier - 1) * 25;
            return new UpgradedItem(item, upgradedItem, new Ingredient[]{new Ingredient("upgradeshard", cost)});
        }
        return null;
    }

    @Override
    public String getCanBeSalvagedError(InventoryItem item) {
        IncursionData incursionData = GatewayTabletItem.getIncursionData(item);
        if (incursionData == null) {
            return Localization.translate("ui", "itemnotsalvageable");
        }
        return null;
    }

    @Override
    public Collection<InventoryItem> getSalvageRewards(InventoryItem item) {
        IncursionData incursionData = GatewayTabletItem.getIncursionData(item);
        int tier = incursionData == null ? 1 : incursionData.getTabletTier();
        int reward = item.getAmount() * tier * 10;
        return Collections.singleton(new InventoryItem("upgradeshard", reward));
    }

    @Override
    public int compareToSameItem(InventoryItem me, InventoryItem them) {
        IncursionData myIncursionData = GatewayTabletItem.getIncursionData(me);
        if (myIncursionData == null) {
            return 1;
        }
        IncursionData theirIncursionData = GatewayTabletItem.getIncursionData(them);
        if (theirIncursionData == null) {
            return -1;
        }
        int tierCompare = Integer.compare(myIncursionData.getTabletTier(), theirIncursionData.getTabletTier());
        if (tierCompare != 0) {
            return tierCompare;
        }
        return super.compareToSameItem(me, them);
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "relic");
    }
}

