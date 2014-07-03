package net.meng6.java_memory_use_experiment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Java object memory use measurement tool.
 * http://www.javamex.com/classmexer/
 */
import com.javamex.classmexer.MemoryUtil;
import com.javamex.classmexer.MemoryUtil.VisibilityFilter;
//import com.sun.xml.internal.ws.util.StringUtils;

import org.apache.commons.io.output.TeeOutputStream;

/**
 * Author: Meng LU <lumeng.dev@gmail.com>
 * Date: 7/1/14 6:06 PM
 *
 * References:
 * * http://stackoverflow.com/questions/52353/in-java-what-is-the-best-way-to-determine-the-size-of-an-object
 * * http://programmers.stackexchange.com/questions/162546/why-the-overhead-when-allocating-objects-arrays-in-java
 * * http://stackoverflow.com/questions/2486191/java-string-pool
 * * https://en.wikipedia.org/wiki/String_interning
 * * http://mindprod.com/jgloss/sizeof.html
 * * http://www.javamex.com/tutorials/memory/object_memory_usage.shtml
 *
 * Typical output:
 *Java environment:
 * JVM architecture System.getProperty("sun.arch.data.model") = 64
 * System.getProperty("java.specification.version") = 1.6
 * System.getProperty("java.version" = 1.6.0_65
 * System.getProperty("java.vm.version") = 20.65-b04-462
 * System.getProperty("java.runtime.version") = 1.6.0_65-b14-462-11M4609
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * |                                          variable/object/array type (N=800) |     total memory (bytes) (T)  |  bytes / element (a) | metadata [+ padding] |        scaling      |
 * |                                                                 measurement |   m1 - m2     m_obj    m_deep |           T/N        |  T - N * ((int) T/N) |                     |
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * |                                                                         int |         0        16        16 |      0     16     16 |                      |                     |
 * |                                                                       short |         0        16        16 |      0     16     16 |                      |                     |
 * |                                                                        long |         0        24        24 |      0     24     24 |                      |                     |
 * |                                                                       float |         0        16        16 |      0     16     16 |                      |                     |
 * |                                                                      double |         0        24        24 |      0     24     24 |                      |                     |
 * |                                                                        char |         0        16        16 |      0     16     16 |                      |                     |
 * |                                                                     boolean |         0        16        16 |      0     16     16 |                      |                     |
 * |                                                                        byte |         0        16        16 |      0     16     16 |                      |                     |
 * |                                                                     Integer |        24        16        16 |     24     16     16 |                      |                     |
 * |                                                                        Long |        24        24        24 |     24     24     24 |                      |                     |
 * |                                                                  BigInteger |        64        40        64 |     64     40     64 |                      |                     |
 * |                                                                  BigDecimal |      2848        40       120 |   2848     40    120 |                      |                     |
 * |                                                     literal string "foobar" |         0        32        64 |      0     32     64 |                      |                     |
 * |                                          string object new String("foobar") |        32        32        64 |     32     32     64 |                      |                     |
 * |                                                                      int[N] |      3216      3216      3216 |      4      4      4 |     16     16     16 |      4 * N +     16 |
 * |                                                                    short[N] |      1616      1616      1616 |      2      2      2 |     16     16     16 |      2 * N +     16 |
 * |                                                                     long[N] |      6416      6416      6416 |      8      8      8 |     16     16     16 |      8 * N +     16 |
 * |                                                                    float[N] |      3216      3216      3216 |      4      4      4 |     16     16     16 |      4 * N +     16 |
 * |                                                                   double[N] |      6416      6416      6416 |      8      8      8 |     16     16     16 |      8 * N +     16 |
 * |                                                                     char[N] |      1616      1616      1616 |      2      2      2 |     16     16     16 |      2 * N +     16 |
 * |                                                                  boolean[N] |       816       816       816 |      1      1      1 |     16     16     16 |      1 * N +     16 |
 * |                                                                     byte[N] |       816       816       816 |      1      1      1 |     16     16     16 |      1 * N +     16 |
 * |                                                                  Integer[N] |      3216      3216      3216 |      4      4      4 |     16     16     16 |      4 * N +     16 |
 * |                                                Integer[800] and 800 Integer |     22416      3216     16016 |     28      4     20 |     16     16     16 |                   ? |
 * |                                                                     Long[N] |      3216      3216      3216 |      4      4      4 |     16     16     16 |      4 * N +     16 |
 * |                                          BigInteger[800] and 800 BigInteger |     54416      3216     54416 |     68      4     68 |     16     16     16 |     68 * N +     16 |
 * |                                                      Long[800] and 800 Long |     22416      3216     22416 |     28      4     28 |     16     16     16 |     28 * N +     16 |
 * |                                                               BigDecimal[N] |      3216      3216      3216 |      4      4      4 |     16     16     16 |      4 * N +     16 |
 * |                                          BigDecimal[800] and 800 BigDecimal |     92816      3216     92816 |    116      4    116 |     16     16     16 |    116 * N +     16 |
 * |                                                                   String[N] |      3216      3216      3216 |      4      4      4 |     16     16     16 |      4 * N +     16 |
 * |      String[800] and 800 literal string "abcdefghij" (w/ string interning?) |      3216      3216      3288 |      4      4      4 |     16     16     88 |                   ? |
 * |         String[800] and 800 new String("abc0000000") (w/ string interning?) |     28816      3216     28856 |     36      4     36 |     16     16     56 |                   ? |
 * |           String[800] and 800 different strings String.format("abc%07d", i) |     60816      3216     60816 |     76      4     76 |     16     16     16 |     76 * N +     16 |
 * |                                            EmptyClass[800] + 800 EmptyClass |     22416      3216     16032 |     28      4     20 |     16     16     32 |                   ? |
 * |                                          SimpleClass[800] + 800 SimpleClass |     28816      3216     28832 |     36      4     36 |     16     16     32 |                   ? |
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 *
 *
 *
 * * A typical example of memory cost of a class:
 *
 * GenericMysteryBox<Long>
 *
 * public class GenericMysteryBox<Item> {        //       16 (object overhead)
 *     private int N;                            //        4 (int)
 *     private Item[] items;                     //        8 (reference to array)
 *                                               //  8N + 24 (array of Long references)
 *                                               //      24N (Long objects)
 *     ...                                                 4 (padding to round up to a multiple of 8)
 * }                                                 -------
 *                                                  32N + 56
 * * Remarks:
 *
 *
 */
