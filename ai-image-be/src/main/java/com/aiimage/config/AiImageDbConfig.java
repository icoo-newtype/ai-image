package com.aiimage.config;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

@Configuration
@Lazy
@RequiredArgsConstructor
@MapperScan(basePackages = "com.aiimage.mapper.image", sqlSessionFactoryRef = "aiImageSqlSessionFactory")
public class AiImageDbConfig {

  private final ApplicationContext context;

  @Bean
  @ConfigurationProperties(prefix = "spring.aiimage-datasource")
  public DataSource aiImageDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  public SqlSessionFactory aiImageSqlSessionFactory(@Qualifier("aiImageDataSource") DataSource dataSource) throws Exception {
    SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
    factory.setDataSource(dataSource);
    factory.setMapperLocations(context.getResources("classpath:/mapper/image/*.xml"));
    factory.setTypeAliasesPackage("com.aiimage.model.image");
    SqlSessionFactory sqlSessionFactory = factory.getObject();
    sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);
    return sqlSessionFactory;
  }

  @Bean
  public SqlSessionTemplate aiImageSqlSessionTemplate(@Qualifier("aiImageSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }
}
