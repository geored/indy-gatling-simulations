package io.swagger.client.api

import io.swagger.client.model._
import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.PopulationBuilder

import java.io.File

import scala.collection.mutable

class DiagnosticsApiSimulation extends Simulation {

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
    val changeConfiguredLoggerLevelPerSecond = config.getDouble("performance.operationsPerSecond.changeConfiguredLoggerLevel") * rateMultiplier * instanceMultiplier
    val getAllLoggersPerSecond = config.getDouble("performance.operationsPerSecond.getAllLoggers") * rateMultiplier * instanceMultiplier
    val getBundlePerSecond = config.getDouble("performance.operationsPerSecond.getBundle") * rateMultiplier * instanceMultiplier
    val getConfiguredLoggerPerSecond = config.getDouble("performance.operationsPerSecond.getConfiguredLogger") * rateMultiplier * instanceMultiplier
    val getNamedLoggerPerSecond = config.getDouble("performance.operationsPerSecond.getNamedLogger") * rateMultiplier * instanceMultiplier
    val getRepoBundlePerSecond = config.getDouble("performance.operationsPerSecond.getRepoBundle") * rateMultiplier * instanceMultiplier
    val getThreadDumpPerSecond = config.getDouble("performance.operationsPerSecond.getThreadDump") * rateMultiplier * instanceMultiplier
    val getThreadDumpByStatePerSecond = config.getDouble("performance.operationsPerSecond.getThreadDumpByState") * rateMultiplier * instanceMultiplier

    val scenarioBuilders: mutable.MutableList[PopulationBuilder] = new mutable.MutableList[PopulationBuilder]()

    // Set up CSV feeders
    val changeConfiguredLoggerLevelPATHFeeder = csv(userDataDirectory + File.separator + "changeConfiguredLoggerLevel-pathParams.csv").random
    val getConfiguredLoggerPATHFeeder = csv(userDataDirectory + File.separator + "getConfiguredLogger-pathParams.csv").random
    val getNamedLoggerPATHFeeder = csv(userDataDirectory + File.separator + "getNamedLogger-pathParams.csv").random
    val getThreadDumpByStatePATHFeeder = csv(userDataDirectory + File.separator + "getThreadDumpByState-pathParams.csv").random

    // Setup all scenarios

    
    val scnchangeConfiguredLoggerLevel = scenario("changeConfiguredLoggerLevelSimulation")
        .feed(changeConfiguredLoggerLevelPATHFeeder)
        .exec(http("changeConfiguredLoggerLevel")
        .httpRequest("PUT","/api/diag/logger/name/${name}/${level}")
)

    // Run scnchangeConfiguredLoggerLevel with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnchangeConfiguredLoggerLevel.inject(
        rampUsersPerSec(1) to(changeConfiguredLoggerLevelPerSecond) during(rampUpSeconds),
        constantUsersPerSec(changeConfiguredLoggerLevelPerSecond) during(durationSeconds),
        rampUsersPerSec(changeConfiguredLoggerLevelPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetAllLoggers = scenario("getAllLoggersSimulation")
        .exec(http("getAllLoggers")
        .httpRequest("GET","/api/diag/logger/configured/all")
)

    // Run scngetAllLoggers with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetAllLoggers.inject(
        rampUsersPerSec(1) to(getAllLoggersPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getAllLoggersPerSecond) during(durationSeconds),
        rampUsersPerSec(getAllLoggersPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetBundle = scenario("getBundleSimulation")
        .exec(http("getBundle")
        .httpRequest("GET","/api/diag/bundle")
)

    // Run scngetBundle with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetBundle.inject(
        rampUsersPerSec(1) to(getBundlePerSecond) during(rampUpSeconds),
        constantUsersPerSec(getBundlePerSecond) during(durationSeconds),
        rampUsersPerSec(getBundlePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetConfiguredLogger = scenario("getConfiguredLoggerSimulation")
        .feed(getConfiguredLoggerPATHFeeder)
        .exec(http("getConfiguredLogger")
        .httpRequest("GET","/api/diag/logger/configured/name/${name}")
)

    // Run scngetConfiguredLogger with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetConfiguredLogger.inject(
        rampUsersPerSec(1) to(getConfiguredLoggerPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getConfiguredLoggerPerSecond) during(durationSeconds),
        rampUsersPerSec(getConfiguredLoggerPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetNamedLogger = scenario("getNamedLoggerSimulation")
        .feed(getNamedLoggerPATHFeeder)
        .exec(http("getNamedLogger")
        .httpRequest("GET","/api/diag/logger/name/${name}")
)

    // Run scngetNamedLogger with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetNamedLogger.inject(
        rampUsersPerSec(1) to(getNamedLoggerPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getNamedLoggerPerSecond) during(durationSeconds),
        rampUsersPerSec(getNamedLoggerPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetRepoBundle = scenario("getRepoBundleSimulation")
        .exec(http("getRepoBundle")
        .httpRequest("GET","/api/diag/repo")
)

    // Run scngetRepoBundle with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetRepoBundle.inject(
        rampUsersPerSec(1) to(getRepoBundlePerSecond) during(rampUpSeconds),
        constantUsersPerSec(getRepoBundlePerSecond) during(durationSeconds),
        rampUsersPerSec(getRepoBundlePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetThreadDump = scenario("getThreadDumpSimulation")
        .exec(http("getThreadDump")
        .httpRequest("GET","/api/diag/threads")
)

    // Run scngetThreadDump with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetThreadDump.inject(
        rampUsersPerSec(1) to(getThreadDumpPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getThreadDumpPerSecond) during(durationSeconds),
        rampUsersPerSec(getThreadDumpPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetThreadDumpByState = scenario("getThreadDumpByStateSimulation")
        .feed(getThreadDumpByStatePATHFeeder)
        .exec(http("getThreadDumpByState")
        .httpRequest("GET","/api/diag/threads/${state}")
)

    // Run scngetThreadDumpByState with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetThreadDumpByState.inject(
        rampUsersPerSec(1) to(getThreadDumpByStatePerSecond) during(rampUpSeconds),
        constantUsersPerSec(getThreadDumpByStatePerSecond) during(durationSeconds),
        rampUsersPerSec(getThreadDumpByStatePerSecond) to(1) during(rampDownSeconds)
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
