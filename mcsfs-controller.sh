#!/usr/bin/env bash
    
gceNAME=mcsfs-gce
gceZONE=us-central1-a
gceNODES=4

function mcsfs-gce-tear-down(){
 	echo "Tearing down cluster..."
	gcloud container clusters delete $gceNAME --zone $gceZONE
	echo
	echo "Done."
}

function mcsfs-gce-deploy(){
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
	echo "Name: $gceNAME"
	echo "Zone: $gceZONE"
	echo "Number of nodes: $gceNODES"
	echo
	gcloud container clusters create $gceNAME --zone $gceZONE --num-nodes $gceNODES
	echo

	# Verify kubectl configuration.
	which kubectl > /dev/null 2>&1
	if [[ $?  == '0' ]]
	then
		gcloud container clusters get-credentials $gceNAME	
		echo "Project cluster:"
		echo
		gcloud container clusters list
		read -p  "Continue? (y/n): " -n 1 -r
		echo
		if [[ ! $REPLY =~ ^[Yy]$ ]]
		then
			mcsfs-gce-tear-down
		        return	
		fi
		provision_cluster
	else
		echo "kubectl not found."
	fi
}

function mcsfs-aws-tear-down(){
	export KUBERNETES_PROVIDER=aws
	cluster/kube-down.sh
}

function mcsfs-aws-deploy(){
	export KUBERNETES_PROVIDER=aws
	echo "Creating cluster..."
	curl -sS https://get.k8s.io | bash
	echo
	provision_cluster
}

provision_cluster(){
	echo
	echo "Cluster info"
	kubectl cluster-info
	echo
	if [[ ! $? == '0' ]]
	then
		echo "ERROR could not configure kubectl"
	else
		# Create deployment and service.
		kubectl create -f mcsfs-definition.yaml
		echo
		echo "Service: "
		echo
		kubectl get services
		echo
		echo "Deployment: "
		echo
		kubectl get deployments
	fi
}
