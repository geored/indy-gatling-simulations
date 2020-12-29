package io.swagger.client.api

import io.swagger.client.model._
import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.PopulationBuilder

import java.io.File

import scala.collection.mutable

class FOLOTrackingRecordAccessApiSimulation extends Simulation {

    def getCurrentDirectory = new File("").getAbsolutePath
    def userDataDirectory = getCurrentDirectory + "/src/gatling/resources/data"
    def userConfDirectory = getCurrentDirectory + "/src/gatling/resources/conf"

    // basic test setup
    val configName = System.getProperty("testConfig", "baseline")
    val config = ConfigFactory.parseFile(new File(userConfDirectory + File.separator + configName + ".conf"))
      .withFallback(ConfigFactory.parseFile(new File(userConfDirectory + File.separator + "default.conf")))
    val durationSeconds = config.getInt("performance.durationSeconds")
    val rampUpSeconds = config.getInt("performance.rampUpSeconds")
    val rampDownSeconds = config.getInt("performance.rampDownSeconds")
    val authentication = config.getString("performance.authorizationHeader")
    val acceptHeader = config.getString("performance.acceptType")
    val contentTypeHeader = config.getString("performance.contentType")
    val rateMultiplier = config.getDouble("performance.rateMultiplier")
    val instanceMultiplier = config.getDouble("performance.instanceMultiplier")

    // global assertion data
    val globalResponseTimeMinLTE = config.getInt("performance.global.assertions.responseTime.min.lte")
    val globalResponseTimeMinGTE = config.getInt("performance.global.assertions.responseTime.min.gte")
    val globalResponseTimeMaxLTE = config.getInt("performance.global.assertions.responseTime.max.lte")
    val globalResponseTimeMaxGTE = config.getInt("performance.global.assertions.responseTime.max.gte")
    val globalResponseTimeMeanLTE = config.getInt("performance.global.assertions.responseTime.mean.lte")
    val globalResponseTimeMeanGTE = config.getInt("performance.global.assertions.responseTime.mean.gte")
    val globalResponseTimeFailedRequestsPercentLTE = config.getDouble("performance.global.assertions.failedRequests.percent.lte")
    val globalResponseTimeFailedRequestsPercentGTE = config.getDouble("performance.global.assertions.failedRequests.percent.gte")
    val globalResponseTimeSuccessfulRequestsPercentLTE = config.getDouble("performance.global.assertions.successfulRequests.percent.lte")
    val globalResponseTimeSuccessfulRequestsPercentGTE = config.getDouble("performance.global.assertions.successfulRequests.percent.gte")

    val sentHeaders =  Map("Authorization" -> "Basic {ADD YOUR BASIC AUTHENTICATION CODE HERE}")

    // Setup http protocol configuration
    val httpConf = http
        .baseURL("http://indy-admin-master-devel.psi.redhat.com")// ADD INDY HOSTNAME HERE
      .headers(sentHeaders)
        .doNotTrackHeader("1")
        .acceptLanguageHeader("en-US,en;q=0.5")
        .acceptEncodingHeader("gzip, deflate")
        .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")
        .acceptHeader(acceptHeader)
        .contentTypeHeader(contentTypeHeader)

    // set authorization header if it has been modified from config
    if(!authentication.equals("~MANUAL_ENTRY~")){
        httpConf.authorizationHeader(authentication)
    }

    // Setup all the operations per second for the test to ultimately be generated from configs
    val clearRecordPerSecond = config.getDouble("performance.operationsPerSecond.clearRecord") * rateMultiplier * instanceMultiplier
    val doDeletePerSecond = config.getDouble("performance.operationsPerSecond.doDelete") * rateMultiplier * instanceMultiplier
    val exportReportPerSecond = config.getDouble("performance.operationsPerSecond.exportReport") * rateMultiplier * instanceMultiplier
    val getRecordPerSecond = config.getDouble("performance.operationsPerSecond.getRecord") * rateMultiplier * instanceMultiplier
    val getRecordIdsPerSecond = config.getDouble("performance.operationsPerSecond.getRecordIds") * rateMultiplier * instanceMultiplier
    val getReportPerSecond = config.getDouble("performance.operationsPerSecond.getReport") * rateMultiplier * instanceMultiplier
    val getZipRepositoryPerSecond = config.getDouble("performance.operationsPerSecond.getZipRepository") * rateMultiplier * instanceMultiplier
    val importReportPerSecond = config.getDouble("performance.operationsPerSecond.importReport") * rateMultiplier * instanceMultiplier
    val initRecordPerSecond = config.getDouble("performance.operationsPerSecond.initRecord") * rateMultiplier * instanceMultiplier
    val recalculateRecordPerSecond = config.getDouble("performance.operationsPerSecond.recalculateRecord") * rateMultiplier * instanceMultiplier
    val sealRecordPerSecond = config.getDouble("performance.operationsPerSecond.sealRecord") * rateMultiplier * instanceMultiplier

