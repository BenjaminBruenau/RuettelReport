package queryService

import commons.environment.DefaultConfig
import commons.environment.EnvConfig.env
object QueryServiceAppConfig extends DefaultConfig {

    lazy val host: String = env("HOST", "0.0.0.0")
    lazy val port: Int = env("PORT", 8080)
    lazy val kafkaBootstrapServers: String = env("KAFKA_BOOTSTRAP_SERVERS", "localhost:29092")
    
}