### Version 1.0.2 (September 29, 2022)
* ADDED: extensive build info. - [Pull Request 259](https://github.com/cdancy/jenkins-rest/pull/259)

### Version 1.0.1 (May 17, 2022)
* FIXED: Fix javadoc for all versions of Java. - [Pull Request 236](https://github.com/cdancy/jenkins-rest/pull/236)
* FIXED: Add charset to JobsApi. - [Pull Request 233](https://github.com/cdancy/jenkins-rest/pull/233)

### Version 1.0.0 (January 13, 2022)
* ADDED: `JobsApi` gained endpoints `stop`, `term`, and `kill`. - [Pull Request 207](https://github.com/cdancy/jenkins-rest/pull/207)
* ADDED: Fix `crumb` issue and create `UserApi` feature. - [Pull Request 195](https://github.com/cdancy/jenkins-rest/pull/195)

### Version 0.0.30 (November 1, 2021)
* Publish project to maven central.

### Version 0.0.29 (February 2, 2021)
* BUGFIX: Fix possible null `url` item int `Task`. - [Pull Request 144](https://github.com/cdancy/jenkins-rest/pull/144)

### Version 0.0.28 (January 1, 2021)
* BUGFIX: Fix null queue item task name. - [Pull Request 138](https://github.com/cdancy/jenkins-rest/pull/138)

### Version 0.0.27 (April 21, 2020)
* BUGFIX: Fix for crumb which was being held in a static field. - [Pull Request 88](https://github.com/cdancy/jenkins-rest/pull/88)

### Version 0.0.25 (February 11, 2020)
* ADDED: Add _color_ to Job. - [Pull Request 86](https://github.com/cdancy/jenkins-rest/pull/86)

### Version 0.0.24 (February 10, 2020)
* BUGFIX: Fix Nullable import - use jclouds version. - [Pull Request 84](https://github.com/cdancy/jenkins-rest/pull/84)

### Version 0.0.23 (January 27, 2020)
* ADDED: `JobsApi.jobList` endpoint. - [Pull Request 81](https://github.com/cdancy/jenkins-rest/pull/81)

### Version 0.0.22 (December 16, 2019)
* ADDED: Artifact `displayPath` is not mandatory anymore. - [Pull Request 77](https://github.com/cdancy/jenkins-rest/pull/77)
* ADDED: Fix crumb issue with Jenkins. - [Pull Request 70](https://github.com/cdancy/jenkins-rest/pull/70)

### Version 0.0.21 (December 2, 2019)
* ADDED: `JobsApi.progressiveText` with additional `buildNumber` param. - [Pull Request 74](https://github.com/cdancy/jenkins-rest/pull/74)

### Version 0.0.20 (October 8, 2019)
* ADDED: `PipelineApi`. - [Pull Request 64](https://github.com/cdancy/jenkins-rest/pull/64)
* ADDED: Bump various dependency versions.

### Version 0.0.19 (June 20, 2019)
* ADDED: 'message' property attached to `Error` object can now be null.

### Version 0.0.18 (April 2, 2019)
* ADDED: `SystemApi` gained endpoints `quietDown` and `cancelQuietDown`. - [Pull Request 54](https://github.com/cdancy/jenkins-rest/pull/54)

### Version 0.0.17 (March 18, 2019)
* ADDED: `JobsApi` gained endpoint `rename`. - [Pull Request 52](https://github.com/cdancy/jenkins-rest/pull/52)
* Bump `gradle` to `4.10.3`
* Bump `jclouds` to `2.1.2`

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
