package io.swagger.client.api

import io.swagger.client.model._
import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.PopulationBuilder

import java.io.File

import scala.collection.mutable

class IndyDirectoryContentBrowseApiSimulation extends Simulation {

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
    val browseDirectoryPerSecond = config.getDouble("performance.operationsPerSecond.browseDirectory") * rateMultiplier * instanceMultiplier
    val browseRootPerSecond = config.getDouble("performance.operationsPerSecond.browseRoot") * rateMultiplier * instanceMultiplier
    val headForDirectoryPerSecond = config.getDouble("performance.operationsPerSecond.headForDirectory") * rateMultiplier * instanceMultiplier

    val scenarioBuilders: mutable.MutableList[PopulationBuilder] = new mutable.MutableList[PopulationBuilder]()

    // Set up CSV feeders
    val browseDirectoryPATHFeeder = csv(userDataDirectory + File.separator + "browseDirectory-pathParams.csv").random
    val browseRootPATHFeeder = csv(userDataDirectory + File.separator + "browseRoot-pathParams.csv").random
    val headForDirectoryPATHFeeder = csv(userDataDirectory + File.separator + "headForDirectory-pathParams.csv").random

    // Setup all scenarios

    
    val scnbrowseDirectory = scenario("browseDirectorySimulation")
        .feed(browseDirectoryPATHFeeder)
        .exec(http("browseDirectory")
        .httpRequest("GET","/api/browse/${packageType}/${type}/${name}/${path}")
)

    // Run scnbrowseDirectory with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnbrowseDirectory.inject(
        rampUsersPerSec(1) to(browseDirectoryPerSecond) during(rampUpSeconds),
        constantUsersPerSec(browseDirectoryPerSecond) during(durationSeconds),
        rampUsersPerSec(browseDirectoryPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnbrowseRoot = scenario("browseRootSimulation")
        .feed(browseRootPATHFeeder)
        .exec(http("browseRoot")
        .httpRequest("GET","/api/browse/${packageType}/${type}/${name}")
)

    // Run scnbrowseRoot with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnbrowseRoot.inject(
        rampUsersPerSec(1) to(browseRootPerSecond) during(rampUpSeconds),
        constantUsersPerSec(browseRootPerSecond) during(durationSeconds),
        rampUsersPerSec(browseRootPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnheadForDirectory = scenario("headForDirectorySimulation")
        .feed(headForDirectoryPATHFeeder)
        .exec(http("headForDirectory")
        .httpRequest("HEAD","/api/browse/${packageType}/${type}/${name}/${path}")
)

    // Run scnheadForDirectory with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnheadForDirectory.inject(
        rampUsersPerSec(1) to(headForDirectoryPerSecond) during(rampUpSeconds),
        constantUsersPerSec(headForDirectoryPerSecond) during(durationSeconds),
        rampUsersPerSec(headForDirectoryPerSecond) to(1) during(rampDownSeconds)
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
