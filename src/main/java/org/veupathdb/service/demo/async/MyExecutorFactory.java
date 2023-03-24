package org.veupathdb.service.demo.async;

import org.jetbrains.annotations.NotNull;
import org.veupathdb.lib.compute.platform.job.JobExecutor;
import org.veupathdb.lib.compute.platform.job.JobExecutorContext;
import org.veupathdb.lib.compute.platform.job.JobExecutorFactory;

public class MyExecutorFactory implements JobExecutorFactory {

  public static final String JOB_TYPE_KEY = "jobType";
  public enum JobType { STRING_REVERSE, WORD_COUNT }

  @NotNull
  @Override
  public JobExecutor newJobExecutor(@NotNull JobExecutorContext jobExecutorContext) {
    return switch (JobType.valueOf(jobExecutorContext.getJobConfig().get(JOB_TYPE_KEY).toString())) {
      case STRING_REVERSE -> new StringReverseJob();
      case WORD_COUNT -> new WordCountJob();
    };
  }
}
