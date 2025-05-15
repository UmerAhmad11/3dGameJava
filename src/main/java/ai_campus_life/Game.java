package ai_campus_life;

//Import Libraries
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
//import com.jme3.scene.shape.RectangleMesh;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.bullet.collision.shapes.CollisionShape;

//Main -> Simple Application which uses ActionListener and Bullet Physics
public class Game extends SimpleApplication implements ActionListener {
    private Geometry cubeGeometry;
    private Node cubeNode;
    private Vector3f cameraOffset;

    private BulletAppState bulletAppState;
    private RigidBodyControl cubePhysicsControl;

    // Movement flags
    private boolean moveForward, moveBackward, moveLeft, moveRight;
    private Vector3f walkDirection = new Vector3f();
    private float moveSpeed = 10f; // Horizontal speed of the cube
    private float jumpForce = 15f; // Upward force for jumping

    private static final String MOVE_CUBE_FORWARD = "MoveCubeForward";
    private static final String MOVE_CUBE_BACKWARD = "MoveCubeBackward";
    private static final String MOVE_CUBE_LEFT = "MoveCubeLeft";
    private static final String MOVE_CUBE_RIGHT = "MoveCubeRight";
    private static final String JUMP_CUBE = "JumpCube";

    private int jumpCount = 0;
    private final int maxJumps = 2;
    private final float groundThreshold = 0.01f; // Tune based on your physics
    private boolean wasInAir = false;


    public static void main(String[] args) {
        Game app = new Game();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Game Window");
        settings.setResolution(1280, 720);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(false);

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        // bulletAppState.setDebugEnabled(true);

        // === Skybox === USING OLD SKYBOX DDS
        //rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/skybox.dds", SkyFactory.EnvMapType.CubeMap));

        Texture skyTexture = assetManager.loadTexture("Textures/Sky/bluesky.png");

        // === Skybox === USINNG PNG
        Spatial sky = SkyFactory.createSky(
            assetManager,
            skyTexture, // right
            skyTexture, // left
            skyTexture, // top
            skyTexture, // bottom
            skyTexture, // back
            skyTexture  // front
        );
        rootNode.attachChild(sky);

        // === Create cube ===
        Box b = new Box(1, 1, 1);
        cubeGeometry = new Geometry("Cube", b);
        Material cubeMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        cubeMat.setColor("Color", ColorRGBA.Magenta);
        cubeGeometry.setMaterial(cubeMat);

        cubeNode = new Node("Cube Node");
        cubeNode.attachChild(cubeGeometry);
        rootNode.attachChild(cubeNode);

        CollisionShape cubeShape = new BoxCollisionShape(new Vector3f(1, 1, 1));
        cubePhysicsControl = new RigidBodyControl(cubeShape, 1f);
        cubeNode.addControl(cubePhysicsControl);
        bulletAppState.getPhysicsSpace().add(cubePhysicsControl);
        cubePhysicsControl.setGravity(new Vector3f(0, -30f, 0));
        cubePhysicsControl.setPhysicsLocation(new Vector3f(0, 2f, 0));

        // === Create ground ===
        Box groundBox = new Box(25f, 0.1f, 25f);
        Geometry groundGeometry = new Geometry("Ground", groundBox);
        groundGeometry.setLocalTranslation(0, -1.1f, 0);

        Texture dirtTexture = assetManager.loadTexture("Textures/dirt.png");
        dirtTexture.setWrap(Texture.WrapMode.Repeat);

        Material groundMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        groundMat.setTexture("ColorMap", dirtTexture);
        groundGeometry.setMaterial(groundMat);
        groundGeometry.getMesh().scaleTextureCoordinates(new Vector2f(25f, 25f));

        rootNode.attachChild(groundGeometry);

        CollisionShape groundShape = new BoxCollisionShape(new Vector3f(25f, 0.1f, 25f));
        RigidBodyControl groundPhysicsControl = new RigidBodyControl(groundShape, 0);
        groundPhysicsControl.setPhysicsLocation(groundGeometry.getLocalTranslation());
        groundGeometry.addControl(groundPhysicsControl);
        bulletAppState.getPhysicsSpace().add(groundPhysicsControl);

        // === Directional Light ===
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        // === Obstacles ===
        addObstacle(new Vector3f(5, 0, 5), new Vector3f(2, 0.5f, 2));
        addObstacle(new Vector3f(-6, 1, -4), new Vector3f(1, 1, 1));
        addObstacle(new Vector3f(0, 0.3f, -6), new Vector3f(3, 0.3f, 1.5f));

        // === Camera ===
        cameraOffset = new Vector3f(0, 4f, 8f); // This can still influence ChaseCamera defaults
        ChaseCamera chaseCam = new ChaseCamera(cam, cubeNode, inputManager);
        chaseCam.setDefaultDistance(cameraOffset.length());
        chaseCam.setDefaultVerticalRotation((float) Math.toRadians(20));
        chaseCam.setMinVerticalRotation(-FastMath.HALF_PI);
        chaseCam.setMaxVerticalRotation(FastMath.HALF_PI);
        chaseCam.setRotationSpeed(3f);

        setupKeys();
    }


