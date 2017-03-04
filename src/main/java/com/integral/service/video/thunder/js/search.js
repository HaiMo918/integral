/**
 * Created by kris on 2017/3/4.
 */
var search = (function(){
    var $typeChoose = $('#type-choose');
    var $searchBtn = $('#search-btn');
    var $searchList = $('#search-list');
    var $searchInput = $('#search-input');
    var prototype = {
        int : function(){
            this.bindEvent();
        },
        bindEvent : function(){
            $('.s_choose').hover(function() {
                $searchList.show();
            }, function() {
                $searchList.hide();
            });
            $('[name=search-type]').unbind().click(function(event) {
                /* Act on the event */
                var select = $(this).text();
                var curVal = $typeChoose.find('em').text();
                $typeChoose.find('em').text(select);
                $(this).text(curVal);
                $searchList.hide();
            });
            //搜索按钮点击
            $('#search-btn').unbind().click(function(event) {
                var searchText = $searchInput.val();
                if(searchText == ''){
                    return;
                }else{
                    var type = $typeChoose.find('em').text();
                    if(type == '优惠券'){
                        location.href="http://jifen.xunlei.com/jifen/exchange/group/0/type/2/searchName/" +  encodeURIComponent(searchText);
                    }else if(type == '促销活动'){
                        location.href = "http://jifen.xunlei.com/fanli/?search="+encodeURIComponent(searchText) + '#content';
                    }
                }
                return false;
            });
        }

    }
    return prototype;
})();

$(function(){
    search.int();
})




