/*
 * 
 * 
 *
 * 
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

/**
 * TimestampType
 */
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2024-02-19T21:28:12.651360Z[Europe/Dublin]")
public class TimestampType   {
  @JsonProperty("time")
  private Long time = null;

  @JsonProperty("usec")
  private Integer usec = null;

  public TimestampType time(Long time) {
    this.time = time;
    return this;
  }

  /**
   * Get time
   * @return time
   **/
  @JsonProperty("time")
  @Schema(description = "")
  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public TimestampType usec(Integer usec) {
    this.usec = usec;
    return this;
  }

  /**
   * Get usec
   * @return usec
   **/
  @JsonProperty("usec")
  @Schema(description = "")
  public Integer getUsec() {
    return usec;
  }

  public void setUsec(Integer usec) {
    this.usec = usec;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TimestampType timestampType = (TimestampType) o;
    return Objects.equals(this.time, timestampType.time) &&
        Objects.equals(this.usec, timestampType.usec);
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, usec);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TimestampType {\n");
    
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    usec: ").append(toIndentedString(usec)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
