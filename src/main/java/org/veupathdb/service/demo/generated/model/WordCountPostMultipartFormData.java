package org.veupathdb.service.demo.generated.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.File;
import java.util.Map;

@JsonDeserialize(
    as = WordCountPostMultipartFormDataImpl.class
)
public interface WordCountPostMultipartFormData {
  @JsonProperty("uploadMethod")
  UploadMethodType getUploadMethod();

  @JsonProperty("uploadMethod")
  void setUploadMethod(UploadMethodType uploadMethod);

  @JsonProperty("url")
  String getUrl();

  @JsonProperty("url")
  void setUrl(String url);

  @JsonProperty("file")
  File getFile();

  @JsonProperty("file")
  void setFile(File file);

  @JsonAnyGetter
  Map<String, Object> getAdditionalProperties();

  @JsonAnySetter
  void setAdditionalProperties(String key, Object value);

  enum UploadMethodType {
    @JsonProperty("url")
    URL("url"),

    @JsonProperty("file")
    FILE("file");

    private final String value;

    public String getValue() {
      return this.value;
    }

    UploadMethodType(String name) {
      this.value = name;
    }
  }
}