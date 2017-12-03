FROM tomcat:8.0-jre8
MAINTAINER Sean Chitwood <darkmane@gmail.com>

ENV JPDA_ADDRESS 0.0.0.0:8000
WORKDIR /usr/local/tomcat
RUN rm -rf /usr/local/tomcat/webapps/ROOT
ADD target/classictraveller-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
CMD /usr/local/tomcat/bin/catalina.sh jpda run

