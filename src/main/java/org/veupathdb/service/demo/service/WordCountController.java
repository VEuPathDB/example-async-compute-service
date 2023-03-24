package org.veupathdb.service.demo.service;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.ServerErrorException;
import org.veupathdb.lib.compute.platform.AsyncPlatform;
import org.veupathdb.lib.compute.platform.job.JobSubmission;
import org.veupathdb.lib.hash_id.HashID;
import org.veupathdb.service.demo.async.MyExecutorFactory;
import org.veupathdb.service.demo.generated.model.WordCountPostMultipartFormData;
import org.veupathdb.service.demo.generated.resources.WordCount;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class WordCountController extends Controller implements WordCount {

  @Override
  public PostWordCountResponse postWordCount(WordCountPostMultipartFormData entity) {

    ObjectNode json = JsonNodeFactory.instance.objectNode();
    json.put(MyExecutorFactory.JOB_TYPE_KEY, MyExecutorFactory.JobType.WORD_COUNT.name());

    try {
      // note: your implementation should protect memory more carefully!
      ByteArrayOutputStream fileContents = new ByteArrayOutputStream();
      switch (entity.getUploadMethod()) {
        case URL:
          new URL(entity.getUrl()).openStream().transferTo(fileContents);
          break;
        case FILE:
          try (FileInputStream in = new FileInputStream(entity.getFile())) {
            in.transferTo(fileContents);
          }
          break;
      }
      json.put("fileContents",fileContents.toString());
    }
    catch (IOException e) {
      throw new ServerErrorException(500);
    }

    var jobID = HashID.ofMD5(json.toString());

    // Check if we have a job already with this hash ID
    var oldJob = AsyncPlatform.getJob(jobID);

    // If we do (if the AsyncPlatform call returned non-null)
    if (oldJob != null)
      // Return that job status.
      return PostWordCountResponse.respond200WithApplicationJson(convert(oldJob));

    AsyncPlatform.submitJob("file-wc-queue", JobSubmission.builder()
        .jobID(jobID)
        .config(json)
        .build());

    return PostWordCountResponse.respond200WithApplicationJson(convert(AsyncPlatform.getJob(jobID)));
  }
}
