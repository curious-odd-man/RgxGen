package com.github.curiousoddman.rgxgen.manual;

import com.github.curiousoddman.rgxgen.CompleteTests;
import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ManualMemoryValidation {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Scanner scanner = new Scanner(System.in);
        System.out.println("This is multi step test for memory and profiling purposes");
        System.out.println("Please start profiler now and press enter to continue...");

        scanner.nextLine();

        /**
         * Tests:
         * 1. One pattern infinite generation of matching values. Test that memory does not grow.
         * 2. One pattern unique values generation. Memory may grow, but should be freed after end of test.
         * 3. Different patterns parsing (without generation). Memory may grow, but should be freed after end of test.
         */

        System.out.println("Test 1: Verify that memory does not grow during this step. Press enter when ready");
        scanner.nextLine();

        List<Object[]> data = CompleteTests.data();
        Future<?> submit = executorService.submit(() -> {
            RgxGen rgxGen = new RgxGen(data.get(2)[2].toString());
            while (!Thread.currentThread()
                          .isInterrupted()) {
                rgxGen.generate();
            }
            System.out.println("Test 1 ended;");
        });

        System.out.println("Press enter to terminate test.");
        scanner.nextLine();
        submit.cancel(true);
        System.out.println("Test completed. See that memory is returned to previous level before continuing..");
        System.out.println("Test 2: Verify that memory grows slowly. Press enter to start test.");
        scanner.nextLine();
        submit = executorService.submit(() -> {
            RgxGen rgxGen = new RgxGen("(a|b|c)*");
            StringIterator stringIterator = rgxGen.iterateUnique();
            while (!Thread.currentThread()
                          .isInterrupted()) {
                stringIterator.next();
            }
            System.out.println("Test 2 ended;");
        });
        System.out.println("Press enter to terminate test.");
        scanner.nextLine();
        System.out.println("Test completed. See that memory is returned to previous level before continuing..");
        submit.cancel(true);
        System.out.println("Test 3: Verify that memory grows slowly. Press enter to start test.");
        scanner.nextLine();

        submit = executorService.submit(() -> {
            Iterator<Object[]> iterator = data.iterator();
            while (!Thread.currentThread()
                          .isInterrupted()) {
                String pattern;
                if (!iterator.hasNext()) {
                    iterator = data.iterator();
                }
                pattern = iterator.next()[2].toString();
                RgxGen rgxGen = new RgxGen(pattern);
            }
            System.out.println("Test 3 ended;");
        });

        System.out.println("Press enter to terminate test.");
        scanner.nextLine();
        submit.cancel(true);

        System.out.println("Test completed. See that memory is returned to previous level before continuing..");
        System.out.println("Press enter to exit.");
        scanner.nextLine();
    }
}
