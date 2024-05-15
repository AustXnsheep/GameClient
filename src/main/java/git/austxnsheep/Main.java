package git.austxnsheep;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import git.austxnsheep.network.GameClient;
import git.austxnsheep.network.listeners.ioprocessors.IOListeners;
import git.austxnsheep.network.packets.post.JoinPacket;
import git.austxnsheep.network.packets.post.PlayerUpdatePacket;
import git.austxnsheep.network.packets.requests.WorldRequestPacket;
import git.austxnsheep.vr.VRManager;
import git.austxnsheep.vr.types.EyePositions;
import git.austxnsheep.worlddata.World;
import git.austxnsheep.worlddata.genericclasses.Location;
import git.austxnsheep.worlddata.particles.waterphysics.WaterPhysics;
import org.lwjgl.openvr.Texture;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRCompositor;

import java.util.logging.Logger;

import static git.austxnsheep.network.listeners.ioprocessors.IOListeners.handleInput;
import static org.lwjgl.openvr.VR.*;

public class Main extends ApplicationAdapter implements WaterPhysics {
    public static PerspectiveCamera camera;
    public static PerspectiveCamera rightEye;
    public static PerspectiveCamera leftEye;
    public static AssetManager assetManager = new AssetManager();
    public static Location location = new Location(new Vector3(0, 0, 0), new Quaternion());
    public static String assetRoot = "C:/Users/Nicholas/Desktop/Ripple Plans/Default Project Directory/Assets/";
    private long lastTimeChecked = 0;
    private ModelBatch modelBatch;
    private Environment environment;
    private CameraInputController camController;
    private FrameBuffer leftEyeFBO;
    private FrameBuffer rightEyeFBO;
    public static boolean developerMode = true;
    static Logger logger = Logger.getLogger(Main.class.getName());
    public static GameClient client;

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        new Lwjgl3Application(new Main(), config);
    }

    public static Logger getLogger() {
        return logger;
    }
    @Override
    public void create() {
        setup();
        client = new GameClient();
        client.connect("127.0.0.1", 54555, 54777);
        if (!developerMode) {
            Main.getLogger().info("VR_IsRuntimeInstalled = " + VR_IsRuntimeInstalled());
            Main.getLogger().info("VR_RuntimePath = " + VR_RuntimePath());
            Main.getLogger().info("VR_IsHmdPresent = " + VR_IsHmdPresent());
            Main.getLogger().info("Initializing VR...");
            VRManager.initializeOpenVR();
            try {
                leftEyeFBO = new FrameBuffer(Pixmap.Format.RGBA8888, VRManager.recommendedWidth, VRManager.recommendedHeight, true);
                rightEyeFBO = new FrameBuffer(Pixmap.Format.RGBA8888, VRManager.recommendedWidth, VRManager.recommendedHeight, true);
            } catch (IllegalStateException e) {
                Gdx.app.error("FrameBuffer", "Error creating frame buffer", e);
            }
        }
        modelBatch = new ModelBatch();
        if (developerMode) {
            Main.getLogger().info("Setting up Camera");
            camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.position.set(10f, 10f, 10f);
            camera.lookAt(0, 0, 0);
            camera.near = 0.1f;
            camera.far = 300.0f;
            camera.update();
        } else {
            Main.getLogger().info("Setting up VR Camera");
            leftEye = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            leftEye.lookAt(0, 0, 0);
            leftEye.update();

            rightEye = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            rightEye.lookAt(0, 0, 0);
            rightEye.update();
        }

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);

        Gdx.input.setCursorCatched(true);
        IOListeners ioListeners = new IOListeners();
        Gdx.input.setInputProcessor(ioListeners);

        environment = new Environment();
        // Setup ambient light
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f)); // Soft white light

        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f)); // Adjust color and direction as needed

        client.sendData(new WorldRequestPacket());
        client.sendData(new JoinPacket());
    }
    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (System.currentTimeMillis() - lastTimeChecked > 1000) { // Check every second
            Gdx.app.log("FPS", "Current FPS: " + Gdx.graphics.getFramesPerSecond());
            lastTimeChecked = System.currentTimeMillis();
            client.sendData(new PlayerUpdatePacket(camera.position));
        }
        if (!developerMode) {
            // Assuming getHeadsetPos updates the passed Vector3 with the headset's position
            Matrix4 headsetPos = VRManager.getHeadsetPos(); // Update headset position

            EyePositions eyePositions = VRManager.getEyePositions(headsetPos); // Get eye positions based on headset position

            // Update camera positions for left and right eyes
            leftEye.position.set(eyePositions.leftEyePosition);
            leftEye.update();

            rightEye.position.set(eyePositions.rightEyePosition);
            rightEye.update();
            leftEyeFBO.begin();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); // Clear framebuffer
            leftEye.position.set(eyePositions.leftEyePosition);
            leftEye.update();
            leftEyeFBO.end();

            // Render to the right eye's framebuffer
            rightEyeFBO.begin();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); // Clear framebuffer
            rightEye.position.set(eyePositions.rightEyePosition);
            rightEye.update();
            rightEyeFBO.end();

            // Submit Framebuffers to VR Compositor
            com.badlogic.gdx.graphics.Texture leftEyeTexture = leftEyeFBO.getColorBufferTexture();
            com.badlogic.gdx.graphics.Texture rightEyeTexture = rightEyeFBO.getColorBufferTexture();
            submitTextures(leftEyeTexture, rightEyeTexture);
        } else {
            // Update the camera
            handleInput(Gdx.graphics.getDeltaTime());
            camera.update();
            camController.update();
        }

        // Render instances with the main camera
        if (!developerMode) {
            modelBatch.begin(leftEye);
            for (ModelInstance instance : World.staticInstances) {
                modelBatch.render(instance, environment);
            }
            for (ModelInstance instance : World.dynamicInstances) {
                modelBatch.render(instance, environment);
            }
            modelBatch.end();
            modelBatch.begin(rightEye);
            for (ModelInstance instance : World.staticInstances) {
                modelBatch.render(instance, environment);
            }
            for (ModelInstance instance : World.dynamicInstances) {
                modelBatch.render(instance, environment);
            }
            modelBatch.end();
        } else {
            modelBatch.begin(camera);
            for (ModelInstance instance : World.staticInstances) {
                modelBatch.render(instance, environment);
            }
            for (ModelInstance instance : World.dynamicInstances) {
                modelBatch.render(instance, environment);
            }
            modelBatch.end();
        }
    }
    @Override
    public void dispose() {
        modelBatch.dispose();
        if (VRManager.vrInitialized) {
            VRManager.shutdown();
        }
    }
    private void submitTextures(com.badlogic.gdx.graphics.Texture leftEyeGdxTexture, com.badlogic.gdx.graphics.Texture rightEyeGdxTexture) {
        System.out.println("Submitting Textures to VR");

        // Ensure VR Compositor is running and in fullscreen
        if (!VRCompositor.VRCompositor_IsFullscreen()) {
            System.err.println("VR Compositor is not running in full screen or is not active.");
            return;
        }

        // Get the OpenGL texture handle from LibGDX textures
        int leftEyeTextureHandle = leftEyeGdxTexture.getTextureObjectHandle();
        int rightEyeTextureHandle = rightEyeGdxTexture.getTextureObjectHandle();

        // Create LWJGL OpenVR Texture instances
        Texture leftEyeTexture = Texture.create();
        leftEyeTexture.handle(leftEyeTextureHandle);
        leftEyeTexture.eType(VR.ETextureType_TextureType_OpenGL);
        leftEyeTexture.eColorSpace(VR.EColorSpace_ColorSpace_Auto);

        Texture rightEyeTexture = Texture.create();
        rightEyeTexture.handle(rightEyeTextureHandle);
        rightEyeTexture.eType(VR.ETextureType_TextureType_OpenGL);
        rightEyeTexture.eColorSpace(VR.EColorSpace_ColorSpace_Auto);

        // Submit the textures to the VR compositor
        int submitResultleft = VRCompositor.VRCompositor_Submit(VR.EVREye_Eye_Left, leftEyeTexture, null, VR.EVRSubmitFlags_Submit_Default);
        int submitResultright = VRCompositor.VRCompositor_Submit(VR.EVREye_Eye_Right, rightEyeTexture, null, VR.EVRSubmitFlags_Submit_Default);

        if (submitResultleft != VR.EVRCompositorError_VRCompositorError_None) {
            System.err.println("Failed to submit texture: " + VR.VR_GetVRInitErrorAsEnglishDescription(submitResultleft));
        }
        if (submitResultright != VR.EVRCompositorError_VRCompositorError_None) {
            System.err.println("Failed to submit texture: " + VR.VR_GetVRInitErrorAsEnglishDescription(submitResultright));
        }
    }

    public static void setup() {
        getLogger().info("Loading assets...");
        // Load Models
        //assetManager.load(new AssetDescriptor<>("C:/Users/Nicholas/Desktop/Ripple Plans/Default Project Directory/Assets/Models/sword.G3DJ", Model.class));

        // Load sounds
        assetManager.load(new AssetDescriptor<>("C:/Users/Nicholas/Desktop/Ripple Plans/Default Project Directory/Assets/Sounds/sunflower_tehee.mp3", Sound.class));

        // Load textures

        // Load others

        assetManager.finishLoading();
    }
}