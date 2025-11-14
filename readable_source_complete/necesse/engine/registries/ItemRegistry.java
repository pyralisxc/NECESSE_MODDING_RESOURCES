/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.GameMusic;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.FishingPoleHolding;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemHolding;
import necesse.inventory.item.SwingSpriteAttackItem;
import necesse.inventory.item.WorkSpriteAttackItem;
import necesse.inventory.item.armorItem.agedChampion.AgedChampionChestplateArmorItem;
import necesse.inventory.item.armorItem.agedChampion.AgedChampionGreavesArmorItem;
import necesse.inventory.item.armorItem.agedChampion.AgedChampionHelmetArmorItem;
import necesse.inventory.item.armorItem.ancestor.AncestorsBootsArmorItem;
import necesse.inventory.item.armorItem.ancestor.AncestorsHatArmorItem;
import necesse.inventory.item.armorItem.ancestor.AncestorsRobeArmorItem;
import necesse.inventory.item.armorItem.ancientFossil.AncientFossilBootsArmorItem;
import necesse.inventory.item.armorItem.ancientFossil.AncientFossilChestplateArmorItem;
import necesse.inventory.item.armorItem.ancientFossil.AncientFossilHelmetArmorItem;
import necesse.inventory.item.armorItem.ancientFossil.AncientFossilMaskArmorItem;
import necesse.inventory.item.armorItem.arachnid.ArachnidChestplateArmorItem;
import necesse.inventory.item.armorItem.arachnid.ArachnidHelmetArmorItem;
import necesse.inventory.item.armorItem.arachnid.ArachnidLegsArmorItem;
import necesse.inventory.item.armorItem.arcanic.ArcanicBootsArmorItem;
import necesse.inventory.item.armorItem.arcanic.ArcanicChestplateArmorItem;
import necesse.inventory.item.armorItem.arcanic.ArcanicHelmetArmorItem;
import necesse.inventory.item.armorItem.battlechef.BattleChefBootsArmorItem;
import necesse.inventory.item.armorItem.battlechef.BattleChefChestplateArmorItem;
import necesse.inventory.item.armorItem.battlechef.BattleChefHatArmorItem;
import necesse.inventory.item.armorItem.bloodplate.BloodplateBootsArmorItem;
import necesse.inventory.item.armorItem.bloodplate.BloodplateChestplateArmorItem;
import necesse.inventory.item.armorItem.bloodplate.BloodplateCowlArmorItem;
import necesse.inventory.item.armorItem.cloth.ClothBootsArmorItem;
import necesse.inventory.item.armorItem.cloth.ClothHatArmorItem;
import necesse.inventory.item.armorItem.cloth.ClothRobeArmorItem;
import necesse.inventory.item.armorItem.copper.CopperBootsArmorItem;
import necesse.inventory.item.armorItem.copper.CopperChestplateArmorItem;
import necesse.inventory.item.armorItem.copper.CopperHelmetArmorItem;
import necesse.inventory.item.armorItem.cosmetics.alien.AlienCostumeBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.alien.AlienCostumeShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.alien.AlienMaskArmorItem;
import necesse.inventory.item.armorItem.cosmetics.animalKeeper.AnimalKeeperHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.animalKeeper.AnimalKeeperShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.animalKeeper.AnimalKeeperShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.chicken.ChickenCostumeBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.chicken.ChickenCostumeShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.chicken.ChickenMaskArmorItem;
import necesse.inventory.item.armorItem.cosmetics.crimson.CrimsonHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.crimson.CrimsonRobeArmorItem;
import necesse.inventory.item.armorItem.cosmetics.crimson.CrimsonShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.elder.ElderHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.elder.ElderShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.elder.ElderShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.exotic.ExoticShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.exotic.ExoticShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.exotic.TurbanArmorItem;
import necesse.inventory.item.armorItem.cosmetics.exotic.WalkingStickHoldItem;
import necesse.inventory.item.armorItem.cosmetics.farmer.FarmerHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.farmer.FarmerPitchForkHoldItem;
import necesse.inventory.item.armorItem.cosmetics.farmer.FarmerShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.farmer.FarmerShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.frog.FrogCostumeBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.frog.FrogCostumeShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.frog.FrogMaskArmorItem;
import necesse.inventory.item.armorItem.cosmetics.horse.HorseCostumeBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.horse.HorseCostumeShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.horse.HorseMaskArmorItem;
import necesse.inventory.item.armorItem.cosmetics.hula.HulaHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.hula.HulaShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.hula.HulaShirtWithTopArmorItem;
import necesse.inventory.item.armorItem.cosmetics.hunter.HunterBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.hunter.HunterHoodArmorItem;
import necesse.inventory.item.armorItem.cosmetics.hunter.HunterHoodMaskArmorItem;
import necesse.inventory.item.armorItem.cosmetics.hunter.HunterShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.jester.JesterBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.jester.JesterHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.jester.JesterShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.lab.AlchemistGlassesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.lab.EngineerGogglesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.lab.LabApronArmorItem;
import necesse.inventory.item.armorItem.cosmetics.lab.LabBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.lab.LabCoatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.mage.MageHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.mage.MageRobeArmorItem;
import necesse.inventory.item.armorItem.cosmetics.mage.MageShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.merchant.MerchantBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.merchant.MerchantShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.miner.MinerBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.miner.MinerHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.miner.MinerShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.CheatShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.CheatShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.CheatWigArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.ChristmasHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.EmpressMaskArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.HardHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.PartyHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.PumpkinMaskArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.ShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.ShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.SunglassesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.SurgicalMaskArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.TrapperHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.VultureMaskArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.WigArmorItem;
import necesse.inventory.item.armorItem.cosmetics.pawnBroker.BlazerArmorItem;
import necesse.inventory.item.armorItem.cosmetics.pawnBroker.DressShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.pawnBroker.TopHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.pirate.CaptainsBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.pirate.CaptainsHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.pirate.CaptainsShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.pirate.PirateBandanaArmorItem;
import necesse.inventory.item.armorItem.cosmetics.pirate.PirateBandanaWithEyePatchArmorItem;
import necesse.inventory.item.armorItem.cosmetics.pirate.PirateBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.pirate.PirateEyePatchArmorItem;
import necesse.inventory.item.armorItem.cosmetics.pirate.PirateShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.plague.PlagueBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.plague.PlagueMaskArmorItem;
import necesse.inventory.item.armorItem.cosmetics.plague.PlagueRobeArmorItem;
import necesse.inventory.item.armorItem.cosmetics.rain.RainBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.rain.RainCoatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.rain.RainHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.safari.SafariHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.safari.SafariShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.safari.SafariShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.sailor.SailorHatArmorItem;
import necesse.inventory.item.armorItem.cosmetics.sailor.SailorShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.sailor.SailorShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.smithing.SmithingApronArmorItem;
import necesse.inventory.item.armorItem.cosmetics.smithing.SmithingShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.snow.SnowBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.snow.SnowCloakArmorItem;
import necesse.inventory.item.armorItem.cosmetics.snow.SnowHoodArmorItem;
import necesse.inventory.item.armorItem.cosmetics.space.SpaceBootsArmorItem;
import necesse.inventory.item.armorItem.cosmetics.space.SpaceHelmetArmorItem;
import necesse.inventory.item.armorItem.cosmetics.space.SpaceSuitArmorItem;
import necesse.inventory.item.armorItem.cosmetics.stylist.StylishFlowerArmorItem;
import necesse.inventory.item.armorItem.cosmetics.stylist.StylistShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.stylist.StylistShoesArmorItem;
import necesse.inventory.item.armorItem.cosmetics.swim.SwimSuitArmorItem;
import necesse.inventory.item.armorItem.cosmetics.swim.SwimTrunksArmorItem;
import necesse.inventory.item.armorItem.cryowitch.CryoWitchHatArmorItem;
import necesse.inventory.item.armorItem.cryowitch.CryoWitchRobeArmorItem;
import necesse.inventory.item.armorItem.cryowitch.CryoWitchShoesArmorItem;
import necesse.inventory.item.armorItem.crystal.AmethystHelmet;
import necesse.inventory.item.armorItem.crystal.CrystalBoots;
import necesse.inventory.item.armorItem.crystal.CrystalChestplate;
import necesse.inventory.item.armorItem.crystal.EmeraldMask;
import necesse.inventory.item.armorItem.crystal.RubyCrown;
import necesse.inventory.item.armorItem.crystal.SapphireEyepatch;
import necesse.inventory.item.armorItem.dawn.DawnBootsArmorItem;
import necesse.inventory.item.armorItem.dawn.DawnChestplateArmorItem;
import necesse.inventory.item.armorItem.dawn.DawnHelmetArmorItem;
import necesse.inventory.item.armorItem.deepfrost.DeepfrostBootsArmorItem;
import necesse.inventory.item.armorItem.deepfrost.DeepfrostChestplateArmorItem;
import necesse.inventory.item.armorItem.deepfrost.DeepfrostHelmetArmorItem;
import necesse.inventory.item.armorItem.demonic.DemonicBootsArmorItem;
import necesse.inventory.item.armorItem.demonic.DemonicChestplateArmorItem;
import necesse.inventory.item.armorItem.demonic.DemonicHelmetArmorItem;
import necesse.inventory.item.armorItem.dryad.DryadBootsArmorItem;
import necesse.inventory.item.armorItem.dryad.DryadChestplateArmorItem;
import necesse.inventory.item.armorItem.dryad.DryadCrownArmorItem;
import necesse.inventory.item.armorItem.dryad.DryadHatArmorItem;
import necesse.inventory.item.armorItem.dryad.DryadHelmetArmorItem;
import necesse.inventory.item.armorItem.dryad.DryadScarfArmorItem;
import necesse.inventory.item.armorItem.dusk.DuskBootsArmorItem;
import necesse.inventory.item.armorItem.dusk.DuskChestplateArmorItem;
import necesse.inventory.item.armorItem.dusk.DuskHelmetArmorItem;
import necesse.inventory.item.armorItem.frost.FrostBootsArmorItem;
import necesse.inventory.item.armorItem.frost.FrostChestplateArmorItem;
import necesse.inventory.item.armorItem.frost.FrostHatArmorItem;
import necesse.inventory.item.armorItem.frost.FrostHelmetArmorItem;
import necesse.inventory.item.armorItem.frost.FrostHoodArmorItem;
import necesse.inventory.item.armorItem.glacial.GlacialBootsArmorItem;
import necesse.inventory.item.armorItem.glacial.GlacialChestplateArmorItem;
import necesse.inventory.item.armorItem.glacial.GlacialCircletArmorItem;
import necesse.inventory.item.armorItem.glacial.GlacialHelmetArmorItem;
import necesse.inventory.item.armorItem.gold.GoldBootsArmorItem;
import necesse.inventory.item.armorItem.gold.GoldChestplateArmorItem;
import necesse.inventory.item.armorItem.gold.GoldCrownArmorItem;
import necesse.inventory.item.armorItem.gold.GoldHelmetArmorItem;
import necesse.inventory.item.armorItem.gunslinger.GunslingerBootsArmorItem;
import necesse.inventory.item.armorItem.gunslinger.GunslingerHatArmorItem;
import necesse.inventory.item.armorItem.gunslinger.GunslingerVestArmorItem;
import necesse.inventory.item.armorItem.iron.IronBootsArmorItem;
import necesse.inventory.item.armorItem.iron.IronChestplateArmorItem;
import necesse.inventory.item.armorItem.iron.IronHelmetArmorItem;
import necesse.inventory.item.armorItem.ivy.IvyBootsArmorItem;
import necesse.inventory.item.armorItem.ivy.IvyChestplateArmorItem;
import necesse.inventory.item.armorItem.ivy.IvyCircletArmorItem;
import necesse.inventory.item.armorItem.ivy.IvyHatArmorItem;
import necesse.inventory.item.armorItem.ivy.IvyHelmetArmorItem;
import necesse.inventory.item.armorItem.ivy.IvyHoodArmorItem;
import necesse.inventory.item.armorItem.leather.LeatherBootsArmorItem;
import necesse.inventory.item.armorItem.leather.LeatherHoodArmorItem;
import necesse.inventory.item.armorItem.leather.LeatherShirtArmorItem;
import necesse.inventory.item.armorItem.mycelium.MyceliumBootsArmorItem;
import necesse.inventory.item.armorItem.mycelium.MyceliumChestplateArmorItem;
import necesse.inventory.item.armorItem.mycelium.MyceliumHoodArmorItem;
import necesse.inventory.item.armorItem.mycelium.MyceliumScarfArmorItem;
import necesse.inventory.item.armorItem.nightsteel.NightsteelBootsArmorItem;
import necesse.inventory.item.armorItem.nightsteel.NightsteelChestplateArmorItem;
import necesse.inventory.item.armorItem.nightsteel.NightsteelCircletArmorItem;
import necesse.inventory.item.armorItem.nightsteel.NightsteelHelmetArmorItem;
import necesse.inventory.item.armorItem.nightsteel.NightsteelMaskArmorItem;
import necesse.inventory.item.armorItem.nightsteel.NightsteelVeilArmorItem;
import necesse.inventory.item.armorItem.ninja.NinjaHoodArmorItem;
import necesse.inventory.item.armorItem.ninja.NinjaRobeArmorItem;
import necesse.inventory.item.armorItem.ninja.NinjaShoesArmorItem;
import necesse.inventory.item.armorItem.pharaoh.PharaohsHeaddress;
import necesse.inventory.item.armorItem.pharaoh.PharaohsRobeArmorItem;
import necesse.inventory.item.armorItem.pharaoh.PharaohsSandalsArmorItem;
import necesse.inventory.item.armorItem.quartz.QuartzBootsArmorItem;
import necesse.inventory.item.armorItem.quartz.QuartzChestplateArmorItem;
import necesse.inventory.item.armorItem.quartz.QuartzCrownArmorItem;
import necesse.inventory.item.armorItem.quartz.QuartzHelmetArmorItem;
import necesse.inventory.item.armorItem.ravenlords.RavenlordsBootsArmorItem;
import necesse.inventory.item.armorItem.ravenlords.RavenlordsChestplateArmorItem;
import necesse.inventory.item.armorItem.ravenlords.RavenlordsHeaddressArmorItem;
import necesse.inventory.item.armorItem.runebound.RuneboundBackBonesArmorItem;
import necesse.inventory.item.armorItem.runebound.RuneboundBonesRobeChestArmorItem;
import necesse.inventory.item.armorItem.runebound.RuneboundBootsArmorItem;
import necesse.inventory.item.armorItem.runebound.RuneboundCrownArmorItem;
import necesse.inventory.item.armorItem.runebound.RuneboundCrownMaskArmorItem;
import necesse.inventory.item.armorItem.runebound.RuneboundHelmetArmorItem;
import necesse.inventory.item.armorItem.runebound.RuneboundHoodArmorItem;
import necesse.inventory.item.armorItem.runebound.RuneboundHornHelmetArmorItem;
import necesse.inventory.item.armorItem.runebound.RuneboundLeatherChestArmorItem;
import necesse.inventory.item.armorItem.runebound.RuneboundRobeArmorItem;
import necesse.inventory.item.armorItem.runebound.RuneboundSkullHelmetArmorItem;
import necesse.inventory.item.armorItem.runestone.RunicBootsArmorItem;
import necesse.inventory.item.armorItem.runestone.RunicChestplateArmorItem;
import necesse.inventory.item.armorItem.runestone.RunicCrownArmorItem;
import necesse.inventory.item.armorItem.runestone.RunicHatArmorItem;
import necesse.inventory.item.armorItem.runestone.RunicHelmetArmorItem;
import necesse.inventory.item.armorItem.runestone.RunicHoodArmorItem;
import necesse.inventory.item.armorItem.shadow.ShadowBootsArmorItem;
import necesse.inventory.item.armorItem.shadow.ShadowHatArmorItem;
import necesse.inventory.item.armorItem.shadow.ShadowHoodArmorItem;
import necesse.inventory.item.armorItem.shadow.ShadowMantleArmorItem;
import necesse.inventory.item.armorItem.sharpshooter.SharpshooterBootsArmorItem;
import necesse.inventory.item.armorItem.sharpshooter.SharpshooterCoatArmorItem;
import necesse.inventory.item.armorItem.sharpshooter.SharpshooterHatArmorItem;
import necesse.inventory.item.armorItem.slime.SlimeBootsArmorItem;
import necesse.inventory.item.armorItem.slime.SlimeChestplateArmorItem;
import necesse.inventory.item.armorItem.slime.SlimeHatArmorItem;
import necesse.inventory.item.armorItem.slime.SlimeHelmetArmorItem;
import necesse.inventory.item.armorItem.soldier.SoldierBootsArmorItem;
import necesse.inventory.item.armorItem.soldier.SoldierCapArmorItem;
import necesse.inventory.item.armorItem.soldier.SoldierChestplateArmorItem;
import necesse.inventory.item.armorItem.soldier.SoldierHelmetArmorItem;
import necesse.inventory.item.armorItem.soulseed.SoulseedBootsArmorItem;
import necesse.inventory.item.armorItem.soulseed.SoulseedChestplateArmorItem;
import necesse.inventory.item.armorItem.soulseed.SoulseedCrownArmorItem;
import necesse.inventory.item.armorItem.spider.SpiderBootsArmorItem;
import necesse.inventory.item.armorItem.spider.SpiderChestplateArmorItem;
import necesse.inventory.item.armorItem.spider.SpiderHelmetArmorItem;
import necesse.inventory.item.armorItem.spiderite.SpideriteChestplateArmorItem;
import necesse.inventory.item.armorItem.spiderite.SpideriteCrownArmorItem;
import necesse.inventory.item.armorItem.spiderite.SpideriteGreavesArmorItem;
import necesse.inventory.item.armorItem.spiderite.SpideriteHatArmorItem;
import necesse.inventory.item.armorItem.spiderite.SpideriteHelmetArmorItem;
import necesse.inventory.item.armorItem.spiderite.SpideriteHoodArmorItem;
import necesse.inventory.item.armorItem.supporter.SupporterBootsArmorItem;
import necesse.inventory.item.armorItem.supporter.SupporterChestplateArmorItem;
import necesse.inventory.item.armorItem.supporter.SupporterHelmetArmorItem;
import necesse.inventory.item.armorItem.thief.ThiefsBootsArmorItem;
import necesse.inventory.item.armorItem.thief.ThiefsCloakArmorItem;
import necesse.inventory.item.armorItem.thief.ThiefsCowlArmorItem;
import necesse.inventory.item.armorItem.tungsten.TungstenBootsArmorItem;
import necesse.inventory.item.armorItem.tungsten.TungstenChestplateArmorItem;
import necesse.inventory.item.armorItem.tungsten.TungstenHelmetArmorItem;
import necesse.inventory.item.armorItem.voixd.VoidBootsArmorItem;
import necesse.inventory.item.armorItem.voixd.VoidHatArmorItem;
import necesse.inventory.item.armorItem.voixd.VoidMaskArmorItem;
import necesse.inventory.item.armorItem.voixd.VoidRobeArmorItem;
import necesse.inventory.item.armorItem.widow.WidowBootsArmorItem;
import necesse.inventory.item.armorItem.widow.WidowChestplateArmorItem;
import necesse.inventory.item.armorItem.widow.WidowHelmetArmorItem;
import necesse.inventory.item.armorItem.witch.WitchHatArmorItem;
import necesse.inventory.item.armorItem.witch.WitchRobeArmorItem;
import necesse.inventory.item.armorItem.witch.WitchShoesArmorItem;
import necesse.inventory.item.arrowItem.BoneArrowItem;
import necesse.inventory.item.arrowItem.BouncingArrowItem;
import necesse.inventory.item.arrowItem.FireArrowItem;
import necesse.inventory.item.arrowItem.FrostArrowItem;
import necesse.inventory.item.arrowItem.IronArrowItem;
import necesse.inventory.item.arrowItem.PoisonArrowItem;
import necesse.inventory.item.arrowItem.SpideriteArrowItem;
import necesse.inventory.item.arrowItem.StoneArrowItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.bulletItem.BouncingBulletItem;
import necesse.inventory.item.bulletItem.CannonballAmmoItem;
import necesse.inventory.item.bulletItem.CrystalBulletItem;
import necesse.inventory.item.bulletItem.FrostBulletItem;
import necesse.inventory.item.bulletItem.SeedBulletItem;
import necesse.inventory.item.bulletItem.SimpleBulletItem;
import necesse.inventory.item.bulletItem.VoidBulletItem;
import necesse.inventory.item.matItem.BookMatItem;
import necesse.inventory.item.matItem.EssenceMatItem;
import necesse.inventory.item.matItem.FishItem;
import necesse.inventory.item.matItem.MatItem;
import necesse.inventory.item.matItem.MultiTextureMatItem;
import necesse.inventory.item.matItem.RevivalPotion;
import necesse.inventory.item.miscItem.AmmoBag;
import necesse.inventory.item.miscItem.AmmoPouch;
import necesse.inventory.item.miscItem.AscendedShardItem;
import necesse.inventory.item.miscItem.BannerItem;
import necesse.inventory.item.miscItem.BinocularsItem;
import necesse.inventory.item.miscItem.BoneOfferingItem;
import necesse.inventory.item.miscItem.ChristmasPresentItem;
import necesse.inventory.item.miscItem.CoinPouch;
import necesse.inventory.item.miscItem.CraftingGuideBookItem;
import necesse.inventory.item.miscItem.DragonSoulsItem;
import necesse.inventory.item.miscItem.EnchantingScrollItem;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.item.miscItem.InfiniteRopeItem;
import necesse.inventory.item.miscItem.Lunchbox;
import necesse.inventory.item.miscItem.PortableMusicPlayerItem;
import necesse.inventory.item.miscItem.PotionBag;
import necesse.inventory.item.miscItem.PotionPouch;
import necesse.inventory.item.miscItem.PresentItem;
import necesse.inventory.item.miscItem.RecipeBookItem;
import necesse.inventory.item.miscItem.RopeItem;
import necesse.inventory.item.miscItem.SeedPouch;
import necesse.inventory.item.miscItem.ShearsItem;
import necesse.inventory.item.miscItem.StrikeBannerItem;
import necesse.inventory.item.miscItem.TabletBox;
import necesse.inventory.item.miscItem.TelescopeItem;
import necesse.inventory.item.miscItem.VinylItem;
import necesse.inventory.item.miscItem.VoidBagItem;
import necesse.inventory.item.miscItem.VoidPouchItem;
import necesse.inventory.item.miscItem.WorkInProgressItem;
import necesse.inventory.item.miscItem.WrappingPaperItem;
import necesse.inventory.item.mountItem.HoverBoardMountItem;
import necesse.inventory.item.mountItem.JumpingBallMountItem;
import necesse.inventory.item.mountItem.MinecartMountItem;
import necesse.inventory.item.mountItem.MountItem;
import necesse.inventory.item.mountItem.RuneboundBoatMountItem;
import necesse.inventory.item.mountItem.SeahorseMountItem;
import necesse.inventory.item.mountItem.SteelBoatMountItem;
import necesse.inventory.item.mountItem.WitchBroomMountItem;
import necesse.inventory.item.mountItem.WoodBoatMountItem;
import necesse.inventory.item.placeableItem.ApiaryFramePlaceableItem;
import necesse.inventory.item.placeableItem.CutterPlaceableItem;
import necesse.inventory.item.placeableItem.FertilizerPlaceableItem;
import necesse.inventory.item.placeableItem.FireworkPlaceableItem;
import necesse.inventory.item.placeableItem.HoneyBeePlaceableItem;
import necesse.inventory.item.placeableItem.ImportedAnimalSpawnItem;
import necesse.inventory.item.placeableItem.StonePlaceableItem;
import necesse.inventory.item.placeableItem.WrenchPlaceableItem;
import necesse.inventory.item.placeableItem.bucketItem.BucketItem;
import necesse.inventory.item.placeableItem.bucketItem.InfiniteWaterBucketItem;
import necesse.inventory.item.placeableItem.consumableItem.CryoHeartItem;
import necesse.inventory.item.placeableItem.consumableItem.DemonHeartItem;
import necesse.inventory.item.placeableItem.consumableItem.GreaterLifeElixirItem;
import necesse.inventory.item.placeableItem.consumableItem.GuardianHeartItem;
import necesse.inventory.item.placeableItem.consumableItem.ItemSetsIncreaseItem;
import necesse.inventory.item.placeableItem.consumableItem.LifeElixirItem;
import necesse.inventory.item.placeableItem.consumableItem.PortalFlaskItem;
import necesse.inventory.item.placeableItem.consumableItem.RecallFlaskItem;
import necesse.inventory.item.placeableItem.consumableItem.RecallScrollItem;
import necesse.inventory.item.placeableItem.consumableItem.RunicHeartItem;
import necesse.inventory.item.placeableItem.consumableItem.SpiderHeartItem;
import necesse.inventory.item.placeableItem.consumableItem.StinkFlaskItem;
import necesse.inventory.item.placeableItem.consumableItem.TeleportationScrollItem;
import necesse.inventory.item.placeableItem.consumableItem.TestChangeItemSetsItem;
import necesse.inventory.item.placeableItem.consumableItem.TestChangeTrinketSlotsItem;
import necesse.inventory.item.placeableItem.consumableItem.TrinketSlotsIncreaseItem;
import necesse.inventory.item.placeableItem.consumableItem.WardenHeartItem;
import necesse.inventory.item.placeableItem.consumableItem.food.EggFoodConsumableItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodMatItem;
import necesse.inventory.item.placeableItem.consumableItem.food.GrainItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions.AccuracyPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions.AttackSpeedPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions.BattlePotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions.FireResistancePotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions.HealthRegenPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions.KnockbackPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions.ManaRegenPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions.RapidPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions.ResistancePotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions.SpeedPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.buffPotions.ThornsPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions.GreaterAccuracyPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions.GreaterAttackSpeedPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions.GreaterBattlePotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions.GreaterHealthRegenPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions.GreaterManaRegenPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions.GreaterRapidPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions.GreaterResistancePotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions.GreaterSpeedPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions.MinionPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions.RangerPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions.StrengthPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions.WebPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.greaterBuffPotions.WisdomPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.resourcePotions.HealthPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.resourcePotions.ManaPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions.BuildingPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions.FishingPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions.GreaterBuildingPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions.GreaterFishingPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions.GreaterMiningPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions.InvisibilityPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions.MiningPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions.PassivePotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions.SpelunkerPotionItem;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.utilityBuffPotions.TreasurePotionItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.AncientVultureSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.CryoQueenSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.CursedCroneSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.EvilsProtectorSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.MotherSlimeSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.NightSwarmSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.PestWardenSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.QueenSpiderSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.ReaperSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.SpiderEmpressSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.SwampGuardianSpawnItem;
import necesse.inventory.item.placeableItem.consumableItem.spawnItems.VoidWizardSpawnItem;
import necesse.inventory.item.placeableItem.fishingRodItem.DepthsCatcherRodItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.inventory.item.placeableItem.followerSummonItem.petFollowerPlaceableItem.PetFollowerPlaceableItem;
import necesse.inventory.item.placeableItem.mapItem.WorldPresetMapItem;
import necesse.inventory.item.placeableItem.tileItem.GrassSeedItem;
import necesse.inventory.item.placeableItem.tileItem.LandfillItem;
import necesse.inventory.item.questItem.ApprenticeScrollQuestItem;
import necesse.inventory.item.questItem.BabySharkQuestItem;
import necesse.inventory.item.questItem.BabySwordfishQuestItem;
import necesse.inventory.item.questItem.BrokenLimbQuestItem;
import necesse.inventory.item.questItem.CapturedSpiritQuestItem;
import necesse.inventory.item.questItem.CaveOysterQuestItem;
import necesse.inventory.item.questItem.CrabClawQuestItem;
import necesse.inventory.item.questItem.CrawlersFootQuestItem;
import necesse.inventory.item.questItem.DarkGemQuestItem;
import necesse.inventory.item.questItem.DeepSpiritSwabQuestItem;
import necesse.inventory.item.questItem.EnchantedCollarQuestItem;
import necesse.inventory.item.questItem.EyePatchQuestItem;
import necesse.inventory.item.questItem.FakeFangsQuestItem;
import necesse.inventory.item.questItem.FeralTailQuestItem;
import necesse.inventory.item.questItem.FrozenBeardQuestItem;
import necesse.inventory.item.questItem.GoblinRingQuestItem;
import necesse.inventory.item.questItem.MagicSandQuestItem;
import necesse.inventory.item.questItem.MummysBandageQuestItem;
import necesse.inventory.item.questItem.PegLegQuestItem;
import necesse.inventory.item.questItem.RazorIcicleQuestItem;
import necesse.inventory.item.questItem.RumBottleQuestItem;
import necesse.inventory.item.questItem.SandRayQuestItem;
import necesse.inventory.item.questItem.SlimeChunkQuestItem;
import necesse.inventory.item.questItem.SlimeSampleQuestItem;
import necesse.inventory.item.questItem.SlimyLauncherQuestItem;
import necesse.inventory.item.questItem.SoakedBowQuestItem;
import necesse.inventory.item.questItem.SpiderLegQuestItem;
import necesse.inventory.item.questItem.SwampEelQuestItem;
import necesse.inventory.item.questItem.WormToothQuestItem;
import necesse.inventory.item.questItem.ZombieArmQuestItem;
import necesse.inventory.item.toolItem.MultiToolItem;
import necesse.inventory.item.toolItem.axeToolItem.CustomAxeToolItem;
import necesse.inventory.item.toolItem.glaiveToolItem.CryoGlaiveToolItem;
import necesse.inventory.item.toolItem.glaiveToolItem.FrostGlaiveToolItem;
import necesse.inventory.item.toolItem.glaiveToolItem.GoldGlaiveToolItem;
import necesse.inventory.item.toolItem.glaiveToolItem.QuartzGlaiveToolItem;
import necesse.inventory.item.toolItem.glaiveToolItem.SlimeGlaiveToolItem;
import necesse.inventory.item.toolItem.miscToolItem.EraserToolItem;
import necesse.inventory.item.toolItem.miscToolItem.FarmingScytheToolItem;
import necesse.inventory.item.toolItem.miscToolItem.NetToolItem;
import necesse.inventory.item.toolItem.miscToolItem.PipetteItem;
import necesse.inventory.item.toolItem.miscToolItem.SickleToolItem;
import necesse.inventory.item.toolItem.miscToolItem.TestToolItem;
import necesse.inventory.item.toolItem.pickaxeToolItem.CustomPickaxeToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.AntiqueBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.ArachnidWebBowToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.AscendedBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowOfDualismProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.CaptorsShortbowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.CopperBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.DemonicBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.DryadBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.FrostBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.GlacialBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.GoldBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.GoldenArachnidWebBowToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.IronBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.IvyBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.NecroticBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.ReanimationBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.TheCrimsonSkyProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.TungstenBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.VulturesBurstProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.WoodBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.DruidsGreatBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GoldGreatbowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.IvyGreatbowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.MyceliumGreatbowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.NightPiercerGreatBowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.SlimeGreatbowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.TheRavensNestProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.TungstenGreatbowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.VoidGreatbowProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.AntiqueRifleProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.CryoBlasterProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.DeathRipperProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.FlintlockProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.HandCannonProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.HandGunProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.LivingShottyProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.MachineGunProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SapphireRevolverProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SeedGunProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.ShardCannonProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.ShotgunProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SixShooterProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SniperProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SnowLauncherProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.WebbedGunProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.AmethystStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.AncientDredgingStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.AscendedStaffToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.BloodBoltProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.BloodGrimoireProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.BloodVolleyProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.BoulderStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.ChargeBeamProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.ChargeShowerProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.ChromaticSpellbookProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.CryoQuakeProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.DragonLanceProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.DredgingStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.DryadBarrageToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.ElderlyWandProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.EmeraldStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.EmeraldWandProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.FishianHealerStaffToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.FlamelingOrbProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.FrostStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.GenieLampProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.GoldenWebWeaverToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.IcicleStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MouseBeamProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MouseTestProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.NecroticFlaskProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.PhantomPopperProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.QuartzStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.RefractorProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.RubyStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.RuneboundScepterProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.SapphireStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.ShadowBeamProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.ShadowBoltProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.SlimeStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.SparklerProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.SprinklerProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.SwampDwellerStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.SwampTomeProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.TheSoulstormProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.TopazStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.UnlabeledPotionProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.VampiricLampProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.VenomShowerProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.VenomStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.VoidMissileProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.VoidStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.WebWeaverToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.WoodStaffProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.meleeProjectileToolItem.AnchorAndChainToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.meleeProjectileToolItem.BoxingGloveGunToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.meleeProjectileToolItem.HeavyHammerProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.meleeProjectileToolItem.ReaperScytheProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.CarapaceDaggerToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.DynamiteStickToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.IceJavelinToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.IronBombToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.NinjaStarToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.SnowBallToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.TileBombToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.ButchersCleaverBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.ChefsSpecialBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.DragonsReboundToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.FrostBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.GlacialBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.HookBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.NightRazorBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.RazorBladeBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.RollingPinBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.SpiderBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.TungstenBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.VoidBoomerangToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.WoodBoomerangToolItem;
import necesse.inventory.item.toolItem.shovelToolItem.CustomShovelToolItem;
import necesse.inventory.item.toolItem.spearToolItem.CopperPitchforkToolItem;
import necesse.inventory.item.toolItem.spearToolItem.CopperSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.CryoSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.DemonicSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.FrostSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.GoldSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.IronSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.IvySpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.RavenBeakSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.TungstenSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.VoidSpearToolItem;
import necesse.inventory.item.toolItem.spearToolItem.VulturesTalonToolItem;
import necesse.inventory.item.toolItem.spearToolItem.WoodSpearToolItem;
import necesse.inventory.item.toolItem.summonToolItem.BashyBushSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.BrainOnAStickToolItem;
import necesse.inventory.item.toolItem.summonToolItem.CryoStaffSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.CrystallizedSkullSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.DryadBranchSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.EmpressCommandToolItem;
import necesse.inventory.item.toolItem.summonToolItem.EyeOfTheVoidSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.FrostPiercerSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.MagicBranchSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.OrbOfSlimesToolItem;
import necesse.inventory.item.toolItem.summonToolItem.PhantomCallerSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.PirateHookSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.ReapersCallSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.RubyShieldsToolItem;
import necesse.inventory.item.toolItem.summonToolItem.SentientSwordSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.SkeletonStaffToolItem;
import necesse.inventory.item.toolItem.summonToolItem.SlimeCanisterSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.SpiderStaffSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.StabbyBushSummonToolItem;
import necesse.inventory.item.toolItem.summonToolItem.SwampsGraspToolItem;
import necesse.inventory.item.toolItem.summonToolItem.VultureStaffSummonToolItem;
import necesse.inventory.item.toolItem.swordToolItem.AgedChampionSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.AmethystSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.AncestorSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.AncestorWandToolItem;
import necesse.inventory.item.toolItem.swordToolItem.AntiqueSwordSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.BarkBladeSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.BloodClawToolItem;
import necesse.inventory.item.toolItem.swordToolItem.CausticExecutionerToolItem;
import necesse.inventory.item.toolItem.swordToolItem.CopperSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.CutlassSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.DemonicSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.FishianWarriorHookSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.FrostSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.GalvanicHammerToolItem;
import necesse.inventory.item.toolItem.swordToolItem.GemstoneLongswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.GoldSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.GoldenCausticExecutionerToolItem;
import necesse.inventory.item.toolItem.swordToolItem.IronSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.IvySwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.KatanaToolItem;
import necesse.inventory.item.toolItem.swordToolItem.LightningHammerToolItem;
import necesse.inventory.item.toolItem.swordToolItem.MLG1SwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.MLG2SwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.NunchucksToolItem;
import necesse.inventory.item.toolItem.swordToolItem.PerfectStormSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.ReinforcedKatanaToolItem;
import necesse.inventory.item.toolItem.swordToolItem.SandKnifeToolItem;
import necesse.inventory.item.toolItem.swordToolItem.SpiderClawSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.TungstenSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.VenomSlasherToolItem;
import necesse.inventory.item.toolItem.swordToolItem.VoidClawSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.WoodSwordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.BrutesBattleaxeToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.DryadGreatHammerToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.FrostGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GlacialGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.HexedBladeGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.IronGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.IvyGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.NecroticGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.QuartzGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.RavenwingGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.SlimeGreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.WoodGreatswordToolItem;
import necesse.inventory.item.trinketItem.BlinkScepterTrinketItem;
import necesse.inventory.item.trinketItem.CactusShieldTrinketItem;
import necesse.inventory.item.trinketItem.CalmingMinersBouquetTrinketItem;
import necesse.inventory.item.trinketItem.CombinedTrinketItem;
import necesse.inventory.item.trinketItem.CrystalShieldTrinketItem;
import necesse.inventory.item.trinketItem.FoolsGambitTrinketItem;
import necesse.inventory.item.trinketItem.ForceOfWindTrinketItem;
import necesse.inventory.item.trinketItem.GhostBootsTrinketItem;
import necesse.inventory.item.trinketItem.HoverBootsTrinketItem;
import necesse.inventory.item.trinketItem.KineticBootsTrinketItem;
import necesse.inventory.item.trinketItem.LeatherDashersTrinketItem;
import necesse.inventory.item.trinketItem.MinersBouquetTrinketItem;
import necesse.inventory.item.trinketItem.ParryBucklerShieldTrinketItem;
import necesse.inventory.item.trinketItem.ShieldTrinketItem;
import necesse.inventory.item.trinketItem.SimpleTrinketItem;
import necesse.inventory.item.trinketItem.SiphonShieldTrinketItem;
import necesse.inventory.item.trinketItem.VoidPhasingStaffTrinketItem;
import necesse.inventory.item.trinketItem.WindBootsTrinketItem;
import necesse.inventory.item.trinketItem.WoodShieldTrinketItem;
import necesse.inventory.item.trinketItem.ZephyrBootsTrinketItem;
import necesse.inventory.lootTable.presets.IncursionBowWeaponsLootTable;
import necesse.inventory.lootTable.presets.IncursionGreatswordWeaponsLootTable;
import necesse.inventory.lootTable.presets.IncursionMagicWeaponsLootTable;
import necesse.inventory.lootTable.presets.IncursionTrinketsLootTable;
import necesse.inventory.lootTable.presets.ToolsLootTable;
import necesse.inventory.lootTable.presets.TrinketsLootTable;
import necesse.inventory.recipe.RecipeBrokerValueCompute;
import necesse.inventory.recipe.Recipes;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class ItemRegistry
extends GameRegistry<ItemRegistryElement> {
    public static final ItemRegistry instance = new ItemRegistry();
    private static int totalItemsObtainable = 0;
    private static int totalStatItemsObtainable = 0;
    private static int totalItems = 0;
    private static int totalTrinkets = 0;
    public static final int WOOD_TOOL_DPS = 50;
    public static final int COPPER_TOOL_DPS = 65;
    public static final int IRON_TOOL_DPS = 80;
    public static final int GOLD_TOOL_DPS = 95;
    public static final int FROST_TOOL_DPS = 110;
    public static final int DEMONIC_TOOL_DPS = 125;
    public static final int RUNIC_TOOL_DPS = 140;
    public static final int IVY_TOOL_DPS = 155;
    public static final int QUARTZ_TOOL_DPS = 170;
    public static final int TUNGSTEN_TOOL_DPS = 185;
    public static final int GLACIAL_TOOLS_DPS = 200;
    public static final int DRYAD_TOOLS_DPS = 215;
    public static final int MYCELIUM_TOOLS_DPS = 230;
    public static final int ANCIENT_FOSSIl_TOOLS_DPS = 245;
    public static final int FROST_TOOL_TIER = 1;
    public static final int DEMONIC_TOOL_TIER = 2;
    public static final int RUNIC_TOOL_TIER = 3;
    public static final int IVY_TOOL_TIER = 4;
    public static final int QUARTZ_TOOL_TIER = 5;
    public static final int TUNGSTEN_TOOL_TIER = 6;
    public static final int GLACIAL_TOOL_TIER = 7;
    public static final int DRYAD_TOOL_TIER = 8;
    public static final int MYCELIUM_TOOL_TIER = 9;
    public static final int ANCIENT_FOSSIL_TOOL_TIER = 10;
    public static final int EQUIPMENT_VALUE_WOOD = 100;
    public static final int EQUIPMENT_VALUE_COPPER = 200;
    public static final int EQUIPMENT_VALUE_IRON = 300;
    public static final int EQUIPMENT_VALUE_GOLD = 350;
    public static final int EQUIPMENT_VALUE_SOLDIER = 375;
    public static final int EQUIPMENT_VALUE_EVILS_PROTECTOR = 400;
    public static final int EQUIPMENT_VALUE_FROST = 500;
    public static final int EQUIPMENT_VALUE_QUEEN_SPIDER = 550;
    public static final int EQUIPMENT_VALUE_VOID_SHARDS = 650;
    public static final int EQUIPMENT_VALUE_VOID_WIZARD = 700;
    public static final int EQUIPMENT_VALUE_RUNESTONE = 750;
    public static final int EQUIPMENT_VALUE_CHIEFTAIN = 800;
    public static final int EQUIPMENT_VALUE_IVY = 850;
    public static final int EQUIPMENT_VALUE_SWAMP_GUARDIAN = 900;
    public static final int EQUIPMENT_VALUE_QUARTZ = 1000;
    public static final int EQUIPMENT_VALUE_ANCIENT_VULTURE = 1050;
    public static final int EQUIPMENT_VALUE_PIRATE_ISLAND = 1150;
    public static final int EQUIPMENT_VALUE_PIRATE_CAPTAIN = 1200;
    public static final int EQUIPMENT_VALUE_TUNGSTEN = 1300;
    public static final int EQUIPMENT_VALUE_REAPER = 1350;
    public static final int EQUIPMENT_VALUE_GLACIAL = 1450;
    public static final int EQUIPMENT_VALUE_CRYO_QUEEN = 1500;
    public static final int EQUIPMENT_VALUE_DRYAD = 1550;
    public static final int EQUIPMENT_VALUE_CURSED_CRONE = 1575;
    public static final int EQUIPMENT_VALUE_MYCELIUM = 1600;
    public static final int EQUIPMENT_VALUE_PEST_WARDEN = 1650;
    public static final int EQUIPMENT_VALUE_ANCIENT_FOSSIL = 1750;
    public static final int EQUIPMENT_VALUE_SAGE_AND_GRIT = 1800;
    public static final int EQUIPMENT_VALUE_DESERT_TEMPLE = 1850;
    public static final int EQUIPMENT_VALUE_TIERED_ONLY = 1900;
    public static final int EQUIPMENT_VALUE_INCURSIONS = 2000;

    private ItemRegistry() {
        super("Item", 32762);
    }

    /*
     * Opcode count of 14431 triggered aggressive code reduction.  Override with --aggressivesizethreshold.
     */
    @Override
    public void registerCore() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "items"));
        ItemRegistry.registerItem("oaklog", new MatItem(500, "anylog").setItemCategory("materials", "logs"), 2.0f, true);
        ItemRegistry.registerItem("sprucelog", new MatItem(500, "anylog").setItemCategory("materials", "logs"), 2.0f, true);
        ItemRegistry.registerItem("pinelog", new MatItem(500, "anylog").setItemCategory("materials", "logs"), 2.0f, true);
        ItemRegistry.registerItem("palmlog", new MatItem(500, "anylog").setItemCategory("materials", "logs"), 2.0f, true);
        ItemRegistry.registerItem("willowlog", new MatItem(500, "anylog").setItemCategory("materials", "logs"), 2.0f, true);
        ItemRegistry.registerItem("maplelog", new MatItem(500, "anylog").setItemCategory("materials", "logs"), 2.0f, true);
        ItemRegistry.registerItem("birchlog", new MatItem(500, "anylog").setItemCategory("materials", "logs"), 2.0f, true);
        ItemRegistry.registerItem("bamboo", new MatItem(500, "anylog").setItemCategory("materials", "logs"), 2.0f, true);
        ItemRegistry.registerItem("deadwoodlog", new MatItem(500, "anylog").setItemCategory("materials", "logs"), 2.0f, true);
        ItemRegistry.registerItem("dryadlog", new MatItem(500, "anylog").setItemCategory("materials", "logs"), 2.0f, true);
        ItemRegistry.registerItem("stone", new StonePlaceableItem(5000), 0.1f, true);
        ItemRegistry.registerItem("sandstone", new StonePlaceableItem(5000), 0.1f, true);
        ItemRegistry.registerItem("swampstone", new StonePlaceableItem(5000), 0.1f, true);
        ItemRegistry.registerItem("snowstone", new StonePlaceableItem(5000), 0.1f, true);
        ItemRegistry.registerItem("granite", new StonePlaceableItem(5000), 0.1f, true);
        ItemRegistry.registerItem("deepstone", new StonePlaceableItem(5000), 0.1f, true);
        ItemRegistry.registerItem("deepsnowstone", new StonePlaceableItem(5000), 0.1f, true);
        ItemRegistry.registerItem("basalt", new StonePlaceableItem(5000), 0.1f, true);
        ItemRegistry.registerItem("deepswampstone", new StonePlaceableItem(5000), 0.1f, true);
        ItemRegistry.registerItem("deepsandstone", new StonePlaceableItem(5000), 0.1f, true);
        ItemRegistry.registerItem("cryptstone", new StonePlaceableItem(5000), 0.1f, true);
        ItemRegistry.registerItem("spiderstone", new StonePlaceableItem(5000), 0.1f, true);
        ItemRegistry.registerItem("batwing", new MatItem(500, new String[0]).setItemCategory("materials", "mobdrops"), 10.0f, true);
        ItemRegistry.registerItem("wool", new MatItem(500, new String[0]).setItemCategory("materials", "mobdrops"), 7.0f, true);
        ItemRegistry.registerItem("leather", new MatItem(500, new String[0]).setItemCategory("materials", "mobdrops"), 7.0f, true);
        ItemRegistry.registerItem("clothscraps", new MatItem(500, Item.Rarity.COMMON, new String[0]).setItemCategory("materials", "mobdrops"), 10.0f, true);
        ItemRegistry.registerItem("clay", new MatItem(500, new String[0]).setItemCategory("materials", "minerals"), 4.0f, true);
        ItemRegistry.registerItem("copperore", new MatItem(500, new String[0]).setItemCategory("materials", "ore"), 1.0f, true);
        ItemRegistry.registerItem("copperbar", new MatItem(250, new String[0]).setItemCategory("materials", "bars"), 4.0f, true);
        ItemRegistry.registerItem("ironore", new MatItem(500, new String[0]).setItemCategory("materials", "ore"), 1.5f, true);
        ItemRegistry.registerItem("ironbar", new MatItem(250, new String[0]).setItemCategory("materials", "bars"), 6.0f, true);
        ItemRegistry.registerItem("goldore", new MatItem(500, new String[0]).setItemCategory("materials", "ore"), 2.5f, true);
        ItemRegistry.registerItem("goldbar", new MatItem(250, new String[0]).setItemCategory("materials", "bars"), 10.0f, true);
        ItemRegistry.registerItem("frostshard", new MatItem(250, Item.Rarity.COMMON, new String[0]).setItemCategory("materials", "minerals"), 10.0f, true);
        ItemRegistry.registerItem("runestone", new MatItem(250, Item.Rarity.COMMON, new String[0]).setItemCategory("materials", "minerals"), 10.0f, true);
        ItemRegistry.registerItem("ivyore", new MatItem(500, Item.Rarity.COMMON, new String[0]).setItemCategory("materials", "ore"), 3.0f, true);
        ItemRegistry.registerItem("ivybar", new MatItem(250, Item.Rarity.COMMON, new String[0]).setItemCategory("materials", "bars"), 12.0f, true);
        ItemRegistry.registerItem("quartz", new MatItem(250, Item.Rarity.COMMON, new String[0]).setItemCategory("materials", "minerals"), 15.0f, true);
        ItemRegistry.registerItem("lifequartz", new MatItem(250, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "minerals"), 20.0f, true);
        ItemRegistry.registerItem("demonicbar", new MatItem(250, Item.Rarity.COMMON, new String[0]).setItemCategory("materials", "bars"), 10.0f, true);
        ItemRegistry.registerItem("tungstenore", new MatItem(500, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "ore"), 6.0f, true);
        ItemRegistry.registerItem("tungstenbar", new MatItem(250, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "bars"), 20.0f, true);
        ItemRegistry.registerItem("glacialore", new MatItem(500, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "ore"), 6.0f, true);
        ItemRegistry.registerItem("glacialbar", new MatItem(250, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "bars"), 20.0f, true);
        ItemRegistry.registerItem("amber", new MatItem(250, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "minerals"), 20.0f, true);
        ItemRegistry.registerItem("myceliumore", new MatItem(500, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "ore"), 6.0f, true);
        ItemRegistry.registerItem("myceliumbar", new MatItem(250, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "bars"), 20.0f, true);
        ItemRegistry.registerItem("ancientfossilore", new MatItem(500, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "ore"), 6.0f, true);
        ItemRegistry.registerItem("ancientfossilbar", new MatItem(250, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "bars"), 20.0f, true);
        ItemRegistry.registerItem("obsidian", new MatItem(250, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "minerals"), 2.0f, true);
        ItemRegistry.registerItem("amethyst", new MatItem(250, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "minerals"), 25.0f, true);
        ItemRegistry.registerItem("sapphire", new MatItem(250, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "minerals"), 25.0f, true);
        ItemRegistry.registerItem("emerald", new MatItem(250, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "minerals"), 25.0f, true);
        ItemRegistry.registerItem("ruby", new MatItem(250, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "minerals"), 25.0f, true);
        ItemRegistry.registerItem("topaz", new MatItem(250, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "minerals"), 25.0f, true);
        ItemRegistry.registerItem("slimeum", new MatItem(500, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "minerals"), 10.0f, true);
        ItemRegistry.registerItem("nightsteelore", new MatItem(500, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "ore"), 8.0f, true);
        ItemRegistry.registerItem("nightsteelbar", new MatItem(250, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "bars"), 24.0f, true);
        ItemRegistry.registerItem("spideriteore", new MatItem(500, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "ore"), 10.0f, true);
        ItemRegistry.registerItem("spideritebar", new MatItem(250, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "bars"), 28.0f, true);
        ItemRegistry.registerItem("pearlescentdiamond", new MatItem(250, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "bars"), 35.0f, true);
        ItemRegistry.registerItem("cavespidergland", new MultiTextureMatItem(3, 100, Item.Rarity.COMMON, new String[0]).setItemCategory("materials", "mobdrops"), 15.0f, true);
        ItemRegistry.registerItem("voidshard", new MatItem(500, Item.Rarity.UNCOMMON, "voidshardtip").setItemCategory("materials", "mobdrops"), 10.0f, true);
        ItemRegistry.registerItem("swampsludge", new MatItem(500, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "mobdrops"), 10.0f, true);
        ItemRegistry.registerItem("bone", new MatItem(500, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "mobdrops"), 8.0f, true);
        ItemRegistry.registerItem("ectoplasm", new MatItem(500, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "mobdrops"), 12.0f, true);
        ItemRegistry.registerItem("glacialshard", new MatItem(500, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "mobdrops"), 12.0f, true);
        ItemRegistry.registerItem("silk", new MatItem(500, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "mobdrops"), 12.0f, true);
        ItemRegistry.registerItem("wormcarapace", new MatItem(500, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "mobdrops"), 12.0f, true);
        ItemRegistry.registerItem("phantomdust", new MatItem(500, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "mobdrops"), 14.0f, true);
        ItemRegistry.registerItem("glass", new MatItem(500, new String[0]), 1.0f, true);
        ItemRegistry.registerItem("glassbottle", new MatItem(500, Item.Rarity.NORMAL, "glassbottletip"), 1.0f, true);
        ItemRegistry.registerItem("book", new BookMatItem(), 15.0f, true);
        ItemRegistry.registerItem("slimematter", new MatItem(500, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "mobdrops"), 15.0f, true);
        ItemRegistry.registerItem("spidervenom", new MatItem(500, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "mobdrops"), 15.0f, true);
        ItemRegistry.registerItem("omnicrystal", new MatItem(500, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "mobdrops"), 15.0f, true);
        ItemRegistry.registerItem("altardust", new MatItem(5000, Item.Rarity.EPIC, new String[0]).setItemCategory("materials", "mobdrops"), 5.0f, true);
        ItemRegistry.registerItem("ravenfeather", new MatItem(500, Item.Rarity.EPIC, new String[0]).setItemCategory("materials", "mobdrops"), 15.0f, true);
        ItemRegistry.registerItem("electrifiedmana", new MatItem(500, Item.Rarity.EPIC, new String[0]).setItemCategory("materials", "mobdrops"), 15.0f, true);
        ItemRegistry.registerItem("alchemyshard", new MatItem(1000, Item.Rarity.RARE, "alchemyshardtip").setItemCategory("materials", "minerals"), 8.0f, true);
        ItemRegistry.registerItem("upgradeshard", new MatItem(1000, Item.Rarity.RARE, "upgradeshardtip").setItemCategory("materials", "minerals"), 8.0f, true);
        ItemRegistry.registerItem("ascendedshard", new AscendedShardItem(), 100.0f, true);
        ItemRegistry.registerItem("shadowessence", new EssenceMatItem(250, Item.Rarity.RARE, 1), 25.0f, true);
        ItemRegistry.registerItem("cryoessence", new EssenceMatItem(250, Item.Rarity.RARE, 1), 25.0f, true);
        ItemRegistry.registerItem("bioessence", new EssenceMatItem(250, Item.Rarity.RARE, 1), 25.0f, true);
        ItemRegistry.registerItem("primordialessence", new EssenceMatItem(250, Item.Rarity.RARE, 1), 25.0f, true);
        ItemRegistry.registerItem("slimeessence", new EssenceMatItem(250, Item.Rarity.EPIC, 2), 30.0f, true);
        ItemRegistry.registerItem("bloodessence", new EssenceMatItem(250, Item.Rarity.EPIC, 2), 30.0f, true);
        ItemRegistry.registerItem("spideressence", new EssenceMatItem(250, Item.Rarity.EPIC, 2), 30.0f, true);
        ItemRegistry.registerItem("mapfragment", new MatItem(250, Item.Rarity.UNCOMMON, "mapfragmenttip").setItemCategory("materials", "mobdrops"), 30.0f, true);
        ItemRegistry.registerItem("spareboatparts", new MatItem(1, Item.Rarity.RARE, "sparepartstip"), 250.0f, true);
        ItemRegistry.registerItem("brokencoppertool", new MultiTextureMatItem(4, 100, Item.Rarity.COMMON, "brokentooltip").setItemCategory("materials", "mobdrops"), 20.0f, true);
        ItemRegistry.registerItem("brokenirontool", new MultiTextureMatItem(4, 100, Item.Rarity.COMMON, "brokentooltip").setItemCategory("materials", "mobdrops"), 30.0f, true);
        ItemRegistry.registerItem("woodpickaxe", new CustomPickaxeToolItem(500, 50, 0.0f, 10, 50, 50, 100, ToolsLootTable.tools), 8.0f, true);
        ItemRegistry.registerItem("copperpickaxe", new CustomPickaxeToolItem(500, 65, 0.0f, 12, 50, 50, 200, ToolsLootTable.tools), 17.0f, true);
        ItemRegistry.registerItem("ironpickaxe", new CustomPickaxeToolItem(500, 80, 0.0f, 14, 50, 50, 400, ToolsLootTable.tools), 25.0f, true);
        ItemRegistry.registerItem("goldpickaxe", new CustomPickaxeToolItem(500, 95, 0.0f, 16, 50, 50, 450, ToolsLootTable.tools), 40.0f, true);
        ItemRegistry.registerItem("frostpickaxe", new CustomPickaxeToolItem(500, 110, 1.0f, 18, 50, 50, 500, ToolsLootTable.tools){

            @Override
            public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
                ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
                tooltips.add(Localization.translate("itemtooltip", "frosttooltip"), 350);
                return tooltips;
            }
        }, 60.0f, true);
        ItemRegistry.registerItem("demonicpickaxe", new CustomPickaxeToolItem(500, 125, 2.0f, 20, 50, 50, 600, ToolsLootTable.tools, Item.Rarity.COMMON){

            @Override
            public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
                ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
                tooltips.add(Localization.translate("itemtooltip", "demonictooltipnew"), 350);
                return tooltips;
            }
        }, 80.0f, true);
        ItemRegistry.registerItem("runicpickaxe", new CustomPickaxeToolItem(500, 140, 3.0f, 22, 50, 50, 700, ToolsLootTable.tools, Item.Rarity.COMMON){

            @Override
            public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
                ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
                tooltips.add(Localization.translate("itemtooltip", "runictooltip"), 350);
                return tooltips;
            }
        }, 100.0f, true);
        ItemRegistry.registerItem("ivypickaxe", new CustomPickaxeToolItem(450, 155, 4.0f, 24, 50, 50, 700, ToolsLootTable.tools, Item.Rarity.COMMON){

            @Override
            public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
                ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
                tooltips.add(Localization.translate("itemtooltip", "ivytooltip"), 350);
                return tooltips;
            }
        }, 100.0f, true);
        ItemRegistry.registerItem("quartzpickaxe", new CustomPickaxeToolItem(450, 170, 5.0f, 26, 50, 50, 700, ToolsLootTable.tools, Item.Rarity.COMMON){

            @Override
            public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
                ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
                tooltips.add(Localization.translate("itemtooltip", "quartztooltip"), 350);
                return tooltips;
            }
        }, 100.0f, true);
        ItemRegistry.registerItem("tungstenpickaxe", new CustomPickaxeToolItem(400, 185, 6.0f, 28, 50, 50, 800, ToolsLootTable.tools, Item.Rarity.UNCOMMON){

            @Override
            public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
                ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
                tooltips.add(Localization.translate("itemtooltip", "tungstentooltip"), 350);
                return tooltips;
            }
        }, 160.0f, true);
        ItemRegistry.registerItem("glacialpickaxe", new CustomPickaxeToolItem(400, 200, 7.0f, 30, 50, 50, 900, ToolsLootTable.tools, Item.Rarity.UNCOMMON){

            @Override
            public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
                ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
                tooltips.add(Localization.translate("itemtooltip", "glacialtooltipnew"), 350);
                return tooltips;
            }
        }, 160.0f, true);
        ItemRegistry.registerItem("icepickaxe", new CustomPickaxeToolItem(400, 245, 10.0f, 24, 50, 50, 1000, ToolsLootTable.tools, Item.Rarity.RARE, 1), 400.0f, true);
        ItemRegistry.registerItem("dryadpickaxe", new CustomPickaxeToolItem(400, 215, 8.0f, 32, 50, 50, 950, ToolsLootTable.tools, Item.Rarity.UNCOMMON){

            @Override
            public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
                ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
                tooltips.add(Localization.translate("itemtooltip", "dryadtooltip"), 350);
                return tooltips;
            }
        }, 160.0f, true);
        ItemRegistry.registerItem("myceliumpickaxe", new CustomPickaxeToolItem(400, 230, 9.0f, 34, 50, 50, 1000, ToolsLootTable.tools, Item.Rarity.UNCOMMON){

            @Override
            public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
                ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
                tooltips.add(Localization.translate("itemtooltip", "myceliumtooltip"), 350);
                return tooltips;
            }
        }, 160.0f, true);
        ItemRegistry.registerItem("ancientfossilpickaxe", new CustomPickaxeToolItem(400, 245, 10.0f, 36, 50, 50, 1000, ToolsLootTable.tools, Item.Rarity.UNCOMMON), 160.0f, true);
        ItemRegistry.registerItem("woodaxe", new CustomAxeToolItem(500, 50, 0.0f, 10, 50, 50, 100, ToolsLootTable.tools), 8.0f, true);
        ItemRegistry.registerItem("copperaxe", new CustomAxeToolItem(500, 65, 0.0f, 12, 50, 50, 200, ToolsLootTable.tools), 17.0f, true);
        ItemRegistry.registerItem("ironaxe", new CustomAxeToolItem(500, 80, 0.0f, 14, 50, 50, 300, ToolsLootTable.tools), 25.0f, true);
        ItemRegistry.registerItem("goldaxe", new CustomAxeToolItem(500, 95, 0.0f, 15, 50, 50, 400, ToolsLootTable.tools), 40.0f, true);
        ItemRegistry.registerItem("frostaxe", new CustomAxeToolItem(500, 110, 0.0f, 16, 50, 50, 500, ToolsLootTable.tools), 60.0f, true);
        ItemRegistry.registerItem("demonicaxe", new CustomAxeToolItem(500, 125, 2.0f, 17, 50, 50, 600, ToolsLootTable.tools, Item.Rarity.COMMON), 80.0f, true);
        ItemRegistry.registerItem("runicaxe", new CustomAxeToolItem(500, 140, 3.0f, 18, 50, 50, 700, ToolsLootTable.tools, Item.Rarity.COMMON), 80.0f, true);
        ItemRegistry.registerItem("ivyaxe", new CustomAxeToolItem(450, 155, 2.0f, 19, 50, 50, 800, ToolsLootTable.tools, Item.Rarity.COMMON), 100.0f, true);
        ItemRegistry.registerItem("quartzaxe", new CustomAxeToolItem(450, 170, 5.0f, 20, 50, 50, 900, ToolsLootTable.tools, Item.Rarity.COMMON), 100.0f, true);
        ItemRegistry.registerItem("tungstenaxe", new CustomAxeToolItem(400, 185, 6.0f, 22, 50, 50, 1000, ToolsLootTable.tools, Item.Rarity.UNCOMMON), 160.0f, true);
        ItemRegistry.registerItem("glacialaxe", new CustomAxeToolItem(400, 200, 7.0f, 24, 50, 50, 1100, ToolsLootTable.tools, Item.Rarity.UNCOMMON), 160.0f, true);
        ItemRegistry.registerItem("dryadaxe", new CustomAxeToolItem(400, 215, 8.0f, 26, 50, 50, 1200, ToolsLootTable.tools, Item.Rarity.UNCOMMON), 160.0f, true);
        ItemRegistry.registerItem("myceliumaxe", new CustomAxeToolItem(400, 230, 9.0f, 28, 50, 50, 1300, ToolsLootTable.tools, Item.Rarity.UNCOMMON), 160.0f, true);
        ItemRegistry.registerItem("ancientfossilaxe", new CustomAxeToolItem(400, 245, 10.0f, 30, 50, 50, 1400, ToolsLootTable.tools, Item.Rarity.UNCOMMON), 160.0f, true);
        ItemRegistry.registerItem("woodshovel", new CustomShovelToolItem(500, 50, 0.0f, 10, 50, 50, 100), 8.0f, true);
        ItemRegistry.registerItem("coppershovel", new CustomShovelToolItem(500, 65, 0.0f, 12, 50, 50, 200), 17.0f, true);
        ItemRegistry.registerItem("ironshovel", new CustomShovelToolItem(500, 80, 0.0f, 14, 50, 50, 300), 25.0f, true);
        ItemRegistry.registerItem("goldshovel", new CustomShovelToolItem(500, 95, 0.0f, 15, 50, 50, 400), 40.0f, true);
        ItemRegistry.registerItem("frostshovel", new CustomShovelToolItem(500, 110, 0.0f, 16, 50, 50, 500), 60.0f, true);
        ItemRegistry.registerItem("demonicshovel", new CustomShovelToolItem(500, 125, 2.0f, 17, 50, 50, 600, Item.Rarity.COMMON), 80.0f, true);
        ItemRegistry.registerItem("runicshovel", new CustomShovelToolItem(500, 140, 3.0f, 18, 50, 50, 700, Item.Rarity.COMMON), 80.0f, true);
        ItemRegistry.registerItem("ivyshovel", new CustomShovelToolItem(450, 155, 2.0f, 19, 50, 50, 800, Item.Rarity.COMMON), 100.0f, true);
        ItemRegistry.registerItem("quartzshovel", new CustomShovelToolItem(450, 170, 5.0f, 20, 50, 50, 900, Item.Rarity.COMMON), 100.0f, true);
        ItemRegistry.registerItem("tungstenshovel", new CustomShovelToolItem(400, 185, 6.0f, 22, 50, 50, 1000, Item.Rarity.UNCOMMON), 160.0f, true);
        ItemRegistry.registerItem("glacialshovel", new CustomShovelToolItem(400, 200, 7.0f, 24, 50, 50, 1100, Item.Rarity.UNCOMMON), 160.0f, true);
        ItemRegistry.registerItem("dryadshovel", new CustomShovelToolItem(400, 215, 8.0f, 26, 50, 50, 1200, Item.Rarity.UNCOMMON), 160.0f, true);
        ItemRegistry.registerItem("myceliumshovel", new CustomShovelToolItem(400, 230, 9.0f, 28, 50, 50, 1300, Item.Rarity.UNCOMMON), 160.0f, true);
        ItemRegistry.registerItem("ancientfossilshovel", new CustomShovelToolItem(400, 245, 10.0f, 30, 50, 50, 1400, Item.Rarity.UNCOMMON), 160.0f, true);
        ItemRegistry.registerItem("godrod", new FishingRodItem(100, 40, 35, 400, 5000, 5, 30, 0, Item.Rarity.LEGENDARY), 0.0f, false);
        ItemRegistry.registerItem("woodfishingrod", new FishingRodItem(5, 37, 30, Item.Rarity.NORMAL), 20.0f, true);
        ItemRegistry.registerItem("ironfishingrod", new FishingRodItem(15, 37, 30, Item.Rarity.NORMAL), 150.0f, true);
        ItemRegistry.registerItem("overgrownfishingrod", new FishingRodItem(25, 38, 14, Item.Rarity.COMMON), 200.0f, true);
        ItemRegistry.registerItem("goldfishingrod", new FishingRodItem(30, 37, 30, Item.Rarity.COMMON), 250.0f, true);
        ItemRegistry.registerItem("depthscatcher", new DepthsCatcherRodItem(), 400.0f, true);
        ItemRegistry.registerItem("godbait", new BaitItem(false, 100), 0.0f, false);
        ItemRegistry.registerItem("wormbait", new BaitItem(true, 10), 2.0f, true);
        ItemRegistry.registerItem("swamplarva", new BaitItem(true, 20), 3.0f, true);
        ItemRegistry.registerItem("anglersbait", new BaitItem(false, 25), 5.0f, true);
        ItemRegistry.registerItem("cheattool", new TestToolItem(), 0.0f, false);
        ItemRegistry.registerItem("sickle", new SickleToolItem(), 50.0f, true);
        ItemRegistry.registerItem("farmingscythe", new FarmingScytheToolItem(), 550.0f, true);
        ItemRegistry.registerItem("net", new NetToolItem(), 40.0f, true);
        ItemRegistry.registerItem("wrench", new WrenchPlaceableItem(), 50.0f, true);
        ItemRegistry.registerItem("cutter", new CutterPlaceableItem(), 50.0f, true);
        ItemRegistry.registerItem("wire", new MatItem(1000, Item.Rarity.NORMAL, "wiretip").setItemCategory("wiring").setItemCategory(ItemCategory.craftingManager, "wiring"), 0.1f, true);
        ItemRegistry.registerItem("multitool", new MultiToolItem(1000), 50.0f, false);
        ItemRegistry.registerItem("eraser", (Item)new EraserToolItem(), 50.0f, false, false, true, new String[0]);
        ItemRegistry.registerItem("pipette", (Item)new PipetteItem(), 50.0f, false, false, true, new String[0]);
        ItemRegistry.registerItem("woodsword", new WoodSwordToolItem(), 10.0f, true);
        ItemRegistry.registerItem("coppersword", new CopperSwordToolItem(), 25.0f, true);
        ItemRegistry.registerItem("ironsword", new IronSwordToolItem(), 40.0f, true);
        ItemRegistry.registerItem("goldsword", new GoldSwordToolItem(), 50.0f, true);
        ItemRegistry.registerItem("nunchucks", new NunchucksToolItem(), 50.0f, true);
        ItemRegistry.registerItem("katana", new KatanaToolItem(), 50.0f, true);
        ItemRegistry.registerItem("heavyhammer", new HeavyHammerProjectileToolItem(), 120.0f, true);
        ItemRegistry.registerItem("frostsword", new FrostSwordToolItem(), 80.0f, true);
        ItemRegistry.registerItem("demonicsword", new DemonicSwordToolItem(), 120.0f, true);
        ItemRegistry.registerItem("lightninghammer", new LightningHammerToolItem(), 150.0f, true);
        ItemRegistry.registerItem("galvanichammer", new GalvanicHammerToolItem(), 250.0f, true);
        ItemRegistry.registerItem("spiderclaw", new SpiderClawSwordToolItem(), 300.0f, true);
        ItemRegistry.registerItem("ivysword", new IvySwordToolItem(), 110.0f, true);
        ItemRegistry.registerItem("amethystsword", new AmethystSwordToolItem(), 350.0f, true);
        ItemRegistry.registerItem("cutlass", new CutlassSwordToolItem(), 500.0f, true);
        ItemRegistry.registerItem("reinforcedkatana", new ReinforcedKatanaToolItem(), 300.0f, true);
        ItemRegistry.registerItem("tungstensword", new TungstenSwordToolItem(), 200.0f, true);
        ItemRegistry.registerItem("reaperscythe", new ReaperScytheProjectileToolItem(), 650.0f, true);
        ItemRegistry.registerItem("barkblade", new BarkBladeSwordToolItem(), 700.0f, true);
        ItemRegistry.registerItem("agedchampionsword", new AgedChampionSwordToolItem(), 800.0f, true);
        ItemRegistry.registerItem("venomslasher", new VenomSlasherToolItem(), 750.0f, true);
        ItemRegistry.registerItem("sandknife", new SandKnifeToolItem(), 450.0f, true);
        ItemRegistry.registerItem("antiquesword", new AntiqueSwordSwordToolItem(), 800.0f, true);
        ItemRegistry.registerItem("bloodclaw", new BloodClawToolItem(), 750.0f, true);
        ItemRegistry.registerItem("causticexecutioner", new CausticExecutionerToolItem(IncursionGreatswordWeaponsLootTable.incursionGreatswordWeapons), 850.0f, true);
        ItemRegistry.registerItem("gemstonelongsword", new GemstoneLongswordToolItem(), 1100.0f, true);
        ItemRegistry.registerItem("perfectstorm", new PerfectStormSwordToolItem(), 450.0f, true);
        ItemRegistry.registerItem("irongreatsword", new IronGreatswordToolItem(), 60.0f, true);
        ItemRegistry.registerItem("frostgreatsword", new FrostGreatswordToolItem(), 120.0f, true);
        ItemRegistry.registerItem("brutesbattleaxe", new BrutesBattleaxeToolItem(), 350.0f, true);
        ItemRegistry.registerItem("ivygreatsword", new IvyGreatswordToolItem(), 160.0f, true);
        ItemRegistry.registerItem("necroticgreatsword", new NecroticGreatswordToolItem(), 500.0f, true);
        ItemRegistry.registerItem("hexedbladegreatsword", new HexedBladeGreatswordToolItem(), 800.0f, true);
        ItemRegistry.registerItem("quartzgreatsword", new QuartzGreatswordToolItem(), 220.0f, true);
        ItemRegistry.registerItem("glacialgreatsword", new GlacialGreatswordToolItem(), 280.0f, true);
        ItemRegistry.registerItem("dryadgreathammer", new DryadGreatHammerToolItem(), 280.0f, true);
        ItemRegistry.registerItem("slimegreatsword", new SlimeGreatswordToolItem(), 400.0f, true);
        ItemRegistry.registerItem("ravenwinggreatsword", new RavenwingGreatswordToolItem(), 450.0f, true);
        ItemRegistry.registerItem("voidclaw", new VoidClawSwordToolItem(), 2000.0f, true);
        ItemRegistry.registerItem("woodspear", new WoodSpearToolItem(), 15.0f, true);
        ItemRegistry.registerItem("copperpitchfork", new CopperPitchforkToolItem(), 60.0f, true);
        ItemRegistry.registerItem("copperspear", new CopperSpearToolItem(), 45.0f, true);
        ItemRegistry.registerItem("ironspear", new IronSpearToolItem(), 75.0f, true);
        ItemRegistry.registerItem("goldspear", new GoldSpearToolItem(), 90.0f, true);
        ItemRegistry.registerItem("frostspear", new FrostSpearToolItem(), 100.0f, true);
        ItemRegistry.registerItem("demonicspear", new DemonicSpearToolItem(), 120.0f, true);
        ItemRegistry.registerItem("voidspear", new VoidSpearToolItem(), 150.0f, true);
        ItemRegistry.registerItem("ivyspear", new IvySpearToolItem(), 130.0f, true);
        ItemRegistry.registerItem("vulturestalon", new VulturesTalonToolItem(), 300.0f, true);
        ItemRegistry.registerItem("tungstenspear", new TungstenSpearToolItem(), 200.0f, true);
        ItemRegistry.registerItem("cryospear", new CryoSpearToolItem(), 650.0f, true);
        ItemRegistry.registerItem("ravenbeakspear", new RavenBeakSpearToolItem(), 800.0f, true);
        ItemRegistry.registerItem("goldglaive", new GoldGlaiveToolItem(), 100.0f, true);
        ItemRegistry.registerItem("frostglaive", new FrostGlaiveToolItem(), 120.0f, true);
        ItemRegistry.registerItem("quartzglaive", new QuartzGlaiveToolItem(), 200.0f, true);
        ItemRegistry.registerItem("cryoglaive", new CryoGlaiveToolItem(), 300.0f, true);
        ItemRegistry.registerItem("slimeglaive", new SlimeGlaiveToolItem(), 400.0f, true);
        ItemRegistry.registerItem("woodbow", new WoodBowProjectileToolItem(), 12.0f, true);
        ItemRegistry.registerItem("copperbow", new CopperBowProjectileToolItem(), 25.0f, true);
        ItemRegistry.registerItem("ironbow", new IronBowProjectileToolItem(), 40.0f, true);
        ItemRegistry.registerItem("goldbow", new GoldBowProjectileToolItem(), 60.0f, true);
        ItemRegistry.registerItem("frostbow", new FrostBowProjectileToolItem(), 80.0f, true);
        ItemRegistry.registerItem("demonicbow", new DemonicBowProjectileToolItem(), 100.0f, true);
        ItemRegistry.registerItem("captorsshortbow", new CaptorsShortbowProjectileToolItem(), 350.0f, true);
        ItemRegistry.registerItem("ivybow", new IvyBowProjectileToolItem(), 110.0f, true);
        ItemRegistry.registerItem("vulturesburst", new VulturesBurstProjectileToolItem(), 300.0f, true);
        ItemRegistry.registerItem("tungstenbow", new TungstenBowProjectileToolItem(), 200.0f, true);
        ItemRegistry.registerItem("glacialbow", new GlacialBowProjectileToolItem(), 200.0f, true);
        ItemRegistry.registerItem("dryadbow", new DryadBowProjectileToolItem(), 340.0f, true);
        ItemRegistry.registerItem("bowofdualism", new BowOfDualismProjectileToolItem(), 700.0f, true);
        ItemRegistry.registerItem("antiquebow", new AntiqueBowProjectileToolItem(), 800.0f, true);
        ItemRegistry.registerItem("thecrimsonsky", new TheCrimsonSkyProjectileToolItem(), 400.0f, true);
        ItemRegistry.registerItem("arachnidwebbow", new ArachnidWebBowToolItem(IncursionBowWeaponsLootTable.incursionBowWeapons), 850.0f, true);
        ItemRegistry.registerItem("ascendedbow", new AscendedBowProjectileToolItem(), 2000.0f, true);
        ItemRegistry.registerItem("goldgreatbow", new GoldGreatbowProjectileToolItem(), 80.0f, true);
        ItemRegistry.registerItem("voidgreatbow", new VoidGreatbowProjectileToolItem(), 120.0f, true);
        ItemRegistry.registerItem("ivygreatbow", new IvyGreatbowProjectileToolItem(), 140.0f, true);
        ItemRegistry.registerItem("tungstengreatbow", new TungstenGreatbowProjectileToolItem(), 250.0f, true);
        ItemRegistry.registerItem("myceliumgreatbow", new MyceliumGreatbowProjectileToolItem(), 300.0f, true);
        ItemRegistry.registerItem("druidsgreatbow", new DruidsGreatBowProjectileToolItem(), 800.0f, true);
        ItemRegistry.registerItem("slimegreatbow", new SlimeGreatbowProjectileToolItem(), 400.0f, true);
        ItemRegistry.registerItem("nightpiercer", new NightPiercerGreatBowProjectileToolItem(), 400.0f, true);
        ItemRegistry.registerItem("theravensnest", new TheRavensNestProjectileToolItem(), 475.0f, true);
        ItemRegistry.registerItem("stonearrow", new StoneArrowItem(), 0.2f, true);
        ItemRegistry.registerItem("firearrow", new FireArrowItem(), 0.2f, true);
        ItemRegistry.registerItem("frostarrow", new FrostArrowItem(), 0.2f, true);
        ItemRegistry.registerItem("poisonarrow", new PoisonArrowItem(), 0.2f, true);
        ItemRegistry.registerItem("bouncingarrow", new BouncingArrowItem(), 0.5f, true);
        ItemRegistry.registerItem("ironarrow", new IronArrowItem(), 0.8f, true);
        ItemRegistry.registerItem("bonearrow", new BoneArrowItem(), 1.2f, true);
        ItemRegistry.registerItem("spideritearrow", new SpideriteArrowItem(), 2.0f, true);
        ItemRegistry.registerItem("handgun", new HandGunProjectileToolItem(), 150.0f, true);
        ItemRegistry.registerItem("sapphirerevolver", new SapphireRevolverProjectileToolItem(), 350.0f, true);
        ItemRegistry.registerItem("webbedgun", new WebbedGunProjectileToolItem(), 300.0f, true);
        ItemRegistry.registerItem("machinegun", new MachineGunProjectileToolItem(), 400.0f, true);
        ItemRegistry.registerItem("shotgun", new ShotgunProjectileToolItem(), 450.0f, true);
        ItemRegistry.registerItem("sniperrifle", new SniperProjectileToolItem(), 450.0f, true);
        ItemRegistry.registerItem("sixshooter", new SixShooterProjectileToolItem(), 450.0f, true);
        ItemRegistry.registerItem("flintlock", new FlintlockProjectileToolItem(), 500.0f, true);
        ItemRegistry.registerItem("handcannon", new HandCannonProjectileToolItem(), 500.0f, true);
        ItemRegistry.registerItem("seedgun", new SeedGunProjectileToolItem(), 600.0f, true);
        ItemRegistry.registerItem("deathripper", new DeathRipperProjectileToolItem(), 600.0f, true);
        ItemRegistry.registerItem("cryoblaster", new CryoBlasterProjectileToolItem(), 650.0f, true);
        ItemRegistry.registerItem("livingshotty", new LivingShottyProjectileToolItem(), 750.0f, true);
        ItemRegistry.registerItem("antiquerifle", new AntiqueRifleProjectileToolItem(), 800.0f, true);
        ItemRegistry.registerItem("shardcannon", new ShardCannonProjectileToolItem(), 1500.0f, true);
        ItemRegistry.registerItem("snowlauncher", (Item)new SnowLauncherProjectileToolItem(), 425.0f, true, false, new String[0]);
        ItemRegistry.registerItem("simplebullet", new SimpleBulletItem(), 0.1f, true);
        ItemRegistry.registerItem("frostbullet", new FrostBulletItem(), 0.1f, true);
        ItemRegistry.registerItem("bouncingbullet", new BouncingBulletItem(), 0.1f, true);
        ItemRegistry.registerItem("voidbullet", new VoidBulletItem(), 0.1f, true);
        ItemRegistry.registerItem("cannonball", new CannonballAmmoItem(), 2.0f, true);
        ItemRegistry.registerItem("crystalbullet", new CrystalBulletItem(), 0.1f, true);
        ItemRegistry.registerItem("seedbullet", new SeedBulletItem(), 0.0f, false);
        ItemRegistry.registerItem("woodstaff", new WoodStaffProjectileToolItem(), 30.0f, true);
        ItemRegistry.registerItem("sparkler", new SparklerProjectileToolItem(), 40.0f, true);
        ItemRegistry.registerItem("sprinkler", new SprinklerProjectileToolItem(), 60.0f, true);
        ItemRegistry.registerItem("bloodbolt", new BloodBoltProjectileToolItem(), 60.0f, true);
        ItemRegistry.registerItem("venomstaff", new VenomStaffProjectileToolItem(), 100.0f, true);
        ItemRegistry.registerItem("froststaff", new FrostStaffProjectileToolItem(), 150.0f, true);
        ItemRegistry.registerItem("sapphirestaff", new SapphireStaffProjectileToolItem(), 150.0f, true);
        ItemRegistry.registerItem("bloodvolley", new BloodVolleyProjectileToolItem(), 150.0f, true);
        ItemRegistry.registerItem("voidstaff", new VoidStaffProjectileToolItem(), 200.0f, true);
        ItemRegistry.registerItem("voidmissile", new VoidMissileProjectileToolItem(), 400.0f, true);
        ItemRegistry.registerItem("runeboundscepter", new RuneboundScepterProjectileToolItem(), 425.0f, true);
        ItemRegistry.registerItem("swamptome", new SwampTomeProjectileToolItem(), 500.0f, true);
        ItemRegistry.registerItem("necroticflask", new NecroticFlaskProjectileToolItem(), 600.0f, true);
        ItemRegistry.registerItem("unlabeledpotion", new UnlabeledPotionProjectileToolItem(), 600.0f, true);
        ItemRegistry.registerItem("boulderstaff", new BoulderStaffProjectileToolItem(), 400.0f, true);
        ItemRegistry.registerItem("dredgingstaff", new DredgingStaffProjectileToolItem(), 500.0f, true);
        ItemRegistry.registerItem("quartzstaff", new QuartzStaffProjectileToolItem(), 200.0f, true);
        ItemRegistry.registerItem("amethyststaff", new AmethystStaffProjectileToolItem(), 325.0f, true);
        ItemRegistry.registerItem("genielamp", new GenieLampProjectileToolItem(), 600.0f, true);
        ItemRegistry.registerItem("vampiriclamp", new VampiricLampProjectileToolItem(), 600.0f, true);
        ItemRegistry.registerItem("elderlywand", new ElderlyWandProjectileToolItem(), 600.0f, true);
        ItemRegistry.registerItem("shadowbolt", new ShadowBoltProjectileToolItem(), 100.0f, true);
        ItemRegistry.registerItem("chromaticspellbook", new ChromaticSpellbookProjectileToolItem(), 100.0f, true);
        ItemRegistry.registerItem("rubystaff", new RubyStaffProjectileToolItem(), 380.0f, true);
        ItemRegistry.registerItem("shadowbeam", new ShadowBeamProjectileToolItem(), 650.0f, true);
        ItemRegistry.registerItem("iciclestaff", new IcicleStaffProjectileToolItem(), 260.0f, true);
        ItemRegistry.registerItem("cryoquake", new CryoQuakeProjectileToolItem(), 700.0f, true);
        ItemRegistry.registerItem("dryadbarrage", new DryadBarrageToolItem(), 340.0f, true);
        ItemRegistry.registerItem("thesoulstorm", new TheSoulstormProjectileToolItem(), 725.0f, true);
        ItemRegistry.registerItem("swampdwellerstaff", new SwampDwellerStaffProjectileToolItem(), 800.0f, true);
        ItemRegistry.registerItem("emeraldstaff", new EmeraldStaffProjectileToolItem(), 420.0f, true);
        ItemRegistry.registerItem("venomshower", new VenomShowerProjectileToolItem(), 800.0f, true);
        ItemRegistry.registerItem("ancientdredgingstaff", new AncientDredgingStaffProjectileToolItem(), 600.0f, true);
        ItemRegistry.registerItem("dragonlance", new DragonLanceProjectileToolItem(), 700.0f, true);
        ItemRegistry.registerItem("slimestaff", new SlimeStaffProjectileToolItem(), 400.0f, true);
        ItemRegistry.registerItem("phantompopper", new PhantomPopperProjectileToolItem(), 400.0f, true);
        ItemRegistry.registerItem("bloodgrimoire", new BloodGrimoireProjectileToolItem(), 700.0f, true);
        ItemRegistry.registerItem("webweaver", new WebWeaverToolItem(IncursionMagicWeaponsLootTable.incursionMagicWeapons), 1000.0f, true);
        ItemRegistry.registerItem("emeraldwand", new EmeraldWandProjectileToolItem(), 350.0f, true);
        ItemRegistry.registerItem("refractor", new RefractorProjectileToolItem(), 1100.0f, true);
        ItemRegistry.registerItem("topazstaff", new TopazStaffProjectileToolItem(), 350.0f, true);
        ItemRegistry.registerItem("ascendedstaff", new AscendedStaffToolItem(), 2000.0f, true);
        ItemRegistry.registerItem("ninjastar", new NinjaStarToolItem(), 0.5f, true);
        ItemRegistry.registerItem("icejavelin", new IceJavelinToolItem(), 1.0f, true);
        ItemRegistry.registerItem("woodboomerang", new WoodBoomerangToolItem(), 20.0f, true);
        ItemRegistry.registerItem("spiderboomerang", new SpiderBoomerangToolItem(), 80.0f, true);
        ItemRegistry.registerItem("boxingglovegun", new BoxingGloveGunToolItem(), 150.0f, true);
        ItemRegistry.registerItem("frostboomerang", new FrostBoomerangToolItem(), 90.0f, true);
        ItemRegistry.registerItem("hook", new HookBoomerangToolItem(), 200.0f, false);
        ItemRegistry.registerItem("rollingpin", new RollingPinBoomerangToolItem(), 100.0f, true);
        ItemRegistry.registerItem("voidboomerang", new VoidBoomerangToolItem(), 100.0f, true);
        ItemRegistry.registerItem("razorbladeboomerang", new RazorBladeBoomerangToolItem(), 150.0f, true);
        ItemRegistry.registerItem("tungstenboomerang", new TungstenBoomerangToolItem(), 60.0f, true);
        ItemRegistry.registerItem("glacialboomerang", new GlacialBoomerangToolItem(), 180.0f, true);
        ItemRegistry.registerItem("butcherscleaver", new ButchersCleaverBoomerangToolItem(), 600.0f, true);
        ItemRegistry.registerItem("chefsspecial", new ChefsSpecialBoomerangToolItem(), 800.0f, true);
        ItemRegistry.registerItem("anchorandchain", new AnchorAndChainToolItem(), 800.0f, true);
        ItemRegistry.registerItem("carapacedagger", new CarapaceDaggerToolItem(), 220.0f, true);
        ItemRegistry.registerItem("dragonsrebound", new DragonsReboundToolItem(), 600.0f, true);
        ItemRegistry.registerItem("nightrazorboomerang", new NightRazorBoomerangToolItem(), 150.0f, true);
        ItemRegistry.registerItem("snowball", new SnowBallToolItem(), 0.1f, true);
        ItemRegistry.registerItem("brainonastick", new BrainOnAStickToolItem(), 400.0f, true);
        ItemRegistry.registerItem("spiderstaff", new SpiderStaffSummonToolItem(), 120.0f, true);
        ItemRegistry.registerItem("magicbranch", new MagicBranchSummonToolItem(), 250.0f, true);
        ItemRegistry.registerItem("frostpiercer", new FrostPiercerSummonToolItem(), 200.0f, true);
        ItemRegistry.registerItem("sentientsword", new SentientSwordSummonToolItem(), 250.0f, true);
        ItemRegistry.registerItem("slimecanister", new SlimeCanisterSummonToolItem(), 400.0f, true);
        ItemRegistry.registerItem("necroticbow", new NecroticBowProjectileToolItem(), 500.0f, true);
        ItemRegistry.registerItem("reanimationbow", new ReanimationBowProjectileToolItem(), 900.0f, true);
        ItemRegistry.registerItem("stabbybush", new StabbyBushSummonToolItem(), 400.0f, true);
        ItemRegistry.registerItem("bashybush", new BashyBushSummonToolItem(), 550.0f, true);
        ItemRegistry.registerItem("vulturestaff", new VultureStaffSummonToolItem(), 400.0f, true);
        ItemRegistry.registerItem("piratehook", new PirateHookSummonToolItem(), 500.0f, false);
        ItemRegistry.registerItem("rubyshields", new RubyShieldsToolItem(), 350.0f, true);
        ItemRegistry.registerItem("reaperscall", new ReapersCallSummonToolItem(), 700.0f, true);
        ItemRegistry.registerItem("cryostaff", new CryoStaffSummonToolItem(), 350.0f, true);
        ItemRegistry.registerItem("dryadbranch", new DryadBranchSummonToolItem(), 280.0f, true);
        ItemRegistry.registerItem("swampsgrasp", new SwampsGraspToolItem(), 800.0f, true);
        ItemRegistry.registerItem("skeletonstaff", new SkeletonStaffToolItem(), 750.0f, true);
        ItemRegistry.registerItem("orbofslimes", new OrbOfSlimesToolItem(), 350.0f, true);
        ItemRegistry.registerItem("phantomcaller", new PhantomCallerSummonToolItem(), 400.0f, true);
        ItemRegistry.registerItem("empresscommand", new EmpressCommandToolItem(), 750.0f, true);
        ItemRegistry.registerItem("crystallizedskull", new CrystallizedSkullSummonToolItem(), 1100.0f, true);
        ItemRegistry.registerItem("eyeofthevoid", new EyeOfTheVoidSummonToolItem(), 2000.0f, true);
        ItemRegistry.registerItem("ironbomb", new IronBombToolItem(), 20.0f, true);
        ItemRegistry.registerItem("dynamitestick", new DynamiteStickToolItem(), 60.0f, true);
        ItemRegistry.registerItem("tilebomb", new TileBombToolItem(), 20.0f, true);
        ItemRegistry.registerItem("bannerofspeed", new BannerItem(Item.Rarity.COMMON, 480, m -> BuffRegistry.Banners.SPEED), 200.0f, true);
        ItemRegistry.registerItem("bannerofdamage", new BannerItem(Item.Rarity.COMMON, 480, m -> BuffRegistry.Banners.DAMAGE), 200.0f, true);
        ItemRegistry.registerItem("bannerofsummonspeed", new BannerItem(Item.Rarity.COMMON, 480, m -> BuffRegistry.Banners.SUMMON_SPEED), 200.0f, true);
        ItemRegistry.registerItem("bannerofdefense", new BannerItem(Item.Rarity.COMMON, 480, m -> BuffRegistry.Banners.DEFENSE), 200.0f, true);
        ItemRegistry.registerItem("mlg1", new MLG1SwordToolItem(), 1000.0f, false);
        ItemRegistry.registerItem("mlg2", new MLG2SwordToolItem(), 1000.0f, false);
        ItemRegistry.registerItem("mousetest", new MouseTestProjectileToolItem(), 0.0f, false);
        ItemRegistry.registerItem("mousebeam", new MouseBeamProjectileToolItem(), 0.0f, false);
        ItemRegistry.registerItem("woodgreatsword", new WoodGreatswordToolItem(), 0.0f, false);
        ItemRegistry.registerItem("goldencausticexecutioner", new GoldenCausticExecutionerToolItem(), 0.0f, false);
        ItemRegistry.registerItem("goldenarachnidwebbow", new GoldenArachnidWebBowToolItem(), 0.0f, false);
        ItemRegistry.registerItem("goldenwebweaver", new GoldenWebWeaverToolItem(), 0.0f, false);
        ItemRegistry.registerItem("flamelingorb", new FlamelingOrbProjectileToolItem(), 0.0f, false);
        ItemRegistry.registerItem("fishianwarriorhook", new FishianWarriorHookSwordToolItem(), 0.0f, false);
        ItemRegistry.registerItem("fishianhealerstaff", new FishianHealerStaffToolItem(), 0.0f, false);
        ItemRegistry.registerItem("ancestorsword", new AncestorSwordToolItem(), 0.0f, false);
        ItemRegistry.registerItem("ancestorwand", new AncestorWandToolItem(), 0.0f, false);
        ItemRegistry.registerItem("chargebeam", new ChargeBeamProjectileToolItem(), 0.0f, false);
        ItemRegistry.registerItem("chargeshower", new ChargeShowerProjectileToolItem(), 0.0f, false);
        ItemRegistry.registerItem("steelboat", new SteelBoatMountItem(), 500.0f, true);
        ItemRegistry.registerItem("runeboundboat", new RuneboundBoatMountItem(), 500.0f, false);
        ItemRegistry.registerItem("inefficientfeather", new MountItem("tameostrich"), 100.0f, true);
        ItemRegistry.registerItem("callofthesea", new SeahorseMountItem(), 1000.0f, true);
        ItemRegistry.registerItem("jumpingball", new JumpingBallMountItem(), 400.0f, true);
        ItemRegistry.registerItem("hoverboard", new HoverBoardMountItem(), 800.0f, true);
        ItemRegistry.registerItem("witchbroom", new WitchBroomMountItem(), 1000.0f, true);
        ItemRegistry.registerItem("leatherglove", new SimpleTrinketItem(Item.Rarity.COMMON, "leatherglovetrinket", 200, TrinketsLootTable.trinkets), 50.0f, true);
        ItemRegistry.registerItem("trackerboot", new SimpleTrinketItem(Item.Rarity.COMMON, "trackerboottrinket", 200, TrinketsLootTable.trinkets), 60.0f, true);
        ItemRegistry.registerItem("shinebelt", new SimpleTrinketItem(Item.Rarity.COMMON, "shinebelttrinket", 200, TrinketsLootTable.trinkets), 100.0f, true);
        ItemRegistry.registerItem("dreamcatcher", new SimpleTrinketItem(Item.Rarity.COMMON, "dreamcatchertrinket", 200, TrinketsLootTable.trinkets), 50.0f, true);
        ItemRegistry.registerItem("nightmaretalisman", new SimpleTrinketItem(Item.Rarity.COMMON, "nightmaretalismantrinket", 500, TrinketsLootTable.trinkets).addDisables("dreamcatcher"), 100.0f, true);
        ItemRegistry.registerItem("prophecyslab", new SimpleTrinketItem(Item.Rarity.COMMON, "prophecyslabtrinket", 200, TrinketsLootTable.trinkets), 100.0f, true);
        ItemRegistry.registerItem("magicmanual", new SimpleTrinketItem(Item.Rarity.COMMON, "magicmanualtrinket", 200, TrinketsLootTable.trinkets), 100.0f, true);
        ItemRegistry.registerItem("scryingcards", new SimpleTrinketItem(Item.Rarity.COMMON, "scryingcardstrinket", 200, TrinketsLootTable.trinkets).addDisables("magicmanual", "prophecyslab"), 100.0f, true);
        ItemRegistry.registerItem("forbiddenspellbook", new SimpleTrinketItem(Item.Rarity.COMMON, "forbiddenspellbooktrinket", 200, TrinketsLootTable.trinkets), 100.0f, true);
        ItemRegistry.registerItem("explorersatchel", new CombinedTrinketItem(Item.Rarity.UNCOMMON, 500, "leatherglove", "trackerboot", "shinebelt"), 200.0f, true);
        ItemRegistry.registerItem("vampiresgift", new SimpleTrinketItem(Item.Rarity.COMMON, "vampiresgifttrinket", 300, TrinketsLootTable.trinkets), 150.0f, true);
        ItemRegistry.registerItem("leatherdashers", new LeatherDashersTrinketItem(), 50.0f, true);
        ItemRegistry.registerItem("zephyrcharm", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "zephyrcharmtrinket", 200, TrinketsLootTable.trinkets), 50.0f, true);
        ItemRegistry.registerItem("zephyrboots", new ZephyrBootsTrinketItem().addDisables("leatherdashers"), 200.0f, true);
        ItemRegistry.registerItem("windboots", new WindBootsTrinketItem().addDisables("leatherdashers", "zephyrboots"), 300.0f, true);
        ItemRegistry.registerItem("hoverboots", new HoverBootsTrinketItem().addDisables("leatherdashers", "zephyrboots", "windboots"), 400.0f, true);
        ItemRegistry.registerItem("ghostboots", new GhostBootsTrinketItem().addDisables("leatherdashers", "zephyrboots", "hoverboots", "hoverboots", "windboots"), 500.0f, true);
        ItemRegistry.registerItem("kineticboots", new KineticBootsTrinketItem().addDisables("zephyrboots", "leatherdashers"), 1000.0f, true);
        ItemRegistry.registerItem("forceofwind", new ForceOfWindTrinketItem(), 200.0f, true);
        ItemRegistry.registerItem("woodshield", new WoodShieldTrinketItem(Item.Rarity.COMMON, 2, 0.5f, 10000, 0.25f, 60, 360.0f, 200), 100.0f, true);
        ItemRegistry.registerItem("hardenedshield", new ShieldTrinketItem(Item.Rarity.COMMON, 4, 0.5f, 9000, 0.25f, 50, 360.0f, 400, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("cactusshield", new CactusShieldTrinketItem(Item.Rarity.UNCOMMON, 400), 300.0f, true);
        ItemRegistry.registerItem("shellofretribution", new CactusShieldTrinketItem(Item.Rarity.RARE, 600).addCombinedTrinkets("spidercharm"), 600.0f, true);
        ItemRegistry.registerItem("tungstenshield", new ShieldTrinketItem(Item.Rarity.UNCOMMON, 6, 0.5f, 7000, 0.25f, 30, 360.0f, 800, TrinketsLootTable.trinkets), 250.0f, true);
        ItemRegistry.registerItem("parrybuckler", new ParryBucklerShieldTrinketItem(Item.Rarity.RARE, 900), 500.0f, true);
        ItemRegistry.registerItem("agedchampionshield", new ShieldTrinketItem(Item.Rarity.RARE, 9, 0.5f, 2000, 100.0f, 0, 360.0f, 900, TrinketsLootTable.trinkets), 600.0f, true);
        ItemRegistry.registerItem("siphonshield", new SiphonShieldTrinketItem(Item.Rarity.RARE, 900), 500.0f, true);
        ItemRegistry.registerItem("carapaceshield", new ShieldTrinketItem(Item.Rarity.RARE, 12, 1.0f, 5000, 0.1f, 10, 360.0f, 1000, TrinketsLootTable.trinkets), 500.0f, true);
        ItemRegistry.registerItem("crystalshield", new CrystalShieldTrinketItem(Item.Rarity.RARE, 1000), 700.0f, true);
        ItemRegistry.registerItem("sparegemstones", new SimpleTrinketItem(Item.Rarity.COMMON, "sparegemstonestrinket", 200, TrinketsLootTable.trinkets), 100.0f, true);
        ItemRegistry.registerItem("spellstone", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "spellstonetrinket", 200, TrinketsLootTable.trinkets).addDisables("sparegemstones"), 300.0f, true);
        ItemRegistry.registerItem("companionlocket", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "companionlockettrinket", 200, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("willowisplantern", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "willowisplanterntrinket", 200, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("essenceofperspective", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "essenceofperspectivetrinket", 200, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("essenceofprolonging", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "essenceofprolongingtrinket", 200, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("essenceofrebirth", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "essenceofrebirthtrinket", 200, TrinketsLootTable.trinkets).addDisables("essenceofperspectivetrinket", "essenceofprolongingtrinket"), 200.0f, true);
        ItemRegistry.registerItem("blinkscepter", new BlinkScepterTrinketItem(), 800.0f, true);
        ItemRegistry.registerItem("magicfoci", new SimpleTrinketItem(Item.Rarity.COMMON, "magicfocitrinket", 500, TrinketsLootTable.trinkets).addDisables("rangefoci", "meleefoci", "summonfoci"), 200.0f, true);
        ItemRegistry.registerItem("rangefoci", new SimpleTrinketItem(Item.Rarity.COMMON, "rangefocitrinket", 500, TrinketsLootTable.trinkets).addDisables("magicfoci", "meleefoci", "summonfoci"), 200.0f, true);
        ItemRegistry.registerItem("meleefoci", new SimpleTrinketItem(Item.Rarity.COMMON, "meleefocitrinket", 500, TrinketsLootTable.trinkets).addDisables("magicfoci", "rangefoci", "summonfoci"), 200.0f, true);
        ItemRegistry.registerItem("summonfoci", new SimpleTrinketItem(Item.Rarity.COMMON, "summonfocitrinket", 500, TrinketsLootTable.trinkets).addDisables("magicfoci", "rangefoci", "meleefoci"), 200.0f, true);
        ItemRegistry.registerItem("balancedfoci", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "balancedfocitrinket", 600, TrinketsLootTable.trinkets).addDisables("magicfoci", "rangefoci", "meleefoci", "summonfoci"), 600.0f, true);
        ItemRegistry.registerItem("demonclaw", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "demonclawtrinket", 500, TrinketsLootTable.trinkets), 40.0f, true);
        ItemRegistry.registerItem("fins", new SimpleTrinketItem(Item.Rarity.COMMON, "finstrinket", 200, TrinketsLootTable.trinkets), 150.0f, true);
        ItemRegistry.registerItem("fuzzydice", new SimpleTrinketItem(Item.Rarity.COMMON, "fuzzydicetrinket", 200, TrinketsLootTable.trinkets), 100.0f, true);
        ItemRegistry.registerItem("noblehorseshoe", new SimpleTrinketItem(Item.Rarity.COMMON, "noblehorseshoetrinket", 250, TrinketsLootTable.trinkets), 150.0f, true);
        ItemRegistry.registerItem("luckycape", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "luckycapetrinket", 350, TrinketsLootTable.trinkets).addDisables("fuzzydice", "noblehorseshoe"), 200.0f, true);
        ItemRegistry.registerItem("miningcharm", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "miningcharmtrinket", 300, TrinketsLootTable.trinkets), 250.0f, true);
        ItemRegistry.registerItem("ancientfeather", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "ancientfeathertrinket", 500, TrinketsLootTable.trinkets), 300.0f, true);
        ItemRegistry.registerItem("assassinscowl", new CombinedTrinketItem(Item.Rarity.RARE, 600, "luckycape", "ancientfeather"), 400.0f, true);
        ItemRegistry.registerItem("airvessel", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "airvesseltrinket", 500, TrinketsLootTable.trinkets), 300.0f, true);
        ItemRegistry.registerItem("regenpendant", new SimpleTrinketItem(Item.Rarity.COMMON, "regenpendanttrinket", 400, TrinketsLootTable.trinkets), 75.0f, true);
        ItemRegistry.registerItem("calmingrose", new SimpleTrinketItem(Item.Rarity.RARE, "calmingrosetrinket", 400, TrinketsLootTable.trinkets), 300.0f, true);
        ItemRegistry.registerItem("calmingminersbouquet", new CalmingMinersBouquetTrinketItem().addDisables("miningcharm", "calmingrose"), 500.0f, true);
        ItemRegistry.registerItem("minersbouquet", new MinersBouquetTrinketItem().addDisables("miningcharm", "calmingrose").addDisabledBy("calmingminersbouquet"), 500.0f, false);
        ItemRegistry.registerItem("mobilitycloak", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "mobilitycloaktrinket", 400, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("travelercloak", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "travelercloaktrinket", 600, TrinketsLootTable.trinkets).addDisables("mobilitycloak", "fins"), 320.0f, true);
        ItemRegistry.registerItem("mesmertablet", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "mesmertablettrinket", 600, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("magicalquiver", new SimpleTrinketItem(Item.Rarity.RARE, "magicalquivertrinket", 500, TrinketsLootTable.trinkets), 500.0f, true);
        ItemRegistry.registerItem("ammobox", new SimpleTrinketItem(Item.Rarity.RARE, "ammoboxtrinket", 700, TrinketsLootTable.trinkets), 700.0f, true);
        ItemRegistry.registerItem("frozenheart", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "frozenhearttrinket", 500, TrinketsLootTable.trinkets), 300.0f, true);
        ItemRegistry.registerItem("frozenwave", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "frozenwavetrinket", 500, TrinketsLootTable.trinkets), 300.0f, true);
        ItemRegistry.registerItem("spidercharm", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "spidercharmtrinket", 600, TrinketsLootTable.trinkets), 400.0f, true);
        ItemRegistry.registerItem("guardianshell", new SimpleTrinketItem(Item.Rarity.RARE, "guardianshelltrinket", 700, TrinketsLootTable.trinkets), 500.0f, true);
        ItemRegistry.registerItem("inducingamulet", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "inducingamulettrinket", 700, TrinketsLootTable.trinkets), 300.0f, true);
        ItemRegistry.registerItem("polarclaw", new SimpleTrinketItem(Item.Rarity.RARE, "polarclawtrinket", 800, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("piratetelescope", new SimpleTrinketItem(Item.Rarity.RARE, "piratetelescopetrinket", 800, TrinketsLootTable.trinkets), 500.0f, true);
        ItemRegistry.registerItem("lifeline", new SimpleTrinketItem(Item.Rarity.RARE, "lifelinettrinket", 800, TrinketsLootTable.trinkets), 350.0f, true);
        ItemRegistry.registerItem("explorercloak", new SimpleTrinketItem(Item.Rarity.RARE, "explorercloaktrinket", 800, TrinketsLootTable.trinkets).addDisables("travelercloak", "mobilitycloak", "fins"), 700.0f, true);
        ItemRegistry.registerItem("frozensoul", new CombinedTrinketItem(Item.Rarity.RARE, 800, "lifeline", "frozenheart"), 600.0f, true);
        ItemRegistry.registerItem("ninjasmark", new SimpleTrinketItem(Item.Rarity.RARE, "ninjasmarktrinket", 800, TrinketsLootTable.trinkets), 350.0f, true);
        ItemRegistry.registerItem("bonehilt", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "bonehilttrinket", 500, TrinketsLootTable.trinkets), 160.0f, true);
        ItemRegistry.registerItem("firestone", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "firestonetrinket", 600, TrinketsLootTable.trinkets), 400.0f, true);
        ItemRegistry.registerItem("lifependant", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "lifependanttrinket", 600, TrinketsLootTable.trinkets).addDisables("regenpendant"), 200.0f, true);
        ItemRegistry.registerItem("spikedboots", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "spikedbootstrinket", 600, TrinketsLootTable.trinkets), 350.0f, true);
        ItemRegistry.registerItem("spikedbatboots", new SimpleTrinketItem(Item.Rarity.RARE, "spikedbatbootstrinket", 1000, TrinketsLootTable.trinkets).addDisables("spikedboots", "vampiresgift"), 450.0f, true);
        ItemRegistry.registerItem("froststone", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "froststonetrinket", 600, TrinketsLootTable.trinkets), 400.0f, true);
        ItemRegistry.registerItem("frostflame", new SimpleTrinketItem(Item.Rarity.RARE, "frostflametrinket", 1000, TrinketsLootTable.trinkets).addDisables("froststone", "firestone"), 600.0f, true);
        ItemRegistry.registerItem("balancedfrostfirefoci", new CombinedTrinketItem(Item.Rarity.EPIC, 1200, "balancedfoci", "frostflame"), 900.0f, true);
        ItemRegistry.registerItem("hysteriatablet", new SimpleTrinketItem(Item.Rarity.RARE, "hysteriatablettrinket", 800, TrinketsLootTable.trinkets).addDisables("mesmertablet", "inducingamulet"), 400.0f, true);
        ItemRegistry.registerItem("frenzyorb", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "frenzyorbtrinket", 1000, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("scryingmirror", new SimpleTrinketItem(Item.Rarity.RARE, "scryingmirrortrinket", 1100, TrinketsLootTable.trinkets), 600.0f, true);
        ItemRegistry.registerItem("diggingclaw", new SimpleTrinketItem(Item.Rarity.RARE, "diggingclawtrinket", 1000, TrinketsLootTable.trinkets), 600.0f, true);
        ItemRegistry.registerItem("templependant", new SimpleTrinketItem(Item.Rarity.RARE, "templependanttrinket", 1000, TrinketsLootTable.trinkets), 700.0f, true);
        ItemRegistry.registerItem("ancientrelics", new SimpleTrinketItem(Item.Rarity.RARE, "ancientrelicstrinket", 1200, TrinketsLootTable.trinkets).addDisables("airvessel", "templependant"), 800.0f, true);
        ItemRegistry.registerItem("gelatincasings", new SimpleTrinketItem(Item.Rarity.EPIC, "gelatincasingstrinket", 1000, IncursionTrinketsLootTable.incursionTrinkets), 250.0f, true);
        ItemRegistry.registerItem("bloodstonering", new SimpleTrinketItem(Item.Rarity.EPIC, "bloodstoneringtrinket", 1000, IncursionTrinketsLootTable.incursionTrinkets), 250.0f, true);
        ItemRegistry.registerItem("claygauntlet", new SimpleTrinketItem(Item.Rarity.COMMON, "claygauntlettrinket", 300, TrinketsLootTable.trinkets), 100.0f, true);
        ItemRegistry.registerItem("chainshirt", new SimpleTrinketItem(Item.Rarity.COMMON, "chainshirttrinket", 300, TrinketsLootTable.trinkets), 100.0f, true);
        ItemRegistry.registerItem("vambrace", new SimpleTrinketItem(Item.Rarity.RARE, "vambracetrinket", 300, TrinketsLootTable.trinkets), 100.0f, true);
        ItemRegistry.registerItem("manica", new SimpleTrinketItem(Item.Rarity.EPIC, "manicatrinket", 800, TrinketsLootTable.trinkets).addDisables("vambrace", "chainshirt"), 200.0f, true);
        ItemRegistry.registerItem("agedchampionscabbard", new SimpleTrinketItem(Item.Rarity.RARE, "agedchampionscabbardtrinket", 600, TrinketsLootTable.trinkets), 350.0f, true);
        ItemRegistry.registerItem("challengerspauldron", new SimpleTrinketItem(Item.Rarity.RARE, "challengerspauldrontrinket", 800, TrinketsLootTable.trinkets), 350.0f, true);
        ItemRegistry.registerItem("clockworkheart", new SimpleTrinketItem(Item.Rarity.EPIC, "clockworkhearttrinket", 1000, TrinketsLootTable.trinkets), 800.0f, true);
        ItemRegistry.registerItem("foolsgambit", new FoolsGambitTrinketItem(Item.Rarity.EPIC, "foolsgambittrinket", 1100), 850.0f, true);
        ItemRegistry.registerItem("necroticsoulskull", new SimpleTrinketItem(Item.Rarity.EPIC, "necroticsoulskull", 1100, TrinketsLootTable.trinkets), 600.0f, true);
        ItemRegistry.registerItem("spiritboard", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "spiritboardtrinket", 600, TrinketsLootTable.trinkets), 450.0f, true);
        ItemRegistry.registerItem("secondwindcharm", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "secondwindcharmtrinket", 600, TrinketsLootTable.trinkets), 450.0f, true);
        ItemRegistry.registerItem("guardianbracelet", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "guardianbracelettrinket", 600, TrinketsLootTable.trinkets), 450.0f, true);
        ItemRegistry.registerItem("summonersbestiary", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "summonersbestiarytrinket", 600, TrinketsLootTable.trinkets), 450.0f, true);
        ItemRegistry.registerItem("spiritgreaves", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "spiritgreavestrinket", 600, TrinketsLootTable.trinkets), 450.0f, true);
        ItemRegistry.registerItem("constructionhammer", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "constructionhammertrinket", 400, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("telescopicladder", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "telescopicladdertrinket", 400, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("toolextender", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "toolextendertrinket", 400, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("itemattractor", new SimpleTrinketItem(Item.Rarity.UNCOMMON, "itemattractortrinket", 400, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("toolbox", new CombinedTrinketItem(Item.Rarity.RARE, 800, "constructionhammer", "telescopicladder", "toolextender", "itemattractor"), 600.0f, true);
        ItemRegistry.registerItem("minersprosthetic", new SimpleTrinketItem(Item.Rarity.RARE, "minersprosthetictrinket", 800, TrinketsLootTable.trinkets), 500.0f, true);
        ItemRegistry.registerItem("cavelingsfoot", new SimpleTrinketItem(Item.Rarity.RARE, "cavelingsfoottrinket", 300, TrinketsLootTable.trinkets), 200.0f, true);
        ItemRegistry.registerItem("cavelingscollection", new SimpleTrinketItem(Item.Rarity.RARE, "cavelingscollectiontrinket", 600, TrinketsLootTable.trinkets), 300.0f, true);
        ItemRegistry.registerItem("voidphasingstaff", new VoidPhasingStaffTrinketItem(), 1500.0f, true);
        ItemRegistry.registerItem("leatherhood", new LeatherHoodArmorItem(), 25.0f, true);
        ItemRegistry.registerItem("leathershirt", new LeatherShirtArmorItem(), 55.0f, true);
        ItemRegistry.registerItem("leatherboots", new LeatherBootsArmorItem(), 10.0f, true);
        ItemRegistry.registerItem("clothhat", new ClothHatArmorItem(), 25.0f, true);
        ItemRegistry.registerItem("clothrobe", new ClothRobeArmorItem(), 55.0f, true);
        ItemRegistry.registerItem("clothboots", new ClothBootsArmorItem(), 10.0f, true);
        ItemRegistry.registerItem("copperhelmet", new CopperHelmetArmorItem(), 15.0f, true);
        ItemRegistry.registerItem("copperchestplate", new CopperChestplateArmorItem(), 30.0f, true);
        ItemRegistry.registerItem("copperboots", new CopperBootsArmorItem(), 5.0f, true);
        ItemRegistry.registerItem("ironhelmet", new IronHelmetArmorItem(), 20.0f, true);
        ItemRegistry.registerItem("ironchestplate", new IronChestplateArmorItem(), 45.0f, true);
        ItemRegistry.registerItem("ironboots", new IronBootsArmorItem(), 8.0f, true);
        ItemRegistry.registerItem("goldcrown", new GoldCrownArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("goldhelmet", new GoldHelmetArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("goldchestplate", new GoldChestplateArmorItem(), 80.0f, true);
        ItemRegistry.registerItem("goldboots", new GoldBootsArmorItem(), 30.0f, true);
        ItemRegistry.registerItem("soldiercap", new SoldierCapArmorItem(), 60.0f, true);
        ItemRegistry.registerItem("soldierhelmet", new SoldierHelmetArmorItem(), 60.0f, true);
        ItemRegistry.registerItem("soldierchestplate", new SoldierChestplateArmorItem(), 100.0f, true);
        ItemRegistry.registerItem("soldierboots", new SoldierBootsArmorItem(), 20.0f, true);
        ItemRegistry.registerItem("spiderhelmet", new SpiderHelmetArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("spiderchestplate", new SpiderChestplateArmorItem(), 75.0f, true);
        ItemRegistry.registerItem("spiderboots", new SpiderBootsArmorItem(), 40.0f, true);
        ItemRegistry.registerItem("bloodplatecowl", new BloodplateCowlArmorItem(), 55.0f, true);
        ItemRegistry.registerItem("bloodplatechestplate", new BloodplateChestplateArmorItem(), 65.0f, true);
        ItemRegistry.registerItem("bloodplateboots", new BloodplateBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("frosthelmet", new FrostHelmetArmorItem(), 60.0f, true);
        ItemRegistry.registerItem("frosthood", new FrostHoodArmorItem(), 60.0f, true);
        ItemRegistry.registerItem("frosthat", new FrostHatArmorItem(), 60.0f, true);
        ItemRegistry.registerItem("frostchestplate", new FrostChestplateArmorItem(), 80.0f, true);
        ItemRegistry.registerItem("frostboots", new FrostBootsArmorItem(), 40.0f, true);
        ItemRegistry.registerItem("demonichelmet", new DemonicHelmetArmorItem(), 45.0f, true);
        ItemRegistry.registerItem("demonicchestplate", new DemonicChestplateArmorItem(), 80.0f, true);
        ItemRegistry.registerItem("demonicboots", new DemonicBootsArmorItem(), 20.0f, true);
        ItemRegistry.registerItem("arachnidhelmet", new ArachnidHelmetArmorItem(), 70.0f, true);
        ItemRegistry.registerItem("arachnidchestplate", new ArachnidChestplateArmorItem(), 130.0f, true);
        ItemRegistry.registerItem("arachnidlegs", new ArachnidLegsArmorItem(), 70.0f, true);
        ItemRegistry.registerItem("voidmask", new VoidMaskArmorItem(), 100.0f, true);
        ItemRegistry.registerItem("voidhat", new VoidHatArmorItem(), 100.0f, true);
        ItemRegistry.registerItem("voidrobe", new VoidRobeArmorItem(), 140.0f, true);
        ItemRegistry.registerItem("voidboots", new VoidBootsArmorItem(), 60.0f, true);
        ItemRegistry.registerItem("thiefscowl", new ThiefsCowlArmorItem(), 100.0f, true);
        ItemRegistry.registerItem("thiefscloak", new ThiefsCloakArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("thiefsboots", new ThiefsBootsArmorItem(), 100.0f, true);
        ItemRegistry.registerItem("runichelmet", new RunicHelmetArmorItem(), 55.0f, true);
        ItemRegistry.registerItem("runichood", new RunicHoodArmorItem(), 55.0f, true);
        ItemRegistry.registerItem("runichat", new RunicHatArmorItem(), 55.0f, true);
        ItemRegistry.registerItem("runiccrown", new RunicCrownArmorItem(), 55.0f, true);
        ItemRegistry.registerItem("runicchestplate", new RunicChestplateArmorItem(), 65.0f, true);
        ItemRegistry.registerItem("runicboots", new RunicBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("pharaohsheaddress", new PharaohsHeaddress(), 100.0f, true);
        ItemRegistry.registerItem("pharaohsrobe", new PharaohsRobeArmorItem(), 120.0f, true);
        ItemRegistry.registerItem("pharaohssandals", new PharaohsSandalsArmorItem(), 60.0f, true);
        ItemRegistry.registerItem("witchhat", new WitchHatArmorItem(), 55.0f, true);
        ItemRegistry.registerItem("witchrobe", new WitchRobeArmorItem(), 65.0f, true);
        ItemRegistry.registerItem("witchshoes", new WitchShoesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("ivyhelmet", new IvyHelmetArmorItem(), 75.0f, true);
        ItemRegistry.registerItem("ivyhood", new IvyHoodArmorItem(), 75.0f, true);
        ItemRegistry.registerItem("ivyhat", new IvyHatArmorItem(), 75.0f, true);
        ItemRegistry.registerItem("ivycirclet", new IvyCircletArmorItem(), 75.0f, true);
        ItemRegistry.registerItem("ivychestplate", new IvyChestplateArmorItem(), 125.0f, true);
        ItemRegistry.registerItem("ivyboots", new IvyBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("quartzhelmet", new QuartzHelmetArmorItem(), 100.0f, true);
        ItemRegistry.registerItem("quartzcrown", new QuartzCrownArmorItem(), 100.0f, true);
        ItemRegistry.registerItem("quartzchestplate", new QuartzChestplateArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("quartzboots", new QuartzBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("gunslingerhat", new GunslingerHatArmorItem(), 100.0f, true);
        ItemRegistry.registerItem("gunslingervest", new GunslingerVestArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("gunslingerboots", new GunslingerBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("tungstenhelmet", new TungstenHelmetArmorItem(), 110.0f, true);
        ItemRegistry.registerItem("tungstenchestplate", new TungstenChestplateArmorItem(), 160.0f, true);
        ItemRegistry.registerItem("tungstenboots", new TungstenBootsArmorItem(), 80.0f, true);
        ItemRegistry.registerItem("shadowhat", new ShadowHatArmorItem(), 110.0f, true);
        ItemRegistry.registerItem("shadowhood", new ShadowHoodArmorItem(), 110.0f, true);
        ItemRegistry.registerItem("shadowmantle", new ShadowMantleArmorItem(), 160.0f, true);
        ItemRegistry.registerItem("shadowboots", new ShadowBootsArmorItem(), 80.0f, true);
        ItemRegistry.registerItem("ninjahood", new NinjaHoodArmorItem(), 120.0f, true);
        ItemRegistry.registerItem("ninjarobe", new NinjaRobeArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("ninjashoes", new NinjaShoesArmorItem(), 80.0f, true);
        ItemRegistry.registerItem("soulseedcrown", new SoulseedCrownArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("soulseedchestplate", new SoulseedChestplateArmorItem(), 165.0f, true);
        ItemRegistry.registerItem("soulseedboots", new SoulseedBootsArmorItem(), 190.0f, true);
        ItemRegistry.registerItem("glacialcirclet", new GlacialCircletArmorItem(), 110.0f, true);
        ItemRegistry.registerItem("glacialhelmet", new GlacialHelmetArmorItem(), 110.0f, true);
        ItemRegistry.registerItem("glacialchestplate", new GlacialChestplateArmorItem(), 160.0f, true);
        ItemRegistry.registerItem("glacialboots", new GlacialBootsArmorItem(), 80.0f, true);
        ItemRegistry.registerItem("deepfrosthood", new DeepfrostHelmetArmorItem(), 180.0f, true);
        ItemRegistry.registerItem("deepfrostchestplate", new DeepfrostChestplateArmorItem(), 190.0f, true);
        ItemRegistry.registerItem("deepfrostboots", new DeepfrostBootsArmorItem(), 160.0f, true);
        ItemRegistry.registerItem("cryowitchhat", new CryoWitchHatArmorItem(), 310.0f, true);
        ItemRegistry.registerItem("cryowitchrobe", new CryoWitchRobeArmorItem(), 230.0f, true);
        ItemRegistry.registerItem("cryowitchshoes", new CryoWitchShoesArmorItem(), 180.0f, true);
        ItemRegistry.registerItem("dryadcrown", new DryadCrownArmorItem(), 240.0f, true);
        ItemRegistry.registerItem("dryadscarf", new DryadScarfArmorItem(), 240.0f, true);
        ItemRegistry.registerItem("dryadhat", new DryadHatArmorItem(), 240.0f, true);
        ItemRegistry.registerItem("dryadhelmet", new DryadHelmetArmorItem(), 240.0f, true);
        ItemRegistry.registerItem("dryadchestplate", new DryadChestplateArmorItem(), 330.0f, true);
        ItemRegistry.registerItem("dryadboots", new DryadBootsArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("myceliumhood", new MyceliumHoodArmorItem(), 110.0f, true);
        ItemRegistry.registerItem("myceliumscarf", new MyceliumScarfArmorItem(), 110.0f, true);
        ItemRegistry.registerItem("myceliumchestplate", new MyceliumChestplateArmorItem(), 160.0f, true);
        ItemRegistry.registerItem("myceliumboots", new MyceliumBootsArmorItem(), 80.0f, true);
        ItemRegistry.registerItem("agedchampionhelmet", new AgedChampionHelmetArmorItem(), 120.0f, true);
        ItemRegistry.registerItem("agedchampionchestplate", new AgedChampionChestplateArmorItem(), 160.0f, true);
        ItemRegistry.registerItem("agedchampiongreaves", new AgedChampionGreavesArmorItem(), 80.0f, true);
        ItemRegistry.registerItem("widowhelmet", new WidowHelmetArmorItem(), 120.0f, true);
        ItemRegistry.registerItem("widowchestplate", new WidowChestplateArmorItem(), 160.0f, true);
        ItemRegistry.registerItem("widowboots", new WidowBootsArmorItem(), 80.0f, true);
        ItemRegistry.registerItem("ancestorshat", new AncestorsHatArmorItem(), 110.0f, true);
        ItemRegistry.registerItem("ancestorsrobe", new AncestorsRobeArmorItem(), 110.0f, true);
        ItemRegistry.registerItem("ancestorsboots", new AncestorsBootsArmorItem(), 110.0f, true);
        ItemRegistry.registerItem("ancientfossilmask", new AncientFossilMaskArmorItem(), 110.0f, true);
        ItemRegistry.registerItem("ancientfossilhelmet", new AncientFossilHelmetArmorItem(), 110.0f, true);
        ItemRegistry.registerItem("ancientfossilchestplate", new AncientFossilChestplateArmorItem(), 160.0f, true);
        ItemRegistry.registerItem("ancientfossilboots", new AncientFossilBootsArmorItem(), 80.0f, true);
        ItemRegistry.registerItem("sharpshooterhat", new SharpshooterHatArmorItem(), 110.0f, true);
        ItemRegistry.registerItem("sharpshootercoat", new SharpshooterCoatArmorItem(), 160.0f, true);
        ItemRegistry.registerItem("sharpshooterboots", new SharpshooterBootsArmorItem(), 80.0f, true);
        ItemRegistry.registerItem("slimehat", new SlimeHatArmorItem(), 300.0f, true);
        ItemRegistry.registerItem("slimehelmet", new SlimeHelmetArmorItem(), 300.0f, true);
        ItemRegistry.registerItem("slimechestplate", new SlimeChestplateArmorItem(), 400.0f, true);
        ItemRegistry.registerItem("slimeboots", new SlimeBootsArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("nightsteelhelmet", new NightsteelHelmetArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("nightsteelmask", new NightsteelMaskArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("nightsteelveil", new NightsteelVeilArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("nightsteelcirclet", new NightsteelCircletArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("nightsteelchestplate", new NightsteelChestplateArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("nightsteelboots", new NightsteelBootsArmorItem(), 120.0f, true);
        ItemRegistry.registerItem("spideritehelmet", new SpideriteHelmetArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("spideritehat", new SpideriteHatArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("spideritehood", new SpideriteHoodArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("spideritecrown", new SpideriteCrownArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("spideritechestplate", new SpideriteChestplateArmorItem(), 300.0f, true);
        ItemRegistry.registerItem("spideritegreaves", new SpideriteGreavesArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("ravenlordsheaddress", new RavenlordsHeaddressArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("ravenlordschestplate", new RavenlordsChestplateArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("ravenlordsboots", new RavenlordsBootsArmorItem(), 120.0f, true);
        ItemRegistry.registerItem("battlechefhat", new BattleChefHatArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("battlechefchestplate", new BattleChefChestplateArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("battlechefboots", new BattleChefBootsArmorItem(), 120.0f, true);
        ItemRegistry.registerItem("dawnhelmet", new DawnHelmetArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("dawnchestplate", new DawnChestplateArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("dawnboots", new DawnBootsArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("duskhelmet", new DuskHelmetArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("duskchestplate", new DuskChestplateArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("duskboots", new DuskBootsArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("sapphireeyepatch", new SapphireEyepatch(), 1000.0f, true);
        ItemRegistry.registerItem("amethysthelmet", new AmethystHelmet(), 1000.0f, true);
        ItemRegistry.registerItem("emeraldmask", new EmeraldMask(), 1000.0f, true);
        ItemRegistry.registerItem("rubycrown", new RubyCrown(), 1000.0f, true);
        ItemRegistry.registerItem("crystalchestplate", new CrystalChestplate(), 1000.0f, true);
        ItemRegistry.registerItem("crystalboots", new CrystalBoots(), 500.0f, true);
        ItemRegistry.registerItem("arcanichelmet", new ArcanicHelmetArmorItem(), 1000.0f, true);
        ItemRegistry.registerItem("arcanicchestplate", new ArcanicChestplateArmorItem(), 1000.0f, true);
        ItemRegistry.registerItem("arcanicboots", new ArcanicBootsArmorItem(), 1000.0f, true);
        ItemRegistry.registerItem("runeboundcrown", new RuneboundCrownArmorItem(), 0.0f, true);
        ItemRegistry.registerItem("runeboundcrownmask", new RuneboundCrownMaskArmorItem(), 0.0f, true);
        ItemRegistry.registerItem("runeboundskullhelmet", new RuneboundSkullHelmetArmorItem(), 0.0f, true);
        ItemRegistry.registerItem("runeboundhelmet", new RuneboundHelmetArmorItem(), 0.0f, true);
        ItemRegistry.registerItem("runeboundhornhelmet", new RuneboundHornHelmetArmorItem(), 0.0f, true);
        ItemRegistry.registerItem("runeboundhood", new RuneboundHoodArmorItem(), 0.0f, true);
        ItemRegistry.registerItem("runeboundbackbones", new RuneboundBackBonesArmorItem(), 0.0f, true);
        ItemRegistry.registerItem("runeboundrobe", new RuneboundRobeArmorItem(), 0.0f, true);
        ItemRegistry.registerItem("runeboundbonesrobe", new RuneboundBonesRobeChestArmorItem(), 0.0f, true);
        ItemRegistry.registerItem("runeboundleatherchest", new RuneboundLeatherChestArmorItem(), 0.0f, true);
        ItemRegistry.registerItem("runeboundboots", new RuneboundBootsArmorItem(), 0.0f, true);
        ItemRegistry.registerItem("shoes", new ShoesArmorItem(0), 100.0f, true);
        ItemRegistry.registerItem("shirt", new ShirtArmorItem(0), 100.0f, true);
        ItemRegistry.registerItem("wig", new WigArmorItem(0), 100.0f, true);
        ItemRegistry.registerItem("cheatshoes", new CheatShoesArmorItem(), 0.0f, false);
        ItemRegistry.registerItem("cheatshirt", new CheatShirtArmorItem(), 0.0f, false);
        ItemRegistry.registerItem("cheatwig", new CheatWigArmorItem(), 0.0f, false);
        ItemRegistry.registerItem("farmerhat", new FarmerHatArmorItem(), 15.0f, true);
        ItemRegistry.registerItem("farmershirt", new FarmerShirtArmorItem(), 35.0f, true);
        ItemRegistry.registerItem("farmershoes", new FarmerShoesArmorItem(), 10.0f, true);
        ItemRegistry.registerItem("farmerpitchfork", new FarmerPitchForkHoldItem(), 50.0f, false);
        ItemRegistry.registerItem("alchemistglasses", new AlchemistGlassesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("labcoat", new LabCoatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("labapron", new LabApronArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("labboots", new LabBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("rainhat", new RainHatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("raincoat", new RainCoatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("rainboots", new RainBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("animalkeepershat", new AnimalKeeperHatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("animalkeepershirt", new AnimalKeeperShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("animalkeepershoes", new AnimalKeeperShoesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("engineergoggles", new EngineerGogglesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("smithingapron", new SmithingApronArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("smithingshoes", new SmithingShoesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("turban", new TurbanArmorItem(), 50.0f, false);
        ItemRegistry.registerItem("exoticshirt", new ExoticShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("exoticshoes", new ExoticShoesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("walkingstick", (Item)new WalkingStickHoldItem(), 50.0f, false, false, false, new String[0]);
        ItemRegistry.registerItem("hardhat", new HardHatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("elderhat", new ElderHatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("eldershirt", new ElderShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("eldershoes", new ElderShoesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("safarihat", new SafariHatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("safarishirt", new SafariShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("safarishoes", new SafariShoesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("minerhat", new MinerHatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("minershirt", new MinerShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("minerboots", new MinerBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("hunterhood", new HunterHoodArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("hunterhoodmask", new HunterHoodMaskArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("huntershirt", new HunterShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("hunterboots", new HunterBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("magehat", new MageHatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("magerobe", new MageRobeArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("mageshoes", new MageShoesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("crimsonhat", new CrimsonHatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("crimsonrobe", new CrimsonRobeArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("crimsonshoes", new CrimsonShoesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("tophat", new TopHatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("blazer", new BlazerArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("dressshoes", new DressShoesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("stylistshirt", new StylistShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("stylistshoes", new StylistShoesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("merchantshirt", new MerchantShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("merchantboots", new MerchantBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("captainshat", new CaptainsHatArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("captainsshirt", new CaptainsShirtArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("captainsboots", new CaptainsBootsArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("pirateeyepatch", new PirateEyePatchArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("piratebandana", new PirateBandanaArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("piratebandanawitheyepatch", new PirateBandanaWithEyePatchArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("pirateshirt", new PirateShirtArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("pirateboots", new PirateBootsArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("vulturemask", new VultureMaskArmorItem(), 200.0f, true);
        ItemRegistry.registerItem("plaguemask", new PlagueMaskArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("plaguerobe", new PlagueRobeArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("plagueboots", new PlagueBootsArmorItem(), 150.0f, true);
        ItemRegistry.registerItem("surgicalmask", new SurgicalMaskArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("trapperhat", new TrapperHatArmorItem(), 20.0f, true);
        ItemRegistry.registerItem("horsemask", new HorseMaskArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("horsecostumeshirt", new HorseCostumeShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("horsecostumeboots", new HorseCostumeBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("christmashat", (Item)new ChristmasHatArmorItem(), 10.0f, true, false, new String[0]);
        ItemRegistry.registerItem("partyhat", (Item)new PartyHatArmorItem(), 10.0f, true, false, new String[0]);
        ItemRegistry.registerItem("pumpkinmask", (Item)new PumpkinMaskArmorItem(), 10.0f, true, false, new String[0]);
        ItemRegistry.registerItem("chickenmask", new ChickenMaskArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("chickencostumeshirt", new ChickenCostumeShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("chickencostumeboots", new ChickenCostumeBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("frogmask", new FrogMaskArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("frogcostumeshirt", new FrogCostumeShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("frogcostumeboots", new FrogCostumeBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("alienmask", new AlienMaskArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("aliencostumeshirt", new AlienCostumeShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("aliencostumeboots", new AlienCostumeBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("sunglasses", new SunglassesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("jesterhat", new JesterHatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("jestershirt", new JesterShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("jesterboots", new JesterBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("hulahat", new HulaHatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("hulaskirtwithtop", new HulaShirtWithTopArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("hulaskirt", new HulaShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("swimsuit", new SwimSuitArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("swimtrunks", new SwimTrunksArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("snowhood", new SnowHoodArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("snowcloak", new SnowCloakArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("snowboots", new SnowBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("sailorhat", new SailorHatArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("sailorshirt", new SailorShirtArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("sailorshoes", new SailorShoesArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("spacehelmet", new SpaceHelmetArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("spacesuit", new SpaceSuitArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("spaceboots", new SpaceBootsArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("empressmask", new EmpressMaskArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("stylishflower", new StylishFlowerArmorItem(), 50.0f, true);
        ItemRegistry.registerItem("supporterhelmet", new SupporterHelmetArmorItem(), 0.0f, false);
        ItemRegistry.registerItem("supporterchestplate", new SupporterChestplateArmorItem(), 0.0f, false);
        ItemRegistry.registerItem("supporterboots", new SupporterBootsArmorItem(), 0.0f, false);
        ItemRegistry.registerItem("carp", new FishItem(250, Item.Rarity.COMMON, new String[]{"anycommonfish"}).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0f, true);
        ItemRegistry.registerItem("cod", new FishItem(250, Item.Rarity.COMMON, new String[]{"anycommonfish"}).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0f, true);
        ItemRegistry.registerItem("herring", new FishItem(250, Item.Rarity.COMMON, new String[]{"anycommonfish"}).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0f, true);
        ItemRegistry.registerItem("mackerel", new FishItem(250, Item.Rarity.COMMON, new String[]{"anycommonfish"}).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0f, true);
        ItemRegistry.registerItem("salmon", new FishItem(250, Item.Rarity.COMMON, new String[]{"anycommonfish"}).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0f, true);
        ItemRegistry.registerItem("trout", new FishItem(250, Item.Rarity.COMMON, new String[]{"anycommonfish"}).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0f, true);
        ItemRegistry.registerItem("tuna", new FishItem(250, Item.Rarity.COMMON, new String[]{"anycommonfish"}).spoilDuration(240).setItemCategory("consumable", "commonfish"), 12.0f, true);
        ItemRegistry.registerItem("gobfish", new FishItem(250, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "specialfish"), 16.0f, true);
        ItemRegistry.registerItem("terrorfish", new FishItem(250, Item.Rarity.RARE, new String[0]).setItemCategory("materials", "specialfish"), 30.0f, true);
        ItemRegistry.registerItem("halffish", new FishItem(250, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "specialfish"), 16.0f, true);
        ItemRegistry.registerItem("rockfish", new FishItem(250, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "specialfish"), 16.0f, true);
        ItemRegistry.registerItem("furfish", new FishItem(250, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "specialfish"), 16.0f, true);
        ItemRegistry.registerItem("icefish", new FishItem(250, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "specialfish"), 16.0f, true);
        ItemRegistry.registerItem("swampfish", new FishItem(250, Item.Rarity.UNCOMMON, new String[0]).setItemCategory("materials", "specialfish"), 16.0f, true);
        ItemRegistry.registerItem("spoiledfood", new MatItem(1000, Item.Rarity.NORMAL, "compostabletip", new String[]{"anycompostable"}).setItemCategory("materials"), 0.1f, true);
        ItemRegistry.getItem("mushroom").setItemCategory("materials", "flowers");
        ItemRegistry.registerItem("wheat", new GrainItem(250, Item.Rarity.NORMAL, new String[0]).cropTexture("wheat").spoilDuration(960).addGlobalIngredient("anycompostable"), 2.0f, true);
        ItemRegistry.registerItem("sugarbeet", new FoodMatItem(250, Item.Rarity.NORMAL, new String[0]).cropTexture("sugarbeet").spoilDuration(480).addGlobalIngredient("anycompostable"), 2.0f, true);
        ItemRegistry.registerItem("flour", new FoodMatItem(250, Item.Rarity.NORMAL, new String[0]).spoilDuration(960), 3.0f, true);
        ItemRegistry.registerItem("sugar", new FoodMatItem(250, Item.Rarity.NORMAL, new String[0]).spoilDuration(960), 3.0f, true);
        ItemRegistry.registerItem("honey", new FoodMatItem(250, Item.Rarity.NORMAL, new String[0]), 5.0f, true);
        ItemRegistry.registerItem("rice", new FoodMatItem(250, Item.Rarity.NORMAL, new String[0]).cropTexture("rice").spoilDuration(960), 3.0f, true);
        ItemRegistry.registerItem("groundcoffee", new FoodMatItem(250, Item.Rarity.NORMAL, new String[0]).cropTexture("coffee").spoilDuration(480), 6.0f, true);
        ItemRegistry.registerItem("beef", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, -10)).debuff().spoilDuration(240).addGlobalIngredient("anyrawmeat").setItemCategory("consumable", "rawfood"), 4.0f, true);
        ItemRegistry.registerItem("rawmutton", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, -10)).debuff().spoilDuration(240).addGlobalIngredient("anyrawmeat").setItemCategory("consumable", "rawfood"), 4.0f, true);
        ItemRegistry.registerItem("rawpork", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, -10)).debuff().spoilDuration(240).addGlobalIngredient("anyrawmeat").setItemCategory("consumable", "rawfood"), 6.0f, true);
        ItemRegistry.registerItem("rawchickenleg", new FoodConsumableItem(100, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, -10)).debuff().spoilDuration(240).addGlobalIngredient("anyrawmeat").setItemCategory("consumable", "rawfood"), 6.0f, true);
        ItemRegistry.registerItem("rabbitleg", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, -10)).debuff().spoilDuration(240).addGlobalIngredient("anyrawmeat").setItemCategory("consumable", "rawfood"), 6.0f, true);
        ItemRegistry.registerItem("duckbreast", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, -10)).debuff().spoilDuration(240).addGlobalIngredient("anyrawmeat").setItemCategory("consumable", "rawfood"), 6.0f, true);
        ItemRegistry.registerItem("frogleg", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, -10)).debuff().spoilDuration(240).addGlobalIngredient("anyrawmeat").setItemCategory("consumable", "rawfood"), 6.0f, true);
        ItemRegistry.registerItem("corn", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 10)).cropTexture("corn").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0f, true);
        ItemRegistry.registerItem("tomato", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.25f))).cropTexture("tomato").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0f, true);
        ItemRegistry.registerItem("cabbage", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.25f))).cropTexture("cabbage").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0f, true);
        ItemRegistry.registerItem("chilipepper", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.02f))).cropTexture("chilipepper").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0f, true);
        ItemRegistry.registerItem("eggplant", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.02f))).cropTexture("eggplant").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0f, true);
        ItemRegistry.registerItem("potato", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.25f))).cropTexture("potato").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0f, true);
        ItemRegistry.registerItem("carrot", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.05f))).cropTexture("carrot").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0f, true);
        ItemRegistry.registerItem("onion", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 20, 240, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.1f))).cropTexture("onion").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 3.0f, true);
        ItemRegistry.registerItem("pumpkin", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 30, 240, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 20)).cropTexture("pumpkin").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 5.0f, true);
        ItemRegistry.registerItem("strawberry", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.5f))).cropTexture("strawberry").spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 3.0f, true);
        ItemRegistry.registerItem("apple", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.02f))).spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 14.0f, true);
        ItemRegistry.registerItem("lemon", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 20, 240, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 20)).spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 16.0f, true);
        ItemRegistry.registerItem("coconut", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.25f))).spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 14.0f, true);
        ItemRegistry.registerItem("banana", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 20, 240, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.05f))).spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 16.0f, true);
        ItemRegistry.registerItem("blueberry", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.02f))).spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 4.0f, true);
        ItemRegistry.registerItem("raspberry", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue<Integer>(BuffModifiers.MAX_MANA_FLAT, 10)).spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 4.0f, true);
        ItemRegistry.registerItem("blackberry", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_SIMPLE, 10, 240, new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.02f))).spoilDuration(480).addGlobalIngredient("anycompostable", "anyfruit").setItemCategory("consumable", "rawfood"), 4.0f, true);
        ItemRegistry.registerItem("milk", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 20, 480, true, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 10)).spoilDuration(240).setItemCategory("consumable", "rawfood"), 2.0f, true);
        ItemRegistry.registerItem("egg", new EggFoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 10, 480, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, -10)).debuff().spoilDuration(240).setItemCategory("consumable", "rawfood"), 2.0f, true);
        ItemRegistry.registerItem("beet", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_SIMPLE, 15, 240, new ModifierValue<Float>(BuffModifiers.MINING_SPEED, Float.valueOf(0.1f))).cropTexture("beet").spoilDuration(480).addGlobalIngredient("anycompostable").setItemCategory("consumable", "rawfood"), 2.0f, true);
        ItemRegistry.registerItem("steak", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_FINE, 25, 240, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.02f))).spoilDuration(240).addGlobalIngredient("anycookedfood"), 5.0f, true);
        ItemRegistry.registerItem("roastedmutton", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_FINE, 25, 240, new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.02f))).spoilDuration(240).addGlobalIngredient("anycookedfood"), 5.0f, true);
        ItemRegistry.registerItem("roastedpork", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_FINE, 25, 480, new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.02f))).spoilDuration(240).addGlobalIngredient("anycookedfood"), 12.0f, true);
        ItemRegistry.registerItem("roastedrabbitleg", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 25, 480, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.05f))).spoilDuration(240).addGlobalIngredient("anycookedfood"), 7.0f, true);
        ItemRegistry.registerItem("roastedduckbreast", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 25, 480, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 20)).spoilDuration(240).addGlobalIngredient("anycookedfood"), 7.0f, true);
        ItemRegistry.registerItem("roastedfrogleg", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 25, 480, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.05f))).spoilDuration(240).addGlobalIngredient("anycookedfood"), 7.0f, true);
        ItemRegistry.registerItem("roastedfish", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 25, 480, new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.05f))).spoilDuration(240).addGlobalIngredient("anycookedfood"), 14.0f, true);
        ItemRegistry.registerItem("chickendrumstick", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 25, 1200, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 10), new ModifierValue<Integer>(BuffModifiers.MAX_RESILIENCE_FLAT, 10)).spoilDuration(120).addGlobalIngredient("anycookedfood"), 8.0f, true);
        ItemRegistry.registerItem("cheese", new FoodConsumableItem(250, Item.Rarity.NORMAL, Settler.FOOD_FINE, 25, 480, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 20)).spoilDuration(480).addGlobalIngredient("anycookedfood"), 5.0f, true);
        ItemRegistry.registerItem("candyapple", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 30, 480, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.05f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.02f))).spoilDuration(240).addGlobalIngredient("anycookedfood"), 22.0f, true);
        ItemRegistry.registerItem("bread", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 20, 480, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 20)).spoilDuration(480).addGlobalIngredient("anycookedfood"), 8.0f, true);
        ItemRegistry.registerItem("hardboiledegg", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 10, 480, new ModifierValue<Integer>(BuffModifiers.MAX_RESILIENCE_FLAT, 20)).spoilDuration(240).addGlobalIngredient("anycookedfood"), 9.0f, true);
        ItemRegistry.registerItem("friedegg", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 10, 480, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 10), new ModifierValue<Integer>(BuffModifiers.MAX_MANA_FLAT, 10)).spoilDuration(120).addGlobalIngredient("anycookedfood"), 6.0f, true);
        ItemRegistry.registerItem("candycane", new FoodConsumableItem(250, Item.Rarity.UNCOMMON, Settler.FOOD_FINE, 10, 300, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.15f))).spoilDuration(1080).addGlobalIngredient("anycookedfood"), 10.0f, true, false, new String[0]);
        ItemRegistry.registerItem("popcorn", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 10, 480, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 20)).spoilDuration(240).addGlobalIngredient("anycookedfood"), 10.0f, true);
        ItemRegistry.registerItem("donut", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 20, 480, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.05f))).spoilDuration(240).addGlobalIngredient("anycookedfood"), 10.0f, true);
        ItemRegistry.registerItem("cookies", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 25, 480, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.05f)), new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 10)).spoilDuration(360).addGlobalIngredient("anycookedfood"), 10.0f, true);
        ItemRegistry.registerItem("raspberryjuice", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 15, 480, true, new ModifierValue<Float>(BuffModifiers.COMBAT_MANA_REGEN, Float.valueOf(1.0f))).spoilDuration(240), 10.0f, true);
        ItemRegistry.registerItem("meatballs", new FoodConsumableItem(250, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.05f)), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.5f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 14.0f, true);
        ItemRegistry.registerItem("smokedfillet", new FoodConsumableItem(250, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.07f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.05f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 18.0f, true);
        ItemRegistry.registerItem("blueberrycake", new FoodConsumableItem(250, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.05f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.05f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 26.0f, true);
        ItemRegistry.registerItem("scrambledeggs", new FoodConsumableItem(250, Item.Rarity.UNCOMMON, Settler.FOOD_FINE, 20, 720, new ModifierValue<Integer>(BuffModifiers.MAX_RESILIENCE_FLAT, 20), new ModifierValue<Float>(BuffModifiers.RESILIENCE_GAIN, Float.valueOf(0.25f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 15.0f, true);
        ItemRegistry.registerItem("cheesybeetbowl", new FoodConsumableItem(250, Item.Rarity.COMMON, Settler.FOOD_FINE, 25, 960, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 30), new ModifierValue<Float>(BuffModifiers.MINING_SPEED, Float.valueOf(0.5f))).spoilDuration(240).addGlobalIngredient("anycookedfood"), 8.0f, true);
        ItemRegistry.registerItem("blackberryicecream", new FoodConsumableItem(250, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.05f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 24.0f, true);
        ItemRegistry.registerItem("fruitsmoothie", new FoodConsumableItem(250, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, true, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.05f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 36.0f, true);
        ItemRegistry.registerItem("fishtaco", new FoodConsumableItem(250, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.05f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.1f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 24.0f, true);
        ItemRegistry.registerItem("juniorburger", new FoodConsumableItem(250, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.1f)), new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 20), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.5f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 22.0f, true);
        ItemRegistry.registerItem("cheeseburger", new FoodConsumableItem(250, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.1f)), new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 50), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.5f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 26.0f, true);
        ItemRegistry.registerItem("omelette", new FoodConsumableItem(250, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 25, 1200, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.STAMINA_REGEN, Float.valueOf(0.2f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 28.0f, true);
        ItemRegistry.registerItem("nachos", new FoodConsumableItem(250, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.25f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 20.0f, true);
        ItemRegistry.registerItem("eggplantparmesan", new FoodConsumableItem(250, Item.Rarity.UNCOMMON, Settler.FOOD_GOURMET, 30, 720, new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.05f)), new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.1f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 20.0f, true);
        ItemRegistry.registerItem("raspberrymousse", new FoodConsumableItem(250, Item.Rarity.RARE, Settler.FOOD_GOURMET, 25, 960, new ModifierValue<Integer>(BuffModifiers.MAX_MANA_FLAT, 50), new ModifierValue<Float>(BuffModifiers.MANA_USAGE, Float.valueOf(-0.25f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 21.0f, true);
        ItemRegistry.registerItem("tropicalstew", new FoodConsumableItem(250, Item.Rarity.RARE, Settler.FOOD_GOURMET, 40, 960, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.05f)), new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 50), new ModifierValue<Integer>(BuffModifiers.ARMOR_FLAT, 4)).spoilDuration(120).addGlobalIngredient("anycookedfood"), 40.0f, true);
        ItemRegistry.registerItem("fishandchips", new FoodConsumableItem(250, Item.Rarity.RARE, Settler.FOOD_GOURMET, 40, 960, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.1f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 30.0f, true);
        ItemRegistry.registerItem("freshpotatosalad", new FoodConsumableItem(250, Item.Rarity.RARE, Settler.FOOD_GOURMET, 40, 960, new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.1f)), new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 30), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.5f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 45.0f, true);
        ItemRegistry.registerItem("rootsalad", new FoodConsumableItem(250, Item.Rarity.RARE, Settler.FOOD_GOURMET, 40, 720, new ModifierValue<Float>(BuffModifiers.MINING_SPEED, Float.valueOf(0.3f)), new ModifierValue<Float>(BuffModifiers.MINING_RANGE, Float.valueOf(1.0f)), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.3f))).spoilDuration(240).addGlobalIngredient("anycookedfood"), 12.0f, true);
        ItemRegistry.registerItem("hotdog", new FoodConsumableItem(250, Item.Rarity.RARE, Settler.FOOD_GOURMET, 40, 960, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 50), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(1.0f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 45.0f, true);
        ItemRegistry.registerItem("ricepudding", new FoodConsumableItem(250, Item.Rarity.RARE, Settler.FOOD_GOURMET, 40, 960, new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.15f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 22.0f, true);
        ItemRegistry.registerItem("minersstew", new FoodConsumableItem(250, Item.Rarity.RARE, Settler.FOOD_GOURMET, 50, 960, new ModifierValue<Float>(BuffModifiers.MINING_SPEED, Float.valueOf(0.5f)), new ModifierValue<Float>(BuffModifiers.MINING_RANGE, Float.valueOf(1.0f)), new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.15f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 24.0f, true);
        ItemRegistry.registerItem("chickencutletdish", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Integer>(BuffModifiers.ARMOR_FLAT, 25), new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(-0.1f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 40.0f, true);
        ItemRegistry.registerItem("sushirolls", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.15f)), new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 50)).spoilDuration(120).addGlobalIngredient("anycookedfood"), 32.0f, true);
        ItemRegistry.registerItem("friedpork", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.15f)), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.5f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 29.0f, true);
        ItemRegistry.registerItem("dessertpancakes", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 65), new ModifierValue<Integer>(BuffModifiers.MAX_RESILIENCE_FLAT, 65)).spoilDuration(120).addGlobalIngredient("anycookedfood"), 28.0f, true);
        ItemRegistry.registerItem("bananapudding", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.15f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.1f)), new ModifierValue<Integer>(BuffModifiers.ARMOR_FLAT, 6)).spoilDuration(120).addGlobalIngredient("anycookedfood"), 84.0f, true);
        ItemRegistry.registerItem("lemontart", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.1f)), new ModifierValue<Integer>(BuffModifiers.ARMOR_PEN_FLAT, 8)).spoilDuration(120).addGlobalIngredient("anycookedfood"), 90.0f, true);
        ItemRegistry.registerItem("parisiansteak", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Integer>(BuffModifiers.MAX_RESILIENCE_FLAT, 10), new ModifierValue<Float>(BuffModifiers.RESILIENCE_REGEN_FLAT, Float.valueOf(1.0f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 25.0f, true);
        ItemRegistry.registerItem("deepfriedchicken", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.15f)), new ModifierValue<Integer>(BuffModifiers.MAX_RESILIENCE_FLAT, 25), new ModifierValue<Float>(BuffModifiers.RESILIENCE_GAIN, Float.valueOf(0.25f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.15f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 35.0f, true);
        ItemRegistry.registerItem("spaghettibolognese", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.75f)), new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 50)).spoilDuration(120).addGlobalIngredient("anycookedfood"), 30.0f, true);
        ItemRegistry.registerItem("porktenderloin", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 80), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(1.0f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 35.0f, true);
        ItemRegistry.registerItem("beefgoulash", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.2f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.15f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 30.0f, true);
        ItemRegistry.registerItem("shishkebab", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.2f)), new ModifierValue<Integer>(BuffModifiers.ARMOR_FLAT, 8)).spoilDuration(120).addGlobalIngredient("anycookedfood"), 28.0f, true);
        ItemRegistry.registerItem("pumpkinpie", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.15f)), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.75f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 30.0f, true);
        ItemRegistry.registerItem("sweetlemonade", new FoodConsumableItem(250, Item.Rarity.RARE, Settler.FOOD_GOURMET, 25, 960, true, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.15f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.15f))).spoilDuration(120), 100.0f, true);
        ItemRegistry.registerItem("raspberryjam", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 30, 1200, new ModifierValue<Float>(BuffModifiers.MAGIC_CRIT_DAMAGE, Float.valueOf(0.2f)), new ModifierValue<Integer>(BuffModifiers.MAX_MANA_FLAT, 200), new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, -50)).spoilDuration(480).addGlobalIngredient("anycookedfood"), 32.0f, true);
        ItemRegistry.registerItem("raspberrypie", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 45, 1200, new ModifierValue<Float>(BuffModifiers.MAGIC_DAMAGE, Float.valueOf(0.2f)), new ModifierValue<Integer>(BuffModifiers.MAX_MANA_FLAT, 75), new ModifierValue<Float>(BuffModifiers.COMBAT_MANA_REGEN, Float.valueOf(1.5f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 34.0f, true);
        ItemRegistry.registerItem("strawberrypie", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.2f)), new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 50)).spoilDuration(120).addGlobalIngredient("anycookedfood"), 35.0f, true);
        ItemRegistry.registerItem("wildsalad", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_GOURMET, 50, 1200, new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.1f)), new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.15f)), new ModifierValue<Float>(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf(0.5f)), new ModifierValue<Integer>(BuffModifiers.MAX_MANA_FLAT, 40), new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.2f))).spoilDuration(120).addGlobalIngredient("anycookedfood"), 28.0f, true);
        ItemRegistry.registerItem("blackcoffee", new FoodConsumableItem(250, Item.Rarity.RARE, Settler.FOOD_FINE, 20, 1800, true, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.25f))).spoilDuration(120), 40.0f, true);
        ItemRegistry.registerItem("cappuccino", new FoodConsumableItem(250, Item.Rarity.EPIC, Settler.FOOD_FINE, 25, 2700, true, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.3f))).spoilDuration(120), 58.0f, true);
        ItemRegistry.registerItem("healthpotion", new HealthPotionItem(Item.Rarity.COMMON, 50, 0.1f), 5.0f, true);
        ItemRegistry.registerItem("greaterhealthpotion", new HealthPotionItem(Item.Rarity.UNCOMMON, 100, 0.2f), 6.0f, true);
        ItemRegistry.registerItem("superiorhealthpotion", new HealthPotionItem(Item.Rarity.RARE, 150, 0.3f), 10.0f, true);
        ItemRegistry.registerItem("manapotion", new ManaPotionItem(Item.Rarity.COMMON, 50), 5.0f, true);
        ItemRegistry.registerItem("greatermanapotion", new ManaPotionItem(Item.Rarity.UNCOMMON, 150), 6.0f, true);
        ItemRegistry.registerItem("superiormanapotion", new ManaPotionItem(Item.Rarity.RARE, 300), 10.0f, true);
        ItemRegistry.registerItem("revivalpotion", new RevivalPotion(), 50.0f, true);
        ItemRegistry.registerItem("speedpotion", new SpeedPotionItem(), 10.0f, true);
        ItemRegistry.registerItem("greaterspeedpotion", new GreaterSpeedPotionItem(), 20.0f, true);
        ItemRegistry.registerItem("healthregenpotion", new HealthRegenPotionItem(), 10.0f, true);
        ItemRegistry.registerItem("greaterhealthregenpotion", new GreaterHealthRegenPotionItem(), 20.0f, true);
        ItemRegistry.registerItem("resistancepotion", new ResistancePotionItem(), 15.0f, true);
        ItemRegistry.registerItem("greaterresistancepotion", new GreaterResistancePotionItem(), 30.0f, true);
        ItemRegistry.registerItem("battlepotion", new BattlePotionItem(), 35.0f, true);
        ItemRegistry.registerItem("greaterbattlepotion", new GreaterBattlePotionItem(), 50.0f, true);
        ItemRegistry.registerItem("attackspeedpotion", new AttackSpeedPotionItem(), 10.0f, true);
        ItemRegistry.registerItem("greaterattackspeedpotion", new GreaterAttackSpeedPotionItem(), 20.0f, true);
        ItemRegistry.registerItem("manaregenpotion", new ManaRegenPotionItem(), 10.0f, true);
        ItemRegistry.registerItem("greatermanaregenpotion", new GreaterManaRegenPotionItem(), 20.0f, true);
        ItemRegistry.registerItem("accuracypotion", new AccuracyPotionItem(), 10.0f, true);
        ItemRegistry.registerItem("greateraccuracypotion", new GreaterAccuracyPotionItem(), 20.0f, true);
        ItemRegistry.registerItem("rapidpotion", new RapidPotionItem(), 10.0f, true);
        ItemRegistry.registerItem("greaterrapidpotion", new GreaterRapidPotionItem(), 20.0f, true);
        ItemRegistry.registerItem("knockbackpotion", new KnockbackPotionItem(), 10.0f, true);
        ItemRegistry.registerItem("thornspotion", new ThornsPotionItem(), 10.0f, true);
        ItemRegistry.registerItem("fireresistancepotion", new FireResistancePotionItem(), 15.0f, true);
        ItemRegistry.registerItem("invisibilitypotion", new InvisibilityPotionItem(), 40.0f, true);
        ItemRegistry.registerItem("fishingpotion", new FishingPotionItem(), 20.0f, true);
        ItemRegistry.registerItem("greaterfishingpotion", new GreaterFishingPotionItem(), 40.0f, true);
        ItemRegistry.registerItem("miningpotion", new MiningPotionItem(), 20.0f, true);
        ItemRegistry.registerItem("greaterminingpotion", new GreaterMiningPotionItem(), 40.0f, true);
        ItemRegistry.registerItem("spelunkerpotion", new SpelunkerPotionItem(), 30.0f, true);
        ItemRegistry.registerItem("treasurepotion", new TreasurePotionItem(), 30.0f, true);
        ItemRegistry.registerItem("passivepotion", new PassivePotionItem(), 10.0f, true);
        ItemRegistry.registerItem("buildingpotion", new BuildingPotionItem(), 15.0f, true);
        ItemRegistry.registerItem("greaterbuildingpotion", new GreaterBuildingPotionItem(), 30.0f, true);
        ItemRegistry.registerItem("strengthpotion", new StrengthPotionItem(), 20.0f, true);
        ItemRegistry.registerItem("rangerpotion", new RangerPotionItem(), 20.0f, true);
        ItemRegistry.registerItem("wisdompotion", new WisdomPotionItem(), 20.0f, true);
        ItemRegistry.registerItem("minionpotion", new MinionPotionItem(), 20.0f, true);
        ItemRegistry.registerItem("webpotion", new WebPotionItem(), 20.0f, true);
        ItemRegistry.registerItem("recallscroll", new RecallScrollItem(), 25.0f, true);
        ItemRegistry.registerItem("teleportationscroll", new TeleportationScrollItem(), 100.0f, true);
        ItemRegistry.registerItem("recallflask", new RecallFlaskItem(), 400.0f, true);
        ItemRegistry.registerItem("stinkflask", new StinkFlaskItem(), 200.0f, true);
        ItemRegistry.registerItem("portalflask", new PortalFlaskItem(), 800.0f, true);
        ItemRegistry.registerItem("enchantingscroll", new EnchantingScrollItem(), 200.0f, true);
        ItemRegistry.registerItem("gatewaytablet", new GatewayTabletItem(), 500.0f, true);
        ItemRegistry.registerItem("craftingguide", new CraftingGuideBookItem(), 20.0f, true);
        ItemRegistry.registerItem("recipebook", new RecipeBookItem(), 400.0f, true);
        ItemRegistry.registerItem("voidpouch", new VoidPouchItem(), 400.0f, true);
        ItemRegistry.registerItem("voidbag", new VoidBagItem(), 1200.0f, true);
        ItemRegistry.registerItem("coinpouch", new CoinPouch(), 600.0f, true);
        ItemRegistry.registerItem("ammopouch", new AmmoPouch(), 200.0f, true);
        ItemRegistry.registerItem("ammobag", new AmmoBag(), 400.0f, true);
        ItemRegistry.registerItem("potionpouch", new PotionPouch(), 800.0f, true);
        ItemRegistry.registerItem("potionbag", new PotionBag(), 1000.0f, true);
        ItemRegistry.registerItem("lunchbox", new Lunchbox(), 400.0f, true);
        ItemRegistry.registerItem("seedpouch", new SeedPouch(), 500.0f, true);
        ItemRegistry.registerItem("tabletbox", new TabletBox(), 1000.0f, true);
        ItemRegistry.registerItem("portablemusicplayer", new PortableMusicPlayerItem(), 800.0f, true);
        ItemRegistry.registerItem("fireworkrocket", new FireworkPlaceableItem(), 5.0f, true);
        ItemRegistry.registerItem("woodboat", new WoodBoatMountItem(), 10.0f, true);
        ItemRegistry.registerItem("minecart", new MinecartMountItem(), 50.0f, true);
        ItemRegistry.registerItem("shears", new ShearsItem(), 50.0f, true);
        ItemRegistry.registerItem("bucket", new BucketItem(), 10.0f, true);
        ItemRegistry.registerItem("infinitewaterbucket", new InfiniteWaterBucketItem(), 250.0f, true);
        ItemRegistry.registerItem("rope", new RopeItem(), 50.0f, true);
        ItemRegistry.registerItem("infiniterope", new InfiniteRopeItem(), 250.0f, true);
        ItemRegistry.registerItem("binoculars", new BinocularsItem(), 100.0f, true);
        ItemRegistry.registerItem("telescope", new TelescopeItem(), 1000.0f, false);
        ItemRegistry.registerItem("strikebanner", new StrikeBannerItem(), 25.0f, true);
        ItemRegistry.registerItem("greenpresent", (Item)new PresentItem(), 10.0f, true, false, new String[0]);
        ItemRegistry.registerItem("bluepresent", (Item)new PresentItem(), 10.0f, true, false, new String[0]);
        ItemRegistry.registerItem("redpresent", (Item)new PresentItem(), 10.0f, true, false, new String[0]);
        ItemRegistry.registerItem("yellowpresent", (Item)new PresentItem(), 10.0f, true, false, new String[0]);
        ItemRegistry.registerItem("christmaspresent", (Item)new ChristmasPresentItem(), 10.0f, true, false, new String[0]);
        ItemRegistry.registerItem("greenwrappingpaper", (Item)new WrappingPaperItem("greenpresent"), 10.0f, true, false, new String[0]);
        ItemRegistry.registerItem("bluewrappingpaper", (Item)new WrappingPaperItem("bluepresent"), 10.0f, true, false, new String[0]);
        ItemRegistry.registerItem("redwrappingpaper", (Item)new WrappingPaperItem("redpresent"), 10.0f, true, false, new String[0]);
        ItemRegistry.registerItem("yellowwrappingpaper", (Item)new WrappingPaperItem("yellowpresent"), 10.0f, true, false, new String[0]);
        ItemRegistry.registerItem("landfill", new LandfillItem(), 0.1f, true);
        ItemRegistry.registerItem("grassseed", new GrassSeedItem("grasstile"), 2.0f, true);
        ItemRegistry.registerItem("overgrowngrassseed", new GrassSeedItem("overgrowngrasstile"), 2.0f, true);
        ItemRegistry.registerItem("swampgrassseed", new GrassSeedItem("swampgrasstile"), 2.0f, true);
        ItemRegistry.registerItem("overgrownswampgrassseed", new GrassSeedItem("overgrownswampgrasstile"), 2.0f, true);
        ItemRegistry.registerItem("plainsgrassseed", new GrassSeedItem("plainsgrasstile"), 2.0f, true);
        ItemRegistry.registerItem("overgrownplainsgrassseed", new GrassSeedItem("overgrownplainsgrasstile"), 2.0f, true);
        ItemRegistry.registerItem("fertilizer", new FertilizerPlaceableItem(), 4.0f, true);
        ItemRegistry.registerItem("mysteriousportal", new EvilsProtectorSpawnItem(), 50.0f, true);
        ItemRegistry.registerItem("royalegg", new QueenSpiderSpawnItem(), 50.0f, true);
        ItemRegistry.registerItem("voidcaller", new VoidWizardSpawnItem(), 50.0f, true);
        ItemRegistry.registerItem("boneoffering", new BoneOfferingItem(), 60.0f, true);
        ItemRegistry.registerItem("spikedfossil", new SwampGuardianSpawnItem(), 70.0f, true);
        ItemRegistry.registerItem("ancientstatue", new AncientVultureSpawnItem(), 100.0f, true);
        ItemRegistry.registerItem("shadowgate", new ReaperSpawnItem(), 140.0f, true);
        ItemRegistry.registerItem("icecrown", new CryoQueenSpawnItem(), 140.0f, true);
        ItemRegistry.registerItem("spiriturn", new CursedCroneSpawnItem(), 140.0f, true);
        ItemRegistry.registerItem("decayingleaf", new PestWardenSpawnItem(), 150.0f, true);
        ItemRegistry.registerItem("dragonsouls", new DragonSoulsItem(), 160.0f, true);
        ItemRegistry.registerItem("slimeeggs", new MotherSlimeSpawnItem(), 170.0f, false);
        ItemRegistry.registerItem("swarmsignal", new NightSwarmSpawnItem(), 180.0f, false);
        ItemRegistry.registerItem("crownofspiderkin", new SpiderEmpressSpawnItem(), 190.0f, false);
        ItemRegistry.registerItem("fishinghold", new FishingPoleHolding(), 0.0f, false);
        ItemRegistry.registerItem("itemhold", new ItemHolding(), 0.0f, false);
        ItemRegistry.registerItem("villagemap", new WorldPresetMapItem(Item.Rarity.RARE, LevelIdentifier.SURFACE_IDENTIFIER, 800, "village", new LocalMessage("biome", "npcvillage"), "surfacevillages"), 40.0f, true);
        ItemRegistry.registerItem("dungeonmap", new WorldPresetMapItem(Item.Rarity.RARE, LevelIdentifier.SURFACE_IDENTIFIER, 800, "voidwizard", new LocalMessage("biome", "dungeon"), "dungeonentrance"), 40.0f, true);
        ItemRegistry.registerItem("piratemap", new WorldPresetMapItem(Item.Rarity.RARE, LevelIdentifier.SURFACE_IDENTIFIER, 800, "piratebanner", new LocalMessage("biome", "piratevillage"), "piratevillages"), 120.0f, true);
        ItemRegistry.registerItem("honeybee", new HoneyBeePlaceableItem(false), 10.0f, true);
        ItemRegistry.registerItem("queenbee", new HoneyBeePlaceableItem(true), 100.0f, true);
        ItemRegistry.registerItem("apiaryframe", new ApiaryFramePlaceableItem(), 30.0f, true);
        ItemRegistry.registerItem("importedcow", new ImportedAnimalSpawnItem(1, true, "cow"), 150.0f, false);
        ItemRegistry.registerItem("importedsheep", new ImportedAnimalSpawnItem(1, true, "sheep"), 150.0f, false);
        ItemRegistry.registerItem("importedpig", new ImportedAnimalSpawnItem(1, true, "pig"), 150.0f, false);
        ItemRegistry.registerItem("eyeinaportal", new PetFollowerPlaceableItem("petevilminion", Item.Rarity.UNCOMMON), 150.0f, true);
        ItemRegistry.registerItem("weticicle", new PetFollowerPlaceableItem("petpenguin", Item.Rarity.UNCOMMON), 250.0f, true);
        ItemRegistry.registerItem("exoticseeds", new PetFollowerPlaceableItem("petparrot", Item.Rarity.UNCOMMON), 300.0f, true);
        ItemRegistry.registerItem("magicstilts", new PetFollowerPlaceableItem("petwalkingtorch", Item.Rarity.UNCOMMON), 500.0f, true);
        ItemRegistry.registerItem("petrock", new PetFollowerPlaceableItem("petcavelingelder", Item.Rarity.RARE), 5.0f, false);
        ItemRegistry.registerItem("squeakytoy", new PetFollowerPlaceableItem("petpug", Item.Rarity.UNCOMMON), 500.0f, false);
        ItemRegistry.registerItem("grizzlycub", new PetFollowerPlaceableItem("petgrizzlybearcub", Item.Rarity.RARE), 5.0f, true);
        ItemRegistry.registerItem("demonheart", new DemonHeartItem(), 30.0f, true);
        ItemRegistry.registerItem("spiderheart", new SpiderHeartItem(), 30.0f, true);
        ItemRegistry.registerItem("runicheart", new RunicHeartItem(), 30.0f, true);
        ItemRegistry.registerItem("guardianheart", new GuardianHeartItem(), 30.0f, true);
        ItemRegistry.registerItem("cryoheart", new CryoHeartItem(), 30.0f, true);
        ItemRegistry.registerItem("wardenheart", new WardenHeartItem(), 30.0f, true);
        ItemRegistry.registerItem("emptypendant", (Item)new TrinketSlotsIncreaseItem(5), 40.0f, true, true, "piratesheath", "wizardsocket");
        ItemRegistry.registerItem("piratesheath", (Item)new TrinketSlotsIncreaseItem(6), 75.0f, true, true, "wizardsocket");
        ItemRegistry.registerItem("wizardsocket", (Item)new TrinketSlotsIncreaseItem(7), 100.0f, true, true, new String[0]);
        ItemRegistry.registerItem("demoncloak", (Item)new ItemSetsIncreaseItem(2), 50.0f, true, true, "arcanearmory", "abyssalcloak");
        ItemRegistry.registerItem("arcanearmory", (Item)new ItemSetsIncreaseItem(3), 300.0f, true, true, "abyssalcloak");
        ItemRegistry.registerItem("abyssalcloak", (Item)new ItemSetsIncreaseItem(4), 200.0f, true, true, new String[0]);
        ItemRegistry.registerItem("lifeelixir", new LifeElixirItem(), 50.0f, true);
        ItemRegistry.registerItem("greaterlifeelixir", new GreaterLifeElixirItem(), 50.0f, true);
        ItemRegistry.registerItem("workinprogress", (Item)new WorkInProgressItem(), 1.0f, true, false, new String[0]);
        ItemRegistry.registerItem("inctrinkets", new TestChangeTrinketSlotsItem(1), 0.0f, false);
        ItemRegistry.registerItem("dectrinkets", new TestChangeTrinketSlotsItem(-1), 0.0f, false);
        ItemRegistry.registerItem("incitemsets", new TestChangeItemSetsItem(1), 0.0f, false);
        ItemRegistry.registerItem("decitemsets", new TestChangeItemSetsItem(-1), 0.0f, false);
        for (GameMusic music : MusicRegistry.getMusic()) {
            ItemRegistry.registerItem(music.getStringID() + "vinyl", (Item)new VinylItem(music), 50.0f, true, false, new String[0]);
        }
        ItemRegistry.registerItem("zombiearm", (Item)new ZombieArmQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("goblinring", (Item)new GoblinRingQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("swampeel", (Item)new SwampEelQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("babyshark", (Item)new BabySharkQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("babyswordfish", (Item)new BabySwordfishQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("frozenbeard", (Item)new FrozenBeardQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("spiderleg", (Item)new SpiderLegQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("crabclaw", (Item)new CrabClawQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("sandray", (Item)new SandRayQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("fakefangs", (Item)new FakeFangsQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("slimechunk", (Item)new SlimeChunkQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("enchantedcollar", (Item)new EnchantedCollarQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("apprenticescroll", (Item)new ApprenticeScrollQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("darkgem", (Item)new DarkGemQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("slimylauncher", (Item)new SlimyLauncherQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("mummysbandage", (Item)new MummysBandageQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("magicsand", (Item)new MagicSandQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("caveoyster", (Item)new CaveOysterQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("capturedspirit", (Item)new CapturedSpiritQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("pegleg", (Item)new PegLegQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("eyepatch", (Item)new EyePatchQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("rumbottle", (Item)new RumBottleQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("deepspiritswab", (Item)new DeepSpiritSwabQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("brokenlimb", (Item)new BrokenLimbQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("feraltail", (Item)new FeralTailQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("razoricicle", (Item)new RazorIcicleQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("slimesample", (Item)new SlimeSampleQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("soakedbow", (Item)new SoakedBowQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("wormtooth", (Item)new WormToothQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("crawlersfoot", (Item)new CrawlersFootQuestItem(), 0.0f, true, false, new String[0]);
        ItemRegistry.registerItem("swingspriteattack", new SwingSpriteAttackItem(), 0.0f, false);
        ItemRegistry.registerItem("workspriteattack", new WorkSpriteAttackItem(), 0.0f, false);
    }

    @Override
    protected void onRegister(ItemRegistryElement object, int id, String stringID, boolean isReplace) {
        for (Integer globalIngredient : object.item.getGlobalIngredients()) {
            GlobalIngredientRegistry.getGlobalIngredient(globalIngredient).registerItemID(id);
        }
        object.item.registerItemCategory();
    }

    @Override
    protected void onRegistryClose() {
        for (ItemRegistryElement item : this.getElements()) {
            item.displayName = item.item.getNewLocalization();
        }
        for (ItemRegistryElement element : this.getElements()) {
            element.item.onItemRegistryClosed();
        }
        totalItems = 0;
        totalItemsObtainable = 0;
        totalStatItemsObtainable = 0;
        totalTrinkets = 0;
        for (ItemRegistryElement e : this.getElements()) {
            ++totalItems;
            if (e.isObtainable) {
                ++totalItemsObtainable;
                if (e.countInStats) {
                    ++totalStatItemsObtainable;
                }
                if (e.item.isTrinketItem()) {
                    ++totalTrinkets;
                }
            }
            for (String itemStringID : e.isObtainedByOtherItemStringIDs) {
                ItemRegistryElement element = (ItemRegistryElement)this.getElement(itemStringID);
                if (element == null) continue;
                element.obtainsOtherItemStringIDs.add(e.getStringID());
            }
        }
    }

    public static void calculateBrokerValues() {
        RecipeBrokerValueCompute compute = Recipes.getDefaultBrokerValueCompute();
        for (ItemRegistryElement element : instance.getElements()) {
            if (!(element.setBrokerValue < 0.0f)) continue;
            compute.addItem(element.getID(), Math.abs(element.setBrokerValue));
        }
        compute.calculate((itemID, brokerValue) -> {
            ((ItemRegistryElement)ItemRegistry.instance.getElement((int)itemID)).brokerValue = brokerValue;
        });
    }

    public static Stream<Item> streamItems() {
        return instance.streamElements().map(e -> e.item);
    }

    public static List<Item> getItems() {
        return ItemRegistry.streamItems().collect(Collectors.toList());
    }

    public static List<Item> getCreativeItems() {
        return ItemRegistry.streamItems().filter(ItemRegistry::isValidCreativeItem).collect(Collectors.toList());
    }

    public static boolean isValidCreativeItem(Item item) {
        return ItemRegistry.isValidCreativeItem(item.getID());
    }

    public static boolean isValidCreativeItem(int id) {
        return ItemRegistry.isObtainableInCreative(id);
    }

    public static GameMessage getLocalization(int id) {
        if (id == -1) {
            return new StaticMessage("N/A");
        }
        return ((ItemRegistryElement)ItemRegistry.instance.getElement((int)id)).displayName;
    }

    public static String getDisplayName(int id) {
        if (id == -1) {
            return null;
        }
        return Objects.requireNonNull(ItemRegistry.getLocalization(id)).translate();
    }

    public static int registerItem(String stringID, Item item, float brokerValue, boolean isObtainable) {
        return ItemRegistry.registerItem(stringID, item, brokerValue, isObtainable, isObtainable, new String[0]);
    }

    public static int registerItem(String stringID, Item item, float brokerValue, boolean isObtainable, boolean countInStats, String ... isObtainedByOtherItemStringIDs) {
        return ItemRegistry.registerItem(stringID, item, brokerValue, isObtainable, countInStats, isObtainable, isObtainedByOtherItemStringIDs);
    }

    public static int registerItem(String stringID, Item item, float brokerValue, boolean isObtainable, boolean countInStats, boolean isObtainableInCreative, String ... isObtainedByOtherItemStringIDs) {
        return ItemRegistry.registerItem(stringID, item, brokerValue, isObtainable, countInStats, isObtainableInCreative, Arrays.asList(isObtainedByOtherItemStringIDs));
    }

    public static int registerItem(String stringID, Item item, float brokerValue, boolean isObtainable, boolean countInStats, List<String> isObtainedByOtherItemStringIDs) {
        return ItemRegistry.registerItem(stringID, item, brokerValue, isObtainable, countInStats, isObtainable, isObtainedByOtherItemStringIDs);
    }

    public static int registerItem(String stringID, Item item, float brokerValue, boolean isObtainable, boolean countInStats, boolean isObtainableInCreative, List<String> isObtainedByOtherItemStringIDs) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register items");
        }
        return instance.register(stringID, new ItemRegistryElement(item, brokerValue, isObtainable, isObtainableInCreative, countInStats, isObtainedByOtherItemStringIDs));
    }

    public static int replaceItem(String stringID, Item item, float brokerValue, boolean isObtainable) {
        return ItemRegistry.replaceItem(stringID, item, brokerValue, isObtainable, isObtainable, new String[0]);
    }

    public static int replaceItem(String stringID, Item item, float brokerValue, boolean isObtainable, boolean countInStats, String ... isObtainedByOtherItemStringIDs) {
        return ItemRegistry.replaceItem(stringID, item, brokerValue, isObtainable, countInStats, isObtainable, isObtainedByOtherItemStringIDs);
    }

    public static int replaceItem(String stringID, Item item, float brokerValue, boolean isObtainable, boolean countInStats, boolean isObtainableInCreative, String ... isObtainedByOtherItemStringIDs) {
        return ItemRegistry.replaceItem(stringID, item, brokerValue, isObtainable, countInStats, isObtainableInCreative, Arrays.asList(isObtainedByOtherItemStringIDs));
    }

    public static int replaceItem(String stringID, Item item, float brokerValue, boolean isObtainable, boolean countInStats, List<String> isObtainedByOtherItemStringIDs) {
        return ItemRegistry.replaceItem(stringID, item, brokerValue, isObtainable, countInStats, isObtainable, isObtainedByOtherItemStringIDs);
    }

    public static int replaceItem(String stringID, Item item, float brokerValue, boolean isObtainable, boolean countInStats, boolean isObtainableInCreative, List<String> isObtainedByOtherItemStringIDs) {
        return instance.replace(stringID, new ItemRegistryElement(item, brokerValue, isObtainable, isObtainableInCreative, countInStats, isObtainedByOtherItemStringIDs));
    }

    public static Item getItem(String stringID) {
        return ItemRegistry.getItem(ItemRegistry.getItemID(stringID));
    }

    public static Item getItem(int id) {
        if (id == -1) {
            return null;
        }
        ItemRegistryElement e = (ItemRegistryElement)instance.getElement(id);
        if (e == null) {
            return null;
        }
        return e.item;
    }

    public static int getItemID(String stringID) {
        try {
            return instance.getElementIDRaw(stringID);
        }
        catch (NoSuchElementException e) {
            return -1;
        }
    }

    public static String getItemStringID(int id) {
        return instance.getElementStringID(id);
    }

    public static boolean itemExists(String stringID) {
        try {
            return instance.getElementIDRaw(stringID) >= 0;
        }
        catch (NoSuchElementException e) {
            return false;
        }
    }

    public static boolean isObtainable(int id) {
        if (id == -1) {
            return false;
        }
        return ((ItemRegistryElement)ItemRegistry.instance.getElement((int)id)).isObtainable;
    }

    public static boolean isObtainableInCreative(int id) {
        if (id == -1) {
            return false;
        }
        return ((ItemRegistryElement)ItemRegistry.instance.getElement((int)id)).isObtainableInCreative;
    }

    public static List<String> getIsObtainedByOtherItemStringIDs(String itemStringID) {
        return ItemRegistry.getIsObtainedByOtherItemStringIDs(ItemRegistry.getItemID(itemStringID));
    }

    public static List<String> getIsObtainedByOtherItemStringIDs(int itemID) {
        if (itemID == -1) {
            return Collections.emptyList();
        }
        return ((ItemRegistryElement)ItemRegistry.instance.getElement((int)itemID)).isObtainedByOtherItemStringIDs;
    }

    public static List<String> getObtainsOtherItemStringIDs(String itemStringID) {
        return ItemRegistry.getObtainsOtherItemStringIDs(ItemRegistry.getItemID(itemStringID));
    }

    public static List<String> getObtainsOtherItemStringIDs(int itemID) {
        if (itemID == -1) {
            return Collections.emptyList();
        }
        return ((ItemRegistryElement)ItemRegistry.instance.getElement((int)itemID)).obtainsOtherItemStringIDs;
    }

    public static boolean countsInStats(int id) {
        if (id == -1) {
            return false;
        }
        return ((ItemRegistryElement)ItemRegistry.instance.getElement((int)id)).countInStats;
    }

    public static float getBrokerValue(int id) {
        if (id == -1) {
            return 0.0f;
        }
        return ((ItemRegistryElement)ItemRegistry.instance.getElement((int)id)).brokerValue;
    }

    public static LoadedMod getItemMod(int id) {
        if (id == -1) {
            return null;
        }
        return ((ItemRegistryElement)ItemRegistry.instance.getElement((int)id)).mod;
    }

    public static int getTotalItemsObtainable() {
        return totalItemsObtainable;
    }

    public static int getTotalStatItemsObtainable() {
        return totalStatItemsObtainable;
    }

    public static int getTotalItems() {
        return totalItems;
    }

    public static int getTotalTrinkets() {
        return totalTrinkets;
    }

    protected static class ItemRegistryElement
    implements IDDataContainer {
        public final Item item;
        public float setBrokerValue;
        public float brokerValue;
        public final boolean isObtainable;
        public final boolean isObtainableInCreative;
        public final boolean countInStats;
        public final List<String> isObtainedByOtherItemStringIDs;
        public final ArrayList<String> obtainsOtherItemStringIDs = new ArrayList();
        public final LoadedMod mod;
        public GameMessage displayName;

        public ItemRegistryElement(Item item, float brokerValue, boolean isObtainable, boolean isObtainableInCreative, boolean countInStats, List<String> isObtainedByOtherItemStringIDs) {
            this.item = item;
            this.setBrokerValue = brokerValue;
            this.brokerValue = Math.max(brokerValue, 0.0f);
            this.isObtainable = isObtainable;
            this.isObtainableInCreative = isObtainableInCreative;
            this.countInStats = countInStats;
            this.isObtainedByOtherItemStringIDs = isObtainedByOtherItemStringIDs;
            this.mod = LoadedMod.getRunningMod();
        }

        @Override
        public IDData getIDData() {
            return this.item.idData;
        }
    }
}

