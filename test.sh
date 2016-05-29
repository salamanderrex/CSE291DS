hdfs dfs -mkdir /input
hdfs dfs -put /root/docker/input/input.txt /input
hadoop jar /root/docker/project3/out/artifacts/project3_jar/project3.jar  BiGram /input /output
hdfs dfs -cat /output/part-r-00000
