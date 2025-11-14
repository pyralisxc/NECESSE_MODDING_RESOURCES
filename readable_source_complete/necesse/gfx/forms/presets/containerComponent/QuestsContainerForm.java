/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketQuestAbandon;
import necesse.engine.network.packet.PacketQuestShare;
import necesse.engine.network.packet.PacketQuestTrack;
import necesse.engine.quest.Quest;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormDropdownButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormQuestComponent;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.sidebar.TrackedSidebarForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.Container;

public class QuestsContainerForm
extends ContainerFormSwitcher<Container> {
    private final Form questsForm = this.addComponent(new Form(350, 400));
    private final ConfirmationForm confirmAbandonForm = this.addComponent(new ConfirmationForm("confirmAbandon"));
    private final FormContentBox questsList;

    public QuestsContainerForm(Client client, Container container) {
        super(client, container);
        this.questsForm.addComponent(new FormLocalLabel("ui", "quests", new FontOptions(20), 0, this.questsForm.getWidth() / 2, 5));
        this.questsList = this.questsForm.addComponent(new FormContentBox(0, 30, this.questsForm.getWidth(), this.questsForm.getHeight() - 32 - 64));
        this.updateQuestsList();
        this.questsForm.addComponent(new FormLocalCheckBox("ui", "tracknewquests", 5, this.questsForm.getHeight() - 60, (boolean)Settings.trackNewQuests.get())).onClicked(e -> {
            Settings.trackNewQuests.set(((FormCheckBox)e.from).checked);
            Settings.saveClientSettings();
        });
        this.questsForm.addComponent(new FormLocalTextButton("ui", "closebutton", 4, this.questsForm.getHeight() - 40, this.questsForm.getWidth() - 8)).onClicked(e -> client.closeContainer(true));
        this.makeCurrent(this.questsForm);
    }

    public void updateQuestsList() {
        this.questsList.clearComponents();
        if (this.client.quests.getTotalQuests() == 0) {
            this.questsList.addComponent(new FormLocalLabel("ui", "noquests", new FontOptions(16), 0, this.questsForm.getWidth() / 2, 30, this.questsForm.getWidth() - 10));
        } else {
            int y = 10;
            boolean addDivider = false;
            for (Quest quest : this.client.quests.getQuests()) {
                if (addDivider) {
                    FormBreakLine breakLine = this.questsList.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 5, y + 5, this.questsForm.getWidth() - 20, true));
                    breakLine.color = new Color(80, 80, 80);
                    y += 10;
                }
                FormQuestComponent questComponent = this.questsList.addComponent(new FormQuestComponent(10, y, this.questsForm.getWidth() - 20, this.container.client, quest));
                boolean isTracked = this.client.trackedQuests.contains(quest.getUniqueID()) || TrackedSidebarForm.isCachedQuestTracked(quest);
                FormLocalCheckBox trackQuest = this.questsList.addComponent(new FormLocalCheckBox("quests", "track", 10, (y += questComponent.getBoundingBox().height) + 4, isTracked));
                trackQuest.onClicked(e -> {
                    if (((FormCheckBox)e.from).checked) {
                        this.client.network.sendPacket(new PacketQuestTrack(quest.getUniqueID(), true));
                        this.client.trackedQuests.add(quest.getUniqueID());
                        TrackedSidebarForm.removeCachedTrackedQuest(this.client, quest.getUniqueID());
                        TrackedSidebarForm.updateTrackedList();
                    } else {
                        this.client.network.sendPacket(new PacketQuestTrack(quest.getUniqueID(), false));
                        this.client.trackedQuests.remove(quest.getUniqueID());
                        TrackedSidebarForm.removeCachedTrackedQuest(this.client, quest.getUniqueID());
                        TrackedSidebarForm.updateTrackedList();
                    }
                });
                Runnable abandonAction = () -> {
                    this.confirmAbandonForm.setupConfirmation(new LocalMessage("quests", "abandonconfirm"), () -> {
                        this.client.network.sendPacket(new PacketQuestAbandon(quest.getUniqueID()));
                        this.client.quests.removeQuest(quest);
                        this.client.trackedQuests.remove(quest.getUniqueID());
                        TrackedSidebarForm.updateTrackedList();
                        this.updateQuestsList();
                        this.makeCurrent(this.questsForm);
                    }, () -> this.makeCurrent(this.questsForm));
                    this.makeCurrent(this.confirmAbandonForm);
                };
                if (quest.canShare()) {
                    FormDropdownButton dropDown = this.questsList.addComponent(new FormDropdownButton(this.questsForm.getWidth() - 160, y, FormInputSize.SIZE_20, ButtonColor.BASE, 150, new LocalMessage("quests", "actions")));
                    if (quest.canShare()) {
                        dropDown.options.add(new LocalMessage("quests", "share"), () -> {
                            if (this.client.getTeam() == -1) {
                                return new LocalMessage("quests", "shareteamneeded");
                            }
                            return null;
                        }, () -> this.client.getTeam() != -1, () -> this.client.network.sendPacket(new PacketQuestShare(quest.getUniqueID())));
                    }
                    dropDown.options.add(new LocalMessage("quests", "abandon"), abandonAction);
                    y += dropDown.getBoundingBox().height + 2;
                } else {
                    FormLocalTextButton abandonButton = this.questsList.addComponent(new FormLocalTextButton("quests", "abandon", this.questsForm.getWidth() - 160, y, 150, FormInputSize.SIZE_20, ButtonColor.BASE));
                    abandonButton.onClicked(e -> abandonAction.run());
                    y += abandonButton.getBoundingBox().height + 2;
                }
                addDivider = true;
            }
            this.questsList.setContentBox(new Rectangle(0, 0, this.questsForm.getWidth(), y + 10));
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.questsForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    @Override
    public boolean shouldOpenInventory() {
        return false;
    }

    @Override
    public boolean shouldShowToolbar() {
        return false;
    }
}

