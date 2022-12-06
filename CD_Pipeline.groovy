pipeline{

  agent any

  properties([
    parameters([
        
        string(
            defaultValue: 'dev', 
            name: 'namespace'
        ),
        string(
            defaultValue: 'dev_cluster', 
            name: 'cluster_name'
        ),
        string(
            defaultValue: 'us-east-2', 
            name: 'region'
        )
    ])
  ])

  stages{

      stage('CheckOutCode'){
        steps{
        git branch: 'development', credentialsId: 'github_cred_id', url: 'github_repo_url'
    
        }
      }
  
      stage('Login to EKS Cluster'){
        steps{
            withCredentials([[ $class: 'AmazonWebServicesCredentialsBinding', credentialsId: "aws-credentials-id-here", accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
                sh  """
                export AWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID}" && export AWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY}"
                aws eks --region ${region} update-kubeconfig --name ${cluster_name}        
                """
            }
        }
      }
      stage('Deploy to development') {
            when {
                expression { 
                   return params.ENVIRONMENT == 'dev'
                }
            }
            steps {
                    sh """
                    echo "deploy to development"
                    kubectl apply -f deployment_file.yaml
                    kubectl apply -f service_file.yaml
                    """
                }
            }

      stage('Deploy Application into EKS cluster'){
        when {
                expression { 
                   return params.ENVIRONMENT == 'production'
                }
            }
        steps{
          sh  """
            kubectl apply -f deployment_file.yaml
            kubectl apply -f service_file.yaml
            """
        }
      }
      
      stage('Deployment Post Checks'){
        steps{
          sh  """
             kubectl get pods -n ${namespace}
             Kubectl get deployment -n ${namespace}
             kubectl get svc -n ${namespace}
              """
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
