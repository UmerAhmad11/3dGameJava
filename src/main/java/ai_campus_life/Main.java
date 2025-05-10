package ai_campus_life;

import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
// Import the Game class
import ai_campus_life.Game;

public class Main extends SimpleApplication {

    private Nifty nifty;
    private NiftyJmeDisplay niftyDisplay;
    private static boolean startGameRequested = false;

    public static void main(String[] args){
        Main menuApp = new Main();
        menuApp.setPauseOnLostFocus(false); // Keep menu running even if focus is lost
        menuApp.start(); // This call is blocking until menuApp.stop() is called

        // After the menuApp stops, check if the game should be started
        if (startGameRequested) {
            Game gameApp = new Game();
            // You might want to set properties for gameApp here, e.g.:
            // gameApp.setPauseOnLostFocus(false);
            gameApp.start();
        }
    }

    @Override
    public void simpleInitApp() {

        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
                assetManager,
                inputManager,
                audioRenderer,
                guiViewPort);
        this.niftyDisplay = niftyDisplay; // Store for later removal
        nifty = niftyDisplay.getNifty();
        StartScreenController startScreen = new StartScreenController(this);
        nifty.fromXml("Interface/Nifty/HelloJme.xml", "start", startScreen);

        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);

        // disable the fly cam
//        flyCam.setEnabled(false);
//        flyCam.setDragToRotate(true);
        inputManager.setCursorVisible(true);
    }

    /**
     * This method should be called by the StartScreenController when the user
     * clicks the "start game" button in the Nifty GUI.
     */
    public void transitionToGame() {
        Main.startGameRequested = true;

        // Perform cleanup of Nifty GUI
        if (niftyDisplay != null) {
            guiViewPort.removeProcessor(niftyDisplay);
        }
        if (nifty != null) {
            nifty.exit(); // Gracefully exit Nifty
        }
        inputManager.setCursorVisible(false); // Hide cursor before game starts
        this.stop(); // Stop the Main (menu) application
    }
}