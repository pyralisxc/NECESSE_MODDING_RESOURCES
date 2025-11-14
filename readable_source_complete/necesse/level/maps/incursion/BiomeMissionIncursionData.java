/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.engine.registries.UniqueIncursionModifierRegistry;
import necesse.engine.registries.UniqueIncursionRewardsRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.object.FallenAltarContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.miscItem.CoinItem;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.IncursionRewardGetter;
import necesse.level.maps.incursion.UniqueIncursionModifier;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public abstract class BiomeMissionIncursionData
extends IncursionData {
    public float difficulty;
    public IncursionBiome biome;
    protected int tabletTier;
    protected boolean modifiersInitialized = false;
    protected ArrayList<UniqueIncursionModifier> uniqueIncursionModifiers = new ArrayList();
    protected LootItemList playerPersonalIncursionCompleteRewards;
    protected LootItemList playerSharedIncursionCompleteRewards;
    protected ArrayList<ModifierValue<?>> levelModifiers = new ArrayList();
    private static final GameLootUtils.LootValueMap<IncursionModifier> modifierMap = new GameLootUtils.LootValueMap<IncursionModifier>(){

        @Override
        public float getValuePerCount(IncursionModifier object) {
            return object.value;
        }

        @Override
        public int getRemainingCount(IncursionModifier object) {
            return object.percent;
        }

        @Override
        public void setRemainingCount(IncursionModifier object, int count) {
            object.percent = count;
        }

        @Override
        public boolean canCombine(IncursionModifier object, IncursionModifier other) {
            return object.modifier == other.modifier;
        }

        @Override
        public void onCombine(IncursionModifier object, IncursionModifier other) {
            object.percent += other.percent;
        }

        @Override
        public IncursionModifier copy(IncursionModifier object, int amount) {
            return new IncursionModifier(object.modifier, object.isLevelModifier, object.invert, object.value, amount);
        }
    };

    public BiomeMissionIncursionData() {
    }

    public BiomeMissionIncursionData(float difficulty, IncursionBiome biome, int tabletTier) {
        this.difficulty = difficulty;
        this.biome = biome;
        this.tabletTier = tabletTier;
    }

    protected void initModifiers() {
        this.levelModifiers = new ArrayList();
        this.uniqueIncursionModifiers = new ArrayList();
        this.playerPersonalIncursionCompleteRewards = new LootItemList(new LootItemInterface[0]);
        this.playerSharedIncursionCompleteRewards = new LootItemList(new LootItemInterface[0]);
        float healthPercentIncreasePerTier = 25.0f;
        float damagePercentIncreasePerTier = 15.0f;
        float lootPercentIncreasePerTier = 15.0f;
        IncursionModifier tierHealthIncrease = new IncursionModifier(LevelModifiers.ENEMY_MAX_HEALTH, false, false, healthPercentIncreasePerTier * (float)(this.tabletTier - 1), 100);
        IncursionModifier tierDamageIncrease = new IncursionModifier(LevelModifiers.ENEMY_DAMAGE, false, false, damagePercentIncreasePerTier * (float)this.tabletTier, 100);
        IncursionModifier lootDropIncrease = new IncursionModifier(LevelModifiers.LOOT, false, false, lootPercentIncreasePerTier * (float)this.tabletTier, 100);
        this.levelModifiers.add(new ModifierValue<Float>(tierHealthIncrease.modifier, Float.valueOf(tierHealthIncrease.value / (float)tierHealthIncrease.percent)));
        this.levelModifiers.add(new ModifierValue<Float>(tierDamageIncrease.modifier, Float.valueOf(tierDamageIncrease.value / (float)tierDamageIncrease.percent)));
        this.levelModifiers.add(new ModifierValue<Float>(lootDropIncrease.modifier, Float.valueOf(lootDropIncrease.value / (float)tierDamageIncrease.percent)));
        int modifiersToAdd = MODIFIERS_TO_ADD_FROM_TIER_1;
        if (this.tabletTier >= 4) {
            modifiersToAdd = MODIFIERS_TO_ADD_FROM_TIER_4;
        }
        if (this.tabletTier >= 8) {
            modifiersToAdd = MODIFIERS_TO_ADD_FROM_TIER_8;
        }
        ArrayList<UniqueIncursionModifier> modifiersAlreadyUsed = new ArrayList<UniqueIncursionModifier>();
        for (int i = 0; i < modifiersToAdd; ++i) {
            GameRandom uniqueModifierRandom = new GameRandom(this.getUniqueID()).nextSeeded(20 * (i + 1) * this.getTabletTier());
            UniqueIncursionModifier newModifier = UniqueIncursionModifierRegistry.getRandomIncursionModifier(uniqueModifierRandom, modifiersAlreadyUsed);
            this.uniqueIncursionModifiers.add(newModifier);
            modifiersAlreadyUsed.add(newModifier);
            this.playerSharedIncursionCompleteRewards.add(UniqueIncursionRewardsRegistry.getSeededRandomReward(this, uniqueModifierRandom, this.getTabletTier()));
        }
        this.modifiersInitialized = true;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addFloat("difficulty", this.difficulty);
        save.addUnsafeString("biome", this.biome.getStringID());
        save.addInt("tabletTier", this.tabletTier);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.difficulty = save.getFloat("difficulty");
        String biomeStringID = save.getUnsafeString("biome");
        this.biome = IncursionBiomeRegistry.getBiome(biomeStringID);
        if (this.biome == null) {
            throw new LoadDataException("Could not find incursion biome with stringID " + biomeStringID);
        }
        this.tabletTier = save.getInt("tabletTier");
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        writer.putNextFloat(this.difficulty);
        writer.putNextShortUnsigned(this.biome.getID());
        writer.putNextInt(this.tabletTier);
    }

    @Override
    public void applyPacket(PacketReader reader) {
        super.applyPacket(reader);
        this.difficulty = reader.getNextFloat();
        int biomeID = reader.getNextShortUnsigned();
        this.biome = IncursionBiomeRegistry.getBiome(biomeID);
        if (this.biome == null) {
            new IllegalArgumentException("Could not find incursion biome with id " + biomeID).printStackTrace();
        }
        this.tabletTier = reader.getNextInt();
    }

    @Override
    public IncursionBiome getIncursionBiome() {
        return this.biome;
    }

    @Override
    public GameSprite getTabletSprite() {
        return this.biome.getTabletSprite();
    }

    @Override
    public void setTabletTier(int tier) {
        this.tabletTier = tier;
        this.modifiersInitialized = false;
    }

    @Override
    public int getTabletTier() {
        return this.tabletTier;
    }

    @Override
    public boolean isSameIncursion(IncursionData otherData) {
        if (otherData.getID() != this.getID()) {
            return false;
        }
        BiomeMissionIncursionData otherBiomeData = (BiomeMissionIncursionData)otherData;
        if (otherBiomeData.getUniqueID() != this.getUniqueID()) {
            return false;
        }
        if (otherBiomeData.difficulty != this.difficulty) {
            return false;
        }
        return otherBiomeData.biome.getID() == this.biome.getID();
    }

    @Override
    public void setUpDetails(FallenAltarContainer container, FallenAltarContainerForm form, FormContentBox content, boolean isOpen) {
        if (!this.modifiersInitialized) {
            this.initModifiers();
        }
        FormFlow flow = new FormFlow(12);
        int fontSize = 16;
        int headlineFontSize = 20;
        content.addComponent(flow.nextY(new FormLocalLabel(this.biome.getLocalization(), new FontOptions(headlineFontSize), 0, content.getMinContentWidth() / 2, 0, content.getMinContentWidth() - 10), 4));
        this.biome.setupTypeAndTierLabels(this, fontSize, content, flow);
        flow.next(14);
        FormFlow lootFlow = new FormFlow();
        Form lootContent = new Form(content.getMinContentWidth(), 0);
        lootContent.drawBase = false;
        lootContent.shouldLimitDrawArea = false;
        for (FairType fairType : this.getLoot(this, new FontOptions(fontSize))) {
            FormFairTypeLabel label = new FormFairTypeLabel("", lootContent.getWidth() / 2, 0);
            label.setMaxWidth(lootContent.getWidth() - 10);
            label.setCustomFairType(fairType);
            label.setTextAlign(FairType.TextAlign.CENTER);
            lootContent.addComponent(lootFlow.nextY(label, 2));
        }
        ArrayList<FairType> privateDrops = this.biome.getPrivateDropsDisplay(new FontOptions(fontSize));
        if (privateDrops != null) {
            for (FairType privateDrop : privateDrops) {
                FormFairTypeLabel label = new FormFairTypeLabel("", lootContent.getWidth() / 2, 0);
                label.setCustomFairType(privateDrop);
                label.setMaxWidth(lootContent.getWidth() - 10);
                label.setTextAlign(FairType.TextAlign.CENTER);
                lootContent.addComponent(lootFlow.nextY(label, 2));
            }
        }
        this.getAndSetupRewardLabels(this.getPlayerPersonalIncursionCompleteRewards(), fontSize, lootContent, lootFlow);
        this.getAndSetupRewardLabels(this.getPlayerSharedIncursionCompleteRewards(), fontSize, lootContent, lootFlow);
        lootContent.setHeight(lootFlow.next());
        if (!lootContent.isEmpty()) {
            content.addComponent(flow.nextY(new FormLocalLabel("ui", "incursionloot", new FontOptions(headlineFontSize), 0, content.getMinContentWidth() / 2, 0, content.getMinContentWidth() - 10), 2));
            content.addComponent(flow.nextY(lootContent, 14));
        }
        FormFlow modifiersFlow = new FormFlow();
        Form modifiersContent = new Form(content.getMinContentWidth(), 0);
        modifiersContent.drawBase = false;
        modifiersContent.shouldLimitDrawArea = false;
        if (!this.levelModifiers.isEmpty()) {
            ((Stream)this.levelModifiers.stream().flatMap(m -> m.getAllTooltips().stream()).sequential()).sorted(Comparator.comparingInt(t -> -t.sign)).forEach(tooltip -> {
                FormFairTypeLabel modifierLabel = new FormFairTypeLabel("", modifiersContent.getWidth() / 2, 0);
                modifierLabel.setFontOptions(new FontOptions(fontSize));
                modifierLabel.setMaxWidth(modifiersContent.getWidth() - 10);
                modifierLabel.setCustomFairType(tooltip.toFairType(modifierLabel.getFontOptions(), false));
                modifierLabel.setTextAlign(FairType.TextAlign.CENTER);
                modifierLabel.setColor(tooltip.getTextColor());
                modifiersContent.addComponent(modifiersFlow.nextY(modifierLabel, 2));
            });
        }
        for (UniqueIncursionModifier uniqueIncursionModifier : this.uniqueIncursionModifiers) {
            GameMessageBuilder modifiersBuilder = new GameMessageBuilder();
            FormFairTypeLabel uniqueModifierLabel = new FormFairTypeLabel(modifiersBuilder, modifiersContent.getWidth() / 2, 0);
            uniqueModifierLabel.setFontOptions(new FontOptions(fontSize));
            uniqueModifierLabel.setMaxWidth(modifiersContent.getWidth() - 10);
            uniqueModifierLabel.setParsers(TypeParsers.GAME_COLOR, TypeParsers.InputIcon(uniqueModifierLabel.getFontOptions()), TypeParsers.ItemIcon(fontSize), TypeParsers.MobIcon(fontSize));
            uniqueModifierLabel.setTextAlign(FairType.TextAlign.CENTER);
            uniqueModifierLabel.setText(uniqueIncursionModifier.getModifierDescription());
            uniqueModifierLabel.setColor(Settings.UI.incursionModifierOrange);
            modifiersContent.addComponent(modifiersFlow.nextY(uniqueModifierLabel, 4));
        }
        modifiersContent.setHeight(modifiersFlow.next());
        if (!modifiersContent.isEmpty()) {
            content.addComponent(flow.nextY(new FormLocalLabel("ui", "incursionmodifiers", new FontOptions(headlineFontSize), 0, content.getMinContentWidth() / 2, 0, content.getMinContentWidth() - 10), 2));
            content.addComponent(flow.nextY(modifiersContent, 14));
        }
        if (GlobalData.isDevMode() && GlobalData.debugCheatActive()) {
            flow.next(14);
            content.addComponent(flow.nextY(new FormLabel("Debug info:", new FontOptions(16), 0, content.getMinContentWidth() / 2, 0), 4));
            content.addComponent(flow.nextY(new FormLabel("UniqueID: " + this.getUniqueID(), new FontOptions(12), 0, content.getMinContentWidth() / 2, 0), 4));
            content.addComponent(flow.nextY(new FormLabel("Difficulty: " + this.difficulty, new FontOptions(12), 0, content.getMinContentWidth() / 2, 0), 4));
        }
        flow.next(2);
        content.setContentBox(new Rectangle(content.getWidth(), flow.next()));
        content.setScroll(0, 0);
    }

    protected abstract Iterable<FairType> getLoot(IncursionData var1, FontOptions var2);

    public void getAndSetupRewardLabels(IncursionRewardGetter rewardsChosen, int fontSize, Form content, FormFlow flow) {
        ArrayList<InventoryItem> rewardItems = rewardsChosen.getRewards(true);
        for (InventoryItem rewardItem : rewardItems) {
            String prefix;
            FormFairTypeLabel label = new FormFairTypeLabel("", content.getWidth() / 2, 0);
            GameBlackboard tooltipBlackboard = new GameBlackboard().set("hideModifierAndRewards", true);
            label.setParsers(TypeParsers.GAME_COLOR, TypeParsers.ItemIcon(fontSize, true, fairItemGlyph -> fairItemGlyph.setTooltipBlackboard(tooltipBlackboard)), TypeParsers.MobIcon(fontSize));
            label.setFontOptions(new FontOptions(fontSize));
            label.setMaxWidth(content.getWidth() - 10);
            int upToAmount = rewardItem.getGndData().getInt("incursionUpToAmount");
            String string = prefix = upToAmount > 1 ? "1-" + upToAmount + " " : "";
            if (rewardItem.item instanceof GatewayTabletItem) {
                String tier = Localization.translate("item", "tier", "tiernumber", (Object)GatewayTabletItem.getIncursionData(rewardItem).getTabletTier());
                label.setText(TypeParsers.getItemParseString(rewardItem) + " " + prefix + tier + " " + GatewayTabletItem.getIncursionData((InventoryItem)rewardItem).getIncursionBiome().displayName.translate());
            } else if (rewardItem.item instanceof TrinketItem) {
                label.setText(TypeParsers.getItemParseString(rewardItem) + " " + prefix + Localization.translate("item", rewardItem.item.getStringID()));
            } else if (rewardItem.item instanceof ToolItem || rewardItem.item instanceof ArmorItem) {
                int itemTier = (int)rewardItem.item.getUpgradeTier(rewardItem);
                if (itemTier > 0) {
                    String tier = Localization.translate("item", "tier", "tiernumber", (Object)itemTier);
                    label.setText(TypeParsers.getItemParseString(rewardItem) + " " + prefix + tier + " " + Localization.translate("item", rewardItem.item.getStringID()));
                } else {
                    label.setText(TypeParsers.getItemParseString(rewardItem) + " " + prefix + Localization.translate("item", rewardItem.item.getStringID()));
                }
            } else {
                if (rewardItem.item instanceof CoinItem) {
                    rewardItem.getGndData().setBoolean("showCoinStackIcon", true);
                    label.setText(prefix + TypeParsers.getItemParseString(rewardItem) + " " + rewardItem.getItemDisplayName());
                }
                label.setText(TypeParsers.getItemParseString(rewardItem) + " " + prefix + rewardItem.getItemDisplayName());
            }
            label.setTextAlign(FairType.TextAlign.CENTER);
            content.addComponent(flow.nextY(label, 2));
            label.setColor(Settings.UI.incursionModifierOrange);
        }
    }

    public int getLootCount() {
        int counter = 0;
        for (FairType loot : this.getLoot(this, new FontOptions(0))) {
            ++counter;
        }
        ArrayList<FairType> privateDropsDisplay = this.biome.getPrivateDropsDisplay(new FontOptions(0));
        if (privateDropsDisplay != null) {
            counter += privateDropsDisplay.size();
        }
        return counter;
    }

    protected FairType getItemMessage(InventoryItem item, FontOptions fontOptions) {
        FairType fairType = new FairType();
        fairType.append(new FairItemGlyph(fontOptions.getSize(), item));
        fairType.append(fontOptions, item.getItemDisplayName());
        return fairType;
    }

    protected FairType getItemMessage(Item item, FontOptions fontOptions) {
        return this.getItemMessage(new InventoryItem(item), fontOptions);
    }

    @Override
    public GameTooltips getOpenButtonTooltips(FallenAltarContainer container) {
        return null;
    }

    @Override
    public String getCanOpenError(FallenAltarContainer container) {
        return this.biome.getCanOpenError(this, container);
    }

    @Override
    public void onOpened(FallenAltarContainer container, ServerClient client) {
        if (client != null) {
            client.newStats.opened_incursions.add(this);
        }
    }

    @Override
    public void onCompleted(ServerClient client) {
        if (client != null) {
            client.newStats.completed_incursions.add(this);
        }
    }

    @Override
    public void onClosed(FallenAltarObjectEntity altar, ServerClient client) {
    }

    @Override
    public IncursionLevel getNewIncursionLevel(FallenAltarObjectEntity altar, LevelIdentifier identifier, Server server, WorldEntity worldEntity, AltarData altarData) {
        return this.biome.getNewIncursionLevel(altar, identifier, this, server, worldEntity, altarData);
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultLevelModifiers() {
        if (!this.modifiersInitialized) {
            this.initModifiers();
        }
        return this.levelModifiers.stream();
    }

    @Override
    public ArrayList<UniqueIncursionModifier> getUniqueIncursionModifiers() {
        if (!this.modifiersInitialized) {
            this.initModifiers();
        }
        return this.uniqueIncursionModifiers;
    }

    @Override
    public IncursionRewardGetter getPlayerPersonalIncursionCompleteRewards() {
        if (!this.modifiersInitialized) {
            this.initModifiers();
        }
        return isForDisplayOnly -> {
            GameRandom lootRandom = new GameRandom(this.getUniqueID()).nextSeeded(77 * this.getTabletTier());
            ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
            this.playerPersonalIncursionCompleteRewards.addItems(items, lootRandom, 1.0f, isForDisplayOnly ? "displayOnly" : null);
            return items;
        };
    }

    @Override
    public IncursionRewardGetter getPlayerSharedIncursionCompleteRewards() {
        if (!this.modifiersInitialized) {
            this.initModifiers();
        }
        return isForDisplayOnly -> {
            GameRandom lootRandom = new GameRandom(this.getUniqueID()).nextSeeded(155 * this.getTabletTier());
            ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
            this.playerSharedIncursionCompleteRewards.addItems(items, lootRandom, 1.0f, isForDisplayOnly ? "displayOnly" : null);
            return items;
        };
    }

    private static class IncursionModifier {
        public final Modifier<Float> modifier;
        public boolean isLevelModifier;
        public boolean invert;
        public float value;
        public int percent;

        public IncursionModifier(Modifier<Float> modifier, boolean isLevelModifier, boolean invert, float value, int percent) {
            this.modifier = modifier;
            this.isLevelModifier = isLevelModifier;
            this.invert = invert;
            this.value = value;
            this.percent = percent;
        }

        public void apply(Collection<ModifierValue<?>> levelModifiers, Collection<ModifierValue<?>> playerModifiers) {
            float modifierValue = (float)this.percent / 100.0f;
            if (this.invert) {
                modifierValue = -modifierValue;
            }
            if (this.isLevelModifier) {
                levelModifiers.add(new ModifierValue<Float>(this.modifier, Float.valueOf(modifierValue)));
            } else {
                playerModifiers.add(new ModifierValue<Float>(this.modifier, Float.valueOf(modifierValue)));
            }
        }
    }
}

