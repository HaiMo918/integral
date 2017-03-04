/**
 * Created by kris on 2017/3/4.
 */
/**
 *	迅雷积分首页模块
 *	@author caizhenbo
 *	@date 2015-03-17
 *
 */
logined_callback['index'] = function(){
    $('#index-login').show();
    $('#index-unlogin').hide();
    Index.getGoldScore();
}


var Index = (function(){
    var getGoldScoreUrl ='http://jifen.xunlei.com//call?c=user&a=getJifenScore';
    var getLotteryGoodsUrl = 'http://jifen.xunlei.com//call?c=user&a=getJifenScore';
    var getWinnerListUrl = 'http://jifen.xunlei.com//call?c=good&a=get_winner_list&page=1&callback=?';
    var navtpl = $('#nav-item-template').html();
    var hotTypetpl = $('#hottype-item-template').html();
    var hotMerchanttpl = $('#hotmerchant-item-template').html();
    var lotteryGoods = {};
    var awardBtnObj = null;
    var prototype = {
        init : function(){
            var that = this;
            this.renderMerchantNav();

        },
        //渲染返金豆商家
        renderMerchantNav:function(){
            $('#nav_list li').each(function(index, el) {
                var $this = $(this);
                var goodsId = $this.attr('goodsid');
                var data = goodsLists[goodsId];
                $this.find('.nav_link dt').eq(0).after(_.template(navtpl, {'lists':data.hot_type}));
                $this.find('.hot_classify').html(_.template(hotTypetpl, {'lists':data.hot_type}));
                $this.find('.hot_business').html(_.template(hotMerchanttpl, {'lists':data.hot_merchant}));
            });
        },
        //获取金豆的数量
        getGoldScore : function(){
            jsonCallBack(getGoldScoreUrl,function(rs){
                if (typeof(rs) =='object'){
                    // 成功则记录 cookie
                    //Sign._cookie.set(rs.data.gold+'_'+rs.data.silver, null);
                    if(rs.result == 0){
                        $('#gold-bean-um').text(rs.data.gold);
                        $('#silver-bean-um').text(rs.data.silver);
                        // Recharge.init(rs.data.gold);
                    }else{
                        $('#gold-bean-um').text('加载失败');
                    }
                }
            });
        }
    };
    return prototype;
})();

$(function(){
    //轮播
    function afterSelected(context, index, flag, oldIndex) {
        var con = context.content;
        var nav = context.nav;
        var $cla = context.selectClass;
        var oldIndex = $('.banner_control a').index($('.banner_control a.cur'));
        con.removeClass($cla).eq(index).addClass($cla);
        context.index = index;
        nav.removeClass('cur').eq(index).addClass('cur');
    };
    var ga = new GALLERY($('#lunbo li'), $('.banner_control a'), 3000, 0, 'on', afterSelected);

    $('.banner_prev').click(function(event) {
        ga.prev();
        return false;
    });
    $('.banner_next').click(function(event) {
        ga.next();
        return false;
    });

    Index.init();
});







