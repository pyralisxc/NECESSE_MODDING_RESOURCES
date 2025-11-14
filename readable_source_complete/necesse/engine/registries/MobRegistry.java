/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.AscendedPylonDummyMob;
import necesse.entity.mobs.BannerOfWarDummyMob;
import necesse.entity.mobs.BoneSpikeMob;
import necesse.entity.mobs.EarthSpikeMob;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.HumanTextureFull;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobTexture;
import necesse.entity.mobs.ProjectileHitboxMob;
import necesse.entity.mobs.SwampSporeDummyMob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.entity.mobs.friendly.BoarMob;
import necesse.entity.mobs.friendly.BullMob;
import necesse.entity.mobs.friendly.ChickenMob;
import necesse.entity.mobs.friendly.CowMob;
import necesse.entity.mobs.friendly.CrocodileMob;
import necesse.entity.mobs.friendly.GrizzlyBearMob;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.mobs.friendly.LifeEssenceFollowingMob;
import necesse.entity.mobs.friendly.PenguinMob;
import necesse.entity.mobs.friendly.PigMob;
import necesse.entity.mobs.friendly.PolarBearMob;
import necesse.entity.mobs.friendly.QueenBeeMob;
import necesse.entity.mobs.friendly.RamMob;
import necesse.entity.mobs.friendly.RoosterMob;
import necesse.entity.mobs.friendly.SheepMob;
import necesse.entity.mobs.friendly.WildOstrichMob;
import necesse.entity.mobs.friendly.critters.BeetCaveCroppler;
import necesse.entity.mobs.friendly.critters.BirdMob;
import necesse.entity.mobs.friendly.critters.BluebirdMob;
import necesse.entity.mobs.friendly.critters.CanaryBirdMob;
import necesse.entity.mobs.friendly.critters.CardinalBirdMob;
import necesse.entity.mobs.friendly.critters.CrabMob;
import necesse.entity.mobs.friendly.critters.DuckMob;
import necesse.entity.mobs.friendly.critters.FrogMob;
import necesse.entity.mobs.friendly.critters.MouseMob;
import necesse.entity.mobs.friendly.critters.RabbitMob;
import necesse.entity.mobs.friendly.critters.ScorpionMob;
import necesse.entity.mobs.friendly.critters.SnowHareMob;
import necesse.entity.mobs.friendly.critters.SpiderCritterMob;
import necesse.entity.mobs.friendly.critters.SquirrelMob;
import necesse.entity.mobs.friendly.critters.SwampSlugMob;
import necesse.entity.mobs.friendly.critters.TurtleMob;
import necesse.entity.mobs.friendly.critters.caveling.DeepSandStoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.DeepSnowStoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.DeepStoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.DeepSwampStoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.DryadCaveling;
import necesse.entity.mobs.friendly.critters.caveling.GraniteCaveling;
import necesse.entity.mobs.friendly.critters.caveling.IncursionCaveling;
import necesse.entity.mobs.friendly.critters.caveling.IncursionFlamelingMob;
import necesse.entity.mobs.friendly.critters.caveling.SandStoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.SnowStoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.StoneCaveling;
import necesse.entity.mobs.friendly.critters.caveling.SwampStoneCaveling;
import necesse.entity.mobs.friendly.human.ElderHumanMob;
import necesse.entity.mobs.friendly.human.GenericHumanMob;
import necesse.entity.mobs.friendly.human.GuardHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.AlchemistHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.AnglerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.AnimalKeeperHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.BlacksmithHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.ExoticMerchantHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.ExplorerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.FarmerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.FriendlyWitchHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.GunsmithHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.HunterHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.MinerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.PawnBrokerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.PirateHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.StylistHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.TraderHumanMob;
import necesse.entity.mobs.hostile.AgedChampionMob;
import necesse.entity.mobs.hostile.AncientArmoredSkeletonMob;
import necesse.entity.mobs.hostile.AncientSkeletonMageMob;
import necesse.entity.mobs.hostile.AncientSkeletonMob;
import necesse.entity.mobs.hostile.AncientSkeletonRaiderMob;
import necesse.entity.mobs.hostile.AncientSkeletonThrowerMob;
import necesse.entity.mobs.hostile.ArcanicPylonMob;
import necesse.entity.mobs.hostile.AscendedBatMob;
import necesse.entity.mobs.hostile.AscendedGolemMob;
import necesse.entity.mobs.hostile.BlackCaveSpiderMob;
import necesse.entity.mobs.hostile.BloatedSpiderMob;
import necesse.entity.mobs.hostile.BoneWalkerMob;
import necesse.entity.mobs.hostile.CaveMoleMob;
import necesse.entity.mobs.hostile.ChickenRaiderMob;
import necesse.entity.mobs.hostile.CrawlingZombieMob;
import necesse.entity.mobs.hostile.CrazedRavenMob;
import necesse.entity.mobs.hostile.CryoFlakeMob;
import necesse.entity.mobs.hostile.CryptBatMob;
import necesse.entity.mobs.hostile.CryptVampireMob;
import necesse.entity.mobs.hostile.CrystalArmadillo;
import necesse.entity.mobs.hostile.CrystalGolemMob;
import necesse.entity.mobs.hostile.DeepCaveSpiritMob;
import necesse.entity.mobs.hostile.DesertCrawlerMob;
import necesse.entity.mobs.hostile.DryadSentinelMob;
import necesse.entity.mobs.hostile.EnchantedCrawlingZombieMob;
import necesse.entity.mobs.hostile.EnchantedZombieArcherMob;
import necesse.entity.mobs.hostile.EnchantedZombieMob;
import necesse.entity.mobs.hostile.FishianHealerMob;
import necesse.entity.mobs.hostile.FishianHookWarriorMob;
import necesse.entity.mobs.hostile.FishianRaiderMob;
import necesse.entity.mobs.hostile.FishianShamanMob;
import necesse.entity.mobs.hostile.FlamelingShooterMob;
import necesse.entity.mobs.hostile.ForestSpectorMob;
import necesse.entity.mobs.hostile.FrostSentryMob;
import necesse.entity.mobs.hostile.FrozenDwarfMob;
import necesse.entity.mobs.hostile.FrozenDwarfRaiderMob;
import necesse.entity.mobs.hostile.GhostSlimeMob;
import necesse.entity.mobs.hostile.GiantCaveSpiderMob;
import necesse.entity.mobs.hostile.GiantScorpionMob;
import necesse.entity.mobs.hostile.GiantSwampSlimeMob;
import necesse.entity.mobs.hostile.GoblinMob;
import necesse.entity.mobs.hostile.HumanRaiderMob;
import necesse.entity.mobs.hostile.IncursionCrawlingZombieMob;
import necesse.entity.mobs.hostile.JackalMob;
import necesse.entity.mobs.hostile.LeggedSlimeThrowerMob;
import necesse.entity.mobs.hostile.MageSlimeMob;
import necesse.entity.mobs.hostile.MosquitoEggMob;
import necesse.entity.mobs.hostile.MosquitoMob;
import necesse.entity.mobs.hostile.MummyMageMob;
import necesse.entity.mobs.hostile.MummyMob;
import necesse.entity.mobs.hostile.MummyRaiderMob;
import necesse.entity.mobs.hostile.NinjaMob;
import necesse.entity.mobs.hostile.NinjaRaiderMob;
import necesse.entity.mobs.hostile.PhantomMob;
import necesse.entity.mobs.hostile.PirateRaiderMob;
import necesse.entity.mobs.hostile.PossessedSettlerMob;
import necesse.entity.mobs.hostile.RogueHunterRaiderMob;
import necesse.entity.mobs.hostile.SandSpiritMob;
import necesse.entity.mobs.hostile.SandwormBody;
import necesse.entity.mobs.hostile.SandwormHead;
import necesse.entity.mobs.hostile.SandwormTail;
import necesse.entity.mobs.hostile.SkeletonMageMob;
import necesse.entity.mobs.hostile.SkeletonMinerMob;
import necesse.entity.mobs.hostile.SkeletonMob;
import necesse.entity.mobs.hostile.SkeletonThrowerMob;
import necesse.entity.mobs.hostile.SlimeWormBody;
import necesse.entity.mobs.hostile.SlimeWormHead;
import necesse.entity.mobs.hostile.SmallCaveSpiderMob;
import necesse.entity.mobs.hostile.SnowWolfMob;
import necesse.entity.mobs.hostile.SpiderkinArcherMob;
import necesse.entity.mobs.hostile.SpiderkinMageMob;
import necesse.entity.mobs.hostile.SpiderkinMob;
import necesse.entity.mobs.hostile.SpiderkinWarriorMob;
import necesse.entity.mobs.hostile.SpiritGhoulMob;
import necesse.entity.mobs.hostile.StabbyBushMob;
import necesse.entity.mobs.hostile.StaticJellyfishMob;
import necesse.entity.mobs.hostile.SwampCaveSpiderMob;
import necesse.entity.mobs.hostile.SwampDwellerMob;
import necesse.entity.mobs.hostile.SwampShooterMob;
import necesse.entity.mobs.hostile.SwampSkeletonMob;
import necesse.entity.mobs.hostile.SwampSlimeMob;
import necesse.entity.mobs.hostile.SwampZombieMob;
import necesse.entity.mobs.hostile.TheMafiaRaiderMob;
import necesse.entity.mobs.hostile.TrapperZombieMob;
import necesse.entity.mobs.hostile.TrenchcoatGoblinChestplateMob;
import necesse.entity.mobs.hostile.TrenchcoatGoblinHelmetMob;
import necesse.entity.mobs.hostile.TrenchcoatGoblinShoesMob;
import necesse.entity.mobs.hostile.TrenchcoatGoblinStackedMob;
import necesse.entity.mobs.hostile.VampireMob;
import necesse.entity.mobs.hostile.VampireRaiderMob;
import necesse.entity.mobs.hostile.VoidApprentice;
import necesse.entity.mobs.hostile.VoidApprenticeRaiderMob;
import necesse.entity.mobs.hostile.WarriorSlimeMob;
import necesse.entity.mobs.hostile.WebSpinnerMob;
import necesse.entity.mobs.hostile.ZombieArcherMob;
import necesse.entity.mobs.hostile.ZombieMob;
import necesse.entity.mobs.hostile.bosses.AncientVultureEggMob;
import necesse.entity.mobs.hostile.bosses.AncientVultureMob;
import necesse.entity.mobs.hostile.bosses.ArenaEntrancePortalMob;
import necesse.entity.mobs.hostile.bosses.BossSpawnPortalMob;
import necesse.entity.mobs.hostile.bosses.ChieftainGauntletSpawnerPortalMob;
import necesse.entity.mobs.hostile.bosses.ChieftainMob;
import necesse.entity.mobs.hostile.bosses.CryoQueenMob;
import necesse.entity.mobs.hostile.bosses.CrystalDragonBody;
import necesse.entity.mobs.hostile.bosses.CrystalDragonHead;
import necesse.entity.mobs.hostile.bosses.CursedCroneSpiritGhoulMob;
import necesse.entity.mobs.hostile.bosses.EvilsPortalMob;
import necesse.entity.mobs.hostile.bosses.EvilsProtectorMob;
import necesse.entity.mobs.hostile.bosses.FallenDragonBody;
import necesse.entity.mobs.hostile.bosses.FallenDragonHead;
import necesse.entity.mobs.hostile.bosses.FallenWizardGhostMob;
import necesse.entity.mobs.hostile.bosses.FallenWizardMob;
import necesse.entity.mobs.hostile.bosses.FlyingSpiritsBody;
import necesse.entity.mobs.hostile.bosses.GritHead;
import necesse.entity.mobs.hostile.bosses.HomePortalMob;
import necesse.entity.mobs.hostile.bosses.IncursionCavelingsSpawnerPortalMob;
import necesse.entity.mobs.hostile.bosses.MoonlightDancerMob;
import necesse.entity.mobs.hostile.bosses.MotherSlimeMob;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.hostile.bosses.NightSwarmStartMob;
import necesse.entity.mobs.hostile.bosses.PestWardenBody;
import necesse.entity.mobs.hostile.bosses.PestWardenHead;
import necesse.entity.mobs.hostile.bosses.PortalMinion;
import necesse.entity.mobs.hostile.bosses.QueenSpiderMob;
import necesse.entity.mobs.hostile.bosses.ReaperMob;
import necesse.entity.mobs.hostile.bosses.ReaperSpiritMob;
import necesse.entity.mobs.hostile.bosses.ReaperSpiritPortalMob;
import necesse.entity.mobs.hostile.bosses.ReturnPortalMob;
import necesse.entity.mobs.hostile.bosses.SageAndGritStartMob;
import necesse.entity.mobs.hostile.bosses.SageHead;
import necesse.entity.mobs.hostile.bosses.SpiderEmpressMob;
import necesse.entity.mobs.hostile.bosses.SpiderHatchlingMob;
import necesse.entity.mobs.hostile.bosses.SpiritTornadoMob;
import necesse.entity.mobs.hostile.bosses.SunlightChampionMob;
import necesse.entity.mobs.hostile.bosses.SwampGuardianBody;
import necesse.entity.mobs.hostile.bosses.SwampGuardianHead;
import necesse.entity.mobs.hostile.bosses.SwampGuardianTail;
import necesse.entity.mobs.hostile.bosses.TheCursedCroneMob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.mobs.hostile.bosses.VoidWizardClone;
import necesse.entity.mobs.hostile.bosses.VultureHatchling;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedBeamMob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedGauntletMob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardPeripheralMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidClawMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidHornMob;
import necesse.entity.mobs.hostile.bosses.theVoid.TheVoidMob;
import necesse.entity.mobs.hostile.pirates.PirateCaptainMob;
import necesse.entity.mobs.hostile.pirates.PirateParrotMob;
import necesse.entity.mobs.hostile.pirates.PirateRecruit;
import necesse.entity.mobs.hostile.theRunebound.BattleChefMob;
import necesse.entity.mobs.hostile.theRunebound.CroneMob;
import necesse.entity.mobs.hostile.theRunebound.RuneboundBruteMob;
import necesse.entity.mobs.hostile.theRunebound.RuneboundShamanMob;
import necesse.entity.mobs.hostile.theRunebound.RuneboundTrapperMob;
import necesse.entity.mobs.hostile.witches.EvilWitchNecroticBowMob;
import necesse.entity.mobs.hostile.witches.EvilWitchNecroticFlaskMob;
import necesse.entity.mobs.hostile.witches.EvilWitchNecroticGreatswordMob;
import necesse.entity.mobs.hostile.witches.EvilWitchStartMob;
import necesse.entity.mobs.polymorph.ChickenPolymorphMob;
import necesse.entity.mobs.polymorph.DuckPolymorphMob;
import necesse.entity.mobs.polymorph.FrogPolymorphMob;
import necesse.entity.mobs.polymorph.RabbitPolymorphMob;
import necesse.entity.mobs.polymorph.RoosterPolymorphMob;
import necesse.entity.mobs.polymorph.SquirrelPolymorphMob;
import necesse.entity.mobs.polymorph.TurtlePolymorphMob;
import necesse.entity.mobs.summon.MinecartMob;
import necesse.entity.mobs.summon.SawBladeMob;
import necesse.entity.mobs.summon.WoodBoatMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AncestorKnightFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AncestorMageFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.ArachnidSpiderMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabyCrawlingZombieFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabyDryadMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySkeletonMageMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySkeletonMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySnowmanMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySpiderMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySpiderkinArcher;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabySpiderkinWarrior;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabyZombieArcherMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BabyZombieMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BashyBushFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.BashyBushMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.ChargingPhantomFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.CryoFlakeFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.DryadSpiritFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.DuskMoonDiscFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FrostPiercerFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.GhostShipFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.LocustFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.OrbOfSlimesFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.PoisonSlimeFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.PouncingSlimeFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.RavenLordFeatherFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.ReaperSpiritFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.RubyDragonBodyFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.RubyDragonHeadFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.RubyShieldFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.SentientSwordFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.SkeletonFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.SlimeGreatswordFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.StabbyBushFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.VultureHatchlingFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.WanderbotFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.HoverboardMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.JumpingBallMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.MinecartMountMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.RuneboundBoatMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.SeahorseMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.SteelBoatMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.TameOstrichMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.WitchBroomMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.WoodBoatMountMob;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.GhostlyBowFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetCavelingElder;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetEvilMinion;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetGrizzlyBearCub;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetParrotMob;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetPenguinMob;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetPugMob;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetWalkingTorchMob;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.WillOWispMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.MobSpawnItem;
import necesse.level.maps.Level;

