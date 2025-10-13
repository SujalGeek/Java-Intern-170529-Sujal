package org.example.services;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class CalculatorServiceTestJUnit5 {

    @BeforeAll
    public static void init()
    {
        System.out.println("This is single time logic");
    }

    @AfterAll
    public static void cleanUp(){
        System.out.println("After all test cases logic");
    }

    @BeforeEach
    public void beforeEachTestCase(){
        System.out.println("This is the Before Each Test Case");
    }

    @AfterEach
    public void afterEachTestCase(){
        System.out.println("This is the after Each Test Case");
    }

    @Test
    @Disabled
    public void addTwoNumbersTest() {
        int result = CalculatorService.addTwoNumbers(5, 3);
        assertEquals(8, result);
        System.out.println("The is the add two numbers tests");
    }

    @Test
    @DisplayName("This is Custom Name")
    public void addAnyNumbersTest() {
        int result = CalculatorService.sumAnyNumbers(1, 2, 3, 4);
        assertEquals(10, result);
        System.out.println("This is add any numbers test cases");

    }
//    @Tags
    // @Nested
//    @TestFactory

}
