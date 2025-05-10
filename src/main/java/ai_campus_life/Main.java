package ai_campus_life;

//Libraries
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;


public class Main extends SimpleApplication {
    private Geometry geom; // The cube, now a class field
    private boolean moveForward = false;
    private boolean moveBackward = false;
    private boolean moveRight = false;
    private boolean moveLeft = false;
    private float movementSpeed = 2f; // units per second

    // Action names (mappings)
    private static final String MOVE_CUBE_FORWARD = "MoveCubeForward";
    private static final String MOVE_CUBE_BACKWARD = "MoveCubeBackward";
    private static final String MOVE_CUBE_LEFT = "MoveCubeLeft";
    private static final String MOVE_CUBE_RIGHT = "MoveCubeRight";

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        flyCam.setEnabled(false);

        // Create a basic cube
        Box b = new Box(1, 1, 1);
        geom = new Geometry("Cube", b); // Initialize the class field
        geom.setLocalTranslation(0, 0, 0);

        // Create a basic material
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);

        // Attach the cube to the scene
        rootNode.attachChild(geom);

        // Add a basic directional light
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        // Set the camera position
        cam.setLocation(new Vector3f(0, 2, 5));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        // Initialize key inputs
        initKeys();
    }

    /**
     * Initializes the key inputs.
     */
    private void initKeys() {
        // Create KeyTriggers and add mappings to the input manager
        inputManager.addMapping(MOVE_CUBE_FORWARD, new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(MOVE_CUBE_BACKWARD, new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(MOVE_CUBE_LEFT, new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(MOVE_CUBE_RIGHT, new KeyTrigger(KeyInput.KEY_RIGHT));

        // Add an ActionListener to listen for these mappings
        inputManager.addListener(actionListener, MOVE_CUBE_FORWARD, MOVE_CUBE_BACKWARD, MOVE_CUBE_LEFT, MOVE_CUBE_RIGHT);
    }

    // ActionListener to handle the key presses and releases
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            switch (name) {
                case MOVE_CUBE_FORWARD:
                    moveForward = isPressed; // Set flag to true on press, false on release
                    break;
                case MOVE_CUBE_BACKWARD:
                    moveBackward = isPressed; // Set flag to true on press, false on release
                    break;
                case MOVE_CUBE_LEFT:
                    moveLeft = isPressed; // Set flag to true on press, false on release
                    break;
                case MOVE_CUBE_RIGHT:
                    moveRight = isPressed; // Set flag to true on press, false on release
                    break;
            }
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        // This method is called every frame
        if (geom != null) { // Ensure the geometry exists
            Vector3f moveDirection = new Vector3f(); // Initialize movement vector

            if (moveForward) {
                moveDirection.z -= 1; // Positive Z for forward
            }
            if (moveBackward) {
                moveDirection.z += 1; // Negative Z for backward
            }
            if (moveRight) {
                moveDirection.x += 1; // Positive X for right
            }
            if (moveLeft) {
                moveDirection.x -= 1; // Negative X for left
            }

            // Normalize to prevent faster diagonal movement and apply speed
            if (moveDirection.lengthSquared() > 0) { // Check if there's any movement
                moveDirection.normalizeLocal().multLocal(movementSpeed * tpf);
                geom.move(moveDirection);
            }
        }
    }
}
