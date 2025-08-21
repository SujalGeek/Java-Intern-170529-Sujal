package work1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamAPI1 {
    public static void main(String[] args) {

        // Stream API - Collection process
        // collection/ group of object
        // 1 - blank
        Stream<Object> emptyStream = Stream.empty();

        // 2 array - object
        String names[] = {"Sujal","Raman","Darsh","Darshan"};

        Stream<String> stream1 = Stream.of(names);
        stream1.forEach(e->{
            System.out.println(e);
        });

        // 3
        Stream<Object> streamBuilder = Stream.builder().build();

        //2
        IntStream stream =Arrays.stream(new int[]{2,4,65,3,256});
        stream.forEach(e->{
            System.out.println(e);
        });

        //5 list,set ke object pe
        List<Integer> list3 = new ArrayList<>();
        list3.add(30);
        list3.add(20);
        list3.add(430);
        list3.add(78);

    Stream<Integer> stream4 =list3.stream();
    stream4.forEach(e->
            System.out.println(e));
    }
    // Object pe call karna ka hota hai
    // List<Integer> list3 = new ArrayList<>(); idhar
    // Arraylist ke object pe


}
