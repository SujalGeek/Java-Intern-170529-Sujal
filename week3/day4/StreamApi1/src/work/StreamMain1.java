package work;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamMain1 {
    // Create a List and filter all even numbers from List
    public static void main(String[] args) {
        List<Integer> l1 = List.of(2,4,6,20,21,22,67);
//        l1.add(45);
        // Unsupported because it is immutable

        System.out.println(l1);
    List<Integer> list2 = new ArrayList<>();
    list2.add(20);
    list2.add(339);
    list2.add(30);
    list2.add(12);

    for (Integer i : list2)
    {
        System.out.print(i+" ");
    }
        System.out.println(list2);
    // list1
        // without stream

        List<Integer> listEven = new ArrayList<>();
    for(Integer j: l1)
    {
        if(j%2 == 0)
        {
            listEven.add(j);
        }
    }

        System.out.println(l1);
        System.out.println(listEven);

        // using stream api
        // Stream is an interface
        System.out.println("Using Stream API");
        Stream<Integer> stream1 = l1.stream();
        // Here we have get the stream and storing in the stream1
        // then filter the even numbers
        // where the predicate we need to get the boolean result true
        // carries the condition and it is returning then
        // will collect and

       List<Integer> newList = stream1.filter(j->j%2==0).collect(Collectors.toList());
        System.out.println(newList);

        List<Integer> newList2 = l1.stream().filter(j->j>10).collect(Collectors.toList());
        System.out.println(newList2);

    }

}
