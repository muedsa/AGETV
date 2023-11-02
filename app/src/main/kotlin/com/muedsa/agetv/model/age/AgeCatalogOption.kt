package com.muedsa.agetv.model.age

import java.util.Collections

sealed class AgeCatalogOption(val text: String, val value: String) {
    data object ALL : AgeCatalogOption("全部", "all")

    data object RegionJP : AgeCatalogOption("日本", "日本")
    data object RegionCN : AgeCatalogOption("中国", "中国")
    data object RegionWW : AgeCatalogOption("欧美", "欧美")

    data object GenreTV : AgeCatalogOption("TV", "TV")
    data object GenreMovie : AgeCatalogOption("剧场版", "剧场版")
    data object GenreOVA : AgeCatalogOption("OVA", "OVA")

    data object Year2023 : AgeCatalogOption("2023", "2023")
    data object Year2022 : AgeCatalogOption("2022", "2022")
    data object Year2021 : AgeCatalogOption("2021", "2021")
    data object Year2020 : AgeCatalogOption("2020", "2020")
    data object Year2019 : AgeCatalogOption("2019", "2019")
    data object Year2018 : AgeCatalogOption("2018", "2018")
    data object Year2017 : AgeCatalogOption("2017", "2017")
    data object Year2016 : AgeCatalogOption("2016", "2016")
    data object Year2015 : AgeCatalogOption("2015", "2015")
    data object Year2014 : AgeCatalogOption("2014", "2014")
    data object Year2013 : AgeCatalogOption("2013", "2013")
    data object Year2012 : AgeCatalogOption("2012", "2012")
    data object Year2011 : AgeCatalogOption("2011", "2011")
    data object Year2010 : AgeCatalogOption("2010", "2010")
    data object Year2009 : AgeCatalogOption("2009", "2009")
    data object Year2008 : AgeCatalogOption("2008", "2008")
    data object Year2007 : AgeCatalogOption("2007", "2007")
    data object Year2006 : AgeCatalogOption("2006", "2006")
    data object Year2005 : AgeCatalogOption("2005", "2005")
    data object Year2004 : AgeCatalogOption("2004", "2004")
    data object Year2003 : AgeCatalogOption("2003", "2003")
    data object Year2002 : AgeCatalogOption("2002", "2002")
    data object Year2001 : AgeCatalogOption("2001", "2001")
    data object Year2000AndBefore : AgeCatalogOption("2000及以前", "2000")

    data object SeasonQ1 : AgeCatalogOption("1月", "1")
    data object SeasonQ2 : AgeCatalogOption("4月", "4")
    data object SeasonQ3 : AgeCatalogOption("7月", "7")
    data object SeasonQ4 : AgeCatalogOption("10月", "10")

    data object StatueSerializing : AgeCatalogOption("连载", "连载")
    data object StatueCompleteD : AgeCatalogOption("完结", "完结")
    data object StatueNotPlay : AgeCatalogOption("未播放", "未播放")

    data object LabelFunny : AgeCatalogOption("搞笑", "搞笑")
    data object LabelSport : AgeCatalogOption("运动", "运动")
    data object LabelInspirational : AgeCatalogOption("励志", "励志")
    data object LabelWarmBlood : AgeCatalogOption("热血", "热血")
    data object LabelFight : AgeCatalogOption("战斗", "战斗")
    data object LabelCompetitive : AgeCatalogOption("竞技", "竞技")
    data object LabelSchool : AgeCatalogOption("校园", "校园")
    data object LabelYouth : AgeCatalogOption("青春", "青春")
    data object LabelLove : AgeCatalogOption("爱情", "爱情")
    data object LabelInLove : AgeCatalogOption("恋爱", "恋爱")
    data object LabelAdventure : AgeCatalogOption("冒险", "冒险")
    data object LabelHarem : AgeCatalogOption("后宫", "后宫")
    data object LabelLily : AgeCatalogOption("百合", "百合")
    data object LabelLoli : AgeCatalogOption("萝莉", "萝莉")
    data object LabelMagic : AgeCatalogOption("魔法", "魔法")
    data object LabelSuspense : AgeCatalogOption("悬疑", "悬疑")
    data object LabelMystery : AgeCatalogOption("推理", "推理")
    data object LabelFantasy : AgeCatalogOption("奇幻", "奇幻")
    data object LabelScienceFiction : AgeCatalogOption("科幻", "科幻")
    data object LabelGame : AgeCatalogOption("游戏", "游戏")
    data object LabelGodDemon : AgeCatalogOption("神魔", "神魔")
    data object LabelHorror : AgeCatalogOption("恐怖", "恐怖")
    data object LabelGory : AgeCatalogOption("血腥", "血腥")
    data object LabelMecha : AgeCatalogOption("机战", "机战")
    data object LabelWar : AgeCatalogOption("战争", "战争")
    data object LabelCrime : AgeCatalogOption("犯罪", "犯罪")
    data object LabelHistory : AgeCatalogOption("历史", "历史")
    data object LabelSocial : AgeCatalogOption("社会", "社会")
    data object LabelWorkplace : AgeCatalogOption("职场", "职场")
    data object LabelDrama : AgeCatalogOption("剧情", "剧情")
    data object LabelFemboy : AgeCatalogOption("伪娘", "伪娘")
    data object LabelBL : AgeCatalogOption("耽美", "耽美")
    data object LabelChildhood : AgeCatalogOption("童年", "童年")
    data object LabelEducation : AgeCatalogOption("教育", "教育")
    data object LabelParentChild : AgeCatalogOption("亲子", "亲子")
    data object LabelLiveAction : AgeCatalogOption("真人", "真人")
    data object LabelMusical : AgeCatalogOption("歌舞", "歌舞")
    data object LabelR18 : AgeCatalogOption("肉番", "肉番")
    data object LabelPrettyGirl : AgeCatalogOption("美少女", "美少女")
    data object LabelLightNovel : AgeCatalogOption("轻小说", "轻小说")
    data object LabelVampire : AgeCatalogOption("吸血鬼", "吸血鬼")
    data object LabelFemaleOriented : AgeCatalogOption("女性向", "女性向")
    data object LabelShort : AgeCatalogOption("泡面番", "泡面番")
    data object LabelHappy : AgeCatalogOption("欢乐向", "欢乐向")

