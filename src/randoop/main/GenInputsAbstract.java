package randoop.main;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import randoop.util.Randomness;
import randoop.util.Util;

import plume.Option;
import plume.OptionGroup;
import plume.Options;
import plume.Unpublicized;

/**
 * Container for Randoop options.
 *
 */
public abstract class GenInputsAbstract extends CommandHandler {

  public GenInputsAbstract(String command, String pitch,
      String commandGrammar, String where, String summary, List<String> notes, String input,
      String output, String example, Options options) {
    super(command, pitch, commandGrammar, where, summary, notes, input, output,
        example, options);
  }

  /**
   * The fully-qualified name of a class to test.
   * These classes are tested in addition to any specified using <tt>--classlist</tt>.
   */
  ///////////////////////////////////////////////////////////////////
  @OptionGroup ("Code under test")
  @Option("The fully-qualified name of a class under test")
  public static List<String> testclass = new ArrayList<String>();

  /**
   * The name of a file that lists classes to test.
   * 
   * In the file, each class under test is specified by its
   * fully-qualified name on a separate line.
   * See an <a href="https://raw.githubusercontent.com/randoop/randoop/master/doc/class_list_example.txt">example</a>.
   * These classes are tested in addition to any specified using <tt>--testclass</tt>.
   */
  @Option("The name of a file that lists classes under test")
  public static String classlist = null;

  // A relative URL like <a href="#specifying-methods"> works when this
  // Javadoc is pasted into the manual, but not in Javadoc proper.
  /**
   * The name of a file that lists methods to test.
   * 
   * In the file, each each method under test is specified on a separate
   * line. The list of methods given by this argument augment
   * any methods derived via the <tt>--testclass</tt> or
   * <tt>--classlist</tt> option.
   * 
   * <p>
   * A constructor line begins with <code>"cons :"</code> followed by the
   * classname, the string <code>&lt;init&gt;</code> and the constructor's
   * parameter types, enclosed in parentheses. Methods are specified in a
   * similar way. For example:
   * <pre class="code">
   * cons : Type0.&lt;init&gt;(Type1, Type2, ..., TypeN)
   * method : Type0.method_name(Type1, Type2, ..., TypeN)
   * </pre>
   * Each <code>Type<i>i</i></code> must be <b>fully-qualified</b> (include package names).
   *
   * <p>
   * See an <a href="https://raw.githubusercontent.com/randoop/randoop/master/doc/method_list_example.txt">example</a>.
   */
  @Option("The name of a file that lists methods under test")
  public static String methodlist = null;

  /**
   * File containing side-effect-free observer methods.
   * These are used to create regression assertions, and to prune tests.
   */
  @Option("File containing observer functions")
  // This file is used to populate RegressionCaptureVisitor.observer_map
  public static File observers = null;
  
  /**
   * Randoop will not attempt to directly call methods whose {@link
   * java.lang.reflect.Method#toString()} matches the regular expression
   * given.  This does not prevent indirect calls to such methods from
   * other, allowed methods.
   * <p>
   * 
   * Randoop only calls methods
   * that are specified by one of the <tt>--testclass</tt>,
   * <tt>-classlist</tt>, or <tt>--methodlist</tt> command-line options;
   * the purpose of <tt>--omitmethods</tt> is to override one of those other
   * command-line options.
   */
  @Option("Do not call methods that match regular expression <string>")
  public static Pattern omitmethods = null;
  
  /**
   * The name of a file that contains fully-qualified
   * field names to be excluded from test generation. Otherwise, Randoop
   * includes all public fields of a visible class.
   */
  @Option("The name of a file containing field names to omit from generated tests")
  public static String omit_field_list = null;
  
  /**
   * If the command line argument public_only is true, only public
   * classes/methods are considered visible.  If public_only is false
   * then any class/method that is not private is considered visible.
   * 
   * <p>
   * FIXME: This option outputs tests that do not compile. Until a fix
   *        is done (which probably involves reflective invocation),
   *        keep the option @Unpublicized. The option should
   *        probably be an enum with PUBLIC, PACKAGE, PUBLIC elements.
   */
  @Unpublicized  
  @Option("Specify whether to use only public members in tests")
  public static boolean public_only = true;

  @Option("Ignore class names specified by user that cannot be found")
  public static boolean silently_ignore_bad_class_names = false;
  
  /** Whether to output error-revealing tests. */
  @OptionGroup("Test classification")
  @Option("Whether to output error-revealing tests")
  public static boolean no_error_revealing_tests = false;

