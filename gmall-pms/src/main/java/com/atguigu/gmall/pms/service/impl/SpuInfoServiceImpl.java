package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.BaseAttrVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.sms.vo.SkuSaleVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescDao descDao;
    @Autowired
    private ProductAttrValueService attrValueService;
    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService saleAttrValueService;
    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Autowired
    private SpuInfoDescService spuInfoDescService;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuInfo(QueryCondition condition, Long cid) {
        // 封装分页条件
        IPage<SpuInfoEntity> page = new Query<SpuInfoEntity>().getPage(condition);
        // 封装查询条件
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        // 如果分类id不为0，要根据分类id查，否则查全部（点击查全站）
        if (cid != 0) {
            wrapper.eq("catalog_id", cid);
        }
        // 如果用户输入了检索条件，根据检索条件查
        //SELECT * FROM `pms_spu_info` WHERE catalog_id =225 and (id=3 OR spu_name LIKE "%华为%");-- 查本类
        //SELECT * FROM `pms_spu_info` WHERE id=3 OR spu_name LIKE "%华为%";-- 查全站
        String key = condition.getKey();
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(t -> t.like("spu_name", key).or().like("id", key));
        }
        return new PageVo(this.page(page, wrapper));
    }


    @Override
//    @Transactional(rollbackFor = FileNotFoundException.class,noRollbackFor = ArithmeticException.class)
//    @Transactional(timeout = 3)
//    @Transactional(readOnly = true)
    @GlobalTransactional
    public void bigSave(SpuInfoVO spuInfoVO) throws FileNotFoundException {
        // 1.保存spu相关的3张表
        // 1.1保存pms_spu_info信息
        Long spuId = saveSpuInfo(spuInfoVO);

        // 1.2. 保存pms_spu_info_desc 就是大图片 两个字段 id desc
//        this.saveSpuInfoDesc(spuInfoVO, spuId);//相同service+REQUIRES_NEW事务传播无效
        this.spuInfoDescService.saveSpuInfoDesc(spuInfoVO, spuId);//不同service+REQUIRES_NEW 事务传播有效
//        int a = 1 / 0;//测试事务传播
//        new FileInputStream(new File("xxxx"));//编译期异常，默认不回滚，尽管控制台报异常了
//        try {
//            TimeUnit.SECONDS.sleep(4);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        // 1.3. 保存pms_product_attr_value
        saveBaseAttrValue(spuInfoVO, spuId);


        // 2.保存sku相关的3张表 + 3.保存营销信息的三张表  跨模块  openfeign
        saveSkuAndSale(spuInfoVO, spuId);
        //        int a = 1 / 0;//测试分布式事务
    }

    private void saveSkuAndSale(SpuInfoVO spuInfoVO, Long spuId) {
        List<SkuInfoVO> skus = spuInfoVO.getSkus();
        if (CollectionUtils.isEmpty(skus)) {
            return;//spu没有sku
        }

        skus.forEach(skuInfoVO -> {
            //2.1 保存pms_sku_info
            skuInfoVO.setSpuId(spuId);
            skuInfoVO.setSkuCode(UUID.randomUUID().toString());
            skuInfoVO.setBrandId(spuInfoVO.getBrandId());
            skuInfoVO.setCatalogId(spuInfoVO.getCatalogId());
            List<String> images = skuInfoVO.getImages();
            //设置默认图片
            if (!CollectionUtils.isEmpty(images)) {
                skuInfoVO.setSkuDefaultImg(StringUtils.isNotBlank(skuInfoVO.getSkuDefaultImg()) ?
                        skuInfoVO.getSkuDefaultImg() : images.get(0));
            }
            this.skuInfoDao.insert(skuInfoVO);
            Long skuId = skuInfoVO.getSkuId();

            //2.2 保存pms_sku_images
            if (!CollectionUtils.isEmpty(images)) {
                List<SkuImagesEntity> skuImagesEntities = images.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setImgUrl(image);
                    skuImagesEntity.setSkuId(skuId);
                    //设置是否默认图片。
                    skuImagesEntity.setDefaultImg(StringUtils.equals(skuInfoVO.getSkuDefaultImg(), image) ? 1 : 0);
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                this.skuImagesService.saveBatch(skuImagesEntities);
            }

            //2.3保存pms_sale_attr_value   json<mysql
            List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVO.getSaleAttrs();
            if (!CollectionUtils.isEmpty(saleAttrs)) {
                saleAttrs.forEach(skuSaleAttrValueEntity -> skuSaleAttrValueEntity.setSkuId(skuId));//设置skuId
                this.saleAttrValueService.saveBatch(saleAttrs);//批量保存销售属性
            }

            //3.保存营销信息的三张表  跨模块  openfeign
            //3.1保存sms_sku_bounds
            //3.2保存sms_sku_ladder
            //3.3 保存sms_sku_full_reduction
            //feign 远程调用sms
            SkuSaleVO skuSaleVO = new SkuSaleVO();
            BeanUtils.copyProperties(skuInfoVO, skuSaleVO);//筛选相同的复制。
            skuSaleVO.setSkuId(skuId);
            this.gmallSmsClient.saveSale(skuSaleVO);
        });
    }

    private void saveBaseAttrValue(SpuInfoVO spuInfoVO, Long spuId) {
        List<BaseAttrVO> baseAttrs = spuInfoVO.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            List<ProductAttrValueEntity> attrValueEntities = baseAttrs.stream().map(baseAttrVO -> {
                ProductAttrValueEntity attrValueEntity = baseAttrVO;
                attrValueEntity.setSpuId(spuId);
                return attrValueEntity;
            }).collect(Collectors.toList());
            this.attrValueService.saveBatch(attrValueEntities);
        }
    }


//    @Transactional(propagation = Propagation.REQUIRES_NEW)//基于接口的动态代理，
    @Transactional
    public void saveSpuInfoDesc(SpuInfoVO spuInfoVO, Long spuId) {
        List<String> spuImages = spuInfoVO.getSpuImages();
        if (!CollectionUtils.isEmpty(spuImages)) {//减小数据库io
            SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
            descEntity.setSpuId(spuId);
            descEntity.setDecript(StringUtils.join(spuImages, ","));
            this.descDao.insert(descEntity);
        }
    }


    private Long saveSpuInfo(SpuInfoVO spuInfoVO) {
        spuInfoVO.setCreateTime(new Date());
        spuInfoVO.setUodateTime(spuInfoVO.getCreateTime());//vs mp的自动更新时间
        this.save(spuInfoVO);//因为spuInfoVO是继承的。形参支持多态
        return spuInfoVO.getId();
    }
}