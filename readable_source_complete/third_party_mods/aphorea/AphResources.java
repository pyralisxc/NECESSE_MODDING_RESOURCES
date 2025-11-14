/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.MobRegistry$Textures
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.HumanTexture
 *  necesse.gfx.GameResources
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTexture.GameTextureSection
 */
package aphorea;

import aphorea.items.tools.weapons.magic.MagicalBroom;
import aphorea.mobs.bosses.BabylonTowerMob;
import aphorea.mobs.bosses.UnstableGelSlime;
import aphorea.mobs.bosses.minions.MiniUnstableGelSlime;
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
import aphorea.mobs.summon.BabyUnstableGelSlime;
import aphorea.mobs.summon.LivingSapling;
import aphorea.mobs.summon.Onyx;
import aphorea.mobs.summon.VolatileGelSlime;
import aphorea.particles.BabylonTowerFallingCrystalParticle;
import aphorea.particles.NarcissistParticle;
import aphorea.particles.SpinelShieldParticle;
import aphorea.projectiles.bullet.SpamBulletProjectile;
import aphorea.projectiles.toolitem.AircutProjectile;
import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.projectiles.toolitem.GelProjectile;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.HumanTexture;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;

public class AphResources {
    public static GameTexture modLogo;
    public static GameTexture[] saberAttackTexture;
    public static GameTexture gunAttackTrackTexture;
    public static GameTexture gunAttackThumbTexture;
    public static GameTexture glacialSaberAttackTrackTexture;
    public static GameTexture glacialSaberAttackThumbTexture;
    public static Map<String, GameTexture> bookTextures;

    public static void initResources() {
        modLogo = GameTexture.fromFile((String)"ui/logo");
        for (int i = 0; i < 28; ++i) {
            AphResources.saberAttackTexture[i] = new GameTexture(GameTexture.fromFile((String)"ui/saberattack"), 0, 22 * i, 66, 22);
        }
        gunAttackTrackTexture = GameTexture.fromFile((String)"ui/gunattacktrack");
        gunAttackThumbTexture = GameTexture.fromFile((String)"ui/gunattackthumb");
        glacialSaberAttackTrackTexture = GameTexture.fromFile((String)"ui/glacialsaberattacktrack");
        glacialSaberAttackThumbTexture = GameTexture.fromFile((String)"ui/glacialsaberattackthumb");
        bookTextures.put("runestutorial_open", GameTexture.fromFile((String)"ui/books/runestutorial_open"));
        bookTextures.put("runestutorial_equip", GameTexture.fromFile((String)"ui/books/runestutorial_equip"));
        bookTextures.put("runestutorial_baserunes", GameTexture.fromFile((String)"ui/books/runestutorial_baserunes"));
        bookTextures.put("runestutorial_modifierrunes", GameTexture.fromFile((String)"ui/books/runestutorial_modifierrunes"));
        bookTextures.put("runestutorial_table", GameTexture.fromFile((String)"ui/books/runestutorial_table"));
        bookTextures.put("runestutorial_craft", GameTexture.fromFile((String)"ui/books/runestutorial_craft"));
        AphResources.mobResources();
        MagicalBroom.worldTexture = GameTexture.fromFile((String)"worlditems/magicalbroom");
        AphResources.projectileResources();
        GelProjectile.GelProjectileParticle.texture = GameTexture.fromFile((String)"particles/gelprojectile");
        SpamBulletProjectile.FirePoolParticle.texture = GameTexture.fromFile((String)"particles/firepool");
        BabylonTowerFallingCrystalParticle.projectileTexture = GameTexture.fromFile((String)"particles/babylontowerfallingcrystalparticle");
        BabylonTowerFallingCrystalParticle.shadowTexture = GameTexture.fromFile((String)"particles/babylontowerfallingcrystalparticle_shadow");
        SpinelShieldParticle.texture = GameTexture.fromFile((String)"particles/spinelshield");
        NarcissistParticle.texture = GameTexture.fromFile((String)"player/weapons/thenarcissist");
        SOUNDS.initSoundResources();
    }

