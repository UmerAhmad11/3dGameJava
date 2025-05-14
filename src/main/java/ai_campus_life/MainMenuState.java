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
import com.simsilica.lemur.effect.*;
import com.simsilica.lemur.effect.AbstractEffect;
;

public class MainMenuState extends BaseAppState {

    private final Main mainApp;
    private Node guiNode;
    private Container menu; // Keep a reference to the menu container

    public MainMenuState(Main mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    protected void initialize(Application app) {
        GuiGlobals.initialize(app);

        guiNode = ((SimpleApplication) app).getGuiNode();

        // Create the full-screen menu container
        menu = new Container();
        menu.setLayout(new BoxLayout(Axis.Y, FillMode.Even)); // Stretch children equally
        menu.setInsets(new Insets3f(50, 50, 50, 50)); // Optional padding inside the full screen
        menu.setBackground(new QuadBackgroundComponent(new ColorRGBA(0.1f, 0.15f, 0.2f, 0.85f)));

        // Set the container to fill the whole screen
        float width = app.getCamera().getWidth();
        float height = app.getCamera().getHeight();
        menu.setPreferredSize(new Vector3f(width, height, 0));
        menu.setLocalTranslation(0, height, 0); // GUI origin is bottom-left, Lemur's top-left is y-aligned to top

        // Add a centered title label
        Label titleLabel = new Label("Campus Life");
        titleLabel.setFontSize(28f);
        titleLabel.setTextHAlignment(HAlignment.Center);
        titleLabel.setInsets(new Insets3f(20, 5, 20, 5));
        menu.addChild(titleLabel);

        // Create a tightly wrapped container
        Container buttonContainer = new Container(new BoxLayout(Axis.Y, FillMode.None));
        buttonContainer.setBackground(new QuadBackgroundComponent(ColorRGBA.DarkGray));
        buttonContainer.setInsets(new Insets3f(5, 5, 5, 5));

        // Add to the menu
        menu.addChild(buttonContainer);

        // Add the button to the container
        Button startButton = buttonContainer.addChild(new Button("Start Life"));
        startButton.setFontSize(22f);
        startButton.setTextHAlignment(HAlignment.Center);
        startButton.setInsets(new Insets3f(10, 20, 10, 20)); // More padding inside the button
        startButton.addClickCommands(source -> mainApp.startGame());

        // Add an exit button
        Button exitButton = menu.addChild(new Button("I gotta dip!"));
        exitButton.setFontSize(22f);
        exitButton.setTextHAlignment(HAlignment.Center);
        exitButton.setInsets(new Insets3f(10, 10, 10, 10));
        exitButton.addClickCommands(source -> mainApp.stop());
        menu.addChild(exitButton);

        // Attach to GUI
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
