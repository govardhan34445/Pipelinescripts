// Jenkinsfile
@Library('first-shared-lib') _
welcomeJob ‘lambdatest’
pipeline{

  agent any

  stages{

      stage('CheckOutCode'){
        steps{
        git branch: 'development', credentialsId: 'github_cred_id', url: 'github_repo_url'
    
        }
      }
  
      stage('Build'){
        steps{
          sh  "mvn clean package"
        }
      }

      stage('ExecuteSonarQubeReport'){
        steps{
          sh  "mvn clean sonar:sonar"
        }
      }
      
      stage('Build Docker Image'){
        steps{
          sh  " docker build -t imagename:tag"
        }
      }
      
      stage('Push Docker Image to Artifactory'){
        steps{
                  withCredentials([usernamePassword(credentialsId: 'myregistry-login', passwordVariable: 'DOCKER_REGISTRY_PWD', usernameVariable: 'DOCKER_REGISTRY_USER')]) {
                                  sh "docker login -u ${DOCKER_REGISTRY_USER} -p ${DOCKER_REGISTRY_PWD} artifactory_Url"
                                  sh "docker push imagename:tag"
                                  sh "docker rmi imagename:tag "

        }
        }
      }
  }//Stages Closing

post{

 success{
 emailext to: 'govardhan34445@gmail.com',
          subject: "Pipeline Build is over .. Build # is ..${env.BUILD_NUMBER} and Build status is.. ${currentBuild.result}.",
          body: "Pipeline Build is over .. Build # is ..${env.BUILD_NUMBER} and Build status is.. ${currentBuild.result}.",
          replyTo: 'govardhan34445@gmail.com'
 }
 
 failure{
 emailext to: 'govardhan34445@gmail.com,34445govardhan@gmail.com',
          subject: "Pipeline Build is over .. Build # is ..${env.BUILD_NUMBER} and Build status is.. ${currentBuild.result}.",
          body: "Pipeline Build is over .. Build # is ..${env.BUILD_NUMBER} and Build status is.. ${currentBuild.result}.",
          replyTo: 'govardhan34445@gmail.com'
 }
 
}


}//Pipeline closing
