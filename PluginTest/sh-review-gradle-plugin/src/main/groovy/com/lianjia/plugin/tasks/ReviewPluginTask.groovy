package com.lianjia.plugin.tasks

import com.lianjia.plugin.extensions.ReviewPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class ReviewPluginTask extends DefaultTask {

  {
    group = 'review Plugin'
    description = '提交代码到greeit，交添加review人'
  }

  ReviewPluginExtension reviewPluginExtension

  @TaskAction
  void run() {
    if (!reviewPluginExtension.checkParams()) {
      throw new GradleException('reviewers params can\'t be null, please check it first')
    }
    executeGitCommand()
  }

  /**
   * 执行git命令，提交代码*/
  private void executeGitCommand() {
    def branchName = getBranchName()
    if (branchName == null || branchName == "") {
      throw new GradleException("get the current branch is failed.")
    }
    def reviewParams = 'git receive-pack'
    reviewPluginExtension.reviewers.each { reviewer -> reviewParams += " --reviewer $reviewer"
    }

    //def command = "git push --receive-pack=git receive-pack --reviewer dengwei@ke.com  origin HEAD:refs/for/${branchName}"
    def command = "git push origin HEAD:refs/for/${branchName}"
    println command
    project.exec {
      try {
        executable 'bash'
        args "-c", "$command"
      } catch (Exception e) {
        println e.message
        println("=====================git push failed with exception.=========================")
      }
    }
  }

  /**
   * 获取当前分支名,并去掉前面的*号
   * @return
   */
  private getBranchName() {
    return ['bash', '-c', 'git branch | grep "*" '].execute().text.substring(2)
  }
}