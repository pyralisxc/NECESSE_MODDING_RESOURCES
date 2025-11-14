/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import necesse.engine.incursionPerkTree.AlchemyShardsDropPotionsPerk;
import necesse.engine.incursionPerkTree.ArcanicInvadersPerk;
import necesse.engine.incursionPerkTree.AscendedShardsCanDropPerk;
import necesse.engine.incursionPerkTree.BossBringsInvadersPerk;
import necesse.engine.incursionPerkTree.CavelingPortalCanSpawnPerk;
import necesse.engine.incursionPerkTree.CavelingsCanSpawnPerk;
import necesse.engine.incursionPerkTree.CavelingsDropBetterLootPerk;
import necesse.engine.incursionPerkTree.ChanceToDropDoubleTabletsPerk;
import necesse.engine.incursionPerkTree.ChanceToMineFullClusterPerk;
import necesse.engine.incursionPerkTree.ChefInvadersPerk;
import necesse.engine.incursionPerkTree.CosmeticArmorRewardPerk;
import necesse.engine.incursionPerkTree.CrystalHollowTabletCanDropPerk;
import necesse.engine.incursionPerkTree.DoubleBossChancePerk;
import necesse.engine.incursionPerkTree.EmpowermentBuffsPerk;
import necesse.engine.incursionPerkTree.EnableBannerOfWarPerk;
import necesse.engine.incursionPerkTree.EnchantScrollsRewardPerk;
import necesse.engine.incursionPerkTree.FiveTimesMoreCoinsChancePerk;
import necesse.engine.incursionPerkTree.FlamelingsCanDiePerk;
import necesse.engine.incursionPerkTree.GraveyardTabletCanDropPerk;
import necesse.engine.incursionPerkTree.ImprovedEmpowermentBuffsPerk;
import necesse.engine.incursionPerkTree.ImprovedEquipmentTiersOnePerk;
import necesse.engine.incursionPerkTree.ImprovedEquipmentTiersThreePerk;
import necesse.engine.incursionPerkTree.ImprovedEquipmentTiersTwoPerk;
import necesse.engine.incursionPerkTree.IncreasedBossLootPerk;
import necesse.engine.incursionPerkTree.IncursionLevelGearRewardPerk;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.incursionPerkTree.MegaUpgradeShardVeinPerk;
import necesse.engine.incursionPerkTree.MobsDropAlchemyShardsPerk;
import necesse.engine.incursionPerkTree.MobsDropAltarDustPerk;
import necesse.engine.incursionPerkTree.MobsDropMoreAltarDustPerk;
import necesse.engine.incursionPerkTree.MobsDropUpgradeShardsPerk;
import necesse.engine.incursionPerkTree.ModifiersAffectEnemiesPerk;
import necesse.engine.incursionPerkTree.MoonArenaTabletCanDropPerk;
import necesse.engine.incursionPerkTree.MoreCoinsRewardPerk;
import necesse.engine.incursionPerkTree.MoreExtractionsPerk;
import necesse.engine.incursionPerkTree.MoreHuntsPerk;
import necesse.engine.incursionPerkTree.MoreMagicWeaponsRewardPerk;
import necesse.engine.incursionPerkTree.MoreMeleeWeaponsRewardPerk;
import necesse.engine.incursionPerkTree.MoreRangedWeaponsRewardPerk;
import necesse.engine.incursionPerkTree.MoreSummonWeaponsRewardPerk;
import necesse.engine.incursionPerkTree.PetsRewardPerk;
import necesse.engine.incursionPerkTree.RavenInvadersPerk;
import necesse.engine.incursionPerkTree.RescueMoreSettlersRewardPerk;
import necesse.engine.incursionPerkTree.RescueSettlerRewardPerk;
import necesse.engine.incursionPerkTree.SecondLifeBuffPerk;
import necesse.engine.incursionPerkTree.SlimeTabletCanDropPerk;
import necesse.engine.incursionPerkTree.SpiderCastleTabletCanDropPerk;
import necesse.engine.incursionPerkTree.SunArenaTabletCanDropPerk;
import necesse.engine.incursionPerkTree.ThirdLifeBuffPerk;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.GameRegistry;
import necesse.engine.util.GameUtils;