    val scenarioBuilders: mutable.MutableList[PopulationBuilder] = new mutable.MutableList[PopulationBuilder]()

    // Set up CSV feeders
    val clearRecordPATHFeeder = csv(userDataDirectory + File.separator + "clearRecord-pathParams.csv").random
    val doDeleteBodyFeeder = csv(userDataDirectory + File.separator + "doDelete-bodyParams.csv", escapeChar = '\\').random
    val getRecordPATHFeeder = csv(userDataDirectory + File.separator + "getRecord-pathParams.csv").random
    val getRecordIdsPATHFeeder = csv(userDataDirectory + File.separator + "getRecordIds-pathParams.csv").random
    val getReportPATHFeeder = csv(userDataDirectory + File.separator + "getReport-pathParams.csv").random
    val getZipRepositoryPATHFeeder = csv(userDataDirectory + File.separator + "getZipRepository-pathParams.csv").random
    val initRecordPATHFeeder = csv(userDataDirectory + File.separator + "initRecord-pathParams.csv").random
    val recalculateRecordPATHFeeder = csv(userDataDirectory + File.separator + "recalculateRecord-pathParams.csv").random
    val sealRecordPATHFeeder = csv(userDataDirectory + File.separator + "sealRecord-pathParams.csv").random

    // Setup all scenarios

    
    val scnclearRecord = scenario("clearRecordSimulation")
        .feed(clearRecordPATHFeeder)
        .exec(http("clearRecord")
        .httpRequest("DELETE","/api/folo/admin/${id}/record")
)

