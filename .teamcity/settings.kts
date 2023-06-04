import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.DotnetMsBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.DotnetTestStep
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetMsBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetPublish
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetTest
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

    buildType(Building_1)

    subProject(Building)
}

object Building_1 : BuildType({
    id("Building")
    name = "Build All"

    vcs {
        root(DslContext.settingsRoot)

        showDependenciesChanges = true
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }

    dependencies {
        snapshot(Building_BuildConsoleWebLinuxX64) {
        }
        snapshot(Building_BuildConsoleWebWinX64) {
        }
        snapshot(Building_BuildDesktopWindows) {
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
            version = DotnetMsBuildStep.MSBuildVersion.V17
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
