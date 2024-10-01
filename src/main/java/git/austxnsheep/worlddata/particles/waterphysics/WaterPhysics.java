package git.austxnsheep.worlddata.particles.waterphysics;


import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import git.austxnsheep.worlddata.objecttypes.ModelStack;

import java.util.List;

public interface WaterPhysics {
    /*
    default void deformSphere(Model sphere, List<ModelStack> modelStacks) {
        Mesh mesh = sphere.meshes.first();
        float[] vertices = new float[mesh.getNumVertices() * mesh.getVertexSize() / 4];
        mesh.getVertices(vertices);

        // Vertex attributes index finding (assuming position attribute is present)
        int positionIdx = mesh.getVertexAttributes().indexOf(VertexAttributes.Usage.Position);

        // Calculate centroid of all ModelStacks
        Vector3 centroid = calculateCentroid(modelStacks);

        // Iterate through each vertex and adjust based on nearest ModelStack
        for (int i = 0; i < vertices.length; i += mesh.getVertexSize() / 4) {
            Vector3 vertexPos = new Vector3(vertices[i + positionIdx], vertices[i + positionIdx + 1], vertices[i + positionIdx + 2]);
            vertexPos.scl(centroid.dst(vertexPos)); // Simple scaling based on distance to centroid

            // Potentially more complex logic here to adjust based on nearest ModelStack
            for (ModelStack stack : modelStacks) {
                Vector3 stackPos = new Vector3();
                stack.transform.getTranslation(stackPos);
                float dist = vertexPos.dst(stackPos);
                if (dist < 5) {
                    Vector3 direction = new Vector3(stackPos).sub(vertexPos).nor();
                    vertexPos.add(direction.scl(4)); // Deform towards or away from the ModelStack
                }
            }

            // Update the vertex position in the array
            vertices[i + positionIdx] = vertexPos.x;
            vertices[i + positionIdx + 1] = vertexPos.y;
            vertices[i + positionIdx + 2] = vertexPos.z;
        }

        // Update the mesh with the new vertices
        mesh.setVertices(vertices);
    }

     */
    default float calculateMaxDistance(Vector3 centroid, List<ModelStack> modelStacks) {
        float maxDistance = 0;
        for (ModelStack stack : modelStacks) {
            Vector3 position = new Vector3();
            stack.transform.getTranslation(position);
            float distance = position.dst(centroid);
            if (distance > maxDistance) {
                maxDistance = distance;
            }
        }
        return maxDistance;
    }
    default Vector3 calculateCentroid(List<ModelStack> modelStacks) {
        Vector3 centroid = new Vector3();
        for (ModelStack stack : modelStacks) {
            Vector3 position = new Vector3();
            stack.transform.getTranslation(position);
            centroid.add(position);
        }
        centroid.scl(1.0f / modelStacks.size());
        return centroid;
    }
}
