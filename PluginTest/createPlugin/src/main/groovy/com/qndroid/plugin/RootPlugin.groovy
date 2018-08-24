package com.qndroid.plugin

import org.gradle.api.Plugin
import com.qndroid.plugin.tasks.CreatePluginProjectTask
import org.gradle.api.Project

class RootPlugin implements Plugin<Project> {
  @Override
  void apply(Project project) {
    //为引入插件的工程创建task
    project.task(name: 'createPluginProject', type: CreatePluginProjectTask)
  }
}

