/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import necesse.engine.incursionPerkTree.IncursionAndTierRequirement;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.IncursionData;

public class IncursionPerk
implements IDDataContainer {
    public final IDData idData = new IDData();
    public static final int respecCost = 1000;
    public final int tier;
    public final int perkCost;
    public final int xPositionOnPerkTree;
    public boolean affectsCurrentIncursion;
    public final Ingredient[] buyPerkIngredientCost;
    public final Ingredient[] respecIngredientCost;
    public final ArrayList<IncursionPerk> prerequisitePerksRequired = new ArrayList();
    public final ArrayList<IncursionPerk> otherPerksThatLockThisPerk = new ArrayList();
    public IncursionAndTierRequirement incursionAndTierRequirement = null;
    public GameTexture iconTexture;
    public boolean highlightLock = false;
    protected GameMessage customName;

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    @Override
    public String getStringID() {
        return this.idData.getStringID();
    }

    @Override
    public int getID() {
        return this.idData.getID();
    }

    public IncursionPerk(int tier, int perkCost, int xPositionOnPerkTree, boolean affectsCurrentIncursion, IncursionPerk ... prerequisitePerkRequired) {
        this.tier = tier;
        this.perkCost = perkCost;
        this.xPositionOnPerkTree = xPositionOnPerkTree;
        this.affectsCurrentIncursion = affectsCurrentIncursion;
        this.buyPerkIngredientCost = new Ingredient[]{new Ingredient("altardust", perkCost)};
        this.respecIngredientCost = new Ingredient[]{new Ingredient("coin", 1000)};
        if (prerequisitePerkRequired != null) {
            this.prerequisitePerksRequired.addAll(Arrays.asList(prerequisitePerkRequired));
        }
    }

    public void loadTextures() {
        try {
            this.iconTexture = GameTexture.fromFileRaw("incursionperks/" + this.getStringID());
        }
        catch (FileNotFoundException e) {
            this.iconTexture = GameTexture.fromFile("incursionperks/" + this.getStringID());
        }
    }

    public void onChallengeRegistryClosed() {
    }

    public IncursionPerk setCustomName(GameMessage name) {
        if (JournalChallengeRegistry.instance.isClosed()) {
            throw new IllegalStateException("Cannot change perk name after registry has closed");
        }
        this.customName = name;
        return this;
    }

    public GameMessage getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return new LocalMessage("incursion", this.getStringID());
    }

    public String getCustomTooltipLocalization() {
        return null;
    }

    public ListGameTooltips getTooltips(AltarData altarData, FallenAltarContainer altarContainer) {
        Ingredient ingredient;
        ListGameTooltips tooltips = new ListGameTooltips();
        Color purple = new Color(184, 69, 227);
        Color cyan = new Color(58, 211, 176);
        Color red = new Color(236, 41, 41);
        String category = "incursion";
        int maxWidth = 350;
        String titleText = Localization.translate("incursion", this.getStringID());
        tooltips.add(new StringTooltips(titleText, purple, maxWidth));
        String translationKey = this.getStringID() + "desc";
        String tooltipDescription = Localization.translate(category, translationKey);
        if (this.getCustomTooltipLocalization() != null) {
            tooltipDescription = this.getCustomTooltipLocalization();
        }
        tooltips.add(new StringTooltips(tooltipDescription, cyan, maxWidth));
        if (!this.hasObtainedPerk(altarData)) {
            tooltips.add("\n");
            if (altarData.getAltarTier() + 1 < this.tier) {
                tooltips.add(new StringTooltips(Localization.translate(category, "perkrequireshighertier", "tier", (Object)(this.tier - 1)), red, maxWidth));
            } else if (this.perkIsLockedByAPerkLockingAllPerksOnTier(altarData)) {
                tooltips.add(new StringTooltips(Localization.translate(category, "requiretabletperk"), red, maxWidth));
                tooltips.add(new StringTooltips(Localization.translate(category, this.getPerkLockingAllPerksOnTier(this.tier).getStringID()), red, maxWidth));
            } else if (this.isPerkLocked(altarData)) {
                for (IncursionPerk perk : this.otherPerksThatLockThisPerk) {
                    if (!altarData.obtainedPerkIDs.contains(perk.getID())) continue;
                    tooltips.add(new StringTooltips(Localization.translate(category, "perkislocked"), red, maxWidth));
                    tooltips.add(new StringTooltips(Localization.translate("incursion", perk.getStringID()), red, maxWidth));
                }
            } else if (!this.hasPrerequisitePerks(altarData)) {
                if (this.prerequisitePerksRequired.size() == 1) {
                    tooltips.add(new StringTooltips(Localization.translate(category, "requireprerequisiteperk"), red, maxWidth));
                    tooltips.add(new StringTooltips(Localization.translate("incursion", this.prerequisitePerksRequired.get(0).getStringID()), red, maxWidth));
                } else {
                    tooltips.add(new StringTooltips(Localization.translate(category, "requireprerequisiteperks"), red, maxWidth));
                    for (IncursionPerk currentPerk : this.prerequisitePerksRequired) {
                        StringTooltips perkLockingThisPerkTitle = new StringTooltips(Localization.translate("incursion", currentPerk.getStringID()), red, maxWidth);
                        tooltips.add(perkLockingThisPerkTitle);
                    }
                }
            } else if (!this.hasSpecificIncursionAtTierCompleted(altarData)) {
                tooltips.add(new StringTooltips(Localization.translate(category, "requires"), red, maxWidth));
                if (this.incursionAndTierRequirement.incursionBiome != null) {
                    tooltips.add(new StringTooltips(Localization.translate(category, "completespecificincursionatspecifictier", "tier", this.incursionAndTierRequirement.tier, category, this.incursionAndTierRequirement.incursionBiome.displayName), red, maxWidth));
                } else {
                    int amountOfIncursionsStillRequiresToComplete = this.incursionAndTierRequirement.amount - altarData.altarStats.completed_incursions.getTotalTiersAbove(this.incursionAndTierRequirement.tier, true);
                    if (amountOfIncursionsStillRequiresToComplete == 1) {
                        tooltips.add(new StringTooltips(Localization.translate(category, "completeamountofincursionsatspecifictiersingular", "tier", (Object)this.incursionAndTierRequirement.tier), red, maxWidth));
                    } else {
                        tooltips.add(new StringTooltips(Localization.translate(category, "completeamountofincursionsatspecifictierplural", "tier", this.incursionAndTierRequirement.tier, "amount", amountOfIncursionsStillRequiresToComplete), red, maxWidth));
                    }
                }
            } else {
                if (!this.otherPerksThatLockThisPerk.isEmpty()) {
                    tooltips.add(new StringTooltips(Localization.translate(category, "thisperklocksotherperk"), cyan, 350));
                    for (IncursionPerk otherPerksThatLockThisPerk : this.otherPerksThatLockThisPerk) {
                        tooltips.add(new StringTooltips(Localization.translate(category, otherPerksThatLockThisPerk.getStringID()), purple));
                    }
                    tooltips.add("\n");
                }
                tooltips.add(Localization.translate("misc", "recipecostsing"));
                CanCraft canCraft = altarContainer.canAffordPerk(this);
                for (int i = 0; i < this.buyPerkIngredientCost.length; ++i) {
                    ingredient = this.buyPerkIngredientCost[i];
                    tooltips.add(ingredient.getTooltips(canCraft.haveIngredients[i], true));
                }
            }
        }
        if (this.canObtainPerk(altarData) && this.canAffordToBuyPerk(altarContainer)) {
            String buyTip = Localization.translate(category, "buyperk");
            if (Input.lastInputIsController) {
                tooltips.add(new InputTooltip(ControllerInput.MENU_SELECT, buyTip));
            } else {
                tooltips.add(new InputTooltip(-100, buyTip));
            }
        }
        if (this.canRespecPerk(altarData)) {
            tooltips.add("\n");
            tooltips.add(Localization.translate("incursion", "removeperk"));
            CanCraft canCraft = altarContainer.canAffordPerkRespec(this);
            for (int i = 0; i < this.respecIngredientCost.length; ++i) {
                ingredient = this.respecIngredientCost[i];
                tooltips.add(ingredient.getTooltips(canCraft.haveIngredients[i], true));
            }
            if (this.canAffordToRespecPerk(altarContainer)) {
                String controlTip = Localization.translate(category, "respecperk", "value", (Object)this.perkCost);
                if (Input.lastInputIsController) {
                    tooltips.add(new InputTooltip(ControllerInput.MENU_BACK, controlTip));
                } else {
                    tooltips.add(new InputTooltip(-99, controlTip));
                }
            }
        }
        return tooltips;
    }

    public boolean isPerkLocked(AltarData altarData) {
        if (this.otherPerksThatLockThisPerk.isEmpty()) {
            return false;
        }
        for (IncursionPerk perk : this.otherPerksThatLockThisPerk) {
            if (!altarData.obtainedPerkIDs.contains(perk.getID())) continue;
            return true;
        }
        return false;
    }

    public boolean isPerkOverwritten(AltarData altarData) {
        return false;
    }

    public boolean canObtainPerk(AltarData altarData) {
        if (this.hasObtainedPerk(altarData)) {
            return false;
        }
        if (!this.hasSpecificIncursionAtTierCompleted(altarData)) {
            return false;
        }
        if (this.perkIsLockedByAPerkLockingAllPerksOnTier(altarData)) {
            return false;
        }
        if (altarData.getAltarTier() + 1 < this.tier) {
            return false;
        }
        if (!this.hasPrerequisitePerks(altarData)) {
            return false;
        }
        return !this.isPerkLocked(altarData);
    }

    public boolean canAffordToBuyPerk(FallenAltarContainer altarContainer) {
        return altarContainer.canAffordPerk(this).canCraft();
    }

    public boolean canRespecPerk(AltarData altarData) {
        if (!this.hasObtainedPerk(altarData)) {
            return false;
        }
        for (Integer obtainedPerkID : altarData.obtainedPerkIDs) {
            IncursionPerk currentPerk = IncursionPerksRegistry.getPerk(obtainedPerkID);
            if (this.locksAllOtherPerksOnTier() && currentPerk != this && currentPerk.tier == this.tier) {
                return false;
            }
            if (!currentPerk.prerequisitePerksRequired.contains(this)) continue;
            boolean areThereOtherPrerequisitePerksAvailable = false;
            for (IncursionPerk perk : currentPerk.prerequisitePerksRequired) {
                if (!perk.hasObtainedPerk(altarData) || perk == this) continue;
                areThereOtherPrerequisitePerksAvailable = true;
            }
            if (areThereOtherPrerequisitePerksAvailable) continue;
            return false;
        }
        return altarData.getAltarTierAfterPerkRespec(this) < this.tier || altarData.areTherePerksLeftAfterRespecOnThisTier(this, this.tier);
    }

    public boolean canAffordToRespecPerk(FallenAltarContainer altarContainer) {
        return altarContainer.canAffordPerkRespec(this).canCraft();
    }

    public boolean hasObtainedPerk(AltarData altarData) {
        return altarData.obtainedPerkIDs.contains(this.getID());
    }

    public boolean hasPrerequisitePerks(AltarData altarData) {
        if (!this.prerequisitePerksRequired.isEmpty()) {
            for (IncursionPerk incursionPerk : this.prerequisitePerksRequired) {
                if (!altarData.obtainedPerkIDs.contains(incursionPerk.getID())) continue;
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    public boolean hasSpecificIncursionAtTierCompleted(AltarData altarData) {
        return true;
    }

    public int getTabletIDForTabletDropPerks() {
        return -1;
    }

    public boolean locksAllOtherPerksOnTier() {
        return false;
    }

    public boolean perkIsLockedByAPerkLockingAllPerksOnTier(AltarData altarData) {
        IncursionPerk perkLockingAllPerksOnTier = this.getPerkLockingAllPerksOnTier(this.tier);
        if (perkLockingAllPerksOnTier == null) {
            return false;
        }
        return !altarData.hasPerk(perkLockingAllPerksOnTier);
    }

    public IncursionPerk getPerkLockingAllPerksOnTier(int tier) {
        for (IncursionPerk perk : IncursionPerksRegistry.getPerksAtTier(tier)) {
            if (!perk.locksAllOtherPerksOnTier() || perk == this) continue;
            return perk;
        }
        return null;
    }

    public int setMaxEquipmentRewardTier() {
        return 3;
    }

    public void onIncursionBossPortalClicked(IncursionLevel level) {
    }

    public void onIncursionLevelAboutToGenerate(IncursionLevel level, AltarData altarData, int modifierIndex) {
    }

    public void onIncursionLevelGenerated(IncursionLevel level, AltarData altarData, int modifierIndex) {
    }

    public void onIncursionLevelCompleted(IncursionLevel level, AltarData altarData, int modifierIndex) {
    }

    public void onIncursionStructuresGenerated(PresetGeneration presets, GameRandom random, Biome biome) {
    }

    public void onGenerateUpgradeAndAlchemyVeins(CaveGeneration cg, String upgradeShardID, String alchemyShardID, GameRandom random) {
    }

    public TicketSystemList<LootItemInterface> onGenerateTabletRewards(TicketSystemList<LootItemInterface> ticketedRewards, GameRandom seededRandom, int tier, IncursionData incursionData) {
        return ticketedRewards;
    }

    public Stream<ModifierValue<?>> getIncursionDataModifiers() {
        return Stream.empty();
    }
}

