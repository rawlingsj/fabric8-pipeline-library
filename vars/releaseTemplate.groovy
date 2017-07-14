#!/usr/bin/groovy
import io.fabric8.Fabric8Commands
def call(Map parameters = [:], body) {
    def flow = new Fabric8Commands()

    def defaultLabel = buildId('release')
    def label = parameters.get('label', defaultLabel)

    def mavenImage = parameters.get('mavenImage', 'fabric8/maven-builder:2.2.297')
    def clientsImage = parameters.get('clientsImage', 'fabric8/builder-clients:0.9')
    def dockerImage = parameters.get('dockerImage', 'docker:1.11')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def jnlpImage = (flow.isOpenShift()) ? 'fabric8/jenkins-slave-base-centos7:0.0.1' : 'jenkinsci/jnlp-slave:2.62'
    def cloud = flow.getCloudConfig()

    podTemplate(cloud: cloud, label: label, inheritFrom: "${inheritFrom}",
            containers: [
                    //[name: 'jnlp', image: "${jnlpImage}", args: '${computer.jnlpmac} ${computer.name}',  workingDir: '/home/jenkins/'],
                    [name: 'maven', image: "${mavenImage}", command: 'cat', ttyEnabled: true,
                     envVars: [[key: 'MAVEN_OPTS', value: '-Duser.home=/home/jenkins/']]],

                    [name   : 'clients', image: "${clientsImage}", command: 'cat', ttyEnabled: true,  workingDir: '/home/jenkins/',
                     envVars: [[key: 'TERM', value: 'dumb']]],

                    [name: 'docker', image: "${dockerImage}", command: 'cat', ttyEnabled: true,  workingDir: '/home/jenkins/',
                     envVars: [[key: 'DOCKER_CONFIG', value: '/home/jenkins/.docker/']]]
            ],
            volumes: [secretVolume(secretName: 'jenkins-maven-settings', mountPath: '/home/jenkins/.m2'),
                      persistentVolumeClaim(claimName: 'jenkins-mvn-local-repo', mountPath: '/home/jenkins/.mvnrepository'),
                      secretVolume(secretName: 'jenkins-docker-cfg', mountPath: '/home/jenkins/.docker'),
                      secretVolume(secretName: 'jenkins-release-gpg', mountPath: '/home/jenkins/.gnupg'),
                      secretVolume(secretName: 'jenkins-hub-api-token', mountPath: '/home/jenkins/.apitoken'),
                      secretVolume(secretName: 'jenkins-ssh-config', mountPath: '/home/jenkins/.ssh'),
                      secretVolume(secretName: 'jenkins-git-ssh', mountPath: '/home/jenkins/.ssh-git'),
                      secretVolume(secretName: 'gke-service-account', mountPath: '/home/jenkins/home/.gke'),
                      hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')],
            envVars: [[key: 'DOCKER_HOST', value: 'unix:/var/run/docker.sock'], [key: 'DOCKER_CONFIG', value: '/home/jenkins/.docker/']]
    ) {

        body(

        )
    }
}