  /** Whether to output regression tests. */
  @Option("Whether to output regression tests")
  public static boolean no_regression_tests = false;

  /**
   * Whether to include assertions in regression tests.
   * If false, then the regression tests contain no assertions
   * (except that if the test throws an exception, it should continue to
   * throw an exception of the same type).
   * Tests without assertions can be used to exercise the code, but they
   * do not enforce any particular behavior, such as values returned.
   */
  @Option("Whether to include assertions in regression tests")
  public static boolean no_regression_assertions = false;

  /**
   * The possible values for exception behavior types.
   * The order INVALID, ERROR, EXPECTED should be maintained.
   */
  public static enum BehaviorType {
    /** Occurrence of exception should be considered invalid */
    INVALID,
    /** Occurrence of exception should be considered an error */
    ERROR,
    /** Occurrence of exception should be considered expected behavior */
    EXPECTED
  }
  
  /**
   * If a test throws a checked exception, should it be included in the 
   * error-revealing test suite (value: ERROR), regression test suite 
   * (value: EXPECTED), or should it be discarded (value: INVALID)? 
   */
  @Option("Whether checked exception is an ERROR, EXPECTED or INVALID")
  public static BehaviorType checked_exception = BehaviorType.EXPECTED;
  
  /**
   * If a test throws a unchecked exception, should the test be included in the 
   * error-revealing test suite (value: ERROR), regression test suite 
   * (value: EXPECTED), or should it be discarded (value: INVALID)? 
   * <p>
   * The arguments --npe-on-null-input and --oom-exception handle special cases of 
   * unchecked exceptions.
   */
  @Option("Whether unchecked exception is an ERROR, EXPECTED or INVALID")
  public static BehaviorType unchecked_exception = BehaviorType.EXPECTED;
  
  /** 
   * If a test where a <code>null</code> value is given as an input throws a 
   * <code>NullPointerException</code>, should the test be be included in the 
   * error-revealing test suite (value: ERROR), regression test suite 
   * (value: EXPECTED), or should it be discarded (value: INVALID)?
   * <p>
   * Tests with other occurrences of <code>NullPointerException</code> are
   * handled by the argument --unchecked-exception.
   */
  @Option("Whether NullPointerException on null inputs is an ERROR, EXPECTED or INVALID")
  public static BehaviorType npe_on_null_input = BehaviorType.INVALID;
  
  /**
   * If a test throws an <code>OutOfMemoryError</code> exception, should it be 
   * included in the error-revealing test suite (value: ERROR), regression test 
   * suite (value: EXPECTED), or should it be discarded (value: INVALID)?
   */
  @Option("Whether OutOfMemoryException is an ERROR, EXPECTED or INVALID")
  public static BehaviorType oom_exception = BehaviorType.INVALID;
  
  
  /**
   * Maximum number of seconds to spend generating tests.
   * 
   * Test generation stops when either the time limit (--timelimit) is reached, 
   * OR the number of generated sequences reaches the input limit (--inputlimit), 
   * OR the number of error-revealing and regression tests reaches the output 
   * limit (--outputlimit).
   *
   * The default value is appropriate for generating tests for a single
   * class in the context of a larger program, but is too small to be effective
   * for generating tests for an entire program.
   *
   * Note that if you use this option, Randoop is nondeterministic: it
   * may generate different test suites on different runs.
   */
  ///////////////////////////////////////////////////////////////////
  @OptionGroup("Limiting test generation")
  @Option("Maximum number of seconds to spend generating tests")
  public static int timelimit = 100;

  /**
   * The maximum number of regression and error-revealing tests to output.
   * 
   * Test generation stops when either the time limit (--timelimit) is reached, 
   * OR the number of generated sequences reaches the input limit (--inputlimit), 
   * OR the number of error-revealing and regression tests reaches the output 
   * limit (--outputlimit).
   * 
   * This option affects how many tests will occur in the output, as opposed to
   * --inputlimit, which affects the number of test method candidates that are 
   * generated internally. This option is a better choice for controlling the 
   * tests you get, because the number of candidates generated will always be 
   * larger since redundant and invalid tests are filtered. 
   * 
   * However, the actual number of tests in the output is still likely to be 
   * smaller than this limit due to the current implementation.
   */
  @Option ("Maximum number of tests to ouput; contrast to --inputlimit")
  public static int outputlimit = 100000000;

