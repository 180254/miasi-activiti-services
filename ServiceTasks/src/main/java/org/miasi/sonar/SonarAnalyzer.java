package org.miasi.sonar;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.http.client.fluent.Request;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONObject;
import org.miasi.common.Config;

public class SonarAnalyzer implements JavaDelegate {

    private static final List<String> PROPERTIES
                                      = Arrays.asList( "# Required metadata\n",
                                                       "sonar.projectKey=java-sonar-runner-simple\n",
                                                       "sonar.projectName=Simple Java project analyzed with the SonarQube Runner\n",
                                                       "sonar.projectVersion=1.0\n",
                                                       "\n",
                                                       "# Comma-separated paths to directories with sources (required)\n",
                                                       "sonar.sources=src\n",
                                                       "\n",
                                                       "# Language\n",
                                                       "sonar.language=java\n",
                                                       "\n",
                                                       "# Encoding of the source files\n",
                                                       "sonar.sourceEncoding=UTF-8" );

    private static final String filePath = "d:\\studia\\semestr 8\\miasi\\test\\";

    public void execute( DelegateExecution delegateExecution ) throws Exception {
        String repositoryUrlStringValue = Config.readFromConfigFile().getGithubAddress();
//        FileUtils.cleanDirectory( new File( filePath ) );
        try ( Git git = Git.cloneRepository()
                .setURI( repositoryUrlStringValue )
                .setDirectory( new File( filePath ) )
                .setCloneAllBranches( true )
                .call() ) {
            git.pull();

            try ( PrintWriter writer = new PrintWriter( filePath + "sonar-project.properties", "UTF-8" ) ) {
                PROPERTIES.stream().forEach( ( property ) -> {
                    writer.println( property );
                } );
            }

            Runtime runtime = Runtime.getRuntime();
            runtime.exec( "d:\\sonar\\sonar-scanner-2.6.1\\bin\\sonar-scanner.bat", null, new File( filePath ) );

        } catch ( GitAPIException | IOException ex ) {
            System.out.println( ex );
        }

        int issuesCount = getSonarViolations();
        delegateExecution.setVariable( "issues", issuesCount );

    }

    private int getSonarViolations() throws IOException {
        String url = "http://localhost:9000/api/issues/search?componentKey=java-sonar-runner-simple";
        String content = Request.Get( url )
                .execute()
                .returnContent()
                .asString();

        JSONObject jsonObject = new JSONObject( content );

        return jsonObject.getJSONArray( "issues" ).length();

    }
}
