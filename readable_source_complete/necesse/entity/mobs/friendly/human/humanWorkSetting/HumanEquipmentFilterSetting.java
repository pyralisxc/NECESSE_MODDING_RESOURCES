/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanWorkSetting;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkContainerHandler;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkSetting;
import necesse.entity.mobs.friendly.human.humanWorkSetting.SetEquipmentFilterSettingAction;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.presets.containerComponent.mob.DialogueForm;
import necesse.gfx.forms.presets.containerComponent.mob.EquipmentFiltersForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class HumanEquipmentFilterSetting
extends HumanWorkSetting<AtomicBoolean> {
    public final HumanMob mob;

    public HumanEquipmentFilterSetting(HumanMob mob) {
        this.mob = mob;
    }

    @Override
    public void writeContainerPacket(ServerClient client, PacketWriter writer) {
        ServerSettlementData settlementData = this.mob.isSettler() ? this.mob.getSettlerSettlementServerData() : null;
        writer.putNextBoolean(settlementData != null && settlementData.networkData.doesClientHaveAccess(client) && this.mob.levelSettler != null);
    }

    @Override
    public AtomicBoolean readContainer(ShopContainer container, PacketReader reader) {
        return new AtomicBoolean(reader.getNextBoolean());
    }

    @Override
    public HumanWorkContainerHandler<AtomicBoolean> setupHandler(ShopContainer container, ShopContainer.ContainerWorkSetting<AtomicBoolean> setting) {
        return new ContainerHandler(container, setting);
    }

    private class ContainerHandler
    extends HumanWorkContainerHandler<AtomicBoolean> {
        private final AtomicBoolean subscribed;
        private final BooleanCustomAction subscribe;
        private final SetEquipmentFilterSettingAction setEquipmentFilterSetting;

        public ContainerHandler(final ShopContainer container, ShopContainer.ContainerWorkSetting<AtomicBoolean> setting) {
            super(container, setting);
            this.subscribed = new AtomicBoolean();
            this.subscribe = container.registerAction(new BooleanCustomAction(){

                @Override
                protected void run(boolean value) {
                    ContainerHandler.this.subscribed.set(value);
                    if (value) {
                        LevelSettler levelSettler;
                        container.subscribeEvent(SettlementSettlerEquipmentFilterChangedEvent.class, event -> event.settlementUniqueID == HumanEquipmentFilterSetting.this.mob.getSettlementUniqueID() && event.mobUniqueID == HumanEquipmentFilterSetting.this.mob.getUniqueID(), ContainerHandler.this.subscribed::get);
                        if (container.client.isServer() && (levelSettler = HumanEquipmentFilterSetting.this.mob.levelSettler) != null) {
                            new SettlementSettlerEquipmentFilterChangedEvent(levelSettler.data, HumanEquipmentFilterSetting.this.mob.getUniqueID(), levelSettler.preferArmorSets, ItemCategoriesFilterChange.fullChange(levelSettler.equipmentFilter)).applyAndSendToClient(container.client.getServerClient());
                        }
                    }
                }
            });
            this.setEquipmentFilterSetting = container.registerAction(new SetEquipmentFilterSettingAction(container));
        }

        @Override
        public boolean setupWorkForm(final ShopContainerForm<?> parent, DialogueForm form) {
            if (((AtomicBoolean)this.setting.data).get()) {
                parent.equipmentForm.filterEquipmentButtonPressed = () -> {
                    AtomicBoolean active = new AtomicBoolean(true);
                    AtomicReference openedForm = new AtomicReference();
                    ItemCategoriesFilter filter = new ItemCategoriesFilter(ItemCategory.equipmentMasterCategory, true);
                    final AtomicBoolean preferArmorSets = new AtomicBoolean();
                    this.container.onEvent(SettlementSettlerEquipmentFilterChangedEvent.class, event -> {
                        if (openedForm.get() == null) {
                            if (event.change != null) {
                                event.change.applyTo(filter);
                            }
                            preferArmorSets.set(event.preferArmorSets);
                            EquipmentFiltersForm equipmentFiltersForm = new EquipmentFiltersForm("equipmentFilter", 408, 300, HumanEquipmentFilterSetting.this.mob, filter, preferArmorSets::get, parent.getClient()){

                                @Override
                                public void onSetPreferArmorSets(boolean newPreferArmorSets) {
                                    preferArmorSets.set(newPreferArmorSets);
                                    ContainerHandler.this.setEquipmentFilterSetting.runAndSendPreferArmorSets(newPreferArmorSets);
                                }

                                @Override
                                public void onItemsChanged(Item[] items, boolean allowed) {
                                    ContainerHandler.this.setEquipmentFilterSetting.runAndSendChange(ItemCategoriesFilterChange.itemsAllowed(items, allowed));
                                }

                                @Override
                                public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
                                    ContainerHandler.this.setEquipmentFilterSetting.runAndSendChange(ItemCategoriesFilterChange.categoryAllowed(category, allowed));
                                }

                                @Override
                                public void onFullChange(ItemCategoriesFilter filter) {
                                    ContainerHandler.this.setEquipmentFilterSetting.runAndSendChange(ItemCategoriesFilterChange.fullChange(filter));
                                }

                                @Override
                                public void onButtonPressed() {
                                    parent.makeCurrent(parent.equipmentForm);
                                }

                                @Override
                                public void onWindowResized(GameWindow window) {
                                    super.onWindowResized(window);
                                    ContainerComponent.setPosFocus(this);
                                }
                            };
                            parent.addAndMakeCurrentTemporary(equipmentFiltersForm, () -> {
                                active.set(false);
                                openedForm.set(null);
                                this.subscribe.runAndSend(false);
                            });
                            equipmentFiltersForm.onWindowResized(WindowManager.getWindow());
                            openedForm.set(equipmentFiltersForm);
                        } else {
                            preferArmorSets.set(event.preferArmorSets);
                            if (event.change != null) {
                                event.change.applyTo(filter);
                            }
                        }
                    }, active::get);
                    this.subscribe.runAndSend(true);
                };
            }
            return false;
        }
    }
}

