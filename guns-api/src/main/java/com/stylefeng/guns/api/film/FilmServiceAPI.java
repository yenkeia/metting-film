package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

public interface FilmServiceAPI {

    // 获取 benner
    List<BannerVO> getBanners();

    // 获取热映影片
    FilmVO getHotFilms(boolean isLimit, int nums, int nowPage, int sortId, int sourceId, int yearId, int catId);

    // 获取即将上映影片【按受欢迎程度排序
    FilmVO getSoonFilms(boolean isLimit, int nums, int nowPage, int sortId, int sourceId, int yearId, int catId);

    // 获取经典影片
    FilmVO getClassicFilms(int nums, int nowPage, int sortId, int sourceId, int yearId, int catId);

    // 获取票房排行榜
    List<FilmInfo> getBoxRanking();

    // 获取人气排行榜
    List<FilmInfo> getExpectRanking();

    // 获取 top100
    List<FilmInfo> getTop();

    // ============ 获取影片条件接口
    // 分类条件
    List<CatVO> getCats();

    // 片源条件 (欧美、日韩、大陆。。。。
    List<SourceVO> getSources();

    // 获取年代
    List<YearVO> getYears();

    // 根据影片 id 或名称获取影片信息
    FilmDetailVO getFilmDetail(int searchType, String searchParam);

    // 获取影片描述信息
    FilmDescVO getFilmDesc(String filmId);

    // 获取图片地址 img
    ImgVO getImgs(String filmId);

    // 获取导演信息
    ActorVO getDectInfo(String filmId);

    // 获取演员信息 actor
    List<ActorVO> getActors(String filmId);

}
