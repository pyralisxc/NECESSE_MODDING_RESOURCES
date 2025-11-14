/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ComponentPriorityManager;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;

public abstract class ComponentList<T extends FormComponent>
implements Iterable<T>,
ComponentPriorityManager {
    private final FormComponent parentComponent;
    private final ArrayList<T> components;
    private final boolean appendPriority;
    private long priorityCounter = 0L;

    public ComponentList(FormComponent parentComponent, boolean appendPriority) {
        this.parentComponent = parentComponent;
        this.components = new ArrayList();
        this.appendPriority = appendPriority;
    }

    public ComponentList(FormComponent parentComponent) {
        this(parentComponent, true);
    }

    public void submitInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isUsed()) {
            return;
        }
        event = this.offsetEvent(event, false);
        this.components.sort(null);
        FormComponent[] comps = this.components.toArray(new FormComponent[0]);
        for (int i = comps.length - 1; i >= 0; --i) {
            FormComponent comp = comps[i];
            if (comp != null && comp.shouldDraw()) {
                boolean isMouseClick = event.isMouseClickEvent();
                comp.handleInputEvent(event, tickManager, perspective);
                if (this.appendPriority && isMouseClick && comp.canBePutOnTopByClick && comp.isMouseOver(event)) {
                    comp.tryPutOnTop();
                    if (comp.shouldUseMouseEvents()) break;
                }
                if (event.isMouseMoveEvent() && comp.shouldUseMouseEvents() && comp.isMouseOver(event)) {
                    event.useMove();
                }
            }
            if (event.isUsed()) break;
        }
    }

    public void submitControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isUsed()) {
            return;
        }
        this.components.sort(null);
        FormComponent[] comps = this.components.toArray(new FormComponent[0]);
        for (int i = comps.length - 1; i >= 0; --i) {
            FormComponent comp = comps[i];
            if (comp != null && comp.shouldDraw()) {
                FormManager manager;
                boolean isSelect = event.getState() == ControllerInput.MENU_SELECT;
                comp.handleControllerEvent(event, tickManager, perspective);
                if (this.appendPriority && isSelect && (manager = this.getManager()) != null && comp.canBePutOnTopByClick && manager.isControllerFocus(comp)) {
                    comp.tryPutOnTop();
                }
            }
            if (event.isUsed()) break;
        }
    }

    public void addNextControllerComponents(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        this.components.sort(null);
        for (FormComponent c : this.components) {
            if (!c.shouldDraw()) continue;
            c.addNextControllerFocus(list, currentXOffset, currentYOffset, customNavigationHandler, area, draw);
        }
    }

    public abstract InputEvent offsetEvent(InputEvent var1, boolean var2);

    public abstract FormManager getManager();

    public void init() {
        this.components.forEach(c -> {
            c.setManager(this.getManager());
            c.inheritStyle(this.parentComponent);
        });
    }

    public void removeComponent(T comp) {
        this.removeComponent(comp, true);
    }

    private void removeComponent(T comp, boolean submitChange) {
        if (this.components.remove(comp)) {
            ((FormComponent)comp).dispose();
            if (submitChange) {
                this.onChange();
            }
        }
    }

    public void onChange() {
    }

    public boolean hasComponent(T component) {
        return this.components.contains(component);
    }

    public void clearComponents() {
        this.removeComponentsIf(c -> true);
    }

    public boolean removeComponentsIf(Predicate<T> filter) {
        boolean changed = false;
        for (int i = 0; i < this.components.size(); ++i) {
            FormComponent comp = (FormComponent)this.components.get(i);
            if (!filter.test(comp)) continue;
            this.removeComponent(comp, false);
            changed = true;
            --i;
        }
        if (changed) {
            this.onChange();
        }
        return changed;
    }

    public Collection<T> getComponents() {
        return this.components;
    }

    public <M extends T> M addComponent(M component) {
        return this.addComponent(component, 0);
    }

    public <M extends T> M addComponent(M component, int zIndex) {
        if (this.hasComponent(component)) {
            throw new IllegalArgumentException("Cannot add the same component twice");
        }
        ((FormComponent)component).zIndex = zIndex;
        this.components.add(component);
        ((FormComponent)component).setPriorityManager(this);
        ((FormComponent)component).setManager(this.getManager());
        ((FormComponent)component).inheritStyle(this.parentComponent);
        this.onChange();
        return component;
    }

    public void sort() {
        this.components.sort(null);
    }

    public void drawComponents(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        Performance.record((PerformanceTimerManager)tickManager, "sort", this::sort);
        Stream<FormComponent> stream = this.components.stream().filter(FormComponent::shouldDraw);
        if (renderBox != null) {
            stream = stream.filter(c -> c.shouldSkipRenderBoxCheck() || renderBox.intersects(c.getBoundingBox()));
        }
        stream.forEach(c -> c.draw(tickManager, perspective, renderBox));
    }

    public void onWindowResized(GameWindow window) {
        this.components.forEach(t -> t.onWindowResized(window));
    }

    public void disposeComponents() {
        while (!this.components.isEmpty()) {
            this.removeComponent((FormComponent)this.components.get(0), false);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return this.components.iterator();
    }

    public T[] toArray(T[] ar) {
        return (FormComponent[])this.components.toArray(ar);
    }

    public Stream<T> stream() {
        return this.components.stream();
    }

    public List<Rectangle> getHitboxes() {
        return this.stream().filter(FormComponent::shouldDraw).flatMap(c -> c.getHitboxes().stream()).collect(Collectors.toList());
    }

    public boolean isMouseOver(InputEvent event) {
        if (event.isMoveUsed()) {
            return false;
        }
        event = this.offsetEvent(event, false);
        for (int i = this.components.size() - 1; i >= 0; --i) {
            FormComponent comp = (FormComponent)this.components.get(i);
            if (!comp.shouldDraw() || !comp.isMouseOver(event)) continue;
            return true;
        }
        return false;
    }

    public int size() {
        return this.components.size();
    }

    public boolean isEmpty() {
        return this.components.isEmpty();
    }

    @Override
    public long getNextPriorityKey() {
        return ++this.priorityCounter;
    }
}

