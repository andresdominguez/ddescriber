/**
 * Some comments.
 */
//<caret>
describe('top describe', function () {
    var a, b, c;

    function someFn(foo, bar) {

    }

    var otherFn = function (hey) {
    };

    it('first it', function () {
        console.log('WWWW');
    });

    it('second it', function () {
        console.log('WWWW');
    });

    fdescribe('inner describe', function () {
        it('inner it 1', function () {
            var a;
        });

        it('inner it 2', function () {

            var a;
        });

        fit('inner it 3', function () {
            var a;
        });
    });
});
