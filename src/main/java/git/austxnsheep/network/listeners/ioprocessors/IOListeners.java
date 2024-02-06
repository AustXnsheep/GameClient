package git.austxnsheep.network.listeners.ioprocessors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import git.austxnsheep.Main;
import git.austxnsheep.network.packets.requests.PushBlockPacket;

import static git.austxnsheep.Main.camera;

public class IOListeners implements InputProcessor {
    private static float yaw = 0f;
    private static float pitch = 0f;
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.F) {
            Main.getLogger().info("Pressed F!");
            Main.client.sendData(new PushBlockPacket());
            return true;
        }
        return false;
    }

    // Other InputProcessor methods must be overridden but can be left empty if not used
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    public static void handleInput(float deltaTime) {
        float cameraSpeed = 5f;
        float sensitivity = 0.2f; // Adjust sensitivity as needed

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.translate(camera.direction.cpy().scl(deltaTime * cameraSpeed));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.translate(camera.direction.cpy().scl(-deltaTime * cameraSpeed));
        }

        // Mouse movement for rotation
        if (Gdx.input.isCursorCatched()) {
            float deltaX = Gdx.input.getDeltaX() * sensitivity;
            float deltaY = Gdx.input.getDeltaY() * sensitivity;

            yaw += deltaX;
            pitch -= deltaY;

            pitch = Math.max(-89, Math.min(89, pitch));

            Vector3 direction = new Vector3(
                    (float) Math.cos(Math.toRadians(pitch)) * (float) Math.cos(Math.toRadians(yaw)),
                    (float) Math.sin(Math.toRadians(pitch)),
                    (float) Math.cos(Math.toRadians(pitch)) * (float) Math.sin(Math.toRadians(yaw))
            ).nor();

            camera.direction.set(direction);
            camera.up.set(Vector3.Y);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.rotate(Vector3.Y, 90 * deltaTime); // Rotate left
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.rotate(Vector3.Y, -90 * deltaTime); // Rotate right
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            camera.translate(camera.direction.cpy().scl(-deltaTime * 5)); // Move backward
        }
    }
}