    // Run scnclearRecord with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnclearRecord.inject(
        rampUsersPerSec(1) to(clearRecordPerSecond) during(rampUpSeconds),
        constantUsersPerSec(clearRecordPerSecond) during(durationSeconds),
        rampUsersPerSec(clearRecordPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndoDelete = scenario("doDeleteSimulation")
        .feed(doDeleteBodyFeeder)
        .exec(http("doDelete")
        .httpRequest("POST","/api/folo/admin/batch/delete")
        .body(StringBody(BatchDeleteRequest.toStringBody("${trackingID}","${storeKey}","${paths}")))
        )

    // Run scndoDelete with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndoDelete.inject(
        rampUsersPerSec(1) to(doDeletePerSecond) during(rampUpSeconds),
        constantUsersPerSec(doDeletePerSecond) during(durationSeconds),
        rampUsersPerSec(doDeletePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnexportReport = scenario("exportReportSimulation")
        .exec(http("exportReport")
        .httpRequest("GET","/api/folo/admin/report/export")
)

    // Run scnexportReport with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnexportReport.inject(
        rampUsersPerSec(1) to(exportReportPerSecond) during(rampUpSeconds),
        constantUsersPerSec(exportReportPerSecond) during(durationSeconds),
        rampUsersPerSec(exportReportPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetRecord = scenario("getRecordSimulation")
        .feed(getRecordPATHFeeder)
        .exec(http("getRecord")
        .httpRequest("GET","/api/folo/admin/${id}/record")
)

    // Run scngetRecord with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetRecord.inject(
        rampUsersPerSec(1) to(getRecordPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getRecordPerSecond) during(durationSeconds),
        rampUsersPerSec(getRecordPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetRecordIds = scenario("getRecordIdsSimulation")
        .feed(getRecordIdsPATHFeeder)
        .exec(http("getRecordIds")
        .httpRequest("GET","/api/folo/admin/report/ids/${type}")
)

    // Run scngetRecordIds with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetRecordIds.inject(
        rampUsersPerSec(1) to(getRecordIdsPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getRecordIdsPerSecond) during(durationSeconds),
        rampUsersPerSec(getRecordIdsPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetReport = scenario("getReportSimulation")
        .feed(getReportPATHFeeder)
        .exec(http("getReport")
        .httpRequest("GET","/api/folo/admin/${id}/report")
)

    // Run scngetReport with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetReport.inject(
        rampUsersPerSec(1) to(getReportPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getReportPerSecond) during(durationSeconds),
        rampUsersPerSec(getReportPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetZipRepository = scenario("getZipRepositorySimulation")
        .feed(getZipRepositoryPATHFeeder)
        .exec(http("getZipRepository")
        .httpRequest("GET","/api/folo/admin/${id}/repo/zip")
)

    // Run scngetZipRepository with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetZipRepository.inject(
        rampUsersPerSec(1) to(getZipRepositoryPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getZipRepositoryPerSecond) during(durationSeconds),
        rampUsersPerSec(getZipRepositoryPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnimportReport = scenario("importReportSimulation")
        .exec(http("importReport")
        .httpRequest("PUT","/api/folo/admin/report/import")
)

    // Run scnimportReport with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnimportReport.inject(
        rampUsersPerSec(1) to(importReportPerSecond) during(rampUpSeconds),
        constantUsersPerSec(importReportPerSecond) during(durationSeconds),
        rampUsersPerSec(importReportPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scninitRecord = scenario("initRecordSimulation")
        .feed(initRecordPATHFeeder)
        .exec(http("initRecord")
        .httpRequest("PUT","/api/folo/admin/${id}/record")
)

    // Run scninitRecord with warm up and reach a constant rate for entire duration
    scenarioBuilders += scninitRecord.inject(
        rampUsersPerSec(1) to(initRecordPerSecond) during(rampUpSeconds),
        constantUsersPerSec(initRecordPerSecond) during(durationSeconds),
        rampUsersPerSec(initRecordPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnrecalculateRecord = scenario("recalculateRecordSimulation")
        .feed(recalculateRecordPATHFeeder)
        .exec(http("recalculateRecord")
        .httpRequest("GET","/api/folo/admin/${id}/record/recalculate")
)

    // Run scnrecalculateRecord with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnrecalculateRecord.inject(
        rampUsersPerSec(1) to(recalculateRecordPerSecond) during(rampUpSeconds),
        constantUsersPerSec(recalculateRecordPerSecond) during(durationSeconds),
        rampUsersPerSec(recalculateRecordPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnsealRecord = scenario("sealRecordSimulation")
        .feed(sealRecordPATHFeeder)
        .exec(http("sealRecord")
        .httpRequest("POST","/api/folo/admin/${id}/record")
)

    // Run scnsealRecord with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnsealRecord.inject(
        rampUsersPerSec(1) to(sealRecordPerSecond) during(rampUpSeconds),
        constantUsersPerSec(sealRecordPerSecond) during(durationSeconds),
        rampUsersPerSec(sealRecordPerSecond) to(1) during(rampDownSeconds)
    )

    setUp(
        scenarioBuilders.toList
    ).protocols(httpConf).assertions(
        global.responseTime.min.lte(globalResponseTimeMinLTE),
        global.responseTime.min.gte(globalResponseTimeMinGTE),
        global.responseTime.max.lte(globalResponseTimeMaxLTE),
        global.responseTime.max.gte(globalResponseTimeMaxGTE),
        global.responseTime.mean.lte(globalResponseTimeMeanLTE),
        global.responseTime.mean.gte(globalResponseTimeMeanGTE),
        global.failedRequests.percent.lte(globalResponseTimeFailedRequestsPercentLTE),
        global.failedRequests.percent.gte(globalResponseTimeFailedRequestsPercentGTE),
        global.successfulRequests.percent.lte(globalResponseTimeSuccessfulRequestsPercentLTE),
        global.successfulRequests.percent.gte(globalResponseTimeSuccessfulRequestsPercentGTE)
    )
}
