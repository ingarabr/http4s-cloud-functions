package com.github.ingarabr.http4s

import cats.effect.unsafe.{IORuntime, IORuntimeConfig}
import cats.effect.IO
import com.google.cloud.functions.{HttpFunction, HttpRequest, HttpResponse}

trait Http4sCloudFunctionIOApp extends Http4sCloudFunction[IO] with HttpFunction {

  val defaultChunkSize = 1024

  val (scheduler, _) = IORuntime.createDefaultScheduler()

  implicit val runtime: IORuntime = IORuntime(
    CatsExecutionContexts.compute,
    CatsExecutionContexts.blocker,
    scheduler,
    () => (),
    IORuntimeConfig()
  )

  override def service(request: HttpRequest, response: HttpResponse): Unit =
    IO.fromEither(fromRequest(defaultChunkSize, request))
      .flatMap(routes.run)
      .flatMap(toResponse(response))
      .unsafeRunSync()

}
