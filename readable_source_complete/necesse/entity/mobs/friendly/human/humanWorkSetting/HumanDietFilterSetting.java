/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanWorkSetting;

import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkContainerHandler;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkSetting;
import necesse.entity.mobs.friendly.human.humanWorkSetting.SetDietSettingAction;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.presets.containerComponent.mob.DialogueForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.diets.SettlementDietFilterForm;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerDietChangedEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class HumanDietFilterSetting
extends HumanWorkSetting<AtomicBoolean> {
    public final HumanMob mob;

    public HumanDietFilterSetting(HumanMob mob) {
        this.mob = mob;
    }

    @Override
    public void writeContainerPacket(ServerClient client, PacketWriter writer) {
        ServerSettlementData settlementData = this.mob.isSettler() ? this.mob.getSettlerSettlementServerData() : null;
        writer.putNextBoolean(settlementData != null && settlementData.networkData.doesClientHaveAccess(client) && this.mob.doesEatFood());
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
        private final SetDietSettingAction setDietSetting;

        public ContainerHandler(final ShopContainer container, ShopContainer.ContainerWorkSetting<AtomicBoolean> setting) {
            super(container, setting);
            this.subscribed = new AtomicBoolean();
            this.subscribe = container.registerAction(new BooleanCustomAction(){

                @Override
                protected void run(boolean value) {
                    ContainerHandler.this.subscribed.set(value);
                    if (value) {
                        LevelSettler levelSettler;
                        container.subscribeEvent(SettlementSettlerDietChangedEvent.class, event -> event.settlementUniqueID == HumanDietFilterSetting.this.mob.getSettlementUniqueID() && event.mobUniqueID == HumanDietFilterSetting.this.mob.getUniqueID(), ContainerHandler.this.subscribed::get);
                        if (container.client.isServer() && (levelSettler = HumanDietFilterSetting.this.mob.levelSettler) != null) {
                            new SettlementSettlerDietChangedEvent(levelSettler.data, HumanDietFilterSetting.this.mob.getUniqueID(), ItemCategoriesFilterChange.fullChange(levelSettler.dietFilter)).applyAndSendToClient(container.client.getServerClient());
                        }
                    }
                }
            });
            this.setDietSetting = container.registerAction(new SetDietSettingAction(container));
        }

        @Override
        public boolean setupWorkForm(final ShopContainerForm<?> parent, final DialogueForm form) {
            if (((AtomicBoolean)this.setting.data).get()) {
                form.addDialogueOption(new LocalMessage("ui", "settlersetdiet"), () -> {
                    AtomicBoolean active = new AtomicBoolean(true);
                    AtomicBoolean openedForm = new AtomicBoolean(false);
                    ItemCategoriesFilter filter = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, true);
                    this.container.onEvent(SettlementSettlerDietChangedEvent.class, event -> {
                        event.change.applyTo(filter);
                        if (!openedForm.get()) {
                            SettlementDietFilterForm dietForm = new SettlementDietFilterForm("diet", 408, 250, HumanDietFilterSetting.this.mob, filter, parent.getClient()){

                                @Override
                                public void onItemsChanged(Item[] items, boolean allowed) {
                                    ContainerHandler.this.setDietSetting.runAndSendChange(ItemCategoriesFilterChange.itemsAllowed(items, allowed));
                                }

                                @Override
                                public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
                                    ContainerHandler.this.setDietSetting.runAndSendChange(ItemCategoriesFilterChange.categoryAllowed(category, allowed));
                                }

                                @Override
                                public void onFullChange(ItemCategoriesFilter filter) {
                                    ContainerHandler.this.setDietSetting.runAndSendChange(ItemCategoriesFilterChange.fullChange(filter));
                                }

                                @Override
                                public void onButtonPressed() {
                                    parent.makeCurrent(form);
                                }

                                @Override
                                public void onWindowResized(GameWindow window) {
                                    super.onWindowResized(window);
                                    ContainerComponent.setPosFocus(this);
                                }
                            };
                            parent.addAndMakeCurrentTemporary(dietForm, () -> {
                                active.set(false);
                                this.subscribe.runAndSend(false);
                            });
                            dietForm.onWindowResized(WindowManager.getWindow());
                            openedForm.set(true);
                        }
                    }, active::get);
                    this.subscribe.runAndSend(true);
                });
                return true;
            }
            return false;
        }
    }
}

