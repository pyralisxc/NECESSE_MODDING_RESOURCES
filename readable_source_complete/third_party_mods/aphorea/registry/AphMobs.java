/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.MobRegistry
 */
package aphorea.registry;

import aphorea.mobs.bosses.BabylonTowerMob;
import aphorea.mobs.bosses.UnstableGelSlime;
import aphorea.mobs.bosses.minions.HearthCrystalMob;
import aphorea.mobs.bosses.minions.MiniUnstableGelSlime;
import aphorea.mobs.bosses.minions.babylon.BabylonBody;
import aphorea.mobs.bosses.minions.babylon.BabylonHead;
import aphorea.mobs.friendly.WildPhosphorSlime;
import aphorea.mobs.hostile.DaggerGoblin;
import aphorea.mobs.hostile.GelSlime;
import aphorea.mobs.hostile.InfectedTreant;
import aphorea.mobs.hostile.PinkWitch;
import aphorea.mobs.hostile.RockyGelSlime;
import aphorea.mobs.hostile.SpinelCaveling;
import aphorea.mobs.hostile.SpinelGolem;
import aphorea.mobs.hostile.SpinelMimic;
import aphorea.mobs.hostile.TungstenCaveling;
import aphorea.mobs.hostile.VoidAdept;
import aphorea.mobs.pet.PetPhosphorSlime;
import aphorea.mobs.runicsummons.RunicBat;
import aphorea.mobs.runicsummons.RunicUnstableGelSlime;
import aphorea.mobs.runicsummons.RunicVultureHatchling;
import aphorea.mobs.summon.BabyUnstableGelSlime;
import aphorea.mobs.summon.LivingSapling;
import aphorea.mobs.summon.Onyx;
import aphorea.mobs.summon.UndeadSkeleton;
import aphorea.mobs.summon.VolatileGelSlime;
import necesse.engine.registries.MobRegistry;

public class AphMobs {
    public static void registerCore() {
        MobRegistry.registerMob((String)"gelslime", GelSlime.class, (boolean)true);
        MobRegistry.registerMob((String)"rockygelslime", RockyGelSlime.class, (boolean)true);
        MobRegistry.registerMob((String)"pinkwitch", PinkWitch.class, (boolean)true);
        MobRegistry.registerMob((String)"voidadept", VoidAdept.class, (boolean)true);
        MobRegistry.registerMob((String)"wildphosphorslime", WildPhosphorSlime.class, (boolean)true);
        MobRegistry.registerMob((String)"copperdaggergoblin", DaggerGoblin.CopperDaggerGoblin.class, (boolean)true);
        MobRegistry.registerMob((String)"irondaggergoblin", DaggerGoblin.IronDaggerGoblin.class, (boolean)true);
        MobRegistry.registerMob((String)"golddaggergoblin", DaggerGoblin.GoldDaggerGoblin.class, (boolean)true);
        MobRegistry.registerMob((String)"infectedtreant", InfectedTreant.class, (boolean)true);
        MobRegistry.registerMob((String)"spinelgolem", SpinelGolem.class, (boolean)true);
        MobRegistry.registerMob((String)"spinelcaveling", SpinelCaveling.class, (boolean)true);
        MobRegistry.registerMob((String)"tungstencaveling", TungstenCaveling.class, (boolean)true);
        MobRegistry.registerMob((String)"spinelmimic", SpinelMimic.class, (boolean)true);
        MobRegistry.registerMob((String)"unstablegelslime", UnstableGelSlime.class, (boolean)true, (boolean)true);
        MobRegistry.registerMob((String)"miniunstablegelslime", MiniUnstableGelSlime.class, (boolean)true, (boolean)false, (boolean)false);
        MobRegistry.registerMob((String)"babylontower", BabylonTowerMob.class, (boolean)true, (boolean)true);
        MobRegistry.registerMob((String)"babylon", BabylonHead.class, (boolean)false);
        MobRegistry.registerMob((String)"babylonbody", BabylonBody.class, (boolean)false, (boolean)false, (boolean)false);
        MobRegistry.registerMob((String)"hearthcrystal", HearthCrystalMob.class, (boolean)true, (boolean)false, (boolean)false);
        MobRegistry.registerMob((String)"babyunstablegelslime", BabyUnstableGelSlime.class, (boolean)false);
        MobRegistry.registerMob((String)"volatilegelslime", VolatileGelSlime.class, (boolean)false);
        MobRegistry.registerMob((String)"undeadskeleton", UndeadSkeleton.class, (boolean)false);
        MobRegistry.registerMob((String)"onyx", Onyx.class, (boolean)false);
        MobRegistry.registerMob((String)"livingsapling", LivingSapling.class, (boolean)false);
        MobRegistry.registerMob((String)"runicunstablegelslime", RunicUnstableGelSlime.class, (boolean)false);
        MobRegistry.registerMob((String)"runicvulturehatchling", RunicVultureHatchling.class, (boolean)false);
        MobRegistry.registerMob((String)"runicbat", RunicBat.class, (boolean)false);
        MobRegistry.registerMob((String)"petphosphorslime", PetPhosphorSlime.class, (boolean)false);
    }
}

