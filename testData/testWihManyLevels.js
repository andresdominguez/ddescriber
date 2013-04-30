//<caret>
describe('suite1', function () {
    it('test1', function () {
    });

    describe("suite2", function () {
        it('test2', function () {
        });

        it('test3', function () {
        });
    });

    describe("suite3", function () {
        it('test4', function () {
        });

        describe("suite4", function () {
            it('test5', function () {
            });

            describe('suite5', function () {
                it('test6', function () {

                });

                xit('should be excluded 1', function () {

                });

                xdescribe('excluded 2', function () {
                    it('inner excluded', function () {

                    });
                });

            })
        });

    });

    describe('suite6', function () {
        it('test7', function () {
        });
    });
});

