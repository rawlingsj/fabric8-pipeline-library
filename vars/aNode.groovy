#!/usr/bin/groovy
def call(Map parameters = [:], body) {

    def defaultLabel = "a.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_')
    def label = parameters.get('label', defaultLabel)

    aTemplate(parameters) {
        node(label) {
            body()
        }
    }
}
