pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }
    
    environment {
        APP_NAME = 'devops-app'
        APP_VERSION = '1.0.0'
        APP_SERVER_USER = 'msservice'
        APP_SERVER_HOST = '' // Set this from Terraform output
        APP_DEPLOY_DIR = '/home/msservice/app'
        JAR_NAME = "${APP_NAME}-${APP_VERSION}.jar"
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from Git...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building application with Maven...'
                dir('java-app') {
                    sh 'mvn clean compile'
                }
            }
        }
        
        stage('Unit Tests') {
            steps {
                echo 'Running unit tests...'
                dir('java-app') {
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit 'java-app/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo 'Packaging application...'
                dir('java-app') {
                    sh 'mvn package -DskipTests'
                }
            }
        }
        
        stage('Deploy to Application Server') {
            steps {
                echo 'Deploying JAR to application server...'
                script {
                    sshagent(['app-server-ssh-key']) {
                        // Create deployment directory
                        sh """
                            ssh -o StrictHostKeyChecking=no ${APP_SERVER_USER}@${APP_SERVER_HOST} '
                                mkdir -p ${APP_DEPLOY_DIR}
                            '
                        """
                        
                        // Copy JAR to application server
                        sh """
                            scp -o StrictHostKeyChecking=no \
                                java-app/target/${JAR_NAME} \
                                ${APP_SERVER_USER}@${APP_SERVER_HOST}:${APP_DEPLOY_DIR}/
                        """
                        
                        // Stop existing application (if running)
                        sh """
                            ssh -o StrictHostKeyChecking=no ${APP_SERVER_USER}@${APP_SERVER_HOST} '
                                pkill -f ${APP_NAME} || true
                            '
                        """
                        
                        // Start application
                        sh """
                            ssh -o StrictHostKeyChecking=no ${APP_SERVER_USER}@${APP_SERVER_HOST} '
                                cd ${APP_DEPLOY_DIR} && \
                                nohup java -jar ${JAR_NAME} > app.log 2>&1 &
                            '
                        """
                        
                        echo 'Waiting for application to start...'
                        sleep 30
                    }
                }
            }
        }
        
        stage('Health Check') {
            steps {
                echo 'Running sanity check on deployed application...'
                script {
                    def healthUrl = "http://${APP_SERVER_HOST}:8081/actuator/health"
                    def response = sh(
                        script: "curl -s ${healthUrl}",
                        returnStdout: true
                    ).trim()
                    
                    echo "Health check response: ${response}"
                    
                    if (!response.contains('"status":"UP"')) {
                        error("Health check failed! Application is not UP.")
                    }
                    
                    echo 'Health check passed! Application is UP and running.'
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline completed successfully!'
            echo "Application deployed and accessible at: http://${APP_SERVER_HOST}:8081"
        }
        failure {
            echo 'Pipeline failed!'
        }
        always {
            cleanWs()
        }
    }
}
