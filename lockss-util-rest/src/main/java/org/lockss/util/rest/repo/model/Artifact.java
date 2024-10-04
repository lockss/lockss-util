/*
 * Copyright (c) 2019, Board of Trustees of Leland Stanford Jr. University,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.lockss.util.rest.repo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.lockss.log.L4JLogger;
import org.lockss.util.StringPool;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LOCKSS repository Artifact
 *
 * Represents an atomic unit of data in a LOCKSS repository.
 */
public class Artifact implements Serializable {
    private static final long serialVersionUID = 1961138745993115018L;
    private final static L4JLogger log = L4JLogger.getLogger();

  // These need to match those in the Artifact model defined in the Swagger/OpenAPI spec
  public final static String ARTIFACT_NAMESPACE_KEY = "namespace";
  public final static String ARTIFACT_UUID_KEY = "uuid";
  public final static String ARTIFACT_AUID_KEY = "auid";
  public final static String ARTIFACT_URI_KEY = "uri";
  public final static String ARTIFACT_VERSION_KEY = "version";
  public final static String ARTIFACT_COMMITTED_STATUS_KEY = "committed";
  public final static String ARTIFACT_LENGTH_KEY = "contentLength";
  public final static String ARTIFACT_DIGEST_KEY = "contentDigest";
  public final static String ARTIFACT_COLLECTION_DATE_KEY = "collectionDate";
  public final static String ARTIFACT_STORE_DATE_KEY = "storeDate";

    // We have chosen to map the artifact UUID to the Solr document's "id" field
    // for the sake of convention, even though Solr appears to support assigning
    // another field as the unique identifier.

  @JsonProperty("uuid")
    private String uuid;
  @JsonProperty("namespace")
    private String namespace = "lockss";
  @JsonProperty("auid")
    private String auid;
  @JsonProperty("uri")
    private String uri;
  @JsonProperty("sortUri")
    private String sortUri;
  @JsonProperty("version")
    private Integer version;
  @JsonProperty("committed")
  private Boolean committed = null;
  @JsonProperty("storageUrl")
  private String storageUrl = null;
  @JsonProperty("contentLength")
    private long contentLength;
  @JsonProperty("contentDigest")
    private String contentDigest;
  @JsonProperty("collectionDate")
    private long collectionDate;

    /**
     * Constructor. Needed by SolrJ for getBeans() support. *
     *
     * TODO: Reconcile difference with constructor below, which checks parameters for illegal arguments.
     */
    public Artifact() {
        // Intentionally left blank
    }

    public Artifact(ArtifactIdentifier aid, Boolean committed, String storageUrl, long contentLength, String contentDigest) {
        this(
                aid.getUuid(), aid.getNamespace(), aid.getAuid(), aid.getUri(), aid.getVersion(),
                committed,
                storageUrl,
                contentLength,
                contentDigest
        );
    }

    public Artifact(String uuid, String namespace, String auid, String uri, Integer version, Boolean committed,
                    String storageUrl, long contentLength, String contentDigest) {
        if (StringUtils.isEmpty(uuid)) {
          throw new IllegalArgumentException(
              "Cannot create Artifact with null or empty UUID");
        }
        this.uuid = uuid;

        if (StringUtils.isEmpty(namespace)) {
          throw new IllegalArgumentException(
              "Cannot create Artifact with null or empty namespace");
        }
        setNamespace(namespace);

        if (StringUtils.isEmpty(auid)) {
          throw new IllegalArgumentException(
              "Cannot create Artifact with null or empty auid");
        }
        setAuid(auid);

    if (StringUtils.isEmpty(uri)) {
      throw new IllegalArgumentException(
        "Cannot create Artifact with null or empty URI");
    }
    this.setUri(uri);

    if (version == null) {
      throw new IllegalArgumentException(
        "Cannot create Artifact with null version");
    }
    setVersion(version);

    if (committed == null) {
      throw new IllegalArgumentException(
        "Cannot create Artifact with null commit status");
    }
    this.committed = committed;

    this.storageUrl = storageUrl;

    this.contentLength = contentLength;
    this.contentDigest = contentDigest;
  }

    @JsonIgnore
    public ArtifactIdentifier getIdentifier() {
        return new ArtifactIdentifier(uuid, namespace, auid, uri, version);
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        if (StringUtils.isEmpty(namespace)) {
          throw new IllegalArgumentException(
              "Cannot set null or empty namespace");
        }
        this.namespace = namespace == null ? null
          : StringPool.MISCELLANEOUS.intern(namespace);
    }
  public Artifact namespace(String namespace) {
    setNamespace(namespace);
    return this;
  }

    public String getAuid() {
        return auid;
    }

    public void setAuid(String auid) {
        if (StringUtils.isEmpty(auid)) {
          throw new IllegalArgumentException("Cannot set null or empty auid");
        }
        this.auid = auid == null ? null : StringPool.AUIDS.intern(auid);
    }
  public Artifact auid(String auid) {
    setAuid(auid);
    return this;
  }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        if (StringUtils.isEmpty(uri)) {
          throw new IllegalArgumentException("Cannot set null or empty URI");
        }
      this.uri = uri;
  }
  public Artifact uri(String uri) {
    setUri(uri);
    return this;
  }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
