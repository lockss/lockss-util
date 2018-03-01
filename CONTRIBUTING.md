# Git Layout

This Git repository uses the LOCKSS naming and policy conventions for Git Flow,
which are:

*   The stable and development branches are `master` and `develop`
    respectively.
*   The prefixes for feature, release, hotfix, support and bugfix branches are
    `feature-`, `release-`, `hotfix-`, `support-` and `bugfix-`
    respectively (but we do not use support and bugfix branches).
*   The prefix for release tags is `version-`
*   Set configuration parameters or use command line options such that finishing
    a feature, release or hotfix squashes during merge and keeps both the local
    and remote branches.

Resources:

* https://danielkummer.github.io/git-flow-cheatsheet/
* https://github.com/petervanderdoes/gitflow-avh
* https://github.com/lockss/lockss-gitflow-init