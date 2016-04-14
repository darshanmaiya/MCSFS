FROM ubuntu

# install dependencies
RUN apt-get update && \
    apt-get install -yy \
    software-properties-common \
    g++ \
    git \
    wget \
    python-pip \
    tomcat7 \
    default-jdk \ 
    maven

# install oracle java 8
RUN add-apt-repository ppa:webupd8team/java && \
    apt-get update && \
    echo debconf shared/accepted-oracle-license-v1-1 select true | sudo debconf-set-selections && \
    echo debconf shared/accepted-oracle-license-v1-1 seen true | sudo debconf-set-selections && \
    apt-get install -yy oracle-java8-installer && \
    apt-get install -yy oracle-java8-set-default

# get source 
RUN wget https://github.com/darshanmaiya/MCSFS/archive/master.tar.gz && \
    tar xf master.tar.gz && \
    cd MCSFS-master && \
    mvn clean install && \
    cd target

CMD java -jar MCSFS-master/target/MCSFS.jar


EXPOSE 8080
