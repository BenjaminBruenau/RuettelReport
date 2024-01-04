package service.environment

import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success, Try}


case class MissingEnvVariableException(variableName: String)
  extends RuntimeException(s"Environment variable '$variableName' not found and no default value provided.")

trait Secret[A]:
  def secretValue: A

object Secret:
  def apply[A](v: A): Secret[A] = new Secret[A]:
    val secretValue: A = v
    private val mask: String = "****"
    override def toString: String = mask


trait EnvConfig[A]:
  def apply(name: String): Option[A]
  final def map[B](f: A => B): EnvConfig[B] = name => apply(name).map(f)

object EnvConfig:
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def env[A](name: String, default: => A)(using get: EnvConfig[A]): A =
    Try(get(name).getOrElse(default)) match
      case Failure(exception) => throw MissingEnvVariableException(name)
      case Success(value) => value

  def envS[A](name: String, default: => A)(using get: EnvConfig[A]): Secret[A] =
    Try(get(name).getOrElse(default)) match
      case Failure(exception) => throw MissingEnvVariableException(name)
      case Success(value) => Secret(value)

  given string: EnvConfig[String] = name =>
    logger.info(s"Reading env:$name")
    Option(System.getenv(name))

  given EnvConfig[Int] = string.map(_.toInt)
  given EnvConfig[Boolean] = string.map(_.toBoolean)



// Default Config shared across services
trait DefaultConfig:

  def host: String
  def port: Int