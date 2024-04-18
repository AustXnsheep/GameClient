package git.austxnsheep.network;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import git.austxnsheep.Main;
import git.austxnsheep.network.UUIDSerializer.UUIDSerializer;
import git.austxnsheep.network.listeners.ClientListener;
import git.austxnsheep.network.packets.post.*;
import git.austxnsheep.network.packets.requests.PushBlockPacket;
import git.austxnsheep.network.packets.requests.WorldRequestPacket;
import git.austxnsheep.types.Player;
import git.austxnsheep.worlddata.objecttypes.Actor;
import git.austxnsheep.worlddata.simplestates.SimpleEntity;
import git.austxnsheep.worlddata.simplestates.simpleentities.SimpleBlockMan;
import git.austxnsheep.worlddata.simplestates.simpleentities.SimplePhysicsInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class GameClient {
    private Client client;

    public GameClient() {
        client = new Client(16384, 4096);
        client.start();

        registerClasses(client.getKryo());

        client.addListener(new ClientListener());
    }

    private void registerClasses(Kryo kryo) {
        kryo.register(JoinPacket.class);
        kryo.register(Player.class);
        kryo.register(TestPacket.class);
        kryo.register(WorldDataPacket.class);
        kryo.register(ActorLocationPacket.class);
        kryo.register(Vector3.class);
        kryo.register(Quaternion.class);
        kryo.register(Color.class);
        kryo.register(ArrayList.class);
        kryo.register(WorldRequestPacket.class);
        kryo.register(PushBlockPacket.class);
        kryo.register(PlayerUpdatePacket.class);
        kryo.register(Actor.class);
        kryo.register(SimpleEntity.class);
        kryo.register(SimpleBlockMan.class);
        kryo.register(SimplePhysicsInstance.class);
        kryo.register(UUID.class, new UUIDSerializer());
        // Register other classes as needed
    }

    public void connect(String address, int tcpPort, int udpPort) {
        try {
            client.connect(5000, address, tcpPort, udpPort);
            System.out.println("Connected to server at " + address + ":" + tcpPort);

            Main.getLogger().info("Test1 passed");
        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendData(Object data) {
        client.sendTCP(data);
    }
}
