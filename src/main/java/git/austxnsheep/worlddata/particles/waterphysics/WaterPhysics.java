package git.austxnsheep.worlddata.particles.waterphysics;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.*;
import java.util.stream.Collectors;

public interface WaterPhysics {
    default List<Mesh> getWaterMeshes(List<WaterGroup> groups) {
        List<Mesh> meshes = new ArrayList<>();
        for (WaterGroup group : groups) {
            meshes.add(createSurroundingMesh(group.particles));
        }
        return meshes;
    }
    default List<WaterGroup> groupWaterParticles(List<ModelInstance> actors, double thresholdDistance, double maxGroupSize) {
        List<WaterGroup> waterGroups = new ArrayList<>();
        Map<ModelInstance, WaterGroup> particleToGroup = new HashMap<>();

        for (ModelInstance actor : actors) {
            if (actor != null && !particleToGroup.containsKey(actor)) {
                Vector3 actorPosition = actor.transform.getTranslation(new Vector3());
                List<ModelInstance> nearby = findNearbyParticles(actors, actorPosition, thresholdDistance);
                nearby = filterParticlesByGroupSize(nearby, particleToGroup, maxGroupSize);

                WaterGroup group;
                if (nearby.isEmpty()) {
                    group = new WaterGroup(Collections.singletonList(actor));
                } else {
                    group = new WaterGroup(new ArrayList<>(nearby)); // Ensure modifiable list
                    for (ModelInstance nearbyActor : nearby) {
                        particleToGroup.put(nearbyActor, group);
                    }
                }
                waterGroups.add(group);
                particleToGroup.put(actor, group); // Ensure the original actor is also mapped
            }
        }

        return waterGroups;
    }
    default Model convertMeshToModel(Mesh mesh, Material material) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.part("meshPart", mesh, GL20.GL_TRIANGLES, material);
        return modelBuilder.end();
    }
    private List<ModelInstance> findNearbyParticles(List<ModelInstance> actors, Vector3 position, double thresholdDistance) {
        return actors.stream()
                .filter(otherActor -> position.dst(otherActor.transform.getTranslation(new Vector3())) <= thresholdDistance)
                .collect(Collectors.toList());
    }

    private List<ModelInstance> filterParticlesByGroupSize(List<ModelInstance> nearby, Map<ModelInstance, WaterGroup> particleToGroup, double maxGroupSize) {
        return nearby.stream()
                .filter(particle -> {
                    WaterGroup group = particleToGroup.get(particle);
                    return group == null || group.particles.size() < maxGroupSize;
                })
                .collect(Collectors.toList());
    }
    private Mesh createSurroundingMesh(List<ModelInstance> modelInstances) {
        // Calculate the combined bounding box
        BoundingBox combinedBox = new BoundingBox();
        Vector3 min = new Vector3();
        Vector3 max = new Vector3();
        boolean first = true;
        for (ModelInstance instance : modelInstances) {
            BoundingBox boundingBox = new BoundingBox();
            instance.calculateBoundingBox(boundingBox);
            if (first) {
                combinedBox.set(boundingBox);
                first = false;
            } else {
                combinedBox.ext(boundingBox);
            }
        }

        // Create a mesh based on the combined bounding box
        // For simplicity, we're creating a box-shaped mesh. Adjust vertices for other shapes.
        float[] vertices = new float[]{
                combinedBox.min.x, combinedBox.min.y, combinedBox.min.z, // Bottom-left-front
                combinedBox.max.x, combinedBox.min.y, combinedBox.min.z, // Bottom-right-front
                combinedBox.max.x, combinedBox.max.y, combinedBox.min.z, // Top-right-front
                combinedBox.min.x, combinedBox.max.y, combinedBox.min.z, // Top-left-front
                combinedBox.min.x, combinedBox.min.y, combinedBox.max.z, // Bottom-left-back
                combinedBox.max.x, combinedBox.min.y, combinedBox.max.z, // Bottom-right-back
                combinedBox.max.x, combinedBox.max.y, combinedBox.max.z, // Top-right-back
                combinedBox.min.x, combinedBox.max.y, combinedBox.max.z  // Top-left-back
        };

        short[] indices = new short[]{
                0, 1, 2, 2, 3, 0, // Front face
                4, 5, 6, 6, 7, 4, // Back face
                0, 3, 7, 7, 4, 0, // Left face
                1, 5, 6, 6, 2, 1, // Right face
                3, 2, 6, 6, 7, 3, // Top face
                0, 1, 5, 5, 4, 0  // Bottom face
        };

        Mesh mesh = new Mesh(true, vertices.length / 3, indices.length,
                VertexAttribute.Position());

        mesh.setVertices(vertices);
        mesh.setIndices(indices);

        return mesh;
    }
}
