package de.helwich.junit;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;

/**
 * Called from JavaSript by jasmine mocks while interpreting the jasmine specs. Builds a spec description tree which can
 * be given to junit before tests are executed.
 * 
 * @author Hendrik Helwich
 */
public class JasmineDescriber {

    /**
     * Stack which is used to build a description tree. After the tests are interpreted, the stack holds the single
     * description tree element.
     */
    private final List<Description> descStack;

    /**
     * Can be set to true after the tests are interpreted to assure that no further tests are interpreted.
     */
    private boolean disabled = false;

    /**
     * Simple id counter used to give all descriptions a unique id.
     */
    private static int descId = 1;

    private void pushDescription(Description desc) {
        checkEnabled();
        Description top = descStack.get(descStack.size() - 1);
        top.addChild(desc);
        descStack.add(desc);
    }

    private Description popDescription() {
        checkEnabled();
        return descStack.remove(descStack.size() - 1);
    }

    private void checkEnabled() {
        if (disabled) {
            throw new RuntimeException("describer used at an unexpected time");
        }
    }

    public JasmineDescriber() {
        descStack = new ArrayList<Description>();
    }

    public void describeStart(String description) {
        pushDescription(Description.createSuiteDescription(description, descId++));
    }

    public void describeEnd() {
        popDescription();
    }

    public void testStart(String description) {
        pushDescription(Description.createTestDescription("", description, descId++));
        popDescription();
    }

    public Description getDescription() {
        if (descStack.size() != 1) {
            throw new RuntimeException("unexpected description state");
        }
        return popDescription();
    }

    public void disable() {
        disabled = true;
    }

    public void setRootName(String name) {
        if (descStack.size() != 0) {
            throw new RuntimeException("root description name must be set exactly one time at init time");
        }
        descStack.add(Description.createSuiteDescription(name, descId++));
    }
}
