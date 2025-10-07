ARG METASTORE_VERSION
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.21 as builder

ARG METASTORE_VERSION
ARG HADOOP_VERSION
ARG JARS_DIR
ARG BIN_DIR
ARG PATCH_DIR

ENV HADOOP_HOME=/opt/hadoop
ENV HIVE_HOME=/opt/hive
ENV HIVE_VER=$METASTORE_VERSION

USER root

RUN set -eux; \
    microdnf install -y gzip util-linux

# Install hadoop
RUN --mount=source=${BIN_DIR},target=/tmp/bin cd /tmp \
    && tar -xzf /tmp/bin/hadoop.tar.gz \
    && mv hadoop-${HADOOP_VERSION} $HADOOP_HOME

# Use standalone binary instead of full-service version on original image
RUN --mount=source=${BIN_DIR},target=/tmp/bin cd /tmp \
    && tar -xf /tmp/bin/hive-standalone-metastore-server-bin.tar.gz \
    && mv apache-hive-metastore-${METASTORE_VERSION}-bin $HIVE_HOME

# Update library jars used by Hive and Hadoop
COPY --chmod=755 ./src/main/resources/scripts/setup.sh $HIVE_HOME/setup.sh
RUN --mount=target=/tmp/patch-jars,type=bind,source=${PATCH_DIR} \
    $HIVE_HOME/setup.sh /tmp/patch-jars

# Hadoop ships with source jars, which aren't necessary and bloat the image, also remove YARN support as we only support K8s
RUN rm -rf $HADOOP_HOME/share/hadoop/common/sources \
           $HADOOP_HOME/share/hadoop/hdfs/sources \
           $HADOOP_HOME/share/hadoop/mapreduce/sources \
           $HADOOP_HOME/share/hadoop/tools/sources \
           $HADOOP_HOME/share/hadoop/yarn/*

# Patch for HIVE-28487: https://github.com/apache/hive/pull/5419
RUN sed -i \
    's/org.apache.hadoop.hive.metastore.tools.MetastoreSchemaTool/org.apache.hadoop.hive.metastore.tools.schematool.MetastoreSchemaTool/' \
    "$HIVE_HOME/bin/ext/schemaTool.sh"

RUN find / -type f -name '*derby*.jar' -delete

FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.21 as final

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

WORKDIR /opt

ARG METASTORE_VERSION
ARG HADOOP_VERSION
ARG JARS_DIR
ARG BIN_DIR
ARG PATCH_DIR

ENV HADOOP_HOME=/opt/hadoop
ENV HIVE_HOME=/opt/hive
ENV HIVE_VER=$METASTORE_VERSION

COPY --from=builder $HADOOP_HOME $HADOOP_HOME
COPY --from=builder $HIVE_HOME $HIVE_HOME

COPY --chmod=755 src/main/resources/scripts/entrypoint.sh /entrypoint.sh

USER root

RUN groupadd -rf hive --gid=1000 && \
    useradd --home $HIVE_HOME -g hive --shell /usr/sbin/nologin --uid 1000 hive -o && \
    chown hive:hive -R $HIVE_HOME

ADD ${JARS_DIR}/* $HIVE_HOME/lib/

USER hive
WORKDIR $HIVE_HOME

ENV VERBOSE=true
ENTRYPOINT ["/entrypoint.sh"]
