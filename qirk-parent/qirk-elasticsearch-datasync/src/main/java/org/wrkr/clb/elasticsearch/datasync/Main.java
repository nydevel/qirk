package org.wrkr.clb.elasticsearch.datasync;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.wrkr.clb.elasticsearch.datasync.config.DataSourceConfig;
import org.wrkr.clb.elasticsearch.datasync.config.ElasticsearchClientConfig;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final String CONFIG_CONTEXT_LOCATION = "qirk-datasync-config.xml";
    private static final String ROOT_CONTEXT_LOCATION = "qirk-datasync-root-ctx.xml";

    private static final String DATABASE_URL_PROPERTY_KEY = "database.url";
    private static final String DATABASE_AUTH_URL_PROPERTY_KEY = "database.auth.url";
    private static final String DATABASE_USERNAME_PROPERTY_KEY = "database.username";
    private static final String DATABASE_PASSWORD_PROPERTY_KEY = "database.password";

    private static final String ELASTICSEARCH_NODES_PROPERTY_KEY = "elasticsearch.nodes";
    private static final String ELASTICSEARCH_SCHEME_PROPERTY_KEY = "elasticsearch.scheme";
    private static final String ELASTICSEARCH_USERNAME_PROPERTY_KEY = "elasticsearch.username";
    private static final String ELASTICSEARCH_PASSWORD_PROPERTY_KEY = "elasticsearch.password";

    public static void main(String[] args) {
        if (args.length == 0) {
            LOG.error("Properties file name is not specified");
            return;
        }
        String propertiesFilePath = args[0];

        Properties props = new Properties();
        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            props.load(input);
        } catch (Exception e) {
            LOG.error("Exception caught during loading properties file", e);
            return;
        }

        ClassPathXmlApplicationContext configContext = new ClassPathXmlApplicationContext(CONFIG_CONTEXT_LOCATION);

        DataSourceConfig dataSourceCfg = configContext.getBean("dataSourceConfig", DataSourceConfig.class);
        dataSourceCfg.setUrl(props.getProperty(DATABASE_URL_PROPERTY_KEY));
        dataSourceCfg.setUsername(props.getProperty(DATABASE_USERNAME_PROPERTY_KEY));
        dataSourceCfg.setPassword(props.getProperty(DATABASE_PASSWORD_PROPERTY_KEY));

        DataSourceConfig authDataSourceCfg = configContext.getBean("authDataSourceConfig", DataSourceConfig.class);
        authDataSourceCfg.setUrl(props.getProperty(DATABASE_AUTH_URL_PROPERTY_KEY));
        authDataSourceCfg.setUsername(props.getProperty(DATABASE_USERNAME_PROPERTY_KEY));
        authDataSourceCfg.setPassword(props.getProperty(DATABASE_PASSWORD_PROPERTY_KEY));

        ElasticsearchClientConfig elasticsearchCfg = configContext.getBean(ElasticsearchClientConfig.class);
        elasticsearchCfg.setJoinedNodes(props.getProperty(ELASTICSEARCH_NODES_PROPERTY_KEY));
        elasticsearchCfg.setScheme(props.getProperty(ELASTICSEARCH_SCHEME_PROPERTY_KEY));
        elasticsearchCfg.setUsername(props.getProperty(ELASTICSEARCH_USERNAME_PROPERTY_KEY));
        elasticsearchCfg.setPassword(props.getProperty(ELASTICSEARCH_PASSWORD_PROPERTY_KEY));

        ClassPathXmlApplicationContext rootContext = new ClassPathXmlApplicationContext(
                new String[] { ROOT_CONTEXT_LOCATION }, configContext);
        DataSynchronizer dataSynchronizer = rootContext.getBean(DataSynchronizer.class);

        dataSynchronizer.synchronize(Arrays.copyOfRange(args, 1, args.length));

        rootContext.close();
        configContext.close();
        return;
    }
}
