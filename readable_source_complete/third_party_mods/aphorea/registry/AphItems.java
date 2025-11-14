/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.registry;

import aphorea.AphDependencies;
import aphorea.items.ammo.GelArrowItem;
import aphorea.items.ammo.SpamBullet;
import aphorea.items.ammo.UnstableGelArrowItem;
import aphorea.items.armor.Gold.GoldHat;
import aphorea.items.armor.Infected.InfectedBoots;
import aphorea.items.armor.Infected.InfectedChestplate;
import aphorea.items.armor.Infected.InfectedHat;
import aphorea.items.armor.Rocky.RockyBoots;
import aphorea.items.armor.Rocky.RockyChestplate;
import aphorea.items.armor.Rocky.RockyHelmet;
import aphorea.items.armor.Spinel.SpinelBoots;
import aphorea.items.armor.Spinel.SpinelChestplate;
import aphorea.items.armor.Spinel.SpinelHat;
import aphorea.items.armor.Spinel.SpinelHelmet;
import aphorea.items.armor.Swamp.SwampBoots;
import aphorea.items.armor.Swamp.SwampChestplate;
import aphorea.items.armor.Swamp.SwampHood;
import aphorea.items.armor.Swamp.SwampMask;
import aphorea.items.armor.Witch.MagicalBoots;
import aphorea.items.armor.Witch.MagicalSuit;
import aphorea.items.armor.Witch.PinkWitchHat;
import aphorea.items.backpacks.AmethystBackpack;
import aphorea.items.backpacks.BasicBackpack;
import aphorea.items.backpacks.DiamondBackpack;
import aphorea.items.backpacks.EmeraldBackpack;
import aphorea.items.backpacks.RubyBackpack;
import aphorea.items.backpacks.SapphireBackpack;
import aphorea.items.banners.AphStrikeBannerItem;
import aphorea.items.banners.BlankBannerItem;
import aphorea.items.banners.logic.AphBanner;
import aphorea.items.banners.logic.AphMightyBanner;
import aphorea.items.banners.logic.AphSummonerExpansionBanner;
import aphorea.items.consumable.InitialRune;
import aphorea.items.consumable.LifeSpinel;
import aphorea.items.consumable.UnstableCore;
import aphorea.items.consumable.VenomExtract;
import aphorea.items.misc.GelSlimeNullifier;
import aphorea.items.misc.books.RunesTutorialBook;
import aphorea.items.runes.AphBaseRune;
import aphorea.items.runes.AphModifierRune;
import aphorea.items.runes.AphRunesInjector;
import aphorea.items.tools.healing.GoldenWand;
import aphorea.items.tools.healing.HealingStaff;
import aphorea.items.tools.healing.MagicalVial;
import aphorea.items.tools.healing.SpinelStaff;
import aphorea.items.tools.healing.WoodenWand;
import aphorea.items.tools.weapons.magic.AdeptsBook;
import aphorea.items.tools.weapons.magic.BabylonCandle;
import aphorea.items.tools.weapons.magic.HarpOfHarmony;
import aphorea.items.tools.weapons.magic.MagicalBroom;
import aphorea.items.tools.weapons.magic.UnstableGelStaff;
import aphorea.items.tools.weapons.melee.battleaxe.DemonicBattleaxe;
import aphorea.items.tools.weapons.melee.battleaxe.UnstableGelBattleaxe;
import aphorea.items.tools.weapons.melee.dagger.CopperDagger;
import aphorea.items.tools.weapons.melee.dagger.DemonicDagger;
import aphorea.items.tools.weapons.melee.dagger.GoldDagger;
import aphorea.items.tools.weapons.melee.dagger.IronDagger;
import aphorea.items.tools.weapons.melee.dagger.LostUmbrellaDagger;
import aphorea.items.tools.weapons.melee.dagger.TungstenDagger;
import aphorea.items.tools.weapons.melee.glaive.WoodenRod;
import aphorea.items.tools.weapons.melee.greatsword.BabylonGreatsword;
import aphorea.items.tools.weapons.melee.greatsword.UnstableGelGreatsword;
import aphorea.items.tools.weapons.melee.rapier.FossilRapier;
import aphorea.items.tools.weapons.melee.rapier.LightRapier;
import aphorea.items.tools.weapons.melee.saber.AphCutlassSaber;
import aphorea.items.tools.weapons.melee.saber.CopperSaber;
import aphorea.items.tools.weapons.melee.saber.CrimsonKora;
import aphorea.items.tools.weapons.melee.saber.DemonicSaber;
import aphorea.items.tools.weapons.melee.saber.GlacialSaber;
import aphorea.items.tools.weapons.melee.saber.GoldSaber;
import aphorea.items.tools.weapons.melee.saber.HoneySaber;
import aphorea.items.tools.weapons.melee.saber.IronSaber;
import aphorea.items.tools.weapons.melee.saber.UnstableGelSaber;
import aphorea.items.tools.weapons.melee.sword.BrokenKora;
import aphorea.items.tools.weapons.melee.sword.Broom;
import aphorea.items.tools.weapons.melee.sword.CryoKatana;
import aphorea.items.tools.weapons.melee.sword.GelSword;
import aphorea.items.tools.weapons.melee.sword.TheNarcissist;
import aphorea.items.tools.weapons.melee.sword.UnstableGelSword;
import aphorea.items.tools.weapons.melee.sword.VoidHammer;
import aphorea.items.tools.weapons.range.blowgun.Blowgun;
import aphorea.items.tools.weapons.range.bow.SpinelCrossbow;
import aphorea.items.tools.weapons.range.greatbow.GelGreatbow;
import aphorea.items.tools.weapons.range.greatbow.UnstableGelGreatbow;
import aphorea.items.tools.weapons.range.gun.TheSpammer;
import aphorea.items.tools.weapons.range.sabergun.ShotgunSaber;
import aphorea.items.tools.weapons.range.sling.FireSling;
import aphorea.items.tools.weapons.range.sling.FrozenSling;
import aphorea.items.tools.weapons.range.sling.Sling;
import aphorea.items.tools.weapons.summoner.InfectedStaff;
import aphorea.items.tools.weapons.summoner.VolatileGelStaff;
import aphorea.items.tools.weapons.throwable.GelBall;
import aphorea.items.tools.weapons.throwable.GelBallGroup;
import aphorea.items.tools.weapons.throwable.UnstableGelveline;
import aphorea.items.trinkets.SwampShield;
import aphorea.items.vanillaitemtypes.AphCustomPickaxeToolItem;
import aphorea.items.vanillaitemtypes.AphGrassSeedItem;
import aphorea.items.vanillaitemtypes.AphMatItem;
import aphorea.items.vanillaitemtypes.AphPetItem;
import aphorea.items.vanillaitemtypes.AphSimpleTrinketItem;
import aphorea.registry.AphBuffs;
import java.util.ArrayList;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class AphItems {
    public static final ArrayList<Item> initialRunes = new ArrayList();

    public static void registerCore() {
        AphItems.registerMaterials();
        AphItems.registerTools();
        AphItems.registerArmor();
        AphItems.registerTrinkets();
        AphItems.registerConsumables();
        AphItems.registerMisc();
        AphItems.registerRunes();
        AphItems.registerMightyBannerItems();
        AphItems.registerSummonerExpansionItems();
    }

    public static void registerMaterials() {
        AphItems.registerItem("unstablegel", new AphMatItem(500, Item.Rarity.COMMON).setItemCategory(new String[]{"materials"}), 10.0f);
        AphItems.registerItem("rockygel", new AphMatItem(500, Item.Rarity.NORMAL).setItemCategory(new String[]{"materials"}), 5.0f);
        AphItems.registerItem("stardust", new AphMatItem(500, Item.Rarity.COMMON).setItemCategory(new String[]{"materials"}), 15.0f);
        AphItems.registerItem("infectedlog", new AphMatItem(500, "anylog").setItemCategory(new String[]{"materials", "logs"}), 2.0f);
        AphItems.registerItem("spinel", new AphMatItem(500, Item.Rarity.UNCOMMON).setItemCategory(new String[]{"materials", "minerals"}), 10.0f);
        AphItems.registerItem("infectedalloy", new AphMatItem(500, Item.Rarity.RARE).setItemCategory(new String[]{"materials"}), 30.0f);
    }

    public static void registerTools() {
        AphItems.registerItem("woodenrod", (Item)new WoodenRod());
        AphItems.registerItem("gelsword", (Item)new GelSword());
        AphItems.registerItem("unstablegelsword", (Item)new UnstableGelSword());
        AphItems.registerItem("unstablegelgreatsword", (Item)new UnstableGelGreatsword());
        AphItems.registerItem("unstablegelbattleaxe", (Item)new UnstableGelBattleaxe());
        AphItems.registerItem("demonicbattleaxe", (Item)new DemonicBattleaxe());
        AphItems.registerItem("coppersaber", (Item)new CopperSaber());
        AphItems.registerItem("ironsaber", (Item)new IronSaber());
        AphItems.registerItem("goldsaber", (Item)new GoldSaber());
        AphItems.registerItem("unstablegelsaber", (Item)new UnstableGelSaber(), 500.0f);
        AphItems.registerItem("demonicsaber", (Item)new DemonicSaber());
        AphItems.registerItem("broom", (Item)new Broom(), 50.0f);
        AphItems.registerItem("voidhammer", (Item)new VoidHammer());
        AphItems.registerItem("copperdagger", (Item)new CopperDagger(), 15.0f);
        AphItems.registerItem("irondagger", (Item)new IronDagger(), 20.0f);
        AphItems.registerItem("golddagger", (Item)new GoldDagger(), 25.0f);
        AphItems.registerItem("demonicdagger", (Item)new DemonicDagger());
        AphItems.registerItem("tungstendagger", (Item)new TungstenDagger());
        AphItems.replaceItem("cutlass", (Item)new AphCutlassSaber(), 500.0f);
        AphItems.registerItem("honeysaber", (Item)new HoneySaber());
        AphItems.registerItem("fossilrapier", (Item)new FossilRapier(), 100.0f);
        AphItems.registerItem("thenarcissist", (Item)new TheNarcissist());
        AphItems.registerItem("brokenkora", (Item)new BrokenKora(), 100.0f);
        AphItems.registerItem("lightrapier", (Item)new LightRapier(), 200.0f);
        AphItems.registerItem("babylongreatsword", (Item)new BabylonGreatsword(), 700.0f);
        AphItems.registerItem("glacialsaber", (Item)new GlacialSaber());
        AphItems.registerItem("lostumbrella", (Item)new LostUmbrellaDagger());
        AphItems.registerItem("cryokatana", (Item)new CryoKatana());
        AphItems.registerItem("crimsonkora", (Item)new CrimsonKora());
        AphItems.registerItem("blowgun", (Item)new Blowgun());
        AphItems.registerItem("sling", (Item)new Sling());
        AphItems.registerItem("firesling", (Item)new FireSling());
        AphItems.registerItem("frozensling", (Item)new FrozenSling());
        AphItems.registerItem("gelgreatbow", (Item)new GelGreatbow());
        AphItems.registerItem("unstablegelgreatbow", (Item)new UnstableGelGreatbow());
        AphItems.registerItem("thespammer", (Item)new TheSpammer(), 200.0f);
        AphItems.registerItem("shotgunsaber", (Item)new ShotgunSaber(), 200.0f);
        AphItems.registerItem("spinelcrossbow", (Item)new SpinelCrossbow());
        AphItems.registerItem("unstablegelstaff", (Item)new UnstableGelStaff());
        AphItems.registerItem("magicalbroom", (Item)new MagicalBroom());
        AphItems.registerItem("adeptsbook", (Item)new AdeptsBook(), 200.0f);
        AphItems.registerItem("harpofharmony", (Item)new HarpOfHarmony());
        AphItems.registerItem("babyloncandle", (Item)new BabylonCandle(), 700.0f);
        AphItems.registerItem("volatilegelstaff", (Item)new VolatileGelStaff());
        AphItems.registerItem("infectedstaff", (Item)new InfectedStaff());
        AphItems.registerItem("gelball", (Item)new GelBall(), 2.0f);
        AphItems.registerItem("gelballgroup", (Item)new GelBallGroup());
        AphItems.registerItem("unstablegelveline", (Item)new UnstableGelveline());
        AphItems.registerItem("superiorpickaxe", (Item)new AphCustomPickaxeToolItem(350, 220, 6.0f, 30, 60, 60, 1200, Item.Rarity.EPIC));
        AphItems.registerItem("healingstaff", (Item)new HealingStaff());
        AphItems.registerItem("magicalvial", (Item)new MagicalVial());
        AphItems.registerItem("woodenwand", (Item)new WoodenWand());
        AphItems.registerItem("goldenwand", (Item)new GoldenWand());
        AphItems.registerItem("spinelstaff", (Item)new SpinelStaff());
        AphItems.registerItem("blankbanner", (Item)new BlankBannerItem());
        AphItems.replaceItem("strikebanner", (Item)new AphStrikeBannerItem(), 50.0f);
        AphItems.replaceItem("bannerofdamage", (Item)new AphBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.BANNER.DAMAGE, 15.0f, new String[0]), 200.0f);
        AphItems.replaceItem("bannerofdefense", (Item)new AphBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.BANNER.DEFENSE, 10.0f, new String[0]), 200.0f);
        AphItems.replaceItem("bannerofspeed", (Item)new AphBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.BANNER.SPEED, 30.0f, new String[0]), 200.0f);
        AphItems.replaceItem("bannerofsummonspeed", (Item)new AphBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.BANNER.SUMMON_SPEED, 75.0f, new String[0]), 200.0f);
        AphItems.registerItem("gelarrow", (Item)new GelArrowItem(), 0.4f);
        AphItems.registerItem("unstablegelarrow", (Item)new UnstableGelArrowItem(), 2.2f);
        AphItems.registerItem("spambullet", (Item)new SpamBullet());
    }

    public static void registerArmor() {
        AphItems.registerItem("rockyhelmet", (Item)new RockyHelmet());
        AphItems.registerItem("rockychestplate", (Item)new RockyChestplate());
        AphItems.registerItem("rockyboots", (Item)new RockyBoots());
        AphItems.registerItem("goldhat", (Item)new GoldHat());
        AphItems.registerItem("pinkwitchhat", (Item)new PinkWitchHat(), 100.0f);
        AphItems.registerItem("magicalsuit", (Item)new MagicalSuit());
        AphItems.registerItem("magicalboots", (Item)new MagicalBoots());
        AphItems.registerItem("swampmask", (Item)new SwampMask());
        AphItems.registerItem("swamphood", (Item)new SwampHood());
        AphItems.registerItem("swampchestplate", (Item)new SwampChestplate());
        AphItems.registerItem("swampboots", (Item)new SwampBoots());
        AphItems.registerItem("infectedhat", (Item)new InfectedHat());
        AphItems.registerItem("infectedchestplate", (Item)new InfectedChestplate());
        AphItems.registerItem("infectedboots", (Item)new InfectedBoots());
        AphItems.registerItem("spinelhelmet", (Item)new SpinelHelmet());
        AphItems.registerItem("spinelhat", (Item)new SpinelHat());
        AphItems.registerItem("spinelchestplate", (Item)new SpinelChestplate());
        AphItems.registerItem("spinelboots", (Item)new SpinelBoots());
    }

    public static void registerTrinkets() {
        AphItems.registerItem("inspirationfoci", (Item)new AphSimpleTrinketItem(Item.Rarity.COMMON, "inspirationfoci", 500).addDisables(new String[]{"magicfoci", "rangefoci", "meleefoci", "summonfoci"}).addDisabledBy(new String[]{"magicfoci", "rangefoci", "meleefoci", "summonfoci"}));
        AphItems.registerItem("iceboots", (Item)new AphSimpleTrinketItem(Item.Rarity.COMMON, "iceboots", 300).addDisabledBy(new String[]{"spikedboots", "spikedbatboots"}));
        AphItems.registerItem("essenceofhealing", (Item)new AphSimpleTrinketItem(Item.Rarity.RARE, "essenceofhealing", 300, true), -1.0f);
        AphItems.registerItem("floralring", (Item)new AphSimpleTrinketItem(Item.Rarity.COMMON, "floralring", 200, true), 30.0f);
        AphItems.registerItem("gelring", (Item)new AphSimpleTrinketItem(Item.Rarity.COMMON, "gelring", 300, true), 50.0f);
        AphItems.registerItem("heartring", (Item)new AphSimpleTrinketItem(Item.Rarity.COMMON, "heartring", 300, true));
        AphItems.registerItem("ringofhealth", (Item)new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, new String[]{"floralring", "gelring", "heartring"}, 400, true).addDisables(new String[]{"floralring", "gelring", "heartring"}));
        AphItems.registerItem("rockyperiapt", (Item)new AphSimpleTrinketItem(Item.Rarity.COMMON, "rockyperiapt", 200));
        AphItems.registerItem("frozenperiapt", (Item)new AphSimpleTrinketItem(Item.Rarity.COMMON, "frozenperiapt", 300));
        AphItems.registerItem("bloodyperiapt", (Item)new AphSimpleTrinketItem(Item.Rarity.COMMON, "bloodyperiapt", 300).addDisabledBy(new String[]{"demonicperiapt", "abysmalperiapt"}));
        AphItems.registerItem("demonicperiapt", (Item)new AphSimpleTrinketItem(Item.Rarity.COMMON, "demonicperiapt", 400).addDisabledBy(new String[]{"abysmalperiapt"}));
        AphItems.registerItem("abysmalperiapt", (Item)new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, "abysmalperiapt", 500));
        AphItems.registerItem("unstableperiapt", (Item)new AphSimpleTrinketItem(Item.Rarity.COMMON, "unstableperiapt", 300).addDisabledBy(new String[]{"necromancyperiapt", "infectedperiapt"}), 100.0f);
        AphItems.registerItem("necromancyperiapt", (Item)new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, "necromancyperiapt", 500));
        AphItems.registerItem("infectedperiapt", (Item)new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, "infectedperiapt", 500));
        AphItems.registerItem("witchmedallion", (Item)new AphSimpleTrinketItem(Item.Rarity.COMMON, "witchmedallion", 300, true), 100.0f);
        AphItems.registerItem("cursedmedallion", (Item)new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, "cursedmedallion", 1000, true), 200.0f);
        AphItems.registerItem("ancientmedallion", (Item)new AphSimpleTrinketItem(Item.Rarity.EPIC, "ancientmedallion", 1200, true).addDisables(new String[]{"witchmedallion", "cursedmedallion"}), -1.0f);
        AphItems.registerItem("swampshield", (Item)new SwampShield());
        AphItems.registerItem("spinelshield", (Item)new AphSimpleTrinketItem(Item.Rarity.RARE, "spinelshield", 600));
        AphItems.registerItem("adrenalinecharm", (Item)new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, "adrenalinecharm", 400), 200.0f);
        AphItems.registerItem("bloomrushcharm", (Item)new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, "bloomrushcharm", 500).addDisables(new String[]{"zephyrcharm", "adrenalinecharm"}));
        AphItems.registerItem("ninjascarf", (Item)new AphSimpleTrinketItem(Item.Rarity.UNCOMMON, "ninjascarf", 400), 200.0f);
    }

    public static void registerConsumables() {
        AphItems.registerItem("unstablecore", (Item)new UnstableCore(), 20.0f);
        AphItems.registerItem("venomextract", (Item)new VenomExtract());
        AphItems.registerItem("lifespinel", (Item)new LifeSpinel(), 60.0f);
        AphItems.registerItem("initialrune", (Item)new InitialRune(), 0.0f);
    }

    public static void registerMisc() {
        AphItems.registerItem("cuberry", (Item)new AphPetItem("petphosphorslime", Item.Rarity.LEGENDARY), 50.0f);
        AphItems.registerItem("basicbackpack", (Item)new BasicBackpack());
        AphItems.registerItem("sapphirebackpack", (Item)new SapphireBackpack());
        AphItems.registerItem("amethystbackpack", (Item)new AmethystBackpack());
        AphItems.registerItem("rubybackpack", (Item)new RubyBackpack());
        AphItems.registerItem("emeraldbackpack", (Item)new EmeraldBackpack());
        AphItems.registerItem("diamondbackpack", (Item)new DiamondBackpack());
        AphItems.registerItem("runestutorialbook", new RunesTutorialBook(), 20.0f);
        AphItems.registerItem("gelslimenullifier", new GelSlimeNullifier());
        AphItems.registerItem("infectedgrassseed", (Item)new AphGrassSeedItem("infectedgrasstile"), 0.2f);
    }

    public static void registerRunes() {
        AphItems.registerItem("rusticrunesinjector", (Item)new AphRunesInjector(Item.Rarity.NORMAL, 0, 1));
        AphItems.registerItem("unstablerunesinjector", (Item)new AphRunesInjector(Item.Rarity.COMMON, 0, 1));
        AphItems.registerItem("demonicrunesinjector", (Item)new AphRunesInjector(Item.Rarity.UNCOMMON, 0, 1));
        AphItems.registerItem("tungstenrunesinjector", (Item)new AphRunesInjector(Item.Rarity.RARE, 0, 2));
        AphItems.registerItem("ancientrunesinjector", (Item)new AphRunesInjector(Item.Rarity.EPIC, 0, 3));
        AphItems.registerItem("runeoffury", new AphBaseRune(Item.Rarity.COMMON, 1, new String[0]).setInitialRune());
        AphItems.registerItem("runeofspeed", new AphBaseRune(Item.Rarity.COMMON, 1, new String[0]).setInitialRune());
        AphItems.registerItem("runeofhealing", new AphBaseRune(Item.Rarity.COMMON, 0, new String[0]).setInitialRune());
        AphItems.registerItem("runeofresistance", new AphBaseRune(Item.Rarity.COMMON, 2, new String[0]).setInitialRune());
        AphItems.registerItem("runeofvalor", new AphBaseRune(Item.Rarity.COMMON, 1, new String[0]){

            @Override
            public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
                ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
                tooltips.add(Localization.translate((String)"itemtooltip", (String)"inspiration"));
                return tooltips;
            }
        }.setInitialRune());
        AphItems.registerItem("runeofdetonation", new AphBaseRune(Item.Rarity.COMMON, 2, "runedamagereduction"));
        AphItems.registerItem("runeofthunder", new AphBaseRune(Item.Rarity.COMMON, 2, "runedamagereductionnoboss"), 50.0f);
        AphItems.registerItem("runeofwinter", new AphBaseRune(Item.Rarity.COMMON, 1, new String[0]));
        AphItems.registerItem("runeofimmortality", new AphBaseRune(Item.Rarity.COMMON, 3, new String[0]));
        AphItems.registerItem("runeofshadows", new AphBaseRune(Item.Rarity.COMMON, 1, new String[0]), 100.0f);
        int value = 40;
        int added = 10;
        AphItems.registerItem("runeofunstablegelslime", new AphBaseRune(Item.Rarity.UNCOMMON, 2, "runedamagereduction"), value += added);
        AphItems.registerItem("runeofevilsprotector", new AphBaseRune(Item.Rarity.UNCOMMON, 2, new String[0]), value += added);
        AphItems.registerItem("runeofqueenspider", new AphBaseRune(Item.Rarity.UNCOMMON, 1, new String[0]), value += added);
        AphItems.registerItem("runeofvoidwizard", new AphBaseRune(Item.Rarity.UNCOMMON, 1, new String[0]), value += added);
        AphItems.registerItem("runeofchieftain", new AphBaseRune(Item.Rarity.UNCOMMON, 1, new String[0]), value += added);
        AphItems.registerItem("runeofswampguardian", new AphBaseRune(Item.Rarity.UNCOMMON, 1, new String[0]), value += added);
        AphItems.registerItem("runeofancientvulture", new AphBaseRune(Item.Rarity.UNCOMMON, 2, "runedamagereduction"), value += added);
        AphItems.registerItem("runeofpiratecaptain", new AphBaseRune(Item.Rarity.UNCOMMON, 2, new String[0]), value += added);
        AphItems.registerItem("runeofreaper", new AphBaseRune(Item.Rarity.RARE, 1, new String[0]), value += added);
        AphItems.registerItem("runeofbabylontower", new AphBaseRune(Item.Rarity.RARE, 1, new String[0]), value += added);
        AphItems.registerItem("runeofcryoqueen", new AphBaseRune(Item.Rarity.RARE, 2, "runedamagereductionnoboss"), value += added);
        AphItems.registerItem("runeofpestwarden", new AphBaseRune(Item.Rarity.RARE, 2, "runedamagereductionnoboss"), value += added);
        AphItems.registerItem("runeofsageandgrit", new AphBaseRune(Item.Rarity.RARE, 2, new String[0]), value += added);
        AphItems.registerItem("runeoffallenwizard", new AphBaseRune(Item.Rarity.RARE, 2, new String[0]), value += added);
        AphItems.registerItem("runeofmotherslime", new AphBaseRune(Item.Rarity.EPIC, 1, "runedamagereductionnoboss"), value += added);
        AphItems.registerItem("runeofnightswarm", new AphBaseRune(Item.Rarity.EPIC, 2, new String[0]), value += added);
        AphItems.registerItem("runeofspiderempress", new AphBaseRune(Item.Rarity.EPIC, 2, new String[0]), value += added);
        AphItems.registerItem("runeofsunlightchampion", new AphBaseRune(Item.Rarity.EPIC, 2, "runedamagereduction"), value += added);
        AphItems.registerItem("runeofmoonlightdancer", new AphBaseRune(Item.Rarity.EPIC, 1, new String[0]), value += added);
        AphItems.registerItem("runeofcrystaldragon", new AphBaseRune(Item.Rarity.EPIC, 2, "runedamagereductionnoboss"), value += added);
        AphItems.registerItem("empoweringrune", new AphModifierRune(Item.Rarity.COMMON, 0, new String[0]));
        AphItems.registerItem("recurrentrune", new AphModifierRune(Item.Rarity.COMMON, 0, new String[0]));
        AphItems.registerItem("devastatingrune", new AphModifierRune(Item.Rarity.COMMON, 0, new String[0]));
        AphItems.registerItem("frenzyrune", new AphModifierRune(Item.Rarity.UNCOMMON, 0, new String[0]), 50.0f);
        AphItems.registerItem("vitalrune", new AphModifierRune(Item.Rarity.UNCOMMON, 0, new String[0]));
        AphItems.registerItem("onyxrune", new AphModifierRune(Item.Rarity.UNCOMMON, 3, "runedamagereduction"), 100.0f);
        AphItems.registerItem("pawningrune", new AphModifierRune(Item.Rarity.UNCOMMON, 2, new String[0]), 200.0f);
        AphItems.registerItem("abysmalrune", new AphModifierRune(Item.Rarity.RARE, 2, new String[0]), 200.0f);
        AphItems.registerItem("tidalrune", new AphModifierRune(Item.Rarity.RARE, 1, new String[0]), 300.0f);
        AphItems.registerItem("ascendantrune", new AphModifierRune(Item.Rarity.EPIC, 0, new String[0]), 300.0f);
    }

    public static void registerMightyBannerItems() {
        if (AphDependencies.checkDependencyMightyBanner()) {
            AphItems.replaceItem("banner_of_fishing", (Item)new AphMightyBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.MIGHTY_BANNER.FISHING, 20.0f, "banneroffishingeffect"), 200.0f);
            AphItems.replaceItem("banner_of_greater_fishing", (Item)new AphMightyBanner(Item.Rarity.RARE, 480, m -> AphBuffs.MIGHTY_BANNER.FISHING_GREATER, 30.0f, "banneroffishingeffect"));
            AphItems.replaceItem("banner_of_health_regen", (Item)new AphMightyBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.MIGHTY_BANNER.HEALTH_REGEN, 0.5f, "bannerofhealthregeneffect").addFloatReplacements(true), 200.0f);
            AphItems.replaceItem("banner_of_greater_health_regen", (Item)new AphMightyBanner(Item.Rarity.RARE, 480, m -> AphBuffs.MIGHTY_BANNER.HEALTH_REGEN_GREATER, 1.0f, "bannerofhealthregeneffect").addFloatReplacements(true));
            AphItems.replaceItem("banner_of_mana_regen", (Item)new AphMightyBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.MIGHTY_BANNER.MANA_REGEN, 200.0f, "bannerofmanaregeneffect"), 200.0f);
            AphItems.replaceItem("banner_of_greater_mana_regen", (Item)new AphMightyBanner(Item.Rarity.RARE, 480, m -> AphBuffs.MIGHTY_BANNER.MANA_REGEN_GREATER, 400.0f, "bannerofmanaregeneffect"));
            AphItems.replaceItem("banner_of_resistance", (Item)new AphMightyBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.MIGHTY_BANNER.RESISTANCE, 8.0f, "bannerofresistanceeffect"), 200.0f);
            AphItems.replaceItem("banner_of_greater_resistance", (Item)new AphMightyBanner(Item.Rarity.RARE, 480, m -> AphBuffs.MIGHTY_BANNER.RESISTANCE_GREATER, 12.0f, "bannerofresistanceeffect"));
            AphItems.replaceItem("banner_of_summoning", (Item)new AphMightyBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.MIGHTY_BANNER.SUMMONING, 1.0f, "bannerofsummoningeffect"), 200.0f);
            AphItems.replaceItem("banner_of_greater_summoning", (Item)new AphMightyBanner(Item.Rarity.RARE, 480, m -> AphBuffs.MIGHTY_BANNER.SUMMONING_GREATER, 2.0f, "bannerofsummoningeffect"));
            AphItems.replaceItem("banner_of_attack_speed", (Item)new AphMightyBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.MIGHTY_BANNER.ATTACK_SPEED, 15.0f, "bannerofattackspeedeffect"), 200.0f);
            AphItems.replaceItem("banner_of_greater_attack_speed", (Item)new AphMightyBanner(Item.Rarity.RARE, 480, m -> AphBuffs.MIGHTY_BANNER.ATTACK_SPEED_GREATER, 20.0f, "bannerofattackspeedeffect"));
        }
    }

    public static void registerSummonerExpansionItems() {
        if (AphDependencies.checkDependencySummonerExpansion()) {
            AphItems.replaceItem("bannerofresilience", (Item)new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.SUMMONER_EXPANSION.BANNER_RESILIENCE, 10.0f), 200.0f, true);
            AphItems.replaceItem("bannerofbouncing", (Item)new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.SUMMONER_EXPANSION.BANNER_BOUNCING, 4.0f), 200.0f, true);
            AphItems.replaceItem("bannerofessence", (Item)new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.SUMMONER_EXPANSION.BANNER_ESSENCE, 200.0f), 200.0f, true);
            AphItems.replaceItem("bannerofstamina", (Item)new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.SUMMONER_EXPANSION.BANNER_STAMINA, 40.0f, 10.0f), 200.0f, true);
            AphItems.replaceItem("bannerofpicking", (Item)new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.SUMMONER_EXPANSION.BANNER_PICKING, 8.0f).addFloatReplacements(true), 200.0f, true);
            AphItems.replaceItem("bannerofdashing", (Item)new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.SUMMONER_EXPANSION.BANNER_DASHING, 1.0f, 10.0f), 200.0f, true);
            AphItems.replaceItem("bannerofmana", (Item)new AphSummonerExpansionBanner(Item.Rarity.COMMON, 480, m -> AphBuffs.SUMMONER_EXPANSION.BANNER_MANA, 10.0f, 25.0f), 200.0f, true);
        }
    }

    private static void registerItem(String stringID, Item item, float brokerValue, boolean isObtainable) {
        ItemRegistry.registerItem((String)stringID, (Item)item, (float)brokerValue, (boolean)isObtainable);
    }

    private static void registerItem(String stringID, Item item, float brokerValue) {
        AphItems.registerItem(stringID, item, brokerValue, true);
    }

    private static void registerItem(String stringID, Item item) {
        AphItems.registerItem(stringID, item, -1.0f, true);
    }

    private static void replaceItem(String stringID, Item item, float brokerValue, boolean isObtainable) {
        ItemRegistry.replaceItem((String)stringID, (Item)item, (float)brokerValue, (boolean)isObtainable);
    }

    private static void replaceItem(String stringID, Item item, float brokerValue) {
        AphItems.replaceItem(stringID, item, brokerValue, true);
    }

    private static void replaceItem(String stringID, Item item) {
        AphItems.replaceItem(stringID, item, -1.0f, true);
    }
}

