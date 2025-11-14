/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.journal;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.journal.JournalEntry;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.registries.JournalRegistry;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.forms.components.FormTabTextComponent;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.journal.FormJournalBiomeEntryComponent;
import necesse.gfx.forms.presets.containerComponent.journal.FormJournalEntryComponent;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.AdventureJournalContainer;

public class JournalContainerForm
extends ContainerFormSwitcher<AdventureJournalContainer> {
    public static String lastOpenBiomeEntry = null;
    public static String lastOpenMobEntry = null;
    public static int lastEntryListScroll = 0;
    public static int lastOpenEntryScroll = 0;
    private final FormComponentList journalComponents;
    private final Form journalForm;
    FormJournalEntryComponent formJournalEntryComponent;
    private final FormContentBox entries;
    private boolean displayingBiomeIncursionButtons;
    private boolean isIncursionSelectionActive;

    public JournalContainerForm(Client client, AdventureJournalContainer container) {
        block8: {
            block7: {
                super(client, container);
                client.tutorial.adventureJournalOpened();
                this.journalComponents = this.addComponent(new FormComponentList());
                this.journalForm = this.journalComponents.addComponent(new Form(925, 500));
                this.journalForm.addComponent(new FormLocalLabel("journal", "journal", new FontOptions(20), 0, this.journalForm.getWidth() / 2, 5));
                Color breakLineBlackColor = new Color(0, 0, 0);
                FormBreakLine topBreakLine = this.journalForm.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 5, 30, this.journalForm.getWidth() - 20, true));
                topBreakLine.color = breakLineBlackColor;
                this.entries = this.journalForm.addComponent(new FormContentBox(0, 40, 350, this.journalForm.getHeight() - 32 - 64 - 10));
                this.entries.onScrollYChanged(e -> {
                    lastEntryListScroll = e.scroll;
                });
                FormBreakLine middleVerticalLine = this.journalForm.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, this.entries.getWidth() + 5, this.entries.getY(), this.journalForm.getHeight() - 85, false));
                middleVerticalLine.color = breakLineBlackColor;
                this.updateJournalEntries(null);
                this.journalForm.addComponent(new FormLocalCheckBox("journal", "displaynotifications", 5, this.journalForm.getHeight() - 60, Settings.displayJournalNotifications)).onClicked(e -> {
                    Settings.displayJournalNotifications = ((FormCheckBox)e.from).checked;
                    Settings.saveClientSettings();
                });
                this.journalForm.addComponent(new FormLocalTextButton("ui", "closebutton", 4, this.journalForm.getHeight() - 40, this.journalForm.getWidth() - 8)).onClicked(e -> client.closeContainer(true));
                this.makeCurrent(this.journalComponents);
                if (lastOpenBiomeEntry == null) break block7;
                JournalEntry journalEntry = JournalRegistry.getJournalEntry(lastOpenBiomeEntry);
                if (journalEntry == null || !GlobalData.debugCheatActive() && !journalEntry.isDiscovered(client)) break block8;
                if (journalEntry.incursionBiome != null) {
                    this.isIncursionSelectionActive = true;
                    this.updateJournalEntries(null);
                }
                boolean foundOpenMobEntry = false;
                if (lastOpenMobEntry != null) {
                    for (JournalEntry.MobJournalData mob : journalEntry.mobsData) {
                        if (!mob.mob.getStringID().equals(lastOpenMobEntry)) continue;
                        this.formJournalEntryComponent.setupItemData(journalEntry, mob, client);
                        foundOpenMobEntry = true;
                        break;
                    }
                    if (lastOpenMobEntry.equals("treasure")) {
                        this.formJournalEntryComponent.setupTreasureData(journalEntry, journalEntry.treasuresData, client);
                        foundOpenMobEntry = true;
                    }
                    if (lastOpenMobEntry.equals("vinyls")) {
                        this.formJournalEntryComponent.setupVinylData(journalEntry, journalEntry.treasuresData, client);
                        foundOpenMobEntry = true;
                    }
                }
                if (!foundOpenMobEntry) {
                    this.formJournalEntryComponent.setupBiomeData(journalEntry, journalEntry.biomeLoot, client);
                }
                break block8;
            }
            for (JournalEntry journalEntry : JournalRegistry.getJournalEntries()) {
                if (!journalEntry.isDiscovered(client)) continue;
                this.formJournalEntryComponent.setupBiomeData(journalEntry, journalEntry.biomeLoot, client);
                break;
            }
        }
        this.entries.setScrollY(lastEntryListScroll);
        lastEntryListScroll = this.entries.getScrollY();
    }

    public void updateJournalEntries(JournalEntry lastSelectedEntry) {
        this.entries.clearComponents();
        if (lastSelectedEntry != null && lastSelectedEntry.incursionBiome != null) {
            this.displayingBiomeIncursionButtons = true;
            this.isIncursionSelectionActive = true;
        }
        if (this.formJournalEntryComponent == null) {
            this.formJournalEntryComponent = this.journalForm.addComponent(new FormJournalEntryComponent(0, 0, 550, this.journalForm, (AdventureJournalContainer)this.container));
        } else {
            this.formJournalEntryComponent.entryContextBox.clearComponents();
            this.formJournalEntryComponent.entryContextBox.setContentBox(new Rectangle(0, 0));
        }
        ArrayList<String> sortedDiscoveredLevels = new ArrayList<String>();
        for (JournalEntry journalEntry : JournalRegistry.getJournalEntries()) {
            if (!GlobalData.debugCheatActive() && !journalEntry.isDiscovered(this.client)) continue;
            sortedDiscoveredLevels.add(journalEntry.getStringID());
        }
        int journalEntryStartY = 5;
        FormMouseHover lastMouseHoverInList = null;
        for (String discoveredLevel : sortedDiscoveredLevels) {
            JournalEntry entry = JournalRegistry.getJournalEntry(discoveredLevel);
            if (entry.incursionBiome != null && !this.displayingBiomeIncursionButtons) {
                FormTabTextComponent biomesTab = this.journalComponents.addComponent(new FormTabTextComponent(Localization.translate("ui", "selectbiomes"), Localization.translate("ui", "biomeentries"), this.journalForm, 5, FormInputSize.SIZE_24, this.entries.getWidth() / 2 - 15){

                    @Override
                    public boolean isSelected() {
                        return !JournalContainerForm.this.isIncursionSelectionActive;
                    }
                });
                biomesTab.onClicked(e -> {
                    if (this.isIncursionSelectionActive) {
                        this.isIncursionSelectionActive = false;
                        this.updateJournalEntries(null);
                        this.entries.setScrollY(0);
                        lastOpenBiomeEntry = null;
                        lastOpenMobEntry = null;
                        lastEntryListScroll = 0;
                        lastOpenEntryScroll = 0;
                    }
                });
                FormTabTextComponent incursionsTab = this.journalComponents.addComponent(new FormTabTextComponent(Localization.translate("ui", "selectincursions"), Localization.translate("ui", "incursionentries"), this.journalForm, biomesTab.getWidth() + 20, FormInputSize.SIZE_24, this.entries.getWidth() / 2 - 15){

                    @Override
                    public boolean isSelected() {
                        return JournalContainerForm.this.isIncursionSelectionActive;
                    }
                });
                incursionsTab.onClicked(e -> {
                    if (!this.isIncursionSelectionActive) {
                        this.isIncursionSelectionActive = true;
                        this.updateJournalEntries(null);
                        this.entries.setScrollY(0);
                        lastOpenBiomeEntry = null;
                        lastOpenMobEntry = null;
                        lastEntryListScroll = 0;
                        lastOpenEntryScroll = 0;
                    }
                });
                this.displayingBiomeIncursionButtons = true;
            }
            if (this.isIncursionSelectionActive && entry.incursionBiome == null || !this.isIncursionSelectionActive && entry.incursionBiome != null) continue;
            FormJournalBiomeEntryComponent biomeEntry = this.entries.addComponent(new FormJournalBiomeEntryComponent(0, journalEntryStartY, 350, entry, this.client, this.entries, this));
            journalEntryStartY += biomeEntry.setupBiomeEntriesAndReturnCurrentHeight(lastMouseHoverInList, lastSelectedEntry);
            lastMouseHoverInList = biomeEntry.lastMouseHoverElement;
        }
        this.entries.setContentBox(new Rectangle(0, 0, 350, journalEntryStartY + 10));
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.journalForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
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

