/**
 * Created by chenyouguo.
 */
define(['jquery', 'lodash'], function ($, _) {
    var cacheData = {};

    var ajaxUtil = {

        ajax: function(option) {

            if (option && option.success) {
                var cb = option.success;
                option.success = this.success(cb);
            }
            $.ajax(option);
        },

        success: function(callback) {

            return function(result) {

                if (result && result.code) {

                    switch  (result.code) {

                        case 500: {
                            alert("系统异常");
                            return;
                        }

                        case 401: {
                            alert("系统未登录,刷新页面试一试");
                            return;
                        }

                        case 405: {
                            alert("没有权限访问");
                            return;
                        }

                        case 403:
                        {
                            alert("请求参数不太对，可以刷新页面试一试");
                            return;
                        }

                        case 520:{
                            alert(result.data);
                            return;
                        }
                    }

                }

                callback(result);
            }

        },


        /**
         * 下载
         * @param url
         */
        download: function(url) {

            var iframe = '<iframe id="my_iframe_xxxw" style="display:none;">';
            if ($('#my_iframe_xxxw').length == 0) {
                $('body').append(iframe);
            }

            document.getElementById('my_iframe_xxxw').src = url;
        },

        download2: function (url, data, method) {
            // 获取url和data
            if (url && data) {
                // data 是 string 或者 array/object
                //data = typeof data === 'string' ? data : $.param(data);
                // 把参数组装成 form的  input
                var inputs = '';
                for(var key in data){
                    var value = typeof data[key] === 'string' ? data[key] : JSON.stringify(data[key]);
                    inputs += "<input type='hidden' name='" + key + "' value='" + value + "' />";
                }
                //$.each(data.split('&'), function () {
                //    var pair = this.split('=');
                //    inputs += '<input type="hidden" name="' + pair[0] + '" value="' + pair[1] + '" />';
                //});
                // request发送请求
                $('<form action="' + url + '" method="' + (method || 'post') + '">' + inputs + '</form>')
                    .appendTo('body').submit().remove();
            }
        },

        /**
         * 符合约定的jsonp通用请求
         * @param path
         * @param data
         * @param urlParams
         * @returns {*}
         */

        doJsonpRequest_v2: function(url, data, urlParams) {

            var dtd = $.Deferred();
            this.doRequest_v2(url, data, urlParams).then(function(res) {
                dtd.resolve(res.data);
            });

            return dtd.promise();
        },

        doPostJsonpRequest: function(path, data, urlParams) {

            var dtd = $.Deferred();
            this.doRequest(path, data, urlParams, 'post').then(function(res) {
                dtd.resolve(res.data);
            });
            return dtd.promise();
        },

        doPostJsonpRequest_v2: function(url, data, urlParams) {

            var dtd = $.Deferred();
            this.doRequest_v2(url, data, urlParams, 'post').then(function(res) {
                dtd.resolve(res.data);
            });
            return dtd.promise();
        },

        /**
         * 符合约定的jsonp通用请求, 返回携带code信息
         * @param path
         * @param data
         * @param urlParams
         * @returns {*}
         */
        doJsonpRequestWithRawResponse: function(path, data, urlParams) {

            var dtd = $.Deferred();
            this.doRequest(path, data, urlParams).then(function(res) {
                dtd.resolve(res);
            });
            return dtd.promise();
        },

        doJsonpRequestWithRawResponse_v2: function(url, data, urlParams) {

            var dtd = $.Deferred();
            this.doRequest_v2(url, data, urlParams).then(function(res) {
                dtd.resolve(res);
            });
            return dtd.promise();
        },

        /**
         * 符合约定的jsonp通用请求, 返回携带code信息
         * @param path
         * @param data
         * @param urlParams
         * @returns {*}
         */
        doPostJsonpRequestWithRawResponse: function(path, data, urlParams) {

            var dtd = $.Deferred();

            this.doRequest(path, data, urlParams, 'post').then(function(res) {
                dtd.resolve(res);
            });
            return dtd.promise();
        },

        doPostJsonpRequestWithRawResponse_v2: function(url, data, urlParams) {

            var dtd = $.Deferred();

            this.doRequest_v2(url, data, urlParams, 'post').then(function(res) {
                dtd.resolve(res);
            });
            return dtd.promise();
        },

        doRequest_v2: function(url, data, urlParams, method) {

            var dtd = $.Deferred();
            var _this = this;
            ajaxUtil.ajax({

                url: _this.parserUrl(url, urlParams),
                dataType: "jsonp",
                async: true,
                data: data,
                method: method || 'get',
                success: function(res) {
                    dtd.resolve(res);
                }
            });

            return dtd.promise();
        },

        parserUrl: function(templateUrl, params) {

            var url = templateUrl;
            if (!params) return url;

            for(var name in params) {
                url = url.replace(['{', name , '}'].join(''), params[name]);
            }

            return url;
        },

        doJsonpRequestWithCache : function(url, data, urlParams, cacheKey){

            var dtd = $.Deferred();

            if(cacheData[cacheKey]){
                dtd.resolve(cacheData[cacheKey]);
            }else{
                ajaxUtil.doJsonpRequest_v2(url, data, urlParams).done(function(res){
                    if(res.length){
                        cacheData[cacheKey] = res;
                    }
                    dtd.resolve(cacheData[cacheKey]);
                });
            }

            return dtd.promise();
        },

        doJsonpPostWithCache : function(url, data, urlParams, cacheKey){

            var dtd = $.Deferred();

            if(cacheData[cacheKey]){
                dtd.resolve(cacheData[cacheKey]);
            }else{
                ajaxUtil.doPostJsonpRequest_v2(url, data, urlParams).done(function(res){
                    if(res.length){
                        cacheData[cacheKey] = res;
                    }
                    dtd.resolve(cacheData[cacheKey]);
                });
            }

            return dtd.promise();

        },

        resetAjaxCache : function(){
            cacheData = {};
        },

        /**
         * 如果有key,则清除对应缓存,没有key,则清除所有缓存
         * @param key
         */
        clearCacheByKey : function(key){
            if(key){
                delete cacheData[key];
            }else{
                cacheData = {};
            }
        }

    };


    return ajaxUtil;
});
