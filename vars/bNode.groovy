#!/usr/bin/groovy
def call(Map parameters = [:], body) {

    def defaultLabel = "b.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_')
    def label = parameters.get('label', defaultLabel)

    bTemplate(parameters) {
        node(label) {
            body()
        }
    }
}
