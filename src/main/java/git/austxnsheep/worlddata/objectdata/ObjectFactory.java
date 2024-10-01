package git.austxnsheep.worlddata.objectdata;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import git.austxnsheep.worlddata.objecttypes.ModelStack;

public interface ObjectFactory {
    ModelBuilder modelBuilder = new ModelBuilder();
    default ModelInstance createNonCollisionCube(float width, float height, float depth, Color color, Vector3 position) {
        Model cubeModel = modelBuilder.createBox(width, height, depth,
                new Material(ColorAttribute.createDiffuse(color)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        ModelInstance cubeInstance = new ModelInstance(cubeModel);

        cubeInstance.transform.setToTranslation(position);

        return cubeInstance;
    }

}
