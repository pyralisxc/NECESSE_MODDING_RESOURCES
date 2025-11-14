/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

public class ItemCombineResult {
    public boolean success;
    public String error;

    private ItemCombineResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public static ItemCombineResult success() {
        return new ItemCombineResult(true, null);
    }

    public static ItemCombineResult failure(String error) {
        return new ItemCombineResult(false, error);
    }

    public static ItemCombineResult failure() {
        return ItemCombineResult.failure(null);
    }
}

