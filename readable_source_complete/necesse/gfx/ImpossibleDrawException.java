/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import necesse.engine.util.ObjectValue;

public class ImpossibleDrawException
extends RuntimeException {
    private static final LinkedList<ObjectValue<Long, Throwable>> drawErrors = new LinkedList();
    private final Iterable<Throwable> causes;

    public static void submitDrawError(Throwable error) {
        error.printStackTrace();
        long currentTime = System.currentTimeMillis();
        drawErrors.add(new ObjectValue<Long, Throwable>(currentTime, error));
        if (drawErrors.size() > 5) {
            ObjectValue<Long, Throwable> last = drawErrors.removeFirst();
            long timeSinceLast = currentTime - (Long)last.object;
            if (timeSinceLast <= 5000L) {
                ArrayList<Throwable> causes = new ArrayList<Throwable>();
                while (!drawErrors.isEmpty()) {
                    causes.add((Throwable)ImpossibleDrawException.drawErrors.removeLast().value);
                }
                throw new ImpossibleDrawException(causes);
            }
        }
    }

    public ImpossibleDrawException(Iterable<Throwable> causes) {
        this.causes = causes;
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        s.println("Impossible draw exception causes:");
        for (Throwable cause : this.causes) {
            cause.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        s.println("Impossible draw exception causes:");
        for (Throwable cause : this.causes) {
            cause.printStackTrace(s);
        }
    }
}

