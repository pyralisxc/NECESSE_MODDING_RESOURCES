/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ExpeditionMissionRegistry;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.HumanAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.ExpeditionList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class ExplorerHumanMob
extends HumanShop {
    public boolean isLost;

    public ExplorerHumanMob() {
        super(500, 200, "explorer");
        this.attackCooldown = 500;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0f);
        this.equipmentInventory.setItem(6, new InventoryItem("ironsword"));
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextBoolean(this.isLost);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.isLost = reader.getNextBoolean();
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<ExplorerHumanMob>(this, new HumanAI(320, true, false, 25000), new AIMover(HumanMob.humanPathIterations));
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(new InventoryItem("safarihat"));
        drawOptions.chestplate(new InventoryItem("safarishirt"));
        drawOptions.boots(new InventoryItem("safarishoes"));
    }

    @Override
    public int getRecruitedToSettlementUniqueID(ServerClient client) {
        if (this.isLost) {
            return 0;
        }
        return super.getRecruitedToSettlementUniqueID(client);
    }

    @Override
    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isTrapped()) {
            return Collections.emptyList();
        }
        GameRandom random = new GameRandom((long)this.getSettlerSeed() * 193L);
        if (this.isVisitor()) {
            return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(300, 500)));
        }
        LootTable items = new LootTable(new CountOfTicketLootItems(random.getIntBetween(1, 2), 100, new LootItem("quartz", Integer.MAX_VALUE), 100, new LootItem("ironore", Integer.MAX_VALUE), 100, new LootItem("goldore", Integer.MAX_VALUE), 100, new LootItem("quartz", Integer.MAX_VALUE)));
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, random.getIntBetween(200, 300), 0.2f, new LootItem("coin", Integer.MAX_VALUE), new Object[0]);
        out.addAll(GameLootUtils.getItemsValuedAt(random, random.getIntBetween(50, 75), 0.2f, items, new Object[0]));
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        if (!this.isSettler()) {
            return Stream.concat(Stream.of(new ModifierValue<Float>(BuffModifiers.INCOMING_DAMAGE_MOD, Float.valueOf(0.5f))), super.getDefaultModifiers());
        }
        return super.getDefaultModifiers();
    }

    @Override
    public boolean shouldSave() {
        if (this.isLost) {
            return false;
        }
        return super.shouldSave();
    }

    public void setLost(boolean isLost) {
        if (this.isLost == isLost) {
            return;
        }
        this.isLost = isLost;
        this.updateTeam();
    }

    @Override
    public void updateTeam() {
        if (this.getLevel() == null || this.isClient()) {
            return;
        }
        if (this.isLost) {
            this.team.set(-1);
            this.owner.set(-1L);
            return;
        }
        super.updateTeam();
    }

    @Override
    public void makeSettler(ServerSettlementData data, LevelSettler settler) {
        if (this.isLost) {
            this.setLost(false);
        }
        super.makeSettler(data, settler);
    }

    @Override
    public boolean canDoExpedition(SettlerExpedition expedition) {
        return ExpeditionMissionRegistry.explorerExpeditionIDs.contains(expedition.getID());
    }

    @Override
    public List<ExpeditionList> getPossibleExpeditions() {
        ServerSettlementData data = this.getSettlerSettlementServerData();
        if (data != null && this.isSettlerWithinSettlement(data.networkData)) {
            ExpeditionList explorerExpeditions = new ExpeditionList(new LocalMessage("ui", "explorerexpeditions"), new LocalMessage("ui", "explorerselectexp"), new LocalMessage("ui", "explorerexpcost"), new LocalMessage("ui", "explorershowmore"), data, this, ExpeditionMissionRegistry.explorerExpeditionIDs.stream().map(ExpeditionMissionRegistry::getExpedition).collect(Collectors.toList()));
            return Collections.singletonList(explorerExpeditions);
        }
        return super.getPossibleExpeditions();
    }

    @Override
    public GameMessage getWorkInvMessage() {
        if (this.completedMission) {
            return new LocalMessage("ui", "explorerexpcomplete");
        }
        return super.getWorkInvMessage();
    }

    @Override
    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        return this.getLocalMessages("explorertalk", 8);
    }
}

