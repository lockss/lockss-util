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

import java.util.Map;
import java.util.Objects;
import com.fasterxml.jackson.annotation.*;

/**
 * Container for the information related to the configuration of an Archival
 * Unit that is the result of a query.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuConfigurationWsResult {

  private Map<String, String> defParams;
  private Map<String, String> nonDefParams;

  /**
   * Provides the definitional parameters of the Archival Unit configuration.
   * 
   * @return a {@code Map<String, String>} with the definitional parameters.
   */
  public Map<String, String> getDefParams() {
    return defParams;
  }
  public void setDefParams(Map<String, String> defParams) {
    this.defParams = defParams;
  }

  /**
   * Provides the non-definitional parameters of the Archival Unit
   * configuration.
   * 
   * @return a {@code Map<String, String>} with the non-definitional parameters.
   */
  public Map<String, String> getNonDefParams() {
    return nonDefParams;
  }
  public void setNonDefParams(Map<String, String> nonDefParams) {
    this.nonDefParams = nonDefParams;
  }

  @Override
  public String toString() {
    return "AuConfigurationWsResult [defParams=" + defParams + ", nonDefParams="
	+ nonDefParams + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuConfigurationWsResult that = (AuConfigurationWsResult) o;
    return Objects.equals(defParams, that.defParams) && Objects.equals(nonDefParams, that.nonDefParams);
  }

  @Override
  public int hashCode() {
    return Objects.hash(defParams, nonDefParams);
  }
}
