package io.swagger.client.api

import io.swagger.client.model._
import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.PopulationBuilder

import java.io.File

import scala.collection.mutable

class MaintenanceApiSimulation extends Simulation {

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
    val affectedByPerSecond = config.getDouble("performance.operationsPerSecond.affectedBy") * rateMultiplier * instanceMultiplier
    val cleanInfinispanCachePerSecond = config.getDouble("performance.operationsPerSecond.cleanInfinispanCache") * rateMultiplier * instanceMultiplier
    val deleteAllPerSecond = config.getDouble("performance.operationsPerSecond.deleteAll") * rateMultiplier * instanceMultiplier
    val deleteAllViaGetPerSecond = config.getDouble("performance.operationsPerSecond.deleteAllViaGet") * rateMultiplier * instanceMultiplier
    val deprecatedRescanPerSecond = config.getDouble("performance.operationsPerSecond.deprecatedRescan") * rateMultiplier * instanceMultiplier
    val doDeletePerSecond = config.getDouble("performance.operationsPerSecond.doDelete") * rateMultiplier * instanceMultiplier
    val exportInfinispanCachePerSecond = config.getDouble("performance.operationsPerSecond.exportInfinispanCache") * rateMultiplier * instanceMultiplier
    val getTombstoneStoresPerSecond = config.getDouble("performance.operationsPerSecond.getTombstoneStores") * rateMultiplier * instanceMultiplier
    val rescanPerSecond = config.getDouble("performance.operationsPerSecond.rescan") * rateMultiplier * instanceMultiplier
    val rescanAllPerSecond = config.getDouble("performance.operationsPerSecond.rescanAll") * rateMultiplier * instanceMultiplier

    val scenarioBuilders: mutable.MutableList[PopulationBuilder] = new mutable.MutableList[PopulationBuilder]()

    // Set up CSV feeders
    val affectedByPATHFeeder = csv(userDataDirectory + File.separator + "affectedBy-pathParams.csv").random
    val cleanInfinispanCachePATHFeeder = csv(userDataDirectory + File.separator + "cleanInfinispanCache-pathParams.csv").random
    val deleteAllPATHFeeder = csv(userDataDirectory + File.separator + "deleteAll-pathParams.csv").random
    val deleteAllViaGetPATHFeeder = csv(userDataDirectory + File.separator + "deleteAllViaGet-pathParams.csv").random
    val deprecatedRescanPATHFeeder = csv(userDataDirectory + File.separator + "deprecatedRescan-pathParams.csv").random
    val doDeleteBodyFeeder = csv(userDataDirectory + File.separator + "doDelete-bodyParams.csv", escapeChar = '\\').random
    val exportInfinispanCachePATHFeeder = csv(userDataDirectory + File.separator + "exportInfinispanCache-pathParams.csv").random
    val getTombstoneStoresPATHFeeder = csv(userDataDirectory + File.separator + "getTombstoneStores-pathParams.csv").random
    val rescanPATHFeeder = csv(userDataDirectory + File.separator + "rescan-pathParams.csv").random

    // Setup all scenarios

    
    val scnaffectedBy = scenario("affectedBySimulation")
        .feed(affectedByPATHFeeder)
        .exec(http("affectedBy")
        .httpRequest("GET","/api/admin/maint/store/affected/${key}")
)

