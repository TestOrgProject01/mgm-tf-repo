# data-api-perf-tests

## Performance Testing Project for Data API team

This project contains performance testing artifacts for the Data API team. At the moment, these artifacts consist of

- [CDP API](https://github.com/MGMResorts/Customer-Data-Product-Services)
- [Customer Search](https://github.com/MGMResorts/CDP-C360-UI)
- (soon) [Recommendation API](https://github.com/MGMResorts/recommendation-engine)

### Project Rundown
This project primarily uses
[JMeter DSL](https://abstracta.us/blog/tools/jmeter-dsl-an-innovative-tool-for-performance-testing/) from Abstracta to
create and execute load tests. JMeter DSL, as the name suggests, is a DSL for JMeter that simplifies the creation of
JMeter scripts and the execution of such scripts. This is an open source project that can be easily embedded into any
modern Java projects.

Using JMeter DSL, we retain most of the JMeter functionality (at least most of the commonly used functionality) while
added additional benefits such as:
- Faster way to write and run tests (no need to write XML or to run [JMeter](https://github.com/apache/jmeter) GUI)
- Incorporate existing Java-based capabilities into the test creation and executions
    - E.g. logging, Cosmos data access, dynamic data generation, Junit-style assertions, etc.
- Conducive to collaboration and source control
    - scale to teams of (virtually) any size
    - incorporate all the features of model source control system (e.g. branching, merging, tagging, etc.)
    - no more _insane_ XML merge conflicts
- Manage complex or dynamic load test scenarios
- Ability to run load tests locally
  and [in Azure environment](https://abstracta.us/blog/jmeter-dsl/new-release-integration-between-jmeter-dsl-and-azure-load-testing-2/) (
  i.e. [Azure Load Testing](https://azure.microsoft.com/en-us/products/load-testing/))
- Ability to
  [generate JMeter JMX file from DSL code](https://abstracta.github.io/jmeter-java-dsl/guide/#dsl-code-generation-from-jmx-file)
  (if you must use the JMeter GUI)
- Integration with
  [Real-time Metric Visualization](https://abstracta.github.io/jmeter-java-dsl/guide/#real-time-metrics-visualization-and-historic-data-storage)
  tools such as [Grafana](https://grafana.com/docs/grafana/latest/features/datasources/influxdb/) and
  [InfluxDB](https://www.influxdata.com/products/influxdb-overview/)
- [DSL Recorder](https://abstracta.github.io/jmeter-java-dsl/guide/#dsl-recorder) to create DSL code via point-and-click
  on browser

#### Convert existing JMX file to DSL code
- Download
  [JMeterDSL CLI](https://github.com/abstracta/jmeter-java-dsl/releases/download/v1.13/jmeter-java-dsl-cli-1.13.jar)
  to start
- Follow
  [DSL code generation from JMX file](https://abstracta.github.io/jmeter-java-dsl/guide/#dsl-code-generation-from-jmx-file)

#### Run JMX file
- Follow [Run JMX file](https://abstracta.github.io/jmeter-java-dsl/guide/#run-jmx-file)

#### Running in Parallel Mode (i.e. not as Thread Group)
- Add `jmeter-java-dsl-parallel` to the project's `pom.xml`:
  ```xml
    <dependency>
        <groupId>com.abstracta.jmeter</groupId>
        <artifactId>jmeter-java-dsl-parallel</artifactId>
        <version>1.13</version>
    </dependency>
  ```
- Follow [Parallel Requests](https://abstracta.github.io/jmeter-java-dsl/guide/#parallel-requests)

#### Main Tools & Libraries
- Java 17+
- Maven 3+
- JMeterDSL 1.13

-----

### Useful Links
- [Awesome JMeter](https://github.com/aliesbelik/awesome-jmeter)

-----

_more to come..._

