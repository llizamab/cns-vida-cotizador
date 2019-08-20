pipeline {
  agent {
    label "jenkins-gradle"
  }
  environment {
    ORG = 'cns-vida-cotizador'
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
          sh "gradle build"
	  sh "ls -R"
	}
	container('docker') {
          sh "ls -ltr build/libs"
          sh "docker build . -t $DOCKER_REGISTRY/$ORG/$APP_NAME:$PREVIEW_VERSION"
	  sh "docker push $DOCKER_REGISTRY/$ORG/$APP_NAME:$PREVIEW_VERSION"
	}
      }
    }
    stage('Deploy QA') {
      when {
        branch 'develop'
      }
      steps {
	container('gradle') {
          sh "echo .. deploying qa"
          sh "gradle build"
	}
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
