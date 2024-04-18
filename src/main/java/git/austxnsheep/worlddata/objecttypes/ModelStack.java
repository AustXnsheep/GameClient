package git.austxnsheep.worlddata.objecttypes;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import java.util.UUID;

public class ModelStack extends ModelInstance {
    public UUID ID;
    public Vector3 velocity;
    public ModelStack(Model model, UUID ID) {
        super(model);
        this.ID = ID;
    }
    public ModelStack(ModelInstance instance, UUID ID) {
        super(instance.model, instance.transform.cpy());
        this.ID = ID;
    }
}
