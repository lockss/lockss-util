/*

Copyright (c) 2000-2023, Board of Trustees of Leland Stanford Jr. University

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

*/

package org.lockss.ws.entities;

/**
 * A wrapper for the parameters used to request a COUNTER report.
 */
public class CounterReportParams {
  private String id;
  private Integer startMonth;
  private Integer startYear;
  private Integer endMonth;
  private Integer endYear;
  private String type;
  private String format;

  /**
   * Provides the identifier of the requested report.
   * 
   * @return a String with the identifier.
   */
  public String getId() {
    return id;
  }

  /**
   * Provides the month at the start of the requested report period.
   * 
   * @return an Integer with the month (January = 1, December = 12).
   */
  public Integer getStartMonth() {
    return startMonth;
  }

  /**
   * Provides the year at the start of the requested report period.
   * 
   * @return an Integer with the year.
   */
  public Integer getStartYear() {
    return startYear;
  }

  /**
   * Provides the month at the end of the requested report period.
   * 
   * @return an Integer with the month (January = 1, December = 12).
   */
  public Integer getEndMonth() {
    return endMonth;
  }

  /**
   * Provides the year at the end of the requested report period.
   * 
   * @return an Integer with the year.
   */
  public Integer getEndYear() {
    return endYear;
  }

  /**
   * Provides the type of the requested report.
   * 
   * @return a String with the type (book or journal).
   */
  public String getType() {
    return type;
  }

  /**
   * Provides the format of the requested report.
   * 
   * @return a String with the format (CSV or TSV).
   */
  public String getFormat() {
    return format;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setStartMonth(Integer startMonth) {
    this.startMonth = startMonth;
  }

  public void setStartYear(Integer startYear) {
    this.startYear = startYear;
  }

  public void setEndMonth(Integer endMonth) {
    this.endMonth = endMonth;
  }

  public void setEndYear(Integer endYear) {
    this.endYear = endYear;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("CounterReportParams [id=");
    builder.append(id);
    builder.append(", startMonth=");
    builder.append(startMonth);
    builder.append(", startYear=");
    builder.append(startYear);
    builder.append(", endMonth=");
    builder.append(endMonth);
    builder.append(", endYear=");
    builder.append(endYear);
    builder.append(", type=");
    builder.append(type);
    builder.append(", format=");
    builder.append(format);
    builder.append("]");
    return builder.toString();
  }
}
