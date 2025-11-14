/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.util.Objects;
import java.util.function.Predicate;
import necesse.engine.MouseDraggingElement;
import necesse.engine.input.Input;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.forms.components.FormContentButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;

public class FormContentIconValueButton<T>
extends FormContentButton {
    protected T value;
    protected ButtonIcon icon;
    protected boolean mirrorX;
    protected boolean mirrorY;
    protected GameMessage[] tooltips;

    public FormContentIconValueButton(int x, int y, int width, FormInputSize size, ButtonColor color) {
        super(x, y, width, size, color);
    }

    public FormContentIconValueButton(int x, int y, FormInputSize size, ButtonColor color) {
        this(x, y, size.height, size, color);
    }

    protected int getIconDrawX(ButtonIcon icon, int x, int width) {
        return x + width / 2 - icon.texture.getWidth() / 2;
    }

    protected int getIconDrawY(ButtonIcon icon, int y, int height) {
        return y + height / 2 - icon.texture.getHeight() / 2;
    }

    public Color getContentColor() {
        return (Color)this.icon.colorGetter.apply(this.getButtonState());
    }

    @Override
    protected void drawContent(int x, int y, int width, int height) {
        if (this.icon == null) {
            return;
        }
        this.icon.texture.initDraw().color(this.getContentColor()).mirror(this.mirrorX, this.mirrorY).draw(this.getIconDrawX(this.icon, x, width), this.getIconDrawY(this.icon, y, height));
    }

    @Override
    protected void addTooltips(PlayerMob perspective) {
        super.addTooltips(perspective);
        GameTooltips tooltips = this.getTooltips();
        if (tooltips != null) {
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
        }
    }

    public GameTooltips getTooltips() {
        if (this.tooltips.length != 0) {
            StringTooltips out = new StringTooltips();
            for (GameMessage tooltip : this.tooltips) {
                out.add(tooltip.translate(), 350);
            }
            return out;
        }
        return null;
    }

    public T getValue() {
        return this.value;
    }

    public FormContentIconValueButton<T> setCurrent(T value, ButtonIcon icon, boolean mirrorX, boolean mirrorY, GameMessage ... tooltips) {
        this.value = value;
        this.icon = icon;
        this.mirrorX = mirrorX;
        this.mirrorY = mirrorY;
        this.tooltips = tooltips;
        return this;
    }

    public FormContentIconValueButton<T> setCurrent(T value, ButtonIcon icon, GameMessage ... tooltips) {
        return this.setCurrent(value, icon, false, false, tooltips);
    }

    public FormContentIconValueButton<T> setCurrent(T value, ButtonIcon icon) {
        return this.setCurrent(value, icon, false, false, new GameMessage[0]);
    }

    @Override
    protected boolean acceptsEvents() {
        return super.acceptsEvents() && !this.isDraggingThis();
    }

    public void setupDragToOtherButtons(Object sameButtonsObject, boolean submitInitialClick, Predicate<T> onSelected) {
        this.onDragStarted(e -> {
            final int id = e.draggingStartedEvent.isUsed() ? e.draggingStartedEvent.getLastID() : e.draggingStartedEvent.getID();
            Renderer.setMouseDraggingElement(new FormContentIconStateDraggingElement(this, sameButtonsObject){

                @Override
                public boolean isKeyDown(Input input) {
                    return input.isKeyDown(id);
                }
            });
            if (submitInitialClick) {
                FormInputEvent<FormContentIconValueButton> fEvent = new FormInputEvent<FormContentIconValueButton>(this, e.draggingStartedEvent);
                this.clickedEvents.onEvent(fEvent);
                if (!fEvent.hasPreventedDefault()) {
                    this.pressed(e.event);
                    this.startCooldown();
                    e.event.use();
                }
            }
        });
        this.onChangedHover(e -> {
            MouseDraggingElement draggingElement;
            if (this.isHovering() && this.isActive() && (draggingElement = Renderer.getMouseDraggingElement()) instanceof FormContentIconStateDraggingElement) {
                FormContentIconStateDraggingElement thisElement = (FormContentIconStateDraggingElement)draggingElement;
                if (thisElement.component != this && !Objects.equals(this.value, thisElement.component.value) && Objects.equals(thisElement.sameObject, sameButtonsObject)) {
                    try {
                        if (onSelected.test(thisElement.component.value)) {
                            this.setCurrent(thisElement.component.value, thisElement.component.icon, thisElement.component.mirrorX, thisElement.component.mirrorY, thisElement.component.tooltips);
                            this.playTickSound();
                            e.event.use();
                        }
                    }
                    catch (ClassCastException classCastException) {
                        // empty catch block
                    }
                }
            }
        });
    }

    protected boolean isDraggingThis() {
        MouseDraggingElement draggingElement = Renderer.getMouseDraggingElement();
        if (draggingElement instanceof FormContentIconStateDraggingElement) {
            FormContentIconStateDraggingElement thisElement = (FormContentIconStateDraggingElement)draggingElement;
            return thisElement.component == this;
        }
        return false;
    }

    @Override
    public void drawDraggingElement(int mouseX, int mouseY) {
    }

    protected static class FormContentIconStateDraggingElement
    implements MouseDraggingElement {
        public final FormContentIconValueButton<?> component;
        public final Object sameObject;

        public FormContentIconStateDraggingElement(FormContentIconValueButton<?> component, Object sameObject) {
            this.component = component;
            this.sameObject = sameObject;
        }

        @Override
        public boolean draw(int mouseX, int mouseY) {
            this.component.drawDraggingElement(mouseX, mouseY);
            return true;
        }
    }
}

