package git.austxnsheep.worlddata.objecttypes;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModelStack extends ModelInstance {
    public UUID ID;
    public Vector3 velocity;
    public String textureID;
    public ModelStack(Model model, UUID ID) {
        super(model);
        this.ID = ID;
    }
    public ModelStack(ModelInstance instance, UUID ID) {
        super(instance.model, instance.transform.cpy());
        this.ID = ID;
    }
    public ModelStack(ModelInstance instance, UUID ID, String textureID) {
        super(instance.model, instance.transform.cpy());
        this.ID = ID;
        this.textureID = textureID;
    }
    private static float distance(ModelStack a, ModelStack b) {
        return a.transform.getTranslation(new Vector3()).dst(b.transform.getTranslation(new Vector3()));
    }
    public static List<List<ModelStack>> findCloseGroups(List<ModelStack> modelStacks, float threshold) {
        List<List<ModelStack>> groups = new ArrayList<>();

        for (int i = 0; i < modelStacks.size(); i++) {
            ModelStack a = modelStacks.get(i);
            List<ModelStack> group = new ArrayList<>();
            group.add(a);

            for (int j = i + 1; j < modelStacks.size(); j++) {
                ModelStack b = modelStacks.get(j);
                if (distance(a, b) <= threshold) {
                    group.add(b);
                }
            }

            if (group.size() > 1) {
                groups.add(group);
            }
        }

        return groups;
    }
}
