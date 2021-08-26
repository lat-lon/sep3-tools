pipeline {
  agent any
  tools {
    maven 'maven-3.8'
    jdk 'openjdk16'
  }
  parameters {
    string(defaultValue: "master", description: 'Set git branch', name: 'BRANCH')
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
          return params.BRANCH == 'master'
        }
      }
      steps{
        sh 'mvn deploy -Dskip.unit.tests=true'
      }
    }
  }
  post {
    always {
      cleanWs notFailBuild: true
    }
  }
}