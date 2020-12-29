var stats = {
    type: "GROUP",
name: "Global Information",
path: "",
pathFormatted: "group_missing-name-b06d1",
stats: {
    "name": "Global Information",
    "numberOfRequests": {
        "total": "480",
        "ok": "0",
        "ko": "480"
    },
    "minResponseTime": {
        "total": "381",
        "ok": "-",
        "ko": "381"
    },
    "maxResponseTime": {
        "total": "2010",
        "ok": "-",
        "ko": "2010"
    },
    "meanResponseTime": {
        "total": "421",
        "ok": "-",
        "ko": "421"
    },
    "standardDeviation": {
        "total": "89",
        "ok": "-",
        "ko": "89"
    },
    "percentiles1": {
        "total": "408",
        "ok": "-",
        "ko": "408"
    },
    "percentiles2": {
        "total": "420",
        "ok": "-",
        "ko": "420"
    },
    "percentiles3": {
        "total": "452",
        "ok": "-",
        "ko": "452"
    },
    "percentiles4": {
        "total": "626",
        "ok": "-",
        "ko": "626"
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
},
contents: {
"req_doget-e194e": {
        type: "REQUEST",
        name: "doGet",
path: "doGet",
pathFormatted: "req_doget-e194e",
stats: {
    "name": "doGet",
    "numberOfRequests": {
        "total": "480",
        "ok": "0",
        "ko": "480"
    },
    "minResponseTime": {
        "total": "381",
        "ok": "-",
        "ko": "381"
    },
    "maxResponseTime": {
        "total": "2010",
        "ok": "-",
        "ko": "2010"
    },
    "meanResponseTime": {
        "total": "421",
        "ok": "-",
        "ko": "421"
    },
    "standardDeviation": {
        "total": "89",
        "ok": "-",
        "ko": "89"
    },
    "percentiles1": {
        "total": "408",
        "ok": "-",
        "ko": "408"
    },
    "percentiles2": {
        "total": "420",
        "ok": "-",
        "ko": "420"
    },
    "percentiles3": {
        "total": "452",
        "ok": "-",
        "ko": "452"
    },
    "percentiles4": {
        "total": "626",
        "ok": "-",
        "ko": "626"
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
