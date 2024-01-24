package dataTransformer

import commons.environment.DefaultConfig
import commons.environment.EnvConfig.env

object DataTransformerAppConfig extends DefaultConfig {

    lazy val host: String = env("HOST", "0.0.0.0")
    lazy val port: Int = env("PORT", 8081)
    lazy val kafkaBootstrapServers: String = env("KAFKA_BOOTSTRAP_SERVERS", ???)
    
}