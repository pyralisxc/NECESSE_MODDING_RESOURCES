/*
 * Decompiled with CFR 0.152.
 */
package tomeofpower.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import tomeofpower.config.TomeConfig;
import tomeofpower.util.BuffCache;
import tomeofpower.util.TomeLogger;

public class ErrorHandler {
    private static final Map<String, ErrorStats> errorCounts = new ConcurrentHashMap<String, ErrorStats>();

    public static void handleBuffApplicationError(Exception e, String context) {
        TomeLogger.error("Buff application failed in context: " + context + " - " + e.getMessage());
        if (TomeConfig.ENABLE_DEBUG_LOGGING) {
            TomeLogger.debug("Stack trace: " + ErrorHandler.getStackTrace(e));
        }
        ErrorHandler.recordError("BUFF_APPLICATION", e, context);
    }

    public static void handleInventoryError(Exception e, String operation) {
        TomeLogger.warn("Inventory operation failed: " + operation + " - " + e.getMessage());
        if (e instanceof NullPointerException) {
            TomeLogger.error("Null pointer in inventory operation - this indicates a serious state issue");
        }
        ErrorHandler.recordError("INVENTORY_OPERATION", e, operation);
    }

    public static void handleEnchantmentError(Exception e, String enchantmentInfo) {
        TomeLogger.warn("Enchantment processing failed for: " + enchantmentInfo + " - " + e.getMessage());
        ErrorHandler.recordError("ENCHANTMENT_PROCESSING", e, enchantmentInfo);
    }

    public static void handleReflectionError(Exception e, String targetClass, String operation) {
        TomeLogger.debug("Reflection operation failed on " + targetClass + "." + operation + " - " + e.getMessage());
        if (TomeConfig.ENABLE_DEBUG_LOGGING) {
            ErrorHandler.recordError("REFLECTION_OPERATION", e, targetClass + "." + operation);
        }
    }

    public static void handleConfigError(Exception e, String configKey) {
        TomeLogger.error("Configuration error for key " + configKey + ": " + e.getMessage());
        TomeLogger.warn("Falling back to default configuration values");
        ErrorHandler.recordError("CONFIGURATION", e, configKey);
    }

    public static void handleCacheError(Exception e, String operation) {
        TomeLogger.warn("Cache operation failed: " + operation + " - " + e.getMessage());
        TomeLogger.info("Clearing cache to recover from error state");
        try {
            BuffCache.clearCache();
        }
        catch (Exception clearException) {
            TomeLogger.error("Failed to clear cache during error recovery: " + clearException.getMessage());
        }
        ErrorHandler.recordError("CACHE_OPERATION", e, operation);
    }

    public static void handleUnexpectedError(Exception e, String context) {
        TomeLogger.error("Unexpected error in " + context + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
        if (TomeConfig.ENABLE_DEBUG_LOGGING) {
            TomeLogger.debug("Full stack trace: " + ErrorHandler.getStackTrace(e));
        }
        ErrorHandler.recordError("UNEXPECTED", e, context);
    }

    public static <T> T requireNonNull(T obj, String fieldName) {
        if (obj == null) {
            IllegalArgumentException e = new IllegalArgumentException("Required field '" + fieldName + "' cannot be null");
            ErrorHandler.handleConfigError(e, fieldName);
            throw e;
        }
        return obj;
    }

    public static int validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            IllegalArgumentException e = new IllegalArgumentException(String.format("Field '%s' value %d is outside valid range [%d, %d]", fieldName, value, min, max));
            ErrorHandler.handleConfigError(e, fieldName);
            return Math.max(min, Math.min(max, value));
        }
        return value;
    }

    public static <T> T safeExecute(Supplier<T> operation, T fallbackValue, String context) {
        try {
            return operation.get();
        }
        catch (Exception e) {
            ErrorHandler.handleUnexpectedError(e, context);
            return fallbackValue;
        }
    }

    public static void safeExecute(Runnable operation, String context) {
        try {
            operation.run();
        }
        catch (Exception e) {
            ErrorHandler.handleUnexpectedError(e, context);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void recordError(String category, Exception e, String context) {
        ErrorStats stats;
        if (!TomeConfig.ENABLE_PERFORMANCE_METRICS) {
            return;
        }
        String key = category + ":" + e.getClass().getSimpleName();
        ErrorStats errorStats = stats = errorCounts.computeIfAbsent(key, k -> new ErrorStats());
        synchronized (errorStats) {
            ++stats.count;
            stats.lastOccurred = System.currentTimeMillis();
            stats.lastMessage = context + " - " + e.getMessage();
            if (stats.count % 10 == 0) {
                TomeLogger.warn(String.format("Error pattern detected: %s occurred %d times, latest: %s", key, stats.count, stats.lastMessage));
            }
        }
    }

    public static String getErrorSummary() {
        if (errorCounts.isEmpty()) {
            return "No errors recorded";
        }
        StringBuilder summary = new StringBuilder("Error Summary:\n");
        for (Map.Entry<String, ErrorStats> entry : errorCounts.entrySet()) {
            ErrorStats stats = entry.getValue();
            summary.append(String.format("- %s: %d occurrences, last: %s\n", entry.getKey(), stats.count, new Date(stats.lastOccurred)));
        }
        return summary.toString();
    }

    private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    private static class ErrorStats {
        int count = 0;
        long lastOccurred = System.currentTimeMillis();
        String lastMessage = "";

        private ErrorStats() {
        }
    }
}

