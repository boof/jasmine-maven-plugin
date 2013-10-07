(function() {
    var jasmineEnv = jasmine.getEnv();
    jasmineEnv.updateInterval = 1000;

    var surefireReporter = new jasmine.SurefireReporter();

    jasmineEnv.addReporter(surefireReporter);

//    jasmineEnv.specFilter = function(spec) {
//        return surefireReporter.specFilter(spec);
//    };

    try {
        jasmineEnv.execute();
    } catch (e) {
        console.log(e);
    }

})();
