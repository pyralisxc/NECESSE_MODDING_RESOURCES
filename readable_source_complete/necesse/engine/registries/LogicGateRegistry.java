/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.stream.Collectors;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.item.Item;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.SimpleEntityLogicGate;
import necesse.level.gameLogicGate.entities.AndLogicGateEntity;
import necesse.level.gameLogicGate.entities.BufferLogicGateEntity;
import necesse.level.gameLogicGate.entities.CountdownLogicGateEntity;
import necesse.level.gameLogicGate.entities.CountdownRelayLogicGateEntity;
import necesse.level.gameLogicGate.entities.CounterLogicGateEntity;
import necesse.level.gameLogicGate.entities.DelayLogicGateEntity;
import necesse.level.gameLogicGate.entities.NAndLogicGateEntity;
import necesse.level.gameLogicGate.entities.NOrLogicGateEntity;
import necesse.level.gameLogicGate.entities.OrLogicGateEntity;
import necesse.level.gameLogicGate.entities.SRLatchLogicGateEntity;
import necesse.level.gameLogicGate.entities.SensorLogicGateEntity;
import necesse.level.gameLogicGate.entities.SoundLogicGateEntity;
import necesse.level.gameLogicGate.entities.TFlipFlopLogicGateEntity;
import necesse.level.gameLogicGate.entities.TimerLogicGateEntity;
import necesse.level.gameLogicGate.entities.XOrLogicGateEntity;

public class LogicGateRegistry
extends GameRegistry<LogicGateRegistryElement> {
    public static final LogicGateRegistry instance = new LogicGateRegistry();
    private static String[] stringIDs = null;

    private LogicGateRegistry() {
        super("LogicGate", 32762);
    }

    @Override
    public void registerCore() {
        LogicGateRegistry.registerLogicGate("andgate", new SimpleEntityLogicGate(AndLogicGateEntity::new), 2.0f, true);
        LogicGateRegistry.registerLogicGate("nandgate", new SimpleEntityLogicGate(NAndLogicGateEntity::new), 2.0f, true);
        LogicGateRegistry.registerLogicGate("orgate", new SimpleEntityLogicGate(OrLogicGateEntity::new), 2.0f, true);
        LogicGateRegistry.registerLogicGate("norgate", new SimpleEntityLogicGate(NOrLogicGateEntity::new), 2.0f, true);
        LogicGateRegistry.registerLogicGate("xorgate", new SimpleEntityLogicGate(XOrLogicGateEntity::new), 2.0f, true);
        LogicGateRegistry.registerLogicGate("tflipflopgate", new SimpleEntityLogicGate(TFlipFlopLogicGateEntity::new), 2.0f, true);
        LogicGateRegistry.registerLogicGate("srlatchgate", new SimpleEntityLogicGate(SRLatchLogicGateEntity::new), 2.0f, true);
        LogicGateRegistry.registerLogicGate("countergate", new SimpleEntityLogicGate(CounterLogicGateEntity::new), 10.0f, true);
        LogicGateRegistry.registerLogicGate("timergate", new SimpleEntityLogicGate(TimerLogicGateEntity::new), 10.0f, true);
        LogicGateRegistry.registerLogicGate("buffergate", new SimpleEntityLogicGate(BufferLogicGateEntity::new), 10.0f, true);
        LogicGateRegistry.registerLogicGate("delaygate", new SimpleEntityLogicGate(DelayLogicGateEntity::new), 10.0f, true);
        LogicGateRegistry.registerLogicGate("sensorgate", new SimpleEntityLogicGate(SensorLogicGateEntity::new), 10.0f, true);
        LogicGateRegistry.registerLogicGate("soundgate", new SimpleEntityLogicGate(SoundLogicGateEntity::new), 10.0f, true);
        LogicGateRegistry.registerLogicGate("countdowngate", new SimpleEntityLogicGate(CountdownLogicGateEntity::new), 10.0f, false);
        LogicGateRegistry.registerLogicGate("countdownrelay", new SimpleEntityLogicGate(CountdownRelayLogicGateEntity::new), 2.0f, false);
    }

    @Override
    protected void onRegister(LogicGateRegistryElement element, int id, String stringID, boolean isReplace) {
        Item item = element.gate.generateNewItem();
        if (item != null) {
            ItemRegistry.registerItem(element.gate.getStringID(), item, element.itemBrokerValue, element.itemObtainable);
        }
    }

    @Override
    protected void onRegistryClose() {
        this.streamElements().map(e -> e.gate).forEach(GameLogicGate::updateLocalDisplayName);
        for (LogicGateRegistryElement element : this.getElements()) {
            element.gate.onLogicGateRegistryClosed();
        }
        stringIDs = (String[])instance.streamElements().map(e -> e.gate.getStringID()).toArray(String[]::new);
    }

    public static int registerLogicGate(String stringID, GameLogicGate logicGate, float itemBrokerValue, boolean itemObtainable) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register logic gates");
        }
        return instance.register(stringID, new LogicGateRegistryElement(logicGate, itemBrokerValue, itemObtainable));
    }

    public static GameLogicGate getLogicGate(int id) {
        LogicGateRegistryElement element = (LogicGateRegistryElement)instance.getElement(id);
        return element == null ? null : element.gate;
    }

    public static int getLogicGateID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static String getLogicGateStringID(int id) {
        return instance.getElementStringID(id);
    }

    public static Iterable<GameLogicGate> getLogicGates() {
        return instance.streamElements().map(e -> e.gate).collect(Collectors.toList());
    }

    public static String[] getGateStringIDs() {
        if (stringIDs == null) {
            throw new IllegalStateException("LogicGateRegistry not yet closed");
        }
        return stringIDs;
    }

    protected static class LogicGateRegistryElement
    implements IDDataContainer {
        public final GameLogicGate gate;
        public final float itemBrokerValue;
        public final boolean itemObtainable;

        public LogicGateRegistryElement(GameLogicGate gate, float itemBrokerValue, boolean itemObtainable) {
            this.gate = gate;
            this.itemBrokerValue = itemBrokerValue;
            this.itemObtainable = itemObtainable;
        }

        @Override
        public IDData getIDData() {
            return this.gate.idData;
        }
    }
}

