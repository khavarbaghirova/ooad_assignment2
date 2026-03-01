import java.util.NoSuchElementException;

public class Reader<T> {
    private final RingBuffer<T> buffer;
    private long readSeq;

    public Reader(RingBuffer<T> buffer, long startSeq) {
        this.buffer = buffer;
        this.readSeq = startSeq;
    }

    public T read() {
        if (readSeq >= buffer.getWriteSeq()) {
            throw new NoSuchElementException("No new data");
        }

        T value = buffer.readAt(readSeq);
        readSeq++;
        return value;
    }
}