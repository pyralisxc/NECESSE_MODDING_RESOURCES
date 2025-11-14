/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.containerSlot.FormContainerMaterialSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.lists.FormIngredientRecipeList;
import necesse.gfx.forms.presets.containerComponent.mob.DialogueForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.mob.ElderContainer;
import necesse.inventory.recipe.Recipes;

public class ElderContainerForm<T extends ElderContainer>
extends ShopContainerForm<T> {
    public static int[] tipIndexes = new int[]{1, 2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    public DialogueForm tipsForm;
    public DialogueForm helpForm;
    public ArrayList<HelpDialogueOption> helpForms;
    public int currentTipIndex;
    public FormContainerSlot ingredientSlot;
    public FormIngredientRecipeList ingredientList;
    private int itemID;

    public ElderContainerForm(Client client, T container, int width, int height, int maxExpeditionsHeight) {
        super(client, container, width, height, maxExpeditionsHeight);
        this.tipsForm = this.addComponent(new DialogueForm("eldertips", width, height, null, null));
        this.helpForms = new ArrayList();
        this.helpForm = this.addComponent(new DialogueForm("elderhelp", width, 220, this.header, new LocalMessage("ui", "elderpicktopic")));
        this.helpForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> this.makeCurrent(this.dialogueForm));
        DialogueForm crafting = new DialogueForm("crafting", width, height, new LocalMessage("ui", "eldercrafting"), null);
        crafting.content.addComponent(crafting.flow.nextY(new FormLabel(Localization.translate("ui", "eldercraftingtip"), new FontOptions(16), 0, crafting.getWidth() / 2, 0, crafting.getWidth() - 20), 10));
        int craftingNext = crafting.flow.next(100);
        int craftingWidth = crafting.getWidth() - 80 - 8;
        this.ingredientSlot = new FormContainerMaterialSlot(client, (Container)container, ((ElderContainer)container).INGREDIENT_SLOT, 4 + craftingWidth + (crafting.getWidth() - craftingWidth) / 2 - 40, craftingNext + 15);
        crafting.content.addComponent(this.ingredientSlot);
        this.ingredientList = new FormIngredientRecipeList(4, craftingNext, craftingWidth, 100, client);
        crafting.content.addComponent(this.ingredientList);
        crafting.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
            container.setInCraftingForm.runAndSend(false);
            this.makeCurrent(this.helpForm);
        });
        this.addHelpForm(new LocalMessage("ui", "eldercrafting"), crafting, () -> container.setInCraftingForm.runAndSend(true));
        crafting.setHeight(Math.max(height, crafting.getContentHeight() + 5));
        this.addHelpForm(new LocalMessage("ui", "eldertrading"), this.simpleDialogueForm("trading", width, height, new LocalMessage("ui", "eldertrading"), new LocalMessage("ui", "eldertradingtipnew")));
        this.addHelpForm(new LocalMessage("ui", "elderobjects"), this.simpleDialogueForm("objects", width, height, new LocalMessage("ui", "elderobjects"), new LocalMessage("ui", "elderobjectstip")));
        this.addHelpForm(new LocalMessage("ui", "eldertiles"), this.simpleDialogueForm("tiles", width, height, new LocalMessage("ui", "eldertiles"), new LocalMessage("ui", "eldertilestip")));
        this.addHelpForm(new LocalMessage("ui", "eldertools"), this.simpleDialogueForm("tools", width, height, new LocalMessage("ui", "eldertools"), new LocalMessage("ui", "eldertoolstip")));
        this.addHelpForm(new LocalMessage("ui", "elderweapons"), this.simpleDialogueForm("weapons", width, height, new LocalMessage("ui", "elderweapons"), new LocalMessage("ui", "elderweaponstip")));
        this.addHelpForm(new LocalMessage("ui", "elderarmor"), this.simpleDialogueForm("armor", width, height, new LocalMessage("ui", "elderarmor"), new LocalMessage("ui", "elderarmortip")));
        this.addHelpForm(new LocalMessage("ui", "eldertrinkets"), this.simpleDialogueForm("trinkets", width, height, new LocalMessage("ui", "eldertrinkets"), new LocalMessage("ui", "eldertrinketstip")));
        this.addHelpForm(new LocalMessage("ui", "elderenchants"), this.simpleDialogueForm("enchants", width, height, new LocalMessage("ui", "elderenchants"), new LocalMessage("ui", "elderenchantstip")));
        this.addHelpForm(new LocalMessage("ui", "elderpotions"), this.simpleDialogueForm("potions", width, height, new LocalMessage("ui", "elderpotions"), new LocalMessage("ui", "elderpotionstip")));
        this.addHelpForm(new LocalMessage("ui", "eldersettlements"), this.simpleDialogueForm("settlements", width, height, new LocalMessage("ui", "eldersettlements"), new LocalMessage("ui", "eldersettlementstip")));
        this.helpForms.sort(Comparator.comparing(c -> c.text.translate()));
        this.helpForms.forEach(HelpDialogueOption::addDialogueOption);
    }

    public ElderContainerForm(Client client, T container) {
        this(client, container, 480, defaultHeight, defaultHeight);
    }

    private DialogueForm simpleDialogueForm(String name, int width, int height, GameMessage header, GameMessage text) {
        DialogueForm dialogueForm = new DialogueForm(name, width, height, header, null);
        dialogueForm.content.addComponent(dialogueForm.flow.nextY(new FormLabel(text.translate(), new FontOptions(16), 0, dialogueForm.getWidth() / 2, 0, dialogueForm.getWidth() - 20), 10));
        dialogueForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> this.makeCurrent(this.helpForm));
        dialogueForm.setHeight(Math.max(height, dialogueForm.getContentHeight() + 5));
        return dialogueForm;
    }

    @Override
    protected void addQuestsDialogueOption() {
        super.addQuestsDialogueOption();
        this.dialogueForm.addDialogueOption(new LocalMessage("ui", "eldertips"), () -> {
            this.currentTipIndex = GameRandom.globalRandom.nextInt(tipIndexes.length);
            this.updateTip();
            this.makeCurrent(this.tipsForm);
        });
        this.dialogueForm.addDialogueOption(new LocalMessage("ui", "elderhelpbutton"), () -> this.makeCurrent(this.helpForm));
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        int nextItemID;
        InventoryItem item = this.ingredientSlot.getContainerSlot().getItem();
        int n = nextItemID = item == null ? -1 : item.item.getID();
        if (this.itemID != nextItemID) {
            this.itemID = nextItemID;
            if (this.itemID == -1) {
                this.ingredientList.setRecipes(null);
            } else {
                this.ingredientList.setRecipes(Recipes.getRecipesFromResultAndIngredient(nextItemID));
            }
        }
        super.draw(tickManager, perspective, renderBox);
    }

    public void updateTip() {
        int tipIndex = Math.floorMod(this.currentTipIndex, tipIndexes.length);
        int tipNum = tipIndexes[tipIndex];
        this.tipsForm.reset(MobRegistry.getLocalization(((ElderContainer)this.container).humanShop.getID()), new LocalMessage("ui", "eldertip" + tipNum));
        this.tipsForm.addDialogueOption(new LocalMessage("ui", "eldernexttip"), () -> {
            ++this.currentTipIndex;
            this.updateTip();
        });
        this.tipsForm.addDialogueOption(new LocalMessage("ui", "elderprevtip"), () -> {
            --this.currentTipIndex;
            this.updateTip();
        });
        this.tipsForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> this.makeCurrent(this.dialogueForm));
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.tipsForm);
        ContainerComponent.setPosFocus(this.helpForm);
        this.helpForms.forEach(h -> ContainerComponent.setPosFocus(h.form));
    }

    private void addHelpForm(GameMessage buttonText, Form helpForm) {
        this.addHelpForm(buttonText, helpForm, null);
    }

    private void addHelpForm(GameMessage buttonText, Form helpForm, Runnable buttonPressed) {
        this.addComponent(helpForm);
        this.helpForms.add(new HelpDialogueOption(buttonText, buttonPressed, helpForm));
    }

    private class HelpDialogueOption {
        public final GameMessage text;
        public final Runnable entered;
        public final Form form;

        public HelpDialogueOption(GameMessage text, Runnable entered, Form form) {
            this.text = text;
            this.entered = entered;
            this.form = form;
        }

        public void addDialogueOption() {
            ElderContainerForm.this.helpForm.addDialogueOption(this.text, () -> {
                if (this.entered != null) {
                    this.entered.run();
                }
                ElderContainerForm.this.makeCurrent(this.form);
            });
        }
    }
}

