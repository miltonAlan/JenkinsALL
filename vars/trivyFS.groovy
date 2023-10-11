

def call (dir='.', severity='HIGH,CRITICAL', timeoutMinutes=20) {
  label 'Trivy'
  timeout (time: timeoutMinutes, unit: 'MINUTES') {
    withEnv (["DIR=$dir", "SEVERITY=$severity"]) {
      // Trivy won't show anything below --severity so need to run one without severity to get the full information
      echo "Trivy scanning dir '$DIR' - informational only to see all issues"
      trivy("fs '$DIR' --no-progress --timeout '${timeoutMinutes}m'")

      echo "Trivy generate report for dir '$DIR'"
      sh('mkdir -p reports')
      trivy("fs '$DIR' --no-progress --timeout '${timeoutMinutes}m' --format template --template '@trivy-html.tpl' -o 'reports/trivy-fs.html'")
      echo "Publish HTML report to Jenkins"
      publishHTML target : [
        allowMissing: true,
        alwaysLinkToLastBuild: true,
        keepAll: true,
        reportDir: 'reports',
        reportFiles: 'trivy-fs.html',
        reportName: 'Trivy FS Scan',
        reportTitles: 'Trivy FS Scan'
      ]

      echo "Trivy scanning dir '$DIR' for severity '$SEVERITY' vulnerabilities only - will fail if any are detected"
      trivy("fs '$DIR' --no-progress --timeout '${timeoutMinutes}m' --exit-code 1 --severity '$SEVERITY'")
    }
  }
}
