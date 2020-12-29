# indy-gatling-simulations

- This Gatling simulation tests are generated from indy swager REST endpoints

For using them you must provide several informations:

  - Adding your own ( or created token ) indy keycloak BASIC Authentication in every scala file at HEADERS location:
    - val sentHeaders =  Map("Authorization" -> "Basic {ADD YOUR BASIC AUTHENTICATION CODE HERE}")
  - Adding INDY host name where you want this test simulations to make requests:
    - val httpConf = http.baseURL("http://indy-admin-master-devel.psi.redhat.com")// ADD INDY HOSTNAME HERE
  - Adding specifig data information (packageType, name , key , args,dryRun,source ...) for replacing endpoint path arguments inside ./src/gatling/resources directory
  - (OPTIONAL) Change data parameters inside ./src/gatling/resources/conf directory for gatling simulation execution data 
    - Like: authorizationHeader = ""  rampUpSeconds = 60 rampDownSeconds = 60  durationSeconds = 360 contentType = "application/json" acceptType = "application/json"rateMultiplier= 1 instanceMultiplier = 1 operationsPerSecond itn...
