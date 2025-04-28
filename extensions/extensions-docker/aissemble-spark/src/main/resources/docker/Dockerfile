# Script for creating base Spark Docker image
#
ARG SPARK_VERSION
ARG SCALA_VERSION
FROM docker.io/apache/spark:${SPARK_VERSION}-scala${SCALA_VERSION}-java17-python3-ubuntu

#pull var from global scope to build stage
ARG SPARK_VERSION
ARG HADOOP_VERSION
ARG PATCH_DIR
ARG PYTHON_VERSION=3.11

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

USER root

# Setup Spark home directory
RUN usermod -d /opt/spark spark
RUN mkdir $SPARK_HOME/checkpoint && \
  mkdir $SPARK_HOME/krausening && \
  mkdir $SPARK_HOME/warehouse && \
  mkdir $SPARK_HOME/policies && \
  mkdir $SPARK_HOME/logs

# Update repositories and add necessary ones
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt-get -y update \
    && apt-get install -y software-properties-common \
    && add-apt-repository ppa:deadsnakes/ppa

# git and build-essential are used during pip install of some dependencies (e.g. python-deequ)
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
  apt-get update -y && apt-get install --assume-yes \
  build-essential \
  curl \
  python${PYTHON_VERSION} \
  python${PYTHON_VERSION}-dev \
  python${PYTHON_VERSION}-distutils \
#Patch for CVE-2023-4863: upgrade libwebp7 to 1.3.2+
  && apt-get upgrade -y libwebp7 \
  && ln -s /usr/bin/python${PYTHON_VERSION} /usr/bin/python \
#Patch for CVE-2023-0286: upgrade cryptography to 39.0.1+ (https://github.com/advisories/GHSA-x4qr-2fvf-3mr5) \
  && apt-get remove python3-cryptography -y \
  && python -m pip install --upgrade cryptography

# Pyspark uses `python3` to execute pyspark pipelines. This links out latest python install to that command.
RUN ln -sf /usr/bin/python3.11 /usr/bin/python3

# Update gosu (based on https://github.com/tianon/gosu/blob/master/INSTALL.md)
# Fixes: GO-2023-2185, GO-2023-1840, GO-2023-1568
COPY --from=docker.io/tianon/gosu:1.17-debian /gosu /usr/sbin/gosu

# Update library jars used by Spark and register PySpark as an available python package
COPY --chmod=755 ./src/main/resources/scripts/setup.sh $SPARK_HOME/setup.sh
RUN --mount=target=/tmp/patch-jars,type=bind,source=${PATCH_DIR} \
    $SPARK_HOME/setup.sh /tmp/patch-jars $SPARK_HOME $SPARK_VERSION $HADOOP_VERSION

RUN find / -type f -name '*derby*.jar' -delete

## Add spark configurations
COPY ./src/main/resources/conf/ $SPARK_HOME/conf/
RUN chown -R spark:spark $SPARK_HOME

# Fixed the Reflection API breaks java module boundary issue for Java 16+
ENV JDK_JAVA_OPTIONS='--add-opens java.base/java.lang=ALL-UNNAMED'

USER spark
