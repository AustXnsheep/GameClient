package git.austxnsheep.network;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import git.austxnsheep.network.listeners.ClientListener;
import git.austxnsheep.network.packets.post.*;
import git.austxnsheep.network.packets.requests.PushBlockPacket;
import git.austxnsheep.network.packets.requests.WorldRequestPacket;
import git.austxnsheep.types.Player;
import git.austxnsheep.worlddata.simplestates.SimplePhysicsInstance;

import java.io.IOException;
import java.util.ArrayList;

public class GameClient {
    private Client client;

    public GameClient() {
        client = new Client();
        client.start();

        registerClasses(client.getKryo());

        client.addListener(new ClientListener());
    }

    private void registerClasses(Kryo kryo) {
        kryo.register(JoinPacket.class);
        kryo.register(Player.class);
        kryo.register(TestPacket.class);
        kryo.register(WorldDataPacket.class);
        kryo.register(SimplePhysicsInstance.class);
        kryo.register(ActorLocationPacket.class);
        kryo.register(Vector3.class);
        kryo.register(Quaternion.class);
        kryo.register(Color.class);
        kryo.register(ArrayList.class);
        kryo.register(WorldRequestPacket.class);
        kryo.register(PushBlockPacket.class);
        kryo.register(PlayerUpdatePacket.class);
        // Register other classes as needed
    }

    public void connect(String address, int tcpPort, int udpPort) {
        try {
            client.connect(5000, address, tcpPort, udpPort);
            System.out.println("Connected to server at " + address + ":" + tcpPort);
        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendData(Object data) {
        client.sendTCP(data);
    }
}
