//<caret>
my.namespace.prototype.describe = function () {
};

describe('top describe', function () {
    it('should not be included iit', function () {
    });

    it('should not be excluded exit', function () {
    });

    it('should not be a describe()', function () {
    });

    it('should not be a ddescribe', function () {
    });

    xit('should be excluded it( iit( describe(', function () {
    });

    fit('should be included it( xit( describe( xdescribe(', function () {
    });

    describe('suite not excluded xdescribe(', function () {
    });

    describe('suite not included ddescribe(', function () {
    });

    xdescribe('suite excluded describe( it(', function () {
    });

    fdescribe('suite included describe( xdescribe( iit(', function () {
    });
});
