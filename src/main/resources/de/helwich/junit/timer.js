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
     * @type {Object.<string, function()>}
     */
    var fns = {};

    /**
     * Function counter used to create function string ids.
     * @type {number}
     */
    var fnId = 0;

    /**
     * Current virtual time in milliseconds.
     * @type {number}
     */
    var time = 0;

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
        var fid = "" + fnId;
        fnId += 1;
        // calculate start time
        var start = time + ms;
        // store function
        fnIds.push(fid);
        fns[fid] = {
            fn: fn,
            start: start
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
    var wait = function() {
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

    setTimeout.wait = wait;
    global.setTimeout =  setTimeout;
    global.clearTimeout = clearTimeout;

}(this));