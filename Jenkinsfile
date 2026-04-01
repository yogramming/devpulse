pipeline {
  agent {
    docker {
      image 'yogramming/devops-agent:v1'
      args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
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
        withCredentials([usernamePassword(credentialsId: 'docker-cred', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
          sh '''
          echo $PASS | docker login -u $USER --password-stdin
          docker push ${DOCKER_IMAGE}
          '''
        }
      }
    }

    stage('Update K8s Manifest') {
      steps {
        withCredentials([string(credentialsId: 'github-cred', variable: 'GITHUB_TOKEN')]) {
          sh '''
          git config user.email "you@example.com"
          git config user.name "yourname"

          sed -i "s|yogramming/devpulse:.*|yogramming/devpulse:${BUILD_NUMBER}|g" k8s-manifests/deployment.yml

          git add k8s-manifests/deployment.yml
          git commit -m "Update image tag to ${BUILD_NUMBER}"

          git push https://${GITHUB_TOKEN}@github.com/yogramming/devpulse.git HEAD:main
          '''
        }
      }
    }

  }
}
