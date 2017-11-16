package com.chuckiefan

import com.chuckiefan.extension.UploadExtension
import com.chuckiefan.task.UploadTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by wangke on 2017/3/15.
 */
class UploadPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.getExtensions().create('pgyConfiguration',UploadExtension.class)
        project.task("pgyUpload", type: UploadTask){
            group = 'upload'
            description = 'apk文件上传至蒲公英的插件，由chuckiefan编写'
        }

    }
}
