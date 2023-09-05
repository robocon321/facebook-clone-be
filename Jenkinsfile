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
        
        stage('BUILD'){
            steps {
                sh 'mvn clean install -DskipTests'
            }
            post {
                success {
                    echo 'Now Archiving...'
                    archiveArtifacts artifacts: '**/target/*.jar'
                }
            }
        }

        stage('UNIT TEST'){
                steps {
                    sh 'mvn test'
                }
        }

        stage('INTEGRATION TEST'){
            steps {
                sh 'mvn verify -DskipUnitTests'
            }
        }
        
        stage ('CODE ANALYSIS WITH CHECKSTYLE'){
            steps {
                sh 'mvn checkstyle:checkstyle'
            }
            post {
                success {
                    echo 'Generated Analysis Result'
                }
            }
        }

        stage('DISCOVERY SERVER CODE ANALYSIS WITH SONARQUBE') {
          
            environment {
                scannerHome = tool "${SONARSCANNER}"
            }

            steps {
                script {
                    withSonarQubeEnv("${SONARSERVER}") {
                        def scanner = sh(script: '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=facebook-clone-DiscoveryServer-be \
                            -Dsonar.projectName=facebook-clone-DiscoveryServer-repo \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=discovery-server/src/ \
                            -Dsonar.java.binaries=discovery-server/target/test-classes/com/example/demo/ \
                            -Dsonar.junit.reportsPath=discovery-server/target/surefire-reports/ \
                            -Dsonar.jacoco.reportsPath=discovery-server/target/jacoco.exec \
                            -Dsonar.java.checkstyle.reportPaths=discovery-server/target/checkstyle-result.xml''', returnStatus: true)
                        
                        if (scanner != 0) {
                            error("SonarQube analysis failed")
                        }
                    }
                }
            }
            
            post {
                failure {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        echo "Stage timed out but continuing..."
                    }
                }
            }
        }
        
        stage('API GATEWAY CODE ANALYSIS WITH SONARQUBE') {
          
            environment {
                scannerHome = tool "${SONARSCANNER}"
            }

            steps {
                script {
                    withSonarQubeEnv("${SONARSERVER}") {
                        def scanner = sh(script: '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=facebook-clone-ApiGateway-be \
                            -Dsonar.projectName=facebook-clone-ApiGateway-repo \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=api-gateway/src/ \
                            -Dsonar.java.binaries=api-gateway/target/test-classes/com/example/demo/ \
                            -Dsonar.junit.reportsPath=api-gateway/target/surefire-reports/ \
                            -Dsonar.jacoco.reportsPath=api-gateway/target/jacoco.exec \
                            -Dsonar.java.checkstyle.reportPaths=api-gateway/target/checkstyle-result.xml''', returnStatus: true)
                        
                        if (scanner != 0) {
                            error("SonarQube analysis failed")
                        }
                    }
                }
            }
            
            post {
                failure {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        echo "Stage timed out but continuing..."
                    }
                }
            }
        }
        

        stage('ACCOUNT SERVICE CODE ANALYSIS WITH SONARQUBE') {
          
            environment {
                scannerHome = tool "${SONARSCANNER}"
            }

            steps {
                script {
                    withSonarQubeEnv("${SONARSERVER}") {
                        def scanner = sh(script: '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=facebook-clone-AccountService-be \
                            -Dsonar.projectName=facebook-clone-AccountService-repo \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=account-service/src/ \
                            -Dsonar.java.binaries=account-service/target/test-classes/com/example/demo/ \
                            -Dsonar.junit.reportsPath=account-service/target/surefire-reports/ \
                            -Dsonar.jacoco.reportsPath=account-service/target/jacoco.exec \
                            -Dsonar.java.checkstyle.reportPaths=account-service/target/checkstyle-result.xml''', returnStatus: true)
                        
                        if (scanner != 0) {
                            error("SonarQube analysis failed")
                        }
                    }
                }
            }
            
            post {
                failure {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        echo "Stage timed out but continuing..."
                    }
                }
            }
        }
        
        stage('QUALITY GATE WITH SONARQUBE') {
            steps {
                script {
                    timeout(time: 10, unit: 'MINUTES') {
                        def qualityGate = waitForQualityGate()
    
                        if (qualityGate.status == 'OK') {
                            echo 'Quality Gate passed. Proceeding with the pipeline.'
                            currentBuild.result = 'FAILURE'
                            echo 'Stage 1 failed but continuing...'
                        } else {
                            error('Quality Gate failed. Aborting the pipeline.')
                        }
                    }
                }    
            }
            post {
                failure {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        echo "Stage timed out but continuing..."
                    }
                }
            }
            
        }

    }
}