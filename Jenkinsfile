pipeline {
  agent any
  tools {
    maven 'maven-3.8'
    jdk 'openjdk16'
  }
  parameters {
    string(defaultValue: "main", description: 'Set git branch', name: 'BRANCH')
  }
  stages {
    stage('Preparation') {
      steps{
        sh 'mvn --version'
      }
    }
    stage('Build') {
      steps{
        sh 'mvn -B -C -q clean install'
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
        }
      }
    }
    stage('Deploy') {
      when {
        expression {
          return params.BRANCH == 'main'
        }
      }
      steps{
        sh 'mvn -B -C -q deploy -Dskip.unit.tests=true'
      }
    }
  }
  post {
    always {
      cleanWs notFailBuild: true
    }
  }
}