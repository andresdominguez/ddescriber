# ddescriber

The ddescriber for Jasmine is an idea plug-in to simplify unit testing in Jasmine (http://pivotal.github.com/jasmine/).

See a how-to video here: http://www.youtube.com/watch?v=xdSofu-lEMA

## New (April 2nd, 2015). Added support for Jasmine 2 and 1.

* Version 3.0 now it supports `fit` and `fdescribe` for Jasmine 2. 
* The old support for `iit` and `ddescribe` still works. Just make sure you click on the checkbox after launching the dialog.

With the plugin you can quickly change a `describe()` into a `fdescribe()` / `ddescribe()` /
`xdescribe()` and an `it()` into a `fit()` / `iit()` / `xit()` to make your test runner, such as 
Karma or JSTD, run a specific set of suites (describe()) and unit tests (it()).

You can launch the dialog by pressing Ctrl SHIFT D (Command Shift D on a Mac).

The dialog has the following features:

Change a single describe() or it() by hitting Enter or clicking on the OK button.
- Start typing to search the list.
- Hit Enter on the currently selected test or suite to transform it / revert it into and iit() and ddescribe()
- Click Clean file (Alt C) to clean all the ddescribe() / xdescribe() and iit() / xit() in the current file.
- Click Exclude (Alt X) to exclude a suite (xdescribe()) or unit test (xit())
- Click include (Alt I) to include a suite (ddescribe()) or unit test (iit()) 
- Click Go (Alt G) to jump to the currently selected test.
