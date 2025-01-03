ARG METASTORE_VERSION
FROM docker.io/apache/hive:${METASTORE_VERSION} AS appsource

FROM docker.io/eclipse-temurin:17-jre AS final

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

WORKDIR /opt

ARG JARS_DIR

ENV HADOOP_HOME=/opt/hadoop
ENV HIVE_HOME=/opt/hive

COPY --from=appsource /opt/hadoop $HADOOP_HOME
COPY --from=appsource /opt/hive $HIVE_HOME

RUN groupadd -rf hive --gid=1000 && \
    useradd --home $HIVE_HOME -g hive --shell /usr/sbin/nologin --uid 1000 hive -o && \
    chown hive:hive -R $HIVE_HOME && \
    ln -s $JAVA_HOME /opt/jre

ADD ${JARS_DIR}/* $HIVE_HOME/lib/

# Remove jars with open vulnerabilities. These jars are included in the apache hive image but not necessary
# when running the hive metastore only
RUN rm ${HIVE_HOME}/lib/avatica-1.12.0.jar ${HIVE_HOME}/lib/htrace-core-3.1.0-incubating.jar \
    ${HADOOP_HOME}/share/hadoop/yarn/timelineservice/lib/htrace-core-3.1.0-incubating.jar  

USER hive

ENTRYPOINT ["/opt/hive/bin/hive", "--skiphadoopversion", "--skiphbasecp", "--verbose", "--service", "metastore"]
