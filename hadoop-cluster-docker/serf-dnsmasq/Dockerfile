# Creates a base ubuntu image with serf and dnsmasq
FROM ubuntu:15.04

MAINTAINER tuan <tuan@gmail.com>

RUN apt-get update && \
apt-get install -y unzip curl dnsmasq

# dnsmasq configuration
ADD dnsmasq/* /etc/

# install serf
RUN curl -Lso serf.zip https://releases.hashicorp.com/serf/0.7.0/serf_0.7.0_linux_amd64.zip && \
unzip serf.zip -d /bin && \
rm serf.zip

# configure serf
ENV SERF_CONFIG_DIR /etc/serf
ADD serf/* $SERF_CONFIG_DIR/
ADD handlers $SERF_CONFIG_DIR/handlers
RUN chmod +x  $SERF_CONFIG_DIR/event-router.sh $SERF_CONFIG_DIR/start-serf-agent.sh
