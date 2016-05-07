#!/usr/bin/env bash
    
gcsNAME=mcsfs-gce
gcsZONE=us-central1-a
gcsNODES=1

function mcsfs-gcs-tear-down(){
 	echo "Tearing down cluster..."
	gcloud container clusters delete $gcsNAME --zone $gcsZONE
	echo
	echo "Done."
}

function mcsfs-gcs-deploy(){
	# Deploy kubernetes cluster on Google Container Engine.

	# Verify gcloud configuration
	which gcloud > /dev/null 2>&1
	if [[ $?  == '0' ]]
	then
		echo "Verify gcloud config"
		echo
		gcloud config list
		read -p  "Continue? (y/n): " -n 1 -r
		echo
		if [[ ! $REPLY =~ ^[Yy]$ ]]
		then
			echo "Done."
		fi
	else
		echo "gcloud not found." 
	fi

	# Create cluster.
	echo "Creating cluster..."
	echo "Name: $gcsNAME"
	echo "Zone: $gcsZONE"
	echo "Number of nodes: $gcsNODES"
	echo
	gcloud container clusters create $gcsNAME --zone $gcsZONE --num-nodes $gcsNODES
	echo

	# Verify kubectl configuration.
	which kubectl > /dev/null 2>&1
	if [[ $?  == '0' ]]
	then
		gcloud container clusters get-credentials $gcsNAME	
		echo "Project cluster:"
		echo
		gcloud container clusters list
		read -p  "Continue? (y/n): " -n 1 -r
		echo
		if [[ ! $REPLY =~ ^[Yy]$ ]]
		then
			mcsfs-gcs-tear-down
		        return	
		fi
		kubectl cluster-info > /dev/null 2>&1
		if [[ ! $? == '0' ]]
			echo "ERROR could not configure kubectl"
		else
			# TODO
			# Create deployment and service here.
		fi
	else
		echo "kubectl not found."
	fi
}
