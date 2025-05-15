package ai_campus_life;

import java.util.ArrayList;
import java.util.Comparator;

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

       // === Jump-heavy Spiral Platforms ===
       /*  Vector3f center = new Vector3f(0, 0, 0);
        int numPlatforms = 25;
        float angleStep = FastMath.TWO_PI / 6f; // tighter rotation
        float radius = 6f;

        for (int i = 0; i < numPlatforms; i++) {
            float angle = i * angleStep;
            
            float height = i * 2.5f + FastMath.rand.nextFloat(); // high vertical gaps
            float x = center.x + FastMath.cos(angle) * radius + FastMath.rand.nextFloat() * 2f;
            float z = center.z + FastMath.sin(angle) * radius + FastMath.rand.nextFloat() * 2f;

            float size = 2f - (i * 0.05f); // gradually smaller platforms
            size = FastMath.clamp(size, 0.6f, 2f); // avoid going below 0.6
            Vector3f platformSize = new Vector3f(size, 0.3f, size);

            addObstacle(new Vector3f(x, height, z), platformSize);
        }*/

        // === Platforming Tower ===
       /*  int numPlatforms = 30;
        float baseHeight = 1f;
        float verticalGap = 2.8f;

        Vector3f lastPos = new Vector3f(0, baseHeight, 0);

        for (int i = 0; i < numPlatforms; i++) {
            // Random horizontal offset from previous platform
            float xOffset = FastMath.nextRandomFloat() * 6f - 3f;  // [-3, 3]
            float zOffset = FastMath.nextRandomFloat() * 6f - 3f;  // [-3, 3]
            float yOffset = verticalGap + FastMath.nextRandomFloat() * 1.2f;

            Vector3f newPos = lastPos.add(new Vector3f(xOffset, yOffset, zOffset));

            // Shrink platforms as player climbs
            float size = FastMath.clamp(2f - i * 0.05f, 0.7f, 2f);
            Vector3f platformSize = new Vector3f(size, 0.3f, size);

            addObstacle(newPos, platformSize);

            lastPos = newPos;
        }*/

        // === Scattered Vertical Platforming Playground ===
    /*  int numPlatforms = 40;
        float groundSize = 25f;  // Match your ground dimensions
        float maxHeight = 60f;
        float minHeight = 2f;

        for (int i = 0; i < numPlatforms; i++) {
            // Random horizontal position across full ground area
            float x = FastMath.nextRandomFloat() * groundSize * 2f - groundSize;
            float z = FastMath.nextRandomFloat() * groundSize * 2f - groundSize;

            // Upward trend with randomness
            float y = minHeight + (i * (maxHeight - minHeight) / numPlatforms)
                    + FastMath.nextRandomFloat() * 2f;

            // Some platforms larger, some smaller
            float size = FastMath.nextRandomFloat() * 1.5f + 0.5f; // [0.5, 2.0]
            Vector3f platformSize = new Vector3f(size, 0.3f, size);

            addObstacle(new Vector3f(x, y, z), platformSize);
        }*/

        // === Designed Parkour Path (Jumpable Chain) ===
