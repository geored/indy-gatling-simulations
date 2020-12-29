package io.swagger.client.api

import io.swagger.client.model._
import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.PopulationBuilder

import java.io.File

import scala.collection.mutable

class PathMappedStorageMaintenanceOperationApiSimulation extends Simulation {

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
    val deletePerSecond = config.getDouble("performance.operationsPerSecond.delete") * rateMultiplier * instanceMultiplier
    val getPerSecond = config.getDouble("performance.operationsPerSecond.get") * rateMultiplier * instanceMultiplier
    val listPerSecond = config.getDouble("performance.operationsPerSecond.list") * rateMultiplier * instanceMultiplier
    val listRootPerSecond = config.getDouble("performance.operationsPerSecond.listRoot") * rateMultiplier * instanceMultiplier

    val scenarioBuilders: mutable.MutableList[PopulationBuilder] = new mutable.MutableList[PopulationBuilder]()

    // Set up CSV feeders
    val deletePATHFeeder = csv(userDataDirectory + File.separator + "delete-pathParams.csv").random
    val getPATHFeeder = csv(userDataDirectory + File.separator + "get-pathParams.csv").random
    val listQUERYFeeder = csv(userDataDirectory + File.separator + "list-queryParams.csv").random
    val listPATHFeeder = csv(userDataDirectory + File.separator + "list-pathParams.csv").random
    val listRootQUERYFeeder = csv(userDataDirectory + File.separator + "listRoot-queryParams.csv").random
    val listRootPATHFeeder = csv(userDataDirectory + File.separator + "listRoot-pathParams.csv").random

    // Setup all scenarios

    
    val scndelete = scenario("deleteSimulation")
        .feed(deletePATHFeeder)
        .exec(http("delete")
        .httpRequest("DELETE","/api/admin/pathmapped/content/${packageType}/${type}/${name}/${path}")
)

    // Run scndelete with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndelete.inject(
        rampUsersPerSec(1) to(deletePerSecond) during(rampUpSeconds),
        constantUsersPerSec(deletePerSecond) during(durationSeconds),
        rampUsersPerSec(deletePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnget = scenario("getSimulation")
        .feed(getPATHFeeder)
        .exec(http("get")
        .httpRequest("GET","/api/admin/pathmapped/content/${packageType}/${type}/${name}/${path}")
)

    // Run scnget with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnget.inject(
        rampUsersPerSec(1) to(getPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getPerSecond) during(durationSeconds),
        rampUsersPerSec(getPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnlist = scenario("listSimulation")
        .feed(listQUERYFeeder)
        .feed(listPATHFeeder)
        .exec(http("list")
        .httpRequest("GET","/api/admin/pathmapped/browse/${packageType}/${type}/${name}/${path}")
        .queryParam("recursive","${recursive}")
        .queryParam("type","${type}")
        .queryParam("limit","${limit}")
)

    // Run scnlist with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnlist.inject(
        rampUsersPerSec(1) to(listPerSecond) during(rampUpSeconds),
        constantUsersPerSec(listPerSecond) during(durationSeconds),
        rampUsersPerSec(listPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnlistRoot = scenario("listRootSimulation")
        .feed(listRootQUERYFeeder)
        .feed(listRootPATHFeeder)
        .exec(http("listRoot")
        .httpRequest("GET","/api/admin/pathmapped/browse/${packageType}/${type}/${name}")
        .queryParam("recursive","${recursive}")
        .queryParam("type","${type}")
        .queryParam("limit","${limit}")
)

    // Run scnlistRoot with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnlistRoot.inject(
        rampUsersPerSec(1) to(listRootPerSecond) during(rampUpSeconds),
        constantUsersPerSec(listRootPerSecond) during(durationSeconds),
        rampUsersPerSec(listRootPerSecond) to(1) during(rampDownSeconds)
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
