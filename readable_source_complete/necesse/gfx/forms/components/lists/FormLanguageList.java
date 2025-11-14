/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.ArrayList;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.fileLanguage.FileLanguage;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.screenHudManager.UniqueScreenFloatText;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.forms.components.lists.FormSelectedElement;
import necesse.gfx.forms.components.lists.FormSelectedList;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormLanguageList
extends FormSelectedList<LanguageElement> {
    protected FormEventsHandler<FormLanguageSelectEvent> languageSelect = new FormEventsHandler();

    public FormLanguageList(int x, int y, int width, int height) {
        super(x, y, width, height, 20);
    }

    @Override
    public void reset() {
        super.reset();
        ArrayList<Language> official = new ArrayList<Language>();
        ArrayList<Language> community = new ArrayList<Language>();
        for (Language language : Localization.getLanguages()) {
            if (Localization.isOfficial(language)) {
                official.add(language);
                continue;
            }
            community.add(language);
        }
        for (Language language : official) {
            if (language.isDebugOnly() && !GlobalData.isDevMode()) continue;
            this.elements.add(new LanguageElement(language));
            if (Localization.getCurrentLang() != language) continue;
            this.setSelected(this.elements.size() - 1);
        }
        if (!community.isEmpty()) {
            this.elements.add(new LanguageElement(new StaticMessage("")));
            this.elements.add(new LanguageElement(new LocalMessage("settingsui", "communitytranslations")));
        }
        for (Language language : community) {
            if (language.isDebugOnly() && !GlobalData.isDevMode()) continue;
            this.elements.add(new LanguageElement(language));
            if (Localization.getCurrentLang() != language) continue;
            this.setSelected(this.elements.size() - 1);
        }
    }

    public FormLanguageList onLanguageSelect(FormEventListener<FormLanguageSelectEvent> listener) {
        this.languageSelect.addListener(listener);
        return this;
    }

    public class LanguageElement
    extends FormSelectedElement<FormLanguageList> {
        public Language language;
        public GameMessage header;

        public LanguageElement(Language language) {
            this.language = language;
        }

        public LanguageElement(GameMessage header) {
            this.header = header;
        }

        public boolean isHeader() {
            return this.language == null;
        }

        @Override
        protected void draw(FormLanguageList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            if (this.isHeader()) {
                String header = this.header.translate();
                FontOptions options = new FontOptions(20).color(FormLanguageList.this.getInterfaceStyle().activeTextColor);
                int stringWidth = FontManager.bit.getWidthCeil(header, options);
                FontManager.bit.drawString(parent.width / 2 - stringWidth / 2, 0.0f, header, options);
            } else {
                String str;
                boolean mouseOver = this.isMouseOver(parent);
                Color col = mouseOver ? FormLanguageList.this.getInterfaceStyle().highlightTextColor : FormLanguageList.this.getInterfaceStyle().activeTextColor;
                String string = str = this.language.localDisplayName.equals(this.language.englishDisplayName) ? this.language.localDisplayName : this.language.localDisplayName + " (" + this.language.englishDisplayName + ")";
                if (this.isSelected()) {
                    str = "> " + str + " <";
                }
                FontOptions options = new FontOptions(16).color(col);
                int width = FontManager.bit.getWidthCeil(str, options);
                FontManager.bit.drawString(parent.width / 2 - width / 2, 2.0f, str, options);
                if (mouseOver) {
                    ListGameTooltips tooltips = new ListGameTooltips();
                    this.language.addTooltips(tooltips);
                    GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
                }
            }
        }

        @Override
        protected void onClick(FormLanguageList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            if (this.isHeader()) {
                return;
            }
            if (event.getID() == -100) {
                GameWindow window = WindowManager.getWindow();
                if (window.isKeyDown(340) || window.isKeyDown(344)) {
                    if (this.language != Localization.English && this.language instanceof FileLanguage) {
                        FileLanguage fileLanguage = (FileLanguage)this.language;
                        ((FileLanguage)Localization.English).fixAndPrintLanguageFile(fileLanguage, fileLanguage.getFilePath(), true);
                        UniqueScreenFloatText floatText = new UniqueScreenFloatText(window.mousePos().hudX, window.mousePos().hudY, "Fixed language file missing keys", new FontOptions(16).outline(), "languageFix");
                        floatText.hoverTime = 4000;
                        Renderer.hudManager.addElement(floatText);
                        FormLanguageList.this.playTickSound();
                    }
                } else {
                    super.onClick(parent, elementIndex, event, perspective);
                    FormLanguageList.this.playTickSound();
                    parent.languageSelect.onEvent(new FormLanguageSelectEvent(parent, elementIndex, this.language));
                }
            }
        }

        @Override
        protected void onControllerEvent(FormLanguageList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (this.isHeader()) {
                return;
            }
            if (event.getState() == ControllerInput.MENU_SELECT) {
                super.onControllerEvent(parent, elementIndex, event, tickManager, perspective);
                FormLanguageList.this.playTickSound();
                parent.languageSelect.onEvent(new FormLanguageSelectEvent(parent, elementIndex, this.language));
                event.use();
            }
        }

        @Override
        public void drawControllerFocus(ControllerFocus current) {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }

    public static class FormLanguageSelectEvent
    extends FormEvent<FormLanguageList> {
        public final int index;
        public final Language language;

        public FormLanguageSelectEvent(FormLanguageList from, int index, Language language) {
            super(from);
            this.index = index;
            this.language = language;
        }
    }
}