public class JavaMemoryUseExperiment {

    private static boolean DEBUG = false;

    private static int N = 800;

    private static long m1;
    private static long m2;
    private static long m_obj;
    private static long m_deep;


    private static long measurement1;
    private static long measurement2;
    private static int measurement_count = 0;

    private class EmptyClass {};    //   16B (object metadata)

    private class SimpleClass {     //   16B (object metadata)
        private int member_int;     //    4B
        private long member_long;   //    8B
        //    4B (padding)
    }                               // = 32B

    private class ComplexClassWithTypeParameter<E> {    //    16 B (object metadata)
        private int n;                                  //     4 B
        private E[] items;                              //     8 B (array reference)
                                                        //   24n B (n Long objects)
                                                        // 8n+24 B (array n Long references)
                                                        //     4 B (padding to round up to a multiple of 8 B)

        public ComplexClassWithTypeParameter(Class<E> c, int n) {
            // Use Array native method to create array of a type only known at run time
            @SuppressWarnings("unchecked")
            final E[] items = (E[]) Array.newInstance(c, n);
            this.n = n;
        }
    }                                                   // = 32n + 56 B

    private static long getFreeMemory () {
        measurement1 = Runtime.getRuntime().freeMemory();


        // waits for free memory measurement to stabilize
        do {
            if (DEBUG) {
                System.out.println(
                        String.format(
                                "Run garbage collector, re-measure free memory ..., %s " +
                                        "time(s) confirmed: %s bytes",
                                measurement_count, measurement1
                        )
                );
            }
            System.gc();
            try { Thread.sleep(250); } catch (Exception x) { }
            measurement2 = measurement1;
            measurement1 = Runtime.getRuntime().freeMemory();
            if (measurement1 == measurement2) ++measurement_count; else measurement_count = 0;
        } while (measurement_count < 5);
        if (DEBUG) {System.out.println("Free memory = " + measurement1 + " bytes");}
        return measurement1;
    }


