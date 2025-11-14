/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.journal;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.journal.JournalEntry;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormIcon;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemIcon;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.presets.containerComponent.journal.JournalContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.gfx.ui.HUD;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.miscItem.VinylItem;

public class FormJournalBiomeEntryComponent
extends FormComponent
implements FormPositionContainer {
    protected FormPosition position;
    protected int width;
    public final JournalEntry journalEntry;
    private final Client client;
    protected boolean isHovering;
    private final Color bossColor;
    private final Color textColor;
    private final FontOptions titleOptions;
    private final FontOptions textOptions;
    private final FontOptions bossTextOptions;
    public FormContentBox entryContextBox;
    public FormMouseHover lastMouseHoverElement;
    private final JournalContainerForm journalForm;

    public FormJournalBiomeEntryComponent(int x, int y, int width, JournalEntry journalEntry, Client client, FormContentBox biomeEntries, JournalContainerForm journalForm) {
        this.bossColor = this.getInterfaceStyle().incursionTierPurple;
        this.textColor = this.getInterfaceStyle().activeTextColor;
        this.titleOptions = new FontOptions(20).color(this.textColor);
        this.textOptions = new FontOptions(16).color(this.textColor);
        this.bossTextOptions = new FontOptions(16).color(this.bossColor);
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.journalEntry = journalEntry;
        this.client = client;
        this.entryContextBox = biomeEntries;
        this.journalForm = journalForm;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
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

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
    }

    public int setupBiomeEntriesAndReturnCurrentHeight(FormMouseHover previousLastMouseHoverElement, JournalEntry lastSelectedEntry) {
        ListGameTooltips tooltips = new ListGameTooltips();
        int mobDiscoveredCount = 0;
        int progressY = 5;
        int initialY = this.getY();
        FormLocalLabel journal = new FormLocalLabel(this.journalEntry.getLocalization(), this.titleOptions, -1, this.getX() + 35, this.getY(), (int)((float)this.entryContextBox.getWidth() * 0.75f));
        this.entryContextBox.addComponent(journal);
        ButtonIcon buttonIcon = this.getInterfaceStyle().button_expanded_16;
        if (this.journalEntry.toggleIsHidden) {
            buttonIcon = this.getInterfaceStyle().button_collapsed_16;
        }
        FormContentIconButton toggleHideButton = this.entryContextBox.addComponent(new FormContentIconButton(10, this.getY(), 20, FormInputSize.SIZE_20, ButtonColor.BASE, buttonIcon, new LocalMessage("journal", "togglehide")), 1);
        toggleHideButton.onClicked(e -> {
            if (this.journalEntry.toggleIsHidden) {
                toggleHideButton.setIcon(this.getInterfaceStyle().button_collapsed_16);
                this.journalEntry.toggleIsHidden = false;
            } else {
                toggleHideButton.setIcon(this.getInterfaceStyle().button_expanded_16);
                this.journalEntry.toggleIsHidden = true;
            }
            int lastScrollY = 0;
            if (JournalContainerForm.lastOpenBiomeEntry != null && JournalContainerForm.lastOpenBiomeEntry.equals(this.journalEntry.getStringID())) {
                lastScrollY = this.journalForm.formJournalEntryComponent.entryContextBox.getScrollY();
            }
            this.journalForm.updateJournalEntries(this.journalEntry);
            this.journalForm.formJournalEntryComponent.setupBiomeData(this.journalEntry, this.journalEntry.biomeLoot, this.client);
            this.journalForm.formJournalEntryComponent.entryContextBox.setScrollY(lastScrollY);
            WindowManager.getWindow().submitNextMoveEvent();
            ControllerInput.submitNextRefreshFocusEvent();
        });
        if (lastSelectedEntry == this.journalEntry) {
            toggleHideButton.tryPrioritizeControllerFocus();
        }
        FormMouseHover biomeMouseHover = this.addMouseHover(progressY - 10, journal.getHeight() + 12);
        biomeMouseHover.onClicked(e -> {
            this.journalForm.formJournalEntryComponent.setupBiomeData(this.journalEntry, this.journalEntry.biomeLoot, this.client);
            this.journalForm.formJournalEntryComponent.entryContextBox.setScrollY(0);
            JournalContainerForm.lastOpenEntryScroll = 0;
        });
        biomeMouseHover.controllerLeftFocus = toggleHideButton;
        toggleHideButton.controllerRightFocus = biomeMouseHover;
        progressY += journal.getHeight();
        int listCounter = 1;
        for (JournalEntry.MobJournalData mobData : this.journalEntry.mobsData) {
            FormLocalLabel mobTextLabel;
            boolean mobDiscovered;
            Mob mob = mobData.mob;
            boolean bl = mobDiscovered = this.client.characterStats.mob_kills.getKills(mob.getStringID()) > 0 || GlobalData.debugCheatActive();
            if (mobDiscovered) {
                ++mobDiscoveredCount;
            }
            if (this.journalEntry.toggleIsHidden) continue;
            FontOptions fontOptions = mob.isBoss() || Objects.equals(mob.getStringID(), "sageandgrit") ? this.bossTextOptions : this.textOptions;
            GameTexture mobIcon = mob.getMobIcon();
            if (mobDiscovered) {
                mobTextLabel = new FormLocalLabel(mob.getLocalization(), fontOptions, -1, this.getX() + 32 + 10, this.getY() + progressY + 8, (int)((float)this.entryContextBox.getWidth() * 0.75f));
                this.entryContextBox.addComponent(mobTextLabel);
                this.entryContextBox.addComponent(new FormIcon(this.getX() + 8, this.getY() + progressY, mobIcon.getWidth(), mobIcon.getHeight(), mobIcon, 1.0f));
            } else {
                mobTextLabel = new FormLocalLabel("mob", "unknown", fontOptions, -1, this.getX() + 32 + 10, this.getY() + progressY + 8, (int)((float)this.entryContextBox.getWidth() * 0.75f));
                this.entryContextBox.addComponent(mobTextLabel);
                this.entryContextBox.addComponent(new FormIcon(this.getX() + 8, this.getY() + progressY, mobIcon.getWidth(), mobIcon.getHeight(), mobIcon, 0.15f));
            }
            int labelHeight = mobTextLabel.getHeight() + 16;
            FormMouseHover mouseHover = this.addMouseHover(progressY, labelHeight);
            mouseHover.onClicked(e -> this.journalForm.formJournalEntryComponent.setupItemData(this.journalEntry, mobData, this.client));
            if (listCounter == 1) {
                mouseHover.controllerUpFocus = toggleHideButton;
            }
            if (listCounter == this.journalEntry.mobsData.size()) {
                if (previousLastMouseHoverElement != null) {
                    previousLastMouseHoverElement.controllerDownFocus = toggleHideButton;
                }
                this.lastMouseHoverElement = mouseHover;
            }
            ++listCounter;
            progressY += labelHeight;
            this.textOptions.color(this.textColor);
        }
        if (!this.journalEntry.toggleIsHidden && this.journalEntry.treasuresData != null) {
            GameTexture coinsStackIcon = GameResources.coinStackIcon;
            FormLocalLabel treasureTextLabel = new FormLocalLabel("journal", "treasures", this.textOptions, -1, this.getX() + 32 + 10, this.getY() + progressY + 8, (int)((float)this.entryContextBox.getWidth() * 0.75f));
            this.entryContextBox.addComponent(treasureTextLabel);
            this.entryContextBox.addComponent(new FormIcon(this.getX() + 8, this.getY() + progressY, coinsStackIcon.getWidth(), coinsStackIcon.getHeight(), coinsStackIcon, 1.0f));
            int treasureLabelHeight = treasureTextLabel.getHeight() + 16;
            FormMouseHover treasureHover = this.addMouseHover(progressY, treasureLabelHeight);
            treasureHover.onClicked(e -> this.journalForm.formJournalEntryComponent.setupTreasureData(this.journalEntry, this.journalEntry.treasuresData, this.client));
            if (previousLastMouseHoverElement != null) {
                previousLastMouseHoverElement.controllerDownFocus = toggleHideButton;
            }
            this.lastMouseHoverElement = treasureHover;
            progressY += treasureLabelHeight;
            boolean hasVinyl = this.journalEntry.treasuresData.streamItems().anyMatch(item -> item instanceof VinylItem);
            if (hasVinyl) {
                FormLocalLabel vinylsTextLabel = new FormLocalLabel("journal", "vinyls", this.textOptions, -1, this.getX() + 32 + 10, this.getY() + progressY + 8, (int)((float)this.entryContextBox.getWidth() * 0.75f));
                this.entryContextBox.addComponent(vinylsTextLabel);
                this.entryContextBox.addComponent(new FormItemIcon(this.getX() + 8, this.getY() + progressY, new InventoryItem("adventurebeginsvinyl"), false){

                    @Override
                    public void addTooltips(PlayerMob perspective) {
                    }
                });
                int vinylLabelHeight = vinylsTextLabel.getHeight() + 16;
                FormMouseHover vinylHover = this.addMouseHover(progressY, vinylLabelHeight);
                vinylHover.onClicked(e -> this.journalForm.formJournalEntryComponent.setupVinylData(this.journalEntry, this.journalEntry.treasuresData, this.client));
                if (previousLastMouseHoverElement != null) {
                    previousLastMouseHoverElement.controllerDownFocus = toggleHideButton;
                }
                this.lastMouseHoverElement = vinylHover;
                progressY += vinylLabelHeight;
            }
        }
        FormLocalLabel discoverLabel = this.getDiscoverLabel(mobDiscoveredCount, this.textOptions, initialY);
        this.entryContextBox.addComponent(discoverLabel);
        FormBreakLine breakLine = this.entryContextBox.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 10, this.getY() + progressY + 5, this.entryContextBox.getWidth() - 30, true));
        breakLine.color = new Color(80, 80, 80);
        if (!tooltips.isEmpty()) {
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
        }
        return progressY += 10;
    }

    private FormMouseHover addMouseHover(int progressY, final int textHeight) {
        return this.entryContextBox.addComponent(new FormMouseHover(10, this.getY() + progressY, 315, textHeight, true){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                super.draw(tickManager, perspective, renderBox);
                if (this.isHovering() && !Input.lastInputIsController) {
                    HUD.selectBoundOptions(new Color(255, 255, 255), false, this.getX() - 5, this.getY(), this.getX() + 325, this.getY() + textHeight).draw();
                }
            }
        });
    }

    private FormLocalLabel getDiscoverLabel(int mobDiscoveredCount, FontOptions textOptions, int initialY) {
        LocalMessage mobsDiscovered = new LocalMessage("journal", "mobsdiscovered", "discovered", mobDiscoveredCount, "total", this.journalEntry.mobsData.size());
        return new FormLocalLabel(mobsDiscovered, textOptions, 1, this.entryContextBox.getWidth() - 20, initialY + 3);
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormJournalBiomeEntryComponent.singleBox(new Rectangle(this.getX(), this.getY(), this.width, 0));
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

