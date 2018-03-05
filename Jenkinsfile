

node {

    stage ('build') {
        git url: 'file:///Users/krise/my-repositories/sonarqube-veracode'
        sh "mvn package"
    }

    stage ('upload-scan') {
        sh 'echo \'upload-scanning\''
    }

}