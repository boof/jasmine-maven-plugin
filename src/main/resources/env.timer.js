console.log('setNativeTimeout');

function setTimeout(fn, ms) {
    console.log('setTimeout');
    console.log(fn);
    console.log(ms);

    return setNativeTimeout(fn, ms);
}
function clearTimeout(n) {
//    Timer.clearTimer(n);
}
function clearInterval(n) {

}
function setInterval(fn, ms) {

}