  /**
   * Maximum number of test method candidates generated.
   * 
   * Test generation stops when either the time limit (--timelimit) is reached, 
   * OR the number of generated sequences reaches the input limit (--inputlimit), 
   * OR the number of error-revealing and regression tests reaches the output 
   * limit (--outputlimit).  
   * The number of tests output will be smaller than then number of test 
   * candidates generated, because redundant and illegal tests may be discarded.
   * 
   * The --outputlimit command-line option is usually more appropriate than
   * --inputlimit.
   */
  @Option("Maximum number of tests generated")
  public static int inputlimit = 100000000;

  /** Do not generate tests with more than this many statements */
  @Option("Do not generate tests with more than <int> statements")
  public static int maxsize = 100;

  /**
   * Use null with the given frequency as an argument to method calls.
   * 
   * For example, a null ratio of 0.05 directs Randoop to use
   * <code>null</code> as an input 5 percent of the time when a
   * non-<code>null</code> value of the appropriate type is available.
   * 
   * Unless --forbid_null is true, a <code>null</code> value will still be used 
   * if no other value can be passed as an argument even if --null-ratio=0.
   * 
   * Randoop never uses <code>null</code> for receiver values.
   */
  ///////////////////////////////////////////////////////////////////
  @OptionGroup("Values used in tests")
  @Option("Use null as an input with the given frequency")
  public static double null_ratio = 0;

  /**
   * Do not use <code>null</code> as input to methods or constructors when no 
   * other argument value can be generated.
   * 
   * If true, Randoop will not generate a test when unable to find a non-null
   * value of appropriate type as an input. This could result in certain class
   * members being untested.
   * 
   * Does not affect the behavior based on --null_ratio, which independently 
   * determines the frequency that <code>null</code> is used as an input.
   */
  @Option("Never use null as input to methods or constructors")
  public static boolean forbid_null = true;
  
  /**
   * A file containing literal values to be used as inputs to methods under test.
   * 
   * Literals in these files are used in addition to all other constants in the 
   * pool. For the format of this file, see documentation in class 
   * {@link randoop.LiteralFileReader}. The special value "CLASSES" (with no 
   * quotes) means to read literals from all classes under test.
   */
  @Option("A file containing literal values to be used as inputs to methods under test")
  public static List<String> literals_file = new ArrayList<String>(); 


  /**
   * How to use literal values that are specified via the
   * <tt>--literals-file</tt> command-line option.
   * @see ClassLiteralsMode
   */
  @Option("How to use literal values specified via --literals-file: ALL, PACKAGE, CLASS, or NONE")
  public static ClassLiteralsMode literals_level = ClassLiteralsMode.CLASS;
  
  /**
   * The possible values of the literals_level command-line argument.
   * @see #literals_level
   */
  public static enum ClassLiteralsMode {
    /** do not use literals specified in a literals file */
    NONE,
      /** a literal for a given class is used as input only to methods of that class */
      CLASS,
      /** a literal is used as input to methods of any classes in the same package */
      PACKAGE,
      /** each literal is used as input to any method under test */
      ALL;
  }
  
  // Implementation note: when checking whether a String S exceeds the given
  // maxlength, we test if StringEscapeUtils.escapeJava(S), because this is
  // the length of the string that will atually be printed out as code.
  /**
   * Maximum length of strings in generated tests, including in assertions.
   * Strings longer than
   * 65KB (or about 10,000 characters) may be rejected by the Java
   * compiler, according to the Java Virtual Machine specification.
   */
  @Option("Maximum length of Strings in generated tests")
  public static int string_maxlen = 10000;
 

  ///////////////////////////////////////////////////////////////////
  @OptionGroup("Varying the nature of generated tests")
  @Option("Specifies initialization routine (class.method)")
  public static String init_routine = null;
  
  /**
   * Try to reuse values from a sequence with the given frequency.
   * 
   * If an alias ratio is given, it should be between 0 and 1.
   * 
   * A ratio of 0 results in tests where each value created within a test input is typically used at most once
   * as an argument in a method call. A ratio of 1 tries to maximize the number of times
   * values are used as inputs to parameters within a test. 
   */
  @Option("Reuse values with the given frequency")
  public static double alias_ratio = 0;

  /**
   * Favor shorter sequences when assembling new sequences out of old ones.
   *
   * Randoop generates new tests by combining old previously-generated tests.
   * If this option is given, tests with fewer calls are given greater weight during
   * its random selection. This has the overall effect of producing smaller JUnit tests.
   */
  @Option("Favor shorter tests during generation")
  public static boolean small_tests = false;

