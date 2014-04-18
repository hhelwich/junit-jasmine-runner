//noinspection ThisExpressionReferencesGlobalObjectJS
/**
 * Implement setTimeout()/clearTimeout() polyfills for nashorn (needed for jasmine asynchronous tests).
 *
 * @author Hendrik Helwich
 */

(function(global) { // create scope

    /**
     * Function string ids in the correct time order.
     * @type {Array.<string>}
     */
    var fnIds = [];

    /**
     * Maps function string ids to the actual functions.
     * @type {Object.<string, {start: number, fn: !function()}>}
     */
    var fns = {};

    /**
     * Current virtual time in milliseconds.
     * @type {number}
     */
    var time = 0;

    /**
     * Returns a new id string.
     *
     * @return {string}
     */
    var getId = (function() {
        var fnId = 0;
        return function() {
            return "" + (fnId++);
        };
    }());

    /**
     * Used to sort function ids by start time.
     *
     * @param {string} fid1
     * @param {string} fid2
     * @returns {number}
     */
    var timeCompare = function(fid1, fid2) {
        return fns[fid1].start - fns[fid2].start;
    };

    /**
     * Polyfill for browser/node.js function for nashorn.
     *
     * @param {!function()} fn
     * @param {number} ms
     * @returns {string}
     */
    var setTimeout = function(fn, ms) {
        // create id
        var fid = getId();
        // store function
        fnIds.push(fid);
        fns[fid] = {
            fn: fn,
            start: time + ms
        };
        // sort functions by start time
        fnIds.sort(timeCompare);
        return fid;
    };

    /**
     * Polyfill for browser/node.js function for nashorn.
     *
     * @param {string} fid
     */
    var clearTimeout = function(fid) {
        if (fns.hasOwnProperty(fid)) { // remove function
            delete fns[fid];
            var idx = fnIds.indexOf(fid);
            fnIds.splice(idx, 1);
        }
    };

    /**
     * Run all asynchronous functions.
     */
    setTimeout.wait = function wait() {
        if (fnIds.length > 0) {
            var fid = fnIds.splice(0, 1)[0];
            var fn = fns[fid];
            delete fns[fid];
            time = fn.start;
            fn.fn();
            wait();
        }
    };

    // export API

    global.setTimeout =  setTimeout;
    global.clearTimeout = clearTimeout;

}(this));