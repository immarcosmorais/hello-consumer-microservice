package consumer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
//import io.vertx.kafka.client.consumer.KafkaConsumer;


public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    WebClient client = WebClient.create(vertx);
    client.get(8080, "localhost", "/")
      .send(ar -> {
        if (ar.succeeded()) {
          HttpResponse<Buffer> response = ar.result();
          System.out.println("Received response with status code " + response.statusCode());

          vertx.createHttpServer().requestHandler(req -> {
            req.response()
              .putHeader("content-type", "text/html")
              .end(response.bodyAsString());
          }).listen(8081, http -> {
            if (http.succeeded()) {
              startPromise.complete();
              System.out.println("HTTP server started on port 8081");
            } else {
              startPromise.fail(http.cause());
            }
          });

        } else {
          System.out.println("Something went wrong " + ar.cause().getMessage());
        }
      });
  }
}
