package hhelwich.junit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class JasmineTestRunner extends Runner {

    public JasmineTestRunner(Class<?> testClass) {}

    public Description getDescription(String string) {
        Description dt3 = Description.createTestDescription(string + "sub aeoaeiiiiiiiiiiioa222ie", string + "aeo");
        Description dt = Description.createTestDescription(string + "sub aeoaeoaie", string + "suaeoaeob aeoaeoaie");
        Description dt2 = Description.createTestDescription(string + "sub aeoaeoa222ie", string
                + "suaeoaeob ae2222oaeoaie");
        dt2.addChild(dt3);
        Description description2 = Description.createSuiteDescription(string + "sub");
        description2.addChild(dt);
        description2.addChild(dt2);
        Description description = Description.createSuiteDescription(string);
        description.addChild(description2);
        return description;
    }

    @Override
    public Description getDescription() {
        Description root = Description.createSuiteDescription("rooooooot");
        root.addChild(getDescription("a"));
        root.addChild(getDescription("b"));
        root.addChild(getDescription("a"));

        return root;
    }

    @Override
    public void run(RunNotifier notifier) {}

}
