# `lockss-util-core` Release Notes

## 1.150 (LOCKSS 2.0.91-beta2)

### Features

* Added `ListUtil#fromInputStream` utility method
* Added `getResource()`, `getResourceAsStream()`, `getResourceContent()` utility methods
* Introduced `RepositoryStatistics` for tracking iterator reiteration time
* Full disk reporting support
* Moved code previously in `lockss-core` to `lockss-util-core`
* Increased buffer size when reading large WARCs

### Kubernetes

* Use `ClientBuilder` to build client from Kubernetes cluster configuration
* Use version 22 of the Java client to match the Kubernetes version
* Removed all direct calls to SnakeYAML; use `io.kubernetes.java-client` calls instead
* Write and apply second network policy for Content Access control; do not copy existing `resourceVersion` into new `NetworkPolicy`

### Dependencies

* Added `jspecify` dependency required by Log4j 2.25


## Changes Since 1.9.0

### Features

*   Added DirArchiver
*   Added HostnameUtil.isValidHostname()

## 1.9.0

### Features

*   ...

### Fixes

*   ...

## 1.8.0

### Features

*   Renamed from `lockss-util` 1.7.0.
*   Log timestamp at start and end of each log file, and at midnight.
*   Reduce logging to console.
