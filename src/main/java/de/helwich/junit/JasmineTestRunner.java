package de.helwich.junit;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jscover2.instrument.Configuration;
import jscover2.instrument.Instrumenter;
import jscover2.report.JSCover2CoverageSummary;
import jscover2.report.JSCover2Data;
import jscover2.report.text.TextReport;
import org.apache.commons.io.FileUtils;
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
    private Configuration config = new Configuration();

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
            } else {
                evalResource(nashorn, "/de/helwich/junit/timer.js");
            }

            evalResource(nashorn, "/jasmine/jasmine.js");
            evalResource(nashorn, "/jasmine/boot.js");

            JasmineDescriber describer = (JasmineDescriber) nashorn.eval("jasmine.junitDescriber = new (Java.type(\""
                    + JasmineDescriber.class.getName() + "\")); ");
            describer.setRootName(testClass.getName());

            evalResource(nashorn, "/de/helwich/junit/describer.js");

            for (String src : info.src()) {
                evalFile(nashorn, info.srcDir() + "/" + src + info.fileSuffix(), info.coverage());
            }
            for (String test : info.test()) {
                evalFile(nashorn, info.testDir() + "/" + test + info.fileSuffix());
            }

            description = describer.getDescription();
            describer.disable();
            reporter = (JasmineReporter) nashorn.eval("jasmine.junitReporter = new (Java.type(\""
                    + JasmineReporter.class.getName() + "\")); ");
            reporter.setDescription(description);
            evalResource(nashorn, "/de/helwich/junit/reporter.js");
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
        return evalFile(nashorn, name, false);
    }

    private final Object evalFile(ScriptEngine nashorn, String name, boolean collectCoverage) {
        try {
            File file = new File(projectDir().getCanonicalFile(), name);
            if (collectCoverage) {
                Instrumenter instrumenter = new Instrumenter(config);
                String code = FileUtils.readFileToString(new File("."+name), Charset.defaultCharset());
                String instrumented = instrumenter.instrument(name, code);
                return nashorn.eval(instrumented);
            } else
                return nashorn.eval(new FileReader(file));
        } catch (Exception e) {
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
        if (info.browser()) {
            nashorn.eval("Envjs.wait()");
        } else {
            nashorn.eval("setTimeout.wait()");
        }
        boolean coverageVarFound = (boolean)nashorn.eval("typeof " + config.getCoverVariableName() + " === 'object'");
        if (info.coverage() && coverageVarFound) {
            //printJSON(nashorn);
            printCoverage(nashorn);
        }
    }

    private void printCoverage(ScriptEngine nashorn) {
        try {
            JSCover2Data jscover2Data = new JSCover2Data((ScriptObjectMirror) nashorn.eval(config.getCoverVariableName()));
            JSCover2CoverageSummary coverageSummary = new JSCover2CoverageSummary(jscover2Data);
            TextReport report = new TextReport();
            System.out.println(report.getTableFormattedFileSummary(coverageSummary));
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    private void printJSON(ScriptEngine nashorn) throws ScriptException {
        String json = (String) nashorn.eval("JSON.stringify("+config.getCoverVariableName()+")");
        System.out.println("json = " + json);
    }
}
