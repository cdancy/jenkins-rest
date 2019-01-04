### Version 0.0.17 (TBA)

### Version 0.0.16 (January 4, 2018)
* FIX: `buildWithParameters` now supports a null parameter map for parameterized builds that do not override any params. - [Pull Request 43](https://github.com/cdancy/jenkins-rest/pull/43)
* Bump `gradle` to `4.10.2`
* Bump `shadow` plugin to `2.0.4`

### Version 0.0.15 (N/A)

### Version 0.0.14 (August 17, 2018)
* `JenkinsClient` now implements `Closeable` to better work with jdk8+ try-with-resources.
* Bump `jclouds` to `2.1.1`.
* Bump `AutoValue` to `1.6.2`
* Bump `gradle-bintray-plugin` to `1.8.4`
* Bump `gradle` to `4.9`

### Version 0.0.13 (July 26, 2018)
* ADDED: Endpoint `Jobs.buildInfo` now returns `Actions` as part of response. - [Pull Request 29](https://github.com/cdancy/jenkins-rest/pull/29)

### Version 0.0.12 (July 15, 2018)
* FIX: for when a build parameter is set to the empty string, the `QueueItem.create()` method should set the parameter value to the empty string.

### Version 0.0.11 (May 24, 2018)
* ADDED: all `JobsApi` endpoints gained the `optionalFolderPath` argument. - [Pull request 22](https://github.com/cdancy/jenkins-rest/pull/22)

### Version 0.0.10 (May 14, 2018)
* ADDED: `PluginManagerApi` with initial endpoint `plugins`. - [Pull request 17](https://github.com/cdancy/jenkins-rest/pull/17)
* REFACTOR: convert all endpoints which return an Integer into an `IntegerResposne` so that we can capture any errors. - [Pull request 18](https://github.com/cdancy/jenkins-rest/pull/18)
* ADDED: all endpoints within `JobsApi` that can take an optional folder path have been amended to provide an optional parameter to do so. - [Pull request 20](https://github.com/cdancy/jenkins-rest/pull/20)

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
