package git.austxnsheep.worlddata.objectdata;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import git.austxnsheep.worlddata.objecttypes.ModelStack;
import git.austxnsheep.worlddata.simplestates.simpleentities.SimplePhysicsInstance;

import java.util.UUID;

public interface ObjectConversionFactory {
    ModelBuilder modelBuilder = new ModelBuilder();
    default ModelStack createModelInstanceFromSimple(SimplePhysicsInstance simpleInstance) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        boolean hasModel = false; // Flag to track if we've successfully created a model part

        Material material = new Material(ColorAttribute.createDiffuse(simpleInstance.getColor()));
        long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;

        MeshPartBuilder meshPartBuilder;

        switch (simpleInstance.getState()) {
            case 0: // Cube
                meshPartBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attributes, material);
                meshPartBuilder.box(simpleInstance.getWidth(), simpleInstance.getHeight(), simpleInstance.getDepth());
                hasModel = true;
                break;
            case 1: // Sphere
                float radius = simpleInstance.getHeight() / 2; // Assuming height represents diameter for the sphere case
                meshPartBuilder = modelBuilder.part("sphere", GL20.GL_TRIANGLES, attributes, material);
                meshPartBuilder.sphere(radius * 2, radius * 2, radius * 2, 10, 10);
                hasModel = true;
                break;
            default:
                // Handle other cases or invalid state
                break;
        }

        if (hasModel) {
            Model model = modelBuilder.end(); // Finalize the model
            ModelStack modelInstance = new ModelStack(model, UUID.randomUUID());

            // Assuming ModelStack has a method or field `transform` to set position and rotation
            Vector3 position = simpleInstance.getPosition();
            Quaternion rotation = simpleInstance.getRotation();
            modelInstance.transform.set(position, rotation);
            modelInstance.ID = simpleInstance.getUuid();

            return modelInstance;
        }

        // No model was created; handle this case as needed
        return null;
    }
}
