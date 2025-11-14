/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms;

import java.util.Collection;
import java.util.function.Predicate;
import necesse.gfx.forms.ComponentList;
import necesse.gfx.forms.components.FormComponent;

public interface ComponentListContainer<T extends FormComponent> {
    public ComponentList<T> getComponentList();

    default public void clearComponents() {
        this.getComponentList().clearComponents();
    }

    default public boolean removeComponentsIf(Predicate<T> filter) {
        return this.getComponentList().removeComponentsIf(filter);
    }

    default public void removeComponent(T comp) {
        this.getComponentList().removeComponent(comp);
    }

    default public boolean hasComponent(T component) {
        return this.getComponentList().hasComponent(component);
    }

    default public Collection<T> getComponents() {
        return this.getComponentList().getComponents();
    }

    default public <M extends T> M addComponent(M component) {
        return this.getComponentList().addComponent(component);
    }

    default public <M extends T> M addComponent(M component, int zIndex) {
        return this.getComponentList().addComponent(component, zIndex);
    }
}

