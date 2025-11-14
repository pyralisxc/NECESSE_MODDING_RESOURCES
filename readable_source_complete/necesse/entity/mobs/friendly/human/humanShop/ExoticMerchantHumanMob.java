/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicOptions;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.sound.GameMusic;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.Level;

public class ExoticMerchantHumanMob
extends HumanShop {
    public ExoticMerchantHumanMob() {
        super(500, 200, "exoticmerchant");
        this.attackCooldown = 500;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0f);
        this.equipmentInventory.setItem(6, new InventoryItem("ironsword"));
        SellingShopItem.ShopItemRequirement isChristmasRequirement = new SellingShopItem.ShopItemRequirement(){

            @Override
            public boolean test(GameRandom random, ServerClient client, HumanShop mob, GameBlackboard blackboard) {
                return GameSeasons.isChristmas();
            }
        };
        this.shop.addSellingItem("theeldersjinglejamvinyl", new SellingShopItem()).setRandomPrice(250, 350).addRequirement(isChristmasRequirement);
        this.shop.addSellingItem("christmastree", new SellingShopItem()).setRandomPrice(600, 800).addRequirement(isChristmasRequirement);
        this.shop.addSellingItem("christmaswreath", new SellingShopItem()).setRandomPrice(120, 150).addRequirement(isChristmasRequirement);
        this.shop.addSellingItem("snowlauncher", new SellingShopItem()).setRandomPrice(600, 700).addRequirement(isChristmasRequirement);
        this.shop.addSellingItem("greenwrappingpaper", new SellingShopItem()).setRandomPrice(20, 40).addRequirement(isChristmasRequirement);
        this.shop.addSellingItem("bluewrappingpaper", new SellingShopItem()).setRandomPrice(20, 40).addRequirement(isChristmasRequirement);
        this.shop.addSellingItem("redwrappingpaper", new SellingShopItem()).setRandomPrice(20, 40).addRequirement(isChristmasRequirement);
        this.shop.addSellingItem("yellowwrappingpaper", new SellingShopItem()).setRandomPrice(20, 40).addRequirement(isChristmasRequirement);
        this.shop.addSellingItem("rope", new SellingShopItem(10, 4)).setRandomPrice(150, 200);
        ArrayList<String> foodList = new ArrayList<String>(Arrays.asList("cheeseburger", "tropicalstew", "minersstew", "chickencutletdish", "sushirolls", "parisiansteak", "dessertpancakes", "deepfriedchicken", "porktenderloin", "pumpkinpie"));
        this.shop.addSellingItem("mealoftheday", new SellingShopItem(20, 4)).setItem((random, client, mob) -> new InventoryItem((String)random.getOneOf(foodList))).setRandomPrice(40, 60);
        this.shop.addSellingItem("piratemap", new SellingShopItem()).setRandomPrice(180, 220).addRequirement((random, client, mob, blackboard) -> !client.characterStats().biomes_visited.isBiomeVisited(BiomeRegistry.getBiome("piratevillage")));
        this.shop.addSellingItem("brainonastick", new SellingShopItem()).setRandomPrice(800, 1200);
        this.shop.addSellingItem("largerarepainting", new SellingShopItem()).setItem((random, client, mob) -> new InventoryItem(PaintingSelectionTable.getRandomLargeRarePaintingIDBasedOnWeight(random))).setRandomPrice(300, 450);
        this.shop.addSellingItem("binoculars", new SellingShopItem()).setRandomPrice(200, 300).addRandomAvailableRequirement(0.25f);
        this.shop.addSellingItem("boxingglovegun", new SellingShopItem()).setRandomPrice(200, 300).addRandomAvailableRequirement(0.25f);
        this.shop.addSellingItem("recipebook", new SellingShopItem()).setRandomPrice(500, 800).addRandomAvailableRequirement(0.25f);
        this.shop.addSellingItem("potionpouch", new SellingShopItem()).setRandomPrice(1000, 1200);
        this.shop.addSellingItem("foolsgambit", new SellingShopItem()).setRandomPrice(1200, 1600).addKilledMobRequirement("piratecaptain");
        this.shop.addSellingItem("ninjasmark", new SellingShopItem()).setRandomPrice(1200, 1600).addKilledMobRequirement("piratecaptain");
        TicketSystemList cosmetics = new TicketSystemList();
        cosmetics.addObject(100, "jumpingball");
        cosmetics.addObject(100, "hula");
        cosmetics.addObject(100, "swim");
        cosmetics.addObject(100, "snow");
        cosmetics.addObject(100, "sailor");
        cosmetics.addObject(50, "jester");
        cosmetics.addObject(50, "space");
        Function<String, SellingShopItem.ShopItemRequirement> cosmeticRequirement = stringID -> (random, client, mob, blackboard) -> {
            String decidedCosmetic = blackboard.getString("decidedCosmetic");
            if (decidedCosmetic == null) {
                decidedCosmetic = (String)cosmetics.getRandomObject(random);
                blackboard.set("decidedCosmetic", decidedCosmetic);
            }
            return decidedCosmetic.equals(stringID);
        };
        this.shop.addSellingItem("jumpingball", new SellingShopItem()).setRandomPrice(600, 800).addRequirement(cosmeticRequirement.apply("jumpingball"));
        this.shop.addSellingItem("hulahat", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("hula"));
        this.shop.addSellingItem("hulaskirtwithtop", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("hula"));
        this.shop.addSellingItem("hulaskirt", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("hula"));
        this.shop.addSellingItem("sunglasses", new SellingShopItem()).setRandomPrice(300, 600).addRequirement(cosmeticRequirement.apply("swim"));
        this.shop.addSellingItem("swimsuit", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("swim"));
        this.shop.addSellingItem("swimtrunks", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("swim"));
        this.shop.addSellingItem("snowhood", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("snow"));
        this.shop.addSellingItem("snowcloak", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("snow"));
        this.shop.addSellingItem("snowboots", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("snow"));
        this.shop.addSellingItem("sailorhat", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("sailor"));
        this.shop.addSellingItem("sailorshirt", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("sailor"));
        this.shop.addSellingItem("sailorshoes", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("sailor"));
        this.shop.addSellingItem("jesterhat", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("jester"));
        this.shop.addSellingItem("jestershirt", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("jester"));
        this.shop.addSellingItem("jesterboots", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("jester"));
        this.shop.addSellingItem("spacehelmet", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("space"));
        this.shop.addSellingItem("spacesuit", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("space"));
        this.shop.addSellingItem("spaceboots", new SellingShopItem()).setRandomPrice(200, 400).addRequirement(cosmeticRequirement.apply("space"));
        this.shop.addSellingItem("turban", new SellingShopItem()).setRandomPrice(150, 250);
        this.shop.addSellingItem("exoticshirt", new SellingShopItem()).setRandomPrice(150, 250);
        this.shop.addSellingItem("exoticshoes", new SellingShopItem()).setRandomPrice(150, 250);
        SellingShopItem.ShopItemRequirement musicDecider = (random, client, mob, blackboard) -> {
            if (!blackboard.containsKey("addMusicPlayer")) {
                blackboard.set("addMusicPlayer", random.getChance(0.25f));
            }
            return blackboard.getBoolean("addMusicPlayer");
        };
        this.shop.addSellingItem("musicplayer", new SellingShopItem()).setRandomPrice(800, 1200).addRequirement(musicDecider);
        this.shop.addSellingItem("portablemusicplayer", new SellingShopItem()).setRandomPrice(800, 1200).addRequirement(musicDecider);
        this.shop.addSellingItem("adventurebeginsvinyl", new SellingShopItem()).setRandomPrice(75, 125).addRequirement(musicDecider);
        this.shop.addSellingItem("homevinyl", new SellingShopItem()).setRandomPrice(75, 125).addRequirement(musicDecider);
        for (GameMusic music : MusicRegistry.getMusic()) {
            if (this.shop.sellingShop.getItem(music.getStringID() + "vinyl") != null) continue;
            this.shop.addSellingItem(music.getStringID() + "vinyl", new SellingShopItem()).setRandomPrice(75, 125).addRequirement(musicDecider).addRequirement((random, client, mob, blackboard) -> {
                Level level = mob.getLevel();
                AbstractMusicList musicList = level.getBiome(mob.getTileX(), mob.getTileY()).getLevelMusic(level, client.playerMob);
                for (MusicOptions musicOptions : musicList.getMusicInList()) {
                    if (musicOptions.music != music) continue;
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(new InventoryItem("turban"));
        drawOptions.chestplate(new InventoryItem("exoticshirt"));
        drawOptions.boots(new InventoryItem("exoticshoes"));
    }

    @Override
    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        return this.getLocalMessages("exoticmerchanttalk", 7);
    }

    @Override
    public long getShopSeed() {
        if (this.isVisitor()) {
            return this.getUniqueID();
        }
        return super.getShopSeed();
    }

    @Override
    public boolean isVisitorShop() {
        return true;
    }
}