  /**
   * Clear the component set each time it contains the given number of inputs.
   *
   * <p>
   * Randoop stores previously-generated tests in a "component" set, and uses them to
   * generate new tests. Setting this variable to a small number can sometimes result
   * in a greater variety of tests generated during a single run.
   * </p>
   */
  @Option("Clear the component set when it gets this big")
  public static int clear = 100000000;

  
  
  ///////////////////////////////////////////////////////////////////
  @OptionGroup ("Outputting the JUnit tests")
  
  /** Maximum number of tests to write to each JUnit file */
  @Option("Maximum number of tests to write to each JUnit file")
  public static int testsperfile = 500;

  /** Base name (no ".java" suffix) of the JUnit file containing error-revealing tests */
  @Option("Base name of the JUnit file(s) containing error-revealing tests")
  public static String error_test_filename = "ErrorTest";

  /** Base name (no ".java" suffix) of the JUnit file containing regression tests */
  @Option("Base name of the JUnit file(s) containing regression tests")
  public static String regression_test_filename = "RegressionTest";

  /** Name of the package for the generated JUnit files */
  @Option("Name of the package for the generated JUnit files")
  public static String junit_package_name = "";

  /** Name of the directory to which JUnit files should be written */
  @Option("Name of the directory to which JUnit files should be written")
  public static String junit_output_dir = null;
  
  @Option("Run Randoop but do not create JUnit tests")
  public static boolean dont_output_tests = false;
  
  /**
   * Indicate which classes that any test written to output must use. 
   * Only tests that use members of classes whose names match the regular 
   * expression are written to the test suites. 
   */
  @Option("Regular expression for names of classes that any test written to output must use")
  public static Pattern include_only_classes = null;
  
  /**
   * Whether to use JUnit's standard reflective mechanisms for invoking
   * tests.  JUnit's reflective invocations can interfere with code
   * instrumentation, such as by the DynComp tool.  If that is a problem,
   * then set this to false and Randoop will output tests that use direct
   * method calls instead of reflection.  The tests will execute methods and
   * assertions, but won't be JUnit suites.
   */
  @Option("If true, use JUnit's reflective invocation; if false, use direct method calls")
  public static boolean junit_reflection_allowed = true;


  ///////////////////////////////////////////////////////////////////
  @OptionGroup("Runtime environment")
  // We do this rather than using java -D so that we can easily pass these
  // to other JVMs
  @Option("-D Specify system properties to be set (similar to java -Dx=y)")
  public static List<String> system_props = new ArrayList<String>();
  
  /**
   * Specify an extra command for recursive JVM calls that Randoop spawns.
   * The argument to the --agent option is the entire extra JVM command.  A
   * typical invocation of Randoop might be:
   * <pre>java -javaagent:<em>jarpath</em>=<em>args</em> randoop.main.Main gentests --agent="-javaagent:<em>jarpath</em>=<em>args</em>"</pre>
   */
  @Option("Specify an extra command for recursive JVM calls")
  public static String agent = null;

  @Option("specify the memory size (in megabytes) for recursive JVM calls")
  public static int mem_megabytes = 1000;

  @Option("Capture all output to stdout and stderr")
  public static boolean capture_output = false;



  ///////////////////////////////////////////////////////////////////
  // I don't see how to create the serialized files, only write to them.
  // Maybe the writing code has bit-rotted?
  @OptionGroup("Serialized input/output of generated tests")  
  @Option("Read serialized test inputs from the given file")
  public static List<String> componentfile_ser = new ArrayList<String>();

  @Option("Read serialized test inputs from the given file (text-based)")
  public static List<String> componentfile_txt = new ArrayList<String>();

  /**
   * Output components (serialized, GZIPPED) to the given file.
   * 
   * Suggestion: use a .gz suffix in file name.
   */
  @Option("Output components (serialized, GZIPPED) to the given file.")
  public static String output_components = null;

  /**
   * Output tests (sequences plus checkers) in serialized form to the given file.
   * 
   * Suggestion: use a .gz suffix in file name.
   */
  @Option("Output tests (sequences plus checkers) in serialized form to the given file.")
  public static String output_tests_serialized = null;

  /**
   * The random seed to use in the generation process.
   *
   * Note that Randoop is deterministic:  running it twice will produce the
   * same test suite.  If you want to produced multiple different test
   * suites, run Randoop multiple times with a different random seed.
   */
  ///////////////////////////////////////////////////////////////////
  @OptionGroup("Controlling randomness")
  @Option("The random seed to use in the generation process")
  public static int randomseed = (int) Randomness.SEED;

  
  ///////////////////////////////////////////////////////////////////
  @OptionGroup("Notifications")
  
