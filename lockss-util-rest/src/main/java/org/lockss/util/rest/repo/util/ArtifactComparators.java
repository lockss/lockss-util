/*

Copyright (c) 2000-2019, Board of Trustees of Leland Stanford Jr. University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.lockss.util.rest.repo.util;

import java.util.Comparator;

import org.lockss.util.rest.repo.model.Artifact;
import org.lockss.util.PreOrderComparator;

public class ArtifactComparators {

  public static final Comparator<Artifact> BY_URI =
      Comparator.comparing(Artifact::getUri, PreOrderComparator.INSTANCE);      
  
  public static final Comparator<Artifact> BY_DECREASING_VERSION =
      Comparator.comparingInt(Artifact::getVersion).reversed();      
  
  public static final Comparator<Artifact> BY_DATE_BY_AUID_BY_DECREASING_VERSION =
      Comparator.comparing(Artifact::getCollectionDate)
                .thenComparing(Artifact::getAuid)
                .thenComparing(Comparator.comparingInt(Artifact::getVersion).reversed());

  public static final Comparator<Artifact> BY_URI_BY_DECREASING_VERSION =
      Comparator.comparing(Artifact::getUri, PreOrderComparator.INSTANCE)
                .thenComparing(Comparator.comparingInt(Artifact::getVersion).reversed());
  
  public static final Comparator<Artifact> BY_URI_BY_AUID_BY_DECREASING_VERSION =
      Comparator.comparing(Artifact::getUri, PreOrderComparator.INSTANCE)
                .thenComparing(Artifact::getAuid)
                .thenComparing(Comparator.comparingInt(Artifact::getVersion).reversed());

  public static final Comparator<Artifact> BY_URI_BY_DATE_BY_AUID_BY_DECREASING_VERSION =
      Comparator.comparing(Artifact::getUri, PreOrderComparator.INSTANCE)
                .thenComparing(Artifact::getCollectionDate)
                .thenComparing(Artifact::getAuid)
                .thenComparing(Comparator.comparingInt(Artifact::getVersion).reversed());
}
