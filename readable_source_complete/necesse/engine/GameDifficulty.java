/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.util.ArrayList;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.ui.ButtonIcon;

public enum GameDifficulty {
    CASUAL(0.4f, 0.6f, false, 1.0f, 0.6f, 0.9f, new LocalMessage("ui", "diffcasual"), new LocalMessage("ui", "diffcasualdesc"), "diffcasualeffect", () -> Settings.UI.difficulty_casual_background, () -> Settings.UI.difficulty_casual),
    ADVENTURE(0.7f, 0.8f, false, 1.0f, 0.85f, 1.0f, new LocalMessage("ui", "diffadventure"), new LocalMessage("ui", "diffadventuredesc"), "diffadventureeffect", () -> Settings.UI.difficulty_adventure_background, () -> Settings.UI.difficulty_adventure),
    CLASSIC(1.0f, 1.0f, false, 1.0f, 1.0f, 1.0f, new LocalMessage("ui", "diffclassic"), new LocalMessage("ui", "diffclassicdesc"), "diffclassiceffect", () -> Settings.UI.difficulty_classic_background, () -> Settings.UI.difficulty_classic),
    HARD(1.2f, 1.2f, true, 0.75f, 1.25f, 1.2f, new LocalMessage("ui", "diffhard"), new LocalMessage("ui", "diffharddesc"), "diffhardeffect", () -> Settings.UI.difficulty_hard_background, () -> Settings.UI.difficulty_hard),
    BRUTAL(1.6f, 1.4f, true, 0.4f, 1.5f, 1.5f, new LocalMessage("ui", "diffbrutal"), new LocalMessage("ui", "diffbrutaldesc"), "diffbrualeffect", () -> Settings.UI.difficulty_brutal_background, () -> Settings.UI.difficulty_brutal);

    public final GameMessage displayName;
    public final GameMessage description;
    public final String effectsMessagePrefix;
    public final float damageTakenModifier;
    public final float raiderDamageModifier;
    public final float knockbackGivenModifier;
    public final float enemySpawnRateModifier;
    public final float enemySpawnCapModifier;
    public boolean canSettlersDie;
    public final Supplier<ButtonIcon> buttonIconBackgroundSupplier;
    public final Supplier<ButtonIcon> buttonIconForegroundSupplier;

    private GameDifficulty(float damageTakenModifier, float raiderDamageModifier, boolean canSettlersDie, float knockbackGivenModifier, float enemySpawnRateModifier, float enemySpawnCapModifier, GameMessage displayName, GameMessage description, String effectsMessagePrefix, Supplier<ButtonIcon> buttonIconBackgroundSupplier, Supplier<ButtonIcon> buttonIconForegroundSupplier) {
        this.damageTakenModifier = damageTakenModifier;
        this.raiderDamageModifier = raiderDamageModifier;
        this.canSettlersDie = canSettlersDie;
        this.knockbackGivenModifier = knockbackGivenModifier;
        this.enemySpawnRateModifier = enemySpawnRateModifier;
        this.enemySpawnCapModifier = enemySpawnCapModifier;
        this.displayName = displayName;
        this.description = description;
        this.effectsMessagePrefix = effectsMessagePrefix;
        this.buttonIconBackgroundSupplier = buttonIconBackgroundSupplier;
        this.buttonIconForegroundSupplier = buttonIconForegroundSupplier;
    }

    public ArrayList<GameMessage> getEffectMessages() {
        ArrayList<GameMessage> messages = new ArrayList<GameMessage>();
        Language lang = Localization.getCurrentLang();
        for (int i = 1; i <= 50 && (lang.translationExists("ui", this.effectsMessagePrefix + i) || Localization.EnglishTranslation.exists("ui", this.effectsMessagePrefix + i)); ++i) {
            messages.add(new LocalMessage("ui", this.effectsMessagePrefix + i));
        }
        return messages;
    }
}

