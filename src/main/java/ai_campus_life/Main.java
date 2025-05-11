package ai_campus_life;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;

public class Main extends SimpleApplication {

    private MainMenuState mainMenuState;
    private static AppSettings gameSettings;

    public static void main(String[] args) {
        
        Main app = new Main();

        // Configure the game settings
        gameSettings = new AppSettings(true);
        gameSettings.setTitle("Main Menu");
        gameSettings.setResolution(1280, 720);
        app.setSettings(gameSettings);
        
        // Start the main menu
        app.start();
    }

    @Override
    public void simpleInitApp() {

        // Hide stats and FPS display
        setDisplayFps(false);
        setDisplayStatView(false);

        // Initialize Lemur
        GuiGlobals.initialize(this);

        // Add the main menu state
        mainMenuState = new MainMenuState(this);
        getStateManager().attach(mainMenuState);
    }

    public void startGame() {
        // Detach the main menu state
        getStateManager().detach(mainMenuState);

        // Start the game
        Game game = new Game();
        game.setSettings(gameSettings);
        game.start();

        // Stop the main menu app
        this.stop();
    }
}