package org.example.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssertExample {
    // assertion - validation actual result with the expected result
    // Assertion class

    @Test
    public void test1(){
        System.out.println("Testing some assertions methods");
        int actual = 12;
        int expected = 12;
        Assertions.assertEquals(actual,expected);
        // Assertions.

    int []actualArrayElements = {1,2,3,4,5};
    int []expectedArrayElements = {1,2,3,4,5};

    Assertions.assertArrayEquals(actualArrayElements,expectedArrayElements);


    }
}
