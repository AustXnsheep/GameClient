package git.austxnsheep.worlddata.particles.waterphysics;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import java.util.List;

public class WaterGroup {
    public List<ModelInstance> particles;
    public WaterGroup(List<ModelInstance> particles) {
        this.particles = particles;
    }
}
