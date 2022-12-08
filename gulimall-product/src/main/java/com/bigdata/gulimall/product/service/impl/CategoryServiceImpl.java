package com.bigdata.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bigdata.common.utils.PageUtils;
import com.bigdata.common.utils.Query;
import com.bigdata.gulimall.product.dao.CategoryDao;
import com.bigdata.gulimall.product.entity.CategoryEntity;
import com.bigdata.gulimall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> getListTree() {
        // 1、 获取所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        // 2、 查找子分类
        return categoryEntities.stream()
                .filter(item -> item.getParentCid().equals(0L))
                .peek(menu -> menu.setChildren(getChildren(menu, categoryEntities)))
                .sorted(Comparator.comparingInt(o -> (o.getSort() == null ? 0 : o.getSort())))
                .sorted(Comparator.comparingInt(o -> Optional.ofNullable(o.getSort()).orElse(0)))
                .collect(Collectors.toList());
    }

    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        return all.stream()
                .filter(item -> item.getParentCid().equals(root.getCatId()))
                .peek(item -> item.setChildren(getChildren(item, all)))
                .sorted(Comparator.comparingInt(o -> Optional.ofNullable(o.getSort()).orElse(0)))
                .collect(Collectors.toList());
    }
}
