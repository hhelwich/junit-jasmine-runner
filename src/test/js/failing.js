/**
We want to demonstrate the behavior of the test framework when a test fails.
 */
describe("A failing suite", function() {
  // Enable this test to see how the Jasmine test runner handles test failures.
  xit("contains spec with a failing expectation", function() {
    expect(true).toBe(false);
  });
});
