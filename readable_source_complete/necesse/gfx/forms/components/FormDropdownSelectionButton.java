/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import necesse.engine.MouseDraggingElement;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.FormClickHandler;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormDraggingEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.events.FormValueEvent;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;
import necesse.gfx.ui.HoverStateTextures;

public class FormDropdownSelectionButton<T>
extends FormComponent
implements FormPositionContainer {
    public boolean setSelectedText = true;
    public boolean removingSubmenuRemovesParent = true;
    protected FormPosition position;
    protected boolean isActive = true;
    protected int width;
    private GameMessage text;
    protected FormInputSize size;
    protected ButtonColor color;
    protected boolean isHovering;
    public int textAlign = 0;
    public boolean alignLeftIfNotFit = true;
    protected int wantedWidth;
    private T selected = null;
    public final OptionsList<T> options = new OptionsList(this);
    private SelectionFloatMenu currentMenu;
    public boolean useHoverMoveEvents = true;
    public boolean acceptRightClicks = false;
    public boolean submitControllerPressEvent = false;
    public double draggingStartDistance = 5.0;
    protected FormClickHandler clickHandler = new FormClickHandler(e -> this.isActive() && this.isMouseOver((InputEvent)e) && (this.currentMenu == null || this.currentMenu.isDisposed() && !InputEvent.isFromSameEvent(e, this.currentMenu.removeEvent)), e -> e.getID() == -100 || e.getID() == -99 && this.acceptRightClicks, e -> {
        this.playTickSound();
        this.currentMenu = ((OptionsList)this.options).getMenu(this.width - 4, this.removingSubmenuRemovesParent);
        if (e.isControllerEvent()) {
            ControllerFocus currentFocus = this.getManager().getCurrentFocus();
            if (currentFocus != null) {
                this.getManager().openFloatMenuAt(this.currentMenu, currentFocus.boundingBox.x, currentFocus.boundingBox.y + this.size.textureDrawOffset + this.size.height);
            } else {
                this.getManager().openFloatMenuAt(this.currentMenu, 0, 0);
            }
        } else {
            this.getManager().openFloatMenu((FloatMenu)this.currentMenu, this.getX() - e.pos.hudX, this.getY() - e.pos.hudY + this.size.textureDrawOffset + this.size.height);
        }
    });
    protected FormEventsHandler<FormValueEvent<FormDropdownSelectionButton<?>, T>> selectedEvents = new FormEventsHandler();
    protected FormEventsHandler<FormInputEvent<FormDropdownSelectionButton<T>>> changedHoverEvents = new FormEventsHandler();
    protected FormEventsHandler<FormDraggingEvent<FormDropdownSelectionButton<T>>> dragStartedEvents = new FormEventsHandler();
    protected InputEvent dragStartDownEvent;

    public FormDropdownSelectionButton(int x, int y, FormInputSize size, ButtonColor color, int width, GameMessage startMessage) {
        this.position = new FormFixedPosition(x, y);
        this.size = size;
        this.width = width;
        this.color = color;
        this.setSelected(null, startMessage);
    }

    public FormDropdownSelectionButton(int x, int y, FormInputSize size, ButtonColor color, int width) {
        this(x, y, size, color, width, new LocalMessage("ui", "selectbutton"));
    }

    public FormDropdownSelectionButton<T> onSelected(FormEventListener<FormValueEvent<FormDropdownSelectionButton<?>, T>> listener) {
        this.selectedEvents.addListener(listener);
        return this;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            boolean nextIsMouseOver;
            double distance;
            InputEvent downEvent;
            if (this.clickHandler.isDown() && (downEvent = this.clickHandler.getDownEvent()) != null && downEvent != this.dragStartDownEvent && (distance = GameMath.diagonalMoveDistance(downEvent.pos.windowX, downEvent.pos.windowY, event.pos.windowX, event.pos.windowY)) >= this.draggingStartDistance) {
                this.dragStartDownEvent = downEvent;
                this.dragStartedEvents.onEvent(new FormDraggingEvent<FormDropdownSelectionButton>(this, event, this.dragStartDownEvent));
            }
            if (this.isHovering != (nextIsMouseOver = this.isMouseOver(event))) {
                this.isHovering = nextIsMouseOver;
                FormInputEvent<FormDropdownSelectionButton> fEvent = new FormInputEvent<FormDropdownSelectionButton>(this, event);
                this.changedHoverEvents.onEvent(fEvent);
            }
            if (nextIsMouseOver && this.useHoverMoveEvents) {
                event.useMove();
            }
        } else {
            this.clickHandler.handleEvent(event);
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isActive() && (event.getState() == ControllerInput.MENU_SELECT || this.acceptRightClicks && event.getState() == ControllerInput.MENU_BACK)) {
            if (this.isControllerFocus() && this.submitControllerPressEvent) {
                if (event.buttonState) {
                    InputEvent inputEvent = InputEvent.ControllerButtonEvent(event, tickManager);
                    this.clickHandler.forceHandleEvent(inputEvent);
                    event.use();
                    this.clickHandler.forceHandleEvent(InputEvent.ControllerButtonEvent(ControllerEvent.buttonEvent(event.controllerHandle, event.getState(), false), tickManager));
                    event.use();
                }
            } else if (this.isControllerFocus() && event.buttonState) {
                InputEvent inputEvent = InputEvent.ControllerButtonEvent(event, tickManager);
                this.clickHandler.forceHandleEvent(inputEvent);
                event.use();
            } else if (!event.buttonState && this.clickHandler.isDown()) {
                this.clickHandler.forceHandleEvent(InputEvent.ControllerButtonEvent(event, tickManager));
                event.use();
            }
        }
    }

    @Override
    public void onControllerUnfocused(ControllerFocus current) {
        this.clickHandler.reset();
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    public FormDropdownSelectionButton<T> onChangedHover(FormEventListener<FormInputEvent<FormDropdownSelectionButton<T>>> listener) {
        this.changedHoverEvents.addListener(listener);
        return this;
    }

    public FormDropdownSelectionButton<T> onDragStarted(FormEventListener<FormDraggingEvent<FormDropdownSelectionButton<T>>> listener) {
        this.dragStartedEvents.addListener(listener);
        return this;
    }

    public void setupDragToOtherButtons(Object sameDropdownObject, boolean submitSelectedEvent, Predicate<T> isValidObject) {
        this.onDragStarted(e -> {
            final int id = e.draggingStartedEvent.isUsed() ? e.draggingStartedEvent.getLastID() : e.draggingStartedEvent.getID();
            Renderer.setMouseDraggingElement(new FormDropdownSelectionDraggingElement(this, sameDropdownObject){

                @Override
                public boolean isKeyDown(Input input) {
                    return input.isKeyDown(id);
                }
            });
        });
        this.onChangedHover(e -> {
            MouseDraggingElement draggingElement;
            if (this.isHovering() && this.isActive() && (draggingElement = Renderer.getMouseDraggingElement()) instanceof FormDropdownSelectionDraggingElement) {
                FormDropdownSelectionDraggingElement thisElement = (FormDropdownSelectionDraggingElement)draggingElement;
                if (thisElement.component != this && !Objects.equals(this.selected, thisElement.component.selected) && Objects.equals(thisElement.sameObject, sameDropdownObject)) {
                    try {
                        if (isValidObject == null || isValidObject.test(thisElement.component.selected)) {
                            this.selected = thisElement.component.selected;
                            if (submitSelectedEvent) {
                                this.selectedEvents.onEvent(new FormValueEvent<FormDropdownSelectionButton, T>(this, this.selected));
                            }
                            if (this.setSelectedText) {
                                this.text = thisElement.component.text;
                            }
                            this.playTickSound();
                        }
                    }
                    catch (ClassCastException classCastException) {
                        // empty catch block
                    }
                }
            }
        });
    }

    public void setupDragToOtherButtons(Object sameDropdownObject, Predicate<T> isValidObject) {
        this.setupDragToOtherButtons(sameDropdownObject, true, isValidObject);
    }

    protected boolean isDraggingThis() {
        MouseDraggingElement draggingElement = Renderer.getMouseDraggingElement();
        if (draggingElement instanceof FormDropdownSelectionDraggingElement) {
            FormDropdownSelectionDraggingElement thisElement = (FormDropdownSelectionDraggingElement)draggingElement;
            return thisElement.component == this;
        }
        return false;
    }

    private void selectedOption(Option<T> option) {
        FormValueEvent event = new FormValueEvent(this, option.value);
        T oldSelected = this.selected;
        this.selected = option.value;
        this.selectedEvents.onEvent(event);
        if (event.hasPreventedDefault()) {
            this.selected = oldSelected;
        } else if (this.currentMenu != null && !this.currentMenu.isDisposed()) {
            this.currentMenu.remove();
        }
        if (this.setSelectedText) {
            this.text = option.text;
        }
    }

    public void setSelected(T value, GameMessage text) {
        this.selected = value;
        this.text = text;
    }

    public T getSelected() {
        return this.selected;
    }

    private FairTypeDrawOptions getTextDrawOptions(String text) {
        return new FairType().append(this.size.getFontOptions().color(this.getTextColor()), text).applyParsers(this.getParsers()).getDrawOptions(FairType.TextAlign.LEFT, -1, false, true);
    }

    private TypeParser[] getParsers() {
        return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.ItemIcon(this.size.getFontOptions().getSize()), TypeParsers.MobIcon(this.size.getFontOptions().getSize()), TypeParsers.InputIcon(this.size.getFontOptions())};
    }

    public String getDisplayText() {
        return this.text.translate();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        boolean useDownTexture;
        Color drawCol = this.getDrawColor();
        ButtonState state = this.getButtonState();
        int textOffset = 0;
        boolean bl = useDownTexture = this.clickHandler.isDown() && this.isHovering();
        if (useDownTexture) {
            this.size.getButtonDownDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
            textOffset = this.size.buttonDownContentDrawOffset;
        } else {
            this.size.getButtonDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
        }
        HoverStateTextures endIcons = this.size.height < 20 ? this.getInterfaceStyle().button_select_small : this.getInterfaceStyle().button_select_big;
        GameTexture endIcon = state == ButtonState.HIGHLIGHTED ? endIcons.highlighted : endIcons.active;
        Rectangle contentRect = this.size.getContentRectangle(this.width);
        FormShader.FormShaderState textState = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(contentRect.x, contentRect.y, contentRect.width - endIcon.getWidth() - 2, contentRect.height));
        try {
            FairTypeDrawOptions textDrawOptions = this.getTextDrawOptions(this.getDisplayText());
            if (this.textAlign == -1) {
                textDrawOptions.draw(contentRect.x + 5, textOffset + this.size.fontDrawOffset, Color.BLACK);
            } else if (this.textAlign == 1) {
                textDrawOptions.draw(contentRect.x + this.width - 5 - textDrawOptions.getBoundingBox().width, textOffset + this.size.fontDrawOffset, Color.BLACK);
            } else if (this.alignLeftIfNotFit && textDrawOptions.getBoundingBox().width > contentRect.width) {
                textDrawOptions.draw(contentRect.x, textOffset + this.size.fontDrawOffset, Color.BLACK);
            } else {
                textDrawOptions.draw(this.width / 2 - textDrawOptions.getBoundingBox().width / 2, textOffset + this.size.fontDrawOffset, Color.BLACK);
            }
        }
        finally {
            textState.end();
        }
        FormShader.FormShaderState endIconState = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(contentRect.x, contentRect.y, contentRect.width, contentRect.height));
        try {
            int endIconHeight = endIcon.getHeight();
            endIcon.initDraw().color(drawCol).draw(contentRect.x + contentRect.width - endIcon.getWidth() - 2, contentRect.y + contentRect.height / 2 - endIconHeight / 2 + textOffset);
        }
        finally {
            endIconState.end();
        }
        if (useDownTexture) {
            this.size.getButtonDownEdgeDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
        } else {
            this.size.getButtonEdgeDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
        }
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        super.drawControllerFocus(current);
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
    }

    protected Color getDrawColor() {
        return this.size.getButtonColor(this.getInterfaceStyle(), this.getButtonState());
    }

    public ButtonState getButtonState() {
        if (!this.isActive()) {
            return ButtonState.INACTIVE;
        }
        if (this.isHovering()) {
            return ButtonState.HIGHLIGHTED;
        }
        return ButtonState.ACTIVE;
    }

    public Color getTextColor() {
        return this.size.getTextColor(this.getInterfaceStyle(), this.getButtonState());
    }

    public int getWantedWidth() {
        HoverStateTextures endIcons = this.size.height < 20 ? this.getInterfaceStyle().button_select_small : this.getInterfaceStyle().button_select_big;
        return this.wantedWidth + (this.width - this.size.getContentRectangle((int)this.width).width) + (endIcons.highlighted.getWidth() + 2) + 15;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormDropdownSelectionButton.singleBox(new Rectangle(this.getX(), this.getY() + this.size.textureDrawOffset, this.width, this.size.height));
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isHovering() {
        return this.isHovering || this.isControllerFocus();
    }

    public void drawDraggingElement(int mouseX, int mouseY) {
        FontManager.bit.drawString(mouseX, mouseY - 20, this.text.translate(), new FontOptions(16).outline().alphaf(0.8f));
    }

    public static class Option<T> {
        public final T value;
        public final GameMessage text;
        public final Supplier<Boolean> isActive;
        public final Color textColor;
        public final Supplier<GameTooltips> hoverTooltips;

        public Option(T value, GameMessage text, Supplier<Boolean> isActive, Color textColor, Supplier<GameTooltips> hoverTooltips) {
            this.value = value;
            this.text = text;
            this.isActive = isActive;
            this.textColor = textColor;
            this.hoverTooltips = hoverTooltips;
        }

        public Option(T value, GameMessage text) {
            this(value, text, null, null, null);
        }
    }

    public class OptionsList<T> {
        private final FormDropdownSelectionButton<T> button;
        private final LinkedList<OptionContainer> options = new LinkedList();

        private OptionsList(FormDropdownSelectionButton<T> button) {
            this.button = button;
        }

        public OptionsList<T> add(Option<T> option) {
            FormDropdownSelectionButton.this.wantedWidth = GameMath.ceil(GameMath.max(FormDropdownSelectionButton.this.wantedWidth, ((FormDropdownSelectionButton)FormDropdownSelectionButton.this).getTextDrawOptions((String)((FormDropdownSelectionButton)FormDropdownSelectionButton.this).text.translate()).getBoundingBox().width));
            this.options.add(new ValueOptionContainer<T>(this.button, option));
            return this;
        }

        public OptionsList<T> add(T value, GameMessage text) {
            return this.add(value, text, null);
        }

        public OptionsList<T> add(T value, GameMessage text, Supplier<GameMessage> tooltip) {
            return this.add(value, text, tooltip, null);
        }

        public OptionsList<T> add(T value, GameMessage text, Supplier<GameMessage> tooltip, Supplier<Boolean> isActive) {
            return this.add(new Option<T>(value, text, isActive, null, tooltip == null ? null : () -> {
                GameMessage tooltipMsg = (GameMessage)tooltip.get();
                if (tooltipMsg != null) {
                    return new StringTooltips(tooltipMsg.translate());
                }
                return null;
            }));
        }

        public OptionsList<T> addSub(GameMessage text) {
            OptionsList<T> subList = new OptionsList<T>(this.button);
            this.options.add(new SubMenuOptionContainer<T>(this.button, subList, text));
            return subList;
        }

        public void clear() {
            FormDropdownSelectionButton.this.wantedWidth = 0;
            this.options.clear();
        }

        public int size() {
            return this.options.size();
        }

        public boolean isEmpty() {
            return this.options.isEmpty();
        }

        private SelectionFloatMenu getMenu(int minWidth, boolean removingSubmenuRemovesParent) {
            SelectionFloatMenu menu = new SelectionFloatMenu(this.button, SelectionFloatMenu.Solid(new FontOptions(12)), minWidth);
            this.options.forEach(o -> o.addToMenu(menu, removingSubmenuRemovesParent));
            return menu;
        }
    }

    protected static class FormDropdownSelectionDraggingElement
    implements MouseDraggingElement {
        public final FormDropdownSelectionButton<?> component;
        public final Object sameObject;

        public FormDropdownSelectionDraggingElement(FormDropdownSelectionButton<?> component, Object sameObject) {
            this.component = component;
            this.sameObject = sameObject;
        }

        @Override
        public boolean draw(int mouseX, int mouseY) {
            this.component.drawDraggingElement(mouseX, mouseY);
            return true;
        }
    }

    private class SubMenuOptionContainer<T>
    extends OptionContainer {
        public final FormDropdownSelectionButton<T> button;
        public final OptionsList<T> subOptions;
        public final GameMessage text;

        public SubMenuOptionContainer(FormDropdownSelectionButton<T> button, OptionsList<T> subOptions, GameMessage text) {
            this.button = button;
            this.subOptions = subOptions;
            this.text = text;
        }

        @Override
        public void addToMenu(SelectionFloatMenu menu, boolean removingSubmenuRemovesParent) {
            menu.add(this.text.translate(), ((OptionsList)this.subOptions).getMenu(0, removingSubmenuRemovesParent), removingSubmenuRemovesParent);
        }
    }

    private static class ValueOptionContainer<T>
    extends OptionContainer {
        public final FormDropdownSelectionButton<T> button;
        public final Option<T> option;

        public ValueOptionContainer(FormDropdownSelectionButton<T> button, Option<T> option) {
            this.button = button;
            this.option = option;
        }

        @Override
        public void addToMenu(SelectionFloatMenu menu, boolean removingSubmenuRemovesParent) {
            menu.add(this.option.text.translate(), this.option.isActive, this.option.textColor, this.option.hoverTooltips, () -> ((FormDropdownSelectionButton)this.button).selectedOption(this.option));
        }
    }

    private static abstract class OptionContainer {
        private OptionContainer() {
        }

        public abstract void addToMenu(SelectionFloatMenu var1, boolean var2);
    }
}