  @Option("Do not display progress update message to console")
  public static boolean noprogressdisplay = false;

  @Option("Display progress message every <int> milliseconds")
  public static long progressinterval = 5000;


  /**
   * Install the given runtime visitor. See class randoop.ExecutionVisitor.
   */
  ///////////////////////////////////////////////////////////////////
  @OptionGroup(value="Advanced extension points")
  @Option("Install the given runtime visitor")
  public static List<String> visitor = new ArrayList<String>();

  
  ///////////////////////////////////////////////////////////////////
  @OptionGroup(value="Logging and troubleshooting Randoop")
  @Option("Perform expensive internal checks (for Randoop debugging)")
  public static boolean debug_checks = false;

  /**
   * Name of a file to which to log lots of information.
   * If not specified, no logging is done.
   */
  @Option("<filename> Name of a file to which to log lots of information")
  public static FileWriter log = null;
  
  ///////////////////////////////////////////////////////////////////
  // Options used when testing Randoop.
  
  /**
   * Create sequences but never execute them. Used to test performance of
   * Randoop's sequence generation code.
   */
  @Unpublicized
  @Option("Create sequences but never execute them")
  public static boolean dontexecute = false;


   
  /**
   * Whether to use the long format for outputting JUnit tests.
   * The long format emits exactly one line per statement, including
   * primitive declarations, and uses boxed primitives. This option is used
   * in the branch-directed generation project.
   */
  ///////////////////////////////////////////////////////////////////
  // These options are only used for the branch-directed generation
  // research project.
  @OptionGroup(value="Branch-directed generation", unpublicized=true)
  @Unpublicized
  /**
   * In long format, primitive values are stored in variables and the
   * variables are used, as in "int x = 3 ; foo(x)".  In short format,
   * primitive values are directly added to methods, as in "foo(3)".
   */
  @Option("Use long format for outputting JUnit tests.")
  public static boolean long_format = false;

  @Unpublicized
  @Option("Output branch->witness-sequences map")
  public static String output_covmap = null;

  @Unpublicized
  @Option("Output witness sequences for coverage branches")
  public static boolean output_cov_witnesses = false;

  @Unpublicized
  @Option("Whenever an object is called for, use an integer")
  public static boolean always_use_ints_as_objects = false;
  
  @Unpublicized
  @Option("The name of a file containing the list of coverage-instrumented classes")
  public static String coverage_instrumented_classes = null;

  @Unpublicized
  @Option("Output covered branches to the given text file")
  public static String output_branches = null;
  
  ///////////////////////////////////////////////////////////////////
  // This is only here to keep the ICSE07ContainersTest working
  // TODO Need to decide to keep the heuristic that uses this in ForwardGenerator
  @OptionGroup(value="Pacheco thesis", unpublicized=true)
  @Unpublicized
  @Option("Use heuristic that may randomly repeat a method call several times")
  public static boolean repeat_heuristic = false;
  
  /**
   * Check that the options given satisfy any specified constraints, and fail if they do not.
   */
  public void checkOptionsValid() {
    
    if (alias_ratio < 0 || alias_ratio > 1) {
      throw new RuntimeException("Alias ratio must be between 0 and 1, inclusive.");
    }

    if (null_ratio < 0 || null_ratio > 1) {
      throw new RuntimeException("Null ratio must be between 0 and 1, inclusive.");
    }

    if (maxsize <= 0) {
      throw new RuntimeException("Maximum sequence size must be greater than zero but was " + maxsize);
    }    
  }

  List<Class<?>> findClassesFromArgs(Options printUsageTo) {
    List<Class<?>> classes = new ArrayList<Class<?>>();

    if (classlist != null) {
      File classListingFile = new File(classlist);
      try {
        classes.addAll(ClassReader.getClassesForFile(classListingFile));
      } catch (Exception e) {
        String msg = Util.toNColsStr("ERROR while reading list of classes to test: " + e.getMessage(), 70);
        System.out.println(msg);
        System.exit(1);
      }
    }
    
    ClassNameErrorHandler errorHandler = new ThrowClassNameError();
    if (silently_ignore_bad_class_names) {
      errorHandler = new WarnOnBadClassName();
    }
    classes.addAll(ClassReader.getClassesForNames(testclass, errorHandler));

    return classes;
  }
}
