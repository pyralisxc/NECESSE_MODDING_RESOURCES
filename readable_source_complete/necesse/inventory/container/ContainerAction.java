/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container;

public enum ContainerAction {
    LEFT_CLICK,
    RIGHT_CLICK,
    QUICK_MOVE,
    QUICK_TRASH,
    QUICK_TRASH_ONE,
    QUICK_DROP,
    QUICK_DROP_ONE,
    TOGGLE_LOCKED,
    TAKE_ONE,
    QUICK_MOVE_ONE,
    QUICK_GET_ONE,
    RIGHT_CLICK_ACTION;


    public int getID() {
        return this.ordinal();
    }

    public static ContainerAction getContainerAction(int ID) {
        ContainerAction[] actions = ContainerAction.values();
        if (ID < 0 || ID >= actions.length) {
            return null;
        }
        return actions[ID];
    }
}

