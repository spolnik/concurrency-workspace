package concurrency.workspace.servers.util;

import java.nio.ByteBuffer;

public class Util {

    private Util() {
        throw new AssertionError();
    }

    public static void transmogrify(ByteBuffer buf) {
        buf.flip();
        for (int i = 0; i< buf.limit(); i++) {
            buf.put(i, (byte) transmogrify(buf.get(i)));
        }
    }

    public static int transmogrify(int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }
}
