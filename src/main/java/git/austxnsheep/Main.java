package git.austxnsheep;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import git.austxnsheep.network.GameClient;
import git.austxnsheep.network.listeners.ClientListener;
import git.austxnsheep.network.listeners.ioprocessors.IOListeners;
import git.austxnsheep.network.packets.post.JoinPacket;
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
    public static OrthographicCamera guiCamera;
    public static PerspectiveCamera rightEye;
    public static PerspectiveCamera leftEye;
    public static AssetManager assetManager = new AssetManager();
    public static Location location = new Location(new Vector3(0, 0, 0), new Quaternion());
    public static String assetRoot = "C:/Users/Nicholas/Desktop/Ripple Plans/Default Project Directory/Assets/";
    private ModelBatch modelBatch;
    private SpriteBatch fontBatch;
    private Environment environment;
    private CameraInputController camController;
    private FrameBuffer leftEyeFBO;
    private FrameBuffer rightEyeFBO;
    private BitmapFont font;
    public static boolean developerMode = true;
    public static boolean isConnectedToServer = false;
    public static String serverIP;
    public static String softwareType;
    static Logger logger = Logger.getLogger(Main.class.getName());
    public static GameClient client;

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        new Lwjgl3Application(new Main(), config);
    }

    public static Logger getLogger() {
        return logger;
    }
    @Override
    public void create() {
        setup();

        // Initialize client and connect
        client = new GameClient();
        client.connect("127.0.0.1", 54778, 54779);

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
                Gdx.app.error("FrameBuffer", "Error creating frame buffer: " + e.getMessage(), e);
            }

            Main.getLogger().info("Setting up VR Cameras");
            leftEye = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            rightEye = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            setupCamera(leftEye, Vector3.Zero, 0.1f, 300.0f);
            setupCamera(rightEye, Vector3.Zero, 0.1f, 300.0f);
        } else {
            Main.getLogger().info("Setting up Developer Camera");
            camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            setupCamera(camera, new Vector3(10f, 10f, 10f), 0.1f, 300.0f);
        }

        setupUICamera();
        setupEnvironment();
        setupInput();

        // Initialize font and batch
        font = new BitmapFont();
        fontBatch = new SpriteBatch();

        // Send initial packets to the server
        client.sendData(new WorldRequestPacket());
        client.sendData(new JoinPacket());
    }
    @Override
    public void render() {
        modelBatch = new ModelBatch();
        // Clear the screen once at the beginning of the frame
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (!developerMode) {
            // VR Mode
            Matrix4 headsetPos = VRManager.getHeadsetPos(); // Update headset position
            EyePositions eyePositions = VRManager.getEyePositions(headsetPos); // Get eye positions

            updateCamera(leftEye, eyePositions.leftEyePosition);
            updateCamera(rightEye, eyePositions.rightEyePosition);

            renderEye(leftEye, leftEyeFBO);
            renderEye(rightEye, rightEyeFBO);

            // Submit Framebuffers to VR Compositor
            submitTextures(leftEyeFBO.getColorBufferTexture(), rightEyeFBO.getColorBufferTexture());
        } else {
            // Developer Mode
            handleInput(Gdx.graphics.getDeltaTime());
            camera.update();
            guiCamera.update();
            fontBatch.setProjectionMatrix(guiCamera.combined);

            renderModelBatch(camera); // Render 3D content with the main camera

            if (Settings.debugMode) {
                renderDebugInformation(); // Render debug information if in debug mode
            }
        }
    }
    @Override
    public void dispose() {
        modelBatch.dispose();
        ClientListener.shutdownScheduler();
        if (VRManager.vrInitialized) {
            VRManager.shutdown();
        }
    }
    @Override
    public void resize(int width, int height) {
        // Update the camera with the new window size
        guiCamera.setToOrtho(false, width, height);
        guiCamera.update();

        // Ensure the projection matrix for the batch is also updated
        fontBatch.setProjectionMatrix(guiCamera.combined);
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
    private void setupCamera(PerspectiveCamera cam, Vector3 position, float near, float far) {
        cam.position.set(position);
        cam.lookAt(0, 0, 0);
        cam.near = near;
        cam.far = far;
        cam.update();
    }
    private void setupUICamera() {
        Main.getLogger().info("Setting up UI Camera");
        guiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        guiCamera.setToOrtho(false);
        guiCamera.update();
    }
    private void setupInput() {
        Main.getLogger().info("Setting up UI IOstream");

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);
        Gdx.input.setCursorCatched(true);

        IOListeners ioListeners = new IOListeners();
        Gdx.input.setInputProcessor(ioListeners);  // Single input processor assignment
    }

    private void setupEnvironment() {
        Main.getLogger().info("Setting up Environment");
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }
    public static void setup() {
        getLogger().info("Loading assets...");
        // Load Models
        //assetManager.load(new AssetDescriptor<>("C:/Users/Nicholas/Desktop/Ripple Plans/Default Project Directory/Assets/Models/sword.G3DJ", Model.class));

        // Load sounds
        assetManager.load(new AssetDescriptor<>("C:/Users/Nicholas/Desktop/Ripple Plans/Default Project Directory/Assets/Sounds/sunflower_tehee.mp3", Sound.class));

        // Load textures
        assetManager.load(new AssetDescriptor<>("C:/Users/Nicholas/Desktop/Ripple Plans/Default Project Directory/Assets/Textures/scary.png", com.badlogic.gdx.graphics.Texture.class));
        // Load others

        assetManager.finishLoading();
    }
    private void updateCamera(Camera eye, Vector3 position) {
        eye.position.set(position);
        eye.update();
    }

    private void renderEye(Camera eye, FrameBuffer fbo) {
        fbo.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(eye);
        renderModels(); // Render both static and dynamic instances
        modelBatch.end();
        fbo.end();
    }

    private void renderModels() {
        for (ModelInstance instance : World.staticInstances) {
            modelBatch.render(instance, environment);
        }
        for (ModelInstance instance : World.dynamicInstances) {
            modelBatch.render(instance, environment);
        }
    }

    private void renderModelBatch(Camera camera) {
        modelBatch.begin(camera);
        renderModels();
        modelBatch.end();
    }

    private void renderDebugInformation() {
        fontBatch.begin();
        font.draw(fontBatch, "CLIENT INFO", 20, Gdx.graphics.getHeight() - 20);
        font.draw(fontBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, Gdx.graphics.getHeight() - 40);
        font.draw(fontBatch, "Camera Position - X: " + camera.position.x + " Y: " + camera.position.y + " Z: " + camera.position.z, 20, Gdx.graphics.getHeight() - 60);
        font.draw(fontBatch, "Camera Direction - X: " + camera.direction.x + " Y: " + camera.direction.y + " Z: " + camera.direction.z, 20, Gdx.graphics.getHeight() - 80);
        font.draw(fontBatch, "FOV: " + camera.fieldOfView, 20, Gdx.graphics.getHeight() - 100);
        font.draw(fontBatch, "Delta Time: " + Gdx.graphics.getDeltaTime(), 20, Gdx.graphics.getHeight() - 120);
        long totalMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;
        font.draw(fontBatch, "Memory (MB) - Used: " + usedMemory + " Free: " + freeMemory + " Total: " + totalMemory, 20, Gdx.graphics.getHeight() - 140);
        float aspectRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        font.draw(fontBatch, "Aspect Ratio: " + aspectRatio, 20, Gdx.graphics.getHeight() - 160);
        font.draw(fontBatch, "Render Time (ms): " + Gdx.graphics.getRawDeltaTime() * 1000f, 20, Gdx.graphics.getHeight() - 180);
        font.draw(fontBatch, "Current Time: " + java.time.LocalTime.now(), 20, Gdx.graphics.getHeight() - 200);
        if (isConnectedToServer) {
            font.draw(fontBatch, "SERVER INFO", 20, Gdx.graphics.getHeight() - 240);
            font.draw(fontBatch, "Dynamic Instances: " + World.dynamicInstances.size() + " Total Instances: " + World.dynamicInstances.size() + World.staticInstances.size(), 20, Gdx.graphics.getHeight() - 260);
            font.draw(fontBatch, "SERVER IP: " + serverIP, 20, Gdx.graphics.getHeight() - 280);
            font.draw(fontBatch, "Server Software: " + softwareType, 20, Gdx.graphics.getHeight() - 300);
        }
        fontBatch.end();
    }
}