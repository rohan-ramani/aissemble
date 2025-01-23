ARG METASTORE_VERSION
FROM docker.io/eclipse-temurin:17-jre AS builder

ARG METASTORE_VERSION
ARG HADOOP_VERSION
ARG JARS_DIR
ARG BIN_DIR
ARG PATCH_DIR

ENV HADOOP_HOME=/opt/hadoop
ENV HIVE_HOME=/opt/hive
ENV HIVE_VER=$METASTORE_VERSION

# Install hadoop
RUN cd /tmp \
    && wget https://archive.apache.org/dist/hadoop/common/hadoop-${HADOOP_VERSION}/hadoop-${HADOOP_VERSION}-lean.tar.gz -O - \
       | tar -xzf - \
    && mv hadoop-${HADOOP_VERSION} $HADOOP_HOME

# Use standalone binary instead of full-service version on original image
COPY ${BIN_DIR}/hive-standalone-metastore-server-bin.tar.gz /tmp
RUN cd /tmp \
    && tar -xf /tmp/hive-standalone-metastore-server-bin.tar.gz \
    && mv apache-hive-metastore-${METASTORE_VERSION}-bin $HIVE_HOME \
    && rm /tmp/hive-standalone-metastore-server-bin.tar.gz

# Update library jars used by Hive and Hadoop
COPY --chmod=755 ./src/main/resources/scripts/setup.sh $HIVE_HOME/setup.sh
ADD ${PATCH_DIR}/* /tmp/patch-jars
RUN $HIVE_HOME/setup.sh /tmp/patch-jars && rm -rf /tmp/patch-jars

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

FROM docker.io/eclipse-temurin:17-jre AS final

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

RUN groupadd -rf hive --gid=1000 && \
    useradd --home $HIVE_HOME -g hive --shell /usr/sbin/nologin --uid 1000 hive -o && \
    chown hive:hive -R $HIVE_HOME

ADD ${JARS_DIR}/* $HIVE_HOME/lib/

USER hive
WORKDIR $HIVE_HOME

ENV VERBOSE=true
ENTRYPOINT ["/entrypoint.sh"]