    JavaMemoryUseExperiment () {

        try {
            FileOutputStream fout= new FileOutputStream("output/stdout.log");
            FileOutputStream ferr= new FileOutputStream("output/stderr.log");

            TeeOutputStream multiOut= new TeeOutputStream(System.out, fout);
            TeeOutputStream multiErr= new TeeOutputStream(System.err, ferr);

            PrintStream stdout= new PrintStream(multiOut);
            PrintStream stderr= new PrintStream(multiErr);

            System.setOut(stdout);
            System.setErr(stderr);
        } catch (FileNotFoundException ex) {
            //Could not create/open the file
        }

        System.out.println("Java environment:");
        System.out.println("JVM architecture System.getProperty(\"sun.arch.data.model\") = "
                + System.getProperty("sun.arch.data.model"));
        System.out.println("System.getProperty(\"java.specification.version\") = "
                + System.getProperty("java.specification.version"));
        System.out.println("System.getProperty(\"java.version\" = "
                + System.getProperty("java.version"));
        System.out.println("System.getProperty(\"java.vm.version\") = "
                + System.getProperty("java.vm.version"));
        System.out.println("System.getProperty(\"java.runtime.version\") = "
                + System.getProperty("java.runtime.version"));


        int i; // iterator
        printLine(179);
        System.out.println(String.format(
                "| %75s | %29s | %20s | %20s | %19s |",
                "variable/object/array type (N="+N+")",
                "  total memory M (bytes)    ",
                " bytes per element ",
                "metadata [+ padding]",
                "scaling      "
        ));
        System.out.println(String.format(
                "| %75s | %9s %9s %9s | %20s | %20s | %19s |",
                "measurement",
                "m1 - m2",
                "m_obj",
                "m_deep",
                "       M/N       ",
                "M - N * ((int) M/N)",
                ""
        ));
        printLine(179);

        m1 = getFreeMemory();
        // no-op here
        m2 = getFreeMemory();
        if (m1 != m2) {
            printArrayMemoryUse("(nothing)");
        }


        m1 = getFreeMemory();
        int var_int = 129;
        // using var_int or not does not make a difference
//        System.out.println(var_int*2);
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_int);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_int, VisibilityFilter.ALL);
        printVariableMemoryUse("int");


        m1 = getFreeMemory();
        short var_short = 12;
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_short);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_short, VisibilityFilter.ALL);
        printVariableMemoryUse("short");


        m1 = getFreeMemory();
        long var_long = 123456789;
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_long);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_long, VisibilityFilter.ALL);
        printVariableMemoryUse("long");


        m1 = getFreeMemory();
        float var_float = 1234.56789f;
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_float);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_float, VisibilityFilter.ALL);
        printVariableMemoryUse("float");


        m1 = getFreeMemory();
        double var_double = 1234.56789;
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_double);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_double, VisibilityFilter.ALL);
        printVariableMemoryUse("double");


        m1 = getFreeMemory();
        char var_char = 'a';
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_char);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_char, VisibilityFilter.ALL);
        printVariableMemoryUse("char");


        m1 = getFreeMemory();
        boolean var_boolean = true;
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_boolean);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_boolean, VisibilityFilter.ALL);
        printVariableMemoryUse("boolean");



        m1 = getFreeMemory();
        byte var_byte = 100;
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_byte);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_byte, VisibilityFilter.ALL);
        printVariableMemoryUse("byte");


        m1 = getFreeMemory();
        Integer var_Integer = new Integer(12345);
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_Integer);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_Integer, VisibilityFilter.ALL);
        printVariableMemoryUse("Integer");


        m1 = getFreeMemory();
        Long var_Long = new Long(123456789);
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_Long);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_Long, VisibilityFilter.ALL);
        printVariableMemoryUse("Long");


        m1 = getFreeMemory();
        BigInteger var_BigInteger = new BigInteger("123456789");
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_BigInteger);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_BigInteger, VisibilityFilter.ALL);
        printVariableMemoryUse("BigInteger");


        m1 = getFreeMemory();
        BigDecimal var_BigDecimal = new BigDecimal(12345.6789);
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_BigDecimal);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_BigDecimal, VisibilityFilter.ALL);
        printVariableMemoryUse("BigDecimal");


        m1 = getFreeMemory();
        String var_literal_string = "foobar";
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_literal_string);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_literal_string, VisibilityFilter.ALL);
        printVariableMemoryUse("literal string \"foobar\"");



        m1 = getFreeMemory();
        String var_literal_object = new String("foobar");
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(var_literal_object);
        m_deep = MemoryUtil.deepMemoryUsageOf(var_literal_object, VisibilityFilter.ALL);
        printVariableMemoryUse("string object new String(\"foobar\")");


        m1 = getFreeMemory();
        int[] array_int = new int[N];
        for (i = 0; i < N; i++){
            array_int[i] = 128 + i;
        }
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_int);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_int, VisibilityFilter.ALL);
        printArrayMemoryUse("int[N]");
        array_int = null;


        m1 = getFreeMemory();
        short[] array_short = new short[N];
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_short);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_short, VisibilityFilter.ALL);
        printArrayMemoryUse("short[N]");
        array_short = null;


        m1 = getFreeMemory();
        long[] array_long = new long[N];
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_long);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_long, VisibilityFilter.ALL);
        printArrayMemoryUse("long[N]");
        array_long = null;


        m1 = getFreeMemory();
        float[] array_float = new float[N];
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_float);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_float, VisibilityFilter.ALL);
        printArrayMemoryUse("float[N]");
        array_float = null;


        m1 = getFreeMemory();
        double[] array_double = new double[N];
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_double);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_double, VisibilityFilter.ALL);
        printArrayMemoryUse("double[N]");
        array_double = null;


        m1 = getFreeMemory();
        char[] array_char = new char[N];
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_char);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_char, VisibilityFilter.ALL);
        printArrayMemoryUse("char[N]");
        array_char = null;


        m1 = getFreeMemory();
        boolean[] array_boolean = new boolean[N];
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_boolean);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_boolean, VisibilityFilter.ALL);
        printArrayMemoryUse("boolean[N]");
        array_boolean = null;

        m1 = getFreeMemory();
        byte[] array_byte = new byte[N];
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_byte);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_byte, VisibilityFilter.ALL);
        printArrayMemoryUse("byte[N]");
        array_byte = null;


        m1 = getFreeMemory();
        Integer[] array_Integer1 = new Integer[N];
        // TODO why m2 becomes smaller than m1 if the following is enabled?
