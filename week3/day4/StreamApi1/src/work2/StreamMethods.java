package work2;

import java.util.List;
import java.util.stream.Collectors;

public class StreamMethods {
    public static void main(String[] args) {
        // filter(Predicate)
        // it takes the predicate so now then it will return true or false
        // boolean value function
        // e->e>10

        // map It takes the function
        // map(function)
        /* to return a value
        each element operation
         */

        List<String> names = List.of("Aman","Ankit","Abhinav","Sujal");
        List<String> newNames = names.stream().filter(e->e.startsWith("A")).collect(Collectors.toList());
        System.out.println(newNames);

        List<Integer> l1 = List.of(23,4,5,7,3);
        List<Integer> newNumbers = l1.stream().map(i->i*i).collect(Collectors.toList());
//        System.out.println(newNumbers);

        names.stream().forEach(e->{
            System.out.println(e);
        });

        // function ka reference hi aagya hai
        newNames.stream().forEach(System.out::println);


    newNumbers.stream().sorted().forEach(System.out::println);

    Integer interger = newNumbers.stream().min((x,y)-> x.compareTo(y)).get();
        System.out.println(interger);

        Integer maxValue = newNumbers.stream().max((x,y)-> x.compareTo(y)).get();
        System.out.println(maxValue);
    }

}
