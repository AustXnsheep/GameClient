package git.austxnsheep.network.packets.post;

import java.util.Random;

public class TestPacket {
    public int data = 1;
    public TestPacket() {
        Random random = new Random();
        data = random.nextInt();
        System.out.println(data);
    }
    @Override
    public String toString() {
        return String.valueOf(data);
    }
}