//        if (StringUtils.isEmpty(version)) {
//          throw new IllegalArgumentException(
//              "Cannot set null or empty version");
//        }
        if (version == null) {
            throw new IllegalArgumentException("Cannot set null version");
        }

        this.version = Integer.valueOf(version);
    }
  public Artifact version(Integer version) {
    setVersion(version);
    return this;
  }


    public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Artifact uuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public String getUuid() {
        return uuid;
    }

    public Boolean getCommitted() {
        return committed;
  }

  public boolean isCommitted() {
    return getCommitted() == true;
  }

  public void setCommitted(Boolean committed) {
    if (committed == null) {
      throw new IllegalArgumentException("Cannot set null commit status");
    }
    this.committed = committed;
  }

  public Artifact committed(Boolean committed) {
    setCommitted(committed);
    return this;
  }


  public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        if (StringUtils.isEmpty(storageUrl)) {
          throw new IllegalArgumentException(
              "Cannot set null or empty storageUrl");
        }
    this.storageUrl = storageUrl;
    }
  public Artifact storageUrl(String storageUrl) {
    setStorageUrl(storageUrl);
    return this;
  }

    public long getContentLength() {
        return contentLength;
  }

    public void setContentLength(long contentLength) {
    this.contentLength = contentLength;
    }

  public Artifact contentLength(long contentLength) {
    this.contentLength = contentLength;
    return this;
  }

    public String getContentDigest() {
        return contentDigest;
  }

    public void setContentDigest(String contentDigest) {
    this.contentDigest = contentDigest;
    }
  public Artifact contentDigest(String contentDigest) {
    this.contentDigest = contentDigest;
    return this;
  }

  /**
   * Provides the artifact collection date.
   *
   * @return a long with the artifact collection date in milliseconds since the
   *         epoch.
   */
  public long getCollectionDate() {
    return collectionDate;
  }

  /**
   * Saves the artifact collection date.
   *
   * @param collectionDate
   *          A long with the artifact collection date in milliseconds since the
   *          epoch.
   */
  public void setCollectionDate(long collectionDate) {
    this.collectionDate = collectionDate;
  }

  public Artifact collectionDate(long collectionDate) {
    this.collectionDate = collectionDate;
    return this;
  }



  @Override
  public String toString() {
    return "Artifact{" +
      "uuid='" + uuid + '\'' +
      ", namespace='" + namespace + '\'' +
      ", auid='" + auid + '\'' +
      ", uri='" + uri + '\'' +
//                 ", sortUri='" + sortUri + '\'' +
      ", version='" + version + '\'' +
      ", committed=" + committed +
      ", storageUrl='" + storageUrl + '\'' +
      ", contentLength='" + contentLength + '\'' +
      ", contentDigest='" + contentDigest + '\'' +
      ", collectionDate='" + collectionDate + '\'' +
      '}';
  }

    @Override
    public boolean equals(Object o) {
      // Cast to Artifact is safe because equalsExceptStorageUrl has
      // already checked instanceof
      return equalsExceptStorageUrl(o)
        && storageUrl.equalsIgnoreCase(((Artifact)o).getStorageUrl());
    }

  public boolean equalsExceptStorageUrl(Object o) {
    if (!(o instanceof Artifact)) {
      return false;
    }
    Artifact other = (Artifact)o;

    return other != null
      && ((this.getIdentifier() == null && other.getIdentifier() == null)
      || (this.getIdentifier() != null && this.getIdentifier().equals(other.getIdentifier())))
      && committed.equals(other.getCommitted())
      && getContentLength() == other.getContentLength()
      && ((contentDigest == null && other.getContentDigest() == null)
      || (contentDigest != null && contentDigest.equals(other.getContentDigest())))
      && getCollectionDate() == other.getCollectionDate();
  }


  /** Return a String that uniquely identifies the Artifact with the
   * specified values.  version -1 means latest version */
  public static String makeKey(String namespace, String auid,
    String uri, int version) {
    StringBuilder sb = new StringBuilder(200);
    sb.append(namespace);
    sb.append(":");
    sb.append(auid);
    sb.append(":");
    sb.append(uri);
    sb.append(":");
    sb.append(version);
    return sb.toString();
  }

  /** Return a String that uniquely identifies "the latest committed
   * version of the Artifact with the specified values" */
  public static String makeLatestKey(String namespace, String auid,
    String uri) {
    return makeKey(namespace, auid, uri, -1);
  }

    /** Return a String that uniquely identifies this Artifact */
    public String makeKey() {
	return Artifact.makeKey(getNamespace(), getAuid(),
				getUri(), getVersion());
    }

    /** Return a String that uniquely identifies "the latest committed
     * version of the Artifact" */
    public String makeLatestKey() {
	return Artifact.makeLatestKey(getNamespace(), getAuid(), getUri());
    }

    // matches ":<ver>" at the end
    static Pattern LATEST_VER_PATTERN = Pattern.compile(":[^:]+$");

    /** Return a String that uniquely identifies "the latest committed
     * version of the Artifact with the specified key" */
    public static String makeLatestKey(String key) {
	Matcher m1 = LATEST_VER_PATTERN.matcher(key);
	if (m1.find()) {
	    return m1.replaceAll(":-1");
	}
	return null;
    }

  public Artifact copyOf() {

    Artifact ret = new Artifact(
      this.getUuid(),
      this.getNamespace(),
      this.getAuid(),
      this.getUri(),
      this.getVersion(),
      this.getCommitted(),
      this.getStorageUrl(),
      this.getContentLength(),
      this.getContentDigest()
    );

    ret.setCollectionDate(this.getCollectionDate());

    return ret;
  }
}
