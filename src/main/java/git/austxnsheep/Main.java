package git.austxnsheep;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import git.austxnsheep.network.GameClient;
import git.austxnsheep.network.listeners.ioprocessors.IOListeners;
import git.austxnsheep.network.packets.post.JoinPacket;
import git.austxnsheep.network.packets.post.PlayerUpdatePacket;
import git.austxnsheep.network.packets.requests.WorldRequestPacket;
import git.austxnsheep.worlddata.World;

import java.util.logging.Logger;

import static git.austxnsheep.network.listeners.ioprocessors.IOListeners.handleInput;

public class Main extends ApplicationAdapter {
    public static PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Environment environment;
    private CameraInputController camController;
    static Logger logger = Logger.getLogger(Main.class.getName());
    public static GameClient client;

    public static void main(String[] args) {
        new LwjglApplication(new Main(), "Ripple", 800, 600);
        client = new GameClient();
        client.connect("64.23.178.23", 54555, 54777);

        JoinPacket data = new JoinPacket();
        client.sendData(data);
        WorldRequestPacket worldRequestPacket = new WorldRequestPacket();
        client.sendData(worldRequestPacket);
    }

    public static Logger getLogger() {
        return logger;
    }
    @Override
    public void create() {
        modelBatch = new ModelBatch();

        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.far = 300.0f;
        camera.update();

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);

        Gdx.input.setCursorCatched(true);
        IOListeners ioListeners = new IOListeners();
        Gdx.input.setInputProcessor(ioListeners);

        environment = new Environment();
        // Setup ambient light
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f)); // Soft white light

        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f)); // Adjust color and direction as needed


        // Add a directional light to simulate sunlight
        // Initialize your PhysicsModelInstances and add them to instances array
        // instances.add(...);
    }
    @Override
    public void render() {
        // Clear the screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Update the camera
        handleInput(Gdx.graphics.getDeltaTime());
        camera.update();
        camController.update();
        client.sendData(new PlayerUpdatePacket(camera.position));

        // Render instances
        modelBatch.begin(camera);
        for (ModelInstance instance : World.staticInstances) {
            modelBatch.render(instance, environment);
            // Update model instance transform from physics here if needed
        }
        for (ModelInstance instance : World.dynamicInstances) {
            modelBatch.render(instance, environment);
            // Update model instance transform from physics here if needed
        }
        modelBatch.end();
    }
    @Override
    public void dispose() {
        // Don't forget to dispose of the modelBatch and any models you've created
        modelBatch.dispose();
        // Dispose other resources if necessary
    }


}