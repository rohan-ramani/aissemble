def gitId = 'github'
def gitRepo = '${projectGitUrl}'
// Set branch in the Jenkins Job or set here if not variable
def gitBranch = 'refs/heads/${branch}'
def projectDirectory = '${rootArtifactId}'

node('master') {
    dir(projectDirectory) {
        checkout([$class: 'GitSCM', branches: [[name: gitBranch]], userRemoteConfigs: [[credentialsId: gitId, url: gitRepo]]])
    }
}

stage("Deploy") {
    node('master') {
        try {
            dir(projectDirectory) {
                slackSend color: "warning",
                        message: "${projectName} deploying"
                sh 'helmfile apply --environment=ci'
                slackSend color: "good",
                        message: "${projectName} deployed successfully"
            }
        } catch (err) {
            slackSend color: "danger",
                    message: "${projectName} failed to deploy properly"
            throw err
        }
    }
}

stage("Running") {
    node('master') {
        try {
            timeout(environmentUptime) {
                input message: 'Ready to kill the environment?', ok: 'Kill Test Server'
            }
        } catch (err) {}
    }
}

stage("Teardown") {
    node('master') {
        try {
            dir(projectDirectory) {
                slackSend color: "warning",
                        message: "${projectName} shutting down"
                sh 'helmfile destroy --environment=ci'
            }
        } catch (err) {
            slackSend color: "danger",
                    message: "${projectName} failed to shut down properly"
            throw err
        }
    }
}

