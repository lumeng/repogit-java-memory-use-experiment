# Empirically Measured Memory Use of some Java Data

Meng LU `<lumeng.dev@gmail.com>`  
July 3, 2014

Java environment:

System.getProperty("sun.arch.data.model") = 64
System.getProperty("java.specification.version") = 1.6
System.getProperty("java.version" = 1.6.0_65
System.getProperty("java.vm.version") = 20.65-b04-462
System.getProperty("java.runtime.version") = 1.6.0_65-b14-462-11M4609

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
|                                                             Empirically Measured Memory Use of some Java Data Structures                                                        |
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
| m1, m2 | Runtime.getRuntime().freeMemory()                                                   |                                                                                  |
| m1-m2  |                                                                                     | decrease in free memory                                                          |
| m_obj  | com.javamex.classmexer.MemoryUtil.memoryUsageOf(<object>)                           | equivalent to java.lang.instrument.Instrumentation.getObjectSize()               |
| m_deep | com.javamex.classmexer.MemoryUtil.deepMemoryUsageOf(<object>, VisibilityFilter.ALL) | recursively use Instrumentation.getObjectSize() to include referenced objects    |
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

|                                          variable/object/array type (N=800) |    total memory M (bytes)     |   bytes per element  | metadata [+ padding] |       scaling       |
|                                                                 measurement |   m1 - m2     m_obj    m_deep |           M/N        |  M - N * ((int) M/N) |                     |
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
|                                                                         int |         0        16        16 |      0     16     16 |                      |                     |
|                                                                       short |         0        16        16 |      0     16     16 |                      |                     |
|                                                                        long |         0        24        24 |      0     24     24 |                      |                     |
|                                                                       float |         0        16        16 |      0     16     16 |                      |                     |
|                                                                      double |         0        24        24 |      0     24     24 |                      |                     |
|                                                                        char |         0        16        16 |      0     16     16 |                      |                     |
|                                                                     boolean |         0        16        16 |      0     16     16 |                      |                     |
|                                                                        byte |         0        16        16 |      0     16     16 |                      |                     |
|                                                                     Integer |        24        16        16 |     24     16     16 |                      |                     |
|                                                                        Long |        24        24        24 |     24     24     24 |                      |                     |
|                                                                  BigInteger |        64        40        64 |     64     40     64 |                      |                     |
|                                                                  BigDecimal |      2848        40       120 |   2848     40    120 |                      |                     |
|                                                     literal string "foobar" |         0        32        64 |      0     32     64 |                      |                     |
|                                          string object new String("foobar") |        32        32        64 |     32     32     64 |                      |                     |
|                                                                      int[N] |      3216      3216      3216 |      4      4      4 |     16     16     16 |      4 * N +     16 |
|                                                                    short[N] |      1616      1616      1616 |      2      2      2 |     16     16     16 |      2 * N +     16 |
|                                                                     long[N] |      6416      6416      6416 |      8      8      8 |     16     16     16 |      8 * N +     16 |
|                                                                    float[N] |      3216      3216      3216 |      4      4      4 |     16     16     16 |      4 * N +     16 |
|                                                                   double[N] |      6416      6416      6416 |      8      8      8 |     16     16     16 |      8 * N +     16 |
|                                                                     char[N] |      1616      1616      1616 |      2      2      2 |     16     16     16 |      2 * N +     16 |
|                                                                  boolean[N] |       816       816       816 |      1      1      1 |     16     16     16 |      1 * N +     16 |
|                                                                     byte[N] |       816       816       816 |      1      1      1 |     16     16     16 |      1 * N +     16 |
|                                                                  Integer[N] |      3216      3216      3216 |      4      4      4 |     16     16     16 |      4 * N +     16 |
|                                                    Integer[N] and N Integer |     22416      3216     16016 |     28      4     20 |     16     16     16 |                   ? |
|                                                                     Long[N] |      3216      3216      3216 |      4      4      4 |     16     16     16 |      4 * N +     16 |
|                                                          Long[N] and N Long |     22416      3216     22416 |     28      4     28 |     16     16     16 |     28 * N +     16 |
|                                              BigInteger[N] and N BigInteger |     54416      3216     54416 |     68      4     68 |     16     16     16 |     68 * N +     16 |
|                                                               BigDecimal[N] |      3216      3216      3216 |      4      4      4 |     16     16     16 |      4 * N +     16 |
|                                              BigDecimal[N] and N BigDecimal |     92816      3216     92816 |    116      4    116 |     16     16     16 |    116 * N +     16 |
|                                                                   String[N] |      3216      3216      3216 |      4      4      4 |     16     16     16 |      4 * N +     16 |
|          String[N] and N literal string "abcdefghij" (w/ string interning?) |      3216      3216      3288 |      4      4      4 |     16     16     88 |                   ? |
|             String[N] and N new String("abc0000000") (w/ string interning?) |     28816      3216     28856 |     36      4     36 |     16     16     56 |                   ? |
|               String[N] and N different strings String.format("abc%07d", i) |     60816      3216     60816 |     76      4     76 |     16     16     16 |     76 * N +     16 |
|                                                EmptyClass[N] + N EmptyClass |     22416      3216     16032 |     28      4     20 |     16     16     32 |                   ? |
|                                              SimpleClass[N] + N SimpleClass |     28816      3216     28832 |     36      4     36 |     16     16     32 |                   ? |
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
