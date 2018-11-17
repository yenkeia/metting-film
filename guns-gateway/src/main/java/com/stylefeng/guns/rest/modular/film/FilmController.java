package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.vo.CatVO;
import com.stylefeng.guns.api.film.vo.FilmVO;
import com.stylefeng.guns.api.film.vo.SourceVO;
import com.stylefeng.guns.api.film.vo.YearVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/film/")
public class FilmController {

    private static final String IMG_PRE = "http://image.impyq.com/";

    @Reference(interfaceClass = FilmServiceAPI.class)
    private FilmServiceAPI filmServiceAPI;

    // 获取首页信息（网关 API 聚合
    @RequestMapping(value = "getIndex", method = RequestMethod.GET)
    public ResponseVO getIndex() {
        FilmIndexVO filmIndexVO = new FilmIndexVO();
        // 获取 banner 信息
        filmIndexVO.setBanner(filmServiceAPI.getBanners());
        // 获取 正在热映 的电影
        filmIndexVO.setHotFilm(filmServiceAPI.getHotFilms(true, 8, 1, 1, 99, 99, 99));
        // 获取 即将上映 的电影
        filmIndexVO.setSoonFilm(filmServiceAPI.getSoonFilms(true, 8, 1, 1, 99, 99, 99));
        // 获取 票房排行榜
        filmIndexVO.setBoxRanking(filmServiceAPI.getBoxRanking());
        // 获取 受欢迎的榜单
        filmIndexVO.setExpectRanking(filmServiceAPI.getExpectRanking());
        // 获取 排名前100 的电影
        filmIndexVO.setTop100(filmServiceAPI.getTop());
        return ResponseVO.success(IMG_PRE, filmIndexVO);
    }

    // 影片条件列表查询接口
    @RequestMapping(value = "getConditionList", method = RequestMethod.GET)
    public ResponseVO getConditionList(
            @RequestParam(name = "catId", required = false, defaultValue = "99") String catId,
            @RequestParam(name = "sourceId", required = false, defaultValue = "99") String sourceId,
            @RequestParam(name = "yearId", required = false, defaultValue = "99") String yearId) {
        FilmConditionVO filmConditionVO = new FilmConditionVO();

        // 标识位
        boolean flag = false;
        // 类型集合
        List<CatVO> cats = filmServiceAPI.getCats();
        List<CatVO> catResult = new ArrayList<>();
        CatVO cat = null;
        for (CatVO catVO : cats) {
            // 判断集合是否存在catId，如果存在，则将对应的实体变成active状态
            // 6
            // 1,2,3,99,4,5 ->
            /*
                优化：【理论上】
                    1、数据层查询按Id进行排序【有序集合 -> 有序数组】
                    2、通过二分法查找
             */
            if (catVO.getCatId().equals("99")) {
                cat = catVO;
                continue;
            }
            if (catVO.getCatId().equals(catId)) {
                flag = true;
                catVO.setActive(true);
            } else {
                catVO.setActive(false);
            }
            catResult.add(catVO);
        }
        // 如果不存在，则默认将全部变为Active状态
        if (!flag) {
            cat.setActive(true);
            catResult.add(cat);
        } else {
            cat.setActive(false);
            catResult.add(cat);
        }

        // 片源集合
        flag = false;
        List<SourceVO> sources = filmServiceAPI.getSources();
        List<SourceVO> sourceResult = new ArrayList<>();
        SourceVO sourceVO = null;
        for (SourceVO source : sources) {
            if (source.getSourceId().equals("99")) {
                sourceVO = source;
                continue;
            }
            if (source.getSourceId().equals(catId)) {
                flag = true;
                source.setActive(true);
            } else {
                source.setActive(false);
            }
            sourceResult.add(source);
        }
        // 如果不存在，则默认将全部变为Active状态
        if (!flag) {
            sourceVO.setActive(true);
            sourceResult.add(sourceVO);
        } else {
            sourceVO.setActive(false);
            sourceResult.add(sourceVO);
        }

        // 年代集合
        flag = false;
        List<YearVO> years = filmServiceAPI.getYears();
        List<YearVO> yearResult = new ArrayList<>();
        YearVO yearVO = null;
        for (YearVO year : years) {
            if (year.getYearId().equals("99")) {
                yearVO = year;
                continue;
            }
            if (year.getYearId().equals(catId)) {
                flag = true;
                year.setActive(true);
            } else {
                year.setActive(false);
            }
            yearResult.add(year);
        }
        // 如果不存在，则默认将全部变为Active状态
        if (!flag) {
            yearVO.setActive(true);
            yearResult.add(yearVO);
        } else {
            yearVO.setActive(false);
            yearResult.add(yearVO);
        }
        filmConditionVO.setCatInfo(catResult);
        filmConditionVO.setSourceInfo(sourceResult);
        filmConditionVO.setYearInfo(yearResult);
        return ResponseVO.success(filmConditionVO);
    }

    @RequestMapping(value = "getFilms", method = RequestMethod.GET)
    public ResponseVO getFilms(FilmRequestVO filmRequestVO) {
        String img_pre = IMG_PRE;
        FilmVO filmVO;
        // 根据 shotType 判断影片查询类型
        switch (filmRequestVO.getShowType()) {
            // 根据 sortId 排序
            // 添加各种条件查询
            // 判断当前是第几页
            case 1:
                filmVO = filmServiceAPI.getHotFilms(
                        false, filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(),
                        filmRequestVO.getCatId());
                break;
            case 2:
                filmVO = filmServiceAPI.getSoonFilms(
                        false, filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(),
                        filmRequestVO.getCatId());
                break;
            case 3:
                filmVO = filmServiceAPI.getClassicFilms(
                        filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(),
                        filmRequestVO.getYearId(), filmRequestVO.getCatId());
                break;
            default:
                filmVO = filmServiceAPI.getHotFilms(
                        false, filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(),
                        filmRequestVO.getCatId());
                break;
        }

        return ResponseVO.success(
                filmVO.getNowPage(), filmVO.getTotalPage(),
                img_pre, filmVO.getFilmInfo());
    }
}
