
def call (imageList=[], severity='HIGH,CRITICAL', timeoutMinutes=30) {
  label 'Trivy'
  List images = []
  if (imageList) {
    images = imageList
  } else {
    images = dockerInferImageTagList()
  }
  timeout (time: timeoutMinutes, unit: 'MINUTES') {
    for (image in images) {
      withEnv (["IMAGE=$image", "SEVERITY=$severity"]) {
        // Trivy won't show anything below --severity so need to run one without severity to get the full information
        echo "Trivy scanning image '$IMAGE' - informational only to see all issues"
        trivy("image --no-progress --timeout '${timeoutMinutes}m' '$IMAGE'")

        String filename = 'trivy-image-' + image.split(':')[0].replace('/', '_') + '.html'
        echo "Trivy generate report for image '$IMAGE'"
        sh('mkdir -p reports')
        trivy("image --no-progress --timeout '${timeoutMinutes}m' '$IMAGE' --format template --template '@trivy-html.tpl' -o 'reports/$filename'")
        echo "Publish HTML report to Jenkins for image '$IMAGE'"
        publishHTML target : [
          allowMissing: true,
          alwaysLinkToLastBuild: true,
          keepAll: true,
          reportDir: 'reports',
          reportFiles: "$filename",
          reportName: "Trivy Image Scan '$IMAGE'",
          reportTitles: "Trivy Image Scan '$IMAGE'"
        ]

        echo "Trivy scanning image '$IMAGE' for severity '$SEVERITY' vulnerabilities only - will fail if any are detected"
        trivy("image --no-progress --timeout '${timeoutMinutes}m' --exit-code 1 --severity '$SEVERITY' '$IMAGE'")
      }
    }
  }
}
