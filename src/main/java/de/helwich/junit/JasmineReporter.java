package de.helwich.junit;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * Called from JavaScript while executing the tests. The notifications are passed directly to a jUnit notifier object.
 * The description tree from test interpretation phase is used here. It is assumed that tests are executed by jasmine in
 * the same order than they are interpreted.
 * 
 * @author Hendrik Helwich
 */
public class JasmineReporter {

    /**
     * jUnit notifier.
     */
    private RunNotifier notifier;

    /**
     * Holds the description tree as list used for convenient synchronization of the tree.
     */
    private List<Description> descriptionStack;

    /**
     * Current stack element which is executed.
     */
    private int descriptionStackIndex;

    public void jasmineStarted(int totalSpecsDefined) {
        Description desc = descriptionStack.get(descriptionStackIndex++);
        notifier.fireTestStarted(desc);
    }

    public void jasmineDone() {
        Description desc = descriptionStack.remove(--descriptionStackIndex);
        notifier.fireTestFinished(desc);
        descriptionStack = null;
    }

    public void suiteStarted(String id, String description, String fullName, String status) {
        Description desc = descriptionStack.get(descriptionStackIndex++);
        if (!description.equals(desc.getDisplayName()) || !desc.isSuite()) {
            throw new RuntimeException("unexpected suite description");
        }
        notifier.fireTestStarted(desc);
    }

    public void suiteDone(String id, String description, String fullName, String status) {
        Description desc = descriptionStack.remove(--descriptionStackIndex);
        if (!description.equals(desc.getDisplayName()) || !desc.isSuite()) {
            throw new RuntimeException("unexpected suite description");
        }
        notifier.fireTestFinished(desc);
    }

    public void specStarted(String id, String description, String fullName, String[] failedExpectations) {
        Description desc = descriptionStack.get(descriptionStackIndex);
        if (!(description + "()").equals(desc.getDisplayName()) || !desc.isTest()) {
            throw new RuntimeException("unexpected test description");
        }
        notifier.fireTestStarted(desc);
    }

    public void specDone(String id, String description, String fullName, String[] failedExpectations, String status) {
        Description desc = descriptionStack.remove(descriptionStackIndex);
        if (!(description + "()").equals(desc.getDisplayName()) || !desc.isTest()) {
            throw new RuntimeException("unexpected test description");
        }
        if ("failed".equals(status)) {
            notifier.fireTestFailure(new Failure(desc, null));
        } else if ("pending".equals(status)) {
            notifier.fireTestIgnored(desc);
        } else {
            notifier.fireTestFinished(desc);
        }
    }

    public void setNotifier(RunNotifier notifier) {
        this.notifier = notifier;
    }

    public void setDescription(Description description) {
        descriptionStack = new ArrayList<Description>();
        traverse(descriptionStack, description);
        descriptionStackIndex = 0;
    }

    private void traverse(List<Description> descriptionStack, Description description) {
        descriptionStack.add(description);
        for (Description desc : description.getChildren()) {
            traverse(descriptionStack, desc);
        }
    }

}
