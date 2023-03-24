package org.veupathdb.service.demo.service;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.veupathdb.lib.compute.platform.AsyncPlatform;
import org.veupathdb.lib.compute.platform.job.JobSubmission;
import org.veupathdb.lib.hash_id.HashID;
import org.veupathdb.lib.jackson.Json;
import org.veupathdb.service.demo.async.MyExecutorFactory;
import org.veupathdb.service.demo.generated.model.ReverseRequest;
import org.veupathdb.service.demo.generated.resources.Reverse;

public class ReverseController extends Controller implements Reverse {

  @Override
  public PostReverseResponse postReverse(ReverseRequest entity) {

    ObjectNode json = JsonNodeFactory.instance.objectNode();
    json.put(MyExecutorFactory.JOB_TYPE_KEY, MyExecutorFactory.JobType.STRING_REVERSE.name());
    json.set("config", Json.convert(entity));

    var jobID = HashID.ofMD5(json.toString());

    // Check if we have a job already with this hash ID
    var oldJob = AsyncPlatform.getJob(jobID);

    // If we do (if the AsyncPlatform call returned non-null)
    if (oldJob != null)
      // Return that job status.
      return PostReverseResponse.respond200WithApplicationJson(convert(oldJob));

    AsyncPlatform.submitJob("string-reverse-queue", JobSubmission.builder()
      .jobID(jobID)
      .config(json)
      .build());

    return PostReverseResponse.respond200WithApplicationJson(convert(AsyncPlatform.getJob(jobID)));
  }

}
