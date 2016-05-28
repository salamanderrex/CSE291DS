/**
 * Created by qingyu on 5/27/16.
 */


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class BiGram {
    // ngram count
    public static class Map1 extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        public static int cnt = 0;
        List ls = new ArrayList();

        @SuppressWarnings("unchecked")
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            StringTokenizer dt = new StringTokenizer(value.toString(), " ");

            while (dt.hasMoreTokens()) {
                ls.add(dt.nextToken());
                cnt++;
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
                str.setLength(0);
            }
        }
    }
    public static class Combiner1 extends Reducer<Text,IntWritable,Text,IntWritable>
    {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException
        {
            int sum = 0;
            for (IntWritable val : values)
            {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    public static class Reduce1 extends Reducer<Text, IntWritable, IntWritable, Text> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            //context.write(key, new IntWritable(sum));
            context.write(new IntWritable(sum), key);
        }


    }

    //change (word,count) -> (count->word)
    // ngram count
    /*
    public static class Map2 extends Mapper<LongWritable, Text, Text, IntWritable> {
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String line = value.toString();
            StringTokenizer stringTokenizer = new StringTokenizer(line);
            {
                int number = 999;
                String word = "empty!!!!";

                if (stringTokenizer.hasMoreTokens()) {
                    String str0 = stringTokenizer.nextToken();
                    word = str0.trim();
                }

                if (stringTokenizer.hasMoreElements()) {
                    String str1 = stringTokenizer.nextToken();
                    number = Integer.parseInt(str1.trim());
                }

                //collector.collect(new IntWritable(number), new Text(word));
                context.write
            }


        }


        public static class Reduce2 extends Reducer<IntWritable, Text, IntWritable, Text> {

            public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException {
                while ((values.hasNext())) {
                    arg2.collect(key, values.next());
                }
            }

        }
        */

    public static void main(String[] args) throws Exception {
        FileUtil.fullyDelete(new File(args[1]));

        Configuration conf = new Configuration();
        //conf.set("grams", args[2]);
        Job job = new Job(conf, "wordcount");
        // job.setNumReduceTasks(0);
        job.setJarByClass(BiGram.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setMapOutputKeyClass(Text.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        job.setMapperClass(Map1.class);
        job.setCombinerClass(Combiner1.class);
        job.setReducerClass(Reduce1.class);
        TextInputFormat.addInputPath(job, new Path(args[0]));
        //TextOutputFormat.setOutputPath(job, new Path("tmp/temp");
        TextOutputFormat.setOutputPath(job, new Path(args[1]));

            /*
            Configuration conf2 = new Configuration();
            Job job2 = new Job(conf2, "reverKeyPair");
            job2.setJarByClass(Ngram.class);

            job2.setOutputKeyClass(IntWritable.class);
            job2.setOutputValueClass(Text.class);
            job2.setMapOutputKeyClass(IntWritable.class);
            job2.setMapOutputValueClass(Text.class);
            job2.setInputFormatClass(TextInputFormat.class);
            job2.setOutputFormatClass(TextOutputFormat.class);

            job2.setMapperClass(Map2.class);
            job2.setCombinerClass(Reduce2.class);
            job2.setReducerClass(Reduce2.class);
            TextInputFormat.addInputPath(job2, new Path("tmp/temp/part-00000"));
            TextOutputFormat.setOutputPath(job2, new Path(args[1]));


            job.submit();
            if (job.waitForCompletion(true)) {
                System.out.println("job 1 done ");
            }
            job2.submit();
            job2.waitForCompletion(true);
            */
        job.waitForCompletion(true);

        System.out.println("Done.");
        System.out.println("cnt is " + Map1.cnt);
    }


}