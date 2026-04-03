# `lockss-util-rest` Release Notes

## 1.7.0 (LOCKSS 2.0.91-beta2)

### Features

* Pagination support for AU configurations retrieval, with new models for pagination and continuation tokens.
* Support for adding specified versions of artifacts.
* Added `Artifact.toStringShort(style)`.
* Pipelined Artifact iterator with improved configurability.
* "WARC-local" journaling support.
* MDQ and MDX service merge changes.
* CDX record endpoint improvements.
* Allow RestTemplate to be reset to change timeouts.

### Improvements

* Refactored URI construction to use `encode().build().expand()` with template variables for proper encoding of special characters (e.g., `+`).
* Consolidated `/aus/{auid}/artifacts` endpoint into `/artifacts`; moved AUID to query parameter.
* Relabeled REST endpoint paths: `normalizeUrl` to `normalizeurl`, `mimeType` to `mediatypes`.
* Made client connection pool in RestTemplate configurable.
* Clean up and update API specifications for `PageInfo`, `ArtifactPageInfo`, `AuidPageInfo`, `AuSize`, `RepositoryInfo` (nullable properties, renamed `resultsPerPage` to `itemsInPage`).
* Propagated enum changes (`IncludeContentEnum`, `BulkAuOpEnum`, etc.) from crawler and poller services.
* Isolated Solr `sortUri` field and computation to `ArtifactSolrDocument`.
* Introduced `RepositoryStatistics`.
* Fixed `RestLockssRepository#callBulkOp` with tests for bulk store operations.

### Bug Fixes

* `getArtifact()` now throws on non-404 error responses.
* Fix encoding of query args containing `+` symbol.
* `LockssArtifactAlreadyExistsException` now takes `ArtifactIdentifier`.

### Dependencies

* Bumped `apache-cxf-core` (Dependabot security alert).


## Changes Since 1.1.1

*   ...

## 1.1.1

*   Out of an abundance of caution, re-released 1.1.0 with Jackson-Databind 2.9.10.8 (CVE-2021-20190).

## 1.1.0

### Features

*   ...

### Fixes

*   ...

## 1.0.0

### Features

*   Initial release.
