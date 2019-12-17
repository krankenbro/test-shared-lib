package com.test

class Docker {
    def static createDockerImage(context, String dockerImageName, String dockerContextFolder, String dockerSourcePath, String version) {
        def dockerFileFolder = getDockerFileFolder(context, dockerImageName)
        def dockerFolder = getDockerFolder(context)
        context.echo "Building docker image \"${dockerImageName}\" using \"${dockerContextFolder}\" as context folder"
        context.bat "xcopy \"..\\workspace@libs\\virto-shared-library\\resources\\${dockerFolder}\\${dockerFileFolder}\\*\" \"${dockerContextFolder}\\\" /Y /E"
        return build(context, dockerContextFolder, "${dockerImageName}:${version}".toLowerCase(), "--build-arg SOURCE=\"${dockerSourcePath}\" .")
    }

    def static getDockerFileFolder(context, String dockerImageName){
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
    @NonCPS
    def static build(context, contextFolder, imageName, args){
        context.dir(contextFolder){
            return context.docker.build(imageName, args)
        }
    }
}
