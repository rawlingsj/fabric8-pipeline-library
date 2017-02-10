#!/usr/bin/groovy

def call(Map parameters = [:], body) {

    def defaultLabel = "jhipster.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_')
    def label = parameters.get('label', defaultLabel)

    def jhipsterImage = parameters.get('jhipsterImage', 'rawlingsj/jhipster-builder:dev2')
    def inheritFrom = parameters.get('inheritFrom', 'base')

    def flow = new io.fabric8.Fabric8Commands()

    if (flow.isOpenShift()) {
        podTemplate(label: label, serviceAccount: 'jenkins', inheritFrom: "${inheritFrom}",
                containers: [
                        [name   : 'jhipster', image: "${jhipsterImage}", command: 'cat', ttyEnabled: true,
                         envVars: [[key: 'TERM', value: 'dumb'],[key: 'MAVEN_OPTS', value: '-Duser.home=/root/']]]],
                volumes: [secretVolume(secretName: 'jenkins-maven-settings', mountPath: '/root/.m2'),
                          persistentVolumeClaim(claimName: 'jenkins-mvn-local-repo', mountPath: '/root/.mvnrepository')]) {
            body()
        }
    } else {
        podTemplate(label: label, serviceAccount: 'jenkins', inheritFrom: "${inheritFrom}",
                containers: [
                        [name   : 'jhipster', image: "${jhipsterImage}", command: 'cat', ttyEnabled: true, privileged: true,
                         envVars: [[key: 'TERM', value: 'dumb'],[key: 'MAVEN_OPTS', value: '-Duser.home=/root/']]]],
                volumes: [secretVolume(secretName: 'jenkins-maven-settings', mountPath: '/root/.m2'),
                          persistentVolumeClaim(claimName: 'jenkins-mvn-local-repo', mountPath: '/root/.mvnrepository'),
                          hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')]) {
            body()
        }
    }

}
