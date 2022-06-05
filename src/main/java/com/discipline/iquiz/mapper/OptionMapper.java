package com.discipline.iquiz.mapper;

import com.discipline.iquiz.po.Option;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OptionMapper {
    @Select({
            "<script>",
            "SELECT * FROM `option` WHERE id in",
            "<foreach collection='ids' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<Option> getOptionsByIds(@Param("ids")String[]ids);

//    @Select({
//            "<script>",
//            "SELECT * FROM `option` WHERE id in",
//            "<foreach collection='ids' item='item' open='(' separator=',' close=')'>",
//            "#{item}",
//            "</foreach>",
//            "AND tid = #{tid}",
//            "</script>"
//    })
//    List<Option> getOptionsByIdsAndTid(@Param("ids")String[]ids,@Param("tid")String tid);


    @Insert("INSERT INTO `option` (id,content,tid) VALUES(#{id},#{content},#{tid})")
    int addOption(@Param("id")String id,@Param("content")String content,@Param("tid")String tid);
}
