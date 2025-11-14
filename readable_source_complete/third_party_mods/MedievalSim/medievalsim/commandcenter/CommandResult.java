/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.commandcenter;

public class CommandResult {
    private final boolean success;
    private final String message;
    private final Object data;

    private CommandResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public Object getData() {
        return this.data;
    }

    public static CommandResult success(String message) {
        return new CommandResult(true, message, null);
    }

    public static CommandResult success(String message, Object data) {
        return new CommandResult(true, message, data);
    }

    public static CommandResult error(String message) {
        return new CommandResult(false, message, null);
    }

    public static CommandResult permissionDenied() {
        return new CommandResult(false, "Permission denied", null);
    }
}

