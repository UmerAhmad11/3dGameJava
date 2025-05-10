package ai_campus_life;

//Libraries
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.RectangleMesh;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;

import com.jme3.scene.Node;



public class Main extends SimpleApplication implements AnalogListener{
    private Geometry geom; // The cube, now a class field
    // final private Vector3f direction = new Vector3f(); // This field was unused
    private Node cubeNode;
    private Vector3f cameraOffset;

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
        cubeNode = new Node("Cube Node");
        cubeNode.attachChild(geom);
        rootNode.attachChild(cubeNode);

        //Create a floor
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        // mat.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg")); // Comment out or remove
        mat.setColor("Color", ColorRGBA.Red); // Set a color for the ground
        Geometry ground = new Geometry("ground", new RectangleMesh(
                new Vector3f(-25, -1, 25),
                new Vector3f(25, -1, 25),
                new Vector3f(-25, -1, -25)));
        ground.setMaterial(mat);
        rootNode.attachChild(ground);

        // Add a basic directional light
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        // Initialize camera position and offset
        // The offset determines how far behind and above the cube the camera will be.
        // (x, y, z) -> (0 means centered horizontally, positive y is above, positive z is behind)
        cameraOffset = new Vector3f(0, 4f, 8f); // Adjust these values to your liking
        Vector3f initialCubePosition = cubeNode.getWorldTranslation();
        cam.setLocation(initialCubePosition.add(cameraOffset)); // Set initial camera position
        cam.lookAt(initialCubePosition, Vector3f.UNIT_Y); // Look at the cube, Y is typically up

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
        inputManager.addListener(this, MOVE_CUBE_FORWARD, MOVE_CUBE_BACKWARD, MOVE_CUBE_LEFT, MOVE_CUBE_RIGHT);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        float moveSpeed = 20f; // Define movement speed

        // Calculate forward/backward movement vector based on camera's direction, projected onto XZ plane
        Vector3f camForward = cam.getDirection().clone();
        camForward.y = 0; // Project onto the horizontal plane (ignore vertical component)
        if (camForward.lengthSquared() > 0) { // Avoid normalizing a zero vector (if camera looks straight up/down)
            camForward.normalizeLocal(); // Ensure consistent speed regardless of camera pitch
        }

        // Calculate left/right strafe movement vector based on camera's left vector, projected onto XZ plane
        Vector3f camStrafeLeft = cam.getLeft().clone();
        camStrafeLeft.y = 0; // Project onto the horizontal plane
        if (camStrafeLeft.lengthSquared() > 0) { // Avoid normalizing a zero vector
            camStrafeLeft.normalizeLocal(); // Ensure consistent speed
        }

        // Apply speed and time-per-frame scaling
        camForward.multLocal(moveSpeed * tpf);
        camStrafeLeft.multLocal(moveSpeed * tpf);

        if (name.equals(MOVE_CUBE_FORWARD)) {
            cubeNode.move(camForward);
        }
        if (name.equals(MOVE_CUBE_BACKWARD)) {
            cubeNode.move(camForward.negate()); // Move in the opposite direction
        }
        if (name.equals(MOVE_CUBE_RIGHT)) {
            // cam.getLeft() points to the camera's left, so negate for rightward movement relative to camera
            cubeNode.move(camStrafeLeft.negate());
        }
        if (name.equals(MOVE_CUBE_LEFT)) {
            cubeNode.move(camStrafeLeft);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf); // Call super's simpleUpdate

        // Update camera position to follow the cube
        if (cubeNode != null && cam != null && cameraOffset != null) {
            Vector3f cubePosition = cubeNode.getWorldTranslation();

            // Calculate the desired camera position by adding the offset to the cube's current position
            Vector3f desiredCamLocation = cubePosition.add(cameraOffset);

            cam.setLocation(desiredCamLocation);
            cam.lookAt(cubePosition, Vector3f.UNIT_Y); // Always look at the cube
        }
    }

}