package org.veupathdb.service.demo.health;

import org.veupathdb.lib.container.jaxrs.health.ExternalDependency;

public class PostgresDependency extends ExternalDependency {

  private final String host;
  private final int port;

  public PostgresDependency(String name, String host, int port) {
    super(name);

    this.host = host;
    this.port = port;
  }

  @Override
  public TestResult test() {
    return pinger.isReachable(host, port)
      ? new TestResult(this, true, Status.ONLINE)
      : new TestResult(this, true, Status.UNKNOWN)
      ;
  }

  @Override
  public void close() { }
}
