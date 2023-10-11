
// ========================================================================== //
//                                   T F S e c
// ========================================================================== //

// Terraform code security scanner

def call (timeoutMinutes=10) {
  label 'tfsec'
  container('tfsec') {
    timeout (time: timeoutMinutes, unit: 'MINUTES') {
      //dir ("components/${COMPONENT}") {
      ansiColor('xterm') {
        // aquasec/tfsec image is based on Alpine, doesn't have bash
        //sh '''#!/usr/bin/env bash -euxo pipefail
        //sh '''#!/bin/sh -eux
        // use --no-color if not using ansicolor plugin
        sh 'tfsec --update'
        sh 'tfsec --version'
        sh' tfsec --run-statistics'  // nice summary table
        sh 'tfsec --soft-fail'       // don't error
        sh 'tfsec'                   // full details and error out if issues found
      }
    }
  }
}
