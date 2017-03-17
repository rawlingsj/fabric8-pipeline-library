@Grab('io.fabric8.kubernetes-api:2.2.205') import io.fabric8.kubernetes.api.pipelines.Pipeline
class Lib {
  static boolean isCI() {

        try {
            def environment = new JobEnvironment()
            environment.setJobName(env.JOB_NAME)
            environment.setBranchName(io.fabric8.Utils.getBranch())
            environment.setGitUrl('gitUrl')

            def kubernetesClient = new DefaultKubernetesClient()

            String namespace = kubernetesClient.getNamespace()
            if (Strings.isNullOrBlank(namespace)) {
                namespace = KubernetesHelper.defaultNamespace()
            }
            def pipeline = Pipelines.getPipeline(kubernetesClient, namespace, environment)
            echo("Found pipeline for job: " + pipeline.getJobName() + " of kind: " + pipeline.getKind())
        } catch (err) {
            echo "ERROR: ${err}"
            error "${err}"
        }
  }
}