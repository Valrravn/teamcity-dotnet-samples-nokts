import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.dockerSupport
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.DockerCommandStep
import jetbrains.buildServer.configs.kotlin.buildSteps.DotnetMsBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.DotnetTestStep
import jetbrains.buildServer.configs.kotlin.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetMsBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetPublish
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetTest
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.dockerRegistry
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.05"

project {

    buildType(DeployAll)
    buildType(Building_1)

    subProject(DeploymentConfigsProject)
    subProject(Building)
}

object Building_1 : BuildType({
    id("Building")
    name = "Build All"

    artifactRules = "bin/**/*.* => ."
    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(DslContext.settingsRoot)

        showDependenciesChanges = true
    }

    triggers {
        vcs {
            enabled = false
        }
    }

    features {
        perfmon {
        }
    }

    dependencies {
        dependency(Building_BuildConsoleWebLinuxX64) {
            snapshot {
            }

            artifacts {
                artifactRules = "**/*.* => ."
            }
        }
        dependency(Building_BuildConsoleWebWinX64) {
            snapshot {
            }

            artifacts {
                artifactRules = "**/*.* => ."
            }
        }
        dependency(Building_BuildDesktopWindows) {
            snapshot {
            }

            artifacts {
                artifactRules = "**/*.* => ."
            }
        }
    }
})

object DeployAll : BuildType({
    name = "Deploy All"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    params {
        checkbox("system.deployall.prompt", "false", label = """Confirm the "Build All" configuration run""", description = "Please avoid excessive Deploy All runs due to significant resources required to run this chain", display = ParameterDisplay.PROMPT, readOnly = true,
                  checked = "true", unchecked = "false")
    }

    steps {
        script {
            name = "Confirmation"

            conditions {
                equals("deployall.prompt", "true")
            }
            scriptContent = """echo "%teamcity.build.triggeredBy.username% triggered the DEPLOY ALL configuration""""
        }
    }

    dependencies {
        snapshot(DeploymentConfigsProject_DeployConsoleLinux) {
        }
        snapshot(DeploymentConfigsProject_DeployConsoleWindows) {
        }
        snapshot(DeploymentConfigsProject_DeployWebLinux) {
        }
        snapshot(DeploymentConfigsProject_DeployWebWindows) {
        }
    }
})


object Building : Project({
    name = "Building Configurations"

    buildType(Building_BuildConsoleWebLinuxX64)
    buildType(Building_BuildDesktopWindows)
    buildType(Building_RunTestsLinux)
    buildType(Building_BuildConsoleWebWinX64)
    buildType(Building_Build)
})

object Building_Build : BuildType({
    name = "Run Tests (Windows)"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dotnetTest {
            name = "Test (Win)"
            projects = "Clock.Tests/Clock.Tests.csproj"
            sdk = "7"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }

    triggers {
        vcs {
            enabled = false
        }
    }

    features {
        perfmon {
        }
    }

    requirements {
        matches("teamcity.agent.jvm.os.family", "Windows")
    }
})

