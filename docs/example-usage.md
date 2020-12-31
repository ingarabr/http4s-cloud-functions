# Example usage

A simple application

```scala mdoc
import cats.effect.IO
import cats.syntax.show._
import com.github.ingarabr.http4s.Http4sCloudFunctionIOApp
import org.http4s._
import org.http4s.dsl._
import org.http4s.implicits._

class HelloFunction extends Http4sCloudFunctionIOApp with Http4sDsl[IO] {

  def routes: HttpApp[IO] =
    HttpRoutes
      .of[IO] {
        case req => Ok(show"Hello there.. ${req.method}")
      }
      .orNotFound

}

```
