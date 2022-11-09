package org.veupathdb.service.demo.health;

import org.veupathdb.lib.container.jaxrs.health.ExternalDependency;

public class RabbitMQDependency extends ExternalDependency {

  private final String host;

  private final int port;

  public RabbitMQDependency(
    String name,
    String host,
    int    port
  ) {
    super(name);

    this.host = host;
    this.port = port;
  }

  @Override
  public TestResult test() {
    if (pinger.isReachable(host, port)) {
      return new TestResult(this, true, Status.ONLINE);
    } else {
      return new TestResult(this, false, Status.UNKNOWN);
    }
  }

  @Override
  public void close() {}
}
