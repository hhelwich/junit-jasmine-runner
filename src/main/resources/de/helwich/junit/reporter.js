/**
 * Delegate jasmine test notifications to Java reporter.
 *
 * @author Hendrik Helwich
 */
(function(){

    var junitReporter = jasmine.junitReporter;

    var reporter = {
        jasmineStarted: function(options) {
            //print("jasmineStarted "+JSON.stringify(options));
            junitReporter.jasmineStarted(options.totalSpecsDefined);
        },
        jasmineDone: function() {
            //print("jasmineDone");
            junitReporter.jasmineDone();
        },
        suiteStarted: function(result) {
            //print("suiteStarted "+JSON.stringify(result));
            junitReporter.suiteStarted(result.id, result.description, result.fullName, result.status);
        },
        suiteDone: function(result) {
            //print("suiteDone"+JSON.stringify(result));
            junitReporter.suiteDone(result.id, result.description, result.fullName, result.status);
        },
        specStarted: function(result) {
            //print("specStarted"+JSON.stringify(result));
            // JS stack isn't particularly useful right now because it doesn't show lines in the test file itself.
            var failureMessages = result.failedExpectations.map(function(failedExpectation) { return failedExpectation.message; });
            // print("Failure messages: " + failureMessages);
            junitReporter.specStarted(result.id, result.description, result.fullName, failureMessages);
        },
        specDone: function(result) {
            // print("specDone"+JSON.stringify(result));
            // JS stack isn't particularly useful right now because it doesn't show lines in the test file itself.
            var failureMessages = result.failedExpectations.map(function(failedExpectation) { return failedExpectation.message; });
            // print("Failure messages: " + failureMessages);
            junitReporter.specDone(result.id, result.description, result.fullName, failureMessages, result.status);
        }
    };

    var env = jasmine.getEnv();
    env.addReporter(reporter);

}());
