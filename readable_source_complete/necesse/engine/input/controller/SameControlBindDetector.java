/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input.controller;

import java.util.HashMap;
import java.util.LinkedList;
import necesse.engine.input.controller.ControllerActionSet;
import necesse.engine.input.controller.ControllerInputState;
import necesse.engine.util.ObjectValue;

public class SameControlBindDetector {
    private static final HashMap<Object, ControllerActionSet> usedInputActionSet = new HashMap();
    private static final LinkedList<ObjectValue<Object, Long>> usedStatesWithDetectionTimer = new LinkedList();

    public static boolean wasInputJustUsedWithDifferentAction(ControllerInputState state, Object inputUsed) {
        if (inputUsed == null) {
            return false;
        }
        ControllerActionSet actionSet = usedInputActionSet.getOrDefault(inputUsed, null);
        if (actionSet != null && actionSet != state.getActionSet()) {
            return true;
        }
        usedStatesWithDetectionTimer.add(new ObjectValue<Object, Long>(inputUsed, System.currentTimeMillis() + 500L));
        usedInputActionSet.put(inputUsed, state.getActionSet());
        return false;
    }

    public static void clearUsedStatesAfterDetectionTimer() {
        while (!usedStatesWithDetectionTimer.isEmpty()) {
            ObjectValue<Object, Long> first = usedStatesWithDetectionTimer.getFirst();
            if ((Long)first.value > System.currentTimeMillis()) break;
            usedInputActionSet.remove(first.object);
            usedStatesWithDetectionTimer.removeFirst();
        }
    }
}

