import java.util.NoSuchElementException;

public class Reader<T> {
    private final RingBuffer<T> buffer;
    private long readSeq;

    public Reader(RingBuffer<T> buffer, long startSeq) {
        this.buffer = buffer;
        this.readSeq = startSeq;
    }

    public T read() {

        long writeSeq = buffer.getWriteSeq();
        long minValidSeq = writeSeq - buffer.getCapacity();

        
        if (readSeq < minValidSeq) {
            readSeq = minValidSeq;
        }

        if (readSeq >= writeSeq) {
            throw new NoSuchElementException("No new data");
        }

        T value = buffer.readAt(readSeq);
        readSeq++;
        return value;
    }
}