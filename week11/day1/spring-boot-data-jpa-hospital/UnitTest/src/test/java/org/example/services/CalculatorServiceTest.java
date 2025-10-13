//package org.example.services;
//
//import org.junit.*;
//
//import java.util.Date;
//
//public class CalculatorServiceTest {
//
//    // test method of addTwoNumbers
//
//    int counter =0;
//    // BeforeClass
//
//    // method should be static
//    @BeforeClass
//    public static void init() {
//        System.out.println("Before all test cases:");
//        System.out.println("Started test: "+new Date());
//    }
//
//    @AfterClass
//    // After all test cases
//    public static void cleanup(){
//        System.out.println("After all test cases");
//        System.out.println("End Test Cases: "+new Date());
//    }
//
//
//    // Before each test case
//    @Before
//    public void beforeEach(){
//        System.out.println("Before each Test Case");
//        counter=0;
//    }
//
//    @After
//    public void afterEach(){
//        System.out.println("After each Test Case");
//    }
//
//    @Test(timeout = 2000)
//    public void addTwoNumbersTest(){
//        for(int i=1;i<=20;i++)
//        {
//            counter+=i;
//        }
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("Test for the Two Numbers");
//        int result = CalculatorService.addTwoNumbers(12,45);
//
//        int expected = 57;
//        Assert.assertEquals(expected,result);
//        // actual result
//
//        // expected result
//        System.out.println("Counter int first test case:"+counter);
//    }
//
//    @Test
//    public void sumAnyNumberTest()
//    {
//        for(int i=1;i<=100;i++)
//        {
//            counter+=i;
//        }
//
//        System.out.println("Test for any numbers");
//       int result =  CalculatorService.sumAnyNumbers(2,4,5,6,7);
//
//        int expectedResult = 24;
//        Assert.assertEquals(expectedResult,result);
//        System.out.println("Counter after second test case: "+counter);
//    }
//}
