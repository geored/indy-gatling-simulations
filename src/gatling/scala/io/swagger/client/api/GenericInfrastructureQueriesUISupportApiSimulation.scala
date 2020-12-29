package io.swagger.client.api

import io.swagger.client.model._
import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.PopulationBuilder

import java.io.File

import scala.collection.mutable

class GenericInfrastructureQueriesUISupportApiSimulation extends Simulation {

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
    val getAddonInjectionJavascriptPerSecond = config.getDouble("performance.operationsPerSecond.getAddonInjectionJavascript") * rateMultiplier * instanceMultiplier
    val getAddonListPerSecond = config.getDouble("performance.operationsPerSecond.getAddonList") * rateMultiplier * instanceMultiplier
    val getAllEndpointsPerSecond = config.getDouble("performance.operationsPerSecond.getAllEndpoints") * rateMultiplier * instanceMultiplier
    val getIndyVersionPerSecond = config.getDouble("performance.operationsPerSecond.getIndyVersion") * rateMultiplier * instanceMultiplier
    val getPackageTypeMapPerSecond = config.getDouble("performance.operationsPerSecond.getPackageTypeMap") * rateMultiplier * instanceMultiplier
    val getPackageTypeNamesPerSecond = config.getDouble("performance.operationsPerSecond.getPackageTypeNames") * rateMultiplier * instanceMultiplier

    val scenarioBuilders: mutable.MutableList[PopulationBuilder] = new mutable.MutableList[PopulationBuilder]()

    // Set up CSV feeders

    // Setup all scenarios

    
    val scngetAddonInjectionJavascript = scenario("getAddonInjectionJavascriptSimulation")
        .exec(http("getAddonInjectionJavascript")
        .httpRequest("GET","/api/stats/addons/active.js")
)

    // Run scngetAddonInjectionJavascript with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetAddonInjectionJavascript.inject(
        rampUsersPerSec(1) to(getAddonInjectionJavascriptPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getAddonInjectionJavascriptPerSecond) during(durationSeconds),
        rampUsersPerSec(getAddonInjectionJavascriptPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetAddonList = scenario("getAddonListSimulation")
        .exec(http("getAddonList")
        .httpRequest("GET","/api/stats/addons/active")
)

    // Run scngetAddonList with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetAddonList.inject(
        rampUsersPerSec(1) to(getAddonListPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getAddonListPerSecond) during(durationSeconds),
        rampUsersPerSec(getAddonListPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetAllEndpoints = scenario("getAllEndpointsSimulation")
        .exec(http("getAllEndpoints")
        .httpRequest("GET","/api/stats/all-endpoints")
)

    // Run scngetAllEndpoints with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetAllEndpoints.inject(
        rampUsersPerSec(1) to(getAllEndpointsPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getAllEndpointsPerSecond) during(durationSeconds),
        rampUsersPerSec(getAllEndpointsPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetIndyVersion = scenario("getIndyVersionSimulation")
        .exec(http("getIndyVersion")
        .httpRequest("GET","/api/stats/version-info")
)

    // Run scngetIndyVersion with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetIndyVersion.inject(
        rampUsersPerSec(1) to(getIndyVersionPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getIndyVersionPerSecond) during(durationSeconds),
        rampUsersPerSec(getIndyVersionPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetPackageTypeMap = scenario("getPackageTypeMapSimulation")
        .exec(http("getPackageTypeMap")
        .httpRequest("GET","/api/stats/package-type/map")
)

    // Run scngetPackageTypeMap with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetPackageTypeMap.inject(
        rampUsersPerSec(1) to(getPackageTypeMapPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getPackageTypeMapPerSecond) during(durationSeconds),
        rampUsersPerSec(getPackageTypeMapPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetPackageTypeNames = scenario("getPackageTypeNamesSimulation")
        .exec(http("getPackageTypeNames")
        .httpRequest("GET","/api/stats/package-type/keys")
)

    // Run scngetPackageTypeNames with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetPackageTypeNames.inject(
        rampUsersPerSec(1) to(getPackageTypeNamesPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getPackageTypeNamesPerSecond) during(durationSeconds),
        rampUsersPerSec(getPackageTypeNamesPerSecond) to(1) during(rampDownSeconds)
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