    public static void mobResources() {
        GelSlime.texture = GameTexture.fromFile((String)"mobs/gelslime");
        RockyGelSlime.texture = GameTexture.fromFile((String)"mobs/rockygelslime");
        PinkWitch.texture = GameTexture.fromFile((String)"mobs/pinkwitch");
        VoidAdept.texture = MobRegistry.Textures.humanTexture((String)"voidadept");
        DaggerGoblin.humanTexture = new HumanTexture(GameTexture.fromFile((String)"mobs/daggergoblin"), null, null);
        InfectedTreant.texture = GameTexture.fromFile((String)"mobs/infectedtreant");
        InfectedTreant.texture_shadow = GameTexture.fromFile((String)"mobs/infectedtreant_shadow");
        InfectedTreant.leavesTexture = AphResources.loadLeafTexture(InfectedTreant.leavesTextureName);
        SpinelGolem.texture = GameTexture.fromFile((String)"mobs/spinelgolem");
        SpinelCaveling.texture = new HumanTexture(GameTexture.fromFile((String)"mobs/spinelcaveling"), GameTexture.fromFile((String)"mobs/spinelcavelingarms_front"), GameTexture.fromFile((String)"mobs/spinelcavelingarms_back"));
        TungstenCaveling.texture = new HumanTexture(GameTexture.fromFile((String)"mobs/tungstencaveling"), GameTexture.fromFile((String)"mobs/tungstencavelingarms_front"), GameTexture.fromFile((String)"mobs/tungstencavelingarms_back"));
        SpinelMimic.texture = GameTexture.fromFile((String)"mobs/spinelmimic");
        SpinelMimic.texture_shadow = GameTexture.fromFile((String)"mobs/spinelmimic_shadow");
        UnstableGelSlime.texture = GameTexture.fromFile((String)"mobs/unstablegelslime");
        UnstableGelSlime.icon = GameTexture.fromFile((String)"mobs/icons/unstablegelslime");
        MiniUnstableGelSlime.texture = GameTexture.fromFile((String)"mobs/miniunstablegelslime");
        BabylonTowerMob.icon = GameTexture.fromFile((String)"mobs/icons/babylontower");
        BabylonHead.texture = GameTexture.fromFile((String)"mobs/babylon");
        BabylonHead.texture_shadow = GameTexture.fromFile((String)"mobs/babylon_shadow");
        BabylonHead.icon = GameTexture.fromFile((String)"mobs/babylon");
        WildPhosphorSlime.texture = GameTexture.fromFile((String)"mobs/phosphorslime");
        WildPhosphorSlime.texture_scared = GameTexture.fromFile((String)"mobs/phosphorslime_scared");
        BabyUnstableGelSlime.texture = GameTexture.fromFile((String)"mobs/babyunstablegelslime");
        VolatileGelSlime.texture = GameTexture.fromFile((String)"mobs/volatilegelslime");
        Onyx.texture = GameTexture.fromFile((String)"mobs/onyx");
        LivingSapling.texture = GameTexture.fromFile((String)"mobs/livingsapling");
        LivingSapling.texture_shadow = GameTexture.fromFile((String)"mobs/livingsapling_shadow");
        LivingSapling.leavesTexture = AphResources.loadLeafTexture(LivingSapling.leavesTextureName);
        PetPhosphorSlime.texture = GameTexture.fromFile((String)"mobs/phosphorslime");
        PetPhosphorSlime.texture_scared = GameTexture.fromFile((String)"mobs/phosphorslime_scared");
    }

    public static void projectileResources() {
        AircutProjectile.CopperAircutProjectile.texture = GameTexture.fromFile((String)"projectiles/aircutcopper");
        AircutProjectile.IronAircutProjectile.texture = GameTexture.fromFile((String)"projectiles/aircutiron");
        AircutProjectile.GoldAircutProjectile.texture = GameTexture.fromFile((String)"projectiles/aircutgold");
        AircutProjectile.UnstableGelAircutProjectile.texture = GameTexture.fromFile((String)"projectiles/aircutunstablegel");
        AircutProjectile.DemonicAircutProjectile.texture = GameTexture.fromFile((String)"projectiles/aircutdemonic");
        AircutProjectile.CrimsonAircutProjectile.texture = GameTexture.fromFile((String)"projectiles/aircutcrimson");
        DaggerProjectile.CopperDaggerProjectile.texture = GameTexture.fromFile((String)"player/weapons/copperdagger");
        DaggerProjectile.IronDaggerProjectile.texture = GameTexture.fromFile((String)"player/weapons/irondagger");
        DaggerProjectile.GoldDaggerProjectile.texture = GameTexture.fromFile((String)"player/weapons/golddagger");
        DaggerProjectile.DemonicDaggerProjectile.texture = GameTexture.fromFile((String)"player/weapons/demonicdagger");
        DaggerProjectile.TungstenDaggerProjectile.texture = GameTexture.fromFile((String)"player/weapons/tungstendagger");
        DaggerProjectile.LostUmbrellaDaggerProjectile.texture = GameTexture.fromFile((String)"player/weapons/lostumbrella");
    }

    public static Supplier<GameTextureSection> loadLeafTexture(String leavesTextureName) {
        if (leavesTextureName != null) {
            try {
                GameTexture particleTexture = GameTexture.fromFileRaw((String)("particles/" + leavesTextureName));
                int leavesRes = particleTexture.getHeight();
                int leafSprites = particleTexture.getWidth() / leavesRes;
                GameTextureSection particleSection = GameResources.particlesTextureGenerator.addTexture(particleTexture);
                return () -> particleSection.sprite(GameRandom.globalRandom.nextInt(leafSprites), 0, leavesRes);
            }
            catch (FileNotFoundException var5) {
                return null;
            }
        }
        return null;
    }

    static {
        saberAttackTexture = new GameTexture[28];
        bookTextures = new HashMap<String, GameTexture>();
    }

    public static class SOUNDS {
        public static void initSoundResources() {
            HARP.Do = GameSound.fromFile((String)"do_harp");
            HARP.Re = GameSound.fromFile((String)"re_harp");
            HARP.Mi = GameSound.fromFile((String)"mi_harp");
            HARP.Fa = GameSound.fromFile((String)"fa_harp");
            HARP.Sol = GameSound.fromFile((String)"sol_harp");
            HARP.La = GameSound.fromFile((String)"la_harp");
            HARP.Si = GameSound.fromFile((String)"si_harp");
            HARP.All = new GameSound[]{HARP.Do, HARP.Re, HARP.Mi, HARP.Fa, HARP.Sol, HARP.La, HARP.Si};
        }

        public static class HARP {
            public static GameSound Do;
            public static GameSound Re;
            public static GameSound Mi;
            public static GameSound Fa;
            public static GameSound Sol;
            public static GameSound La;
            public static GameSound Si;
            public static GameSound[] All;
        }
    }
}

