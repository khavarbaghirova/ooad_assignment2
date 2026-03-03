public class Main {
    public static void main(String[] args) {

        RingBuffer<Integer> ringBuffer = new RingBuffer<>(3);

        Writer<Integer> writer = ringBuffer.createWriter();
        Reader<Integer> reader1 = ringBuffer.createReader();
        Reader<Integer> reader2 = ringBuffer.createReader();
        
        writer.write(5);
        writer.write(6);
        writer.write(7);
        System.out.println("Writing: 5, 6, 7");

        System.out.println("Reader 1 reads:");
        System.out.println(reader1.read()); 
        System.out.println(reader1.read()); 

        System.out.println("Reader 2 reads:");
        System.out.println(reader2.read()); 

        //if buffer is full, next write overwrites oldest element (5)
        writer.write(8);
        System.out.println("Writing: 8 (overwrites 5)");

        System.out.println("Reader 1 reads:");
        System.out.println(reader1.read()); 

        System.out.println("Reader2 reads:");
        System.out.println(reader2.read()); 
        System.out.println(reader2.read()); 
        System.out.println(reader2.read()); 
    }
}