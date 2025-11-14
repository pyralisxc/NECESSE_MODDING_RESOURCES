/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.reports.FeedbackData;
import necesse.reports.ReportUtils;

public abstract class FeedbackForm
extends FormSwitcher {
    private static long nextFeedbackTime;
    public String state;
    public int maxTextBoxWidth;
    public int maxTextBoxHeight;
    public Form inputForm;
    protected FormTextBox textBox;
    protected FormLocalTextButton sendButton;

    public FeedbackForm(String state, int maxTextBoxWidth, int maxTextBoxHeight) {
        GameWindow window = WindowManager.getWindow();
        this.state = state;
        this.maxTextBoxWidth = maxTextBoxWidth;
        this.maxTextBoxHeight = maxTextBoxHeight;
        int inputWidth = GameMath.limit(window.getHudWidth() - 100, Math.min(300, maxTextBoxWidth), maxTextBoxWidth);
        this.inputForm = this.addComponent(new Form("feedbackinput", inputWidth, 100));
        this.updateInputForm();
        this.onWindowResized(window);
        this.makeCurrent(this.inputForm);
    }

    @Override
    protected void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener(){

            @Override
            public void onChange(Language language) {
                FeedbackForm.this.updateInputForm();
                FeedbackForm.this.onWindowResized(WindowManager.getWindow());
            }

            @Override
            public boolean isDisposed() {
                return FeedbackForm.this.isDisposed();
            }
        });
    }

    private void updateInputForm() {
        this.inputForm.clearComponents();
        int inputHeight = GameMath.limit(WindowManager.getWindow().getHudHeight() - 150, Math.min(100, this.maxTextBoxHeight), this.maxTextBoxHeight);
        FormFlow inputFlow = new FormFlow(5);
        this.inputForm.addComponent(new FormLocalLabel("ui", "givefeedback", new FontOptions(20), 0, this.inputForm.getWidth() / 2, inputFlow.next(30)));
        FormContentBox textContent = this.inputForm.addComponent(new FormContentBox(4, inputFlow.next(inputHeight) + 4, this.inputForm.getWidth() - 8, inputHeight - 8, GameBackground.textBox));
        this.textBox = textContent.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, this.getInterfaceStyle().textBoxTextColor, 0, 0, textContent.getMinContentWidth(), 40, 1000));
        this.textBox.allowTyping = true;
        this.textBox.setEmptyTextSpace(new Rectangle(textContent.getX(), textContent.getY(), textContent.getWidth(), textContent.getHeight()));
        this.textBox.onChange(e -> {
            Rectangle box = textContent.getContentBoxToFitComponents();
            textContent.setContentBox(box);
            textContent.scrollToFit(this.textBox.getCaretBoundingBox());
        });
        this.textBox.onCaretMove(e -> {
            if (!e.causedByMouse) {
                textContent.scrollToFit(this.textBox.getCaretBoundingBox());
            }
        });
        inputFlow.next(5);
        this.inputForm.addComponent(inputFlow.nextY(new FormLocalLabel("ui", "givefeedbacktip", new FontOptions(16), 0, this.inputForm.getWidth() / 2, 0, this.inputForm.getWidth()), 5));
        int buttonsY = inputFlow.next(40);
        this.sendButton = this.inputForm.addComponent(new FormLocalTextButton("ui", "sendfeedback", 4, buttonsY, this.inputForm.getWidth() / 2 - 6));
        this.sendButton.onClicked(e -> {
            String feedback = this.textBox.getText();
            if (!feedback.isEmpty()) {
                this.sendFeedback(new FeedbackData(this.state), feedback);
            }
        });
        this.inputForm.addComponent(new FormLocalTextButton("ui", "backbutton", this.inputForm.getWidth() / 2 + 2, buttonsY, this.inputForm.getWidth() / 2 - 6)).onClicked(e -> {
            this.textBox.setTyping(false);
            this.backPressed();
        });
        this.inputForm.setHeight(inputFlow.next());
    }

    private void sendFeedback(FeedbackData data, String feedback) {
        AtomicBoolean interrupted = new AtomicBoolean(false);
        Thread thread = new Thread(() -> {
            String error = ReportUtils.sendFeedback(data, feedback);
            if (!interrupted.get()) {
                if (error == null) {
                    nextFeedbackTime = System.currentTimeMillis() + 30000L;
                    NoticeForm thanksForm = this.addComponent(new NoticeForm("feedbackthanks", 300, 400), (form, current) -> {
                        if (!current.booleanValue()) {
                            this.removeComponent(form);
                        }
                    });
                    thanksForm.setupNotice(new LocalMessage("ui", "sendreportthanks"), (GameMessage)new LocalMessage("ui", "continuebutton"));
                    thanksForm.onContinue(() -> {
                        this.makeCurrent(this.inputForm);
                        this.textBox.setTyping(false);
                        this.backPressed();
                    });
                    this.textBox.setText("");
                    this.makeCurrent(thanksForm);
                } else {
                    ConfirmationForm errorForm = this.addComponent(new ConfirmationForm("feedbackerror", 300, 400), (form, current) -> {
                        if (!current.booleanValue()) {
                            this.removeComponent(form);
                        }
                    });
                    errorForm.setupConfirmation(new StaticMessage(error), (GameMessage)new LocalMessage("ui", "sendreportretry"), (GameMessage)new LocalMessage("ui", "backbutton"), () -> this.sendFeedback(data, feedback), () -> this.makeCurrent(this.inputForm));
                    this.makeCurrent(errorForm);
                }
            }
        });
        NoticeForm waitForm = this.addComponent(new NoticeForm("feedbackwait", 300, 400), (form, current) -> {
            if (!current.booleanValue()) {
                this.removeComponent(form);
            }
        });
        waitForm.setupNotice(new LocalMessage("ui", "sendingfeedback"), (GameMessage)new LocalMessage("ui", "cancelbutton"));
        waitForm.setButtonCooldown(200);
        waitForm.onContinue(() -> {
            interrupted.set(true);
            thread.interrupt();
            this.makeCurrent(this.inputForm);
        });
        this.makeCurrent(waitForm);
        thread.start();
    }

    public void startTyping() {
        this.textBox.setTyping(true);
    }

    public abstract void backPressed();

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.isCurrent(this.inputForm)) {
            long timeLeft = nextFeedbackTime - System.currentTimeMillis();
            if (timeLeft < 0L) {
                this.sendButton.setActive(!this.textBox.getText().isEmpty());
                this.sendButton.setLocalTooltip(null);
            } else {
                this.sendButton.setActive(false);
                this.sendButton.setLocalTooltip(new LocalMessage("ui", "sendfeedbackwaittime", "time", GameUtils.formatSeconds((int)Math.ceil((double)timeLeft / 1000.0))));
            }
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.inputForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public void submitEscapeEvent(InputEvent event) {
        if (this.isCurrent(this.inputForm)) {
            this.textBox.setTyping(false);
            this.backPressed();
            event.use();
        }
    }
}

