public class Writer<T> {
    private final RingBuffer<T> buffer;

    public Writer(RingBuffer<T> buffer) {
        this.buffer = buffer;
    }

    //writes item into the ring buffer
    public void write(T item) {
        buffer.writeRing(item);
    }
}