    data object ResourceBDRIP : AgeCatalogOption("BDRIP", "BDRIP")
    data object ResourceGERIP : AgeCatalogOption("AGERIP", "AGERIP")

    data object OrderByTime : AgeCatalogOption("更新时间", "time")
    data object OrderByName : AgeCatalogOption("名称", "name")
    data object OrderByHits : AgeCatalogOption("点击量", "hits")

    companion object {
        val Regions =
            listOf(ALL, RegionJP, RegionCN, RegionWW).let { Collections.unmodifiableList(it) }

        val Genres =
            listOf(ALL, GenreTV, GenreMovie, GenreOVA).let { Collections.unmodifiableList(it) }

        val Seasons = listOf(ALL, SeasonQ1, SeasonQ2, SeasonQ3, SeasonQ4).let {
            Collections.unmodifiableList(it)
        }

        val Status = listOf(
            ALL,
            StatueSerializing,
            StatueCompleteD,
            StatueNotPlay
        ).let { Collections.unmodifiableList(it) }

        val Years = listOf(
            ALL,
            Year2023,
            Year2022,
            Year2021,
            Year2020,
            Year2019,
            Year2018,
            Year2017,
            Year2016,
            Year2015,
            Year2014,
            Year2013,
            Year2012,
            Year2011,
            Year2010,
            Year2009,
            Year2008,
            Year2007,
            Year2006,
            Year2005,
            Year2004,
            Year2003,
            Year2002,
            Year2001,
            Year2000AndBefore,
        ).let { Collections.unmodifiableList(it) }

        val Labels = listOf(
            ALL,
            LabelFunny,
            LabelSport,
            LabelInspirational,
            LabelWarmBlood,
            LabelFight,
            LabelCompetitive,
            LabelSchool,
            LabelYouth,
            LabelLove,
            LabelInLove,
            LabelAdventure,
            LabelHarem,
            LabelLily,
            LabelLoli,
            LabelMagic,
            LabelSuspense,
            LabelMystery,
            LabelFantasy,
            LabelScienceFiction,
            LabelGame,
            LabelGodDemon,
            LabelHorror,
            LabelGory,
            LabelMecha,
            LabelWar,
            LabelCrime,
            LabelHistory,
            LabelSocial,
            LabelWorkplace,
            LabelDrama,
            LabelFemboy,
            LabelBL,
            LabelChildhood,
            LabelEducation,
            LabelParentChild,
            LabelLiveAction,
            LabelMusical,
            LabelR18,
            LabelPrettyGirl,
            LabelLightNovel,
            LabelVampire,
            LabelFemaleOriented,
            LabelShort,
            LabelHappy
        ).let { Collections.unmodifiableList(it) }

        val Resources =
            listOf(ALL, ResourceBDRIP, ResourceGERIP).let { Collections.unmodifiableList(it) }

        val Order =
            listOf(OrderByTime, OrderByName, OrderByHits).let { Collections.unmodifiableList(it) }
    }
}