public class IncursionPerksRegistry
extends GameRegistry<IncursionPerk> {
    public static final IncursionPerksRegistry instance = new IncursionPerksRegistry();
    public static IncursionPerk MOBS_DROP_ALTAR_DUST;
    public static IncursionPerk MOBS_DROP_MORE_ALTAR_DUST;
    public static IncursionPerk FLAMELINGS_CAN_DIE;
    public static IncursionPerk ALTAR_DUST_DROPS_ALCHEMY_SHARDS;
    public static IncursionPerk ALTAR_DUST_DROPS_UPGRADE_SHARDS;
    public static IncursionPerk MORE_EXTRACTIONS;
    public static IncursionPerk MORE_HUNTS;
    public static IncursionPerk RAVEN_INVADERS;
    public static IncursionPerk ARCANIC_INVADERS;
    public static IncursionPerk CHEF_INVADER;
    public static IncursionPerk MEGA_UPGRADE_SHARD_VEIN;
    public static IncursionPerk MORE_MELEE_WEAPONS_REWARD;
    public static IncursionPerk MORE_RANGED_WEAPONS_REWARD;
    public static IncursionPerk MORE_MAGIC_WEAPONS_REWARD;
    public static IncursionPerk MORE_SUMMON_WEAPONS_REWARD;
    public static IncursionPerk COSMETIC_ARMOR_REWARD;
    public static IncursionPerk SLIME_CAVE_TABLET_CAN_DROP;
    public static IncursionPerk GRAVEYARD_TABLET_CAN_DROP;
    public static IncursionPerk SPIDER_CASTLE_TABLET_CAN_DROP;
    public static IncursionPerk SUN_ARENA_TABLET_CAN_DROP;
    public static IncursionPerk MOON_ARENA_TABLET_CAN_DROP;
    public static IncursionPerk CRYSTAL_HOLLOW_TABLET_CAN_DROP;
    public static IncursionPerk ASCENDED_SHARDS_CAN_DROP;
    public static IncursionPerk CAVELINGS_CAN_SPAWN;
    public static IncursionPerk CAVELING_PORTAL_CAN_SPAWN;
    public static IncursionPerk CAVELINGS_DROP_BETTER_LOOT;
    public static IncursionPerk ALCHEMY_SHARDS_DROP_POTIONS;
    public static IncursionPerk CHANCE_TO_MINE_FULL_CLUSTER;
    public static IncursionPerk ENCHANT_SCROLLS_REWARD;
    public static IncursionPerk MORE_COINS_REWARD;
    public static IncursionPerk PETS_REWARD;
    public static IncursionPerk CHANCE_TO_DROP_DOUBLE_TABLETS;
    public static IncursionPerk FIVE_TIMES_MORE_COINS_CHANCE;
    public static IncursionPerk RESCUE_SETTLER_REWARD;
    public static IncursionPerk RESCUE_MORE_SETTLERS_REWARD;
    public static IncursionPerk BOSS_BRINGS_INVADERS;
    public static IncursionPerk DOUBLE_BOSS_CHANCE;
    public static IncursionPerk MODIFIERS_AFFECT_ENEMIES;
    public static IncursionPerk IMPROVED_EQUIPMENT_TIERS_ONE;
    public static IncursionPerk IMPROVED_EQUIPMENT_TIERS_TWO;
    public static IncursionPerk IMPROVED_EQUIPMENT_TIERS_THREE;
    public static IncursionPerk INCURSION_LEVEL_GEAR_REWARD;
    public static IncursionPerk EMPOWERMENT_BUFFS;
    public static IncursionPerk SECOND_LIFE_BUFF;
    public static IncursionPerk INCREASED_BOSS_LOOT;
    public static IncursionPerk IMPROVED_EMPOWERMENT_BUFFS;
    public static IncursionPerk THIRD_LIFE_BUFF;
    public static IncursionPerk ENABLE_BANNER_OF_WAR;
    private final int tier1Cost = 30;
    private final int tier2Cost = 50;
    private final int tier3Cost = 75;
    private final int tier4Cost = 125;
    private final int tier5Cost = 200;
    private final int tier6Cost = 320;
    private final int tier7Cost = 500;
    private final int tier8Cost = 800;
    private final int tier9Cost = 1300;
    private final int tier10Cost = 2000;

    private IncursionPerksRegistry() {
        super("Incursion Perks", 32762);
    }

    @Override
    public void registerCore() {
        MORE_MELEE_WEAPONS_REWARD = IncursionPerksRegistry.registerPerk("moremeleeweaponsreward", new MoreMeleeWeaponsRewardPerk((Integer)1, 30, 1, new IncursionPerk[0]));
        MORE_RANGED_WEAPONS_REWARD = IncursionPerksRegistry.registerPerk("morerangedweaponsreward", new MoreRangedWeaponsRewardPerk((Integer)1, 30, 2, new IncursionPerk[0]));
        MOBS_DROP_ALTAR_DUST = IncursionPerksRegistry.registerPerk("mobsdropaltardust", new MobsDropAltarDustPerk((Integer)1, 30, 3, new IncursionPerk[0]));
        MORE_MAGIC_WEAPONS_REWARD = IncursionPerksRegistry.registerPerk("moremagicweaponsreward", new MoreMagicWeaponsRewardPerk((Integer)1, 30, 4, new IncursionPerk[0]));
        MORE_COINS_REWARD = IncursionPerksRegistry.registerPerk("morecoinsreward", new MoreCoinsRewardPerk((Integer)1, 30, 5, new IncursionPerk[0]));
        MORE_SUMMON_WEAPONS_REWARD = IncursionPerksRegistry.registerPerk("moresummonweaponsreward", new MoreSummonWeaponsRewardPerk((Integer)1, 30, 6, new IncursionPerk[0]));
        ALTAR_DUST_DROPS_UPGRADE_SHARDS = IncursionPerksRegistry.registerPerk("mobsdropupgradeshards", new MobsDropUpgradeShardsPerk((Integer)2, 50, 1, MOBS_DROP_ALTAR_DUST));
        ALTAR_DUST_DROPS_ALCHEMY_SHARDS = IncursionPerksRegistry.registerPerk("mobsdropalchemyshards", new MobsDropAlchemyShardsPerk((Integer)2, 50, 2, MOBS_DROP_ALTAR_DUST));
        IncursionPerksRegistry.ALTAR_DUST_DROPS_UPGRADE_SHARDS.otherPerksThatLockThisPerk.add(ALTAR_DUST_DROPS_ALCHEMY_SHARDS);
        IncursionPerksRegistry.ALTAR_DUST_DROPS_ALCHEMY_SHARDS.otherPerksThatLockThisPerk.add(ALTAR_DUST_DROPS_UPGRADE_SHARDS);
        COSMETIC_ARMOR_REWARD = IncursionPerksRegistry.registerPerk("cosmeticarmorreward", new CosmeticArmorRewardPerk((Integer)2, 50, 3, new IncursionPerk[0]));
        SLIME_CAVE_TABLET_CAN_DROP = IncursionPerksRegistry.registerPerk("slimecavetabletcandrop", new SlimeTabletCanDropPerk((Integer)2, 100, 4, MOBS_DROP_ALTAR_DUST));
        FIVE_TIMES_MORE_COINS_CHANCE = IncursionPerksRegistry.registerPerk("fivetimesmorecoinschance", new FiveTimesMoreCoinsChancePerk((Integer)2, 50, 5, MORE_COINS_REWARD));
        CAVELINGS_CAN_SPAWN = IncursionPerksRegistry.registerPerk("cavelingscanspawn", new CavelingsCanSpawnPerk((Integer)2, 50, 6, new IncursionPerk[0]));
        GRAVEYARD_TABLET_CAN_DROP = IncursionPerksRegistry.registerPerk("graveyardtabletcandrop", new GraveyardTabletCanDropPerk((Integer)3, 150, 4, SLIME_CAVE_TABLET_CAN_DROP));
        MORE_EXTRACTIONS = IncursionPerksRegistry.registerPerk("moreextractions", new MoreExtractionsPerk((Integer)3, 75, 3, new IncursionPerk[0]));
        MORE_HUNTS = IncursionPerksRegistry.registerPerk("morehunts", new MoreHuntsPerk((Integer)3, 75, 5, new IncursionPerk[0]));
        IncursionPerksRegistry.MORE_EXTRACTIONS.otherPerksThatLockThisPerk.add(MORE_HUNTS);
        IncursionPerksRegistry.MORE_HUNTS.otherPerksThatLockThisPerk.add(MORE_EXTRACTIONS);
        ALCHEMY_SHARDS_DROP_POTIONS = IncursionPerksRegistry.registerPerk("alchemyshardsdroppotions", new AlchemyShardsDropPotionsPerk((Integer)3, 75, 2, ALTAR_DUST_DROPS_ALCHEMY_SHARDS));
        IncursionPerksRegistry.ALCHEMY_SHARDS_DROP_POTIONS.otherPerksThatLockThisPerk.add(ALTAR_DUST_DROPS_UPGRADE_SHARDS);
        MEGA_UPGRADE_SHARD_VEIN = IncursionPerksRegistry.registerPerk("megaupgradeshardveinperk", new MegaUpgradeShardVeinPerk((Integer)3, 75, 1, ALTAR_DUST_DROPS_UPGRADE_SHARDS));
        IncursionPerksRegistry.MEGA_UPGRADE_SHARD_VEIN.otherPerksThatLockThisPerk.add(ALTAR_DUST_DROPS_ALCHEMY_SHARDS);
        MOBS_DROP_MORE_ALTAR_DUST = IncursionPerksRegistry.registerPerk("mobsdropmorealtardust", new MobsDropMoreAltarDustPerk((Integer)4, 125, 1, ALCHEMY_SHARDS_DROP_POTIONS, MEGA_UPGRADE_SHARD_VEIN));
        RAVEN_INVADERS = IncursionPerksRegistry.registerPerk("raveninvaders", new RavenInvadersPerk((Integer)4, 125, 2, new IncursionPerk[0]));
        ARCANIC_INVADERS = IncursionPerksRegistry.registerPerk("arcanicinvaders", new ArcanicInvadersPerk((Integer)4, 125, 3, new IncursionPerk[0]));
        CHEF_INVADER = IncursionPerksRegistry.registerPerk("chefinvader", new ChefInvadersPerk((Integer)4, 125, 4, new IncursionPerk[0]));
        SPIDER_CASTLE_TABLET_CAN_DROP = IncursionPerksRegistry.registerPerk("spidercastletabletcandrop", new SpiderCastleTabletCanDropPerk((Integer)4, 225, 5, GRAVEYARD_TABLET_CAN_DROP));
        CAVELING_PORTAL_CAN_SPAWN = IncursionPerksRegistry.registerPerk("cavelingportalcanspawn", new CavelingPortalCanSpawnPerk((Integer)4, 125, 6, CAVELINGS_CAN_SPAWN));
        CHANCE_TO_MINE_FULL_CLUSTER = IncursionPerksRegistry.registerPerk("chancetominefullcluster", new ChanceToMineFullClusterPerk((Integer)5, 200, 1, MOBS_DROP_MORE_ALTAR_DUST));
        ENCHANT_SCROLLS_REWARD = IncursionPerksRegistry.registerPerk("enchantsreward", new EnchantScrollsRewardPerk((Integer)5, 200, 2, new IncursionPerk[0]));
        RESCUE_SETTLER_REWARD = IncursionPerksRegistry.registerPerk("rescuesettlerreward", new RescueSettlerRewardPerk((Integer)5, 200, 4, new IncursionPerk[0]));
        MOON_ARENA_TABLET_CAN_DROP = IncursionPerksRegistry.registerPerk("moonarenatabletcandrop", new MoonArenaTabletCanDropPerk((Integer)5, 320, 5, SPIDER_CASTLE_TABLET_CAN_DROP));
        CAVELINGS_DROP_BETTER_LOOT = IncursionPerksRegistry.registerPerk("cavelingsbetterloot", new CavelingsDropBetterLootPerk((Integer)5, 200, 6, CAVELING_PORTAL_CAN_SPAWN));
        CHANCE_TO_DROP_DOUBLE_TABLETS = IncursionPerksRegistry.registerPerk("doubletabletchancedrop", new ChanceToDropDoubleTabletsPerk((Integer)6, 320, 1, new IncursionPerk[0]));
        PETS_REWARD = IncursionPerksRegistry.registerPerk("petsreward", new PetsRewardPerk((Integer)6, 320, 2, new IncursionPerk[0]));
        BOSS_BRINGS_INVADERS = IncursionPerksRegistry.registerPerk("bossbringsinvaders", new BossBringsInvadersPerk((Integer)6, 320, 3, RAVEN_INVADERS, ARCANIC_INVADERS, CHEF_INVADER));
        SUN_ARENA_TABLET_CAN_DROP = IncursionPerksRegistry.registerPerk("sunarenatabletcandrop", new SunArenaTabletCanDropPerk((Integer)6, 470, 5, MOON_ARENA_TABLET_CAN_DROP));
        IMPROVED_EQUIPMENT_TIERS_ONE = IncursionPerksRegistry.registerPerk("improvedtiersone", new ImprovedEquipmentTiersOnePerk((Integer)6, 320, 6, new IncursionPerk[0]));
        FLAMELINGS_CAN_DIE = IncursionPerksRegistry.registerPerk("flamelingscandie", new FlamelingsCanDiePerk((Integer)7, 500, 1, new IncursionPerk[0]));
        RESCUE_MORE_SETTLERS_REWARD = IncursionPerksRegistry.registerPerk("rescuemoresettlersreward", new RescueMoreSettlersRewardPerk((Integer)7, 500, 4, RESCUE_SETTLER_REWARD));
        CRYSTAL_HOLLOW_TABLET_CAN_DROP = IncursionPerksRegistry.registerPerk("crystalhollowtabletcandrop", new CrystalHollowTabletCanDropPerk((Integer)7, 680, 5, SUN_ARENA_TABLET_CAN_DROP));
        DOUBLE_BOSS_CHANCE = IncursionPerksRegistry.registerPerk("doublebosschance", new DoubleBossChancePerk((Integer)8, 800, 3, BOSS_BRINGS_INVADERS));
        SECOND_LIFE_BUFF = IncursionPerksRegistry.registerPerk("secondlifebuff", new SecondLifeBuffPerk((Integer)8, 800, 2, DOUBLE_BOSS_CHANCE));
        EMPOWERMENT_BUFFS = IncursionPerksRegistry.registerPerk("empowermentbuffs", new EmpowermentBuffsPerk((Integer)8, 800, 4, DOUBLE_BOSS_CHANCE));
        IncursionPerksRegistry.EMPOWERMENT_BUFFS.otherPerksThatLockThisPerk.add(SECOND_LIFE_BUFF);
        IncursionPerksRegistry.SECOND_LIFE_BUFF.otherPerksThatLockThisPerk.add(EMPOWERMENT_BUFFS);
        IMPROVED_EQUIPMENT_TIERS_TWO = IncursionPerksRegistry.registerPerk("improvedtierstwo", new ImprovedEquipmentTiersTwoPerk((Integer)8, 800, 6, IMPROVED_EQUIPMENT_TIERS_ONE));
        THIRD_LIFE_BUFF = IncursionPerksRegistry.registerPerk("thirdlifebuff", new ThirdLifeBuffPerk((Integer)9, 1300, 2, SECOND_LIFE_BUFF));
        IMPROVED_EMPOWERMENT_BUFFS = IncursionPerksRegistry.registerPerk("improvedempowermentbuffs", new ImprovedEmpowermentBuffsPerk((Integer)9, 1300, 4, EMPOWERMENT_BUFFS));
        IncursionPerksRegistry.IMPROVED_EMPOWERMENT_BUFFS.otherPerksThatLockThisPerk.add(SECOND_LIFE_BUFF);
        IncursionPerksRegistry.THIRD_LIFE_BUFF.otherPerksThatLockThisPerk.add(EMPOWERMENT_BUFFS);
        MODIFIERS_AFFECT_ENEMIES = IncursionPerksRegistry.registerPerk("modifiersaffectenemiesperk", new ModifiersAffectEnemiesPerk((Integer)9, 1300, 5, new IncursionPerk[0]));
        INCURSION_LEVEL_GEAR_REWARD = IncursionPerksRegistry.registerPerk("incursionlevelgearreward", new IncursionLevelGearRewardPerk((Integer)9, 1300, 6, IMPROVED_EQUIPMENT_TIERS_TWO));
        ENABLE_BANNER_OF_WAR = IncursionPerksRegistry.registerPerk("enablebannerofwar", new EnableBannerOfWarPerk((Integer)10, 2000, 1, new IncursionPerk[0]));
        INCREASED_BOSS_LOOT = IncursionPerksRegistry.registerPerk("increasedbossloot", new IncreasedBossLootPerk((Integer)10, 2000, 3, DOUBLE_BOSS_CHANCE));
        ASCENDED_SHARDS_CAN_DROP = IncursionPerksRegistry.registerPerk("ascendedshardsdrop", new AscendedShardsCanDropPerk((Integer)10, 2300, 4, new IncursionPerk[0]));
        IMPROVED_EQUIPMENT_TIERS_THREE = IncursionPerksRegistry.registerPerk("improvedtiersthree", new ImprovedEquipmentTiersThreePerk((Integer)10, 2000, 6, INCURSION_LEVEL_GEAR_REWARD));
    }

    @Override
    protected void onRegister(IncursionPerk object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
        for (IncursionPerk element : this.getElements()) {
            element.onChallengeRegistryClosed();
        }
    }

    public static IncursionPerk registerPerk(String stringID, IncursionPerk incursionPerk) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register perks");
        }
        instance.register(stringID, incursionPerk);
        return incursionPerk;
    }

    public static Iterable<IncursionPerk> getPerks() {
        return instance.getElements();
    }

    public static ArrayList<IncursionPerk> getPerksAtTier(int tier) {
        ArrayList<IncursionPerk> perksAtTier = new ArrayList<IncursionPerk>();
        for (IncursionPerk perk : instance.getElements()) {
            if (perk.tier != tier) continue;
            perksAtTier.add(perk);
        }
        return perksAtTier;
    }

    public static boolean doesPerkExists(String stringID) {
        try {
            return instance.getElementIDRaw(stringID) >= 0;
        }
        catch (NoSuchElementException e) {
            return false;
        }
    }

    public static int getTotalPerkCount() {
        return instance.size();
    }

    public static IncursionPerk getPerk(String id) {
        return (IncursionPerk)instance.getElement(id);
    }

    public static IncursionPerk getPerk(int id) {
        return (IncursionPerk)instance.getElement(id);
    }

    public static int getPerkID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static String getPerkStringID(int id) {
        return instance.getElementStringID(id);
    }

    public static Iterable<IncursionPerk> fromPerkIDs(Iterable<Integer> perkIDs) {
        return GameUtils.mapIterable(perkIDs.iterator(), IncursionPerksRegistry::getPerk);
    }

    public static Iterable<IncursionPerk> fromPerkIDsSorted(Collection<Integer> perkIDs) {
        return () -> perkIDs.stream().sorted().map(IncursionPerksRegistry::getPerk).iterator();
    }
}

