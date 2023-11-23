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

import java.util.Arrays;
import javax.activation.DataHandler;
import com.fasterxml.jackson.annotation.*;

/**
 * A wrapper for the result of a hash operation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HasherWsResult {
  public static final String BLOCK_FILE_TYPE = "Block";
  public static final String RECORD_FILE_TYPE = "Record";

  private Long startTime;
  private String recordFileName;
  private DataHandler recordFileDataHandler;
  private String blockFileName;
  private DataHandler blockFileDataHandler;
  private byte[] hashResult;
  private String errorMessage;
  private String status;
  private Long bytesHashed;
  private Integer filesHashed;
  private Long elapsedTime;

  /**
   * Provides the instant when the hashing operation started.
   * 
   * @return a Long with the instant in milliseconds since the start of 1970.
   */
  public Long getStartTime() {
    return startTime;
  }
  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  /**
   * Provides the name of the record file.
   * 
   * @return a String with the name of the record file.
   */
  public String getRecordFileName() {
    return recordFileName;
  }
  public void setRecordFileName(String recordFileName) {
    this.recordFileName = recordFileName;
  }

  /**
   * Provides the content of the record file.
   * 
   * @return a DataHandler through which to obtain the content of the record
   *         file.
   */
  public DataHandler getRecordFileDataHandler() {
    return recordFileDataHandler;
  }
  public void setRecordFileDataHandler(DataHandler recordFileDataHandler) {
    this.recordFileDataHandler = recordFileDataHandler;
  }

  /**
   * Provides the name of the block file.
   * 
   * @return a String with the name of the block file.
   */
  public String getBlockFileName() {
    return blockFileName;
  }
  public void setBlockFileName(String blockFileName) {
    this.blockFileName = blockFileName;
  }

  /**
   * Provides the content of the block file.
   * 
   * @return a DataHandler through which to obtain the content of the block
   *         file.
   */
  public DataHandler getBlockFileDataHandler() {
    return blockFileDataHandler;
  }
  public void setBlockFileDataHandler(DataHandler blockFileDataHandler) {
    this.blockFileDataHandler = blockFileDataHandler;
  }

  /**
   * Provides the result of the hash.
   * 
   * @return a byte[] with the result of the hash.
   */
  public byte[] getHashResult() {
    return hashResult;
  }
  public void setHashResult(byte[] hashResult) {
    this.hashResult = hashResult;
  }

  /**
   * Provides the error message.
   * 
   * @return a String with the error message, if any.
   */
  public String getErrorMessage() {
    return errorMessage;
  }
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * Provides the status. <br>
   * The possible values are Init, Starting, Running, Done, Error and
   * RequestError.
   * 
   * @return a String with the status.
   */
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Provides the count of the bytes hashed.
   * 
   * @return a Long with the count of the bytes hashed..
   */
  public Long getBytesHashed() {
    return bytesHashed;
  }
  public void setBytesHashed(Long bytesHashed) {
    this.bytesHashed = bytesHashed;
  }

  /**
   * Provides the count of the files hashed.
   * 
   * @return an Integer with the count of the files hashed..
   */
  public Integer getFilesHashed() {
    return filesHashed;
  }
  public void setFilesHashed(Integer filesHashed) {
    this.filesHashed = filesHashed;
  }

  /**
   * Provides the length of time that the hashing operation took to complete.
   * 
   * @return a Long with the length of time in milliseconds.
   */
  public Long getElapsedTime() {
    return elapsedTime;
  }
  public void setElapsedTime(Long elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  @Override
  public String toString() {
    return "[HasherWsResult: startTime=" + startTime + ", recordFileName="
	+ recordFileName + ", blockFileName=" + blockFileName
	+ ", hashResult=" + Arrays.toString(hashResult) + ", errorMessage="
	+ errorMessage + ", status=" + status + ", bytesHashed=" + bytesHashed
	+ ", filesHashed=" + filesHashed + ", elapsedTime=" + elapsedTime + "]";
  }

}
