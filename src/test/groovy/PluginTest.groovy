import com.github.chuckiefan.UploadPlugin
import com.github.chuckiefan.task.UploadTask
import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class PluginTest {

    @Test
    public void obtainPlugin() {
        Project project = ProjectBuilder.builder().build()

        project.pluginManager.apply 'java'
        project.pluginManager.apply UploadPlugin
        assertTrue(project.tasks.pgyUpload instanceof UploadTask)
    }
}
