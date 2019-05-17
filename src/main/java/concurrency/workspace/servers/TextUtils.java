package concurrency.workspace.servers;

public interface TextUtils {
    default int transmogrify(int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }
}
