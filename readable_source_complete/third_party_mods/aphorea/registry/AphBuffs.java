/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.Modifier
 *  necesse.engine.modifiers.ModifierValue
 *  necesse.engine.network.Packet
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.network.packet.PacketForceOfWind
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.registries.BuffRegistry$Debuffs
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.registries.MobRegistry
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobBeforeHitCalculatedEvent
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 *  necesse.entity.mobs.buffs.staticBuffs.HiddenCooldownBuff
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.SimpleTrinketBuff
 *  necesse.entity.mobs.itemAttacker.FollowPosition
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.particle.ParticleOption
 *  necesse.entity.particle.SmokePuffParticle
 *  necesse.gfx.GameResources
 *  necesse.gfx.gameFont.FontManager
 *  necesse.inventory.InventoryItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.regionSystem.RegionPositionGetter
 */
package aphorea.registry;

import aphorea.AphDependencies;
import aphorea.buffs.AdrenalineBuff;
import aphorea.buffs.AphShownBuff;
import aphorea.buffs.AphShownCooldownBuff;
import aphorea.buffs.Banners.AphBannerBuff;
import aphorea.buffs.Banners.AphBasicBannerBuff;
import aphorea.buffs.Banners.AphMightyBasicBannerBuff;
import aphorea.buffs.BerserkerRushActiveBuff;
import aphorea.buffs.CursedBuff;
import aphorea.buffs.DaggerAttackBuff;
import aphorea.buffs.HarmonyBuff;
import aphorea.buffs.HoneyedBuff;
import aphorea.buffs.NarcissistBuff;
import aphorea.buffs.Runes.AphBaseRuneActiveBuff;
import aphorea.buffs.Runes.AphBaseRuneTrinketBuff;
import aphorea.buffs.Runes.AphModifierRuneTrinketBuff;
import aphorea.buffs.SetBonus.GoldHatSetBonusBuff;
import aphorea.buffs.SetBonus.InfectedSetBonusBuff;
import aphorea.buffs.SetBonus.PinkWitchSetBonusBuff;
import aphorea.buffs.SetBonus.RockySetBonusBuff;
import aphorea.buffs.SetBonus.SpinelHatSetBonusBuff;
import aphorea.buffs.SetBonus.SpinelHelmetSetBonusBuff;
import aphorea.buffs.SetBonus.SwampHoodSetBonusBuff;
import aphorea.buffs.SetBonus.SwampMaskSetBonusBuff;
import aphorea.buffs.StickyBuff;
import aphorea.buffs.StopBuff;
import aphorea.buffs.StunBuff;
import aphorea.buffs.Trinkets.Charm.AdrenalineCharmBuff;
import aphorea.buffs.Trinkets.Charm.BloomrushCharmBuff;
import aphorea.buffs.Trinkets.Essence.EssenceofHealingBuff;
import aphorea.buffs.Trinkets.Foci.InspirationFociBuff;
import aphorea.buffs.Trinkets.Medallion.AncientMedallionBuff;
import aphorea.buffs.Trinkets.Medallion.CursedMedallionBuff;
import aphorea.buffs.Trinkets.Medallion.WitchMedallionBuff;
import aphorea.buffs.Trinkets.Periapt.AbysmalPeriaptBuff;
import aphorea.buffs.Trinkets.Periapt.BloodyPeriaptBuff;
import aphorea.buffs.Trinkets.Periapt.DemonicPeriaptBuff;
import aphorea.buffs.Trinkets.Periapt.FrozenPeriaptBuff;
import aphorea.buffs.Trinkets.Periapt.RockyPeriaptBuff;
import aphorea.buffs.Trinkets.Periapt.Summoner.InfectedPeriaptBuff;
import aphorea.buffs.Trinkets.Periapt.Summoner.NecromancyPeriaptBuff;
import aphorea.buffs.Trinkets.Periapt.Summoner.UnstablePeriaptBuff;
import aphorea.buffs.Trinkets.Ring.FloralRingBuff;
import aphorea.buffs.Trinkets.Shield.SpinelShieldBuff;
import aphorea.buffs.TrinketsActive.BloodyPeriaptActiveBuff;
import aphorea.buffs.TrinketsActive.DemonicPeriaptActiveBuff;
import aphorea.buffs.TrinketsActive.PeriaptActiveBuff;
import aphorea.buffs.TrinketsActive.RockyPeriaptActiveBuff;
import aphorea.buffs.TrinketsActive.SpinelShieldActiveBuff;
import aphorea.buffs.UnstableGelSlimeRushBuff;
import aphorea.buffs.VenomExtractBuff;
import aphorea.levelevents.runes.AphAbysmalRuneEvent;
import aphorea.levelevents.runes.AphRuneOfCryoQueenEvent;
import aphorea.levelevents.runes.AphRuneOfCrystalDragonEvent;
import aphorea.levelevents.runes.AphRuneOfDetonationEvent;
import aphorea.levelevents.runes.AphRuneOfMotherSlimeEvent;
import aphorea.levelevents.runes.AphRuneOfPestWardenEvent;
import aphorea.levelevents.runes.AphRuneOfQueenSpiderEvent;
import aphorea.levelevents.runes.AphRuneOfSunlightChampionEvent;
import aphorea.levelevents.runes.AphRuneOfThunderEvent;
import aphorea.levelevents.runes.AphTidalRuneEvent;
import aphorea.mobs.runicsummons.RunicAttackingFollowingMob;
import aphorea.mobs.runicsummons.RunicFlyingAttackingFollowingMob;
import aphorea.packets.AphRuneOfUnstableGelSlimePacket;
import aphorea.projectiles.rune.RuneOfSpiderEmpressProjectile;
import aphorea.registry.AphModifiers;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import aphorea.utils.magichealing.AphMagicHealing;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.HiddenCooldownBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.SimpleTrinketBuff;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.SmokePuffParticle;
import necesse.gfx.GameResources;
import necesse.gfx.gameFont.FontManager;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class AphBuffs {
    public static Buff ADRENALINE;
    public static Buff STOP;
    public static Buff STUN;
    public static Buff FALLEN_STUN;
    public static Buff STICKY;
    public static Buff HONEYED;
    public static Buff CURSED;
    public static Buff HARMONY;
    public static Buff DAGGER_ATTACK;
    public static Buff BERSERKER_RUSH;
    public static Buff PERIAPT_ACTIVE;
    public static Buff SABER_DASH_ACTIVE;
    public static Buff RUNE_INJECTOR_ACTIVE;
    public static Buff BERSERKER_RUSH_COOLDOWN;
    public static Buff SPIN_ATTACK_COOLDOWN;
    public static Buff PERIAPT_COOLDOWN;
    public static Buff SABER_DASH_COOLDOWN;
    public static Buff RUNE_INJECTOR_COOLDOWN;

    public static void registerCore() {
        STOP = new StopBuff();
        BuffRegistry.registerBuff((String)"stopbuff", (Buff)STOP);
        STUN = new StunBuff();
        BuffRegistry.registerBuff((String)"stunbuff", (Buff)STUN);
        FALLEN_STUN = new StunBuff(){

            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                GameRandom random = GameRandom.globalRandom;
                AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
                float distance = 75.0f;
                for (int i = 0; i < 4; ++i) {
                    owner.getLevel().entityManager.addParticle(owner.x + GameMath.sin((float)currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5), owner.y + GameMath.cos((float)currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5) * 0.85f, Particle.GType.CRITICAL).sprite(GameResources.puffParticles.sprite(random.getIntBetween(0, 4), 0, 12)).height(0.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                        float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 30.0f / 250.0f), Float::sum).floatValue();
                        float distY = (distance - 20.0f) * 0.85f;
                        pos.x = owner.x + GameMath.sin((float)angle) * (distance - distance / 2.0f * lifePercent);
                        pos.y = owner.y + GameMath.cos((float)angle) * distY - 20.0f * lifePercent;
                    }).color((options, lifeTime, timeAlive, lifePercent) -> {
                        options.color(AphColors.dark_magic);
                        if (lifePercent > 0.5f) {
                            options.alpha(2.0f * (1.0f - lifePercent));
                        }
                    }).size((options, lifeTime, timeAlive, lifePercent) -> options.size(22, 22)).lifeTime(1000);
                }
            }
        };
        BuffRegistry.registerBuff((String)"fallenstunbuff", (Buff)FALLEN_STUN);
        STICKY = new StickyBuff();
        BuffRegistry.registerBuff((String)"stickybuff", (Buff)STICKY);
        HONEYED = new HoneyedBuff();
        BuffRegistry.registerBuff((String)"honeyedbuff", (Buff)HONEYED);
        CURSED = new CursedBuff();
        BuffRegistry.registerBuff((String)"cursedbuff", (Buff)CURSED);
        ADRENALINE = new AdrenalineBuff();
        BuffRegistry.registerBuff((String)"adrenalinebuff", (Buff)ADRENALINE);
        HARMONY = new HarmonyBuff();
        BuffRegistry.registerBuff((String)"harmonybuff", (Buff)HARMONY);
        DAGGER_ATTACK = new DaggerAttackBuff();
        BuffRegistry.registerBuff((String)"daggerattackbuff", (Buff)DAGGER_ATTACK);
        BERSERKER_RUSH = new BerserkerRushActiveBuff();
        BuffRegistry.registerBuff((String)"berserkerrushactive", (Buff)BERSERKER_RUSH);
        PERIAPT_ACTIVE = new PeriaptActiveBuff();
        BuffRegistry.registerBuff((String)"periaptactivebuff", (Buff)PERIAPT_ACTIVE);
        SABER_DASH_ACTIVE = new HiddenCooldownBuff();
        BuffRegistry.registerBuff((String)"saberdashactivebuff", (Buff)SABER_DASH_ACTIVE);
        BuffRegistry.registerBuff((String)"narcissistbuff", (Buff)new NarcissistBuff());
        BERSERKER_RUSH_COOLDOWN = new AphShownCooldownBuff();
        BuffRegistry.registerBuff((String)"berserkerrushcooldown", (Buff)BERSERKER_RUSH_COOLDOWN);
        SPIN_ATTACK_COOLDOWN = new AphShownCooldownBuff();
        BuffRegistry.registerBuff((String)"spinattackcooldown", (Buff)SPIN_ATTACK_COOLDOWN);
        PERIAPT_COOLDOWN = new AphShownCooldownBuff();
        BuffRegistry.registerBuff((String)"periaptcooldown", (Buff)PERIAPT_COOLDOWN);
        SABER_DASH_COOLDOWN = new AphShownCooldownBuff();
        BuffRegistry.registerBuff((String)"saberdashcooldown", (Buff)SABER_DASH_COOLDOWN);
        SET_BONUS.GOLD_HAT = new GoldHatSetBonusBuff();
        BuffRegistry.registerBuff((String)"goldhatsetbonus", (Buff)SET_BONUS.GOLD_HAT);
        SET_BONUS.ROCKY = new RockySetBonusBuff();
        BuffRegistry.registerBuff((String)"rockysetbonus", (Buff)SET_BONUS.ROCKY);
        SET_BONUS.WITCH = new PinkWitchSetBonusBuff();
        BuffRegistry.registerBuff((String)"pinkwitchsetbonus", (Buff)SET_BONUS.WITCH);
        SET_BONUS.SWAMP_MASK = new SwampMaskSetBonusBuff();
        BuffRegistry.registerBuff((String)"swampmasksetbonus", (Buff)SET_BONUS.SWAMP_MASK);
        SET_BONUS.SWAMP_HOOP = new SwampHoodSetBonusBuff();
        BuffRegistry.registerBuff((String)"swamphoodsetbonus", (Buff)SET_BONUS.SWAMP_HOOP);
        SET_BONUS.INFECTED = new InfectedSetBonusBuff();
        BuffRegistry.registerBuff((String)"infectedsetbonus", (Buff)SET_BONUS.INFECTED);
        SET_BONUS.SPINEL_HELMET = new SpinelHelmetSetBonusBuff();
        BuffRegistry.registerBuff((String)"spinelhelmetsetbonus", (Buff)SET_BONUS.SPINEL_HELMET);
        SET_BONUS.SPINEL_HAT = new SpinelHatSetBonusBuff();
        BuffRegistry.registerBuff((String)"spinelhatsetbonus", (Buff)SET_BONUS.SPINEL_HAT);
        BANNER.BLANK = AphBasicBannerBuff.floatModifier((Modifier<Float>)BuffModifiers.HEALTH_REGEN, 0.1f);
        BuffRegistry.registerBuff((String)"blankbanner", (Buff)BANNER.BLANK);
        BANNER.STRIKE = new AphBasicBannerBuff(AphBasicBannerBuff.AphBasicBannerBuffModifier.floatModifier((Modifier<Float>)BuffModifiers.HEALTH_REGEN, 0.1f), AphBasicBannerBuff.AphBasicBannerBuffModifier.floatModifier((Modifier<Float>)BuffModifiers.COMBAT_HEALTH_REGEN, 0.1f));
        BuffRegistry.registerBuff((String)"strikebanner", (Buff)BANNER.STRIKE);
        BANNER.DAMAGE = AphBasicBannerBuff.floatModifier((Modifier<Float>)BuffModifiers.ALL_DAMAGE, 0.15f);
        BuffRegistry.registerBuff((String)"aph_bannerofdamage", (Buff)BANNER.DAMAGE);
        BANNER.DEFENSE = AphBasicBannerBuff.floatModifier((value, effect) -> Float.valueOf(Math.max(0.5f, 1.0f - effect.floatValue() * value.floatValue())), 1.0f, (Modifier<Float>)BuffModifiers.INCOMING_DAMAGE_MOD, 0.1f);
        BuffRegistry.registerBuff((String)"aph_bannerofdefense", (Buff)BANNER.DEFENSE);
        BANNER.SPEED = AphBasicBannerBuff.floatModifier((Modifier<Float>)BuffModifiers.SPEED, 0.3f);
        BuffRegistry.registerBuff((String)"aph_bannerofspeed", (Buff)BANNER.SPEED);
        BANNER.SUMMON_SPEED = AphBasicBannerBuff.floatModifier((Modifier<Float>)BuffModifiers.SUMMONS_SPEED, 0.75f);
        BuffRegistry.registerBuff((String)"aph_bannerofsummonspeed", (Buff)BANNER.SUMMON_SPEED);
        POTION.VENOM_EXTRACT = new VenomExtractBuff();
        BuffRegistry.registerBuff((String)"venomextractbuff", (Buff)POTION.VENOM_EXTRACT);
        AphBuffs.trinketBuffs();
        BuffRegistry.registerBuff((String)"rockyperiaptactive", (Buff)new RockyPeriaptActiveBuff());
        BuffRegistry.registerBuff((String)"bloodyperiaptactive", (Buff)new BloodyPeriaptActiveBuff());
        BuffRegistry.registerBuff((String)"demonicperiaptactive", (Buff)new DemonicPeriaptActiveBuff());
        BuffRegistry.registerBuff((String)"unstablegelslimerushbuff", (Buff)new UnstableGelSlimeRushBuff());
        RUNE_INJECTOR_ACTIVE = new AphShownBuff();
        BuffRegistry.registerBuff((String)"runesinjectoractive", (Buff)RUNE_INJECTOR_ACTIVE);
        RUNE_INJECTOR_COOLDOWN = new AphShownCooldownBuff();
        BuffRegistry.registerBuff((String)"runesinjectorcooldown", (Buff)RUNE_INJECTOR_COOLDOWN);
        AphBuffs.runesInjectors();
        AphBuffs.tier0BaseRunes();
        AphBuffs.tier1BaseRunes();
        AphBuffs.tier2BaseRunes();
        AphBuffs.tier3BaseRunes();
        AphBuffs.tier0ModifierRunes();
        AphBuffs.tier1ModifierRunes();
        AphBuffs.tier2ModifierRunes();
        AphBuffs.tier3ModifierRunes();
        AphBuffs.registerMightyBannerBuffs();
        AphBuffs.registerSummonerExpansionBuffs();
    }

    public static void trinketBuffs() {
        BuffRegistry.registerBuff((String)"inspirationfoci", (Buff)new InspirationFociBuff());
        BuffRegistry.registerBuff((String)"iceboots", (Buff)new SimpleTrinketBuff("iceboots", new ModifierValue[]{new ModifierValue(BuffModifiers.FRICTION, (Object)Float.valueOf(-0.75f)).max((Object)Float.valueOf(0.5f)), new ModifierValue(BuffModifiers.SPEED, (Object)Float.valueOf(0.5f)), new ModifierValue(BuffModifiers.ARMOR_FLAT, (Object)4)}));
        BuffRegistry.registerBuff((String)"essenceofhealing", (Buff)new EssenceofHealingBuff());
        BuffRegistry.registerBuff((String)"floralring", (Buff)new FloralRingBuff());
        BuffRegistry.registerBuff((String)"gelring", (Buff)new SimpleTrinketBuff("gelring", new ModifierValue[]{new ModifierValue(AphModifiers.MAGIC_HEALING_RECEIVED, (Object)Float.valueOf(0.3f))}));
        BuffRegistry.registerBuff((String)"heartring", (Buff)new SimpleTrinketBuff("heartring", new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_HEALTH_FLAT, (Object)20)}));
        BuffRegistry.registerBuff((String)"rockyperiapt", (Buff)new RockyPeriaptBuff());
        BuffRegistry.registerBuff((String)"frozenperiapt", (Buff)new FrozenPeriaptBuff());
        BuffRegistry.registerBuff((String)"bloodyperiapt", (Buff)new BloodyPeriaptBuff());
        BuffRegistry.registerBuff((String)"demonicperiapt", (Buff)new DemonicPeriaptBuff());
        BuffRegistry.registerBuff((String)"abysmalperiapt", (Buff)new AbysmalPeriaptBuff());
        BuffRegistry.registerBuff((String)"unstableperiapt", (Buff)new UnstablePeriaptBuff());
        BuffRegistry.registerBuff((String)"necromancyperiapt", (Buff)new NecromancyPeriaptBuff());
        BuffRegistry.registerBuff((String)"infectedperiapt", (Buff)new InfectedPeriaptBuff());
        BuffRegistry.registerBuff((String)"witchmedallion", (Buff)new WitchMedallionBuff());
        BuffRegistry.registerBuff((String)"cursedmedallion", (Buff)new CursedMedallionBuff());
        BuffRegistry.registerBuff((String)"ancientmedallion", (Buff)new AncientMedallionBuff());
        BuffRegistry.registerBuff((String)"spinelshield", (Buff)new SpinelShieldBuff());
        BuffRegistry.registerBuff((String)"spinelshieldactive", (Buff)new SpinelShieldActiveBuff());
        BuffRegistry.registerBuff((String)"adrenalinecharm", (Buff)new AdrenalineCharmBuff());
        BuffRegistry.registerBuff((String)"bloomrushcharm", (Buff)new BloomrushCharmBuff());
        BuffRegistry.registerBuff((String)"ninjascarf", (Buff)new SimpleTrinketBuff("ninjascarf", new ModifierValue[0]));
    }

    public static void runesInjectors() {
        BuffRegistry.registerBuff((String)"rusticrunesinjector", (Buff)new AphModifierRuneTrinketBuff().setEffectNumberVariation(-0.2f));
        BuffRegistry.registerBuff((String)"unstablerunesinjector", (Buff)new AphModifierRuneTrinketBuff().setHealthCost(0.1f).setEffectNumberVariation(0.2f));
        BuffRegistry.registerBuff((String)"demonicrunesinjector", (Buff)new AphModifierRuneTrinketBuff());
        BuffRegistry.registerBuff((String)"tungstenrunesinjector", (Buff)new AphModifierRuneTrinketBuff());
        BuffRegistry.registerBuff((String)"ancientrunesinjector", (Buff)new AphModifierRuneTrinketBuff());
    }

    public static void tier0BaseRunes() {
        float baseEffectNumber = 40.0f;
        BuffRegistry.registerBuff((String)"runeoffury", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 10000, "runeoffuryactive").setHealthCost(0.2f));
        BuffRegistry.registerBuff((String)"runeoffuryactive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 20000, new ModifierValue[0]){

            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                buff.addModifier(BuffModifiers.ALL_DAMAGE, (Object)Float.valueOf(effectNumber / 100.0f));
                buff.addModifier(BuffModifiers.ATTACK_SPEED, (Object)Float.valueOf(effectNumber / 100.0f));
            }

            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(AphColors.red).height(16.0f);
                }
            }
        });
        baseEffectNumber = 200.0f;
        float extraEffectNumberMod = 1.5f;
        BuffRegistry.registerBuff((String)"runeofspeed", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, extraEffectNumberMod, 5000){

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                float strength = this.getEffectNumber(player);
                SoundManager.playSound((GameSound)GameResources.swoosh, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)player).volume(0.5f).pitch(1.7f));
                Point2D.Float dir = PacketForceOfWind.getMobDir((Mob)player);
                PacketForceOfWind.applyToMob((Level)level, (Mob)player, (float)dir.x, (float)dir.y, (float)strength);
                player.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, (Mob)player, 0.15f, null), level.isServer());
                player.buffManager.forceUpdateBuffs();
                player.buffManager.addBuff(new ActiveBuff(BuffRegistry.INVULNERABLE_ACTIVE, (Mob)player, 500, null), false);
                if (level.isServer()) {
                    ServerClient serverClient = player.getServerClient();
                    player.getServer().network.sendToClientsWithEntityExcept((Packet)new PacketForceOfWind((Mob)player, player.moveX, player.moveY, strength), (RegionPositionGetter)player, serverClient);
                }
            }
        });
        baseEffectNumber = 25.0f;
        BuffRegistry.registerBuff((String)"runeofhealing", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 30000){

            @Override
            public float getBaseHealing() {
                return this.getBaseEffectNumber() / 100.0f;
            }

            @Override
            public float getHealing(float effectNumberVariation) {
                return this.getBaseHealing() * effectNumberVariation;
            }
        });
        baseEffectNumber = 100.0f;
        BuffRegistry.registerBuff((String)"runeofresistance", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 10000, "runeofresistanceactive"));
        BuffRegistry.registerBuff((String)"runeofresistanceactive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 10000, "runeofresistancecooldown", new ModifierValue[0]){

            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                buff.addModifier(BuffModifiers.ARMOR, (Object)Float.valueOf(effectNumber / 100.0f));
            }

            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(AphColors.iron).height(16.0f);
                }
            }
        });
        BuffRegistry.registerBuff((String)"runeofresistancecooldown", (Buff)new HiddenCooldownBuff(){

            public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
                super.init(buff, eventSubscriber);
                buff.addModifier(BuffModifiers.ARMOR, (Object)Float.valueOf(-0.5f));
            }
        });
        baseEffectNumber = 10.0f;
        BuffRegistry.registerBuff((String)"runeofvalor", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, (int)(baseEffectNumber * 1000.0f), "runeofvaloractive"){

            @Override
            public int getDuration(PlayerMob player) {
                return (int)(this.getEffectNumber(player) * 1000.0f);
            }
        });
        BuffRegistry.registerBuff((String)"runeofvaloractive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 10000, new ModifierValue[]{new ModifierValue(AphModifiers.INSPIRATION_EFFECT, (Object)Float.valueOf(1.0f)), new ModifierValue(AphModifiers.INSPIRATION_ABILITY_SPEED, (Object)Float.valueOf(1.0f))}){

            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(AphColors.gold).height(16.0f);
                }
            }
        });
        baseEffectNumber = 60.0f;
        BuffRegistry.registerBuff((String)"runeofdetonation", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 8000){

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                player.getLevel().entityManager.events.add((LevelEvent)new AphRuneOfDetonationEvent(player, player.x, player.y, this.getEffectNumber(player) / 100.0f));
            }
        }.setHealthCost(0.05f));
        baseEffectNumber = 60.0f;
        BuffRegistry.registerBuff((String)"runeofthunder", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 10000){

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                float range = 600.0f;
                if (player.getDistance((float)targetX, (float)targetY) > range) {
                    this.preventUsage = true;
                    if (level.isClient()) {
                        new AphAreaList(new AphArea(range, level.isCave ? AphColors.dark_magic : AphColors.lighting)).setOnlyVision(false).executeClient(level, player.x, player.y, 1.0f, 1.0f, 0.0f);
                    }
                } else if (level.isServer()) {
                    player.getLevel().entityManager.events.add((LevelEvent)new AphRuneOfThunderEvent((Mob)player, targetX, targetY, this.getEffectNumber(player) / 100.0f));
                }
            }
        });
        baseEffectNumber = 10.0f;
        BuffRegistry.registerBuff((String)"runeofwinter", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, (int)(baseEffectNumber * 1000.0f), "runeofwinteractive"){

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                AphAreaList areaList = new AphAreaList(new AphArea(500.0f, AphColors.ice).setDebuffArea((int)(this.getEffectNumber(player) * 1000.0f), BuffRegistry.Debuffs.FROSTSLOW.getStringID()));
                areaList.execute((Mob)player, false);
            }

            @Override
            public int getDuration(PlayerMob player) {
                return (int)(this.getEffectNumber(player) * 1000.0f);
            }
        });
        BuffRegistry.registerBuff((String)"runeofwinteractive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 20000, new ModifierValue[0]));
        baseEffectNumber = 5.0f;
        extraEffectNumberMod = 2.0f;
        BuffRegistry.registerBuff((String)"runeofimmortality", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, extraEffectNumberMod, (int)(baseEffectNumber * 1000.0f), "runeofimmortalityactive"){

            @Override
            public int getDuration(PlayerMob player) {
                return (int)(this.getEffectNumber(player) * 1000.0f);
            }
        });
        BuffRegistry.registerBuff((String)"runeofimmortalityactive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, extraEffectNumberMod, 5000, "runeofimmortalitycooldown", new ModifierValue[0]){

            public void onBeforeHitCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event) {
                super.onBeforeHitCalculated(buff, event);
                if (buff.owner.isServer() && !event.isPrevented() && event.damage >= buff.owner.getHealth()) {
                    event.prevent();
                    buff.owner.setHealth(buff.owner.getMaxHealth());
                    if (this.cooldownBuff != null) {
                        buff.owner.buffManager.addBuff(new ActiveBuff(this.cooldownBuff, buff.owner, 30000, null), true);
                    }
                    buff.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.INVULNERABLE_ACTIVE, buff.owner, 3000, null), true);
                    buff.owner.buffManager.addBuff(new ActiveBuff(RUNE_INJECTOR_COOLDOWN, buff.owner, 30000, null), true);
                }
            }
        });
        BuffRegistry.registerBuff((String)"runeofimmortalitycooldown", (Buff)new HiddenCooldownBuff(){

            public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
                super.init(buff, eventSubscriber);
                buff.addModifier(BuffModifiers.MAX_HEALTH, (Object)Float.valueOf(-0.5f));
            }
        });
        baseEffectNumber = 5.0f;
        BuffRegistry.registerBuff((String)"runeofshadows", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, (int)(baseEffectNumber * 1000.0f), "runeofshadowsactive"){

            @Override
            public int getDuration(PlayerMob player) {
                return (int)(this.getEffectNumber(player) * 1000.0f);
            }
        });
        BuffRegistry.registerBuff((String)"runeofshadowsactive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 10000, new ModifierValue(BuffModifiers.INVISIBILITY, (Object)true), new ModifierValue(BuffModifiers.TARGET_RANGE, (Object)Float.valueOf(-0.7f)), new ModifierValue(BuffModifiers.INTIMIDATED, (Object)true)));
    }

    public static void tier1BaseRunes() {
        float baseEffectNumber = 5.0f;
        BuffRegistry.registerBuff((String)"runeofunstablegelslime", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 20000){

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                if (player.isServerClient()) {
                    ServerClient serverClient = player.getServerClient();
                    for (int i = 0; i < 2; ++i) {
                        RunicAttackingFollowingMob mob = (RunicAttackingFollowingMob)MobRegistry.getMob((String)"runicunstablegelslime", (Level)player.getLevel());
                        player.serverFollowersManager.addFollower("runicunstablegelslimes", (Mob)mob, FollowPosition.WALK_CLOSE, "runeofunstablegelslime", 1.0f, 2, (MserverClient, Mmob) -> ((RunicAttackingFollowingMob)((Object)Mmob)).updateEffectNumber(this.getEffectNumber(player)), true);
                        mob.getLevel().entityManager.addMob((Mob)mob, player.x, player.y);
                    }
                    int tileX = player.getX() / 32;
                    int tileY = player.getY() / 32;
                    Point moveOffset = player.getPathMoveOffset();
                    ArrayList<Point> possiblePoints = new ArrayList<Point>();
                    int maxRange = 5;
                    int minRange = 3;
                    for (int x = tileX - maxRange; x <= tileX + maxRange; ++x) {
                        if (Math.abs(x - tileX) < minRange) continue;
                        for (int y = tileY - maxRange; y <= tileY + maxRange; ++y) {
                            if (Math.abs(y - tileY) < minRange) continue;
                            int mobX = x * 32 + moveOffset.x;
                            int mobY = y * 32 + moveOffset.y;
                            if (player.collidesWith(player.getLevel(), mobX, mobY)) continue;
                            possiblePoints.add(new Point(mobX, mobY));
                        }
                    }
                    if (!possiblePoints.isEmpty()) {
                        Point point = (Point)possiblePoints.get(GameRandom.globalRandom.nextInt(possiblePoints.size()));
                        server.network.sendToClientsAtEntireLevel((Packet)new AphRuneOfUnstableGelSlimePacket(serverClient.slot, point.x, point.y), serverClient.getLevel());
                    }
                }
            }
        });
        baseEffectNumber = 50.0f;
        BuffRegistry.registerBuff((String)"runeofevilsprotector", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 10000, "runeofevilsprotectoractive"));
        BuffRegistry.registerBuff((String)"runeofevilsprotectoractive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 20000, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, (Object)Float.valueOf(-0.5f))}){

            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                Level level = buff.owner.getLevel();
                if (level != null) {
                    boolean night = !level.isCave && level.getWorldEntity().isNight();
                    buff.addModifier(BuffModifiers.ATTACK_SPEED, (Object)Float.valueOf(effectNumber / 100.0f * (float)(night ? 2 : 1)));
                }
            }

            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(AphColors.demonic).height(16.0f);
                }
            }
        });
        baseEffectNumber = 14.0f;
        BuffRegistry.registerBuff((String)"runeofqueenspider", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 6000){

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                player.getLevel().entityManager.events.add((LevelEvent)new AphRuneOfQueenSpiderEvent((Mob)player, (int)player.x, (int)player.y, (int)(this.getEffectNumber(player) * 1000.0f), GameRandom.globalRandom));
            }
        });
        baseEffectNumber = 50.0f;
        float extraEffectNumberMod = 2.0f;
        BuffRegistry.registerBuff((String)"runeofvoidwizard", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, extraEffectNumberMod, 10000, "runeofvoidwizardactive"));
        BuffRegistry.registerBuff((String)"runeofvoidwizardactive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, extraEffectNumberMod, 20000, new ModifierValue[]{new ModifierValue(BuffModifiers.PROJECTILE_BOUNCES, (Object)Integer.MAX_VALUE)}){

            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                buff.addModifier(BuffModifiers.PROJECTILE_VELOCITY, (Object)Float.valueOf(effectNumber / 100.0f));
            }

            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(AphColors.dark_magic).height(16.0f);
                }
            }
        });
        baseEffectNumber = 20.0f;
        BuffRegistry.registerBuff((String)"runeofchieftain", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, extraEffectNumberMod, 20000, "runeofchieftainactive").setHealthCost(0.05f));
        BuffRegistry.registerBuff((String)"runeofchieftainactive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, extraEffectNumberMod, 20000, new ModifierValue[0]){
            int bound;

            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                this.updateBuff(buff);
            }

            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                this.updateBuff(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && this.bound == 0 || GameRandom.globalRandom.nextInt(this.bound) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(AphColors.red).height(16.0f);
                }
            }

            public void serverTick(ActiveBuff buff) {
                super.serverTick(buff);
                this.updateBuff(buff);
            }

            public void updateBuff(ActiveBuff buff) {
                PlayerMob player = (PlayerMob)buff.owner;
                float healthLostPercent = 1.0f - player.getHealthPercent();
                this.bound = (int)Math.max(healthLostPercent * 4.0f, 0.0f);
                buff.setModifier(BuffModifiers.ALL_DAMAGE, (Object)Float.valueOf(healthLostPercent * this.getEffectNumber(player) / 100.0f));
            }
        });
        baseEffectNumber = 3.0f;
        BuffRegistry.registerBuff((String)"runeofswampguardian", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, (int)(baseEffectNumber * 1000.0f), "runeofswampguardianactive"){

            @Override
            public int getDuration(PlayerMob player) {
                return (int)(this.getEffectNumber(player) * 1000.0f);
            }
        });
        BuffRegistry.registerBuff((String)"runeofswampguardianactive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 25000, new ModifierValue[]{new ModifierValue(BuffModifiers.INCOMING_DAMAGE_MOD, (Object)Float.valueOf(0.8f))}){

            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(AphColors.green).height(16.0f);
                }
            }
        });
        baseEffectNumber = 10.0f;
        (BuffRegistry.registerBuff((String)"runeofancientvulture", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 10000){

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                if (player.isServerClient()) {
                    RunicFlyingAttackingFollowingMob mob = (RunicFlyingAttackingFollowingMob)MobRegistry.getMob((String)"runicvulturehatchling", (Level)player.getLevel());
                    player.serverFollowersManager.addFollower("runicvulturehatchlings", (Mob)mob, FollowPosition.WALK_CLOSE, "runeofancientvulture", 1.0f, 1, (MserverClient, Mmob) -> ((RunicFlyingAttackingFollowingMob)((Object)Mmob)).updateEffectNumber(this.getEffectNumber(player)), true);
                    mob.getLevel().entityManager.addMob((Mob)mob, player.x, player.y);
                }
            }
        })).setHealthCost(0.05f);
        baseEffectNumber = 1.0f;
        BuffRegistry.registerBuff((String)"runeofpiratecaptain", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 8000, "runeofpiratecaptainactive"));
        BuffRegistry.registerBuff((String)"runeofpiratecaptainactive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 16000, new ModifierValue[0]){
            int bound;

            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                this.updateCoins(buff);
            }

            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && this.bound == 0 || GameRandom.globalRandom.nextInt(this.bound) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(AphColors.gold).height(16.0f);
                }
            }

            public void updateCoins(ActiveBuff buff) {
                PlayerMob player = (PlayerMob)buff.owner;
                int coins = Math.min(player.getInv().getAmount(ItemRegistry.getItem((String)"coin"), false, false, false, false, "buy"), 20000) / 200;
                this.bound = Math.max(4 - coins / 50, 0);
                buff.setModifier(BuffModifiers.ALL_DAMAGE, (Object)Float.valueOf((float)coins * this.getEffectNumber(player) / 100.0f));
            }

            public void onItemAttacked(ActiveBuff buff, int targetX, int targetY, ItemAttackerMob mob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, GNDItemMap attackMap) {
                super.onItemAttacked(buff, targetX, targetY, mob, attackHeight, item, slot, animAttack, attackMap);
                if (mob.isServer() && mob.isPlayer) {
                    ((PlayerMob)mob).getInv().removeItems(ItemRegistry.getItem((String)"coin"), 3, false, false, false, false, "buy");
                }
                this.updateCoins(buff);
            }
        });
    }

    public static void tier2BaseRunes() {
        float baseEffectNumber = 200.0f;
        (BuffRegistry.registerBuff((String)"runeofreaper", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 10000){

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                float range = this.getEffectNumber(player);
                if (player.getDistance((float)targetX, (float)targetY) > range) {
                    this.preventUsage = true;
                    if (level.isClient()) {
                        new AphAreaList(new AphArea(range, AphColors.tungsten)).setOnlyVision(false).executeClient(level, player.x, player.y, 1.0f, 1.0f, 0.0f);
                    }
                } else if (level.getObject((int)(targetX / 32), (int)(targetY / 32)).isSolid) {
                    this.preventUsage = true;
                } else {
                    if (level.isClient()) {
                        player.getLevel().entityManager.addParticle((ParticleOption)new SmokePuffParticle(player.getLevel(), player.x, player.y, AphColors.tungsten), Particle.GType.CRITICAL);
                        player.getLevel().entityManager.addParticle((ParticleOption)new SmokePuffParticle(player.getLevel(), (float)targetX, (float)targetY, AphColors.tungsten), Particle.GType.CRITICAL);
                    }
                    player.setPos((float)targetX, (float)targetY, true);
                }
                super.run(level, player, targetX, targetY);
            }
        })).setHealthCost(0.05f);
        baseEffectNumber = 100.0f;
        BuffRegistry.registerBuff((String)"runeofbabylontower", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 10000, "runeofbabylontoweractive"));
        BuffRegistry.registerBuff((String)"runeofbabylontoweractive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 20000, new ModifierValue[]{new ModifierValue(BuffModifiers.SPEED, (Object)Float.valueOf(-1.0f)).max((Object)Float.valueOf(-1.0f))}){

            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                Level level = buff.owner.getLevel();
                if (level != null) {
                    buff.addModifier(BuffModifiers.ALL_DAMAGE, (Object)Float.valueOf(effectNumber / 100.0f));
                }
            }

            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(GameRandom.globalRandom.getFloatBetween(-2.0f, 2.0f), GameRandom.globalRandom.getFloatBetween(-2.0f, 2.0f)).color(AphColors.spinel).heightMoves(GameRandom.globalRandom.getFloatBetween(0.0f, 16.0f), GameRandom.globalRandom.getFloatBetween(16.0f, 32.0f));
                }
            }
        });
        baseEffectNumber = 300.0f;
        BuffRegistry.registerBuff((String)"runeofcryoqueen", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 30000){

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                player.getLevel().entityManager.events.add((LevelEvent)new AphRuneOfCryoQueenEvent((Mob)player, (int)player.x, (int)player.y, GameRandom.globalRandom.getFloatBetween(0.0f, 360.0f), GameRandom.globalRandom.nextBoolean(), this.getEffectNumber(player)));
                player.buffManager.addBuff(new ActiveBuff(STOP, (Mob)player, 1000, null), true);
            }
        });
        baseEffectNumber = 10.0f;
        BuffRegistry.registerBuff((String)"runeofpestwarden", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, (int)(baseEffectNumber * 1000.0f), "runeofpestwardenactive"){

            @Override
            public int getDuration(PlayerMob player) {
                return (int)(this.getEffectNumber(player) * 1000.0f);
            }

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                player.getLevel().entityManager.events.add((LevelEvent)new AphRuneOfPestWardenEvent((Mob)player));
            }
        });
        BuffRegistry.registerBuff((String)"runeofpestwardenactive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 22000, new ModifierValue[0]){

            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && GameRandom.globalRandom.nextInt(2) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(AphColors.green).height(16.0f);
                }
            }
        });
        baseEffectNumber = 20.0f;
        BuffRegistry.registerBuff((String)"runeofsageandgrit", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 20000, "runeofsageandgritactive"){
            public final AphAreaList areaList = new AphAreaList(new AphArea(500.0f, AphColors.green));

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                List arrayList;
                super.run(level, player, targetX, targetY);
                if (level.isServer() && !(arrayList = (List)player.getLevel().entityManager.streamAreaMobsAndPlayers(player.x, player.y, 500).filter(target -> target != player && AphMagicHealing.canHealMob((Mob)player, target)).collect(Collectors.toList())).isEmpty()) {
                    this.preventBuff = true;
                    this.temporalBuff = false;
                    arrayList.forEach(target -> target.getLevel().entityManager.events.add((LevelEvent)new MobHealthChangeEvent(target, (int)((float)target.getMaxHealth() * 0.2f))));
                    this.areaList.sendExecutePacket(level, player.x, player.y);
                }
            }
        });
        BuffRegistry.registerBuff((String)"runeofsageandgritactive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 20000, new ModifierValue[0]){

            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                buff.addModifier(BuffModifiers.ALL_DAMAGE, (Object)Float.valueOf(effectNumber / 100.0f));
                buff.addModifier(BuffModifiers.ATTACK_SPEED, (Object)Float.valueOf(effectNumber / 100.0f));
                buff.addModifier(BuffModifiers.SPEED, (Object)Float.valueOf(effectNumber / 100.0f));
            }
        });
        baseEffectNumber = 3.0f;
        BuffRegistry.registerBuff((String)"runeoffallenwizard", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, (int)baseEffectNumber * 1000, "runeoffallenwizardactive"){

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                player.getLevel().entityManager.streamAreaMobsAndPlayers(player.x, player.y, 32768).filter(target -> target != player).forEach(mob -> {
                    float timeMod = 1.0f;
                    if (mob.isBoss()) {
                        timeMod = 0.25f;
                    } else if (mob.isPlayer || mob.isHuman) {
                        timeMod = 0.5f;
                    }
                    mob.addBuff(new ActiveBuff(FALLEN_STUN, (Mob)player, this.getEffectNumber(player) * timeMod, (Attacker)player), true);
                });
            }

            @Override
            public int getDuration(PlayerMob player) {
                return (int)(this.getEffectNumber(player) * 1000.0f);
            }
        });
        BuffRegistry.registerBuff((String)"runeoffallenwizardactive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 18000, new ModifierValue[0]));
    }

    public static void tier3BaseRunes() {
        float baseEffectNumber = 60.0f;
        BuffRegistry.registerBuff((String)"runeofmotherslime", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 24000){

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                float range = 600.0f;
                if (player.getDistance((float)targetX, (float)targetY) > range) {
                    this.preventUsage = true;
                    if (level.isClient()) {
                        new AphAreaList(new AphArea(range, AphColors.paletteMotherSlime)).setOnlyVision(false).executeClient(level, player.x, player.y, 1.0f, 1.0f, 0.0f);
                    }
                } else if (level.getObject((int)(targetX / 32), (int)(targetY / 32)).isSolid) {
                    this.preventUsage = true;
                } else {
                    player.dismount();
                    if (level.isServer()) {
                        player.getLevel().entityManager.events.add((LevelEvent)new AphRuneOfMotherSlimeEvent((Mob)player, targetX, targetY, this.getEffectNumber(player)));
                        player.buffManager.addBuff(new ActiveBuff(STOP, (Mob)player, 1000, null), true);
                    }
                }
            }
        });
        baseEffectNumber = 5.0f;
        BuffRegistry.registerBuff((String)"runeofnightswarm", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, (int)baseEffectNumber * 1000, "runeofnightswarmactive"){

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                if (player.isServerClient()) {
                    for (int i = 0; i < 5; ++i) {
                        RunicFlyingAttackingFollowingMob mob = (RunicFlyingAttackingFollowingMob)MobRegistry.getMob((String)"runicbat", (Level)player.getLevel());
                        player.serverFollowersManager.addFollower("runicbats", (Mob)mob, FollowPosition.WALK_CLOSE, "runeofnightswarm", 1.0f, 8, (MserverClient, Mmob) -> ((RunicFlyingAttackingFollowingMob)((Object)Mmob)).updateEffectNumber(this.getEffectNumber(player)), true);
                        mob.getLevel().entityManager.addMob((Mob)mob, player.x, player.y);
                    }
                }
            }

            @Override
            public int getDuration(PlayerMob player) {
                return (int)(this.getEffectNumber(player) * 1000.0f);
            }
        });
        BuffRegistry.registerBuff((String)"runeofnightswarmactive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 20000, new ModifierValue[0]));
        baseEffectNumber = 7.0f;
        float extraEffectNumberMod = 2.0f;
        BuffRegistry.registerBuff((String)"runeofspiderempress", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, extraEffectNumberMod, 10000){

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                float initialAngle = (float)Math.toDegrees(Math.atan2((float)targetY - player.y, (float)targetX - player.x)) + 45.0f;
                int iterations = Math.max(Math.round(this.getEffectNumber(player)), 2);
                float anglePerProjectile = 90 / (iterations - 1);
                for (int i = 0; i < iterations; ++i) {
                    float currentAngle = initialAngle + anglePerProjectile * (float)i;
                    player.getLevel().entityManager.projectiles.add((Entity)new RuneOfSpiderEmpressProjectile(player.x, player.y, currentAngle, new GameDamage(0.0f), 120.0f, (Mob)player));
                }
            }
        });
        baseEffectNumber = 600.0f;
        BuffRegistry.registerBuff((String)"runeofsunlightchampion", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 40000){

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                player.getLevel().entityManager.events.add((LevelEvent)new AphRuneOfSunlightChampionEvent((int)this.getEffectNumber(player), (Mob)player));
                player.buffManager.addBuff(new ActiveBuff(STUN, (Mob)player, 3000, null), true);
            }
        });
        baseEffectNumber = 0.5f;
        BuffRegistry.registerBuff((String)"runeofmoonlightdancer", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 10000, "runeofmoonlightdanceractive"));
        BuffRegistry.registerBuff((String)"runeofmoonlightdanceractive", (Buff)new AphBaseRuneActiveBuff(baseEffectNumber, 20000, new ModifierValue[0]){
            int bound;

            @Override
            public void initExtraModifiers(ActiveBuff buff, float effectNumber) {
                super.initExtraModifiers(buff, effectNumber);
                this.updateBuff(buff);
            }

            public void clientTick(ActiveBuff buff) {
                super.clientTick(buff);
                this.updateBuff(buff);
                Mob owner = buff.owner;
                if (owner.isVisible() && this.bound == 0 || GameRandom.globalRandom.nextInt(this.bound) == 0) {
                    owner.getLevel().entityManager.addParticle(owner.x + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)GameRandom.globalRandom.nextInt(5) + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).color(AphColors.black).height(16.0f);
                }
            }

            public void serverTick(ActiveBuff buff) {
                super.serverTick(buff);
                this.updateBuff(buff);
            }

            public void updateBuff(ActiveBuff buff) {
                PlayerMob player = (PlayerMob)buff.owner;
                float speedModifier = player.getSpeedModifier() - 1.0f;
                if (speedModifier <= 0.0f) {
                    this.bound = 0;
                } else {
                    this.bound = (int)Math.max(4.0f - speedModifier / 25.0f, 0.0f);
                    buff.setModifier(BuffModifiers.ALL_DAMAGE, (Object)Float.valueOf(speedModifier * this.getEffectNumber(player)));
                }
            }
        });
        baseEffectNumber = 20.0f;
        BuffRegistry.registerBuff((String)"runeofcrystaldragon", (Buff)new AphBaseRuneTrinketBuff(baseEffectNumber, 25000){

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                float angle = (float)Math.toDegrees(Math.atan2((float)targetY - player.y, (float)targetX - player.x));
                player.getLevel().entityManager.events.add((LevelEvent)new AphRuneOfCrystalDragonEvent((Mob)player, new GameRandom(), 1000.0f, this.getEffectNumber(player) / 100.0f, 100, 5000, angle));
            }
        });
    }

    public static void tier0ModifierRunes() {
        BuffRegistry.registerBuff((String)"empoweringrune", (Buff)new AphModifierRuneTrinketBuff().setEffectNumberVariation(0.2f).setCooldownVariation(0.2f));
        BuffRegistry.registerBuff((String)"recurrentrune", (Buff)new AphModifierRuneTrinketBuff().setEffectNumberVariation(-0.2f).setCooldownVariation(-0.2f));
        BuffRegistry.registerBuff((String)"devastatingrune", (Buff)new AphModifierRuneTrinketBuff().setEffectNumberVariation(0.3f).setHealthCost(0.1f));
    }

    public static void tier1ModifierRunes() {
        BuffRegistry.registerBuff((String)"frenzyrune", (Buff)new AphModifierRuneTrinketBuff().setCooldownVariation(-0.4f).setHealthCost(0.1f));
        BuffRegistry.registerBuff((String)"vitalrune", (Buff)new AphModifierRuneTrinketBuff().setEffectNumberVariation(-0.2f).setCooldownVariation(0.1f).setHealthCost(-0.05f));
        BuffRegistry.registerBuff((String)"onyxrune", (Buff)new AphModifierRuneTrinketBuff(){

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                if (player.isServerClient()) {
                    Mob mob = MobRegistry.getMob((String)"onyx", (Level)player.getLevel());
                    player.serverFollowersManager.addFollower("onyx", mob, FollowPosition.WALK_CLOSE, "onyxrune", 1.0f, 1, null, true);
                    mob.getLevel().entityManager.addMob(mob, player.x, player.y);
                }
            }
        }.setEffectNumberVariation(-0.2f).setHealthCost(0.05f));
        BuffRegistry.registerBuff((String)"pawningrune", (Buff)new AphModifierRuneTrinketBuff(){

            @Override
            public void run(Level level, PlayerMob player, int targetX, int targetY) {
                super.run(level, player, targetX, targetY);
                if (!player.buffManager.hasBuff("pawningruneactive")) {
                    player.buffManager.addBuff(new ActiveBuff("pawningruneactive", (Mob)player, 10000, null), false);
                    SoundManager.playSound((GameSound)GameResources.magicbolt1, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)player).volume(1.0f).pitch(1.0f));
                }
            }
        });
        BuffRegistry.registerBuff((String)"pawningruneactive", (Buff)new AphShownBuff(){
            int pawnHealing;

            public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
                if (activeBuff.owner.isServer()) {
                    int healing = Math.min((int)((float)activeBuff.owner.getMaxHealth() * 0.2f), activeBuff.owner.getMaxHealth() - activeBuff.owner.getHealth());
                    MobHealthChangeEvent changeHeal = new MobHealthChangeEvent(activeBuff.owner, healing);
                    activeBuff.owner.getLevel().entityManager.events.add((LevelEvent)changeHeal);
                    this.pawnHealing = (int)((float)healing * 1.4f);
                }
            }

            public void onRemoved(ActiveBuff buff) {
                super.onRemoved(buff);
                if (this.pawnHealing > 0) {
                    if (buff.owner.isServer()) {
                        MobHealthChangeEvent changeHeal = new MobHealthChangeEvent(buff.owner, -this.pawnHealing);
                        buff.owner.getLevel().entityManager.events.add((LevelEvent)changeHeal);
                    } else {
                        SoundManager.playSound((GameSound)GameResources.npchurt, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)buff.owner).pitch(((Float)GameRandom.globalRandom.getOneOf((Object[])new Float[]{Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)})).floatValue()));
                    }
                }
            }

            public void drawIcon(int x, int y, ActiveBuff buff) {
                super.drawIcon(x, y, buff);
                String text = Integer.toString(this.pawnHealing);
                int width = FontManager.bit.getWidthCeil(text, durationFontOptions);
                FontManager.bit.drawString((float)(x + 28 - width), (float)(y + 30 - FontManager.bit.getHeightCeil(text, durationFontOptions)), text, durationFontOptions);
            }
        });
    }

    public static void tier2ModifierRunes() {
        BuffRegistry.registerBuff((String)"abysmalrune", (Buff)new AphModifierRuneTrinketBuff(){

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                if (!player.buffManager.hasBuff("abysmalrunecooldown")) {
                    player.getLevel().entityManager.events.add((LevelEvent)new AphAbysmalRuneEvent((Mob)player, targetX, targetY));
                    player.buffManager.addBuff(new ActiveBuff("abysmalrunecooldown", (Mob)player, 10000, null), true);
                }
            }
        }.setHealthCost(0.1f));
        BuffRegistry.registerBuff((String)"abysmalrunecooldown", (Buff)new AphShownCooldownBuff());
        BuffRegistry.registerBuff((String)"tidalrune", (Buff)new AphModifierRuneTrinketBuff(){

            @Override
            public void runServer(Server server, PlayerMob player, int targetX, int targetY) {
                super.runServer(server, player, targetX, targetY);
                player.getLevel().entityManager.events.add((LevelEvent)new AphTidalRuneEvent((Mob)player));
                player.buffManager.forceUpdateBuffs();
            }

            @Override
            public void runClient(Client client, PlayerMob player, int targetX, int targetY) {
                super.runClient(client, player, targetX, targetY);
                float maxDist = 128.0f;
                int lifeTime = 1100;
                int minHeight = 0;
                int maxHeight = 30;
                int particles = 77;
                for (int i = 0; i < particles; ++i) {
                    float height = (float)minHeight + (float)(maxHeight - minHeight) * (float)i / (float)particles;
                    AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(GameRandom.globalRandom.nextFloat() * 360.0f));
                    float outDistance = GameRandom.globalRandom.getFloatBetween(60.0f, maxDist + 32.0f);
                    boolean counterclockwise = GameRandom.globalRandom.nextBoolean();
                    player.getLevel().entityManager.addParticle(player.x + GameRandom.globalRandom.getFloatBetween(0.0f, GameMath.sin((float)currentAngle.get().floatValue()) * maxDist), player.y + GameRandom.globalRandom.getFloatBetween(0.0f, GameMath.cos((float)currentAngle.get().floatValue()) * maxDist * 0.75f), Particle.GType.CRITICAL).color((Color)GameRandom.globalRandom.getOneOf((Object[])AphColors.paletteOcean)).height(height).moves((pos, delta, cLifeTime, timeAlive, lifePercent) -> {
                        float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 150.0f / 250.0f), Float::sum).floatValue();
                        if (counterclockwise) {
                            angle = -angle;
                        }
                        float linearDown = GameMath.lerpExp((float)lifePercent, (float)0.525f, (float)0.0f, (float)1.0f);
                        pos.x = player.x + outDistance * GameMath.sin((float)angle) * linearDown;
                        pos.y = player.y + outDistance * GameMath.cos((float)angle) * linearDown * 0.75f;
                    }).lifeTime(lifeTime).sizeFades(14, 18);
                }
            }
        }.setCooldownVariation(0.1f));
    }

    public static void tier3ModifierRunes() {
        BuffRegistry.registerBuff((String)"ascendantrune", (Buff)new AphModifierRuneTrinketBuff().setEffectNumberVariation(1.0f).setCooldownVariation(1.0f));
    }

    public static void registerMightyBannerBuffs() {
        if (AphDependencies.checkDependencyMightyBanner()) {
            MIGHTY_BANNER.FISHING = AphMightyBasicBannerBuff.intModifier((Modifier<Integer>)BuffModifiers.FISHING_POWER, 20);
            BuffRegistry.registerBuff((String)"aph_banneroffishing_normal", (Buff)MIGHTY_BANNER.FISHING);
            MIGHTY_BANNER.FISHING_GREATER = AphMightyBasicBannerBuff.intModifier((Modifier<Integer>)BuffModifiers.FISHING_POWER, 30);
            BuffRegistry.registerBuff((String)"aph_banneroffishing_greater", (Buff)MIGHTY_BANNER.FISHING_GREATER);
            MIGHTY_BANNER.HEALTH_REGEN = AphMightyBasicBannerBuff.floatModifier((Modifier<Float>)BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 0.5f);
            BuffRegistry.registerBuff((String)"aph_bannerofhealthregen_normal", (Buff)MIGHTY_BANNER.HEALTH_REGEN);
            MIGHTY_BANNER.HEALTH_REGEN_GREATER = AphMightyBasicBannerBuff.floatModifier((Modifier<Float>)BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, 1.0f);
            BuffRegistry.registerBuff((String)"aph_bannerofhealthregen_greater", (Buff)MIGHTY_BANNER.HEALTH_REGEN_GREATER);
            MIGHTY_BANNER.MANA_REGEN = AphMightyBasicBannerBuff.floatModifier((Modifier<Float>)BuffModifiers.COMBAT_MANA_REGEN, 2.0f);
            BuffRegistry.registerBuff((String)"aph_bannerofmanaregen_normal", (Buff)MIGHTY_BANNER.MANA_REGEN);
            MIGHTY_BANNER.MANA_REGEN_GREATER = AphMightyBasicBannerBuff.floatModifier((Modifier<Float>)BuffModifiers.COMBAT_MANA_REGEN, 4.0f);
            BuffRegistry.registerBuff((String)"aph_bannerofmanaregen_greater", (Buff)MIGHTY_BANNER.MANA_REGEN_GREATER);
            MIGHTY_BANNER.RESISTANCE = AphMightyBasicBannerBuff.intModifier((Modifier<Integer>)BuffModifiers.ARMOR_FLAT, 8);
            BuffRegistry.registerBuff((String)"aph_bannerofresistance_normal", (Buff)MIGHTY_BANNER.RESISTANCE);
            MIGHTY_BANNER.RESISTANCE_GREATER = AphMightyBasicBannerBuff.intModifier((Modifier<Integer>)BuffModifiers.ARMOR_FLAT, 12);
            BuffRegistry.registerBuff((String)"aph_bannerofresistance_greater", (Buff)MIGHTY_BANNER.RESISTANCE_GREATER);
            MIGHTY_BANNER.SUMMONING = AphMightyBasicBannerBuff.intModifier((Modifier<Integer>)BuffModifiers.MAX_SUMMONS, 1);
            BuffRegistry.registerBuff((String)"aph_bannerofsummoning_normal", (Buff)MIGHTY_BANNER.SUMMONING);
            MIGHTY_BANNER.SUMMONING_GREATER = AphMightyBasicBannerBuff.intModifier((Modifier<Integer>)BuffModifiers.MAX_SUMMONS, 2);
            BuffRegistry.registerBuff((String)"aph_bannerofsummoning_greater", (Buff)MIGHTY_BANNER.SUMMONING_GREATER);
            MIGHTY_BANNER.ATTACK_SPEED = AphMightyBasicBannerBuff.floatModifier((Modifier<Float>)BuffModifiers.ATTACK_SPEED, 0.15f);
            BuffRegistry.registerBuff((String)"aph_bannerofattackspeed_normal", (Buff)MIGHTY_BANNER.ATTACK_SPEED);
            MIGHTY_BANNER.ATTACK_SPEED_GREATER = AphMightyBasicBannerBuff.floatModifier((Modifier<Float>)BuffModifiers.ATTACK_SPEED, 0.2f);
            BuffRegistry.registerBuff((String)"aph_bannerofattackspeed_greater", (Buff)MIGHTY_BANNER.ATTACK_SPEED_GREATER);
        }
    }

    public static void registerSummonerExpansionBuffs() {
        if (AphDependencies.checkDependencyMightyBanner()) {
            SUMMONER_EXPANSION.BANNER_RESILIENCE = new AphBasicBannerBuff(AphBasicBannerBuff.AphBasicBannerBuffModifier.floatModifier((Modifier<Float>)BuffModifiers.MAX_RESILIENCE, 0.1f), AphBasicBannerBuff.AphBasicBannerBuffModifier.floatModifier((Modifier<Float>)BuffModifiers.RESILIENCE_REGEN_FLAT, 0.1f));
            BuffRegistry.registerBuff((String)"aph_bannerofresilience", (Buff)SUMMONER_EXPANSION.BANNER_RESILIENCE);
            SUMMONER_EXPANSION.BANNER_BOUNCING = AphBasicBannerBuff.intModifier((Modifier<Integer>)BuffModifiers.PROJECTILE_BOUNCES, 4);
            BuffRegistry.registerBuff((String)"aph_bannerofbouncing", (Buff)SUMMONER_EXPANSION.BANNER_BOUNCING);
            SUMMONER_EXPANSION.BANNER_ESSENCE = AphBasicBannerBuff.floatModifier((Modifier<Float>)BuffModifiers.LIFE_ESSENCE_DURATION, 2.0f);
            BuffRegistry.registerBuff((String)"aph_bannerofessence", (Buff)SUMMONER_EXPANSION.BANNER_ESSENCE);
            SUMMONER_EXPANSION.BANNER_STAMINA = new AphBasicBannerBuff(AphBasicBannerBuff.AphBasicBannerBuffModifier.floatModifier((Modifier<Float>)BuffModifiers.STAMINA_CAPACITY, 0.4f), AphBasicBannerBuff.AphBasicBannerBuffModifier.floatModifier((Modifier<Float>)BuffModifiers.STAMINA_REGEN, 0.1f), AphBasicBannerBuff.AphBasicBannerBuffModifier.floatModifier((Modifier<Float>)BuffModifiers.STAMINA_USAGE, -0.1f));
            BuffRegistry.registerBuff((String)"aph_bannerofstamina", (Buff)SUMMONER_EXPANSION.BANNER_STAMINA);
            SUMMONER_EXPANSION.BANNER_PICKING = AphBasicBannerBuff.floatModifier((Modifier<Float>)BuffModifiers.ITEM_PICKUP_RANGE, 8.0f);
            BuffRegistry.registerBuff((String)"aph_bannerofpicking", (Buff)SUMMONER_EXPANSION.BANNER_PICKING);
            SUMMONER_EXPANSION.BANNER_DASHING = new AphBasicBannerBuff(AphBasicBannerBuff.AphBasicBannerBuffModifier.intModifier((Modifier<Integer>)BuffModifiers.DASH_STACKS, 1), AphBasicBannerBuff.AphBasicBannerBuffModifier.floatModifier((Modifier<Float>)BuffModifiers.DASH_COOLDOWN, -0.1f));
            BuffRegistry.registerBuff((String)"aph_bannerofdashing", (Buff)SUMMONER_EXPANSION.BANNER_DASHING);
            SUMMONER_EXPANSION.BANNER_MANA = new AphBasicBannerBuff(AphBasicBannerBuff.AphBasicBannerBuffModifier.floatModifier((Modifier<Float>)BuffModifiers.MAX_MANA, 0.1f), AphBasicBannerBuff.AphBasicBannerBuffModifier.floatModifier((Modifier<Float>)BuffModifiers.MANA_REGEN, 0.25f));
            BuffRegistry.registerBuff((String)"aph_bannerofmana", (Buff)SUMMONER_EXPANSION.BANNER_MANA);
        }
    }

    public static class SET_BONUS {
        public static SetBonusBuff GOLD_HAT;
        public static SetBonusBuff ROCKY;
        public static SetBonusBuff WITCH;
        public static SetBonusBuff SWAMP_MASK;
        public static SetBonusBuff SWAMP_HOOP;
        public static SetBonusBuff INFECTED;
        public static SetBonusBuff SPINEL_HELMET;
        public static SetBonusBuff SPINEL_HAT;
    }

    public static class BANNER {
        public static AphBannerBuff BLANK;
        public static AphBannerBuff STRIKE;
        public static AphBannerBuff DAMAGE;
        public static AphBannerBuff DEFENSE;
        public static AphBannerBuff SPEED;
        public static AphBannerBuff SUMMON_SPEED;
    }

    public static class POTION {
        public static Buff VENOM_EXTRACT;
    }

    public static class MIGHTY_BANNER {
        public static AphBannerBuff FISHING;
        public static AphBannerBuff FISHING_GREATER;
        public static AphBannerBuff HEALTH_REGEN;
        public static AphBannerBuff HEALTH_REGEN_GREATER;
        public static AphBannerBuff MANA_REGEN;
        public static AphBannerBuff MANA_REGEN_GREATER;
        public static AphBannerBuff RESISTANCE;
        public static AphBannerBuff RESISTANCE_GREATER;
        public static AphBannerBuff SUMMONING;
        public static AphBannerBuff SUMMONING_GREATER;
        public static AphBannerBuff ATTACK_SPEED;
        public static AphBannerBuff ATTACK_SPEED_GREATER;
    }

    public static class SUMMONER_EXPANSION {
        public static AphBannerBuff BANNER_RESILIENCE;
        public static AphBannerBuff BANNER_BOUNCING;
        public static AphBannerBuff BANNER_ESSENCE;
        public static AphBannerBuff BANNER_STAMINA;
        public static AphBannerBuff BANNER_PICKING;
        public static AphBannerBuff BANNER_DASHING;
        public static AphBannerBuff BANNER_MANA;
    }
}

