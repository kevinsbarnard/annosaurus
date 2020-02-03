package org.mbari.vars.annotation.messaging.zeromq

import java.util.concurrent.{LinkedBlockingQueue, TimeUnit}

import io.reactivex.disposables.Disposable
import io.reactivex.subjects.Subject
import org.mbari.vars.annotation.Constants
import org.mbari.vars.annotation.messaging.{AnnotationMessage, MessageBus}
import org.slf4j.LoggerFactory
import org.zeromq.{SocketType, ZContext}

import scala.util.control.NonFatal



/**
 * @author Brian Schlining
 * @since 2020-01-30T15:47:00
 */
class ZeroMQPublisher(val topic: String,
                      val port: Int,
                      val subject: Subject[_]) {

  private[this] val context = new ZContext()
  private[this] val queue = new LinkedBlockingQueue[AnnotationMessage]()
  private[this] val disposable: Disposable = MessageBus.RxSubject
    .ofType(classOf[AnnotationMessage])
    .subscribe(m => queue.offer(m))
  private[this] val log = LoggerFactory.getLogger(getClass)

  @volatile
  var ok = true
  val thread = new Thread(new Runnable {
    private val publisher = context.createSocket(SocketType.PUB)
    publisher.bind(s"tcp://*:$port")

    override def run(): Unit = while (ok) {
      try {
        val msg = queue.poll(3600L, TimeUnit.SECONDS)
        if (msg != null) {
          val json = Constants.GSON.toJson(msg.content)
          publisher.sendMore(topic)
          publisher.send(json)
        }
      }
      catch {
        case NonFatal(e) =>
          log.warn("An exception was thrown in ZeroMQPublishers publish thread", e)
      }
    }
  }, "ZeroMQPublisher")
  thread.setDaemon(true)
  thread.start()


  def close(): Unit = {
    context.destroy()
    disposable.dispose()
    ok = false
  }


}
