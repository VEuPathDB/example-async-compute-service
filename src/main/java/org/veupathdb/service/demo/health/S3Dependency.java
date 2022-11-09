package org.veupathdb.service.demo.health;

import org.veupathdb.lib.container.jaxrs.health.ExternalDependency;

public class S3Dependency extends ExternalDependency {

  private final String host;
  private final int port;

  public S3Dependency(String name, String host, int port) {
    super(name);

    this.host = host;
    this.port = port;
  }

  @Override
  public TestResult test() {
    return pinger.isReachable(host, port)
      ? new TestResult(this, true, Status.ONLINE)
      : new TestResult(this, false, Status.UNKNOWN)
      ;
  }

  @Override
  public void close() {}
}
