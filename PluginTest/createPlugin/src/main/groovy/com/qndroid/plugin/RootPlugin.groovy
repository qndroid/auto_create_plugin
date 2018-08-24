package com.qndroid.plugin

import com.qndroid.plugin.extension.PluginConfigurationExtension
import org.gradle.api.Plugin
import com.qndroid.plugin.tasks.CreatePluginProjectTask
import org.gradle.api.Project

class RootPlugin implements Plugin<Project> {

  Project mProject
  PluginConfigurationExtension mPluginConfigurationExtension

  @Override
  void apply(Project project) {

    mProject = project
    mPluginConfigurationExtension =
        project.extensions.create('pluginConfig', PluginConfigurationExtension, project)
    //为引入插件的工程创建task
    project.task('createPluginProject', type: CreatePluginProjectTask) {
      pluginConfigurationExtension = mPluginConfigurationExtension
    }
  }
}

