#!/bin/bash

# test the hadoop cluster by running wordcount

# create input files
#mkdir input
#echo "Hello Docker" >input/file2.txt
#echo "Hello Hadoop" >input/file1.txt



# create input directory on HDFS
#hadoop fd -rm -r input
#hadoop fs -mkdir -p input
hdfs dfs -rm -r /input
hdfs dfs -mkdir /input
# put input files to HDFS
hdfs dfs -put /root/jar/input/* /input

# run wordcount
hadoop jar /root/jar/291p_3.jar BiGram /input /output

# print the output of wordcount
echo -e "\ntemp2----"
hdfs dfs -cat /tmp/temp2/part-r-00000
echo -e "\nbigram output:------------"
hdfs dfs -cat /output/part-r-00000


echo -e "\npretty print-----------"
rm /root/jar/part-r-00000
hadoop fs -get /output/part-r-00000 /root/jar/
python /root/jar/display.py
