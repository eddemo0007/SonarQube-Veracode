

node {

    stage ('build') {
        git url: 'file:///Users/krise/my-repositories/sonarqube-veracode'
        mvn package
    }

    stage ('upload-scan') {
        sh 'echo \'upload-scanning\''
    }

}