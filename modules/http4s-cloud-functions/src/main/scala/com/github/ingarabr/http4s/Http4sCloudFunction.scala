package com.github.ingarabr.http4s

import cats.effect.Sync
import cats.syntax.apply._
import cats.syntax.functor._
import cats.syntax.flatMap._
import com.google.cloud.functions.{HttpRequest, HttpResponse}
import fs2.io.{readInputStream, writeOutputStream}
import org.typelevel.ci.CIString
import org.http4s.{Header, Headers, HttpApp, Method, ParseResult, Request, Response, Uri}

import scala.jdk.CollectionConverters._

abstract class Http4sCloudFunction[F[_]: Sync] {
  def routes: HttpApp[F]

  protected def toResponse(httpResponse: HttpResponse)(response: Response[F]): F[Unit] =
    for {
      _ <- Sync[F].blocking {
        httpResponse.setStatusCode(response.status.code)
        response.headers.foreach(h => httpResponse.appendHeader(h.name.toString, h.value))
      }
      fos = Sync[F].delay(httpResponse.getOutputStream)
      _ <- response.body.through(writeOutputStream(fos)).compile.drain
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
              .map { case (k, v) => Header.Raw(CIString(k), v) }
              .toList
          ),
          body = readInputStream(
            fis = Sync[F].blocking(request.getInputStream),
            chunkSize = chunkSize,
            closeAfterUse = true
          )
        )
      )

}
