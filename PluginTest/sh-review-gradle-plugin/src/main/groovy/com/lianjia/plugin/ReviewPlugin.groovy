package com.lianjia.plugin

import com.lianjia.plugin.extensions.ReviewPluginExtension
import com.lianjia.plugin.tasks.ReviewPluginTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ReviewPlugin implements Plugin<Project> {
  @Override
  void apply(Project project) {

    ReviewPluginExtension mReviewPluginExtension = project.extensions.create(
        ReviewPluginExtension.NAME,
        ReviewPluginExtension, project)

    project.tasks.create("reviewPlugin",
        ReviewPluginTask) {
      reviewPluginExtension = mReviewPluginExtension
    }
  }
}

