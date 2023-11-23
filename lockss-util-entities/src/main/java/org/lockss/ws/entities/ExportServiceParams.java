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
 * A wrapper for the parameters used to request a ExportService to download the
 * content.
 * 
 * @author Ahmed AlSum
 */
public class ExportServiceParams {
  private String auid;
  private TypeEnum fileType;
  private boolean isCompress = true;
  private boolean isExcludeDirNodes = true;
  private FilenameTranslationEnum xlateFilenames;
  private String filePrefix;
  private long maxSize = -1; // The default is there is no maximum file size
  private int maxVersions = -1;

  public ExportServiceParams() {
    super();
    fileType = TypeEnum.WARC_RESPONSE;
    xlateFilenames = FilenameTranslationEnum.XLATE_NONE;
    filePrefix = "lockss_export";
  }

  /**
   * @return the auid
   */
  public String getAuid() {
    return auid;
  }

  /**
   * @param auid
   *          to set the AU Id
   */
  public void setAuid(String auid) {
    this.auid = auid;
  }

  /**
   * @return the fileType
   */
  public TypeEnum getFileType() {
    return fileType;
  }

  /**
   * @param fileType
   *          the fileType to set
   */
  public void setFileType(TypeEnum fileType) {
    this.fileType = fileType;
  }

  /**
   * @return the isCompress
   */
  public boolean isCompress() {
    return isCompress;
  }

  /**
   * @param isCompress
   *          the isCompress to set
   */
  public void setCompress(boolean isCompress) {
    this.isCompress = isCompress;
  }

  /**
   * @return the isExcludeDirNodes
   */
  public boolean isExcludeDirNodes() {
    return isExcludeDirNodes;
  }

  /**
   * @param isExcludeDirNodes
   *          the isExcludeDirNodes to set
   */
  public void setExcludeDirNodes(boolean isExcludeDirNodes) {
    this.isExcludeDirNodes = isExcludeDirNodes;
  }

  /**
   * @return the xlateFilenames
   */
  public FilenameTranslationEnum getXlateFilenames() {
    return xlateFilenames;
  }

  /**
   * @param xlateFilenames
   *          the xlateFilenames to set
   */
  public void setXlateFilenames(FilenameTranslationEnum xlateFilenames) {
    this.xlateFilenames = xlateFilenames;
  }

  /**
   * @return the filePrefix
   */
  public String getFilePrefix() {
    return filePrefix;
  }

  /**
   * @param filePrefix
   *          the filePrefix to set
   */
  public void setFilePrefix(String filePrefix) {
    this.filePrefix = filePrefix;
  }

  /**
   * @return the maxSize
   */
  public long getMaxSize() {
    return maxSize;
  }

  /**
   * @param maxSize
   *          the maxSize to set
   */
  public void setMaxSize(long maxSize) {
    this.maxSize = maxSize;
  }

  /**
   * Provides The maximum number of versions included, for content files that
   * have older versions. (ARC and WARC only).
   * 
   * @return the maxVersions
   */
  public int getMaxVersions() {
    return maxVersions;
  }

  /**
   * @param maxVersions
   *          the maxVersions to set
   */
  public void setMaxVersions(int maxVersions) {
    this.maxVersions = maxVersions;
  }

  public enum TypeEnum {
    WARC_RESPONSE, WARC_RESOURCE, ARC_RESPONSE, ARC_RESOURCE, ZIP
  }

  public enum FilenameTranslationEnum {
    XLATE_NONE, XLATE_WINDOWS, XLATE_MAC
  }
}
