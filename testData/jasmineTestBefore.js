describe('top describe', function () {
    it('first it', function () {
        console.log('WWWW');
    });

    it('second it', function () {
        console.log('WWWW');
    });

    ddescribe('inner describe', function () {
        it('inner it 1', function () {
            var a;
        });

        it('inner it 2', function () {
            <caret>
            var a;
        });

        iit('inner it 3', function () {
            var a;
        });
    });
});
