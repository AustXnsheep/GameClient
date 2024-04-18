package git.austxnsheep.worlddata;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import git.austxnsheep.types.Entity;
import git.austxnsheep.worlddata.objecttypes.ModelStack;
import git.austxnsheep.worlddata.particles.waterphysics.WaterGroup;

import java.util.ArrayList;
import java.util.List;

public class World {
    /*
    Contains information about the world. This data gets sent periodically from the server. (What is a string???)
    */
    public static List<ModelInstance> staticInstances = new ArrayList<>();
    public static List<ModelStack> dynamicInstances = new ArrayList<>();
    public static List<Entity> entitiesList = new ArrayList<>();
    public World() {}
}
