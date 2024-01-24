package queryService

import commons.environment.DefaultConfig
import commons.environment.EnvConfig.env
object QueryServiceAppConfig extends DefaultConfig {

    lazy val host: String = env("QUERY_SERVICE_HOST", "0.0.0.0")
    lazy val port: Int = env("QUERY_SERVICE_PORT", 8080)
    lazy val kafkaBootstrapServers: String = env("KAFKA_BOOTSTRAP_SERVERS", ???)
    
}