/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import necesse.engine.GameInfo;
import necesse.engine.GamePatchNotes;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.position.FormPositionDynamic;
import necesse.gfx.gameFont.FontOptions;

public abstract class PatchNotesForm
extends FormSwitcher {
    public int maxTextBoxWidth;
    public int maxTextBoxHeight;
    public Form form;
    protected FormContentBox contentBox;
    protected FormTextBox textBox;
    protected FormLocalTextButton backButton;

    public PatchNotesForm(int maxTextBoxWidth, int maxTextBoxHeight) {
        this.maxTextBoxWidth = maxTextBoxWidth;
        this.maxTextBoxHeight = maxTextBoxHeight;
        this.form = this.addComponent(new Form("patchnotes", 100, 100));
        this.updateForm();
        this.onWindowResized(WindowManager.getWindow());
        this.makeCurrent(this.form);
    }

    @Override
    protected void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener(){

            @Override
            public void onChange(Language language) {
                PatchNotesForm.this.updateForm();
                PatchNotesForm.this.onWindowResized(WindowManager.getWindow());
            }

            @Override
            public boolean isDisposed() {
                return PatchNotesForm.this.isDisposed();
            }
        });
    }

    public void updateForm() {
        this.form.clearComponents();
        FormLocalLabel label = this.form.addComponent(new FormLocalLabel("ui", "patchnotes", new FontOptions(20), 0, this.form.getWidth() / 2, 5));
        label.setPosition(new FormPositionDynamic(() -> this.form.getWidth() / 2, () -> 5));
        this.contentBox = this.form.addComponent(new FormContentBox(4, 0, this.form.getWidth() - 8, 10, GameBackground.textBox));
        FontOptions header1FontOptions = new FontOptions(32);
        FontOptions header2FontOptions = new FontOptions(20);
        FontOptions baseFontOptions = new FontOptions(16);
        this.textBox = this.contentBox.addComponent(new FormTextBox(baseFontOptions, FairType.TextAlign.LEFT, this.getInterfaceStyle().textBoxTextColor, 0, 0, this.contentBox.getMinContentWidth()));
        this.textBox.allowTyping = false;
        this.textBox.setEmptyTextSpace(new Rectangle(this.contentBox.getX(), this.contentBox.getY(), this.contentBox.getWidth(), this.contentBox.getHeight()));
        this.textBox.onChange(e -> {
            Rectangle box = this.contentBox.getContentBoxToFitComponents();
            this.contentBox.setContentBox(box);
            this.contentBox.scrollToFit(this.textBox.getCaretBoundingBox());
        });
        this.textBox.onCaretMove(e -> {
            if (!e.causedByMouse) {
                this.contentBox.scrollToFit(this.textBox.getCaretBoundingBox());
            }
        });
        this.textBox.setParsers(TypeParsers.headerParser("===", "===", true, header2FontOptions), TypeParsers.headerParser("==", "==", true, header1FontOptions), TypeParsers.MARKDOWN_URL);
        StringBuilder builder = new StringBuilder();
        for (GamePatchNotes patchNote : GameInfo.patchNotes) {
            builder.append("==").append(patchNote.version).append("==\n");
            if (patchNote.releaseDate != null) {
                builder.append(patchNote.releaseDate).append("\n");
            }
            if (patchNote.announcementLink != null) {
                builder.append("[Click here to open Steam announcement](").append(patchNote.announcementLink).append(")\n");
            }
            if (patchNote.releaseDate != null || patchNote.announcementLink != null) {
                builder.append("\n");
            }
            builder.append(patchNote.text).append("\n\n");
        }
        this.textBox.setText(builder.toString());
        this.contentBox.setScrollY(0);
        this.backButton = this.form.addComponent(new FormLocalTextButton("ui", "backbutton", 4, 0, this.form.getWidth() - 8));
        this.backButton.onClicked(e -> {
            this.textBox.setTyping(false);
            this.backPressed();
        });
        this.updateSize(WindowManager.getWindow());
    }

    public void updateSize(GameWindow window) {
        int formWidth = GameMath.limit(window.getHudWidth() - 100, Math.min(300, this.maxTextBoxWidth), this.maxTextBoxWidth);
        int formHeight = GameMath.limit(window.getHudHeight() - 150, Math.min(100, this.maxTextBoxHeight), this.maxTextBoxHeight);
        this.form.setWidth(formWidth);
        FormFlow inputFlow = new FormFlow(35);
        this.contentBox.setY(inputFlow.next(formHeight) + 4);
        this.contentBox.setWidth(this.form.getWidth() - 8);
        this.contentBox.setHeight(formHeight - 8);
        this.textBox.setMaxWidth(this.contentBox.getMinContentWidth());
        this.textBox.setEmptyTextSpace(new Rectangle(this.contentBox.getX(), this.contentBox.getY(), this.contentBox.getWidth(), this.contentBox.getHeight()));
        inputFlow.next(5);
        this.backButton.setWidth(this.form.getWidth() - 8);
        this.backButton.setY(inputFlow.next(40));
        this.form.setHeight(inputFlow.next());
        Rectangle box = this.contentBox.getContentBoxToFitComponents();
        this.contentBox.setContentBox(box);
    }

    public abstract void backPressed();

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.updateSize(window);
        this.form.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public void submitEscapeEvent(InputEvent event) {
        if (this.isCurrent(this.form)) {
            this.textBox.setTyping(false);
            this.backPressed();
            event.use();
        }
    }
}

