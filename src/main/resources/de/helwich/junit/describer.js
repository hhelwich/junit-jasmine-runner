//noinspection ThisExpressionReferencesGlobalObjectJS
/**
 * Mock jasmine functions to get test information before execution of tests.
 *
 * @author Hendrik Helwich
 */
(function(global){

    var describer = jasmine.junitDescriber;

	var mocks = {
		describe: function(name, func) {
			describer.describeStart(name);
			var ret = orig.describe.apply(this, arguments);
			describer.describeEnd();
			return ret;
		},
		it: function(description, func) {
			describer.testStart(description);
			return orig.it.apply(this, arguments);
        },
		xdescribe: function(name, func) {},
        xit: function(description, func) {
			describer.testStart(description);
			return orig.xit.apply(this, arguments);
        }
	};

	var orig = {};
	for (var name in mocks) {
		if (mocks.hasOwnProperty(name)) {
			orig[name] = global[name];
			global[name] = mocks[name];
		}
	}

}(this));
