# GoodOldPlayFramework

Use Play Scala 2.5 like it's Play Scala 2.4

* Add the library in your build
```scala
resolvers += "good-old-play-framework repository" at "https://raw.githubusercontent.com/mathieuancelin/good-old-play-framework/master/repository/releases"

libraryDependencies += "org.reactivecouchbase" %% "good-old-play-framework" % "1.0.1"
```
* in the `conf/application.conf` file add
  - `play.application.loader = "gopf.play.GooOldPlayFrameworkLoader"`
* in the `build.sbt` file, add
  - `routesGenerator := StaticRoutesGenerator`
* declare your controllers as objects
  - `object MyController extends Controller { ... }`
* use the trait `gopf.play.GoodOldPlayframework` wherever you want or just use `gopf.play.api._` imports
* Have fun not using `@Inject` (except maybe for Filter, ErrorHandler, modules, etc ...)

## Before

```scala
package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.ws.WSClient
import scala.concurrent.ExecutionContext

@Singleton
class MyController @Inject()(wsClient: WSClient)(implicit ec: ExecutionContext) extends Controller {

  def ip = Action.async {
    wsClient.url("http://jsonip.com").get.map { resp =>
      Ok(resp.json)
    }
  }
}
```

## After

```scala
package controllers

import play.api.mvc._
import gopf.play.api.libs.concurrent.Execution.Implicits._
import gopf.play.api.libs.ws._

object MyController extends Controller {

  def ip = Action.async {
    WS.url("http://jsonip.com").get.map { resp =>
      Ok(resp.json)
    }
  }
}
```
