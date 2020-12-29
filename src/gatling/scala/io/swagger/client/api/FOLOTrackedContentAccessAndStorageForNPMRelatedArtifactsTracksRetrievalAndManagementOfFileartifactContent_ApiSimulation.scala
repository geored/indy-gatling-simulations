package io.swagger.client.api

import io.swagger.client.model._
import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.PopulationBuilder

import java.io.File

import scala.collection.mutable

class FOLOTrackedContentAccessAndStorageForNPMRelatedArtifactsTracksRetrievalAndManagementOfFileartifactContent_ApiSimulation extends Simulation {

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
    val doCreatePerSecond = config.getDouble("performance.operationsPerSecond.doCreate") * rateMultiplier * instanceMultiplier
    val doCreate_0PerSecond = config.getDouble("performance.operationsPerSecond.doCreate_0") * rateMultiplier * instanceMultiplier
    val doGetPerSecond = config.getDouble("performance.operationsPerSecond.doGet") * rateMultiplier * instanceMultiplier
    val doGet_0PerSecond = config.getDouble("performance.operationsPerSecond.doGet_0") * rateMultiplier * instanceMultiplier
    val doHeadPerSecond = config.getDouble("performance.operationsPerSecond.doHead") * rateMultiplier * instanceMultiplier
    val doHead_0PerSecond = config.getDouble("performance.operationsPerSecond.doHead_0") * rateMultiplier * instanceMultiplier

    val scenarioBuilders: mutable.MutableList[PopulationBuilder] = new mutable.MutableList[PopulationBuilder]()

    // Set up CSV feeders
    val doCreatePATHFeeder = csv(userDataDirectory + File.separator + "doCreate-pathParams.csv").random
    val doCreatePATHFeeder1 = csv(userDataDirectory + File.separator + "doCreate_0-pathParams.csv").random
    val doGetPATHFeeder = csv(userDataDirectory + File.separator + "doGet-pathParams.csv").random
    val doGetPATHFeeder1 = csv(userDataDirectory + File.separator + "doGet_0-pathParams.csv").random
    val doHeadQUERYFeeder = csv(userDataDirectory + File.separator + "doHead-queryParams.csv").random
    val doHeadPATHFeeder = csv(userDataDirectory + File.separator + "doHead-pathParams.csv").random
    val doHeadQUERYFeeder1 = csv(userDataDirectory + File.separator + "doHead_0-queryParams.csv").random
    val doHeadPATHFeeder1 = csv(userDataDirectory + File.separator + "doHead_0-pathParams.csv").random

    // Setup all scenarios

    
    val scndoCreate = scenario("doCreateSimulation")
        .feed(doCreatePATHFeeder)
        .exec(http("doCreate")
        .httpRequest("PUT","/api/folo/track/${id}/npm/${type}/${name}/${packageName}")
)

    // Run scndoCreate with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndoCreate.inject(
        rampUsersPerSec(1) to(doCreatePerSecond) during(rampUpSeconds),
        constantUsersPerSec(doCreatePerSecond) during(durationSeconds),
        rampUsersPerSec(doCreatePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndoCreate_0 = scenario("doCreate_0Simulation")
        .feed(doCreatePATHFeeder)
        .exec(http("doCreate_0")
        .httpRequest("PUT","/api/folo/track/${id}/npm/${type}/${name}/${packageName}/${versionTarball}")
)

    // Run scndoCreate_0 with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndoCreate_0.inject(
        rampUsersPerSec(1) to(doCreate_0PerSecond) during(rampUpSeconds),
        constantUsersPerSec(doCreate_0PerSecond) during(durationSeconds),
        rampUsersPerSec(doCreate_0PerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndoGet = scenario("doGetSimulation")
        .feed(doGetPATHFeeder)
        .exec(http("doGet")
        .httpRequest("GET","/api/folo/track/${id}/npm/${type}/${name}/${packageName}")
)

    // Run scndoGet with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndoGet.inject(
        rampUsersPerSec(1) to(doGetPerSecond) during(rampUpSeconds),
        constantUsersPerSec(doGetPerSecond) during(durationSeconds),
        rampUsersPerSec(doGetPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndoGet_0 = scenario("doGet_0Simulation")
        .feed(doGetPATHFeeder)
        .exec(http("doGet_0")
        .httpRequest("GET","/api/folo/track/${id}/npm/${type}/${name}/${packageName}/${versionTarball}")
)

    // Run scndoGet_0 with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndoGet_0.inject(
        rampUsersPerSec(1) to(doGet_0PerSecond) during(rampUpSeconds),
        constantUsersPerSec(doGet_0PerSecond) during(durationSeconds),
        rampUsersPerSec(doGet_0PerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndoHead = scenario("doHeadSimulation")
        .feed(doHeadQUERYFeeder)
        .feed(doHeadPATHFeeder)
        .exec(http("doHead")
        .httpRequest("HEAD","/api/folo/track/${id}/npm/${type}/${name}/${packageName}")
        .queryParam("cache-only","${cache-only}")
)

    // Run scndoHead with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndoHead.inject(
        rampUsersPerSec(1) to(doHeadPerSecond) during(rampUpSeconds),
        constantUsersPerSec(doHeadPerSecond) during(durationSeconds),
        rampUsersPerSec(doHeadPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndoHead_0 = scenario("doHead_0Simulation")
        .feed(doHeadQUERYFeeder)
        .feed(doHeadPATHFeeder)
        .exec(http("doHead_0")
        .httpRequest("HEAD","/api/folo/track/${id}/npm/${type}/${name}/${packageName}/${versionTarball}")
        .queryParam("cache-only","${cache-only}")
)

    // Run scndoHead_0 with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndoHead_0.inject(
        rampUsersPerSec(1) to(doHead_0PerSecond) during(rampUpSeconds),
        constantUsersPerSec(doHead_0PerSecond) during(durationSeconds),
        rampUsersPerSec(doHead_0PerSecond) to(1) during(rampDownSeconds)
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
