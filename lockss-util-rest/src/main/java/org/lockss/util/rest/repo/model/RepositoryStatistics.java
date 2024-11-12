package org.lockss.util.rest.repo.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/**
 * Various statistics about the repository&#x27;s internal operation.
 */
@Schema(description = "Various statistics about the repository's internal operation.")
@Validated



public class RepositoryStatistics   {
  @JsonProperty("timeSpentReiteratingIterators")
  private Long timeSpentReiteratingIterators = null;

  public RepositoryStatistics timeSpentReiteratingIterators(Long timeSpentReiteratingIterators) {
    this.timeSpentReiteratingIterators = timeSpentReiteratingIterators;
    return this;
  }

  /**
   * Time spent reiterating a new artifact iterator during continuation.
   * @return timeSpentReiteratingIterators
   **/
  @Schema(description = "Time spent reiterating a new artifact iterator during continuation.")
  
    public Long getTimeSpentReiteratingIterators() {
    return timeSpentReiteratingIterators;
  }

  public void setTimeSpentReiteratingIterators(Long timeSpentReiteratingIterators) {
    this.timeSpentReiteratingIterators = timeSpentReiteratingIterators;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RepositoryStatistics repositoryStatistics = (RepositoryStatistics) o;
    return Objects.equals(this.timeSpentReiteratingIterators, repositoryStatistics.timeSpentReiteratingIterators);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timeSpentReiteratingIterators);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RepositoryStatistics {\n");
    
    sb.append("    timeSpentReiteratingIterators: ").append(toIndentedString(timeSpentReiteratingIterators)).append("\n");
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
