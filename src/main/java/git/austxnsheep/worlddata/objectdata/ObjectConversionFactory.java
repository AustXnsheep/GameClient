package git.austxnsheep.worlddata.objectdata;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import git.austxnsheep.Main;
import git.austxnsheep.worlddata.objecttypes.ModelStack;
import git.austxnsheep.worlddata.simplestates.simpleentities.SimplePhysicsInstance;
import org.lwjgl.openvr.Texture;

import java.util.UUID;

import static git.austxnsheep.Main.assetManager;

public interface ObjectConversionFactory {
    default ModelStack createModelInstanceFromSimple(SimplePhysicsInstance simpleInstance) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        boolean hasModel = false; // Flag to track if we've successfully created a model part

        // Get the appropriate texture for the object's state
        String texturePath = "C:/Users/Nicholas/Desktop/Ripple Plans/Default Project Directory/Assets/Textures/scary.png";
        com.badlogic.gdx.graphics.Texture texture = null;
        if (assetManager.isLoaded(texturePath, Texture.class)) {
            texture = assetManager.get(texturePath, com.badlogic.gdx.graphics.Texture.class);
        }

        // Create a material, using a texture if available, or a color otherwise
        Material material;
        if (texture != null) {
            Main.getLogger().info("Texture found!");
            material = new Material(TextureAttribute.createDiffuse(texture));
        } else {
            // Fallback to color if no texture is available
            material = new Material(ColorAttribute.createDiffuse(simpleInstance.getColor()));
        }

        long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        MeshPartBuilder meshPartBuilder;

        switch (simpleInstance.getState()) {
            case 0: // Cube
                meshPartBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attributes, material);
                meshPartBuilder.box(simpleInstance.getWidth(), simpleInstance.getHeight(), simpleInstance.getDepth());
                hasModel = true;
                break;
            case 1: // Sphere
                float radius = simpleInstance.getHeight() / 2;
                meshPartBuilder = modelBuilder.part("sphere", GL20.GL_TRIANGLES, attributes, material);
                meshPartBuilder.sphere(radius * 2, radius * 2, radius * 2, 10, 10);
                hasModel = true;
                break;
            default:
                Main.getLogger().severe("Attempted to create object with unknown shape.");
                break;
        }

        if (hasModel) {
            Model model = modelBuilder.end();
            ModelStack modelInstance = new ModelStack(model, UUID.randomUUID());

            Vector3 position = simpleInstance.getPosition();
            Quaternion rotation = simpleInstance.getRotation();
            modelInstance.transform.set(position, rotation);
            modelInstance.ID = simpleInstance.getUuid();
            modelInstance.velocity = simpleInstance.deltaDirection;

            return modelInstance;
        }

        return null;
    }
}
