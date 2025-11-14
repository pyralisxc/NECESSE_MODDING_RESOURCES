/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketShopContainerUpdate;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.ExpeditionList;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormDialogueOption;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.forms.presets.SelectSettlementContinueForm;
import necesse.gfx.forms.presets.containerComponent.PartyConfigForm;
import necesse.gfx.forms.presets.containerComponent.mob.DialogueForm;
import necesse.gfx.forms.presets.containerComponent.mob.EquipmentForm;
import necesse.gfx.forms.presets.containerComponent.mob.MobContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerBuyingForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerSellingForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopEquipmentForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopQuestsForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;
import necesse.inventory.container.Container;
import necesse.inventory.container.mob.ContainerExpedition;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.mob.ShopContainerPartyResponseEvent;
import necesse.inventory.container.mob.ShopContainerPartyUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementOpenSettlementListEvent;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class ShopContainerForm<T extends ShopContainer>
extends MobContainerFormSwitcher<T> {
    public static int defaultHeight = 220;
    public int width;
    public int height;
    public GameMessage header;
    public DialogueForm workInvForm;
    public DialogueForm missionFailedForm;
    public DialogueForm dialogueForm;
    public DialogueForm moodForm;
    public DialogueForm recruitForm;
    public DialogueForm whatCanYouDoForm;
    public DialogueForm workForm;
    public ShopQuestsForm questForm;
    public DialogueForm[] expeditionsForms;
    public DialogueForm[] unavailableExpeditionsForms;
    public DialogueForm expeditionFocus;
    public ShopContainerSellingForm sellingForm;
    public ShopContainerBuyingForm buyingForm;
    public EquipmentForm equipmentForm;
    public boolean waitingForPartyConfirm;
    public DialogueForm partyResponseForm;
    public PartyConfigForm partyConfigForm;
    public FormDialogueOption acceptRecruitButton;
    public int expeditionBuyCost;
    public FormDialogueOption expeditionBuyButton;

    protected ShopContainerForm(Client client, T container, int width, int height, int maxExpeditionsHeight) {
        super(client, container);
        this.width = width;
        this.height = height;
        this.header = MobRegistry.getLocalization(((ShopContainer)((Object)container)).humanShop.getID());
        ((Container)((Object)container)).onEvent(ShopContainerPartyUpdateEvent.class, event -> {
            this.updateDialogue();
            this.onWindowResized(WindowManager.getWindow());
        });
        this.workInvForm = this.addComponent(new DialogueForm("workinv", width, height, this.header, null));
        this.updateWorkInventoryForm();
        this.dialogueForm = this.addComponent(new DialogueForm("dialogue", width, height, this.header, ((ShopContainer)((Object)container)).introMessage));
        this.moodForm = this.addComponent(new DialogueForm("mood", width + 40, height, null, null));
        if (((ShopContainer)((Object)container)).happinessModifiers != null) {
            this.moodForm.reset(this.header, (content, flow) -> {
                if (container.humanShop.isOnStrike()) {
                    LocalMessage strikeMessage = new LocalMessage("settlement", "onstrike");
                    if (container.hungerStrike) {
                        strikeMessage = new LocalMessage("settlement", "onstrikehungry");
                    }
                    DialogueForm.addText(content, flow, strikeMessage);
                } else if (container.humanShop.isOnWorkBreak()) {
                    DialogueForm.addText(content, flow, new LocalMessage("settlement", "onworkbreak"));
                }
                FontOptions fontOptions = new FontOptions(16);
                int prefixWidth = Math.max(FontManager.bit.getWidthCeil("-", fontOptions), FontManager.bit.getWidthCeil("+", fontOptions));
                int numberWidth = FontManager.bit.getWidthCeil(Integer.toString(Math.abs(container.settlerHappiness)), fontOptions);
                for (HappinessModifier modifier : container.happinessModifiers) {
                    numberWidth = Math.max(numberWidth, FontManager.bit.getWidthCeil(Integer.toString(Math.abs(modifier.happiness)), fontOptions));
                }
                String prefix = container.settlerHappiness == 0 ? "" : (container.settlerHappiness > 0 ? "+" : "-");
                int x = 5;
                DialogueForm.addText(content, flow.split(), new StaticMessage(prefix), x + prefixWidth / 2 - FontManager.bit.getWidthCeil(prefix, fontOptions) / 2, Integer.MAX_VALUE);
                DialogueForm.addText(content, flow.split(), new StaticMessage(Integer.toString(Math.abs(container.settlerHappiness))), x += prefixWidth, Integer.MAX_VALUE);
                DialogueForm.addText(content, flow, Settler.getMood(container.settlerHappiness).getDescription(), x += numberWidth + 10, content.getWidth() - x - 5);
                if (!container.happinessModifiers.isEmpty()) {
                    flow.next(16);
                    DialogueForm.addText(content, flow, new LocalMessage("settlement", "thoughts"), prefixWidth + numberWidth + 10, content.getWidth() - prefixWidth - numberWidth - 20);
                    for (HappinessModifier modifier : container.happinessModifiers) {
                        String prefix2 = modifier.happiness == 0 ? "" : (modifier.happiness > 0 ? "+" : "-");
                        int x2 = 5;
                        DialogueForm.addText(content, flow.split(), new StaticMessage(prefix2), x2 + prefixWidth / 2 - FontManager.bit.getWidthCeil(prefix2, fontOptions) / 2, Integer.MAX_VALUE);
                        DialogueForm.addText(content, flow.split(), new StaticMessage(Integer.toString(Math.abs(modifier.happiness))), x2 += prefixWidth, Integer.MAX_VALUE);
                        DialogueForm.addText(content, flow, modifier.description, x2 += numberWidth + 10, content.getWidth() - x2 - 5);
                    }
                }
            });
        }
        this.moodForm.addComponent(new FormContentIconButton(this.moodForm.getWidth() - 40, 4, FormInputSize.SIZE_20, ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().button_help_20, new GameMessage[]{new LocalMessage("ui", "settlershelpbutton")}){

            @Override
            public GameTooltips getTooltips(PlayerMob perspective) {
                return new StringTooltips().add(Localization.translate("settlement", "moodhelp"), 400);
            }
        }, 100);
        this.moodForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> this.makeCurrent(this.dialogueForm));
        this.partyResponseForm = this.addComponent(new DialogueForm("partyresponse", width, height, this.header, null));
        this.partyConfigForm = this.addComponent(new PartyConfigForm(client, (Container)((Object)container), ((ShopContainer)((Object)container)).PARTY_SLOTS_START, ((ShopContainer)((Object)container)).PARTY_SLOTS_END, width, null, () -> {
            container.setIsInPartyConfig.runAndSend(false);
            this.makeCurrent(this.dialogueForm);
        }, () -> ContainerComponent.setPosFocus(this.partyConfigForm)));
        ((Container)((Object)container)).onEvent(ShopContainerPartyResponseEvent.class, e -> {
            if (this.waitingForPartyConfirm) {
                if (e.error != null) {
                    this.partyResponseForm.reset(this.header, e.error);
                    this.partyResponseForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> this.makeCurrent(this.dialogueForm));
                    this.makeCurrent(this.partyResponseForm);
                } else {
                    container.setIsInPartyConfig.runAndSend(true);
                    this.makeCurrent(this.partyConfigForm);
                }
                this.waitingForPartyConfirm = false;
            }
        });
        if (((ShopContainer)((Object)container)).hasSettlerAccess) {
            this.equipmentForm = this.addComponent(new ShopEquipmentForm(client, (ShopContainer)((Object)container), e -> {
                container.setIsInEquipment.runAndSend(false);
                this.makeCurrent(this.dialogueForm);
            }));
        }
        this.workForm = this.addComponent(new DialogueForm("work", width, height, this.header, null));
        this.questForm = this.addComponent(new ShopQuestsForm("quest", width, height, client, (ShopContainer)((Object)container)){

            @Override
            public void backPressed() {
                ShopContainerForm.this.makeCurrent(ShopContainerForm.this.dialogueForm);
            }
        });
        this.sellingForm = this.addComponent(new ShopContainerSellingForm((ShopContainer)((Object)container), width, height){

            @Override
            public void onBackPressed() {
                ShopContainerForm.this.makeCurrent(ShopContainerForm.this.dialogueForm);
            }
        });
        this.buyingForm = this.addComponent(new ShopContainerBuyingForm((ShopContainer)((Object)container), width, height){

            @Override
            public void onBackPressed() {
                ShopContainerForm.this.makeCurrent(ShopContainerForm.this.dialogueForm);
            }
        });
        this.recruitForm = this.addComponent(new DialogueForm("recruit", width, height, this.header, ((ShopContainer)((Object)container)).getRecruitMessage()));
        if (((ShopContainer)((Object)container)).recruitItems != null) {
            LocalMessage recruitMessage = new LocalMessage("ui", "acceptbutton");
            if (((ShopContainer)((Object)container)).humanShop.isDowned()) {
                recruitMessage = new LocalMessage("ui", "settlerrevivebutton");
            }
            this.acceptRecruitButton = this.recruitForm.addDialogueOption(recruitMessage, () -> {
                NoticeForm loadingForm = new NoticeForm("loadingsettlements", 300, 400);
                loadingForm.setButtonCooldown(-2);
                loadingForm.setupNotice(new LocalMessage("ui", "loadingdotdot"));
                this.addComponent(loadingForm, (form, switched) -> {
                    if (!switched.booleanValue()) {
                        this.removeComponent(form);
                    }
                });
                this.makeCurrent(loadingForm);
                container.acceptRecruitAction.runAndSend();
            });
        } else {
            this.acceptRecruitButton = null;
        }
        this.updateAcceptRecruitButton();
        if (((ShopContainer)((Object)container)).humanShop.isDowned()) {
            this.recruitForm.addDialogueOption(new LocalMessage("ui", "settlergoodbye"), () -> client.closeContainer(true));
        } else {
            this.recruitForm.addDialogueOption(new LocalMessage("ui", "settlerwhatcanyoudo"), () -> this.makeCurrent(this.whatCanYouDoForm));
            this.recruitForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> this.makeCurrent(this.dialogueForm));
        }
        this.recruitForm.setHeight(Math.max(this.recruitForm.getContentHeight() + 5, height));
        GameMessageBuilder builder = new GameMessageBuilder();
        ((ShopContainer)((Object)container)).humanShop.jobTypeHandler.getTypePriorities().stream().filter(t -> !t.disabledBySettler && t.type.displayName != null).sorted(Comparator.comparingInt(t -> t.type.getID())).forEachOrdered(t -> builder.append("\n\t- ").append(t.type.displayName));
        if (((ShopContainer)((Object)container)).humanShop.canJoinAdventureParties) {
            builder.append("\n").append("ui", "settlericanadventure");
        }
        LocalMessage whatCanYouDoMessage = new LocalMessage("ui", "settlericando", "list", builder);
        this.whatCanYouDoForm = this.addComponent(new DialogueForm("whatcanyoudo", width, height, this.header, whatCanYouDoMessage));
        this.whatCanYouDoForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> this.makeCurrent(this.recruitForm));
        this.whatCanYouDoForm.setHeight(Math.max(this.whatCanYouDoForm.getContentHeight() + 5, height));
        this.missionFailedForm = this.addComponent(new DialogueForm("missionFailed", width, height, this.header, ((ShopContainer)((Object)container)).missionFailedMessage != null ? ((ShopContainer)((Object)container)).missionFailedMessage : new LocalMessage("ui", "settlermissionfailed")));
        this.missionFailedForm.addDialogueOption(new LocalMessage("ui", "continuebutton"), () -> {
            container.continueFailedMissionAction.runAndSend();
            this.makeCurrent(this.dialogueForm);
        });
        this.expeditionsForms = new DialogueForm[((ShopContainer)((Object)container)).expeditionLists.size()];
        this.unavailableExpeditionsForms = new DialogueForm[((ShopContainer)((Object)container)).expeditionLists.size()];
        for (int i = 0; i < ((ShopContainer)((Object)container)).expeditionLists.size(); ++i) {
            DialogueForm unavailableForm;
            DialogueForm expeditionsForm;
            int listIndex = i;
            ExpeditionList expeditionList = ((ShopContainer)((Object)container)).expeditionLists.get(i);
            this.expeditionsForms[i] = expeditionsForm = this.addComponent(new DialogueForm("expeditions" + i, width, maxExpeditionsHeight, this.header, expeditionList.selectMessage));
            expeditionsForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> this.makeCurrent(this.dialogueForm));
            this.unavailableExpeditionsForms[i] = unavailableForm = this.addComponent(new DialogueForm("expeditionsUnavailable" + i, width, maxExpeditionsHeight, this.header, expeditionList.selectMessage));
            unavailableForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> this.makeCurrent(expeditionsForm));
            boolean addedUnavailable = false;
            for (int j = 0; j < expeditionList.expeditions.size(); ++j) {
                int expeditionIndex = j;
                ContainerExpedition expedition = expeditionList.expeditions.get(j);
                if (expedition.available) {
                    expeditionsForm.addDialogueOption(expedition.expedition.getDisplayName(), () -> this.focusOnExpedition(listIndex, expeditionIndex, expeditionList.focusMessage, expedition, expeditionsForm));
                    continue;
                }
                addedUnavailable = true;
                FormDialogueOption option = unavailableForm.addDialogueOption(expedition.expedition.getDisplayName(), () -> {});
                option.setActive(false);
                GameMessage unavailableMessage = expedition.expedition.getUnavailableMessage();
                if (unavailableMessage == null) continue;
                option.tooltipsSupplier = () -> new StringTooltips(unavailableMessage.translate());
            }
            if (addedUnavailable) {
                expeditionsForm.addDialogueOption(expeditionList.moreOptionsDialogue, () -> this.makeCurrent(unavailableForm));
            }
            expeditionsForm.setHeight(Math.min(Math.max(expeditionsForm.getContentHeight(), height), maxExpeditionsHeight));
            unavailableForm.setHeight(Math.min(Math.max(unavailableForm.getContentHeight(), height), maxExpeditionsHeight));
        }
        this.expeditionFocus = this.addComponent(new DialogueForm("expeditionFocus", width, height, null, null));
        this.updateDialogue();
        if (((ShopContainer)((Object)container)).humanShop.missionFailed) {
            this.makeCurrent(this.missionFailedForm);
        } else if (((ShopContainer)((Object)container)).workInvMessage != null && !((ShopContainer)((Object)container)).humanShop.workInventory.isEmpty()) {
            this.makeCurrent(this.workInvForm);
        } else if (((ShopContainer)((Object)container)).startInRecruitment) {
            this.makeCurrent(this.recruitForm);
        } else if (((ShopContainer)((Object)container)).quests != null && !((ShopContainer)((Object)container)).quests.isEmpty()) {
            this.makeCurrent(this.questForm);
        } else {
            this.makeCurrent(this.dialogueForm);
        }
    }

    public ShopContainerForm(Client client, T container) {
        this(client, container, 408, defaultHeight, defaultHeight);
    }

    public void updateDialogue() {
        this.updateWorkForms();
        this.dialogueForm.reset(this.header, ((ShopContainer)this.container).introMessage);
        this.setupExtraDialogueOptions();
        if (((ShopContainer)this.container).recruitItems != null || ((ShopContainer)this.container).recruitError != null) {
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlerjoinme"), () -> this.makeCurrent(this.recruitForm));
        } else if (this.isCurrent(this.recruitForm)) {
            this.makeCurrent(this.dialogueForm);
        }
        if (((ShopContainer)this.container).hasSettlerAccess) {
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlerchangeequipment"), () -> {
                ((ShopContainer)this.container).setIsInEquipment.runAndSend(true);
                this.makeCurrent(this.equipmentForm);
            });
        } else if (this.equipmentForm != null && this.isCurrent(this.equipmentForm)) {
            this.makeCurrent(this.dialogueForm);
        }
        boolean canChangeWork = this.updateWorkForms();
        if (canChangeWork) {
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlerchangework"), () -> this.makeCurrent(this.workForm));
        } else if (this.isCurrent(this.workForm)) {
            this.makeCurrent(this.dialogueForm);
        }
        this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlergoodbye"), () -> this.client.closeContainer(true));
        this.dialogueForm.setHeight(Math.max(this.dialogueForm.getContentHeight() + 5, this.height));
    }

    protected void setupExtraDialogueOptions() {
        this.addMoodDialogueOption();
        this.addQuestsDialogueOption();
        this.addAdventurePartyDialogueOptions();
        this.addShopDialogueOptions();
        this.addExpeditionsDialogueOptions();
    }

    protected void addMoodDialogueOption() {
        if (((ShopContainer)this.container).happinessModifiers != null) {
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlermood"), () -> this.makeCurrent(this.moodForm));
        }
    }

    protected void addQuestsDialogueOption() {
        if (((ShopContainer)this.container).quests != null) {
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "elderwantquest"), () -> {
                this.questForm.updateActive();
                this.makeCurrent(this.questForm);
            });
        }
    }

    protected void addAdventurePartyDialogueOptions() {
        if (((ShopContainer)this.container).hasSettlerAccess && ((ShopContainer)this.container).canJoinAdventureParties && !((ShopContainer)this.container).isInYourAdventureParty) {
            if (this.isCurrent(this.partyConfigForm)) {
                ((ShopContainer)this.container).setIsInPartyConfig.runAndSend(false);
                this.makeCurrent(this.dialogueForm);
            }
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlerjoinparty"), () -> {
                ((ShopContainer)this.container).joinAdventurePartyAction.runAndSend();
                this.waitingForPartyConfirm = true;
            });
            if (((ShopContainer)this.container).isSettlerOutsideSettlement) {
                this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlerreturntosettlement"), () -> ((ShopContainer)this.container).returnToSettlementAction.runAndSend());
            }
        } else if (((ShopContainer)this.container).isInYourAdventureParty) {
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "confiureadventureparty"), () -> {
                ((ShopContainer)this.container).setIsInPartyConfig.runAndSend(true);
                this.makeCurrent(this.partyConfigForm);
            });
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "settlerleaveparty"), () -> ((ShopContainer)this.container).leaveAdventurePartyAction.runAndSend());
        }
    }

    protected void addShopDialogueOptions() {
        if (((ShopContainer)this.container).sellingItems != null && !((ShopContainer)this.container).sellingItems.isEmpty()) {
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "traderwantbuy"), () -> this.makeCurrent(this.sellingForm));
        }
        if (((ShopContainer)this.container).buyingItems != null && !((ShopContainer)this.container).buyingItems.isEmpty()) {
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "traderwantsell"), () -> this.makeCurrent(this.buyingForm));
        }
    }

    protected void addExpeditionsDialogueOptions() {
        int i = 0;
        while (i < ((ShopContainer)this.container).expeditionLists.size()) {
            int finalI = i++;
            ExpeditionList expeditionList = ((ShopContainer)this.container).expeditionLists.get(finalI);
            this.dialogueForm.addDialogueOption(expeditionList.selectDialogue, () -> this.makeCurrent(this.expeditionsForms[finalI]));
        }
    }

    protected void updateWorkInventoryForm() {
        this.workInvForm.reset(MobRegistry.getLocalization(((ShopContainer)this.container).humanShop.getID()), ((ShopContainer)this.container).getWorkInvMessage());
        this.workInvForm.addDialogueOption(new LocalMessage("ui", "settlerreceiveitems"), () -> {
            ((ShopContainer)this.container).workItemsAction.runAndSend(ShopContainer.WorkItemsAction.RECEIVE);
            if (((ShopContainer)this.container).startInRecruitment) {
                this.makeCurrent(this.recruitForm);
            } else {
                this.makeCurrent(this.dialogueForm);
            }
        });
        this.workInvForm.addDialogueOption(new LocalMessage("ui", "continuebutton"), () -> {
            ((ShopContainer)this.container).workItemsAction.runAndSend(ShopContainer.WorkItemsAction.CONTINUE);
            if (((ShopContainer)this.container).startInRecruitment) {
                this.makeCurrent(this.recruitForm);
            } else {
                this.makeCurrent(this.dialogueForm);
            }
        });
        this.workInvForm.setHeight(GameMath.limit(this.workInvForm.getContentHeight() + 5, 160, 280));
        if (Input.lastInputIsController) {
            this.workInvForm.content.setScrollY(100000);
        }
        if (this.isCurrent(this.workInvForm) && ((ShopContainer)this.container).humanShop.workInventory.isEmpty()) {
            this.makeCurrent(this.dialogueForm);
        }
    }

    protected boolean updateWorkForms() {
        ((ShopContainer)this.container).humanShop.workDirty = false;
        this.workForm.reset(MobRegistry.getLocalization(((ShopContainer)this.container).humanShop.getID()), new LocalMessage("ui", "settlerchangewhat"));
        boolean out = this.setupExtraWorkOptions();
        this.workForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> this.makeCurrent(this.dialogueForm));
        return out;
    }

    public boolean setupExtraWorkOptions() {
        AtomicBoolean out = new AtomicBoolean(false);
        ((ShopContainer)this.container).workSettings.stream().sorted(Comparator.comparingInt(cws -> cws.setting.dialogueSort)).forEach(cws -> out.set(cws.handler.setupWorkForm(this, this.workForm) || out.get()));
        return out.get();
    }

    public void updateAcceptRecruitButton() {
        if (this.acceptRecruitButton == null) {
            return;
        }
        this.acceptRecruitButton.setActive(((ShopContainer)this.container).canPayForRecruit());
    }

    public void openSettlementList(final int mobUniqueID, List<SettlementOpenSettlementListEvent.SettlementOption> options) {
        if (((ShopContainer)this.container).humanShop.getUniqueID() != mobUniqueID) {
            GameLog.warn.println("Received wrong settlement list for mob id " + mobUniqueID);
            return;
        }
        SelectSettlementContinueForm.Option[] selectOptions = (SelectSettlementContinueForm.Option[])options.stream().map(o -> new SelectSettlementContinueForm.Option(true, o.settlementUniqueID, o.name){

            @Override
            public void onSelected(SelectSettlementContinueForm form) {
                ((ShopContainerForm)ShopContainerForm.this).client.network.sendPacket(PacketShopContainerUpdate.recruitSettler(mobUniqueID, this.settlementUniqueID));
                NoticeForm loadingForm = new NoticeForm("loadingresponse", 300, 400);
                loadingForm.setButtonCooldown(-2);
                loadingForm.setupNotice(new LocalMessage("ui", "loadingdotdot"));
                ShopContainerForm.this.addComponent(loadingForm, (lForm, switched) -> {
                    if (!switched.booleanValue()) {
                        ShopContainerForm.this.removeComponent(lForm);
                    }
                });
                ShopContainerForm.this.makeCurrent(loadingForm);
            }
        }).toArray(SelectSettlementContinueForm.Option[]::new);
        SelectSettlementContinueForm selectSettlementForm = new SelectSettlementContinueForm("movetosettlement", 300, 400, new LocalMessage("ui", "settlementselect"), selectOptions){

            @Override
            public void onCancel() {
                ShopContainerForm.this.makeCurrent(ShopContainerForm.this.recruitForm);
                this.removeComponent(this);
            }
        };
        this.addComponent(selectSettlementForm);
        this.makeCurrent(selectSettlementForm);
    }

    public void submitRecruitResponse(int mobUniqueID, GameMessage error) {
        if (((ShopContainer)this.container).humanShop.getUniqueID() != mobUniqueID) {
            GameLog.warn.println("Received wrong recruit response for mob id " + mobUniqueID);
            return;
        }
        if (error == null) {
            ((ShopContainer)this.container).payForRecruit();
            this.client.closeContainer(true);
        } else {
            NoticeForm errorForm = new NoticeForm("loadingresponse", 300, 400);
            errorForm.setupNotice(error);
            errorForm.setButtonCooldown(50);
            errorForm.onContinue(() -> this.makeCurrent(this.recruitForm));
            this.addComponent(errorForm, (form, switched) -> {
                if (!switched.booleanValue()) {
                    this.removeComponent(form);
                }
            });
            this.makeCurrent(errorForm);
        }
    }

    public void updateExpeditionBuyButton(PlayerMob perspective) {
        if (this.expeditionBuyButton != null) {
            int amount = perspective.getInv().getAmount(ItemRegistry.getItem("coin"), true, true, false, false, "buy");
            this.expeditionBuyButton.setActive(amount >= this.expeditionBuyCost);
        }
    }

    public void focusOnExpedition(int listIndex, int expeditionIndex, GameMessage costMessage, ContainerExpedition expedition, DialogueForm expeditionsForm) {
        this.expeditionBuyCost = expedition.price;
        String message = costMessage.translate().replace("<expedition>", expedition.expedition.getDisplayName().translate()).replace("<cost>", Integer.toString(this.expeditionBuyCost)).replace("<chance>", Integer.toString((int)(expedition.successChance * 100.0f)));
        this.expeditionFocus.reset(MobRegistry.getLocalization(((ShopContainer)this.container).humanShop.getID()), new StaticMessage(message));
        this.expeditionBuyButton = this.expeditionFocus.addDialogueOption(new LocalMessage("ui", "buybutton"), () -> ((ShopContainer)this.container).buyExpeditionButton.runAndSend(listIndex, expeditionIndex));
        this.expeditionFocus.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
            this.makeCurrent(expeditionsForm);
            this.expeditionBuyButton = null;
        });
        this.updateExpeditionBuyButton(this.client.getPlayer());
        this.makeCurrent(this.expeditionFocus);
        ContainerComponent.setPosFocus(this.expeditionFocus);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.missionFailedForm);
        ContainerComponent.setPosFocus(this.workInvForm);
        ContainerComponent.setPosFocus(this.dialogueForm);
        ContainerComponent.setPosFocus(this.moodForm);
        ContainerComponent.setPosFocus(this.partyResponseForm);
        ContainerComponent.setPosFocus(this.partyConfigForm);
        ContainerComponent.setPosFocus(this.workForm);
        ContainerComponent.setPosFocus(this.recruitForm);
        ContainerComponent.setPosFocus(this.whatCanYouDoForm);
        ContainerComponent.setPosFocus(this.questForm);
        for (DialogueForm expeditionsForm : this.expeditionsForms) {
            ContainerComponent.setPosFocus(expeditionsForm);
        }
        for (DialogueForm expeditionsForm : this.unavailableExpeditionsForms) {
            ContainerComponent.setPosFocus(expeditionsForm);
        }
        ContainerComponent.setPosFocus(this.expeditionFocus);
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updateExpeditionBuyButton(perspective);
        if (this.isCurrent(this.recruitForm)) {
            this.updateAcceptRecruitButton();
        }
        if (((ShopContainer)this.container).humanShop.workDirty) {
            this.updateWorkInventoryForm();
            this.updateWorkForms();
        }
        super.draw(tickManager, perspective, renderBox);
    }
}

