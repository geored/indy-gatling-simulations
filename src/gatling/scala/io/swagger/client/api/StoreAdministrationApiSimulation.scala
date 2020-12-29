package io.swagger.client.api

import io.swagger.client.model._
import com.typesafe.config.ConfigFactory

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.PopulationBuilder

import java.io.File

import scala.collection.mutable

class StoreAdministrationApiSimulation extends Simulation {

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
    val createPerSecond = config.getDouble("performance.operationsPerSecond.create") * rateMultiplier * instanceMultiplier
    val create_0PerSecond = config.getDouble("performance.operationsPerSecond.create_0") * rateMultiplier * instanceMultiplier
    val deletePerSecond = config.getDouble("performance.operationsPerSecond.delete") * rateMultiplier * instanceMultiplier
    val delete_0PerSecond = config.getDouble("performance.operationsPerSecond.delete_0") * rateMultiplier * instanceMultiplier
    val existsPerSecond = config.getDouble("performance.operationsPerSecond.exists") * rateMultiplier * instanceMultiplier
    val exists_0PerSecond = config.getDouble("performance.operationsPerSecond.exists_0") * rateMultiplier * instanceMultiplier
    val getPerSecond = config.getDouble("performance.operationsPerSecond.get") * rateMultiplier * instanceMultiplier
    val getAllPerSecond = config.getDouble("performance.operationsPerSecond.getAll") * rateMultiplier * instanceMultiplier
    val getAll_0PerSecond = config.getDouble("performance.operationsPerSecond.getAll_0") * rateMultiplier * instanceMultiplier
    val getRemoteByUrlPerSecond = config.getDouble("performance.operationsPerSecond.getRemoteByUrl") * rateMultiplier * instanceMultiplier
    val get_0PerSecond = config.getDouble("performance.operationsPerSecond.get_0") * rateMultiplier * instanceMultiplier
    val returnDisabledStoresPerSecond = config.getDouble("performance.operationsPerSecond.returnDisabledStores") * rateMultiplier * instanceMultiplier
    val revalidateArtifactStorePerSecond = config.getDouble("performance.operationsPerSecond.revalidateArtifactStore") * rateMultiplier * instanceMultiplier
    val revalidateArtifactStoresPerSecond = config.getDouble("performance.operationsPerSecond.revalidateArtifactStores") * rateMultiplier * instanceMultiplier
    val storePerSecond = config.getDouble("performance.operationsPerSecond.store") * rateMultiplier * instanceMultiplier
    val store_0PerSecond = config.getDouble("performance.operationsPerSecond.store_0") * rateMultiplier * instanceMultiplier

    val scenarioBuilders: mutable.MutableList[PopulationBuilder] = new mutable.MutableList[PopulationBuilder]()

    // Set up CSV feeders
    val createPATHFeeder1 = csv(userDataDirectory + File.separator + "create-pathParams.csv").random
    val createBodyFeeder1 = csv(userDataDirectory + File.separator + "create-bodyParams.csv", escapeChar = '\\').random
    val createPATHFeeder = csv(userDataDirectory + File.separator + "create_0-pathParams.csv").random
    val createBodyFeeder = csv(userDataDirectory + File.separator + "create_0-bodyParams.csv", escapeChar = '\\').random
    val deleteQUERYFeeder = csv(userDataDirectory + File.separator + "delete-queryParams.csv").random
    val deletePATHFeeder1 = csv(userDataDirectory + File.separator + "delete-pathParams.csv").random
    val deletePATHFeeder = csv(userDataDirectory + File.separator + "delete_0-pathParams.csv").random
    val existsPATHFeeder1 = csv(userDataDirectory + File.separator + "exists-pathParams.csv").random
    val existsPATHFeeder = csv(userDataDirectory + File.separator + "exists_0-pathParams.csv").random
    val getPATHFeeder1 = csv(userDataDirectory + File.separator + "get-pathParams.csv").random
    val getAllPATHFeeder1 = csv(userDataDirectory + File.separator + "getAll-pathParams.csv").random
    val getAllPATHFeeder = csv(userDataDirectory + File.separator + "getAll_0-pathParams.csv").random
    val getRemoteByUrlQUERYFeeder = csv(userDataDirectory + File.separator + "getRemoteByUrl-queryParams.csv").random
    val getRemoteByUrlPATHFeeder = csv(userDataDirectory + File.separator + "getRemoteByUrl-pathParams.csv").random
    val getPATHFeeder = csv(userDataDirectory + File.separator + "get_0-pathParams.csv").random
    val returnDisabledStoresPATHFeeder = csv(userDataDirectory + File.separator + "returnDisabledStores-pathParams.csv").random
    val revalidateArtifactStorePATHFeeder = csv(userDataDirectory + File.separator + "revalidateArtifactStore-pathParams.csv").random
    val revalidateArtifactStoresPATHFeeder = csv(userDataDirectory + File.separator + "revalidateArtifactStores-pathParams.csv").random
    val storePATHFeeder1 = csv(userDataDirectory + File.separator + "store-pathParams.csv").random
    val storeBodyFeeder1 = csv(userDataDirectory + File.separator + "store-bodyParams.csv", escapeChar = '\\').random
    val storePATHFeeder = csv(userDataDirectory + File.separator + "store_0-pathParams.csv").random
    val storeBodyFeeder = csv(userDataDirectory + File.separator + "store_0-bodyParams.csv", escapeChar = '\\').random

