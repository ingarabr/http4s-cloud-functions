package com.github.ingarabr.http4s

import cats.data.Kleisli
import cats.effect.{Blocker, ContextShift, Sync}
import cats.implicits._
import com.google.cloud.functions.{HttpRequest, HttpResponse}
import org.http4s._
import scala.jdk.CollectionConverters._

abstract class Http4sCloudFunction[F[_]: Sync] {
  def routes: Kleisli[F, Request[F], Response[F]]

  def blocker: Blocker

  implicit def contextShift: ContextShift[F]

  protected def toResponse(
      toResponse: HttpResponse
  )(response: Response[F]): F[Unit] =
    for {
      _ <- blocker.delay(toResponse.setStatusCode(response.status.code))
      _ <- blocker.delay(
        response.headers.toList
          .map(_.toRaw)
          .foreach(h => toResponse.appendHeader(h.name.toString, h.value))
      )
      _ <-
        response.body
          .through(
            fs2.io
              .writeOutputStream(
                Sync[F].delay(toResponse.getOutputStream),
                blocker
              )
          )
          .compile
          .drain
    } yield ()

  protected def fromRequest(
      request: HttpRequest
  ): Either[ParseFailure, Request[F]] = {

    for {
      method <- Method.fromString(request.getMethod)
      uri <- Uri.fromString(request.getUri)
    } yield Request(
      method = method,
      uri = uri,
      headers = Headers.of(
        request.getHeaders.asScala
          .map[Header] {
            case (k, v) => Header(k, v.asScala.mkString(","))
          }
          .toList: _*
      ),
      body = fs2.io.readInputStream(
        fis = blocker.delay(request.getInputStream),
        chunkSize = 1024,
        blocker = blocker,
        closeAfterUse = true
      )
    )
  }

}
