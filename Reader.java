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
         //oldest valid sequence still in buffer
        long minValidSeq = writeSeq - buffer.getCapacity();

        
        //if reader is too slow and data was overwritten, move it forward to the earliest valid position
        if (readSeq < minValidSeq) {
            readSeq = minValidSeq;
        }

        //if reader caught up with writer, there's no new data
        if (readSeq >= writeSeq) {
            throw new NoSuchElementException("No new data");
        }

        T value = buffer.readAt(readSeq);
        readSeq++;
        return value;
    }
}