package org.veupathdb.service.demo.async;

import org.jetbrains.annotations.NotNull;
import org.veupathdb.lib.compute.platform.job.JobContext;
import org.veupathdb.lib.compute.platform.job.JobExecutor;
import org.veupathdb.lib.compute.platform.job.JobResult;
import org.veupathdb.lib.jackson.Json;

import java.util.Scanner;
import java.util.regex.Pattern;

public class WordCountJob implements JobExecutor {
  @NotNull
  @Override
  public JobResult execute(@NotNull JobContext jobContext) {

    // retrieve trimmed file data from context
    var fileContents = Json.parse(jobContext.getConfig().get("fileContents"), String.class).trim();

    // zero words if trimmed data is empty
    long numWords = 0;
    if (!fileContents.isEmpty()) {
      // should have one more word than the number of whitespace strings in the data
      numWords = new Scanner(fileContents).findAll(Pattern.compile("\s+")).count() + 1;
    }

    // write this job's result file and announce success
    jobContext.getWorkspace().write("output", numWords + " words found");
    return JobResult.success("output");
  }
}