public class MobRegistry
extends ClassedGameRegistry<Mob, MobRegistryElement> {
    public static final MobRegistry instance = new MobRegistry();
    private static final HashMap<Integer, Integer> mobIDToSpawnItemIDMap = new HashMap();
    public static HashSet<Integer> textColoredIcons = new HashSet();

    private MobRegistry() {
        super("Mob", 32762);
    }

    @Override
    public void registerCore() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "mobs"));
        MobRegistry.registerMob("sheep", SheepMob.class, true);
        MobRegistry.registerMob("ram", RamMob.class, true);
        MobRegistry.registerMob("wildostrich", WildOstrichMob.class, true);
        MobRegistry.registerMob("cow", CowMob.class, true);
        MobRegistry.registerMob("bull", BullMob.class, true);
        MobRegistry.registerMob("pig", PigMob.class, true);
        MobRegistry.registerMob("boar", BoarMob.class, true);
        MobRegistry.registerMob("chicken", ChickenMob.class, true);
        MobRegistry.registerMob("rooster", RoosterMob.class, true);
        MobRegistry.registerMob("honeybee", HoneyBeeMob.class, true, false, new LocalMessage("item", "honeybee"), null);
        MobRegistry.registerMob("queenbee", QueenBeeMob.class, true, false, new LocalMessage("item", "queenbee"), null);
        MobRegistry.registerMob("penguin", PenguinMob.class, true);
        MobRegistry.registerMob("polarbear", PolarBearMob.class, true);
        MobRegistry.registerMob("grizzlybear", GrizzlyBearMob.class, true);
        MobRegistry.registerMob("crocodile", CrocodileMob.class, false);
        MobRegistry.registerMob("rabbit", RabbitMob.class, true);
        MobRegistry.registerMob("squirrel", SquirrelMob.class, true);
        MobRegistry.registerMob("snowhare", SnowHareMob.class, true);
        MobRegistry.registerMob("crab", CrabMob.class, true);
        MobRegistry.registerMob("scorpion", ScorpionMob.class, true);
        MobRegistry.registerMob("turtle", TurtleMob.class, true);
        MobRegistry.registerMob("swampslug", SwampSlugMob.class, true);
        MobRegistry.registerMob("frog", FrogMob.class, true);
        MobRegistry.registerMob("duck", DuckMob.class, true);
        MobRegistry.registerMob("bird", BirdMob.class, true);
        MobRegistry.registerMob("bluebird", BluebirdMob.class, true);
        MobRegistry.registerMob("canarybird", CanaryBirdMob.class, true);
        MobRegistry.registerMob("cardinalbird", CardinalBirdMob.class, true);
        MobRegistry.registerMob("spider", SpiderCritterMob.class, true);
        MobRegistry.registerMob("mouse", MouseMob.class, true);
        MobRegistry.registerMob("beetcavecroppler", BeetCaveCroppler.class, true);
        MobRegistry.registerMob("stonecaveling", StoneCaveling.class, true, false, null);
        MobRegistry.registerMob("snowstonecaveling", SnowStoneCaveling.class, true, false, null);
        MobRegistry.registerMob("granitecaveling", GraniteCaveling.class, true, false, null);
        MobRegistry.registerMob("swampstonecaveling", SwampStoneCaveling.class, true, false, null);
        MobRegistry.registerMob("sandstonecaveling", SandStoneCaveling.class, true, false, null);
        MobRegistry.registerMob("deepstonecaveling", DeepStoneCaveling.class, true, false, null);
        MobRegistry.registerMob("deepsnowstonecaveling", DeepSnowStoneCaveling.class, true, false, null);
        MobRegistry.registerMob("dryadcaveling", DryadCaveling.class, true, false, null);
        MobRegistry.registerMob("deepswampstonecaveling", DeepSwampStoneCaveling.class, true, false, null);
        MobRegistry.registerMob("deepsandstonecaveling", DeepSandStoneCaveling.class, true, false, null);
        MobRegistry.registerMob("flameling", IncursionFlamelingMob.class, true, false, null);
        MobRegistry.registerMob("incursioncaveling", IncursionCaveling.class, true, false, null);
        MobRegistry.registerMob("human", GenericHumanMob.class, true);
        MobRegistry.registerMob("farmerhuman", FarmerHumanMob.class, true);
        MobRegistry.registerMob("blacksmithhuman", BlacksmithHumanMob.class, true);
        MobRegistry.registerMob("guardhuman", GuardHumanMob.class, true);
        MobRegistry.registerMob("magehuman", MageHumanMob.class, true);
        MobRegistry.registerMob("gunsmithhuman", GunsmithHumanMob.class, true);
        MobRegistry.registerMob("alchemisthuman", AlchemistHumanMob.class, true);
        MobRegistry.registerMob("hunterhuman", HunterHumanMob.class, true);
        MobRegistry.registerMob("elderhuman", ElderHumanMob.class, true);
        MobRegistry.registerMob("anglerhuman", AnglerHumanMob.class, true);
        MobRegistry.registerMob("pawnbrokerhuman", PawnBrokerHumanMob.class, true);
        MobRegistry.registerMob("animalkeeperhuman", AnimalKeeperHumanMob.class, true);
        MobRegistry.registerMob("stylisthuman", StylistHumanMob.class, true);
        MobRegistry.registerMob("piratehuman", PirateHumanMob.class, true);
        MobRegistry.registerMob("explorerhuman", ExplorerHumanMob.class, true);
        MobRegistry.registerMob("minerhuman", MinerHumanMob.class, true);
        MobRegistry.registerMob("traderhuman", TraderHumanMob.class, true);
        MobRegistry.registerMob("exoticmerchanthuman", ExoticMerchantHumanMob.class, true);
        MobRegistry.registerMob("friendlywitchhuman", FriendlyWitchHumanMob.class, true);
        MobRegistry.registerMob("evilwitch", EvilWitchStartMob.class, true, false);
        MobRegistry.registerMob("evilwitchflask", EvilWitchNecroticFlaskMob.class, false, false, new LocalMessage("mob", "evilwitch"), null);
        MobRegistry.registerMob("evilwitchbow", EvilWitchNecroticBowMob.class, false, false, new LocalMessage("mob", "evilwitch"), null);
        MobRegistry.registerMob("evilwitchgreatsword", EvilWitchNecroticGreatswordMob.class, false, false, new LocalMessage("mob", "evilwitch"), null);
        MobRegistry.registerMob("zombie", ZombieMob.class, true);
        MobRegistry.registerMob("trapperzombie", TrapperZombieMob.class, true);
        MobRegistry.registerMob("goblin", GoblinMob.class, true);
        MobRegistry.registerMob("trenchcoatgoblinstacked", TrenchcoatGoblinStackedMob.class, true);
        MobRegistry.registerMob("trenchcoatgoblinhelmet", TrenchcoatGoblinHelmetMob.class, true, false, false);
        MobRegistry.registerMob("trenchcoatgoblinchestplate", TrenchcoatGoblinChestplateMob.class, true, false, false);
        MobRegistry.registerMob("trenchcoatgoblinshoes", TrenchcoatGoblinShoesMob.class, true, false, false);
        MobRegistry.registerMob("vampire", VampireMob.class, true);
        MobRegistry.registerMob("zombiearcher", ZombieArcherMob.class, true);
        MobRegistry.registerMob("crawlingzombie", CrawlingZombieMob.class, true);
        MobRegistry.registerMob("giantcavespider", GiantCaveSpiderMob.class, true);
        MobRegistry.registerMob("blackcavespider", BlackCaveSpiderMob.class, true);
        MobRegistry.registerMob("swampcavespider", SwampCaveSpiderMob.class, true);
        MobRegistry.registerMob("cavemole", CaveMoleMob.class, true);
        MobRegistry.registerMob("frozendwarf", FrozenDwarfMob.class, true);
        MobRegistry.registerMob("frostsentry", FrostSentryMob.class, true);
        MobRegistry.registerMob("swampzombie", SwampZombieMob.class, true);
        MobRegistry.registerMob("swampslime", SwampSlimeMob.class, true);
        MobRegistry.registerMob("swampshooter", SwampShooterMob.class, true);
        MobRegistry.registerMob("enchantedzombie", EnchantedZombieMob.class, true);
        MobRegistry.registerMob("enchantedzombiearcher", EnchantedZombieArcherMob.class, true);
        MobRegistry.registerMob("enchantedcrawlingzombie", EnchantedCrawlingZombieMob.class, true);
        MobRegistry.registerMob("voidapprentice", VoidApprentice.class, true);
        MobRegistry.registerMob("mummy", MummyMob.class, true);
        MobRegistry.registerMob("mummymage", MummyMageMob.class, true);
        MobRegistry.registerMob("sandspirit", SandSpiritMob.class, true);
        MobRegistry.registerMob("jackal", JackalMob.class, true);
        MobRegistry.registerMob("giantscorpion", GiantScorpionMob.class, false);
        MobRegistry.registerMob("skeleton", SkeletonMob.class, true);
        MobRegistry.registerMob("skeletonthrower", SkeletonThrowerMob.class, true);
        MobRegistry.registerMob("skeletonmage", SkeletonMageMob.class, false);
        MobRegistry.registerMob("deepcavespirit", DeepCaveSpiritMob.class, true);
        MobRegistry.registerMob("skeletonminer", SkeletonMinerMob.class, true);
        MobRegistry.registerMob("ninja", NinjaMob.class, true);
        MobRegistry.registerMob("snowwolf", SnowWolfMob.class, true);
        MobRegistry.registerMob("cryoflake", CryoFlakeMob.class, true);
        MobRegistry.registerMob("giantswampslime", GiantSwampSlimeMob.class, true);
        MobRegistry.registerMob("swampskeleton", SwampSkeletonMob.class, true);
        MobRegistry.registerMob("smallswampcavespider", SmallCaveSpiderMob.class, true);
        MobRegistry.registerMob("swampdweller", SwampDwellerMob.class, true);
        MobRegistry.registerMob("sandworm", SandwormHead.class, true);
        MobRegistry.registerMob("sandwormbody", SandwormBody.class, false);
        MobRegistry.registerMob("sandwormtail", SandwormTail.class, false);
        MobRegistry.registerMob("desertcrawler", DesertCrawlerMob.class, true);
        MobRegistry.registerMob("ancientskeleton", AncientSkeletonMob.class, true);
        MobRegistry.registerMob("ancientskeletonthrower", AncientSkeletonThrowerMob.class, true);
        MobRegistry.registerMob("ancientarmoredskeleton", AncientArmoredSkeletonMob.class, true);
        MobRegistry.registerMob("ancientskeletonmage", AncientSkeletonMageMob.class, true);
        MobRegistry.registerMob("leggedslimethrower", LeggedSlimeThrowerMob.class, true);
        MobRegistry.registerMob("mageslime", MageSlimeMob.class, true);
        MobRegistry.registerMob("ghostslime", GhostSlimeMob.class, true);
        MobRegistry.registerMob("warriorslime", WarriorSlimeMob.class, true);
        MobRegistry.registerMob("slimeworm", SlimeWormHead.class, true);
        MobRegistry.registerMob("slimewormbody", SlimeWormBody.class, false);
        MobRegistry.registerMob("cryptbat", CryptBatMob.class, true);
        MobRegistry.registerMob("phantom", PhantomMob.class, true);
        MobRegistry.registerMob("cryptvampire", CryptVampireMob.class, true);
        MobRegistry.registerMob("webspinner", WebSpinnerMob.class, true);
        MobRegistry.registerMob("bloatedspider", BloatedSpiderMob.class, true);
        MobRegistry.registerMob("spiderkin", SpiderkinMob.class, true);
        MobRegistry.registerMob("spiderkinwarrior", SpiderkinWarriorMob.class, true);
        MobRegistry.registerMob("spiderkinarcher", SpiderkinArcherMob.class, true);
        MobRegistry.registerMob("spiderkinmage", SpiderkinMageMob.class, true);
        MobRegistry.registerMob("crystalgolem", CrystalGolemMob.class, true);
        MobRegistry.registerMob("crystalarmadillo", CrystalArmadillo.class, true);
        MobRegistry.registerMob("fishianhookwarrior", FishianHookWarriorMob.class, true);
        MobRegistry.registerMob("fishianhealer", FishianHealerMob.class, true);
        MobRegistry.registerMob("fishianshaman", FishianShamanMob.class, false, false, false);
        MobRegistry.registerMob("staticjellyfish", StaticJellyfishMob.class, true);
        MobRegistry.registerMob("agedchampion", AgedChampionMob.class, true);
        MobRegistry.registerMob("stabbybush", StabbyBushMob.class, true);
        MobRegistry.registerMob("bashybush", BashyBushMob.class, true);
        MobRegistry.registerMob("mosquitoegg", MosquitoEggMob.class, true);
        MobRegistry.registerMob("mosquito", MosquitoMob.class, true);
        MobRegistry.registerMob("runeboundbrute", RuneboundBruteMob.class, true);
        MobRegistry.registerMob("runeboundshaman", RuneboundShamanMob.class, true);
        MobRegistry.registerMob("runeboundtrapper", RuneboundTrapperMob.class, true);
        MobRegistry.registerMob("bonewalker", BoneWalkerMob.class, true);
        MobRegistry.registerMob("forestspector", ForestSpectorMob.class, true);
        MobRegistry.registerMob("dryadsentinel", DryadSentinelMob.class, true);
        MobRegistry.registerMob("spiritghoul", SpiritGhoulMob.class, true);
        MobRegistry.registerMob("flamelingshooter", FlamelingShooterMob.class, false, false);
        MobRegistry.registerMob("crazedraven", CrazedRavenMob.class, true);
        MobRegistry.registerMob("arcanicpylon", ArcanicPylonMob.class, true);
        MobRegistry.registerMob("battlechef", BattleChefMob.class, true);
        MobRegistry.registerMob("incursioncavelingsspawnerportal", IncursionCavelingsSpawnerPortalMob.class, false);
        MobRegistry.registerMob("possessedsettler", PossessedSettlerMob.class, true);
        int unknownRaiderID = MobRegistry.registerMob("unknownraider", HumanRaiderMob.class, false);
        MobRegistry.registerMob("humanraider", HumanRaiderMob.class, true);
        MobRegistry.registerMob("fishianraider", FishianRaiderMob.class, true);
        MobRegistry.registerMob("ninjaraider", NinjaRaiderMob.class, true);
        MobRegistry.registerMob("chickenraider", ChickenRaiderMob.class, true);
        MobRegistry.registerMob("roguehunterraider", RogueHunterRaiderMob.class, true);
        MobRegistry.registerMob("frozendwarfraider", FrozenDwarfRaiderMob.class, true);
        MobRegistry.registerMob("voidapprenticeraider", VoidApprenticeRaiderMob.class, true);
        MobRegistry.registerMob("vampireraider", VampireRaiderMob.class, true);
        MobRegistry.registerMob("mummyraider", MummyRaiderMob.class, true);
        MobRegistry.registerMob("pirateraider", PirateRaiderMob.class, true);
        MobRegistry.registerMob("mafiaraider", TheMafiaRaiderMob.class, true);
        MobRegistry.registerMob("ancientskeletonraider", AncientSkeletonRaiderMob.class, true);
        MobRegistry.registerMob("tameostrich", TameOstrichMob.class, false);
        MobRegistry.registerMob("seahorse", SeahorseMob.class, false);
        MobRegistry.registerMob("petpenguin", PetPenguinMob.class, false);
        MobRegistry.registerMob("petparrot", PetParrotMob.class, false);
        MobRegistry.registerMob("minecartmount", MinecartMountMob.class, false, false, new LocalMessage("mob", "minecart"), null);
        MobRegistry.registerMob("woodboatmount", WoodBoatMountMob.class, false, false, new LocalMessage("mob", "woodboat"), null);
        MobRegistry.registerMob("steelboat", SteelBoatMob.class, false);
        MobRegistry.registerMob("runeboundboat", RuneboundBoatMob.class, false);
        MobRegistry.registerMob("petwalkingtorch", PetWalkingTorchMob.class, false);
        MobRegistry.registerMob("jumpingball", JumpingBallMob.class, false);
        MobRegistry.registerMob("witchbroom", WitchBroomMob.class, false);
        MobRegistry.registerMob("hoverboard", HoverboardMob.class, false);
        MobRegistry.registerMob("petcavelingelder", PetCavelingElder.class, false);
        MobRegistry.registerMob("petgrizzlybearcub", PetGrizzlyBearCub.class, false);
        MobRegistry.registerMob("petevilminion", PetEvilMinion.class, false);
        MobRegistry.registerMob("willowisp", WillOWispMob.class, false);
        MobRegistry.registerMob("petpug", PetPugMob.class, false);
        MobRegistry.registerMob("ghostlybow", GhostlyBowFollowingMob.class, false);
        MobRegistry.registerMob("babyzombie", BabyZombieMob.class, false);
        MobRegistry.registerMob("babyzombiearcher", BabyZombieArcherMob.class, false);
        MobRegistry.registerMob("babyspider", BabySpiderMob.class, false);
        MobRegistry.registerMob("frostpiercer", FrostPiercerFollowingMob.class, false);
        MobRegistry.registerMob("sentientsword", SentientSwordFollowingMob.class, false);
        MobRegistry.registerMob("babysnowman", BabySnowmanMob.class, false);
        MobRegistry.registerMob("playerpoisonslime", PoisonSlimeFollowingMob.class, false);
        MobRegistry.registerMob("babycrawlingzombie", BabyCrawlingZombieFollowingMob.class, false);
        MobRegistry.registerMob("stabbybushfollowingmob", StabbyBushFollowingMob.class, false);
        MobRegistry.registerMob("bashybushfollowingmob", BashyBushFollowingMob.class, false);
        MobRegistry.registerMob("playervulturehatchling", VultureHatchlingFollowingMob.class, false);
        MobRegistry.registerMob("playerghostship", GhostShipFollowingMob.class, false);
        MobRegistry.registerMob("playerreaperspirit", ReaperSpiritFollowingMob.class, false);
        MobRegistry.registerMob("playercryoflake", CryoFlakeFollowingMob.class, false);
        MobRegistry.registerMob("dryadspirit", DryadSpiritFollowingMob.class, false);
        MobRegistry.registerMob("babydryad", BabyDryadMob.class, false);
        MobRegistry.registerMob("playerpouncingslime", PouncingSlimeFollowingMob.class, false);
        MobRegistry.registerMob("slimegreatswordslime", SlimeGreatswordFollowingMob.class, false);
        MobRegistry.registerMob("skeletonfollowing", SkeletonFollowingMob.class, false);
        MobRegistry.registerMob("ancestorknight", AncestorKnightFollowingMob.class, false);
        MobRegistry.registerMob("ancestormage", AncestorMageFollowingMob.class, false);
        MobRegistry.registerMob("babyskeleton", BabySkeletonMob.class, false);
        MobRegistry.registerMob("babyskeletonmage", BabySkeletonMageMob.class, false);
        MobRegistry.registerMob("playerchargingphantom", ChargingPhantomFollowingMob.class, false);
        MobRegistry.registerMob("orbofslimesslime", OrbOfSlimesFollowingMob.class, false);
        MobRegistry.registerMob("babyspiderkinwarrior", BabySpiderkinWarrior.class, false);
        MobRegistry.registerMob("babyspiderkinarcher", BabySpiderkinArcher.class, false);
        MobRegistry.registerMob("ravenlordfeather", RavenLordFeatherFollowingMob.class, false);
        MobRegistry.registerMob("duskmoondisc", DuskMoonDiscFollowingMob.class, false);
        MobRegistry.registerMob("rubyshield", RubyShieldFollowingMob.class, false);
        MobRegistry.registerMob("rubydragonhead", RubyDragonHeadFollowingMob.class, false);
        MobRegistry.registerMob("rubydragonbody", RubyDragonBodyFollowingMob.class, false);
        MobRegistry.registerMob("arachnidspider", ArachnidSpiderMob.class, false);
        MobRegistry.registerMob("locust", LocustFollowingMob.class, false);
        MobRegistry.registerMob("evilsprotector", EvilsProtectorMob.class, true, true, new LocalMessage("quests", "evilsprotectortip"));
        MobRegistry.registerMob("evilsportal", EvilsPortalMob.class, true);
        MobRegistry.registerMob("portalminion", PortalMinion.class, true);
        MobRegistry.registerMob("queenspider", QueenSpiderMob.class, true, true, new LocalMessage("quests", "queenspidertip"));
        MobRegistry.registerMob("spiderhatchling", SpiderHatchlingMob.class, true);
        MobRegistry.registerMob("voidwizard", VoidWizard.class, true, true, new LocalMessage("quests", "voidwizardtip"));
        MobRegistry.registerMob("voidwizardclone", VoidWizardClone.class, false, false, new LocalMessage("mob", "voidwizard"), null);
        MobRegistry.registerMob("chieftain", ChieftainMob.class, true, true, new LocalMessage("quests", "chieftaintip"));
        MobRegistry.registerMob("chieftaingauntletspawnerportal", ChieftainGauntletSpawnerPortalMob.class, false);
        MobRegistry.registerMob("crone", CroneMob.class, false);
        MobRegistry.registerMob("swampguardian", SwampGuardianHead.class, true, true, new LocalMessage("quests", "swampguardiantip"));
        MobRegistry.registerMob("swampguardianbody", SwampGuardianBody.class, false, true);
        MobRegistry.registerMob("swampguardiantail", SwampGuardianTail.class, false, true);
        MobRegistry.registerMob("ancientvulture", AncientVultureMob.class, true, true, new LocalMessage("quests", "ancientvulturetip"));
        MobRegistry.registerMob("ancientvultureegg", AncientVultureEggMob.class, true);
        MobRegistry.registerMob("vulturehatchling", VultureHatchling.class, true);
        MobRegistry.registerMob("piratecaptain", PirateCaptainMob.class, true, true, new LocalMessage("quests", "piratecaptaintip"));
        MobRegistry.registerMob("piraterecruit", PirateRecruit.class, true);
        MobRegistry.registerMob("pirateparrot", PirateParrotMob.class, true);
        MobRegistry.registerMob("reaper", ReaperMob.class, true, true, new LocalMessage("quests", "reapertip"));
        MobRegistry.registerMob("reaperspiritportal", ReaperSpiritPortalMob.class, true, false, false);
        MobRegistry.registerMob("reaperspirit", ReaperSpiritMob.class, true);
        MobRegistry.registerMob("cryoqueen", CryoQueenMob.class, true, true, new LocalMessage("quests", "cryoqueentip"));
        MobRegistry.registerMob("thecursedcrone", TheCursedCroneMob.class, true, true, new LocalMessage("quests", "thecursedcronetip"));
        MobRegistry.registerMob("cursedcronespiritghoul", CursedCroneSpiritGhoulMob.class, true, false, false);
        MobRegistry.registerMob("spirittornado", SpiritTornadoMob.class, false);
        MobRegistry.registerMob("pestwarden", PestWardenHead.class, true, true, new LocalMessage("quests", "pestwardentip"));
        MobRegistry.registerMob("pestwardenbody", PestWardenBody.class, true, true, false);
        MobRegistry.registerMob("grit", GritHead.class, true, true);
        MobRegistry.registerMob("sage", SageHead.class, true, true);
        MobRegistry.registerMob("flyingspiritsbody", FlyingSpiritsBody.class, false, true);
        MobRegistry.registerMob("sageandgrit", SageAndGritStartMob.class, true, true, new LocalMessage("quests", "sageandgrittip"));
        MobRegistry.registerMob("fallenwizard", FallenWizardMob.class, true, true, new LocalMessage("quests", "fallenwizardtip"));
        MobRegistry.registerMob("fallenwizardghost", FallenWizardGhostMob.class, false, false, new LocalMessage("mob", "fallenwizard"), null);
        MobRegistry.registerMob("fallendragon", FallenDragonHead.class, true);
        MobRegistry.registerMob("fallendragonbody", FallenDragonBody.class, false);
        MobRegistry.registerMob("motherslime", MotherSlimeMob.class, true, true);
        MobRegistry.registerMob("nightswarm", NightSwarmStartMob.class, true, true);
        MobRegistry.registerMob("nightswarmbat", NightSwarmBatMob.class, true, false, false);
        MobRegistry.registerMob("spiderempress", SpiderEmpressMob.class, true, true);
        MobRegistry.registerMob("sunlightchampion", SunlightChampionMob.class, true, true);
        MobRegistry.registerMob("sunlightgauntlet", SunlightChampionMob.SunlightGauntletMob.class, false, false);
        MobRegistry.registerMob("moonlightdancer", MoonlightDancerMob.class, true, true);
        MobRegistry.registerMob("crystaldragon", CrystalDragonHead.class, true, true);
        MobRegistry.registerMob("crystaldragonbody", CrystalDragonBody.class, true, true, false);
        MobRegistry.registerMob("ascendedwizard", AscendedWizardMob.class, true, true);
        MobRegistry.registerMob("ascendedgauntlet", AscendedGauntletMob.class, false, false);
        MobRegistry.registerMob("ascendedgolem", AscendedGolemMob.class, false, false, false);
        MobRegistry.registerMob("ascendedbat", AscendedBatMob.class, false, false, false);
        MobRegistry.registerMob("ascendedbeam", AscendedBeamMob.class, false);
        MobRegistry.registerMob("ascendedpylondummy", AscendedPylonDummyMob.class, false, false, new LocalMessage("object", "ascendedpylon"), null);
        MobRegistry.registerMob("ascendedwizardperipheral", AscendedWizardPeripheralMob.class, false, false, new LocalMessage("mob", "ascendedwizard"), null);
        MobRegistry.registerMob("thevoid", TheVoidMob.class, true, true);
        MobRegistry.registerMob("thevoidhorn", TheVoidHornMob.class, false, false);
        MobRegistry.registerMob("thevoidclaw", TheVoidClawMob.class, false, false);
        MobRegistry.registerMob("wanderbotfollowingmob", WanderbotFollowingMob.class, false, false);
        MobRegistry.registerMob("incursioncrawlingzombie", IncursionCrawlingZombieMob.class, true);
        MobRegistry.registerMob("woodboat", WoodBoatMob.class, false);
        MobRegistry.registerMob("minecart", MinecartMob.class, false);
        MobRegistry.registerMob("sawblade", SawBladeMob.class, false);
        MobRegistry.registerMob("trainingdummy", TrainingDummyMob.class, false, false, new LocalMessage("object", "trainingdummy"), null);
        MobRegistry.registerMob("bannerofwardummy", BannerOfWarDummyMob.class, false, false, new StaticMessage("BANNER_OF_WAR_DUMMY"), null);
        MobRegistry.registerMob("swampsporedummy", SwampSporeDummyMob.class, false, false, new StaticMessage("SWAMP_SPORE_DUMMY"), null);
        MobRegistry.registerMob("projectilehitbox", ProjectileHitboxMob.class, false, false, new StaticMessage("PROJECTILE_HITBOX"), null);
        MobRegistry.registerMob("lifeessencefollower", LifeEssenceFollowingMob.class, false);
        MobRegistry.registerMob("homeportal", HomePortalMob.class, false);
        MobRegistry.registerMob("returnportal", ReturnPortalMob.class, false);
        MobRegistry.registerMob("bossspawnportal", BossSpawnPortalMob.class, false);
        MobRegistry.registerMob("arenaentranceportal", ArenaEntrancePortalMob.class, false);
        MobRegistry.registerMob("bonespike", BoneSpikeMob.class, false);
        MobRegistry.registerMob("earthspike", EarthSpikeMob.class, false);
        MobRegistry.registerMob("chickenpolymorph", ChickenPolymorphMob.class, false, false, new LocalMessage("mob", "chicken"), null);
        MobRegistry.registerMob("frogpolymorph", FrogPolymorphMob.class, false, false, new LocalMessage("mob", "frog"), null);
        MobRegistry.registerMob("rabbitpolymorph", RabbitPolymorphMob.class, false, false, new LocalMessage("mob", "rabbit"), null);
        MobRegistry.registerMob("duckpolymorph", DuckPolymorphMob.class, false, false, new LocalMessage("mob", "duck"), null);
        MobRegistry.registerMob("roosterpolymorph", RoosterPolymorphMob.class, false, false, new LocalMessage("mob", "rooster"), null);
        MobRegistry.registerMob("turtlepolymorph", TurtlePolymorphMob.class, false, false, new LocalMessage("mob", "turtle"), null);
        MobRegistry.registerMob("squirrelpolymorph", SquirrelPolymorphMob.class, false, false, new LocalMessage("mob", "squirrel"), null);
    }

    @Override
    protected void onRegister(MobRegistryElement object, int id, String stringID, boolean isReplace) {
        super.onRegister(object, id, stringID, isReplace);
        if (object.createSpawnItem) {
            MobSpawnItem spawnItem = new MobSpawnItem(1, true, stringID);
            int spawnItemID = ItemRegistry.registerItem(stringID + "spawnitem", (Item)spawnItem, 50.0f, false, false, true, new String[0]);
            mobIDToSpawnItemIDMap.put(id, spawnItemID);
        }
    }

    @Override
    protected void onRegistryClose() {
    }

    public static GameMessage getLocalization(int id) {
        if (id == -1) {
            return new StaticMessage("N/A");
        }
        return ((MobRegistryElement)MobRegistry.instance.getElement((int)id)).displayName;
    }

    public static GameMessage getLocalization(String stringID) {
        return MobRegistry.getLocalization(MobRegistry.getMobID(stringID));
    }

    public static String getDisplayName(int id) {
        if (id == -1) {
            return null;
        }
        return MobRegistry.getLocalization(id).translate();
    }

    public static GameMessage getKillHintLocalization(int id) {
        if (id == -1) {
            return null;
        }
        return ((MobRegistryElement)MobRegistry.instance.getElement((int)id)).killHint;
    }

    public static GameMessage getKillHintLocalization(String stringID) {
        return MobRegistry.getKillHintLocalization(MobRegistry.getMobID(stringID));
    }

    public static String getKillHint(int id) {
        GameMessage localization = MobRegistry.getKillHintLocalization(id);
        return localization != null ? localization.translate() : null;
    }

    public static int registerMob(String stringID, Class<? extends Mob> mobClass, boolean countKillStat) {
        return MobRegistry.registerMob(stringID, mobClass, countKillStat, false, countKillStat);
    }

    public static int registerMob(String stringID, Class<? extends Mob> mobClass, boolean countKillStat, boolean isBossMob, boolean createSpawnItem) {
        return MobRegistry.registerMob(stringID, mobClass, countKillStat, isBossMob, new LocalMessage("mob", stringID), null, createSpawnItem);
    }

    public static int registerMob(String stringID, Class<? extends Mob> mobClass, boolean countKillStat, boolean isBossMob) {
        return MobRegistry.registerMob(stringID, mobClass, countKillStat, isBossMob, new LocalMessage("mob", stringID), null);
    }

    public static int registerMob(String stringID, Class<? extends Mob> mobClass, boolean countKillStat, boolean isBossMob, GameMessage killHint) {
        return MobRegistry.registerMob(stringID, mobClass, countKillStat, isBossMob, new LocalMessage("mob", stringID), killHint);
    }

    public static int registerMob(String stringID, Class<? extends Mob> mobClass, boolean countKillStat, boolean isBossMob, GameMessage displayName, GameMessage killHint) {
        return MobRegistry.registerMob(stringID, mobClass, countKillStat, isBossMob, displayName, killHint, countKillStat);
    }

    public static int registerMob(String stringID, Class<? extends Mob> mobClass, boolean countKillStat, boolean isBossMob, GameMessage displayName, GameMessage killHint, boolean createSpawnItem) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register mobs");
        }
        try {
            return instance.register(stringID, new MobRegistryElement(mobClass, countKillStat, isBossMob, createSpawnItem, displayName, killHint));
        }
        catch (NoSuchMethodException e) {
            System.err.println("Could not register mob " + mobClass.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
        }
    }

    @Deprecated
    public static Mob getMob(int id) {
        try {
            return (Mob)((MobRegistryElement)instance.getElement(id)).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Mob getMob(int id, Level level) {
        try {
            Mob out = (Mob)((MobRegistryElement)instance.getElement(id)).newInstance(new Object[0]);
            if (out != null) {
                out.onConstructed(level);
            }
            return out;
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public static Mob getMob(String stringID) {
        return MobRegistry.getMob(MobRegistry.getMobID(stringID));
    }

    public static Mob getMob(String stringID, Level level) {
        return MobRegistry.getMob(MobRegistry.getMobID(stringID), level);
    }

    public static int getMobID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static int getMobID(Class<? extends Mob> clazz) {
        return instance.getElementID(clazz);
    }

    public static GameTexture getMobIcon(String stringID) {
        return MobRegistry.getMobIcon(MobRegistry.getMobID(stringID));
    }

    public static GameTexture getMobIcon(int id) {
        if (id == -1) {
            return GameResources.error;
        }
        return ((MobRegistryElement)MobRegistry.instance.getElement((int)id)).icon;
    }

    public static boolean mobExists(String stringID) {
        try {
            return instance.getElementIDRaw(stringID) >= 0;
        }
        catch (NoSuchElementException e) {
            return false;
        }
    }

    public static String getMobStringID(int id) {
        return instance.getElementStringID(id);
    }

    public static boolean countMobKillStat(int id) {
        if (id < 0 || id >= instance.size()) {
            return false;
        }
        return ((MobRegistryElement)MobRegistry.instance.getElement((int)id)).countKillStat;
    }

    public static boolean isBossMob(int id) {
        if (id < 0 || id >= instance.size()) {
            return false;
        }
        return ((MobRegistryElement)MobRegistry.instance.getElement((int)id)).isBossMob;
    }

    public static Stream<ClassIDDataContainer<Mob>> streamMobs() {
        return instance.streamElements().map(e -> e);
    }

    public static List<ClassIDDataContainer<Mob>> getMobs() {
        return MobRegistry.streamMobs().collect(Collectors.toList());
    }

    public static void loadMobIcons() {
        for (MobRegistryElement element : instance.getElements()) {
            element.loadIcon();
        }
    }

    public static MobSpawnItem getMobSpawnItemID(String stringID) {
        return MobRegistry.getMobSpawnItemID(MobRegistry.getMobID(stringID));
    }

    public static MobSpawnItem getMobSpawnItemID(int id) {
        Integer spawnItemID = mobIDToSpawnItemIDMap.get(id);
        if (spawnItemID == null) {
            return null;
        }
        return (MobSpawnItem)ItemRegistry.getItem(spawnItemID);
    }

    protected static class MobRegistryElement
    extends ClassIDDataContainer<Mob> {
        public boolean countKillStat;
        public boolean isBossMob;
        public boolean createSpawnItem;
        public GameMessage displayName;
        public GameMessage killHint;
        public GameTexture icon;

        public MobRegistryElement(Class<? extends Mob> mobClass, boolean countKillStat, boolean isBossMob, boolean createSpawnItem, GameMessage displayName, GameMessage killHint) throws NoSuchMethodException {
            super(mobClass, new Class[0]);
            this.countKillStat = countKillStat;
            this.isBossMob = isBossMob;
            this.createSpawnItem = createSpawnItem;
            this.displayName = displayName;
            this.killHint = killHint;
        }

        public void loadIcon() {
            this.icon = GameTexture.fromFile("mobs/icons/" + this.getStringID());
        }
    }

    public static class Textures {
        public static GameTexture human_shadow;
        public static GameTexture human_baby_shadow;
        public static GameTexture human_big_shadow;
        public static GameTexture small_shadow;
        public static GameTexture human_enchained_iron;
        public static GameTexture sheep;
        public static GameTexture sheep_sheared;
        public static GameTexture sheep_shadow;
        public static GameTexture ram;
        public static GameTexture ram_sheared;
        public static GameTexture lamb;
        public static GameTexture lamb_shadow;
        public static GameTexture ostrich;
        public static GameTexture ostrichMount;
        public static GameTexture ostrich_shadow;
        public static GameTexture seahorse_front;
        public static GameTexture seahorse_back;
        public static GameTexture seahorse_shadow;
        public static GameTexture cow;
        public static GameTexture cow_shadow;
        public static GameTexture bull;
        public static GameTexture calf;
        public static GameTexture calf_shadow;
        public static GameTexture pig;
        public static GameTexture pig_shadow;
        public static GameTexture boar;
        public static GameTexture piglet;
        public static GameTexture piglet_shadow;
        public static GameTexture penguin;
        public static GameTexture chicken;
        public static GameTexture chicken_shadow;
        public static GameTexture rooster;
        public static GameTexture rooster_shadow;
        public static GameTexture chick;
        public static GameTexture chick_shadow;
        public static MobTexture honeyBee;
        public static MobTexture queenBee;
        public static GameTexture rabbit;
        public static GameTexture squirrel;
        public static GameTexture snowHare;
        public static GameTexture swampSlug;
        public static GameTexture bird_shadow;
        public static GameTexture bird;
        public static GameTexture bluebird;
        public static GameTexture canaryBird;
        public static GameTexture cardinalBird;
        public static MobTexture crab;
        public static MobTexture scorpion;
        public static MobTexture turtle;
        public static MobTexture duck;
        public static MobTexture frog;
        public static MobTexture spider;
        public static MobTexture mouse;
        public static MobTexture beetCaveCroppler;
        public static HumanTexture stoneCaveling;
        public static HumanTexture snowStoneCaveling;
        public static HumanTexture graniteCaveling;
        public static HumanTexture swampStoneCaveling;
        public static HumanTexture sandStoneCaveling;
        public static HumanTexture flameling;
        public static HumanTexture deepStoneCaveling;
        public static HumanTexture deepSnowStoneCaveling;
        public static HumanTexture dryadCaveling;
        public static HumanTexture deepSwampStoneCaveling;
        public static HumanTexture deepSandStoneCaveling;
        public static HumanTexture incursionCaveling;
        public static GameTexture caveling_shadow;
        public static HumanTexture zombie;
        public static HumanTexture zombieArcher;
        public static HumanTexture zombieArcherWithBow;
        public static HumanTexture trapperZombie;
        public static HumanTexture swampZombie;
        public static HumanTexture trenchcoatgoblin_stacked;
        public static GameTexture goblin;
        public static GameTexture trenchcoatgoblin_shoes;
        public static GameTexture trenchcoatgoblin_chestplate;
        public static GameTexture trenchcoatgoblin_helmet;
        public static HumanTexture enchantedZombie;
        public static HumanTexture enchantedZombieArcher;
        public static HumanTexture enchantedZombieArcherWithBow;
        public static HumanTexture ninja;
        public static HumanTexture vampire;
        public static HumanTexture frozenDwarf;
        public static HumanTexture voidApprentice;
        public static HumanTexture mummy;
        public static HumanTexture mummyMage;
        public static HumanTexture skeleton;
        public static HumanTexture skeletonMiner;
        public static HumanTexture skeletonMage;
        public static HumanTexture fishianHookWarrior;
        public static HumanTexture fishianHealer;
        public static HumanTexture fishianShaman;
        public static GameTexture voidApprentice_shadow;
        public static GameTexture frozenDwarfHair;
        public static GameTexture sandSpirit;
        public static GameTexture deepCaveSpirit;
        public static GameTexture swampSlime;
        public static GameTexture swampSlime_shadow;
        public static GameTexture cryoFlake;
        public static GameTexture cryoFlakePet;
        public static MobTexture crawlingZombie;
        public static MobTexture enchantedCrawlingZombie;
        public static MobTexture giantCaveSpider;
        public static MobTexture giantSnowCaveSpider;
        public static MobTexture giantSwampCaveSpider;
        public static MobTexture smallSwampCaveSpider;
        public static MobTexture jackal;
        public static MobTexture snowWolf;
        public static MobTexture giantScorpion;
        public static GameTexture swampShooter;
        public static GameTexture mole;
        public static GameTexture frostSentry;
        public static GameTexture giantSwampSlime;
        public static GameTexture giantSwampSlime_shadow;
        public static GameTexture sandWorm;
        public static GameTexture sandWorm_mask;
        public static GameTexture sandWorm_shadow;
        public static GameTexture desertCrawler;
        public static HumanTexture swampSkeleton;
        public static HumanTexture swampDweller;
        public static HumanTexture ancientSkeleton;
        public static HumanTexture ancientArmoredSkeleton;
        public static HumanTexture ancientSkeletonMage;
        public static MobTexture leggedSlime;
        public static MobTexture mageSlime;
        public static MobTexture ghostSlime;
        public static MobTexture warriorSlime;
        public static MobTexture slimeWorm;
        public static GameTexture slimeWorm_mask;
        public static GameTexture cryptBat;
        public static MobTexture phantom;
        public static HumanTexture cryptVampire;
        public static HumanTexture spiderkin;
        public static HumanTexture spiderkinWarrior;
        public static HumanTexture spiderkinArcher;
        public static HumanTexture spiderkinMage;
        public static GameTexture spiderkin_light;
        public static GameTexture spiderkinWarrior_light;
        public static GameTexture spiderkinArcher_light;
        public static GameTexture spiderkinMage_light;
        public static MobTexture webSpinner;
        public static MobTexture webSpinner_shadow;
        public static MobTexture bloatedSpider;
        public static MobTexture bloatedSpider_shadow;
        public static MobTexture staticJellyfish;
        public static GameTexture mosquitoEgg;
        public static GameTexture mosquito;
        public static GameTexture mosquito_shadow;
        public static HumanTexture crone;
        public static HumanTexture boneWalker;
        public static GameTexture forestSpector;
        public static GameTexture forestSpector_shadow;
        public static GameTexture dryadSentinel;
        public static GameTexture dryadSentinel_shadow;
        public static GameTexture flamelingShooter;
        public static HumanTexture crazedRaven;
        public static GameTexture arcanicPylon;
        public static GameTexture frostPiercer;
        public static GameTexture sentientSword;
        public static GameTexture sentientSword_shadow;
        public static GameTexture steelBoat;
        public static GameTexture runeboundBoat;
        public static GameTexture walkingTorch;
        public static GameTexture reaperSpiritPet;
        public static GameTexture jumpingBall;
        public static GameTexture jumpingBall_shadow;
        public static GameTexture cavelingElder;
        public static GameTexture poisonSlime;
        public static GameTexture poisonSlime_shadow;
        public static GameTexture dryadSpirit;
        public static GameTexture pouncingSlime;
        public static GameTexture pouncingSlime_shadow;
        public static GameTexture greatswordSlime;
        public static GameTexture greatswordSlime_shadow;
        public static MobTexture chargingPhantom;
        public static MobTexture orbOfSlimesSlime;
        public static MobTexture babySpider;
        public static MobTexture arachnidSpider;
        public static MobTexture babyCrawlingZombie;
        public static MobTexture babyDryad;
        public static MobTexture hoverBoard;
        public static MobTexture witchBroom;
        public static HumanTexture babyZombie;
        public static HumanTexture babyZombieArcher;
        public static HumanTexture babySnowman;
        public static HumanTexture babySkeleton;
        public static HumanTexture babySkeletonMage;
        public static HumanTexture babySpiderkinWarrior;
        public static HumanTexture babySpiderkinArcher;
        public static HumanTexture stabbyBush;
        public static HumanTexture bashyBush;
        public static HumanTexture ancestorKnight;
        public static HumanTexture ancestorMage;
        public static GameTexture duskMoonDisc;
        public static GameTexture rubyShield;
        public static GameTexture rubyShield_shadow;
        public static GameTexture rubyDragon;
        public static GameTexture emeraldPillar;
        public static GameTexture stabbyBush_shadow;
        public static GameTexture lifeEssence;
        public static GameTexture willOWisp;
        public static GameTexture ghostlyBow;
        public static GameTexture locust;
        public static GameTexture wanderBot;
        public static GameTexture wanderBot_front;
        public static GameTexture wanderBot_shadow;
        public static GameTexture evilsProtector;
        public static GameTexture evilsProtector_shadow;
        public static GameTexture evilsProtector2;
        public static GameTexture evilsProtectorBomb;
        public static GameTexture evilsProtectorBomb_shadow;
        public static GameTexture portalMinion;
        public static GameTexture evilMinion;
        public static GameTexture queenSpiderBody;
        public static GameTexture queenSpiderHead;
        public static GameTexture queenSpiderLeg;
        public static GameTexture queenSpiderDebris;
        public static GameTexture queenSpider_shadow;
        public static GameTexture queenSpiderLeg_shadow;
        public static GameTexture queenSpider_spit;
        public static MobTexture spiderHatchling;
        public static GameTexture voidWizard2;
        public static GameTexture voidWizard3;
        public static GameTexture voidWizard_shadow;
        public static GameTexture parrot;
        public static HumanTexture voidWizard;
        public static HumanTexture pirateCaptain;
        public static HumanTexture pirateRecruit1;
        public static HumanTexture pirateRecruit2;
        public static HumanTexture pirateRecruit3;
        public static GameTexture pirateCaptainShip;
        public static GameTexture pirateCaptainShip_shadow;
        public static GameTexture pirateCaptainShip_mask;
        public static GameTexture ghostShip;
        public static GameTexture ancientVulture;
        public static GameTexture ancientVulture_shadow;
        public static GameTexture ancientVultureEgg;
        public static GameTexture ancientVultureEgg_shadow;
        public static GameTexture vultureHatchling;
        public static GameTexture vultureHatchling_shadow;
        public static GameTexture reaper;
        public static GameTexture reaper_shadow;
        public static GameTexture reaperGlow;
        public static GameTexture reaperSpirit;
        public static GameTexture reaperSpiritPortal;
        public static GameTexture cryoQueen;
        public static GameTexture swampGuardian;
        public static GameTexture swampGuardian_shadow;
        public static GameTexture swampGuardian_mask;
        public static GameTexture pestWarden;
        public static GameTexture pestWarden_shadow;
        public static GameTexture pestWarden_mask;
        public static GameTexture flyingSpirits;
        public static HumanTexture fallenWizard;
        public static GameTexture fallenWizardDragon;
        public static GameTexture nightSwarmBat;
        public static MobTexture motherSlime;
        public static GameTexture spiderEmpressHead;
        public static GameTexture spiderEmpressTorso;
        public static GameTexture spiderEmpressDress;
        public static GameTexture spiderEmpressLegTop;
        public static GameTexture spiderEmpressLegBottom;
        public static GameTexture spiderEmpressArmTop;
        public static GameTexture spiderEmpressArmBottom;
        public static GameTexture spiderEmpressHand;
        public static GameTexture spiderEmpressDebris;
        public static GameTexture spiderEmpressRageHead;
        public static GameTexture spiderEmpressRageTorso;
        public static GameTexture spiderEmpressRageDress;
        public static GameTexture spiderEmpressRageLegTop;
        public static GameTexture spiderEmpressRageLegBottom;
        public static GameTexture spiderEmpressRageArmTop;
        public static GameTexture spiderEmpressRageArmBottom;
        public static GameTexture spiderEmpressRageHand;
        public static GameTexture sunlightChampionEye;
        public static GameTexture sunlightChampionChestplate;
        public static GameTexture sunlightChampionJet;
        public static GameTexture sunlightGauntlet;
        public static GameTexture sunlightGauntletFire;
        public static GameTexture sunlightGauntletJet;
        public static GameTexture moonlightDancer;
        public static GameTexture moonlightDancerInvincible;
        public static GameTexture moonlightDancerDebris;
        public static GameTexture moonlightDancerHead;
        public static GameTexture crystalGolem;
        public static GameTexture crystalArmadillo;
        public static GameTexture crystalArmadillo_light;
        public static GameTexture crystalDragon;
        public static GameTexture crystalDragon_shadow;
        public static GameTexture crystalDragonHead;
        public static HumanTexture chieftain;
        public static GameTexture theCursedCrone;
        public static GameTexture theCursedCroneFrontEffects;
        public static GameTexture theCursedCroneBackEffects;
        public static GameTexture theCursedCrone_shadow;
        public static MobTexture spiritGhoul;
        public static GameTexture ascendedWizard_stage1;
        public static GameTexture ascendedWizard_stage2;
        public static GameTexture ascendedGauntlet;
        public static GameTexture ascendedGauntletJet;
        public static GameTexture ascendedGolem;
        public static GameTexture ascendedBat;
        public static GameTexture theVoidMapIcon;
        public static GameTexture theVoidHead;
        public static GameTexture theVoidClaw;
        public static GameTexture theVoidDebris;
        public static GameTexture mound1;
        public static GameTexture mound2;
        public static GameTexture mound3;
        public static GameTexture mounds32;
        public static GameTexture mountmask;
        public static GameTexture swimmask;
        public static GameTexture boat_shadow;
        public static GameTexture woodBoat;
        public static GameTexture minecart;
        public static GameTexture minecart_shadow;
        public static GameTexture sawblade;
        public static GameTexture[] boat_mask;
        public static GameTexture[] runeboundboat_mask;
        public static GameTexture[] minecart_mask;
        public static GameTexture polarBear;
        public static GameTexture polarBear_shadow;
        public static GameTexture grizzlyBear;
        public static GameTexture grizzlyBear_shadow;
        public static GameTexture grizzlyBearCub;
        public static GameTexture pug;
        public static GameTexture pug_shadow;
        public static GameTexture portalSphere;
        public static GameTexture spawnSphere;
        public static GameTexture bossPortal;
        public static GameTexture ravenlords_set_feather;
        public static GameTexture crocodile;
        public static GameTexture crocodile_shadow;
        public static GameTexture necroticflaskdebris;
        public static GameTexture unlabeledpotiondebris;
        public static GameTexture bigBoneSpike;
        public static GameTexture bigEarthSpike;
        public static GameTexture unknownRaid;

        public static void load() {
            human_shadow = Textures.fromFile("human_shadow");
            human_baby_shadow = Textures.fromFile("human_baby_shadow");
            human_big_shadow = Textures.fromFile("human_big_shadow");
            small_shadow = Textures.fromFile("small_shadow");
            human_enchained_iron = GameTexture.fromFile("player/armor/enchained_iron");
            sheep = Textures.fromFile("sheep");
            sheep_sheared = Textures.fromFile("sheep_sheared");
            sheep_shadow = Textures.fromFile("sheep_shadow");
            ram = Textures.fromFile("ram");
            ram_sheared = Textures.fromFile("ram_sheared");
            lamb = Textures.fromFile("lamb");
            lamb_shadow = Textures.fromFile("lamb_shadow");
            ostrich = Textures.fromFile("ostrich");
            ostrichMount = Textures.fromFile("ostrichmount");
            ostrich_shadow = Textures.fromFile("ostrich_shadow");
            seahorse_front = Textures.fromFile("seahorse_front");
            seahorse_back = Textures.fromFile("seahorse_back");
            seahorse_shadow = Textures.fromFile("seahorse_shadow");
            cow = Textures.fromFile("cow");
            cow_shadow = Textures.fromFile("cow_shadow");
            bull = Textures.fromFile("bull");
            calf = Textures.fromFile("calf");
            calf_shadow = Textures.fromFile("calf_shadow");
            pig = Textures.fromFile("pig");
            boar = Textures.fromFile("boar");
            pig_shadow = Textures.fromFile("pig_shadow");
            piglet = Textures.fromFile("piglet");
            piglet_shadow = Textures.fromFile("piglet_shadow");
            penguin = Textures.fromFile("penguin");
            chicken = Textures.fromFile("chicken");
            chicken_shadow = Textures.fromFile("chicken_shadow");
            rooster = Textures.fromFile("rooster");
            rooster_shadow = Textures.fromFile("rooster_shadow");
            chick = Textures.fromFile("chick");
            chick_shadow = Textures.fromFile("chick_shadow");
            honeyBee = Textures.fromFiles("honeybee");
            queenBee = Textures.fromFiles("queenbee");
            rabbit = Textures.fromFile("rabbit");
            squirrel = Textures.fromFile("squirrel");
            snowHare = Textures.fromFile("snowhare");
            crab = Textures.fromFiles("crab");
            scorpion = Textures.fromFiles("scorpion");
            turtle = Textures.fromFiles("turtle");
            swampSlug = Textures.fromFile("swampslug");
            frog = Textures.fromFiles("frog");
            duck = Textures.fromFiles("duck");
            bird_shadow = Textures.fromFile("bird_shadow");
            bird = Textures.fromFile("bird");
            bluebird = Textures.fromFile("bluebird");
            canaryBird = Textures.fromFile("canary");
            cardinalBird = Textures.fromFile("cardinal");
            spider = Textures.fromFiles("spider");
            mouse = Textures.fromFiles("mouse");
            beetCaveCroppler = Textures.fromFiles("beetcavecroppler");
            stoneCaveling = new HumanTexture(Textures.fromFile("stonecaveling"), Textures.fromFile("stonecavelingarms_front"), Textures.fromFile("stonecavelingarms_back"));
            snowStoneCaveling = new HumanTexture(Textures.fromFile("snowstonecaveling"), Textures.fromFile("snowstonecavelingarms_front"), Textures.fromFile("snowstonecavelingarms_back"));
            graniteCaveling = new HumanTexture(Textures.fromFile("granitecaveling"), Textures.fromFile("granitecavelingarms_front"), Textures.fromFile("granitecavelingarms_back"));
            swampStoneCaveling = new HumanTexture(Textures.fromFile("swampstonecaveling"), Textures.fromFile("swampstonecavelingarms_front"), Textures.fromFile("swampstonecavelingarms_back"));
            sandStoneCaveling = new HumanTexture(Textures.fromFile("sandstonecaveling"), Textures.fromFile("sandstonecavelingarms_front"), Textures.fromFile("sandstonecavelingarms_back"));
            deepStoneCaveling = new HumanTexture(Textures.fromFile("deepstonecaveling"), Textures.fromFile("deepstonecavelingarms_front"), Textures.fromFile("deepstonecavelingarms_back"));
            deepSnowStoneCaveling = new HumanTexture(Textures.fromFile("deepsnowstonecaveling"), Textures.fromFile("deepsnowstonecavelingarms_front"), Textures.fromFile("deepsnowstonecavelingarms_back"));
            dryadCaveling = new HumanTexture(Textures.fromFile("dryadcaveling"), Textures.fromFile("dryadcavelingarms_front"), Textures.fromFile("dryadcavelingarms_back"));
            deepSwampStoneCaveling = new HumanTexture(Textures.fromFile("deepswampstonecaveling"), Textures.fromFile("deepswampstonecavelingarms_front"), Textures.fromFile("deepswampstonecavelingarms_back"));
            deepSandStoneCaveling = new HumanTexture(Textures.fromFile("deepsandstonecaveling"), Textures.fromFile("deepsandstonecavelingarms_front"), Textures.fromFile("deepsandstonecavelingarms_back"));
            flameling = new HumanTexture(Textures.fromFile("flameling"), Textures.fromFile("flamelingarms_front"), Textures.fromFile("flamelingarms_back"));
            incursionCaveling = new HumanTexture(Textures.fromFile("incursioncaveling"), Textures.fromFile("incursioncavelingarms_front"), Textures.fromFile("incursioncavelingarms_back"));
            caveling_shadow = Textures.fromFile("caveling_shadow");
            zombie = Textures.humanTexture("zombie");
            zombieArcher = Textures.humanTexture("zombiearcher");
            zombieArcherWithBow = Textures.humanTexture("zombiearcherbow", "zombiearcherarms");
            trapperZombie = Textures.humanTexture("trapperzombie", "zombiearms");
            swampZombie = Textures.humanTexture("swampzombie", "swampzombiearms");
            goblin = Textures.fromFile("goblin");
            trenchcoatgoblin_stacked = Textures.humanTexture("trenchcoatgoblin_stacked", "trenchcoatgoblin_stacked_arms");
            trenchcoatgoblin_helmet = Textures.fromFile("trenchcoatgoblin_helmet");
            trenchcoatgoblin_chestplate = Textures.fromFile("trenchcoatgoblin_chestplate");
            trenchcoatgoblin_shoes = Textures.fromFile("trenchcoatgoblin_shoes");
            mummy = Textures.humanTexture("mummy");
            mummyMage = Textures.humanTexture("mummymage");
            vampire = Textures.humanTexture("vampire");
            frozenDwarf = Textures.humanTexture("frozendwarf");
            frozenDwarfHair = Textures.fromFile("frozendwarfhair1");
            enchantedZombie = Textures.humanTexture("enchantedzombie");
            enchantedZombieArcher = Textures.humanTexture("enchantedzombiearcher");
            enchantedZombieArcherWithBow = Textures.humanTexture("enchantedzombiearcherbow", "enchantedzombiearms");
            ninja = Textures.humanTexture("ninja");
            voidApprentice = Textures.humanTexture("voidapprentice");
            voidApprentice_shadow = Textures.fromFile("voidapprentice_shadow");
            sandSpirit = Textures.fromFile("sandspirit");
            skeleton = Textures.humanTexture("skeleton");
            deepCaveSpirit = Textures.fromFile("deepcavespirit");
            skeletonMiner = Textures.humanTexture("skeletonminer");
            skeletonMage = Textures.humanTexture("skeletonmage");
            fishianHookWarrior = Textures.humanTexture("fishianhookwarrior");
            fishianHealer = Textures.humanTexture("fishianhealer");
            fishianShaman = Textures.humanTexture("fishianshaman");
            swampSlime = Textures.fromFile("swampslime");
            swampSlime_shadow = Textures.fromFile("swampslime_shadow");
            crawlingZombie = Textures.fromFiles("crawlingzombie");
            enchantedCrawlingZombie = Textures.fromFiles("enchantedcrawlingzombie", "crawlingzombie_shadow");
            giantCaveSpider = Textures.fromFiles("giantcavespider", "giantspider_shadow");
            giantSnowCaveSpider = Textures.fromFiles("giantsnowcavespider", "giantspider_shadow");
            giantSwampCaveSpider = Textures.fromFiles("giantswampcavespider", "giantspider_shadow");
            smallSwampCaveSpider = Textures.fromFiles("smallswampcavespider", "smallswampcavespider_shadow");
            jackal = Textures.fromFiles("jackal", "wolf_shadow");
            snowWolf = Textures.fromFiles("snowwolf", "wolf_shadow");
            giantScorpion = Textures.fromFiles("giantscorpion", "giantscorpion_shadow");
            cryoFlake = Textures.fromFile("cryoflake");
            swampShooter = Textures.fromFile("swampshooter");
            mole = Textures.fromFile("mole");
            frostSentry = Textures.fromFile("frostsentry");
            giantSwampSlime = Textures.fromFile("giantswampslime");
            giantSwampSlime_shadow = Textures.fromFile("giantswampslime_shadow");
            swampDweller = Textures.humanTexture("swampdweller");
            sandWorm = Textures.fromFile("sandworm");
            sandWorm_shadow = Textures.fromFile("sandworm_shadow");
            sandWorm_mask = Textures.fromFile("sandworm_mask");
            desertCrawler = Textures.fromFile("desertcrawler");
            swampSkeleton = Textures.humanTexture("swampskeleton");
            ancientSkeleton = Textures.humanTexture("ancientskeleton");
            ancientArmoredSkeleton = Textures.humanTexture("ancientarmoredskeleton");
            ancientSkeletonMage = Textures.humanTexture("ancientskeletonmage");
            leggedSlime = Textures.fromFiles("leggedslime");
            mageSlime = Textures.fromFiles("mageslime");
            ghostSlime = Textures.fromFiles("ghostslime");
            warriorSlime = Textures.fromFiles("warriorslime");
            slimeWorm = Textures.fromFiles("slimeworm");
            slimeWorm_mask = Textures.fromFile("slimeworm_mask");
            cryptBat = Textures.fromFile("cryptbat");
            phantom = Textures.fromFiles("phantom");
            cryptVampire = Textures.humanTexture("cryptvampire");
            spiderkin = Textures.humanTexture("spiderkin/spiderkin");
            spiderkinWarrior = Textures.humanTexture("spiderkin/spiderkinwarrior");
            spiderkinArcher = Textures.humanTexture("spiderkin/spiderkinarcher");
            spiderkinMage = Textures.humanTexture("spiderkin/spiderkinmage");
            spiderkin_light = Textures.fromFile("spiderkin/spiderkin_light");
            spiderkinWarrior_light = Textures.fromFile("spiderkin/spiderkinwarrior_light");
            spiderkinArcher_light = Textures.fromFile("spiderkin/spiderkinarcher_light");
            spiderkinMage_light = Textures.fromFile("spiderkin/spiderkinmage_light");
            webSpinner = Textures.fromFiles("webspinner");
            webSpinner_shadow = Textures.fromFiles("webspinner_shadow");
            bloatedSpider = Textures.fromFiles("bloatedspider");
            bloatedSpider_shadow = Textures.fromFiles("bloatedspider_shadow");
            staticJellyfish = Textures.fromFiles("staticjellyfish");
            mosquitoEgg = Textures.fromFile("mosquitoegg");
            mosquito = Textures.fromFile("mosquito");
            mosquito_shadow = Textures.fromFile("mosquito_shadow");
            crone = Textures.humanTexture("crone");
            boneWalker = Textures.humanTexture("bonewalker");
            forestSpector = Textures.fromFile("forestspector");
            forestSpector_shadow = Textures.fromFile("forestspector_shadow");
            dryadSentinel = Textures.fromFile("dryadsentinel");
            dryadSentinel_shadow = Textures.fromFile("dryadsentinel_shadow");
            flamelingShooter = Textures.fromFile("flamelingshooter");
            crazedRaven = Textures.humanTexture("crazedraven");
            arcanicPylon = Textures.fromFile("arcanicpylon");
            frostPiercer = Textures.fromFile("frostpiercer");
            sentientSword = Textures.fromFile("sentientsword");
            sentientSword_shadow = Textures.fromFile("sentientsword_shadow");
            steelBoat = Textures.fromFile("steelboat");
            runeboundBoat = Textures.fromFile("runeboundboat");
            walkingTorch = Textures.fromFile("walkingtorch");
            babyZombie = Textures.humanTexture("babyzombie");
            babyZombieArcher = Textures.humanTexture("babyzombiearcher", "babyzombiearms");
            babySnowman = Textures.humanTexture("babysnowman");
            poisonSlime = Textures.fromFile("poisonslime");
            poisonSlime_shadow = Textures.fromFile("poisonslime_shadow");
            dryadSpirit = Textures.fromFile("dryadspirit");
            pouncingSlime = Textures.fromFile("pouncingslime");
            pouncingSlime_shadow = Textures.fromFile("pouncingslime_shadow");
            greatswordSlime = Textures.fromFile("greatswordslime");
            greatswordSlime_shadow = Textures.fromFile("greatswordslime_shadow");
            chargingPhantom = Textures.fromFiles("playerchargingphantom");
            orbOfSlimesSlime = Textures.fromFiles("orbofslimesslime");
            reaperSpiritPet = Textures.fromFile("playerreaperspirit");
            jumpingBall = Textures.fromFile("jumpingball");
            jumpingBall_shadow = Textures.fromFile("jumpingball_shadow");
            babySpider = Textures.fromFiles("babyspider");
            babyCrawlingZombie = Textures.fromFiles("babycrawlingzombie");
            babyDryad = Textures.fromFiles("babydryad");
            cryoFlakePet = Textures.fromFile("playercryoflake");
            hoverBoard = Textures.fromFiles("hoverboard");
            witchBroom = Textures.fromFiles("witchbroom");
            ancestorKnight = Textures.humanTexture("ancestorknight");
            ancestorMage = Textures.humanTexture("ancestormage");
            babySkeleton = Textures.humanTexture("babyskeleton");
            babySkeletonMage = Textures.humanTexture("babyskeletonmage");
            babySpiderkinWarrior = Textures.humanTexture("babyspiderkinwarrior");
            babySpiderkinArcher = Textures.humanTexture("babyspiderkinarcher");
            stabbyBush = Textures.humanTexture("stabbybush");
            bashyBush = Textures.humanTexture("bashybush");
            stabbyBush_shadow = Textures.fromFile("stabbybush_shadow");
            duskMoonDisc = Textures.fromFile("duskmoondisc");
            cavelingElder = Textures.fromFile("cavelingelder");
            grizzlyBearCub = Textures.fromFile("grizzlycub");
            pug = Textures.fromFile("pug");
            pug_shadow = Textures.fromFile("pug_shadow");
            crystalGolem = Textures.fromFile("crystalgolem");
            crystalArmadillo = Textures.fromFile("crystalarmadillo");
            crystalArmadillo_light = Textures.fromFile("crystalarmadillo_light");
            crystalDragon = Textures.fromFile("crystaldragon");
            crystalDragon_shadow = Textures.fromFile("crystaldragon_shadow");
            crystalDragonHead = Textures.fromFile("crystaldragonhead");
            rubyShield = Textures.fromFile("rubyshield");
            rubyShield_shadow = Textures.fromFile("rubyshield_shadow");
            rubyDragon = Textures.fromFile("rubydragon");
            emeraldPillar = Textures.fromFile("emeraldpillar");
            lifeEssence = Textures.fromFile("lifeessence");
            willOWisp = Textures.fromFile("willowisp");
            ghostlyBow = Textures.fromFile("ghostlybow");
            locust = Textures.fromFile("locust");
            wanderBot = Textures.fromFile("wanderbot");
            wanderBot_front = Textures.fromFile("wanderbot_front");
            wanderBot_shadow = Textures.fromFile("wanderbot_shadow");
            evilsProtector = Textures.fromFile("evilsprotector");
            evilsProtector_shadow = Textures.fromFile("evilsprotector_shadow");
            evilsProtector2 = Textures.fromFile("evilsprotector2");
            evilsProtectorBomb = Textures.fromFile("evilsprotectorbomb");
            evilsProtectorBomb_shadow = Textures.fromFile("evilsprotectorbomb_shadow");
            portalMinion = Textures.fromFile("portalminion");
            evilMinion = Textures.fromFile("evilminion");
            queenSpiderBody = Textures.fromFile("queenspiderbody");
            queenSpiderHead = Textures.fromFile("queenspiderhead");
            queenSpiderLeg = Textures.fromFile("queenspiderleg");
            queenSpiderDebris = Textures.fromFile("queenspiderdebris");
            queenSpider_shadow = Textures.fromFile("queenspider_shadow");
            queenSpiderLeg_shadow = Textures.fromFile("queenspiderleg_shadow");
            queenSpider_spit = Textures.fromFile("queenspider_spit");
            spiderHatchling = Textures.fromFiles("spiderhatchling");
            arachnidSpider = Textures.fromFiles("arachnidspider");
            voidWizard = Textures.humanTexture("voidwizard");
            voidWizard2 = Textures.fromFile("voidwizard2");
            voidWizard3 = Textures.fromFile("voidwizard3");
            voidWizard_shadow = Textures.fromFile("voidwizard_shadow");
            pirateCaptain = Textures.humanTexture("pirates/piratecaptain");
            pirateCaptainShip = Textures.fromFile("pirates/piratecaptainship");
            pirateCaptainShip_shadow = Textures.fromFile("pirates/piratecaptainship_shadow");
            pirateCaptainShip_mask = Textures.fromFile("pirates/piratecaptainship_mask");
            ghostShip = Textures.fromFile("ghostship");
            pirateRecruit1 = Textures.humanTexture("pirates/pirate1");
            pirateRecruit2 = Textures.humanTexture("pirates/pirate2");
            pirateRecruit3 = Textures.humanTexture("pirates/pirate3");
            parrot = Textures.fromFile("pirates/parrot");
            ancientVulture = Textures.fromFile("ancientvulture");
            ancientVulture_shadow = Textures.fromFile("ancientvulture_shadow");
            ancientVultureEgg = Textures.fromFile("ancientvultureegg");
            ancientVultureEgg_shadow = Textures.fromFile("ancientvultureegg_shadow");
            vultureHatchling = Textures.fromFile("vulturehatchling");
            vultureHatchling_shadow = Textures.fromFile("vulturehatchling_shadow");
            reaper = Textures.fromFile("reaper");
            reaperGlow = Textures.fromFile("reaperglow");
            reaper_shadow = Textures.fromFile("reaper_shadow");
            reaperSpirit = Textures.fromFile("reaperspirit");
            reaperSpiritPortal = Textures.fromFile("reaperspiritportal");
            cryoQueen = Textures.fromFile("cryoqueen");
            swampGuardian = Textures.fromFile("swampguardian");
            swampGuardian_shadow = Textures.fromFile("swampguardian_shadow");
            swampGuardian_mask = Textures.fromFile("swampguardian_mask");
            pestWarden = Textures.fromFile("pestwarden");
            pestWarden_shadow = Textures.fromFile("pestwarden_shadow");
            pestWarden_mask = Textures.fromFile("pestwarden_mask");
            flyingSpirits = Textures.fromFile("flyingspirits");
            fallenWizard = Textures.humanTexture("fallenwizard");
            fallenWizardDragon = Textures.fromFile("fallenwizarddragon");
            nightSwarmBat = Textures.fromFile("nightswarmbat");
            motherSlime = Textures.fromFiles("motherslime");
            spiderEmpressHead = Textures.fromFile("spiderempress_head");
            spiderEmpressTorso = Textures.fromFile("spiderempress_torso");
            spiderEmpressDress = Textures.fromFile("spiderempress_dress");
            spiderEmpressLegTop = Textures.fromFile("spiderempress_legtop");
            spiderEmpressLegBottom = Textures.fromFile("spiderempress_legbottom");
            spiderEmpressArmTop = Textures.fromFile("spiderempress_armtop");
            spiderEmpressArmBottom = Textures.fromFile("spiderempress_armbottom");
            spiderEmpressHand = Textures.fromFile("spiderempress_hand");
            spiderEmpressRageHead = Textures.fromFile("spiderempressrage_head");
            spiderEmpressRageTorso = Textures.fromFile("spiderempressrage_torso");
            spiderEmpressRageDress = Textures.fromFile("spiderempressrage_dress");
            spiderEmpressRageLegTop = Textures.fromFile("spiderempressrage_legtop");
            spiderEmpressRageLegBottom = Textures.fromFile("spiderempressrage_legbottom");
            spiderEmpressRageArmTop = Textures.fromFile("spiderempressrage_armtop");
            spiderEmpressRageArmBottom = Textures.fromFile("spiderempressrage_armbottom");
            spiderEmpressRageHand = Textures.fromFile("spiderempressrage_hand");
            spiderEmpressDebris = Textures.fromFile("spiderempress_debris");
            sunlightChampionEye = Textures.fromFile("sunlightchampioneye");
            sunlightChampionChestplate = Textures.fromFile("sunlightchampionchestplate");
            sunlightChampionJet = Textures.fromFile("sunlightchampionjet");
            sunlightGauntlet = Textures.fromFile("sunlightgauntlet");
            sunlightGauntletFire = Textures.fromFile("sunlightgauntletfire");
            sunlightGauntletJet = Textures.fromFile("sunlightgauntletjet");
            moonlightDancer = Textures.fromFile("moonlightdancer");
            moonlightDancerInvincible = Textures.fromFile("moonlightdancerinvincible");
            moonlightDancerDebris = Textures.fromFile("moonlightdancerdebris");
            moonlightDancerHead = Textures.fromFile("moonlightdancerhead");
            chieftain = Textures.humanTexture("chieftain");
            theCursedCrone = Textures.fromFile("thecursedcrone");
            theCursedCroneFrontEffects = Textures.fromFile("thecursedcronefronteffects");
            theCursedCroneBackEffects = Textures.fromFile("thecursedcronebackeffects");
            theCursedCrone_shadow = Textures.fromFile("thecursedcrone_shadow");
            spiritGhoul = Textures.fromFiles("spiritghoul");
            ascendedWizard_stage1 = Textures.fromFile("ascendedwizard_stage1");
            ascendedWizard_stage2 = Textures.fromFile("ascendedwizard_stage2");
            ascendedGauntlet = Textures.fromFile("ascendedgauntlet");
            ascendedGauntletJet = Textures.fromFile("ascendedgauntletjet");
            ascendedGolem = Textures.fromFile("ascendedgolem");
            ascendedBat = Textures.fromFile("ascendedbat");
            theVoidMapIcon = Textures.fromFile("thevoid_mapicon");
            theVoidHead = Textures.fromFile("thevoid_head");
            theVoidClaw = Textures.fromFile("thevoid_claw");
            theVoidDebris = Textures.fromFile("thevoid_debris");
            mound1 = Textures.fromFile("mound1");
            mound2 = Textures.fromFile("mound2");
            mound3 = Textures.fromFile("mound3");
            mounds32 = Textures.fromFile("mounds32");
            mountmask = GameTexture.fromFile("mobs/mountmask");
            swimmask = GameTexture.fromFile("mobs/swimmask");
            boat_shadow = Textures.fromFile("boat_shadow");
            woodBoat = Textures.fromFile("woodboat");
            GameTexture boat_mask_sprites = Textures.fromFile("boatmask");
            int boatSprites = boat_mask_sprites.getHeight() / 64;
            boat_mask = new GameTexture[boatSprites];
            for (int i = 0; i < boatSprites; ++i) {
                Textures.boat_mask[i] = new GameTexture(boat_mask_sprites, 0, i, 64);
            }
            GameTexture runeboundBoat_mask_sprites = Textures.fromFile("runeboundboatmask");
            int runeboundBoatSprites = runeboundBoat_mask_sprites.getHeight() / 64;
            runeboundboat_mask = new GameTexture[runeboundBoatSprites];
            for (int i = 0; i < runeboundBoatSprites; ++i) {
                Textures.runeboundboat_mask[i] = new GameTexture(runeboundBoat_mask_sprites, 0, i, 64);
            }
            minecart = Textures.fromFile("minecart");
            minecart_shadow = Textures.fromFile("minecart_shadow");
            GameTexture minecart_mask_sprites = Textures.fromFile("minecart_mask");
            int minecartSprites = minecart_mask_sprites.getHeight() / 64;
            minecart_mask = new GameTexture[minecartSprites];
            for (int i = 0; i < minecartSprites; ++i) {
                Textures.minecart_mask[i] = new GameTexture(minecart_mask_sprites, 0, i, 64);
            }
            sawblade = Textures.fromFile("sawblade");
            polarBear = Textures.fromFile("polarbear");
            polarBear_shadow = Textures.fromFile("polarbear_shadow");
            grizzlyBear = Textures.fromFile("grizzly");
            grizzlyBear_shadow = Textures.fromFile("grizzly_shadow");
            crocodile = Textures.fromFile("crocodile");
            portalSphere = Textures.fromFile("portalsphere");
            spawnSphere = Textures.fromFile("spawnsphere");
            bossPortal = Textures.fromFile("bossportal");
            ravenlords_set_feather = Textures.fromFile("ravenlordssetfeather");
            necroticflaskdebris = GameTexture.fromFile("particles/necroticflaskdebris");
            unlabeledpotiondebris = GameTexture.fromFile("particles/unlabeledpotiondebris");
            bigBoneSpike = GameTexture.fromFile("particles/bigbonespike");
            bigEarthSpike = GameTexture.fromFile("particles/bigearthspike");
            unknownRaid = GameTexture.fromFile("mobs/icons/unknownraid");
            GameTexture overlay = GameTexture.fromFile("objects/breakobjectoverlay", true);
        }

        public static GameTexture fromFile(String path) {
            return GameTexture.fromFile("mobs/" + path);
        }

        public static GameTexture fromFile(String path, GameTexture defaultNotFound) {
            return GameTexture.fromFile("mobs/" + path, defaultNotFound);
        }

        public static MobTexture fromFiles(String path, String shadowPath) {
            return new MobTexture(Textures.fromFile(path), Textures.fromFile(shadowPath));
        }

        public static MobTexture fromFiles(String path) {
            return Textures.fromFiles(path, path + "_shadow");
        }

        public static HumanTexture humanTexture(String path, String armsPath) {
            return new HumanTexture(Textures.fromFile(path), Textures.fromFile(armsPath + "_left"), Textures.fromFile(armsPath + "_right"));
        }

        public static HumanTexture humanTexture(String path) {
            return Textures.humanTexture(path, path + "arms");
        }

        public static HumanTextureFull humanTextureFull(String headPath, String backHeadPath, String eyelidsPath, String hairPath, String backHairPath, String bodyPath, String backBodyPath, String armsPath, String backArmsPath, String feetPath, String backFeetPath) {
            return new HumanTextureFull(Textures.fromFile(headPath, null), Textures.fromFile(backHeadPath, null), Textures.fromFile(eyelidsPath, null), Textures.fromFile(hairPath, null), Textures.fromFile(backHairPath, null), Textures.fromFile(bodyPath), Textures.fromFile(backBodyPath, null), Textures.fromFile(armsPath + "_left"), Textures.fromFile(backArmsPath + "_left", null), Textures.fromFile(armsPath + "_right"), Textures.fromFile(backArmsPath + "_right", null), Textures.fromFile(feetPath), Textures.fromFile(backFeetPath, null));
        }

        public static HumanTextureFull humanTextureFull(String path) {
            return Textures.humanTextureFull(path + "head", path + "head_back", path + "eyelids", path + "hair", path + "hair_back", path + "body", path + "body_back", path + "arms", path + "arms_back", path + "feet", path + "feet_back");
        }
    }
}