/*      int numPlatforms = 40;
        float jumpRangeXZ = 5f; // Max horizontal jump reach
        float jumpHeight = 3.5f; // Max vertical jump reach
        float startY = 1.5f;

        Vector3f lastPos = new Vector3f(0, startY, 0);

        for (int i = 0; i < numPlatforms; i++) {
            // Random reachable offset
            float xOffset = FastMath.nextRandomFloat() * jumpRangeXZ * 2f - jumpRangeXZ;
            float zOffset = FastMath.nextRandomFloat() * jumpRangeXZ * 2f - jumpRangeXZ;
            float yOffset = FastMath.nextRandomFloat() * jumpHeight;

            Vector3f newPos = lastPos.add(new Vector3f(xOffset, yOffset, zOffset));

            // Random size: big early, small later
            float size = FastMath.clamp(2f - i * 0.04f + FastMath.nextRandomFloat() * 0.5f, 0.6f, 2f);
            Vector3f platformSize = new Vector3f(size, 0.3f, size);

            // === Colored platform ===
            Geometry platform = new Geometry("Platform" + i, new Box(platformSize.x, platformSize.y, platformSize.z));
            platform.setLocalTranslation(newPos);

            // Random color: green or orange
            ColorRGBA color = FastMath.nextRandomFloat() > 0.5f ? ColorRGBA.Orange : ColorRGBA.Green;
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", color);
            platform.setMaterial(mat);

            // Add physics
            RigidBodyControl control = new RigidBodyControl(new BoxCollisionShape(platformSize), 0);
            platform.addControl(control);
            bulletAppState.getPhysicsSpace().add(control);

            rootNode.attachChild(platform);

            lastPos = newPos;
        } */

        // === True 3D Platforming Playground ===
        /* 
        int numPlatforms = 50;
        float groundSize = 25f;
        float maxJumpXZ = 6f;
        float maxJumpY = 4f;

        Vector3f currentPos = new Vector3f(0, 2f, 0); // Start above ground

        for (int i = 0; i < numPlatforms; i++) {
            // Random jumpable offset
            float xOffset = FastMath.nextRandomFloat() * maxJumpXZ * 2f - maxJumpXZ;
            float zOffset = FastMath.nextRandomFloat() * maxJumpXZ * 2f - maxJumpXZ;
            float yOffset = FastMath.nextRandomFloat() * maxJumpY * 0.7f + 2f; // Always go up at least 2

            Vector3f nextPos = currentPos.add(new Vector3f(xOffset, yOffset, zOffset));

            // Make sure it stays over ground area
            nextPos.x = FastMath.clamp(nextPos.x, -groundSize + 3f, groundSize - 3f);
            nextPos.z = FastMath.clamp(nextPos.z, -groundSize + 3f, groundSize - 3f);

            float size = FastMath.clamp(2f - i * 0.035f + FastMath.nextRandomFloat(), 0.6f, 2.2f);
            Vector3f platformSize = new Vector3f(size, 0.3f, size);

            // Create platform
            Geometry platform = new Geometry("Platform" + i, new Box(platformSize.x, platformSize.y, platformSize.z));
            platform.setLocalTranslation(nextPos);

            // Alternate colors
            ColorRGBA color = (i % 2 == 0) ? ColorRGBA.Orange : ColorRGBA.Green;
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", color);
            platform.setMaterial(mat);

            RigidBodyControl control = new RigidBodyControl(new BoxCollisionShape(platformSize), 0);
            platform.addControl(control);
            bulletAppState.getPhysicsSpace().add(control);
            rootNode.attachChild(platform);

            currentPos = nextPos; // move to next start point
        }*/

        // === Scattered + Reachable + Climbable Platforms ===
        // === Scattered + Reachable + Climbable Platforms (Improved) ===
        /* 
        int numPlatforms = 50;
        float groundSize = 25f;
        float minY = 2f;
        float maxY = 60f;
        float maxJumpXZ = 6f;
        float maxJumpY = 4.5f;
        float minHorizontalDistance = 3f;
        float minVerticalDistance = 2.2f;

        ArrayList<Vector3f> scatteredPositions = new ArrayList<>();
        int attempts = 0;
        int maxAttempts = 2000;

        // Step 1: Generate valid scattered positions with spacing
        while (scatteredPositions.size() < numPlatforms && attempts < maxAttempts) {
            attempts++;

            float x = FastMath.nextRandomFloat() * groundSize * 2f - groundSize;
            float z = FastMath.nextRandomFloat() * groundSize * 2f - groundSize;
            float y = FastMath.nextRandomFloat() * (maxY - minY) + minY;
            Vector3f candidate = new Vector3f(x, y, z);

            boolean valid = true;
            for (Vector3f other : scatteredPositions) {
                float horizDist = new Vector2f(candidate.x, candidate.z).distance(new Vector2f(other.x, other.z));
                float vertDist = Math.abs(candidate.y - other.y);
                if (horizDist < minHorizontalDistance && vertDist < minVerticalDistance) {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                scatteredPositions.add(candidate);
            }
        }

        // Step 2: Sort by height (Y) so climbing goes up
        scatteredPositions.sort(Comparator.comparingDouble(v -> v.y));

        // Step 3: Build reachable platform chain
        for (int i = 0; i < scatteredPositions.size(); i++) {
            Vector3f pos = scatteredPositions.get(i);

            if (i > 0) {
                Vector3f last = scatteredPositions.get(i - 1);
                Vector3f diff = pos.subtract(last);

                float dx = FastMath.clamp(diff.x, -maxJumpXZ, maxJumpXZ);
                float dz = FastMath.clamp(diff.z, -maxJumpXZ, maxJumpXZ);
                float dy = FastMath.clamp(diff.y, 1f, maxJumpY);

                pos = last.add(new Vector3f(dx, dy, dz));
                scatteredPositions.set(i, pos);
            }

            float size = FastMath.clamp(2f - i * 0.03f + FastMath.nextRandomFloat(), 0.6f, 2.5f);
            Vector3f platformSize = new Vector3f(size, 0.3f, size);

            Geometry platform = new Geometry("Platform" + i, new Box(platformSize.x, platformSize.y, platformSize.z));
            platform.setLocalTranslation(pos);

            ColorRGBA color = (i % 2 == 0) ? ColorRGBA.Green : ColorRGBA.fromRGBA255(211, 84, 0, 0); // Your orange
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", color);
            platform.setMaterial(mat);

            RigidBodyControl control = new RigidBodyControl(new BoxCollisionShape(platformSize), 0);
            platform.addControl(control);
            bulletAppState.getPhysicsSpace().add(control);
            rootNode.attachChild(platform);
        }*/

        ArrayList<Vector3f> manualPositions = getManualPlatformPositions();

        for (int i = 0; i < manualPositions.size(); i++) {
            Vector3f pos = manualPositions.get(i);
            float size = FastMath.clamp(2f - i * 0.03f + FastMath.nextRandomFloat(), 0.7f, 2.5f);
            addObstacle(pos, new Vector3f(size, 0.3f, size));
        }






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

    private ArrayList<Vector3f> getManualPlatformPositions() {
        ArrayList<Vector3f> list = new ArrayList<>();
        list.add(new Vector3f(8, 2, 0));
        list.add(new Vector3f(3, 5, 2));
        // ... Add up to 50
        return list;
    }
    
    
}