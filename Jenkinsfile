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
                        -Dsonar.projectName=facebook-clone-DiscoveryServer-repo \
                        -Dsonar.projectVersion=1.0 \
                        -Dsonar.sources=discovery-server/src/ \
                        -Dsonar.java.binaries=discovery-server/target/test-classes/com/example/demo/ \
                        -Dsonar.junit.reportsPath=discovery-server/target/surefire-reports/ \
                        -Dsonar.jacoco.reportsPath=discovery-server/target/jacoco.exec \
                        -Dsonar.java.checkstyle.reportPaths=discovery-server/target/checkstyle-result.xml'''
                }
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: false
                }
            }
        }
    stages{
        stage('CODE ANALYSIS with SONARQUBE') {
          
            environment {
                scannerHome = tool "${SONARSCANNER}"
            }

            steps {
                withSonarQubeEnv("${SONARSERVER}") {
                    sh '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=facebook-clone-be \
                        -Dsonar.projectName=facebook-clone-ApiGateway-repo \
                        -Dsonar.projectVersion=1.0 \
                        -Dsonar.sources=api-gateway/src/ \
                        -Dsonar.java.binaries=api-gateway/target/test-classes/com/example/demo/ \
                        -Dsonar.junit.reportsPath=api-gateway/target/surefire-reports/ \
                        -Dsonar.jacoco.reportsPath=api-gateway/target/jacoco.exec \
                        -Dsonar.java.checkstyle.reportPaths=api-gateway/target/checkstyle-result.xml'''
                }
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: false
                }
            }
        }

    }
}
