pipeline {
    
    agent any
    
    tools {
        jdk "OracleJDK17"
        maven "MAVEN3"
    }
    
    environment {
        SNAP_REPO = 'facebook-clone-backend-snapshot'
        NEXUS_USER = 'admin'
        NEXUS_PASS = '0123456789'
        RELEASE_REPO = 'facebook-clone-backend-release'
        CENTRAL_REPO = 'facebook-clone-backend-central'
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
            article {
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
            article {
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
            
            article {
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
            
            article {
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
        }

        stage('AUTH SERVICE CODE ANALYSIS WITH SONARQUBE') {
          
            environment {
                scannerHome = tool "${SONARSCANNER}"
            }

            steps {
                script {
                    withSonarQubeEnv("${SONARSERVER}") {
                        def scanner = sh(script: '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=facebook-clone-AuthService-be \
                            -Dsonar.projectName=facebook-clone-AuthService-repo \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=auth-service/src/ \
                            -Dsonar.java.binaries=auth-service/target/test-classes/com/example/demo/ \
                            -Dsonar.junit.reportsPath=auth-service/target/surefire-reports/ \
                            -Dsonar.jacoco.reportsPath=auth-service/target/jacoco.exec \
                            -Dsonar.java.checkstyle.reportPaths=auth-service/target/checkstyle-result.xml''', returnStatus: true)
                        
                        if (scanner != 0) {
                            error("SonarQube analysis failed")
                        }
                    }
                }
            }

            article {
                failure {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        echo "Stage timed out but continuing..."
                    }
                }
            }
        }

        stage('FILE SERVICE CODE ANALYSIS WITH SONARQUBE') {
          
            environment {
                scannerHome = tool "${SONARSCANNER}"
            }

            steps {
                script {
                    withSonarQubeEnv("${SONARSERVER}") {
                        def scanner = sh(script: '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=facebook-clone-FileService-be \
                            -Dsonar.projectName=facebook-clone-FileService-repo \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=file-service/src/ \
                            -Dsonar.java.binaries=file-service/target/test-classes/com/example/demo/ \
                            -Dsonar.junit.reportsPath=file-service/target/surefire-reports/ \
                            -Dsonar.jacoco.reportsPath=file-service/target/jacoco.exec \
                            -Dsonar.java.checkstyle.reportPaths=file-service/target/checkstyle-result.xml''', returnStatus: true)
                        
                        if (scanner != 0) {
                            error("SonarQube analysis failed")
                        }
                    }
                }
            }

            article {
                failure {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        echo "Stage timed out but continuing..."
                    }
                }
            }
        }

        stage('LOCATION SERVICE CODE ANALYSIS WITH SONARQUBE') {
          
            environment {
                scannerHome = tool "${SONARSCANNER}"
            }

            steps {
                script {
                    withSonarQubeEnv("${SONARSERVER}") {
                        def scanner = sh(script: '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=facebook-clone-LocationService-be \
                            -Dsonar.projectName=facebook-clone-LocationService-repo \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=location-service/src/ \
                            -Dsonar.java.binaries=location-service/target/test-classes/com/example/demo/ \
                            -Dsonar.junit.reportsPath=location-service/target/surefire-reports/ \
                            -Dsonar.jacoco.reportsPath=location-service/target/jacoco.exec \
                            -Dsonar.java.checkstyle.reportPaths=location-service/target/checkstyle-result.xml''', returnStatus: true)
                        
                        if (scanner != 0) {
                            error("SonarQube analysis failed")
                        }
                    }
                }
            }

            article {
                failure {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        echo "Stage timed out but continuing..."
                    }
                }
            }
        }

        stage('ARTICLE SERVICE CODE ANALYSIS WITH SONARQUBE') {
          
            environment {
                scannerHome = tool "${SONARSCANNER}"
            }

            steps {
                script {
                    withSonarQubeEnv("${SONARSERVER}") {
                        def scanner = sh(script: '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=facebook-clone-ArticleService-be \
                            -Dsonar.projectName=facebook-clone-ArticleService-repo \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=article-service/src/ \
                            -Dsonar.java.binaries=article-service/target/test-classes/com/example/demo/ \
                            -Dsonar.junit.reportsPath=article-service/target/surefire-reports/ \
                            -Dsonar.jacoco.reportsPath=article-service/target/jacoco.exec \
                            -Dsonar.java.checkstyle.reportPaths=article-service/target/checkstyle-result.xml''', returnStatus: true)
                        
                        if (scanner != 0) {
                            error("SonarQube analysis failed")
                        }
                    }
                }
            }

            article {
                failure {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        echo "Stage timed out but continuing..."
                    }
                }
            }
        }

        stage('FRIEND SERVICE CODE ANALYSIS WITH SONARQUBE') {
          
            environment {
                scannerHome = tool "${SONARSCANNER}"
            }

            steps {
                script {
                    withSonarQubeEnv("${SONARSERVER}") {
                        def scanner = sh(script: '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=facebook-clone-FriendService-be \
                            -Dsonar.projectName=facebook-clone-FriendService-repo \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=friend-service/src/ \
                            -Dsonar.java.binaries=friend-service/target/test-classes/com/example/demo/ \
                            -Dsonar.junit.reportsPath=friend-service/target/surefire-reports/ \
                            -Dsonar.jacoco.reportsPath=friend-service/target/jacoco.exec \
                            -Dsonar.java.checkstyle.reportPaths=friend-service/target/checkstyle-result.xml''', returnStatus: true)
                        
                        if (scanner != 0) {
                            error("SonarQube analysis failed")
                        }
                    }
                }
            }

            article {
                failure {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        echo "Stage timed out but continuing..."
                    }
                }
            }
        }

        stage('COMMENT SERVICE CODE ANALYSIS WITH SONARQUBE') {
          
            environment {
                scannerHome = tool "${SONARSCANNER}"
            }

            steps {
                script {
                    withSonarQubeEnv("${SONARSERVER}") {
                        def scanner = sh(script: '''${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=facebook-clone-CommentService-be \
                            -Dsonar.projectName=facebook-clone-CommentService-repo \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=comment-service/src/ \
                            -Dsonar.java.binaries=comment-service/target/test-classes/com/example/demo/ \
                            -Dsonar.junit.reportsPath=comment-service/target/surefire-reports/ \
                            -Dsonar.jacoco.reportsPath=comment-service/target/jacoco.exec \
                            -Dsonar.java.checkstyle.reportPaths=comment-service/target/checkstyle-result.xml''', returnStatus: true)
                        
                        if (scanner != 0) {
                            error("SonarQube analysis failed")
                        }
                    }
                }
            }

            article {
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
                        } else {
                            currentBuild.result = 'FAILURE'
                            echo 'Stage 1 failed but continuing...'
                            error('Quality Gate failed. Aborting the pipeline.')
                        }
                    }
                }    
            }
            article {
                failure {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        echo "Stage timed out but continuing..."
                    }
                }
            }
            
        }

        stage("PUBLISH TO NEXUS REPOSITORY MANAGER") {
            steps{
                nexusArtifactUploader(
                  nexusVersion: 'nexus3',
                  protocol: 'http',
                  nexusUrl: "${NEXUSIP}:${NEXUSPORT}",
                  groupId: 'com.robocon321',
                  version: "${env.BUILD_ID}-${env.BUILD_TIMESTAMP}",
                  repository: "${RELEASE_REPO}",
                  credentialsId: "${NEXUS_LOGIN}",
                  artifacts: [
                    [artifactId: 'account-service',
                     classifier: '',
                     file: 'account-service/target/account-service-0.0.1-SNAPSHOT.jar',
                     type: 'jar'],
                     
                    [artifactId: 'api-gateway',
                     classifier: '',
                     file: 'api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar',
                     type: 'jar'],
                     
                    [artifactId: 'coverage-report',
                     classifier: '',
                     file: 'coverage-report/target/coverage-report-0.0.1-SNAPSHOT.jar',
                     type: 'jar'],
                     
                    [artifactId: 'discovery-server',
                     classifier: '',
                     file: 'discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar',
                     type: 'jar'],

                    [artifactId: 'auth-service',
                     classifier: '',
                     file: 'auth-service/target/auth-service-0.0.1-SNAPSHOT.jar',
                     type: 'jar'],

                    [artifactId: 'file-service',
                     classifier: '',
                     file: 'file-service/target/file-service-0.0.1-SNAPSHOT.jar',
                     type: 'jar'],

                    [artifactId: 'location-service',
                     classifier: '',
                     file: 'location-service/target/location-service-0.0.1-SNAPSHOT.jar',
                     type: 'jar'],

                    [artifactId: 'article-service',
                     classifier: '',
                     file: 'article-service/target/article-service-0.0.1-SNAPSHOT.jar',
                     type: 'jar'],

                    [artifactId: 'friend-service',
                     classifier: '',
                     file: 'friend-service/target/friend-service-0.0.1-SNAPSHOT.jar',
                     type: 'jar'],

                    [artifactId: 'comment-service',
                     classifier: '',
                     file: 'comment-service/target/comment-service-0.0.1-SNAPSHOT.jar',
                     type: 'jar'],
                  ]
                )
            }
        }
    }
}
