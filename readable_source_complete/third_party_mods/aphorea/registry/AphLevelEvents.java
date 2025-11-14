/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.LevelEventRegistry
 */
package aphorea.registry;

import aphorea.items.tools.weapons.melee.greatsword.logic.GreatswordDashLevelEvent;
import aphorea.items.tools.weapons.melee.rapier.logic.RapierDashLevelEvent;
import aphorea.items.tools.weapons.melee.saber.logic.SaberDashLevelEvent;
import aphorea.items.tools.weapons.melee.saber.logic.SaberJumpLevelEvent;
import aphorea.levelevents.AphNarcissistEvent;
import aphorea.levelevents.AphSpinelShieldEvent;
import aphorea.levelevents.babylon.BabylonTowerFallingCrystalAttackEvent;
import aphorea.levelevents.runes.AphAbysmalRuneEvent;
import aphorea.levelevents.runes.AphRuneOfCryoQueenEvent;
import aphorea.levelevents.runes.AphRuneOfCrystalDragonEvent;
import aphorea.levelevents.runes.AphRuneOfDetonationEvent;
import aphorea.levelevents.runes.AphRuneOfMotherSlimeEvent;
import aphorea.levelevents.runes.AphRuneOfPestWardenEvent;
import aphorea.levelevents.runes.AphRuneOfQueenSpiderEvent;
import aphorea.levelevents.runes.AphRuneOfSunlightChampionEvent;
import aphorea.levelevents.runes.AphRuneOfSunlightChampionExplosionEvent;
import aphorea.levelevents.runes.AphRuneOfThunderEvent;
import aphorea.levelevents.runes.AphTidalRuneEvent;
import aphorea.mobs.summon.VolatileGelSlime;
import aphorea.objects.BabylonEntranceObject;
import aphorea.projectiles.bullet.SpamBulletProjectile;
import aphorea.projectiles.toolitem.GelProjectile;
import necesse.engine.registries.LevelEventRegistry;

public class AphLevelEvents {
    public static void registerCore() {
        LevelEventRegistry.registerEvent((String)"gelprojectilegroundeffect", GelProjectile.GelProjectileGroundEffectEvent.class);
        LevelEventRegistry.registerEvent((String)"firepoolgroundeffect", SpamBulletProjectile.FirePoolGroundEffectEvent.class);
        LevelEventRegistry.registerEvent((String)"saberdash", SaberDashLevelEvent.class);
        LevelEventRegistry.registerEvent((String)"saberjump", SaberJumpLevelEvent.class);
        LevelEventRegistry.registerEvent((String)"rapierdash", RapierDashLevelEvent.class);
        LevelEventRegistry.registerEvent((String)"greatsworddash", GreatswordDashLevelEvent.class);
        LevelEventRegistry.registerEvent((String)"volatilegelexplosion", VolatileGelSlime.VolatileGelExplosion.class);
        LevelEventRegistry.registerEvent((String)"spambulletexplosion", SpamBulletProjectile.SpamBulletExplosion.class);
        LevelEventRegistry.registerEvent((String)"spinelshield", AphSpinelShieldEvent.class);
        LevelEventRegistry.registerEvent((String)"narcissistbuff", AphNarcissistEvent.class);
        LevelEventRegistry.registerEvent((String)"babylonentrance", BabylonEntranceObject.BabylonEntranceEvent.class);
        LevelEventRegistry.registerEvent((String)"babylontowerfallingcrystalattack", BabylonTowerFallingCrystalAttackEvent.class);
        AphLevelEvents.baseRunes();
        AphLevelEvents.modifierRunes();
    }

    public static void baseRunes() {
        LevelEventRegistry.registerEvent((String)"runeofdetonationevent", AphRuneOfDetonationEvent.class);
        LevelEventRegistry.registerEvent((String)"runeofthunderevent", AphRuneOfThunderEvent.class);
        LevelEventRegistry.registerEvent((String)"runeofqueenspiderevent", AphRuneOfQueenSpiderEvent.class);
        LevelEventRegistry.registerEvent((String)"runeofcryoqueenevent", AphRuneOfCryoQueenEvent.class);
        LevelEventRegistry.registerEvent((String)"runeofpestwardenevent", AphRuneOfPestWardenEvent.class);
        LevelEventRegistry.registerEvent((String)"runeofmotherslimeevent", AphRuneOfMotherSlimeEvent.class);
        LevelEventRegistry.registerEvent((String)"runeofsunlightchampionevent", AphRuneOfSunlightChampionEvent.class);
        LevelEventRegistry.registerEvent((String)"runeofsunlightchampionexplosionevent", AphRuneOfSunlightChampionExplosionEvent.class);
        LevelEventRegistry.registerEvent((String)"runeofcrystaldragonevent", AphRuneOfCrystalDragonEvent.class);
    }

    public static void modifierRunes() {
        LevelEventRegistry.registerEvent((String)"abysmalruneevent", AphAbysmalRuneEvent.class);
        LevelEventRegistry.registerEvent((String)"tildalruneevent", AphTidalRuneEvent.class);
    }
}

