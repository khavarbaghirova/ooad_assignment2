import java.util.ArrayList;
import java.util.Collections;

public class RingBuffer<T> {
    private final ArrayList<T> buffer;
    private final int capacity;
    private long writeSeq;
    private boolean writerCreated;

    public RingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity should be positive");
        }
        this.capacity = capacity;
        this.buffer = new ArrayList<>(Collections.nCopies(capacity, null));
        this.writeSeq = 0;
        this.writerCreated = false;
    }

    public Writer<T> createWriter() {
        if (writerCreated) {
            throw new IllegalStateException("Only one writer is allowed");
        }
        writerCreated = true;
        return new Writer<>(this);
    }

    public Reader<T> createReader() {
        long startSeq = Math.max(0, writeSeq - capacity);
        return new Reader<>(this, startSeq);
    }

    void writeRing(T item) {
        int index = (int) (writeSeq % capacity);
        buffer.set(index, item);
        writeSeq++;
    }

    T readAt(long sequence) {
        int index = (int) (sequence % capacity);
        return buffer.get(index);
    }

    long getWriteSeq() {
        return writeSeq;
    }

    int getCapacity() {
        return capacity;
    }
}