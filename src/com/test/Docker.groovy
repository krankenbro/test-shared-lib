package com.test

class Docker {
    def static createDockerImage(context, String dockerImageName, String dockerContextFolder, String dockerSourcePath, String version) {
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
}
