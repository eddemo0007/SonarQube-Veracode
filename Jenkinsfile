

node {

    stage ('build') {
        git url: 'file:///Users/krise/my-repositories/sonarqube-veracode'
        def mvnHome = '/opt/apache-maven-3.3.9/bin'
        //sh "echo 'mvnHome = ${mvnHome}'"
        sh "${mvnHome}/mvn package"
    }

    stage ('upload-scan') {
        //sh "echo 'upload-scanning\'"
        withCredentials([usernamePassword(credentialsId: '', passwordVariable: '4b7d3273163c91200bec3aa82f784bfadd41895b48e71a419c77a5d6e5a0ee83880ff4a0684b2e8e28a578f935d8a4fa70218b387ec71cb6f47397d122bfd732', usernameVariable: '4dd707b1aa937e7a8a1400f02e649ba2')]) {
            veracode applicationName: 'SonarQube plugin', criticality: 'VeryHigh', fileNamePattern: '', pHost: '', pPassword: '', pUser: '', replacementPattern: '', sandboxName: '', scanExcludesPattern: '', scanIncludesPattern: '', scanName: 'foo', uploadExcludesPattern: '', uploadIncludesPattern: '**/target/*.jar', useIDkey: true, vid: '4dd707b1aa937e7a8a1400f02e649ba2', vkey: '4b7d3273163c91200bec3aa82f784bfadd41895b48e71a419c77a5d6e5a0ee83880ff4a0684b2e8e28a578f935d8a4fa70218b387ec71cb6f47397d122bfd732', vpassword: '', vuser: ''
        }
    }
}