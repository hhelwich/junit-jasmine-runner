package hhelwich.junit;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation used to configure jasmine test runner.
 * 
 * @author Hendrik Helwich
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface JasmineTest {

    String srcDir() default "/src/main/javascript";

    String testDir() default "/src/test/javascript";

    String fileSuffix() default ".js";

    String[] src();

    String[] test();
}
