pipeline {
  agent {
    label "jenkins-gradle"
  }
  environment {
    ORG = 'cns'
    APP_NAME = 'cns-vida-cotizador'
  }
  stages {
    stage('Pull Request') {
      when {
        branch 'PR-*'
      }
      environment {
        PREVIEW_VERSION = "0.0.0-SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER"
      }
      steps {
        container('gradle') {
          sh "echo 'on PR-*...' - version $PREVIEW_VERSION"
          sh "graddle build"
        }
      }
    }
    stage('Deploy QA') {
      when {
        branch 'develop'
      }
      steps {
        sh "echo .. deploying qa"
      }
    }
    stage('Build Release') {
      when {
        branch 'master'
      }
      steps {
        container('gradle') {
          // ensure we're not on a detached head
          sh "git checkout master"
          sh "git config --global credential.helper store"
          
		  sh "echo 'on master...'"
        }
      }
    }
  }
}
