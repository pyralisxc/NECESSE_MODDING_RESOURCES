/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.notifications;

import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.level.maps.levelData.settlementData.notifications.FullInventorySettlementNotification;
import necesse.level.maps.levelData.settlementData.notifications.HungrySettlementNotification;
import necesse.level.maps.levelData.settlementData.notifications.LowFoodSettlementNotification;
import necesse.level.maps.levelData.settlementData.notifications.NoBedSettlementNotification;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNoFlagNotification;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotification;
import necesse.level.maps.levelData.settlementData.notifications.UnhappySettlementNotification;

public class SettlementNotificationRegistry
extends GameRegistry<NotificationRegistryElement> {
    public static final SettlementNotificationRegistry instance = new SettlementNotificationRegistry();

    private SettlementNotificationRegistry() {
        super("SettlementNotification", 32762);
    }

    @Override
    public void registerCore() {
        SettlementNotificationRegistry.registerNotification("lowfood", new LowFoodSettlementNotification());
        SettlementNotificationRegistry.registerNotification("hungry", new HungrySettlementNotification());
        SettlementNotificationRegistry.registerNotification("fullinventory", new FullInventorySettlementNotification());
        SettlementNotificationRegistry.registerNotification("nobed", new NoBedSettlementNotification());
        SettlementNotificationRegistry.registerNotification("noflag", new SettlementNoFlagNotification());
        SettlementNotificationRegistry.registerNotification("unhappy", new UnhappySettlementNotification());
    }

    @Override
    protected void onRegister(NotificationRegistryElement object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerNotification(String stringID, SettlementNotification notification) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register settlement notifications");
        }
        return instance.register(stringID, new NotificationRegistryElement(notification));
    }

    public static int replaceNotification(String stringID, SettlementNotification notification) {
        return instance.register(stringID, new NotificationRegistryElement(notification));
    }

    public static SettlementNotification getNotification(int id) {
        NotificationRegistryElement element;
        if (id >= instance.size()) {
            id = 0;
        }
        if ((element = (NotificationRegistryElement)instance.getElement(id)) == null) {
            throw new NullPointerException(SettlementNotificationRegistry.instance.objectCallName + " ID " + id + " seems to be missing or corrupted");
        }
        return element.notification;
    }

    public static int getNotificationID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static SettlementNotification getNotification(String stringID) {
        NotificationRegistryElement element = (NotificationRegistryElement)instance.getElement(stringID);
        if (element == null) {
            throw new NullPointerException(SettlementNotificationRegistry.instance.objectCallName + " stringID " + stringID + " seems to be missing or corrupted");
        }
        return element.notification;
    }

    protected static class NotificationRegistryElement
    implements IDDataContainer {
        public final SettlementNotification notification;

        public NotificationRegistryElement(SettlementNotification notification) {
            this.notification = notification;
        }

        @Override
        public IDData getIDData() {
            return this.notification.idData;
        }
    }
}

