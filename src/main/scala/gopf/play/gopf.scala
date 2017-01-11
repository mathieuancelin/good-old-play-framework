package gopf.play

import java.sql.Connection
import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}
import java.util.concurrent.{Executors, ThreadFactory}

import akka.actor.{ActorSystem, Scheduler}
import akka.stream.Materializer
import play.api.ApplicationLoader.Context
import play.api.Mode.Mode
import play.api._
import play.api.cache.CacheApi
import play.api.db.DBApi
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

class GooOldPlayFrameworkLoader extends ApplicationLoader {
  def load(context: Context) = {
    val application = new GuiceApplicationBuilder(
      environment = context.environment,
      configuration = context.initialConfiguration
    ).build()
    GooOldPlayFrameworkContext._ref.set(application) // Wooow !!!!!
    application
  }
}

object GooOldPlayFrameworkContext {

  // Yeah, I know, it's really really bad ...
  private[play] val _ref: AtomicReference[Application] = new AtomicReference[Application]()

  lazy val application: Application = Option(_ref.get()).get
  lazy val actorSystem: ActorSystem = application.actorSystem
  lazy val materializer: Materializer = application.materializer
  lazy val configuration: Configuration = application.configuration
  lazy val mode: Mode = application.mode
  lazy val scheduler: Scheduler = actorSystem.scheduler
  lazy val injector: Injector = application.injector
  lazy val playExecutionContext: ExecutionContext = injector.instanceOf(classOf[ExecutionContext])
  lazy val environment: Environment = injector.instanceOf(classOf[Environment])
  lazy val WS: WSClient = injector.instanceOf(classOf[WSClient])
  lazy val dbApi: DBApi = injector.instanceOf(classOf[DBApi])
  lazy val cache: CacheApi = injector.instanceOf(classOf[CacheApi])
  lazy val procNbr = Runtime.getRuntime.availableProcessors()

  private def factory(of: String) = new ThreadFactory {
    val counter = new AtomicInteger(0)
    override def newThread(r: Runnable): Thread = new Thread(r, s"$of-${counter.incrementAndGet()}")
  }

  lazy val httpRequestExecContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(procNbr *
    configuration.getInt("gopf.threadpools.http-requests").getOrElse(2), factory("http-requests")))
  lazy val httpCallsExecContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(procNbr *
    configuration.getInt("gopf.threadpools.http-calls").getOrElse(10), factory("http-calls")))
  lazy val dataStoreExecContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(procNbr *
    configuration.getInt("gopf.threadpools.data-store").getOrElse(5), factory("data-store")))

}

trait GoodOldPlayframework {

  object Implicits {
    implicit def defaultActorSystem: ActorSystem = GooOldPlayFrameworkContext.actorSystem
    implicit def defaultMaterializer: Materializer = GooOldPlayFrameworkContext.materializer
    implicit def defaultScheduler: Scheduler = GooOldPlayFrameworkContext.scheduler
    implicit def defaultContext: ExecutionContext = GooOldPlayFrameworkContext.playExecutionContext
  }

  def WS = GooOldPlayFrameworkContext.WS
  def Cache = GooOldPlayFrameworkContext.cache
  def Configuration = GooOldPlayFrameworkContext.configuration
  def Application = GooOldPlayFrameworkContext.application
  def Injector = GooOldPlayFrameworkContext.injector
  def Mode = GooOldPlayFrameworkContext.mode
  def DB = GooOldPlayFrameworkContext.dbApi

  def currentApplication = GooOldPlayFrameworkContext.application
  def defaultContext = GooOldPlayFrameworkContext.playExecutionContext
  def defaultScheduler = GooOldPlayFrameworkContext.scheduler
  def defaultMaterializer = GooOldPlayFrameworkContext.scheduler
  def defaultActorSystem = GooOldPlayFrameworkContext.configuration
  def httpRequestsContext = GooOldPlayFrameworkContext.httpRequestExecContext
  def httpCallsContext = GooOldPlayFrameworkContext.httpCallsExecContext
  def dataStoreContext = GooOldPlayFrameworkContext.dataStoreExecContext
}

object api {
  object Play {
    def application = GooOldPlayFrameworkContext.application
    def maybeApplication = Option(GooOldPlayFrameworkContext.application)
    def injector = GooOldPlayFrameworkContext.injector
    def classloader = GooOldPlayFrameworkContext.application.classloader
    def configuration = GooOldPlayFrameworkContext.configuration
    def current = GooOldPlayFrameworkContext.application
    def isDev = GooOldPlayFrameworkContext.mode == Mode.Dev
    def isProd = GooOldPlayFrameworkContext.mode == Mode.Prod
    def isTest = GooOldPlayFrameworkContext.mode == Mode.Test
    def mode = GooOldPlayFrameworkContext.mode
    def getFile(relativePath: String) = GooOldPlayFrameworkContext.application.getFile(relativePath)
    def getExistingFile(relativePath: String) = GooOldPlayFrameworkContext.application.getExistingFile(relativePath)
    def resource(name: String) = GooOldPlayFrameworkContext.application.resource(name)
    def resourceAsStream(name: String) = GooOldPlayFrameworkContext.application.resourceAsStream(name)
  }
  object libs {
    object db {
      object DB {
        def getConnection(name: String = "default", autocommit: Boolean = true) = GooOldPlayFrameworkContext.dbApi.database(name).getConnection(autocommit)
        def getDataSource(name: String = "default") = GooOldPlayFrameworkContext.dbApi.database(name).dataSource
        def withConnection[A](block: (Connection) => A) = GooOldPlayFrameworkContext.dbApi.database("default").withConnection(block)
        def withConnection[A](name: String)(block: (Connection) => A) = GooOldPlayFrameworkContext.dbApi.database(name).withConnection(block)
        def withTransaction[A](block: (Connection) => A) = GooOldPlayFrameworkContext.dbApi.database("default").withTransaction(block)
        def withTransaction[A](name: String = "default")(block: (Connection) => A) = GooOldPlayFrameworkContext.dbApi.database(name).withTransaction(block)
      }
    }
    object ws {
      def WS = GooOldPlayFrameworkContext.WS
    }
    object cache {
      def Cache = GooOldPlayFrameworkContext.cache
    }
    object concurrent {
      object Akka {
        object Implicits {
          implicit def defaultActorSystem: ActorSystem = GooOldPlayFrameworkContext.actorSystem
          implicit def defaultMaterializer: Materializer = GooOldPlayFrameworkContext.materializer
          implicit def defaultScheduler: Scheduler = GooOldPlayFrameworkContext.scheduler
        }
        def defaultScheduler: Scheduler = GooOldPlayFrameworkContext.scheduler
        def defaultActorSystem: ActorSystem = GooOldPlayFrameworkContext.actorSystem
        def defaultMaterializer: Materializer = GooOldPlayFrameworkContext.materializer
      }
      object Execution {
        object Implicits {
          implicit def defaultContext: ExecutionContext = GooOldPlayFrameworkContext.playExecutionContext
        }
        def defaultContext: ExecutionContext = GooOldPlayFrameworkContext.playExecutionContext
        def httpRequestsContext = GooOldPlayFrameworkContext.httpRequestExecContext
        def httpCallsContext = GooOldPlayFrameworkContext.httpCallsExecContext
        def dataStoreContext = GooOldPlayFrameworkContext.dataStoreExecContext
      }
    }
  }
}
