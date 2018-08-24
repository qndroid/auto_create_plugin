/*
 * Tencent is pleased to support the open source community by making VasDolly available.
 *
 * Copyright (C) 2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License");you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qndroid.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by qndroid on 18/8/24.*/

class CreatePluginProjectTask extends DefaultTask {

  String pluginPackageName
  def pluginProjectName
  def pluginClassName

  CreatePluginProjectTask() {
    group = 'plugin_create'
  }

  @TaskAction
  void taskAction() {
    if (checkParams()) executeLogic()
  }

  void executeLogic() {
    //第1步，开始在根工程下创建插件工程目录
    def pluginDir = project.rootProject.file(pluginProjectName)
    createFile(pluginDir, true)

    //第2步 生成输入的包名
    String packagePath = "src.main.groovy.${pluginPackageName}"
    def packageNameList = packagePath.split("\\.")
    def pack = "${pluginDir.path}/"
    packageNameList.each { packName ->
      pack += packName
      def packFile = project.file(pack)
      createFile(packFile, true)
      pack = packFile.path + "/"
    }
    createPluginClass(pack)

    //第3步 main/resources
    String resourcesPath = "src.main.resources.META-INF.gradle-plugins"
    def resourcesNameList = resourcesPath.split("\\.")
    pack = "${pluginDir.path}/"
    resourcesNameList.each { resourceName ->
      pack += resourceName
      def packFile = project.file(pack)
      createFile(packFile, true)
      pack = packFile.path + "/"
    }
    //3.1 生成properties文件
    def plguinPropertiesPath = "$pluginProjectName/src/main/resources/META-INF/gradle-plugins/$pluginPackageName" +
        ".properties"
    def plguinPropertiesFile = project.file(plguinPropertiesPath)
    createFile(plguinPropertiesFile, false)
    plguinPropertiesFile.withWriter { writer ->
      def content = "implementation-class=$pluginPackageName.$pluginClassName"
      writer.write(content)
    }

    //第4步，生成build.gradle文件
    def buildGradlePath = "${pluginDir.path}/build.gradle"
    def buildGradleFile = project.file(buildGradlePath)
    createFile(buildGradleFile, false)
    buildGradleFile.withWriter { writer ->
      writer.append("apply plugin: \'groovy\'\n\n")
      writer.append("repositories { jcenter() }\n\n")
      writer.append("dependencies {\n\n")
      writer.append("compile gradleApi()\n")
      writer.append("compile localGroovy()\n")
      writer.append("}")
    }

    //第5步，生成一个对应的properties属性文件
    def propertiesPath = "${pluginDir.path}/gradle.properties"
    def propertiesFile = project.file(propertiesPath)
    createFile(propertiesFile, false)

    //第六步，将当前工程加入到setting.gradle文件中
    addProjectIntoSettings()
  }

  /**
   * 参数较验
   * @return
   */
  boolean checkParams() {
    if (pluginProjectName == null || pluginProjectName == "") return false
    if (pluginPackageName == null || pluginPackageName == "") return false
    if (pluginClassName == null || pluginClassName == "") return false

    return true
  }

  /**
   * 文件及文件夹创建
   * @param file
   * @param isDir
   */
  private void createFile(File file, boolean isDir) {
    if (file == null || !file.exists()) {
      isDir ? file.mkdir() : file.createNewFile()
    }
  }

  /**
   * 创建Plugin类文件*/
  private void createPluginClass(rootPath) {
    def pluginPath = "${rootPath}/$pluginPackageName/$pluginClassName"
    def pluginFile = project.file(pluginPath)
    createFile(pluginFile, false)
    pluginFile.withWriter { writer ->
      writer.append("package $pluginPackageName")
      writer.append("import org.gradle.api.Plugin\n\n")
      writer.append("import org.gradle.api.Project\n\n")
      writer.append("class ${pluginClassName} implements Plugin<Project> { \n")
      writer.append("   @Override\n")
      writer.append("   void apply(Project project) {\n\n")
      writer.append("   }\n")
      writer.append("}\n\n")
    }
  }

  /**
   * 将Plugin工添加到setting文件中*/
  private void addProjectIntoSettings() {
    def settingFile = project.file(project.rootProject.getRootDir().path + "/settings.gradle")
    def settingContent = "${settingFile.text}\ninclude \':${pluginProjectName}\'"
    settingFile.withWriter { writer -> writer.append(settingContent)
    }
  }
}
