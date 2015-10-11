package com.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Handler;

/**
 * This is a verticle. A verticle is a _Vert.x component_. This verticle is implemented in Java, but you can
 * implement them in JavaScript, Groovy or even Ruby.
 */
public class Server extends AbstractVerticle {

        EventBus eb;

  /**
   * This method is called when the verticle is deployed. It creates a HTTP server and registers a simple request
   * handler.
   * <p/>
   * Notice the `listen` method. It passes a lambda checking the port binding result. When the HTTP server has been
   * bound on the port, it call the `complete` method to inform that the starting has completed. Else it reports the
   * error.
   *
   * @param fut the future
   */
  @Override
  public void start(Future<Void> fut) {

      eb = vertx.eventBus();

      Router router = Router.router(vertx);

      // Bind "/" to our hello message.
      router.route("/").handler(routingContext -> {
        HttpServerResponse response = routingContext.response();
        
        response
            .putHeader("content-type", "text/html")
            .end("<h1>Hello from my first Vert.x 3 application</h1>");
      });

      
      router.route("/ping").handler(this::ping);


    vertx
        .createHttpServer()
        .requestHandler(router::accept)
        .listen(8080, result -> {
          if (result.succeeded()) {
            fut.complete();
          } else {
            fut.fail(result.cause());
          }
        });
  }

    private void ping(RoutingContext routingContext) {
      String msg = routingContext.request().getParam("msg");
      eb.send("ping",msg,reply -> {
        if (reply.succeeded()) {
          String out = reply.result().body().toString();
          System.out.println("Received reply in server" + out);
          routingContext
          .response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(out);

        } else {
          System.out.println("No reply in server");

          routingContext
          .response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end("Failed");
        }

    });
   

  }

}
