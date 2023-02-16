pipeline {
   agent any
   stages {
     stage('Build and Test') {
        steps {
            sh 'build here...'
            sh 'run tests here if you like ...'
        }
     }
   }
   post {
      always {
        junit '**/reports/junit/*.xml'
      }
   } 
}
