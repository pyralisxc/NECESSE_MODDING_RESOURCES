/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop.explorerMission;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.EmptyConstructorGameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.MoveToTile;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.ExpeditionMission;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.TradingMission;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.lootTable.LootTable;

public abstract class SettlerMission
implements IDDataContainer {
    public final IDData idData = new IDData();
    private boolean over;
    public static final ExplorerMissionRegistry registry = new ExplorerMissionRegistry();

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    public SettlerMission() {
        registry.applyIDData(this.getClass(), this.idData);
    }

    public abstract boolean canStart(HumanMob var1);

    public abstract void start(HumanMob var1);

    public GameMessage getActivityMessage(HumanMob mob) {
        return new LocalMessage("activities", "onmission");
    }

    public abstract void addSaveData(HumanMob var1, SaveData var2);

    public abstract void applySaveData(HumanMob var1, LoadData var2);

    public abstract void setupMovementPacket(HumanMob var1, PacketWriter var2);

    public abstract void applyMovementPacket(HumanMob var1, PacketReader var2);

    public abstract MoveToTile getMoveOutPoint(HumanMob var1);

    public void clientTick(HumanMob humanMob) {
    }

    public abstract void serverTick(HumanMob var1);

    public LootTable getLootTable(HumanMob mob) {
        return new LootTable();
    }

    public boolean isMobVisible(HumanMob mob) {
        return true;
    }

    public boolean isMobIdle(HumanMob mob) {
        return false;
    }

    public void markOver() {
        this.over = true;
    }

    public boolean isOver() {
        return this.over;
    }

    public void addDebugTooltips(ListGameTooltips tooltips) {
    }

    static {
        registry.registerCore();
    }

    public static class ExplorerMissionRegistry
    extends EmptyConstructorGameRegistry<SettlerMission> {
        private ExplorerMissionRegistry() {
            super("ExplorerMission", Short.MAX_VALUE);
        }

        @Override
        public void registerCore() {
            this.registerMission("expedition", ExpeditionMission.class);
            this.registerMission("trading", TradingMission.class);
        }

        @Override
        protected void onRegistryClose() {
        }

        public void registerMission(String stringID, Class<? extends SettlerMission> missionClass) {
            try {
                this.register(stringID, new ClassIDDataContainer<SettlerMission>(missionClass, new Class[0]));
            }
            catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(missionClass.getSimpleName() + " does not have a constructor with no parameters");
            }
        }
    }
}

