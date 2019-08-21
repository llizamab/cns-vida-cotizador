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
	}
	container('docker') {
	  // sonar scaner
	  configFileProvider([configFile(fileId: 'sonar-properties', variable: 'FILE')]) {
           sh "cat $FILE > sonar-runner.properties"
          }

	  sh """
	  docker run -v \$(pwd):/usr/src \
            -v \$(pwd)/sonar-runner.properties:/usr/lib/sonar-scanner/conf/sonar-scanner.properties \
	     newtmitch/sonar-scanner sonar-scanner
	  """

          sh "ls -ltr build/libs"
          sh "docker build . -t $DOCKER_REGISTRY/$ORG/$APP_NAME:$PREVIEW_VERSION"
	  sh "wget https://s3-us-west-2.amazonaws.com/cdn.apside.cl/ca.crt"
	  sh "mkdir -p /etc/docker/certs.d/harbor.apside.info/"
	  sh "mv ca.crt /etc/docker/certs.d/harbor.apside.info/ca.crt"

	  withCredentials([usernamePassword(credentialsId: 'harbor-admin', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
            sh "docker login --username=$USER --password=$PASS $DOCKER_REGISTRY"
          }

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
  post {
    success {
      echo 'Todo OK!'
    }
    failure {
       echo 'Notificar que algo ha fallado'
    }
  }
}
