package com.chuckiefan.task

import com.squareup.okhttp.MediaType
import com.squareup.okhttp.MultipartBuilder
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import org.json.JSONObject

import java.util.concurrent.TimeUnit

/**
 * Created by wangke on 2017/3/15.
 * 上传apk文件任务
 */
class UploadTask extends DefaultTask {


    static String uKey
    static String _api_key
    static File file
    static Integer installType
    static String password
    static String updateDescription

    private final String API_END_POINT = "http://www.pgyer.com/apiv1"

    @Override
    Task doFirst(Action<? super Task> action) {
        dependsOn('assemble')

        return super.doFirst(action)
    }

    @TaskAction
    def upload() {
        initDatas()
        checkValues()
        String endpoint = API_END_POINT + "/app/upload"
        JSONObject result = post(endpoint)
        int code = result.get('code')
        String errorMsg = result.get('message')
        if (0 != code) {
            throw new GradleException("发布失败：$errorMsg")
        } else {
            println "发布：${file.name} 成功: ${result.toString()}"

        }

    }

    def initDatas() {
        def ext = getProject().pgyConfiguration
        if (null == ext) throw new GradleException('蒲公英未配置！')
        uKey = ext.uKey
        _api_key = ext._api_key
        file = ext.file
        installType = ext.installType
        password = ext.password
        updateDescription = ext.updateDescription
    }


    JSONObject post(String endpoint) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        MultipartBuilder multipartBuilder = new MultipartBuilder()
                .type(MultipartBuilder.FORM)

        multipartBuilder.addFormDataPart("_api_key", new String(_api_key))
        multipartBuilder.addFormDataPart("uKey", new String(uKey))
        multipartBuilder.addFormDataPart("file",
                file.name,
                RequestBody.create(MediaType.parse("application/vnd.android.package-archive"), file)
        )

        if (null != updateDescription) {

            multipartBuilder.addFormDataPart("updateDescription", new String(updateDescription))
        }
        if (null != password) {
            multipartBuilder.addFormDataPart("password", new String(password))

        }
        if (null != installType) {

            multipartBuilder.addFormDataPart("installType", installType + "")
        }

//        HashMap<String, String> params = new HashMap<String, String>()

        Request request = new Request.Builder().url(endpoint).
                post(multipartBuilder.build()).
                build()

        Response response = client.newCall(request).execute();
        if (response == null || response.body() == null) return null;
        InputStream is = response.body().byteStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        JSONObject json = new JSONObject(reader.readLine())
        is.close()

        return json


    }

    def checkValues() {


        if (null == uKey || null == _api_key || null == file) {
            throw new GradleException("uKey 或 apiKey 或 file 丢失")
        }
        if (!file.isFile()) {
            throw new GradleException('所配置文件不能是目录')
        }
        if (!file.name.endsWith('.apk')) {

            throw new GradleException('文件名不合法，需以apk结尾，请检查配置文件后再重试')
        }
    }
}
