package com.atguigu.gmall.pms.controller;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


/**
 * 商品三级分类
 *
 * @author pakchoi
 * @email lxf@atguigu.com
 * @date 2020-09-10 22:34:03
 */
@Api(tags = "商品三级分类管理")
@RestController//http://127.0.0.1:8888/pms/category?t=1606134959329&level=0
@RequestMapping("pms/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("根据分类等级或者父id查询分类")
    @GetMapping
    public Resp<List<CategoryEntity>> queryCategoriesByPidOrLevel(@RequestParam(value = "level", defaultValue = "0")Integer level111,
                                                                  @RequestParam(value = "parentCid", required = false)Long pid111){
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        // 判断分类的级别是否为0
        if (level111 != 0) {
            wrapper.eq("cat_level", level111);
        }
        // 判断父节点的id是否为null
        if (pid111 != null) {
            wrapper.eq("parent_cid", pid111);
        }

        List<CategoryEntity> categoryEntities = categoryService.list(wrapper);
        System.out.println("干干干");
        return Resp.ok(categoryEntities);
    }


    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:category:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = categoryService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{catId}")
    @PreAuthorize("hasAuthority('pms:category:info')")
    public Resp<CategoryEntity> info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return Resp.ok(category);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:category:save')")
    public Resp<Object> save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:category:update')")
    public Resp<Object> update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:category:delete')")
    public Resp<Object> delete(@RequestBody Long[] catIds){
		categoryService.removeByIds(Arrays.asList(catIds));

        return Resp.ok(null);
    }

}
