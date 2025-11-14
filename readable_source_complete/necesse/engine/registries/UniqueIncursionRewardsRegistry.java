/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.GameLog;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.UpgradeTierLootItem;
import necesse.inventory.lootTable.presets.IncursionLootLists;
import necesse.inventory.lootTable.presets.PetsLootTable;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.UniqueIncursionReward;

public class UniqueIncursionRewardsRegistry
extends GameRegistry<UniqueIncursionReward> {
    public static final UniqueIncursionRewardsRegistry instance = new UniqueIncursionRewardsRegistry();
    public static int closeRangeWeapons;
    public static int greatswordWeapons;
    public static int spearWeapons;
    public static int glaiveWeapons;
    public static int bowWeapons;
    public static int greatBowWeapons;
    public static int gunWeapons;
    public static int magicWeapons;
    public static int throwWeapons;
    public static int summonWeapons;
    public static int incursionCloseRangeWeapons;
    public static int incursionGreatswordWeapons;
    public static int incursionSpearWeapons;
    public static int incursionGlaiveWeapons;
    public static int incursionBowWeapons;
    public static int incursionGreatBowWeapons;
    public static int incursionGunWeapons;
    public static int incursionMagicWeapons;
    public static int incursionThrowWeapons;
    public static int incursionSummonWeapons;
    public static int tools;
    public static int headArmors;
    public static int bodyArmors;
    public static int feetArmors;
    public static int trinkets;
    public static int armorSets;
    public static int incursionHeadArmors;
    public static int incursionBodyArmors;
    public static int incursionFeetArmors;
    public static int incursionTrinkets;
    public static int incursionArmorSets;
    public static int rareIncursionWeapons;
    public static int rareIncursionTrinkets;
    public static int rareIncursionArmorSets;
    public static int cosmeticArmorSets;
    public static int pets;

    private UniqueIncursionRewardsRegistry() {
        super("UniqueIncursionReward", 32766);
    }

    @Override
    public void registerCore() {
        UniqueIncursionRewardsRegistry.registerCloseRangeWeapon("closerangeweapon", new UniqueIncursionReward(LootTablePresets.closeRangeWeapons));
        UniqueIncursionRewardsRegistry.registerIncursionCloseRangeWeapon("incursioncloserangeweapon", new UniqueIncursionReward(LootTablePresets.incursionCloseRangeWeapons));
        UniqueIncursionRewardsRegistry.registerGreatswordWeapon("greatsword", new UniqueIncursionReward(LootTablePresets.greatswordWeapons));
        UniqueIncursionRewardsRegistry.registerIncursionGreatswordWeapon("incursiongreatsword", new UniqueIncursionReward(LootTablePresets.incursionGreatswordWeapons));
        UniqueIncursionRewardsRegistry.registerSpearWeapon("spear", new UniqueIncursionReward(LootTablePresets.spearWeapons));
        UniqueIncursionRewardsRegistry.registerIncursionSpearWeapon("incursionspear", new UniqueIncursionReward(LootTablePresets.incursionSpearWeapons));
        UniqueIncursionRewardsRegistry.registerGlaiveWeapon("glaive", new UniqueIncursionReward(LootTablePresets.glaiveWeapons));
        UniqueIncursionRewardsRegistry.registerIncursionGlaiveWeapon("incursionglaive", new UniqueIncursionReward(LootTablePresets.incursionGlaiveWeapons));
        UniqueIncursionRewardsRegistry.registerBowWeapon("bow", new UniqueIncursionReward(LootTablePresets.bowWeapons));
        UniqueIncursionRewardsRegistry.registerIncursionBowWeapon("incursionbow", new UniqueIncursionReward(LootTablePresets.incursionBowWeapons));
        UniqueIncursionRewardsRegistry.registerGreatBowWeapon("greatbow", new UniqueIncursionReward(LootTablePresets.greatBowWeapons));
        UniqueIncursionRewardsRegistry.registerIncursionGreatBowWeapon("incursiongreatbow", new UniqueIncursionReward(LootTablePresets.incursionGreatBowWeapons));
        UniqueIncursionRewardsRegistry.registerGunWeapon("gun", new UniqueIncursionReward(LootTablePresets.gunWeapons));
        UniqueIncursionRewardsRegistry.registerGunWeapon("incursiongun", new UniqueIncursionReward(LootTablePresets.incursionGunWeapons));
        UniqueIncursionRewardsRegistry.registerMagicWeapon("magicweapon", new UniqueIncursionReward(LootTablePresets.magicWeapons));
        UniqueIncursionRewardsRegistry.registerIncursionMagicWeapon("incursionmagicweapon", new UniqueIncursionReward(LootTablePresets.incursionMagicWeapons));
        UniqueIncursionRewardsRegistry.registerThrowWeapon("throwingweapon", new UniqueIncursionReward(LootTablePresets.throwWeapons));
        UniqueIncursionRewardsRegistry.registerIncursionThrowWeapon("incursionthrowingweapon", new UniqueIncursionReward(LootTablePresets.incursionThrowWeapons));
        UniqueIncursionRewardsRegistry.registerSummonWeapon("summonweapon", new UniqueIncursionReward(LootTablePresets.summonWeapons));
        UniqueIncursionRewardsRegistry.registerIncursionSummonWeapon("incursionsummonweapon", new UniqueIncursionReward(LootTablePresets.incursionSummonWeapons));
        UniqueIncursionRewardsRegistry.registerTools("tools", new UniqueIncursionReward(LootTablePresets.tools));
        UniqueIncursionRewardsRegistry.registerHeadArmors("headarmor", new UniqueIncursionReward(LootTablePresets.headArmor));
        UniqueIncursionRewardsRegistry.registerBodyArmors("bodyarmor", new UniqueIncursionReward(LootTablePresets.bodyArmor));
        UniqueIncursionRewardsRegistry.registerFeetArmors("feetarmor", new UniqueIncursionReward(LootTablePresets.feetArmor));
        UniqueIncursionRewardsRegistry.registerTrinkets("trinkets", new UniqueIncursionReward(LootTablePresets.trinkets));
        UniqueIncursionRewardsRegistry.registerArmorSets("armorsets", new UniqueIncursionReward(LootTablePresets.armorSets));
        UniqueIncursionRewardsRegistry.registerIncursionHeadArmors("incursionheadarmor", new UniqueIncursionReward(LootTablePresets.incursionHeadArmor));
        UniqueIncursionRewardsRegistry.registerIncursionBodyArmors("incursionbodyarmor", new UniqueIncursionReward(LootTablePresets.incursionBodyArmor));
        UniqueIncursionRewardsRegistry.registerIncursionFeetArmors("incursionfeetarmor", new UniqueIncursionReward(LootTablePresets.incursionFeetArmor));
        UniqueIncursionRewardsRegistry.registerIncursionTrinkets("incursiontrinkets", new UniqueIncursionReward(LootTablePresets.incursionTrinkets));
        UniqueIncursionRewardsRegistry.registerIncursionArmorSets("incursionarmorsets", new UniqueIncursionReward(LootTablePresets.incursionArmorSets));
        UniqueIncursionRewardsRegistry.registerRareIncursionWeapons("rareincursionweapons", new UniqueIncursionReward(LootTablePresets.rareIncursionWeapons));
        UniqueIncursionRewardsRegistry.registerRareIncursionTrinkets("rareincursiontrinkets", new UniqueIncursionReward(LootTablePresets.rareIncursionTrinkets));
        UniqueIncursionRewardsRegistry.registerRareIncursionArmor("rareincursionarmorsets", new UniqueIncursionReward(LootTablePresets.rareIncursionArmorSets));
        UniqueIncursionRewardsRegistry.registerCosmeticArmorSets("cosmeticarmorsets", new UniqueIncursionReward(LootTablePresets.cosmeticArmorSets));
        UniqueIncursionRewardsRegistry.registerPets("pets", new UniqueIncursionReward(PetsLootTable.petsLootTable));
    }

    @Override
    protected void onRegister(UniqueIncursionReward object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
    }

    public static List<UniqueIncursionReward> getUniqueIncursionRewards() {
        return instance.streamElements().collect(Collectors.toList());
    }

    public static int registerReward(String stringID, UniqueIncursionReward modifier) {
        return instance.register(stringID, modifier);
    }

    public static int registerCloseRangeWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        closeRangeWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionCloseRangeWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionCloseRangeWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerGreatswordWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        greatswordWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionGreatswordWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionGreatswordWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerSpearWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        spearWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionSpearWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionSpearWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerGlaiveWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        glaiveWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionGlaiveWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionGlaiveWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerBowWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        bowWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionBowWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionBowWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerGreatBowWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        greatBowWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionGreatBowWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionGreatBowWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerGunWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        gunWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionGunWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionGunWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerMagicWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        magicWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionMagicWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionMagicWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerThrowWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        throwWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionThrowWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionThrowWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerSummonWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        summonWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionSummonWeapon(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionSummonWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerTools(String stringID, UniqueIncursionReward modifier) {
        int id;
        tools = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerHeadArmors(String stringID, UniqueIncursionReward modifier) {
        int id;
        headArmors = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerBodyArmors(String stringID, UniqueIncursionReward modifier) {
        int id;
        bodyArmors = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerFeetArmors(String stringID, UniqueIncursionReward modifier) {
        int id;
        feetArmors = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerTrinkets(String stringID, UniqueIncursionReward modifier) {
        int id;
        trinkets = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerArmorSets(String stringID, UniqueIncursionReward modifier) {
        int id;
        armorSets = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionHeadArmors(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionHeadArmors = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionBodyArmors(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionBodyArmors = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionFeetArmors(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionFeetArmors = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionTrinkets(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionTrinkets = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerIncursionArmorSets(String stringID, UniqueIncursionReward modifier) {
        int id;
        incursionArmorSets = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerRareIncursionWeapons(String stringID, UniqueIncursionReward modifier) {
        int id;
        rareIncursionWeapons = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerRareIncursionTrinkets(String stringID, UniqueIncursionReward modifier) {
        int id;
        rareIncursionTrinkets = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerRareIncursionArmor(String stringID, UniqueIncursionReward modifier) {
        int id;
        rareIncursionArmorSets = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerCosmeticArmorSets(String stringID, UniqueIncursionReward modifier) {
        int id;
        cosmeticArmorSets = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static int registerPets(String stringID, UniqueIncursionReward modifier) {
        int id;
        pets = id = UniqueIncursionRewardsRegistry.registerReward(stringID, modifier);
        return id;
    }

    public static LootItemInterface getRandomTierXBiomeWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(closeRangeWeapons, greatswordWeapons, spearWeapons, glaiveWeapons, bowWeapons, greatBowWeapons, gunWeapons, magicWeapons, throwWeapons, summonWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXIncursionWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(incursionCloseRangeWeapons, incursionGreatswordWeapons, incursionGlaiveWeapons, incursionBowWeapons, incursionSpearWeapons, incursionGreatBowWeapons, incursionGunWeapons, incursionMagicWeapons, incursionThrowWeapons, incursionSummonWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXBiomeAndIncursionWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(closeRangeWeapons, greatswordWeapons, spearWeapons, glaiveWeapons, bowWeapons, greatBowWeapons, gunWeapons, magicWeapons, throwWeapons, summonWeapons, incursionCloseRangeWeapons, incursionGreatswordWeapons, incursionGlaiveWeapons, incursionBowWeapons, incursionSpearWeapons, incursionGreatBowWeapons, incursionGunWeapons, incursionMagicWeapons, incursionThrowWeapons, incursionSummonWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXBiomeMeleeWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(closeRangeWeapons, greatswordWeapons, glaiveWeapons, throwWeapons, spearWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXIncursionMeleeWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(incursionCloseRangeWeapons, incursionGreatswordWeapons, incursionGlaiveWeapons, incursionThrowWeapons, incursionSpearWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXBiomeAndIncursionMeleeWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(closeRangeWeapons, greatswordWeapons, glaiveWeapons, throwWeapons, spearWeapons, incursionCloseRangeWeapons, incursionGreatswordWeapons, incursionGlaiveWeapons, incursionThrowWeapons, incursionSpearWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXBiomeRangeWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(bowWeapons, gunWeapons, greatBowWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXIncursionRangeWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(incursionBowWeapons, incursionGunWeapons, incursionGreatBowWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXBiomeAndIncursionRangeWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(bowWeapons, gunWeapons, greatBowWeapons, incursionBowWeapons, incursionGunWeapons, incursionGreatBowWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXBiomeSummonWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(summonWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXIncursionSummonWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(incursionSummonWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXBiomeAndIncursionSummonWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(summonWeapons, incursionSummonWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXBiomeMagicWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(magicWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXIncursionMagicWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(magicWeapons, incursionMagicWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXBiomeAndIncursionMagicWeaponReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(magicWeapons, incursionMagicWeapons));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXBiomeArmorSetReward() {
        UniqueIncursionReward armorSet = (UniqueIncursionReward)instance.getElement(armorSets);
        return armorSet.lootItemList;
    }

    public static LootItemInterface getRandomTierXIncursionArmorSetReward() {
        UniqueIncursionReward incursionArmorSet = (UniqueIncursionReward)instance.getElement(incursionArmorSets);
        return incursionArmorSet.lootItemList;
    }

    public static LootItemInterface getRandomTierXBiomeAndIncursionArmorSetReward(GameRandom seededRandom) {
        UniqueIncursionReward incursionArmorSet = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(armorSets, incursionArmorSets));
        return incursionArmorSet.lootItemList;
    }

    public static LootItemInterface getRandomCosmeticArmorSetReward() {
        UniqueIncursionReward cosmeticArmorSet = (UniqueIncursionReward)instance.getElement(cosmeticArmorSets);
        return cosmeticArmorSet.lootItemList;
    }

    public static LootItemInterface getRandomPetReward() {
        UniqueIncursionReward petsReward = (UniqueIncursionReward)instance.getElement(pets);
        return petsReward.lootItemList;
    }

    public static LootItemInterface getSeededRandomTierXGatewayTablets(int tabletDropAmount, final int tier, final IncursionData incursionData) {
        LootItemList lootList = new LootItemList(new LootItemInterface[0]);
        for (int i = 0; i < tabletDropAmount; ++i) {
            lootList.add(new LootItemInterface(){

                @Override
                public void addPossibleLoot(LootList list, Object ... extra) {
                    list.add("gatewaytablet");
                }

                @Override
                public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
                    InventoryItem tablet = new InventoryItem("gatewaytablet");
                    GatewayTabletItem.initializeGatewayTablet(tablet, random, tier, incursionData);
                    list.add(tablet);
                }
            });
        }
        return lootList;
    }

    public static LootItemInterface getCoinsReward(int multiplier, int tier) {
        LootItemList lootList = new LootItemList(new LootItemInterface[0]);
        int baseAmount = 2000;
        int minPerTier = 400;
        int maxPerTier = 600;
        lootList.add(LootItem.between("coin", (baseAmount + minPerTier * tier) * multiplier, (baseAmount + maxPerTier * tier) * multiplier));
        return lootList;
    }

    public static LootItemInterface getUpgradeShardsReward(int tier) {
        LootItemList lootList = new LootItemList(new LootItemInterface[0]);
        int baseAmount = 100;
        int minPerTier = 25;
        int maxPerTier = 50;
        lootList.add(LootItem.between("upgradeshard", baseAmount + minPerTier * tier, baseAmount + maxPerTier * tier));
        return lootList;
    }

    public static LootItemInterface getAlchemyShardsReward(int tier) {
        LootItemList lootList = new LootItemList(new LootItemInterface[0]);
        int baseAmount = 100;
        int minPerTier = 25;
        int maxPerTier = 50;
        lootList.add(LootItem.between("alchemyshard", baseAmount + minPerTier * tier, baseAmount + maxPerTier * tier));
        return lootList;
    }

    public static LootItemInterface getAltarDustReward(int tier) {
        LootItemList lootList = new LootItemList(new LootItemInterface[0]);
        int baseAmount = 50;
        int minPerTier = 15;
        int maxPerTier = 30;
        lootList.add(LootItem.between("altardust", baseAmount + minPerTier * tier, baseAmount + maxPerTier * tier));
        return lootList;
    }

    public static LootItemInterface getTierOneEssencesReward(GameRandom seededRandom, int tier) {
        LootItemList lootList = new LootItemList(new LootItemInterface[0]);
        int baseAmount = 14;
        int minPerTier = 4;
        int maxPerTier = 8;
        LootItem randomEssence = (LootItem)seededRandom.getOneOf(IncursionLootLists.tierOneEssences);
        lootList.add(LootItem.between(randomEssence.itemStringID, baseAmount + minPerTier * tier, baseAmount + maxPerTier * tier));
        return lootList;
    }

    public static LootItemInterface getTierTwoEssencesReward(GameRandom seededRandom, int tier) {
        LootItemList lootList = new LootItemList(new LootItemInterface[0]);
        int baseAmount = 10;
        int minPerTier = 3;
        int maxPerTier = 6;
        OneOfLootItems essencesBasedOnTiers = new OneOfLootItems(new LootItemInterface[0]);
        essencesBasedOnTiers.add(new LootItem("slimeessence"));
        if (tier >= IncursionPerksRegistry.GRAVEYARD_TABLET_CAN_DROP.tier) {
            essencesBasedOnTiers.add(new LootItem("bloodessence"));
            if (tier >= IncursionPerksRegistry.SPIDER_CASTLE_TABLET_CAN_DROP.tier) {
                essencesBasedOnTiers.add(new LootItem("spideressence"));
                if (tier >= IncursionPerksRegistry.CRYSTAL_HOLLOW_TABLET_CAN_DROP.tier) {
                    essencesBasedOnTiers.add(new LootItem("omnicrystal"));
                }
            }
        }
        LootItem randomEssence = (LootItem)seededRandom.getOneOf(essencesBasedOnTiers);
        lootList.add(LootItem.between(randomEssence.itemStringID, baseAmount + minPerTier * tier, baseAmount + maxPerTier * tier));
        return lootList;
    }

    public static LootItemInterface getIncursionOresReward(GameRandom seededRandom, int tier) {
        LootItemList lootList = new LootItemList(new LootItemInterface[0]);
        int baseAmount = 14;
        int minPerTier = 4;
        int maxPerTier = 8;
        OneOfLootItems oresBasedOnTier = new OneOfLootItems(new LootItemInterface[0]);
        oresBasedOnTier.add(new LootItem("slimeum"));
        if (tier >= IncursionPerksRegistry.GRAVEYARD_TABLET_CAN_DROP.tier) {
            oresBasedOnTier.add(new LootItem("nightsteelore"));
            if (tier >= IncursionPerksRegistry.SPIDER_CASTLE_TABLET_CAN_DROP.tier) {
                oresBasedOnTier.add(new LootItem("spideriteore"));
                if (tier >= IncursionPerksRegistry.CRYSTAL_HOLLOW_TABLET_CAN_DROP.tier) {
                    oresBasedOnTier.add(new LootItem("pearlescentshard"));
                }
            }
        }
        LootItem randomOre = (LootItem)seededRandom.getOneOf(oresBasedOnTier);
        lootList.add(LootItem.between(randomOre.itemStringID, baseAmount + minPerTier * tier, baseAmount + maxPerTier * tier));
        return lootList;
    }

    private static int getRandomTierForEquipment(GameRandom seededRandom, int maxTier) {
        TicketSystemList ticketedTier = new TicketSystemList();
        int ticketReductionPerTier = 5;
        for (int i = 1; i <= maxTier; ++i) {
            ticketedTier.addObject(100 - ticketReductionPerTier * i, (Object)i);
        }
        return (Integer)ticketedTier.getRandomObject(seededRandom);
    }

    public static int getMaxTierAvailableForTabletDrop(HashSet<Integer> perkIDsToCheck) {
        int altarTier = 1;
        for (IncursionPerk perk : IncursionPerksRegistry.fromPerkIDs(perkIDsToCheck)) {
            altarTier = GameMath.max(altarTier, perk.tier);
        }
        return GameMath.min(altarTier, IncursionData.TABLET_TIER_UPGRADE_CAP);
    }

    private static int getRandomTierForTablets(GameRandom seededRandom, int currentTabletTier) {
        TicketSystemList ticketedTier = new TicketSystemList();
        ticketedTier.addObject(50, (Object)currentTabletTier);
        ticketedTier.addObject(25, (Object)(currentTabletTier - 1));
        ticketedTier.addObject(10, (Object)(currentTabletTier - 2));
        return GameMath.max((Integer)ticketedTier.getRandomObject(seededRandom), IncursionData.MINIMUM_TIER);
    }

    public static LootItemInterface getSeededRandomReward(IncursionData incursionData, GameRandom seededRandom, int tabletTier) {
        TicketSystemList<LootItemInterface> ticketedRewards = new TicketSystemList<LootItemInterface>();
        int tabletTierWeight = 10 * tabletTier;
        int limitedTabletTier = GameMath.limit(tabletTier, IncursionData.MINIMUM_TIER, IncursionData.DROPS_TIER_CAP);
        int tabletRewardTier = 1;
        int equipmentRewardTier = 1;
        int maxEquipmentRewardTier = Math.min(tabletTier, 3);
        if (incursionData != null) {
            for (IncursionPerk perk : IncursionPerksRegistry.fromPerkIDs(incursionData.currentIncursionPerkIDs)) {
                if (perk.setMaxEquipmentRewardTier() <= maxEquipmentRewardTier) continue;
                maxEquipmentRewardTier = perk.setMaxEquipmentRewardTier();
            }
            equipmentRewardTier = UniqueIncursionRewardsRegistry.getRandomTierForEquipment(seededRandom, GameMath.min(Math.min(maxEquipmentRewardTier, tabletTier), IncursionData.ITEM_TIER_UPGRADE_CAP));
            for (IncursionPerk perk : IncursionPerksRegistry.fromPerkIDsSorted(incursionData.currentIncursionPerkIDs)) {
                if (perk.affectsCurrentIncursion) continue;
                ticketedRewards = perk.onGenerateTabletRewards(ticketedRewards, seededRandom, equipmentRewardTier, incursionData);
            }
            tabletRewardTier = UniqueIncursionRewardsRegistry.getRandomTierForTablets(seededRandom, limitedTabletTier);
        }
        ticketedRewards.addObject(100, (Object)UniqueIncursionRewardsRegistry.getSeededRandomTierXGatewayTablets(1, tabletRewardTier, incursionData));
        ticketedRewards.addObject((int)(40.0 + (double)tabletTierWeight * 0.75), (Object)UniqueIncursionRewardsRegistry.getSeededRandomTierXGatewayTablets(2, tabletRewardTier, incursionData));
        if (incursionData != null && incursionData.currentIncursionPerkIDs.contains(IncursionPerksRegistry.INCURSION_LEVEL_GEAR_REWARD.getID())) {
            ticketedRewards.addObject(20 + tabletTierWeight, (Object)new UpgradeTierLootItem(UniqueIncursionRewardsRegistry.getRandomTierXBiomeWeaponReward(seededRandom), equipmentRewardTier));
        } else {
            ticketedRewards.addObject(70 + tabletTierWeight, (Object)new UpgradeTierLootItem(UniqueIncursionRewardsRegistry.getRandomTierXBiomeWeaponReward(seededRandom), equipmentRewardTier));
        }
        if (incursionData != null && incursionData.currentIncursionPerkIDs.contains(IncursionPerksRegistry.INCURSION_LEVEL_GEAR_REWARD.getID())) {
            ticketedRewards.addObject(tabletTierWeight, (Object)new UpgradeTierLootItem(UniqueIncursionRewardsRegistry.getRandomTierXBiomeArmorSetReward(), equipmentRewardTier));
        } else {
            ticketedRewards.addObject(40 + tabletTierWeight, (Object)new UpgradeTierLootItem(UniqueIncursionRewardsRegistry.getRandomTierXBiomeArmorSetReward(), equipmentRewardTier));
        }
        int coinMultiplier = 1;
        if (incursionData != null && incursionData.currentIncursionPerkIDs.contains(IncursionPerksRegistry.MORE_COINS_REWARD.getID())) {
            if (incursionData.currentIncursionPerkIDs.contains(IncursionPerksRegistry.FIVE_TIMES_MORE_COINS_CHANCE.getID()) && seededRandom.getChance(0.1f)) {
                coinMultiplier = 5;
            }
            ticketedRewards.addObject(150 + tabletTierWeight / 2, (Object)UniqueIncursionRewardsRegistry.getCoinsReward(coinMultiplier, tabletTier));
        } else {
            ticketedRewards.addObject(75 + tabletTierWeight / 2, (Object)UniqueIncursionRewardsRegistry.getCoinsReward(coinMultiplier, tabletTier));
        }
        ticketedRewards.addObject(100 + tabletTierWeight / 2, (Object)UniqueIncursionRewardsRegistry.getUpgradeShardsReward(tabletTier));
        ticketedRewards.addObject(75 + tabletTierWeight / 2, (Object)UniqueIncursionRewardsRegistry.getAlchemyShardsReward(tabletTier));
        ticketedRewards.addObject(100 + tabletTierWeight / 2, (Object)UniqueIncursionRewardsRegistry.getAltarDustReward(tabletTier));
        ticketedRewards.addObject(50 + tabletTierWeight / 2, (Object)UniqueIncursionRewardsRegistry.getTierOneEssencesReward(seededRandom, tabletTier));
        ticketedRewards.addObject(GameMath.max(0, -30 + (int)((double)tabletTierWeight * 1.5)), (Object)UniqueIncursionRewardsRegistry.getTierTwoEssencesReward(seededRandom, tabletTier));
        ticketedRewards.addObject(GameMath.max(0, -30 + (int)((double)tabletTierWeight * 1.5)), (Object)UniqueIncursionRewardsRegistry.getIncursionOresReward(seededRandom, tabletTier));
        if (ticketedRewards.isEmpty()) {
            GameLog.warn.println("No rewards found.");
            return null;
        }
        return ticketedRewards.getRandomObject(seededRandom);
    }

    public static LootItemInterface getRandomTrinket() {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(trinkets);
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomIncursionTrinket() {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(incursionTrinkets);
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXArmorReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(headArmors, bodyArmors, feetArmors));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getRandomTierXIncursionArmorReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(incursionHeadArmors, incursionBodyArmors, incursionFeetArmors));
        return rewardTypeChosen.lootItemList;
    }

    public static LootItemInterface getSeededRandomTierXRareReward(GameRandom seededRandom) {
        UniqueIncursionReward rewardTypeChosen = (UniqueIncursionReward)instance.getElement(seededRandom.getOneOf(rareIncursionWeapons, rareIncursionArmorSets, rareIncursionTrinkets));
        return rewardTypeChosen.lootItemList;
    }
}

