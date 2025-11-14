/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.AgedChampionWaveProjectile;
import necesse.entity.projectile.AmethystStaffProjectile;
import necesse.entity.projectile.AncientBoneProjectile;
import necesse.entity.projectile.AncientSkeletonMageProjectile;
import necesse.entity.projectile.AncientVultureProjectile;
import necesse.entity.projectile.AscendedBeamProjectile;
import necesse.entity.projectile.AscendedBoltProjectile;
import necesse.entity.projectile.AscendedBoltSoundProjectile;
import necesse.entity.projectile.AscendedBombProjectile;
import necesse.entity.projectile.AscendedBowBoltProjectile;
import necesse.entity.projectile.AscendedFractureProjectile;
import necesse.entity.projectile.AscendedGolemSpawnProjectile;
import necesse.entity.projectile.AscendedOrbProjectile;
import necesse.entity.projectile.AscendedShardBombProjectile;
import necesse.entity.projectile.AscendedShardProjectile;
import necesse.entity.projectile.AscendedSlashProjectile;
import necesse.entity.projectile.AscendedStaffBeamProjectile;
import necesse.entity.projectile.BabyBoneProjectile;
import necesse.entity.projectile.BarkBladeLeafProjectile;
import necesse.entity.projectile.BashyBushSpawnProjectile;
import necesse.entity.projectile.BloodClawProjectile;
import necesse.entity.projectile.BloodGrimoireRightClickProjectile;
import necesse.entity.projectile.BoneArrowProjectile;
import necesse.entity.projectile.BoneProjectile;
import necesse.entity.projectile.BoneSpikesProjectile;
import necesse.entity.projectile.BoulderStaffProjectile;
import necesse.entity.projectile.BouncingArrowProjectile;
import necesse.entity.projectile.BouncingSlimeBallProjectile;
import necesse.entity.projectile.ButchersCleaverBoomerangProjectile;
import necesse.entity.projectile.CannonBallProjectile;
import necesse.entity.projectile.CaptainCannonBallProjectile;
import necesse.entity.projectile.CarapaceDaggerProjectile;
import necesse.entity.projectile.CausticExecutionerProjectile;
import necesse.entity.projectile.CaveSpiderSpitProjectile;
import necesse.entity.projectile.CaveSpiderWebProjectile;
import necesse.entity.projectile.ChargeShowerProjectile;
import necesse.entity.projectile.ChefsSpecialRollingPinProjectile;
import necesse.entity.projectile.ChieftainShieldProjectile;
import necesse.entity.projectile.CoinProjectile;
import necesse.entity.projectile.CrazedRavenFeatherProjectile;
import necesse.entity.projectile.CrescentDiscFollowingProjectile;
import necesse.entity.projectile.CrimsonSkyArrowProjectile;
import necesse.entity.projectile.CryoMissileProjectile;
import necesse.entity.projectile.CryoQuakeProjectile;
import necesse.entity.projectile.CryoQuakeWeaponProjectile;
import necesse.entity.projectile.CryoShardProjectile;
import necesse.entity.projectile.CryoSpearShardProjectile;
import necesse.entity.projectile.CryoVolleyProjectile;
import necesse.entity.projectile.CryoWarningProjectile;
import necesse.entity.projectile.CryoWaveProjectile;
import necesse.entity.projectile.CrystalDragonShardProjectile;
import necesse.entity.projectile.CrystalGolemSpawnProjectile;
import necesse.entity.projectile.CrystalShieldRetaliationProjectile;
import necesse.entity.projectile.DawnFireballProjectile;
import necesse.entity.projectile.DruidsGreatBowPetalProjectile;
import necesse.entity.projectile.DryadBowProjectile;
import necesse.entity.projectile.DuskVolleyProjectile;
import necesse.entity.projectile.DynamiteStickProjectile;
import necesse.entity.projectile.EarthSpikesProjectile;
import necesse.entity.projectile.EmeraldStaffProjectile;
import necesse.entity.projectile.EmeraldWandProjectile;
import necesse.entity.projectile.EmpressAcidProjectile;
import necesse.entity.projectile.EmpressSlashProjectile;
import necesse.entity.projectile.EmpressSlashWarningProjectile;
import necesse.entity.projectile.EmpressWebBallProjectile;
import necesse.entity.projectile.EvilWitchGreatswordWaveProjectile;
import necesse.entity.projectile.EvilsProtectorAttack1Projectile;
import necesse.entity.projectile.EyeOfTheVoidSpawnProjectile;
import necesse.entity.projectile.FallenWizardBallProjectile;
import necesse.entity.projectile.FallenWizardWaveProjectile;
import necesse.entity.projectile.FireArrowProjectile;
import necesse.entity.projectile.FlamelingShooterProjectile;
import necesse.entity.projectile.FrostArrowProjectile;
import necesse.entity.projectile.FrostSentryProjectile;
import necesse.entity.projectile.FrostStaffProjectile;
import necesse.entity.projectile.GhostArrowProjectile;
import necesse.entity.projectile.GhostSkullProjectile;
import necesse.entity.projectile.GlacialBowProjectile;
import necesse.entity.projectile.GoldBoltProjectile;
import necesse.entity.projectile.GritArrowProjectile;
import necesse.entity.projectile.GritBoomerangProjectile;
import necesse.entity.projectile.HexedBladeGreatswordWaveProjectile;
import necesse.entity.projectile.HostileIceJavelinProjectile;
import necesse.entity.projectile.IceJavelinProjectile;
import necesse.entity.projectile.IcicleStaffProjectile;
import necesse.entity.projectile.IronArrowProjectile;
import necesse.entity.projectile.IronBombProjectile;
import necesse.entity.projectile.LivingShottyLeafProjectile;
import necesse.entity.projectile.NecroticFlaskProjectile;
import necesse.entity.projectile.NecroticGreatswordWaveProjectile;
import necesse.entity.projectile.NightPiercerArrowProjectile;
import necesse.entity.projectile.NinjaStarProjectile;
import necesse.entity.projectile.PathTestProjectile;
import necesse.entity.projectile.PhantomBobbleProjectile;
import necesse.entity.projectile.PhantomBoltProjectile;
import necesse.entity.projectile.PlayerSnowballProjectile;
import necesse.entity.projectile.PoisonArrowProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.QuartzBoltProjectile;
import necesse.entity.projectile.QueenSpiderEggProjectile;
import necesse.entity.projectile.QueenSpiderSpitProjectile;
import necesse.entity.projectile.RavenBeakSpearProjectile;
import necesse.entity.projectile.ReaperScythePlayerProjectile;
import necesse.entity.projectile.RollingPinProjectile;
import necesse.entity.projectile.RubyStaffProjectile;
import necesse.entity.projectile.SageArrowProjectile;
import necesse.entity.projectile.SageBoomerangProjectile;
import necesse.entity.projectile.SapphireRevolverProjectile;
import necesse.entity.projectile.SapphireStaffProjectile;
import necesse.entity.projectile.SkeletonMageProjectile;
import necesse.entity.projectile.SlimeBoltProjectile;
import necesse.entity.projectile.SlimeEggProjectile;
import necesse.entity.projectile.SlimeGreatBowArrowProjectile;
import necesse.entity.projectile.SlimeGreatswordProjectile;
import necesse.entity.projectile.SmallSpiritLeafProjectile;
import necesse.entity.projectile.SmiteBeamProjectile;
import necesse.entity.projectile.SnowballProjectile;
import necesse.entity.projectile.SparklerProjectile;
import necesse.entity.projectile.SpideriteArrowProjectile;
import necesse.entity.projectile.SpideriteWaveProjectile;
import necesse.entity.projectile.SpiritBeamProjectile;
import necesse.entity.projectile.SpiritOrbProjectile;
import necesse.entity.projectile.SpiritSkullProjectile;
import necesse.entity.projectile.SprinklerProjectile;
import necesse.entity.projectile.StabbyBushSpawnProjectile;
import necesse.entity.projectile.StarVeilProjectile;
import necesse.entity.projectile.StoneArrowProjectile;
import necesse.entity.projectile.StoneProjectile;
import necesse.entity.projectile.StormingIncursionModifierProjectile;
import necesse.entity.projectile.SwampBallProjectile;
import necesse.entity.projectile.SwampBoltProjectile;
import necesse.entity.projectile.SwampBoulderProjectile;
import necesse.entity.projectile.SwampDwellerStaffFlowerProjectile;
import necesse.entity.projectile.SwampDwellerStaffPetalProjectile;
import necesse.entity.projectile.SwampRazorProjectile;
import necesse.entity.projectile.SwampTomeProjectile;
import necesse.entity.projectile.TheRavensNestProjectile;
import necesse.entity.projectile.TileBombProjectile;
import necesse.entity.projectile.TrapArrowProjectile;
import necesse.entity.projectile.TrapperNetProjectile;
import necesse.entity.projectile.TrenchcoatGoblinSpawnProjectile;
import necesse.entity.projectile.UnlabeledPotionProjectile;
import necesse.entity.projectile.VampireProjectile;
import necesse.entity.projectile.VenomShowerProjectile;
import necesse.entity.projectile.VenomSlasherWaveProjectile;
import necesse.entity.projectile.VenomStaffProjectile;
import necesse.entity.projectile.VoidApprenticeProjectile;
import necesse.entity.projectile.VoidWizardCloneProjectile;
import necesse.entity.projectile.VoidWizardMissileProjectile;
import necesse.entity.projectile.VoidWizardWaveProjectile;
import necesse.entity.projectile.VultureHatchlingProjectile;
import necesse.entity.projectile.VulturesBurstProjectile;
import necesse.entity.projectile.WaterSprayProjectile;
import necesse.entity.projectile.WaterboltProjectile;
import necesse.entity.projectile.WeaponChargeSmiteBeamProjectile;
import necesse.entity.projectile.ZombieArrowProjectile;
import necesse.entity.projectile.boomerangProjectile.AnchorBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.BoxingGloveBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.FishianWarriorHookBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.FrostBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.HookBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.RazorBladeBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.ReaperScytheProjectile;
import necesse.entity.projectile.boomerangProjectile.SpiderBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.TopazSpinningProjectile;
import necesse.entity.projectile.boomerangProjectile.TungstenBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.VoidBoomerangProjectile;
import necesse.entity.projectile.boomerangProjectile.WoodBoomerangProjectile;
import necesse.entity.projectile.bulletProjectile.BouncingBulletProjectile;
import necesse.entity.projectile.bulletProjectile.CrystalBulletProjectile;
import necesse.entity.projectile.bulletProjectile.FrostBulletProjectile;
import necesse.entity.projectile.bulletProjectile.HandGunBulletProjectile;
import necesse.entity.projectile.bulletProjectile.NecroticBoltProjectile;
import necesse.entity.projectile.bulletProjectile.ReanimationBoltProjectile;
import necesse.entity.projectile.bulletProjectile.SeedBulletProjectile;
import necesse.entity.projectile.bulletProjectile.SniperBulletProjectile;
import necesse.entity.projectile.bulletProjectile.WebbedGunBulletProjectile;
import necesse.entity.projectile.followingProjectile.AncestorMageProjectile;
import necesse.entity.projectile.followingProjectile.BloodBoltProjectile;
import necesse.entity.projectile.followingProjectile.ChromaticBoltProjectile;
import necesse.entity.projectile.followingProjectile.CryptVampireBoltProjectile;
import necesse.entity.projectile.followingProjectile.ElderlyWandProjectile;
import necesse.entity.projectile.followingProjectile.EvilsProtectorAttack2Projectile;
import necesse.entity.projectile.followingProjectile.FallenWizardScepterProjectile;
import necesse.entity.projectile.followingProjectile.FishianHealProjectile;
import necesse.entity.projectile.followingProjectile.GlacialBoomerangProjectile;
import necesse.entity.projectile.followingProjectile.MageSlimeBoltProjectile;
import necesse.entity.projectile.followingProjectile.MouseTestProjectile;
import necesse.entity.projectile.followingProjectile.NightRazorBoomerangProjectile;
import necesse.entity.projectile.followingProjectile.PhantomMissileProjectile;
import necesse.entity.projectile.followingProjectile.ShadowBoltProjectile;
import necesse.entity.projectile.followingProjectile.TicTacToePunishProjectile;
import necesse.entity.projectile.followingProjectile.VoidBulletProjectile;
import necesse.entity.projectile.followingProjectile.VoidMissileProjectile;
import necesse.entity.projectile.followingProjectile.VoidWizardHomingProjectile;
import necesse.entity.projectile.laserProjectile.AscendedGolemBeamProjectile;
import necesse.entity.projectile.laserProjectile.ChargeBeamProjectile;
import necesse.entity.projectile.laserProjectile.CrystalGolemBeamProjectile;
import necesse.entity.projectile.laserProjectile.ShadowBeamProjectile;
import necesse.entity.projectile.laserProjectile.VoidLaserProjectile;
import necesse.entity.projectile.laserProjectile.VoidTrapProjectile;
import necesse.entity.projectile.pathProjectile.CrimsonSkyArrowPathProjectile;
import necesse.entity.projectile.pathProjectile.CryoQuakeCirclingProjectile;
import necesse.entity.projectile.pathProjectile.CryoWarningCirclingProjectile;
import necesse.entity.projectile.pathProjectile.StaticJellyfishProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;

