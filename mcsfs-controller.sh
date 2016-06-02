#!/usr/bin/env bash

#     Copyright (C) 2016 DropTheBox (Aviral Takkar, Darshan Maiya, Wei-Tsung Lin)

#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.

#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.

#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
gceNAME=mcsfs-gce
gceZONE=us-central1-a
gceNODES=1

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
