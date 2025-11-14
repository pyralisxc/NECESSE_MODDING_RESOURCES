/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class EnableBannerOfWarLevelEvent
extends LevelEvent
implements LevelBuffsEntityComponent {
    public EnableBannerOfWarLevelEvent() {
        super(true);
        this.shouldSave = true;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public Stream<ModifierValue<?>> getLevelModifiers() {
        return Stream.of(new ModifierValue<Boolean>(LevelModifiers.BANNER_OF_WAR_DISABLED, false).max(false));
    }
}