    // Setup all scenarios

    
    val scncreate = scenario("createSimulation")
        .feed(createBodyFeeder)
        .feed(createPATHFeeder)
        .exec(http("create")
        .httpRequest("POST","/api/admin/stores/${packageType}/${type}")
        .body(StringBody(ArtifactStore.toStringBody("${path_mask_patterns}","${metadata}","${path_style}","${authoritative_index}","${key}","${description}","${name}","${type}","${create_time}","${disable_timeout}","${packageType}","${disabled}")))
        )

    // Run scncreate with warm up and reach a constant rate for entire duration
    scenarioBuilders += scncreate.inject(
        rampUsersPerSec(1) to(createPerSecond) during(rampUpSeconds),
        constantUsersPerSec(createPerSecond) during(durationSeconds),
        rampUsersPerSec(createPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scncreate_0 = scenario("create_0Simulation")
        .feed(createBodyFeeder)
        .feed(createPATHFeeder)
        .exec(http("create_0")
        .httpRequest("POST","/api/admin/${type}")
        .body(StringBody(ArtifactStore.toStringBody("${path_mask_patterns}","${metadata}","${path_style}","${authoritative_index}","${key}","${description}","${name}","${type}","${create_time}","${disable_timeout}","${packageType}","${disabled}")))
        )

    // Run scncreate_0 with warm up and reach a constant rate for entire duration
    scenarioBuilders += scncreate_0.inject(
        rampUsersPerSec(1) to(create_0PerSecond) during(rampUpSeconds),
        constantUsersPerSec(create_0PerSecond) during(durationSeconds),
        rampUsersPerSec(create_0PerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndelete = scenario("deleteSimulation")
        .feed(deleteQUERYFeeder)
        .feed(deletePATHFeeder)
        .exec(http("delete")
        .httpRequest("DELETE","/api/admin/stores/${packageType}/${type}/${name}")
        .queryParam("deleteContent","${deleteContent}")
)

    // Run scndelete with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndelete.inject(
        rampUsersPerSec(1) to(deletePerSecond) during(rampUpSeconds),
        constantUsersPerSec(deletePerSecond) during(durationSeconds),
        rampUsersPerSec(deletePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scndelete_0 = scenario("delete_0Simulation")
        .feed(deletePATHFeeder)
        .exec(http("delete_0")
        .httpRequest("DELETE","/api/admin/${type}/${name}")
)

    // Run scndelete_0 with warm up and reach a constant rate for entire duration
    scenarioBuilders += scndelete_0.inject(
        rampUsersPerSec(1) to(delete_0PerSecond) during(rampUpSeconds),
        constantUsersPerSec(delete_0PerSecond) during(durationSeconds),
        rampUsersPerSec(delete_0PerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnexists = scenario("existsSimulation")
        .feed(existsPATHFeeder)
        .exec(http("exists")
        .httpRequest("HEAD","/api/admin/stores/${packageType}/${type}/${name}")
)

    // Run scnexists with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnexists.inject(
        rampUsersPerSec(1) to(existsPerSecond) during(rampUpSeconds),
        constantUsersPerSec(existsPerSecond) during(durationSeconds),
        rampUsersPerSec(existsPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnexists_0 = scenario("exists_0Simulation")
        .feed(existsPATHFeeder)
        .exec(http("exists_0")
        .httpRequest("HEAD","/api/admin/${type}/${name}")
)

    // Run scnexists_0 with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnexists_0.inject(
        rampUsersPerSec(1) to(exists_0PerSecond) during(rampUpSeconds),
        constantUsersPerSec(exists_0PerSecond) during(durationSeconds),
        rampUsersPerSec(exists_0PerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnget = scenario("getSimulation")
        .feed(getPATHFeeder)
        .exec(http("get")
        .httpRequest("GET","/api/admin/stores/${packageType}/${type}/${name}")
)

    // Run scnget with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnget.inject(
        rampUsersPerSec(1) to(getPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getPerSecond) during(durationSeconds),
        rampUsersPerSec(getPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetAll = scenario("getAllSimulation")
        .feed(getAllPATHFeeder)
        .exec(http("getAll")
        .httpRequest("GET","/api/admin/stores/${packageType}/${type}")
)

    // Run scngetAll with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetAll.inject(
        rampUsersPerSec(1) to(getAllPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getAllPerSecond) during(durationSeconds),
        rampUsersPerSec(getAllPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetAll_0 = scenario("getAll_0Simulation")
        .feed(getAllPATHFeeder)
        .exec(http("getAll_0")
        .httpRequest("GET","/api/admin/${type}")
)

    // Run scngetAll_0 with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetAll_0.inject(
        rampUsersPerSec(1) to(getAll_0PerSecond) during(rampUpSeconds),
        constantUsersPerSec(getAll_0PerSecond) during(durationSeconds),
        rampUsersPerSec(getAll_0PerSecond) to(1) during(rampDownSeconds)
    )

    
    val scngetRemoteByUrl = scenario("getRemoteByUrlSimulation")
        .feed(getRemoteByUrlQUERYFeeder)
        .feed(getRemoteByUrlPATHFeeder)
        .exec(http("getRemoteByUrl")
        .httpRequest("GET","/api/admin/stores/${packageType}/${type}/query/byUrl")
        .queryParam("url","${url}")
)

    // Run scngetRemoteByUrl with warm up and reach a constant rate for entire duration
    scenarioBuilders += scngetRemoteByUrl.inject(
        rampUsersPerSec(1) to(getRemoteByUrlPerSecond) during(rampUpSeconds),
        constantUsersPerSec(getRemoteByUrlPerSecond) during(durationSeconds),
        rampUsersPerSec(getRemoteByUrlPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnget_0 = scenario("get_0Simulation")
        .feed(getPATHFeeder)
        .exec(http("get_0")
        .httpRequest("GET","/api/admin/${type}/${name}")
)

    // Run scnget_0 with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnget_0.inject(
        rampUsersPerSec(1) to(get_0PerSecond) during(rampUpSeconds),
        constantUsersPerSec(get_0PerSecond) during(durationSeconds),
        rampUsersPerSec(get_0PerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnreturnDisabledStores = scenario("returnDisabledStoresSimulation")
        .feed(returnDisabledStoresPATHFeeder)
        .exec(http("returnDisabledStores")
        .httpRequest("GET","/api/admin/stores/${packageType}/${type}/all_invalid")
)

    // Run scnreturnDisabledStores with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnreturnDisabledStores.inject(
        rampUsersPerSec(1) to(returnDisabledStoresPerSecond) during(rampUpSeconds),
        constantUsersPerSec(returnDisabledStoresPerSecond) during(durationSeconds),
        rampUsersPerSec(returnDisabledStoresPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnrevalidateArtifactStore = scenario("revalidateArtifactStoreSimulation")
        .feed(revalidateArtifactStorePATHFeeder)
        .exec(http("revalidateArtifactStore")
        .httpRequest("POST","/api/admin/stores/${packageType}/${type}/${name}/revalidate")
)

    // Run scnrevalidateArtifactStore with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnrevalidateArtifactStore.inject(
        rampUsersPerSec(1) to(revalidateArtifactStorePerSecond) during(rampUpSeconds),
        constantUsersPerSec(revalidateArtifactStorePerSecond) during(durationSeconds),
        rampUsersPerSec(revalidateArtifactStorePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnrevalidateArtifactStores = scenario("revalidateArtifactStoresSimulation")
        .feed(revalidateArtifactStoresPATHFeeder)
        .exec(http("revalidateArtifactStores")
        .httpRequest("POST","/api/admin/stores/${packageType}/${type}/revalidate/all")
)

    // Run scnrevalidateArtifactStores with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnrevalidateArtifactStores.inject(
        rampUsersPerSec(1) to(revalidateArtifactStoresPerSecond) during(rampUpSeconds),
        constantUsersPerSec(revalidateArtifactStoresPerSecond) during(durationSeconds),
        rampUsersPerSec(revalidateArtifactStoresPerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnstore = scenario("storeSimulation")
        .feed(storeBodyFeeder)
        .feed(storePATHFeeder)
        .exec(http("store")
        .httpRequest("PUT","/api/admin/stores/${packageType}/${type}/${name}")
        .body(StringBody(ArtifactStore.toStringBody("${path_mask_patterns}","${metadata}","${path_style}","${authoritative_index}","${key}","${description}","${name}","${type}","${create_time}","${disable_timeout}","${packageType}","${disabled}")))
        )

    // Run scnstore with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnstore.inject(
        rampUsersPerSec(1) to(storePerSecond) during(rampUpSeconds),
        constantUsersPerSec(storePerSecond) during(durationSeconds),
        rampUsersPerSec(storePerSecond) to(1) during(rampDownSeconds)
    )

    
    val scnstore_0 = scenario("store_0Simulation")
        .feed(storeBodyFeeder)
        .feed(storePATHFeeder)
        .exec(http("store_0")
        .httpRequest("PUT","/api/admin/${type}/${name}")
        .body(StringBody(ArtifactStore.toStringBody("${path_mask_patterns}","${metadata}","${path_style}","${authoritative_index}","${key}","${description}","${name}","${type}","${create_time}","${disable_timeout}","${packageType}","${disabled}")))
        )

    // Run scnstore_0 with warm up and reach a constant rate for entire duration
    scenarioBuilders += scnstore_0.inject(
        rampUsersPerSec(1) to(store_0PerSecond) during(rampUpSeconds),
        constantUsersPerSec(store_0PerSecond) during(durationSeconds),
        rampUsersPerSec(store_0PerSecond) to(1) during(rampDownSeconds)
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
