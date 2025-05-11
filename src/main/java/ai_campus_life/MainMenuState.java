package ai_campus_life;


import com.jme3.app.state.BaseAppState;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;

public class MainMenuState extends BaseAppState {

    private final Main mainApp;
    private Node guiNode;
    private Container menu; // Keep a reference to the menu container

    public MainMenuState(Main mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    protected void initialize(Application app) {
        // Ensure Lemur is initialized. If Main.java already does this (recommended),
        // this call is harmless and ensures it's initialized if this state is used standalone.
        GuiGlobals.initialize(app);
        // The "glass" style should be set as default in Main.java for consistency.

        guiNode = ((SimpleApplication) app).getGuiNode();

        // Create the main menu container
        menu = new Container();
        // Use BoxLayout for vertical arrangement of elements
        menu.setLayout(new BoxLayout(Axis.Y, FillMode.None));
        // Add padding inside the container
        menu.setInsets(new Insets3f(15, 15, 15, 15)); // top, left, bottom, right
        // Set a semi-transparent background for the container
        menu.setBackground(new QuadBackgroundComponent(new ColorRGBA(0.1f, 0.15f, 0.2f, 0.85f)));

        // Add a title label
        Label titleLabel = new Label("Main Menu");
        titleLabel.setFontSize(28f); // Larger font for the title
        titleLabel.setTextHAlignment(HAlignment.Center); // Center the text
        titleLabel.setInsets(new Insets3f(5, 5, 20, 5)); // Padding: top, left, bottom, right
        menu.addChild(titleLabel);

        // Add the start button
        Button startButton = menu.addChild(new Button("Start Game"));
        startButton.setFontSize(20f);
        startButton.setTextHAlignment(HAlignment.Center);
        startButton.setInsets(new Insets3f(10, 10, 10, 10)); // Padding for the button
        startButton.addClickCommands(source -> mainApp.startGame());

        // Add an exit button
        Button exitButton = new Button("Exit");
        exitButton.setFontSize(20f);
        exitButton.setTextHAlignment(HAlignment.Center);
        exitButton.setInsets(new Insets3f(10, 10, 10, 10));
        exitButton.addClickCommands(source -> mainApp.stop()); // Exit the application
        menu.addChild(exitButton);

        // Calculate the preferred size of the menu *after* adding all children
        Vector3f preferredSize = menu.getPreferredSize();

        // Center the menu on the screen
        // The guiNode's origin (0,0) is the bottom-left of the screen.
        float x = (app.getCamera().getWidth() - preferredSize.x) / 2;
        float y = (app.getCamera().getHeight() - preferredSize.y) / 2;
        menu.setLocalTranslation(x, y, 0);

        // Attach the menu to the GUI node
        guiNode.attachChild(menu);
    }
    

    @Override
    protected void cleanup(Application app) {
        // Detach only the UI elements this state created
        if (guiNode != null && menu != null) {
            guiNode.detachChild(menu);
        }
    }

    @Override
    protected void onEnable() {
        // Called when the state is attached or re-enabled
        if (menu != null) {
            // Re-center the menu on enable
            Vector3f preferredSize = menu.getPreferredSize();
            float x = (getApplication().getCamera().getWidth() - preferredSize.x) / 2;
            float y = (getApplication().getCamera().getHeight() + preferredSize.y) / 2;
            menu.setLocalTranslation(x, y, 0);

            // Make sure the menu is visible
            menu.setCullHint(Node.CullHint.Inherit);
        }
    }

    @Override
    protected void onDisable() {
        // Called when the state is detached or disabled
        if (menu != null) {
            menu.setCullHint(Node.CullHint.Always); // Show the menu
; // Disable interaction with the menu
        }
    }
}
