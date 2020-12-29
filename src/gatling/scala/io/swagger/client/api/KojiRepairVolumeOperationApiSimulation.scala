package io.swagger.client.api

import io.swagger.client.model._
import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.PopulationBuilder

import java.io.File

import scala.collection.mutable

class KojiRepairVolumeOperationApiSimulation extends Simulation {

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
    val repairAllMetadataTimeoutPerSecond = config.getDouble("performance.operationsPerSecond.repairAllMetadataTimeout") * rateMultiplier * instanceMultiplier
    val repairAllPathMasksPerSecond = config.getDouble("performance.operationsPerSecond.repairAllPathMasks") * rateMultiplier * instanceMultiplier
    val repairMetadataTimeoutPerSecond = config.getDouble("performance.operationsPerSecond.repairMetadataTimeout") * rateMultiplier * instanceMultiplier
    val repairPathMasksPerSecond = config.getDouble("performance.operationsPerSecond.repairPathMasks") * rateMultiplier * instanceMultiplier
    val repairVolumesPerSecond = config.getDouble("performance.operationsPerSecond.repairVolumes") * rateMultiplier * instanceMultiplier

    val scenarioBuilders: mutable.MutableList[PopulationBuilder] = new mutable.MutableList[PopulationBuilder]()

    // Set up CSV feeders
    val repairAllMetadataTimeoutQUERYFeeder = csv(userDataDirectory + File.separator + "repairAllMetadataTimeout-queryParams.csv").random
    val repairMetadataTimeoutBodyFeeder = csv(userDataDirectory + File.separator + "repairMetadataTimeout-bodyParams.csv", escapeChar = '\\').random
    val repairPathMasksBodyFeeder = csv(userDataDirectory + File.separator + "repairPathMasks-bodyParams.csv", escapeChar = '\\').random
    val repairVolumesBodyFeeder = csv(userDataDirectory + File.separator + "repairVolumes-bodyParams.csv", escapeChar = '\\').random

    // Setup all scenarios

    
    val scnrepairAllMetadataTimeout = scenario("repairAllMetadataTimeoutSimulation")
        .feed(repairAllMetadataTimeoutQUERYFeeder)
        .exec(http("repairAllMetadataTimeout")
        .httpRequest("POST","/api/repair/koji/metadata/timeout/all")
        .queryParam("isDryRun","${isDryRun}")
)

    // Run scnrepairAllMetadataTimeout with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnrepairAllMetadataTimeout.inject(
        rampUsersPerSec(1) to(repairAllMetadataTimeoutPerSecond) during(rampUpSeconds),
        constantUsersPerSec(repairAllMetadataTimeoutPerSecond) during(durationSeconds),
        rampUsersPerSec(repairAllMetadataTimeoutPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnrepairAllPathMasks = scenario("repairAllPathMasksSimulation")
        .exec(http("repairAllPathMasks")
        .httpRequest("POST","/api/repair/koji/mask/all")
)

    // Run scnrepairAllPathMasks with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnrepairAllPathMasks.inject(
        rampUsersPerSec(1) to(repairAllPathMasksPerSecond) during(rampUpSeconds),
        constantUsersPerSec(repairAllPathMasksPerSecond) during(durationSeconds),
        rampUsersPerSec(repairAllPathMasksPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnrepairMetadataTimeout = scenario("repairMetadataTimeoutSimulation")
        .feed(repairMetadataTimeoutBodyFeeder)
        .exec(http("repairMetadataTimeout")
        .httpRequest("POST","/api/repair/koji/metadata/timeout")
        .body(StringBody(KojiRepairRequest.toStringBody("${source}","${args}","${dryRun}")))
        )

    // Run scnrepairMetadataTimeout with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnrepairMetadataTimeout.inject(
        rampUsersPerSec(1) to(repairMetadataTimeoutPerSecond) during(rampUpSeconds),
        constantUsersPerSec(repairMetadataTimeoutPerSecond) during(durationSeconds),
        rampUsersPerSec(repairMetadataTimeoutPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnrepairPathMasks = scenario("repairPathMasksSimulation")
        .feed(repairPathMasksBodyFeeder)
        .exec(http("repairPathMasks")
        .httpRequest("POST","/api/repair/koji/mask")
        .body(StringBody(KojiRepairRequest.toStringBody("${source}","${args}","${dryRun}")))
        )

    // Run scnrepairPathMasks with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnrepairPathMasks.inject(
        rampUsersPerSec(1) to(repairPathMasksPerSecond) during(rampUpSeconds),
        constantUsersPerSec(repairPathMasksPerSecond) during(durationSeconds),
        rampUsersPerSec(repairPathMasksPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnrepairVolumes = scenario("repairVolumesSimulation")
        .feed(repairVolumesBodyFeeder)
        .exec(http("repairVolumes")
        .httpRequest("POST","/api/repair/koji/vol")
        .body(StringBody(KojiRepairRequest.toStringBody("${source}","${args}","${dryRun}")))
        )

    // Run scnrepairVolumes with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnrepairVolumes.inject(
        rampUsersPerSec(1) to(repairVolumesPerSecond) during(rampUpSeconds),
        constantUsersPerSec(repairVolumesPerSecond) during(durationSeconds),
        rampUsersPerSec(repairVolumesPerSecond) to(1) during(rampDownSeconds)
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
