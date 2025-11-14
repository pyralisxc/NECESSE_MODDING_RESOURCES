/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanWorkSetting;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkContainerHandler;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkSetting;
import necesse.entity.mobs.friendly.human.humanWorkSetting.SetPrioritySettingAction;
import necesse.entity.mobs.job.JobType;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.presets.containerComponent.mob.DialogueForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementJobPrioritiesForm;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerPrioritiesData;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesChangedEvent;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class HumanWorkPrioritiesSetting
extends HumanWorkSetting<AtomicBoolean> {
    public final HumanMob mob;

    public HumanWorkPrioritiesSetting(HumanMob mob) {
        this.mob = mob;
    }

    @Override
    public void writeContainerPacket(ServerClient client, PacketWriter writer) {
        ServerSettlementData settlementData = this.mob.isSettler() ? this.mob.getSettlerSettlementServerData() : null;
        boolean out = settlementData != null && settlementData.networkData.doesClientHaveAccess(client) && this.mob.jobTypeHandler.getTypePriorities().stream().anyMatch(tp -> tp.type.canChangePriority && !tp.disabledBySettler);
        writer.putNextBoolean(out);
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
        private final SetPrioritySettingAction setPrioritySetting;

        public ContainerHandler(final ShopContainer container, ShopContainer.ContainerWorkSetting<AtomicBoolean> setting) {
            super(container, setting);
            this.subscribed = new AtomicBoolean();
            this.subscribe = container.registerAction(new BooleanCustomAction(){

                @Override
                protected void run(boolean value) {
                    ContainerHandler.this.subscribed.set(value);
                    if (value) {
                        LevelSettler levelSettler;
                        container.subscribeEvent(SettlementSettlerPrioritiesChangedEvent.class, event -> event.settlementUniqueID == HumanWorkPrioritiesSetting.this.mob.getSettlementUniqueID() && event.mobUniqueID == HumanWorkPrioritiesSetting.this.mob.getUniqueID(), ContainerHandler.this.subscribed::get);
                        if (container.client.isServer() && (levelSettler = HumanWorkPrioritiesSetting.this.mob.levelSettler) != null) {
                            SettlementSettlerPrioritiesData data = new SettlementSettlerPrioritiesData(levelSettler, HumanWorkPrioritiesSetting.this.mob);
                            new SettlementSettlerPrioritiesChangedEvent(levelSettler.data, HumanWorkPrioritiesSetting.this.mob.getUniqueID(), true, data.priorities).applyAndSendToClient(container.client.getServerClient());
                        }
                    }
                }
            });
            this.setPrioritySetting = container.registerAction(new SetPrioritySettingAction(container));
        }

        @Override
        public boolean setupWorkForm(final ShopContainerForm<?> parent, final DialogueForm form) {
            if (((AtomicBoolean)this.setting.data).get()) {
                form.addDialogueOption(new LocalMessage("ui", "settlersetworkpriority"), () -> {
                    AtomicBoolean active = new AtomicBoolean(true);
                    HashMap priorities = new HashMap();
                    AtomicReference prioritiesForm = new AtomicReference();
                    this.container.onEvent(SettlementSettlerPrioritiesChangedEvent.class, event -> {
                        if (prioritiesForm.get() == null) {
                            if (event.includeDisabledBySettler) {
                                priorities.putAll(event.priorities);
                                prioritiesForm.set(new SettlementJobPrioritiesForm("priorities", 408, 250, HumanWorkPrioritiesSetting.this.mob, priorities){

                                    @Override
                                    public void onSubmitUpdate(JobType type, SettlementSettlerPrioritiesData.TypePriority typePriority) {
                                        ContainerHandler.this.setPrioritySetting.runAndSendChange(type, typePriority.priority, typePriority.disabledByPlayer);
                                    }

                                    @Override
                                    public void onBack() {
                                        parent.makeCurrent(form);
                                    }

                                    @Override
                                    public void onWindowResized(GameWindow window) {
                                        super.onWindowResized(window);
                                        ContainerComponent.setPosFocus(this);
                                    }
                                });
                                parent.addAndMakeCurrentTemporary((SettlementJobPrioritiesForm)prioritiesForm.get(), () -> {
                                    active.set(false);
                                    this.subscribe.runAndSend(false);
                                });
                                ((SettlementJobPrioritiesForm)prioritiesForm.get()).onWindowResized(WindowManager.getWindow());
                            }
                        } else {
                            for (Map.Entry<JobType, SettlementSettlerPrioritiesData.TypePriority> e : event.priorities.entrySet()) {
                                SettlementSettlerPrioritiesData.TypePriority priority = (SettlementSettlerPrioritiesData.TypePriority)priorities.get(e.getKey());
                                if (priority == null) continue;
                                priority.priority = e.getValue().priority;
                                priority.disabledByPlayer = e.getValue().disabledByPlayer;
                                ((SettlementJobPrioritiesForm)prioritiesForm.get()).updatePrioritiesContent();
                            }
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

