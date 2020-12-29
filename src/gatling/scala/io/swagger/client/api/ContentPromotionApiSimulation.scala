package io.swagger.client.api

import io.swagger.client.model._
import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.PopulationBuilder

import java.io.File

import scala.collection.mutable

class ContentPromotionApiSimulation extends Simulation {

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
    val promotePathsPerSecond = config.getDouble("performance.operationsPerSecond.promotePaths") * rateMultiplier * instanceMultiplier
    val promoteToGroupPerSecond = config.getDouble("performance.operationsPerSecond.promoteToGroup") * rateMultiplier * instanceMultiplier
    val rollbackGroupPromotePerSecond = config.getDouble("performance.operationsPerSecond.rollbackGroupPromote") * rateMultiplier * instanceMultiplier
    val rollbackPathsPerSecond = config.getDouble("performance.operationsPerSecond.rollbackPaths") * rateMultiplier * instanceMultiplier

    val scenarioBuilders: mutable.MutableList[PopulationBuilder] = new mutable.MutableList[PopulationBuilder]()

    // Set up CSV feeders
    val promoteToGroupBodyFeeder = csv(userDataDirectory + File.separator + "promoteToGroup-bodyParams.csv", escapeChar = '\\').random
    val rollbackGroupPromoteBodyFeeder = csv(userDataDirectory + File.separator + "rollbackGroupPromote-bodyParams.csv", escapeChar = '\\').random

    // Setup all scenarios

    
    val scnpromotePaths = scenario("promotePathsSimulation")
        .exec(http("promotePaths")
        .httpRequest("POST","/api/promotion/paths/promote")
)

    // Run scnpromotePaths with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnpromotePaths.inject(
        rampUsersPerSec(1) to(promotePathsPerSecond) during(rampUpSeconds),
        constantUsersPerSec(promotePathsPerSecond) during(durationSeconds),
        rampUsersPerSec(promotePathsPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnpromoteToGroup = scenario("promoteToGroupSimulation")
        .feed(promoteToGroupBodyFeeder)
        .exec(http("promoteToGroup")
        .httpRequest("POST","/api/promotion/groups/promote")
        .body(StringBody(GroupPromoteRequest.toStringBody("${targetGroup}","${source}","${target}","${callback}","${dryRun}","${fireEvents}","${promotionId}","${async}")))
        )

    // Run scnpromoteToGroup with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnpromoteToGroup.inject(
        rampUsersPerSec(1) to(promoteToGroupPerSecond) during(rampUpSeconds),
        constantUsersPerSec(promoteToGroupPerSecond) during(durationSeconds),
        rampUsersPerSec(promoteToGroupPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnrollbackGroupPromote = scenario("rollbackGroupPromoteSimulation")
        .feed(rollbackGroupPromoteBodyFeeder)
        .exec(http("rollbackGroupPromote")
        .httpRequest("POST","/api/promotion/groups/rollback")
        .body(StringBody(GroupPromoteResult.toStringBody("${validations}","${resultCode}","${request}","${error}")))
        )

    // Run scnrollbackGroupPromote with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnrollbackGroupPromote.inject(
        rampUsersPerSec(1) to(rollbackGroupPromotePerSecond) during(rampUpSeconds),
        constantUsersPerSec(rollbackGroupPromotePerSecond) during(durationSeconds),
        rampUsersPerSec(rollbackGroupPromotePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnrollbackPaths = scenario("rollbackPathsSimulation")
        .exec(http("rollbackPaths")
        .httpRequest("POST","/api/promotion/paths/rollback")
)

    // Run scnrollbackPaths with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnrollbackPaths.inject(
        rampUsersPerSec(1) to(rollbackPathsPerSecond) during(rampUpSeconds),
        constantUsersPerSec(rollbackPathsPerSecond) during(durationSeconds),
        rampUsersPerSec(rollbackPathsPerSecond) to(1) during(rampDownSeconds)
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
