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
                // target/ en el volumen Windows/OneDrive suele dar EPERM al escribir; compilar en /tmp del contenedor.
                sh '''
                    rm -rf /tmp/maven-build/frontendS9
                    mvn -f /workspace/frontendS9/pom.xml verify sonar:sonar \
                      -Dproject.build.directory=/tmp/maven-build/frontendS9/target \
                      -Dsonar.host.url=${SONAR_HOST} \
                      -Dsonar.token=${SONAR_TOKEN}
                '''
            }
        }
    }
}
