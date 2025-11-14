/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.ProjectileRegistry
 */
package aphorea.registry;

import aphorea.projectiles.arrow.GelArrowProjectile;
import aphorea.projectiles.arrow.UnstableGelArrowProjectile;
import aphorea.projectiles.bullet.ShotgunBulletProjectile;
import aphorea.projectiles.bullet.SpamBulletProjectile;
import aphorea.projectiles.mob.MiniUnstableGelSlimeProjectile;
import aphorea.projectiles.mob.PinkWitchProjectile;
import aphorea.projectiles.mob.RockyGelSlimeLootProjectile;
import aphorea.projectiles.mob.RockyGelSlimeProjectile;
import aphorea.projectiles.mob.SpinelGolemBeamProjectile;
import aphorea.projectiles.rune.RuneOfCryoQueenProjectile;
import aphorea.projectiles.rune.RuneOfSpiderEmpressProjectile;
import aphorea.projectiles.toolitem.AircutProjectile;
import aphorea.projectiles.toolitem.BabylonCandleProjectile;
import aphorea.projectiles.toolitem.BlueBerryProjectile;
import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.projectiles.toolitem.FireSlingStoneProjectile;
import aphorea.projectiles.toolitem.FrozenSlingStoneProjectile;
import aphorea.projectiles.toolitem.GelProjectile;
import aphorea.projectiles.toolitem.GlacialShardBigProjectile;
import aphorea.projectiles.toolitem.GlacialShardMediumProjectile;
import aphorea.projectiles.toolitem.GlacialShardSmallProjectile;
import aphorea.projectiles.toolitem.GoldenWandProjectile;
import aphorea.projectiles.toolitem.HoneyProjectile;
import aphorea.projectiles.toolitem.MusicalNoteProjectile;
import aphorea.projectiles.toolitem.OpenLostUmbrellaProjectile;
import aphorea.projectiles.toolitem.SlingStoneProjectile;
import aphorea.projectiles.toolitem.SpinelArrowProjectile;
import aphorea.projectiles.toolitem.UnstableGelProjectile;
import aphorea.projectiles.toolitem.UnstableGelvelineProjectile;
import aphorea.projectiles.toolitem.VoidStoneProjectile;
import aphorea.projectiles.toolitem.WoodenWandProjectile;
import necesse.engine.registries.ProjectileRegistry;

public class AphProjectiles {
    public static void registerCore() {
        ProjectileRegistry.registerProjectile((String)"gelarrow", GelArrowProjectile.class, (String)"gelarrow", (String)"gelarrow_shadow");
        ProjectileRegistry.registerProjectile((String)"unstablegelarrow", UnstableGelArrowProjectile.class, (String)"unstablegelarrow", (String)"unstablegelarrow_shadow");
        ProjectileRegistry.registerProjectile((String)"spambullet", SpamBulletProjectile.class, (String)"spambullet", (String)"ball_shadow");
        ProjectileRegistry.registerProjectile((String)"shotgunbullet", ShotgunBulletProjectile.class, (String)"shotgunbullet", null);
        ProjectileRegistry.registerProjectile((String)"gel", GelProjectile.class, (String)"gel", (String)"ball_shadow");
        ProjectileRegistry.registerProjectile((String)"unstablegel", UnstableGelProjectile.class, (String)"unstablegel", (String)"unstablegel_shadow");
        ProjectileRegistry.registerProjectile((String)"slingstone", SlingStoneProjectile.class, (String)"slingstone", (String)"ball_shadow");
        ProjectileRegistry.registerProjectile((String)"slingfirestone", FireSlingStoneProjectile.class, (String)"slingfirestone", (String)"ball_shadow");
        ProjectileRegistry.registerProjectile((String)"slingfrozenstone", FrozenSlingStoneProjectile.class, (String)"slingfrozenstone", (String)"ball_shadow");
        ProjectileRegistry.registerProjectile((String)"unstablegelveline", UnstableGelvelineProjectile.class, (String)"unstablegelveline", (String)"unstablegelveline_shadow");
        ProjectileRegistry.registerProjectile((String)"voidstone", VoidStoneProjectile.class, (String)"voidstone", (String)"voidstone_shadow");
        ProjectileRegistry.registerProjectile((String)"woodenwand", WoodenWandProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"goldenwand", GoldenWandProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"bigglacialshard", GlacialShardBigProjectile.class, (String)"glacialshard_big", (String)"glacialshard_big_shadow");
        ProjectileRegistry.registerProjectile((String)"mediumglacialshard", GlacialShardMediumProjectile.class, (String)"glacialshard_medium", (String)"glacialshard_medium_shadow");
        ProjectileRegistry.registerProjectile((String)"smallglacialshard", GlacialShardSmallProjectile.class, (String)"glacialshard_small", (String)"glacialshard_small_shadow");
        ProjectileRegistry.registerProjectile((String)"honey", HoneyProjectile.class, (String)"honey", (String)"honey_shadow");
        ProjectileRegistry.registerProjectile((String)"blueberry", BlueBerryProjectile.class, (String)"blueberry", (String)"blueberry_shadow");
        ProjectileRegistry.registerProjectile((String)"musicalnote", MusicalNoteProjectile.class, (String)"musicalnote", (String)"musicalnote_shadow");
        ProjectileRegistry.registerProjectile((String)"spinelarrow", SpinelArrowProjectile.class, (String)"spinelarrow", (String)"spinelarrow_shadow");
        ProjectileRegistry.registerProjectile((String)"babyloncandle", BabylonCandleProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"copperaircut", AircutProjectile.CopperAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"ironaircut", AircutProjectile.IronAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"goldaircut", AircutProjectile.GoldAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"unstablegelaircut", AircutProjectile.UnstableGelAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"demonicaircut", AircutProjectile.DemonicAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"crimsonaircut", AircutProjectile.CrimsonAircutProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"copperdagger", DaggerProjectile.CopperDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"irondagger", DaggerProjectile.IronDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"golddagger", DaggerProjectile.GoldDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"demonicdagger", DaggerProjectile.DemonicDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"tungstendagger", DaggerProjectile.TungstenDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"lostumbrelladagger", DaggerProjectile.LostUmbrellaDaggerProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"openlostumbrella", OpenLostUmbrellaProjectile.class, (String)"lostumbrella_open", null);
        ProjectileRegistry.registerProjectile((String)"pinkwitch", PinkWitchProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"rockygelslime", RockyGelSlimeProjectile.class, (String)"stone", (String)"stone_shadow");
        ProjectileRegistry.registerProjectile((String)"rockygelslimeloot", RockyGelSlimeLootProjectile.class, (String)"rockygel", (String)"ball_shadow");
        ProjectileRegistry.registerProjectile((String)"spinelgolembeam", SpinelGolemBeamProjectile.class, (String)"rockygel", (String)"ball_shadow");
        ProjectileRegistry.registerProjectile((String)"miniunstablegelslime", MiniUnstableGelSlimeProjectile.class, (String)"miniunstablegelslime", null);
        ProjectileRegistry.registerProjectile((String)"runeofcryoqueen", RuneOfCryoQueenProjectile.class, null, null);
        ProjectileRegistry.registerProjectile((String)"runeofspiderempress", RuneOfSpiderEmpressProjectile.class, (String)"webball", (String)"webball_shadow");
    }
}

