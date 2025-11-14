/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;
import necesse.entity.levelEvent.ArcanicPylonLightningLevelEvent;
import necesse.entity.levelEvent.AscendedBatJailLevelEvent;
import necesse.entity.levelEvent.AscendedBlackHoleEvent;
import necesse.entity.levelEvent.AscendedFractureGroundEvent;
import necesse.entity.levelEvent.AscendedIncursionEvent;
import necesse.entity.levelEvent.BloodGrimoireParticleLevelEvent;
import necesse.entity.levelEvent.BounceGlyphTrapEvent;
import necesse.entity.levelEvent.ChainLightningEffectLevelEvent;
import necesse.entity.levelEvent.ChickenGlyphTrapEvent;
import necesse.entity.levelEvent.ChieftainGauntletEvent;
import necesse.entity.levelEvent.EmpressAcidGroundEvent;
import necesse.entity.levelEvent.ExtractionIncursionEvent;
import necesse.entity.levelEvent.FallenWizardRespawnEvent;
import necesse.entity.levelEvent.FallingIcicleEvent;
import necesse.entity.levelEvent.FlameTrapEvent;
import necesse.entity.levelEvent.HuntIncursionEvent;
import necesse.entity.levelEvent.IncursionPerkModifiers.FlamelingsCanDiePerkLevelEvent;
import necesse.entity.levelEvent.IncursionPerkModifiers.MobsDropAlchemyShardsPerkLevelEvent;
import necesse.entity.levelEvent.IncursionPerkModifiers.MobsDropAltarDustPerkLevelEvent;
import necesse.entity.levelEvent.IncursionPerkModifiers.MobsDropMoreAltarDustPerkLevelEvent;
import necesse.entity.levelEvent.IncursionPerkModifiers.MobsDropUpgradeShardsPerkLevelEvent;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.OverChargedManaHitsLevelEvent;
import necesse.entity.levelEvent.ReverseDamageGlyphTrapEvent;
import necesse.entity.levelEvent.ShowAttackTickEvent;
import necesse.entity.levelEvent.SmokePuffCloudLevelEvent;
import necesse.entity.levelEvent.SmokePuffLevelEvent;
import necesse.entity.levelEvent.SpikeTrapEvent;
import necesse.entity.levelEvent.SpiritCorruptedLevelEvent;
import necesse.entity.levelEvent.SwordCleanSliceAttackEvent;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.levelEvent.TeleportFailEvent;
import necesse.entity.levelEvent.TempleEntranceEvent;
import necesse.entity.levelEvent.TheVoidBlackHoleGroundEvent;
import necesse.entity.levelEvent.TheVoidClawGroundShatterGroundEvent;
import necesse.entity.levelEvent.TheVoidMovingRainLevelEvent;
import necesse.entity.levelEvent.TicTacToeLevelEvent;
import necesse.entity.levelEvent.TrialIncursionEvent;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.levelEvent.explosionEvent.ArcanicPylonExplosionLevelEvent;
import necesse.entity.levelEvent.explosionEvent.AscendedBombExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.AscendedLightningExplosionLevelEvent;
import necesse.entity.levelEvent.explosionEvent.AscendedPushExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.BloatedSpiderExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.BombExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.BoneSpikeMobExplosionLevelEvent;
import necesse.entity.levelEvent.explosionEvent.BoulderHitExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.CannonBallExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.CaptainCannonBallExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.CursedCroneSpiritBeamsExplosionLevelEvent;
import necesse.entity.levelEvent.explosionEvent.DynamiteExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.ElectricOrbExplosionLevelEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosiveModifierChargeUpLevelEvent;
import necesse.entity.levelEvent.explosionEvent.ExplosiveModifierExplosionLevelEvent;
import necesse.entity.levelEvent.explosionEvent.FlamelingsModifierSmokePuffLevelEvent;
import necesse.entity.levelEvent.explosionEvent.GhostSkullExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.RubyStaffExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.SoulseedExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.StabbyBushExplosionLevelEvent;
import necesse.entity.levelEvent.explosionEvent.StormingModifierExplosionLevelEvent;
import necesse.entity.levelEvent.explosionEvent.SupernovaExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.TNTExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.VoidRainExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.VoidWizardExplosionEvent;
import necesse.entity.levelEvent.explosionEvent.splashEvent.DamageSplashEvent;
import necesse.entity.levelEvent.explosionEvent.splashEvent.FreezeSplashEvent;
import necesse.entity.levelEvent.explosionEvent.splashEvent.HealSplashEvent;
import necesse.entity.levelEvent.explosionEvent.splashEvent.LocustDeathSplashEvent;
import necesse.entity.levelEvent.explosionEvent.splashEvent.NecroPoisonSplashEvent;
import necesse.entity.levelEvent.explosionEvent.splashEvent.PolymorphSplashEvent;
import necesse.entity.levelEvent.explosionEvent.splashEvent.SimpleSplashEvent;
import necesse.entity.levelEvent.explosionEvent.splashEvent.SlimeSplashEvent;
import necesse.entity.levelEvent.explosionEvent.splashEvent.SmiteSplashEvent;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.levelEvent.incursionModifiers.AlchemicalInterferenceModifierLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.AlchemyShardsDropPotionsLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.AscendedShardsBossDropLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.ChanceToMineFullClusterLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.CrawlmageddonModifierLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.DoubleBossChanceLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.EmpowermentBuffsModifierLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.EnableBannerOfWarLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.ExplosiveModifierLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.FlamelingsModifierLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.FrenzyModifierLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.ModifiersAffectEnemiesLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.SecondLifePerkLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.StormingModifierLevelEvent;
import necesse.entity.levelEvent.incursionModifiers.TremorsModifierLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AmethystGlyphEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AncientDredgingStaffEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AscendedLightningLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AscendedPylonChargeUpAttackLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AscendedSlimeQuakeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AscendedSlimeQuakeWarningEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.BrutesBattleaxeDashLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CaveSpiderSpitEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CaveSpiderWebEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ChargeBeamWarningLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ChargeShowerLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ChieftainDashLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CryoStormLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CrystalBombEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CrystalDragonLaserLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CrystallizeShatterEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CursedCroneSpiritSkullsEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.DawnSwirlEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.DredgingStaffEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ElectricOrbEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.EmeraldGlyphEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.EvilsProtectorBombAttackEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.FallenWizardBeamLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.FireDanceLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ForestSpectorDrainSoulLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.FrozenEnemyHitLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.FrozenEnemyShatterLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GalvanicTrailEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HydroPumpLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.KatanaDashLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.LightningTrailEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.LinedUpSpiritBeamsLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobDashLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobManaChangeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MoundShockWaveLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MouseBeamLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.NecroticSoulSkullPushEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.QueenSpiderSpitEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ReturnLifeOnHitLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.RubyGlyphEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.RuneSpiritPoolEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SapphireGlyphEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SingleSpiritBeamLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SlimeGreatbowEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SlimeQuakeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SlimeQuakeWarningEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SmallGroundWebEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SmiteLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SpideriteWaveGroundWebEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SunlightOrbEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.TheCrimsonSkyEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.TheVoidBreathBeamLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.TheVoidClawBeamLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.TheVoidHornBeamLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.TopazGlyphEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.VenomStaffEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.VoidClawDashLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.VoidRainAttackEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.WeaponChargeSmiteLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.WebWeaverWebEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard.VoidWizardGooEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard.VoidWizardHomingEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard.VoidWizardMissileEvent;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.AncientSkeletonRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.BasicSettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.ChickenPeopleSettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.FishianSettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.FrozenDwarvesSettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.HumanSettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.MummiesSettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.NinjaSettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.PiratesSettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.RogueHuntersSettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.TheMafiaRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.VampiresSettlementRaidLevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.VoidApprenticesSettlementRaidLevelEvent;
import necesse.gfx.forms.presets.creative.CreativeToolsTab;

