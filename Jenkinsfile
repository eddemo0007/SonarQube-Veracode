
// env vars
buildNumber = "${env.BUILD_NUMBER}"

    final scmVars = checkout(scm)
    //echo "scmVars: ${scmVars}"
    buildVer = scmVars.GIT_COMMIT.substring(0,7)    // first 6 chars to match the short form
    echo "last commit git hash (short) = ${buildVer}"
    veracodeBuildNumber = "${buildNumber}-${buildVer}"
    echo "Veracode build ID = ${veracodeBuildNumber}"
    
// run on any node
node {



    stage ('build') {
        git url: 'file:///Users/krise/my-repositories/sonarqube-veracode'
        def mvnHome = '/opt/apache-maven-3.3.9/bin'
        sh "${mvnHome}/mvn package"
    }

    stage ('upload-scan') {
         /*withCredentials([string(credentialsId: 'secret_text', variable: 'MY_SECRET')]) {
            sh "echo secret=$MY_SECRET"
        } */

        withCredentials([ usernamePassword ( 
            credentialsId: 'veracode_login', passwordVariable: 'VERACODE_PASSWORD', usernameVariable: 'VERACODE_USERNAME') ]) {
            veracode applicationName: 'SonarQube plugin', criticality: 'VeryHigh', fileNamePattern: '', pHost: '', pPassword: '', pUser: '', replacementPattern: '', sandboxName: '', scanExcludesPattern: '', scanIncludesPattern: '', scanName: 'Jenkins pipeline (${veracodeBuildNumber})', uploadExcludesPattern: '', uploadIncludesPattern: '**/target/*.jar', useIDkey: true, vid: "${VERACODE_USERNAME}", vkey: "${VERACODE_PASSWORD}", vpassword: '', vuser: ''
        }
    }
}