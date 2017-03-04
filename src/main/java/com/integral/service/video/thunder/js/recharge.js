/**
 * Created by kris on 2017/3/4.
 */
/**
 *	迅雷积分充值模块
 *	@author zhangjinfeng<keenhome@126.com>
 *	@date 2013-08-31
 *
 */

var Recharge = (function(){
    // 迅雷会员价格表
    var vipRechargeMap = {
        0 : {
            name:'迅雷会员',
            price:{1:10,2:20,3:30,4:40,5:50,6:60,7:70,8:80,9:90,12:99},
            score:{1:10,2:20,3:30,4:40,5:50,6:60,7:70,8:80,9:90,12:99},
            url:'http://pay.vip.xunlei.com/index.html?referfrom=VIP_3724&ig=1&month='
        },
        1 : {
            name:'迅雷白金会员',
            price:{1:15,2:30,3:45,4:60,5:75,6:90,7:105,8:120,9:135,12:149},
            score:{1:15,2:30,3:45,4:60,5:75,6:90,7:105,8:120,9:135,12:149},
            url:'http://pay.vip.xunlei.com/baijin.html?referfrom=VIP_3724&ig=1&month='
        },
        2 : {
            name:'迅雷钻石会员',
            price:{1:30,2:60,6:120,12:240},
            score:{1:30,2:60,6:120,12:240},
            url:'http://pay.vip.xunlei.com/zshy.html?referfrom=VIP_3724&month='
        }
    };
    var goldBean = 0;
    var saveMoney = 0;
    var curVipType = 0;
    var curVipTime = 1;

    // 网游加速器充值配置
    var wyRechargeMap = {
        0 : {
            name:'迅雷网游加速会员',
            price:{1:15,2:30,3:45,4:60,5:75,6:90,7:105,8:120,9:135,12:149},
            score:{1:15,2:30,3:45,4:60,5:75,6:90,7:105,8:120,9:135,12:149},
            url:'http://pay.vip.xunlei.com/game.html?referfrom=VIP_3778'
        },
        1 : {
            name:'高级网游加速会员',
            price:{1:30,2:60,3:90,4:120,5:150,6:180,7:210,8:240,9:270,12:360},
            score:{1:30,2:60,3:90,4:120,5:150,6:180,7:210,8:240,9:270,12:360},
            url:'http://pay.vip.xunlei.com/highgame.html?referfrom=VIP_3778'
        }
    };
    var curWyType = 0;
    var curWyTime = 1;

    // 牛X金钻充值配置
    var niuxRechargeMap = {
        0 : {
            name:'牛X页游金钻',
            price:{1:15,2:30,3:45,4:60,5:75,6:90,7:105,8:120,9:135,10:150,12:149},
            score:{1:15,2:30,3:45,4:60,5:75,6:90,7:105,8:120,9:135,10:150,12:149},
            url:'http://vip.niu.xunlei.com/pay.html?referfrom=jifen'
        }
    };
    var curniuxType = 0;
    var curniuxTime = 1;


    var prototype ={
        setBean : function(bean){
            goldBean = bean || 0;
            this.getSaveMoney();
        },
        getSaveMoney:function(){
            saveMoney = Math.floor(goldBean/200);
        },
        init : function(score){
            this.setBean(score);
            //this.showBeanTips();
            this.vipTab();
            this.wyTab();
            this.niuxTab();
        },
        // 充值模块TAB切换
        vipTab : function(){
            var that = this;
            // 显示下拉
            $('#vipMore').click(function(){
                $('#vipTypes').show();
                return false;
            });
            // 选择
            $('#vipTypes').delegate('[index]','click',function(){
                var index = $(this).attr('index');
                curVipType = index;
                $('#selectedVipName').val(vipRechargeMap[curVipType]['name']);
                $('#vipTypes').hide();
                that.showVipTips();
                return false;
            });

            // 隐藏下拉
            $('body').click(function(){
                $('#vipTypes').hide();
            });

            //  初始化显示第一个
            $('#selectedVipName').val(vipRechargeMap[curVipType]['name']);
            $('#vipTime').val(curVipTime);
            $('#vipTypes').hide();
            that.showVipTips();

            // 输入购买时间
            $('#vipTime').keyup(function(){
                var timeValue = $(this).val();
                if( !(/\d/.test(timeValue)) ){
                    $(this).val(1).focus();
                    timeValue =1;
                }
                timeValue = parseInt(timeValue);
                if(timeValue > 9){
                    $(this).val(12).focus();
                    timeValue =12;
                }else if(timeValue<1){
                    $(this).val(1).focus();
                    timeValue =1;
                }
                curVipTime = timeValue;
                that.showVipTips();
                return false;
            });

            // 购买会员
            $('#vipCharge').click(function(){
                if(!haslogin()){
                    login();
                    return false;
                }
                // var spendBean = (parseInt($('#vipPrice').text()) - parseInt($('#vip-pay').text())) * 200;
                // window.open(vipRechargeMap[curVipType]['url']+curVipTime + '&bean=' + spendBean);
                window.open(vipRechargeMap[curVipType]['url']+curVipTime);
                return false;
            });
        },
        wyTab : function(){
            var that = this;
            // 显示下拉
            $('#wyMore').click(function(){
                $('#wyTypes').show();
                return false;
            });
            // 选择
            $('#wyTypes').delegate('[index]','click',function(){
                var index = $(this).attr('index');
                curWyType = index;
                $('#selectedWyName').val(wyRechargeMap[curWyType]['name']);
                $('#wyTypes').hide();
                that.showWyTips();
                return false;
            });

            // 隐藏下拉
            $('body').click(function(){
                $('#wyTypes').hide();
            });

            // 初始化，显示第一个
            $('#selectedWyName').val(wyRechargeMap[curWyType]['name']);
            $('#wyTypes').hide();
            $('#wyTime').val(curWyTime);
            that.showWyTips();

            // 输入购买时间
            $('#wyTime').keyup(function(){
                var timeValue = $(this).val();
                if( !(/\d/.test(timeValue)) ){
                    if(timeValue == ''){
                        timeValue = 1;
                    }else{
                        timeValue = 1;
                        $(this).val(1).focus();
                    }
                }
                timeValue = parseInt(timeValue);
                if(timeValue > 10){
                    $(this).val(12).focus();
                    timeValue =12;
                }else if(timeValue<1){
                    $(this).val(1).focus();
                    timeValue =1;
                }
                curWyTime = timeValue;
                that.showWyTips();
                return false;
            }).blur(function(event) {
                if($(this).val() == ''){
                    $(this).val(1);
                }
            });

            // 购买会员
            $('#wyCharge').click(function(){
                if(!haslogin()){
                    login();
                    return false;
                }
                // var spendBean = (parseInt($('#wyPrice').text()) - parseInt($('#wy-pay').text())) * 200;
                // window.open(wyRechargeMap[curWyType]['url']+'?month='+curWyTime +'&bean=' + spendBean);
                window.open(wyRechargeMap[curWyType]['url']+'&month='+curWyTime);
                return false;
            });
        },
        niuxTab : function(){
            var that = this;
            // 显示下拉
            $('#niuxMore').click(function(){
                $('#niuxTypes').show();
                return false;
            });
            // 选择
            $('#niuxTypes').delegate('[index]','click',function(){
                var index = $(this).attr('index');
                curniuxType = index;
                $('#selectedniuxName').val(niuxRechargeMap[curniuxType]['name']);
                $('#niuxTypes').hide();
                that.showniuxTips();
                return false;
            });

            // 隐藏下拉
            $('body').click(function(){
                $('#niuxTypes').hide();
            });

            // 初始化，显示第一个
            $('#selectedniuxName').val(niuxRechargeMap[curniuxType]['name']);
            $('#niuxTypes').hide();
            $('#niuxTime').val(curniuxTime);
            that.showniuxTips();

            // 输入购买时间
            $('#niuxTime').keyup(function(){
                var timeValue = $(this).val();
                if( !(/\d/.test(timeValue)) ){
                    $(this).val(1).focus();
                    timeValue =1;
                }
                timeValue = parseInt(timeValue);
                if(timeValue > 10){
                    $(this).val(12).focus();
                    timeValue =12;
                }else if(timeValue<1){
                    $(this).val(1).focus();
                    timeValue =1;
                }
                curniuxTime = timeValue;
                that.showniuxTips();
                return false;
            });

            // 购买会员
            $('#niuxCharge').click(function(){
                if(!haslogin()){
                    login();
                    return false;
                }
                // var spendBean = (parseInt($('#niuxPrice').text()) - parseInt($('#niux-pay').text())) * 200;
                // window.open(niuxRechargeMap[curniuxType]['url']+'&month='+curniuxTime + '&bean=' + spendBean);
                window.open(niuxRechargeMap[curniuxType]['url']+'&month='+curniuxTime);
                return false;
            });
        },
        //金豆提示
        showBeanTips : function(){
            $('[name="bean-num"]').text(goldBean);
            $('[name= "save-money"]').text(saveMoney);
        },
        showniuxTips : function(){
            $('#niuxPrice').html(niuxRechargeMap[curniuxType]['price'][curniuxTime] + '元');
            $('[name=niuxReturnScore]').html(niuxRechargeMap[curniuxType]['score'][curniuxTime]);
            //$('#niux-pay').html(this.saveNums(niuxRechargeMap[curniuxType]['score'][curniuxTime]) + '元');
        },
        showWyTips : function(){
            $('#wyPrice').html(wyRechargeMap[curWyType]['price'][curWyTime] +'元');
            $('[name=wyReturnScore]').html(wyRechargeMap[curWyType]['score'][curWyTime]);
            //$('#wy-pay').html(this.saveNums(wyRechargeMap[curWyType]['score'][curWyTime]) + '元');
        },
        showVipTips : function(){
            $('#vipPrice').html(vipRechargeMap[curVipType]['price'][curVipTime] + '元');
            $('[name=vipReturnScore]').html(vipRechargeMap[curVipType]['score'][curVipTime]);
            //$('#vip-pay').html(this.saveNums(vipRechargeMap[curVipType]['score'][curVipTime]) + '元');
        },
        saveNums : function(val){
            if( val >= saveMoney){
                return (val - saveMoney);
            }else{
                return 0;
            }
        }

    };
    return prototype;

})();

$(function(){
    // if(!haslogin()){
    //        Recharge.init(0);
    // }
    Recharge.init(0);
});