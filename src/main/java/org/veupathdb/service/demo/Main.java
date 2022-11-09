package org.veupathdb.service.demo;

import org.veupathdb.lib.compute.platform.AsyncPlatform;
import org.veupathdb.lib.compute.platform.config.*;
import org.veupathdb.lib.container.jaxrs.config.Options;
import org.veupathdb.lib.container.jaxrs.providers.DependencyProvider;
import org.veupathdb.lib.container.jaxrs.server.ContainerResources;
import org.veupathdb.lib.container.jaxrs.server.Server;
import org.veupathdb.service.demo.async.MyExecutorFactory;
import org.veupathdb.service.demo.health.PostgresDependency;
import org.veupathdb.service.demo.health.RabbitMQDependency;
import org.veupathdb.service.demo.health.S3Dependency;

public class Main extends Server {
  private final MyOptions options = new MyOptions();

  public static void main(String[] args) {
    var server = new Main();


    server.start(args);
  }

  @Override
  protected ContainerResources newResourceConfig(Options options) {
    final var out =  new Resources(options);

    // Enabled by default for debugging purposes, this should be removed when
    // production ready.
    out.property("jersey.config.server.tracing.type", "ALL")
      .property("jersey.config.server.tracing.threshold", "VERBOSE");

    return out;
  }

  @Override
  protected Options newOptions() {
    return options;
  }

  @Override
  protected void postCliParse(Options opts) {
    initializeAsyncPlatform();
    registerPlatformDependencies();
  }

  private void initializeAsyncPlatform() {
    AsyncPlatform.init(AsyncPlatformConfig.builder()
      .dbConfig(AsyncDBConfig.builder()
        .dbName(options.getQueueDBName())
        .host(options.getQueueDBHost())
        .port(options.getQueueDBPort())
        .username(options.getQueueDBUsername())
        .password(options.getQueueDBPassword())
        .poolSize(options.getQueueDBPoolSize())
        .build())
      .addQueue(AsyncQueueConfig.builder()
        .id("string-reverse-queue")
        .username(options.getJobQueueUsername())
        .password(options.getJobQueuePassword())
        .host(options.getJobQueueHost())
        .port(options.getJobQueuePort())
        .workers(options.getJobQueueWorkers())
        .build())
      .addQueue(AsyncQueueConfig.builder()
        .id("file-wc-queue")
        .username(options.getJobQueueUsername())
        .password(options.getJobQueuePassword())
        .host(options.getJobQueueHost())
        .port(options.getJobQueuePort())
        .workers(options.getJobQueueWorkers())
        .build())
      .s3Config(AsyncS3Config.builder()
        .host(options.getS3Host())
        .bucket(options.getS3Bucket())
        .accessToken(options.getS3AccessToken())
        .secretKey(options.getS3SecretKey())
        .port(options.getS3Port())
        .https(options.getS3UseHttps())
        .build())
      .jobConfig(AsyncJobConfig.builder()
        .executorFactory(new MyExecutorFactory())
        .expirationDays(options.getJobCacheTimeoutDays())
        .build())
      .build());
  }

  private void registerPlatformDependencies() {
    var depProv = DependencyProvider.getInstance();

    depProv.register(new RabbitMQDependency("job-queue", options.getJobQueueHost(), options.getJobQueuePort()));
    depProv.register(new S3Dependency("s3", options.getS3Host(), options.getS3Port()));
    depProv.register(new PostgresDependency("queue-db", options.getQueueDBHost(), options.getQueueDBPort()));
  }
}
