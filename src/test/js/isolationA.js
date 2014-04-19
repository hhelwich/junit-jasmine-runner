var a = "a";

describe("Isolation A", function () {

    it("test runs are isolated", function () {
        expect(a).toBe("a");
        expect(typeof b).toBe("undefined");
    });

});
