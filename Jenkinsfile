

node {

    stage ('build') {
        git url: 'file:///Users/krise/my-repositories/sonarqube-veracode'
        def mvnHome = '/opt/apache-maven-3.3.9/bin/'
        sh "echo 'mvnHome = ${mvnHome}'"
        //sh "mvn package"
    }

    stage ('upload-scan') {
        sh 'echo \'upload-scanning\''
    }

}