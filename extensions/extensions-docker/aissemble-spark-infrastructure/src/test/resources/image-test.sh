#!/bin/sh

###
# #%L
# aiSSEMBLE::Extensions::Docker::Spark Infrastructure
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###

#Create events dir that's usually mounted at runtime
mkdir /tmp/spark-events
$SPARK_HOME/sbin/start-history-server.sh &

#Switch to Embedded Derby DB for Thrift Server test
sed -i 's/jdbc:mysql:\/\/hive-metastore-db:3306\/metastore?createDatabaseIfNotExist=true&amp;allowPublicKeyRetrieval=true&amp;useSSL=false/jdbc:derby:\/tmp\/metastore;create=true/' $SPARK_HOME/conf/hive-site.xml
sed -i 's/com.mysql.cj.jdbc.Driver/org.apache.derby.jdbc.EmbeddedDriver/' $SPARK_HOME/conf/hive-site.xml
$SPARK_HOME/sbin/start-thriftserver.sh
