package git.austxnsheep.worlddata;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import git.austxnsheep.types.Entity;

import java.util.ArrayList;
import java.util.List;

public class World {
    /*
    Contains information about the world. This data gets sent periodically from the server. (What is a string???)
     */
    public static List<ModelInstance> staticInstances = new ArrayList<>();
    public static List<ModelInstance> dynamicInstances = new ArrayList<>();
    public static List<Entity> entitiesList = new ArrayList<>();
    public World() {}
}
