package com.github.ingarabr.http4s

import cats.effect.Blocker

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{Executors, ThreadFactory}
import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

/**
  * Inspired by cats.effect.internal.PoolUtils
  */
object CatsExecutionContexts {

  val blocker: Blocker = {
    val executor = Executors.newCachedThreadPool(factory("blocking"))
    val service = exitOnFatal(ExecutionContext.fromExecutorService(executor))
    Blocker.liftExecutionContext(service)
  }

  val compute: ExecutionContext = {
    // lower-bound of 2 to prevent pathological deadlocks on virtual machines
    val bound = math.max(2, Runtime.getRuntime.availableProcessors())
    val executor = Executors.newFixedThreadPool(bound, factory("compute"))
    exitOnFatal(ExecutionContext.fromExecutor(executor))
  }

  private def factory(name: String) =
    new ThreadFactory {
      val ctr = new AtomicInteger(0)

      def newThread(r: Runnable): Thread = {
        val back = new Thread(r, s"$name-${ctr.getAndIncrement()}")
        back.setDaemon(true)
        back
      }
    }

  private def exitOnFatal(ec: ExecutionContext): ExecutionContext =
    new ExecutionContext {
      def execute(r: Runnable): Unit =
        ec.execute(new Runnable {
          def run(): Unit =
            try {
              r.run()
            } catch {
              case NonFatal(t) =>
                reportFailure(t)

              case t: Throwable =>
                // under most circumstances, this will work even with fatal errors
                t.printStackTrace()
                System.exit(1)
            }
        })

      def reportFailure(t: Throwable): Unit =
        ec.reportFailure(t)
    }

}
