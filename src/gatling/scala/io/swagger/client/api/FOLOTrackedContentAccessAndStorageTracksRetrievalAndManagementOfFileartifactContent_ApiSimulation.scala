package io.swagger.client.api

import io.swagger.client.model._
import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.PopulationBuilder

import java.io.File

import scala.collection.mutable

class FOLOTrackedContentAccessAndStorageTracksRetrievalAndManagementOfFileartifactContent_ApiSimulation extends Simulation {

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

    val sentHeaders =  Map("Authorization" -> "Basic Z2dlb3JnaWU6R2Vvcmd5QFJlZGhhdDE=")

    // Setup http protocol configuration
    val httpConf = http
        .baseURL("http://indy-admin-master-devel.psi.redhat.com")
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
    val doCreatePerSecond = config.getDouble("performance.operationsPerSecond.doCreate") * rateMultiplier * instanceMultiplier
    val doGetPerSecond = config.getDouble("performance.operationsPerSecond.doGet") * rateMultiplier * instanceMultiplier
    val doHeadPerSecond = config.getDouble("performance.operationsPerSecond.doHead") * rateMultiplier * instanceMultiplier

    val scenarioBuilders: mutable.MutableList[PopulationBuilder] = new mutable.MutableList[PopulationBuilder]()

    // Set up CSV feeders
    val doCreatePATHFeeder = csv(userDataDirectory + File.separator + "doCreate-pathParams.csv").random
    val doGetPATHFeeder = csv(userDataDirectory + File.separator + "doGet-pathParams.csv").random
    val doHeadQUERYFeeder = csv(userDataDirectory + File.separator + "doHead-queryParams.csv").random
    val doHeadPATHFeeder = csv(userDataDirectory + File.separator + "doHead-pathParams.csv").random

    // Setup all scenarios

    
    val scndoCreate = scenario("doCreateSimulation")
        .feed(doCreatePATHFeeder)
        .exec(http("doCreate")
        .httpRequest("PUT","/api/folo/track/${id}/maven/${type}/${name}/${path}")
)

    // Run scndoCreate with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndoCreate.inject(
        rampUsersPerSec(1) to(doCreatePerSecond) during(rampUpSeconds),
        constantUsersPerSec(doCreatePerSecond) during(durationSeconds),
        rampUsersPerSec(doCreatePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndoGet = scenario("doGetSimulation")
        .feed(doGetPATHFeeder)
        .exec(http("doGet")
        .httpRequest("GET","/api/folo/track/${id}/maven/${type}/${name}/${path}")
)

    // Run scndoGet with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndoGet.inject(
        rampUsersPerSec(1) to(doGetPerSecond) during(rampUpSeconds),
        constantUsersPerSec(doGetPerSecond) during(durationSeconds),
        rampUsersPerSec(doGetPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndoHead = scenario("doHeadSimulation")
        .feed(doHeadQUERYFeeder)
        .feed(doHeadPATHFeeder)
        .exec(http("doHead")
        .httpRequest("HEAD","/api/folo/track/${id}/maven/${type}/${name}/${path}")
        .queryParam("cache-only","${cache-only}")
)

    // Run scndoHead with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndoHead.inject(
        rampUsersPerSec(1) to(doHeadPerSecond) during(rampUpSeconds),
        constantUsersPerSec(doHeadPerSecond) during(durationSeconds),
        rampUsersPerSec(doHeadPerSecond) to(1) during(rampDownSeconds)
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
