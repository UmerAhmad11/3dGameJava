package ai_campus_life;


import com.jme3.app.state.BaseAppState;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;

public class MainMenuState extends BaseAppState {

    private final Main mainApp;
    private Node guiNode;

    public MainMenuState(Main mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    protected void initialize(Application app) {
        GuiGlobals.initialize(app);
        guiNode = ((SimpleApplication) app).getGuiNode();

        // Create the main menu container
        Container menu = new Container();
        menu.setLocalTranslation(app.getCamera().getWidth() / 2 - 150, app.getCamera().getHeight() / 2 + 100, 0);

        // Add a title label
        menu.addChild(new Label("Main Menu"));

        // Add the start button
        Button startButton = menu.addChild(new Button("Start Game"));
        startButton.addClickCommands(source -> mainApp.startGame());

        // Attach the menu to the GUI node
        guiNode.attachChild(menu);
    }

    @Override
    protected void cleanup(Application app) {
        // Cleanup when the state is detached
        guiNode.detachAllChildren();
    }

    @Override
    protected void onEnable() {
        // Called when the state is attached
    }

    @Override
    protected void onDisable() {
        // Called when the state is detached
    }
}

