package io.swagger.client.api

import io.swagger.client.model._
import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.PopulationBuilder

import java.io.File

import scala.collection.mutable

class NotFoundCacheApiSimulation extends Simulation {

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

    val sentHeaders =  Map("Authorization" -> "Basic YOUR BASICAUTH HERE=")

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
    val clearAllPerSecond = config.getDouble("performance.operationsPerSecond.clearAll") * rateMultiplier * instanceMultiplier
    val clearStorePerSecond = config.getDouble("performance.operationsPerSecond.clearStore") * rateMultiplier * instanceMultiplier
    val deprecatedClearStorePerSecond = config.getDouble("performance.operationsPerSecond.deprecatedClearStore") * rateMultiplier * instanceMultiplier
    val deprecatedGetStorePerSecond = config.getDouble("performance.operationsPerSecond.deprecatedGetStore") * rateMultiplier * instanceMultiplier
    val getAllPerSecond = config.getDouble("performance.operationsPerSecond.getAll") * rateMultiplier * instanceMultiplier
    val getInfoPerSecond = config.getDouble("performance.operationsPerSecond.getInfo") * rateMultiplier * instanceMultiplier
    val getStorePerSecond = config.getDouble("performance.operationsPerSecond.getStore") * rateMultiplier * instanceMultiplier
    val getStoreInfoPerSecond = config.getDouble("performance.operationsPerSecond.getStoreInfo") * rateMultiplier * instanceMultiplier

    val scenarioBuilders: mutable.MutableList[PopulationBuilder] = new mutable.MutableList[PopulationBuilder]()

    // Set up CSV feeders
    val clearStorePATHFeeder = csv(userDataDirectory + File.separator + "clearStore-pathParams.csv").random
    val deprecatedClearStorePATHFeeder = csv(userDataDirectory + File.separator + "deprecatedClearStore-pathParams.csv").random
    val deprecatedGetStoreQUERYFeeder = csv(userDataDirectory + File.separator + "deprecatedGetStore-queryParams.csv").random
    val deprecatedGetStorePATHFeeder = csv(userDataDirectory + File.separator + "deprecatedGetStore-pathParams.csv").random
    val getAllQUERYFeeder = csv(userDataDirectory + File.separator + "getAll-queryParams.csv").random
    val getStoreQUERYFeeder = csv(userDataDirectory + File.separator + "getStore-queryParams.csv").random
    val getStorePATHFeeder = csv(userDataDirectory + File.separator + "getStore-pathParams.csv").random
    val getStoreInfoPATHFeeder = csv(userDataDirectory + File.separator + "getStoreInfo-pathParams.csv").random

    // Setup all scenarios

    
    val scnclearAll = scenario("clearAllSimulation")
        .exec(http("clearAll")
        .httpRequest("DELETE","/api/nfc")
)

    // Run scnclearAll with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnclearAll.inject(
        rampUsersPerSec(1) to(clearAllPerSecond) during(rampUpSeconds),
        constantUsersPerSec(clearAllPerSecond) during(durationSeconds),
        rampUsersPerSec(clearAllPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnclearStore = scenario("clearStoreSimulation")
        .feed(clearStorePATHFeeder)
        .exec(http("clearStore")
        .httpRequest("DELETE","/api/nfc/${packageType}/${type}/${name}${path}")
)

    // Run scnclearStore with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnclearStore.inject(
        rampUsersPerSec(1) to(clearStorePerSecond) during(rampUpSeconds),
        constantUsersPerSec(clearStorePerSecond) during(durationSeconds),
        rampUsersPerSec(clearStorePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndeprecatedClearStore = scenario("deprecatedClearStoreSimulation")
        .feed(deprecatedClearStorePATHFeeder)
        .exec(http("deprecatedClearStore")
        .httpRequest("DELETE","/api/nfc/${type}/${name}${path}")
)

    // Run scndeprecatedClearStore with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndeprecatedClearStore.inject(
        rampUsersPerSec(1) to(deprecatedClearStorePerSecond) during(rampUpSeconds),
        constantUsersPerSec(deprecatedClearStorePerSecond) during(durationSeconds),
        rampUsersPerSec(deprecatedClearStorePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndeprecatedGetStore = scenario("deprecatedGetStoreSimulation")
        .feed(deprecatedGetStoreQUERYFeeder)
        .feed(deprecatedGetStorePATHFeeder)
        .exec(http("deprecatedGetStore")
        .httpRequest("GET","/api/nfc/${type}/${name}")
        .queryParam("pageIndex","${pageIndex}")
        .queryParam("pageSize","${pageSize}")
)

    // Run scndeprecatedGetStore with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndeprecatedGetStore.inject(
        rampUsersPerSec(1) to(deprecatedGetStorePerSecond) during(rampUpSeconds),
        constantUsersPerSec(deprecatedGetStorePerSecond) during(durationSeconds),
        rampUsersPerSec(deprecatedGetStorePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetAll = scenario("getAllSimulation")
        .feed(getAllQUERYFeeder)
        .exec(http("getAll")
        .httpRequest("GET","/api/nfc")
        .queryParam("pageIndex","${pageIndex}")
        .queryParam("pageSize","${pageSize}")
)

    // Run scngetAll with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetAll.inject(
        rampUsersPerSec(1) to(getAllPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getAllPerSecond) during(durationSeconds),
        rampUsersPerSec(getAllPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetInfo = scenario("getInfoSimulation")
        .exec(http("getInfo")
        .httpRequest("GET","/api/nfc/info")
)

    // Run scngetInfo with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetInfo.inject(
        rampUsersPerSec(1) to(getInfoPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getInfoPerSecond) during(durationSeconds),
        rampUsersPerSec(getInfoPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetStore = scenario("getStoreSimulation")
        .feed(getStoreQUERYFeeder)
        .feed(getStorePATHFeeder)
        .exec(http("getStore")
        .httpRequest("GET","/api/nfc/${packageType}/${type}/${name}")
        .queryParam("pageIndex","${pageIndex}")
        .queryParam("pageSize","${pageSize}")
)

    // Run scngetStore with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetStore.inject(
        rampUsersPerSec(1) to(getStorePerSecond) during(rampUpSeconds),
        constantUsersPerSec(getStorePerSecond) during(durationSeconds),
        rampUsersPerSec(getStorePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetStoreInfo = scenario("getStoreInfoSimulation")
        .feed(getStoreInfoPATHFeeder)
        .exec(http("getStoreInfo")
        .httpRequest("GET","/api/nfc/${packageType}/${type}/${name}/info")
)

    // Run scngetStoreInfo with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetStoreInfo.inject(
        rampUsersPerSec(1) to(getStoreInfoPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getStoreInfoPerSecond) during(durationSeconds),
        rampUsersPerSec(getStoreInfoPerSecond) to(1) during(rampDownSeconds)
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
