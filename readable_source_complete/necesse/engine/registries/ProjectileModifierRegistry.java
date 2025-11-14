/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;
import necesse.entity.projectile.modifiers.ProjectileModifier;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;

public class ProjectileModifierRegistry
extends ClassedGameRegistry<ProjectileModifier, ProjectileModifierRegistryElement> {
    public static final ProjectileModifierRegistry instance = new ProjectileModifierRegistry();

    private ProjectileModifierRegistry() {
        super("ProjectileModifier", 32762);
    }

    @Override
    public void registerCore() {
        ProjectileModifierRegistry.registerModifier("resilienceonhit", ResilienceOnHitProjectileModifier.class);
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerModifier(String stringID, Class<? extends ProjectileModifier> modifierClass) {
        try {
            return instance.register(stringID, new ProjectileModifierRegistryElement(modifierClass));
        }
        catch (NoSuchMethodException e) {
            System.err.println("Could not register ProjectileModifier " + modifierClass.getSimpleName() + ": Missing constructor with no parameters");
            return -1;
        }
    }

    public static ProjectileModifier getModifier(int id) {
        try {
            return (ProjectileModifier)((ProjectileModifierRegistryElement)instance.getElement(id)).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ProjectileModifier getModifier(String stringID) {
        return ProjectileModifierRegistry.getModifier(ProjectileModifierRegistry.getModifierID(stringID));
    }

    public static int getModifierID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static int getModifierID(Class<? extends ProjectileModifier> clazz) {
        return instance.getElementID(clazz);
    }

    public static String getModifierStringID(int id) {
        return instance.getElementStringID(id);
    }

    protected static class ProjectileModifierRegistryElement
    extends ClassIDDataContainer<ProjectileModifier> {
        public ProjectileModifierRegistryElement(Class<? extends ProjectileModifier> modifierClass) throws NoSuchMethodException {
            super(modifierClass, new Class[0]);
        }
    }
}

