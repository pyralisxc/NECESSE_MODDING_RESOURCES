/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.util.GameUtils;
import necesse.entity.manager.EntityComponent;

public class EntityComponentManager<T> {
    private final HashMap<Class<? extends EntityComponent>, HashMap<T, Object>> list = new HashMap();

    protected static boolean checkClassOrSuperImplementEntityComponent(Class<?> clazz, Predicate<Class<? extends EntityComponent>> onFound) {
        boolean success = false;
        while (clazz != null) {
            for (Class<?> anInterface : clazz.getInterfaces()) {
                if (EntityComponent.class.isAssignableFrom(anInterface)) {
                    success = onFound.test(anInterface) || success;
                }
                EntityComponentManager.checkClassOrSuperImplementEntityComponent(anInterface, onFound);
            }
            clazz = clazz.getSuperclass();
        }
        return success;
    }

    public void add(T key, Object object) {
        EntityComponentManager.checkClassOrSuperImplementEntityComponent(object.getClass(), entityInterface -> {
            this.add((Class<? extends EntityComponent>)entityInterface, key, object);
            return true;
        });
    }

    private void add(Class<? extends EntityComponent> entityComponentClass, T key, Object object) {
        this.list.compute(entityComponentClass, (listKey, map) -> {
            if (map == null) {
                map = new HashMap<Object, Object>();
            }
            map.put(key, object);
            return map;
        });
    }

    public boolean remove(T key, Object object) {
        return EntityComponentManager.checkClassOrSuperImplementEntityComponent(object.getClass(), entityInterface -> this.remove((Class<? extends EntityComponent>)entityInterface, key));
    }

    private boolean remove(Class<? extends EntityComponent> entityComponentClass, T key) {
        AtomicBoolean success = new AtomicBoolean();
        this.list.compute(entityComponentClass, (listKey, map) -> {
            if (map == null) {
                map = new HashMap();
            }
            if (map.remove(key) != null) {
                success.set(true);
            }
            return map;
        });
        return success.get();
    }

    public <C extends Class<? extends EntityComponent>> int getCount(C component) {
        HashMap<T, Object> map = this.list.get(component);
        return map == null ? 0 : map.size();
    }

    public <R extends EntityComponent, C extends Class<? extends R>> Iterable<R> getAll(C component) {
        HashMap<T, Object> map = this.list.get(component);
        if (map != null) {
            return GameUtils.mapIterable(map.values().iterator(), o -> (EntityComponent)o);
        }
        return Collections.emptyList();
    }

    public <R extends EntityComponent, C extends Class<? extends R>> Stream<R> streamAll(C component) {
        HashMap<T, Object> map = this.list.get(component);
        if (map != null) {
            return map.values().stream().map(o -> (EntityComponent)o);
        }
        return Stream.empty();
    }
}

