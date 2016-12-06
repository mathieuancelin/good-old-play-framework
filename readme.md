# GoodOldPlayFramework

Use Play Scala 2.5  like it's Play Scala 2.4

* Add the library in your build
```scala
resolvers += "good-old-play-framework repository" at "https://raw.githubusercontent.com/mathieuancelin/good-old-play-framework/master/repository/releases"

libraryDependencies += "org.reactivecouchbase" %% "good-old-play-framework" % "1.0.0"
```
* in the `conf/application.conf` file add
  - `play.application.loader = "old.play.GooOldPlayframeworkLoader"`
* in the `build.sbt` file, add
  - `routesGenerator := StaticRoutesGenerator`
* declare your controllers as objects
  - `object MyController extends Controller { ... }`
* use the trait `old.play.GoodOldPlayframework` wherever you want or just use `old.play.api._` imports
* Have fun not using `@Inject` (except maybe for Filter, ErrorHandler, modules, etc ...)


```scala
package controllers

import play.api.mvc._
import old.play.api.libs.concurrent.Execution.Implicits._
import old.play.api.libs.ws._

object MyController extends Controller {

  def ip = Action.async {
    WS.url("http://jsonip.com").get.map { resp =>
      Ok(resp.json)
    }
  }
}
```