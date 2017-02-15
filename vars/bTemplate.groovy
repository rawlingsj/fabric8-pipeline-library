#!/usr/bin/groovy
def call(Map parameters = [:], body) {

    def defaultLabel = "b.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_')
    def label = parameters.get('label', defaultLabel)
    def inheritFrom = parameters.get('inheritFrom', 'base')

      podTemplate(label: label, inheritFrom: "${inheritFrom}",
            containers: [[name: 'b', image: "rawlingsj/b-image:0.1", command: 'cat', ttyEnabled: true]],
              envVars: [[key: 'DUMMY', value: 'TEST']]) {

            body()
    }
}
