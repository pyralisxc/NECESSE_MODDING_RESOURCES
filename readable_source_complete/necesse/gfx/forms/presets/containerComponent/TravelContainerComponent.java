/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import java.awt.Dimension;
import java.awt.Rectangle;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.WorldGenerator;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.FormTravelContainerGrid;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.travel.IslandData;
import necesse.inventory.container.travel.TravelContainer;
import necesse.inventory.container.travel.TravelDir;
import necesse.level.maps.biomes.Biome;

public class TravelContainerComponent<T extends TravelContainer>
extends ContainerFormSwitcher<T> {
    private FocusData focus;
    private Form travelForm;
    private final Form focusForm;
    private Dimension hudSize;
    private FormTravelContainerGrid grid;
    private final FormLocalLabel focusLabel;
    private final FormLabel focusTip;
    private String lastSavedNotes;
    private final FormContentBox notesContent;
    private final FormTextBox notes;
    private final FormLocalTextButton travelButton;

    public static TypeParser[] getNoteParsers(FontOptions fontOptions) {
        return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.REMOVE_URL, TypeParsers.URL_OPEN, TypeParsers.ItemIcon(fontOptions.getSize()), TypeParsers.MobIcon(fontOptions.getSize()), TypeParsers.InputIcon(fontOptions)};
    }

    public TravelContainerComponent(Client client, T container) {
        super(client, container);
        this.updateTravelForm();
        this.focusForm = this.addComponent(new Form("focusForm", 280, 280));
        this.focusLabel = this.focusForm.addComponent(new FormLocalLabel(null, new FontOptions(20), 0, this.focusForm.getWidth() / 2, 5));
        this.focusTip = this.focusForm.addComponent(new FormLabel("", new FontOptions(12), 0, this.focusForm.getWidth() / 2, 25));
        this.focusForm.addComponent(new FormLocalLabel("ui", "travelnotes", new FontOptions(16), -1, 5, 40));
        this.notesContent = this.focusForm.addComponent(new FormContentBox(4, 60, this.focusForm.getWidth() - 8, this.focusForm.getHeight() - 80 - 56 - 8, GameBackground.textBox));
        FontOptions notesOptions = new FontOptions(16);
        this.notes = this.notesContent.addComponent(new FormTextBox(notesOptions, FairType.TextAlign.LEFT, this.getInterfaceStyle().textBoxTextColor, 0, 0, this.notesContent.getMinContentWidth(), 10, -1){

            @Override
            public void changedTyping(boolean value) {
                super.changedTyping(value);
                if (!value) {
                    TravelContainerComponent.this.saveIslandNotes();
                }
            }
        });
        this.notes.setParsers(TravelContainerComponent.getNoteParsers(notesOptions));
        this.notes.allowItemAppend = true;
        this.notes.setEmptyTextSpace(new Rectangle(this.notesContent.getX(), this.notesContent.getY(), this.notesContent.getWidth(), this.notesContent.getHeight()));
        this.notes.onChange(e -> {
            Rectangle box = this.notesContent.getContentBoxToFitComponents();
            this.notesContent.setContentBox(box);
            this.notesContent.scrollToFit(this.notes.getCaretBoundingBox());
        });
        this.notes.onCaretMove(e -> {
            if (!e.causedByMouse) {
                this.notesContent.scrollToFit(this.notes.getCaretBoundingBox());
            }
        });
        this.notes.onInputEvent(e -> {
            if (e.event.getID() == 256) {
                ((FormTypingComponent)e.from).setTyping(false);
                e.event.use();
                e.preventDefault();
            }
        });
        this.travelButton = this.focusForm.addComponent(new FormLocalTextButton("ui", "travelconfirm", 4, this.focusForm.getHeight() - 80, this.focusForm.getWidth() - 8));
        this.travelButton.onClicked(e -> {
            this.saveIslandNotes();
            this.travelTo(this.focus.destination);
        });
        this.focusForm.addComponent(new FormLocalTextButton("ui", "backbutton", 4, this.focusForm.getHeight() - 40, this.focusForm.getWidth() - 8)).onClicked(e -> {
            this.saveIslandNotes();
            this.focus = null;
            this.makeCurrent(this.travelForm);
            this.onWindowResized(WindowManager.getWindow());
        });
        this.makeCurrent(this.travelForm);
        this.onWindowResized(WindowManager.getWindow());
    }

    private void updateTravelForm() {
        Dimension lastHudSize = this.hudSize;
        GameWindow window = WindowManager.getWindow();
        this.hudSize = new Dimension(window.getHudWidth(), window.getHudHeight());
        if (!this.hudSize.equals(lastHudSize)) {
            int minHud = Math.min(this.hudSize.width, this.hudSize.height);
            Dimension formSize = minHud > 900 ? new Dimension(480, 600) : (minHud > 720 ? new Dimension(400, 520) : new Dimension(320, 440));
            if (this.travelForm == null || this.travelForm.getWidth() != formSize.width || this.travelForm.getHeight() != formSize.height) {
                boolean isCurrent = false;
                if (this.travelForm != null) {
                    isCurrent = this.isCurrent(this.travelForm);
                    this.removeComponent(this.travelForm);
                }
                this.travelForm = this.addComponent(new Form("travelForm", formSize.width, formSize.height));
                this.travelForm.addComponent(new FormLocalLabel("ui", ((TravelContainer)this.container).travelDir == TravelDir.None ? "travelworldmap" : "travelselect", new FontOptions(20), 0, this.travelForm.getWidth() / 2, 5));
                this.travelForm.addComponent(new FormLocalLabel("ui", "travelcoords", new FontOptions(16), -1, 5, 30));
                FormDropdownSelectionButton coordinateSetting = this.travelForm.addComponent(new FormDropdownSelectionButton(4, 50, FormInputSize.SIZE_24, ButtonColor.BASE, this.travelForm.getWidth() - 8, Settings.mapCoordinates.displayName));
                for (FormTravelContainerGrid.CoordinateSetting value : FormTravelContainerGrid.CoordinateSetting.values()) {
                    coordinateSetting.options.add(value, value.displayName);
                }
                coordinateSetting.onSelected(e -> {
                    Settings.mapCoordinates = (FormTravelContainerGrid.CoordinateSetting)((Object)((Object)e.value));
                    Settings.saveClientSettings();
                });
                this.grid = this.travelForm.addComponent(new FormTravelContainerGrid(this.grid, 0, 80, this.travelForm.getWidth(), this.travelForm.getHeight() - 120, this.client, (TravelContainer)this.container, this));
                this.travelForm.addComponent(new FormLocalTextButton("ui", "travelcancel", 4, this.travelForm.getHeight() - 40, this.travelForm.getWidth() - 8)).onClicked(e -> this.client.closeContainer(true));
                if (((TravelContainer)this.container).travelDir != TravelDir.None) {
                    this.travelForm.addComponent(new FormContentIconButton(this.travelForm.getWidth() - 5 - 20, 5, FormInputSize.SIZE_20, ButtonColor.BASE, this.getInterfaceStyle().button_help_20, new LocalMessage("ui", "travelhelp")));
                }
                if (isCurrent) {
                    this.makeCurrent(this.travelForm);
                }
                this.prioritizeControllerFocus(this.grid);
            }
        }
    }

    private void saveIslandNotes() {
        String newText = this.notes.getText();
        if (!newText.equals(this.lastSavedNotes)) {
            this.lastSavedNotes = newText;
            IslandData destination = this.focus.destination;
            this.client.islandNotes.set(destination.islandX, destination.islandY, newText);
            this.grid.reloadNotes(destination.islandX, destination.islandY);
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.updateTravelForm();
        this.travelForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.focusForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public void focusTravel(IslandData destination, Biome biome) {
        String notes;
        if (this.focus != null) {
            return;
        }
        this.focus = new FocusData(destination, biome);
        if (((TravelContainer)this.container).travelDir == TravelDir.None) {
            this.travelButton.setActive(false);
            this.travelButton.setLocalTooltip("ui", "travelcantmap");
        } else if (!destination.canTravel) {
            this.travelButton.setActive(false);
            this.travelButton.setLocalTooltip("ui", "travelcantrange");
        } else if (this.client.getLevel().isIslandPosition() && destination.islandX == this.client.getLevel().getIslandX() && destination.islandY == this.client.getLevel().getIslandY()) {
            this.travelButton.setActive(false);
            this.travelButton.setLocalTooltip("ui", "travelcantcurrent");
        } else {
            this.travelButton.setActive(true);
            this.travelButton.setLocalTooltip(null);
        }
        this.focusLabel.setLocalization(this.focus.biome.getLocalization());
        if (GlobalData.debugActive()) {
            this.focusTip.setText("(" + destination.islandX + ", " + destination.islandY + ")");
        } else if (this.client.getLevel().isIslandPosition()) {
            this.focusTip.setText("(" + (destination.islandX - this.client.getLevel().getIslandX()) + ", " + (destination.islandY - this.client.getLevel().getIslandY()) + ")");
        } else {
            this.focusTip.setText("");
        }
        if (GlobalData.debugCheatActive()) {
            this.focusTip.addLine(new StaticMessage("(" + WorldGenerator.getIslandSize(destination.islandX, destination.islandY) + ")"));
        }
        if ((notes = this.client.islandNotes.get(destination.islandX, destination.islandY)) != null) {
            this.notes.setText(notes);
        } else {
            this.notes.setText("");
        }
        this.lastSavedNotes = this.notes.getText();
        Rectangle box = this.notesContent.getContentBoxToFitComponents();
        this.notesContent.setContentBox(box);
        this.makeCurrent(this.focusForm);
        this.onWindowResized(WindowManager.getWindow());
    }

    public void travelTo(IslandData destination) {
        ((TravelContainer)this.container).travelToDestination.runAndSend(destination.islandX, destination.islandY);
    }

    @Override
    public boolean shouldOpenInventory() {
        return false;
    }

    @Override
    public boolean shouldShowToolbar() {
        return false;
    }

    private static class FocusData {
        public final IslandData destination;
        public final Biome biome;

        public FocusData(IslandData destination, Biome biome) {
            this.destination = destination;
            this.biome = biome;
        }
    }
}

