package com.zhu.controller;

import com.github.pagehelper.PageInfo;
import com.zhu.pojo.Pig;
import com.zhu.pojo.PigType;
import com.zhu.service.pig.PigService;
import com.zhu.service.pig.PigTypeService;
import com.zhu.utils.json.JSONData;
import com.zhu.utils.json.ResultJson;
import com.zhu.vo.QueryPigParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class PigController {

    @Autowired
    private PigTypeService pigTypeService;
    @Autowired
    private PigService pigService;

    @GetMapping("/pig/getTypeSum")
    public JSONData queryTypeSum(){
        List<String> types = new ArrayList<>();
        List<Integer> sums = new ArrayList<>();
        List<PigType> pigTypes = pigTypeService.queryTypeSum();
        for (int i = 0; i < pigTypes.size(); i++) {
            types.add(pigTypes.get(i).getPigType());
            sums.add(pigTypes.get(i).getPigTypeSum());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("pigType",types);
        map.put("pigTypeSum",sums);
        return JSONData.buildSuccess(map);
    }


    @GetMapping("/pigInfo/list")
    public JSONData queryAllPig(){
        List<Pig> pigs = pigService.selectAllPigBCondition(null);
        return JSONData.buildSuccess(pigs);
    }

    @PostMapping("/pigInfo/list/search/{currentPage}/{pageSize}")
    public JSONData queryAllPigOfPage(@RequestBody QueryPigParam queryPigParam,@PathVariable("currentPage") Integer currentPage,@PathVariable Integer pageSize){
        System.out.println("=========>"+queryPigParam);
        PageInfo<Pig> pigPageInfo = pigService.selectAllPigByPageQuery(queryPigParam, currentPage, pageSize);
        List<Pig> list = pigPageInfo.getList();
        long total = pigPageInfo.getTotal();
        Map<String, Object> map = new HashMap<>();
        map.put("rows",list);
        map.put("total",total);
        return JSONData.buildSuccess(map);
    }

    @PostMapping("/addPig")
    public JSONData addPig(@RequestBody Pig pig){
        pigService.insertSelective(pig);
        return JSONData.buildSuccess("添加成功");
    }


    @GetMapping("/pigInfo/{id}")
    public JSONData queryPigById(@PathVariable("id") Integer id){
        System.out.println(id);
        Pig pig = pigService.selectByPrimaryKey(id);
        return JSONData.buildSuccess(pig);
    }

    @PutMapping("/pigInfo/{id}")
    public JSONData updatePig(@PathVariable("id") Integer id,@RequestBody Pig pig){
        System.out.println("更新======>"+pig);
        pigService.updateByPrimaryKey(pig);
        return JSONData.buildSuccess("更新成功");
    }

    @DeleteMapping("/pigInfo/{id}")
    public JSONData deletePig(@PathVariable("id") Integer id){
        System.out.println("删除的id===>"+id);
        pigService.deleteByPrimaryKey(id);
        return JSONData.buildSuccess("删除成功！");
    }




}
