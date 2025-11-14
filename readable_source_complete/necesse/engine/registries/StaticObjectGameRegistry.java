/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.NoSuchElementException;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.IDDataContainer;

public abstract class StaticObjectGameRegistry<T extends IDDataContainer>
extends GameRegistry<T> {
    public StaticObjectGameRegistry(String objectCallName, int maxSize) {
        super(objectCallName, maxSize);
    }

    public StaticObjectGameRegistry(String objectCallName, int maxSize, boolean stringIDUnique) {
        super(objectCallName, maxSize, stringIDUnique);
    }

    public int registerObject(String stringID, T obj) {
        return this.register(stringID, obj);
    }

    public T getObject(int id) {
        try {
            return this.getElement(id);
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    public T getObject(String stringID) {
        int elementID = this.getElementID(stringID);
        if (elementID == -1) {
            return null;
        }
        return this.getObject(elementID);
    }

    public int getObjectID(String stringID) {
        return this.getElementID(stringID);
    }
}

