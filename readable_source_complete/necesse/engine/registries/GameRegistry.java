/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.RegistryClosedException;

public abstract class GameRegistry<T extends IDDataContainer> {
    public static final Pattern stringIDPattern = Pattern.compile("[a-zA-Z0-9_\\-]+");
    protected final String objectCallName;
    protected final int maxSize;
    protected final boolean stringIDUnique;
    protected Pattern thisStringIDPattern = stringIDPattern;
    private final List<T> list;
    private final HashMap<String, Integer> stringIDToIDMap;
    private boolean registrationOpen;

    public static boolean validStringID(String stringID) {
        return stringIDPattern.matcher(stringID).matches();
    }

    protected GameRegistry(String objectCallName, int maxSize) {
        this(objectCallName, maxSize, true);
    }

    protected GameRegistry(String objectCallName, int maxSize, boolean stringIDUnique) {
        this.objectCallName = objectCallName;
        this.maxSize = maxSize;
        this.stringIDUnique = stringIDUnique;
        this.list = new ArrayList<T>();
        this.stringIDToIDMap = new HashMap();
        this.registrationOpen = true;
    }

    public abstract void registerCore();

    protected final int register(String stringID, T object) {
        return this.registerObj(stringID, object).getIDData().getID();
    }

    protected final <R extends T> R registerObj(String stringID, R object) {
        if (!this.registrationOpen) {
            throw new RegistryClosedException(this.objectCallName + " registration is closed");
        }
        if (this.list.size() > this.maxSize) {
            throw new IllegalStateException("Could not register " + this.objectCallName + ", max count reached");
        }
        if (this.thisStringIDPattern != null && !this.thisStringIDPattern.matcher(stringID).matches()) {
            throw new IllegalArgumentException("Tried to register " + this.objectCallName + " with invalid stringID: \"" + stringID + "\"");
        }
        if (this.stringIDUnique && this.getElementID(stringID) != -1) {
            throw new IllegalStateException("Tried to register duplicate " + this.objectCallName + " with stringID \"" + stringID + "\"");
        }
        int id = this.list.size();
        this.list.add(object);
        this.stringIDToIDMap.put(stringID, id);
        object.getIDData().setData(id, stringID);
        this.onRegister(object, id, stringID, false);
        return object;
    }

    protected final int replace(String stringID, T object) {
        return this.replaceObj(stringID, object).getIDData().getID();
    }

    protected final <R extends T> R replaceObj(String stringID, R object) {
        if (!this.registrationOpen) {
            throw new RegistryClosedException(this.objectCallName + " registration is closed");
        }
        int elementID = this.getElementID(stringID);
        if (elementID == -1) {
            return this.registerObj(stringID, object);
        }
        this.list.set(elementID, object);
        object.getIDData().setData(elementID, stringID);
        this.onRegister(object, elementID, stringID, true);
        return object;
    }

    protected abstract void onRegister(T var1, int var2, String var3, boolean var4);

    public final void closeRegistry() {
        this.registrationOpen = false;
        this.onRegistryClose();
    }

    public final boolean isOpen() {
        return this.registrationOpen;
    }

    public final boolean isClosed() {
        return !this.isOpen();
    }

    protected abstract void onRegistryClose();

    protected Iterable<T> getElements() {
        return this.list;
    }

    protected Stream<T> streamElements() {
        return this.list.stream();
    }

    protected int getTotalElements() {
        return this.list.size();
    }

    protected T getElement(int id) {
        try {
            return this.getElementRaw(id);
        }
        catch (NoSuchElementException e) {
            System.err.println("Could not find " + this.objectCallName + " id " + id + " in memory (out og bounds)");
            return null;
        }
    }

    protected T getElementRaw(int id) {
        if (id < 0 || id >= this.list.size()) {
            throw new NoSuchElementException();
        }
        return (T)((IDDataContainer)this.list.get(id));
    }

    protected T getElement(String stringID) {
        int id = this.getElementID(stringID);
        return id == -1 ? null : (T)this.getElement(id);
    }

    protected int getElementID(String stringID) {
        try {
            return this.getElementIDRaw(stringID);
        }
        catch (NoSuchElementException e) {
            return -1;
        }
    }

    protected int getElementIDRaw(String stringID) throws NoSuchElementException {
        Integer id = this.stringIDToIDMap.get(stringID);
        if (id == null) {
            throw new NoSuchElementException();
        }
        return id;
    }

    protected String getElementStringID(int id) {
        return this.getElement(id).getIDData().getStringID();
    }

    protected int size() {
        return this.list.size();
    }
}

