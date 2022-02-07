package org.example;

import db.dao.CategoriesMapper;
import db.model.Categories;
import db.model.CategoriesExample;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Mybatis {
    public static void main(String[] args) throws IOException {


   String resource = "mybatis-config.xml";
   InputStream inputStream = Resources.getResourceAsStream(resource);
   SqlSessionFactory sqlSessionFactory = new
           SqlSessionFactoryBuilder().build(inputStream);


   SqlSession session = sqlSessionFactory.openSession();
   CategoriesMapper categoriesMapper = session.getMapper(CategoriesMapper.class);
   CategoriesExample example = new CategoriesExample();
   example.createCriteria().andIdEqualTo(616);

   List<Categories> list = categoriesMapper.selectByExample(example);

        System.out.println(categoriesMapper.countByExample(example));
        System.out.println(list);
    }

}