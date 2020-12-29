var stats = {
    type: "GROUP",
name: "Global Information",
path: "",
pathFormatted: "group_missing-name-b06d1",
stats: {
    "name": "Global Information",
    "numberOfRequests": {
        "total": "960",
        "ok": "0",
        "ko": "960"
    },
    "minResponseTime": {
        "total": "382",
        "ok": "-",
        "ko": "382"
    },
    "maxResponseTime": {
        "total": "10446",
        "ok": "-",
        "ko": "10446"
    },
    "meanResponseTime": {
        "total": "572",
        "ok": "-",
        "ko": "572"
    },
    "standardDeviation": {
        "total": "437",
        "ok": "-",
        "ko": "437"
    },
    "percentiles1": {
        "total": "474",
        "ok": "-",
        "ko": "474"
    },
    "percentiles2": {
        "total": "493",
        "ok": "-",
        "ko": "493"
    },
    "percentiles3": {
        "total": "1092",
        "ok": "-",
        "ko": "1092"
    },
    "percentiles4": {
        "total": "1879",
        "ok": "-",
        "ko": "1879"
    },
    "group1": {
        "name": "t < 800 ms",
        "count": 0,
        "percentage": 0
    },
    "group2": {
        "name": "800 ms < t < 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group3": {
        "name": "t > 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group4": {
        "name": "failed",
        "count": 960,
        "percentage": 100
    },
    "meanNumberOfRequestsPerSecond": {
        "total": "2",
        "ok": "-",
        "ko": "2"
    }
},
contents: {
"req_postcreatehoste-b2d92": {
        type: "REQUEST",
        name: "postCreateHostedByZip",
path: "postCreateHostedByZip",
pathFormatted: "req_postcreatehoste-b2d92",
stats: {
    "name": "postCreateHostedByZip",
    "numberOfRequests": {
        "total": "480",
        "ok": "0",
        "ko": "480"
    },
    "minResponseTime": {
        "total": "383",
        "ok": "-",
        "ko": "383"
    },
    "maxResponseTime": {
        "total": "2645",
        "ok": "-",
        "ko": "2645"
    },
    "meanResponseTime": {
        "total": "556",
        "ok": "-",
        "ko": "556"
    },
    "standardDeviation": {
        "total": "279",
        "ok": "-",
        "ko": "279"
    },
    "percentiles1": {
        "total": "475",
        "ok": "-",
        "ko": "475"
    },
    "percentiles2": {
        "total": "491",
        "ok": "-",
        "ko": "491"
    },
    "percentiles3": {
        "total": "1064",
        "ok": "-",
        "ko": "1064"
    },
    "percentiles4": {
        "total": "1780",
        "ok": "-",
        "ko": "1780"
    },
    "group1": {
        "name": "t < 800 ms",
        "count": 0,
        "percentage": 0
    },
    "group2": {
        "name": "800 ms < t < 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group3": {
        "name": "t > 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group4": {
        "name": "failed",
        "count": 480,
        "percentage": 100
    },
    "meanNumberOfRequestsPerSecond": {
        "total": "1",
        "ok": "-",
        "ko": "1"
    }
}
    },"req_putcreatehosted-bcb09": {
        type: "REQUEST",
        name: "putCreateHostedByZip",
path: "putCreateHostedByZip",
pathFormatted: "req_putcreatehosted-bcb09",
stats: {
    "name": "putCreateHostedByZip",
    "numberOfRequests": {
        "total": "480",
        "ok": "0",
        "ko": "480"
    },
    "minResponseTime": {
        "total": "382",
        "ok": "-",
        "ko": "382"
    },
    "maxResponseTime": {
        "total": "10446",
        "ok": "-",
        "ko": "10446"
    },
    "meanResponseTime": {
        "total": "588",
        "ok": "-",
        "ko": "588"
    },
    "standardDeviation": {
        "total": "552",
        "ok": "-",
        "ko": "552"
    },
    "percentiles1": {
        "total": "474",
        "ok": "-",
        "ko": "474"
    },
    "percentiles2": {
        "total": "495",
        "ok": "-",
        "ko": "495"
    },
    "percentiles3": {
        "total": "1171",
        "ok": "-",
        "ko": "1171"
    },
    "percentiles4": {
        "total": "2017",
        "ok": "-",
        "ko": "2017"
    },
    "group1": {
        "name": "t < 800 ms",
        "count": 0,
        "percentage": 0
    },
    "group2": {
        "name": "800 ms < t < 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group3": {
        "name": "t > 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group4": {
        "name": "failed",
        "count": 480,
        "percentage": 100
    },
    "meanNumberOfRequestsPerSecond": {
        "total": "1",
        "ok": "-",
        "ko": "1"
    }
}
    }
}

}

function fillStats(stat){
    $("#numberOfRequests").append(stat.numberOfRequests.total);
    $("#numberOfRequestsOK").append(stat.numberOfRequests.ok);
    $("#numberOfRequestsKO").append(stat.numberOfRequests.ko);

    $("#minResponseTime").append(stat.minResponseTime.total);
    $("#minResponseTimeOK").append(stat.minResponseTime.ok);
    $("#minResponseTimeKO").append(stat.minResponseTime.ko);

    $("#maxResponseTime").append(stat.maxResponseTime.total);
    $("#maxResponseTimeOK").append(stat.maxResponseTime.ok);
    $("#maxResponseTimeKO").append(stat.maxResponseTime.ko);

    $("#meanResponseTime").append(stat.meanResponseTime.total);
    $("#meanResponseTimeOK").append(stat.meanResponseTime.ok);
    $("#meanResponseTimeKO").append(stat.meanResponseTime.ko);

    $("#standardDeviation").append(stat.standardDeviation.total);
    $("#standardDeviationOK").append(stat.standardDeviation.ok);
    $("#standardDeviationKO").append(stat.standardDeviation.ko);

    $("#percentiles1").append(stat.percentiles1.total);
    $("#percentiles1OK").append(stat.percentiles1.ok);
    $("#percentiles1KO").append(stat.percentiles1.ko);

    $("#percentiles2").append(stat.percentiles2.total);
    $("#percentiles2OK").append(stat.percentiles2.ok);
    $("#percentiles2KO").append(stat.percentiles2.ko);

    $("#percentiles3").append(stat.percentiles3.total);
    $("#percentiles3OK").append(stat.percentiles3.ok);
    $("#percentiles3KO").append(stat.percentiles3.ko);

    $("#percentiles4").append(stat.percentiles4.total);
    $("#percentiles4OK").append(stat.percentiles4.ok);
    $("#percentiles4KO").append(stat.percentiles4.ko);

    $("#meanNumberOfRequestsPerSecond").append(stat.meanNumberOfRequestsPerSecond.total);
    $("#meanNumberOfRequestsPerSecondOK").append(stat.meanNumberOfRequestsPerSecond.ok);
    $("#meanNumberOfRequestsPerSecondKO").append(stat.meanNumberOfRequestsPerSecond.ko);
}