public class ProjectileRegistry
extends ClassedGameRegistry<Projectile, ProjectileRegistryElement> {
    public static final ProjectileRegistry instance = new ProjectileRegistry();

    private ProjectileRegistry() {
        super("Projectile", 32762);
    }

    @Override
    public void registerCore() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "projectiles"));
        ProjectileRegistry.registerProjectile("stonearrow", StoneArrowProjectile.class, "stonearrow", "arrow_shadow");
        ProjectileRegistry.registerProjectile("ironarrow", IronArrowProjectile.class, "ironarrow", "arrow_shadow");
        ProjectileRegistry.registerProjectile("bouncingarrow", BouncingArrowProjectile.class, "bouncingarrow", "arrow_shadow");
        ProjectileRegistry.registerProjectile("vampirebolt", VampireProjectile.class, "bloodbolt", "bloodbolt_shadow");
        ProjectileRegistry.registerProjectile("ninjastar", NinjaStarProjectile.class, "ninjastar", "ninjastar_shadow");
        ProjectileRegistry.registerProjectile("woodboomerang", WoodBoomerangProjectile.class, "woodboomerang", "woodboomerang_shadow");
        ProjectileRegistry.registerProjectile("spiderboomerang", SpiderBoomerangProjectile.class, "spiderboomerang", "spiderboomerang_shadow");
        ProjectileRegistry.registerProjectile("frostboomerang", FrostBoomerangProjectile.class, "frostboomerang", "frostboomerang_shadow");
        ProjectileRegistry.registerProjectile("voidboomerang", VoidBoomerangProjectile.class, "voidboomerang", "voidboomerang_shadow");
        ProjectileRegistry.registerProjectile("bloodbolt", BloodBoltProjectile.class, "bloodbolt", "bloodbolt_shadow");
        ProjectileRegistry.registerProjectile("necroticbolt", NecroticBoltProjectile.class, "necroticbolt", "bloodbolt_shadow");
        ProjectileRegistry.registerProjectile("reanimationbolt", ReanimationBoltProjectile.class, "necroticbolt", "bloodbolt_shadow");
        ProjectileRegistry.registerProjectile("sparkler", SparklerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("sprinkler", SprinklerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("hookboomerang", HookBoomerangProjectile.class, "hookboomerang", "hookboomerang_shadow");
        ProjectileRegistry.registerProjectile("firearrow", FireArrowProjectile.class, "firearrow", "arrow_shadow");
        ProjectileRegistry.registerProjectile("frostarrow", FrostArrowProjectile.class, "frostarrow", "arrow_shadow");
        ProjectileRegistry.registerProjectile("voidlaser", VoidLaserProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("handgunbullet", HandGunBulletProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("bouncingbullet", BouncingBulletProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("frostbullet", FrostBulletProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("waterbolt", WaterboltProjectile.class, "waterbolt", "waterbolt_shadow");
        ProjectileRegistry.registerProjectile("sniperbullet", SniperBulletProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("webbedgunbullet", WebbedGunBulletProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("evilsprotector1", EvilsProtectorAttack1Projectile.class, "evilsprotector1", "evilsprotector1_shadow");
        ProjectileRegistry.registerProjectile("traparrow", TrapArrowProjectile.class, "ironarrow", "arrow_shadow");
        ProjectileRegistry.registerProjectile("voidtrap", VoidTrapProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("evilsprotector2", EvilsProtectorAttack2Projectile.class, "evilsprotector2", "evilsprotector2_shadow");
        ProjectileRegistry.registerProjectile("zombiearrow", ZombieArrowProjectile.class, "stonearrow", "arrow_shadow");
        ProjectileRegistry.registerProjectile("cannonball", CannonBallProjectile.class, "cannonball", "cannonball_shadow");
        ProjectileRegistry.registerProjectile("captaincannonball", CaptainCannonBallProjectile.class, "cannonball", "cannonball_shadow");
        ProjectileRegistry.registerProjectile("voidwizardhoming", VoidWizardHomingProjectile.class, "voidwizardhoming", "voidwizardhoming_shadow");
        ProjectileRegistry.registerProjectile("voidwizardclone", VoidWizardCloneProjectile.class, "voidwizardclone", "voidwizardclone_shadow");
        ProjectileRegistry.registerProjectile("voidwizardmissile", VoidWizardMissileProjectile.class, "voidwizardmissile", "voidwizardmissile_shadow");
        ProjectileRegistry.registerProjectile("voidwizardwave", VoidWizardWaveProjectile.class, "voidwizardwave", null);
        ProjectileRegistry.registerProjectile("voidapprentice", VoidApprenticeProjectile.class, "voidapprentice", "voidapprentice_shadow");
        ProjectileRegistry.registerProjectile("waterspray", WaterSprayProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("mousetest", MouseTestProjectile.class, "bloodbolt", "bloodbolt_shadow");
        ProjectileRegistry.registerProjectile("goldbolt", GoldBoltProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("ironbomb", IronBombProjectile.class, "ironbomb", "ironbomb_shadow");
        ProjectileRegistry.registerProjectile("dynamitestick", DynamiteStickProjectile.class, "dynamitestick", "dynamitestick_shadow");
        ProjectileRegistry.registerProjectile("tilebomb", TileBombProjectile.class, "tilebomb", "tilebomb_shadow");
        ProjectileRegistry.registerProjectile("voidmissile", VoidMissileProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("stone", StoneProjectile.class, "stone", "stone_shadow");
        ProjectileRegistry.registerProjectile("coin", CoinProjectile.class, "coin", "coin_shadow");
        ProjectileRegistry.registerProjectile("snowball", SnowballProjectile.class, "snowball", "snowball_shadow");
        ProjectileRegistry.registerProjectile("playersnowball", PlayerSnowballProjectile.class, "snowball", "snowball_shadow");
        ProjectileRegistry.registerProjectile("ancientvulture", AncientVultureProjectile.class, "ancientfeather", "ancientfeather_shadow");
        ProjectileRegistry.registerProjectile("vulturehatchling", VultureHatchlingProjectile.class, "hatchlingfeather", "hatchlingfeather_shadow");
        ProjectileRegistry.registerProjectile("quartzbolt", QuartzBoltProjectile.class, "quartzbolt", "bolt_shadow");
        ProjectileRegistry.registerProjectile("vulturesburst", VulturesBurstProjectile.class, "vulturesburst", null);
        ProjectileRegistry.registerProjectile("froststaff", FrostStaffProjectile.class, "froststaff", "froststaff_shadow");
        ProjectileRegistry.registerProjectile("iciclestaff", IcicleStaffProjectile.class, "icicle", "icicle_shadow");
        ProjectileRegistry.registerProjectile("sapphirestaff", SapphireStaffProjectile.class, "crystalbullet", "crystalbullet_shadow");
        ProjectileRegistry.registerProjectile("topazstaff", TopazSpinningProjectile.class, "topazswirl", "topazswirl_shadow");
        ProjectileRegistry.registerProjectile("amethyststaff", AmethystStaffProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("rubystaff", RubyStaffProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("emeraldstaff", EmeraldStaffProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("tungstenboomerang", TungstenBoomerangProjectile.class, "tungstenboomerang", "tungstenboomerang_shadow");
        ProjectileRegistry.registerProjectile("bone", BoneProjectile.class, "bone", "bone_shadow");
        ProjectileRegistry.registerProjectile("ancientbone", AncientBoneProjectile.class, "ancientbone", "bone_shadow");
        ProjectileRegistry.registerProjectile("bonearrow", BoneArrowProjectile.class, "bonearrow", "arrow_shadow");
        ProjectileRegistry.registerProjectile("elderlywand", ElderlyWandProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("shadowbolt", ShadowBoltProjectile.class, "shadowbolt", null);
        ProjectileRegistry.registerProjectile("reaperscythe", ReaperScytheProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("shadowbeam", ShadowBeamProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("reaperscytheplayer", ReaperScythePlayerProjectile.class, "reaperscytheplayer", null);
        ProjectileRegistry.registerProjectile("boxingglove", BoxingGloveBoomerangProjectile.class, "boxingglove", null);
        ProjectileRegistry.registerProjectile("cavespiderweb", CaveSpiderWebProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("cavespiderspit", CaveSpiderSpitProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("venomstaff", VenomStaffProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("cryomissile", CryoMissileProjectile.class, "cryomissile", null);
        ProjectileRegistry.registerProjectile("glacialbow", GlacialBowProjectile.class, "glacialbow", "glacialbow_shadow");
        ProjectileRegistry.registerProjectile("glacialboomerang", GlacialBoomerangProjectile.class, "glacialboomerang", "glacialboomerang_shadow");
        ProjectileRegistry.registerProjectile("swampbolt", SwampBoltProjectile.class, "swampbolt", "bolt_shadow");
        ProjectileRegistry.registerProjectile("cryowarning", CryoWarningProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("cryoquake", CryoQuakeProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("cryoquakecircle", CryoQuakeCirclingProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("cryowarningcircle", CryoWarningCirclingProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("cryoshard", CryoShardProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("cryowave", CryoWaveProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("cryovolley", CryoVolleyProjectile.class, "cryomissile", null);
        ProjectileRegistry.registerProjectile("cryoquakeweapon", CryoQuakeWeaponProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("cryospearshard", CryoSpearShardProjectile.class, "cryospearshard", "cryospearshard_shadow");
        ProjectileRegistry.registerProjectile("voidbullet", VoidBulletProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("poisonarrow", PoisonArrowProjectile.class, "poisonarrow", "arrow_shadow");
        ProjectileRegistry.registerProjectile("icejavelin", IceJavelinProjectile.class, "icejavelin", "icejavelin_shadow");
        ProjectileRegistry.registerProjectile("frostsentry", FrostSentryProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("hostileicejavelin", HostileIceJavelinProjectile.class, "hostileicejavelin", "hostileicejavelin_shadow");
        ProjectileRegistry.registerProjectile("swamprazor", SwampRazorProjectile.class, "swamprazor", "swamprazor_shadow");
        ProjectileRegistry.registerProjectile("swampboulder", SwampBoulderProjectile.class, "swampboulder", "swampboulder_shadow");
        ProjectileRegistry.registerProjectile("boulderstaff", BoulderStaffProjectile.class, "boulderstaff", "swampboulder_shadow");
        ProjectileRegistry.registerProjectile("razorbladeboomerang", RazorBladeBoomerangProjectile.class, "razorbladeboomerang", "swamprazor_shadow");
        ProjectileRegistry.registerProjectile("carapacedagger", CarapaceDaggerProjectile.class, "carapacedagger", "carapacedagger_shadow");
        ProjectileRegistry.registerProjectile("sageboomerang", SageBoomerangProjectile.class, "sageboomerang", "dragonsrebound_shadow");
        ProjectileRegistry.registerProjectile("gritboomerang", GritBoomerangProjectile.class, "gritboomerang", "dragonsrebound_shadow");
        ProjectileRegistry.registerProjectile("rollingpin", RollingPinProjectile.class, "rollingpinboomerang", "rollingpin_shadow");
        ProjectileRegistry.registerProjectile("chefsspecialrollingpin", ChefsSpecialRollingPinProjectile.class, "rollingpinboomerang", "rollingpin_shadow");
        ProjectileRegistry.registerProjectile("butcherscleaverboomerang", ButchersCleaverBoomerangProjectile.class, "butcherscleaverboomerang", "butcherscleaver_shadow");
        ProjectileRegistry.registerProjectile("gritarrow", GritArrowProjectile.class, "gritarrow", null);
        ProjectileRegistry.registerProjectile("sagearrow", SageArrowProjectile.class, "sagearrow", null);
        ProjectileRegistry.registerProjectile("babybone", BabyBoneProjectile.class, "babybone", "babybone_shadow");
        ProjectileRegistry.registerProjectile("queenspideregg", QueenSpiderEggProjectile.class, "queenspideregg", "queenspideregg_shadow");
        ProjectileRegistry.registerProjectile("queenspiderspit", QueenSpiderSpitProjectile.class, null, "queenspiderspit_shadow");
        ProjectileRegistry.registerProjectile("necroticflasksplash", NecroticFlaskProjectile.class, "necroticflasksplash", "necroticflasksplash_shadow");
        ProjectileRegistry.registerProjectile("unlabeledpotionsplash", UnlabeledPotionProjectile.class, "unlabeledpotion", "unlabeledpotion_shadow");
        ProjectileRegistry.registerProjectile("necroticgreatswordwave", NecroticGreatswordWaveProjectile.class, "necroticwave", null);
        ProjectileRegistry.registerProjectile("hexedbladegreatswordwave", HexedBladeGreatswordWaveProjectile.class, "hexedwave", null);
        ProjectileRegistry.registerProjectile("evilwitchgreatswordwave", EvilWitchGreatswordWaveProjectile.class, "hostilenecroticwave", null);
        ProjectileRegistry.registerProjectile("pathtest", PathTestProjectile.class, null, "bolt_shadow");
        ProjectileRegistry.registerProjectile("swamptome", SwampTomeProjectile.class, "swamptome", "swamptome_shadow");
        ProjectileRegistry.registerProjectile("fallenwizardscepter", FallenWizardScepterProjectile.class, "fallenwizardscepter", "fallenwizardscepter_shadow");
        ProjectileRegistry.registerProjectile("fallenwizardwave", FallenWizardWaveProjectile.class, "fallenwizardwave", null);
        ProjectileRegistry.registerProjectile("fallenwizardball", FallenWizardBallProjectile.class, "fallenwizardball", "fallenwizardball_shadow");
        ProjectileRegistry.registerProjectile("skeletonmage", SkeletonMageProjectile.class, "skeletonmage", "skeletonmage_shadow");
        ProjectileRegistry.registerProjectile("ancientskeletonmage", AncientSkeletonMageProjectile.class, "ancientskeletonmage", "ancientskeletonmage_shadow");
        ProjectileRegistry.registerProjectile("swampdwellerstaffflower", SwampDwellerStaffFlowerProjectile.class, "swampdwellerstaffflower", "swampdwellerstaffflower_shadow");
        ProjectileRegistry.registerProjectile("swampdwellerstaffpetal", SwampDwellerStaffPetalProjectile.class, "swampdwellerstaffpetal", "swampdwellerstaffpetal_shadow");
        ProjectileRegistry.registerProjectile("druidsgreatbowpetal", DruidsGreatBowPetalProjectile.class, "druidsgreatbowpetal", "druidsgreatbowpetal_shadow");
        ProjectileRegistry.registerProjectile("nightpiercerarrow", NightPiercerArrowProjectile.class, "nightpiercerarrow", "nightpiercerarrow_shadow");
        ProjectileRegistry.registerProjectile("spideritearrow", SpideriteArrowProjectile.class, "spideritearrow", "spideritearrow_shadow");
        ProjectileRegistry.registerProjectile("swampball", SwampBallProjectile.class, "swampball", "swampball_shadow");
        ProjectileRegistry.registerProjectile("venomshower", VenomShowerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("agedchampionwave", AgedChampionWaveProjectile.class, "agedchampionwave", null);
        ProjectileRegistry.registerProjectile("venomslasherwave", VenomSlasherWaveProjectile.class, "venomslasherwave", null);
        ProjectileRegistry.registerProjectile("slimegreatswordprojectile", SlimeGreatswordProjectile.class, "slimegreatswordprojectile", null);
        ProjectileRegistry.registerProjectile("slimebolt", SlimeBoltProjectile.class, "slimebolt", "slimebolt_shadow");
        ProjectileRegistry.registerProjectile("livingshotty", LivingShottyLeafProjectile.class, "livingshotty", "livingshotty_shadow");
        ProjectileRegistry.registerProjectile("bouncingslimeball", BouncingSlimeBallProjectile.class, "bouncingslimeball", "bouncingslimeball_shadow");
        ProjectileRegistry.registerProjectile("mageslimebolt", MageSlimeBoltProjectile.class, "mageslimebolt", "mageslimebolt_shadow");
        ProjectileRegistry.registerProjectile("phantombolt", PhantomBoltProjectile.class, "phantombolt", "phantombolt_shadow");
        ProjectileRegistry.registerProjectile("cryptvampirebolt", CryptVampireBoltProjectile.class, "cryptvampirebolt", "cryptvampirebolt_shadow");
        ProjectileRegistry.registerProjectile("slimeegg", SlimeEggProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("phantombobble", PhantomBobbleProjectile.class, "phantombobble", "phantombobble_shadow");
        ProjectileRegistry.registerProjectile("phantommissile", PhantomMissileProjectile.class, "phantommissile", "phantommissile_shadow");
        ProjectileRegistry.registerProjectile("nightrazorboomerang", NightRazorBoomerangProjectile.class, "nightrazorboomerang", "nightrazorboomerang_shadow");
        ProjectileRegistry.registerProjectile("bloodgrimoire", BloodGrimoireRightClickProjectile.class, "bloodgrimoireprojectile", null);
        ProjectileRegistry.registerProjectile("bloodclaw", BloodClawProjectile.class, "bloodclaw", null);
        ProjectileRegistry.registerProjectile("thecrimsonskyarrow", CrimsonSkyArrowProjectile.class, "thecrimsonskyarrow", "nightpiercerarrow_shadow");
        ProjectileRegistry.registerProjectile("thecrimsonskypatharrow", CrimsonSkyArrowPathProjectile.class, "thecrimsonskyarrow", "nightpiercerarrow_shadow");
        ProjectileRegistry.registerProjectile("slimegreatbowarrow", SlimeGreatBowArrowProjectile.class, "slimegreatbowarrow", "nightpiercerarrow_shadow");
        ProjectileRegistry.registerProjectile("causticexecutioner", CausticExecutionerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("spideritewave", SpideriteWaveProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("empresswebball", EmpressWebBallProjectile.class, "webball", "webball_shadow");
        ProjectileRegistry.registerProjectile("empressslashwarning", EmpressSlashWarningProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("empressslash", EmpressSlashProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("empressacid", EmpressAcidProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("theravensnest", TheRavensNestProjectile.class, "theravensnest", "theravensnestshadow");
        ProjectileRegistry.registerProjectile("dawnfireball", DawnFireballProjectile.class, "dawnfireball", null);
        ProjectileRegistry.registerProjectile("duskvolley", DuskVolleyProjectile.class, "duskvolley", null);
        ProjectileRegistry.registerProjectile("starvail", StarVeilProjectile.class, "starveil", null);
        ProjectileRegistry.registerProjectile("crescentdisc", CrescentDiscFollowingProjectile.class, "crescentdisc", null);
        ProjectileRegistry.registerProjectile("ravenbeaktip", RavenBeakSpearProjectile.class, "ravenbeaktip", null);
        ProjectileRegistry.registerProjectile("stormingprojectile", StormingIncursionModifierProjectile.class, "stormingprojectile", null);
        ProjectileRegistry.registerProjectile("crystalbullet", CrystalBulletProjectile.class, "crystalbullet", "crystalbullet_shadow");
        ProjectileRegistry.registerProjectile("shardslinger", SapphireRevolverProjectile.class, "crystalbullet", "crystalbullet_shadow");
        ProjectileRegistry.registerProjectile("emeraldwand", EmeraldWandProjectile.class, "emeraldwandprojectile", null);
        ProjectileRegistry.registerProjectile("crystalshieldretaliation", CrystalShieldRetaliationProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("crystalgolembeam", CrystalGolemBeamProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("crystaldragonshard", CrystalDragonShardProjectile.class, "crystaldragonshard", "crystalbullet_shadow");
        ProjectileRegistry.registerProjectile("crystalgolemspawn", CrystalGolemSpawnProjectile.class, null, "queenspideregg_shadow");
        ProjectileRegistry.registerProjectile("trenchcoatgoblinspawn", TrenchcoatGoblinSpawnProjectile.class, null, "crystalbullet_shadow");
        ProjectileRegistry.registerProjectile("stabbybushspawn", StabbyBushSpawnProjectile.class, null, "queenspideregg_shadow");
        ProjectileRegistry.registerProjectile("bashybushspawn", BashyBushSpawnProjectile.class, null, "queenspideregg_shadow");
        ProjectileRegistry.registerProjectile("seedbullet", SeedBulletProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("fishianwarriorhook", FishianWarriorHookBoomerangProjectile.class, "fishianwarriorhook", null);
        ProjectileRegistry.registerProjectile("anchor", AnchorBoomerangProjectile.class, "anchor", "anchor_shadow");
        ProjectileRegistry.registerProjectile("fishianheal", FishianHealProjectile.class, "fishianheal", null);
        ProjectileRegistry.registerProjectile("staticjellyfish", StaticJellyfishProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("bonespikesprojectile", BoneSpikesProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("earthspikesprojectile", EarthSpikesProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("trappernet", TrapperNetProjectile.class, "trappernet", "trappernet_shadow");
        ProjectileRegistry.registerProjectile("chieftainshield", ChieftainShieldProjectile.class, "chieftainshield", "chieftainshield_shadow");
        ProjectileRegistry.registerProjectile("dryadleaf", DryadBowProjectile.class, "dryadleaf", "dryadleaf_shadow");
        ProjectileRegistry.registerProjectile("smallspiritleaf", SmallSpiritLeafProjectile.class, "smallspiritleaf", null);
        ProjectileRegistry.registerProjectile("spiritorb", SpiritOrbProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("barkbladeleaf", BarkBladeLeafProjectile.class, "barkbladeleaf", "barkbladeleaf_shadow");
        ProjectileRegistry.registerProjectile("tictactoepunish", TicTacToePunishProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("spiritbeam", SpiritBeamProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("spiritskull", SpiritSkullProjectile.class, "spiritskull", null);
        ProjectileRegistry.registerProjectile("flamelingshooterprojectile", FlamelingShooterProjectile.class, "evilsprotector1", "evilsprotector1_shadow");
        ProjectileRegistry.registerProjectile("chromaticbolt", ChromaticBoltProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("ghostskull", GhostSkullProjectile.class, "skulls", "cannonball_shadow");
        ProjectileRegistry.registerProjectile("ancestormageprojectile", AncestorMageProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("crazedravenfeather", CrazedRavenFeatherProjectile.class, "ravenbeaktip", null);
        ProjectileRegistry.registerProjectile("smitebeam", SmiteBeamProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("weaponchargesmitebeam", WeaponChargeSmiteBeamProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("ghostarrow", GhostArrowProjectile.class, "ghostarrow", null);
        ProjectileRegistry.registerProjectile("chargebeam", ChargeBeamProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("chargeshower", ChargeShowerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("ascendedbolt", AscendedBoltProjectile.class, "ascendedbolt", "ascendedbolt_shadow");
        ProjectileRegistry.registerProjectile("ascendedboltsound", AscendedBoltSoundProjectile.class, "ascendedbolt", "ascendedbolt_shadow");
        ProjectileRegistry.registerProjectile("ascendedbomb", AscendedBombProjectile.class, "ascendedbomb", "ascendedbomb_shadow");
        ProjectileRegistry.registerProjectile("ascendedorb", AscendedOrbProjectile.class, "ascendedorb", "ascendedorb_shadow");
        ProjectileRegistry.registerProjectile("ascendedfracture", AscendedFractureProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("ascendedslash", AscendedSlashProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("ascendedbeam", AscendedBeamProjectile.class, "ascendedbeam", "ascendedbeam_shadow");
        ProjectileRegistry.registerProjectile("ascendedshardbomb", AscendedShardBombProjectile.class, "ascendedshardbomb", "ascendedshardbomb_shadow");
        ProjectileRegistry.registerProjectile("ascendedshard", AscendedShardProjectile.class, "ascendedshard", "ascendedshard_shadow");
        ProjectileRegistry.registerProjectile("ascendedgolembeam", AscendedGolemBeamProjectile.class, null, null);
        ProjectileRegistry.registerProjectile("ascendedgolemspawn", AscendedGolemSpawnProjectile.class, null, "queenspideregg_shadow");
        ProjectileRegistry.registerProjectile("ascendedstaffbeam", AscendedStaffBeamProjectile.class, "ascendedbeam", "ascendedbeam_shadow");
        ProjectileRegistry.registerProjectile("eyeofthevoidspawnprojectile", EyeOfTheVoidSpawnProjectile.class, "eyeofthevoidprojectile", "ascendedbeam_shadow");
        ProjectileRegistry.registerProjectile("ascendedbowbolt", AscendedBowBoltProjectile.class, "ascendedbolt", "ascendedbolt_shadow");
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerProjectile(String stringID, Class<? extends Projectile> projectileClass, String texturePath, String shadowTexturePath) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register projectiles");
        }
        try {
            return instance.register(stringID, new ProjectileRegistryElement(projectileClass, texturePath, shadowTexturePath));
        }
        catch (NoSuchMethodException e) {
            System.err.println("Could not register projectile " + projectileClass.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
        }
    }

    public static Projectile getProjectile(int id) {
        try {
            return (Projectile)((ProjectileRegistryElement)instance.getElement(id)).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Projectile getProjectile(int id, Level level) {
        Projectile out = ProjectileRegistry.getProjectile(id);
        out.setLevel(level);
        return out;
    }

    public static Projectile getProjectile(String stringID) {
        return ProjectileRegistry.getProjectile(ProjectileRegistry.getProjectileID(stringID));
    }

    public static Projectile getProjectile(String stringID, Level level) {
        return ProjectileRegistry.getProjectile(ProjectileRegistry.getProjectileID(stringID), level);
    }

    public static Projectile getProjectile(int id, Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        Projectile out = ProjectileRegistry.getProjectile(id);
        out.applyData(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
        out.setLevel(level);
        return out;
    }

    public static Projectile getProjectile(int id, Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, Mob owner) {
        Projectile out = ProjectileRegistry.getProjectile(id, level);
        out.applyData(x, y, targetX, targetY, speed, distance, damage, owner);
        return out;
    }

    public static Projectile getProjectile(String stringID, Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        return ProjectileRegistry.getProjectile(ProjectileRegistry.getProjectileID(stringID), level, x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }

    public static Projectile getProjectile(String stringID, Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, Mob owner) {
        return ProjectileRegistry.getProjectile(ProjectileRegistry.getProjectileID(stringID), level, x, y, targetX, targetY, speed, distance, damage, owner);
    }

    public static int getProjectileID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static int getProjectileID(Class<? extends Projectile> clazz) {
        return instance.getElementID(clazz);
    }

    public static String getProjectileStringID(int id) {
        return instance.getElementStringID(id);
    }

    protected static class ProjectileRegistryElement
    extends ClassIDDataContainer<Projectile> {
        public final String texturePath;
        public final String shadowTexturePath;
        public GameTexture texture;
        public GameTexture shadowTexture;

        public ProjectileRegistryElement(Class<? extends Projectile> pickupClass, String texturePath, String shadowTexturePath) throws NoSuchMethodException {
            super(pickupClass, new Class[0]);
            this.texturePath = texturePath;
            this.shadowTexturePath = shadowTexturePath;
        }

        public void loadTexture() {
            this.texture = this.texturePath != null ? GameTexture.fromFile("projectiles/" + this.texturePath) : null;
            this.shadowTexture = this.shadowTexturePath != null ? GameTexture.fromFile("projectiles/" + this.shadowTexturePath) : null;
        }
    }

    public static class Textures {
        public static void load() {
            instance.streamElements().forEach(ProjectileRegistryElement::loadTexture);
        }

        public static GameTexture getTexture(int id) {
            if (id < 0 || id > instance.size() - 1) {
                return GameResources.error;
            }
            GameTexture texture = ((ProjectileRegistryElement)ProjectileRegistry.instance.getElement((int)id)).texture;
            return texture == null ? GameResources.error : texture;
        }

        public static GameTexture getShadowTexture(int id) {
            if (id < 0 || id > instance.size() - 1) {
                return null;
            }
            return ((ProjectileRegistryElement)ProjectileRegistry.instance.getElement((int)id)).shadowTexture;
        }
    }
}

