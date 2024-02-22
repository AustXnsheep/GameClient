package git.austxnsheep.worlddata.objectdata;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import git.austxnsheep.worlddata.simplestates.simpleentities.SimplePhysicsInstance;

public interface ObjectConversionFactory {
    ModelBuilder modelBuilder = new ModelBuilder();
    default ModelInstance createModelInstanceFromSimple(SimplePhysicsInstance simpleInstance) {
        switch (simpleInstance.getState()) {
            case 0: {
                // Start building a new box model with dimensions and color from the simpleInstance
                Model model = modelBuilder.createBox(
                        simpleInstance.getWidth(), simpleInstance.getHeight(), simpleInstance.getDepth(),
                        new Material(ColorAttribute.createDiffuse(simpleInstance.getColor())),
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

                // Create a ModelInstance from the Model
                ModelInstance modelInstance = new ModelInstance(model);

                // Set the ModelInstance's position and rotation based on the SimplePhysicsInstance
                Vector3 position = simpleInstance.getPosition();
                Quaternion rotation = simpleInstance.getRotation();
                modelInstance.transform.set(position, rotation);

                return modelInstance;
            }
            case 1: {
                float diameter = simpleInstance.getHeight()/2;
                Model model = modelBuilder.createSphere(diameter, diameter, diameter, 10, 10,
                        new Material(ColorAttribute.createDiffuse(simpleInstance.getColor())),
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
                return new ModelInstance(model);
            }
            default: return null;
        }
    }
}
