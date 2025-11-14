/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

public enum PresetRotation {
    CLOCKWISE(1),
    ANTI_CLOCKWISE(3),
    HALF_180(2);

    public final int dirOffset;

    private PresetRotation(int dirOffset) {
        this.dirOffset = dirOffset;
    }

    public static PresetRotation toRotationAngle(int rightAngles) {
        if ((rightAngles %= 4) == 0) {
            return null;
        }
        if (rightAngles == 3 || rightAngles == -1) {
            return ANTI_CLOCKWISE;
        }
        if (rightAngles == -3 || rightAngles == 1) {
            return CLOCKWISE;
        }
        return HALF_180;
    }

    public static PresetRotation addRotations(PresetRotation last, PresetRotation rotation) {
        if (last == null) {
            return rotation;
        }
        if (rotation == null) {
            return last;
        }
        return PresetRotation.toRotationAngle(last.dirOffset + rotation.dirOffset);
    }
}

