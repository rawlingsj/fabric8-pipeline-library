#!/usr/bin/groovy
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def flow = new io.fabric8.Fabric8Commands()

    def expose = config.exposeApp ?: 'true'
    def kind = flow.isOpenShift() ? 'DeploymentConfig' : 'Deployment'

    def list = """
    {
      "apiVersion" : "v1",
      "kind" : "List",
      "labels" : { },
      "metadata" : {
        "annotations" : {
          "description" : "${config.label} example",
          "fabric8.${env.JOB_NAME}/iconUrl" : "${config.icon}"
        },
        "labels" : { },
        "name" : "${env.JOB_NAME}"
      },
      "objects" : [{
        "kind": "Service",
        "apiVersion": "v1",
        "metadata": {
            "name": "${env.JOB_NAME}",
            "labels": {
                "component": "${env.JOB_NAME}",
                "container": "${config.label}",
                "group": "quickstarts",
                "project": "${env.JOB_NAME}",
                "provider": "fabric8",
                "expose": "true",
                "version": "${config.version}"
            },
            "annotations": {
                "fabric8.${env.JOB_NAME}/iconUrl" : "${config.icon}",
                "prometheus.io/port": "${config.port}",
                "prometheus.io/scheme": "http",
                "prometheus.io/scrape": "true"
            }
        },
        "spec": {
            "ports": [
                {
                    "protocol": "TCP",
                    "port": 80,
                    "targetPort": ${config.port}
                }
            ],
            "selector": {
                "component": "${env.JOB_NAME}",
                "container": "${config.label}",
                "group": "quickstarts",
                "project": "${env.JOB_NAME}",
                "provider": "fabric8",
                "version": "${config.version}"
            },
            "type": "LoadBalancer",
            "sessionAffinity": "None"
        }
    },
    {
        "kind": "${kind}",
        "apiVersion": "v1",
        "metadata": {
            "name": "${env.JOB_NAME}",
            "generation": 1,
            "creationTimestamp": null,
            "labels": {
                "component": "${env.JOB_NAME}",
                "container": "${config.label}",
                "group": "quickstarts",
                "project": "${env.JOB_NAME}",
                "provider": "fabric8",
                "expose": "${expose}",
                "version": "${config.version}"
            },
            "annotations": {
                "fabric8.${env.JOB_NAME}/iconUrl" : "${config.icon}"
            }
        },
        "spec": {
            "replicas": 1,
            "selector": {
                "component": "${env.JOB_NAME}",
                "container": "${config.label}",
                "group": "quickstarts",
                "project": "${env.JOB_NAME}",
                "provider": "fabric8",
                "version": "${config.version}"
            },
            "template": {
                "metadata": {
                    "labels": {
                        "component": "${env.JOB_NAME}",
                        "container": "${config.label}",
                        "group": "quickstarts",
                        "project": "${env.JOB_NAME}",
                        "provider": "fabric8",
                        "version": "${config.version}"
                    }
                },
                "spec": {
                    "containers": [
                        {
                            "name": "${env.JOB_NAME}",
                            "image": "${env.FABRIC8_DOCKER_REGISTRY_SERVICE_HOST}:${env.FABRIC8_DOCKER_REGISTRY_SERVICE_PORT}/${env.KUBERNETES_NAMESPACE}/${env.JOB_NAME}:${config.version}",
                            "ports": [
                                {
                                    "name": "web",
                                    "containerPort": ${config.port},
                                    "protocol": "TCP"
                                }
                            ],
                            "env": [
                                {
                                    "name": "KUBERNETES_NAMESPACE",
                                    "valueFrom": {
                                        "fieldRef": {
                                            "apiVersion": "v1",
                                            "fieldPath": "metadata.namespace"
                                        }
                                    }
                                }
                            ],
                            "imagePullPolicy": "IfNotPresent"
                        }
                    ],
                    "restartPolicy": "Always",
                    "terminationGracePeriodSeconds": 10,
                    "dnsPolicy": "ClusterFirst"
                }
            }
        }
    }]}
    """

    echo 'using Kubernetes resources:\n' + list
    return list

  }
