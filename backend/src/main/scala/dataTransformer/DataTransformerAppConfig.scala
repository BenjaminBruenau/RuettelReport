package dataTransformer

import service.environment.DefaultConfig
import service.environment.EnvConfig.env

object DataTransformerAppConfig extends DefaultConfig {

    lazy val host: String = env("HOST", "localhost")
    lazy val port: Int = env("PORT", 8080)
    lazy val kafkaBootstrapServers: String = env("KAFKA_BOOTSTRAP_SERVERS", "localhost:29092")
    
}