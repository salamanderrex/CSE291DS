/**
 * Created by qingyu on 5/27/16.
 */


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.iterators.ListIteratorWrapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.map.InverseMapper;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class BiGram extends Configured implements Tool {
    // ngram count


    public static class Map1 extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        List ls = new ArrayList();

        @SuppressWarnings("unchecked")
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            StringTokenizer dt = new StringTokenizer(value.toString(), " ");

            while (dt.hasMoreTokens()) {
                ls.add(dt.nextToken());
            }

        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            //int va = Integer.parseInt(context.getConfiguration().get("grams"));
            int va = 2; // bigram!!
            StringBuffer str = new StringBuffer("");
            for (int i = 0; i < ls.size() - va; i++) {
                for (int j = 0; j < va; j++) {
                    if (j != 0)
                        str = str.append(" ");
                    str = str.append(ls.get(i + j));
                }
                word.set(str.toString());
                context.write(word, one);
                context.write(new Text("THIS_IS TOTAL_COUNT"), one);
                str.setLength(0);
            }
        }
    }


    public  static class Reduce1 extends Reducer<Text, IntWritable, Text, IntWritable> {
        int count = 0;
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            count = count + sum;

            context.write(key, new IntWritable(sum));
        }



    }


    //change (word,count) -> (count->word)
    // ngram count
    public static class Map2 extends Mapper<LongWritable, Text, IntWritable, Text> {
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String line = value.toString();
            StringTokenizer stringTokenizer = new StringTokenizer(line);
            {
                int number = 999;
                String word = "empty!!!!";

                String str0 = "";
                if (stringTokenizer.hasMoreTokens()) {
                    //Bigram
                    str0 = stringTokenizer.nextToken();

                }

                if (stringTokenizer.hasMoreElements()) {
                    String str1 = stringTokenizer.nextToken();

                    word = str0.trim() + " " + str1.trim();
                    String str2 = stringTokenizer.nextToken();
                    number = Integer.parseInt(str2.trim());
                }

                //collector.collect(new IntWritable(number), new Text(word));
                context.write(new IntWritable(number), new Text(word));
            }


        }


    }

    public static class Reduce2 extends Reducer<IntWritable, Text, IntWritable, Text> {

        public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text value : values) {
                context.write(key, value);
            }

        }
    }


    public static class MyKeyComparator extends WritableComparator {
        protected MyKeyComparator() {
            super(IntWritable.class, true);
        }

        @SuppressWarnings("rawtypes")
        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            IntWritable key1 = (IntWritable) w1;
            IntWritable key2 = (IntWritable) w2;
            return -1 * key1.compareTo(key2);
        }
    }
/*
    public static void main(String[] args) throws Exception {
        Job job2 = new Job(conf, "reverKeyPair");
        job2.setJarByClass(BiGram.class);
        job2.setOutputKeyClass(IntWritable.class);
        job2.setOutputValueClass(Text.class);
        //job2.setMapOutputKeyClass(IntWritable.class);
        //job2.setMapOutputValueClass(Text.class);
        job2.setInputFormatClass(TextInputFormat.class);
        job2.setOutputFormatClass(TextOutputFormat.class);
        //reserve sort
        job2.setSortComparatorClass(MyKeyComparator.class);
        job2.setMapperClass(Map2.class);
        job2.setReducerClass(Reduce2.class);
        TextInputFormat.addInputPath(job2, new Path("/tmp/temp1/part-r-00000"));
        TextOutputFormat.setOutputPath(job2, new Path(args[1]));
        job2.submit();
        job2.waitForCompletion(true);
        //job.waitForCompletion(true);
        System.out.println("Done.");
    }
*/

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new Configuration(), new BiGram(), args);
        System.exit(exitCode);
    }


    public int run(String[] args) throws Exception {

        FileUtil.fullyDelete(new File(args[1]));
        FileUtil.fullyDelete(new File("/tmp"));

        Configuration conf = new Configuration();


        Path output = new Path("/tmp/temp1");
        FileSystem hdfs = FileSystem.get(conf);

        // delete existing directory
        if (hdfs.exists(output)) {
            hdfs.delete(output, true);
        }

        Path output2 = new Path(args[1]);

        // delete existing directory
        if (hdfs.exists(output2)) {
            hdfs.delete(output2, true);
        }
        //conf.set("grams", args[2]);
        Job job = new Job(conf, "wordcount");
        // job.setNumReduceTasks(0);
        job.setJarByClass(BiGram.class);

        //job.setMapOutputKeyClass(Text.class);
        //job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        job.setMapperClass(Map1.class);
        job.setCombinerClass(Reduce1.class);
        job.setReducerClass(Reduce1.class);
        TextInputFormat.addInputPath(job, new Path(args[0]));
        TextOutputFormat.setOutputPath(job, new Path("/tmp/temp1"));

        //TextOutputFormat.setOutputPath(job, new Path(args[1]));


        job.submit();
        job.waitForCompletion(true);
        System.out.println("job 1 done");



        Configuration conf2 = new Configuration();
        Job job2 = new Job(conf2, "reverKeyPair");
        job2.setJarByClass(BiGram.class);

        job2.setOutputKeyClass(IntWritable.class);
        job2.setOutputValueClass(Text.class);
        //job2.setMapOutputKeyClass(IntWritable.class);
        //job2.setMapOutputValueClass(Text.class);
        job2.setInputFormatClass(TextInputFormat.class);
        job2.setOutputFormatClass(TextOutputFormat.class);

        //reserve sort
        job2.setSortComparatorClass(MyKeyComparator.class);

        job2.setMapperClass(Map2.class);
        job2.setReducerClass(Reduce2.class);
        //job2.setReducerClass(Reducer.class);
        job2.setNumReduceTasks(1);
        TextInputFormat.addInputPath(job2, new Path("/tmp/temp1/part-r-00000"));
        TextOutputFormat.setOutputPath(job2, new Path(args[1]));

        job2.submit();
        job2.waitForCompletion(true);
        System.out.println("done!!!!!!!!!!!!!");
        return 0;

    }


}
