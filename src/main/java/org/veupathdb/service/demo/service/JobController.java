package org.veupathdb.service.demo.service;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.StreamingOutput;
import org.veupathdb.lib.compute.platform.AsyncPlatform;
import org.veupathdb.lib.hash_id.HashID;
import org.veupathdb.service.demo.generated.model.JobBulkStatusResponseImpl;
import org.veupathdb.service.demo.generated.resources.Jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JobController extends Controller implements Jobs {

  @Override
  public GetJobsByJobIdResponse getJobsByJobId(String rawJobID) {
    return GetJobsByJobIdResponse.respond200WithApplicationJson(convert(getJobOrNotFound(rawJobID)));
  }

  @Override
  public GetJobsFilesByJobIdResponse getJobsFilesByJobId(String jobId) {
    var job = getJobOrNotFound(jobId);

    // If the job isn't finished, we don't allow listing its files
    if (!job.getStatus().isFinished())
      throw new ForbiddenException();

    var results = AsyncPlatform.getJobFiles(job.getJobID());
    var out     = new ArrayList<String>(results.size());

    for (var res : results)
      out.add(res.getName());

    return GetJobsFilesByJobIdResponse.respond200WithApplicationJson(out);
  }

  @Override
  public PostJobsStatusesResponse postJobsStatuses(List<String> entity) {
    // Create a response object
    var out = new JobBulkStatusResponseImpl();

    // Iterate through the input IDs
    entity.stream()
      // Attempt to parse the input strings to HashID instances
      .map(this::hashIDOrNull)
      // Remove the invalids
      .filter(Objects::nonNull)
      // Lookup the job for each of the valid HashID instances
      .map(AsyncPlatform::getJob)
      // Remove not-found jobs
      .filter(Objects::nonNull)
      // Append the statuses to the outgoing response object.
      .forEach(it -> out.setAdditionalProperties(it.getJobID().getString(), it.getStatus().toString()));

    return PostJobsStatusesResponse.respond200WithApplicationJson(out);
  }

  private HashID hashIDOrNull(String rawID) {
    try {
      return new HashID(rawID);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public GetJobsFilesByJobIdAndFileNameResponse getJobsFilesByJobIdAndFileName(String jobId, String fileName) {
    var job = getJobOrNotFound(jobId);

    // If the job isn't finished, we don't allow listing its files
    if (!job.getStatus().isFinished())
      throw new ForbiddenException();

    var results = AsyncPlatform.getJobFiles(job.getJobID());

    for (var res : results) {
      if (res.getName().equals(fileName)) {
        return GetJobsFilesByJobIdAndFileNameResponse.respond200WithTextPlain((StreamingOutput) output -> {
          try (var input = res.open()) {
            input.transferTo(output);
          }
        }, GetJobsFilesByJobIdAndFileNameResponse.headersFor200().withContentDisposition("attachment; filename=output.txt"));
      }
    }

    throw new NotFoundException();
  }
}
