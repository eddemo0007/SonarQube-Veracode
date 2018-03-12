
// env vars
buildNumber = "${env.BUILD_NUMBER}"

node {

    stage ('build') {
        git url: 'file:///Users/krise/my-repositories/sonarqube-veracode'
        def mvnHome = '/opt/apache-maven-3.3.9/bin'
        sh "${mvnHome}/mvn package"
    }

    stage ('upload-scan') {
        withCredentials([ usernamePassword ( 
            credentialsId: 'veracode_login', passwordVariable: 'veracode_password', usernameVariable: 'veracode_username') ]) {
            sh 'echo uname=$veracode_username  pwd=$veracode_password'
            veracode applicationName: 'SonarQube plugin', criticality: 'VeryHigh', fileNamePattern: '', pHost: '', pPassword: '', pUser: '', replacementPattern: '', sandboxName: '', scanExcludesPattern: '', scanIncludesPattern: '', scanName: 'Jenkins pipeline (${buildNumber})', uploadExcludesPattern: '', uploadIncludesPattern: '**/target/*.jar', useIDkey: true, vid: '${veracode_username}', vkey: '${veracode_password}', vpassword: '', vuser: ''
        }
    }
}