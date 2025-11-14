/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.util.GameMath;
import necesse.engine.util.generalTree.GeneralTree;
import necesse.engine.util.generalTree.GeneralTreeNode;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class DebugMobAIForm
extends Form {
    public final Mob mob;
    public final BehaviourTreeAI<?> treeAI;
    private final HudDrawElement hudElement;
    private int maxLevel;

    public DebugMobAIForm(Level clientLevel, Mob mob, Consumer<DebugMobAIForm> closePressed) {
        this(clientLevel, mob, 350, 300, closePressed);
    }

    public DebugMobAIForm(Level clientLevel, final Mob mob, int width, int height, Consumer<DebugMobAIForm> closePressed) {
        super(width, height);
        this.mob = mob;
        this.hudElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                final DrawOptions drawOptions = HUD.levelBoundOptions(camera, mob.getSelectBox());
                list.add(new SortedDrawable(){

                    @Override
                    public int getPriority() {
                        return 0;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        drawOptions.draw();
                    }
                });
            }
        };
        clientLevel.hudManager.addElement(this.hudElement);
        this.treeAI = mob.ai;
        this.setDraggingBox(new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE));
        this.onDragged(e -> {
            GameWindow window = WindowManager.getWindow();
            e.x = GameMath.limit(e.x, -this.getWidth() + 20, window.getHudWidth() - 20);
            e.y = GameMath.limit(e.y, -this.getHeight() + 20, window.getHudHeight() - 20);
        });
        FormLabel title = this.addComponent(new FormLabel(mob.getDisplayName(), new FontOptions(16), 0, this.getWidth() / 2, 4));
        FormLabel subTitle = this.addComponent(new FormLabel("" + mob.getUniqueID(), new FontOptions(12), 0, this.getWidth() / 2, 20));
        FormTextButton closeButton = this.addComponent(new FormTextButton("Close", 4, this.getHeight() - 24, this.getWidth() - 8, FormInputSize.SIZE_20, ButtonColor.BASE));
        closeButton.onClicked(e -> closePressed.accept(this));
        this.setMinimumResize(100, 100);
        this.allowResize(true, true, true, true, e -> {
            title.setX(e.width / 2);
            subTitle.setX(e.width / 2);
            closeButton.setWidth(e.width - 8);
            closeButton.setY(e.height - 24);
        });
        if (this.treeAI != null) {
            Form contentForm = this.addComponent(new Form(this.getWidth(), this.getHeight() - 35 - 28));
            contentForm.setY(35);
            contentForm.drawBase = false;
            Form treeForm = contentForm.addComponent(new Form(contentForm.getWidth(), contentForm.getHeight() - 75), 1);
            FormContentBox treeContent = treeForm.addComponent(new FormContentBox(0, 0, treeForm.getWidth(), treeForm.getHeight()));
            int nodeWidth = 100;
            int nodeHeight = 50;
            NodeInfo rootNode = new NodeInfo(new HashMap<Integer, GeneralTreeNode>(), null, 0, this.treeAI.tree);
            new GeneralTree(nodeHeight + 10, 10, 20).calculateNodePositions(rootNode);
            rootNode.addNodeComponents(treeContent, nodeWidth, nodeHeight);
            treeContent.fitContentBoxToComponents(20);
            treeContent.centerContentHorizontal();
            treeContent.centerContentVertical();
            Form blackboardForm = contentForm.addComponent(new Form(contentForm.getWidth(), contentForm.getHeight() - treeForm.getHeight()));
            blackboardForm.setY(treeForm.getHeight());
            FormContentBox blackboardContent = blackboardForm.addComponent(new FormContentBox(0, 0, blackboardForm.getWidth(), blackboardForm.getHeight()){

                @Override
                public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                    DebugMobAIForm.this.updateBlackboardContent(this);
                    super.draw(tickManager, perspective, renderBox);
                }
            });
            blackboardForm.drawBase = false;
            this.onResize(e -> {
                contentForm.setWidth(e.width);
                contentForm.setHeight(e.height - 35 - 28);
                blackboardForm.setWidth(contentForm.getWidth());
                treeForm.setWidth(contentForm.getWidth());
                treeForm.setHeight(contentForm.getHeight() - blackboardForm.getHeight());
                blackboardForm.setY(contentForm.getHeight() - blackboardForm.getHeight());
                if (treeForm.getHeight() < 20) {
                    treeForm.setHeight(20);
                    blackboardForm.setY(20);
                    blackboardForm.setHeight(contentForm.getHeight() - blackboardForm.getY());
                }
                treeContent.setWidth(treeForm.getWidth());
                treeContent.setHeight(treeForm.getHeight());
                treeContent.fitContentBoxToComponents(20);
                treeContent.centerContentHorizontal();
                treeContent.centerContentVertical();
                blackboardContent.setWidth(blackboardForm.getWidth());
                blackboardContent.setHeight(blackboardForm.getHeight());
                blackboardContent.fitContentBoxToComponents(5);
            });
            treeForm.setMinimumResize(0, 20);
            treeForm.allowResize(false, true, false, false, e -> {
                e.height = Math.min(e.height, contentForm.getHeight() - 20);
                blackboardForm.setY(e.y + e.height);
                blackboardForm.setHeight(contentForm.getHeight() - blackboardForm.getY());
                treeContent.setHeight(e.height);
                treeContent.fitContentBoxToComponents(20);
                treeContent.centerContentHorizontal();
                treeContent.centerContentVertical();
                blackboardContent.setWidth(blackboardForm.getWidth());
                blackboardContent.setHeight(blackboardForm.getHeight());
                blackboardContent.fitContentBoxToComponents(5);
            });
        } else {
            FormLabel label = this.addComponent(new FormLabel("Mob does not\nhave BehaviourTreeAI", new FontOptions(16), 0, this.getWidth() / 2, this.getHeight() / 2 - 8));
            this.onResize(e -> label.setPosition(e.width / 2, e.height / 2 - 8));
        }
        GameWindow window = WindowManager.getWindow();
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    private void updateBlackboardContent(FormContentBox contentBox) {
        contentBox.clearComponents();
        FormFlow flow = new FormFlow(0);
        contentBox.addComponent(flow.nextY(new FormLabel(String.valueOf(this.treeAI.blackboard.mover), new FontOptions(12), -1, 0, 0)));
        this.treeAI.blackboard.forEach((key, item) -> contentBox.addComponent(flow.nextY(new FormLabel(key + ": " + item, new FontOptions(12), -1, 0, 0))));
        contentBox.fitContentBoxToComponents(10);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.hudElement.remove();
    }

    private static String className(Class<?> clazz) {
        Class<?> superclass;
        int lIndex;
        String name = clazz.getName();
        int pIndex = name.lastIndexOf(".");
        if (pIndex != -1) {
            name = name.substring(pIndex + 1);
        }
        if ((lIndex = name.lastIndexOf("$")) != -1 && (superclass = clazz.getSuperclass()) != null) {
            return DebugMobAIForm.className(superclass) + "{" + name.substring(lIndex + 1) + "}";
        }
        return name;
    }

    private class NodeInfo
    extends GeneralTreeNode {
        public final AINode<?> node;
        public final ArrayList<NodeInfo> children;
        private AINodeComponent component;

        public NodeInfo(HashMap<Integer, GeneralTreeNode> prevLevelNodes, NodeInfo parent, int childIndex, AINode<?> node) {
            super(prevLevelNodes, parent, childIndex, 100);
            this.children = new ArrayList();
            DebugMobAIForm.this.maxLevel = Math.max(DebugMobAIForm.this.maxLevel, this.level);
            this.node = node;
            int i = 0;
            for (AINode<?> child : node.debugChildren()) {
                this.children.add(new NodeInfo(prevLevelNodes, this, i, child));
                ++i;
            }
        }

        @Override
        public List<? extends GeneralTreeNode> getChildren() {
            return this.children;
        }

        public int getMaxChildrenLevelSize() {
            int out = this.children.size();
            for (NodeInfo child : this.children) {
                out = Math.max(out, child.getMaxChildrenLevelSize());
            }
            return out;
        }

        public void addNodeComponents(FormContentBox content, int width, int height) {
            this.component = new AINodeComponent(this, this.x, this.y, width, height);
            content.addComponent(this.component);
            for (NodeInfo child : this.children) {
                child.addNodeComponents(content, width, height);
            }
        }
    }

    public static class AINodeTextComponent
    extends FormComponent
    implements FormPositionContainer {
        public NodeInfo info;
        private FormPosition position;
        public FontOptions fontOptions;
        public String name;

        public AINodeTextComponent(NodeInfo info, int x, int y, FontOptions fontOptions) {
            this.info = info;
            this.position = new FormFixedPosition(x, y);
            this.fontOptions = fontOptions;
            this.name = info.node.getClass().getName();
            int pointI = this.name.lastIndexOf(".");
            if (pointI != -1) {
                this.name = this.name.substring(pointI + 1);
            }
        }

        public AINodeTextComponent(NodeInfo info, int x, int y) {
            this(info, x, y, new FontOptions(16));
        }

        @Override
        public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        }

        @Override
        public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        }

        @Override
        public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        }

        @Override
        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
            Color color = Color.YELLOW;
            if (this.info.node.lastResult != null) {
                switch (this.info.node.lastResult) {
                    case SUCCESS: {
                        color = Color.GREEN;
                        break;
                    }
                    case FAILURE: {
                        color = Color.RED;
                        break;
                    }
                    case RUNNING: {
                        color = Color.BLUE;
                    }
                }
            }
            FontManager.bit.drawString(this.getX(), this.getY(), this.name, this.fontOptions.color(color));
        }

        @Override
        public List<Rectangle> getHitboxes() {
            return AINodeTextComponent.singleBox(new Rectangle(this.getX(), this.getY(), FontManager.bit.getWidthCeil(this.name, this.fontOptions), FontManager.bit.getHeightCeil(this.name, this.fontOptions)));
        }

        @Override
        public FormPosition getPosition() {
            return this.position;
        }

        @Override
        public void setPosition(FormPosition position) {
            this.position = position;
        }
    }

    public static class AINodeComponent
    extends FormComponent
    implements FormPositionContainer {
        public NodeInfo info;
        private FormPosition position;
        public int width;
        public int height;
        private boolean isMouseOver;

        public AINodeComponent(NodeInfo info, int x, int y, int width, int height) {
            this.info = info;
            this.position = new FormFixedPosition(x, y);
            this.width = width;
            this.height = height;
        }

        @Override
        public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
            if (event.isMouseMoveEvent()) {
                this.isMouseOver = this.isMouseOver(event);
                if (this.isMouseOver) {
                    event.useMove();
                }
            }
            if (event.getID() == -100 && this.isMouseOver(event)) {
                if (!event.state) {
                    SelectionFloatMenu menu = new SelectionFloatMenu(this);
                    this.info.node.addDebugActions(menu);
                    if (!menu.isEmpty()) {
                        this.getManager().openFloatMenu(menu);
                    }
                }
                event.use();
            }
        }

        @Override
        public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        }

        @Override
        public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
            Color color = Color.YELLOW;
            if (this.info.node.lastResult != null) {
                switch (this.info.node.lastResult) {
                    case SUCCESS: {
                        color = Color.GREEN;
                        break;
                    }
                    case FAILURE: {
                        color = Color.RED;
                        break;
                    }
                    case RUNNING: {
                        color = Color.BLUE;
                    }
                }
            }
            Renderer.initQuadDraw(this.width, this.height).color(color).alpha(0.5f).draw(this.getX(), this.getY());
            String name = DebugMobAIForm.className(this.info.node.getClass());
            if (this.isMouseOver) {
                ListGameTooltips tooltips = new ListGameTooltips(name);
                tooltips.add("Last result: " + (this.info.node.lastResult == null ? "INACTIVE" : this.info.node.lastResult));
                this.info.node.addDebugTooltips(tooltips);
                GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
            }
            if (this.info.parent != null && ((NodeInfo)this.info.parent).component != null) {
                AINodeComponent parent = ((NodeInfo)this.info.parent).component;
                Renderer.drawLineRGBA(this.getX() + this.width / 2, this.getY(), parent.getX() + parent.width / 2, parent.getY() + parent.height, 1.0f, 1.0f, 0.0f, 1.0f);
            }
            for (NodeInfo child : this.info.children) {
                Renderer.drawLineRGBA(this.getX() + this.width / 2, this.getY() + this.height, child.component.getX() + ((NodeInfo)child).component.width / 2, child.component.getY(), 1.0f, 1.0f, 0.0f, 1.0f);
            }
            FormShader.FormShaderState state = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(this.width, this.height));
            try {
                FairType type = new FairType().append(new FontOptions(12), name);
                FairTypeDrawOptions drawOptions = type.getDrawOptions(FairType.TextAlign.CENTER, this.width, true);
                int drawOptionsHeight = drawOptions.getBoundingBox().height;
                drawOptions.draw(this.width / 2, Math.max(0, this.height / 2 - drawOptionsHeight / 2), new Color(20, 20, 20));
            }
            finally {
                state.end();
            }
        }

        @Override
        public List<Rectangle> getHitboxes() {
            return AINodeComponent.singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
        }

        @Override
        public FormPosition getPosition() {
            return this.position;
        }

        @Override
        public void setPosition(FormPosition position) {
            this.position = position;
        }
    }
}

