#!/bin/sh
###
# #%L
# aiSSEMBLE::Extensions::Docker::Spark
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###

#---
## Updates a Spark's JARs based on a list of Maven coordinates
##
## @Arguments: list of maven coordinates in `group:artifact:version:classifier` format where `:classifier` is optional
#---
update_maven_jars() {
  echo
  echo "Updating jars in $SPARK_JARS ($SPARK_VERSION)"
  echo
  mvnjars="$1"

  mkdir /tmp/jars || exit $?
  for gav in $mvnjars; do
    #Parse GAV into separate variables, classifier may or may not be present as the last item
    group=$(echo "$gav" | cut -d : -f 1)
    artifact=$(echo "$gav" | cut -d : -f 2)
    version=$(echo "$gav" | cut -d : -f 3)
    classifier=$(echo "$gav" | cut -d : -f 4)
    if [ -n "$classifier" ]; then
      classifier="-$classifier"
    fi
    echo "-------------------------------------------"
    echo "Replacing $group:$artifact with updated JAR"

    # Fetch the updated JAR from Maven Central
    jar="$artifact-$version$classifier.jar"
    path=$(echo "$group" | sed 's|\.|/|g')
    url="https://repo1.maven.org/maven2/$path/$artifact/$version/$jar"
    echo "Fetching $url"
    wget -q "$url" -P /tmp/jars || exit $?

    # Find the old jar that is being replaced
    replaceable=$(find / -type f -regex "$SPARK_JARS/$artifact-[^-]*$classifier.jar" 2>/dev/null)
    echo "Replacing '$replaceable'"
    count=$(echo "$replaceable" | wc -w )
    if [  "$count" -gt 1 ]; then
      echo "Unexpected number of matches to replace!"
      exit 1
    fi

    # Delete the old JAR and move the new one into the Spark classpath
  if [ -n "$replaceable" ]; then
      rm "$replaceable" || exit $?
    fi
    mv "/tmp/jars/$jar" "$SPARK_JARS" || exit $?
    echo
  done
}

#---
## Updates Spark's jackson-mapper JAR to a RedHat version
##
## @param: the RedHat version name for the JAR
#---
update_jackson() {
  ### The codehaus version of Jackson is defunct, but RedHat has published a patched version
  JACKSON_VER=$1
  echo "Updating Jackson to $JACKSON_VER"
  jackson="https://maven.repository.redhat.com/ga/org/codehaus/jackson/jackson-mapper-asl/$JACKSON_VER/jackson-mapper-asl-$JACKSON_VER.jar"
  wget -q "$jackson" -P /tmp/jars || exit $?
  rm $SPARK_JARS/jackson-mapper-asl-*.jar || exit $?
  mv "/tmp/jars/jackson-mapper-asl-$JACKSON_VER.jar" "$SPARK_JARS" || exit $?
}

#---
## Removes all JARs supporting Mesos functionality from Spark's JARs
#---
remove_mesos() {
  echo "Remove Mesos JARs"
  # Mesos support is being dropped in 4.0.0: https://issues.apache.org/jira/browse/SPARK-44442
  find $SPARK_JARS -name '*mesos*.jar' -exec echo "Deleting {}" \; -exec rm {} \; || exit $?
}

#---
## Creates a Python package entry for the bundled PySpark installation
##
## @param: Spark's home directory
## @param: the Spark version
#---
register_pyspark() {
  echo "Register PySpark installation with PIP"
  # Following approach mentioned in https://github.com/pypa/pip/issues/10458
  echo "\
from setuptools import setup

setup(
    name='pyspark',
    version='$2',
    description='A dummy package representing the provided PySpark installation',
)" > "$1/python/setup.py"
  python3 -m pip install "$1/python"
}

SPARK_JARS="$1/jars"
SPARK_VERSION=$2
HADOOP_VERSION=$3

update_maven_jars "com.google.code.gson:gson:2.8.9 \
                   com.google.guava:guava:33.3.1-jre \
                   com.squareup.okhttp3:okhttp:3.14.9 \
                   io.netty:netty-codec-http2:4.1.116.Final \
                   io.netty:netty-codec-http:4.1.116.Final \
                   io.netty:netty-common:4.1.116.Final \
                   org.apache.avro:avro-ipc:1.11.4 \
                   org.apache.avro:avro-mapred:1.11.4 \
                   org.apache.avro:avro:1.11.4 \
                   org.apache.commons:commons-compress:1.27.1 \
                   commons-io:commons-io:2.16.1 \
                   commons-codec:commons-codec:1.17.2 \
                   org.apache.derby:derby:10.16.1.1 \
                   org.apache.derby:derbytools:10.16.1.1 \
                   org.apache.derby:derbyshared:10.16.1.1 \
                   org.apache.hadoop.thirdparty:hadoop-shaded-guava:1.3.0 \
                   org.apache.hadoop:hadoop-client-api:$HADOOP_VERSION \
                   org.apache.hadoop:hadoop-client-runtime:$HADOOP_VERSION \
                   org.apache.hadoop:hadoop-yarn-server-web-proxy:$HADOOP_VERSION \
                   org.apache.hive:hive-beeline:2.3.10 \
                   org.apache.hive:hive-cli:2.3.10 \
                   org.apache.hive:hive-common:2.3.10 \
                   org.apache.hive:hive-exec:2.3.10:core \
                   org.apache.hive:hive-jdbc:2.3.10 \
                   org.apache.hive:hive-llap-common:2.3.10 \
                   org.apache.hive:hive-metastore:2.3.10 \
                   org.apache.hive:hive-serde:2.3.10 \
                   org.apache.hive:hive-shims:2.3.10 \
                   org.apache.hive.shims:hive-shims-0.23:2.3.10 \
                   org.apache.hive.shims:hive-shims-common:2.3.10 \
                   org.apache.hive.shims:hive-shims-scheduler:2.3.10 \
                   org.apache.thrift:libthrift:0.16.0 \
                   org.apache.ivy:ivy:2.5.3 \
                   org.apache.parquet:parquet-column:1.15.0 \
                   org.apache.parquet:parquet-format-structures:1.15.0 \
                   org.apache.parquet:parquet-encoding:1.15.0 \
                   org.apache.parquet:parquet-jackson:1.15.0 \
                   org.apache.parquet:parquet-hadoop:1.15.0 \
                   org.apache.parquet:parquet-common:1.15.0 \
                   org.apache.zookeeper:zookeeper-jute:3.9.3 \
                   org.apache.zookeeper:zookeeper:3.9.3"
update_jackson '1.9.14.jdk17-redhat-00001'
remove_mesos
register_pyspark $SPARK_HOME $SPARK_VERSION