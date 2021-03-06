package akka

import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import instruments.{TestCases, Timer}

import scala.concurrent.{ExecutionContext, Future}

class AkkaStreamAPI(implicit mat: Materializer, ec: ExecutionContext) extends TestCases[Future]
  with Timer[Future] {

  override def rangeToListOfStrings(
    range: Range
  ): Future[List[String]] = {
    val source: Source[Int, NotUsed] = Source(range)
    val flow = Flow[Int].map(_.toString)
    val sink = Sink.collection[String, List[String]]
    source.via(flow).runWith(sink)
  }

  override def apiName: String = "akka-streams"

  override def timer[R](
    task: => Future[R], retries: Int
  ): Future[Unit] = Future.sequence(List.fill(retries)(timer(task))).map(printTime)

  override def timer[R](task: => Future[R]): Future[Long] = {
    val startTime = getMillis
    task.map(_ => getMillis - startTime)
  }
}
