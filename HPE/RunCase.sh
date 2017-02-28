#!/bin/sh
#
#You maybe modify the following path:
JAVA_HOME=/usr/java/jdk1.8.0_60
#JAVA_HOME=/usr/java/jrockit-jdk1.6.0_26-R28.1.4-4.0.1

#OTHER_LOGS=""

#Common
#OPTS="-server -Xmx4g  -Xms4g -Duser.timezone=GMT+8 "
OPTS="-server -Xmx32g  -Xms32g -Duser.timezone=GMT+8 -verbose:class"

#-------------------------------------
APPROOT=$(readlink -e $0)
APPROOT=$(dirname $APPROOT)
APP_NAME=$1
NOW=`date +%Y%m%d%H%M%S`
HOSTNAME=`hostname`
#-------------------------------------
#----------- Sun JDK
OPTS="$OPTS -XX:+UseParallelGC -XX:+DisableExplicitGC "
OPTS="$OPTS -verbose:gc -Xloggc:$APPROOT/logs/${APP_NAME}.$NOW.gc.log -XX:+PrintGCDetails"
OPTS="$OPTS -Djava.library.path=/usr/lib64"
#java.library.path: find /usr -name libndbclient.so
#OPTS="$OPTS -Djava.rmi.server.hostname=$HOSTNAME -Dcom.sun.management.jmxremote.port=7091 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false "

#---------- Jrockit
#OPTS="$OPTS -Xgc:deterministic -XpauseTarget:400ms -XX:+UseCallProfiling -XX:+UseLargePagesForHeap -XX:+UseNewHashFunction "
#OPTS="$OPTS -XX:+FlightRecordingDumpOnUnhandledException -XX:FlightRecordingDumpPath=${APP_NAME}.$NOW.Exception.jfr"
#OPTS="$OPTS -XX:FlightRecorderOptions=dumponexit=true,dumponexitpath=logs/${APP_NAME}.$NOW.jfr,defaultrecording=true"
#OPTS="$OPTS -Xmanagement -Dcom.sun.management.jmxremote.port=7091 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false "
#-------------------------------------
# check for java version 1.6.0_20 or later
$JAVA_HOME/bin/java -version 2>&1 | grep "java version" | awk '{print $3}' | tr -d \" | awk -F. '$2<6{msg="Java version " $0 " is less than 1.6"} $2=6{split($3,array,"_")} END{print msg; if(array[2]<"20") {print "Java 1.6 update is below the required Update 20"}}'

rm -r $APPROOT/logs/$1*.*

JAVA_LIB=$APPROOT/lib
CLASSPATH=$APPROOT/conf:.:$JAVA_LIB
for i in $JAVA_LIB/*.jar ; do
    CLASSPATH=$CLASSPATH:$i
done


MAIN_CLASS=com.hp.snap.evaluation.imdb.business.cases.$1

echo Start case $1 $2 $3 $4 $5 $6

$JAVA_HOME/bin/java $OPTS -cp $CLASSPATH $MAIN_CLASS $2 $3 $4 $5 $6 2>&1 | tee $APPROOT/logs/${APP_NAME}.$NOW.log 


#================================================
# Run Case Script for HP SNAP IMDB evaluation
#                         BOW(bow@hp.com)
#================================================
