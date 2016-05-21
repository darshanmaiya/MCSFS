package mcsfs.queue;

import java.util.LinkedList;
import java.util.Queue;

import mcsfs.store.Store;
import mcsfs.store.gcs.GCStore;
import mcsfs.store.s3.S3Store;
import mcsfs.store.azure.AzureStore;

public class JobQueue {
	public static Queue<Job> jobs = new LinkedList<Job>();

	public static void startQueueProcessing() {

		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		                JobQueue.runJobs();
		            }
		        },
		        60000 
		);
	}

	public static void runJobs() {
		Store cloudStore;
		Queue<Job> newJobs = new LinkedList<Job>();
		for(Job job : jobs) {
			try {
				if(job.getProvider().equals(GCStore.class.toString()))
					cloudStore = new GCStore();
				else if(job.getProvider().equals(S3Store.class.toString()))
					cloudStore = new S3Store();
				else
					cloudStore = new AzureStore();

				if(job.getOperation() == QueueOperations.REMOVE) {
					cloudStore.remove(job.getAccessKey());
				} else if (job.getOperation() == QueueOperations.UPLOAD) {
					cloudStore.store(job.getFile());
					
					job.getFile().delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
				job.setRetries(job.getRetries() + 1);

				if(job.getRetries() < 50)
					newJobs.add(job);
			} finally {
				jobs = newJobs;
			}
		}
	}

	public static void addJob(Job job) {
		jobs.add(job);
	}
}
