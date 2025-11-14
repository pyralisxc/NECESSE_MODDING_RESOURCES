/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.journal;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.journal.JournalEntry;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameResources;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormIcon;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemIcon;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.presets.containerComponent.journal.JournalContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.AdventureJournalContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.VinylItem;
import necesse.inventory.lootTable.LootList;

public class FormJournalEntryComponent
extends FormComponent
implements FormPositionContainer {
    private boolean setLastEntryScroll = false;
    protected FormPosition position;
    protected int width;
    protected boolean isHover;
    public FormContentBox entryContextBox;
    private final Form journalForm;
    private final AdventureJournalContainer container;
    private final Color bossColor;
    private final Color textColor;
    private final Color breakLineBlackColor;
    private final Color breakLineGrayColor;
    private final FontOptions regularTextOptions;
    private final FontOptions normalMobTitleOptions;
    private final FontOptions bossTitleOptions;
    private int yPos;
    private int xPos;

    public FormJournalEntryComponent(int x, int y, int width, Form journalForm, AdventureJournalContainer container) {
        this.bossColor = this.getInterfaceStyle().incursionTierPurple;
        this.textColor = this.getInterfaceStyle().activeTextColor;
        this.breakLineBlackColor = new Color(0, 0, 0);
        this.breakLineGrayColor = new Color(80, 80, 80);
        this.regularTextOptions = new FontOptions(16).color(this.textColor);
        this.normalMobTitleOptions = new FontOptions(20).color(this.textColor);
        this.bossTitleOptions = new FontOptions(20).color(this.bossColor);
        this.yPos = 0;
        this.xPos = 10;
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.journalForm = journalForm;
        this.container = container;
        this.entryContextBox = journalForm.addComponent(new FormContentBox(365, 40, 550, journalForm.getHeight() - 32 - 64));
        this.entryContextBox.onScrollYChanged(e -> {
            JournalContainerForm.lastOpenEntryScroll = e.scroll;
        });
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.isHover = this.isMouseOver(event);
            if (this.isHover) {
                event.useMove();
            }
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
    }

    public void setupBiomeChallengeData() {
    }

    public void setupBiomeData(JournalEntry entry, LootList loot, final Client client) {
        JournalContainerForm.lastOpenBiomeEntry = entry.getStringID();
        JournalContainerForm.lastOpenMobEntry = null;
        this.entryContextBox.clearComponents();
        this.resetXYPos();
        String biomeNameID = entry.getStringID();
        FormLocalLabel biomeName = new FormLocalLabel("journal", biomeNameID, this.normalMobTitleOptions, -1, this.xPos, this.yPos + 8, this.entryContextBox.getWidth() - 10);
        this.entryContextBox.addComponent(biomeName);
        this.yPos += biomeName.getHeight() + 12 + 16;
        FormBreakLine breakLine = this.entryContextBox.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 5, this.yPos - 12, this.entryContextBox.getWidth() - 25, true));
        breakLine.color = this.breakLineBlackColor;
        boolean addedHeader = false;
        for (final JournalChallenge challenge : entry.getChallenges()) {
            if (!addedHeader) {
                this.entryContextBox.addComponent(new FormLocalLabel("journal", "challenges", new FontOptions(16), -1, this.xPos + 8, this.yPos));
                this.yPos += 20;
                FormBreakLine challengeBreakLine = this.entryContextBox.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 5, this.yPos, this.entryContextBox.getWidth() - 25, true));
                challengeBreakLine.color = this.breakLineGrayColor;
                this.yPos += 10;
                addedHeader = true;
            }
            FormFlow flow = new FormFlow(this.yPos);
            challenge.addJournalFormContent(client, this.entryContextBox, flow);
            flow.next(6);
            if (challenge.hasReward(client.getClient())) {
                LootList lootList = new LootList();
                challenge.addRewardsToList(lootList, client);
                FairType rewardType = new FairType();
                FontOptions rewardTypeFontOptions = new FontOptions(16);
                if (lootList.addRewardsToFairType(rewardType, rewardTypeFontOptions, true, false, null)) {
                    rewardType.applyParsers(TypeParsers.ItemIcon(rewardTypeFontOptions.getSize(), true));
                } else {
                    rewardType = null;
                }
                if (rewardType != null) {
                    LocalMessage claimText;
                    Form rewardForm = new Form(this.entryContextBox.getWidth() - 41, 56);
                    rewardForm.setBackground(GameBackground.indent);
                    rewardForm.setPosition(13, flow.next());
                    this.entryContextBox.addComponent(rewardForm);
                    int claimButtonWidth = 170;
                    FormFlow rewardFormFlow = new FormFlow(5);
                    rewardForm.addComponent(rewardFormFlow.nextY(new FormLocalLabel("journal", "challengesreward", new FontOptions(16), -1, 5, 0)));
                    rewardFormFlow.next(4);
                    FormFairTypeLabel rewardLabel = rewardFormFlow.nextY(new FormFairTypeLabel("", 5, 0).setCustomFairType(rewardType).setMaxWidth(rewardForm.getWidth() - claimButtonWidth - 10), 5);
                    rewardForm.addComponent(rewardLabel);
                    final AtomicBoolean checkCompleted = new AtomicBoolean();
                    if (challenge.isClaimed(client)) {
                        checkCompleted.set(false);
                        claimText = new LocalMessage("journal", "challengeclaimed");
                    } else {
                        checkCompleted.set(true);
                        claimText = new LocalMessage("journal", "claimchallenge");
                    }
                    FormLocalTextButton button = rewardForm.addComponent(new FormLocalTextButton(claimText, rewardForm.getWidth() - claimButtonWidth - 7, rewardLabel.getY(), claimButtonWidth, FormInputSize.SIZE_24, ButtonColor.BASE){

                        @Override
                        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                            if (checkCompleted.get()) {
                                this.setActive(challenge.isCompleted(client));
                            }
                            super.draw(tickManager, perspective, renderBox);
                        }
                    });
                    button.setActive(checkCompleted.get());
                    button.onClicked(e -> {
                        checkCompleted.set(false);
                        button.setActive(false);
                        button.setLocalization("journal", "challengeclaimed");
                        this.container.claimChallengeRewardButton.runAndSend(challenge.getID());
                    });
                    rewardFormFlow.next(6);
                    rewardForm.setHeight(rewardFormFlow.next());
                    flow.next(rewardFormFlow.next());
                }
            }
            flow.next(6);
            this.yPos = flow.next();
        }
        this.displayLoot(loot, this.regularTextOptions, client, entry, item -> true);
        if (!this.setLastEntryScroll) {
            this.setLastEntryScroll = true;
            this.entryContextBox.setScrollY(JournalContainerForm.lastOpenEntryScroll);
        }
        JournalContainerForm.lastOpenEntryScroll = this.entryContextBox.getScrollY();
    }

    public void setupItemData(JournalEntry journalEntry, JournalEntry.MobJournalData entryData, Client client) {
        FormLocalLabel mobNameLabel;
        FormIcon formIcon;
        JournalContainerForm.lastOpenBiomeEntry = journalEntry.getStringID();
        JournalContainerForm.lastOpenMobEntry = entryData.mob.getStringID();
        this.entryContextBox.clearComponents();
        this.resetXYPos();
        Mob mob = entryData.mob;
        boolean mobDiscovered = client.characterStats.mob_kills.getKills(mob.getStringID()) > 0 || GlobalData.debugCheatActive();
        FontOptions mobTitleTextOptions = mob.isBoss() || Objects.equals(mob.getStringID(), "sageandgrit") ? this.bossTitleOptions : this.normalMobTitleOptions;
        GameTexture entryMobIcon = mob.getMobIcon();
        if (mobDiscovered) {
            formIcon = new FormIcon(this.xPos, this.yPos, entryMobIcon.getWidth(), entryMobIcon.getHeight(), entryMobIcon, 1.0f);
            mobNameLabel = new FormLocalLabel(MobRegistry.getLocalization(mob.getStringID()), mobTitleTextOptions, -1, this.xPos + entryMobIcon.getWidth() + 8, this.yPos + 8, this.entryContextBox.getWidth() - 10);
        } else {
            formIcon = new FormIcon(this.xPos, this.yPos, entryMobIcon.getWidth(), entryMobIcon.getHeight(), entryMobIcon, 0.15f);
            mobNameLabel = new FormLocalLabel("mob", "unknown", mobTitleTextOptions, -1, this.xPos + entryMobIcon.getWidth() + 8, this.yPos + 8, this.entryContextBox.getWidth() - 10);
        }
        this.entryContextBox.addComponent(formIcon);
        this.entryContextBox.addComponent(mobNameLabel);
        this.yPos += mobNameLabel.getHeight() + 12 + 16;
        FormBreakLine breakLine = this.entryContextBox.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 5, this.yPos - 12, this.entryContextBox.getWidth() - 25, true));
        breakLine.color = this.breakLineBlackColor;
        this.displayLoot(entryData.itemDrops, this.regularTextOptions, client, null, item -> true);
        if (!this.setLastEntryScroll) {
            this.setLastEntryScroll = true;
            this.entryContextBox.setScrollY(JournalContainerForm.lastOpenEntryScroll);
        }
        JournalContainerForm.lastOpenEntryScroll = this.entryContextBox.getScrollY();
    }

    public void setupTreasureData(JournalEntry journalEntry, LootList treasureData, Client client) {
        JournalContainerForm.lastOpenBiomeEntry = journalEntry.getStringID();
        JournalContainerForm.lastOpenMobEntry = "treasure";
        this.entryContextBox.clearComponents();
        this.resetXYPos();
        FontOptions regularTextOptions = new FontOptions(16).color(this.textColor);
        FontOptions treasureTitleOptions = new FontOptions(20).color(this.textColor);
        GameTexture coinStackIcon = GameResources.coinStackIcon;
        FormIcon formIcon = new FormIcon(this.xPos, this.yPos, coinStackIcon.getWidth(), coinStackIcon.getHeight(), GameResources.coinStackIcon, 1.0f);
        FormLocalLabel mobNameLabel = new FormLocalLabel("journal", "treasures", treasureTitleOptions, -1, this.xPos + coinStackIcon.getWidth() + 8, this.yPos + 8);
        this.entryContextBox.addComponent(formIcon);
        this.entryContextBox.addComponent(mobNameLabel);
        this.yPos += 48;
        FormBreakLine breakLine = this.entryContextBox.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 5, this.yPos - 12, this.entryContextBox.getWidth() - 25, true));
        breakLine.color = this.breakLineBlackColor;
        this.displayLoot(treasureData, regularTextOptions, client, null, item -> !(item instanceof VinylItem));
        if (!this.setLastEntryScroll) {
            this.setLastEntryScroll = true;
            this.entryContextBox.setScrollY(JournalContainerForm.lastOpenEntryScroll);
        }
        JournalContainerForm.lastOpenEntryScroll = this.entryContextBox.getScrollY();
    }

    public void setupVinylData(JournalEntry journalEntry, LootList treasureData, Client client) {
        JournalContainerForm.lastOpenBiomeEntry = journalEntry.getStringID();
        JournalContainerForm.lastOpenMobEntry = "vinyls";
        this.entryContextBox.clearComponents();
        this.resetXYPos();
        FontOptions regularTextOptions = new FontOptions(16).color(this.textColor);
        FontOptions treasureTitleOptions = new FontOptions(20).color(this.textColor);
        FormItemIcon formIcon = new FormItemIcon(this.xPos, this.yPos, new InventoryItem("adventurebeginsvinyl"), false){

            @Override
            public void addTooltips(PlayerMob perspective) {
            }
        };
        FormLocalLabel mobNameLabel = new FormLocalLabel("journal", "vinyls", treasureTitleOptions, -1, this.xPos + formIcon.getBoundingBox().width + 8, this.yPos + 8);
        this.entryContextBox.addComponent(formIcon);
        this.entryContextBox.addComponent(mobNameLabel);
        this.yPos += 48;
        FormBreakLine breakLine = this.entryContextBox.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 5, this.yPos - 12, this.entryContextBox.getWidth() - 25, true));
        breakLine.color = this.breakLineBlackColor;
        this.displayLoot(treasureData, regularTextOptions, client, null, item -> item instanceof VinylItem);
        if (!this.setLastEntryScroll) {
            this.setLastEntryScroll = true;
            this.entryContextBox.setScrollY(JournalContainerForm.lastOpenEntryScroll);
        }
        JournalContainerForm.lastOpenEntryScroll = this.entryContextBox.getScrollY();
    }

    private void displayLoot(LootList lootList, FontOptions textOptions, Client client, JournalEntry entry, Predicate<Item> lootFilter) {
        LinkedHashMap<InventoryItem, FairType> discoveredItems = new LinkedHashMap<InventoryItem, FairType>();
        LinkedHashMap<InventoryItem, FairType> undiscoveredItems = new LinkedHashMap<InventoryItem, FairType>();
        List items = lootList.streamItems().filter(lootFilter).sorted(Item::compareTo).collect(Collectors.toList());
        for (Item item : items) {
            final boolean itemObtained = client.characterStats.items_obtained.isItemObtained(item.getStringID()) || entry != null || GlobalData.debugCheatActive();
            FairType fairType = new FairType();
            final InventoryItem inventoryItem = new InventoryItem(item.getStringID(), 1);
            fairType.append(new FairItemGlyph(24, inventoryItem){

                @Override
                public void drawIcon(InventoryItem currentDrawnItem, float x, float y, int size, float alpha) {
                    inventoryItem.drawIcon(null, (int)x, (int)y, 32, itemObtained ? new Color(1.0f, 1.0f, 1.0f, alpha) : new Color(0.15f, 0.15f, 0.15f, alpha));
                }

                @Override
                public GameTooltips getTooltip(InventoryItem currentDrawnItem) {
                    if (!itemObtained) {
                        return null;
                    }
                    return super.getTooltip(currentDrawnItem);
                }
            }.offsetY(4));
            if (itemObtained) {
                discoveredItems.put(inventoryItem, fairType);
                continue;
            }
            undiscoveredItems.put(inventoryItem, fairType);
        }
        FontOptions discoveredTitleOptions = new FontOptions(16).color(this.textColor);
        boolean leftItemPosition = true;
        if (entry != null && !discoveredItems.isEmpty()) {
            String key = "surface";
            if (entry.levelIdentifier != null && (entry.levelIdentifier.equals(LevelIdentifier.CAVE_IDENTIFIER) || entry.levelIdentifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER))) {
                key = "cave";
            }
            this.entryContextBox.addComponent(new FormLocalLabel("journal", key, discoveredTitleOptions, -1, this.xPos + 8, this.yPos + 8));
            this.yPos += 40;
            this.displayItemsAndReturnLastItemPosAsBoolean(discoveredItems, true);
        } else {
            if (!discoveredItems.isEmpty()) {
                this.entryContextBox.addComponent(new FormLocalLabel("journal", "discovered", discoveredTitleOptions, -1, this.xPos + 8, this.yPos + 8));
                this.yPos += 40;
                leftItemPosition = this.displayItemsAndReturnLastItemPosAsBoolean(discoveredItems, true);
            }
            if (!leftItemPosition) {
                this.xPos = 10;
                this.yPos += 32;
            }
            if (!undiscoveredItems.isEmpty()) {
                this.entryContextBox.addComponent(new FormLocalLabel("journal", "undiscovered", discoveredTitleOptions, -1, this.xPos + 8, this.yPos + 8));
                this.yPos += 40;
                this.displayItemsAndReturnLastItemPosAsBoolean(undiscoveredItems, false);
            }
        }
        textOptions.color(this.textColor);
        this.entryContextBox.setContentBox(new Rectangle(0, 0, this.journalForm.getWidth() / 2, this.yPos + 32));
    }

    private void resetXYPos() {
        this.yPos = 0;
        this.xPos = 10;
    }

    public boolean displayItemsAndReturnLastItemPosAsBoolean(LinkedHashMap<InventoryItem, FairType> items, boolean discoveredItems) {
        boolean leftItemPosition = true;
        FormBreakLine discoverLine = this.entryContextBox.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 5, this.yPos - 12, this.entryContextBox.getWidth() - 25, true));
        discoverLine.color = this.breakLineGrayColor;
        int rightXPos = (int)((float)this.journalForm.getWidth() / 3.5f);
        int highestLineCount = 0;
        for (Map.Entry<InventoryItem, FairType> item : items.entrySet()) {
            FormFairTypeLabel fairTypeLabel = new FormFairTypeLabel("", this.xPos, this.yPos);
            fairTypeLabel.setCustomFairType(item.getValue());
            FormLabel textLabel = this.getTextLabelBasedOnItemDiscovered(discoveredItems, item, rightXPos);
            int linesCount = textLabel.getLinesCount();
            if (linesCount > highestLineCount) {
                highestLineCount = linesCount;
            }
            if (leftItemPosition) {
                this.xPos = rightXPos;
                leftItemPosition = false;
            } else {
                this.xPos = 10;
                this.yPos = highestLineCount > 1 ? (this.yPos += 32 + (6 * highestLineCount - 1)) : (this.yPos += 32);
                leftItemPosition = true;
                highestLineCount = 0;
            }
            this.entryContextBox.addComponent(fairTypeLabel);
            this.entryContextBox.addComponent(textLabel);
        }
        return leftItemPosition;
    }

    private FormLabel getTextLabelBasedOnItemDiscovered(boolean discoveredItems, Map.Entry<InventoryItem, FairType> item, int rightXPos) {
        InventoryItem inventoryItem = item.getKey();
        FormLabel textLabel = discoveredItems ? new FormLocalLabel(inventoryItem.getItemLocalization(), this.regularTextOptions, -1, this.xPos + 36, this.yPos + 10, rightXPos - 50) : new FormLabel(inventoryItem.item.getTranslatedTypeName(), this.regularTextOptions, -1, this.xPos + 36, this.yPos + 10, rightXPos - 50);
        return textLabel;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormJournalEntryComponent.singleBox(new Rectangle(this.getX(), this.getY(), this.width, 22));
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }
}

