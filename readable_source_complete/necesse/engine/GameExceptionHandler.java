/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class GameExceptionHandler {
    public static int crashAfterConsecutiveExceptions = 1;
    public static int maxSavedExceptions = 20;
    private LinkedList<Throwable> savedExceptions = new LinkedList();
    private int frameExceptions;
    private int tickExceptions;
    private String inText;

    public GameExceptionHandler(String inText) {
        this.inText = inText;
    }

    public void clear(boolean isGameTick) {
        if (isGameTick) {
            this.tickExceptions = 0;
        }
        this.frameExceptions = 0;
    }

    public void submitException(boolean isGameTick, Exception e, Runnable onCrash) {
        this.addSavedException(e, true);
        if (isGameTick) {
            ++this.tickExceptions;
        } else {
            ++this.frameExceptions;
        }
        if (this.frameExceptions + this.tickExceptions >= crashAfterConsecutiveExceptions) {
            onCrash.run();
        }
    }

    private void addSavedException(Exception e, boolean print) {
        boolean foundSame = this.savedExceptions.removeIf(ex -> GameExceptionHandler.isSameException(ex, e));
        if (print) {
            if (foundSame) {
                System.err.println("Another error in " + this.inText + ": " + e.toString());
            } else {
                System.err.println("Error in " + this.inText + ":");
                e.printStackTrace(System.err);
            }
        }
        this.savedExceptions.addFirst(e);
        if (this.savedExceptions.size() > maxSavedExceptions) {
            this.savedExceptions.removeLast();
        }
    }

    public void addSavedException(Exception e) {
        this.addSavedException(e, false);
    }

    public List<Throwable> getSavedExceptions() {
        return this.savedExceptions;
    }

    public static boolean isSameException(Throwable e1, Throwable e2) {
        return GameExceptionHandler.isSameException(e1, e2, 0, 10);
    }

    public static boolean isSameException(Throwable e1, Throwable e2, int count, int max) {
        StackTraceElement[] s2;
        if (e1 == e2) {
            return true;
        }
        if (!Objects.equals(e1.getMessage(), e2.getMessage())) {
            return false;
        }
        StackTraceElement[] s1 = e1.getStackTrace();
        if (s1.length != (s2 = e2.getStackTrace()).length) {
            return false;
        }
        for (int i = 0; i < s1.length; ++i) {
            if (s1[i].equals(s2[i])) continue;
            return false;
        }
        Throwable c1 = e1.getCause();
        Throwable c2 = e2.getCause();
        if (c1 != null && c2 != null && count < max) {
            return GameExceptionHandler.isSameException(c1, c2, ++count, max);
        }
        return c1 == null && c2 == null;
    }
}