object Building_BuildConsoleWebLinuxX64 : BuildType({
    name = "Build console & web (linux-x64)"

    artifactRules = "bin => bin"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dotnetPublish {
            name = "Build console"
            projects = "Clock.Console/Clock.Console.csproj"
            runtime = "linux-x64"
            outputDir = "bin/Clock.Console/linux-x64"
            args = "/p:PublishTrimmed=true /p:PublishSingleFile=true"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetPublish {
            name = "Build web"
            projects = "Clock.Web/Clock.Web.csproj"
            runtime = "linux-x64"
            outputDir = "bin/Clock.Web/linux-x64"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }

    triggers {
        vcs {
            enabled = false
        }
    }

    features {
        perfmon {
        }
    }

    dependencies {
        snapshot(Building_RunTestsLinux) {
        }
    }

    requirements {
        exists("DotNetCoreSDK7.0_Path")
        exists("DotNetCoreRuntime7.0_Path")
    }
})

object Building_BuildConsoleWebWinX64 : BuildType({
    name = "Build console & web (win-x64)"

    artifactRules = "bin => bin"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dotnetPublish {
            name = "Build Console (win-x64)"
            projects = "Clock.Console/Clock.Console.csproj"
            runtime = "win-x64"
            outputDir = "bin/Clock.Console/win-x64"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetPublish {
            name = "Build web"
            projects = "Clock.Web/Clock.Web.csproj"
            runtime = "win-x64"
            outputDir = "bin/Clock.Web/win-x64"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }

    triggers {
        vcs {
            enabled = false
        }
    }

    features {
        perfmon {
        }
    }

    dependencies {
        snapshot(Building_Build) {
        }
    }

    requirements {
        exists("DotNetCoreRuntime7.0_Path")
    }
})

object Building_BuildDesktopWindows : BuildType({
    name = "Build Desktop (Windows)"

    artifactRules = """
        bin/Clock.Desktop/win/**/*.* => bin/Clock.Desktop.zip
        bin/Clock.Desktop.Uwp/win/**/*.* => bin/Clock.Desktop.Uwp.zip
    """.trimIndent()

    params {
        param("system.PublishDir", "../bin/Clock.Desktop/win/")
        param("system.VersionPrefix", "1.0.0")
        param("system.VersionSuffix", "%build.number%")
        param("system.AppxPackageDir", "../bin/Clock.Desktop.Uwp/win/")
        param("system.configuration", "Release")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dotnetMsBuild {
            name = "Build Desktop (Win)"
            projects = """
                Clock.Desktop/Clock.Desktop.csproj
                Clock.Desktop.Uwp/Clock.Desktop.Uwp.csproj
            """.trimIndent()
            version = DotnetMsBuildStep.MSBuildVersion.V16
            targets = "Restore;Rebuild;Publish"
            sdk = "7"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }

    triggers {
        vcs {
            enabled = false
        }
    }

    features {
        perfmon {
        }
    }

    dependencies {
        snapshot(Building_Build) {
        }
    }

    requirements {
        matches("teamcity.agent.jvm.os.family", "Windows")
    }
})

object Building_RunTestsLinux : BuildType({
    name = "Run Tests (Linux)"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dotnetTest {
            name = "Tests (Linux)"
            projects = "Clock.Tests/Clock.Tests.csproj"
            dockerImage = "mcr.microsoft.com/dotnet/sdk:7.0"
            dockerImagePlatform = DotnetTestStep.ImagePlatform.Linux
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }

    triggers {
        vcs {
            enabled = false
        }
    }

    features {
        perfmon {
        }
    }

    requirements {
        matches("teamcity.agent.jvm.os.family", "Linux")
    }
})


object DeploymentConfigsProject : Project({
    name = "Deployment Configurations"
    description = "This subproject contains configurations that carry out delivery"

    buildType(DeploymentConfigsProject_DeployConsoleWindows)
    buildType(DeploymentConfigsProject_DeployConsoleLinux)
    buildType(DeploymentConfigsProject_DeployWebLinux)
    buildType(DeploymentConfigsProject_DeployWebWindows)

    features {
        dockerRegistry {
            id = "PROJECT_EXT_5"
            name = "Docker Registry"
            userName = "valrravn"
            password = "credentialsJSON:0ff181ee-cc10-48ac-b5f4-ce50ca2013b4"
        }
    }
})

object DeploymentConfigsProject_DeployConsoleLinux : BuildType({
    name = "Deploy Console (Linux)"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dockerCommand {
            name = "Pull runtime dependencies"
            commandType = other {
                subCommand = "pull"
                commandArgs = "mcr.microsoft.com/dotnet/runtime-deps:7.0-alpine"
            }
        }
        dockerCommand {
            name = "Build container"
            commandType = build {
                source = file {
                    path = "context/console.linux.dockerfile"
                }
                contextDir = "context"
                platform = DockerCommandStep.ImagePlatform.Linux
                namesAndTags = "valrravn/clock-console:ubuntu"
                commandArgs = "--build-arg baseImage=mcr.microsoft.com/dotnet/runtime-deps:7.0-alpine"
            }
        }
        dockerCommand {
            name = "Push container"
            commandType = push {
                namesAndTags = "valrravn/clock-console:ubuntu"
            }
        }
    }

    features {
        dockerSupport {
            loginToRegistry = on {
                dockerRegistryId = "PROJECT_EXT_5"
            }
        }
    }

    dependencies {
        dependency(Building_BuildConsoleWebLinuxX64) {
            snapshot {
            }

            artifacts {
                artifactRules = "bin => context"
            }
        }
    }

    requirements {
        contains("teamcity.agent.os.name", "ubuntu-20.04")
    }
})

object DeploymentConfigsProject_DeployConsoleWindows : BuildType({
    name = "Deploy Console (Windows)"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dockerCommand {
            name = "Pull container"
            commandType = other {
                subCommand = "pull"
                commandArgs = "mcr.microsoft.com/dotnet/runtime:7.0"
            }
        }
        dockerCommand {
            name = "Build container"
            commandType = build {
                source = file {
                    path = "context/console.windows.dockerfile"
                }
                contextDir = "context"
                platform = DockerCommandStep.ImagePlatform.Windows
                namesAndTags = "valrravn/clock-console:windows"
                commandArgs = "--build-arg baseImage=mcr.microsoft.com/dotnet/runtime:7.0"
            }
        }
        dockerCommand {
            name = "Push container"
            commandType = push {
                namesAndTags = "valrravn/clock-console:windows"
            }
        }
    }

    features {
        dockerSupport {
            loginToRegistry = on {
                dockerRegistryId = "PROJECT_EXT_5"
            }
        }
    }

    dependencies {
        dependency(Building_BuildConsoleWebWinX64) {
            snapshot {
            }

            artifacts {
                artifactRules = "bin => context"
            }
        }
    }

    requirements {
        contains("teamcity.agent.os.name", "windows-server-2022")
    }
})

object DeploymentConfigsProject_DeployWebLinux : BuildType({
    name = "Deploy Web (Linux)"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dockerCommand {
            name = "Pull runtime dependencies"
            commandType = other {
                subCommand = "pull"
                commandArgs = "mcr.microsoft.com/dotnet/runtime-deps:7.0-alpine"
            }
        }
        dockerCommand {
            name = "Build container"
            commandType = build {
                source = file {
                    path = "context/web.linux.dockerfile"
                }
                contextDir = "context"
                platform = DockerCommandStep.ImagePlatform.Linux
                namesAndTags = "valrravn/clock-web:ubuntu"
                commandArgs = "--build-arg baseImage=mcr.microsoft.com/dotnet/runtime-deps:7.0-alpine"
            }
        }
        dockerCommand {
            name = "Push container"
            commandType = push {
                namesAndTags = "valrravn/clock-web:ubuntu"
            }
        }
    }

    features {
        dockerSupport {
            loginToRegistry = on {
                dockerRegistryId = "PROJECT_EXT_5"
            }
        }
    }

    dependencies {
        dependency(Building_BuildConsoleWebLinuxX64) {
            snapshot {
            }

            artifacts {
                artifactRules = "bin => context"
            }
        }
    }

    requirements {
        contains("teamcity.agent.os.name", "ubuntu-20.04")
    }
})

object DeploymentConfigsProject_DeployWebWindows : BuildType({
    name = "Deploy Web (Windows)"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dockerCommand {
            name = "Pull container"
            commandType = other {
                subCommand = "pull"
                commandArgs = "mcr.microsoft.com/dotnet/runtime:7.0"
            }
        }
        dockerCommand {
            name = "Build container"
            commandType = build {
                source = file {
                    path = "context/web.windows.dockerfile"
                }
                contextDir = "context"
                platform = DockerCommandStep.ImagePlatform.Windows
                namesAndTags = "valrravn/clock-web:windows"
                commandArgs = "--build-arg baseImage=mcr.microsoft.com/dotnet/runtime:7.0"
            }
        }
        dockerCommand {
            name = "Push container"
            commandType = push {
                namesAndTags = "valrravn/clock-web:windows"
            }
        }
    }

    features {
        dockerSupport {
            loginToRegistry = on {
                dockerRegistryId = "PROJECT_EXT_5"
            }
        }
    }

    dependencies {
        dependency(Building_BuildConsoleWebWinX64) {
            snapshot {
            }

            artifacts {
                artifactRules = "bin => context"
            }
        }
    }

    requirements {
        contains("teamcity.agent.os.name", "windows-server-2022")
    }
})
