/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.FairTypeDrawOptionsContainer;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;

public class FormFairTypeButton
extends FormButton
implements FormPositionContainer {
    private FormPosition position;
    protected FormInputSize size;
    protected int width;
    protected ButtonColor color;
    protected GameMessage text;
    protected FairType customFairType;
    protected FairType.TextAlign textAlign = FairType.TextAlign.CENTER;
    public final FairTypeDrawOptionsContainer drawOptions;
    protected boolean cutLastLineWord;
    protected boolean addEllipsis;
    protected Supplier<Color> defaultColor = () -> this.getInterfaceStyle().activeTextColor;
    protected TypeParser<?>[] parsers = new TypeParser[]{TypeParsers.GAME_COLOR};
    protected FormEventsHandler<FormEvent<FormFairTypeButton>> updateEvents = new FormEventsHandler();

    public FormFairTypeButton(GameMessage text, int x, int y, int width, FormInputSize size, ButtonColor color) {
        this.position = new FormFixedPosition(x, y);
        this.text = text;
        this.width = width;
        this.size = size;
        this.color = color;
        this.drawOptions = new FairTypeDrawOptionsContainer(() -> {
            FontOptions fontOptions = this.size.getFontOptions();
            FairType type = this.customFairType != null ? this.customFairType : new FairType().append(fontOptions, this.getText().translate());
            TypeParser[] parsers = this.getParsers();
            if (parsers != null) {
                type.applyParsers(parsers);
            }
            return type.getDrawOptions(this.getTextAlign(), this.width, true, 1, this.cutLastLineWord, this.addEllipsis ? fontOptions : null, true);
        });
        this.drawOptions.onUpdate(() -> this.updateEvents.onEvent(new FormEvent<FormFairTypeButton>(this)));
    }

    public FormFairTypeButton(GameMessage text, int x, int y, int width, FormInputSize size) {
        this(text, x, y, width, size, ButtonColor.BASE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        boolean useDownTexture;
        if (this.getText().hasUpdated()) {
            this.drawOptions.reset();
        }
        Color drawCol = this.getDrawColor();
        ButtonState state = this.getButtonState();
        int textOffset = 0;
        boolean bl = useDownTexture = this.isDown() && this.isHovering();
        if (useDownTexture) {
            this.size.getButtonDownDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
            textOffset = this.size.buttonDownContentDrawOffset;
        } else {
            this.size.getButtonDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
        }
        Rectangle contentRect = this.size.getContentRectangle(this.width);
        FormShader.FormShaderState textState = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(contentRect.x, contentRect.y, contentRect.width, contentRect.height));
        try {
            FairTypeDrawOptions drawOptions = this.drawOptions.get();
            if (drawOptions != null) {
                drawOptions.draw(this.width / 2, textOffset + this.size.fontDrawOffset, this.getDefaultColor().get());
            }
        }
        finally {
            textState.end();
        }
        if (useDownTexture) {
            this.size.getButtonDownEdgeDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
        } else {
            this.size.getButtonEdgeDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
        }
        if (this.isHovering()) {
            this.addTooltips(perspective);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormFairTypeButton.singleBox(new Rectangle(this.getX(), this.getY() + this.size.textureDrawOffset, this.width, this.size.height));
    }

    protected void addTooltips(PlayerMob perspective) {
    }

    public FairType.TextAlign getTextAlign() {
        return this.textAlign;
    }

    public FormFairTypeButton setTextAlign(FairType.TextAlign textAlign) {
        this.textAlign = textAlign;
        this.drawOptions.reset();
        return this;
    }

    public FormFairTypeButton setEnding(boolean cutLastLineWord, boolean addEllipsis) {
        this.cutLastLineWord = cutLastLineWord;
        this.addEllipsis = addEllipsis;
        this.drawOptions.reset();
        return this;
    }

    public FormFairTypeButton setEnding(boolean addEllipsis) {
        return this.setEnding(addEllipsis, addEllipsis);
    }

    public Supplier<Color> getDefaultColor() {
        return this.defaultColor;
    }

    public FormFairTypeButton setDefaultColor(Supplier<Color> color) {
        this.defaultColor = color;
        this.drawOptions.reset();
        return this;
    }

    public FormFairTypeButton setDefaultColor(Color color) {
        return this.setDefaultColor(() -> color);
    }

    public TypeParser<?>[] getParsers() {
        return this.parsers;
    }

    public FormFairTypeButton setParsers(TypeParser<?> ... parsers) {
        this.parsers = parsers;
        this.drawOptions.reset();
        return this;
    }

    public FormFairTypeButton setCustomFairType(FairType fairType) {
        this.customFairType = fairType;
        this.drawOptions.reset();
        return this;
    }

    public GameMessage getText() {
        return this.text;
    }

    public FormFairTypeButton setText(GameMessage text) {
        if (text == null) {
            text = new StaticMessage("");
        }
        this.text = text;
        this.customFairType = null;
        this.drawOptions.reset();
        return this;
    }

    public FormFairTypeButton setText(String text) {
        return this.setText(new StaticMessage(text));
    }

    public FormFairTypeButton setSize(FormInputSize size) {
        this.size = size;
        this.drawOptions.reset();
        return this;
    }

    public FormFairTypeButton setWidth(FormInputSize size) {
        this.size = size;
        this.drawOptions.reset();
        return this;
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

