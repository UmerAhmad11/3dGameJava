package ai_campus_life;

// import de.lessvoid.nifty.Nifty;
// import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class StartScreenController implements ScreenController {
    private Main mainApp;

    // Constructor called from Main.java: new StartScreenController(this)
    public StartScreenController(Main app) {
        this.mainApp = app;
    }

    // Bind, onStartScreen, onEndScreen methods as required by Nifty ScreenController
    @Override
    public void bind(de.lessvoid.nifty.Nifty nifty, de.lessvoid.nifty.screen.Screen screen) { /* ... */ }
    @Override
    public void onStartScreen() { /* ... */ }
    @Override
    public void onEndScreen() { /* ... */ }

    /**
     * This method should be linked to your "Start Game" button in the Nifty XML.
     * For example, in your XML: <button ... interact="onClick:startGameClicked()" />
     */
    public void startGameClicked() {
        if (mainApp != null) {
            mainApp.transitionToGame();
        }
    }
}
