/**
 * Created by dongchen on 2016-03-29
 */
define(['ViewBase', 'jquery', 'lodash', 'ajaxUtil'], function (ViewBase, $, _, ajaxUtil) {
    var
        tplPath = './module/demo/demo.tpl';

    var view = ViewBase.extend({
        events: {
            'click -> .demo-1-balance-submit': 'onClickBalanceQueryHandler',
            'click -> .demo-1-flowset-submit': 'onClickFlowsetQueryHandler',
            'click -> .demo-1-chargeInfo-submit': 'onClickChargeInfoeQueryHandler'
        },

        init: function (opts) {
            view.__super__.init(opts);
            this.loadCss('./module/demo/demo.css');
        },

        update: function () {
        },

        render: function () {
            this.el.html(_.template(this.getTpl(tplPath, "demo-1-tpl")));
        },

        onClickBalanceQueryHandler: function (event) {

            var
                _this = this,
                $target = $(event.target),
                mobile = $target.parent().find('.demo-1-balance-mobile').val(),
                type = $target.parent().find('.demo-1-balance-type').val(),
                $respCtn = _this.el.find('.demo-1-balance-resp'),
                data = {
                    mobile: mobile,
                    queryType: type
                };

            $respCtn.html('');
            ajaxUtil.doRequest_v2('/balance', data).done(function (res) {

                var html = _.template(_this.getTpl(tplPath, 'demo-1-balance-resp-tpl'), {'variable': 'data'})(res.data);
                $respCtn.html(html);
            });
        },

        onClickFlowsetQueryHandler: function (event) {
            var
                _this = this,
                $target = $(event.target),
                mobile = $target.parent().find('.demo-1-flowset-mobile').val(),
                month = $target.parent().find('.demo-1-flowset-month').val(),
                $respCtn = _this.el.find('.demo-1-flowset-resp'),
                data = {
                    mobile: mobile,
                    month: month
                };

            $respCtn.html('');
            ajaxUtil.doRequest_v2('/flowset', data).done(function (res) {

                var html = _.template(_this.getTpl(tplPath, 'demo-1-flowset-resp-tpl'), {'variable': 'data'})(res.data);
                _this.el.find('.demo-1-flowset-resp').html(html);
            });
        },

        onClickChargeInfoeQueryHandler: function (event) {
            var
                _this = this,
                $target = $(event.target),
                mobile = $target.parent().find('.demo-1-chargeInfo-mobile').val(),
                month = $target.parent().find('.demo-1-chargeInfo-month').val(),
                $respCtn =_this.el.find('.demo-1-chargeInfo-resp'),
                data = {
                    mobile: mobile,
                    month: month
                };
            $respCtn.html('');
            ajaxUtil.doRequest_v2('/chargeInfo', data).done(function (res) {

                var html = _.template(_this.getTpl(tplPath, 'demo-1-chargeInfo-resp-tpl'), {'variable': 'data'})(res.data);
                $respCtn.html(html);
            });
        }
    });
    return view;
});