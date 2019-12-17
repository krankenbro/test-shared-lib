package com.test

class Docker{
    def static createDockerImage(context, String dockerImageName, dockerContextFolder, dockerSourcePath, version)
    {
        def dockerFileFolder = getDockerFileFolder(context, dockerImageName)
        def dockerFolder = getDockerFolder(context)
        context.echo "Building docker image \"${dockerImageName}\" using \"${dockerContextFolder}\" as context folder"
        context.bat "xcopy \"..\\workspace@libs\\virto-shared-library\\resources\\${dockerFolder}\\${dockerFileFolder}\\*\" \"${dockerContextFolder}\\\" /Y /E"
        return build(context, dockerContextFolder, "${dockerImageName}:${version}".toLowerCase(), "--build-arg SOURCE=\"${dockerSourcePath}\" .")
    }
    def static buildDockerImage(context, dockerImageName, dockerContextFolder, dockerSourcePath, version){
        def dockerFileFolder = getDockerFileFolder(context, dockerImageName)
        def dockerFolder = getDockerFolder(context)
        context.echo "Building docker image \"${dockerImageName}\" using \"${dockerContextFolder}\" as context folder"
        context.bat "xcopy \"..\\workspace@libs\\virto-shared-library\\resources\\${dockerFolder}\\${dockerFileFolder}\\*\" \"${dockerContextFolder}\\\" /Y /E"
        return build(context, dockerContextFolder, "${dockerImageName}:${version}".toLowerCase(), "--build-arg SOURCE=\"${dockerSourcePath}\" .")
    }

    def static getDockerFileFolder(context, dockerImageName){
        return dockerImageName.replaceAll('/', '.')
    }
    def static getDockerFolder(context){
        def dockerFolder = ""
        if(context.projectType == 'NETCORE2') {
            dockerFolder = "docker.core\\windowsnano"
        }
        else {
            dockerFolder = "docker"
        }
        return  dockerFolder
    }

    def static build(context, contextFolder, imageName, args){
        context.dir(contextFolder){
            //return context.docker.build(imageName, args)
            return "test"
        }
    }

    def static createDockerImage(context, dockerImageName, dockerContextFolder, dockerSourcePath, version) {
        def dockerFileFolder = dockerImageName.replaceAll("/", ".")
        def dockerFolder = ""
        if(context.projectType == 'NETCORE2') {
            dockerFolder = "docker.core\\windowsnano"
            dockerImageName = dockerImageName // + "-core"
        }
        else {
            dockerFolder = "docker"
        }
        context.echo "Building docker image \"${dockerImageName}\" using \"${dockerContextFolder}\" as context folder"
        context.bat "xcopy \"..\\workspace@libs\\virto-shared-library\\resources\\${dockerFolder}\\${dockerFileFolder}\\*\" \"${dockerContextFolder}\\\" /Y /E"
        def dockerImage
        context.dir(dockerContextFolder)
                {
                    dockerImage = context.docker.build("${dockerImageName}:${version}".toLowerCase(), "--build-arg SOURCE=\"${dockerSourcePath}\" .")
                }
        return dockerImage
    }

    def static startDockerTestEnvironment(context, dockerTag)
    {
        def composeFolder = Utilities.getComposeFolder(context)
        context.dir(composeFolder)
                {
                    def platformPort = Utilities.getPlatformPort(context)
                    def storefrontPort = Utilities.getStorefrontPort(context)
                    def sqlPort = Utilities.getSqlPort(context)

                    context.echo "DOCKER_PLATFORM_PORT=${platformPort}"
                    // 1. stop containers
                    // 2. remove instances including database
                    // 3. start up new containers
                    context.withEnv(["DOCKER_TAG=${dockerTag}", "DOCKER_PLATFORM_PORT=${platformPort}", "DOCKER_STOREFRONT_PORT=${storefrontPort}", "DOCKER_SQL_PORT=${sqlPort}", "COMPOSE_PROJECT_NAME=${context.env.BUILD_TAG}" ]) {
                        context.bat "docker-compose stop"
                        context.bat "docker-compose rm -f -v"
                        context.bat "docker-compose up -d"
                    }

                    // 4. check if all docker containers are running
                    if(!checkAllDockerTestEnvironments(context)) {
                        // 5. try running it again
                        context.withEnv(["DOCKER_TAG=${dockerTag}", "DOCKER_PLATFORM_PORT=${platformPort}", "DOCKER_STOREFRONT_PORT=${storefrontPort}", "DOCKER_SQL_PORT=${sqlPort}", "COMPOSE_PROJECT_NAME=${context.env.BUILD_TAG}" ]) {
                            context.bat "docker-compose up -d"
                        }

                        // 6. check one more time
                        if(!checkAllDockerTestEnvironments(context)) {
                            throw new Exception("can't start one or more docker containers");
                        }
                    }
                }
    }

    def static checkAllDockerTestEnvironments(context)
    {
        if(!checkDockerTestEnvironment(context, "vc-platform-web")) { return false }
        if(!checkDockerTestEnvironment(context, "vc-storefront-web")) { return false }
        if(!checkDockerTestEnvironment(context, "vc-db")) { return false }

        return true
    }

    def static checkDockerTestEnvironment(context, containerId)
    {
        //def tag = context.env.BUILD_TAG.replace("-", "").toLowerCase()
        def tag = context.env.BUILD_TAG.toLowerCase()
        def containerName = "${tag}_${containerId}_1"
        containerName = containerName.replaceAll("\\.", '')
        context.echo "Checking ${containerName} state ..."
        String result = context.bat(returnStdout: true, script: "docker inspect -f {{.State.Running}} ${containerName}").trim()

        if(result.endsWith('true'))
        {
            context.echo "Docker ${containerId} is RUNNING"
            return true
        }
        else
        {
            context.echo "Docker ${containerId} FAILED"
            return false
        }
    }

    def static stopDockerTestEnvironment(context, dockerTag)
    {
        def composeFolder = Utilities.getComposeFolder(context)
        context.dir(composeFolder)
        {
            context.withEnv(["DOCKER_TAG=${dockerTag}", "COMPOSE_PROJECT_NAME=${context.env.BUILD_TAG}"]) {
                context.bat "docker-compose down -v"
            }
        }
    }
}
