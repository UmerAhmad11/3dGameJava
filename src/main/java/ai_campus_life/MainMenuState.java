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

        float width = app.getCamera().getWidth();
        float height = app.getCamera().getHeight();

        // === OUTER full-screen container ===
        menu = new Container();
        menu.setPreferredSize(new Vector3f(width, height, 0));
        menu.setLocalTranslation(0, height, 0); // Align to top-left (Lemur origin)

        // Background (optional)
        menu.setBackground(new QuadBackgroundComponent(new ColorRGBA(0.5f, 0.25f, 0.2f, 0.85f)));
        

        // === INNER centered container ===
        Container centerContainer = new Container(new BoxLayout(Axis.Y, FillMode.None));
        centerContainer.setLocalTranslation((width - 300) / 2, height / 2 + 100, 0); // X-centered, Y mid-screen
        centerContainer.setPreferredSize(new Vector3f(300, 0, 0)); // Fixed width, height auto

        // Title
        Label titleLabel = new Label("Campus Life");
        titleLabel.setFontSize(70f);
        titleLabel.setColor(ColorRGBA.Black);
        titleLabel.setTextHAlignment(HAlignment.Center);
        titleLabel.setInsets(new Insets3f(20, 5, 20, 5));
        centerContainer.addChild(titleLabel);

        // Start Button
        Button startButton = new Button("Start Life :D");
        startButton.setFontSize(35f);
        startButton.setInsets(new Insets3f(10, 545, 10, 545));
        startButton.setTextHAlignment(HAlignment.Center);
        startButton.addClickCommands(source -> mainApp.startGame());
        centerContainer.addChild(startButton);

        // Exit Button
        Button exitButton = new Button("I gotta dip :(");
        exitButton.setFontSize(35f);
        exitButton.setInsets(new Insets3f(10, 545, 10, 545));
        exitButton.setTextHAlignment(HAlignment.Center);
        exitButton.addClickCommands(source -> mainApp.stop());
        centerContainer.addChild(exitButton);

        // Name
        Label nameLabel = new Label("By: Syed Umer Ahmad");
        nameLabel.setFontSize(70f);
        nameLabel.setColor(ColorRGBA.Black);
        nameLabel.setTextHAlignment(HAlignment.Center);
        nameLabel.setInsets(new Insets3f(20, 5, 20, 5));
        centerContainer.addChild(nameLabel);

        // Add inner container to full-screen container
        menu.addChild(centerContainer);

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
