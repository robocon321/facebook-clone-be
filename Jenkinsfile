pipeline {
    
    agent any
    
    tools {
        jdk "OracleJDK17"
        maven "MAVEN3"
    }
    
    environment {
        SNAP_REPO = 'facebook-clone-snapshot'
        NEXUS_USER = 'admin'
        NEXUS_PASS = '0123456789'
        RELEASE_REPO = 'facebook-clone-release'
        CENTRAL_REPO = 'facebook-clone-maven-central'
        NEXUSIP = 'localhost'
        NEXUSPORT = '8081'
        NEXUS_GRP_REPO = 'facebook-clone-group'
        NEXUS_LOGIN = 'nexuslogin'
        SONARSERVER = 'sonarqubeserver'
        SONARSCANNER = 'sonarqubescanner'
    }
    
    stages{
        stage('CODE ANALYSIS with SONARQUBE') {
          
            environment {
                scannerHome = tool "${SONARSCANNER}"
            }

            steps {
                withSonarQubeEnv("${SONARSERVER}") {
                    sh '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=facebook-clone-be \
                        -Dsonar.projectName=facebook-clone-repo \
                        -Dsonar.projectVersion=1.0 \
                        -Dsonar.sources=src/ \
                        -Dsonar.java.binaries=target/test-classes/com/visualpathit/account/controllerTest/ \
                        -Dsonar.junit.reportsPath=target/surefire-reports/ \
                        -Dsonar.jacoco.reportsPath=target/jacoco.exec \
                        -Dsonar.java.checkstyle.reportPaths=target/checkstyle-result.xml'''
                }

                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

    }
}
