/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.floatMenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerGlyphTip;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackgroundTextures;
import necesse.gfx.Renderer;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class SelectionFloatMenu
extends FloatMenu {
    private final LinkedList<SelectionBoxList> boxes = new LinkedList();
    private int totalWidth;
    private int totalHeight;
    private SelectionFloatMenu subMenu = null;
    private final SelectionFloatMenuStyle style;
    private final int minWidth;
    public InputEvent createEvent;
    public InputEvent removeEvent;

    public static SelectionFloatMenuStyle Solid(final FontOptions fontOptions) {
        return new SelectionFloatMenuStyle(){

            @Override
            public int getPadding() {
                return 4;
            }

            @Override
            public int getListMargin() {
                return 2;
            }

            @Override
            public void drawListBackground(SelectionBoxList list, int drawX, int drawY) {
                Settings.UI.selectionBox.getDrawOptions(drawX, drawY, list.width, list.height + 2).draw();
            }

            @Override
            public void drawListBackgroundEdge(SelectionBoxList list, int drawX, int drawY) {
                Settings.UI.selectionBox.getEdgeDrawOptions(drawX, drawY, list.width, list.height + 2).draw();
            }

            @Override
            public void drawButtonBackground(int drawX, int drawY, Dimension dimensions, boolean isActive, boolean isMouseOver) {
                GameBackgroundTextures texture = Settings.UI.selectionBox;
                if (!isActive) {
                    texture = Settings.UI.selectionBox_inactive;
                } else if (isMouseOver) {
                    texture = Settings.UI.selectionBox_highlighted;
                }
                texture.getCenterDrawOptions(drawX - 2, drawY - 2, dimensions.width + 4, dimensions.height + 6).draw();
            }

            @Override
            public void drawButtonBackgroundEdge(int drawX, int drawY, Dimension dimensions, boolean isActive, boolean isMouseOver) {
                GameBackgroundTextures texture = Settings.UI.selectionBox;
                if (!isActive) {
                    texture = Settings.UI.selectionBox_inactive;
                } else if (isMouseOver) {
                    texture = Settings.UI.selectionBox_highlighted;
                }
                texture.getCenterEdgeDrawOptions(drawX - 2, drawY - 2, dimensions.width + 4, dimensions.height + 6).draw();
            }

            @Override
            public FontOptions getFontOptions() {
                return fontOptions;
            }

            @Override
            public Color getFontColor(boolean isActive, boolean isMouseOver) {
                if (!isActive) {
                    return Settings.UI.selectionBoxInactiveTextColor;
                }
                if (isMouseOver) {
                    return Settings.UI.selectionBoxHighlightedTextColor;
                }
                return Settings.UI.selectionBoxActiveTextColor;
            }
        };
    }

    public static SelectionFloatMenuStyle Transparent(final FontOptions fontOptions) {
        return new SelectionFloatMenuStyle(){

            @Override
            public int getPadding() {
                return 4;
            }

            @Override
            public int getListMargin() {
                return 0;
            }

            @Override
            public void drawListBackground(SelectionBoxList list, int drawX, int drawY) {
            }

            @Override
            public void drawListBackgroundEdge(SelectionBoxList list, int drawX, int drawY) {
            }

            @Override
            public void drawButtonBackground(int drawX, int drawY, Dimension dimensions, boolean isActive, boolean isMouseOver) {
                float b = isMouseOver ? 0.3f : 0.0f;
                Renderer.initQuadDraw(dimensions.width, dimensions.height).color(b, b, b, 0.8f).draw(drawX, drawY);
            }

            @Override
            public void drawButtonBackgroundEdge(int drawX, int drawY, Dimension dimensions, boolean isActive, boolean isMouseOver) {
            }

            @Override
            public FontOptions getFontOptions() {
                return fontOptions;
            }

            @Override
            public Color getFontColor(boolean isActive, boolean isMouseOver) {
                if (isActive) {
                    return Color.WHITE;
                }
                return Color.GRAY;
            }
        };
    }

    public SelectionFloatMenu(FormComponent parent, SelectionFloatMenuStyle style, int minWidth) {
        super(parent);
        this.style = style;
        this.minWidth = minWidth;
        this.boxes.add(new SelectionBoxList(style.getListMargin()));
    }

    public SelectionFloatMenu(FormComponent parent, SelectionFloatMenuStyle style) {
        this(parent, style, 0);
    }

    public SelectionFloatMenu(FormComponent parent) {
        this(parent, SelectionFloatMenu.Solid(new FontOptions(12)));
    }

    @Override
    public void init() {
        super.init();
        SelectionBoxList.SelectionBox first = (SelectionBoxList.SelectionBox)this.boxes.getFirst().buttons.getFirst();
        if (first != null) {
            this.parent.prioritizeControllerFocus(first);
        }
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        boolean isMouseClick = event.isMouseClickEvent();
        boolean remove = event.wasMouseClickEvent() && event.state;
        boolean bl = remove = this.menuInputEvent(event, isMouseClick) || remove;
        if (remove && this.createEvent != null && this.createEvent.state && !event.state) {
            int removeEventID;
            int createEventID = this.createEvent.isUsed() ? this.createEvent.getLastID() : this.createEvent.getID();
            int n = removeEventID = event.isUsed() ? event.getLastID() : event.getID();
            if (createEventID == removeEventID) {
                remove = false;
                this.createEvent = null;
            }
        }
        if (remove) {
            event.use();
            this.remove();
            this.removeEvent = event;
        }
    }

    private boolean menuInputEvent(InputEvent event, boolean isMouseClick) {
        boolean keep = false;
        if (this.subMenu != null) {
            if (this.subMenu.menuInputEvent(event, isMouseClick)) {
                this.subMenu.remove();
            } else if (isMouseClick) {
                keep = true;
            }
        }
        for (SelectionBoxList box : this.boxes) {
            if (!box.handleInputEvent(event)) continue;
            keep = true;
        }
        return isMouseClick && !keep;
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.subMenu != null) {
            this.subMenu.handleControllerEvent(event, tickManager, perspective);
        }
        for (SelectionBoxList box : this.boxes) {
            box.handleControllerEvent(event, tickManager, perspective);
        }
    }

    public SelectionFloatMenu setCreateEvent(InputEvent event) {
        if (event.state) {
            this.createEvent = event;
        }
        return this;
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        if (this.subMenu != null) {
            this.subMenu.addNextControllerFocus(list, currentXOffset, currentYOffset, customNavigationHandler, area, draw);
        }
        for (SelectionBoxList box : this.boxes) {
            for (SelectionBoxList.SelectionBox button : box.buttons) {
                Dimension dimensions = button.getDimensions();
                Rectangle boundingBox = new Rectangle(this.getDrawX() + box.xOffset, this.getDrawY() + button.yOffset, dimensions.width, dimensions.height);
                list.add(new ControllerFocus(button, boundingBox, 0, 0, 0, customNavigationHandler));
            }
        }
    }

    @Override
    public int getDrawX() {
        return Math.min(WindowManager.getWindow().getHudWidth() - this.totalWidth, super.getDrawX());
    }

    @Override
    public int getDrawY() {
        return Math.min(this.getHudHeight() - this.totalHeight, super.getDrawY());
    }

    public int getHudHeight() {
        return WindowManager.getWindow().getHudHeight() - (Input.lastInputIsController ? ControllerGlyphTip.getHeight() + 2 : 0);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective) {
        for (SelectionBoxList box : this.boxes) {
            box.draw(this.getDrawX(), this.getDrawY() - this.style.getListMargin());
        }
        if (this.subMenu != null) {
            this.subMenu.draw(tickManager, perspective);
        }
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        return this.boxes.stream().anyMatch(b -> new Rectangle(this.getDrawX(), this.getDrawY() + ((SelectionBoxList)b).xOffset, ((SelectionBoxList)b).width, ((SelectionBoxList)b).height).contains(event.pos.hudX, event.pos.hudY));
    }

    private void add(FairTypeDrawOptions textDrawOptions, FairTypeDrawOptions endingDrawOptions, Supplier<Boolean> isActive, Color textColor, Supplier<GameTooltips> hoverTooltips, BiConsumer<SelectionBoxList.SelectionBox, InputEvent> eventHandler, BiConsumer<SelectionBoxList.SelectionBox, ControllerEvent> controllerHandler) {
        SelectionBoxList last = this.boxes.getLast();
        Dimension dimensions = this.getButtonDimensions(textDrawOptions, endingDrawOptions);
        if (last.height + this.style.getListMargin() * 2 + dimensions.height > this.getHudHeight()) {
            this.boxes.addLast(new SelectionBoxList(last.xOffset + this.style.getListMargin() + last.width));
            this.boxes.getLast().add(textDrawOptions, endingDrawOptions, isActive, textColor, hoverTooltips, eventHandler, controllerHandler);
        } else {
            last.add(textDrawOptions, endingDrawOptions, isActive, textColor, hoverTooltips, eventHandler, controllerHandler);
        }
        this.totalHeight = Math.max(this.totalHeight, this.boxes.getLast().height);
        this.totalWidth = this.boxes.getLast().xOffset + this.boxes.getLast().width;
    }

    private TypeParser[] getParsers() {
        return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.ItemIcon(this.style.getFontOptions().getSize()), TypeParsers.MobIcon(this.style.getFontOptions().getSize()), TypeParsers.InputIcon(this.style.getFontOptions())};
    }

    public SelectionFloatMenu add(String str, Supplier<Boolean> isActive, Color strColor, Supplier<GameTooltips> hoverTooltips, Runnable onClicked) {
        FairType type = new FairType().append(this.style.getFontOptions(), str).applyParsers(this.getParsers());
        FairTypeDrawOptions drawOptions = type.getDrawOptions(FairType.TextAlign.LEFT, -1, false, true);
        BiConsumer<SelectionBoxList.SelectionBox, InputEvent> eventHandler = (b, e) -> {
            if (b.isActive != null && !b.isActive.get().booleanValue()) {
                b.isDown = false;
            }
            if (e.isMouseClickEvent()) {
                if (b.isInputOver((InputEvent)e)) {
                    if ((b.isActive == null || b.isActive.get().booleanValue()) && e.getID() == -100) {
                        if (e.state) {
                            b.isDown = true;
                        } else if (b.isDown) {
                            onClicked.run();
                            this.parent.playTickSound();
                            b.isDown = false;
                        }
                    }
                    e.use();
                } else {
                    b.isDown = false;
                }
            }
        };
        BiConsumer<SelectionBoxList.SelectionBox, ControllerEvent> controllerHandler = (b, e) -> {
            if (b.isActive != null && !b.isActive.get().booleanValue()) {
                b.isDown = false;
            }
            if (e.getState() == ControllerInput.MENU_SELECT) {
                if (b.isCurrentControllerFocus()) {
                    if (b.isActive == null || b.isActive.get().booleanValue()) {
                        if (e.buttonState) {
                            b.isDown = true;
                        } else if (b.isDown) {
                            onClicked.run();
                            this.parent.playTickSound();
                            b.isDown = false;
                        }
                    }
                    e.use();
                } else {
                    b.isDown = false;
                }
            }
        };
        this.add(drawOptions, null, isActive, strColor, hoverTooltips, eventHandler, controllerHandler);
        return this;
    }

    public SelectionFloatMenu add(String str, Runnable onClicked) {
        return this.add(str, null, null, null, onClicked);
    }

    public SelectionFloatMenu add(String str, Supplier<Boolean> isActive, Color strColor, Supplier<GameTooltips> hoverTooltips, SelectionFloatMenu subMenu, boolean removingSubmenuRemovesParent) {
        FairType type = new FairType().append(this.style.getFontOptions(), str).applyParsers(this.getParsers());
        FairTypeDrawOptions textDrawOptions = type.getDrawOptions(FairType.TextAlign.LEFT, -1, false, true);
        FairType ending = new FairType().append(this.style.getFontOptions(), ">");
        FairTypeDrawOptions endingDrawOptions = ending.getDrawOptions(FairType.TextAlign.RIGHT);
        BiConsumer<SelectionBoxList.SelectionBox, InputEvent> eventHandler = (b, e) -> {
            if (b.isActive != null && !b.isActive.get().booleanValue()) {
                b.isDown = false;
            }
            if (e.isMouseClickEvent()) {
                if (b.isInputOver((InputEvent)e)) {
                    if ((b.isActive == null || b.isActive.get().booleanValue()) && e.getID() == -100) {
                        if (e.state) {
                            b.isDown = true;
                        } else if (b.isDown) {
                            e.use();
                            if (this.subMenu == subMenu) {
                                this.subMenu.remove();
                            } else {
                                this.subMenu = subMenu;
                                Point drawPos = b.getDrawPos();
                                this.subMenu.init(drawPos.x + b.getDimensions().width, drawPos.y, () -> {
                                    if (this.subMenu == subMenu) {
                                        subMenu.dispose();
                                        this.subMenu = null;
                                        if (removingSubmenuRemovesParent) {
                                            this.remove();
                                        }
                                    }
                                });
                            }
                            this.parent.playTickSound();
                            b.isDown = false;
                        }
                    }
                    e.use();
                } else {
                    b.isDown = false;
                }
            }
        };
        BiConsumer<SelectionBoxList.SelectionBox, ControllerEvent> controllerHandler = (b, e) -> {
            if (b.isActive != null && !b.isActive.get().booleanValue()) {
                b.isDown = false;
            }
            if (e.getState() == ControllerInput.MENU_SELECT) {
                if (b.isCurrentControllerFocus()) {
                    if (b.isActive == null || b.isActive.get().booleanValue()) {
                        if (e.buttonState) {
                            b.isDown = true;
                        } else if (b.isDown) {
                            e.use();
                            if (this.subMenu == subMenu) {
                                this.subMenu.remove();
                            } else {
                                this.subMenu = subMenu;
                                Point drawPos = b.getDrawPos();
                                this.subMenu.init(drawPos.x + b.getDimensions().width, drawPos.y, () -> {
                                    if (this.subMenu == subMenu) {
                                        subMenu.dispose();
                                        this.subMenu = null;
                                        if (removingSubmenuRemovesParent) {
                                            this.remove();
                                        }
                                    }
                                });
                            }
                            this.parent.playTickSound();
                            b.isDown = false;
                        }
                    }
                    e.use();
                } else {
                    b.isDown = false;
                }
            }
        };
        this.add(textDrawOptions, endingDrawOptions, isActive, strColor, hoverTooltips, eventHandler, controllerHandler);
        return this;
    }

    public SelectionFloatMenu add(String str, SelectionFloatMenu subMenu, boolean removingSubmenuRemovesParent) {
        return this.add(str, null, null, null, subMenu, removingSubmenuRemovesParent);
    }

    public boolean isEmpty() {
        for (SelectionBoxList box : this.boxes) {
            if (box.buttons.isEmpty()) continue;
            return false;
        }
        return true;
    }

    private Dimension getButtonDimensions(FairTypeDrawOptions textDrawOptions, FairTypeDrawOptions endingDrawOptions) {
        Rectangle textBox = textDrawOptions.getBoundingBox();
        Dimension dimensions = new Dimension(textBox.width + this.style.getPadding() * 2, textBox.height + this.style.getPadding() * 2);
        if (endingDrawOptions != null) {
            Rectangle endBox = endingDrawOptions.getBoundingBox();
            dimensions.width += endBox.width + this.style.getPadding() * 2;
            dimensions.height = Math.max(dimensions.height, endBox.height + this.style.getPadding() * 2);
        }
        return dimensions;
    }

    public static abstract class SelectionFloatMenuStyle {
        public abstract int getPadding();

        public abstract int getListMargin();

        public abstract void drawListBackground(SelectionBoxList var1, int var2, int var3);

        public abstract void drawListBackgroundEdge(SelectionBoxList var1, int var2, int var3);

        public abstract void drawButtonBackground(int var1, int var2, Dimension var3, boolean var4, boolean var5);

        public abstract void drawButtonBackgroundEdge(int var1, int var2, Dimension var3, boolean var4, boolean var5);

        public abstract FontOptions getFontOptions();

        public abstract Color getFontColor(boolean var1, boolean var2);
    }

    private class SelectionBoxList {
        private final int xOffset;
        private final LinkedList<SelectionBox> buttons = new LinkedList();
        private int width = 0;
        private int height = 0;

        public SelectionBoxList(int xOffset) {
            this.xOffset = xOffset;
            this.width = SelectionFloatMenu.this.minWidth;
        }

        private SelectionBox add(FairTypeDrawOptions textDrawOptions, FairTypeDrawOptions endingDrawOptions, Supplier<Boolean> isActive, Color textColor, Supplier<GameTooltips> hoverTooltips, BiConsumer<SelectionBox, InputEvent> eventHandler, BiConsumer<SelectionBox, ControllerEvent> controllerHandler) {
            SelectionBox box = new SelectionBox(textDrawOptions, endingDrawOptions, isActive, textColor, hoverTooltips, eventHandler, controllerHandler, this.height);
            this.buttons.add(box);
            Dimension dimensions = box.getDimensions();
            this.width = Math.max(this.width, dimensions.width);
            this.height += dimensions.height;
            return box;
        }

        public boolean handleInputEvent(InputEvent event) {
            boolean out = false;
            for (SelectionBox selectionBox : this.buttons) {
                if (!selectionBox.handleInputEvent(event)) continue;
                out = true;
            }
            return out;
        }

        public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            for (SelectionBox selectionBox : this.buttons) {
                selectionBox.handleControllerEvent(event, tickManager, perspective);
            }
        }

        public void draw(int drawX, int drawY) {
            SelectionFloatMenu.this.style.drawListBackground(this, drawX + this.xOffset, drawY);
            for (SelectionBox selectionBox : this.buttons) {
                selectionBox.draw(drawX + this.xOffset, drawY);
            }
            SelectionFloatMenu.this.style.drawListBackgroundEdge(this, drawX + this.xOffset, drawY);
        }

        private class SelectionBox
        implements ControllerFocusHandler {
            public final FairTypeDrawOptions textDrawOptions;
            public final FairTypeDrawOptions endingDrawOptions;
            public final Supplier<Boolean> isActive;
            public final Color textColor;
            public final Supplier<GameTooltips> hoverTooltips;
            public final BiConsumer<SelectionBox, InputEvent> eventHandler;
            public final BiConsumer<SelectionBox, ControllerEvent> controllerHandler;
            public final int yOffset;
            public boolean isDown;
            protected boolean isHovering;

            public SelectionBox(FairTypeDrawOptions textDrawOptions, FairTypeDrawOptions endingDrawOptions, Supplier<Boolean> isActive, Color textColor, Supplier<GameTooltips> hoverTooltips, BiConsumer<SelectionBox, InputEvent> eventHandler, BiConsumer<SelectionBox, ControllerEvent> controllerHandler, int yOffset) {
                this.textDrawOptions = textDrawOptions;
                this.endingDrawOptions = endingDrawOptions;
                this.isActive = isActive;
                this.textColor = textColor;
                this.hoverTooltips = hoverTooltips;
                this.eventHandler = eventHandler;
                this.controllerHandler = controllerHandler;
                this.yOffset = yOffset;
            }

            protected Point getDrawPos() {
                return new Point(SelectionFloatMenu.this.getDrawX() + SelectionBoxList.this.xOffset, SelectionFloatMenu.this.getDrawY() + this.yOffset);
            }

            public boolean handleInputEvent(InputEvent event) {
                this.eventHandler.accept(this, event);
                boolean isMouseOver = this.isInputOver(event);
                return !event.isMouseClickEvent() || isMouseOver;
            }

            @Override
            public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
                this.controllerHandler.accept(this, event);
            }

            @Override
            public boolean handleControllerNavigate(int dir, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
                return false;
            }

            public boolean isCurrentControllerFocus() {
                return SelectionFloatMenu.this.parent.getManager().isControllerFocus(this);
            }

            protected boolean isInputOver(InputEvent event) {
                if (event.isUsed()) {
                    this.isHovering = false;
                    return false;
                }
                Point drawPos = this.getDrawPos();
                boolean contains = new Rectangle(this.getDimensions()).contains(event.pos.hudX - drawPos.x, event.pos.hudY - drawPos.y + SelectionFloatMenu.this.style.getListMargin());
                if (contains && event.isMouseMoveEvent()) {
                    event.use();
                }
                this.isHovering = contains;
                return contains;
            }

            protected Dimension getDimensions() {
                Dimension dimensions = SelectionFloatMenu.this.getButtonDimensions(this.textDrawOptions, this.endingDrawOptions);
                return new Dimension(Math.max(SelectionBoxList.this.width, dimensions.width), dimensions.height);
            }

            public void draw(int drawX, int drawY) {
                GameTooltips tooltips;
                boolean active = this.isActive == null || this.isActive.get() != false;
                boolean isMouseOver = this.isDown || this.isHovering || SelectionFloatMenu.this.parent.isControllerFocus(this);
                SelectionFloatMenu.this.style.drawButtonBackground(drawX, drawY += this.yOffset, this.getDimensions(), active, isMouseOver);
                Rectangle textBox = this.textDrawOptions.getBoundingBox();
                this.textDrawOptions.draw(drawX + SelectionFloatMenu.this.style.getPadding() - textBox.x, drawY + SelectionFloatMenu.this.style.getPadding() - textBox.y, this.textColor != null ? this.textColor : SelectionFloatMenu.this.style.getFontColor(active, isMouseOver));
                if (this.endingDrawOptions != null) {
                    Rectangle endBox = this.endingDrawOptions.getBoundingBox();
                    this.endingDrawOptions.draw(drawX + SelectionBoxList.this.width - endBox.width - endBox.x - SelectionFloatMenu.this.style.getPadding(), drawY + SelectionFloatMenu.this.style.getPadding() - endBox.y, this.textColor != null ? this.textColor : SelectionFloatMenu.this.style.getFontColor(active, isMouseOver));
                }
                SelectionFloatMenu.this.style.drawButtonBackgroundEdge(drawX, drawY, this.getDimensions(), active, isMouseOver);
                if (isMouseOver && this.hoverTooltips != null && (tooltips = this.hoverTooltips.get()) != null) {
                    GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
                }
            }

            @Override
            public void drawControllerFocus(ControllerFocus current) {
                ControllerFocusHandler.super.drawControllerFocus(current);
                GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
            }
        }
    }
}