    // Run scnaffectedBy with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnaffectedBy.inject(
        rampUsersPerSec(1) to(affectedByPerSecond) during(rampUpSeconds),
        constantUsersPerSec(affectedByPerSecond) during(durationSeconds),
        rampUsersPerSec(affectedByPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scncleanInfinispanCache = scenario("cleanInfinispanCacheSimulation")
        .feed(cleanInfinispanCachePATHFeeder)
        .exec(http("cleanInfinispanCache")
        .httpRequest("DELETE","/api/admin/maint/infinispan/cache/${name}")
)

    // Run scncleanInfinispanCache with warm up and reach a constant rate for entire duration
    scenarioBuilders += scncleanInfinispanCache.inject(
        rampUsersPerSec(1) to(cleanInfinispanCachePerSecond) during(rampUpSeconds),
        constantUsersPerSec(cleanInfinispanCachePerSecond) during(durationSeconds),
        rampUsersPerSec(cleanInfinispanCachePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndeleteAll = scenario("deleteAllSimulation")
        .feed(deleteAllPATHFeeder)
        .exec(http("deleteAll")
        .httpRequest("DELETE","/api/admin/maint/content/all${path}")
)

    // Run scndeleteAll with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndeleteAll.inject(
        rampUsersPerSec(1) to(deleteAllPerSecond) during(rampUpSeconds),
        constantUsersPerSec(deleteAllPerSecond) during(durationSeconds),
        rampUsersPerSec(deleteAllPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndeleteAllViaGet = scenario("deleteAllViaGetSimulation")
        .feed(deleteAllViaGetPATHFeeder)
        .exec(http("deleteAllViaGet")
        .httpRequest("DELETE","/api/admin/maint/delete/all${path}")
)

    // Run scndeleteAllViaGet with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndeleteAllViaGet.inject(
        rampUsersPerSec(1) to(deleteAllViaGetPerSecond) during(rampUpSeconds),
        constantUsersPerSec(deleteAllViaGetPerSecond) during(durationSeconds),
        rampUsersPerSec(deleteAllViaGetPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndeprecatedRescan = scenario("deprecatedRescanSimulation")
        .feed(deprecatedRescanPATHFeeder)
        .exec(http("deprecatedRescan")
        .httpRequest("GET","/api/admin/maint/rescan/${type}/${name}")
)

    // Run scndeprecatedRescan with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndeprecatedRescan.inject(
        rampUsersPerSec(1) to(deprecatedRescanPerSecond) during(rampUpSeconds),
        constantUsersPerSec(deprecatedRescanPerSecond) during(durationSeconds),
        rampUsersPerSec(deprecatedRescanPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndoDelete = scenario("doDeleteSimulation")
        .feed(doDeleteBodyFeeder)
        .exec(http("doDelete")
        .httpRequest("POST","/api/admin/maint/content/batch/delete")
        .body(StringBody(BatchDeleteRequest.toStringBody("${trackingID}","${storeKey}","${paths}")))
        )

    // Run scndoDelete with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndoDelete.inject(
        rampUsersPerSec(1) to(doDeletePerSecond) during(rampUpSeconds),
        constantUsersPerSec(doDeletePerSecond) during(durationSeconds),
        rampUsersPerSec(doDeletePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnexportInfinispanCache = scenario("exportInfinispanCacheSimulation")
        .feed(exportInfinispanCachePATHFeeder)
        .exec(http("exportInfinispanCache")
        .httpRequest("GET","/api/admin/maint/infinispan/cache/${name}${key}")
)

    // Run scnexportInfinispanCache with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnexportInfinispanCache.inject(
        rampUsersPerSec(1) to(exportInfinispanCachePerSecond) during(rampUpSeconds),
        constantUsersPerSec(exportInfinispanCachePerSecond) during(durationSeconds),
        rampUsersPerSec(exportInfinispanCachePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetTombstoneStores = scenario("getTombstoneStoresSimulation")
        .feed(getTombstoneStoresPATHFeeder)
        .exec(http("getTombstoneStores")
        .httpRequest("GET","/api/admin/maint/stores/tombstone/${packageType}/hosted")
)

    // Run scngetTombstoneStores with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetTombstoneStores.inject(
        rampUsersPerSec(1) to(getTombstoneStoresPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getTombstoneStoresPerSecond) during(durationSeconds),
        rampUsersPerSec(getTombstoneStoresPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnrescan = scenario("rescanSimulation")
        .feed(rescanPATHFeeder)
        .exec(http("rescan")
        .httpRequest("GET","/api/admin/maint/rescan/${packageType}/${type}/${name}")
)

    // Run scnrescan with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnrescan.inject(
        rampUsersPerSec(1) to(rescanPerSecond) during(rampUpSeconds),
        constantUsersPerSec(rescanPerSecond) during(durationSeconds),
        rampUsersPerSec(rescanPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnrescanAll = scenario("rescanAllSimulation")
        .exec(http("rescanAll")
        .httpRequest("GET","/api/admin/maint/rescan/all")
)

    // Run scnrescanAll with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnrescanAll.inject(
        rampUsersPerSec(1) to(rescanAllPerSecond) during(rampUpSeconds),
        constantUsersPerSec(rescanAllPerSecond) during(durationSeconds),
        rampUsersPerSec(rescanAllPerSecond) to(1) during(rampDownSeconds)
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