    private void setupKeys() {
        inputManager.addMapping(MOVE_CUBE_FORWARD, new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(MOVE_CUBE_BACKWARD, new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(MOVE_CUBE_LEFT, new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(MOVE_CUBE_RIGHT, new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(JUMP_CUBE, new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(this, MOVE_CUBE_FORWARD, MOVE_CUBE_BACKWARD, MOVE_CUBE_LEFT, MOVE_CUBE_RIGHT, JUMP_CUBE);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        
        if (name.equals(MOVE_CUBE_FORWARD)) {
            moveForward = isPressed;
        } else if (name.equals(MOVE_CUBE_BACKWARD)) {
            moveBackward = isPressed;
        } else if (name.equals(MOVE_CUBE_LEFT)) {
            moveLeft = isPressed;
        } else if (name.equals(MOVE_CUBE_RIGHT)) {
            moveRight = isPressed;
        } else if (name.equals(JUMP_CUBE) && isPressed && jumpCount < maxJumps) {
            // Basic jump: Apply an upward impulse.
            // For a more robust jump, you'd add a check to see if the cube is on the ground.
            // For example: if (isOnGround()) { cubePhysicsControl.applyCentralImpulse(new Vector3f(0, jumpForce, 0)); }
            // Checking isOnGround() can be done by a short raycast downwards or checking if vertical velocity is near zero.
            // For now, we'll allow jumping anytime for simplicity.
            cubePhysicsControl.applyImpulse(new Vector3f(0, jumpForce, 0), Vector3f.ZERO);
            jumpCount++;
            
            
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);

        // Player movement logic
        Vector3f camDir = cam.getDirection().clone();
        camDir.y = 0; // Movement is horizontal
        if (camDir.lengthSquared() > 0) {
            camDir.normalizeLocal();
        } else {
            camDir.set(Vector3f.UNIT_Z); // Default forward if camera is straight up/down
        }

        Vector3f camLeft = cam.getLeft().clone();
        camLeft.y = 0; // Movement is horizontal
         if (camLeft.lengthSquared() > 0) {
            camLeft.normalizeLocal();
        } else {
            camLeft.set(Vector3f.UNIT_X.negate()); // Default left
        }

        walkDirection.set(0, 0, 0);
        if (moveForward) {
            walkDirection.addLocal(camDir);
        }
        if (moveBackward) {
            walkDirection.addLocal(camDir.negate());
        }
        if (moveLeft) {
            walkDirection.addLocal(camLeft);
        }
        if (moveRight) {
            walkDirection.addLocal(camLeft.negate());
        }

        Vector3f currentPhysicsVelocity = cubePhysicsControl.getLinearVelocity();
        if (walkDirection.lengthSquared() > 0) {
            walkDirection.normalizeLocal().multLocal(moveSpeed); // Scale to desired speed
            // Apply horizontal movement, preserve existing vertical velocity (for gravity/jumping)
            cubePhysicsControl.setLinearVelocity(new Vector3f(walkDirection.x, currentPhysicsVelocity.y, walkDirection.z));
        } else {
            // No input, stop horizontal movement, preserve vertical velocity
            cubePhysicsControl.setLinearVelocity(new Vector3f(0, currentPhysicsVelocity.y, 0));
        }

        // Camera follow logic
        if (cubeNode != null && cam != null && cameraOffset != null) {
            // cubeNode's world translation is now updated by the physics system
            Vector3f cubePosition = cubeNode.getWorldTranslation(); 
            Vector3f desiredCamLocation = cubePosition.add(cameraOffset);
            cam.setLocation(desiredCamLocation);
            cam.lookAt(cubePosition, Vector3f.UNIT_Y);
        }

        float verticalVelocity = cubePhysicsControl.getLinearVelocity().y;
        boolean isOnGround = Math.abs(verticalVelocity) < groundThreshold;

        // Detect the transition: in air → on ground
        if (isOnGround && wasInAir) {
            jumpCount = 0;  // Reset only when it lands
        }

        // Update wasInAir flag
        wasInAir = !isOnGround;
    }

    private void addObstacle(Vector3f position, Vector3f halfExtents) {
        Box box = new Box(halfExtents.x, halfExtents.y, halfExtents.z);
        Geometry obstacle = new Geometry("Obstacle", box);
        obstacle.setLocalTranslation(position);
    
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);
        obstacle.setMaterial(mat);
    
        CollisionShape shape = new BoxCollisionShape(halfExtents);
        RigidBodyControl control = new RigidBodyControl(shape, 0); // static
        obstacle.addControl(control);
        bulletAppState.getPhysicsSpace().add(control);
    
        rootNode.attachChild(obstacle);
    }
    
}