/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.notifications;

import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;

public enum SettlementNotificationSeverity {
    NOTE(() -> Settings.UI.settlement_note_base, () -> Settings.UI.settlement_note_shadow, () -> Settings.UI.settlement_note_icon, isVisible -> {}),
    WARNING(() -> Settings.UI.settlement_warning_base, () -> Settings.UI.settlement_warning_shadow, () -> Settings.UI.settlement_warning_icon, isVisible -> {
        if (!isVisible.booleanValue()) {
            return;
        }
        SoundManager.playSound(GameResources.pop, SoundEffect.ui().volume(0.8f).pitch(0.7f));
    }),
    URGENT(() -> Settings.UI.settlement_error_base, () -> Settings.UI.settlement_error_shadow, () -> Settings.UI.settlement_error_icon, isVisible -> {
        if (!isVisible.booleanValue()) {
            return;
        }
        SoundManager.playSound(GameResources.pop, SoundEffect.ui().volume(0.8f).pitch(0.7f));
    });

    public final Supplier<GameTexture> baseTexture;
    public final Supplier<GameTexture> shadowTexture;
    public final Supplier<GameTexture> iconTexture;
    public final Consumer<Boolean> playNewNotificationSound;

    private SettlementNotificationSeverity(Supplier<GameTexture> baseTexture, Supplier<GameTexture> shadowTexture, Supplier<GameTexture> iconTexture, Consumer<Boolean> playNewNotificationSound) {
        this.baseTexture = baseTexture;
        this.shadowTexture = shadowTexture;
        this.iconTexture = iconTexture;
        this.playNewNotificationSound = playNewNotificationSound;
    }
}

