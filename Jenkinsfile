pipeline {
  agent {
    docker {
      image 'yogramming/devops-agent:v1'
      args '--user root -v /var/run/docker.sock:/var/run/docker.sock --add-host=host.docker.internal:host-gateway'
    }
  }

  environment {
    DOCKER_IMAGE = "yogramming/devpulse:${BUILD_NUMBER}"
    SONAR_URL = "http://host.docker.internal:9000"
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        sh './mvnw clean verify'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withCredentials([string(credentialsId: 'sonarqube', variable: 'SONAR_TOKEN')]) {
          sh '''
          ./mvnw sonar:sonar \
            -Dsonar.projectKey=devpulse \
            -Dsonar.host.url=$SONAR_URL \
            -Dsonar.login=$SONAR_TOKEN
          '''
        }
      }
    }

    stage('Build Docker Image') {
      steps {
        sh 'docker build -t ${DOCKER_IMAGE} .'
      }
    }

    stage('Push Docker Image') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
          sh '''
          echo $PASS | docker login -u $USER --password-stdin
          docker push ${DOCKER_IMAGE}
          '''
        }
      }
    }

    stage('Update K8s Manifest') {
      steps {
        dir("${WORKSPACE}") {
          withCredentials([usernamePassword(credentialsId: 'github-creds', usernameVariable: 'GIT_USER', passwordVariable: 'GITHUB_TOKEN')]) {
            sh '''
            git config user.email "yogramming@devpulse.com"
            git config user.name "yogramming"

            sed -i "s|yogramming/devpulse:.*|yogramming/devpulse:${BUILD_NUMBER}|g" k8s-manifests/deployment.yml

            git add k8s-manifests/deployment.yml
            git commit -m "Update image tag to ${BUILD_NUMBER}" || echo "No changes to commit"

            git push https://${GIT_USER}:${GITHUB_TOKEN}@github.com/yogramming/devpulse.git HEAD:main
            '''
          }
        }
      }
    }

  }
}
