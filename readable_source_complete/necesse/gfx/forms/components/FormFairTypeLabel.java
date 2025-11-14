/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.FairTypeDrawOptionsContainer;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;

public class FormFairTypeLabel
extends FormComponent
implements FormPositionContainer {
    protected FormPosition position;
    protected boolean isHovering;
    public boolean useHoverMoveEvents = true;
    protected GameMessage text;
    protected FontOptions fontOptions;
    protected FairType.TextAlign textAlign;
    protected FairType customFairType;
    public final FairTypeDrawOptionsContainer drawOptions;
    protected int maxWidth = -1;
    protected int maxLines = -1;
    protected boolean cutLastLineWord;
    protected boolean addEllipsis;
    protected Supplier<Color> color = () -> this.getInterfaceStyle().activeTextColor;
    protected TypeParser[] parsers = new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.URL_OPEN, TypeParsers.MARKDOWN_URL};
    protected FormEventsHandler<FormEvent<FormFairTypeLabel>> updateEvents = new FormEventsHandler();

    public FormFairTypeLabel(GameMessage text, FontOptions fontOptions, FairType.TextAlign textAlign, int x, int y) {
        this.fontOptions = fontOptions;
        this.textAlign = textAlign;
        this.drawOptions = new FairTypeDrawOptionsContainer(() -> {
            FairType type;
            FairType fairType = type = this.customFairType != null ? this.customFairType : new FairType().append(this.fontOptions, this.getText().translate());
            if (this.getParsers() != null) {
                type.applyParsers(this.getParsers());
            }
            return type.getDrawOptions(this.getTextAlign(), this.getMaxWidth(), true, this.getMaxLines(), this.cutLastLineWord, this.addEllipsis ? this.fontOptions : null, true);
        });
        this.drawOptions.onUpdate(() -> this.updateEvents.onEvent(new FormEvent<FormFairTypeLabel>(this)));
        this.setPosition(new FormFixedPosition(x, y));
        this.setText(text);
    }

    public FormFairTypeLabel(GameMessage text, int x, int y) {
        this(text, new FontOptions(16), FairType.TextAlign.LEFT, x, y);
    }

    public FormFairTypeLabel(String text, int x, int y) {
        this(new StaticMessage(text), x, y);
    }

    public FormFairTypeLabel onUpdated(FormEventListener<FormEvent<FormFairTypeLabel>> listener) {
        this.updateEvents.addListener(listener);
        return this;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        FairTypeDrawOptions drawOptions = this.drawOptions.get();
        boolean nextIsMouseOver = false;
        if (event.isMouseMoveEvent() && this.isHovering != (nextIsMouseOver = this.isMouseOver(event))) {
            this.isHovering = nextIsMouseOver;
        }
        if (drawOptions != null) {
            drawOptions.handleInputEvent(this.getX(), this.getY(), event);
        }
        if (nextIsMouseOver && this.useHoverMoveEvents) {
            event.useMove();
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
        FairTypeDrawOptions drawOptions;
        if (this.getText().hasUpdated()) {
            this.drawOptions.reset();
        }
        if ((drawOptions = this.drawOptions.get()) != null) {
            drawOptions.draw(this.getX(), this.getY(), this.getColor().get());
        }
    }

    public boolean isHovering() {
        return this.isHovering;
    }

    public boolean displaysFullText() {
        FairTypeDrawOptions drawOptions = this.drawOptions.get();
        if (drawOptions != null) {
            drawOptions.displaysEverything();
        }
        return true;
    }

    public void setText(GameMessage text, TypeParser ... parsers) {
        this.text = text;
        this.parsers = parsers;
    }

    @Override
    public List<Rectangle> getHitboxes() {
        FairTypeDrawOptions drawOptions = this.drawOptions.get();
        if (drawOptions != null) {
            return FormFairTypeLabel.singleBox(drawOptions.getBoundingBox(this.getX(), this.getY()));
        }
        return FormFairTypeLabel.singleBox(new Rectangle());
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    public FormFairTypeLabel setCustomFairType(FairType fairType) {
        this.customFairType = fairType;
        this.drawOptions.reset();
        return this;
    }

    public GameMessage getText() {
        return this.text;
    }

    public FormFairTypeLabel setText(GameMessage text) {
        if (text == null) {
            text = new StaticMessage("");
        }
        this.text = text;
        this.customFairType = null;
        this.drawOptions.reset();
        return this;
    }

    public FormFairTypeLabel setText(String text) {
        return this.setText(new StaticMessage(text));
    }

    public FontOptions getFontOptions() {
        return this.fontOptions;
    }

    public FormFairTypeLabel setFontOptions(FontOptions fontOptions) {
        this.fontOptions = fontOptions;
        this.drawOptions.reset();
        return this;
    }

    public FairType.TextAlign getTextAlign() {
        return this.textAlign;
    }

    public FormFairTypeLabel setTextAlign(FairType.TextAlign textAlign) {
        this.textAlign = textAlign;
        this.drawOptions.reset();
        return this;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public FormFairTypeLabel setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        this.drawOptions.reset();
        return this;
    }

    public int getMaxLines() {
        return this.maxLines;
    }

    public FormFairTypeLabel setMaxLines(int maxLines, boolean cutLastLineWord, boolean addEllipsis) {
        this.maxLines = maxLines;
        this.cutLastLineWord = cutLastLineWord;
        this.addEllipsis = addEllipsis;
        this.drawOptions.reset();
        return this;
    }

    public FormFairTypeLabel setMaxLines(int maxLines, boolean addEllipsis) {
        return this.setMaxLines(maxLines, addEllipsis, addEllipsis);
    }

    public FormFairTypeLabel setMax(int maxWidth, int maxLines, boolean cutLastLineWord, boolean addEllipsis) {
        this.setMaxWidth(maxWidth);
        return this.setMaxLines(maxLines, cutLastLineWord, addEllipsis);
    }

    public FormFairTypeLabel setMax(int maxWidth, int maxLines, boolean addEllipsis) {
        return this.setMax(maxWidth, maxLines, addEllipsis, addEllipsis);
    }

    public Supplier<Color> getColor() {
        return this.color;
    }

    public FormFairTypeLabel setColor(Supplier<Color> color) {
        this.color = color;
        this.drawOptions.reset();
        return this;
    }

    public FormFairTypeLabel setColor(Color color) {
        return this.setColor(() -> color);
    }

    public TypeParser[] getParsers() {
        return this.parsers;
    }

    public FormFairTypeLabel setParsers(TypeParser ... parsers) {
        this.parsers = parsers;
        this.drawOptions.reset();
        return this;
    }
}

