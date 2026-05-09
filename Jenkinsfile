// Paso 3 — Pipeline de ejemplo: backend + frontend contra SonarQube en la misma red Docker.
// Requisitos en Jenkins:
// 1) Credencial tipo "Secret text", id: sonar-token (token de usuario en SonarQube).
// 2) Este archivo como Jenkinsfile desde SCM o copiar contenido en Pipeline script.

pipeline {
    agent any

    environment {
        SONAR_HOST = 'http://sonarqube:9000'
        SONAR_TOKEN = credentials('sonar-token')
    }

    stages {
        stage('Sonar — Backend') {
            steps {
                // Sin "clean": en Windows+OneDrive Docker suele fallar al borrar target/ (filesystem bloqueado).
                sh '''
                    mvn -f /workspace/backendS9/pom.xml verify sonar:sonar \
                      -Dsonar.host.url=${SONAR_HOST} \
                      -Dsonar.token=${SONAR_TOKEN}
                '''
            }
        }
        stage('Sonar — Frontend') {
            steps {
                // Si el volumen de Windows/OneDrive bloquea /workspace/.../target, compilar todo frontend en /tmp.
                sh '''
                    rm -rf /tmp/frontendS9-build
                    mkdir -p /tmp/frontendS9-build
                    cp -a /workspace/frontendS9/. /tmp/frontendS9-build/
                    mvn -f /tmp/frontendS9-build/pom.xml verify sonar:sonar \
                      -Dsonar.host.url=${SONAR_HOST} \
                      -Dsonar.token=${SONAR_TOKEN}
                '''
            }
        }
    }
}
