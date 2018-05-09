### Version 0.0.9 (May 9, 2018)

* REFACTOR: don't assume "crumb validation" being enabled. - [Pull request 15](https://github.com/cdancy/jenkins-rest/pull/15)

### Version 0.0.8 (May 7, 2018)

* ADDED: Expose `modules` option in `JenkinsClient` builder. - [Pull request 12](https://github.com/cdancy/jenkins-rest/pull/12)

### Version 0.0.7 (April 26, 2018)

* BUG: Fix `QueueItem` incorrect parameter merging. - [Pull request 8](https://github.com/cdancy/jenkins-rest/pull/8)

### Version 0.0.6 (April 21, 2018)

* ADDED: `QueueApi` gained endpoints `cancel` and `queueItem`.

### Version 0.0.5 (April 18, 2018)

* REFACTOR: Do not throw exception when deprecated headers are missing.

### Version 0.0.4 (April 15, 2018)

* REFACTOR: various changes project wide to bring up-to-date with modern Jenkins.

### Version 0.0.3 (May 12, 2016)

* REFACTOR: JobsApi.build* endpoints will now return a 0 should a queueId not be handed back from Jenkins.

### Version 0.0.2 (April 19, 2016)

* ADDED: JobsApi.
* ADDED: QueueApi.

### Version 0.0.1 (April 14, 2016)

* init for jenkins-rest
* ADDED: SystemApi.
* ADDED: StatisticsApi.
