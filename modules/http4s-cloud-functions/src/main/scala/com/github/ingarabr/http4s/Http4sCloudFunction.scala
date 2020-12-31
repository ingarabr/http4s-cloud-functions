package com.github.ingarabr.http4s

import cats.effect.{Blocker, ContextShift, Sync}
import cats.syntax.apply._
import cats.syntax.functor._
import cats.syntax.flatMap._
import com.google.cloud.functions.{HttpRequest, HttpResponse}
import fs2.io.{readInputStream, writeOutputStream}
import org.http4s.{Header, Headers, HttpApp, Method, ParseResult, Request, Response, Uri}

import scala.jdk.CollectionConverters._

abstract class Http4sCloudFunction[F[_]: Sync] {
  def routes: HttpApp[F]

  def blocker: Blocker

  implicit def contextShift: ContextShift[F]

  protected def toResponse(httpResponse: HttpResponse)(response: Response[F]): F[Unit] =
    for {
      _ <- blocker.delay {
        httpResponse.setStatusCode(response.status.code)
        response.headers.foreach(h => httpResponse.appendHeader(h.name.toString, h.value))
      }
      fos = Sync[F].delay(httpResponse.getOutputStream)
      _ <- response.body.through(writeOutputStream(fos, blocker)).compile.drain
    } yield ()

  protected def fromRequest(chunkSize: Int, request: HttpRequest): ParseResult[Request[F]] =
    (Method.fromString(request.getMethod), Uri.fromString(request.getUri))
      .mapN((method, uri) =>
        Request(
          method = method,
          uri = uri,
          headers = Headers(
            request.getHeaders.asScala.view
              .mapValues(_.asScala.mkString(","))
              .map { case (k, v) => Header(k, v) }
              .toList
          ),
          body = readInputStream(
            fis = blocker.delay(request.getInputStream),
            chunkSize = chunkSize,
            blocker = blocker,
            closeAfterUse = true
          )
        )
      )

}
