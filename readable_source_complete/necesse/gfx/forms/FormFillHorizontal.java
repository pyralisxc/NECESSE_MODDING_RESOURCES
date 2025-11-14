/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.shader.FormShader;

public class FormFillHorizontal
extends FormComponent
implements FormPositionContainer {
    public int minWidth;
    public int maxWidth;
    public int spacing;
    public int padding;
    public Alignment alignment;
    protected boolean updateNextDraw = true;
    protected FormComponentList componentList = new FormComponentList();
    protected LinkedList<ComponentWidthController> componentControllers = new LinkedList();
    protected FormPosition position;
    protected int xOffset;

    public FormFillHorizontal(int x, int y, Alignment alignment) {
        this(x, y, alignment, 0, -1, 4, 0);
    }

    public FormFillHorizontal(int x, int y, Alignment alignment, int minWidth, int maxWidth) {
        this(x, y, alignment, minWidth, maxWidth, 4, 0);
    }

    public FormFillHorizontal(int x, int y, Alignment alignment, int minWidth, int maxWidth, int spacing, int padding) {
        this.position = new FormFixedPosition(x, y);
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.spacing = spacing;
        this.padding = padding;
        this.alignment = alignment;
    }

    @Override
    protected void init() {
        super.init();
        this.componentList.setManager(this.getManager());
        this.componentList.inheritStyle(this);
        Localization.addListener(new LocalizationChangeListener(){

            @Override
            public void onChange(Language language) {
                FormFillHorizontal.this.updateNextDraw = true;
            }

            @Override
            public boolean isDisposed() {
                return FormFillHorizontal.this.isDisposed();
            }
        });
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        event = InputEvent.ReplacePosEvent(event, InputPosition.fromHudPos(WindowManager.getWindow().getInput(), event.pos.hudX - this.getX() - this.xOffset, event.pos.hudY - this.getY()));
        this.componentList.handleInputEvent(event, tickManager, perspective);
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        this.componentList.addNextControllerFocus(list, currentXOffset + this.getX() + this.xOffset, currentYOffset + this.getY(), customNavigationHandler, area, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.updateNextDraw) {
            this.updateComponents();
        }
        FormShader.FormShaderState shaderState = GameResources.formShader.startState(new Point(this.getX() + this.xOffset, this.getY()), null);
        try {
            Rectangle offsetRenderBox = null;
            if (renderBox != null) {
                offsetRenderBox = new Rectangle(renderBox);
                offsetRenderBox.translate(-this.getX() - this.xOffset, -this.getY());
            }
            this.componentList.draw(tickManager, perspective, offsetRenderBox);
        }
        finally {
            shaderState.end();
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        if (this.updateNextDraw) {
            this.updateComponents();
        }
        List<Rectangle> hitboxes = this.componentList.getHitboxes();
        hitboxes.forEach(rectangle -> rectangle.translate(this.getX() + this.xOffset, this.getY()));
        return hitboxes;
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        if (this.updateNextDraw) {
            this.updateComponents();
        }
        return this.componentList.isMouseOver(event);
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        this.componentList.handleControllerEvent(event, tickManager, perspective);
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    public <T extends FormDropdownSelectionButton<?>> T addComponent(final T dropdown) {
        ComponentWidthController controller = new ComponentWidthController(dropdown){

            @Override
            public int getWantedWidth() {
                return dropdown.getWantedWidth();
            }

            @Override
            public int getWidth() {
                return dropdown.getWidth();
            }

            @Override
            public void setWidth(int width) {
                dropdown.setWidth(width);
            }
        };
        this.componentList.addComponent(dropdown);
        this.componentControllers.add(controller);
        return dropdown;
    }

    public <T extends FormTextButton> T addComponent(final T button) {
        ComponentWidthController controller = new ComponentWidthController(button){

            @Override
            public int getWantedWidth() {
                return button.getWantedWidth();
            }

            @Override
            public int getWidth() {
                return button.getWidth();
            }

            @Override
            public void setWidth(int width) {
                button.setWidth(width);
            }
        };
        this.componentList.addComponent(button);
        this.componentControllers.add(controller);
        return button;
    }

    public void updateComponents() {
        int thisComponentSize;
        int i;
        int actualMaxWidth;
        if (this.componentControllers.isEmpty()) {
            return;
        }
        int wantedTotalWidth = this.componentControllers.stream().reduce(0, (sum, tab) -> sum + tab.getWantedWidth() + this.spacing + this.padding, Integer::sum);
        ComponentWidthController[] sortedComponents = (ComponentWidthController[])this.componentControllers.stream().sorted(Comparator.comparing(ComponentWidthController::getWantedWidth, Comparator.reverseOrder())).toArray(ComponentWidthController[]::new);
        int n = actualMaxWidth = this.maxWidth <= 0 ? (wantedTotalWidth -= this.spacing) : this.maxWidth;
        if (wantedTotalWidth < this.minWidth && this.minWidth <= this.maxWidth) {
            int componentSize = (this.minWidth - this.spacing * (sortedComponents.length - 1)) / sortedComponents.length;
            actualMaxWidth = 0;
            for (i = 0; i < sortedComponents.length; ++i) {
                thisComponentSize = componentSize;
                int newTotalComponentWidth = componentSize * sortedComponents.length;
                int missingSpace = actualMaxWidth - newTotalComponentWidth;
                if (i < missingSpace) {
                    ++thisComponentSize;
                }
                ComponentWidthController component = sortedComponents[i];
                component.setWidth(thisComponentSize);
            }
            actualMaxWidth = wantedTotalWidth = this.minWidth;
        } else {
            ComponentWidthController[] componentSize = sortedComponents;
            i = componentSize.length;
            for (thisComponentSize = 0; thisComponentSize < i; ++thisComponentSize) {
                ComponentWidthController component = componentSize[thisComponentSize];
                component.setWidth(component.getWantedWidth() + this.padding);
            }
        }
        if (wantedTotalWidth > actualMaxWidth) {
            int savingsNeeded = wantedTotalWidth - actualMaxWidth;
            block7: for (i = 0; i < sortedComponents.length && savingsNeeded > 0; ++i) {
                int currentWidth = sortedComponents[i].getWidth();
                int nextWidth = i < sortedComponents.length - 1 ? sortedComponents[i + 1].getWidth() : 1;
                int possibleSavings = (currentWidth - nextWidth) * (i + 1);
                int neededToSavePerComponent = GameMath.ceil((float)Math.min(savingsNeeded, possibleSavings) / ((float)i + 1.0f));
                int newWidth = currentWidth - neededToSavePerComponent;
                int missingSpace = neededToSavePerComponent * (i + 1) - savingsNeeded;
                for (int j = 0; j <= i; ++j) {
                    int extra = j < missingSpace ? 1 : 0;
                    sortedComponents[j].setWidth(newWidth + extra);
                    if ((savingsNeeded -= neededToSavePerComponent - extra) <= 0) continue block7;
                }
            }
        }
        int totalComponentWidth = GameMath.min(actualMaxWidth, wantedTotalWidth);
        switch (this.alignment) {
            case Left: {
                this.xOffset = 0;
                break;
            }
            case Center: {
                this.xOffset = -totalComponentWidth / 2;
                break;
            }
            case Right: {
                this.xOffset = -totalComponentWidth;
            }
        }
        int nextComponentX = 0;
        for (ComponentWidthController component : this.componentControllers) {
            component.setX(nextComponentX);
            nextComponentX += component.getWidth() + this.spacing;
        }
        this.updateNextDraw = false;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.componentList.onWindowResized(window);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.componentList.dispose();
    }

    public static enum Alignment {
        Left,
        Center,
        Right;

    }

    public static abstract class ComponentWidthController {
        public final FormPositionContainer component;

        public ComponentWidthController(FormPositionContainer component) {
            this.component = component;
        }

        public abstract int getWantedWidth();

        public abstract int getWidth();

        public abstract void setWidth(int var1);

        public void setX(int x) {
            this.component.setX(x);
        }
    }
}

