#!/usr/bin/groovy
def call(Map parameters = [:], body) {

    def defaultLabel = "c.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_')
    def label = parameters.get('label', defaultLabel)
    def inheritFrom = parameters.get('inheritFrom', 'base')

      podTemplate(label: label, inheritFrom: "${inheritFrom}",
            containers: [[name: 'c', image: "rawlingsj/test:0.1", command: 'cat', ttyEnabled: true]]) {

            body()
    }
}
