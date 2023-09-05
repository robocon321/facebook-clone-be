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

        
        stage('CODE ANALYSIS Discovery Server with SONARQUBE') {
          
            environment {
                scannerHome = tool "${SONARSCANNER}"
            }

            steps {
                script {
                    withSonarQubeEnv("${SONARSERVER}") {
                        sh '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=facebook-clone-DiscoveryServer-be \
                            -Dsonar.projectName=facebook-clone-DiscoveryServer-repo \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=discovery-server/src/ \
                            -Dsonar.java.binaries=discovery-server/target/test-classes/com/example/demo/ \
                            -Dsonar.junit.reportsPath=discovery-server/target/surefire-reports/ \
                            -Dsonar.jacoco.reportsPath=discovery-server/target/jacoco.exec \
                            -Dsonar.java.checkstyle.reportPaths=discovery-server/target/checkstyle-result.xml'''

                        timeout(time: 20, unit: 'SECONDS') {
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
            }
            
            post {
                failure {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        echo "Stage timed out but continuing..."
                    }
                }
            }
        }


        stage('CODE ANALYSIS Api Gateway with SONARQUBE') {
          
            environment {
                scannerHome = tool "${SONARSCANNER}"
            }

            steps {
                script {
                    withSonarQubeEnv("${SONARSERVER}") {
                        sh '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=facebook-clone-ApiGateway-be \
                            -Dsonar.projectName=facebook-clone-ApiGateway-repo \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=api-gateway/src/ \
                            -Dsonar.java.binaries=api-gateway/target/test-classes/com/example/demo/ \
                            -Dsonar.junit.reportsPath=api-gateway/target/surefire-reports/ \
                            -Dsonar.jacoco.reportsPath=api-gateway/target/jacoco.exec \
                            -Dsonar.java.checkstyle.reportPaths=api-gateway/target/checkstyle-result.xml'''

                        timeout(time: 20, unit: 'SECONDS') {
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
