/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.system.APIUtil
 */
package necesse.engine.platforms.sharedOnPC.window;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.APIUtil;

public class GLFWGameError {
    private static final Map<Integer, String> ERROR_CODES = APIUtil.apiClassTokens((field, value) -> 65536 < value && value < 131072, null, (Class[])new Class[]{GLFW.class});
    private static final Object throwLock = new Object();
    private static int[] handledCodes;
    public final int errorCode;
    public final String errorName;
    public final long errorDescriptionPointer;
    public final String errorDescription;
    public final StackTraceElement[] stackTrace;
    protected boolean caught = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void throwError(int errorCode, long errorDescriptionPointer) {
        Object object = throwLock;
        synchronized (object) {
            GLFWGameError error = new GLFWGameError(errorCode, errorDescriptionPointer);
            if (handledCodes != null && (handledCodes.length == 0 || IntStream.of(handledCodes).anyMatch(code -> error.errorCode == code))) {
                throw new GLFWGameErrorException(error);
            }
            error.print();
        }
    }

    private GLFWGameError(int errorCode, long errorDescriptionPointer) {
        this.errorCode = errorCode;
        this.errorName = ERROR_CODES.getOrDefault(errorCode, "GLFW_UNKNOWN_ERROR");
        this.errorDescriptionPointer = errorDescriptionPointer;
        this.errorDescription = errorDescriptionPointer == 0L ? "No description" : GLFWErrorCallback.getDescription((long)errorDescriptionPointer);
        StackTraceElement[] fullStackTrace = Thread.currentThread().getStackTrace();
        this.stackTrace = Arrays.copyOfRange(fullStackTrace, 6, fullStackTrace.length);
    }

    public void print(PrintStream stream) {
        stream.println(this.errorName + ": " + this.errorDescription);
        for (StackTraceElement e : this.stackTrace) {
            stream.println("\t" + e.toString());
        }
    }

    public void print() {
        this.print(System.err);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void tryGLFWError(Handler handler) {
        Object object = throwLock;
        synchronized (object) {
            try {
                handledCodes = handler.errorCodes;
                handler.run();
            }
            catch (GLFWGameErrorException e) {
                handler.onCatch(e.error);
            }
            finally {
                handledCodes = null;
                handler.onFinally();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static <T> T tryGLFWError(Supplier<T> handler) {
        Object object = throwLock;
        synchronized (object) {
            try {
                handledCodes = handler.errorCodes;
                T t = handler.run();
                return t;
            }
            catch (GLFWGameErrorException e) {
                T t2 = handler.onCatch(e.error);
                return t2;
            }
            finally {
                handledCodes = null;
                handler.onFinally();
            }
        }
    }

    private static class GLFWGameErrorException
    extends RuntimeException {
        public final GLFWGameError error;

        public GLFWGameErrorException(GLFWGameError error) {
            this.error = error;
        }
    }

    public static abstract class Handler {
        protected final int[] errorCodes;

        public Handler(int ... errorCodes) {
            this.errorCodes = errorCodes;
        }

        public abstract void run();

        public abstract void onCatch(GLFWGameError var1);

        public void onFinally() {
        }
    }

    public static abstract class Supplier<T> {
        protected final int[] errorCodes;

        public Supplier(int ... errorCodes) {
            this.errorCodes = errorCodes;
        }

        public abstract T run();

        public abstract T onCatch(GLFWGameError var1);

        public void onFinally() {
        }
    }
}

