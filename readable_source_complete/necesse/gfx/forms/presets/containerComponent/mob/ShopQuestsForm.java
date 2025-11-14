/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.quest.Quest;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormDialogueOption;
import necesse.gfx.forms.components.FormQuestComponent;
import necesse.gfx.forms.presets.containerComponent.mob.DialogueForm;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.container.events.ShopContainerQuestUpdateEvent;
import necesse.inventory.container.mob.ContainerQuest;
import necesse.inventory.container.mob.ShopContainer;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;

public abstract class ShopQuestsForm
extends DialogueForm {
    private final Client client;
    private final ShopContainer container;
    private final int maxHeight;
    private FormDialogueOption acceptQuest;
    private ContainerQuest currentQuest;

    public ShopQuestsForm(String name, int width, int height, Client client, ShopContainer container) {
        super(name, width, height, null, null);
        this.client = client;
        this.container = container;
        this.maxHeight = height;
        this.updateActive();
        container.onEvent(ShopContainerQuestUpdateEvent.class, e -> this.updateActive());
    }

    public void updateActive() {
        if (this.container.quests != null) {
            if (this.currentQuest != null && this.currentQuest.quest != null) {
                for (ContainerQuest cq : this.container.quests) {
                    if (cq.quest == null || cq.quest.getUniqueID() != this.currentQuest.quest.getUniqueID()) continue;
                    this.currentQuest = cq;
                    this.updateQuestDialogues();
                    return;
                }
            }
            for (ContainerQuest cq : this.container.quests) {
                if (cq.quest == null || !this.canCompleteQuest(cq.quest)) continue;
                this.currentQuest = cq;
                this.updateQuestDialogues();
                return;
            }
            for (ContainerQuest cq : this.container.quests) {
                if (cq.quest == null || this.hasQuest(cq.quest)) continue;
                this.currentQuest = cq;
                this.updateQuestDialogues();
                return;
            }
            this.currentQuest = !this.container.quests.isEmpty() ? this.container.quests.get(0) : null;
            this.updateQuestDialogues();
        }
    }

    private boolean hasQuest(Quest quest) {
        return this.client.quests.getQuest(quest.getUniqueID()) != null;
    }

    private boolean canCompleteQuest(Quest quest) {
        return quest.canComplete(this.container.getClient());
    }

    public void updateQuestDialogues() {
        GameMessage headerMessage = MobRegistry.getLocalization(this.container.humanShop.getID());
        this.acceptQuest = null;
        if (this.currentQuest != null) {
            boolean canComplete = this.currentQuest.quest != null && this.canCompleteQuest(this.currentQuest.quest);
            GameMessage introMessage = this.currentQuest.introMessage;
            if (introMessage == null) {
                introMessage = new LocalMessage("ui", "elderquestintro");
            }
            if (canComplete) {
                introMessage = new LocalMessage("ui", "elderquestdone");
            }
            this.reset(headerMessage, introMessage);
            if (this.currentQuest.quest != null) {
                FormQuestComponent questComponent = new FormQuestComponent(10, 10, this.content.getWidth() - 40, this.container.client, this.currentQuest.quest);
                questComponent.showTitle = false;
                questComponent.showHandIn = false;
                questComponent.titleColor = this.getInterfaceStyle().textBoxTextColor;
                questComponent.textColor = this.getInterfaceStyle().textBoxTextColor;
                FormContentBox questContent = new FormContentBox(10, 0, this.content.getWidth() - 20, questComponent.getBoundingBox().height + 20, GameBackground.textBox);
                questContent.addComponent(questComponent);
                this.content.addComponent(this.flow.nextY(questContent, 10));
                this.acceptQuest = this.addDialogueOption(new LocalMessage("ui", "elderacceptquest"), () -> {
                    if (this.canCompleteQuest(this.currentQuest.quest) || this.hasQuest(this.currentQuest.quest)) {
                        this.container.questCompleteButton.runAndSend(this.currentQuest.quest.getUniqueID());
                    } else {
                        this.container.questTakeButton.runAndSend(this.currentQuest.quest.getUniqueID());
                    }
                });
                if (!canComplete && this.currentQuest.canSkip) {
                    FormDialogueOption skipQuest = this.addDialogueOption(new LocalMessage("ui", "elderskipquest"), () -> this.container.questSkipButton.runAndSend(this.currentQuest.quest.getUniqueID()));
                    if (this.currentQuest.skipError != null) {
                        skipQuest.setActive(false);
                        skipQuest.tooltipsSupplier = () -> new StringTooltips(this.currentQuest.skipError.translate());
                    }
                }
                if (!canComplete && this.container.quests.size() > 1) {
                    for (int i = 0; i < this.container.quests.size(); ++i) {
                        ContainerQuest e = this.container.quests.get(i);
                        if (e != this.currentQuest && (e.quest == null || this.currentQuest.quest == null || e.quest.getUniqueID() != this.currentQuest.quest.getUniqueID())) continue;
                        int nextIndex = (i + 1) % this.container.quests.size();
                        ContainerQuest nextQuest = this.container.quests.get(nextIndex);
                        this.addDialogueOption(new LocalMessage("ui", "eldernotready"), () -> {
                            this.currentQuest = nextQuest;
                            this.updateQuestDialogues();
                        });
                    }
                }
            }
        } else {
            boolean settlementAccess;
            NetworkSettlementData settlement = this.container.humanShop.getSettlerSettlementNetworkData();
            boolean settlementActive = settlement != null && settlement.hasOwner() && !settlement.isDisbanded();
            boolean bl = settlementAccess = settlementActive && settlement.doIHaveAccess(this.client);
            this.reset(headerMessage, new LocalMessage("ui", settlementActive ? (settlementAccess ? "eldernoquest" : "eldernoaccess") : "eldernoactive"));
        }
        this.addDialogueOption(new LocalMessage("ui", "backbutton"), this::backPressed);
        this.setHeight(Math.max(this.getContentHeight() + 5, this.maxHeight));
        ContainerComponent.setPosFocus(this);
        this.updateAcceptQuestButtons();
        ControllerInput.submitNextRefreshFocusEvent();
    }

    public void updateAcceptQuestButtons() {
        if (this.acceptQuest != null && this.currentQuest.quest != null) {
            boolean hasQuest = this.hasQuest(this.currentQuest.quest);
            boolean canCompleteQuest = this.canCompleteQuest(this.currentQuest.quest);
            LocalMessage nextText = hasQuest || canCompleteQuest ? new LocalMessage("ui", "eldercompletequest") : new LocalMessage("ui", "elderacceptquest");
            if (!this.acceptQuest.getText().equals(nextText)) {
                this.acceptQuest.setText(nextText, Integer.MAX_VALUE);
                ControllerInput.submitNextRefreshFocusEvent();
            }
            this.acceptQuest.setActive(!hasQuest || canCompleteQuest);
        }
    }

    public abstract void backPressed();

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updateAcceptQuestButtons();
        super.draw(tickManager, perspective, renderBox);
    }
}

