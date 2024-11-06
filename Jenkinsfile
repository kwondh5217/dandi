pipeline {
    agent any

    environment {
        IMAGE_NAME = "fosong98/dandi-back"
        IMAGE_TAG = "latest"
        HOST = "k11e205.p.ssafy.io"
    }

    stages {
        stage('Fetch Secrets') {
            steps {
                withCredentials([file(credentialsId: 'firebase-secret', variable: 'FIREBASE_SECRET'),
                                 file(credentialsId: 'application-secret', variable: 'APPLICATION_SECRET'),
                                 file(credentialsId: 'application-prod', variable: 'APPLICATION_PROD'),
                                 file(credentialsId: 'redisson', variable: 'REDISSON')]) {
                    sh '''
                        mkdir -p module-api/src/main/resources/firebase
                        cp $FIREBASE_SECRET module-api/src/main/resources/firebase/dandi-4158a-firebase-adminsdk-h61xd-7ec9de5fad.json
                        cp $APPLICATION_SECRET module-api/src/main/resources/application-secret.yml
                        cp $APPLICATION_PROD module-api/src/main/resources/application-prod.yml
                        cp $REDISSON module-api/src/main/resources/redisson.yaml
                    '''
                }
            }
        }

        stage('JIB') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub', passwordVariable: 'password', usernameVariable: 'name')]) {
                    sh "chmod 777 gradlew"
                    sh "./gradlew :module-api:jib -Djib.to.image=$IMAGE_NAME:$IMAGE_TAG -Djib.to.auth.username=$name -Djib.to.auth.password=$password"
                }
            }
        }

        stage('COMPOSE') {
            steps {
                sshagent(['ubuntu-ssh']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ubuntu@$HOST '
                                                    docker compose -f app-compose.yml pull &&
                                                    docker compose -f app-compose.yml down &&
                                                    docker compose -f app-compose.yml up -d
                                                '
                    '''
                }
            }
        }
    }
}