//        for (i = 0; i < N; i++) {
//            array_Integer1[i] = new Integer(123);
//        }
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_Integer1);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_Integer1, VisibilityFilter.ALL);
        printArrayMemoryUse("Integer[N]");
        array_Integer1 = null;


        m1 = getFreeMemory();
        Integer[] array_Integer2 = new Integer[N];
        for (i = 0; i < N; i++) {
            array_Integer2[i] = new Integer(128 + i);  // 128+i so interning does not matter (or rather matters less?)
        }
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_Integer2);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_Integer2, VisibilityFilter.ALL);
        printArrayMemoryUse("Integer[N] and N Integer");
        array_Integer2 = null;


        m1 = getFreeMemory();
        Long[] array_Long = new Long[N];
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_Long);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_Long, VisibilityFilter.ALL);
        printArrayMemoryUse("Long[N]");
        array_Long = null;


        m1 = getFreeMemory();
        array_Long = new Long[N];
        for (i = 0; i < N; i++) {
            array_Long[i] = new Long(1280 + i);  // 128+i so interning does not matter (or rather matters less?)
        }
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_Long);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_Long, VisibilityFilter.ALL);
        printArrayMemoryUse("Long[N] and N Long");
        array_Long = null;



        m1 = getFreeMemory();
        BigInteger[] array_BigInteger = new BigInteger[N];
        for (i = 0; i < N; i++) {
            array_BigInteger[i] = new BigInteger(String.format("%s", 12345678 + i));  // 128+i so interning does not matter (or rather matters less?)
        }
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_BigInteger);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_BigInteger, VisibilityFilter.ALL);
        printArrayMemoryUse("BigInteger[N] and N BigInteger");
        array_BigInteger = null;


        m1 = getFreeMemory();
        BigDecimal[] array_BigDecimal1 = new BigDecimal[N];
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_BigDecimal1);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_BigDecimal1, VisibilityFilter.ALL);
        printArrayMemoryUse("BigDecimal[N]");
        array_BigDecimal1 = null;
        System.gc();



        m1 = getFreeMemory();
        BigDecimal[] array_BigDecimal2 = new BigDecimal[N];
        for (i = 0; i < N; i++) {
            array_BigDecimal2[i] = new BigDecimal(123456789.123456 + i);
        }
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_BigDecimal2);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_BigDecimal2, VisibilityFilter.ALL);
        printArrayMemoryUse("BigDecimal[N] and N BigDecimal");
        array_BigDecimal2 = null;



        m1 = getFreeMemory();
        String[] array_String1 = new String[N];
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_String1);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_String1, VisibilityFilter.ALL);
        printArrayMemoryUse("String[N]");
        array_String1 = null;


        m1 = getFreeMemory();
        String[] array_String2 = new String[N];
        for (i = 0; i < N; i++) {
            array_String2[i] = "abcdefghij";
        }
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_String2);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_String2, VisibilityFilter.ALL);
        printArrayMemoryUse("String[N] and N literal string \"abcdefghij\" (w/ string interning?)");
        array_String2 = null;



        m1 = getFreeMemory();
        String[] array_String3 = new String[N];
        for (i = 0; i < N; i++) {
            array_String3[i] = new String("abc0000000");
        }
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_String3);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_String3, VisibilityFilter.ALL);
        printArrayMemoryUse("String[N] and N new String(\"abc0000000\") (w/ string interning?)");
        array_String3 = null;



        m1 = getFreeMemory();
        String[] array_String4 = new String[N];
        for (i = 0; i < N; i++) {
            array_String4[i] = String.format("abc%07d", i);
        }
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_String4);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_String4, VisibilityFilter.ALL);
        printArrayMemoryUse("String[N] and N different strings String.format(\"abc%07d\", i)");
        array_String4 = null;


        m1 = getFreeMemory();
        EmptyClass[] array_EmptyClass = new EmptyClass[N];
        for (i = 0; i < N; i++) {
            array_EmptyClass[i] = new EmptyClass();
        }
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_EmptyClass);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_EmptyClass, VisibilityFilter.ALL);
        printArrayMemoryUse("EmptyClass[N] + N EmptyClass");
        array_EmptyClass = null;


        m1 = getFreeMemory();
        SimpleClass[] array_SimpleClass = new SimpleClass[N];
        for (i = 0; i < N; i++) {
            array_SimpleClass[i] = new SimpleClass();
        }
        m2 = getFreeMemory();
        m_obj = MemoryUtil.memoryUsageOf(array_SimpleClass);
        m_deep = MemoryUtil.deepMemoryUsageOf(array_SimpleClass, VisibilityFilter.ALL);
        printArrayMemoryUse("SimpleClass[N] + N SimpleClass");
        array_SimpleClass = null;

        printLine(179);

    }

    private static void printArrayMemoryUse(String type) {
        System.out.println(
                String.format(
                        "| %75s | %9d %9d %9d | %6d %6d %6d | %6d %6d %6d | %19s |",
                        type,
                        (m1-m2), m_obj, m_deep,
                        (m1-m2)/N, m_obj/N, m_deep/N,
                        (m1-m2) - N * ((int) (m1 - m2)/N), m_obj - N*((int) m_obj/N), m_deep - N*((int) m_deep/N),
                        (m1-m2) == m_deep ? String.format("%6d * N + %6d", m_deep/N, m_deep - N*((int) m_deep/N)) : "?"
                )
        );
    }

    private static void printVariableMemoryUse(String type) {
        System.out.println(
                String.format("| %75s | %9d %9d %9d | %6d %6d %6d |"
                                + String.format(String.format("%%0%dd", 22), 0).replace("0", " ") + "|"
                                + String.format(String.format("%%0%dd", 21), 0).replace("0", " ") + "|",
                        type,
                        (m1-m2), m_obj, m_deep,
                        (m1-m2), m_obj, m_deep
                )
        );
    }

    private static void printLine(int width) {
        System.out.println(
                String.format(String.format("%%0%dd", width), 0).replace("0", "-")
        );
    }

    public static void main(String[] args) {
        new JavaMemoryUseExperiment();
    }
}

