var b = "b";

describe("Isolation B", function () {

    it("test runs are isolated", function () {
        expect(b).toBe("b");
        expect(typeof a).toBe("undefined");
    });

});
