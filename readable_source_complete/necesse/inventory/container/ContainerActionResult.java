/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container;

public class ContainerActionResult {
    public int value;
    public String error;

    public ContainerActionResult(String error) {
        this.value = 0;
        this.error = error;
    }

    public ContainerActionResult(int value) {
        this.value = value;
    }

    public ContainerActionResult(int value, String error) {
        this.value = value;
        this.error = error;
    }
}

