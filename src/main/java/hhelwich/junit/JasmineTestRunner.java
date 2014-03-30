package hhelwich.junit;

import java.io.File;
import java.net.URL;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * The jasmine / jUnit test runner.
 * 
 * @author Hendrik Helwich
 */
public class JasmineTestRunner extends Runner {

    private final JasmineTest info;
    private final Class<?> testClass;
    private final ScriptEngine nashorn;
    private final JasmineReporter reporter;
    private final Description description;

    public JasmineTestRunner(Class<?> testClass) {
        try {
            this.testClass = testClass;
            info = testClass.getAnnotation(JasmineTest.class);
            if (info == null) {
                throw new RuntimeException("annotation " + JasmineTest.class.getName() + " is missing on class "
                        + testClass.getName());
            }

            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            nashorn = scriptEngineManager.getEngineByName("nashorn");
            if (nashorn == null) {
                throw new RuntimeException("please use java 8");
            }

            if (info.browser()) {
                evalResource(nashorn, "/envjs/env.js");
            }

            evalResource(nashorn, "/jasmine/jasmine.js");
            evalResource(nashorn, "/jasmine/boot.js");

            JasmineDescriber describer = (JasmineDescriber) nashorn.eval("jasmine.junitDescriber = new (Java.type(\""
                    + JasmineDescriber.class.getName() + "\")); ");
            describer.setRootName(testClass.getName());

            evalResource(nashorn, "/hhelwich/junit/describer.js");

            for (String src : info.src()) {
                evalFile(nashorn, info.srcDir() + "/" + src + info.fileSuffix());
            }
            for (String test : info.test()) {
                evalFile(nashorn, info.testDir() + "/" + test + info.fileSuffix());
            }

            description = describer.getDescription();
            describer.disable();
            reporter = (JasmineReporter) nashorn.eval("jasmine.junitReporter = new (Java.type(\""
                    + JasmineReporter.class.getName() + "\")); ");
            reporter.setDescription(description);
            evalResource(nashorn, "/hhelwich/junit/reporter.js");
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Description getDescription() {
        return description;
    }

    private File projectDir() {
        String relPath = testClass.getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath + "../../");
        return targetDir;
    }

    private final Object evalResource(ScriptEngine nashorn, String name) {
        URL url = testClass.getResource(name);
        String src = url.toExternalForm();
        try {
            return nashorn.eval("load('" + src + "')");
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private final Object evalFile(ScriptEngine nashorn, String name) {
        File file = new File(projectDir(), name);
        String src = file.getAbsolutePath();
        try {
            return nashorn.eval("load('" + src + "')");
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            runThrows(notifier);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public void runThrows(RunNotifier notifier) throws ScriptException {
        reporter.setNotifier(notifier);
        nashorn.eval("jasmine.getEnv().execute();");
    }
}
