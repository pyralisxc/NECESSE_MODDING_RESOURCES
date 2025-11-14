/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.events;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.ListIterator;
import necesse.engine.network.PacketReader;
import necesse.inventory.container.events.AdventurePartyChangedEvent;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.events.FullShopStockUpdateEvent;
import necesse.inventory.container.events.ShopContainerQuestUpdateEvent;
import necesse.inventory.container.events.ShopWealthUpdateEvent;
import necesse.inventory.container.events.SingleShopStockUpdateEvent;
import necesse.inventory.container.events.SleepUpdateContainerEvent;
import necesse.inventory.container.events.SpawnUpdateContainerEvent;
import necesse.inventory.container.events.StylistSettlersUpdateContainerEvent;
import necesse.inventory.container.mob.ShopContainerPartyResponseEvent;
import necesse.inventory.container.mob.ShopContainerPartyUpdateEvent;
import necesse.inventory.container.object.HomestoneUpdateEvent;
import necesse.inventory.container.object.missionBoard.AllMissionsUpdateEvent;
import necesse.inventory.container.object.missionBoard.AvailableExpeditionSettlersResponseEvent;
import necesse.inventory.container.object.missionBoard.DeletedMissionUpdateEvent;
import necesse.inventory.container.object.missionBoard.MissionBoardSlotsUpdateEvent;
import necesse.inventory.container.object.missionBoard.SingleMissionUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementForestryZoneUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementHusbandryZoneUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementMoveErrorEvent;
import necesse.inventory.container.settlement.events.SettlementNewSettlerDietChangedEvent;
import necesse.inventory.container.settlement.events.SettlementNewSettlerEquipmentFilterChangedEvent;
import necesse.inventory.container.settlement.events.SettlementNewSettlerRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementOpenSettlementListEvent;
import necesse.inventory.container.settlement.events.SettlementOpenStorageConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkZoneConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRecolorEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRenameEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerDietChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerDietsEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFiltersEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerListEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.inventory.container.settlement.events.SettlementStorageChangeAllowedEvent;
import necesse.inventory.container.settlement.events.SettlementStorageEvent;
import necesse.inventory.container.settlement.events.SettlementStorageFullUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementStorageLimitsEvent;
import necesse.inventory.container.settlement.events.SettlementStoragePriorityLimitEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneNameEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZonesEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeRemoveEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationsEvent;
import necesse.inventory.container.teams.PvPAllTeamsUpdateEvent;
import necesse.inventory.container.teams.PvPCurrentTeamUpdateEvent;
import necesse.inventory.container.teams.PvPJoinRequestUpdateEvent;
import necesse.inventory.container.teams.PvPMemberUpdateEvent;
import necesse.inventory.container.teams.PvPOwnerUpdateEvent;
import necesse.inventory.container.teams.PvPPublicUpdateEvent;
import necesse.inventory.container.travel.IslandsResponseEvent;

public class ContainerEventRegistry {
    public static ArrayList<RegistryElement> events = new ArrayList();

    public static int registerUpdate(Class<? extends ContainerEvent> eventClass) {
        try {
            if (events.stream().anyMatch(e -> e.eventClass == eventClass)) {
                throw new IllegalArgumentException("Cannot register the same update class twice");
            }
            int id = events.size();
            events.add(new RegistryElement(eventClass));
            return id;
        }
        catch (NoSuchMethodException e2) {
            throw new IllegalArgumentException("ContainerEvent class must have a (PacketReader) constructor");
        }
    }

    public static boolean replaceUpdate(Class<? extends ContainerEvent> lastEventClass, Class<? extends ContainerEvent> newEventClass) {
        ListIterator<RegistryElement> li = events.listIterator();
        while (li.hasNext()) {
            RegistryElement next = li.next();
            if (next.eventClass != lastEventClass) continue;
            try {
                li.set(new RegistryElement(newEventClass));
                return true;
            }
            catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("SettlementUpdate class must have a (PacketReader) constructor and a (SettlementLevelData, ServerClient, PacketReader) constructor");
            }
        }
        return false;
    }

    public static int getID(ContainerEvent event) {
        return ContainerEventRegistry.getID(event.getClass());
    }

    public static int getID(Class<? extends ContainerEvent> eventClass) {
        for (int i = 0; i < events.size(); ++i) {
            RegistryElement e = events.get(i);
            if (!e.eventClass.equals(eventClass)) continue;
            return i;
        }
        return -1;
    }

    public static Constructor<? extends ContainerEvent> getReaderConstructor(int id) {
        if (id < 0 || id >= events.size()) {
            throw new IllegalStateException("Does not have container event with ID " + id);
        }
        return ContainerEventRegistry.events.get((int)id).readerConstructor;
    }

    static {
        ContainerEventRegistry.registerUpdate(SettlementDataEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementRemovedEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementOpenSettlementListEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementMoveErrorEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementSettlersChangedEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementSettlerBasicsEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementSettlerListEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementOpenStorageConfigEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementStorageEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementSingleStorageEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementStorageChangeAllowedEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementStorageLimitsEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementStoragePriorityLimitEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementStorageFullUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementOpenWorkstationEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementWorkstationsEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementSingleWorkstationsEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementWorkstationEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementWorkstationRecipeUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementWorkstationRecipeRemoveEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementWorkZonesEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementWorkZoneRemovedEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementWorkZoneChangedEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementWorkZoneNameEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementOpenWorkZoneConfigEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementForestryZoneUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementHusbandryZoneUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementSettlerEquipmentFiltersEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementNewSettlerEquipmentFilterChangedEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementSettlerEquipmentFilterChangedEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementSettlerDietsEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementNewSettlerDietChangedEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementSettlerDietChangedEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementSettlerPrioritiesEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementSettlerPrioritiesChangedEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementRestrictZonesFullEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementNewSettlerRestrictZoneChangedEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementSettlerRestrictZoneChangedEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementRestrictZoneChangedEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementRestrictZoneRenameEvent.class);
        ContainerEventRegistry.registerUpdate(SettlementRestrictZoneRecolorEvent.class);
        ContainerEventRegistry.registerUpdate(MissionBoardSlotsUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(SingleMissionUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(DeletedMissionUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(AllMissionsUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(AvailableExpeditionSettlersResponseEvent.class);
        ContainerEventRegistry.registerUpdate(PvPCurrentTeamUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(PvPAllTeamsUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(PvPOwnerUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(PvPPublicUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(PvPMemberUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(PvPJoinRequestUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(HomestoneUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(SleepUpdateContainerEvent.class);
        ContainerEventRegistry.registerUpdate(SpawnUpdateContainerEvent.class);
        ContainerEventRegistry.registerUpdate(IslandsResponseEvent.class);
        ContainerEventRegistry.registerUpdate(ShopWealthUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(FullShopStockUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(SingleShopStockUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(ShopContainerQuestUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(ShopContainerPartyUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(ShopContainerPartyResponseEvent.class);
        ContainerEventRegistry.registerUpdate(AdventurePartyChangedEvent.class);
        ContainerEventRegistry.registerUpdate(StylistSettlersUpdateContainerEvent.class);
    }

    public static class RegistryElement {
        public final Class<? extends ContainerEvent> eventClass;
        public final Constructor<? extends ContainerEvent> readerConstructor;

        public RegistryElement(Class<? extends ContainerEvent> eventClass) throws NoSuchMethodException {
            this.eventClass = eventClass;
            this.readerConstructor = eventClass.getConstructor(PacketReader.class);
        }
    }
}

