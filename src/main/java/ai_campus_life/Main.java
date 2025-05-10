package ai_campus_life;

//Import Libraries
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

//Main -> Simple Application which uses Analog Listener
public class Main extends SimpleApplication implements AnalogListener{
    private Geometry geom;
    private Node cubeNode;
    private Vector3f cameraOffset;

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

        Box b = new Box(1, 1, 1);
        geom = new Geometry("Cube", b);
        geom.setLocalTranslation(0, 0, 0);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);

        cubeNode = new Node("Cube Node");
        cubeNode.attachChild(geom);
        rootNode.attachChild(cubeNode);

        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/dirt.png"));
        Geometry ground = new Geometry("ground", new RectangleMesh(
                new Vector3f(-25, -1, 25),
                new Vector3f(25, -1, 25),
                new Vector3f(-25, -1, -25)));
        ground.setMaterial(mat);
        rootNode.attachChild(ground);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        cameraOffset = new Vector3f(0, 4f, 8f);
        Vector3f initialCubePosition = cubeNode.getWorldTranslation();
        cam.setLocation(initialCubePosition.add(cameraOffset));
        cam.lookAt(initialCubePosition, Vector3f.UNIT_Y);
        initKeys();
    }

    private void initKeys() {
        inputManager.addMapping(MOVE_CUBE_FORWARD, new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(MOVE_CUBE_BACKWARD, new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(MOVE_CUBE_LEFT, new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(MOVE_CUBE_RIGHT, new KeyTrigger(KeyInput.KEY_RIGHT));

        inputManager.addListener(this, MOVE_CUBE_FORWARD, MOVE_CUBE_BACKWARD, MOVE_CUBE_LEFT, MOVE_CUBE_RIGHT);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        float moveSpeed = 20f;

        Vector3f camForward = cam.getDirection().clone();
        camForward.y = 0;
        if (camForward.lengthSquared() > 0) {
            camForward.normalizeLocal();
        }

        Vector3f camStrafeLeft = cam.getLeft().clone();
        camStrafeLeft.y = 0;
        if (camStrafeLeft.lengthSquared() > 0) {
            camStrafeLeft.normalizeLocal();
        }

        camForward.multLocal(moveSpeed * tpf);
        camStrafeLeft.multLocal(moveSpeed * tpf);

        if (name.equals(MOVE_CUBE_FORWARD)) {
            cubeNode.move(camForward);
        }
        if (name.equals(MOVE_CUBE_BACKWARD)) {
            cubeNode.move(camForward.negate());
        }
        if (name.equals(MOVE_CUBE_RIGHT)) {
            cubeNode.move(camStrafeLeft.negate());
        }
        if (name.equals(MOVE_CUBE_LEFT)) {
            cubeNode.move(camStrafeLeft);
        }
    }
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        if (cubeNode != null && cam != null && cameraOffset != null) {
            Vector3f cubePosition = cubeNode.getWorldTranslation();

            Vector3f desiredCamLocation = cubePosition.add(cameraOffset);

            cam.setLocation(desiredCamLocation);
            cam.lookAt(cubePosition, Vector3f.UNIT_Y);
        }
    }

}