

node {

    stage ('build') {
        git url: 'file:///Users/krise/my-repositories/sonarqube-veracode'
        def mvnHome = tool 'M3'
        sh "echo 'mvnHome = ${mvnHome}'"
        //sh "mvn package"
    }

    stage ('upload-scan') {
        sh 'echo \'upload-scanning\''
    }

}