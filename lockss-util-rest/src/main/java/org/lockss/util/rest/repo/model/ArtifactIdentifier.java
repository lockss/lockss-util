/*
 * Copyright (c) 2017-2019, Board of Trustees of Leland Stanford Jr. University,
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
import com.google.common.collect.ComparisonChain;
import org.apache.commons.lang3.builder.EqualsBuilder;
import java.io.Serializable;
import java.util.Objects;

import org.lockss.util.*;

/**
 * Class that serves as an identifier for artifacts.
 *
 * Artifacts are identified uniquely by the tuple of (Namespace, AUID, URL, Version). Within the context of a LOCKSS
 * repository, they are also uniquely identified by their artifact ID.
 *
 * Comparable is implemented to allow for an ordering of artifacts.
 */
public class ArtifactIdentifier implements Serializable, Comparable<ArtifactIdentifier> {
    private String uuid;
    private String namespace;
    private String auid;
    private String uri;
    private Integer version;

    public ArtifactIdentifier(String namespace, String auid, String uri, Integer version) {
        this(null, namespace, auid, uri, version);
    }

    public ArtifactIdentifier(String uuid, String namespace, String auid, String uri, Integer version) {
        this.uuid = uuid;
        setNamespace(namespace);
        setAuid(auid);
        this.uri = uri;
        this.version = version == null ? null : Integer.valueOf(version);
    }

    /**
     * Returns the namespace encoded in this artifact identifier.
     *
     * @return Namespace
     */
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
      this.namespace = namespace == null ? null
        : StringPool.MISCELLANEOUS.intern(namespace);
    }

    public void setAuid(String auid) {
        this.auid = auid == null ? null :StringPool.AUIDS.intern(auid);
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Returns the Archival Unit ID (AUID) encoded in this artifact identifier.
     *
     * @return Archival unit ID
     */
    public String getAuid() {
        return auid;
    }

    /**
     * Returns the URI component in this artifact identifier.
     *
     * @return ArtifactData URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns the version component encoded in this artifact identifier.
     *
     * @return ArtifactData version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Returns the artifact's UUID.
     *
     * @return Internal artifact UUID
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the artifact's UUID.
     *
     * @param uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Implements Comparable - The canonical order here from most significant to least significant is the assigned
     * namespace, archival unit (AU), URI, and version. The artifact UUID is a unique internal handle and has no
     * useful ordering in this context, and so is not included in the comparison calculation.
     *
     * @param other The other instance of ArtifactIdentifier to compare against.
     * @return An integer indicating whether order relative to other.
     */
    @Override
    public int compareTo(ArtifactIdentifier other) {
        return ComparisonChain.start()
                .compare(this.getNamespace(), other.getNamespace())
                .compare(this.getAuid(), other.getAuid())
                .compare(this.getUri(), other.getUri(),
			 PreOrderComparator.INSTANCE)
                .compare(this.getVersion(), other.getVersion())
//                .compare(this.getId(), this.getId())
                .result();
    }

    @Override
    public boolean equals(Object o) {
       ArtifactIdentifier other = (ArtifactIdentifier)o;
       return new EqualsBuilder()
         .append(uri, other.getUri())
         .append(version, other.getVersion())
         .append(auid, other.getAuid())
         .append(namespace, other.getNamespace())
         .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, auid, uri, version);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "ArtifactIdentifier{" +
                "uuid='" + uuid + '\'' +
                ", namespace='" + namespace + '\'' +
                ", auid='" + auid + '\'' +
                ", uri='" + uri + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    /**
     * Returns the artifact stem of this artifact identifier, which represents a tuple
     * containing the namespace, AUID, and URL.
     *
     * @return A {@link ArtifactStem} containing the artifact stem of this artifact identifier.
     */
    @JsonIgnore
    public ArtifactStem getArtifactStem() {
        return new ArtifactStem(getNamespace(), getAuid(), getUri());
    }

    /**
     * Struct representing a tuple of namespace, AUID, and URL. Used for artifact version locking.
     */
    public static class ArtifactStem {
        private final String namespace;
        private final String auid;
        private final String uri;

        public ArtifactStem(String namespace, String auid, String uri) {
            this.namespace = namespace;
            this.auid = auid;
            this.uri = uri;
        }

      /**
       * Returns the Archival Unit ID (AUID) encoded in this artifact identifier.
       *
       * @return Archival unit ID
       */
      public String getAuid() {
        return auid;
      }

      /**
       * Returns the namespace encoded in this stem
       *
       * @return Namespace
       */
      public String getNamespace() {
        return namespace;
      }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArtifactStem that = (ArtifactStem) o;
            return namespace.equals(that.namespace) && auid.equals(that.auid) && uri.equals(that.uri);
        }

        @Override
        public int hashCode() {
            return Objects.hash(namespace, auid, uri);
        }
    }
}
