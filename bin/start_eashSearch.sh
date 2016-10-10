#! /bin/sh
JAVA_HOME=/usr/local/java
export JAVA_HOME

SCRIPT="$0"
while [ -h "$SCRIPT" ] ; do
  ls=`ls -ld "$SCRIPT"`
  # Drop everything prior to ->
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    SCRIPT="$link"
  else
    SCRIPT=`dirname "$SCRIPT"`/"$link"
  fi
done

SERVER_HOME=`dirname "$SCRIPT"`
SERVER_HOME=`cd "$SERVER_HOME"; pwd`
export SERVER_HOME

LIBDIR=$SERVER_HOME/lib
export LIBDIR

CLASSPATH=${CLASSPATH}:${SERVER_HOME}/conf
for lib in ${LIBDIR}/*.jar
do
 CLASSPATH=$CLASSPATH:$lib
 export CLASSPATH
done

PATH=${JAVA_HOME}/bin:${PATH}
echo $PATH
export PATH

java=$JAVA_HOME/bin/java

#java opts
#JAVA_OPTS="-Xms1024m -Xmx2048m -XX:PermSize=256m -XX:MaxPermSize=512m"
JAVA_OPTS="-server -Xms2048m -Xmx2048m \
-XX:PermSize=1024m \
-XX:MaxPermSize=1024m \
-XX:+UseConcMarkSweepGC  \
-XX:+CMSClassUnloadingEnabled \
-XX:+UseParNewGC \
-XX:ParallelGCThreads=4 \
-XX:+PrintGCDetails \
-Xloggc:/data/logs/easySearch/jvm_gc.log"


export JAVA_OPTS

echo "JAVA_HOME  :$JAVA_HOME"
echo "SERVER_HOME:$SERVER_HOME"
echo "CLASSPATH  :$CLASSPATH"

exec $java $JAVA_OPTS com.mob.easySearch.EasySearchBoot $* &
