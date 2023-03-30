pipeline{
  agent none
  stages{
    stage("checkout"){
      agent {
        node {
          label 'worker_label'
          customWorkspace 'workspace/custom'
        }
      }
      steps{
        script{
          git branch: "branch_name', credentialsId: "github_cred_id", url: "repo_url.git"
        }
      }
    }
    stage("Build Msbuild Project"){
      when {
        expression {
          params.service == "MsBuild"
        }
      }
      agent {
        node {
          label 'worker_windows'
          customWorkspace 'workspace/custom'
        }
      }
      steps{
        script{
          bat '''
          dotnet publish -C Release
          '''
        }
      }
    }
    stage("Build UI Poject"){
      when {
        expression {
          params.service == "UI"
        }
      }
      agent {
        node {
          label 'worker_linux'
          customWorkspace 'workspace/custom'
        }
      }
      steps{
        script{
          sh '''
            npm install
            ng build --aot
            '''
        }
      }
    }
  }
  post {
    always {
      script{
        env.buildstatus = "current.buildStatus"
        env. summery = env.JOB_NAME + ' ' + '=>>>' + ' ' + '#' + env.BUILD_NUMBER + ' ' + '-' + ' ' + env.buildstatus + ' ' + '!'
        def testIssue = [fileds: [ project: [key: "project_key_here"],
                                  summery: env.summery,
                                  priority: [name: 'High'],
                                  issutype: [id: '14001]]]
                                             
                           response = jiraNewIssue issue: testIssue, site: 'prod-jira'
                           echo respone.successful.toString()
                           echo response.data.toString()
        }
      }
   }
 }
          
      