public class LevelEventRegistry
extends ClassedGameRegistry<LevelEvent, LevelEventRegistryElement> {
    public static final LevelEventRegistry instance = new LevelEventRegistry();

    private LevelEventRegistry() {
        super("LevelEvent", 32762);
    }

    @Override
    public void registerCore() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "levelevents"));
        LevelEventRegistry.registerEvent("toolitem", ToolItemMobAbilityEvent.class);
        LevelEventRegistry.registerEvent("lightningtrail", LightningTrailEvent.class);
        LevelEventRegistry.registerEvent("galvanictrail", GalvanicTrailEvent.class);
        LevelEventRegistry.registerEvent("evilsprotectorbombattack", EvilsProtectorBombAttackEvent.class);
        LevelEventRegistry.registerEvent("voidwizardgoo", VoidWizardGooEvent.class);
        LevelEventRegistry.registerEvent("voidwizardhoming", VoidWizardHomingEvent.class);
        LevelEventRegistry.registerEvent("voidwizardexplosion", VoidWizardExplosionEvent.class);
        LevelEventRegistry.registerEvent("voidwizardmissile", VoidWizardMissileEvent.class);
        LevelEventRegistry.registerEvent("mobhealthinc", MobHealthChangeEvent.class);
        LevelEventRegistry.registerEvent("mobmanainc", MobManaChangeEvent.class);
        LevelEventRegistry.registerEvent("cavespiderweb", CaveSpiderWebEvent.class);
        LevelEventRegistry.registerEvent("cavespiderspit", CaveSpiderSpitEvent.class);
        LevelEventRegistry.registerEvent("venomstaff", VenomStaffEvent.class);
        LevelEventRegistry.registerEvent("dredgingstaff", DredgingStaffEvent.class);
        LevelEventRegistry.registerEvent("firedance", FireDanceLevelEvent.class);
        LevelEventRegistry.registerEvent("mousebeam", MouseBeamLevelEvent.class);
        LevelEventRegistry.registerEvent("queenspiderspit", QueenSpiderSpitEvent.class);
        LevelEventRegistry.registerEvent("fallenwizardbeam", FallenWizardBeamLevelEvent.class);
        LevelEventRegistry.registerEvent("webspinnerweb", SmallGroundWebEvent.class);
        LevelEventRegistry.registerEvent("mobdash", MobDashLevelEvent.class);
        LevelEventRegistry.registerEvent("katanadash", KatanaDashLevelEvent.class);
        LevelEventRegistry.registerEvent("crystaldragonlaser", CrystalDragonLaserLevelEvent.class);
        LevelEventRegistry.registerEvent("stabbybushexplosion", StabbyBushExplosionLevelEvent.class);
        LevelEventRegistry.registerEvent("necropoisonexplosion", NecroPoisonSplashEvent.class);
        LevelEventRegistry.registerEvent("polymorphsplash", PolymorphSplashEvent.class);
        LevelEventRegistry.registerEvent("necropoisonaura", NecroticSoulSkullPushEvent.class);
        LevelEventRegistry.registerEvent("frozenenemyhit", FrozenEnemyHitLevelEvent.class);
        LevelEventRegistry.registerEvent("frozenenemyshatter", FrozenEnemyShatterLevelEvent.class);
        LevelEventRegistry.registerEvent("runicspiritpool", RuneSpiritPoolEvent.class);
        LevelEventRegistry.registerEvent("singlespiritbeams", SingleSpiritBeamLevelEvent.class);
        LevelEventRegistry.registerEvent("linedupspiritbeams", LinedUpSpiritBeamsLevelEvent.class);
        LevelEventRegistry.registerEvent("cursedcronespiritbeamsexplosion", CursedCroneSpiritBeamsExplosionLevelEvent.class);
        LevelEventRegistry.registerEvent("forestspectordrainsoul", ForestSpectorDrainSoulLevelEvent.class);
        LevelEventRegistry.registerEvent("brutesbattleaxedash", BrutesBattleaxeDashLevelEvent.class);
        LevelEventRegistry.registerEvent("arcanicpylonlightning", ArcanicPylonLightningLevelEvent.class);
        LevelEventRegistry.registerEvent("arcanicpylonexplosion", ArcanicPylonExplosionLevelEvent.class);
        LevelEventRegistry.registerEvent("chargebeamwarning", ChargeBeamWarningLevelEvent.class);
        LevelEventRegistry.registerEvent("chargeshower", ChargeShowerLevelEvent.class);
        LevelEventRegistry.registerEvent("ascendedpylonattack", ChargeShowerLevelEvent.class);
        LevelEventRegistry.registerEvent("ascendedbombexplosion", AscendedBombExplosionEvent.class);
        LevelEventRegistry.registerEvent("ascendedpylonchargeupattack", AscendedPylonChargeUpAttackLevelEvent.class);
        LevelEventRegistry.registerEvent("ascendedpushexplosion", AscendedPushExplosionEvent.class);
        LevelEventRegistry.registerEvent("ascendedfractureevent", AscendedFractureGroundEvent.class);
        LevelEventRegistry.registerEvent("ascendedslimequakewarning", AscendedSlimeQuakeWarningEvent.class);
        LevelEventRegistry.registerEvent("ascendedslimequake", AscendedSlimeQuakeEvent.class);
        LevelEventRegistry.registerEvent("ascendedlightning", AscendedLightningLevelEvent.class);
        LevelEventRegistry.registerEvent("ascendedlightningexplosion", AscendedLightningExplosionLevelEvent.class);
        LevelEventRegistry.registerEvent("ascendedbatjail", AscendedBatJailLevelEvent.class);
        LevelEventRegistry.registerEvent("voidblackhole", TheVoidBlackHoleGroundEvent.class);
        LevelEventRegistry.registerEvent("voidclawgroundshatter", TheVoidClawGroundShatterGroundEvent.class);
        LevelEventRegistry.registerEvent("ascendedblackhole", AscendedBlackHoleEvent.class);
        LevelEventRegistry.registerEvent("voidhornbeam", TheVoidHornBeamLevelEvent.class);
        LevelEventRegistry.registerEvent("voidbreathbeam", TheVoidBreathBeamLevelEvent.class);
        LevelEventRegistry.registerEvent("voidrain", VoidRainAttackEvent.class);
        LevelEventRegistry.registerEvent("voidrainexplosion", VoidRainExplosionEvent.class);
        LevelEventRegistry.registerEvent("voidclawbeam", TheVoidClawBeamLevelEvent.class);
        LevelEventRegistry.registerEvent("voidmovingrain", TheVoidMovingRainLevelEvent.class);
        LevelEventRegistry.registerEvent("voidclawdash", VoidClawDashLevelEvent.class);
        LevelEventRegistry.registerEvent("teleport", TeleportEvent.class);
        LevelEventRegistry.registerEvent("teleportfail", TeleportFailEvent.class);
        LevelEventRegistry.registerEvent("smokepuff", SmokePuffLevelEvent.class);
        LevelEventRegistry.registerEvent("smokepuffcloud", SmokePuffCloudLevelEvent.class);
        LevelEventRegistry.registerEvent("crystallizeshatter", CrystallizeShatterEvent.class);
        LevelEventRegistry.registerEvent("huntincursion", HuntIncursionEvent.class);
        LevelEventRegistry.registerEvent("extractionincursion", ExtractionIncursionEvent.class);
        LevelEventRegistry.registerEvent("trialincursion", TrialIncursionEvent.class);
        LevelEventRegistry.registerEvent("ascendedincursion", AscendedIncursionEvent.class);
        LevelEventRegistry.registerEvent("frenzymodifier", FrenzyModifierLevelEvent.class);
        LevelEventRegistry.registerEvent("tremorsmodifier", TremorsModifierLevelEvent.class);
        LevelEventRegistry.registerEvent("alchemicalinterferencemodifier", AlchemicalInterferenceModifierLevelEvent.class);
        LevelEventRegistry.registerEvent("crawlmageddonmodifier", CrawlmageddonModifierLevelEvent.class);
        LevelEventRegistry.registerEvent("explosivemodifier", ExplosiveModifierLevelEvent.class);
        LevelEventRegistry.registerEvent("explosivemodifierexplosion", ExplosiveModifierExplosionLevelEvent.class);
        LevelEventRegistry.registerEvent("explosivemodifierchargeup", ExplosiveModifierChargeUpLevelEvent.class);
        LevelEventRegistry.registerEvent("flamelingsmodifier", FlamelingsModifierLevelEvent.class);
        LevelEventRegistry.registerEvent("flamelingsmodifiersmokepuff", FlamelingsModifierSmokePuffLevelEvent.class);
        LevelEventRegistry.registerEvent("stormingmodifier", StormingModifierLevelEvent.class);
        LevelEventRegistry.registerEvent("stormingmodifierexplosion", StormingModifierExplosionLevelEvent.class);
        LevelEventRegistry.registerEvent("flamelingscandieperk", FlamelingsCanDiePerkLevelEvent.class);
        LevelEventRegistry.registerEvent("mobsdropaltardust", MobsDropAltarDustPerkLevelEvent.class);
        LevelEventRegistry.registerEvent("mobsdropmorealtardust", MobsDropMoreAltarDustPerkLevelEvent.class);
        LevelEventRegistry.registerEvent("mobsdropalchemyshards", MobsDropAlchemyShardsPerkLevelEvent.class);
        LevelEventRegistry.registerEvent("mobsdropupgradeshards", MobsDropUpgradeShardsPerkLevelEvent.class);
        LevelEventRegistry.registerEvent("empowermentbuffs", EmpowermentBuffsModifierLevelEvent.class);
        LevelEventRegistry.registerEvent("enablebannerofwar", EnableBannerOfWarLevelEvent.class);
        LevelEventRegistry.registerEvent("alchemyshardsdroppotions", AlchemyShardsDropPotionsLevelEvent.class);
        LevelEventRegistry.registerEvent("ascendedsharddrops", AscendedShardsBossDropLevelEvent.class);
        LevelEventRegistry.registerEvent("chancetominefullcluster", ChanceToMineFullClusterLevelEvent.class);
        LevelEventRegistry.registerEvent("doublebosschance", DoubleBossChanceLevelEvent.class);
        LevelEventRegistry.registerEvent("secondlife", SecondLifePerkLevelEvent.class);
        LevelEventRegistry.registerEvent("modifiersaffectenemies", ModifiersAffectEnemiesLevelEvent.class);
        LevelEventRegistry.registerEvent("humanraid", HumanSettlementRaidLevelEvent.class);
        LevelEventRegistry.registerEvent("fishianraid", FishianSettlementRaidLevelEvent.class);
        LevelEventRegistry.registerEvent("chickenpeopleraid", ChickenPeopleSettlementRaidLevelEvent.class);
        LevelEventRegistry.registerEvent("ninjasraid", NinjaSettlementRaidLevelEvent.class);
        LevelEventRegistry.registerEvent("roguehuntersraid", RogueHuntersSettlementRaidLevelEvent.class);
        LevelEventRegistry.registerEvent("frozendwarvesraid", FrozenDwarvesSettlementRaidLevelEvent.class);
        LevelEventRegistry.registerEvent("voidapprenticeraid", VoidApprenticesSettlementRaidLevelEvent.class);
        LevelEventRegistry.registerEvent("vampireraid", VampiresSettlementRaidLevelEvent.class);
        LevelEventRegistry.registerEvent("mummyraid", MummiesSettlementRaidLevelEvent.class);
        LevelEventRegistry.registerEvent("pirateraid", PiratesSettlementRaidLevelEvent.class);
        LevelEventRegistry.registerEvent("ancientskeletonraid", AncientSkeletonRaidLevelEvent.class);
        LevelEventRegistry.registerEvent("themafiaraid", TheMafiaRaidLevelEvent.class);
        LevelEventRegistry.registerEvent("flametrap", FlameTrapEvent.class);
        LevelEventRegistry.registerEvent("spiketrap", SpikeTrapEvent.class);
        LevelEventRegistry.registerEvent("tntexplosion", TNTExplosionEvent.class);
        LevelEventRegistry.registerEvent("fishing", FishingEvent.class);
        LevelEventRegistry.registerEvent("cannonballexplosion", CannonBallExplosionEvent.class);
        LevelEventRegistry.registerEvent("ghostskullexplosion", GhostSkullExplosionEvent.class);
        LevelEventRegistry.registerEvent("captaincannonballexplosion", CaptainCannonBallExplosionEvent.class);
        LevelEventRegistry.registerEvent("bombexplosion", BombExplosionEvent.class);
        LevelEventRegistry.registerEvent("dynamiteexplosion", DynamiteExplosionEvent.class);
        LevelEventRegistry.registerEvent("boulderhit", BoulderHitExplosionEvent.class);
        LevelEventRegistry.registerEvent("hydropump", HydroPumpLevelEvent.class);
        LevelEventRegistry.registerEvent("moundshockwave", MoundShockWaveLevelEvent.class);
        LevelEventRegistry.registerEvent("ancientdredgingstaff", AncientDredgingStaffEvent.class);
        LevelEventRegistry.registerEvent("templeentrance", TempleEntranceEvent.class);
        LevelEventRegistry.registerEvent("fallenwizardrespawn", FallenWizardRespawnEvent.class);
        LevelEventRegistry.registerEvent("slimequakewarning", SlimeQuakeWarningEvent.class);
        LevelEventRegistry.registerEvent("slimequake", SlimeQuakeEvent.class);
        LevelEventRegistry.registerEvent("nightswarm", NightSwarmLevelEvent.class);
        LevelEventRegistry.registerEvent("returnlifeonhit", ReturnLifeOnHitLevelEvent.class);
        LevelEventRegistry.registerEvent("bloodgrimoireparticle", BloodGrimoireParticleLevelEvent.class);
        LevelEventRegistry.registerEvent("showattacktick", ShowAttackTickEvent.class);
        LevelEventRegistry.registerEvent("swordcleansliceattack", SwordCleanSliceAttackEvent.class);
        LevelEventRegistry.registerEvent("thecrimsonsky", TheCrimsonSkyEvent.class);
        LevelEventRegistry.registerEvent("slimegreatbowevent", SlimeGreatbowEvent.class);
        LevelEventRegistry.registerEvent("waitforseconds", WaitForSecondsEvent.class);
        LevelEventRegistry.registerEvent("bloatedspiderexplosion", BloatedSpiderExplosionEvent.class);
        LevelEventRegistry.registerEvent("webweaverweb", WebWeaverWebEvent.class);
        LevelEventRegistry.registerEvent("spideritewaveweb", SpideriteWaveGroundWebEvent.class);
        LevelEventRegistry.registerEvent("empressacidwave", EmpressAcidGroundEvent.class);
        LevelEventRegistry.registerEvent("dawnswirl", DawnSwirlEvent.class);
        LevelEventRegistry.registerEvent("supernovaexplosion", SupernovaExplosionEvent.class);
        LevelEventRegistry.registerEvent("sunlightorb", SunlightOrbEvent.class);
        LevelEventRegistry.registerEvent("crystalbomb", CrystalBombEvent.class);
        LevelEventRegistry.registerEvent("chainlightningeffect", ChainLightningEffectLevelEvent.class);
        LevelEventRegistry.registerEvent("overchargedmanahit", OverChargedManaHitsLevelEvent.class);
        LevelEventRegistry.registerEvent("electricorb", ElectricOrbEvent.class);
        LevelEventRegistry.registerEvent("electricorbexplosion", ElectricOrbExplosionLevelEvent.class);
        LevelEventRegistry.registerEvent("sapphireglyph", SapphireGlyphEvent.class);
        LevelEventRegistry.registerEvent("amethystglyph", AmethystGlyphEvent.class);
        LevelEventRegistry.registerEvent("rubyglyph", RubyGlyphEvent.class);
        LevelEventRegistry.registerEvent("rubyexplosion", RubyStaffExplosionEvent.class);
        LevelEventRegistry.registerEvent("emeraldglyph", EmeraldGlyphEvent.class);
        LevelEventRegistry.registerEvent("topazglyph", TopazGlyphEvent.class);
        LevelEventRegistry.registerEvent("bonespikemobexplosion", BoneSpikeMobExplosionLevelEvent.class);
        LevelEventRegistry.registerEvent("chieftaingauntlet", ChieftainGauntletEvent.class);
        LevelEventRegistry.registerEvent("chieftaindash", ChieftainDashLevelEvent.class);
        LevelEventRegistry.registerEvent("tictactoe", TicTacToeLevelEvent.class);
        LevelEventRegistry.registerEvent("cursedcronespiritskulls", CursedCroneSpiritSkullsEvent.class);
        LevelEventRegistry.registerEvent("spritcorrupted", SpiritCorruptedLevelEvent.class);
        LevelEventRegistry.registerEvent("fallingicicle", FallingIcicleEvent.class);
        LevelEventRegistry.registerEvent("chickenglyphtrap", ChickenGlyphTrapEvent.class);
        LevelEventRegistry.registerEvent("bounceglyphtrap", BounceGlyphTrapEvent.class);
        LevelEventRegistry.registerEvent("reversedamageglyphtrap", ReverseDamageGlyphTrapEvent.class);
        LevelEventRegistry.registerEvent("soulseedexplosion", SoulseedExplosionEvent.class);
        LevelEventRegistry.registerEvent("simplesplashevent", SimpleSplashEvent.class);
        LevelEventRegistry.registerEvent("healsplashevent", HealSplashEvent.class);
        LevelEventRegistry.registerEvent("freezesplashevent", FreezeSplashEvent.class);
        LevelEventRegistry.registerEvent("smitesplashevent", SmiteSplashEvent.class);
        LevelEventRegistry.registerEvent("smitelevelevent", SmiteLevelEvent.class);
        LevelEventRegistry.registerEvent("weaponchargesmitelevelevent", WeaponChargeSmiteLevelEvent.class);
        LevelEventRegistry.registerEvent("damagesplashevent", DamageSplashEvent.class);
        LevelEventRegistry.registerEvent("slimesplashevent", SlimeSplashEvent.class);
        LevelEventRegistry.registerEvent("locustdeathsplashevent", LocustDeathSplashEvent.class);
        LevelEventRegistry.registerEvent("cryostorm", CryoStormLevelEvent.class);
    }

    @Override
    protected void onRegister(LevelEventRegistryElement object, int id, String stringID, boolean isReplace) {
        super.onRegister(object, id, stringID, isReplace);
        if (BasicSettlementRaidLevelEvent.class.isAssignableFrom(object.data.aClass)) {
            try {
                BasicSettlementRaidLevelEvent raidEvent = (BasicSettlementRaidLevelEvent)object.data.newInstance(new Object[0]);
                CreativeToolsTab.addRaidType(id, raidEvent.getMobStringID(), new LocalMessage("misc", raidEvent.getRaidTypeStringID()));
            }
            catch (IllegalAccessException | InstantiationException | InvocationTargetException ignored) {
                GameLog.warn.println("Failed to register raid type: " + stringID + " for creative mode.");
            }
        }
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerEvent(String stringID, Class<? extends LevelEvent> levelEvent) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register level events");
        }
        try {
            return instance.register(stringID, new LevelEventRegistryElement(levelEvent));
        }
        catch (NoSuchMethodException e) {
            System.err.println("Could not register LevelEvent " + levelEvent.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
        }
    }

    public static LevelEvent getEvent(int id) {
        try {
            return (LevelEvent)((LevelEventRegistryElement)instance.getElement(id)).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LevelEvent getEvent(String stringID) {
        return LevelEventRegistry.getEvent(LevelEventRegistry.getEventID(stringID));
    }

    public static int getEventID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static int getEventID(Class<? extends LevelEvent> clazz) {
        return instance.getElementID(clazz);
    }

    public static String getEventStringID(int id) {
        return instance.getElementStringID(id);
    }

    protected static class LevelEventRegistryElement
    extends ClassIDDataContainer<LevelEvent> {
        public LevelEventRegistryElement(Class<? extends LevelEvent> levelEventClass) throws NoSuchMethodException {
            super(levelEventClass, new Class[0]);
        }
    }
}

