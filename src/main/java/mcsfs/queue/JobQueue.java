/*
    Copyright (C) 2016 DropTheBox (Aviral Takkar, Darshan Maiya, Wei-Tsung Lin)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

				if(job.getRetries() < 60)
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
