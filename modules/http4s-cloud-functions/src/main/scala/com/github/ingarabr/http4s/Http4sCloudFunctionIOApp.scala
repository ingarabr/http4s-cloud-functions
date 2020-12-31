package com.github.ingarabr.http4s

import cats.effect.{Blocker, ContextShift, IO}
import com.google.cloud.functions.{HttpFunction, HttpRequest, HttpResponse}

trait Http4sCloudFunctionIOApp extends Http4sCloudFunction[IO] with HttpFunction {

  implicit val contextShift: ContextShift[IO] = IO.contextShift(CatsExecutionContexts.compute)
  val blocker: Blocker = CatsExecutionContexts.blocker

  override def service(request: HttpRequest, response: HttpResponse): Unit =
    IO.fromEither(fromRequest(request))
      .flatMap(routes.run)
      .flatMap(toResponse(response))
      .unsafeRunSync()

}
