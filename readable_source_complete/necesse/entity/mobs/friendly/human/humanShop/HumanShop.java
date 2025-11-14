/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanAngerTargetAINode;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.ShopContainerData;
import necesse.entity.mobs.friendly.human.humanShop.ShopManager;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.mob.ContainerQuest;
import necesse.inventory.container.mob.ShopContainer;
import necesse.level.maps.hudManager.floatText.MoodFloatText;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;

public abstract class HumanShop
extends HumanMob {
    public final ShopManager shop = new ShopManager();

    public HumanShop(int nonSettlerHealth, int settlerHealth, String settlerStringID) {
        super(nonSettlerHealth, settlerHealth, settlerStringID);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        SaveData shopSave = new SaveData("SHOP");
        this.shop.addSaveData(shopSave);
        if (!shopSave.isEmpty()) {
            save.addSaveData(shopSave);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        LoadData shopSave = save.getFirstLoadDataByName("SHOP");
        if (shopSave != null) {
            this.shop.applyLoadData(shopSave);
        }
    }

    @Override
    public void init() {
        super.init();
        this.shop.init(this.getLevel(), this.getSettlerSeed());
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.shop.serverTick(this.getWorldEntity(), this.getServer());
    }

    public GameMessage getWorkInvMessage() {
        return new LocalMessage("ui", "settlercarryitems");
    }

    public List<InventoryItem> getFreeItems() {
        return this.workInventory;
    }

    public ShopManager getShop() {
        if (this.isVisitor() && !this.isVisitorShop()) {
            return null;
        }
        return this.shop;
    }

    public int getShopHappiness() {
        return this.isSettler() ? this.settlerHappiness : 50;
    }

    public int getRandomHappinessPrice(GameRandom random, int best, int worst, int range) {
        return HumanShop.getRandomHappinessPrice(random, this.getShopHappiness(), best, worst, range);
    }

    public static int getRandomHappinessPrice(GameRandom random, int happiness, int best, int worst, int range) {
        float happinessPercent = (float)happiness / 100.0f;
        int totalRange = worst - best;
        range = GameMath.limit(range, 1, Math.abs(totalRange));
        if (totalRange < 0) {
            range = -range;
        }
        float rHappiness = Math.abs(GameMath.limit(happinessPercent, 0.0f, 1.0f) - 1.0f);
        int added = (int)(rHappiness * (float)(totalRange - range));
        int randomInt = range < 0 ? -random.nextInt(-range + 1) : random.nextInt(range + 1);
        return best + added + randomInt;
    }

    public static int getRandomHappinessMiddlePrice(GameRandom random, int happiness, int middlePrice, int rangeDivisor, int happinessDivisor) {
        int fullRange = middlePrice / rangeDivisor;
        int range = fullRange / happinessDivisor;
        return HumanShop.getRandomHappinessPrice(random, happiness, middlePrice - fullRange / 2, middlePrice + fullRange / 2, range);
    }

    public static void conditionSection(GameRandom random, boolean active, Consumer<GameRandom> ifActive, Consumer<GameRandom> ifInactive) {
        GameRandom nextRandom = random.nextSeeded();
        if (active) {
            if (ifActive != null) {
                ifActive.accept(nextRandom);
            }
        } else if (ifInactive != null) {
            ifInactive.accept(nextRandom);
        }
    }

    public static void conditionSection(GameRandom random, boolean active, Consumer<GameRandom> ifActive) {
        HumanShop.conditionSection(random, active, ifActive, null);
    }

    @Override
    public void interact(PlayerMob player) {
        super.interact(player);
        if (this.isServer() && player.isServerClient()) {
            ServerClient client = player.getServerClient();
            GameMessage error = this.getInteractError(player);
            if (error == null) {
                PacketOpenContainer openShopPacket = this.getOpenShopPacket(client.getServer(), client);
                if (openShopPacket != null) {
                    ContainerRegistry.openAndSendContainer(client, openShopPacket);
                }
            } else {
                client.sendChatMessage(error);
            }
        }
    }

    public PacketOpenContainer getOpenShopPacket(Server server, ServerClient client) {
        return this.getShopContainerData(client).getPacket(ContainerRegistry.SHOP_CONTAINER, this);
    }

    public GameMessage getDialogueIntroMessage(ServerClient client) {
        ArrayList<MoodFloatText.Moods> moods = this.getMoods();
        if (moods != null && !moods.isEmpty()) {
            return GameRandom.globalRandom.getOneOf(moods).moodMessage;
        }
        return this.getRandomMessage(GameRandom.globalRandom, client);
    }

    public final GameMessage getBaseRecruitError(ServerClient client) {
        int settlementUniqueID = this.getRecruitedToSettlementUniqueID(client);
        if (settlementUniqueID != 0) {
            CachedSettlementData settlement = SettlementsWorldData.getSettlementsData(this).getCachedData(settlementUniqueID);
            if (settlement != null) {
                if (!settlement.hasAccess(client)) {
                    return new LocalMessage("ui", this.isDowned() ? "settlerrevivenoperm" : "settlerrecruitnoperm");
                }
            } else {
                return new LocalMessage("ui", "settlementnotfound");
            }
        }
        return null;
    }

    public GameMessage getRecruitError(ServerClient client) {
        return null;
    }

    public List<InventoryItem> getRecruitItems(ServerClient client) {
        return null;
    }

    public boolean startInRecruitForm(ServerClient client) {
        return this.isDowned();
    }

    public boolean isVisitorShop() {
        return false;
    }

    public ArrayList<ContainerQuest> getQuests(ServerClient client) {
        return null;
    }

    public boolean completeQuest(ServerClient client, int questUniqueID) {
        return false;
    }

    public boolean skipQuest(ServerClient client, int questUniqueID) {
        return false;
    }

    public ShopContainerData getShopContainerData(ServerClient client) {
        GameMessage recruitError;
        GameMessage introMessage;
        boolean isVisitorShop = this.isVisitorShop();
        boolean isVisitor = this.isVisitor();
        boolean isSettler = this.isSettler();
        boolean isDowned = this.downedState != null;
        ArrayList<ContainerQuest> quests = isDowned || isVisitor ? null : this.getQuests(client);
        GameMessage gameMessage = introMessage = this.isTrapped() ? this.getTrappedMessage(client) : this.getDialogueIntroMessage(client);
        if (isSettler) {
            recruitError = null;
        } else {
            recruitError = this.getBaseRecruitError(client);
            if (!(this.isDowned() || isVisitor && !isVisitorShop || recruitError != null)) {
                recruitError = this.getRecruitError(client);
            }
        }
        ArrayList<InventoryItem> recruitItems = null;
        if (recruitError == null) {
            if (this.isDowned()) {
                recruitItems = new ArrayList<InventoryItem>();
                recruitItems.add(new InventoryItem("revivalpotion", 1));
            } else if (!isSettler) {
                recruitItems = this.getRecruitItems(client);
            }
        }
        ShopManager shopManager = isDowned || isVisitor && !isVisitorShop ? null : this.getShop();
        Packet content = ShopContainer.getContainerContent(this, client, this.getWorkInvMessage(), this.getMissionFailedMessage(), introMessage, quests, shopManager, recruitError, recruitItems, this.startInRecruitForm(client), this.getPossibleExpeditions(), this.workSettings);
        return new ShopContainerData(content, shopManager);
    }

    public GameMessage getMissionFailedMessage() {
        return this.missionFailedMessage;
    }

    public GameMessage getInteractError(Mob mob) {
        HumanAngerTargetAINode humanAngerHandler;
        if (this.buffManager.hasBuff(BuffRegistry.HUMAN_ANGRY) && (humanAngerHandler = this.ai.blackboard.getObject(HumanAngerTargetAINode.class, "humanAngerHandler")) != null && humanAngerHandler.enemies.contains(mob)) {
            return new LocalMessage("mobmsg", "angryshop");
        }
        if (this.isBusy()) {
            return new LocalMessage("mobmsg", "busyhuman", "name", this.getSettlerName());
        }
        return null;
    }
}

