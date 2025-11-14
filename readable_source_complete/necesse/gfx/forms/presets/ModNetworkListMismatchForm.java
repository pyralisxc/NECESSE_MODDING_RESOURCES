/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModListData;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModNetworkData;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.lists.FormModListSimpleElement;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.ModSaveConfirmationForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;

public class ModNetworkListMismatchForm
extends FormSwitcher
implements ContinueComponent {
    private ConfirmationForm intro;
    private Form review;
    private ModSaveConfirmationForm useServerConfirmation;
    private FormContentBox serverModsContent;
    private FormContentBox myModsContent;
    private FormTextButton useServer;
    private List<ModListData> serverModsList;
    private ArrayList<Runnable> continueEvents = new ArrayList();
    private boolean isContinued;

    public ModNetworkListMismatchForm() {
        this.intro = this.addComponent(new ConfirmationForm("intro"));
        this.review = this.addComponent(new Form("review", 640, 320));
        this.review.addComponent(new FormLocalLabel("ui", "servermods", new FontOptions(20), 0, this.review.getWidth() / 4, 2));
        this.serverModsContent = this.review.addComponent(new FormContentBox(4, 24, this.review.getWidth() / 2 - 8, this.review.getHeight() - 64));
        this.review.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, this.review.getWidth() / 2, 4, this.review.getHeight() - 40 - 8, false));
        this.review.addComponent(new FormLocalLabel("ui", "mymods", new FontOptions(20), 0, this.review.getWidth() - this.review.getWidth() / 4, 2));
        this.myModsContent = this.review.addComponent(new FormContentBox(this.review.getWidth() / 2 + 4, 24, this.review.getWidth() / 2 - 8, this.review.getHeight() - 64));
        this.useServer = this.review.addComponent(new FormLocalTextButton("ui", "useservermods", 4, this.review.getHeight() - 40, this.review.getWidth() / 2 - 6));
        this.useServer.onClicked(e -> {
            if (this.serverModsList != null) {
                ModLoader.saveModListSettings(this.serverModsList);
            } else {
                System.err.println("Error saving server mod list");
            }
            this.makeCurrent(this.useServerConfirmation);
        });
        this.review.addComponent(new FormLocalTextButton("ui", "continuebutton", this.review.getWidth() / 2 + 2, this.review.getHeight() - 40, this.review.getWidth() / 2 - 6)).onClicked(e -> this.applyContinue());
        this.useServerConfirmation = this.addComponent(new ModSaveConfirmationForm("useserverconf"));
        this.useServerConfirmation.setupModSaveConfirmation(this::applyContinue);
        this.makeCurrent(this.intro);
    }

    public void setup(List<ModNetworkData> loadList) {
        LoadedMod myMod;
        this.intro.setupConfirmation(new LocalMessage("ui", "modmismatch"), (GameMessage)new LocalMessage("ui", "continuebutton"), (GameMessage)new LocalMessage("ui", "modreview"), this::applyContinue, () -> this.makeCurrent(this.review));
        this.serverModsContent.clearComponents();
        this.myModsContent.clearComponents();
        FormFlow serverModsFlow = new FormFlow();
        FormFlow myModsFlow = new FormFlow();
        ArrayList<LoadedMod> myMods = new ArrayList<LoadedMod>(ModLoader.getAllMods());
        Comparator<LoadedMod> comparator = Comparator.comparingInt(m -> m.isEnabled() ? -1000 : m.loadLocation.modProvider.getLoadOrder());
        comparator = comparator.thenComparing(m -> m.isEnabled() ? "" : m.name);
        myMods.sort(comparator);
        boolean hasAllMods = true;
        this.serverModsList = new ArrayList<ModListData>();
        for (ModNetworkData loadMod : loadList) {
            myMod = myMods.stream().filter(m -> m.id.equals(loadMod.id) && m.version.equals(loadMod.version)).findFirst().orElse(null);
            if (myMod != null) {
                ModListData listData = new ModListData(myMod);
                listData.enabled = true;
                this.serverModsList.add(listData);
                continue;
            }
            if (loadMod.clientside) continue;
            hasAllMods = false;
        }
        if (hasAllMods) {
            for (LoadedMod myMod2 : myMods) {
                if (!this.serverModsList.stream().noneMatch(m -> m.matchesMod(myMod2))) continue;
                ModListData listData = new ModListData(myMod2);
                listData.enabled = false;
                this.serverModsList.add(listData);
            }
        }
        int myModsIndex = 0;
        int loadListIndex = 0;
        while (true) {
            ModNetworkData loadMod;
            LoadedMod loadedMod = myMod = myModsIndex < myMods.size() ? (LoadedMod)myMods.get(myModsIndex) : null;
            if (myMod != null && (!myMod.isEnabled() || myMod.clientside)) {
                if (!myMod.isEnabled()) {
                    this.myModsContent.addComponent(myModsFlow.nextY(new FormModListSimpleElement(myMod.name + " " + myMod.version, this.myModsContent.getMinContentWidth(), myMod.loadLocation.modProvider, false, this.getInterfaceStyle().inactiveTextColor, Localization.translate("ui", "moddisabled"))));
                } else if (myMod.clientside) {
                    this.myModsContent.addComponent(myModsFlow.nextY(new FormModListSimpleElement(myMod.name + " " + myMod.version, this.myModsContent.getMinContentWidth(), myMod.loadLocation.modProvider, true, this.getInterfaceStyle().inactiveTextColor, Localization.translate("ui", "modclientside"))));
                }
                ++myModsIndex;
                continue;
            }
            ModNetworkData modNetworkData = loadMod = loadListIndex < loadList.size() ? loadList.get(loadListIndex) : null;
            if (loadMod != null && loadMod.clientside) {
                this.serverModsContent.addComponent(serverModsFlow.nextY(new FormModListSimpleElement(loadMod.name + " " + loadMod.version, this.serverModsContent.getMinContentWidth(), loadMod.getModProvider(), true, this.getInterfaceStyle().inactiveTextColor, Localization.translate("ui", "modclientside"))));
                ++loadListIndex;
                continue;
            }
            if (loadMod == null) {
                if (myMod == null) break;
                this.myModsContent.addComponent(myModsFlow.nextY(new FormModListSimpleElement(myMod.name + " " + myMod.version, this.myModsContent.getMinContentWidth(), myMod.loadLocation.modProvider, true, this.getInterfaceStyle().activeTextColor)));
                ++myModsIndex;
                continue;
            }
            boolean hasMod = myMods.stream().anyMatch(m -> m.id.equals(loadMod.id));
            boolean correctVersion = hasMod && myMods.stream().anyMatch(m -> m.id.equals(loadMod.id) && m.version.equals(loadMod.version));
            Color color = this.getInterfaceStyle().activeTextColor;
            StringTooltips tooltips = null;
            if (!hasMod) {
                color = this.getInterfaceStyle().errorTextColor;
                tooltips = new StringTooltips(Localization.translate("ui", "modmissing"));
            } else if (!correctVersion) {
                color = this.getInterfaceStyle().errorTextColor;
                tooltips = new StringTooltips(Localization.translate("ui", "modinversion"));
            } else if (myMod == null || !myMod.id.equals(loadMod.id) || !myMod.version.equals(loadMod.version)) {
                color = this.getInterfaceStyle().warningTextColor;
                tooltips = new StringTooltips(Localization.translate("ui", "modinposition"));
            }
            this.serverModsContent.addComponent(serverModsFlow.nextY(new FormModListSimpleElement(loadMod.name + " " + loadMod.version, this.serverModsContent.getMinContentWidth(), loadMod.getModProvider(), true, color, tooltips)));
            ++loadListIndex;
            if (myMod == null) continue;
            this.myModsContent.addComponent(myModsFlow.nextY(new FormModListSimpleElement(myMod.name + " " + myMod.version, this.myModsContent.getMinContentWidth(), myMod.loadLocation.modProvider, true, this.getInterfaceStyle().activeTextColor)));
            ++myModsIndex;
        }
        this.useServer.setActive(hasAllMods);
        if (!hasAllMods) {
            this.useServer.setTooltip(Localization.translate("ui", "modmissingsome"));
            this.serverModsList = null;
        } else {
            this.useServer.setTooltip(null);
        }
        this.myModsContent.setContentBox(new Rectangle(this.myModsContent.getWidth(), myModsFlow.next()));
        this.serverModsContent.setContentBox(new Rectangle(this.serverModsContent.getWidth(), serverModsFlow.next()));
        this.makeCurrent(this.intro);
    }

    @Override
    public final void onContinue(Runnable continueEvent) {
        if (continueEvent != null) {
            this.continueEvents.add(continueEvent);
        }
    }

    @Override
    public final void applyContinue() {
        if (this.canContinue()) {
            this.continueEvents.forEach(Runnable::run);
            this.isContinued = true;
        }
    }

    @Override
    public boolean isContinued() {
        return this.isContinued;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.intro.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.review.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.useServerConfirmation.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }
}

