package edu.ucsd.dbmi.perank.index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * count the fingerprint get from the MixFingerNoHD
 * @author nansu
 *
 */
public class IPPVCounter extends Configured implements Tool {

	public static int Total;

	public IPPVCounter(int Total) {
		this.Total = Total;
	}

	public static class CounterMap extends
			Mapper<LongWritable, Text, Text, IntWritable> {

		// key: StartNodes<iterationClass> value:Endnodes;iterationNum; ��; W
		// key: StartNodes<iterationClass> value:Endnodes;iterationNum; ��; S

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			Text word_key = new Text();
			String line = value.toString();
		
				word_key.set(line);
				context.write(word_key, new IntWritable(1));


		}

	}

	public static class CounterReduce extends
			Reducer<Text, IntWritable, Text, DoubleWritable> {

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int TOTAL=Integer.valueOf(context.getConfiguration().get("Total"));
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			double value = (double)sum / TOTAL;
			context.write(key, new DoubleWritable(value));
		}
	}

	public int run(String[] args) throws Exception {

		boolean success = false;
		boolean ini = true;
		int iterations = 0;
		Job job = new Job(getConf());
		job.setJarByClass(IPPVCounter.class);
		job.setJobName("PERank node finger print counter");
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.getConfiguration().set("Total", String.valueOf(Total));
		job.setMapperClass(CounterMap.class);
		job.setReducerClass(CounterReduce.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
//		getConf().set("mapred.compress.map.output", "true");
//		getConf().set("mapred.output.compression.type", "BLOCK"); 
//		getConf().set("mapred.map.output.compression.codec", "org.apache.hadoop.io.compress.GzipCodec");
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
		FileOutputFormat.setCompressOutput(job, true);
		success = job.waitForCompletion(true);
		
		return success ? 0 : 1;

	}

	public static void main(String[] args) throws Exception {
		
		int seeds=Integer.valueOf(args[0]);
		Long startTime=System.currentTimeMillis();
		int ret = ToolRunner.run(new IPPVCounter(seeds), args);
		Long endTime=System.currentTimeMillis();
		System.out.println("time spend: "+(endTime-startTime)+"ms");
		System.exit(ret);

	}

}
