package concurrency.workspace.buffers;

import java.nio.CharBuffer;

public class BufferFillDrain {

    public static void main(String[] args) {
        final var buffer = CharBuffer.allocate(100);

        while (fillBuffer(buffer)) {
            buffer.flip();
            drainBuffer(buffer);
            buffer.clear();
        }
    }

    private static void drainBuffer(CharBuffer buffer) {
        while (buffer.hasRemaining()) {
            System.err.print(buffer.get());
        }
        System.out.println();
    }

    private static boolean fillBuffer(CharBuffer buffer) {
        if (index >= strings.length) return false;

        strings[index++]
            .chars()
            .forEach(c -> buffer.put((char) c));

        return true;
    }

    private static int index = 0;

    private static final String[] strings = {
        "Lorem Ipsum is simply dummy text of the printing",
        "and typesetting industry. Lorem Ipsum has been",
        "the industry's standard dummy text ever since the",
        "1500s, when an unknown printer took a galley of type",
        "and scrambled it to make a type specimen book.",
        "It has survived not only five centuries, but also",
        "the leap into electronic typesetting, remaining",
        "essentially unchanged. It was popularised in the 1960s",
        "with the release of Letraset sheets containing",
        "Lorem Ipsum passages, and more recently with desktop",
        "publishing software like Aldus PageMaker including",
        "versions of Lorem Ipsum."
    };
}
