/**
 * Created by kris on 2017/3/22.
 */
(function e(t, n, r) {
    function s(o, u) {
        if (!n[o]) {
            if (!t[o]) {
                var a = typeof require == "function" && require;
                if (!u && a)return a(o, !0);
                if (i)return i(o, !0);
                throw new Error("Cannot find module '" + o + "'");
            }
            var f = n[o] = {exports: {}};
            t[o][0].call(f.exports, function (e) {
                var n = t[o][1][e];
                return s(n ? n : e)
            }, f, f.exports, e, t, n, r)
        }
        return n[o].exports
    }

    var i = typeof require == "function" && require;
    for (var o = 0; o < r.length; o++)s(r[o]);
    return s
})({
    1: [function (require, module, exports) {
        var AD = require("./modules/ads.js");
        var RandomKey = require("./modules/uuid.js");
        var AccountRisk = require("./modules/accoun-trisk.js");
        require("./modules/capslock.js");
        require("./modules/placeholder.js");
        require("../../plugins/md5.js");
        var phoneTool = require("./modules/phone-login.js");
        var mars = require("./modules/mars.js");
        window.Tool = {
            ui: {
                init: function () {
                    this.headTip();
                    this.loginTypeSwitch()
                }, loginTypeSwitch: function () {
                    config.jqLoginType.on("click", function () {
                        if (this.value == 1) config.jqSingleFunction.addClass("z-panel-switch"); else config.jqSingleFunction.removeClass("z-panel-switch")
                    })
                },
                headTip: function () {
                    var nickname = $.Cookie.get("VipRNAME"), url = document.location.href.toLowerCase();
                    if (!nickname || (nickname === "" || nickname === "\u552f\u54c1\u4f1a\u4f1a\u5458")) config.jqHeadTip.html("\u5982\u6709\u8d26\u53f7\uff0c\u8bf7\u767b\u5f55"); else {
                        nickname = nickname.length > 6 ? nickname.substr(0, 6) + "..." : nickname;
                        config.jqHeadTip.html("<b>" + nickname + "</b>\uff0c\u6b22\u8fce\u56de\u6765\uff01\u8bf7\u767b\u5f55")
                    }
                }, moreThird: function () {
                    config.jqControlThird.toggleClass("z-open");
                    config.jqMoreThird.toggleClass("z-third-way-in")
                },
                verifyCode: function () {
                    config.jqVerifyCode.removeClass("f-hidden");
                    Tool.module.changeCode();
                    Tool.event.changeCode()
                }, hideVerifyCode: function () {
                    config.jqVerifyCode.addClass("f-hidden");
                    config.jqLoginCode.val("");
                    config.requestKey = ""
                }, capslock: function (isCapslock) {
                    config.jqCapslockTip[isCapslock ? "addClass" : "removeClass"]("z-ui-tooltips-in")
                }
            }, event: {
                init: function () {
                    var that = this;
                    $(document).placeholder();
                    that.moreThird();
                    that.submit();
                    $.Listeners.sub("inputBindFinish").onsuccess(function () {
                        that.loginNameInput();
                        that.pwdInput()
                    });
                    $.Listeners.sub("needCaptcha").onsuccess(function () {
                        that.captchaInput()
                    })
                }, moreThird: function () {
                    config.jqControlThird.on("click", function () {
                        Tool.ui.moreThird()
                    })
                }, changeCode: function () {
                    config.jqVerifyImgWrap.add(config.jqChangeCode).on("click", function () {
                        config.jqLoginCode.tip.status();
                        Tool.module.changeCode()
                    })
                }, loginNameInput: function () {
                    config.jqLoginName.on("blur", function () {
                        Tool.module.valid.loginName(config.jqLoginName)
                    })
                }, pwdInput: function () {
                    config.jqLoginPwd.on("keypress",
                        function (e) {
                            Tool.ui.capslock(capslock.detect(e))
                        }).on("blur", function () {
                        Tool.ui.capslock(false);
                        Tool.module.valid.pwd(config.jqLoginPwd)
                    })
                }, captchaInput: function () {
                    config.jqLoginCode.off("blur").off("input").off("propertychange").on("blur", function () {
                        var _val = $(this).val();
                        var valid = Tool.module.valid.code(config.jqLoginCode, _val);
                        if (valid) Tool.module.ajaxCheckCaptcha(config.jqLoginCode, _val)
                    }).on("focus", function () {
                        if (config.jqLoginCode.hasClass("z-input-success"))return;
                        config.jqLoginCode.val("")
                    })
                },
                submit: function () {
                    config.jqLoginSubmit.on("click", function (event) {
                        event.preventDefault();
                        setTimeout(function () {
                            Tool.module.formSubmit()
                        }, 100)
                    });
                    $(document).on("keydown", function (event) {
                        if (event.keyCode == 13) {
                            config.jqLoginName.blur();
                            config.jqLoginSubmit.trigger("click")
                        }
                    })
                }
            }, module: {
                init: function () {
                    AD.init();
                    this.autocompleteLoginName();
                    this.inputBindTip(config.aFormInput)
                }, redirect: function () {
                    window.location.href = config.redirectUrl
                }, autocompleteLoginName: function () {
                    var remLoginName = config.userName;
                    if (remLoginName) {
                        if (!("placeholder" in document.createElement("input"))) config.jqLoginName.trigger("keypress");
                        config.jqLoginName.focus().val(remLoginName)
                    }
                }, inputBindTip: function (arr) {
                    function Tip(ele) {
                        this.$input = ele;
                        this.$tip = ele.closest(".u-form-item").next(".u-form-tip")
                    }

                    Tip.prototype.status = function (status, msg, errorMark) {
                        this.$input.removeClass("z-input-error z-input-success");
                        var msg = status == "error" ? msg || "" : "";
                        if (!errorMark) (status == "success" || status == "error") && this.$input.addClass("z-input-" +
                            status);
                        this.$tip.html(msg)
                    };
                    $(arr).each(function (num, ele) {
                        ele.tip = new Tip(ele)
                    });
                    $.Listeners.pub("inputBindFinish").success()
                }, changeCode: function () {
                    var src = Tool.data.rootPath + "/captcha/getCaptcha?type=0&vipc=" + encodeURIComponent(config.requestKey) + "&v=" + (new Date).getTime();
                    if (!config.jqVerifyImg) {
                        config.jqVerifyImg = $('<img width="49" height="34" />');
                        config.jqVerifyImg.attr("src", src).on("error", function () {
                            $(this).attr("src", "//member-ssl.vipstatic.com/img/share/placeholder.png");
                            config.jqLoginCode.tip.status("error",
                                "\u7cfb\u7edf\u5f02\u5e38\uff0c\u8bf7\u5237\u65b0\u9875\u9762\u518d\u8bd5\u3002")
                        });
                        config.jqVerifyImgWrap.append(config.jqVerifyImg)
                    } else config.jqVerifyImg.attr("src", src);
                    config.jqLoginCode.removeClass("z-input-error z-input-success");
                    return false
                }, ajaxCheckCaptcha: function (ele, val) {
                    var isVaild = "success";
                    var msg = "";
                    $.ajax({
                        type: "POST",
                        url: Tool.data.rootPath + "/captcha/ajaxCheckCaptcha",
                        data: {"captcha": val, "vipc": config.requestKey, "anticache": (new Date).getTime(), "type": 0},
                        dataType: "json",
                        success: function (data) {
                            if (data.result ==
                                "haslogin") Tool.module.redirect();
                            if (!data.result) {
                                isVaild = "error";
                                msg = data.errorCode != 20203 ? "\u9a8c\u8bc1\u7801\u6709\u8bef" : "\u9a8c\u8bc1\u7801\u5df2\u8fc7\u671f";
                                config.requestKey = data.vipc;
                                Tool.module.changeCode()
                            }
                            ele.tip.status(isVaild, msg)
                        }
                    })
                }, formSubmit: function () {
                    var submitBtn = config.jqLoginSubmit;
                    if (submitBtn.hasClass("z-ui-btn-loading"))return false;
                    if (!this.valid.submit())return false;
                    mars.send();
                    submitBtn.addClass("z-ui-btn-loading");
                    $.ajax({
                        url: Tool.data.rootPath + "/login",
                        type: "post",
                        dataType: "json",
                        data: {
                            loginName: config.jqLoginName.val(),
                            password: config.jqLoginPwd.val(),
                            remUser: +config.jqRememberName.prop("checked"),
                            vipc: config.requestKey,
                            captcha: config.isNeedCheckCaptcha ? config.jqLoginCode.val() : ""
                        },
                        success: function (data) {
                            if (data.result == "haslogin") Tool.module.redirect(); else if (data.result == "error") {
                                Tool.module.submitError(data.errorCode, data);
                                submitBtn.removeClass("z-ui-btn-loading")
                            } else if (data.result == "success") Tool.module.afterLogin(data.data.signedApiUrl, !!data.data.illegalState);
                            else {
                                Tool.module.submitError(6, data);
                                submitBtn.removeClass("z-ui-btn-loading")
                            }
                        }
                    })
                }, submitError: function (status, rtn) {
                    var vipc;
                    var signedApiUrl;
                    if (rtn.data) {
                        vipc = rtn.data.vipc;
                        signedApiUrl = rtn.data.signedApiUrl
                    }
                    if (vipc !== null && (vipc !== undefined && vipc !== "")) {
                        config.requestKey = vipc;
                        config.isNeedCheckCaptcha = true;
                        Tool.ui.verifyCode();
                        $.Listeners.pub("needCaptcha").success()
                    } else {
                        Tool.ui.hideVerifyCode();
                        config.isNeedCheckCaptcha = false
                    }
                    if (isNaN(status))return false;
                    var errorStatus = status;
                    var loginError =
                        {
                            0: [config.jqLoginCode, "\u9a8c\u8bc1\u7801\u6709\u8bef"],
                            1: [config.jqLoginName, "\u767b\u5f55\u540d\u548c\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a"],
                            2: [config.jqLoginName, "\u767b\u5f55\u540d\u6709\u8bef"],
                            3: [config.jqLoginPwd, '\u767b\u5f55\u540d\u6216\u5bc6\u7801\u9519\u8bef<a href="' + Tool.data.rootPath + '/register" title="\u514d\u8d39\u6ce8\u518c" target="_blank" class="u-link">\u514d\u8d39\u6ce8\u518c</a>\u6216<a href="https://safe.vip.com/login/findPW/page" title="\u627e\u56de\u5bc6\u7801" target="_blank" class="u-link">\u627e\u56de\u5bc6\u7801</a>?',
                                true],
                            4: [config.jqLoginPwd, '\u767b\u5f55\u540d\u6216\u5bc6\u7801\u9519\u8bef<a href="' + Tool.data.rootPath + '/register" title="\u514d\u8d39\u6ce8\u518c" target="_blank" class="u-link">\u514d\u8d39\u6ce8\u518c</a>\u6216<a href="https://safe.vip.com/login/findPW/page" title="\u627e\u56de\u5bc6\u7801" target="_blank" class="u-link">\u627e\u56de\u5bc6\u7801</a>?', true],
                            5: [config.jqLoginName, "\u8d26\u6237\u5df2\u88ab\u51bb\u7ed3\uff0c\u8bf7\u8054\u7cfb\u5ba2\u670d"],
                            6: [config.jqLoginName, "\u60a8\u7684\u8d26\u6237\u53ef\u80fd\u5b58\u5728\u5f02\u5e38\uff0c\u672c\u6b21\u767b\u5f55\u5931\u8d25\u3002\u5982\u6709\u9700\u8981\uff0c\u8bf7\u8054\u7cfb\u5ba2\u670d\uff1a400-6789-888"],
                            8: [config.jqLoginCode, "\u4e3a\u4e86\u60a8\u7684\u8d26\u6237\u5b89\u5168\uff0c\u8bf7\u8f93\u5165\u9a8c\u8bc1\u7801\u3002"],
                            17: [config.jqLoginName, "\u767b\u5f55\u5931\u8d25\uff0c\u8bf7\u660e\u65e5\u518d\u8bd5\u6216\u8054\u7cfb\u5ba2\u670d"]
                        };
                    if (errorStatus == 6) {
                        alert(loginError[errorStatus][1]);
                        return false
                    }
                    if (errorStatus == 18) {
                        window.usernameDialog = $.Dialog({
                            model: true,
                            opacity: 0.5,
                            title: "\u8bf7\u9009\u62e9\u60a8\u8981\u767b\u5f55\u7684\u8d26\u53f7",
                            size: "medium",
                            content: $.Tpl("J_username_tpl", rtn.data),
                            closeBtnAttr: {"mars_sead": "383|2|4|17"},
                            showEvent: function () {
                                $("input[name=username]").eq(0).prop("checked", true);
                                var _list = $(".m-username-list");
                                if (_list.find("li").length > 5) _list.addClass("z-list-over")
                            },
                            closeEvent: function () {
                                this.remove()
                            }
                        });
                        usernameDialog.open();
                        $(".J-username-submit").on("click", function () {
                            var _data = {
                                selectId: $("input[name=username]:checked").val(),
                                loginName: $("input[name=username]:checked").data("username")
                            };
                            Tool.module.selectAccountLogin(_data)
                        });
                        return false
                    }
                    if (errorStatus ==
                        16 && (signedApiUrl && signedApiUrl != "")) {
                        var tpl = $.Tpl("J_loginChallenge_tpl");
                        var dialog = $.Dialog({
                            node: "#J_dialog",
                            custom: true,
                            customStyle: true,
                            size: [400, 246],
                            content: tpl
                        });
                        dialog.open();
                        $(".u-loginChallenge-btn").on("click", function () {
                            window.location.href = signedApiUrl
                        });
                        var times = 3;
                        var callself = function () {
                            $(".u-left-time").html(times);
                            if (times !== 0) {
                                times--;
                                setTimeout(callself, 1E3)
                            } else window.location.href = signedApiUrl
                        };
                        callself();
                        return false
                    }
                    if (!loginError[errorStatus]) {
                        alert("\u8bf7\u6c42\u5931\u8d25\uff0c\u8bf7\u5237\u65b0\u9875\u9762\u91cd\u8bd5");
                        return false
                    }
                    loginError[errorStatus][0].tip.status("error", loginError[errorStatus][1], loginError[errorStatus][2]);
                    return false
                }, selectAccountLogin: function (data) {
                    var submitBtn = config.jqLoginSubmit;
                    $.ajax({
                        url: Tool.data.rootPath + "/login/selectAccountLogin",
                        type: "post",
                        data: data,
                        dataType: "json",
                        success: function (data) {
                            usernameDialog.close();
                            if (data.result == "haslogin") Tool.module.redirect(); else if (data.result == "error") {
                                Tool.module.submitError(data.errorCode, data);
                                submitBtn.removeClass("z-ui-btn-loading")
                            } else if (data.result ==
                                "success") Tool.module.afterLogin(data.data.signedApiUrl, !!data.data.illegalState); else {
                                Tool.module.submitError(6, data);
                                submitBtn.removeClass("z-ui-btn-loading")
                            }
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            Tool.module.redirect();
                            return
                        }
                    })
                }, afterLogin: function (url, isRisk) {
                    $.ajax({
                        url: url, type: "get", dataType: "jsonp", success: function (result) {
                            if (result.status == 1) {
                                if (isRisk) {
                                    AccountRisk.init(function () {
                                        Tool.module.redirect()
                                    }, config.jqLoginPwd.val());
                                    return
                                }
                                Tool.module.needBindPhone()
                            } else alert("\u8bf7\u6c42\u5931\u8d25\uff0c\u8bf7\u5237\u65b0\u9875\u9762\u91cd\u8bd5")
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            Tool.module.redirect();
                            return
                        }
                    })
                }, needBindPhone: function () {
                    var phoneComponent = new PhoneBind({
                        phoneNum: "",
                        isBg: true,
                        haveTip: true,
                        show: false,
                        success: function (phone) {
                            Tool.module.redirect()
                        },
                        cancel: function () {
                            Tool.module.redirect()
                        }
                    });
                    $.ajax({
                        type: "get",
                        url: "//myi.vip.com/jsonp/api/phone/phone_safe_number",
                        dataType: "jsonp",
                        success: function (result) {
                            if (result.code == 200 && !result.data)try {
                                phoneComponent.show()
                            } catch (e) {
                                Tool.module.redirect()
                            } else Tool.module.redirect()
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            Tool.module.redirect()
                        }
                    })
                }, valid: {
                    loginName: function (ele) {
                        var _val = ele.val().replace(/\s/g, "");
                        var isVaild = "";
                        var msg = "";
                        ele.val(_val);
                        if (_val == "") {
                            isVaild = "error";
                            msg = "\u8bf7\u8f93\u5165\u767b\u5f55\u540d"
                        } else if (!/^[0-9a-zA-Z@\.\-_]+$/.test(_val)) {
                            isVaild = "error";
                            msg = "\u767b\u5f55\u540d\u6709\u8bef"
                        }
                        ele.tip.status(isVaild, msg);
                        return isVaild == ""
                    }, code: function (ele) {
                        var _val = ele.val();
                        var isVaild = "";
                        var msg = "";
                        if (!_val || !config.requestKey) {
                            isVaild = "error";
                            msg = "\u8bf7\u8f93\u5165\u9a8c\u8bc1\u7801";
                            ele.tip.status(isVaild, msg)
                        }
                        return isVaild == ""
                    }, pwd: function (ele) {
                        var _val = ele.val().replace(/\s/g, "");
                        var isVaild = "";
                        var msg = "";
                        ele.val(_val);
                        if (_val == "") {
                            isVaild = "error";
                            msg = "\u8bf7\u8f93\u5165\u5bc6\u7801"
                        }
                        ele.tip.status(isVaild, msg);
                        return isVaild == ""
                    }, submit: function () {
                        var result = true;
                        if (!this.loginName(config.jqLoginName)) result = false;
                        if (!this.pwd(config.jqLoginPwd)) result = false;
                        if (config.isNeedCheckCaptcha && (!this.code(config.jqLoginCode) || config.jqLoginCode.hasClass("z-input-error"))) result =
                            false;
                        return result
                    }
                }
            }, data: {rootPath: "https://passport.vip.com"}, init: function () {
                this.ui.init();
                this.event.init();
                this.module.init()
            }
        };
        window.phoneLoginUrl = {mobileLoginUrl: "/mobileLogin", mobileLoginWithPWUrl: "/mobileLogin/mobileLoginWithPW"};
        window.config = {
            init: function () {
                this.jqHeadTip = $("#J_head_tip");
                this.jqSingleFunction = $("#J_single_function");
                this.jqLoginType = $("input[name=J_login_type]");
                this.jqLoginName = $("#J_login_name");
                this.jqLoginPwd = $("#J_login_pwd");
                this.jqLoginCode = $("#J_login_code");
                this.jqLoginSubmit = $("#J_login_submit");
                this.jqChangeCode = $("#J_change_code");
                this.jqVerifyImgWrap = $("#J_verify_img_wrap");
                this.jqVerifyCode = $(".J_verify_code");
                this.jqCapslockTip = $("#J_capslock_tip");
                this.jqRememberName = $("#J_remember_username");
                this.jqPhoneMsg = $("#J_phone_msg");
                this.jqLoginPhone = $("#J_login_phone");
                this.jqPhoneCode = $("#J_phone_code");
                this.jqPhoneSubmit = $("#J_phone_submit");
                this.jqPhoneVerifyImgWrap = $("#J_phoneverify_img_wrap");
                this.jqPhonechangeCode = $("#J_phonechange_code");
                this.jqPhoneverifyCode =
                    $("#J_phoneverify_code");
                this.jqRememberPhone = $("#J_remember_phone");
                this.jqPhoneMsgGet = $("#J_phone_msg_get");
                this.jqPasswordSubmit = $("#J_password-submit");
                this.jqPhoneMain = $("#J_phone_main");
                this.jqNewPwd = $("#J_newPwd");
                this.jqAgainPwd = $("#J_againPwd");
                this.jqControlThird = $("#J_more_third_control");
                this.jqMoreThird = $("#J_more_third_wrap");
                this.requestKey = "";
                this.processId = "";
                this.isNeedCheckCaptcha = false;
                this.aFormInput = [this.jqLoginName, this.jqLoginPwd, this.jqLoginCode];
                this.phoneFormInput = [this.jqLoginPhone,
                    this.jqPhoneMsg, this.jqPhoneCode];
                this.redirectUrl = $.Cookie.get("vipshop_passport_src");
                this.userName = $.Cookie.get("login_username");
                this.requestPhoneKey = "";
                this.loginPhone = $.Cookie.get("login_mobile")
            }
        };
        window.phoneLoginSuccess = function (data) {
            Tool.module.afterLogin(data.data.signedApiUrl, !!data.data.illegalState)
        };
        $(function () {
            config.init();
            Tool.init();
            phoneTool.init();
            mars.init()
        })
    }, {
        "../../plugins/md5.js": 11,
        "./modules/accoun-trisk.js": 2,
        "./modules/ads.js": 3,
        "./modules/capslock.js": 4,
        "./modules/mars.js": 5,
        "./modules/phone-login.js": 7,
        "./modules/placeholder.js": 8,
        "./modules/uuid.js": 10
    }],
    2: [function (require, module, exports) {
        module.exports = {
            init: function (cb, oldPwd) {
                if ($("#J_risktips")[0])return false;
                var that = this;
                that.config = {
                    closeFunc: cb,
                    oldPwd: oldPwd,
                    $mask: $(".ui-window-mask"),
                    nickname: $.Cookie.get("VipRNAME") || "",
                    username: $.Cookie.get("login_username"),
                    capsLock: false,
                    timeout: {delay: null},
                    interval: {countdown: null}
                };
                that.configReset = function () {
                    that.config.nickname = $.Cookie.get("VipRNAME") || "", that.config.$risk =
                        $("#J_risktips");
                    that.config.$mask = $(".ui-window-mask");
                    that.config.$risk_pwd = $(".J_risk_pwd");
                    that.config.$risk_pwd1 = $("#J_risk_pwd1");
                    that.config.$risk_pwd2 = $("#J_risk_pwd2");
                    that.config.$show_warn1 = $("#J_show_warn1");
                    that.config.$show_warn2 = $("#J_show_warn2");
                    that.config.$content = $("#J_risktips_content");
                    that.config.$submit = $("#J_submit_editorpwd");
                    that.config.$success = $("#J_risk_succ");
                    that.config.$fail = $("#J_risk_fail");
                    that.config.$iknow = $("#J_risk_iknow");
                    that.config.$again = $("#J_risk_again");
                    that.config.$second = $(".J_risk_second");
                    that.config.$close = $(".J_risktips_close");
                    that.config.$degree = $("#J_degree")
                };
                that.ui = {
                    init: function () {
                        that.ui.dialogOpen()
                    }, dialogOpen: function () {
                        var _mask = that.config.$mask[0] ? "" : '<div class="ui-window-mask"></div>';
                        $("body").append($(_mask + $("#J_risktips_tpl").html()));
                        that.configReset();
                        that.config.$risk.find(".J_risk_username").html(that.config.nickname);
                        if (window.location.search.indexOf("gotype=2") >= 0) {
                            that.config.$close.removeClass("f-hidden");
                            that.config.$risk.addClass("z-dialog-small")
                        }
                        var left =
                            ($(window).width() - that.config.$risk.width()) / 2, top = ($(window).height() - that.config.$risk.height()) / 2;
                        that.config.$risk.css({"left": left, "top": top}).addClass("z-ui-dialog-in");
                        that.config.$mask.show();
                        that.config.$risk_pwd1.parent().placeholder();
                        that.config.$risk_pwd2.parent().placeholder()
                    }, dialogClose: function () {
                        that.config.$mask.hide();
                        that.config.$risk.remove();
                        that.config.closeFunc && that.config.closeFunc()
                    }, focuswarn: function (result, iswarn) {
                        var red_border_warn = "z-ui-form-item-warning";
                        var red_tips_warn =
                            "z-ui-tooltips-in";
                        var blue_warn = "z-ui-tooltips-info";
                        var $elem = result.elem;
                        that.config.$content.find(".z-ui-tooltips-in").removeClass(red_tips_warn);
                        if (!result.result)if (iswarn) {
                            $elem.find(".J_err_msg").html(result.tips);
                            $elem.removeClass([blue_warn, red_border_warn].join(" ")).addClass(red_tips_warn)
                        } else {
                            $elem.find(".J_warn_msg").html(result.tips);
                            $elem.removeClass([red_tips_warn, red_border_warn].join(" ")).addClass(blue_warn)
                        } else $elem.removeClass([red_border_warn, red_tips_warn, blue_warn].join(" "))
                    },
                    blurwarn: function (result) {
                        var blue_warn = "z-ui-tooltips-info";
                        var red_border_warn = "z-ui-form-item-warning";
                        var red_tips_warn = "z-ui-tooltips-in";
                        if (!result.result) result.elem.removeClass([red_tips_warn, blue_warn].join(" ")).addClass(red_border_warn); else result.elem.removeClass([red_tips_warn, blue_warn, red_tips_warn].join(" "))
                    }, submitType: function (step) {
                        var c_loading = "z-ui-btn-loading";
                        var c_disable = "ui-btn-disable";
                        that.config.$submit.data("cansubmit", false);
                        if (step == 0) {
                            that.config.$submit.removeClass(c_loading).addClass(c_disable);
                            that.config.$content.removeClass("f-hidden");
                            that.config.$fail.addClass("f-hidden")
                        } else if (step == 1) {
                            that.config.$submit.removeClass(c_loading + " " + c_disable);
                            that.config.$submit.data("cansubmit", true);
                            that.config.$content.removeClass("f-hidden");
                            that.config.$fail.addClass("f-hidden")
                        } else if (step == 2) that.config.$submit.addClass(c_loading).removeClass(c_disable); else if (step == 3) {
                            that.config.$content.addClass("f-hidden");
                            that.config.$success.removeClass("f-hidden")
                        } else if (step == 4) {
                            that.config.$content.addClass("f-hidden");
                            that.config.$fail.removeClass("f-hidden")
                        }
                    }, degree: function (lv) {
                        var _lv = ["z-step-0", "z-step-1", "z-step-2", "z-step-3"];
                        var _type = ["\u5f31", "\u5f31", "\u4e2d", "\u9ad8"];
                        that.config.$degree.removeClass(_lv.join(" ")).addClass(_lv[lv]);
                        that.config.$degree.find(".u-progess-text").html(_type[lv])
                    }
                };
                that.ev = {
                    init: function () {
                        that.ui.init();
                        var _init = ["focus", "blur", "press", "submit", "click"];
                        for (var i = 0; i < _init.length; i++)that.ev[_init[i]]()
                    }, checkpwd_1: function (iswarn) {
                        var letterPattern = /[a-zA-Z]+/, numbericPattern =
                            /[0-9]+/, specialPattern = /[~!@#$%^&*()_+`\-=\[\]\\{}\|;':",\.\/<>\?]/, illegalPattern = /[^a-zA-Z0-9~!@#$%^&*()_+`\-=\[\]\\{}\|;':",\.\/<>\?]+/, result = new Object, pwd = that.config.$risk_pwd1.val(), iswarn = iswarn || that.config.$risk_pwd1.data("iswarn"), calcCharsCategory = function (pwd) {
                            var category = 0;
                            if (letterPattern.test(pwd)) category++;
                            if (numbericPattern.test(pwd)) category++;
                            if (specialPattern.test(pwd)) category++;
                            return category
                        };
                        result.elem = that.config.$show_warn1;
                        result.result = false;
                        result.degree = 0;
                        if (pwd &&
                            calcCharsCategory(pwd) <= 1) result.degree = 1; else if (pwd && (calcCharsCategory(pwd) == 2 && pwd.length < 6)) result.degree = 1; else if (pwd && (calcCharsCategory(pwd) == 2 && (pwd.length >= 6 && pwd.length < 12))) result.degree = 2; else if (pwd && (calcCharsCategory(pwd) == 2 && pwd.length >= 12)) result.degree = 3; else if (pwd && (calcCharsCategory(pwd) == 3 && pwd.length < 6)) result.degree = 1; else if (pwd && (calcCharsCategory(pwd) == 3 && (pwd.length >= 6 && pwd.length < 10))) result.degree = 2; else if (pwd && (calcCharsCategory(pwd) == 3 && pwd.length >= 10)) result.degree =
                            3;
                        if (!pwd && !iswarn) result.tips = "\u5bc6\u7801\u4e3a6-20\u4f4d\u5b57\u7b26\uff0c\u5305\u542b\u5b57\u6bcd\u3001\u6570\u5b57\u548c\u7b26\u53f7\u4e2d\u7684\u4e24\u79cd"; else if (!pwd && iswarn) result.tips = "\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a"; else if (pwd.length < 6) result.tips = "\u5bc6\u7801\u4e0d\u80fd\u5c11\u4e8e6\u4f4d"; else if (illegalPattern.test(pwd)) {
                            result.degree = 1;
                            result.tips = "\u5bc6\u7801\u4e2d\u5305\u542b\u975e\u6cd5\u5b57\u7b26"
                        } else if (calcCharsCategory(pwd) <= 1) result.tips = "\u5bc6\u7801\u9700\u8981\u5305\u542b\u5b57\u6bcd\u3001\u6570\u5b57\u3001\u7b26\u53f7\u4e2d\u81f3\u5c11\u4e24\u79cd";
                        else if (that.config.username == pwd) result.tips = "\u5bc6\u7801\u4e0d\u80fd\u4e0e\u767b\u5f55\u540d\u4e00\u81f4"; else if (that.config.oldPwd == pwd) result.tips = "\u65b0\u5bc6\u7801\u4e0d\u80fd\u4e0e\u65e7\u5bc6\u7801\u4e00\u81f4"; else result.result = true;
                        return result
                    }, checkpwd_2: function (iswarn) {
                        var result = new Object, pwd = that.config.$risk_pwd2.val(), iswarn = iswarn || that.config.$risk_pwd2.data("iswarn"), rc_pwd = that.config.$risk_pwd1.val();
                        result.result = false;
                        result.elem = that.config.$show_warn2;
                        if (!pwd && !iswarn) result.tips =
                            "\u8bf7\u518d\u6b21\u8f93\u5165\u4e0a\u9762\u7684\u5bc6\u7801"; else if (!pwd && iswarn) result.tips = "\u4e24\u6b21\u8f93\u5165\u7684\u5bc6\u7801\u4e0d\u4e00\u81f4"; else if (pwd && rc_pwd != pwd) result.tips = "\u4e24\u6b21\u8f93\u5165\u7684\u5bc6\u7801\u4e0d\u4e00\u81f4\uff0c\u8bf7\u91cd\u8bd5"; else if (pwd && rc_pwd == pwd) result.result = true;
                        return result
                    }, cansubmit: function () {
                        if (that.ev.checkpwd_1().result && that.ev.checkpwd_2().result)return true;
                        return false
                    }, delayhide: function (index) {
                        that.config.timeout.delay =
                            setTimeout(function () {
                                that.ui.focuswarn({result: true, elem: that.config["$show_warn" + index]})
                            }, 3E3)
                    }, click: function () {
                        that.config.$iknow.click(function () {
                            that.ui.dialogClose();
                            clearInterval(that.config.interval.countdown)
                        });
                        that.config.$again.click(function () {
                            that.ui.submitType(0);
                            that.config.$risk_pwd1.val("");
                            that.config.$risk_pwd2.val("");
                            that.ui.degree(0);
                            clearInterval(that.config.interval.countdown)
                        });
                        that.config.$close.click(function () {
                            that.ui.dialogClose()
                        })
                    }, focus: function () {
                        that.config.$risk_pwd1.focus(function () {
                            var iswarn =
                                that.config.$risk_pwd1.data("iswarn");
                            that.config.$degree.removeClass("f-hidden");
                            that.ui.focuswarn(that.ev.checkpwd_1(iswarn), iswarn)
                        });
                        that.config.$risk_pwd2.focus(function () {
                            var iswarn = that.config.$risk_pwd2.data("iswarn");
                            that.ui.focuswarn(that.ev.checkpwd_2(iswarn), iswarn)
                        })
                    }, blur: function () {
                        that.config.$risk_pwd1.blur(function () {
                            var result = that.ev.checkpwd_1();
                            if (!result.result) that.config.$risk_pwd1.data("iswarn", true);
                            that.ui.blurwarn(result);
                            clearTimeout(that.config.timeout.delay)
                        });
                        that.config.$risk_pwd2.blur(function () {
                            var result =
                                that.ev.checkpwd_2();
                            if (!result.result) that.config.$risk_pwd2.data("iswarn", true);
                            that.ui.blurwarn(result);
                            clearTimeout(that.config.timeout.delay)
                        })
                    }, press: function () {
                        that.config.$risk_pwd.each(function (i, v) {
                            (function (i, $elem) {
                                var index = i + 1;
                                $elem.keypress(function (event) {
                                    that.config.capsLock = capslock.detect(event);
                                    if (that.config.capsLock) {
                                        that.ui.focuswarn({
                                            tips: "\u952e\u76d8\u5927\u5199\u9501\u5b9a\u5df2\u6253\u5f00\uff0c\u8bf7\u6ce8\u610f\u5927\u5c0f\u5199",
                                            result: false,
                                            elem: that.config["$show_warn" +
                                            index]
                                        });
                                        that.config["$risk_pwd" + index].data("capsLock", true)
                                    } else if (that.config["$risk_pwd" + index].data("capsLock")) {
                                        that.ui.focuswarn({result: true, elem: that.config["$show_warn" + index]});
                                        that.config["$risk_pwd" + index].data("capsLock", false)
                                    } else if (that.config["$risk_pwd" + index].data("iswarn")) {
                                        that.config["$risk_pwd" + index].data("iswarn", false);
                                        that.ev.delayhide(index)
                                    }
                                });
                                that.config["$risk_pwd" + index].keyup(function () {
                                    if (that.ev.cansubmit()) that.ui.submitType(1); else that.ui.submitType(0)
                                });
                                that.config["$risk_pwd" +
                                index].keydown(function (e) {
                                    e.stopPropagation()
                                })
                            })(i, $(v))
                        });
                        that.config["$risk_pwd1"].keyup(function (e) {
                            var val = $(this).val();
                            var result = that.ev.checkpwd_1(val);
                            that.ui.degree(result.degree);
                            var code = e.keyCode;
                            if (code == 13) that.config["$risk_pwd2"].focus()
                        });
                        that.config["$risk_pwd2"].keyup(function (e) {
                            var code = e.keyCode;
                            if (code == 13) that.config.$submit.trigger("click")
                        })
                    }, submitSuccess: function () {
                        var time = 3;
                        that.config.$second.html(time);
                        that.ui.submitType(3);
                        that.config["$risk_pwd2"].off("keyup");
                        $(document).off("keydown keypress").on("keypress", function (e) {
                            var code = e.keyCode;
                            if (code == 13) that.config.$iknow.trigger("click")
                        });
                        that.config.interval.countdown = setInterval(function () {
                            time = --time > 0 ? time : 0;
                            if (!time) {
                                clearInterval(that.config.interval.countdown);
                                that.ui.dialogClose()
                            }
                            that.config.$second.html(time)
                        }, 1E3)
                    }, submitFail: function () {
                        var time = 3;
                        that.config.$second.html(time);
                        that.ui.submitType(4);
                        that.config.interval.countdown = setInterval(function () {
                            time = --time > 0 ? time : 0;
                            if (!time) {
                                clearInterval(that.config.interval.countdown);
                                that.ui.dialogClose()
                            }
                            that.config.$second.html(time)
                        }, 1E3)
                    }, submit: function () {
                        that.config.$submit.on("click", function () {
                            var cansubmit = $(this).data("cansubmit");
                            if (cansubmit) {
                                var pwd1 = that.config.$risk_pwd1.val();
                                var pwd2 = that.config.$risk_pwd2.val();
                                that.ui.submitType(2);
                                that.data.resetpwd(pwd1, pwd2).done(function (rtn) {
                                    if (rtn.code == 0) that.ev.submitSuccess(); else that.ev.submitFail()
                                }).fail(function () {
                                    that.ev.submitFail()
                                })
                            }
                        })
                    }
                };
                that.data = {
                    resetpwd: function (pwd1, pwd2) {
                        return $.ajax({
                            type: "post", url: "https://passport.vip.com/reset_password",
                            dataType: "json", cache: false, data: {password: pwd1, confirm: pwd2}
                        })
                    }
                };
                that.ev.init()
            }
        }
    }, {}],
    3: [function (require, module, exports) {
        var template = function (tpl, values) {
            return tpl.replace(/\{\{\s*(.+?)\s*\}\}/g, function (m, v) {
                return values[v] || ""
            })
        };
        var adsMod = {
            api: "//pcapi.vip.com/cmc/index.php?callback=?",
            tpl: '<a href="{{ link }}" target="">{{ content }}</a>',
            init: function () {
                var that = this;
                $(".J-ads").each(function () {
                    var adsid = $(this).data("adsid");
                    that.fetch(adsid, $(this))
                });
                return this
            },
            fetch: function (ids,
                             $this) {
                var that = this;
                $.getJSON(this.api, {type: ids}, function (res) {
                    if ($this.data("bg")) that.bg(res, $this); else that.render(res, $this)
                })
            },
            bg: function (res, $this) {
                for (var k in res) {
                    var item = res[k].items[0];
                    $this.css("background-image", "url(" + item.img + ")")
                }
            },
            render: function (res, $this) {
                $this.empty();
                var tplel = $this.data("tpl");
                var adtpl = tplel ? $(tplel).html() : this.tpl;
                for (var k in res) {
                    var items = res[k].items, i, len = items.length;
                    for (i = 0; i < len; i++) {
                        var item = items[i];
                        if (item.img) {
                            var imgtpl = '<img src="{{ img }}" alt="{{ name }}" />';
                            item.content = template(imgtpl, item)
                        } else item.content = item.name;
                        var $item = $(template(adtpl, item));
                        if (!item.link)if ($item.is("a")) $item.removeAttr("href"); else $item.find("a").removeAttr("href"); else if (item.blank)if ($item.is("a")) $item.attr("target", "_blank"); else $item.find("a").attr("target", "_blank");
                        $this.append($item)
                    }
                }
            }
        };
        window.adsMod = adsMod;
        module.exports = adsMod
    }, {}],
    4: [function (require, module, exports) {
        (function (root) {
            var capslock = {
                detect: function (e) {
                    var valueCapsLock = e.keyCode ? e.keyCode : e.which;
                    var valueShift = e.shiftKey ? e.shiftKey : valueCapsLock == 16 ? true : false;
                    if (valueCapsLock >= 65 && valueCapsLock <= 90 && !valueShift || valueCapsLock >= 97 && valueCapsLock <= 122 && valueShift)return true; else return false
                }
            };
            if (typeof define === "function" && define.cmd) define(function (require, exports, module) {
                module.exports = capslock
            }); else root.capslock = capslock;
            $(function () {
                document.msCapsLockWarningOff = true
            })
        })(window)
    }, {}],
    5: [function (require, module, exports) {
        var Mars = {
            maxHeapHeight: 10, hoverHeap: [], inputHeap: [], init: function () {
                var that =
                    this;
                $("body").delegate("[passport_mars_sead_hover]", "mouseenter", function () {
                    that.hoverProcess(this)
                }).delegate("[passport_mars_sead_input]", "keydown", function () {
                    that.inputProcess(this)
                })
            }, hoverProcess: function (target) {
                if (this.hoverHeap.length >= this.maxHeapHeight) this.hoverHeap.pop();
                var c = new Coordinate(getElementLeft(target), getElementTop(target));
                var r = new HoverRec(c);
                this.hoverHeap.unshift(r)
            }, inputProcess: function (target) {
                if (this.inputHeap.length >= this.maxHeapHeight) this.inputHeap.pop();
                var r =
                    new InputRec($(target).attr("passport_mars_sead_input"));
                this.inputHeap.unshift(r)
            }, send: function () {
                var hasMar = typeof Mar != "undefined" && (typeof Mar.Seed != "undefined" && typeof Mar.Seed.request == "function");
                if (this.hoverHeap.length > 0 && hasMar) Mar.Seed.request("span", "hover", "active_login_ck_collect", {
                    "platform": "web",
                    "data": this.hoverHeap
                });
                if (this.inputHeap.length > 0 && hasMar) Mar.Seed.request("input", "change", "active_login_keystroke_collect", {
                    "platform": "web",
                    "data": this.inputHeap
                })
            }
        };
        var InputRec = function (name) {
            this.t =
                "" + (new Date).getTime();
            this.x = name
        };
        var HoverRec = function (coordinate) {
            this.t = "" + (new Date).getTime();
            this.position = [{"c1": "" + coordinate}]
        };
        var Coordinate = function (x, y) {
            this.x = x, this.y = y
        };
        Coordinate.prototype.toString = function () {
            return this.x + ":" + this.y
        };
        function getElementLeft(element) {
            var actualLeft = element.offsetLeft;
            var current = element.offsetParent;
            while (current !== null) {
                actualLeft += current.offsetLeft;
                current = current.offsetParent
            }
            return actualLeft
        }

        function getElementTop(element) {
            var actualTop =
                element.offsetTop;
            var current = element.offsetParent;
            while (current !== null) {
                actualTop += current.offsetTop;
                current = current.offsetParent
            }
            return actualTop
        }

        module.exports = Mars
    }, {}],
    6: [function (require, module, exports) {
        var DEGREE = function (arg) {
            this.init(arg)
        };
        DEGREE.prototype = {
            init: function (arg) {
                arg = arg || {};
                var config = {
                    letterPattern: /[a-zA-Z]+/,
                    numbericPattern: /[0-9]+/,
                    specialPattern: /[`\-=\[\];',\.\/\~\!@#$\%\^&\*\\\(\)_\+{}|:"<>\?]+/,
                    fullPattern: /[^0-9a-zA-Z`\-=\[\];',\.\/\~\!@#$\%\^&\*\\\(\)_\+{}|:"<>\?]+/,
                    ruleRange1: [1, 21],
                    ruleRange2: [1, 6, 12, 21],
                    ruleRange3: [1, 6, 10, 21],
                    el: $("#J_degree"),
                    strClass: ".lv-name",
                    lvClass: ["f-hidden", "z-pwd-weak", "z-pwd-medium", "z-pwd-strong"],
                    lvString: ["\u5f31", "\u5f31", "\u4e2d", "\u5f3a"]
                };
                this.config = $.extend(config, arg);
                return this
            }, calcCharsCategory: function (val) {
                var config = this.config, category = 0;
                if (this.checkIllegalChar(val)) {
                    category = 1;
                    return category
                }
                config.letterPattern.test(val) && category++;
                config.numbericPattern.test(val) && category++;
                config.specialPattern.test(val) &&
                category++;
                return category
            }, checkIllegalChar: function (val) {
                var config = this.config;
                if (config.fullPattern.test(val))return true;
                return false
            }, calc: function (pwd) {
                var degree = 0, pwdLen = pwd.length, calcConut = this.calcCharsCategory(pwd);
                if (!pwd)return degree;
                degree = 1;
                for (var i = 0, l = this.config["ruleRange" + calcConut].length; i < l; i++)if (pwdLen < this.config["ruleRange" + calcConut][i]) {
                    degree = i;
                    break
                }
                return degree
            }, pub: function () {
                var that = this;
                var arg = Array.prototype.slice.call(arguments);
                return that.ui(that.calc(arg[0]))
            },
            ui: function (lv) {
                var config = this.config;
                config.el.removeClass(config.lvClass.join(" ")).addClass(config.lvClass[lv]);
                config.el.find(config.strClass).html(config.lvString[lv]);
                return lv
            }, config: null
        };
        module.exports = DEGREE
    }, {}],
    7: [function (require, module, exports) {
        var Strength = require("./password-strength");
        var Tips = require("./tip-component");
        require("./placeholder.js");
        var phoneTool = {
            data: {
                getMsgNum: function () {
                    if (config.jqPhoneMsgGet.hasClass("ui-btn-disable"))return false;
                    var _date = {
                        processId: config.processId,
                        mobile: config.jqLoginPhone.val(),
                        vipc: config.requestPhoneKey,
                        captcha: config.jqPhoneCode.val()
                    };
                    _date = VTM.otherData ? $.extend({}, _date, VTM.otherData) : _date;
                    $.ajax({
                        type: "POST",
                        url: phoneTool.config.rootPath + "/mobileLogin/getMobileVerifyCode",
                        data: _date,
                        dataType: "json",
                        success: function (res) {
                            if (res.result == "haslogin") phoneTool.module.redirect(); else if (res.result == "success") {
                                var _processId = res.data.processId;
                                if (_processId) config.processId = _processId;
                                var time = 60;
                                config.jqPhoneMsgGet.addClass("ui-btn-disable").html('<span class="time">' +
                                    time + "</span>\u79d2\u540e\u91cd\u65b0\u83b7\u53d6");
                                config.jqPhoneMsgGet.data("code", "1");
                                var second = function () {
                                    if (time > 0) {
                                        config.jqPhoneMsgGet.find(".time").html(time);
                                        time--
                                    } else {
                                        clearInterval(timeInt);
                                        config.jqPhoneMsgGet.removeClass("ui-btn-disable").html("\u83b7\u53d6\u9a8c\u8bc1\u7801");
                                        config.jqPhoneMsgGet.data("code", "")
                                    }
                                };
                                var timeInt = self.setInterval(second, 1E3)
                            } else phoneTool.module.msgError(res.errorCode)
                        }
                    })
                }, addSaltFn: function (pwd) {
                    return md5(pwd)
                }
            }, ui: {
                init: function () {
                    this.phoneVerifyCodeInit()
                },
                phoneVerifyCodeInit: function () {
                    config.requestPhoneKey = VTM.mobileLoginVipc;
                    var src = phoneTool.config.rootPath + "/captcha/getCaptcha?type=0&vipc=" + encodeURIComponent(config.requestPhoneKey) + "&v=" + (new Date).getTime();
                    config.jqPhoneVerifyImg = $('<img width="49" height="34" />');
                    config.jqPhoneVerifyImg.attr("src", src).on("error", function () {
                        $(this).attr("src", "//member-ssl.vipstatic.com/img/share/placeholder.png");
                        config.jqPhoneCode.tip.status("error", "\u7cfb\u7edf\u5f02\u5e38\uff0c\u8bf7\u5237\u65b0\u9875\u9762\u518d\u8bd5\u3002")
                    });
                    config.jqPhoneVerifyImgWrap.append(config.jqPhoneVerifyImg)
                }
            }, event: {
                init: function () {
                    var that = this;
                    that.phoneCaptchaInput();
                    this.changePhoneCode();
                    that.submit();
                    $(document).placeholder();
                    $.Listeners.sub("inputPhoneBindFinish").onsuccess(function () {
                        that.phoneInput();
                        that.phoneCodeInput();
                        that.phoneMsgInput();
                        that.getMsgNum();
                        that.passwordInput()
                    })
                }, changePhoneCode: function () {
                    config.jqPhoneVerifyImgWrap.add(config.jqPhonechangeCode).on("click", function () {
                        config.jqPhoneCode.tip.status();
                        phoneTool.module.changePhoneCode()
                    })
                },
                phoneInput: function () {
                    config.jqLoginPhone.on("blur", function () {
                        phoneTool.module.valid.phoneNumVerify(config.jqLoginPhone)
                    })
                }, phoneCodeInput: function () {
                    config.jqPhoneCode.on("blur", function () {
                        phoneTool.module.valid.phoneCode(config.jqPhoneCode)
                    })
                }, phoneMsgInput: function () {
                    config.jqPhoneMsg.on("blur", function () {
                        phoneTool.module.valid.phoneMsg(config.jqPhoneMsg)
                    })
                }, getMsgNum: function () {
                    config.jqPhoneMsgGet.on("click", function () {
                        phoneTool.module.valid.phoneNumVerify(config.jqLoginPhone);
                        phoneTool.module.valid.phoneCode(config.jqPhoneCode);
                        if (config.jqPhoneMsgGet.hasClass("ui-btn-disable"))return false;
                        if (config.jqLoginPhone.hasClass("z-input-error") || (config.jqPhoneCode.hasClass("z-input-error") || !config.jqPhoneCode.hasClass("z-input-success")))return false;
                        phoneTool.data.getMsgNum()
                    })
                }, phoneCaptchaInput: function () {
                    config.jqPhoneCode.off("blur").off("input").off("propertychange").on("blur", function () {
                        var _val = $(this).val();
                        if (_val.length > 0) phoneTool.module.ajaxPhoneCheckCaptcha(config.jqPhoneCode, _val); else config.jqPhoneCode.tip.status("error",
                            "\u8bf7\u8f93\u5165\u56fe\u7247\u9a8c\u8bc1\u7801")
                    })
                }, submit: function () {
                    config.jqPhoneSubmit.on("click", function (event) {
                        event.preventDefault();
                        phoneTool.module.formSubmit()
                    });
                    config.jqPasswordSubmit.on("click", function (event) {
                        event.preventDefault();
                        phoneTool.module.formPassportSubmit()
                    })
                }, passwordInput: function () {
                    config.Strength = new Strength;
                    var payPwdTips = new Tips(config.jqNewPwd), againPwdTips = new Tips(config.jqAgainPwd), val;
                    config.jqNewPwd.on("keyup", function () {
                        val = config.jqNewPwd.val();
                        config.Strength.pub(val)
                    }).on("blur",
                        function () {
                            var rtn = phoneTool.event.checkpassportval();
                            rtn ? payPwdTips.setTxt(rtn).hightLight(true) : payPwdTips.hightLight(false).showTip(false)
                        }).on("keypress", function (e) {
                        if (capslock.detect(e)) payPwdTips.setTxt("\u952e\u76d8\u5927\u5199\u9501\u5b9a\u5df2\u6253\u5f00\uff0c\u8bf7\u6ce8\u610f\u5927\u5c0f\u5199", "info").showTip(true); else payPwdTips.showTip(false)
                    }).on("focus", function () {
                        if (!$(this).data("warning") && !$(this).val()) payPwdTips.setTxt("\u5bc6\u7801\u75316-20\u4f4d\u5b57\u6bcd\uff0c\u6570\u5b57\u548c\u7b26\u53f7\u81f3\u5c11\u4e24\u79cd\u4ee5\u4e0a\u5b57\u7b26\u7ec4\u5408\uff0c\u533a\u5206\u5927\u5c0f\u5199",
                            "info").showTip(true)
                    });
                    config.jqAgainPwd.on("blur", function () {
                        var rtn = phoneTool.event.checkagainval();
                        rtn ? againPwdTips.setTxt(rtn).hightLight(true) : againPwdTips.hightLight(false).showTip(false)
                    }).on("keypress", function (e) {
                        if (capslock.detect(e)) againPwdTips.setTxt("\u952e\u76d8\u5927\u5199\u9501\u5b9a\u5df2\u6253\u5f00\uff0c\u8bf7\u6ce8\u610f\u5927\u5c0f\u5199", "info").showTip(true); else againPwdTips.showTip(false)
                    }).on("focus", function () {
                        if (!$(this).data("warning") && !$(this).val()) againPwdTips.setTxt("\u8bf7\u518d\u6b21\u8f93\u5165\u4e0a\u9762\u7684\u5bc6\u7801",
                            "info").showTip(true)
                    })
                }, checkpassportval: function () {
                    var val = config.jqNewPwd.val();
                    var result = "";
                    if (!val) result = "\u8bf7\u8f93\u5165\u5bc6\u7801"; else if (config.Strength.checkIllegalChar(val)) result = "\u5bc6\u7801\u4e2d\u5305\u542b\u975e\u6cd5\u5b57\u7b26\uff0c\u8bf7\u91cd\u65b0\u8f93\u5165"; else if (val.length < 6) result = "\u8bf7\u8f93\u51656-20\u4f4d\u5bc6\u7801"; else if (config.Strength.pub(val) < 2) result = "\u5bc6\u7801\u8fc7\u4e8e\u7b80\u5355\uff0c\u8bf7\u8f93\u5165\u5b57\u6bcd\u3001\u6570\u5b57\u3001\u7b26\u53f7\u81f3\u5c11\u4e24\u79cd\u4ee5\u4e0a\u7ec4\u5408\u5bc6\u7801";
                    else if (val == config.loginName) result = "\u5bc6\u7801\u4e0d\u80fd\u4e0e\u767b\u5f55\u540d\u4e00\u81f4";
                    return result
                }, checkagainval: function () {
                    var val = config.jqNewPwd.val();
                    var againval = config.jqAgainPwd.val();
                    var result = "";
                    if (!againval) result = "\u8bf7\u8f93\u5165\u5bc6\u7801"; else if (val != againval) result = "\u4e24\u6b21\u8f93\u5165\u7684\u5bc6\u7801\u4e0d\u4e00\u81f4\uff0c\u8bf7\u91cd\u8bd5";
                    return result
                }
            }, module: {
                init: function () {
                    this.inputPhoneBindTip(config.phoneFormInput);
                    this.autocompleteLoginPhone()
                },
                autocompleteLoginPhone: function () {
                    var remLoginPhone = config.loginPhone;
                    if (remLoginPhone) {
                        if (!("placeholder" in document.createElement("input"))) config.jqLoginPhone.trigger("keypress");
                        config.jqLoginPhone.focus().val(remLoginPhone)
                    }
                }, redirect: function () {
                    window.location.href = config.redirectUrl
                }, inputPhoneBindTip: function (arr) {
                    function Tip(ele) {
                        this.$input = ele;
                        this.$tip = ele.closest(".u-form-item").next(".u-form-tip")
                    }

                    Tip.prototype.status = function (status, msg, errorMark) {
                        this.$input.removeClass("z-input-error z-input-success");
                        var msg = status == "error" ? msg || "" : "";
                        if (!errorMark) (status == "success" || status == "error") && this.$input.addClass("z-input-" + status);
                        this.$tip.html(msg)
                    };
                    $(arr).each(function (num, ele) {
                        ele.tip = new Tip(ele)
                    });
                    $.Listeners.pub("inputPhoneBindFinish").success()
                }, changePhoneCode: function () {
                    var src = phoneTool.config.rootPath + "/captcha/getCaptcha?type=0&vipc=" + encodeURIComponent(config.requestPhoneKey) + "&v=" + (new Date).getTime();
                    if (!config.jqPhoneVerifyImg) {
                        config.jqPhoneVerifyImg = $('<img width="49" height="34" />');
                        config.jqPhoneVerifyImg.attr("src", src).on("error", function () {
                            $(this).attr("src", "//member-ssl.vipstatic.com/img/share/placeholder.png");
                            config.jqPhoneCode.tip.status("error", "\u7cfb\u7edf\u5f02\u5e38\uff0c\u8bf7\u5237\u65b0\u9875\u9762\u518d\u8bd5\u3002")
                        });
                        config.jqPhoneVerifyImgWrap.append(config.jqPhoneVerifyImg)
                    } else config.jqPhoneVerifyImg.attr("src", src);
                    config.jqPhoneCode.removeClass("z-input-error z-input-success");
                    config.jqPhoneCode.val("");
                    return false
                }, ajaxPhoneCheckCaptcha: function (ele,
                                                    val) {
                    var isVaild = "success";
                    var msg = "";
                    $.ajax({
                        type: "POST",
                        url: phoneTool.config.rootPath + "/captcha/ajaxCheckCaptcha",
                        data: {
                            "captcha": val,
                            "vipc": config.requestPhoneKey,
                            "anticache": (new Date).getTime(),
                            "type": 0
                        },
                        dataType: "json",
                        success: function (data) {
                            if (data.result == "haslogin") phoneTool.module.redirect(); else if (!data.result) {
                                isVaild = "error";
                                msg = data.errorCode != 20203 ? "\u9a8c\u8bc1\u7801\u9519\u8bef" : "\u9a8c\u8bc1\u7801\u5df2\u8fc7\u671f";
                                config.requestPhoneKey = data.vipc;
                                phoneTool.module.changePhoneCode()
                            } else {
                                if (data.remain <
                                    2) {
                                    phoneTool.module.changePhoneCode();
                                    return false
                                }
                                if (!config.jqPhoneMsgGet.data("code")) config.jqPhoneMsgGet.removeClass("ui-btn-disable")
                            }
                            ele.tip.status(isVaild, msg)
                        },
                        error: function () {
                            alert("\u8bf7\u6c42\u5931\u8d25\uff0c\u8bf7\u5237\u65b0\u9875\u9762\u91cd\u8bd5")
                        }
                    })
                }, needBindPhone: function () {
                    var phoneComponent = new PhoneBind({
                        phoneNum: "",
                        isBg: true,
                        haveTip: true,
                        show: false,
                        success: function (phone) {
                            phoneTool.module.redirect()
                        },
                        cancel: function () {
                            phoneTool.module.redirect()
                        }
                    });
                    $.ajax({
                        type: "get", url: "//myi.vip.com/jsonp/api/phone/phone_safe_number",
                        dataType: "jsonp", success: function (result) {
                            if (result.code == 200 && !result.data)try {
                                phoneComponent.show()
                            } catch (e) {
                                phoneTool.module.redirect()
                            } else phoneTool.module.redirect()
                        }, error: function (jqXHR, textStatus, errorThrown) {
                            phoneTool.module.redirect()
                        }
                    })
                }, formSubmit: function () {
                    var submitBtn = config.jqPhoneSubmit;
                    if (submitBtn.hasClass("z-ui-btn-loading"))return false;
                    if (!this.valid.submit())return false;
                    submitBtn.addClass("z-ui-btn-loading");
                    var _date = {
                        processId: config.processId,
                        mobile: config.jqLoginPhone.val(),
                        vipc: config.requestPhoneKey,
                        captcha: config.jqPhoneCode.val(),
                        verifyCode: config.jqPhoneMsg.val(),
                        remUser: +config.jqRememberPhone.prop("checked")
                    };
                    _date = VTM.otherData ? $.extend({}, _date, VTM.otherData) : _date;
                    $.ajax({
                        url: phoneTool.config.rootPath + phoneLoginUrl.mobileLoginUrl,
                        type: "post",
                        dataType: "json",
                        data: _date,
                        success: function (data) {
                            if (data.result == "haslogin") phoneTool.module.redirect(); else if (data.result == "error") {
                                phoneTool.module.submitError(data.errorCode);
                                submitBtn.removeClass("z-ui-btn-loading")
                            } else if (data.result ==
                                "success") phoneLoginSuccess(data); else {
                                phoneTool.module.submitError(6);
                                submitBtn.removeClass("z-ui-btn-loading")
                            }
                        },
                        error: function () {
                            phoneTool.module.submitError(6)
                        }
                    })
                }, formPassportSubmit: function () {
                    var submitBtn = config.jqPasswordSubmit;
                    if (submitBtn.hasClass("z-ui-btn-loading"))return false;
                    if (!this.valid.passportSubmit())return false;
                    submitBtn.addClass("z-ui-btn-loading");
                    var _passport = phoneTool.data.addSaltFn(config.jqNewPwd.val());
                    var _date = {
                        processId: config.processId, mobile: config.jqLoginPhone.val(),
                        password: _passport, remUser: +config.jqRememberPhone.prop("checked")
                    };
                    _date = VTM.otherData ? $.extend({}, _date, VTM.otherData) : _date;
                    $.ajax({
                        url: phoneTool.config.rootPath + phoneLoginUrl.mobileLoginWithPWUrl,
                        type: "post",
                        dataType: "json",
                        data: _date,
                        success: function (data) {
                            if (data.result == "haslogin") phoneTool.module.redirect(); else if (data.result == "error") {
                                phoneTool.module.passportError(data.errorCode);
                                submitBtn.removeClass("z-ui-btn-loading")
                            } else if (data.result == "success") phoneLoginSuccess(data); else {
                                phoneTool.module.passportError(6);
                                submitBtn.removeClass("z-ui-btn-loading")
                            }
                        },
                        error: function () {
                            phoneTool.module.passportError(6)
                        }
                    })
                }, msgError: function (status) {
                    if (isNaN(status))return false;
                    var errorStatus = status;
                    var loginError = {
                        0: [config.jqPhoneCode, "\u9a8c\u8bc1\u7801\u9519\u8bef"],
                        1: [config.jqPhoneMsg, "\u9a8c\u8bc1\u7801\u53d1\u9001\u5931\u8d25"],
                        3: [config.jqLoginPhone, "\u8be5\u624b\u673a\u83b7\u53d6\u9a8c\u8bc1\u7801\u5df2\u8fbe\u4e0a\u9650\uff0c\u8bf7\u9694\u65e5\u91cd\u8bd5"],
                        4: [config.jqLoginPhone, '\u624b\u673a\u53f7\u8d26\u6237\u4e0d\u5b58\u5728<a href="' +
                        phoneTool.config.rootPath + '/register" title="\u514d\u8d39\u6ce8\u518c" target="_blank" class="u-link">\u514d\u8d39\u6ce8\u518c</a>?'],
                        5: [config.jqLoginPhone, "\u60a8\u7684\u624b\u673a\u4e0d\u652f\u6301\u6b64\u767b\u5f55\u65b9\u5f0f\uff0c\u8bf7\u4f7f\u7528\u5176\u5b83\u767b\u5f55\u65b9\u5f0f"],
                        6: [config.jqLoginPhone, "\u60a8\u7684\u64cd\u4f5c\u592a\u8fc7\u9891\u7e41\uff0c\u8bf7\u9694\u65e5\u518d\u6765"],
                        8: [config.jqPhoneCode, "\u4e3a\u4e86\u60a8\u7684\u8d26\u6237\u5b89\u5168\uff0c\u8bf7\u8f93\u5165\u9a8c\u8bc1\u7801\u3002"],
                        20203: [config.jqPhoneCode, "\u9a8c\u8bc1\u7801\u5df2\u8fc7\u671f"]
                    };
                    if (errorStatus == 20203) phoneTool.module.changePhoneCode();
                    if (!loginError[errorStatus]) {
                        alert("\u8bf7\u6c42\u5931\u8d25\uff0c\u8bf7\u5237\u65b0\u9875\u9762\u91cd\u8bd5");
                        return false
                    }
                    loginError[errorStatus][0].tip.status("error", loginError[errorStatus][1]);
                    return false
                }, submitError: function (status) {
                    if (isNaN(status))return false;
                    if (status == 2) {
                        config.jqPhoneMain.addClass("z-passport-show");
                        config.jqLoginPhone.attr("disabled", true).addClass("z-ui-input-disable");
                        return false
                    }
                    var errorStatus = status;
                    var loginError = {
                        0: [config.jqPhoneCode, "\u9a8c\u8bc1\u7801\u9519\u8bef"],
                        1: [config.jqLoginPhone, '\u624b\u673a\u53f7\u8d26\u6237\u4e0d\u5b58\u5728<a href="' + phoneTool.config.rootPath + '/register" title="\u514d\u8d39\u6ce8\u518c" target="_blank" class="u-link">\u514d\u8d39\u6ce8\u518c</a>?'],
                        3: [config.jqPhoneMsg, "\u9a8c\u8bc1\u7801\u9519\u8bef"],
                        4: [config.jqPhoneMsg, "\u9a8c\u8bc1\u7801\u8fc7\u671f"],
                        5: [config.jqLoginPhone, "\u60a8\u7684\u8d26\u6237\u53ef\u80fd\u5b58\u5728\u5f02\u5e38\uff0c\u672c\u6b21\u767b\u5f55\u5931\u8d25\u3002\u5982\u6709\u9700\u8981\uff0c\u8bf7\u8054\u7cfb\u5ba2\u670d\uff1a400-6789-888"],
                        7: [config.jqLoginPhone, "\u8be5\u624b\u673a\u83b7\u53d6\u9a8c\u8bc1\u7801\u5df2\u8fbe\u4e0a\u9650\uff0c\u8bf7\u9694\u65e5\u91cd\u8bd5"],
                        8: [config.jqPhoneCode, "\u9a8c\u8bc1\u7801\u9519\u8bef"],
                        9: [config.jqPhoneMsg, "\u624b\u673a\u53f7\u4e0e\u77ed\u4fe1\u9a8c\u8bc1\u7801\u4e0d\u5339\u914d\uff0c\u8bf7\u786e\u8ba4\u540e\u91cd\u65b0\u8f93\u5165"],
                        20203: [config.jqPhoneCode, "\u9a8c\u8bc1\u7801\u5df2\u8fc7\u671f"]
                    };
                    if (errorStatus == 5) {
                        alert(loginError[errorStatus][1]);
                        return false
                    }
                    if (errorStatus == 20203 || (errorStatus ==
                        0 || errorStatus == 8)) phoneTool.module.changePhoneCode();
                    if (!loginError[errorStatus]) {
                        alert("\u8bf7\u6c42\u5931\u8d25\uff0c\u8bf7\u5237\u65b0\u9875\u9762\u91cd\u8bd5");
                        return false
                    }
                    loginError[errorStatus][0].tip.status("error", loginError[errorStatus][1]);
                    return false
                }, passportError: function (status) {
                    if (isNaN(status))return false;
                    var errorStatus = status;
                    var loginError = {
                        5: [config.jqLoginPhone, "\u60a8\u7684\u8d26\u6237\u53ef\u80fd\u5b58\u5728\u5f02\u5e38\uff0c\u672c\u6b21\u767b\u5f55\u5931\u8d25\u3002\u5982\u6709\u9700\u8981\uff0c\u8bf7\u8054\u7cfb\u5ba2\u670d\uff1a400-6789-888"],
                        1: [config.jqLoginPhone, '\u624b\u673a\u53f7\u8d26\u6237\u4e0d\u5b58\u5728<a href="' + phoneTool.config.rootPath + '/register" title="\u514d\u8d39\u6ce8\u518c" target="_blank" class="u-link">\u514d\u8d39\u6ce8\u518c</a>?']
                    };
                    if (errorStatus == 5) {
                        alert(loginError[errorStatus][1]);
                        return false
                    }
                    if (!loginError[errorStatus]) {
                        alert("\u8bf7\u6c42\u5931\u8d25\uff0c\u8bf7\u5237\u65b0\u9875\u9762\u91cd\u8bd5");
                        return false
                    }
                    loginError[errorStatus][0].tip.status("error", loginError[errorStatus][1]);
                    return false
                }, valid: {
                    phoneNumVerify: function (ele) {
                        var _val =
                            ele.val().replace(/\s/g, "");
                        var isVaild = "";
                        var msg = "";
                        ele.val(_val);
                        if (_val == "") {
                            isVaild = "error";
                            msg = "\u8bf7\u8f93\u5165\u624b\u673a\u53f7"
                        } else if (!/^1[34578][0-9]\d{8}$/.test(_val)) {
                            isVaild = "error";
                            msg = "\u624b\u673a\u53f7\u6709\u8bef"
                        }
                        ele.tip.status(isVaild, msg);
                        return isVaild == ""
                    }, phoneCode: function (ele) {
                        var _val = ele.val();
                        var isVaild = "";
                        var msg = "";
                        if (!_val || !config.requestPhoneKey) {
                            isVaild = "error";
                            msg = "\u8bf7\u8f93\u5165\u9a8c\u8bc1\u7801";
                            ele.tip.status(isVaild, msg)
                        }
                        return isVaild == ""
                    }, phoneMsg: function (ele) {
                        var _val =
                            ele.val();
                        var isVaild = "";
                        var msg = "";
                        if (!_val) {
                            isVaild = "error";
                            msg = "\u8bf7\u8f93\u5165\u77ed\u4fe1\u9a8c\u8bc1\u7801";
                            ele.tip.status(isVaild, msg)
                        }
                        ele.tip.status(isVaild, msg);
                        return isVaild == ""
                    }, submit: function () {
                        var result = true;
                        if (!this.phoneNumVerify(config.jqLoginPhone)) result = false;
                        if (!this.phoneCode(config.jqPhoneCode)) result = false;
                        if (!this.phoneMsg(config.jqPhoneMsg)) result = false;
                        return result
                    }, passportSubmit: function () {
                        var result = true;
                        if (!this.phoneNumVerify(config.jqLoginPhone)) result = false;
                        var payPwdTips = new Tips(config.jqNewPwd), againPwdTips = new Tips(config.jqAgainPwd);
                        var rtnPassprot = phoneTool.event.checkpassportval();
                        var rtnAgain = phoneTool.event.checkagainval();
                        if (rtnPassprot || rtnAgain) {
                            var thisTips = "";
                            if (rtnPassprot) {
                                thisTips = thisTips || payPwdTips;
                                payPwdTips.setTxt(rtnPassprot).hightLight(true)
                            }
                            if (rtnAgain) {
                                thisTips = thisTips || againPwdTips;
                                againPwdTips.setTxt(rtnAgain).hightLight(true)
                            }
                            thisTips && thisTips.showTip(true);
                            result = false
                        }
                        return result
                    }
                }
            }, config: {rootPath: "https://passport.vip.com"},
            init: function () {
                this.ui.init();
                this.event.init();
                this.module.init()
            }
        };
        module.exports = phoneTool
    }, {"./password-strength": 6, "./placeholder.js": 8, "./tip-component": 9}],
    8: [function (require, module, exports) {
        (function ($) {
            var supportPlaceholder = "placeholder" in document.createElement("input");
            var placeholder = function () {
                var $placeholderInputs;
                if (!supportPlaceholder) {
                    $placeholderInputs = this.find("input:text, input:password").filter(function () {
                        var text = $(this).attr("placeholder");
                        return text && text !== ""
                    });
                    $placeholderInputs.each(function () {
                        var $me =
                            $(this), $placeholderSpan, text = $me.attr("placeholder"), spanHtml;
                        text = text || "";
                        spanHtml = '<span class="ie-placeholder">' + text + "</span>";
                        $me.after(spanHtml);
                        $placeholderSpan = $me.next(".ie-placeholder");
                        $me.on("keydown", function (e) {
                            $placeholderSpan.hide()
                        });
                        $me.on("blur", function (e) {
                            var $target = $(e.target);
                            var val = $target.val();
                            if (val.length === 0) $placeholderSpan.show(); else $placeholderSpan.hide()
                        });
                        $placeholderSpan.on("click", function () {
                            $me.focus()
                        })
                    })
                }
                return this
            };
            if (typeof define === "function" && define.cmd) define(function (require,
                                                                             exports, module) {
                $.fn.placeholder = placeholder
            }); else $.fn.placeholder = placeholder
        })(jQuery)
    }, {}],
    9: [function (require, module, exports) {
        var Tip = function (ele) {
            this.options = {};
            this.init(ele)
        };
        Tip.prototype = {
            userList: [], init: function (ele) {
                this.options.jqInput = ele || $("#J_input");
                this.options.jqBox = this.options.jqInput.closest(".ui-form-item-group");
                this.options.jqTip = this.options.jqBox.find(".ui-tooltips");
                this.options.jqTipCon = this.options.jqTip.find(".ui-tooltips-content");
                if (!ele.data("is-bind")) {
                    this.options.jqInput.data("warning",
                        false);
                    this.bind()
                }
                this.userList.push(this)
            }, hightLight: function (isShow, type) {
                var clClass = isShow ? "addClass" : "removeClass";
                var input = this.options.jqInput;
                var type = type || "all";
                if (type != "all")if (isShow) input.data("error-type", type); else if (type != input.data("error-type"))return this;
                input.data("warning", isShow);
                this.options.jqBox[clClass]("z-ui-form-item-warning");
                return this
            }, showTip: function (isShow, isFade) {
                if (this.options.jqTipCon.text() == "")return this;
                var self = this;
                var clClass = isShow ? "addClass" : "removeClass";
                self.options.jqBox.css("z-index", isShow ? 10 : "");
                clearTimeout(this.options.jqInput.data("timer"));
                this.options.jqBox[clClass]("z-ui-tooltips-in");
                if (isShow && isFade) {
                    var timer = setTimeout(function () {
                        self.showTip(false)
                    }, 3E3);
                    this.options.jqInput.data("timer", timer)
                }
                return this
            }, hideAll: function () {
                $(this.userList).each(function (num, ele) {
                    ele.showTip(false)
                });
                return this
            }, setTxt: function (txt, type) {
                var tpl;
                if (type == "info") {
                    tpl = "<p>{{txt}}</p>";
                    this.options.jqTip.removeClass("ui-tooltips-warning").addClass("ui-tooltips-info")
                } else if (type ==
                    "empty") tpl = ""; else {
                    tpl = '<p class="ui-tooltips-msg"><i class="vipFont if-sigh">&#xe620;</i>{{txt}}</p>';
                    this.options.jqTip.removeClass("ui-tooltips-info").addClass("ui-tooltips-warning")
                }
                this.options.jqTipCon.html(tpl.replace(/\{\{txt\}\}/, txt));
                return this
            }, bind: function () {
                var self = this;
                this.options.jqInput.data("is-bind", true).on("focus", function () {
                    Tip.prototype.hideAll();
                    if (self.options.jqInput.data("warning")) self.showTip(true, true)
                }).on("blur", function () {
                    self.options.jqBox.css("z-index", "");
                    self.showTip(false)
                })
            }
        };
        module.exports = Tip
    }, {}],
    10: [function (require, module, exports) {
        var CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".split("");
        module.exports = {
            uuid: function (len, radix) {
                var chars = CHARS, uuid = [], i;
                radix = radix || chars.length;
                if (len)for (i = 0; i < len; i++)uuid[i] = chars[0 | Math.random() * radix]; else {
                    var r;
                    uuid[8] = uuid[13] = uuid[18] = uuid[23] = "-";
                    uuid[14] = "4";
                    for (i = 0; i < 36; i++)if (!uuid[i]) {
                        r = 0 | Math.random() * 16;
                        uuid[i] = chars[i == 19 ? r & 3 | 8 : r]
                    }
                }
                return uuid.join("")
            }, uuidFast: function () {
                var chars =
                    CHARS, uuid = new Array(36), rnd = 0, r;
                for (var i = 0; i < 36; i++)if (i == 8 || (i == 13 || (i == 18 || i == 23))) uuid[i] = "-"; else if (i == 14) uuid[i] = "4"; else {
                    if (rnd <= 2) rnd = 33554432 + Math.random() * 16777216 | 0;
                    r = rnd & 15;
                    rnd = rnd >> 4;
                    uuid[i] = chars[i == 19 ? r & 3 | 8 : r]
                }
                return uuid.join("")
            }, uuidCompact: function () {
                return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
                    var r = Math.random() * 16 | 0, v = c == "x" ? r : r & 3 | 8;
                    return v.toString(16)
                })
            }
        }
    }, {}],
    11: [function (require, module, exports) {
        window.md5 = require("./modules/SecurityPlugin/md5");
        $.Listeners.pub("md5.loaded").success()
    }, {"./modules/SecurityPlugin/md5": 12}],
    12: [function (require, module, exports) {
        (function ($) {
            function safe_add(x, y) {
                var lsw = (x & 65535) + (y & 65535), msw = (x >> 16) + (y >> 16) + (lsw >> 16);
                return msw << 16 | lsw & 65535
            }

            function bit_rol(num, cnt) {
                return num << cnt | num >>> 32 - cnt
            }

            function md5_cmn(q, a, b, x, s, t) {
                return safe_add(bit_rol(safe_add(safe_add(a, q), safe_add(x, t)), s), b)
            }

            function md5_ff(a, b, c, d, x, s, t) {
                return md5_cmn(b & c | ~b & d, a, b, x, s, t)
            }

            function md5_gg(a, b, c, d, x, s, t) {
                return md5_cmn(b &
                    d | c & ~d, a, b, x, s, t)
            }

            function md5_hh(a, b, c, d, x, s, t) {
                return md5_cmn(b ^ c ^ d, a, b, x, s, t)
            }

            function md5_ii(a, b, c, d, x, s, t) {
                return md5_cmn(c ^ (b | ~d), a, b, x, s, t)
            }

            function binl_md5(x, len) {
                x[len >> 5] |= 128 << len % 32;
                x[(len + 64 >>> 9 << 4) + 14] = len;
                var i, olda, oldb, oldc, oldd, a = 1732584193, b = -271733879, c = -1732584194, d = 271733878;
                for (i = 0; i < x.length; i += 16) {
                    olda = a;
                    oldb = b;
                    oldc = c;
                    oldd = d;
                    a = md5_ff(a, b, c, d, x[i], 7, -680876936);
                    d = md5_ff(d, a, b, c, x[i + 1], 12, -389564586);
                    c = md5_ff(c, d, a, b, x[i + 2], 17, 606105819);
                    b = md5_ff(b, c, d, a, x[i + 3], 22, -1044525330);
                    a = md5_ff(a, b, c, d, x[i + 4], 7, -176418897);
                    d = md5_ff(d, a, b, c, x[i + 5], 12, 1200080426);
                    c = md5_ff(c, d, a, b, x[i + 6], 17, -1473231341);
                    b = md5_ff(b, c, d, a, x[i + 7], 22, -45705983);
                    a = md5_ff(a, b, c, d, x[i + 8], 7, 1770035416);
                    d = md5_ff(d, a, b, c, x[i + 9], 12, -1958414417);
                    c = md5_ff(c, d, a, b, x[i + 10], 17, -42063);
                    b = md5_ff(b, c, d, a, x[i + 11], 22, -1990404162);
                    a = md5_ff(a, b, c, d, x[i + 12], 7, 1804603682);
                    d = md5_ff(d, a, b, c, x[i + 13], 12, -40341101);
                    c = md5_ff(c, d, a, b, x[i + 14], 17, -1502002290);
                    b = md5_ff(b, c, d, a, x[i + 15], 22, 1236535329);
                    a = md5_gg(a, b, c, d, x[i + 1], 5, -165796510);
                    d = md5_gg(d, a, b, c, x[i + 6], 9, -1069501632);
                    c = md5_gg(c, d, a, b, x[i + 11], 14, 643717713);
                    b = md5_gg(b, c, d, a, x[i], 20, -373897302);
                    a = md5_gg(a, b, c, d, x[i + 5], 5, -701558691);
                    d = md5_gg(d, a, b, c, x[i + 10], 9, 38016083);
                    c = md5_gg(c, d, a, b, x[i + 15], 14, -660478335);
                    b = md5_gg(b, c, d, a, x[i + 4], 20, -405537848);
                    a = md5_gg(a, b, c, d, x[i + 9], 5, 568446438);
                    d = md5_gg(d, a, b, c, x[i + 14], 9, -1019803690);
                    c = md5_gg(c, d, a, b, x[i + 3], 14, -187363961);
                    b = md5_gg(b, c, d, a, x[i + 8], 20, 1163531501);
                    a = md5_gg(a, b, c, d, x[i + 13], 5, -1444681467);
                    d = md5_gg(d, a, b, c, x[i + 2], 9, -51403784);
                    c = md5_gg(c, d, a, b, x[i + 7], 14, 1735328473);
                    b = md5_gg(b, c, d, a, x[i + 12], 20, -1926607734);
                    a = md5_hh(a, b, c, d, x[i + 5], 4, -378558);
                    d = md5_hh(d, a, b, c, x[i + 8], 11, -2022574463);
                    c = md5_hh(c, d, a, b, x[i + 11], 16, 1839030562);
                    b = md5_hh(b, c, d, a, x[i + 14], 23, -35309556);
                    a = md5_hh(a, b, c, d, x[i + 1], 4, -1530992060);
                    d = md5_hh(d, a, b, c, x[i + 4], 11, 1272893353);
                    c = md5_hh(c, d, a, b, x[i + 7], 16, -155497632);
                    b = md5_hh(b, c, d, a, x[i + 10], 23, -1094730640);
                    a = md5_hh(a, b, c, d, x[i + 13], 4, 681279174);
                    d = md5_hh(d, a, b, c, x[i], 11, -358537222);
                    c = md5_hh(c, d, a, b, x[i + 3], 16, -722521979);
                    b = md5_hh(b, c, d, a, x[i + 6], 23, 76029189);
                    a = md5_hh(a, b, c, d, x[i + 9], 4, -640364487);
                    d = md5_hh(d, a, b, c, x[i + 12], 11, -421815835);
                    c = md5_hh(c, d, a, b, x[i + 15], 16, 530742520);
                    b = md5_hh(b, c, d, a, x[i + 2], 23, -995338651);
                    a = md5_ii(a, b, c, d, x[i], 6, -198630844);
                    d = md5_ii(d, a, b, c, x[i + 7], 10, 1126891415);
                    c = md5_ii(c, d, a, b, x[i + 14], 15, -1416354905);
                    b = md5_ii(b, c, d, a, x[i + 5], 21, -57434055);
                    a = md5_ii(a, b, c, d, x[i + 12], 6, 1700485571);
                    d = md5_ii(d, a, b, c, x[i + 3], 10, -1894986606);
                    c = md5_ii(c, d, a, b, x[i + 10], 15, -1051523);
                    b = md5_ii(b, c, d, a, x[i + 1], 21, -2054922799);
                    a = md5_ii(a, b, c, d, x[i + 8], 6, 1873313359);
                    d = md5_ii(d, a, b, c, x[i + 15], 10, -30611744);
                    c = md5_ii(c, d, a, b, x[i + 6], 15, -1560198380);
                    b = md5_ii(b, c, d, a, x[i + 13], 21, 1309151649);
                    a = md5_ii(a, b, c, d, x[i + 4], 6, -145523070);
                    d = md5_ii(d, a, b, c, x[i + 11], 10, -1120210379);
                    c = md5_ii(c, d, a, b, x[i + 2], 15, 718787259);
                    b = md5_ii(b, c, d, a, x[i + 9], 21, -343485551);
                    a = safe_add(a, olda);
                    b = safe_add(b, oldb);
                    c = safe_add(c, oldc);
                    d = safe_add(d, oldd)
                }
                return [a, b, c, d]
            }

            function binl2rstr(input) {
                var i, output = "";
                for (i = 0; i < input.length * 32; i += 8)output += String.fromCharCode(input[i >>
                    5] >>> i % 32 & 255);
                return output
            }

            function rstr2binl(input) {
                var i, output = [];
                output[(input.length >> 2) - 1] = undefined;
                for (i = 0; i < output.length; i += 1)output[i] = 0;
                for (i = 0; i < input.length * 8; i += 8)output[i >> 5] |= (input.charCodeAt(i / 8) & 255) << i % 32;
                return output
            }

            function rstr_md5(s) {
                return binl2rstr(binl_md5(rstr2binl(s), s.length * 8))
            }

            function rstr_hmac_md5(key, data) {
                var i, bkey = rstr2binl(key), ipad = [], opad = [], hash;
                ipad[15] = opad[15] = undefined;
                if (bkey.length > 16) bkey = binl_md5(bkey, key.length * 8);
                for (i = 0; i < 16; i += 1) {
                    ipad[i] =
                        bkey[i] ^ 909522486;
                    opad[i] = bkey[i] ^ 1549556828
                }
                hash = binl_md5(ipad.concat(rstr2binl(data)), 512 + data.length * 8);
                return binl2rstr(binl_md5(opad.concat(hash), 512 + 128))
            }

            function rstr2hex(input) {
                var hex_tab = "0123456789abcdef", output = "", x, i;
                for (i = 0; i < input.length; i += 1) {
                    x = input.charCodeAt(i);
                    output += hex_tab.charAt(x >>> 4 & 15) + hex_tab.charAt(x & 15)
                }
                return output
            }

            function str2rstr_utf8(input) {
                return unescape(encodeURIComponent(input))
            }

            function raw_md5(s) {
                return rstr_md5(str2rstr_utf8(s))
            }

            function hex_md5(s) {
                return rstr2hex(raw_md5(s))
            }

            function raw_hmac_md5(k, d) {
                return rstr_hmac_md5(str2rstr_utf8(k), str2rstr_utf8(d))
            }

            function hex_hmac_md5(k, d) {
                return rstr2hex(raw_hmac_md5(k, d))
            }

            function md5(string, key, raw) {
                if (!key) {
                    if (!raw)return hex_md5(string);
                    return raw_md5(string)
                }
                if (!raw)return hex_hmac_md5(key, string);
                return raw_hmac_md5(key, string)
            }

            if (typeof define === "function" && define.amd) define(function () {
                return md5
            }); else $.md5 = md5;
            module.exports = md5
        })(this)
    }, {}]
}, {}, [1]);