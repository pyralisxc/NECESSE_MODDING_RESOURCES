/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.dlc.DLC;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.packet.PacketQuestGiverRequest;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.team.PlayerTeam;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.QuestGiver;
import necesse.entity.mobs.QuestMarkerOptions;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.HumanAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.gfx.GameHair;
import necesse.gfx.HumanGender;
import necesse.gfx.HumanLook;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.mob.ContainerQuest;
import necesse.inventory.container.mob.ElderContainer;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.ConsumeFoodLevelJob;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementClientQuests;
import necesse.level.maps.light.GameLight;

public class ElderHumanMob
extends HumanShop
implements QuestGiver {
    private final int elderHairWeight = 140;
    public static LootTable lootTable = new LootTable(new ConditionLootItem("petrock", (random, objects) -> GameSeasons.isAprilFools()), new OneOfLootItems(new LootItem("elderhat"), new LootItem("eldershirt"), new LootItem("eldershoes")));
    public final QuestGiver.QuestGiverObject quest = new QuestGiver.QuestGiverObject(this, false);

    public ElderHumanMob() {
        super(500, 500, "elder");
        this.setSpeed(30.0f);
        this.attackCooldown = 500;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0f);
        this.canJoinAdventureParties = false;
        this.jobTypeHandler.getJobHandler(ConsumeFoodLevelJob.class).disabledBySettler = true;
        this.jobTypeHandler.getTypePriorities().stream().filter(p -> !p.type.getStringID().equals("needs")).forEach(p -> {
            p.disabledBySettler = true;
        });
        this.equipmentInventory.setItem(6, new InventoryItem("coppersword"));
        this.shop.addSellingItem("revivalpotion", new SellingShopItem()).setRandomPrice(300, 400);
        this.shop.addSellingItem("supporterhelmet", new SellingShopItem()).setStaticPrice(250, 250).addDLCRequirement(DLC.SUPPORTER_PACK);
        this.shop.addSellingItem("supporterchestplate", new SellingShopItem()).setStaticPrice(250, 250).addDLCRequirement(DLC.SUPPORTER_PACK);
        this.shop.addSellingItem("supporterboots", new SellingShopItem()).setStaticPrice(250, 250).addDLCRequirement(DLC.SUPPORTER_PACK);
        this.shop.addSellingItem("ammopouch", new SellingShopItem()).setRandomPrice(400, 500).addQuestTierCompletedRequirement("evilsprotector");
        this.shop.addSellingItem("lunchbox", new SellingShopItem()).setRandomPrice(500, 600).addQuestTierCompletedRequirement("queenspider");
        this.shop.addSellingItem("voidpouch", new SellingShopItem()).setRandomPrice(600, 800).addQuestTierCompletedRequirement("voidwizard");
        this.shop.addSellingItem("recallflask", new SellingShopItem()).setRandomPrice(1000, 1200).addQuestTierCompletedRequirement("ancientvulture");
        this.shop.addSellingItem("shippingchest", new SellingShopItem()).setRandomPrice(1100, 1400).addQuestTierCompletedRequirement("chieftain");
        this.shop.addSellingItem("coinpouch", new SellingShopItem()).setRandomPrice(1200, 1500).addQuestTierCompletedRequirement("piratecaptain");
        this.shop.addSellingItem("hoverboard", new SellingShopItem()).setRandomPrice(1500, 1800).addQuestTierCompletedRequirement("reaper");
        this.shop.addSellingItem("bannerstand", new SellingShopItem()).setRandomPrice(250, 350).addQuestTierCompletedRequirement("cryoqueen");
        this.shop.addSellingItem("missionboard", new SellingShopItem()).setRandomPrice(1500, 2000).addQuestTierCompletedRequirement("thecursedcrone");
        this.shop.addSellingItem("portalflask", new SellingShopItem()).setRandomPrice(1600, 2400).addQuestTierCompletedRequirement("pestwarden");
        this.shop.addSellingItem("blinkscepter", new SellingShopItem()).setRandomPrice(1700, 2400).addQuestTierCompletedRequirement("sageandgrit");
        this.shop.addSellingItem("voidbag", new SellingShopItem()).setRandomPrice(1900, 2600).addQuestTierCompletedRequirement("fallenwizard");
        this.shop.addSellingItem("frogmask", new SellingShopItem()).setRandomPrice(200, 400).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.FOREST_SURFACE_CHALLENGES_ID);
        this.shop.addSellingItem("frogcostumeshirt", new SellingShopItem()).setRandomPrice(200, 400).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.FOREST_SURFACE_CHALLENGES_ID);
        this.shop.addSellingItem("frogcostumeboots", new SellingShopItem()).setRandomPrice(200, 400).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.FOREST_SURFACE_CHALLENGES_ID);
        this.shop.addSellingItem("constructionhammer", new SellingShopItem()).setRandomPrice(400, 600).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.FOREST_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("seedgun", new SellingShopItem()).setRandomPrice(1000, 1200).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.FOREST_DEEP_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("seedpouch", new SellingShopItem()).setRandomPrice(800, 1000).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.FOREST_DEEP_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("telescopicladder", new SellingShopItem()).setRandomPrice(400, 600).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.PLAINS_SURFACE_CHALLENGES_ID);
        this.shop.addSellingItem("hoverboots", new SellingShopItem()).setRandomPrice(300, 400).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.PLAINS_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("challengersbanner", new SellingShopItem()).setRandomPrice(2000, 2500).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.PLAINS_DEEP_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("bannerofpeace", new SellingShopItem()).setRandomPrice(400, 600).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.SNOW_SURFACE_CHALLENGES_ID);
        this.shop.addSellingItem("horsemask", new SellingShopItem()).setRandomPrice(200, 400).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.SNOW_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("horsecostumeshirt", new SellingShopItem()).setRandomPrice(200, 400).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.SNOW_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("horsecostumeboots", new SellingShopItem()).setRandomPrice(200, 400).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.SNOW_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("minersprosthetic", new SellingShopItem()).setRandomPrice(800, 1200).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.SNOW_DEEP_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("chickenmask", new SellingShopItem()).setRandomPrice(200, 400).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.DUNGEON_CHALLENGES_ID);
        this.shop.addSellingItem("chickencostumeshirt", new SellingShopItem()).setRandomPrice(200, 400).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.DUNGEON_CHALLENGES_ID);
        this.shop.addSellingItem("chickencostumeboots", new SellingShopItem()).setRandomPrice(200, 400).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.DUNGEON_CHALLENGES_ID);
        this.shop.addSellingItem("itemattractor", new SellingShopItem()).setRandomPrice(400, 600).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.SWAMP_SURFACE_CHALLENGES_ID);
        this.shop.addSellingItem("infiniterope", new SellingShopItem()).setRandomPrice(800, 1200).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.SWAMP_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("infinitewaterbucket", new SellingShopItem()).setRandomPrice(2000, 2500).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.SWAMP_DEEP_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("toolextender", new SellingShopItem()).setRandomPrice(400, 600).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.DESERT_SURFACE_CHALLENGES_ID);
        this.shop.addSellingItem("alienmask", new SellingShopItem()).setRandomPrice(200, 400).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.DESERT_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("aliencostumeshirt", new SellingShopItem()).setRandomPrice(200, 400).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.DESERT_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("aliencostumeboots", new SellingShopItem()).setRandomPrice(200, 400).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.DESERT_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("callofthesea", new SellingShopItem()).setRandomPrice(1600, 2000).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.DESERT_DEEP_CAVES_CHALLENGES_ID);
        this.shop.addSellingItem("bannerofwar", new SellingShopItem()).setRandomPrice(2500, 3000).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.TEMPLE_CHALLENGES_ID);
        this.shop.addSellingItem("teleportationstone", new SellingShopItem()).setRandomPrice(2000, 2500).addJournalChallengeCompleteRequirement(JournalChallengeRegistry.PIRATE_VILLAGE_CHALLENGES_ID);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<ElderHumanMob>(this, new HumanAI(480, true, true, 25000), new AIMover(humanPathIterations));
        if (this.isClient()) {
            this.getLevel().getClient().network.sendPacket(new PacketQuestGiverRequest(this.getUniqueID()));
        }
    }

    @Override
    public void randomizeLook(HumanLook look, HumanGender gender, GameRandom random) {
        this.gender = HumanGender.MALE;
        super.randomizeLook(look, this.gender, random);
        look.setFacialFeature(random.getOneOf(1, 3, 4));
        look.setHairColor(GameHair.getRandomHairColorAtSpecificWeight(random, 140));
        this.getRandomName(random);
    }

    @Override
    public QuestGiver.QuestGiverObject getQuestGiverObject() {
        return this.quest;
    }

    public SettlementClientQuests getSettlementClientQuests(ServerClient client) {
        if (!this.isSettler()) {
            return null;
        }
        NetworkSettlementData settlement = this.getSettlerSettlementNetworkData();
        if (settlement == null || !settlement.doesClientHaveAccess(client)) {
            return null;
        }
        ServerSettlementData data = this.getSettlerSettlementServerData();
        if (data != null && data.networkData.hasOwner() && !data.networkData.isDisbanded()) {
            return data.getClientsQuests(client);
        }
        return null;
    }

    @Override
    public List<Quest> getGivenQuests(ServerClient client) {
        ArrayList<Quest> quests = new ArrayList<Quest>();
        SettlementClientQuests clientQuests = this.getSettlementClientQuests(client);
        if (clientQuests != null) {
            Quest settlementQuest = clientQuests.getQuest();
            if (settlementQuest != null) {
                quests.add(settlementQuest);
            }
            quests.addAll(clientQuests.getTierQuests());
        }
        return quests;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        this.quest.addSaveData(save);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.quest.applyLoadData(save);
    }

    @Override
    public LootTable getLootTable() {
        return new LootTable(lootTable, super.getLootTable());
    }

    @Override
    public int getSettlerHappiness() {
        return 100;
    }

    @Override
    public boolean canSubmitNoBedNotification() {
        return false;
    }

    @Override
    public float getRegenFlat() {
        if (this.adventureParty.isInAdventureParty() && !this.isSettlerWithinSettlement()) {
            return super.getRegenFlat();
        }
        return super.getRegenFlat() * 2.5f;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.quest.clientTick();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.quest.serverTick();
    }

    @Override
    public void tickHunger() {
        this.hungerLevel = 1.0f;
    }

    @Override
    public void interact(PlayerMob player) {
        super.interact(player);
        if (this.isClient() && this.getLevel().getClient().getPlayer() == player) {
            this.getLevel().getClient().tutorial.elderInteracted();
        }
    }

    @Override
    public List<HappinessModifier> getHappinessModifiers() {
        return new ArrayList<HappinessModifier>();
    }

    @Override
    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(new InventoryItem("elderhat"));
        drawOptions.chestplate(new InventoryItem("eldershirt"));
        drawOptions.boots(new InventoryItem("eldershoes"));
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (GameSeasons.isAprilFools() && !this.isAttacking) {
            if (this.objectUser != null && !this.objectUser.drawsUser()) {
                return;
            }
            if (!this.isVisible()) {
                return;
            }
            int drawX = camera.getDrawX(x) - 32;
            int drawY = camera.getDrawY(y) - 48;
            GameLight light = level.getLightLevel(ElderHumanMob.getTileCoordinate(x), ElderHumanMob.getTileCoordinate(y));
            int dir = this.getDir();
            Point sprite = this.getAnimSprite(x, y, dir);
            final TextureDrawOptionsEnd options = MobRegistry.Textures.cavelingElder.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
            list.add(new MobDrawable(){

                @Override
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
        } else {
            super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        }
    }

    @Override
    public QuestMarkerOptions getMarkerOptions(PlayerMob perspective) {
        return QuestMarkerOptions.combine(this.quest.getMarkerOptions(perspective), super.getMarkerOptions(perspective));
    }

    @Override
    protected String getRandomName(GameRandom random) {
        if (random.getChance(0.1f)) {
            return ElderHumanMob.getRandomName(random, elderNames);
        }
        return super.getRandomName(random);
    }

    @Override
    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        if (GameSeasons.isAprilFools()) {
            return this.getLocalMessages("elderapriltalk", 5);
        }
        return this.getLocalMessages("eldertalk", 6);
    }

    @Override
    public ArrayList<ContainerQuest> getQuests(ServerClient client) {
        SettlementClientQuests quests = this.getSettlementClientQuests(client);
        if (quests != null) {
            ArrayList<Quest> tierQuests;
            ArrayList<ContainerQuest> out = new ArrayList<ContainerQuest>();
            Quest settlementQuest = quests.getQuest();
            if (settlementQuest != null) {
                out.add(new ContainerQuest(null, settlementQuest, true, quests.canSkipQuest()));
            }
            if (!(tierQuests = quests.getTierQuests()).isEmpty()) {
                for (Quest tierQuest : tierQuests) {
                    out.add(new ContainerQuest(null, tierQuest, false, null));
                }
            }
            return out;
        }
        return null;
    }

    @Override
    public boolean completeQuest(ServerClient client, int questUniqueID) {
        SettlementClientQuests quests = this.getSettlementClientQuests(client);
        if (quests != null) {
            Quest regularQuest = quests.getQuest();
            if (regularQuest != null && regularQuest.getUniqueID() == questUniqueID) {
                regularQuest.complete(client);
                for (InventoryItem item : quests.completeQuestAndGetReward()) {
                    this.getLevel().entityManager.pickups.add(item.getPickupEntity(this.getLevel(), client.playerMob.x, client.playerMob.y));
                }
                return true;
            }
            for (Quest tierQuest : quests.getTierQuests()) {
                if (tierQuest.getUniqueID() != questUniqueID) continue;
                tierQuest.complete(client);
                for (InventoryItem item : quests.completeTierQuestAndGetReward(tierQuest)) {
                    this.getLevel().entityManager.pickups.add(item.getPickupEntity(this.getLevel(), client.playerMob.x, client.playerMob.y));
                }
                quests.removeCurrentQuest();
                if (client.achievementsLoaded()) {
                    client.achievements().VILLAGE_HELPER.markCompleted(client);
                    PlayerTeam playerTeam = client.getPlayerTeam();
                    if (playerTeam != null) {
                        playerTeam.streamOnlineMembers(client.getServer()).filter(ServerClient::achievementsLoaded).forEach(c -> c.achievements().VILLAGE_HELPER.markCompleted((ServerClient)c));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean skipQuest(ServerClient client, int questUniqueID) {
        Quest regularQuest;
        SettlementClientQuests quests = this.getSettlementClientQuests(client);
        if (quests != null && (regularQuest = quests.getQuest()) != null && regularQuest.getUniqueID() == questUniqueID) {
            quests.removeCurrentQuest();
            return true;
        }
        return false;
    }

    @Override
    public PacketOpenContainer getOpenShopPacket(Server server, ServerClient client) {
        return ElderContainer.getElderContainerData(this, client).getPacket(ContainerRegistry.ELDER_CONTAINER, this);
    }
}

