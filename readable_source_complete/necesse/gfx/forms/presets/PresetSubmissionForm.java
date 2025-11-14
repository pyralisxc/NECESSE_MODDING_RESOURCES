/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.level.maps.presets.Preset;
import necesse.reports.BasicsData;
import necesse.reports.ReportUtils;

public class PresetSubmissionForm
extends FormSwitcher
implements ContinueComponent {
    public static long NEXT_PRESET_SUBMISSION_TIME = 0L;
    private final ArrayList<Runnable> continueEvents = new ArrayList();
    private boolean isContinued;
    protected Preset preset;
    protected GameTexture previewTexture;
    protected Form inputForm;
    protected FormLocalCheckBox includeCreditsCheckBox;
    protected FormContentBox creditsNameContent;
    protected FormTextBox titleTextBox;
    protected FormTextBox detailsTextBox;
    protected FormTextBox creditsNameTextBox;
    protected FormLocalCheckBox agreeTermsCheckBox;
    protected FormLocalTextButton submitButton;

    public PresetSubmissionForm(int width, int maxPreviewHeight, Preset preset, final GameTexture previewTexture, String initialCreditsName) {
        this.preset = preset;
        this.previewTexture = previewTexture;
        this.inputForm = this.addComponent(new Form("presetSubmission", width, maxPreviewHeight));
        FormFlow inputFlow = new FormFlow(4);
        this.inputForm.addComponent(inputFlow.nextY(new FormLocalLabel("ui", "presetsubmission", new FontOptions(20), 0, this.inputForm.getWidth() / 2, 0), 15));
        int previewPadding = 10;
        int previewWidth = Math.min(previewTexture.getWidth(), width - previewPadding * 2);
        int previewHeight = Math.min(previewTexture.getHeight(), maxPreviewHeight);
        TextureDrawOptionsEnd previewDrawOptions = previewTexture.initDraw();
        if (previewDrawOptions.getWidth() > previewWidth) {
            previewDrawOptions = previewDrawOptions.size(previewWidth, false);
        }
        if (previewDrawOptions.getHeight() > previewHeight) {
            previewDrawOptions = previewDrawOptions.size(previewHeight, false);
        }
        int previewX = (this.inputForm.getWidth() - previewDrawOptions.getWidth()) / 2;
        this.inputForm.addComponent(new FormCustomDraw(previewX, inputFlow.next(previewDrawOptions.getHeight()), previewDrawOptions.getWidth(), previewDrawOptions.getHeight()){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                TextureDrawOptionsEnd drawOptions = previewTexture.initDraw().mirrorY().size(this.width, this.height);
                int xOffset = (this.width - drawOptions.getWidth()) / 2;
                int yOffset = (this.height - drawOptions.getHeight()) / 2;
                drawOptions.draw(this.getX() + xOffset, this.getY() + yOffset);
                GameBackground.form.getEdgeDrawOptions(this.getX(), this.getY(), this.width, this.height).draw();
            }
        });
        inputFlow.next(15);
        this.inputForm.addComponent(inputFlow.nextY(new FormLocalLabel("ui", "presetsubmissiontitle", new FontOptions(16), -1, 4, 0), 2));
        this.inputForm.addComponent(inputFlow.nextY(new FormLocalLabel("ui", "presetsubmissiontitletip", new FontOptions(12), -1, 4, 0, this.inputForm.getWidth() - 8), 4));
        int titleHeight = 36;
        FormContentBox titleContent = this.inputForm.addComponent(new FormContentBox(4, inputFlow.next(titleHeight) + 4, this.inputForm.getWidth() - 8, titleHeight - 8, GameBackground.textBox){

            @Override
            protected boolean hasScrollbarX() {
                return false;
            }

            @Override
            protected boolean hasScrollbarY() {
                return false;
            }
        });
        this.titleTextBox = titleContent.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, this.getInterfaceStyle().textBoxTextColor, 4, 4, -1, 1, 100));
        this.titleTextBox.allowTyping = true;
        this.titleTextBox.setEmptyTextSpace(new Rectangle(titleContent.getX(), titleContent.getY(), titleContent.getWidth(), titleContent.getHeight()));
        this.titleTextBox.onChange(e -> {
            Rectangle box = titleContent.getContentBoxToFitComponents();
            box.width += box.x + 4;
            box.height += box.y + 4;
            box.x = 0;
            box.y = 0;
            titleContent.setContentBox(box);
            titleContent.scrollToFitForced(this.titleTextBox.getCaretBoundingBox());
        });
        this.titleTextBox.onCaretMove(e -> {
            if (!e.causedByMouse) {
                titleContent.scrollToFitForced(this.titleTextBox.getCaretBoundingBox());
            }
        });
        this.titleTextBox.tabTypingComponent = () -> this.detailsTextBox;
        this.inputForm.addComponent(inputFlow.nextY(new FormLocalLabel("ui", "presetsubmissiondetails", new FontOptions(16), -1, 4, 0), 2));
        this.inputForm.addComponent(inputFlow.nextY(new FormLocalLabel("ui", "presetsubmissiondetailstip", new FontOptions(12), -1, 4, 0, this.inputForm.getWidth() - 8), 4));
        int detailsHeight = 100;
        FormContentBox detailsContent = this.inputForm.addComponent(new FormContentBox(4, inputFlow.next(detailsHeight) + 4, this.inputForm.getWidth() - 8, detailsHeight - 8, GameBackground.textBox));
        this.detailsTextBox = detailsContent.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, this.getInterfaceStyle().textBoxTextColor, 4, 4, detailsContent.getMinContentWidth() - 8, 40, 1000));
        this.detailsTextBox.allowTyping = true;
        this.detailsTextBox.setEmptyTextSpace(new Rectangle(detailsContent.getX(), detailsContent.getY(), detailsContent.getWidth(), detailsContent.getHeight()));
        this.detailsTextBox.onChange(e -> {
            Rectangle box = detailsContent.getContentBoxToFitComponents();
            box.width += box.x + 4;
            box.height += box.y + 4;
            box.x = 0;
            box.y = 0;
            detailsContent.setContentBox(box);
            Rectangle caretBoundingBox = this.detailsTextBox.getCaretBoundingBox();
            caretBoundingBox.height += 4;
            detailsContent.scrollToFit(caretBoundingBox);
        });
        this.detailsTextBox.onCaretMove(e -> {
            if (!e.causedByMouse) {
                detailsContent.scrollToFit(this.detailsTextBox.getCaretBoundingBox());
            }
        });
        this.detailsTextBox.tabTypingComponent = () -> {
            if (this.includeCreditsCheckBox.checked) {
                return this.creditsNameTextBox;
            }
            return this.titleTextBox;
        };
        inputFlow.next(5);
        this.includeCreditsCheckBox = this.inputForm.addComponent(inputFlow.nextY(new FormLocalCheckBox("ui", "presetsubmissionincludecredits", 4, 0, false, this.inputForm.getWidth() - 8).useButtonTexture(), 4));
        this.includeCreditsCheckBox.onClicked(e -> {
            this.creditsNameContent.backgroundDrawBrightness = ((FormCheckBox)e.from).checked ? 1.0f : 0.5f;
            this.creditsNameTextBox.allowTyping = ((FormCheckBox)e.from).checked;
            this.creditsNameTextBox.allowTextSelect = ((FormCheckBox)e.from).checked;
            this.creditsNameTextBox.allowCaretSelect = ((FormCheckBox)e.from).checked;
            if (!((FormCheckBox)e.from).checked) {
                this.creditsNameTextBox.setTyping(false);
            }
        });
        this.inputForm.addComponent(inputFlow.nextY(new FormLocalLabel("ui", "presetsubmissioncreditsname", new FontOptions(16), -1, 4, 0), 4));
        int creditsNameHeight = 36;
        this.creditsNameContent = this.inputForm.addComponent(new FormContentBox(4, inputFlow.next(creditsNameHeight) + 4, this.inputForm.getWidth() - 8, creditsNameHeight - 8, GameBackground.textBox){

            @Override
            protected boolean hasScrollbarX() {
                return false;
            }

            @Override
            protected boolean hasScrollbarY() {
                return false;
            }
        });
        this.creditsNameTextBox = this.creditsNameContent.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, this.getInterfaceStyle().textBoxTextColor, 4, 4, -1, 1, 30));
        this.creditsNameTextBox.allowTyping = false;
        this.creditsNameContent.backgroundDrawBrightness = 0.5f;
        this.creditsNameTextBox.setText(initialCreditsName);
        this.creditsNameTextBox.setEmptyTextSpace(new Rectangle(this.creditsNameContent.getX(), this.creditsNameContent.getY(), this.creditsNameContent.getWidth(), this.creditsNameContent.getHeight()));
        this.creditsNameTextBox.onChange(e -> {
            Rectangle box = this.creditsNameContent.getContentBoxToFitComponents();
            box.width += box.x + 4;
            box.height += box.y + 4;
            box.x = 0;
            box.y = 0;
            this.creditsNameContent.setContentBox(box);
            this.creditsNameContent.scrollToFitForced(this.creditsNameTextBox.getCaretBoundingBox());
        });
        this.creditsNameTextBox.onCaretMove(e -> {
            if (!e.causedByMouse) {
                this.creditsNameContent.scrollToFitForced(this.creditsNameTextBox.getCaretBoundingBox());
            }
        });
        this.creditsNameTextBox.tabTypingComponent = () -> this.titleTextBox;
        inputFlow.next(5);
        this.agreeTermsCheckBox = this.inputForm.addComponent(inputFlow.nextY(new FormLocalCheckBox("ui", "presetsubmissionagree", 4, 0, false, this.inputForm.getWidth() - 8).useButtonTexture(), 5));
        this.agreeTermsCheckBox.handleClicksIfNoEventHandlers = true;
        inputFlow.next(5);
        int endButtonsY = inputFlow.next(36);
        int endButtonsWidth = this.inputForm.getWidth() / 2 - 4 - 2;
        this.submitButton = this.inputForm.addComponent(new FormLocalTextButton("ui", "presetsubmissionsend", 4, endButtonsY, endButtonsWidth, FormInputSize.SIZE_32, ButtonColor.BASE));
        this.submitButton.onClicked(e -> {
            String title = this.titleTextBox.getText();
            String details = this.detailsTextBox.getText();
            boolean includeCredits = this.includeCreditsCheckBox.checked;
            String creditsName = this.creditsNameTextBox.getText();
            if (title.isEmpty() || details.isEmpty() || includeCredits && creditsName.isEmpty()) {
                return;
            }
            this.submitPreset(preset, previewTexture, title, details, includeCredits, creditsName);
        });
        this.inputForm.addComponent(new FormLocalTextButton("ui", "closebutton", 4 + endButtonsWidth + 4, endButtonsY, endButtonsWidth, FormInputSize.SIZE_32, ButtonColor.BASE)).onClicked(e -> this.applyContinue());
        this.inputForm.setHeight(inputFlow.next());
        this.makeCurrent(this.inputForm);
        this.onWindowResized(WindowManager.getWindow());
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (!event.isUsed() && event.state && (event.getID() == 256 || event.getID() == Control.INVENTORY.getKey())) {
            this.applyContinue();
            event.use();
            return;
        }
        super.handleInputEvent(event, tickManager, perspective);
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (!event.isUsed() && event.buttonState && (event.getState() == ControllerInput.MENU_BACK || event.getState() == ControllerInput.MAIN_MENU || event.getState() == ControllerInput.INVENTORY)) {
            this.applyContinue();
            event.use();
            return;
        }
        super.handleControllerEvent(event, tickManager, perspective);
    }

    @Override
    public void onContinue(Runnable continueEvent) {
        if (continueEvent != null) {
            this.continueEvents.add(continueEvent);
        }
    }

    @Override
    public void applyContinue() {
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
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        String title = this.titleTextBox.getText();
        String details = this.detailsTextBox.getText();
        boolean includeCredits = this.includeCreditsCheckBox.checked;
        String creditsName = this.creditsNameTextBox.getText();
        boolean agreedTerms = this.agreeTermsCheckBox.checked;
        boolean submitButtonActive = !title.isEmpty() && !details.isEmpty() && (!includeCredits || !creditsName.isEmpty()) && agreedTerms && NEXT_PRESET_SUBMISSION_TIME <= System.currentTimeMillis();
        this.submitButton.setActive(submitButtonActive);
        if (!submitButtonActive) {
            if (NEXT_PRESET_SUBMISSION_TIME <= System.currentTimeMillis()) {
                this.submitButton.setLocalTooltip(new LocalMessage("ui", "presetsubmissionmissing"));
            } else {
                this.submitButton.setLocalTooltip(new LocalMessage("ui", "presetsubmittodevscooldown"));
            }
        } else {
            this.submitButton.setLocalTooltip(null);
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.previewTexture.delete();
        if (this.detailsTextBox != null) {
            this.detailsTextBox.setTyping(false);
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.inputForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    private void submitPreset(Preset preset, GameTexture previewTexture, String title, String details, boolean includeCredits, String creditsName) {
        AtomicBoolean interrupted = new AtomicBoolean(false);
        Thread thread = new Thread(() -> {
            String error = ReportUtils.submitPreset(new BasicsData("MainGame"), this.preset, this.previewTexture, title, details, includeCredits, creditsName);
            if (!interrupted.get()) {
                if (error == null) {
                    NEXT_PRESET_SUBMISSION_TIME = System.currentTimeMillis() + 60000L;
                    NoticeForm thanksForm = this.addComponent(new NoticeForm("presetthanks", 500, 400), (form, current) -> {
                        if (!current.booleanValue()) {
                            this.removeComponent(form);
                        }
                    });
                    thanksForm.setupNotice(contentBox -> {
                        FormFlow thanksFlow = new FormFlow(15);
                        contentBox.addComponent(thanksFlow.nextY(new FormLocalLabel("ui", "presetsubmitthanks", new FontOptions(20), 0, contentBox.getWidth() / 2, 10, contentBox.getWidth() - 20), 15));
                        contentBox.addComponent(thanksFlow.nextY(new FormLocalLabel("ui", "presetsubmitreviews", new FontOptions(20), 0, contentBox.getWidth() / 2, 10, contentBox.getWidth() - 20), 15));
                        contentBox.addComponent(thanksFlow.nextY(new FormLocalLabel("ui", "presetsubmitreviewswhen", new FontOptions(20), 0, contentBox.getWidth() / 2, 10, contentBox.getWidth() - 20), 10));
                        int socialButtonsY = thanksFlow.next(47);
                        ArrayList<Object[]> socialButtons = new ArrayList<Object[]>();
                        socialButtons.add(new Object[]{this.getInterfaceStyle().steam_logo, new LocalMessage("misc", "followsteamnews"), "https://store.steampowered.com/news/app/1169040"});
                        socialButtons.add(new Object[]{this.getInterfaceStyle().discord_logo, new LocalMessage("misc", "joindiscord"), "https://discord.gg/YBhNh52dpy"});
                        socialButtons.add(new Object[]{this.getInterfaceStyle().x_logo, new LocalMessage("misc", "followx"), "https://x.com/NecesseGame"});
                        socialButtons.add(new Object[]{this.getInterfaceStyle().youtube_logo, new LocalMessage("misc", "subscribeyoutube"), "https://www.youtube.com/@Necesse?sub_confirmation=1"});
                        socialButtons.add(new Object[]{this.getInterfaceStyle().reddit_logo, new LocalMessage("misc", "joinreddit"), "https://reddit.com/r/Necesse"});
                        int socialButtonsSize = 32;
                        int socialButtonsPadding = 4;
                        int socialButtonsUsableWidth = contentBox.getWidth() - socialButtonsPadding * 2;
                        int socialButtonsXOffset = (socialButtonsUsableWidth - socialButtons.size() * (socialButtonsSize + socialButtonsPadding) - socialButtonsPadding) / 2;
                        for (int i = 0; i < socialButtons.size(); ++i) {
                            Object[] socialButton = (Object[])socialButtons.get(i);
                            ButtonIcon icon = (ButtonIcon)socialButton[0];
                            GameMessage message = (GameMessage)socialButton[1];
                            String url = (String)socialButton[2];
                            contentBox.addComponent(new FormContentIconButton(socialButtonsXOffset + i * (socialButtonsSize + socialButtonsPadding), socialButtonsY, FormInputSize.SIZE_32, ButtonColor.BASE, icon, message)).onClicked(e -> GameUtils.openURL(url));
                        }
                    });
                    thanksForm.onContinue(() -> {
                        this.makeCurrent(this.inputForm);
                        this.detailsTextBox.setTyping(false);
                        this.applyContinue();
                    });
                    this.detailsTextBox.setText("");
                    this.makeCurrent(thanksForm);
                } else {
                    ConfirmationForm errorForm = this.addComponent(new ConfirmationForm("presetsubmiterror", 500, 400), (form, current) -> {
                        if (!current.booleanValue()) {
                            this.removeComponent(form);
                        }
                    });
                    errorForm.setupConfirmation(new StaticMessage(error), (GameMessage)new LocalMessage("ui", "sendreportretry"), (GameMessage)new LocalMessage("ui", "backbutton"), () -> this.submitPreset(preset, previewTexture, title, details, includeCredits, creditsName), () -> this.makeCurrent(this.inputForm));
                    this.makeCurrent(errorForm);
                }
            }
        });
        NoticeForm waitForm = this.addComponent(new NoticeForm("submitpresetwait", 300, 400), (form, current) -> {
            if (!current.booleanValue()) {
                this.removeComponent(form);
            }
        });
        waitForm.setupNotice(new LocalMessage("ui", "presetsubmitting"), (GameMessage)new LocalMessage("ui", "cancelbutton"));
        waitForm.setButtonCooldown(200);
        waitForm.onContinue(() -> {
            interrupted.set(true);
            thread.interrupt();
            this.makeCurrent(this.inputForm);
        });
        this.makeCurrent(waitForm);
        thread.start();
    }
}

